package com.valreja.covidtracker.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.valreja.covidtracker.Symptom;

import java.util.ArrayList;

public class UserDatabase extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "test.db";
    public static final int VERSION = 4;

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

        String CREATE_TABLE = getCreateTableQuery();
        Log.d("DATABASE","onUpgrade" + " " + CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public long insertSymptoms(ArrayList<Symptom> symptomsArrayList,float heartRate,float respRate) {
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

        return sqlDB.insert(DBConstants.TABLE_NAME, null, cv);
    }
}
