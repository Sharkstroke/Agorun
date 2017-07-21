package com.unipd.fabio.agorun;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SelfieActivity extends AppCompatActivity {

    private static final String PERMISSION = "publish_actions";
    private final int CAMERA_REQUEST = 2;



    private boolean canPresentShareDialogWithPhotos;
    private boolean dialogShowed, cancelProgressBar;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private File image;
    private ProgressBar progressBar;

    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onCancel() {
            Log.d("HelloFacebook", "Canceled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
            String title = getString(R.string.error);
            String alertMessage = error.getMessage();
            showResult(title, alertMessage);
        }

        @Override
        public void onSuccess(Sharer.Result result) {
            Log.d("HelloFacebook", "Success!");
            if (result.getPostId() != null) {
                String title = getString(R.string.success);
                String id = result.getPostId();
                String alertMessage = getString(R.string.successpost);
                showResult(title, alertMessage);
            }
        }

        private void showResult(String title, String alertMessage) {
            new AlertDialog.Builder(SelfieActivity.this)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);

        callbackManager = CallbackManager.Factory.create();


        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);

        final Button takeASelfie = (Button) findViewById(R.id.takeaselfiebtn);
        takeASelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeASelfie();
            }
        });

        final Button noSelfie = (Button) findViewById(R.id.noselfiebtn);
        noSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbarselfie);
    }

    private void takeASelfie() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("Exception", "IOException");
            }
            // Continue only if the File was successfully created
            if (image != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private void createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    //    String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                "agorun",  // prefix
                ".jpg",       // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = "file://" + image.getAbsolutePath();
        // return image;
    }

    private void postPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        while (bitmap == null) {    // BRUTE FORCE
            bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        }
        // progressBar.setVisibility(View.INVISIBLE);
        //         Log.d("bitmap",bitmap.toString());
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        bitmap1 = Bitmap.createScaledBitmap(bitmap1, 400, 400, true);

        Bitmap bmOverlay = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        canvas.drawBitmap(bitmap1, 0, 0, null);

        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bmOverlay).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().setPhotos(photos).build();
        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            // pendingAction = PendingAction.POST_PHOTO;
            // We need to get new permissions, then complete the action when we get called back.
            LoginManager.getInstance().logInWithPublishPermissions(
                    this,
                    Arrays.asList(PERMISSION));
        }
        dialogShowed = true;
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                postPhoto();
                callbackManager.onActivityResult(requestCode, resultCode, data);
      /*      } else if (resultCode == RESULT_CANCELED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);*/
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cancelProgressBar) {
            progressBar.setVisibility(View.INVISIBLE);
            cancelProgressBar = false;
            Toast.makeText(getApplicationContext(), "Cancel progress bar", Toast.LENGTH_SHORT).show();
        } else if (dialogShowed) {
            cancelProgressBar = true;
            dialogShowed = false;
            Toast.makeText(getApplicationContext(), "Dialog Showed", Toast.LENGTH_SHORT).show();
        }
    }
}
