package br.ufrn.imd.behere.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "be_here_db";
    private static final int DB_VERSION = 2;
    private static final String SUBJECTS_TABLE_CREATE = "CREATE TABLE SUBJECTS (ID INTEGER PRIMARY KEY, SCHEDULE TEXT, NAME TEXT, LOCATION TEXT);";
    private static final String LINKS_TABLE_CREATE = "CREATE TABLE LINKS (ID INTEGER PRIMARY KEY, TYPE INTEGER, DESCRIPTION TEXT);";
    private static final String USER_LINKS_TABLE_CREATE = "CREATE TABLE USER_LINKS (USER INTEGER, LINK INTEGER);";
    private static final String USER_SUBJECTS_TABLE_CREATE = "CREATE TABLE USER_SUBJECTS (USER INTEGER, SUBJECT INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SUBJECTS_TABLE_CREATE);
        db.execSQL(LINKS_TABLE_CREATE);
        db.execSQL(USER_LINKS_TABLE_CREATE);
        db.execSQL(USER_SUBJECTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                // Drop all tables
                db.execSQL("DROP TABLE IF EXISTS USER");
                db.execSQL("DROP TABLE IF EXISTS SUBJECTS");
                db.execSQL("DROP TABLE IF EXISTS USER_LINKS");
                db.execSQL("DROP TABLE IF EXISTS USER_SUBJECTS");

                // Create new tables
                db.execSQL(SUBJECTS_TABLE_CREATE);
                db.execSQL(LINKS_TABLE_CREATE);
                db.execSQL(USER_LINKS_TABLE_CREATE);
                db.execSQL(USER_SUBJECTS_TABLE_CREATE);
        }
    }
}