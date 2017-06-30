package com.unipd.fabio.agorun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ImageProva extends AppCompatActivity implements DBConnection{

    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_prova);

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Base64encode",encodedImage);

    }

    public void onClickSendImage (View view) {
        new ConnectDB(this).execute("uploadimage",encodedImage);
    }

    public void onClickGetImage (View view) {
        new ConnectDB(this).execute("getimage");
    }

    public void onTaskCompleted (ArrayList<String> ls) {
        if (ls.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Image Sent", Toast.LENGTH_SHORT).show();
        } else if (ls.get(0).equals("Image not found")) {
            Toast.makeText(getApplicationContext(),ls.get(0),Toast.LENGTH_LONG).show();
        } else {

            String encodedString = ls.get(0);

            byte[] b = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);

            ImageView mImg;
            mImg = (ImageView) findViewById(R.id.imageview);
            mImg.setImageBitmap(image);

        }
    }
}
