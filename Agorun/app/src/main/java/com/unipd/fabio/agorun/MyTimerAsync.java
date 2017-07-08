package com.unipd.fabio.agorun;

import android.os.AsyncTask;

/**
 * Created by fabio on 08/07/17.
 */

public class MyTimerAsync extends AsyncTask<Void, Void, Void> {

    private MapsActivity mapsActivity;

    @Override
    protected Void doInBackground(Void... params) {
        mapsActivity = MapsActivity.getMapsData();
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        mapsActivity.setStartMonitoringVisibility(1);
    }
}
