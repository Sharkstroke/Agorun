package com.unipd.fabio.agorun;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ArrayAdapter;
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

    private static final boolean IS_MY_ACTIVITY = true;

    private double latStart;
    private double latDest;
    private double lngStart;
    private double lngDest;
    private Address add;
    private Address add2;

    private String datePar;
    private String timePar;
    private String dateTimePar;  // parametro da passare al db

    private String result = "";
    private int connections = 0;

    private double preciseStartLat;
    private double preciseStartLng;
    private double preciseDestLat;
    private double preciseDestLng;

    private boolean PRECISE_COORDS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_track_restyled);

        calendar = Calendar.getInstance();
        setGraphicalObjects();



        if (getIntent().getExtras() != null) {
            String startAddressPassed = getIntent().getExtras().getString("StartingAddress");

            if (startAddressPassed != null) {
                this.PRECISE_COORDS = true;

                String[] addresses = startAddressPassed.split("_");
                parseFullAddress(addresses);

                String addressStartFixed = startAddress.getText().toString().replaceAll("\\s", "");
                String addressDestinationFixed = destinationAddress.getText().toString().replaceAll("\\s", "");
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + addressStartFixed + "&destinations=" + addressDestinationFixed + "&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyCW_gvTeNeb_Gzxv8kphisyTr-PZX58djQ";
                startSearch(url);
            }
        }


        mapsActivity = MapsActivity.getMapsData();

        // TODO: creare il button per intercettare l'avvenuta creazione della attività e creare il marker sulla mappa.



        // Controllo quando l'utente smette di digitare l'indirizzo di destinazione, avendo già completato quello di partenza.
        setDestinationAddressListener();

        // Controllo quando l'utente smette di digitare l'indirizzo di partenza, avendo già completato quello di destinazione.
        setStartAddressListener();

        // Setto la data di oggi


        int yyyy = calendar.get(Calendar.YEAR);
        int mm   = calendar.get(Calendar.MONTH) + 1;
        int dd   = calendar.get(Calendar.DAY_OF_MONTH);

        datePar = yyyy + "-" + mm + "-" + dd;

        // setto l'ora corrente
        int hour   = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timeview.setText(checkHourMinute(hour,minute));
        timePar = checkHourMinute(hour,minute);

        myHour = timePar;
        myDate = datePar;


        dateTimePar = datePar + "%20" + timePar;

        Toast.makeText(getApplicationContext(),dateTimePar,Toast.LENGTH_LONG).show();

    }

    private String myDate;
    private String myHour;

    private void parseFullAddress(String[] addresses) {
        this.startAddress.setText(addresses[0]);
        this.destinationAddress.setText(addresses[1]);
        this.preciseStartLat = Double.parseDouble(addresses[2]);
        this.preciseStartLng = Double.parseDouble(addresses[3]);
        this.preciseDestLat = Double.parseDouble(addresses[4]);
        this.preciseDestLng = Double.parseDouble(addresses[5]);
    }

    private void setGraphicalObjects() {
        startAddress = (EditText) findViewById(R.id.startAddress);
        destinationAddress = (EditText) findViewById(R.id.destinationAddress);
        trackLength = (Spinner) findViewById(R.id.trackLength);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.trackLength, R.layout.spinner_item);
        trackLength.setAdapter(adapter);
        experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.experience, R.layout.spinner_item);
        experienceSpinner.setAdapter(adapter2);
        createActivity = (Button) findViewById(R.id.createActivityButton);
        dateview = (TextView) findViewById(R.id.datetextview);
        dateview.setTypeface(null, Typeface.BOLD);
        DateFormat df = DateFormat.getDateInstance();
        dateview.setText(df.format(calendar.getTime()));
        timeview = (TextView) findViewById(R.id.timetextview);
        timeview.setTypeface(null, Typeface.BOLD);

        TextView textView = (TextView) findViewById(R.id.trackLengthChoice);
        SpannableString content = new SpannableString("Select the length range of the track");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        TextView textView2 = (TextView) findViewById(R.id.chooseExperience);
        SpannableString content2 = new SpannableString("Select the difficulty of the track");
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        textView2.setText(content2);

        TextView textView3 = (TextView) findViewById(R.id.chooseDate);
        SpannableString content3 = new SpannableString("Select the date of the activity");
        content3.setSpan(new UnderlineSpan(), 0, content3.length(), 0);
        textView3.setText(content3);


        TextView textView4 = (TextView) findViewById(R.id.chooseTime);
        SpannableString content4 = new SpannableString("Choose the starting time");
        content4.setSpan(new UnderlineSpan(), 0, content4.length(), 0);
        textView4.setText(content4);


    }

    private void setDestinationAddressListener() {
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
    }

    private void setStartAddressListener() {
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
                                    //System.out.println("Start address wrong: "+url);

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
            Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = null;


                list = gc.getFromLocationName(start, 1);

                add = list.get(0);
                //System.out.println("ADDRESS: " + add.getAddressLine(0)+", "+add.getLocality());

                String locality = add.getLocality();
            if (!this.PRECISE_COORDS) {
                latStart = add.getLatitude();
                lngStart = add.getLongitude();
                System.out.println("LAT Start = "+latStart+", LNG Start = "+lngStart);
            } else {
                latStart = this.preciseStartLat;
                lngStart = this.preciseStartLng;
            }


            et = (EditText) findViewById(R.id.destinationAddress);
            String destination = et.getText().toString();

            //System.out.println("DESTINATION = "+destination);

                list = gc.getFromLocationName(destination, 1);

                add2 = list.get(0);
                //System.out.println("L'INDIRIZZO COMPLETO E': "+add2.getAddressLine(0) + ", "+add2.getLocality());
            if (!this.PRECISE_COORDS) {
                latDest = add2.getLatitude();
                lngDest = add2.getLongitude();
                System.out.println("LAT Dest  = "+latDest+", LNG Dest = "+lngDest);
            } else {
                latDest = this.preciseDestLat;
                lngDest = this.preciseDestLng;
            }


            experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
    //        String experience = experienceSpinner.getSelectedItem().toString();
    
        // ATTENZIONE!!! SID GIOCATTOLO

            finish();

            //mapsActivity.addMarkerToMap(latDest, lngDest, "DESTINATION");
            connect();
            //MapsActivity.getMapsData().setStartMonitoringVisibility(1);
            if (MapsActivity.getMapsData().isTimeForMonitoring()) {
                MapsActivity.getMapsData().setStartMonitoringVisibility(true);
            }
        } catch(Exception e) {
            System.out.println("Geolocalizzazione fallita.");
        }
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

        result = ls.get(0);

        if (! result.equals("Success") && ! result.equals("Failed")) {
            result = "";
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            connect();
        } else if (result.equals("Success")) {
            Toast.makeText(this, "Activity Created!", Toast.LENGTH_SHORT).show();
            mapsActivity.addMarkerToMap(IS_MY_ACTIVITY, ls.get(1),latStart, lngStart,
                    new String(add.getAddressLine(0)+", "+add.getLocality()),
                    new String(add2.getAddressLine(0)+ ", "+add2.getLocality()));

            System.out.println("MY HOUR = "+myHour);
            MySharedPreferencesHandler.putSharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, ls.get(1));
            MySharedPreferencesHandler.putSharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, myHour+":00");
            MySharedPreferencesHandler.putSharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate, myDate);

            if (MapsActivity.getMapsData().isTimeForMonitoring()) {
                MapsActivity.getMapsData().setStartMonitoringVisibility(true);
            }
        } else {
            Toast.makeText(this, "Creation of the activity failed", Toast.LENGTH_SHORT).show();
        }

        result = "";
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
