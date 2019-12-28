package com.starfishlam.doeok.ui.reviews;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.starfishlam.doeok.MainApp;
import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ReviewsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private ReviewsViewModel reviewsViewModel;

    String restIdStr, restNameStr, titleStr, timeStr, contentStr;
    int userId, ratingInt, idInt;

    TextView restName, title, rating, time;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        reviewsViewModel =
                ViewModelProviders.of(this).get(ReviewsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_reviews, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainApp parent = (MainApp) getActivity();
        userId = parent.getCurrentUserID();

        connect();
    }

    public void connect(){
        final ProgressDialog pdialog = new ProgressDialog(getActivity());

        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting...");
        pdialog.show();

        final String url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/my_reviews.php?userid=" + userId;

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                success = true;
                pdialog.setMessage("Loading...");
                pdialog.show();
                jsonString = getJsonPage(url);
                if (jsonString.equals("Fail to login"))
                    success = false;
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    parse_JSON_String_and_Switch_Activity(jsonString);
                } else {
                    alert( "Error", "Fail to connect" );
                }
                pdialog.hide();
            }
        }.execute("");
    }

    public void parse_JSON_String_and_Switch_Activity(String JSONString) {
        List<Review> reviews = new ArrayList<Review>();

        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            JSONArray jsonEvents = rootJSONObj.getJSONArray("list");

            for (int i = 0 ; i < jsonEvents.length(); i++) {
                JSONObject jsonEvent = jsonEvents.getJSONObject(i);

                restIdStr = jsonEvent.getString("restid");
                titleStr = jsonEvent.getString("title");
                timeStr = jsonEvent.getString("time");
                ratingInt = jsonEvent.getInt("rating");
                restNameStr = jsonEvent.getString("restname");
                contentStr = jsonEvent.getString("content");
                idInt = jsonEvent.getInt("id");

                Review review = new Review(idInt, restNameStr, "", timeStr, ratingInt, titleStr, contentStr);

                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyReviewsListAdapter adapter = new MyReviewsListAdapter(getActivity(), R.layout.fragment_reviews_item, reviews);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    public String getJsonPage(String url) {
        HttpURLConnection conn_object = null;
        final int HTML_BUFFER_SIZE = 2*1024*1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];

        try {
            URL url_object = new URL(url);
            conn_object = (HttpURLConnection) url_object.openConnection();
            conn_object.setInstanceFollowRedirects(true);

            BufferedReader reader_list = new BufferedReader(new InputStreamReader(conn_object.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_list, htmlBuffer, HTML_BUFFER_SIZE);
            reader_list.close();
            return HTMLSource;
        } catch (Exception e) {
            return "Fail to login";
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            if (conn_object != null) {
                conn_object.disconnect();
            }
        }
    }

    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException
    {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }

    protected void alert(String title, String mymessage){
        new AlertDialog.Builder(getActivity())
                .setMessage(mymessage)
                .setTitle(title)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton){}
                        }
                )
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Review review = (Review) parent.getItemAtPosition(position);

        Intent reviewsDetails = new Intent(view.getContext(), ReviewDetails.class);
        reviewsDetails.putExtra("review", review);
        startActivityForResult(reviewsDetails, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                connect();
            }
        }
    }
}