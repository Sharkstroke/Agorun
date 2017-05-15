package com.unipd.fabio.agorun;

/**
 * Created by fabio on 15/05/17.
 */

public class MyTrack {

    private String start;
    private String destination;
    private int km;
    private String difficulty;

    public MyTrack(String start, String destination, String difficulty) {
        this.start = start;
        this.destination = destination;
        this.difficulty = difficulty;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getStart() {
        return this.start;
    }

    public String getDestination() {
        return this.destination;
    }

    public int getKm() {
        return this.km;
    }

    public String getDifficulty() { return this.difficulty; }
}
