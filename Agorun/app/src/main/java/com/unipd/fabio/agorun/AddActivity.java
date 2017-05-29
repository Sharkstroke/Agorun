package com.unipd.fabio.agorun;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import java.util.ListIterator;

import java.util.Timer;
import java.util.TimerTask;

// TODO: aggiungere opzione per scegliere il tempo di corsa (approssimativo). Aggiungere l'impostazione automatica dei km minimi di corsa tramite le Google Matrix APIs.

public class AddActivity extends AppCompatActivity implements GeoTask.Geo, DBConnection {

    private MapsActivity mapsActivity;

    private EditText startAddress;
    private EditText destinationAddress;
    private Spinner experienceSpinner;
    private Spinner trackLength;
    private Button createActivity;
    private Calendar calendar;
    private TextView dateview;
    private TextView timeview;

    private double latStart;
    private double latDest;
    private double lngStart;
    private double lngDest;

    private String datePar;
    private String timePar;
    private String dateTimePar;  // parametro da passare al db

    private String result = "";
    private int connections = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_track);

        startAddress = (EditText) findViewById(R.id.startAddress);
        destinationAddress = (EditText) findViewById(R.id.destinationAddress);
        trackLength = (Spinner) findViewById(R.id.trackLength);
        experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
        createActivity = (Button) findViewById(R.id.createActivityButton);

        if (getIntent().getExtras() != null) {
            String startAddressPassed = getIntent().getExtras().getString("StartingAddress");

            if (startAddressPassed != null) {
                //startAddress.setText(startAddressPassed);
                String[] addresses = startAddressPassed.split("_");
                this.startAddress.setText(addresses[0]);
                this.destinationAddress.setText(addresses[1]);
                String addressStartFixed = startAddress.getText().toString().replaceAll("\\s", "");
                String addressDestinationFixed = destinationAddress.getText().toString().replaceAll("\\s", "");
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + addressStartFixed + "&destinations=" + addressDestinationFixed + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyCW_gvTeNeb_Gzxv8kphisyTr-PZX58djQ";
                System.out.println("Start address wrong: "+url);
                startSearch(url);
            }
        }


        mapsActivity = MapsActivity.getMapsData();

        // TODO: creare il button per intercettare l'avvenuta creazione della attività e creare il marker sulla mappa.



        // Controllo quando l'utente smette di digitare l'indirizzo di destinazione, avendo già completato quello di partenza.
        destinationAddress.addTextChangedListener(
                new TextWatcher() {
                    boolean isTyping = false;
                    Timer timer = new Timer();
                    long delay = 1000;
                      @Override
                      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                      }

                      @Override
                      public void onTextChanged(CharSequence s, int start, int before, int count) {
                          createActivity.setEnabled(false);
                      }

                      @Override
                      public void afterTextChanged(Editable s) {
                          createActivity.setEnabled(false);
                          if (!isTyping) {
                              isTyping = true;
                          }
                          timer.cancel();
                          timer = new Timer();
                          timer.schedule(new TimerTask() {
                              @Override
                              public void run() {
                                  isTyping = false;
                                  if (startAddress != null && startAddress.getText().toString().trim().length() > 0) {

                                      // Elimino gli spazi tra le parole per passare gli indirizzi come url in modo corretto
                                      String addressStartFixed = startAddress.getText().toString().replaceAll("\\s", "");
                                      String addressDestinationFixed = destinationAddress.getText().toString().replaceAll("\\s", "");

                                      // Lancio l'url passandogli gli indirizzi e la API key.
                                      String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + addressStartFixed + "&destinations=" + addressDestinationFixed + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyCW_gvTeNeb_Gzxv8kphisyTr-PZX58djQ";
                                      System.out.println("Destination address wrong: "+url);

                                      // Avvio il parsing ed il calcolo dei km.
                                      //new GeoTask(AddActivity.this).execute(url);
                                      this.cancel();
                                      startSearch(url);
                                  }
                              }
                          }, delay);
                      }
                }
        );

        // Controllo quando l'utente smette di digitare l'indirizzo di partenza, avendo già completato quello di destinazione.
        startAddress.addTextChangedListener(
                new TextWatcher() {
                    boolean isTyping = false;
                    Timer timer = new Timer();
                    long delay = 1000;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        createActivity.setEnabled(false);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        createActivity.setEnabled(false);
                        if (!isTyping) {
                            isTyping = true;
                        }
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                if (destinationAddress != null && destinationAddress.getText().toString().trim().length() > 0) {

                                    // Elimino gli spazi tra le parole per passare gli indirizzi come url in modo corretto
                                    String addressStartFixed = startAddress.getText().toString().replaceAll("\\s", "");
                                    String addressDestinationFixed = destinationAddress.getText().toString().replaceAll("\\s", "");

                                    // Lancio l'url passandogli gli indirizzi e la API key.
                                    String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + addressStartFixed + "&destinations=" + addressDestinationFixed + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyCW_gvTeNeb_Gzxv8kphisyTr-PZX58djQ";
                                    System.out.println("Start address wrong: "+url);

                                    // Avvio il parsing ed il calcolo dei km.
                                    //new GeoTask(AddActivity.this).execute(url);
                                    this.cancel();
                                    startSearch(url);
                                }
                            }
                        }, delay);
                    }
                }
        );

        // Setto la data di oggi

        calendar = Calendar.getInstance();

        dateview = (TextView) findViewById(R.id.datetextview);
        dateview.setTypeface(null, Typeface.BOLD);


        DateFormat df = DateFormat.getDateInstance();
        dateview.setText(df.format(calendar.getTime()));

        int yyyy = calendar.get(Calendar.YEAR);
        int mm   = calendar.get(Calendar.MONTH) + 1;
        int dd   = calendar.get(Calendar.DAY_OF_MONTH);

        datePar = yyyy + "-" + mm + "-" + dd;

        // setto l'ora corrente

        timeview = (TextView) findViewById(R.id.timetextview);
        timeview.setTypeface(null, Typeface.BOLD);

        int hour   = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timeview.setText(checkHourMinute(hour,minute));
        timePar = checkHourMinute(hour,minute);

        dateTimePar = datePar + "%20" + timePar;

        Toast.makeText(getApplicationContext(),dateTimePar,Toast.LENGTH_LONG).show();

    }

    public void startSearch(final String url) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Lancio la ricerca del percorso minimo tra partenza e destinazione, in background.
                    new GeoTask(AddActivity.this).execute(url);
                } catch(Exception e) {
                    System.out.println(e.toString());
                }
            }
        });
    }

    // Metodo richiamato al click sul button di creazione attività: serve per ottenere le coordinate degli indirizzi selezionati.
    public void geoLocate(View v) throws IOException {
            // Ottengo le coordinate geografiche del punto di partenza così da disegnare il Marker.
            EditText et = (EditText) findViewById(R.id.startAddress);
            String start = et.getText().toString();
            System.out.println("STARTADDRESS=" + start);
            Geocoder gc = new Geocoder(this);
            List<Address> list = null;

            list = gc.getFromLocationName(start, 1);

            Address add = list.get(0);
            System.out.println("ADDRESS: " + add.getAddressLine(0)+" "+add.getLocality());
            String locality = add.getLocality();

            latStart = add.getLatitude();
            lngStart = add.getLongitude();




            /*
                // Questa parte dovrà essere sotituita dal caricamento delle coordinate sul database, non dovrebbe essere necessario disegnare un Marker in corrispondenza
                // anche del punto di arrivo.
            */
            et = (EditText) findViewById(R.id.destinationAddress);
            String destination = et.getText().toString();


            list = gc.getFromLocationName(destination, 1);

            Address add2 = list.get(0);
            locality = add.getLocality();

            latDest = add2.getLatitude();
            lngDest = add2.getLongitude();


            final Spinner experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
            String experience = experienceSpinner.getSelectedItem().toString();

            mapsActivity.addMarkerToMap(latStart, lngStart, new String(add.getAddressLine(0)+" "+add.getLocality()), new String(add2.getAddressLine(0)+ " "+add2.getLocality()), trackLength.getSelectedItem().toString(), experience.toString());
            finish();

            //mapsActivity.addMarkerToMap(latDest, lngDest, "DESTINATION");

            connect();

    }

    private void connect() {
        connections++;
/*
        double longStart = 16.3679;
        double latEnd = 24.3753;
        double latStart = 3.3424;
        double longEnd = 12.5903;*/

        final Spinner lengthSpinner = (Spinner) findViewById(R.id.trackLength);
        final Spinner diffSpinner   = (Spinner) findViewById(R.id.ExperienceSpinner);

        if (!startAddress.getText().toString().equals("") && !startAddress.getText().toString().equals("")) {
            new ConnectDB(this).execute(
                    "createrun",
                    latStart + "",
                    lngStart + "",
                    latDest + "",
                    lngDest + "",
                    lengthSpinner.getSelectedItemPosition() + "",
                    diffSpinner.getSelectedItemPosition() + "",
                    dateTimePar);
        } else {
            Toast.makeText(getApplicationContext(),"Insert Addresses",Toast.LENGTH_SHORT).show();
        }
    }

    public void onTaskCompleted(ArrayList<String> ls) {

        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            Toast.makeText(this, "Connection Problem", Toast.LENGTH_SHORT).show();
            return;
        }

        ListIterator it = ls.listIterator();
        while (it.hasNext()) {
            result = result + (it.next());
        }

        if (! result.equals("Success") && ! result.equals("Failed")) {
            result = "";
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            connect();
        } else if (result.equals("Success")) {
            Toast.makeText(this, "Activity Created!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Creation of the activity failed", Toast.LENGTH_SHORT).show();
        }
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
        createActivity.setEnabled(true);
    }

    public void onClickDate (View view) {
        calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet (DatePicker view,int year, int month, int day){
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                DateFormat df = DateFormat.getDateInstance();
                dateview.setText(df.format(calendar.getTime()));

                month++;
                datePar = year + "-" + month + "-" + day;

                dateTimePar = datePar + "%20" + timePar;

                Toast.makeText(getApplicationContext(),dateTimePar,Toast.LENGTH_LONG).show();
            }
        };

        new DatePickerDialog(AddActivity.this,R.style.DialogThemeCustom, date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void onClickTime (View view) {

        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                timeview.setText(checkHourMinute(selectedHour,selectedMinute));

                timePar = checkHourMinute(selectedHour,selectedMinute);

                dateTimePar = datePar + "%20" + timePar;

                Toast.makeText(getApplicationContext(),dateTimePar,Toast.LENGTH_LONG).show();
            }
        };

        new TimePickerDialog(AddActivity.this, R.style.DialogThemeCustom, time,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show();
    }

    private String checkHourMinute (int hour, int minute) {
        String sh = hour + "";
        String sm = minute + "";
        if (hour < 10) {
            sh = "0" + sh;
        }
        if (minute < 10) {
            sm = "0" + sm;
        }
        return sh + ":" + sm;
    }


}
