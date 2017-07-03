package com.unipd.fabio.agorun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VotedTracks extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votedtracks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        System.out.println("onCreate");

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


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch(position) {
                case 0:
                    Fragment fragment = PlaceholderFragment.newInstance(position+1, start1, dest1, km1);
                    return fragment;
                case 1:
                    Fragment fragment2 = PlaceholderFragment.newInstance(position+2, start2, dest2, km2);
                    return fragment2;
                default:
                    return null;
            }
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 2;
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

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static String s = "startingP";
        private static String d = "destinationP";
        private static String k = "kmP";

        public static PlaceholderFragment newInstance(int sectionNumber, String starting, String destination, String totKm) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(s, starting);
            args.putString(d, destination);
            args.putString(k, totKm);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView start  = (TextView) rootView.findViewById(R.id.start);
            TextView destination = (TextView) rootView.findViewById(R.id.destination);
            TextView km = (TextView) rootView.findViewById(R.id.km);
            start.setText(getArguments().getString(s));
            destination.setText(getArguments().getString(d));
            km.setText(getArguments().getString(k));

            return rootView;
        }
    }
}