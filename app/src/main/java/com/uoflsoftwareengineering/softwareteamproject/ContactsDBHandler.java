package com.uoflsoftwareengineering.softwareteamproject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import com.uoflsoftwareengineering.softwareteamproject.Contacts;

/**
 * Created by Tylor on 9/30/2016.
 */
public class ContactsDBHandler extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Contacts.db";
    public static final String TABLE_CONTACTS = "Contacts";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_CONTACTNAME = "_Name";
    public static final String COLUMN_CONTACTPHONENUMBER = "_PhoneNumber";


    public ContactsDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_CONTACTS +
                "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONTACTNAME + " TEXT, "
                + COLUMN_CONTACTPHONENUMBER + " TEXT "
                +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    //Add new row to the database
    public void addContact(Contacts contact){
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACTNAME, contact.get_ContactName());
        values.put(COLUMN_CONTACTPHONENUMBER, contact.get_ContactPhoneNumber());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    //Delete a product from the database
    public void deleteProduct(String productName)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM product where _Name = 'eric'");
    }

    //Print out the table as a string
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + ";";

        //Cursor points to a location in your results
        Cursor contactCursor= db.rawQuery(query, null);
        //Move to the first row in your results
        contactCursor.moveToFirst();

        //Position after the last row means the end of the results
        while (!contactCursor.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (contactCursor.getString(contactCursor.getColumnIndex("_Name")) != null) {
                dbString += contactCursor.getString(contactCursor.getColumnIndex("_Name"));
                dbString += " ";
                dbString += contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber"));
                dbString += "\n";
            }
            contactCursor.moveToNext();
        }
        db.close();
        return dbString;
    }
}
