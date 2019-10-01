package com.example.hk19;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "persons.db";
    public static final String TABLE_PERSONS = "persons";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_PERSON_NAME = "personName";
    public static final String COLUMN_PERSON_NUMBER = "personNumber";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = " CREATE TABLE " + TABLE_PERSONS + " ( " +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_PERSON_NAME + " TEXT , " +
                        COLUMN_PERSON_NUMBER + " TEXT " +
                        ");" ;
        sqLiteDatabase.execSQL(query);
    }

    public void addPerson(PersonDetails persons){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PERSON_NAME,persons.getPersonName());
        values.put(COLUMN_PERSON_NUMBER,persons.getPersonNumber());
        SQLiteDatabase db  = getWritableDatabase();
        db.insert(TABLE_PERSONS,null,values);
        db.close();
    }

    public void updatePerson(String personName,String newVal){
        SQLiteDatabase db = getWritableDatabase();
        String query =  " UPDATE " + TABLE_PERSONS +
                        " SET " + COLUMN_PERSON_NUMBER + "=\"" + newVal + "\"" +
                        " WHERE " + COLUMN_PERSON_NAME + "=\"" + personName + "\";";
        db.execSQL(query);
    }

    public String searchDatabase(String personName){
        String val="";
        SQLiteDatabase db = getWritableDatabase();
        String query =  " SELECT " + COLUMN_PERSON_NUMBER + " FROM " + TABLE_PERSONS +
                        " WHERE " + COLUMN_PERSON_NAME + " =\"" + personName + "\";";
        Cursor c =db.rawQuery(query,null);
        c.moveToFirst();
        if(!c.isAfterLast())
            if(!c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER)).equals("")) val=c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER));
        return val;
    }

    public List<PersonDetails> databaseToList(){
        SQLiteDatabase db = getReadableDatabase();
        String query = " SELECT * FROM " + TABLE_PERSONS + ";";
        //Cursor c = db.query(TABLE_PERSONS,columns,null,null,null,null,null);
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        List<PersonDetails> dbPersons=new ArrayList<>();
        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex(COLUMN_PERSON_NAME))!=null){
                PersonDetails temp = new PersonDetails();
                temp.setPersonName(c.getString(c.getColumnIndex(COLUMN_PERSON_NAME)));
                temp.setPersonNumber(c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER)));
                dbPersons.add(temp);
            }
            c.moveToNext();
        }
        db.close();
        return dbPersons;
    }

    public void deletePerson(String personName) {
        SQLiteDatabase db = getWritableDatabase();
        String query =  " DELETE FROM " + TABLE_PERSONS +
                        " WHERE " + COLUMN_PERSON_NAME + " =\"" + personName + "\";";
        db.execSQL(query);
    }
}
