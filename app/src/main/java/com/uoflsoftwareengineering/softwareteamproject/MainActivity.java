package com.uoflsoftwareengineering.softwareteamproject;

//All References that are used in main activity are here
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

//Added from Alex
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements LocationListener
{

    //btnSendSMS will be the emergency button
    Button btnSendSMS;

    //btnContacts will be used to transition to the contacts page
    Button btnContacts;

    //dbHandler, contactCursor, and rowCount will be used to keep track of data
    //dbHandler will be used to make calls to the database
    //contactCursor wiil be used to iterate through the list of contacts dbHandler returns
    //If there are no contacts aka rowCount = 0, the emergency button will gray out and disable
    ContactsDBHandler dbHandler;
    Cursor contactCursor;
    int rowCount;

    //locationManager will use the cell phones location information, latitude and longitude will be used
    //to send in message
    LocationManager locationManager;
    double latitude,longitude;

    //Timer and TimerTask will be used to send the location information to emergency contacts on a minute by minute basis
    Timer myTimer;
    TimerTask msgTask;

    //isInDanger initially set to false, user must click button to notify of danger
    Boolean isInDanger = false;

    /** Called when the activity is first created. */
    @Override

    public boolean onCreateOptionsMenu(Menu menu){
    //inflate menu
     getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected (MenuItem item) {

        switch (item.getItemId()){
            case R.id.control:
                return true;
            case R.id.addContact:
                Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
            case R.id.about:
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize the necessary components
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        btnContacts = (Button) findViewById(R.id.btnContacts);
        dbHandler = new ContactsDBHandler(this,null,null,1);
        contactCursor = dbHandler.getContactCursor();
        contactCursor.moveToFirst();

        rowCount = contactCursor.getCount();

        if(rowCount == 0)
        {
            btnSendSMS.setEnabled(false);
        }

        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v) {
                if (isInDanger) {
                    isInDanger = false;
                    btnSendSMS.setText("EMERGENCY");
                    btnSendSMS.setBackgroundResource(R.drawable.emergencybutton_rounded_corners);
                    pause();

                }
                else
                {
                    isInDanger = true;
                    btnSendSMS.setBackgroundResource(R.drawable.emergencybuttonend_rounded_corners);
                    btnSendSMS.setText("End Emergency Messaging");
                    resume();
                }
            }

        });

        btnContacts.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class
                );
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    public void prepareMessage()
    {
        String phoneNo;
        String message;
            while (!contactCursor.isAfterLast())
            {

                if (contactCursor.getString(contactCursor.getColumnIndex("_Name")) != null) {
                    message = contactCursor.getString(contactCursor.getColumnIndex("_Name"));
                    message += " I am in danger come find me http://maps.google.com/?q="
                            + String.valueOf(latitude) + "," + String.valueOf(longitude)
                            + " this is a test of the AMBER alert system";

                    phoneNo = contactCursor.getString(contactCursor.getColumnIndex("_PhoneNumber"));

                    if (phoneNo.length() > 0 && message.length() > 0) {
                        sendSMS(phoneNo, message);
                    } else {
                        Toast.makeText(getBaseContext(),
                                "Please enter both phone number and message.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                contactCursor.moveToNext();
            }
            contactCursor.moveToFirst();
            //Timer timing;

    }
    public void resume()
    {
        this.msgTask = new TimerTask() {
        @Override
        public void run()
        {
            prepareMessage();
        }
    };
        this.myTimer = new Timer();
        this.myTimer.scheduleAtFixedRate(msgTask, 0, 60000 );
    }

    public void pause()
    {
        this.myTimer.cancel();
    }
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

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}



