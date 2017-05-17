package com.unipd.fabio.agorun;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.List;

/**
 * Created by fabio on 15/05/17.
 */


// TODO: aggiungere opzione per scegliere il tempo di corsa (approssimativo). Aggiungere l'impostazione automatica dei km minimi di corsa tramite le Google Matrix APIs.

public class AddActivity extends AppCompatActivity implements GeoTask.Geo {

    private MapsActivity mapsActivity;

    private EditText startAddress;
    private EditText destinationAddress;
    private Spinner experienceSpinner;
    private Spinner trackLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_track);

        mapsActivity = MapsActivity.getMapsData();

        // TODO: creare il button per intercettare l'avvenuta creazione della attività e creare il marker sulla mappa.

        startAddress = (EditText) findViewById(R.id.startAddress);
        destinationAddress = (EditText) findViewById(R.id.destinationAddress);
        trackLength = (Spinner) findViewById(R.id.trackLength);
        experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);

        destinationAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Niente.
                } else {
                    // Controllo che il campo startAddress sia già stato compilato
                    if (startAddress != null && startAddress.getText().toString().trim().length() > 0) {

                        // Elimino gli spazi tra le parole per passare gli indirizzi come url in modo corretto
                        String addressStartFixed = startAddress.getText().toString().replaceAll("\\s", "");
                        String addressDestinationFixed = destinationAddress.getText().toString().replaceAll("\\s", "");

                        // Lancio l'url passandogli gli indirizzi e la API key.
                        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + addressStartFixed + "&destinations=" + addressDestinationFixed + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyCW_gvTeNeb_Gzxv8kphisyTr-PZX58djQ";
                        System.out.println(url);

                        // Avvio il parsing ed il calcolo dei km.
                        new GeoTask(AddActivity.this).execute(url);
                    }
                }
            }
        });
    }

    // Metodo richiamato al click sul button di creazione attività: serve per ottenere le coordinate degli indirizzi selezionati.
    public void geoLocate(View v) throws IOException {
        // Ottengo le coordinate geografiche del punto di partenza così da disegnare il Marker.
        EditText et = (EditText) findViewById(R.id.startAddress);
        String start = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(start, 1);
        Address add = list.get(0);
        String locality = add.getLocality();

        double latStart = add.getLatitude();
        double lngStart = add.getLongitude();




        /*
            // Questa parte dovrà essere sotituita dal caricamento delle coordinate sul database, non dovrebbe essere necessario disegnare un Marker in corrispondenza
            // anche del punto di arrivo.
        */
        et = (EditText) findViewById(R.id.destinationAddress);
        String destination = et.getText().toString();

        list = gc.getFromLocationName(destination, 1);
        Address add2 = list.get(0);
        locality = add.getLocality();

        double latDest = add.getLatitude();
        double lngDest = add.getLongitude();


        final Spinner experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
        String experience = experienceSpinner.getSelectedItem().toString();

        mapsActivity.addMarkerToMap(latStart, lngStart, add.getAddressLine(0), add2.getAddressLine(0), trackLength.getSelectedItem().toString(), experience.toString());
        finish();

        //mapsActivity.addMarkerToMap(latDest, lngDest, "DESTINATION");
    }

    public void clickbutton(View v) {
        final Spinner experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
        String experience = experienceSpinner.getSelectedItem().toString();
        System.out.println("Ho scelto: "+experience.toString());
    }

    @Override
    public void setDouble(String min) {
        String res[] = min.split(",");
        int kilom = Integer.parseInt(res[1])/1000;
        if (kilom >= 0 && kilom <= 5) {
            trackLength.setSelection(0);
        } else if (kilom > 5 && kilom <= 10) {
            trackLength.setSelection(1);
        } else if (kilom > 10 && kilom <= 15) {
            trackLength.setSelection(2);
        } else if (kilom > 15 && kilom <= 20) {
            trackLength.setSelection(3);
        } else if (kilom > 20) {
            trackLength.setSelection(4);
        }
    }
}
