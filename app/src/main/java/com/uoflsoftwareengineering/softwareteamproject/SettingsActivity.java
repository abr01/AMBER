package com.uoflsoftwareengineering.softwareteamproject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;



/**
 * Created by Tylor on 9/18/2016.
 */
public class SettingsActivity extends Activity {

    private final Context context = this;
    private TextView txtContactList;
    private ContactsDBHandler dbHandler;
    private EditText ContactName;
    private EditText ContactPhoneNumber;
    private String Name;
    private String Number;
    private Contacts contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton btnSettings = (ImageButton) findViewById(R.id.imgBtnBack);
        ImageButton btnAddContact = (ImageButton) findViewById(R.id.imgBtnAddContact);



        dbHandler = new ContactsDBHandler(this,null,null,1);
        printDatabase();

        btnSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li;
                View addContactsView;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                li = LayoutInflater.from(context);
                addContactsView = li.inflate(R.layout.activity_addcontact, null);
                alertDialogBuilder.setView(addContactsView);
                final EditText txtContactName = (EditText) addContactsView.findViewById(R.id.contactAddName);
                final EditText txtContactPhoneNumber = (EditText) addContactsView.findViewById(R.id.contactAddPhoneNumber);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add Contact",
                                new DialogInterface.OnClickListener(){

                                    public void onClick(DialogInterface dialog,int id) {

                                        //ContactName = (EditText) findViewById(R.id.contactAddName);
                                        //ContactPhoneNumber = (EditText) findViewById(R.id.contactAddPhoneNumber);
                                        Name = txtContactName.getText().toString();
                                        Number = txtContactPhoneNumber.getText().toString();
                                        contact = new Contacts(Name,Number);
                                        dbHandler.addContact(contact);
                                        printDatabase();

                                    }

                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    //Prints Database as a String
    public void printDatabase(){
        txtContactList = (TextView) findViewById(R.id.txtContactList);
        String dbString = dbHandler.databaseToString();
        txtContactList.setText(dbString);

    }

}