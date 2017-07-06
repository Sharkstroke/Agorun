package com.unipd.fabio.agorun;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.File;

import static android.R.attr.bitmap;
import static com.unipd.fabio.agorun.R.id.imageView;

/**
 * Created by root on 04/07/17.
 */

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        RatingBar r = (RatingBar) findViewById(R.id.ratingBar);
        r.setEnabled(false);


/*
        File sd = Environment.getExternalStorageDirectory();
        File image = new File("", "1.png");
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        ImageView view = (ImageView) findViewById(R.id.imageView2) ;
        view.setImageBitmap(RoundedImageView.getCroppedBitmap(bitmap,360));*/



    }


}
