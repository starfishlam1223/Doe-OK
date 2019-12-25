package com.starfishlam.doeok.ui.map;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    GoogleMap mMap;
    String urlStr, data;
    InputStream is;
    BufferedReader br;
    StringBuilder sb;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        urlStr = (String) objects[1];

        try {
            Log.e("ADYNC", "Task started");
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            is = httpURLConnection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            String line = "";
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject rootObject = new JSONObject(s);
            JSONArray restaurants = rootObject.getJSONArray("results");
            Log.e("JSON rest no", String.valueOf(restaurants.length()));

            for(int i = 0; i < restaurants.length(); i++) {
                JSONObject restJson = restaurants.getJSONObject(i);
                JSONObject locationJson = restJson.getJSONObject("geometry").getJSONObject("location");

                String lat = locationJson.getString("lat");
                String lng = locationJson.getString("lng");

                String name = restJson.getString("name");
                String vicinity = restJson.getString("vicinity");

                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                Log.e("JSON rest name", name);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name);
                markerOptions.position(latLng);
                mMap.addMarker(markerOptions);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
