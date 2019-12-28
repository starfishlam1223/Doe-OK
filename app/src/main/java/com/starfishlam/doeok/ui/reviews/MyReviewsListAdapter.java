package com.starfishlam.doeok.ui.reviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import static android.app.Activity.RESULT_OK;

public class MyReviewsListAdapter extends ArrayAdapter<Review> {
    Context mCtx;
    int resource;
    List<Review> reviewList;

    public MyReviewsListAdapter(Context mCtx, int resource, List<Review> reviewList) {
        super(mCtx, resource, reviewList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        final View view = inflater.inflate(resource, null);

        TextView rest = view.findViewById(R.id.my_review_rest);
        TextView title = view.findViewById(R.id.my_review_title);
        TextView time = view.findViewById(R.id.my_review_datetime);
        TextView rating = view.findViewById(R.id.my_review_rating);

        final Review review = reviewList.get(position);

        rest.setText(review.getRestName());
        title.setText(review.getTitle());
        time.setText(review.getCommentDate());
        rating.setText(Integer.toString(review.getRating()));


        return view;
    }
}