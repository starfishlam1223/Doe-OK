package com.starfishlam.doeok.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.starfishlam.doeok.MainApp;
import com.starfishlam.doeok.R;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private View root;

    private LocationRequest request;
    private GoogleApiClient client;

    private MapViewModel mapViewModel;
    LatLng prevLatLng, currentLatLng;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        root = inflater.inflate(R.layout.fragment_map, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        client = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
        } else {
            prevLatLng = currentLatLng;
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions options = new MarkerOptions();
            options.position(currentLatLng);
            options.title("Current Position");

            Marker marker = map.addMarker(options);
            Restaurant currnetLocation = new Restaurant("", "", "", 0);
            marker.setTag(currnetLocation);

            if (prevLatLng == null) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
                map.animateCamera(update);

                retrieveRestaurants();
            } else if (distBetweenLatLng(prevLatLng, currentLatLng) > 500) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
                map.animateCamera(update);

                retrieveRestaurants();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void retrieveRestaurants() {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("&location=" + currentLatLng.latitude + "," + currentLatLng.longitude);
        sb.append("&radius=" + 2000);
        sb.append("&type=" + "restaurant");
        sb.append("&key=" + getResources().getString(R.string.google_places_key));

        String url = sb.toString();

        Object data[] = new Object[5];
        data[0] = map;
        data[1] = url;
        data[2] = getContext();
        data[3] = getResources().getString(R.string.google_places_key);
        MainApp parent = (MainApp) getActivity();
        data[4] = parent.getCurrentUserID();

        GetNearbyPlaces gnp = new GetNearbyPlaces();
        gnp.execute(data);
    }

    private double distBetweenLatLng(LatLng latLng1, LatLng latLng2) {
        double lat1 = latLng1.latitude;
        double lon1 = latLng1.longitude;
        double lat2 = latLng2.latitude;
        double lon2 = latLng2.longitude;

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);}

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);}

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);}
}