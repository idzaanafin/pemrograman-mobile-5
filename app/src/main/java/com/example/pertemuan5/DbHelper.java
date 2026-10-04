package com.example.pertemuan5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context) {
        super(context, "db_mahasiswa", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS mhs (" +
                "nrp TEXT PRIMARY KEY, " +
                "nama TEXT, " +
                "jurusan TEXT, " +
                "fakultas TEXT, " +
                "no_hp TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mhs");
        onCreate(db);
    }
}
