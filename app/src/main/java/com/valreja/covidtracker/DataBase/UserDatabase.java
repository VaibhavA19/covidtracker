package com.valreja.covidtracker.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.valreja.covidtracker.Symptom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserDatabase extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "test.db";
    public static final int VERSION = 5;

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    String getCreateTableQuery() {
        String CREATE_TABLE = DBConstants.CREATE + DBConstants.TABLE_NAME
                + DBConstants.LBR + DBConstants.COLUMN_ID + DBConstants.INT_PK_AUTOIC
                + DBConstants.COMMA
                + DBConstants.COLUMN_HEART_RATE + DBConstants.TYPE_REAL
                + DBConstants.COMMA
                + DBConstants.COLUMN_RESPIRATORY_RATE + DBConstants.TYPE_REAL
                + DBConstants.COMMA
                + DBConstants.COLUMN_NAUSEA_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_HEADACHE_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_DIARRHEA_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_SOAR_THROAT_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_FEVER_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_MUSCLE_ACHE_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_LOSS_OF_TASTE_SMELL_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_COUGH_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_SHORT_BREATH_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_TIRED_RATING + DBConstants.TYPE_INTEGER
                + DBConstants.COMMA
                + DBConstants.COLUMN_LATITUDE + DBConstants.TYPE_REAL
                + DBConstants.COMMA
                + DBConstants.COLUMN_LONGITUDE + DBConstants.TYPE_REAL
                + DBConstants.COMMA
                + DBConstants.COLUMN_TIMESTAMP + DBConstants.TYPE_TEXT
                + DBConstants.RBR
                + DBConstants.TERMINATE;
        return CREATE_TABLE;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = getCreateTableQuery();
        Log.d("DATABASE","OnCreate " + CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBConstants.TABLE_NAME );
        String CREATE_TABLE = getCreateTableQuery();
        Log.d("DATABASE","onUpgrade" + " " + CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public long insertSymptoms(ArrayList<Symptom> symptomsArrayList,float heartRate,float respRate, double latitude, double longitude) {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String CREATE_TABLE = getCreateTableQuery();
        Log.d("DATABASE","onUpgrade" + " " + CREATE_TABLE);
        sqlDB.execSQL(CREATE_TABLE);
        ContentValues cv = new ContentValues();
        Log.d("DATABASE", "Insert " + DBConstants.TABLE_NAME );
        for (Symptom symptom: symptomsArrayList
             ) {
            cv.put(symptom.getSymptomColumnName(),symptom.getRating());
        }
        cv.put(DBConstants.COLUMN_HEART_RATE,heartRate);
        cv.put(DBConstants.COLUMN_RESPIRATORY_RATE,respRate);
        cv.put(DBConstants.COLUMN_LATITUDE,latitude);
        cv.put(DBConstants.COLUMN_LONGITUDE,longitude);
        cv.put(DBConstants.COLUMN_TIMESTAMP, String.valueOf(Calendar.getInstance().getTime()));

        return sqlDB.insert(DBConstants.TABLE_NAME, null, cv);
    }

    public List<DBEntry> getDBEntries(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBConstants.TABLE_NAME, null);
        List<DBEntry> dbEntryList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                DBEntry dbEntry = new DBEntry();
                dbEntry.setID(cursor.getInt(0));
                dbEntry.setHEART_RATE(cursor.getDouble(1));
                dbEntry.setRESPIRATORY_RATE(cursor.getInt(2));
                dbEntry.setNAUSEA_RATING(cursor.getInt(3));
                dbEntry.setHEADACHE_RATING(cursor.getInt(4));
                dbEntry.setDIARRHEA_RATING(cursor.getInt(5));
                dbEntry.setSOAR_THROAT_RATING(cursor.getInt(6));
                dbEntry.setFEVER_RATING(cursor.getInt(7));
                dbEntry.setMUSCLE_ACHE_RATING(cursor.getInt(8));
                dbEntry.setLOSS_OF_TASTE_SMELL_RATING(cursor.getInt(9));
                dbEntry.setCOUGH_RATING(cursor.getInt(10));
                dbEntry.setSHORT_BREATH_RATING(cursor.getInt(11));
                dbEntry.setTIRED_RATING(cursor.getInt(12));
                dbEntry.setLATITUDE(cursor.getDouble(13));
                dbEntry.setLONGITUDE(cursor.getDouble(14));
                dbEntry.setTIMESTAMP(cursor.getString(15));
                dbEntryList.add(dbEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  dbEntryList;
    }
}
