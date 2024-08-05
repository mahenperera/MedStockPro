package com.example.medstockpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PatientList extends AppCompatActivity {

    private ListView patientListView;


    public static String patientDetailsName;
    public static String patientDetailsAge;
    public static String patientDetailsId;
    public static String patientDetailsEmail;


    static ArrayList<String> patientListNames = new ArrayList<>();
    static ArrayList<String> patientListAges = new ArrayList<>();
    static ArrayList<String> patientListIds = new ArrayList<>();
    static ArrayList<String> patientListEmails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        patientListView = findViewById(R.id.patient_listview);

        final ArrayList<String> patientList = new ArrayList<>();

        final ArrayAdapter patientAdapter = new ArrayAdapter<String>(this, R.layout.patient_listitem, patientList);
        patientListView.setAdapter(patientAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        String[] info = new String[4];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Patients");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                patientList.clear();

                patientListNames.clear();
                patientListAges.clear();
                patientListIds.clear();
                patientListEmails.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String userIdNew = snapshot.getKey();

                    if(Objects.equals(userIdNew, userID)) {

                        for(DataSnapshot snapshot1 : dataSnapshot.child(userIdNew).getChildren()){

                            int i = 0;

                            for(DataSnapshot snapshot2 : snapshot1.getChildren()) {

                                info[i] = snapshot2.getValue().toString();
                                i++;
                            }

                            patientList.add("Name : " + info[3] + "\n" + "Age : " + info[0] + "\n" + "ID : " + info[2] + "\n" + "Email : " + info[1]);

                            patientListNames.add(info[3]);
                            patientListAges.add(info[0]);
                            patientListIds.add(info[2]);
                            patientListEmails.add(info[1]);
                        }
                    }

                }
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                patientDetailsName = patientListNames.get(position);
                patientDetailsAge = patientListAges.get(position);
                patientDetailsId = patientListIds.get(position);
                patientDetailsEmail = patientListEmails.get(position);

                Intent intent = new Intent(PatientList.this, CreatePrescription.class);
                startActivity(intent);
            }
        });

    }
}