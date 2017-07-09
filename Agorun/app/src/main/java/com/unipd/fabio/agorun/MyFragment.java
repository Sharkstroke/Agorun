package com.unipd.fabio.agorun;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabio on 04/07/17.
 */

public class MyFragment extends Fragment implements OnMapReadyCallback, DBConnection {

    GoogleMap myMap;

    private final String ARG_SECTION_NUMBER = "section_number";
    private String startToShow = "startingP";
    private String destinationToShow = "destinationP";
    private String kmToShow = "kmP";
    private String startLatToPass = "startLat";
    private String startLngToPass = "startLng";
    private String endLatToPass = "endLat";
    private String endLngToPass = "endLng";
    private String activitySid = "activitySid";

    private String startLat;
    private String startLng;
    private String endLat;
    private String endLng;
    private String sid;

    private List<LatLng[]> trackPoints;

    private boolean liked;
    private FloatingActionButton likeButton;

    MapView m;
    GoogleMap map;

    // Ritorno una istanza di questa classe passando come argomenti i dettagli sul percorso che sto per andare a presentare.
    public MyFragment newInstance(int sectionNumber, String starting, String destination, String totKm) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(startToShow, starting);
        System.out.println("Ho appena messo starting: "+starting);
        args.putString(destinationToShow, destination);
        args.putString(kmToShow, totKm);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFragment newInstance(int sectionNumber, String allData, List<LatLng[]> latLngList) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();

        String[] parsed = allData.split("_");

        this.startLat = parsed[1];
        this.startLng = parsed[2];
        this.endLat = parsed[3];
        this.endLng = parsed[4];

        String starting = parsed[5];
        String destination = parsed[6];
        String totKm = parsed[7];

        String likes = parsed[8];

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        // Passo il sid al bundle.
        args.putString(activitySid, parsed[0]);

        args.putString(startLatToPass, startLat);
        args.putString(startLngToPass, startLng);
        args.putString(endLatToPass, endLat);
        args.putString(endLngToPass, endLng);

        args.putString(startToShow, starting);

        args.putString(destinationToShow, destination);
        args.putString(kmToShow, totKm);

        trackPoints = latLngList;
        //System.out.println("Trackpoints: "+trackPoints);

        fragment.setArguments(args);
        return fragment;
    }

    private static View view;

    // Faccio l'inflate dell'XML per la rappresentazione della mappa nella metà superiore del display e dei dettagli di un percorso nella metà inferiore.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.my_layout, container, false);

            TextView start  = (TextView) view.findViewById(R.id.startMyFragment);
            TextView destination = (TextView) view.findViewById(R.id.destinationMyFragment);
            TextView km = (TextView) view.findViewById(R.id.kmMyFragment);
            FloatingActionButton likeTrack = (FloatingActionButton) view.findViewById(R.id.fab);

            // Avvio il caricamento asincrono della mappa.
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapMyLayout)).getMapAsync(this);


            // Setto il testo nelle TextViews.
            start.setText(getArguments().getString(startToShow));
            destination.setText(getArguments().getString(destinationToShow));

            km.setText(getArguments().getString(kmToShow));

            setLikeTrackListener(likeTrack);

            this.startLat = getArguments().getString(startLatToPass);
            this.startLng = getArguments().getString(startLngToPass);
            this.endLat = getArguments().getString(endLatToPass);
            this.endLng = getArguments().getString(endLngToPass);
            this.sid = getArguments().getString(activitySid);

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    // Quando la mappa è pronta, vado ad effettuare il focus sul percorso. Questo metodo viene richiamato automaticamente.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                                        Double.parseDouble(this.startLat), Double.parseDouble(this.startLng)
                                                        ), 17)
        );

        // Aggiungo un marker nel punto di partenza, di colore blu.
        myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(this.startLat), Double.parseDouble(this.startLng))).flat(false)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // Aggiungo un marker nel punto di destinazione, di colore rosso.
        myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(this.endLat), Double.parseDouble(this.endLng))).flat(false)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // drawLines(trackPoints);

    }

    public void drawLines (List<LatLng[]> list) {
        //Polyline line;
        for (LatLng[] latLngs : list) {
            myMap.addPolyline(new PolylineOptions().add(latLngs)).setColor(Color.RED);
        }
    }

    private void setLikeTrackListener(final FloatingActionButton likeButton) {
        this.likeButton = likeButton;
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liked) {
                    likeButton.setImageResource(R.drawable.thumbs_up_hand_symbol);
                    liked = true;
                    new ConnectDB(MyFragment.this).execute("liketrack",sid,"like");
                    System.out.println("Sono in !liked");
                } else {
                    likeButton.setImageResource(R.drawable.thumbs_up);
                    liked = false;
                    new ConnectDB(MyFragment.this).execute("liketrack",sid,"dislike");
                    System.out.println("Sono in liked");
                }
            }
        });
    }


    @Override
    public void onTaskCompleted(ArrayList<String> result) {
        if (! result.get(0).equals("Success")) {
            Toast.makeText(getContext(),"Like error",Toast.LENGTH_SHORT).show();
            if (liked) {
                liked = false;
                likeButton.setImageResource(R.drawable.thumbs_up);
            } else {
                liked = true;
                likeButton.setImageResource(R.drawable.thumbs_up_hand_symbol);
            }
        }
    }
}