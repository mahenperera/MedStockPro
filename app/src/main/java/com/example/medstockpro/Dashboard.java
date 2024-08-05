package com.example.medstockpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {
    private Button dashBtn1;
    private Button dashBtn2;
    private Button dashBtn3;
    private Button dashLogoutBtn;
    private Button viewPatientsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dashBtn1 = (Button) findViewById(R.id.add_stock_btn);
        dashBtn2 = (Button) findViewById(R.id.add_patient_btn);
        dashBtn3 = (Button) findViewById(R.id.write_pres_btn);
        dashLogoutBtn =  (Button) findViewById(R.id.dash_logout_btn);
        viewPatientsBtn = (Button) findViewById(R.id.view_patient_btn);

        dashBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddStock();
            }
        });

        dashBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPatient();
            }
        });

        dashBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWritePres();
            }
        });

        dashLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginPage();
            }
        });

        viewPatientsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPatientList();
            }
        });

    }
    public void openAddStock() {
        Intent intent = new Intent(this, AddStock.class);
        startActivity(intent);
    }
    public void openAddPatient() {
        Intent intent = new Intent(this, AddPatient.class);
        startActivity(intent);
    }
    public void openWritePres() {
        Intent intent = new Intent(this, PatientList.class);
        startActivity(intent);
    }
    public void openLoginPage() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    public void openPatientList() {
        Intent intent = new Intent(this, ViewPatients.class);
        startActivity(intent);
    }

}