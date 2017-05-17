package com.unipd.fabio.agorun;

import android.location.Location;

public class MyLocationRegistered {

    private volatile Location location;

    public Location getPosition() {
        return this.location;
    }

    public void setPosition(Location location) {
        this.location = location;
    }
}
