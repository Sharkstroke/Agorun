package com.unipd.fabio.agorun;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

public class ActivitySummary extends Activity {

    private TextView organizerName;
    private TextView organizerSurname;
    private TextView activityStartAddress;
    private TextView activityDestinationAddress;
    private TextView activityLength;
    private TextView activityDifficulty;
    private TextView creatorName;
    private TextView activityTime;

    private Button joinButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        organizerName = (TextView) findViewById(R.id.organizerName);
        organizerSurname = (TextView) findViewById(R.id.organizerSurname);
        activityStartAddress = (TextView) findViewById(R.id.activityStartAddress);
        activityDestinationAddress = (TextView) findViewById(R.id.activityDestinationAddress);
        activityLength = (TextView) findViewById(R.id.activityLength);
        activityDifficulty = (TextView) findViewById(R.id.activityDifficulty);
        creatorName = (TextView) findViewById(R.id.creatorName);
        joinButton = (Button) findViewById(R.id.joinButton);

        activityTime = (TextView) findViewById(R.id.activityTime);

        if (getIntent().getExtras() != null) {
            String startAddressPassed = getIntent().getExtras().getString("ActivityData");

            /*
                0: sid
                1: start
                2: destination
                3: length
                4: difficulty
                5: datetime
                6: name
             */
            if (startAddressPassed != null) {
                //startAddress.setText(startAddressPassed);
                String[] addresses = startAddressPassed.split("_");
                activityStartAddress.setText("Start: "+addresses[1]);
                activityDestinationAddress.setText("Destination: "+addresses[2]);
                activityLength.setText("Length: "+MapsActivity.getMapsData().getLengthRange(addresses[3]));
                activityDifficulty.setText("Difficulty: "+MapsActivity.getMapsData().getDifficultyRange(addresses[4]));

                //MapsActivity.getMapsData().getLengthRange()
            }
        }


    }

    public void joinPressed() {
        // TODO: Richiesta al DB per joinare l'attività.
    }
}
