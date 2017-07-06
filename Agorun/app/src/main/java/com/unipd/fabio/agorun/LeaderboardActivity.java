package com.unipd.fabio.agorun;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by root on 06/07/17.
 */

public class LeaderboardActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        LinearLayout layout = (LinearLayout ) findViewById(R.id.list);
        final int N = 50; // total number of textviews to add

        final LinearLayout[] myitem = new LinearLayout[N];
        final ImageView[] myImageViews = new ImageView[N]; // create an empty array;
        final TextView[] myTextViews = new TextView[N];
        final TextView[] myTextViews2 = new TextView[N];
        final View[] myViews = new View[N];

        // create an empty array;

        for (int i = 0; i < N; i++) {
            // create a new textview
            final ImageView rowImageViews = new ImageView(this);
            final TextView rowTextViews = new TextView(this);
            final TextView rowTextViews2 = new TextView(this);
            final LinearLayout rowItems = new LinearLayout(this);
            final View rowView = new View(this);


            // set some properties of rowTextView or something
            rowItems.setOrientation(LinearLayout.HORIZONTAL);
            rowItems.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1f));

            rowImageViews.setImageResource(R.mipmap.ic_launcher_round);
            rowImageViews.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0.40f));

            rowTextViews.setText("Name Aka");
            rowTextViews.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0.20f));
            rowTextViews.setTextColor(Color.WHITE);
            rowTextViews.setTextSize(20);
            rowTextViews.setPadding(20,20,20,20);

            rowTextViews2.setText("Rank");
            rowTextViews2.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0.20f));
            rowTextViews2.setTextColor(Color.WHITE);
            rowTextViews2.setTextSize(20);
            rowTextViews2.setPadding(20,20,20,20);

            rowView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,2,1));
            rowView.setBackgroundColor(Color.WHITE);

            // add the textview to the linearlayout
            rowItems.addView(rowImageViews);
            rowItems.addView(rowTextViews);
            rowItems.addView(rowTextViews2);
            //rowItems.addView(rowView);
            layout.addView(rowItems);
            layout.addView(rowView);
            // save a reference to the textview for later
            myImageViews[i] = rowImageViews;
            myTextViews[i] = rowTextViews;
            myTextViews2[i] = rowTextViews2;
            myitem[i] = rowItems;
        }


    }
}
