package com.starfishlam.doeok.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.starfishlam.doeok.Common;
import com.starfishlam.doeok.R;
import com.starfishlam.doeok.ui.map.details.Details;

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
    String ApiKey;
    int currentUserId;
    Double currentLat, currentLng;
    Double restLat, restLng;

    Context mCtx;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        urlStr = (String) objects[1];
        mCtx = (Context) objects[2];
        ApiKey = (String) objects[3];
        currentUserId = (int) objects[4];
        currentLat = (Double) objects[5];
        currentLng = (Double) objects[6];

        try {
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
    protected void onPostExecute(final String s) {
        try {
            JSONObject rootObject = new JSONObject(s);
            JSONArray restaurants = rootObject.getJSONArray("results");
            Log.e("JSON rest no", String.valueOf(restaurants.length()));

            String latStr = "", lngStr = "";

            for(int i = 0; i < restaurants.length(); i++) {
                JSONObject restJson = restaurants.getJSONObject(i);
                JSONObject locationJson = restJson.getJSONObject("geometry").getJSONObject("location");

                latStr = locationJson.getString("lat");
                lngStr = locationJson.getString("lng");
                restLat = Double.parseDouble(latStr);
                restLng = Double.parseDouble(lngStr);

                String id = restJson.getString("id");

                String name = restJson.getString("name");
                String vicinity = restJson.getString("vicinity");

                int open = Common.REST_UNKNOWN;
                if (restJson.has("opening_hours")) {
                    if (restJson.getJSONObject("opening_hours").getBoolean("open_now")) {
                        open = Common.REST_OPEN;
                    } else {
                        open = Common.REST_CLOSE;
                    }
                }

                LatLng latLng = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lngStr));

                JSONArray photoDetails = restJson.getJSONArray("photos");
                String photo_reference = photoDetails.getJSONObject(0).getString("photo_reference");
                String photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photo_reference + "&key=" + ApiKey;

                IconGenerator ig = new IconGenerator(mCtx);
                ig.setColor(R.color.colorPrimaryDark); // green background
                ig.setTextAppearance(R.style.MarkerBoxText); // black text
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(ig.makeIcon(name)));

                Marker restMarker = mMap.addMarker(markerOptions);
                Restaurant rest = new Restaurant(id, name, vicinity, open, latLng.latitude, latLng.longitude, photo);
                restMarker.setTag(rest);
            }

            final Double finalCurLat = currentLat;
            final Double finalCurLng = currentLng;
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Restaurant info = (Restaurant) marker.getTag();
                    if (info.getName() != "") {
                        Intent details = new Intent(mCtx, Details.class);
                        details.putExtra("currentUser", currentUserId);
                        details.putExtra("info", info);
                        details.putExtra("currentLat", finalCurLat);
                        details.putExtra("currentLng", finalCurLng);
                        mCtx.startActivity(details);
                    }
                    return false;
                }
            });

            if (rootObject.has("next_page_token")) {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                sb.append("&pagetoken=" + rootObject.getString("next_page_token"));
                sb.append("&key=" + ApiKey);

                String url = sb.toString();

                Object data[] = new Object[7];
                data[0] = mMap;
                data[1] = url;
                data[2] = mCtx;
                data[3] = ApiKey;
                data[4] = currentUserId;
                data[5] = currentLat;
                data[6] = currentLng;

                GetNearbyPlaces gnp = new GetNearbyPlaces();
                gnp.execute(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
