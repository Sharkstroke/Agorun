package com.unipd.fabio.agorun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by root on 04/07/17.
 */

public class ProfileActivity extends AppCompatActivity implements DBConnection {

    private TextView name;
    private TextView experience;
    private TextView rank;
    private TextView created;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        RatingBar r = (RatingBar) findViewById(R.id.ratingBar);
        r.setEnabled(false);

        name = (TextView) findViewById(R.id.act_name);
        experience = (TextView) findViewById(R.id.experience);
        rank = (TextView) findViewById(R.id.rank);
        created = (TextView) findViewById(R.id.created);

        if (getIntent().getExtras() != null && getIntent().hasExtra("email")) {
            email = getIntent().getStringExtra("email");
        } else {
            email = ConnectDB.getUser();
        }

        //Toast.makeText(getApplicationContext(),email,Toast.LENGTH_SHORT).show();

        new ConnectDB(this).execute("getinfouser",email);


/*
        File sd = Environment.getExternalStorageDirectory();
        File image = new File("", "1.png");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        ImageView view = (ImageView) findViewById(R.id.imageView2) ;
        view.setImageBitmap(RoundedImageView.getCroppedBitmap(bitmap,360));*/



    }


    @Override
    public void onTaskCompleted(ArrayList<String> result) {
        if (! result.get(0).equals("Problem getting informations") && ! result.get(0).equals("User not found")) {
            String[] infos = result.get(0).split("\\|");
            name.setText(infos[0]);
            rank.setText(infos[2]);
            experience.setText(MapsActivity.getDifficultyRange(infos[3]));
            created.setText(infos[4]);
        } else {
            Toast.makeText(getApplicationContext(),"Problem getting infos",Toast.LENGTH_SHORT).show();
        }
    }
}
