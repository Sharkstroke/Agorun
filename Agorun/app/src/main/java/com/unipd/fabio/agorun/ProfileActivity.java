package com.unipd.fabio.agorun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by root on 04/07/17.
 */

public class ProfileActivity extends AppCompatActivity implements DBConnection {

    private TextView name;
    private TextView experience;
    private TextView rank;
    private TextView created;
    private ImageView profileImage;

    private static boolean IS_MY_PROFILE;

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
        profileImage = (ImageView) findViewById(R.id.profileImage);



        if (getIntent().getExtras() != null && getIntent().hasExtra("email")) {
            IS_MY_PROFILE = false;
            email = getIntent().getStringExtra("email");
        } else {
            IS_MY_PROFILE = true;
            email = ConnectDB.getUser();
        }

        new ConnectDB(this).execute("getimage", email);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_MY_PROFILE) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 1);
                }
            }
        });



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

    private String encodedImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        profileImage.setImageBitmap(selectedImage);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();

                        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        new ConnectDB(this).execute("uploadimage", encodedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }


    @Override
    public void onTaskCompleted(ArrayList<String> result) {
        if (result.get(0).equals("getimage")) {
            // Get immagine.
            if (result.get(1).equals("Image not found")) {
                Toast.makeText(getApplicationContext(), "Image not found", Toast.LENGTH_SHORT).show();
            } else {
                String encodedString = result.get(1);

                byte[] b = Base64.decode(encodedString,Base64.DEFAULT);
                Bitmap profImage = BitmapFactory.decodeByteArray(b, 0, b.length);
                profileImage.setImageBitmap(profImage);
            }
        } else {
            if (result.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Image Sent", Toast.LENGTH_SHORT).show();
            } else if (!result.get(0).equals("Problem getting informations") && !result.get(0).equals("User not found")) {
                String[] infos = result.get(0).split("\\|");
                name.setText(infos[0]);
                rank.setText(infos[2]);
                experience.setText(MapsActivity.getDifficultyRange(infos[3]));
                created.setText(infos[4]);
            } else {
                Toast.makeText(getApplicationContext(), "Problem getting infos", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
