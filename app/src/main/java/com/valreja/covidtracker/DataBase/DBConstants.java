package com.valreja.covidtracker.DataBase;

public class DBConstants {
    public static String TABLE_NAME = "vaibhav";

    public static String COLUMN_ID = "id";
    public static String COLUMN_HEART_RATE = "heart";
    public static String COLUMN_RESPIRATORY_RATE = "respiration";
    public static String COLUMN_NAUSEA_RATING = "nausea";
    public static String COLUMN_HEADACHE_RATING = "headache";
    public static String COLUMN_DIARRHEA_RATING = "diarrhea";
    public static String COLUMN_SOAR_THROAT_RATING = "throat";
    public static String COLUMN_FEVER_RATING = "fever";
    public static String COLUMN_MUSCLE_ACHE_RATING = "muscle";
    public static String COLUMN_LOSS_OF_TASTE_SMELL_RATING = "smell";
    public static String COLUMN_COUGH_RATING = "cough";
    public static String COLUMN_SHORT_BREATH_RATING = "breath";
    public static String COLUMN_TIRED_RATING = "tired";

    public static String CREATE = " CREATE TABLE IF NOT EXISTS ";
    public static String COMMA = " , ";
    public static String LBR = " ( ";
    public static String RBR = " ) ";
    public static String TERMINATE = " ; ";
    public static String INT_PK_AUTOIC = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    public static String TYPE_INTEGER = " INTEGER ";
    public static String TYPE_REAL = " REAL ";
    public static String TYPE_TEXT = " TEXT ";
}
