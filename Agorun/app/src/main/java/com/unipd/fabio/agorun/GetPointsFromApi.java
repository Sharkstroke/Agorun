package com.unipd.fabio.agorun;

/**
 * Created by riccardo on 21/06/17.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class GetPointsFromApi extends AsyncTask<Void,Void,List<LatLng[]>> {

    private MapsActivity mapsActivity;
    private String track;

    private String sid;


    public GetPointsFromApi(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
        track = "";
    }

    protected List<LatLng[]> doInBackground (Void... voids) {
        String origin = "";
        String destination = "";
        String waypoints = "";

        List<String> points = mapsActivity.getPoints();
        int pointsSize = points.size();

        for (int i=0; i<pointsSize; i++) {
            String point = points.get(i);
            if (i==0) {
                origin = point;
            } else if (i == pointsSize - 1) {
                destination = point;
            } else {
                waypoints += (point + "|");
            }
        }

        if (! waypoints.isEmpty()) {
            waypoints = waypoints.substring(0, waypoints.length() - 1);
        }

        List<LatLng[]> list = new ArrayList<>();

        JSONObject json;

        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + origin + "&" +
                    "destination=" + destination + "&" +
                    "waypoints=" + waypoints + "&" +
                    "mode=walking&" +
                    "key=AIzaSyBeWGMKmMNOJWlYtVZaxQGnm4FZf9xi6sc");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //      Log.d("url", url.toString());

            StringBuilder sb = new StringBuilder();

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            json = new JSONObject(sb.toString());

            Log.d("JSON", json.toString());

            // Analisi del file JSON ritornato dall'API (ci sono tanti for perché ci sono tanti annidamenti)

            JSONArray routes = (JSONArray) json.get("routes");
            for (int j = 0; j < routes.length(); j++) {
                JSONObject jsonRoute = (JSONObject) routes.get(j);
                JSONArray legs = (JSONArray) jsonRoute.get("legs");
                for (int ilegs = 0; ilegs < legs.length(); ilegs++) {
                    JSONObject jsonLeg = (JSONObject) legs.get(ilegs);
                    JSONArray steps = (JSONArray) jsonLeg.get("steps");
                    for (int jsteps = 0; jsteps < steps.length(); jsteps++) {
                        JSONObject jsonStep = (JSONObject) steps.get(jsteps);
                        JSONObject jsonPolyline = (JSONObject) jsonStep.get("polyline");
                        String polyline = jsonPolyline.getString("points");
                        //             list.add(polyline);
                        track += (polyline + "!");
                        List<LatLng> polylist = PolyUtil.decode(polyline);
                        LatLng[] polyarray    = new LatLng[polylist.size()];
                        polyarray = polylist.toArray(polyarray);
                        list.add(polyarray);
                    }
                }
            }

        } catch (IOException | JSONException e) { Log.d("Error",e.getMessage()); }

        /*
        LatLng[] arrayCoordinates = new LatLng[list.size()];
        arrayCoordinates = list.toArray(arrayCoordinates);
        return arrayCoordinates;
        */
        return list;

    }

    public void setActivitySid(String sid) {
        this.sid = sid;
    }

    protected void onPostExecute (List<LatLng[]> list) {
        mapsActivity.drawLines(list);
        if(! track.isEmpty()) {
            // TODO: mapsActivity.setConnections(0);
            new ConnectDB(mapsActivity).execute("settrack", sid,track);
        }
    }

}
