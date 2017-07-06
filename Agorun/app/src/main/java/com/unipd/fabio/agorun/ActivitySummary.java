package com.unipd.fabio.agorun;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivitySummary extends Activity implements DBConnection {

    private TextView organizerName;
    private TextView organizerSurname;
    private TextView activityStartAddress;
    private TextView activityDestinationAddress;
    private TextView activityLength;
    private TextView activityDifficulty;
    private TextView creatorName;
    private TextView activityTime;

    private Button joinButton;

    private String sid;
    private int connections;

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
                sid = addresses[0];
                activityStartAddress.setText("Start: "+addresses[1]);
                activityDestinationAddress.setText("Destination: "+addresses[2]);
                activityLength.setText("Length: "+MapsActivity.getMapsData().getLengthRange(addresses[3]));
                activityDifficulty.setText("Difficulty: "+MapsActivity.getMapsData().getDifficultyRange(addresses[4]));

                //MapsActivity.getMapsData().getLengthRange()
            }
        }


    }

    public void joinPressed(View view) {
        connections = 0;
        connect();
    }

    private void connect() {
        new ConnectDB(this).execute("joinrun",sid);
    }

    public void onTaskCompleted (ArrayList<String> ls) {

        if (connections >= 5) {
            Toast.makeText(getApplicationContext(),"Connection problem",Toast.LENGTH_SHORT).show();
            return;
        }

        if (! ls.get(0).equals("Join Riuscito")) {
            connections++;
            connect();
        } else {
            Toast.makeText(getApplicationContext(),sid,Toast.LENGTH_SHORT).show();
        }

    }
}
