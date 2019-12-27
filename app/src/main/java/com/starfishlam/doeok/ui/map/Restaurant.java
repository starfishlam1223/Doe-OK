package com.starfishlam.doeok.ui.map;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String id, name, vicinity;
    int open;

    public Restaurant(String id, String name, String vicinity, int open) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.open = open;
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
}
