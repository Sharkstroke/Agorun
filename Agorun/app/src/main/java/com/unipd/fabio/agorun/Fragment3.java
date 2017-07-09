package com.unipd.fabio.agorun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Giulio on 06/06/2017.
 */

public class Fragment3 extends Fragment implements View.OnClickListener {


    private ImageView img;
    private View v;
    private  static String start;
    private  static String stop;
    private  static String km;
    private  static String exp;
    private  static String name;
    private  static String noj;
    private  static String medlevel;
    private static String hour;
    private static String date;
    private String emailCreator;

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);

        this.start =  getArguments().getString("start");
        this.stop =  getArguments().getString("stop");
        this.km =  getArguments().getString("km");
        this.exp =  getArguments().getString("exp");
        this.name =  getArguments().getString("name");
        this.hour = getArguments().getString("hour");
        this.date = getArguments().getString("date");
        this.noj = getArguments().getString("noj");
        this.medlevel = getArguments().getString("medlevel");
        emailCreator = getArguments().getString("email");

        Toast.makeText(getContext(),emailCreator,Toast.LENGTH_SHORT).show();




        TextView txtview1 = (TextView) view.findViewById(R.id.Creator);
        txtview1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview2 = (TextView) view.findViewById(R.id.joinN);
        txtview2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview3 = (TextView) view.findViewById(R.id.Start);
        txtview3.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview4 = (TextView) view.findViewById(R.id.Destination);
        txtview4.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview5 = (TextView) view.findViewById(R.id.medlevel);
        txtview5.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview16 = (TextView) view.findViewById(R.id.medleve_text);
        txtview16.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview12 = (TextView) view.findViewById(R.id.Creator_text);
        txtview12.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview13 = (TextView) view.findViewById(R.id.joinN_text);
        txtview13.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview14 = (TextView) view.findViewById(R.id.Start_text);
        txtview14.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview15 = (TextView) view.findViewById(R.id.Destination_text);
        txtview15.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        txtview1.setText(this.name);
        txtview2.setText(this.noj);
        txtview3.setText(this.start);
        txtview4.setText(this.stop);
        txtview5.setText(this.medlevel);

        /*txtview6.setText(this.hour);
        txtview7.setText(this.date);*/


        img = (ImageView) view.findViewById(R.id.toprofile);
        img.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));







        img = (ImageView) view.findViewById(R.id.toprofile);
        img.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));




        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // get the button view
        ImageView v  = (ImageView) getView().findViewById(R.id.toprofile);
        // set a onclick listener for when the button gets clicked
        v.setOnClickListener(new View.OnClickListener() {
            // Start new list activity
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                Bundle b = new Bundle();
                b.putString("email",emailCreator);
                b.putString("name", name); //Your id
                b.putString("noj", noj); //Your id
                b.putString("start", start); //Your id
                b.putString("stop", stop); //Your id
                b.putString("medlevel", medlevel); //Your id
                b.putString("name", name); //Your id
                b.putString("name", name); //Your id
                myIntent.putExtras(b); //Put your id to your next Intent
                startActivity(myIntent);
            }
        });
    }


   /* public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.toprofile:
                gotoprofile();
                break;


        }
    }*/


    public void gotoprofile(View v) {
        Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        startActivity(myIntent);
    }


    @Override
    public void onClick(View v) {

    }
}
