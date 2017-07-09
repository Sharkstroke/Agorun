package com.unipd.fabio.agorun; /**
 * package com.unipd.fabio.provamaps;
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,
        DBConnection {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Marker whereAmI;

    // TROVARE UNA SOLUZIONE UN PO' PIU' APPROPRIATA DI QUESTA: IN OGNI CASO QUESTO FUNZIONA.
    private static MapsActivity mact;

    private LocationManager locationManager;
    private LocationManager m_locationManager;
    private Location l;
    private String provider;
    private ImageButton hamburgerMenu;
    private TextView search_tw;
    private TextView startingAddressTop;
    private TextView destinationAddressTop;
    private ProgressBar progressBar;
    private Button startMonitoring;


    private static final int THRESHOLD_HOUR = 19;
    private static final int POSITION_FREQUENCY = 15000;

    private static boolean IS_MONITORING;


    private LocationListener locationListener;

    private List<LatLng> routePoints = new LinkedList<>();

    private Timer timer;

    private final MyLocationRegistered formerPos = new MyLocationRegistered();

    private Map<Marker, String> markersMap = new HashMap<>();

    private Map<Double, Double> positionsRecorded = new HashMap<>();

    private Map<Marker, Marker> destinationsMap = new HashMap<>();

    private Map<String, Marker> destinationMarkersMap = new HashMap<>();

    private int connections = 0;

    private String[] session_info;
    private Marker markerclicked;
    private String sidclicked;

    Timer cameraSteadyTimer = new Timer();

    Timer myTimer = new Timer();


    private LatLng northEast;
    private LatLng southWest;

    // Coordinates at top right and bottom left of the screen: used for selecting and showing the visible activities only.
    private double latNorthEast;
    private double lngNorthEast;
    private double latSouthWest;
    private double lngSouthWest;
    private String sids = "";

    private Fragment fragment1;

    private String start;
    private String stop;
    private String km;
    private String exp;
    private String hour;
    private String date;


    private OnSwipeTouchListener GestureManager;

    private List<String> points = new ArrayList<String>();

    private Circle startArea;
    private Circle endArea;

    private static final int START = 0;
    private static final int DESTINATION = 1;

    PowerManager powerManager;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        new ConnectDB(this).execute("getmessage");
        // Mod Riccardo
        Intent intent = getIntent();
        if (intent.hasExtra("message")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete entry")
                    .setMessage(intent.getExtras().getString("message"))
                    .setCancelable(true)
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
        }

        // Setting an instance of this class in order to use its methods from the outside.
        mact = this;

        String svcName = Context.LOCATION_SERVICE;

        locationManager = (LocationManager) getSystemService(svcName);
        //setLocalizationMethods();
        // Setting all the necessary to look for a matching provider with our preferences.
        //setCriteria();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setGraphicalObjects();

        // Creating a listener for the search bar: once the user launches the search, this onClick method is callled.
        setSearchListener(search_tw);

         /*Giulio mod.*/
        setHamburgerMenuListener(hamburgerMenu);
         /*Giulio mod.*/

        setStartMonitoringListener(startMonitoring);

        setGestureManagerListener();

        if (isTimeForMonitoring()) {
            startMonitoring.setVisibility(View.VISIBLE);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isInternetAvailable()) {
            createInternetAvailabilityErrorAlert();
        }

        //new Thread(new MyTimer()).start();

        //runOnUiThread(new MyTimer());

        //createCircle();

    }


    // Setto i criteri per la localizzazione.
    private void setCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        provider = locationManager.getBestProvider(criteria, true);
    }

    private void setGraphicalObjects() {
        this.search_tw = (TextView) findViewById(R.id.search_bar);
        this.startingAddressTop = (TextView) findViewById(R.id.startingPointInMain);
        this.destinationAddressTop = (TextView) findViewById(R.id.destinationPointInMain);
        this.startMonitoring = (Button) findViewById(R.id.startMonitorButton);
        this.hamburgerMenu = (ImageButton) findViewById(R.id.button1);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);
    }

    private void setStartMonitoringListener(final Button startMonitoring) {
        startMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String joinedActivitySid = MySharedPreferencesHandler.getMySharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, "");
                // TODO: inizio monitoraggio attività dell'utente.
                if (IS_MONITORING) {
                    /** STO MONITORANDO: IL MONITORING STA PER ESSERE DISATTIVATO.*/

                    // TODO doppia conferma.

                    // startMonitoring.setText("START");

                    //Toast.makeText(getApplicationContext(),"ismonitoring",Toast.LENGTH_SHORT);
                    
                    if (checkIfPointReached(DESTINATION)) {
                        // TODO: Assegnazione punteggio.

                        IS_MONITORING = false;

                        // Aumento del ranking.
                        new ConnectDB(MapsActivity.this).execute("increaserank");

                        GetPointsFromApi getPointsFromApi = new GetPointsFromApi(MapsActivity.this);
                        getPointsFromApi.setActivitySid(joinedActivitySid);
                        getPointsFromApi.execute();

                        MySharedPreferencesHandler.removeMySharedPreferences(getApplicationContext(),
                            MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid,
                            MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour,
                            MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate
                        );

                        startMonitoring.setVisibility(View.INVISIBLE);

                        for (Marker m : markersMap.keySet()) {
                            m.setVisible(true);
                        }

                        for (Marker m : destinationMarkersMap.values()) {
                            m.setVisible(false);
                        }

                        // Tolgo i cerchi colorati una volta arrivato.


                    } else {
                        // TODO: mostra doppia conferma.
                        AlertDialog.Builder builder= new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("Are you sure?");
                        builder.setMessage("It seems you're not arrived! Do you really want to stop?");
                        builder.setCancelable(false);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setPositiveButton("Yes, I can't take it anymore",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ConnectDB(MapsActivity.this).execute("disjoinrun",joinedActivitySid);
                                IS_MONITORING = false;
                                removeStartingCircled();
                                removeDestinationCircle();

                                MySharedPreferencesHandler.removeMySharedPreferences(getApplicationContext(),
                                        MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid,
                                        MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour,
                                        MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate
                                );
                                startMonitoring.setVisibility(View.INVISIBLE);
                                startMonitoring.setText("START");
                                hamburgerMenu.setClickable(true);
                                hamburgerMenu.setEnabled(true);
                                removeStartingCircled();
                                removeDestinationCircle();

                                for (Marker m : markersMap.keySet()) {
                                    m.setVisible(true);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("No! I can do it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                } else {
                    /** NON STO ANCORA MONITORANDO: IL MONITORING INIZIA ORA.*/

                    createCircle();
                    if (checkIfPointReached(START)) {
                        startMonitoring.setText("STOP");
                        IS_MONITORING = true;
                        hamburgerMenu.setClickable(false);
                        hamburgerMenu.setEnabled(false);


                        Marker startMarker; // TODO: valutare se serve.
                        String[] parsed;

                        for (Marker m : markersMap.keySet()) {
                            parsed = markersMap.get(m).split("_");
                            if (parsed[0].equals(joinedActivitySid)) {
                                startMarker = m;
                            } else {
                                m.setVisible(false);
                            }
                        }

                        /** Rendo visibile il marker di destinazione.*/
                        if (destinationMarkersMap.containsKey(joinedActivitySid)) {
                            Marker destMarker = destinationMarkersMap.get(joinedActivitySid);
                            destMarker.setVisible(true);

                            createDestinationCircle(destMarker.getPosition());

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Get closer to the starting point!", Toast.LENGTH_SHORT).show();
                    }

                    /*for (Marker m : markersMap.keySet()) {
                        if (!m.getId().equals(joinedActivityMarkerId)) {
                            m.setVisible(!m.isVisible());
                        }
                    }*/
                }
            }
        });
    }

    public void setStartMonitoringVisibility(int toTurn) {
        // 0 = false; 1 = true.
        if (isTimeForMonitoring()) {
            startMonitoring.setVisibility(toTurn);
        }
    }

    private class MyTimer extends TimerTask {

        @Override
        public void run() {

            while (!isTimeForMonitoring()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTimeForMonitoring()) {
                            System.out.println("Prima");
                            startMonitoring.setVisibility(View.VISIBLE);
                            System.out.println("Dopo");
                        }
                    }
                });
            }
        }
    }

    private void setSearchListener(TextView search_tw) {
        search_tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Creating the new intent to which we pass the static instance of the class.
                    Intent intent = new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(mact);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.i("1", e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.i("2", e.toString());
                }
            }
        });
    }

    private void setHamburgerMenuListener(final ImageButton hamburgerMenu) {
        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                PopupMenu popup = new PopupMenu(MapsActivity.this, hamburgerMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_maps, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                MapsActivity.this,
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();

                        if (item.getItemId() == R.id.one) {
                            Intent intent = new Intent(MapsActivity.this, AddActivity.class);
                            startActivity(intent);
                        } if (item.getItemId() == R.id.popularTracks) {
                            Intent intent = new Intent(MapsActivity.this, VotedTracks.class);
                            startActivity(intent);
                        } if (item.getItemId() == R.id.profile) {
                            Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        } if (item.getItemId() == R.id.leaderboard) {
                            Intent intent = new Intent(MapsActivity.this, LeaderboardActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }

        });
    }

    private void setGestureManagerListener() {
        GestureManager = (new OnSwipeTouchListener(MapsActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MapsActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(MapsActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(MapsActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(MapsActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Static method used to get the static instance of this class previously created.
    public static MapsActivity getMapsData() {
        return mact;
    }

    // In the onResume we're going to geolocalize the user again.
    @Override
    protected void onResume() {
        super.onResume();
        int i = 0;
        float accuracy = 0.0f;
        if (mMap != null) {
            if (l == null) {
                do{
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                    l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (l != null) {
                        accuracy = l.getAccuracy();
                    }
                    i++;
                } while (l == null && accuracy == 0.0 && accuracy > 20.0 && i < 3);
            } else {
                i = 0;
                accuracy = 0.0f;
                do {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                    l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (l != null) {
                        accuracy = l.getAccuracy();
                    }
                    i++;
                } while(l == null && accuracy == 0.0 && accuracy > 20.0 && i < 3);
            }

            //createCircle();
            //checkIfPointReached();
            // TODO: inserire check periodico dell'orario.
            if (isTimeForMonitoring()) {
                startMonitoring.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isTimeForMonitoring() {
        // Ottengo l'ora corrente.
        Date date = new Date();
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat minutes = new SimpleDateFormat("mm");
        String currentHour = hour.format(date);
        String currentMinutes = minutes.format(date);

        // Ottengo l'orario dell'attività joinata.
        String myFullHour = MySharedPreferencesHandler.getMySharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, "");
        if (!myFullHour.isEmpty()) {
            String[] startParsed = myFullHour.split(":");
            String[] newHour = Arrays.copyOf(startParsed, startParsed.length - 1);
            // Controllo se l'ora attuale corrisponde a quella dell'attività joinata.
            if (newHour[0].equals(currentHour)) {
                // Controllo se il minuto corrente è maggiore od uguale al minuto corrispondente all'inzio dell'attività joinata.
                return (currentMinutes.equals(newHour[1]) || Integer.parseInt(currentMinutes) > Integer.parseInt(newHour[1]));
            } else if (Integer.parseInt(currentHour) >= Integer.parseInt(newHour[0])+1) {
                return true;
            }
            return false;
        }
        return false;
    }

    // Method called automatically when the search ends. It allows to see the results and to move the camera to the specified
    // point.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.

                Place place = PlaceAutocomplete.getPlace(this, data);
                search_tw.setText(place.getAddress().toString());
                //Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                LatLng latLngFound = place.getLatLng();

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFound, 17));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // Method returning a LatLng object from a Location one.
    public static LatLng fromLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());

    }

    // Method called whenever the phone changes its location.
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            // Update of the former position with the current one: by doing so, we can refer to it and to draw the track.
            formerPos.setPosition(location);
            // Update the map location.

            LatLng latlng = fromLocationToLatLng(location);

            if (mMap != null) {
                // Update of the camera position.
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            }

            if (whereAmI != null) {
                whereAmI.remove();
            }

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Translation of the latitude and longitude of the current position into an address.
            /*Geocoder gc = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
            } catch (IOException e) {
                Log.d("Exception", "IO Exception", e);
            }*/
        }
    }

    // Drawing the track.
    private void drawTrack(Location location) {
        mMap.addPolyline((new PolylineOptions().add(new LatLng(formerPos.getPosition().getLatitude(), formerPos.getPosition().getLongitude()),
                new LatLng(location.getLatitude(), location.getLongitude())
        )).width(5).color(Color.RED).geodesic(true));
        /*Polyline route = mMap.addPolyline(new PolylineOptions()
                .width(15)
                .color(Color.GREEN)
                .geodesic(true)
        );
        route.setPoints(routePoints);*/
    }

    private void drawTrack(LatLng ll1, LatLng ll2) {
        mMap.addPolyline(new PolylineOptions().add(ll1, ll2).width(7).color(Color.RED).geodesic(true));
    }

    // Recording the new position
    private void recordPosition(Location location) {
        positionsRecorded.put(location.getLatitude(), location.getLongitude());
        routePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //MapFragment map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            //.getMap();
            MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            map.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                // Configure the map display options

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {}

    // Method called when the user taps on the map.
    @Override
    public void onMapClick(LatLng latLng) {
        progressBar.setVisibility(View.GONE);
        disableDestinationMarkers();

        // new GetPointsFromApi(this).execute(); // TODO: METTERLO NEL PUNTO GIUSTO

        //Toast.makeText(getApplicationContext(), ""+ checkIfPointReached(), Toast.LENGTH_SHORT).show();
        // Mando i punti all'Api, poi mi ritornerà le linee lungo la strada
    }

    public void drawLines (List<LatLng[]> list) {
        //Polyline line;
        for (LatLng[] latLngs : list) {
            mMap.addPolyline(new PolylineOptions().add(latLngs)).setColor(Color.RED);
        }
    }

    public List<String> getPoints() {
        return points;
    }

    private Marker tempMarker;
    String startingAdd = "";
    String destinationAdd = "";

    private LatLng startLatLng;
    private LatLng destLatLng;

    // Method called during long-time click.
    @Override
    public void onMapLongClick(LatLng latLng) {
        //disableDestinationMarkers();

        if (!IS_MONITORING) {

            // Getting the latitude and longitude of the point where the user has pressed.
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            // Starting geocoding and translating the latitude and longitude into an address.
            Geocoder gc = new Geocoder(this);
            try {
                // If this is the first long-time click, it means we're setting the starting point.
                if (tempMarker == null) {
                    List<Address> list = null;
                    list = gc.getFromLocation(latitude, longitude, 1);

                    Address add = list.get(0);
                    startingAdd = add.getAddressLine(0) + ", " + add.getLocality();
                    startingAddressTop.setText("Starting point: " + startingAdd);
                    tempMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()).flat(false));

                    // Adding the tempMarker object to the markersMap map, with an empty String value associated since we just need
                    // this marker as a reference when we'll use it for creating the activity. For now, we don't have any information
                    // tho.
                    markersMap.put(tempMarker, "");
                    startLatLng = latLng;
                } else {
                    // If we're here, it means we're now setting the destination point.
                    List<Address> list = null;
                    list = gc.getFromLocation(latitude, longitude, 1);
                    Address add = list.get(0);
                    destinationAdd = add.getAddressLine(0) + ", " + add.getLocality();

                    destLatLng = latLng;

                    destinationAddressTop.setText("Destination address: " + destinationAdd);
                    //mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).flat(false));

                    // I launch a new AddActivity Intent, to which I'm passing parameters such as start address destination address,
                    // start and destination latitude and longitude.
                    Intent newActivity = new Intent(MapsActivity.this, AddActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("StartingAddress", new String(startingAdd + "_" + destinationAdd + "_" +
                            startLatLng.latitude + "_" + startLatLng.longitude + "_" +
                            destLatLng.latitude + "_" + destLatLng.longitude));
                    newActivity.putExtras(bundle);
                    startActivity(newActivity);
                    // Removing the tempMarker object from the google map.
                    tempMarker.remove();
                    tempMarker = null;
                    startingAdd = "";
                    destinationAdd = "";
                }
            } catch (Exception e) {
                Log.i("GG", e.toString());
            }
            //mMap.addMarker(new MarkerOptions().position(latLng).title("Lat = "+latLng.latitude+", Long = "+latLng.longitude));
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        disableDestinationMarkers();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        LatLng newCameraPosition = new LatLng(marker.getPosition().latitude-0.001, marker.getPosition().longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCameraPosition, 17));
        //     marker.showInfoWindow();//////////// Mod riccardo
        connections = 0;
        markerclicked = marker;
        //Log.d("Marker clicked", "fadlk");
        if (markersMap.containsKey(marker)) {
            String details = markersMap.get(marker);
            String[] strings = details.split("_");
            sidclicked = strings[0];
            Toast.makeText(getApplicationContext(),sidclicked,Toast.LENGTH_LONG).show();
            if (isInternetAvailable()) {
                connect("getinforun", sidclicked);
            }


        }
        progressBar.setVisibility(View.VISIBLE);

        ///////// mod riccardo
        return true;
    }

    public int getConnections() {
        return connections;
    }

    private void disableDestinationMarkers() {
        for (Marker m : destinationsMap.values()) {
            m.setVisible(false);
        }
    }

    private void moveToFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.map, fragment, "first").addToBackStack(null).commit();

    }


    int radius = 10;

    public boolean checkIfPointReached(final int point) {
        if (point == START){
            if (startArea == null || l == null) {
                return false;
            }

            // Riaggiorno la posizione.
            setLocalizationMethods();

            double distance = SphericalUtil.computeDistanceBetween(fromLocationToLatLng(l), startArea.getCenter());
            Toast.makeText(getApplicationContext(),(distance< startArea.getRadius())+"",Toast.LENGTH_SHORT).show();
            return distance < startArea.getRadius();
        } else {
            if (endArea == null || l == null) {
                return false;
            }

            // Riaggiorno la posizione.
            setLocalizationMethods();

            double distance = SphericalUtil.computeDistanceBetween(fromLocationToLatLng(l), endArea.getCenter());
            return distance < endArea.getRadius();
        }

    }

    public void twoMarkersZoom(Marker marker) {
        Geocoder gc = new Geocoder(this);
        try {
            String[] parsed = markersMap.get(marker).split("_");
            String myDest = parsed[2];
            List<Address> list = null;
            list = gc.getFromLocationName(myDest, 1);

            Address addS = list.get(0);

            mMap.addMarker(new MarkerOptions().position(new LatLng(addS.getLatitude(), addS.getLongitude())).flat(false)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        } catch (Exception e) {
            System.out.println("ERRORACCIO");
            //Log.d("Error Localization", e.getMessage());
        }
        /**
         *
         * !!!!!!!!!!!!!!!! CODICE NON FUNZIONANTE !!!!!!!!!!!!!!!!
         *
         * if (markersMap.containsKey(marker)) {
            if (!destinationsMap.containsKey(marker)) {
                String[] parsed = markersMap.get(marker).split(",");
                String destination = parsed[2];
                Geocoder gc = new Geocoder(this);
                try {
                    if (tempMarker == null) {
                        List<Address> list = null;
                        list = gc.getFromLocationName(destination, 1);

                        Address address = list.get(0);

                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        Marker secondMarker = mMap.addMarker(new MarkerOptions().position(latLng).flat(false));
                        builder.include(marker.getPosition());
                        builder.include(secondMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int padding = 50;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cu);

                        destinationsMap.put(marker, secondMarker);

                    }
                } catch (Exception e) {
                    Log.d("Error Localization", e.getMessage());
                }
            } else {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(marker.getPosition());
                builder.include(destinationsMap.get(marker).getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 14;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        }*/
    }

    int tag = 0;

    static AlertDialog alert;

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            MyTimerAsync performBackgroundTask = new MyTimerAsync();
                            // PerformBackgroundTask this class is the class that extends AsynchTask
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000); //execute in every 50000 ms
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        customMapStyle();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // Codice per la personalizzazione dei controlli sulla mappa
        setMapOptions();

        setLocationListener();

        // Personalizzazione pulsante My Location
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_BOTTOM);
        rlp.setMargins(0, 180, 180, 0);

        setLocalizationMethods();

        if (l != null) {
            updateWithNewLocation(l);

        } else {
            createLocalizationErrorAlert();
        }

        // Richiedo update di posizione continuamente
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20,
                new LocationListener() {
                    public void onLocationChanged(Location location) {
                            //recordPosition(location);

                        l = location;


                        if (IS_MONITORING) {

                                updateWithNewLocation(location);

                                drawTrack(location);

                                double lat = location.getLatitude();
                                double lng = location.getLongitude();

                        //        Toast.makeText(getApplicationContext(),"dsfajkl",Toast.LENGTH_SHORT).show();

                                points.add(lat + "," + lng);
                            }
                    }

                    public void onProviderDisabled(String provider) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                    }
                }
        );


        setInfoFragmentListener();

        setCameraMover();

        //runOnUiThread(new MyTimer());


        //TimerTask myTimerTask = new MyTimer();
        //myTimer.scheduleAtFixedRate(myTimerTask, 0, 5000);

        callAsynchronousTask();

        //createCircle();
    }

    private void setLocationListener() {
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Prima disegno il percorso, passando la nuova posizione rilevata.
                //drawTrack(location);
                // Poi faccio l'update della posizione del marker.
                //updateWithNewLocation(location);
            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
    }

    // Mano a mano che mi sposto sulla mappa, dopo un tot di ms scarico le attiità nell'area visibile su schermo.
    private void setCameraMover() {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            long delay = 460;

            @Override
            public void onCameraMove() {
                if (timer != null) {
                    timer.cancel();
                }
                if (mMap.getCameraPosition().zoom >= 14.50) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            this.cancel();
                            // TODO: invio al DB per calcolare attività in zona.
                            setCoordinates();
                        }
                    }, delay);
                }
            }
        });
    }

    // Metodo per customizzare il tema di sfondo della mappa in base all'orario corrente.
    private void customMapStyle() {
        Calendar cal = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        boolean success = false;
        if (currentHour <= THRESHOLD_HOUR) {
            success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.day_style_json));
        } else {
            success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.night_style_json));
        }
    }

    // Setto le opzioni generali della mappa.
    private void setMapOptions() {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    // Creo l'alert per far attivare la geolocalizzazione.
    private void createLocalizationErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(
                "The Location service must be turned on in order to use Agorun properly. Click Settings and activate it.")
                .setTitle("No connectivity!")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                alert.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                            }
                        });
        alert = builder.create();
        alert.show();
    }

    private void createInternetAvailabilityErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(
                "No connectivity. Turn on your mobile data or Wi-Fi in order to use Agorun properly!")
                .setTitle("Localization disabled!")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                startActivity(intent);
                                alert.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                            }
                        });
        alert = builder.create();
        alert.show();
    }


    private void setLocalizationMethods() {
        int i = 0;
        float accuracy = 0.0f;
        l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (l == null) {
            do {
                i++;
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //System.out.println("Sono nel primo if." + l.getAccuracy() );
                if (l != null) {
                    accuracy = l.getAccuracy();
                }
            } while (l == null && accuracy == 0.0 && accuracy > 20.0 && i < 3);
        } else {
            i = 0;
            accuracy = 0.0f;
            do {
                i++;
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                if (l != null) {
                    accuracy = l.getAccuracy();
                }
                //\System.out.println("Sono nell'else." + l.getAccuracy() );
            } while(l == null && accuracy == 0.0 && accuracy > 20.0 && i < 3);
        }
    }

    private void setInfoFragmentListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Intent activitySummary = new Intent(MapsActivity.this.get, ActivitySummary.class);
                Intent newActivity = new Intent(MapsActivity.this, ActivitySummary.class);
                if (markersMap.containsKey(marker)) {
                    String data = markersMap.get(marker);
                    Bundle bundle = new Bundle();
                    bundle.putString("ActivityData", data);
                    newActivity.putExtras(bundle);
                    startActivity(newActivity);
                }
            }
        });
    }

    public Marker getTempMarker() {
        return this.tempMarker;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    // Starto la ricerca dei markers nella zona visibile dello schermo.
    private void setCoordinates() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("ZOOM LEVEL = " + mMap.getCameraPosition().zoom);
                try {
                    LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    northEast = bounds.northeast;
                    southWest = bounds.southwest;

                    latNorthEast = northEast.latitude;
                    lngNorthEast = northEast.longitude;
                    latSouthWest = southWest.latitude;
                    lngSouthWest = southWest.longitude;

                    if (! IS_MONITORING) {

                        // Salvo i marker già scaricati per non riscaricarli
                        for (Map.Entry<Marker, String> ms : markersMap.entrySet()) {
                            String value = ms.getValue();
                            Log.d("key,value", ms.getKey() + "/" + ms.getValue());
                            int limit = value.indexOf('_');
                            if (limit != -1) {
                                String sid = value.substring(0, limit);
                                if (!Arrays.asList(sids.split(",")).contains(sid)) {
                                    sids += (sid + ",");
                                }
                            }
                        }

                        connections = 0;
                        progressBar.setVisibility(View.VISIBLE);
                        if (isInternetAvailable()) {
                            connect("getruns", null);
                        } else {
                            createInternetAvailabilityErrorAlert();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

            }
        });
    }

    static String trackKm;
    private String trackExperience;

    private String addrS;
    private String addrD;
    private String dateAndTime;
    private String creatorName;
    private String averageExp;


    // Metodi per l'aggiunta di Markers nella mappa --> le posizioni di questi veranno get-tati dal DB.
    public Marker addMarkerToMap(boolean isMyActivity, String sid, LatLng latLng) {
        MapsActivity.trackKm = km;
        //trackExperience = experience;
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
        if (isMyActivity) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        /*Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker( // Al posto dell'argomento di icon, passare BitmapDescriptorFactory.fromResource(R.drawable.FILEIMMAGINE)));
                BitmapDescriptorFactory.HUE_AZURE)).flat(false)); */
        if (addrD.isEmpty()) {
            markersMap.put(marker, new String(sid + "_" + addrS));
        } else {
            System.out.println("INDIRIZZO DI DESTINAZIONE DEFINITIVO: "+addrD);
            //markersMap.put(marker, new String(sid + "_" + addrS + "_" + addrD + "_" + km + "_" + experience + "_" + dateAndTime + "_" + creatorName + "_" + averageExp));
            markersMap.put(marker, new String(sid + "_" + addrS + "_" + addrD));
        }
        return marker;
    }

    public Marker addMarkerToMap(boolean isMyActivity, String sid, double latS,
                                 double longitS, String addrS, String addrD) {
        this.addrS = addrS;
        this.addrD = addrD;

        return this.addMarkerToMap(isMyActivity, sid, new LatLng(latS, longitS));
        //return this.addMarkerToMap(isMyActivity, sid, new LatLng(latS, longitS), km, experience, dateAndTime, creatorName, averageExp);
    }

    private void connect(String mode, String sid) {
        connections++;
        ConnectDB cdb = new ConnectDB(this);
        if (mode.equals("getruns")) {
            cdb.execute(mode, latNorthEast + "", lngNorthEast + "", latSouthWest + "", lngSouthWest + "",
                    sids);
        } else {
            cdb.execute(mode, sid);
        }
    }

    public String getLengthRange(String length) {
        if (length.equals("0")) {
            return "Short: 0-5 km";
        } else if (length.equals("1")) {
            return "Medium: 6-10 km";
        } else if (length.equals("2")) {
            return "Long: 11-15 km";
        } else if (length.equals("3")) {
            return "Champion: 16-20 km";
        } else {
            return "God: 21+ km";
        }
    }

    public String getDifficultyRange(String difficulty) {
        if (difficulty.equals("0")) {
            return "Beginner";
        } else if (difficulty.equals("1")) {
            return "Amateur";
        } else if (difficulty.equals("2")) {
            return "Advanced";
        } else {
            return "Expert";
        }
    }

    public String geoLocateStart(double lat, double lng) {
        Geocoder gc = new Geocoder(this);
        try {
            List<Address> list = null;
            list = gc.getFromLocation(lat, lng, 1);

            Address addS = list.get(0);
            String startingAddress = addS.getAddressLine(0) + ", " + addS.getLocality();
            return startingAddress;
        } catch (Exception e) {
            System.out.println("ERRORACCIO");
            //Log.d("Error Localization", e.getMessage());
        }
        return null;
    }

    ListIterator positionsIterator;

    private void createCircle() {
        String activitySid = MySharedPreferencesHandler.getMySharedPreferencesString(getApplicationContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, "");
        if (!activitySid.isEmpty()) {
            Marker targetMarker = null;
            String[] allParsedStrings;
            for (Marker m : markersMap.keySet()) {
                allParsedStrings = markersMap.get(m).split("_");
                if (allParsedStrings[0].equals(activitySid)) {
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    targetMarker = m;
                    targetMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    //System.out.println("Il sid è: " + activitySid);
                    break;
                }
            }

            if (targetMarker != null) {
                startArea = mMap.addCircle(new CircleOptions()
                        .center(targetMarker.getPosition())
                        .radius(50));
                        //.strokeColor(Color.RED));
            }
        }
    }

    private void createDestinationCircle(LatLng destLatLng) {
        endArea = mMap.addCircle(new CircleOptions().center(destLatLng).radius(50).strokeColor(Color.GREEN));
    }

    private void removeStartingCircled() {
        if (startArea != null) {
            startArea.remove();
        }
    }

    private void removeDestinationCircle() {
        if (endArea != null) {
            endArea.remove();
        }
    }

    private List<LatLng> downloadedPositions = new ArrayList<>();

    public void onTaskCompleted(ArrayList<String> ls) {
        String result = "";


        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            Toast.makeText(this, "Connection Problem", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = ls.remove(0); // Usata perché ho query diverse nella stessa classe: mi dice il tipo di query

        //Log.d("LINE",ls.get(0));

        switch (query) {
            case "getruns":
                try {
                    ListIterator it = ls.listIterator();

                    if (mMap != null) {
                        DrawMarkers drawMarkers = new DrawMarkers();
                        drawMarkers.execute(it, null, null);
                        //System.out.println("***************dopo la chiamata**********");
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
                break;

            case "getinforun":
                ListIterator it = ls.listIterator();

                while (it.hasNext()) {
                    result = result + it.next();
                    session_info = result.split(";");

                    if (session_info[0].equals("ok")) {

                        Log.d("result", result);
                        //TODO: Controllare che tutte le if-else serve a qualcosa

                        Marker arg0 = markerclicked;
                        connections = 0;

                        // Getting view from the layout file info_window_layout
                        View v = getLayoutInflater().inflate(R.layout.acitivityuser_info, null);

                        LatLng latLng = arg0.getPosition();


                        arg0.setTag(Integer.valueOf(tag));


                        if (markersMap.containsKey(arg0)) {
                            String details = markersMap.get(arg0);
                            String[] strings = details.split("_");
                            String sid = strings[0];

                            // Parametri della attività.
                            double endlat = Double.parseDouble(session_info[1]);
                            double endlng = Double.parseDouble(session_info[2]);
                            String length = session_info[3];
                            String difficulty = session_info[4];
                            String datetime = session_info[5];
                            String name = session_info[6];
                            //int numOfJoins = Integer.parseInt(session_info[7]);
                            //int medlevel = Integer.parseInt(session_info[8]);

                            // Creo marker di destinazione e lo rendo invisibile.
                            Marker destinationMarkerToAdd = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(endlat, endlng)).visible(false));

                            // Aggiungo il marker di destinazione all'HashMap.
                            destinationMarkersMap.put(sid, destinationMarkerToAdd);

                            ///// Parso la data e l'ora separatamente /////

                            String[] fullDate = datetime.split(" ");
                            this.date = fullDate[0];
                            hour = fullDate[1];

                            /////                                   /////
                            ///System.out.println("DATA ED ORA: " + datetime);

                            //*sid+"_"+addrS+"_"+addrD+"_"+km+"_"+experience)*//*

                            String[] gotFromHashMap = markersMap.get(arg0).split("_");
                            if (gotFromHashMap.length > 2) {
                                // TODO: da sistemare, in quanto concatena continuamente dateTime e name.
                                String string = markersMap.get(arg0);
                                String concat = string + "_" + datetime + "_" + name;

                                markersMap.put(arg0, concat);

                                //name = this.name;


                                //tvLat.setText("Indirizzo di partenza: " + gotFromHashMap[1]);

                                // Tolgo la città dalla stringa relativa al punto di partenza dopo aver cliccato su un marker, da mostrare nel summary.
                                String[] startParsed = gotFromHashMap[1].split(",");
                                String[] newStart = Arrays.copyOf(startParsed, startParsed.length - 1);
                                start = TextUtils.join(",", newStart);

                                // Tolgo la città dalla stringa relativa al punto di destinazione dopo aver cliccato su un marker, da mostrare nel summary.
                                String[] stopParsed = gotFromHashMap[2].split(",");
                                String[] newStop = Arrays.copyOf(stopParsed, stopParsed.length - 1);
                                stop = TextUtils.join(",", newStop);


                                //km = gotFromHashMap[3];
                                //exp = gotFromHashMap[4];

                                // Traduco il chilometraggio.
                                km = getLengthRange(length);

                                // Traduco l'esperienza.
                                exp = getDifficultyRange(difficulty);

                                this.twoMarkersZoom(arg0);

                            } else {
                                Geocoder gc = new Geocoder(mact);
                                try {
                                    // Traduco latitudine e longitudine del punto di arrivo.
                                    List<Address> list = null;
                                    list = gc.getFromLocation(endlat, endlng, 1);
                                    Address add = list.get(0);
                                    String dest = add.getAddressLine(0) + ", " + add.getLocality();

                                    String string = markersMap.get(arg0);
                                    String putData = string + "_" + dest + "_" + length + "_" + difficulty;
                                    markersMap.put(arg0, putData);

                                    // Tolgo la città dall'indirizzo di partenza nel summary.
                                    String[] startParsed = gotFromHashMap[1].split(",");
                                    String[] newStart = Arrays.copyOf(startParsed, startParsed.length - 1);
                                    start = TextUtils.join(",", newStart);

                                    // Tolgo la città dall'indirizzo di destinazione nel summary.
                                    String[] stopParsed = dest.split(",");
                                    String[] newStop = Arrays.copyOf(stopParsed, stopParsed.length - 1);
                                    stop = TextUtils.join(",", newStop);

                                    // Traduco il chilometraggio.
                                    km = getLengthRange(length);

                                    // Traduco l'esperienza.
                                    exp = getDifficultyRange(difficulty);

                                    // Zoom sui due markers.
                                    this.twoMarkersZoom(arg0);

                                } catch (Exception e) {
                                    System.out.println("Geolocalizzazione fallita.");
                                }
                            }

                        }
                        tag++;


                        fragment1 = new Fragment1();

                        Bundle args = new Bundle();
                        args.putString("start", start);
                        args.putString("stop", stop);
                        args.putString("km", km);
                        args.putString("exp", exp);
                        args.putString("hour", hour);
                        args.putString("date", date);
                        args.putString("name", session_info[6]);
                        args.putString("noj", session_info[7]);
                        //args.putString("medlevel", session_info[8]);
                        args.putString("medlevel", "medium");
                        args.putString("markerId", markerclicked.getId());

                        // Parso la stringa corrispondente al marker cliccato.
                        String[] parsedString = markersMap.get(arg0).split("_");

                        // Salvo il sid dell'attività joinata.
                        String activitySid = parsedString[0];

                        //System.out.println("Dopo assegnamento: "+activitySid);
                        // Passo il sid al Fragment.
                        args.putString("activitySid", activitySid);
                        //System.out.println("Ho salvato "+activitySid + "nel bundle");

                        // Disegno il cerchio trasparente attorno al punto di partenza.


                        fragment1.setArguments(args);
                        moveToFragment(fragment1);

                        progressBar.setVisibility(View.GONE);
                    } else {
                        connections++;
                        result = "";
                        if (isInternetAvailable()) {
                            connect("getinforun", sidclicked);
                        } else {
                            createInternetAvailabilityErrorAlert();
                        }
                    }
                }

                break;

            case "settrack":
                if (ls.get(0).equals("Success")) {
                    // TODO: rendere invisibile.
                    Toast.makeText(getApplicationContext(), "Track sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Track error", Toast.LENGTH_SHORT).show();
                }

                break;

            case "getmessage":
                if (!ls.get(0).equals("Error") && !ls.get(0).equals("No messages")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Messages by Agorun")
                            .setMessage(ls.get(0).replace("\\n", "\n"))
                            .setCancelable(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .show();
                }
                break;

            case "increaserank":
                if(!ls.get(0).equals("Error")) {
                    Toast.makeText(getApplicationContext(),"Rank Increased!!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
        }
/*
                session_info = result.split(";");
                TextView tvLat = (TextView) infowindow.findViewById(R.id.tv_lat);
                tvLat.setText("sfda");
                Log.d("Connection ok","ciao");
            }
        }*/
        result = "";
    }
}

