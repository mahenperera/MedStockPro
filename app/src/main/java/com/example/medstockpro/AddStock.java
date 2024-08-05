package com.example.medstockpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class AddStock extends AppCompatActivity {

    private EditText searchEditText;
    private List<String> medicineNames;
    private Map<String, Integer> medicineStock;
    private ListView medicineListView;
    private MedicineListAdapter adapter;
    private FirebaseAuth auth;
    private Button button;
    private TextView textView;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference medicineStockRef = database.getReference("medicineStock").child(userID);
    DatabaseReference medicineTypesRef = database.getReference("medicineTypes").child(userID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstock);

        searchEditText = findViewById(R.id.searchEditText);
        medicineListView = findViewById(R.id.medicine_list_view);

        medicineStock = new HashMap<>();
        //medicineStock.put("Medicine A", 10);
        //medicineStock.put("Medicine B", 20);

        medicineNames = new ArrayList<>(medicineStock.keySet());

        adapter = new MedicineListAdapter(this, R.layout.list_item_medicine, medicineNames);
        medicineListView.setAdapter(adapter);

        medicineListView.setOnItemClickListener((parent, view, position, id) -> {
            showMedicineDetails(medicineNames.get(position));
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


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        Button addMedicineButton = findViewById(R.id.addMedicineButton);
        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMedicineDialog();
            }
        });


        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void showMedicineDetails(final String medicineName) {
    }


    private void showAddMedicineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Medicine");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medicine, null);
        final EditText medicineNameInput = dialogView.findViewById(R.id.medicineNameInput);
        final EditText initialStockInput = dialogView.findViewById(R.id.initialStockInput);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String medicineName = medicineNameInput.getText().toString().trim();
                if (!medicineName.isEmpty()) {
                    int initialStock = Integer.parseInt(initialStockInput.getText().toString());
                    addNewMedicine(medicineName, initialStock);
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }


    class MedicineType {
        private String name;
        private int initialStock;

        public MedicineType() {
        }

        public MedicineType(String name, int initialStock) {
            this.name = name;
            this.initialStock = initialStock;
        }

        public String getName() {
            return name;
        }

        public int getInitialStock() {
            return initialStock;
        }
    }


    private void addNewMedicine(String medicineName, int initialStock) {
        medicineStock.put(medicineName, initialStock);
        medicineNames.add(medicineName);
        adapter.notifyDataSetChanged();

        // Create a new MedicineType object
        MedicineType newMedicineType = new MedicineType(medicineName, initialStock);

        // Add medicine data to Firebase under "medicineTypes" node
        medicineTypesRef.child(medicineName).setValue(newMedicineType);

        // Add medicine data to Firebase under "medicineStock" node
        medicineStockRef.child(medicineName).setValue(initialStock);
    }


    private class MedicineListAdapter extends ArrayAdapter<String> {

        public MedicineListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_medicine, parent, false);
            }

            final String medicineName = getItem(position);

            TextView medicineNameTextView = convertView.findViewById(R.id.medicineNameTextView);
            final TextView stockTextView = convertView.findViewById(R.id.stockTextView);

            Button incrementButton = convertView.findViewById(R.id.incrementButton);
            Button decrementButton = convertView.findViewById(R.id.decrementButton);

            medicineNameTextView.setText(medicineName);
            stockTextView.setText(String.valueOf(medicineStock.get(medicineName)));

            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentStock = medicineStock.get(medicineName);
                    int newStock = currentStock + 1;
                    medicineStock.put(medicineName, newStock);
                    stockTextView.setText(String.valueOf(newStock));

                    medicineStockRef.child(medicineName).setValue(newStock);
                }
            });

            decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int currentStock = medicineStock.get(medicineName);
                    int newStock = currentStock - 1;
                    medicineStock.put(medicineName, newStock);
                    stockTextView.setText(String.valueOf(newStock));

                    medicineStockRef.child(medicineName).setValue(newStock);
                }
            });

            return convertView;
        }
    }
}