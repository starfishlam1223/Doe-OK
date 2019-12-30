package com.starfishlam.doeok;

import java.io.Serializable;
import java.util.List;

public class Reviews implements Serializable {
    List<Review> reviews;

    public Reviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
