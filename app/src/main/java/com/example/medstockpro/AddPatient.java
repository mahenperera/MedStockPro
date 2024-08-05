package com.example.medstockpro;

import android.app.Activity;
import android.app.usage.NetworkStats;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class AddPatient extends Activity {

    private EditText patientName;
    private EditText patientAge;
    private EditText patientID;
    private EditText patientEmail;
    Button addPatientBtn;
    DatabaseReference addPatientRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        patientName = findViewById(R.id.patient_name);
        patientAge = findViewById(R.id.patient_age);
        patientID = findViewById(R.id.patient_id);
        patientEmail = findViewById(R.id.patient_email);
        addPatientBtn = findViewById(R.id.add_patient_btn);

        addPatientRef = FirebaseDatabase.getInstance().getReference("Patients");

        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPatientData();
            }
        });
    }

    private void insertPatientData(){
        String name = patientName.getText().toString();
        String age = patientAge.getText().toString();
        String id = patientID.getText().toString();
        String email = patientEmail.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String userID = user.getUid();

        PatientData patientData = new PatientData(name, age, id, email);

        addPatientRef.child(userID).child(id).setValue(patientData);
        Toast.makeText(this, "Patient Added Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

}
