package com.starfishlam.doeok.ui.reviews;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.starfishlam.doeok.Common;
import com.starfishlam.doeok.R;
import com.starfishlam.doeok.Review;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReviewDetails extends AppCompatActivity implements TextWatcher, RadioGroup.OnCheckedChangeListener {

    Review reviewOri;
    String titleOriStr, contentOriStr;
    String titleNewStr, contentNewStr;

    TextView title, content;
    RadioGroup rating;
    Button save, delete;

    int id, ratingOriInt, ratingNewInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);

        Intent parent = getIntent();
        reviewOri = (Review) parent.getSerializableExtra("review");
        id = reviewOri.getId();

        if (id == -1) {
            Toast.makeText(this, "Cannot find Review!", Toast.LENGTH_SHORT).show();
            finish();
        }

        title = findViewById(R.id.details_edit_review_title);
        content = findViewById(R.id.details_edit_review_content);
        rating = findViewById(R.id.details_edit_review_rating);
        save = findViewById(R.id.details_edit_review_save);
        delete = findViewById(R.id.details_edit_review_delete);

        titleOriStr = reviewOri.getTitle();
        contentOriStr = reviewOri.getContent();
        ratingOriInt = reviewOri.getRating();
        ratingNewInt = reviewOri.getRating();

        title.setText(titleOriStr);
        content.setText(contentOriStr);
        if (ratingOriInt == 1) {
            rating.check(R.id.details_edit_1);
        } else if (ratingOriInt == 2) {
            rating.check(R.id.details_edit_2);
        } else if (ratingOriInt == 3) {
            rating.check(R.id.details_edit_3);
        } else if (ratingOriInt == 4) {
            rating.check(R.id.details_edit_4);
        } else if (ratingOriInt == 5) {
            rating.check(R.id.details_edit_5);
        }

        title.addTextChangedListener(this);
        rating.setOnCheckedChangeListener(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleNewStr = title.getText().toString();
                contentNewStr = content.getText().toString();

                connect(Common.ACTION_EDIT_REVIEW);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect(Common.ACTION_DELETE_REVIEW);
            }
        });
    }

    private void connect(final int taskCode) {
        final ProgressDialog pdialog = new ProgressDialog(this);

        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting...");
        pdialog.show();

        String url = "";
        if (taskCode == Common.ACTION_DELETE_REVIEW) {
            url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/delete.php?id=" + id;
        } else if (taskCode == Common.ACTION_EDIT_REVIEW) {
            url = "https://i.cs.hku.hk/~hslam/comp3330/doe-ok/php/edit.php?id=" + id  + "&rating=" + ratingNewInt + "&title=" + titleNewStr + "&content=" + contentNewStr;
        }
        Log.e("url", url);

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
                    parse_JSON_String_and_Go_Back(jsonString);
                } else {
                    alert( "Error", "Fail to connect" );
                }
                pdialog.hide();
            }
        }.execute("");
    }

    public void parse_JSON_String_and_Go_Back(String JSONString) {
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            if (rootJSONObj.getBoolean("result")) {
                Intent result = new Intent();
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toast.makeText(this, "Request failed! Please try again!", Toast.LENGTH_LONG);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (allNonEmpty()) {
            save.setEnabled(true);
        } else {
            save.setEnabled(false);
        }
    }

    public Boolean allNonEmpty() {
        String titleStr = title.getText().toString();

        boolean emptyTitle = titleStr.isEmpty();

        return !emptyTitle;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.details_edit_1) {
            ratingNewInt = 1;
        } else if (checkedId == R.id.details_edit_2) {
            ratingNewInt = 2;
        } else if (checkedId == R.id.details_edit_3) {
            ratingNewInt = 3;
        } else if (checkedId == R.id.details_edit_4) {
            ratingNewInt = 4;
        } else if (checkedId == R.id.details_edit_5) {
            ratingNewInt = 5;
        }

        if (allNonEmpty()) {
            save.setEnabled(true);
        } else {
            save.setEnabled(false);
        }
    }
}
