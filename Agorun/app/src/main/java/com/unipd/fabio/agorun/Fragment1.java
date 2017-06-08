package com.unipd.fabio.agorun;

/**
 * Created by Giulio on 06/06/2017.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment1 extends Fragment {

    private  static String start;
    private  static String stop;
    private  static String km;
    private  static String exp;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.start =  getArguments().getString("start");
        this.stop =  getArguments().getString("stop");
        this.km =  getArguments().getString("km");
        this.exp =  getArguments().getString("exp");

        View view = inflater.inflate(R.layout.fragment_fragment1, container,  false);
        TextView txtview1  = (TextView) view.findViewById(R.id.act_start);
        TextView txtview2  = (TextView) view.findViewById(R.id.act_dest);
        TextView txtview3  = (TextView) view.findViewById(R.id.act_exp);
        TextView txtview4  = (TextView) view.findViewById(R.id.act_km);
        txtview1.setText(this.start);
        txtview2.setText(this.stop);
        txtview3.setText(this.exp);
        txtview4.setText(this.km);


        return view;



    }

}