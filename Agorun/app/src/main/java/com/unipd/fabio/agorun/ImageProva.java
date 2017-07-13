package com.unipd.fabio.agorun;

import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageProva implements DBConnection{

    private String encodedImage;
    private Bitmap image;
    private ImageView imageView;

    public ImageProva(ImageView imageView, Bitmap bitmap) {
        this.imageView = imageView;
        image = bitmap;
    }


    public void onClickSendImage (View view) {
        new ConnectDB(this).execute("uploadimage",encodedImage);
    }

    public Bitmap getImageBitmap() {
        return this.image;
    }

    public void onClickGetImage () {
        new ConnectDB(this).execute("getimage");
    }

    public void onTaskCompleted (ArrayList<String> ls) {

            String encodedString = ls.get(0);

            byte[] b = Base64.decode(encodedString,Base64.DEFAULT);
            //image = BitmapFactory.decodeByteArray(b, 0, b.length);

            //image = BitmapFactory.decodeResource(,R.mipmap.ic_launcher);

            imageView.setImageBitmap(image);

    }

}
