package com.unipd.fabio.agorun; /**package com.unipd.fabio.provamaps;*/

import android.content.Context;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public Marker whereAmI;

    // TROVARE UNA SOLUZIONE UN PO' PIU' APPROPRIATA DI QUESTA: IN OGNI CASO QUESTO FUNZIONA.
    private static MapsActivity mact;

    private LocationManager locationManager;
    private LocationManager m_locationManager;
    private Location l;
    private String provider;

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

    private Timer timer;

    private final MyLocationRegistered formerPos = new MyLocationRegistered();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        timer = new Timer();

        mact = this;

        String svcName= Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

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
        Button actv = (Button) findViewById(R.id.findPlace);
        actv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(v.getContext(), AddActivity.class);
                startActivity(intent);

                /*try {
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(mact);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.i("1", e.toString());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.i("2", e.toString());
                }*/
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
        //setUpMapIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                /*((TextView) findViewById(R.id.searched_address))
                        .setText(place.getName()+",\n"+
                                place.getAddress() +"\n" + place.getPhoneNumber());*/
                LatLng latLngFound = place.getLatLng();
                mMap.addMarker(new MarkerOptions()
                        .position(latLngFound)
                        .flat(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFound, 17));

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());

    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            // Assegno alla posizione "precedente" la posizione corrente: in questo modo, quando vado a colorare il percorso, viene
            // colorata la parte da questo punto all'ultimo registrato.
            formerPos.setPosition(location);
            // Update the map location.

            LatLng latlng=fromLocationToLatLng(location);


            //System.out.println("Latitude: "+latlng.latitude+", Longitude: "+latlng.longitude);

            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
              //      17));

            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            }

            if(whereAmI!=null) {
                whereAmI.remove();
            }

            /*whereAmI=mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE)).flat(false).title("I'm here!"));*/

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
        mMap.addPolyline((new PolylineOptions().add(new LatLng(formerPos.getPosition().getLatitude(), formerPos.getPosition().getLongitude()),
                                                    new LatLng(location.getLatitude(), location.getLongitude())
                                                    )).width(5).color(Color.BLUE).geodesic(true));
    }

    private void drawTrack(LatLng ll1, LatLng ll2) {
        mMap.addPolyline(new PolylineOptions().add(ll1, ll2).width(7).color(Color.RED).geodesic(true));
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

    @Override
    public void onMapClick(LatLng latLng) {}

    @Override
    public void onMapLongClick(LatLng latLng) {
        //mMap.addMarker(new MarkerOptions().position(latLng).title("Lat = "+latLng.latitude+", Long = "+latLng.longitude));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                drawTrack(location);
                // Poi faccio l'update della posizione del marker.
                updateWithNewLocation(location);
            }

            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {}
        };

        l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (l == null) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        }
        if (l != null) {
            LatLng latlng=fromLocationToLatLng(l);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                    17));
            updateWithNewLocation(l);

        } else {
            System.out.println("NULLO");
        }

        // Richiedo update di posizione continuamente
        locationManager.requestLocationUpdates(provider, 2000, 5,
                new LocationListener() {
                    public void onLocationChanged(Location location) {
                        // Prima disegno il percorso, passando la nuova posizione rilevata.
                        drawTrack(location);
                        // Poi faccio l'update della posizione del marker.
                        updateWithNewLocation(location);
                    }

                    public void onProviderDisabled(String provider) {}
                    public void onProviderEnabled(String provider) {}
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {}
                }
        );



        // InfoWindow viene usata per customizzare le finestre di info che appaiono al click su un Marker.
        // La customizzazione qui avviene tramite un Adapter.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);

                // Setting the longitude
                tvLng.setText("Longitude:" + latLng.longitude);

                // Returning the view containing InfoWindow contents
                return v;

            }

        });

        /**
         * SOLO PER TESTARE LA COLORAZIONE DEL PERCORSO DEFINITO DA PUNTI IN SEQUENZA
         */
        /*
        for (int k = 0; k < listCoords.size()-1; k++) {
            this.drawTrack(listCoords.get(k), listCoords.get(k + 1));
        }*/

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

    // Metodi per l'aggiunta di Markers nella mappa --> le posizioni di questi veranno get-tati dal DB.
    public void addMarkerToMap(LatLng latLng, String description) {
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker( // Al posto dell'argomento di icon, passare BitmapDescriptorFactory.fromResource(R.drawable.FILEIMMAGINE)));
                BitmapDescriptorFactory.HUE_AZURE)).flat(false).title(description));
    }

    public void addMarkerToMap(double lat, double longit, String description) {
        this.addMarkerToMap(new LatLng(lat, longit), description);
    }
}

