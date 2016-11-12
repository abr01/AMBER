package com.uoflsoftwareengineering.softwareteamproject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import java.util.List;


/**
 * Created by Tylor on 9/18/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private final Context context = this;
    //private TextView txtContactList;
    private ContactsDBHandler dbHandler;
    //private EditText ContactName;
    //private EditText ContactPhoneNumber;
    private String Name;
    private String Number;
    private Contacts contact;


    //ALEX'S CODE
    @Override
    public void onCreateContextMenu(ContextMenu cmenu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(cmenu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, cmenu);
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.Edit:

                return true;
            case R.id.Delete:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

        public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Buttons to be used always on the page, other buttons will be built dynamically
        ImageButton btnBack= (ImageButton) findViewById(R.id.imgBtnBack);
        ImageButton btnAddContact = (ImageButton) findViewById(R.id.imgBtnAddContact);

        //Create ContactsDBHandler Object to use and display all of the contacts already stored in the database if any
        dbHandler = new ContactsDBHandler(this,null,null,1);
        printDatabase();

        //Click  to go back to the MainActivity page
        btnBack.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        //Click to add contact
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li;
                View addContactsView;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                li = LayoutInflater.from(context);
                addContactsView = li.inflate(R.layout.activity_addcontact, null);

                //Set a view to display to add a contact
                alertDialogBuilder.setView(addContactsView);
                final EditText txtContactName = (EditText) addContactsView.findViewById(R.id.contactAddName);
                final EditText txtContactPhoneNumber = (EditText) addContactsView.findViewById(R.id.contactAddPhoneNumber);
                txtContactPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add Contact",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        //ContactName = (EditText) findViewById(R.id.contactAddName);
                                        //ContactPhoneNumber = (EditText) findViewById(R.id.contactAddPhoneNumber);
                                        Name = txtContactName.getText().toString();
                                        Number = txtContactPhoneNumber.getText().toString();

                                        //If there is valid input add the contact otherwise notify it wasn't valid and to try again
                                        if(Name.length() >= 1 && Number.length() == 14)
                                        {
                                            contact = new Contacts(Name, Number);
                                            dbHandler.addContact(contact);
                                            //printDatabase();
                                        }

                                        else
                                        {
                                            new AlertDialog.Builder(context)
                                                    .setTitle("Invalid Input")
                                                    .setMessage("Name must be at least one letter and phone number must be 10 digits long.")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                                                    {
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    }

                                })
                        //On cancel, close and go back to the SettingsActivity page
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog,int id)
                                    {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    //Prints Database as a String
    public void printDatabase()
    {
        /*txtContactList = (ListView) findViewById(R.id.txtContactList);
        ListView myView = (ListView) findViewById(R.id.txtContactList);
        String dbString = dbHandler.databaseToString();
        txtContactList.setText(dbString);*/
    }
}
