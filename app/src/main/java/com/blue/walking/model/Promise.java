package com.blue.walking.model;

import java.io.Serializable;

public class Promise implements Serializable {

    public String txtDate;
    public String txtTime;
    public String txtPlace;

    public Promise() {
    }

    public Promise(String txtDate, String txtTime, String txtPlace) {
        this.txtDate = txtDate;
        this.txtTime = txtTime;
        this.txtPlace = txtPlace;
    }
}
