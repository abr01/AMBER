//Main project
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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
//These are not used libraries, can we get rid of them?
import android.graphics.Point;
import android.view.MenuInflater;
import android.support.v7.widget.PopupMenu;


public class MainActivity extends AppCompatActivity implements LocationListener
{
    //####Not all of these variables are needed at this level of the class, could be declared in the
    //####functions

    //btnSendSMS will be the emergency button
    Button btnSendSMS;
    //####Get rid of button contacts once finalized
    //btnContacts will be used to transition to the contacts page
    //Button btnContacts;
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
    Timer msgTimer;
    TimerTask msgTask;

    //isInDanger initially set to false, user must click button to notify of danger
    //####isInDanve IS A TEMPORARY VARIABLE//
    Boolean isInDanger = false;

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
        //Set the contenct to match the xml file that is activity_main.xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the necessary components
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        //####Should rename this button will come back to it
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
       // btnContacts = (Button) findViewById(R.id.btnContacts);

        //dbHandler creates an instance of the object ContactsDBHandler
        dbHandler = new ContactsDBHandler(this,null,null,1);
        //dbHandler.getContactCursor gets all of the contacts stored within a database
        contactCursor = dbHandler.getContactCursor();
        //contactCursor.moveToFirst();
        rowCount = contactCursor.getCount();

        if(rowCount == 0)
        {
            btnSendSMS.setEnabled(false);
        }

        //Function that tells the button btnSendSMS what to do onClick
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v) {

                //If the user clicks "EMERGENCY", notify the user that the message is being relayed
                if (isInDanger) {
                    isInDanger = false;
                    btnSendSMS.setText("EMERGENCY");
                    btnSendSMS.setBackgroundResource(R.drawable.emergencybutton_rounded_corners);
                    pause();
                }
                //If the user clicks "End Emergency Messaging", end the emergency messaging
                else
                {
                    isInDanger = true;
                    btnSendSMS.setBackgroundResource(R.drawable.emergencybuttonend_rounded_corners);
                    btnSendSMS.setText("End Emergency Messaging");
                    //Send emergency message with updated location minute by minute
                    resume();
                }
            }

        });

       /* btnContacts.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class
                );
                MainActivity.this.startActivity(myIntent);
            }
        });*/
    }

    //This iterates through each of the contacts making sure that each user gets
    //a gps location of the person in danger
    public void prepareMessage()
    {
        String phoneNo;
        String message;
        contactCursor.moveToFirst();
            while (!contactCursor.isAfterLast())
            {
                if (contactCursor.getString(contactCursor.getColumnIndex("_Name")) != null) {
                    message = contactCursor.getString(contactCursor.getColumnIndex("_Name"));
                    message += " I am in danger come find me http://maps.google.com/?q="
                            + String.valueOf(latitude) + "," + String.valueOf(longitude)
                            + " this is a test of the AMBER alert system";

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
            //contactCursor.moveToFirst();
            //Timer timing;

    }

    //This function will run the prepareMessage method every minute when called
    public void resume()
    {
        this.msgTask = new TimerTask() {
        @Override
        public void run()
        {
            prepareMessage();
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
}



