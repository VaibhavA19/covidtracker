package com.valreja.covidtracker.DataBase;

import java.io.File;

public class DBHelper {




    private void createDB(){
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath("demo.db");
        databaseFile.mkdirs();
    }
}
