package com.uoflsoftwareengineering.softwareteamproject;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Tylor on 9/30/2016.
 */

public class Contacts extends AppCompatActivity {

    //All Contacts have an id, name, and phone number
    private int _id;
    private String _name;
    private String _phoneNumber;


    public Contacts(String ContactName, String ContactPhoneNumber) {
        this._name = ContactName;
        this._phoneNumber = ContactPhoneNumber;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_ContactName() {
        return _name;
    }

    public void set_ContactName(String _name) {
        this._name = _name;
    }

    public String get_ContactPhoneNumber(){
        return _phoneNumber;
    }

    public void set_ContactPhoneNumber(String _phoneNumber){
        this._phoneNumber = _phoneNumber;
    }
}

