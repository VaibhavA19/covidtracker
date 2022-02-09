package com.valreja.covidtracker;

public class Symptom {
    private String Name;
    private float rating;
    private String SymptomColumnName;

    public String getName() {
        return Name;
    }

    public Symptom(String name, int rating, String symptomColumnName) {
        Name = name;
        this.SymptomColumnName = symptomColumnName;
        this.rating = rating;
    }


    public String getSymptomColumnName() {
        return SymptomColumnName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
