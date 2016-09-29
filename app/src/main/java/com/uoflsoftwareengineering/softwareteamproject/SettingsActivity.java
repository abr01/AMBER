package com.uoflsoftwareengineering.softwareteamproject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


/**
 * Created by Tylor on 9/18/2016.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton btnSettings = (ImageButton) findViewById(R.id.imgBtnBack);
        btnSettings.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(i);
    }
}
