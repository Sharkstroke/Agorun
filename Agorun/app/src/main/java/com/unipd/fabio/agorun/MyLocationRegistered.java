package com.unipd.fabio.agorun;

import android.location.Location;

/**
 * Created by fabio on 15/05/17.
 */

public class MyLocationRegistered {

    private volatile Location location;

    public Location getPosition() {
        return this.location;
    }

    public void setPosition(Location location) {
        this.location = location;
    }
}
