package com.unipd.fabio.agorun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

/**
 * Created by fabio on 15/05/17.
 */

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_track);

        // TODO: creare il button per intercettare l'avvenuta creazione della attività e creare il marker sulla mappa.
    }


    public void clickbutton(View v) {
        final Spinner experienceSpinner = (Spinner) findViewById(R.id.ExperienceSpinner);
        String experience = experienceSpinner.getSelectedItem().toString();
        System.out.println("Ho scelto: "+experience.toString());
    }
}
