package br.ufrn.imd.behere.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseInstance {

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase database;

    public static void createDBInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
            database = dbHelper.getReadableDatabase();
        }
    }
}