package com.example.wangboyuan.placessearch;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
//    The minimum distance to change Updates in meters
//    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
//
//    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
//    The minimum time between updates in milliseconds
//    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1; // 1 minute
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            showMessage("ACCESS_FINE_LOCATION return!");
//            return;
//        } else {
//            showMessage("ACCESS_FINE_LOCATION checked approved.");
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, (LocationListener) this, null);
//        if (location == null) {
//            showMessage("Changed to NETWORK_PROVIDER.");
//            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        }else{
//            //showMessage("GPS_PROVIDER!");
//        }
//        if (location != null) {
//            latitude = location.getLatitude();
//            longitude = location.getLongitude();
//            showMessage(latitude + "," + longitude);
//        } else {
//            //getLngAndLatWithNetwork();
//            showMessage("NULL!");
//        }

public class GPSTracker extends Service implements LocationListener {

    private double latitude;
    private double longitude;

    @SuppressLint("MissingPermission")
    public GPSTracker(LocationManager locationManager){
        //@SuppressLint("MissingPermission")
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, (LocationListener) this, null);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
