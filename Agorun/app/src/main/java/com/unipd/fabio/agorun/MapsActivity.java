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
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
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
    private ImageButton HamburgerMenu;
    private TextView search_tw;
    private TextView startingAddressTop;
    private TextView destinationAddressTop;
    private ProgressBar progressBar;
    private Button stopMonitoring;
    private Button startMonitoring;

    private static final int THRESHOLD_HOUR = 19;
    private static final int POSITION_FREQUENCY = 1500;

    private static boolean IS_MONITORING;


    private LocationListener locationListener;

    private LatLng latlng1 = new LatLng(45.407126, 11.889635);
    private LatLng latlng2 = new LatLng(45.407192, 11.889117);
    private LatLng latlng3 = new LatLng(45.407426, 11.887964);
    private LatLng latlng4 = new LatLng(45.407658, 11.886083);
    private LatLng latlng5 = new LatLng(45.408934, 11.886866);
    private LatLng latlng6 = new LatLng(45.409209, 11.887740);
    private LatLng latlng7 = new LatLng(45.408676, 11.891422);
    private LatLng latlng8 = new LatLng(45.407302, 11.891325);
    private List<LatLng> listCoords = new LinkedList<>();

    private List<LatLng> routePoints = new LinkedList<>();

    private Timer timer;

    private final MyLocationRegistered formerPos = new MyLocationRegistered();

    private Map<Marker, String> markersMap = new HashMap<>();

    private Map<Double, Double> positionsRecorded = new HashMap<>();

    private Map<Marker, Marker> destinationsMap = new HashMap<>();

    private int connections = 0;

    private String[] session_info;
    private Marker markerclicked;
    private String sidclicked;

    Timer cameraSteadyTimer = new Timer();


    private LatLng northEast;
    private LatLng southWest;

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

    private OnSwipeTouchListener GestureManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        timer = new Timer();

        mact = this;

        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        provider = locationManager.getBestProvider(criteria, true);

        listCoords.add(latlng1);
        listCoords.add(latlng2);
        listCoords.add(latlng3);
        listCoords.add(latlng4);
        listCoords.add(latlng5);
        listCoords.add(latlng6);
        listCoords.add(latlng7);
        listCoords.add(latlng8);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.findPlace);
        //Button actv = (Button) findViewById(R.id.findPlace);

        search_tw = (TextView) findViewById(R.id.search_bar);
        this.startingAddressTop = (TextView) findViewById(R.id.startingPointInMain);
        this.destinationAddressTop = (TextView) findViewById(R.id.destinationPointInMain);
        this.stopMonitoring = (Button) findViewById(R.id.stopMonitoring);
        this.startMonitoring = (Button) findViewById(R.id.startMonitorButton);

        /*Giulio mod.*/
       /* Button newActivity = (Button) findViewById(R.id.newActivity);*/

       /* newActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);
            }
        });*/
         /*Giulio mod.*/
        search_tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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

         /*Giulio mod.*/
        HamburgerMenu = (ImageButton) findViewById(R.id.button1);

        HamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                PopupMenu popup = new PopupMenu(MapsActivity.this, HamburgerMenu);
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
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }

        });
         /*Giulio mod.*/

        progressBar = (ProgressBar) findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);

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



    public List<LatLng> getListCoords() {
        return this.listCoords;
    }

    public static MapsActivity getMapsData() {
        return mact;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            if (l == null) {
                l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
           /* if (l != null) {
                updateWithNewLocation(l);
            }*/
        }
        //setUpMapIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.

                Place place = PlaceAutocomplete.getPlace(this, data);
                search_tw.setText(place.getAddress().toString());
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

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

    public static LatLng fromLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());

    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            // Assegno alla posizione "precedente" la posizione corrente: in questo modo, quando vado a colorare il percorso, viene
            // colorata la parte da questo punto all'ultimo registrato.
            formerPos.setPosition(location);
            // Update the map location.

            LatLng latlng = fromLocationToLatLng(location);

            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

            }

            if (whereAmI != null) {
                whereAmI.remove();
            }

            double lat = location.getLatitude();
            double lng = location.getLongitude();

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

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
            }
        }
    }

    // Coloro il pezzo di strada che va dalla posizione precedentemente registrata a quella attuale.
    private void drawTrack(Location location) {
        /*mMap.addPolyline((new PolylineOptions().add(new LatLng(formerPos.getPosition().getLatitude(), formerPos.getPosition().getLongitude()),
                new LatLng(location.getLatitude(), location.getLongitude())
        )).width(5).color(Color.RED).geodesic(true));*/
        Polyline route = mMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.GREEN)
                .geodesic(true)
        );
        route.setPoints(routePoints);
    }

    private void drawTrack(LatLng ll1, LatLng ll2) {
        mMap.addPolyline(new PolylineOptions().add(ll1, ll2).width(7).color(Color.RED).geodesic(true));
    }

    private void recordPosition(Location location) {
        positionsRecorded.put(location.getLatitude(), location.getLongitude());
        routePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        System.out.println("HO CAMBIATO LA MIA POSIZIONE");
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
    private void setUpMap() {
    }

    @Override
    public void onMapClick(LatLng latLng) {
        progressBar.setVisibility(View.GONE);
        disableDestinationMarkers();
    }

    private Marker tempMarker;
    String startingAdd = "";
    String destinationAdd = "";

    private LatLng startLatLng;
    private LatLng destLatLng;

    @Override
    public void onMapLongClick(LatLng latLng) {
        disableDestinationMarkers();
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        Geocoder gc = new Geocoder(this);
        try {
            if (tempMarker == null) {
                List<Address> list = null;
                list = gc.getFromLocation(latitude, longitude, 1);

                Address add = list.get(0);
                startingAdd = add.getAddressLine(0) + ", " + add.getLocality();
                startingAddressTop.setText("Starting point: " + startingAdd);
                tempMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker()).flat(false));
                markersMap.put(tempMarker, "");
                startLatLng = latLng;
            } else {
                List<Address> list = null;
                list = gc.getFromLocation(latitude, longitude, 1);
                Address add = list.get(0);
                destinationAdd = add.getAddressLine(0) + ", " + add.getLocality();

                destLatLng = latLng;

                destinationAddressTop.setText("Destination address: " + destinationAdd);
                //mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).flat(false));

                Intent newActivity = new Intent(MapsActivity.this, AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("StartingAddress", new String(startingAdd + "_" + destinationAdd + "_" +
                                                                startLatLng.latitude + "_" + startLatLng.longitude + "_" +
                                                                destLatLng.latitude + "_" + destLatLng.longitude));
                newActivity.putExtras(bundle);
                startActivity(newActivity);
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
        Log.d("Marker clicked", "fadlk");
        if (markersMap.containsKey(marker)) {
            String details = markersMap.get(marker);
            String[] strings = details.split("_");
            sidclicked = strings[0];
            Toast.makeText(getApplicationContext(),sidclicked,Toast.LENGTH_LONG).show();
            connect("getinforun", sidclicked);


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

    public void startMonitoringPressed(View view) {
        // TODO: ottenimento sid dell'attività session_info[0].equals("ok")ata e che si sta per avviare In base al sid eccetera, identificare i due marker di partenza e di arrivo
        // TODO: e rendere invisibili tutti i marker sulla mappa tranne questi due.
        for (Marker m : markersMap.keySet()) {
            m.setVisible(!m.isVisible());
        }

        stopMonitoring.setVisibility(View.VISIBLE);
        // Inizio il monitoring
        IS_MONITORING = true;

       // System.out.println("MAP SIZE=" + positionsRecorded.size());

    }

    public void stopMonitoring(View view) {
        // TODO: fornire opzioni per fare il resume o per concludere l'attività.
        IS_MONITORING = false;


    }

    int radius = 10;

    // TODO: il metodo deve ritornare un booleano e deve controllare se l'utente si trova nel rettangolo attorno al punto di destinazione oppure se la distanza
    // TODO: tra l'utente ed il punto di destinazione è minore del raggio definito come campo.
    public void checkDestinationReached() {
        //double distance = SphericalUtil.computeDistanceBetween(mMarkerA.getPosition(), mMarkerB.getPosition());
        //mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        //Bounds bounds = new Bounds(l.getLatitude()-10, l.getLatitude()+10, l.getLongitude()-5, l.getLongitude()+5);
        //return bounds.contains(l.getLatitude(), l.getLongitude());
    }

    public void twoMarkersZoom(Marker marker) {
        if (markersMap.containsKey(marker)) {
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
        }
    }

    int tag = 0;

    static AlertDialog alert;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

        if (!success) {
            System.out.println("JSON parsing non funzionante.");
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // Codice per la personalizzazione dei controlli sulla mappa
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        // Personalizzazione pulsante My Location
        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.ALIGN_BOTTOM);
        rlp.setMargins(0, 180, 180, 0);

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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

        l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (l == null) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        }
        if (l != null) {
            LatLng latlng = fromLocationToLatLng(l);
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
            //      17));
            updateWithNewLocation(l);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(
                    "You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
                    .setTitle("LostyFound")
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

        // Richiedo update di posizione continuamente
        locationManager.requestLocationUpdates(provider, POSITION_FREQUENCY, 5,
                new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Prima disegno il percorso, passando la nuova posizione rilevata.
                        //if (!IS_MONITORING) {
                            recordPosition(location);
                            drawTrack(location);
                            updateWithNewLocation(location);
                        //}
                        // Poi faccio l'update della posizione del marker.

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


        // InfoWindow viene usata per customizzare le finestre di info che appaiono al click su un Marker.
        // La customizzazione qui avviene tramite un Adapter.


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

        /***
         *
         * Setto un listener per la telecamera così da consentire la registrazione del movimento ed il successivo
         * download delle attività nella zona.
         */
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
                            //System.out.println("lat="+mMap.getCameraPosition().target.latitude+", lng = "+mMap.getCameraPosition().target.longitude);
                        }
                    }, delay);
                }
            }
        });


        // addMarkerToMap(40.1111,11.1111,"","","","");

        //connect("getruns",null);

    }


