package com.starfishlam.doeok.ui.map.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ReviewListAdapter extends ArrayAdapter<Review> {
    Context mCtx;
    int resource;
    List<Review> reviewList;

    public ReviewListAdapter(Context mCtx, int resource, List<Review> reviewList) {
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

        TextView title = view.findViewById(R.id.review_title);
        TextView fullname = view.findViewById(R.id.review_fullname);
        TextView time = view.findViewById(R.id.review_datetime);
        TextView rating = view.findViewById(R.id.review_rating);
        TextView content = view.findViewById(R.id.review_content);

        final Review review = reviewList.get(position);

        title.setText(review.getTitle());
        fullname.setText(review.getFullname());
        time.setText(review.getCommentDate());
        rating.setText(Integer.toString(review.getRating()));
        content.setText(review.getContent());

        return view;
    }
}
