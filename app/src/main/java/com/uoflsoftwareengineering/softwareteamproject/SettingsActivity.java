package com.uoflsoftwareengineering.softwareteamproject;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import static android.R.drawable.ic_delete;
import static android.R.drawable.ic_menu_edit;


/**
 * Created by Tylor on 9/18/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    private final Context context = this;
    private final static int txtSize = 18;
    private DBHandler dbHandler;
    private String Name;
    private String Number;
    private Contacts contact;

    DBHandler contactDBHandler;
    Cursor contactCursor;

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

    public boolean onCreateOptionsMenu(Menu menu)
    {
        //inflate menu
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Buttons to be used always on the page, other buttons will be built dynamically
        ImageButton btnBack= (ImageButton) findViewById(R.id.imgBtnBack);
        ImageButton btnAddContact = (ImageButton) findViewById(R.id.imgBtnAddContact);

        //Create DBHandler Object to use and display all of the contacts already stored in the database if any
        dbHandler = new DBHandler(this,null,null,1);

        makeContactsTable();

        //Click  to go back to the MainActivity page
        btnBack.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        //Click to add contact
        btnAddContact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addContact();

            }
        });
    }


    public void makeContactsTable()
    {

        TableLayout ContactTable = (TableLayout) findViewById(R.id.contactTable);
        ContactTable.removeAllViews();

        // ContactTable = null;
        TableRow tRow = new TableRow(this);
        TextView nameColumn = new TextView(this);
        TextView numberColumn = new TextView(this);
        nameColumn.setText("Name     ");
        nameColumn.setTextSize(txtSize);
        numberColumn.setText("Number");
        numberColumn.setTextSize(txtSize);

        ContactTable.addView(tRow);
        tRow.addView(nameColumn);
        tRow.addView(numberColumn);

        contactDBHandler = new DBHandler(this, null, null, 1);
        //dbHandler.getContactCursor gets all of the contacts stored within a database
        contactCursor = contactDBHandler.getContactCursor();
        contactCursor.moveToFirst();

        for (int i = 0; i < contactCursor.getCount(); i++) {

            final int ContactID;
            final String ContactName;
            final String ContactPhoneNumber;


            ImageButton editButton = new ImageButton(this);
            editButton.setBackgroundResource(ic_menu_edit);

            ContactID = contactCursor.getInt(contactCursor.getColumnIndex("_ID"));
            ContactName = contactCursor.getString(contactCursor.getColumnIndex("_Name"));
            ContactPhoneNumber = contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber"));

            editButton.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    editContact(ContactID,ContactName,ContactPhoneNumber);
                }
            });

            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setBackgroundResource(ic_delete);
            deleteButton.setOnClickListener(new ImageButton.OnClickListener()
            {
                public void onClick(View v)
                {
                    deleteContact(ContactID);
                }
            });

            TableRow row = new TableRow(this);
            TextView nameField = new TextView(this);
            nameField.setText(contactCursor.getString(contactCursor.getColumnIndex("_Name")));
            nameField.setTextSize(txtSize);

            TextView phoneField = new TextView(this);
            phoneField.setText(contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber")));
            phoneField.setTextSize(txtSize);

            row.addView(nameField);
            row.addView(phoneField);
            row.addView(editButton);
            row.addView(deleteButton);
            ContactTable.addView(row);

            contactCursor.moveToNext();
        }
    }

    public void addContact()
    {
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
                                    makeContactsTable();
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

    public void editContact(final int ContactID, String Name, String Number)
    {
        LayoutInflater li;
        View editContactsView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        editContactsView = li.inflate(R.layout.activity_editcontact, null);

        //Set a view to display to add a contact
        alertDialogBuilder.setView(editContactsView);
        final EditText txtContactName = (EditText) editContactsView.findViewById(R.id.contactEditName);
        final EditText txtContactPhoneNumber = (EditText) editContactsView.findViewById(R.id.contactEditPhoneNumber);
        txtContactPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        txtContactName.setText(Name);
        txtContactPhoneNumber.setText(Number);


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Edit Contact",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                //If there is valid input add the contact otherwise notify it wasn't valid and to try again
                                if(txtContactName.getText().toString().length() >= 1 && txtContactPhoneNumber.getText().toString().length() == 14)
                                {
                                    contact = new Contacts(txtContactName.getText().toString(), txtContactPhoneNumber.getText().toString());
                                    dbHandler.editContact(ContactID, txtContactName.getText().toString(), txtContactPhoneNumber.getText().toString());
                                    makeContactsTable();
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

    public void deleteContact(final int ContactID)
    {
        LayoutInflater li;
        View deleteContactsView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        deleteContactsView = li.inflate(R.layout.activity_deletecontact, null);

        //Set a view to display to add a contact
        alertDialogBuilder.setView(deleteContactsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dbHandler.deleteContact(ContactID);
                                makeContactsTable();
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
}