/*
    private class TimerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    //System.out.println("eccomi");
                    Thread.sleep(3000);
                    //System.out.println("*********POSITION: LAT = " + formerPos.getPosition().getLatitude() + ", LONG = " + formerPos.getPosition().getLongitude() + "*********");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }
*/

    public Marker getTempMarker() {
        return this.tempMarker;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

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

                    for (Map.Entry<Marker,String> ms : markersMap.entrySet()) {
                        String value = ms.getValue();
                        Log.d("key,value",ms.getKey() + "/" + ms.getValue());
                        int limit = value.indexOf('_');
                        if (limit != -1) {
                            String sid = value.substring(0, limit);
                            if (! Arrays.asList(sids.split(",")).contains(sid)) {
                                sids += (sid + ",");
                            }
                        }
                    }

                    //System.out.println("NORTH EAST: " + latNorthEast + ", " + lngNorthEast);
                    //System.out.println("SOUTH WEST: " + latSouthWest + ", " + lngSouthWest);

                    //System.out.println("Lat: "+cameraCenterPointLatitude+"; Lon: "+cameraCenterPointLongitude);
                    connections = 0;
                    progressBar.setVisibility(View.VISIBLE);
                    connect("getruns", null);
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


    // Metodi per l'aggiunta di Markers nella mappa --> le posizioni di questi veranno get-tati dal DB.
    public Marker addMarkerToMap(boolean isMyActivity, String sid, LatLng latLng, String km, String experience) {
        MapsActivity.trackKm = km;
        trackExperience = experience;
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
        if (addrD == "" && km == "" && experience == "") {
            markersMap.put(marker, new String(sid + "_" + addrS));
        } else {
            markersMap.put(marker, new String(sid + "_" + addrS + "_" + addrD + "_" + km + "_" + experience));
        }
        return marker;
    }

    public Marker addMarkerToMap(boolean isMyActivity, String sid, double latS, double longitS, String addrS, String addrD, String km, String experience) {
        this.addrS = addrS;
        this.addrD = addrD;
        //System.out.println("ADDR-D = "+addrD);
        return this.addMarkerToMap(isMyActivity, sid, new LatLng(latS, longitS), km, experience);
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


    private List<LatLng> downloadedPositions = new ArrayList<>();

    public void onTaskCompleted(ArrayList<String> ls) {
        String result = "";


        if (connections >= 5) {                     // Provo la connessione 5 volte, altrimenti do errore di connessione
            Toast.makeText(this, "Connection Problem", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = ls.remove(0);

        //Log.d("LINE",ls.get(0));

        if (query.equals("getruns")) {
            try {
                ListIterator it = ls.listIterator();
                //this.positionsIterator = ls.listIterator();
                /*while (it.hasNext()) {
                    result = result + (it.next());
                    if (result.equals("Problems selecting activities")) {
                        Toast.makeText(getApplicationContext(), "Here there are no activities!", Toast.LENGTH_SHORT).show();
                    } else if (!(result.charAt(0) == 'C')) {  // Connection failed
                        Log.d("result", result);

                        connections = 0;

                        String[] session_point = result.split(";");
                        String sid = session_point[0];

                        //                Log.d("sid", sid);

                        double lat = Double.parseDouble(session_point[1]);
                        double lng = Double.parseDouble(session_point[2]);

                        Geocoder gc = new Geocoder(this);
                        try {
                            if (tempMarker == null) {
                                List<Address> list = null;
                                list = gc.getFromLocation(lat, lng, 1);

                                Address addS = list.get(0);
                                String startingAddress = addS.getAddressLine(0) + ", " + addS.getLocality();

                                //Marker marker = addMarkerToMap(false, sid, lat, lng, startingAddress, "", "", "");

                                if (session_point.length == 4) {
                                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    Log.d("Green", sid);
                                }
                            }
                        } catch (Exception e) {
                            Log.d("Error Localization", e.getMessage());
                        }

                        result = "";
                    } else {
                        Log.d("result", result);
                        connections++;
                        result = "";
                        connect("getruns", null);
                    }
                }*/

                if (mMap != null) {
                    DrawMarkers drawMarkers = new DrawMarkers();
                    drawMarkers.execute(it, null, null);
                    //System.out.println("***************dopo la chiamata**********");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            progressBar.setVisibility(View.GONE);
        } else {
            ListIterator it = ls.listIterator();

            while (it.hasNext()) {
                result = result + it.next();
                session_info = result.split(";");

                if (session_info[0].equals("ok")) {

                    Log.d("result", result);
                    //TODO: Controllare che tutte le if-else serve a qualcosa
                    //Giulio mod.
                    //mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                     /*   @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }*/

                    // Defines the contents of the InfoWindow
                    // @Override
                    //public View getInfoContents(Marker arg0) {
                    Marker arg0 = markerclicked;
                    connections = 0;

                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.acitivityuser_info, null);

                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();


                    // Utilizzare il Tag per identificare il Marker.
                    arg0.setTag(Integer.valueOf(tag));

                    // Getting reference to the TextView to set latitude
                           /* TextView tvLat = (TextView) v.findViewById(R.id.act_start);

                            // Getting reference to the TextView to set longitude
                            TextView tvLng = (TextView) v.findViewById(R.id.act_dest);

                            TextView km = (TextView) v.findViewById(R.id.act_km);

                            TextView experience = (TextView) v.findViewById(R.id.act_exp);*/

                    if (markersMap.containsKey(arg0)) {
                        String details = markersMap.get(arg0);
                        String[] strings = details.split("_");
                        String sid = strings[0];

                        double endlat = Double.parseDouble(session_info[1]);
                        double endlng = Double.parseDouble(session_info[2]);
                        String length = session_info[3];
                        String difficulty = session_info[4];
                        String datetime = session_info[5];
                        String name = session_info[6];
                        int numOfJoins = Integer.parseInt(session_info[7]);
                        int medlevel = Integer.parseInt(session_info[8]);

                        //*sid+"_"+addrS+"_"+addrD+"_"+km+"_"+experience)*//*


                        String[] gotFromHashMap = markersMap.get(arg0).split("_");
                        if (gotFromHashMap.length > 2) {
                            String string = markersMap.get(arg0);
                            String concat = string + "_" + datetime + "_" + name;
                            markersMap.put(arg0, concat);


                            //tvLat.setText("Indirizzo di partenza: " + gotFromHashMap[1]);

                            String[] startParsed = gotFromHashMap[1].split(",");
                            String[] newStart = Arrays.copyOf(startParsed, startParsed.length-1);
                            start = TextUtils.join(",", newStart);

                            //tvLng.setText("Indirizzo di arrivo: " + gotFromHashMap[2]);
                            String[] stopParsed = gotFromHashMap[2].split(",");
                            String[] newStop = Arrays.copyOf(stopParsed, stopParsed.length-1);
                            stop = TextUtils.join(",", newStop);
                            //km.setText("Km: " + getLengthRange(gotFromHashMap[3]));
                            km = gotFromHashMap[3];
                            //experience.setText("Experience: " + getDifficultyRange(gotFromHashMap[4]));
                            exp = gotFromHashMap[4];
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
                                //System.out.println("PUT DATA: " + putData);

                                //tvLat.setText("Indirizzo di partenza: " + gotFromHashMap[1]);
                                String[] startParsed = gotFromHashMap[1].split(",");
                                String[] newStart = Arrays.copyOf(startParsed, startParsed.length-1);
                                start = TextUtils.join(",", newStart);


                                //tvLng.setText("Indirizzo di arrivo: " + dest);
                                String[] stopParsed = dest.split(",");
                                String[] newStop = Arrays.copyOf(stopParsed, stopParsed.length-1);
                                stop = TextUtils.join(",", newStop);
                                //       km.setText("Km: " + strings[3]);
                                //km.setText("Km: " + getLengthRange(length));
                                km = getLengthRange(length);
                                //experience.setText("Experience: " + getDifficultyRange(difficulty));
                                exp = getDifficultyRange(difficulty);
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


                    fragment1.setArguments(args);
                    moveToFragment(fragment1);


                    // Returning the view containing InfoWindow contents

                    //      infowindow = v;

                    //return v;

                    // }

                    //  });
                    //   markerclicked.showInfoWindow();

                    // Giulio Mod.
                    progressBar.setVisibility(View.GONE);
                } else {
                    connections++;
                    result = "";
                    connect("getinforun", sidclicked);
                }
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

