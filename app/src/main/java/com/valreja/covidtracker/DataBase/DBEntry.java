package com.valreja.covidtracker.DataBase;

public class DBEntry {
    private int ID;
    private double HEART_RATE;
    private double RESPIRATORY_RATE;
    private int NAUSEA_RATING;
    private int HEADACHE_RATING;
    private int DIARRHEA_RATING;
    private int SOAR_THROAT_RATING;
    private int FEVER_RATING;
    private int MUSCLE_ACHE_RATING;
    private int LOSS_OF_TASTE_SMELL_RATING;
    private int COUGH_RATING;
    private int SHORT_BREATH_RATING;
    private int TIRED_RATING;
    private double LATITUDE;
    private double LONGITUDE;
    private String TIMESTAMP;

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setHEART_RATE(double HEART_RATE) {
        this.HEART_RATE = HEART_RATE;
    }


    public int getID() {
        return ID;
    }

    public double getHEART_RATE() {
        return HEART_RATE;
    }

    public double getRESPIRATORY_RATE() {
        return RESPIRATORY_RATE;
    }

    public int getNAUSEA_RATING() {
        return NAUSEA_RATING;
    }

    public int getHEADACHE_RATING() {
        return HEADACHE_RATING;
    }

    public int getDIARRHEA_RATING() {
        return DIARRHEA_RATING;
    }

    public int getSOAR_THROAT_RATING() {
        return SOAR_THROAT_RATING;
    }

    public int getFEVER_RATING() {
        return FEVER_RATING;
    }

    public int getMUSCLE_ACHE_RATING() {
        return MUSCLE_ACHE_RATING;
    }

    public int getLOSS_OF_TASTE_SMELL_RATING() {
        return LOSS_OF_TASTE_SMELL_RATING;
    }

    public int getCOUGH_RATING() {
        return COUGH_RATING;
    }

    public int getSHORT_BREATH_RATING() {
        return SHORT_BREATH_RATING;
    }

    public int getTIRED_RATING() {
        return TIRED_RATING;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    public String getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setRESPIRATORY_RATE(double RESPIRATORY_RATE) {
        this.RESPIRATORY_RATE = RESPIRATORY_RATE;
    }

    public void setNAUSEA_RATING(int NAUSEA_RATING) {
        this.NAUSEA_RATING = NAUSEA_RATING;
    }

    public void setHEADACHE_RATING(int HEADACHE_RATING) {
        this.HEADACHE_RATING = HEADACHE_RATING;
    }

    public void setDIARRHEA_RATING(int DIARRHEA_RATING) {
        this.DIARRHEA_RATING = DIARRHEA_RATING;
    }

    public void setSOAR_THROAT_RATING(int SOAR_THROAT_RATING) {
        this.SOAR_THROAT_RATING = SOAR_THROAT_RATING;
    }

    public void setFEVER_RATING(int FEVER_RATING) {
        this.FEVER_RATING = FEVER_RATING;
    }

    public void setMUSCLE_ACHE_RATING(int MUSCLE_ACHE_RATING) {
        this.MUSCLE_ACHE_RATING = MUSCLE_ACHE_RATING;
    }

    public void setLOSS_OF_TASTE_SMELL_RATING(int LOSS_OF_TASTE_SMELL_RATING) {
        this.LOSS_OF_TASTE_SMELL_RATING = LOSS_OF_TASTE_SMELL_RATING;
    }

    public void setCOUGH_RATING(int COUGH_RATING) {
        this.COUGH_RATING = COUGH_RATING;
    }

    public void setSHORT_BREATH_RATING(int SHORT_BREATH_RATING) {
        this.SHORT_BREATH_RATING = SHORT_BREATH_RATING;
    }

    public void setTIRED_RATING(int TIRED_RATING) {
        this.TIRED_RATING = TIRED_RATING;
    }

    public void setLATITUDE(double LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public void setLONGITUDE(double LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }
}
