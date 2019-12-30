package com.starfishlam.doeok.ui.map;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String id, name, vicinity;
    int open;
    Double lat, lng;
    String image;

    public Restaurant(String id, String name, String vicinity, int open, Double lat, Double lng, String image) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.open = open;
        this.lat = lat;
        this.lng = lng;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public int getOpen() {
        return open;
    }

    public String getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getImage() {
        return image;
    }
}
