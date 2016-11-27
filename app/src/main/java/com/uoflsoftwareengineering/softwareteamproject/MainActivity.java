//Main project
package com.uoflsoftwareengineering.softwareteamproject;

//All References that are used in main activity are here
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

//Added from Alex
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements LocationListener
{
    //####Not all of these variables are needed at this level of the class, could be declared in the
    //####functions
    private final Context context = this;
    //btnSendSMS will be the emergency button
    Button btnSendSMS;
    //####Get rid of button contacts once finalized
    //btnContacts will be used to transition to the contacts page
    //Button btnContacts;
    //dbHandler, contactCursor, and rowCount will be used to keep track of data
    //dbHandler will be used to make calls to the database
    //contactCursor wiil be used to iterate through the list of contacts dbHandler returns
    //If there are no contacts aka rowCount = 0, the emergency button will gray out and disable
    DBHandler contactDBHandler;
    Cursor contactCursor;

    boolean isPasswordSet;
    String password1;
    String password2;
    String passwordCheck;
    String storedPassword;

    int contactCount;
    //locationManager will use the cell phones location information, latitude and longitude will be used
    //to send in message
    LocationManager locationManager;
    double latitude,longitude;

    //Timer and TimerTask will be used to send the location information to emergency contacts on a minute by minute basis
    Timer msgTimer;
    TimerTask msgTask;

    //isInDanger initially set to false, user must click button to notify of danger
    //####isInDanve IS A TEMPORARY VARIABLE//
    int isInDanger;

    /** Called when the activity is first created. */
    //ALEX'S CODE
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    //inflate menu
     getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

//ALEX'S CODE
    public boolean onOptionsItemSelected (MenuItem item) {

        switch (item.getItemId()){
            case R.id.control:
                return true;
            case R.id.addContact:
                if(contactDBHandler.isInDanger() == 1)
                {
                    manageContactsError();
                    return true;
                }

                else
                {
                    //Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class);
                    //MainActivity.this.startActivity(myIntent);
                    checkPasswordManageContacts();
                    return true;
                }
            //case R.id.about:
            //   return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void onCreate(Bundle savedInstanceState)
    {
        //Set the contenct to match the xml file that is activity_main.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the necessary location components

        //Gets the last known location sent from this phone, prevents null value.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        Location l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = l.getLatitude();
        longitude = l.getLongitude();

        //####Should rename this button will come back to it
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
       // btnContacts = (Button) findViewById(R.id.btnContacts);

        //dbHandler creates an instance of the object DBHandler
        contactDBHandler = new DBHandler(this,null,null,1);
        //dbHandler.getContactCursor gets all of the contacts stored within a database
        contactCursor = contactDBHandler.getContactCursor();
        //contactCursor.moveToFirst();
        contactCount= contactCursor.getCount();

        if(contactCount == 0)
        {
            btnSendSMS.setEnabled(false);
        }

        isPasswordSet = contactDBHandler.isPasswordSet();

        //If the password is not set, set it here
        if(!isPasswordSet)
        {
            setPassword();
        }

        isInDanger = contactDBHandler.isInDanger();
        if (isInDanger == 0)
        {
            btnSendSMS.setText("EMERGENCY");
            btnSendSMS.setBackgroundResource(R.drawable.emergencybutton_rounded_corners);
        }
        //If the user clicks "End Emergency Messaging", end the emergency messaging
        else
        {
            btnSendSMS.setText("End Emergency Messaging");
            btnSendSMS.setBackgroundResource(R.drawable.emergencybuttonend_rounded_corners);
            resume();
        }

        //Function that tells the button btnSendSMS what to do onClick
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                isInDanger = contactDBHandler.isInDanger();
                //If the user clicks "EMERGENCY", notify the user that the message is being relayed
                if (isInDanger == 1)
                {
                    checkPasswordEmergencyButton();
                }
                //If the user clicks "End Emergency Messaging", end the emergency messaging
                else
                {
                    contactDBHandler.updateUserStatus(isInDanger);
                    btnSendSMS.setBackgroundResource(R.drawable.emergencybuttonend_rounded_corners);
                    btnSendSMS.setText("End Emergency Messaging");
                    //Send emergency message with updated location minute by minute
                    resume();
                }
            }

        });
    }

    public void setPassword()
    {
        LayoutInflater li;
        View createPasswordView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        createPasswordView= li.inflate(R.layout.activity_createpassword, null);

        //Set a view to display to add a contact
        alertDialogBuilder.setView(createPasswordView);
        final EditText Password1 = (EditText) createPasswordView.findViewById(R.id.password1);
        final EditText Password2 = (EditText) createPasswordView.findViewById(R.id.password2);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create Password",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                //ContactName = (EditText) findViewById(R.id.contactAddName);
                                //ContactPhoneNumber = (EditText) findViewById(R.id.contactAddPhoneNumber);
                                password1 = Password1.getText().toString();
                                password2 = Password2.getText().toString();

                                //If there is valid input add the contact otherwise notify it wasn't valid and to try again
                                if(password1.equals(password2) && password1.length() > 0)
                                {
                                    contactDBHandler.addPassword(password1);
                                }

                                else
                                {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Invalid Input")
                                            .setMessage("The two passwords you entered were not equal. Password Creation Failed")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                                            {
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    setPassword();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //On location change, set the latitude and longitude variables
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    //These function were required to be added because of the location service otherwise the program would not run
    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void manageContactsError()
    {
        LayoutInflater li;
        View emergencyIsActiveView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        emergencyIsActiveView = li.inflate(R.layout.activity_managecontactserror, null);

        alertDialogBuilder.setView(emergencyIsActiveView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {

                            }

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void checkPasswordEmergencyButton()
    {
        LayoutInflater li;
        View confirmPasswordView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        confirmPasswordView= li.inflate(R.layout.activity_confirmpassword, null);

        //Set a view to display to add a contact
        alertDialogBuilder.setView(confirmPasswordView);
        final EditText passwordConfirmation = (EditText) confirmPasswordView.findViewById(R.id.passwordConfirmation);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Confirm Password",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                //ContactName = (EditText) findViewById(R.id.contactAddName);
                                //ContactPhoneNumber = (EditText) findViewById(R.id.contactAddPhoneNumber);
                                passwordCheck = passwordConfirmation.getText().toString();
                                storedPassword = contactDBHandler.getPassword();
                                //If there is valid input add the contact otherwise notify it wasn't valid and to try again
                                if(passwordCheck.equals(storedPassword))
                                {
                                    contactDBHandler.updateUserStatus(1);
                                    isInDanger = contactDBHandler.isInDanger();
                                    btnSendSMS.setText("EMERGENCY");
                                    btnSendSMS.setBackgroundResource(R.drawable.emergencybutton_rounded_corners);
                                    pause();

                                    prepareSafeMessage();
                                }

                                else
                                {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Invalid Password")
                                            .setMessage("The password you entered was invalid. Emergency messaging will continue")
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

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void checkPasswordManageContacts()
    {
        LayoutInflater li;
        View confirmPasswordView;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        li = LayoutInflater.from(context);
        confirmPasswordView= li.inflate(R.layout.activity_confirmpassword, null);

        //Set a view to display to add a contact
        alertDialogBuilder.setView(confirmPasswordView);
        final EditText passwordConfirmation = (EditText) confirmPasswordView.findViewById(R.id.passwordConfirmation);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Confirm Password",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                //ContactName = (EditText) findViewById(R.id.contactAddName);
                                //ContactPhoneNumber = (EditText) findViewById(R.id.contactAddPhoneNumber);
                                passwordCheck = passwordConfirmation.getText().toString();
                                storedPassword = contactDBHandler.getPassword();
                                //If there is valid input add the contact otherwise notify it wasn't valid and to try again
                                if(passwordCheck.equals(storedPassword))
                                {
                                    Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class);
                                    MainActivity.this.startActivity(myIntent);
                                }

                                else
                                {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Invalid Password")
                                            .setMessage("The password you entered was invalid.")
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

                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    //This iterates through each of the contacts making sure that each user gets
    //a gps location of the person in danger
    public void prepareEmergencyMessage()
    {
        String phoneNo;
        String message;
        contactCursor.moveToFirst();

            while (!contactCursor.isAfterLast())
            {
                if (contactCursor.getString(contactCursor.getColumnIndex("_Name")) != null) {
                    message = contactCursor.getString(contactCursor.getColumnIndex("_Name"));
                    message += " I am in danger come find me http://maps.google.com/?q="
                            + String.valueOf(latitude) + "," + String.valueOf(longitude);


                    phoneNo = contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber"));

                    //If it's valid, send message
                    if (phoneNo.length() > 0 && message.length() > 0) {
                        sendSMS(phoneNo, message);
                    } else {
                        Toast.makeText(getBaseContext(),
                                "Invalid Phone Number or Message",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                contactCursor.moveToNext();
            }
    }

    public void prepareSafeMessage()
    {
        String phoneNo;
        String message;
        contactCursor.moveToFirst();

        while (!contactCursor.isAfterLast())
        {
            if (contactCursor.getString(contactCursor.getColumnIndex("_Name")) != null) {
                message = contactCursor.getString(contactCursor.getColumnIndex("_Name"));
                message += " I am safe now. Emergency messaging ending";


                phoneNo = contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber"));

                //If it's valid, send message
                if (phoneNo.length() > 0 && message.length() > 0) {
                    sendSMS(phoneNo, message);
                } else {
                    Toast.makeText(getBaseContext(),
                            "Invalid Phone Number or Message",
                            Toast.LENGTH_SHORT).show();
                }
            }
            contactCursor.moveToNext();
        }
    }


    //This function will run the prepareMessage method every minute when called
    public void resume()
    {
        this.msgTask = new TimerTask()
        {
            @Override
            public void run()
            {
                prepareEmergencyMessage();
            }
        };

        this.msgTimer= new Timer();
        this.msgTimer.scheduleAtFixedRate(msgTask, 0, 60000 );
    }

    //This function will stop the sending of the messages
    public void pause()
    {
        this.msgTimer.cancel();
    }

    //This method was found online and uses phone services to send sms messages to other cell phones
    //Will reference this code in our report
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

}



