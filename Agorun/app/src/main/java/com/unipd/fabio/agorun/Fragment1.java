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

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private ImageButton joinActivity;

    private  static String start;
    private  static String stop;
    private  static String km;
    private  static String exp;
    private String hour;
    private String date;
    private String activitySid;

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

        this.start =  getArguments().getString("start");
        this.stop =  getArguments().getString("stop");
        this.km =  getArguments().getString("km");
        this.exp =  getArguments().getString("exp");
        this.hour = getArguments().getString("hour");
        this.date = getArguments().getString("date");
        this.activitySid = getArguments().getString("activitySid");

        view = inflater.inflate(R.layout.fragment_fragment_new1, container,  false);

        TextView txtview1  = (TextView) view.findViewById(R.id.act_start);
        TextView txtview2  = (TextView) view.findViewById(R.id.act_dest);
        TextView txtview3  = (TextView) view.findViewById(R.id.act_exp);
        TextView txtview4  = (TextView) view.findViewById(R.id.act_km);

        this.joinActivity = (ImageButton) view.findViewById(R.id.act_join);

        txtview1.setText(this.start);
        txtview2.setText(this.stop);
        txtview3.setText(this.exp);
        txtview4.setText(this.km);

        joinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, activitySid);
                System.out.println("Just added: "+MySharedPreferencesHandler.getMySharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivitySid, ""));
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinedActivityHour, hour);
                MySharedPreferencesHandler.putSharedPreferencesString(getContext(), MySharedPreferencesHandler.MyPreferencesKeys.joinActivityDate, date);

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
            }
        });

        view.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeTop() {
                Toast.makeText(getContext(), "top", Toast.LENGTH_SHORT).show();
                Log.d("top","top");
                fragment3 = new Fragment3();
                moveToFragment(fragment3);


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
}