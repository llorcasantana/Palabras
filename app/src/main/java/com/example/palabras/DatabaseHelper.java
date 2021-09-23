package com.example.palabras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "QuizWord.db";
    public static final String TABLE_NAME = "Quiz_Data";

    public static final String COL1 = "ID";
    public static final String COL2 = "ID_PHOTO";
    public static final String COL3 = "SOLUTION";
    public static final String COL4 = "CR_1";
    public static final String COL5 = "CR_2";
    public static final String COL6 = "CR_3";
    public static final String COL7 = "CR_4";
    public static final String COL8 = "SOLUTIONED";
    public static final String COL9 = "DIFFICULT";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, ID_PHOTO TEXT, SOLUTION TEXT, CR_1 TEXT, CR_2 TEXT, CR_3 TEXT, CR_4 TEXT, SOLUTIONED TEXT, DIFFICULT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public boolean setCompleted(String id_photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL2, id_photo);
        contentValues.put(COL8, "1");
        long result = db.update(TABLE_NAME, contentValues, "ID_PHOTO=?",new String[]{id_photo});
        db.close();
        return result != -1;

    }

    public boolean insertData(String id_photo, String solution, String CR_1, String CR_2, String CR_3, String CR_4, String Difficult){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, id_photo);
        contentValues.put(COL3, solution);
        contentValues.put(COL4, CR_1);
        contentValues.put(COL5, CR_2);
        contentValues.put(COL6, CR_3);
        contentValues.put(COL7, CR_4);
        contentValues.put(COL8, "");
        contentValues.put(COL9, Difficult);

        long result = db.insert(TABLE_NAME,null, contentValues);
        db.close();
        return result != -1;
    }
    public Cursor getRandomQuiz(String Difficult){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("Select * FROM "+TABLE_NAME+" WHERE "+COL9+" = '"+Difficult+"' AND "+COL8+" = '' ORDER BY RANDOM() LIMIT 1",null);

    }
    public Cursor getQuiz(String lastQuiz){
        SQLiteDatabase db = getWritableDatabase();
        int id = Integer.parseInt(lastQuiz);
        return db.rawQuery("Select * FROM "+TABLE_NAME+" WHERE "+COL2+" = '"+id+"'",null);
    }
    public Cursor getAllCompleted(){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("Select * FROM "+TABLE_NAME+" WHERE "+COL8+" = '1'",null);
    }
    public Cursor getAllPool(){
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery("Select * FROM "+TABLE_NAME+" WHERE "+COL9+" = '1'",null);
    }
}

