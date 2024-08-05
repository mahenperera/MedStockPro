package com.example.medstockpro;


import static com.example.medstockpro.PatientList.patientDetailsName;
import static com.example.medstockpro.PatientList.patientDetailsAge;
import static com.example.medstockpro.PatientList.patientDetailsId;
import static com.example.medstockpro.PatientList.patientDetailsEmail;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Handler;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CreatePrescription extends AppCompatActivity {

    private TextView patientName;
    private TextView patientAge;
    private TextView patientId;
    private TextView patientEmail;
    private ListView medicineListView;
    private Button createPres;
    private Button viewPres;


    private Map<String, Integer> medicineStock;
    private List<String> medicineNames;
    private MedicineListAdapter1 adapter;
    private static final long DELAY = 60 * 1000;
    private static final int NOTIFICATION_ID = 1;
    private Map<String, Integer> presCountForMedicine = new HashMap<>();


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference medicineStockRef = database.getReference("medicineStock").child(userID);
    DatabaseReference medicineTypesRef = database.getReference("medicineTypes").child(userID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_prescription);

        patientName = (TextView) findViewById(R.id.patient_name);
        patientAge = (TextView) findViewById(R.id.patient_age);
        patientId = (TextView) findViewById(R.id.patient_id);
        patientEmail = (TextView) findViewById(R.id.patient_email);

        createPres = (Button) findViewById(R.id.create_pres_button);
        viewPres = (Button) findViewById(R.id.view_pres_button);

        medicineListView = (ListView) findViewById(R.id.medicine_listview);

        patientName.setText("Patient Name : " + patientDetailsName);
        patientAge.setText("Age : " + patientDetailsAge);
        patientId.setText("ID : " + patientDetailsId);
        patientEmail.setText("Email : " + patientDetailsEmail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        createPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePrescriptionPdf();
                checkStockAndNotify();
            }
        });


        viewPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generatePrescriptionPdf();
                viewPrescriptionPdf();
            }
        });


        medicineStockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                    String medicineName = medicineSnapshot.getKey();
                    int stock = medicineSnapshot.getValue(Integer.class);
                    medicineStock.put(medicineName, stock);
                    medicineNames.add(medicineName);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        medicineStock = new HashMap<>();
        medicineNames = new ArrayList<>(medicineStock.keySet());

        adapter = new MedicineListAdapter1(this, R.layout.list_item_medicine2, medicineNames);
        medicineListView.setAdapter(adapter);

        medicineListView.setOnItemClickListener((parent, view, position, id) -> {
            showMedicineDetails(medicineNames.get(position));
        });
    }


    private void showMedicineDetails(final String medicineName) {
    }


    private class MedicineListAdapter1 extends ArrayAdapter<String> {

        public MedicineListAdapter1(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_medicine2, parent, false);
            }

            final String medicineName = getItem(position);
            TextView medicineNameTextView = convertView.findViewById(R.id.medicine_name);

            final TextView countTextView = convertView.findViewById(R.id.medicine_count);
            final TextView presCountTextView = convertView.findViewById(R.id.pres_count);

            Button incrementButton = convertView.findViewById(R.id.incrementButton);
            Button decrementButton = convertView.findViewById(R.id.decrementButton);

            medicineNameTextView.setText(medicineName);
            countTextView.setText("Count: " + String.valueOf(medicineStock.get(medicineName)));

            int presCount = presCountForMedicine.getOrDefault(medicineName, 0);
            presCountTextView.setText(String.valueOf(presCount));


            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentStock = medicineStock.get(medicineName);
                    int newStock = currentStock - 1;
                    medicineStock.put(medicineName, newStock);
                    countTextView.setText("Count: " + String.valueOf(newStock));

                    int newPresCount = presCountForMedicine.getOrDefault(medicineName, 0) + 1;
                    presCountForMedicine.put(medicineName, newPresCount);
                    presCountTextView.setText(String.valueOf(newPresCount));

                    medicineStockRef.child(medicineName).setValue(newStock);
                }
            });

            decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentStock = medicineStock.get(medicineName);
                    int newStock = currentStock + 1;

                    medicineStock.put(medicineName, newStock);
                    countTextView.setText("Count: " + String.valueOf(newStock));

                    int newPresCount = Math.max(presCountForMedicine.getOrDefault(medicineName, 0) - 1, 0);
                    presCountForMedicine.put(medicineName, newPresCount);
                    presCountTextView.setText(String.valueOf(newPresCount));

                    medicineStockRef.child(medicineName).setValue(newStock);
                }
            });

            return convertView;
        }
    }


    private void generatePrescriptionPdf() {
        try {
            Map<String, Integer> prescriptionData = presCountForMedicine;

            File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String pdfFileName = "prescription.pdf";
            String filePath = new File(externalStorageDir, pdfFileName).getAbsolutePath();

            // Generate the PDF
            PdfGenerator.generatePrescriptionPdf(prescriptionData, filePath);

            Log.d("PDF Generation", "PDF successfully created at: " + filePath);

            showToast("Prescription successfully created!");
        } catch (Exception e) {
            e.printStackTrace();
            showToast("PDF creation failed. Please check logcat for details.");
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void viewPrescriptionPdf() {
        Intent intent = new Intent(CreatePrescription.this, ViewPdfActivity.class);
        startActivity(intent);
    }


    private void checkStockAndNotify() {
        DatabaseReference medicineStockRef = database.getReference("medicineStock").child(userID);

        medicineStockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                    String medicineName = medicineSnapshot.getKey();
                    int currentStock = medicineSnapshot.getValue(Integer.class);
                    int updatedStock = currentStock - presCountForMedicine.getOrDefault(medicineName, 0);

                    if (updatedStock < 5) {
                        showLowStockNotification(medicineName, updatedStock);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void showLowStockNotification(String medicineName, int updatedStock) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Low Stock Alert")
                .setContentText("Stock for " + medicineName + " is below 5. Current stock: " + updatedStock)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        //notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}



