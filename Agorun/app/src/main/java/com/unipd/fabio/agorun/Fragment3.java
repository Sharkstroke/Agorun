package com.unipd.fabio.agorun;

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

public class Fragment3 extends Fragment {



    public Fragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment2, container,  false);
        TextView txtview1  = (TextView) view.findViewById(R.id.act_user);
        txtview1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview2  = (TextView) view.findViewById(R.id.act_start);
        txtview2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview3  = (TextView) view.findViewById(R.id.act_dest);
        txtview3.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview4  = (TextView) view.findViewById(R.id.act_exp);
        txtview4.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        TextView txtview5  = (TextView) view.findViewById(R.id.act_km);
        txtview5.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right));

        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        img.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.left_to_right));

        return view;

    }

}
