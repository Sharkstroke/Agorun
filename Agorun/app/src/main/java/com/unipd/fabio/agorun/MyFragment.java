package com.unipd.fabio.agorun;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by fabio on 04/07/17.
 */

public class MyFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap myMap;

    private final String ARG_SECTION_NUMBER = "section_number";
    private String startToShow = "startingP";
    private String destinationToShow = "destinationP";
    private String kmToShow = "kmP";

    private List<LatLng[]> trackPoints;

    private boolean liked;

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
        String starting = parsed[0];
        String destination = parsed[1];
        String totKm = parsed[2];

        String likes = parsed[3];

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(startToShow, starting);

        args.putString(destinationToShow, destination);
        args.putString(kmToShow, totKm);

        trackPoints = latLngList;
        System.out.println("Trackpoints: "+trackPoints);

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
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    // Quando la mappa è pronta, vado ad effettuare il focus sul percorso. Questo metodo viene richiamato automaticamente.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, 23.2), 17));

        //MapsActivity.getMapsData().drawLines(this.trackPoints);

    }

    private void setLikeTrackListener(final FloatingActionButton likeButton) {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!liked) {
                    likeButton.setImageResource(R.drawable.thumbs_up_hand_symbol);
                    liked = true;
                    System.out.println("Sono in !liked");
                } else {
                    likeButton.setImageResource(R.drawable.thumbs_up);
                    liked = false;
                    System.out.println("Sono in liked");
                }
            }
        });
    }
}