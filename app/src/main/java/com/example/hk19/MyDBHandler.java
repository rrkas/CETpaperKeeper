package com.example.hk19;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
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
                        COLUMN_PERSON_NUMBER + " INTEGER " +
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

    public void updatePerson(String personName,int newVal){
        SQLiteDatabase db = getWritableDatabase();
        String query =  " UPDATE " + TABLE_PERSONS +
                        " SET " + COLUMN_PERSON_NUMBER + "=" + newVal +
                        " WHERE " + COLUMN_PERSON_NAME + "=\"" + personName + "\";";
        db.execSQL(query);
    }

    public int searchDatabase(String personName){
        int val =0;
        SQLiteDatabase db = getWritableDatabase();
        String query =  "SELECT " + COLUMN_PERSON_NUMBER + " FROM " + TABLE_PERSONS +
                        " WHERE " + COLUMN_PERSON_NAME + " =\"" + personName + "\";";
        Cursor c =db.rawQuery(query,null);
        if(!c.isAfterLast())
            if(c.getInt(c.getColumnIndex("personName"))!=0) val=Integer.valueOf(c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER)));
        return val;
    }

    public PersonDetails[] databaseToArray(){
        SQLiteDatabase db = getReadableDatabase();
        String columns[] = {COLUMN_PERSON_NAME,COLUMN_PERSON_NUMBER};
        String query = " SELECT * FROM " + TABLE_PERSONS + ";";
        //Cursor c = db.query(TABLE_PERSONS,columns,null,null,null,null,null);
        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        PersonDetails[] dbPersons = new PersonDetails[c.getCount()];
        for(int i=0;!c.isAfterLast();i++){
            if(c.getString(c.getColumnIndex(COLUMN_PERSON_NAME))!=null && c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER))!=null){
                dbPersons[i].setPersonName(c.getString(c.getColumnIndex(COLUMN_PERSON_NAME)));
                dbPersons[i].setPersonNumber(Integer.valueOf(c.getString(c.getColumnIndex(COLUMN_PERSON_NUMBER))));
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
    }
}
