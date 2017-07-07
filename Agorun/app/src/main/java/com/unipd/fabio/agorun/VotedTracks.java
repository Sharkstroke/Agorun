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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

/* ## TODO: citare nei crediti la fonte delle icone con il pollice. Da inserire: Icon made by http://www.flaticon.com/authors/dave-gandy from www.flaticon.com*/

public class VotedTracks extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static boolean liked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votedtracks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String start1 = "Via A 1";
        String dest1 = "Via A 1";
        String km1 = "Km 1";

        String start2 = "Via B 2";
        String dest2 = "Via B 2";
        String km2 = "Km 2";

        private int[] likedButtons;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.likedButtons = new int[getCount()];
        }

        @Override
        public Fragment getItem(int position) {

            /** Tenere queste due righe sotto al posto delle rispettive copie nei vari cases, per renderizzare le mappe. **/
            //PlaceholderFragment placeholderFragment = new PlaceholderFragment();
            //getSupportFragmentManager().beginTransaction().add(placeholderFragment, placeholderFragment.getTag());

            switch(position) {
                case 0:
                    //Fragment fragment = PlaceholderFragment.newInstance(position+1, start1, dest1, km1);
                    //PlaceholderFragment placeholderFragment1 = new PlaceholderFragment();
                    //getSupportFragmentManager().beginTransaction().add(placeholderFragment1, placeholderFragment1.getTag());
                    Fragment myFragment = new MyFragment().newInstance(0, start1, dest1, km1);
                    getSupportFragmentManager().beginTransaction().add(myFragment, myFragment.getTag());
                    System.out.println("Caso 0");
                    return myFragment;
                case 1:
                    //PlaceholderFragment placeholderFragment2 = new PlaceholderFragment();
                    //getSupportFragmentManager().beginTransaction().add(placeholderFragment2, placeholderFragment2.getTag());
                    Fragment fragment2 = new MyFragment().newInstance(1, start2, dest2, km2);
                    getSupportFragmentManager().beginTransaction().add(fragment2, fragment2.getTag());
                    System.out.println("Caso 1");
                    return fragment2;
                    //Fragment fragment2 = new PlaceholderFragment();
                    //return fragment2;
                case 2:
                    //PlaceholderFragment placeholderFragment3 = new PlaceholderFragment();
                    //getSupportFragmentManager().beginTransaction().add(placeholderFragment3, placeholderFragment3.getTag());
                    Fragment fragment3 = new MyFragment().newInstance(2, "Via", "Mia", "3");
                    getSupportFragmentManager().beginTransaction().add(fragment3, fragment3.getTag());
                    System.out.println("Caso 2");
                    return fragment3;
                default:
                    return null;
            }
            //return PlaceholderFragment.newInstance(position + 1);
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
                    return "Pippo";
                case 1:
                    return "Paperino";
                case 2:
                    return "Topolino";
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
                //MapFragment mapFragment = (MapFragment) view.findViewById(R.id.mapFragment);

            } catch (InflateException e) {
        /* map is already there, just return view as it is */
            }
            return view;
        }

    }


    /*public static class MyFragment extends Fragment {

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

                SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapMyLayout);
                if (mapFragment != null) {
                    System.out.println("Non sono null");
                } else {
                    System.out.println("Sono null");
                }

                start.setText(getArguments().getString(startToShow));
                destination.setText(getArguments().getString(destinationToShow));

                km.setText(getArguments().getString(kmToShow));

                setLikeTrackLlistener(likeTrack);
            } catch (InflateException e) {
            }
            return view;
        }

        private void setLikeTrackLlistener(final FloatingActionButton likeTrack) {
            likeTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!liked) {
                        // Caso in cui l'utente abbia appena messo Like.
                        likeTrack.setImageResource(R.drawable.thumbs_up_hand_symbol);
                        liked = true;
                    } else {
                        // Caso in cui l'utente abbia appena tolto Like.
                        likeTrack.setImageResource(R.drawable.thumbs_up);
                        liked = false;
                    }

                }
            });
        }

    }*/
}