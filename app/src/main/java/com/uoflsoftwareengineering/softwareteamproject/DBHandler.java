package com.uoflsoftwareengineering.softwareteamproject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

/**
 * Created by Tylor on 9/30/2016.
 */
public class DBHandler extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AmberDB.db";
    public static final String TABLE_CONTACTS = "Contacts";
    public static final String COLUMN_ID = "_ID";

    public static final String COLUMN_CONTACTNAME = "_Name";
    public static final String COLUMN_CONTACTPHONENUMBER = "_PhoneNumber";

    public static final String COLUMN_PASSWORD = "_Password";
    public static final String TABLE_PASSWORD = "Password";

    public static final String COLUMN_ISINDANGER = "_IsInDanger";
    public static final String TABLE_USERSTATUS = "UserStatus";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Create the database  tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CreateContactTable = "CREATE TABLE " + TABLE_CONTACTS +
                "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONTACTNAME + " TEXT, "
                + COLUMN_CONTACTPHONENUMBER + " TEXT "
                +
                ");";

        String CreatePasswordTable = "CREATE TABLE " + TABLE_PASSWORD +
                "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PASSWORD + " TEXT "
                +
                ");";

        String CreateUserStatusTable = "CREATE TABLE " + TABLE_USERSTATUS +
                "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ISINDANGER + " INTEGER "
                +
                ");";

        db.execSQL(CreateContactTable);
        db.execSQL(CreatePasswordTable);
        db.execSQL(CreateUserStatusTable);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ISINDANGER, 0);
        db.insert(TABLE_USERSTATUS, null, values);

    }

    //In case of database upgrade, drop all tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORD);
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

    public void editContact(int ContactID, String ContactName, String ContactNumber)
    {
        String WhereClause = "_ID = " + Integer.toString(ContactID);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACTNAME, ContactName);
        values.put(COLUMN_CONTACTPHONENUMBER, ContactNumber);
        db.update(TABLE_CONTACTS, values, WhereClause, null);
    }

    //Delete a product from the database
    public void deleteContact(int ContactID)
    {
        String WhereClause = "_ID = " +  Integer.toString(ContactID);
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, WhereClause, null);
        //db.execSQL("DELETE FROM Contacts where _ID = " + Integer.toString(ContactID));
    }

    //Get the table Contacts and return the table in a Curosr variable
    public Cursor getContactCursor()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_ID + ", "
                                 + COLUMN_CONTACTNAME + ", "
                                 + COLUMN_CONTACTPHONENUMBER + " FROM "
                                 + TABLE_CONTACTS + ";";
        //String query = "SELECT * FROM " + TABLE_CONTACTS + ";";
        //Cursor points to a location in your results
        Cursor contactCursor= db.rawQuery(query, null);
        return contactCursor;

    }

    //Create password for user
    public void addPassword(String password){

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, password);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PASSWORD, null, values);
        db.close();

    }

    //returns the password stored for the user
    public boolean isPasswordSet()
    {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_PASSWORD + ";";
        //String query = "SELECT _Password FROM " + TABLE_PASSWORD + ";";

        //Cursor points to a location in your results
        Cursor passwordCursor= db.rawQuery(query, null);
        if(passwordCursor.getCount() == 1)
        {
            return true;
        }

        else
        {
            return false;
        }

    }

    public String getPassword()
    {
        String password;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_PASSWORD + ";";
        Cursor passwordCursor = db.rawQuery(query, null);

        passwordCursor.moveToFirst();
        password = passwordCursor.getString(passwordCursor.getColumnIndex(COLUMN_PASSWORD));

        return password;
    }

    public int isInDanger()
    {
        int isIndanger;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_ISINDANGER + " FROM " + TABLE_USERSTATUS + ";";
        //String query = "SELECT _IsInDanger FROM UserStatus";
        Cursor userStatusCursor = db.rawQuery(query, null);

        userStatusCursor.moveToFirst();
        isIndanger = userStatusCursor.getInt(userStatusCursor.getColumnIndex(COLUMN_ISINDANGER));
        //isIndanger = userStatusCursor.getInt(userStatusCursor.getColumnIndex("_IsInDanger"));

        return isIndanger;
    }

    public void updateUserStatus(int currentStatus)
    {
        if(currentStatus == 0)
        {
            currentStatus = 1;
        }

        else
        {
            currentStatus = 0;
        }

        SQLiteDatabase db = getWritableDatabase();
        //String query = "UPDATE UserStatus ";
        //query += "SET _IsInDanger = " + currentStatus;
        String query = "UPDATE " + TABLE_USERSTATUS + " SET " + COLUMN_ISINDANGER + " = " + currentStatus;
        db.execSQL(query);
    }

}
