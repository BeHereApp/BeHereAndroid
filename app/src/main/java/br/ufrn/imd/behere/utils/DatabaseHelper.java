package br.ufrn.imd.behere.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "be_here_db";
    private static final int DB_VERSION = 1;
    private static final String USERS_TABLE_CREATE = "CREATE TABLE USERS (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, PASSWORD TEXT);";
    private static final String SUBJECTS_TABLE_CREATE = "CREATE TABLE SUBJECTS (ID INTEGER PRIMARY KEY AUTOINCREMENT, SCHEDULE TEXT, NAME TEXT, LOCATION TEXT);";
    private static final String USER_LINKS_TABLE_CREATE = "CREATE TABLE USER_LINKS (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER INTEGER, LINK_TYPE INTEGER);";
    private static final String USER_SUBJECTS_TABLE_CREATE = "CREATE TABLE USER_SUBJECTS (ID INTEGER PRIMARY KEY AUTOINCREMENT, USER INTEGER, SUBJECT INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(SUBJECTS_TABLE_CREATE);
        db.execSQL(USER_LINKS_TABLE_CREATE);
        db.execSQL(USER_SUBJECTS_TABLE_CREATE);

        populateDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void populateDatabase(SQLiteDatabase db) {
        // Create Users
        db.execSQL("INSERT INTO USERS VALUES(1, 'admin', 'admin')");
        db.execSQL("INSERT INTO USERS VALUES(2, 'professor', 'professor')");
        db.execSQL("INSERT INTO USERS VALUES(3, 'student', 'student')");

        // Create Subjects
        db.execSQL("INSERT INTO SUBJECTS VALUES(1, '35T12', 'DESENVOLVIMENTO DE SISTEMAS PARA DISPOSITIVOS MÓVEIS', 'A309 - CIVT')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(2, '35T34', 'DESENVOLVIMENTO DE SISTEMAS WEB II', 'A308 - CIVT')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(3, '24T34', 'GRAFOS', 'A306 - CIVT')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(4, '24T12', 'LÓGICA APLICADA A ENGENHARIA DE SOFTWARE', 'A304 - CIVT')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(5, '24T56', 'PROGRAMAÇÃO CONCORRENTE', 'A303 - CIVT')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(6, '2M1234', 'REDES DE COMPUTADORES', 'CIVT - A307')");
        db.execSQL("INSERT INTO SUBJECTS VALUES(7, '35T56', 'TÓPICOS ESPECIAIS I', 'CIVT - A308')");

        // Create Links
        db.execSQL("INSERT INTO USER_LINKS VALUES(1, 1, 1)");
        db.execSQL("INSERT INTO USER_LINKS VALUES(2, 1, 2)");
        db.execSQL("INSERT INTO USER_LINKS VALUES(3, 2, 1)");
        db.execSQL("INSERT INTO USER_LINKS VALUES(4, 3, 2)");

        // Relates Users and Subjects
        int id = 1;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 7; j++) {
                db.execSQL("INSERT INTO USER_SUBJECTS VALUES(" + (id++) + "," + i + "," + j + ")");
            }
        }
    }
}
