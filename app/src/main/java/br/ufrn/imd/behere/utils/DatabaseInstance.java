package br.ufrn.imd.behere.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseInstance {

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase databaseRead;
    public static SQLiteDatabase databaseWrite;

    public static void createDBInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
            databaseRead = dbHelper.getReadableDatabase();
            databaseWrite = dbHelper.getWritableDatabase();
        }
    }
}