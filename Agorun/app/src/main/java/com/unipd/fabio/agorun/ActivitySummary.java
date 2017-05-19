package com.unipd.fabio.agorun;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class ActivitySummary extends Activity {

    private TextView organizerName;
    private TextView organizerSurname;
    private TextView activityStartAddress;
    private TextView activityDestinationAddress;
    private TextView activityTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        organizerName = (TextView) findViewById(R.id.organizerName);
        organizerSurname = (TextView) findViewById(R.id.organizerSurname);
        activityStartAddress = (TextView) findViewById(R.id.activityStartAddress);
        activityDestinationAddress = (TextView) findViewById(R.id.activityDestinationAddress);
        activityTime = (TextView) findViewById(R.id.activityTime);

        organizerName.setText("Fabio");
        organizerSurname.setText("Pazzini");
        activityStartAddress.setText("Via Ognissanti 7");
        activityDestinationAddress.setText("Via Morvillo 2");
        activityTime.setText("16:30");



    }
}
