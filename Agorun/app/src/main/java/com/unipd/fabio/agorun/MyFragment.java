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

/**
 * Created by fabio on 04/07/17.
 */

public class MyFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap myMap;

    private final String ARG_SECTION_NUMBER = "section_number";
    private String startToShow = "startingP";
    private String destinationToShow = "destinationP";
    private String kmToShow = "kmP";

    MapView m;
    GoogleMap map;

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

    private static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.my_layout, container, false);
            //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //TextView textView = (TextView) view.findViewById(R.id.section_label);
            TextView start  = (TextView) view.findViewById(R.id.startMyFragment);
            TextView destination = (TextView) view.findViewById(R.id.destinationMyFragment);
            TextView km = (TextView) view.findViewById(R.id.kmMyFragment);
            FloatingActionButton likeTrack = (FloatingActionButton) view.findViewById(R.id.fab);

            System.out.println("TROVATO:" +((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapMyLayout)));
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapMyLayout)).getMapAsync(this);


            start.setText(getArguments().getString(startToShow));
            destination.setText(getArguments().getString(destinationToShow));

            km.setText(getArguments().getString(kmToShow));

        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, 23.2), 17));
    }
}