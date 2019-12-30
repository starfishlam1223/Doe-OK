package com.starfishlam.doeok.ui.map.details;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.starfishlam.doeok.Common;
import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;
import com.starfishlam.doeok.ui.map.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Details extends ListActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    Context mCtx = this;

    String userId;
    String fullnameStr;
    String commentDateStr;
    int ratingInt;
    String titleStr;
    String contentStr;

    LatLng restLatLng, currentLatLng;
    String restId;
    String nameStr;
    int openStatus;
    String vicinityStr;
    double avgRatingDb;
    String imageStr;

    RelativeLayout noReview;
    ConstraintLayout writeReview;
    TextView name, vicinity, distance, open, avgRating;
    EditText reviewTitle, reviewContent;
    RadioGroup rating;
    Button submit;
    ImageView image;

    int currentUserId;
    String reviewTitleStr, reviewContentStr;
    int reviewRatingInt;

    Boolean emptyTitle = true, emptyRating = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initializeFramework();

        noReview = findViewById(R.id.no_review_box);
        writeReview = findViewById(R.id.details_write_review);

        image = findViewById(R.id.details_image);
        name = findViewById(R.id.details_name);
        vicinity = findViewById(R.id.details_vicinity);
        distance = findViewById(R.id.details_distance);
        open = findViewById(R.id.details_open);
        avgRating = findViewById(R.id.details_rating);

        reviewTitle = findViewById(R.id.details_write_review_title);
        reviewContent = findViewById(R.id.details_write_review_content);

        rating = findViewById(R.id.details_write_review_rating);

        submit = findViewById(R.id.details_write_review_submit);

        Intent parent = getIntent();
        Restaurant rest = (Restaurant) parent.getSerializableExtra("info");
        restId = rest.getId();
        currentUserId = parent.getIntExtra("currentUser", -1);
        Double restLat = rest.getLat();
        Double restLng = rest.getLng();
        Log.e("restLat", String.valueOf(restLat));
        Log.e("restLng", String.valueOf(restLng));
        restLatLng = new LatLng(restLat, restLng);
        Double currentLat = parent.getDoubleExtra("currentLat", 0);
        Double currentLng = parent.getDoubleExtra("currentLng", 0);
        Log.e("currentLat", String.valueOf(currentLat));
        Log.e("currentLng", String.valueOf(currentLng));
        currentLatLng = new LatLng(currentLat, currentLng);
        imageStr = rest.getImage();

        Double ditanceDb = distBetweenLatLng(restLatLng, currentLatLng);
        Log.e("ditanceDb", String.valueOf(ditanceDb));
        int distanceInt = (int) Math.round(ditanceDb / 100) * 100;

        if (currentUserId == -1) {
            Toast msg = Toast.makeText(this ,"Cannot identify user!", Toast.LENGTH_LONG);
            msg.show();
            finish();
        }

        new DownloadImageTask(image).execute(imageStr);
        nameStr = rest.getName();
        openStatus = rest.getOpen();
        vicinityStr = rest.getVicinity();

        name.setText(nameStr);
        if (openStatus == Common.REST_OPEN) {
            open.setText(R.string.rest_open);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                open.setTextAppearance(R.style.RestOpen);
            }
        } else if (openStatus == Common.REST_CLOSE) {
            open.setText(R.string.rest_close);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                open.setTextAppearance(R.style.RestClosed);
            }
        }
        vicinity.setText(vicinityStr);
        distance.setText("~" + distanceInt + "m");

        setup_write_review();

        connect(Common.ACTION_GET_AVERAGE);
        connect(Common.ACTION_GET_REVIEW_LIST);
    }

    private void setup_write_review() {
        reviewTitle.addTextChangedListener(this);
        rating.setOnCheckedChangeListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewTitleStr = reviewTitle.getText().toString();
                reviewContentStr = reviewContent.getText().toString();

                connect(Common.ACTION_WRITE_REVIEW);
            }
        });
    }

    private void connect(final int taskCode) {
        final ProgressDialog pdialog = new ProgressDialog(this);

        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting...");
        pdialog.show();

        String url = "";
        if (taskCode == Common.ACTION_GET_AVERAGE) {
            url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/average.php?restid=" + restId;
        } else if (taskCode == Common.ACTION_GET_REVIEW_LIST) {
            url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/reviews.php?restid=" + restId;
        } else if (taskCode == Common.ACTION_WRITE_REVIEW) {
            url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/write.php?restid=" + restId + "&userid=" + currentUserId + "&rating=" + reviewRatingInt + "&title=" + reviewTitleStr + "&content=" + reviewContentStr + "&rest_name=" + nameStr;
        }

        final String finalUrl = url;
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                success = true;
                pdialog.setMessage("Loading...");
                pdialog.show();
                jsonString = getJsonPage(finalUrl);
                if (jsonString.equals("Fail to login"))
                    success = false;
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    if (taskCode == Common.ACTION_GET_AVERAGE) {
                        parse_JSON_String_and_Show_Average(jsonString);
                    } else if (taskCode == Common.ACTION_GET_REVIEW_LIST) {
                        parse_JSON_String_and_Show_Reviews(jsonString);
                    } else if (taskCode == Common.ACTION_WRITE_REVIEW) {
                        parse_JSON_String_and_Refresh(jsonString);
                    }
                } else {
                    alert( "Error", "Fail to connect" );
                }
                pdialog.hide();
            }
        }.execute("");
    }

    public void parse_JSON_String_and_Show_Average(String JSONString) {
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            if (rootJSONObj.getString("average") == "null") {
                avgRatingDb = 0;
            } else {
                avgRatingDb = rootJSONObj.getDouble("average");
            }

            avgRating.setText(Double.toString(avgRatingDb));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parse_JSON_String_and_Show_Reviews(String JSONString) {
        final List<Review> reviews = new ArrayList<Review>();

        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            final JSONArray jsonEvents = rootJSONObj.getJSONArray("list");

            if (jsonEvents.length() > 0) {
                noReview.setVisibility(View.GONE);
            }

            for (int i = 0 ; i < jsonEvents.length(); i++) {
                final JSONObject jsonEvent = jsonEvents.getJSONObject(i);

                userId = jsonEvent.getString("userid");
                if (Integer.parseInt(userId) == currentUserId) {
                    writeReview.setVisibility(View.GONE);
                }

                final QBUser[] reviewer = new QBUser[1];
                final int finalI = i;
                QBUsers.getUser(Integer.parseInt(userId)).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        reviewer[0] = qbUser;

                        fullnameStr = reviewer[0].getFullName();
                        try {
                            commentDateStr = jsonEvent.getString("time");
                            ratingInt = jsonEvent.getInt("rating");
                            titleStr = jsonEvent.getString("title");
                            contentStr = jsonEvent.getString("content");

                            Review review = new Review(nameStr, fullnameStr, commentDateStr, ratingInt, titleStr, contentStr);

                            reviews.add(review);

                            if (finalI == jsonEvents.length() - 1) {
                                ReviewListAdapter adapter = new ReviewListAdapter(mCtx, R.layout.activity_details_review, reviews);
                                setListAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error", e.getMessage());
                        finish();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parse_JSON_String_and_Refresh(String JSONString) {
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            if (rootJSONObj.getBoolean("result")) {
                connect(Common.ACTION_GET_AVERAGE);
                connect(Common.ACTION_GET_REVIEW_LIST);
            } else {
                Toast.makeText(this, "Review submit failed! Please try again!", Toast.LENGTH_LONG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        new AlertDialog.Builder(this)
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
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (allNonEmpty()) {
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
        }
    }

    public Boolean allNonEmpty() {
        String reviewTitleStr = reviewTitle.getText().toString();

        emptyTitle = reviewTitleStr.isEmpty();

        return (!emptyTitle && !emptyRating);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.details_write_1) {
            reviewRatingInt = 1;
        } else if (checkedId == R.id.details_write_2) {
            reviewRatingInt = 2;
        } else if (checkedId == R.id.details_write_3) {
            reviewRatingInt = 3;
        } else if (checkedId == R.id.details_write_4) {
            reviewRatingInt = 4;
        } else if (checkedId == R.id.details_write_5) {
            reviewRatingInt = 5;
        }

        emptyRating = false;

        if (allNonEmpty()) {
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
        }
    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), Common.APP_ID, Common.AUTH_KEY, Common.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Common.ACCOUNT_KEY);
    }

    private double distBetweenLatLng(LatLng latLng1, LatLng latLng2) {
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
