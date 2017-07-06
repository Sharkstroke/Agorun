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

/**
 * Created by Giulio on 06/06/2017.
 */

public class Fragment3 extends Fragment implements View.OnClickListener {


    private ImageView img;
    private View v;

    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);



        TextView txtview1 = (TextView) view.findViewById(R.id.Creator);
        txtview1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview2 = (TextView) view.findViewById(R.id.joinN);
        txtview2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview3 = (TextView) view.findViewById(R.id.Start);
        txtview3.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview4 = (TextView) view.findViewById(R.id.Destination);
        txtview4.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview12 = (TextView) view.findViewById(R.id.Creator_text);
        txtview12.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview13 = (TextView) view.findViewById(R.id.joinN_text);
        txtview13.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview14 = (TextView) view.findViewById(R.id.Start_text);
        txtview14.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview15 = (TextView) view.findViewById(R.id.Destination_text);
        txtview15.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));



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
