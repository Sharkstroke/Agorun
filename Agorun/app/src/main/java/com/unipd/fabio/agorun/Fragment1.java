package com.unipd.fabio.agorun;

/**
 * Created by Giulio on 06/06/2017.
 */


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment implements DBConnection {

    private ImageButton joinActivity;
    private ImageButton disjoinActivity;

    private MapsActivity mapsActivity;

    private  static String start;
    private  static String stop;
    private  static String km;
    private  static String exp;
    private  static String name;
    private String hour;
    private String date;
    private String activitySid;
    private String emailCreator;
    private String markerId;

    private View view;
    private Fragment3 fragment3;


    public Fragment1() {
        // Required empty public constructor
    }

    public  String getExp() {
        return exp;
    }

    public  String getStart() {
        return start;
    }

    public  String getStop() {
        return stop;
    }

    public  String getKm() {
        return km;
    }

    public String getHour() { return hour; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public static void setExp(String exp) {
        Fragment1.exp = exp;
    }

    public static void setKm(String km) {
        Fragment1.km = km;
    }

    public static void setStart(String start) {
        Fragment1.start = start;
    }

    public static void setStop(String stop) {
        Fragment1.stop = stop;
    }

    public void setHour(String hour) { this.hour = hour; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mapsActivity = MapsActivity.getMapsData();

        this.start =  getArguments().getString("start");
        this.stop =  getArguments().getString("stop");
        this.km =  getArguments().getString("km");
        this.exp =  getArguments().getString("exp");
        this.name =  getArguments().getString("name");
        this.hour = getArguments().getString("hour");
        this.date = getArguments().getString("date");
        this.activitySid = getArguments().getString("activitySid");
        this.emailCreator = getArguments().getString("email");
        this.markerId = getArguments().getString("markerId");

        view = inflater.inflate(R.layout.fragment_fragment_new1, container,  false);

        TextView txtview1  = (TextView) view.findViewById(R.id.act_start);
        TextView txtview2  = (TextView) view.findViewById(R.id.act_dest);
        TextView txtview3  = (TextView) view.findViewById(R.id.act_exp);
        TextView txtview4  = (TextView) view.findViewById(R.id.act_km);
        TextView txtview5  = (TextView) view.findViewById(R.id.Creator);
        TextView txtview6  = (TextView) view.findViewById(R.id.act_hour);
        TextView txtview7  = (TextView) view.findViewById(R.id.act_date);


        this.joinActivity = (ImageButton) view.findViewById(R.id.act_join);
        this.disjoinActivity = (ImageButton) view.findViewById(R.id.act_disjoin);

        txtview1.setText(this.start);
        txtview2.setText(this.stop);
        txtview3.setText(this.exp);
        txtview4.setText(this.km);
        txtview5.setText(this.name);
        txtview6.setText(this.hour);
        txtview7.setText(this.date);

        joinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectDB(Fragment1.this).execute("joinrun",activitySid);
            }
        });

        disjoinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectDB(Fragment1.this).execute("disjoinrun",activitySid);
            }
        });

        view.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeTop() {
/*<<<<<<< HEAD
                Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();
                Log.d("top","top");
                fragment3 = new Fragment3();

                Bundle args = new Bundle();
                args.putString("name" , name);
                args.putString("start" , start);
                args.putString("stop",stop);
                args.putString("km", km);
                args.putString("exp", exp);
                args.putString("hour", hour);
                args.putString("date", date);
                args.putString("noj", getArguments().getString("noj"));
                args.putString("medlevel",  getArguments().getString("medlevel"));



                fragment3.setArguments(args);
                moveToFragment(fragment3);
=======*/

                    MapsActivity.getMapsData().setSummarySwiped(true);
                    Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();
                    Log.d("top", "top");
                    fragment3 = new Fragment3();

                    Bundle args = new Bundle();
                    args.putString("name", name);
                    args.putString("start", start);
                    args.putString("stop", stop);
                    args.putString("km", km);
                    args.putString("exp", exp);
                    args.putString("hour", hour);
                    args.putString("date", date);
                    args.putString("noj", getArguments().getString("noj"));
                    args.putString("medlevel", getArguments().getString("medlevel"));
                    args.putString("email",emailCreator);


                    fragment3.setArguments(args);
                    moveToFragment(fragment3);

// >>>>>>> 078eafc03bd11c77602ecae4ea0020c487bebc22


            }

            public void onSwipeRight() {
                Toast.makeText(getContext(), "right", Toast.LENGTH_SHORT).show();
                Log.d("right","right");
            }
            public void onSwipeLeft() {
                Toast.makeText(getContext(), "left", Toast.LENGTH_SHORT).show();
                Log.d("left","left");
            }
            public void onSwipeBottom() {
                Toast.makeText(getContext(), "bottom", Toast.LENGTH_SHORT).show();
                Log.d("bottom","bottom");
            }

            /*public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(getContext(),"left",Toast.LENGTH_SHORT).show();
                }
                return true;
            }*/
        });

        return view;

    }

    private void moveToFragment(Fragment fragment) {



        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_to_top, R.anim.slide_down)
                .add(R.id.map, fragment, "second").addToBackStack(null).commit();

    }

    @Override
    public void onTaskCompleted(ArrayList<String> result) {
        switch (result.get(0)) {
            case "Join Riuscito":
                if (MySharedPreferencesHandler.getMySharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, "") != "" &&
                        MySharedPreferencesHandler.getMySharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate, "") != "" &&
                        MySharedPreferencesHandler.getMySharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, "") != "") {

                    // Cancello
                    MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid);
                    MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate);
                    MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour);
                }
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, activitySid);
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, hour);
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate, date);

                if (MapsActivity.getMapsData().isTimeForMonitoring()) {
                    MapsActivity.getMapsData().setStartMonitoringVisibility(1);
                }

                boolean alarm = (PendingIntent.getBroadcast(MapsActivity.getMapsData(), 0, new Intent("ALARM"), PendingIntent.FLAG_NO_CREATE) == null);

                if(alarm){
                    Intent itAlarm = new Intent("ALARM");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MapsActivity.getMapsData(),0,itAlarm,0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.SECOND, 3);
                    AlarmManager alarme = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    alarme.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60000, pendingIntent);
                }
                Toast.makeText(getContext(), "Joined!", Toast.LENGTH_SHORT).show();
                for (Marker m : mapsActivity.getMarkersMap().keySet()) {
                    if (m.getId().equals(markerId))
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }

                break;
            case "Disjoined from this session":
                MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid);
                MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate);
                MySharedPreferencesHandler.removeMySharedPreferences(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour);

                mapsActivity.setStartMonitoringVisibility(false);
                mapsActivity.removeStartingCircle();
                for (Marker m : mapsActivity.getMarkersMap().keySet()) {
                    if (m.getId().equals(markerId))
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                Toast.makeText(getContext(), "Disjoined!", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "Error in this operation", Toast.LENGTH_SHORT).show();
        }
    }
}