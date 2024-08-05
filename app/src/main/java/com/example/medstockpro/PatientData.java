package com.example.medstockpro;

public class PatientData {

    String name;
    String age;
    String id;
    String email;


    public PatientData(String name, String age, String id, String email) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
