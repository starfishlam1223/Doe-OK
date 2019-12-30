package com.starfishlam.doeok.ui.map.details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;
import com.starfishlam.doeok.Reviews;

import java.util.List;

public class ViewReviews extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        Intent parent = getIntent();
        Reviews reviewsObj = (Reviews) parent.getSerializableExtra("reviews");
        List<Review> reviews = reviewsObj.getReviews();

        ReviewListAdapter adapter = new ReviewListAdapter(this, R.layout.activity_details_review, reviews);
        setListAdapter(adapter);
    }
}
