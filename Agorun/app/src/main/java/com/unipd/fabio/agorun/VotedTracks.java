package com.unipd.fabio.agorun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.List;

/* ## TODO: citare nei crediti la fonte delle icone con il pollice. Da inserire: Icon made by http://www.flaticon.com/authors/dave-gandy from www.flaticon.com*/

public class VotedTracks extends AppCompatActivity implements DBConnection {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static final int PAGES_TO_SAVE_STATE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votedtracks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new ConnectDB(this).execute("getbesttracks");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        *//** PAGES_TO_SAVE_STATE indica il numero di pagine (schede) di cui si deve mantenere lo stato.
         * Per il nostro caso, è bene tenere lo stato di un numero di pagine secondo me compreso tra 5 e 7.
          *//*
        mViewPager.setOffscreenPageLimit(PAGES_TO_SAVE_STATE);
        mViewPager.setAdapter(mSectionsPagerAdapter);*/
    }

    @Override
    public void onTaskCompleted(ArrayList<String> result) {
        if (result.get(0).equals("Error")) {
            Toast.makeText(getApplicationContext(),"Error getting tracks",Toast.LENGTH_SHORT).show();
        } else {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),result);

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);

            /** PAGES_TO_SAVE_STATE indica il numero di pagine (schede) di cui si deve mantenere lo stato.
             * Per il nostro caso, è bene tenere lo stato di un numero di pagine secondo me compreso tra 5 e 7.
             */
            mViewPager.setOffscreenPageLimit(PAGES_TO_SAVE_STATE);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        /* TODO: Anziché start1, dest1 e così via, occorrerà mettere un collegamento con il DB per avere le info:
        * TODO: 1) Indirizzo di partenza
        * TODO: 2) Indirizzo di destinazione
        * TODO: 3) Km totali del percorso
        * TODO: 4) Difficoltà del percorso
        *
        * TODO: Bisognerà anche passare un parametro per impostare lo zoom in modo appropriato per ogni scheda.
        * */

        String start1 = "Via A 1";
        String dest1 = "Via A 1";
        String km1 = "Km 1";

        String start2 = "Via B 2";
        String dest2 = "Via B 2";
        String km2 = "Km 2";

        private int[] likedButtons;
        private List<String> tracks;


        public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> tracks) {
            super(fm);
            this.likedButtons = new int[getCount()];
            this.tracks = tracks;

            /** Esempio di track: 239$45.92209263216224$12.735204845666884$45.92281540750679$12.735194116830826$0$0$mfhwGugvlA!mfhwGugvlAYJ!gghwGigvlAWJ!_hhwG}fvlAOiA!ohhwGgivlAK{@!$1"
             *
             * La stringa contiene 9 elementi, divisi dal $
             *
             * 1) sid
             * 2) start lat
             * 3) start lng
             * 4) end lat
             * 5) end lng
             * 6) length
             * 7) difficulty
             * 8) path
             * 9) likes
             *
             * Sotto c'è un ciclo che stampa tutti gli elementi di tutte le track (al momento nel db ce n'è una sola)
             * Per decodificare il percorso usare il metodo PolyUtil.decode(path) e per disegnarlo usare il metodo drawLine di MapsActivity
             */

            for (String track : tracks) {     // track è la stringa che contiene le info del percorso
                for (String trackinfo : track.split("\\$")) {  // trackinfo contiene una info
                    Toast.makeText(getApplicationContext(), trackinfo, Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public Fragment getItem(int position) {

            /** Tenere queste due righe sotto al posto delle rispettive copie nei vari cases, per renderizzare le mappe. **/
            //PlaceholderFragment placeholderFragment = new PlaceholderFragment();
            //getSupportFragmentManager().beginTransaction().add(placeholderFragment, placeholderFragment.getTag());

            switch(position) {
                case 0:
                    Fragment myFragment = new MyFragment().newInstance(0, start1, dest1, km1);
                    getSupportFragmentManager().beginTransaction().add(myFragment, myFragment.getTag());
                    return myFragment;
                case 1:
                    Fragment fragment2 = new MyFragment().newInstance(1, start2, dest2, km2);
                    getSupportFragmentManager().beginTransaction().add(fragment2, fragment2.getTag());
                    return fragment2;
                case 2:
                    Fragment fragment3 = new MyFragment().newInstance(2, "Via", "Mia", "3");
                    getSupportFragmentManager().beginTransaction().add(fragment3, fragment3.getTag());
                    return fragment3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Section 0";
                case 1:
                    return "Section 1";
                case 2:
                    return "Section 2";
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        MapView m;
        GoogleMap map;

        public PlaceholderFragment() {
        }

        public PlaceholderFragment newInstance() {
            return new PlaceholderFragment();
        }

        private static View view;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null)
                    parent.removeView(view);
            }
            try {
                view = inflater.inflate(R.layout.activity_truiton_map_fragment, container, false);
            } catch (InflateException e) {
        /* map is already there, just return view as it is */
            }
            return view;
        }

    }

}