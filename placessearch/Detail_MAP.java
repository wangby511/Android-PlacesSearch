package com.example.wangboyuan.placessearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("ValidFragment")
public class Detail_MAP extends Fragment implements OnMapReadyCallback {
    private View rootview;
    private GoogleMap mMap;
    private String latitude = "34.02";
    private String longitude = "-118.28";
    private String placeName = "Marker";
    private boolean ListenerFirstTime = true;
    private String responseContent;
    private LatLngBounds.Builder builder;


    @SuppressLint("ValidFragment")
    public Detail_MAP(String a, String b, String c) {
        placeName = a;
        latitude = b;
        longitude = c;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.detail_map, container, false);
        super.onCreate(savedInstanceState);
//        showMessage("Detail_MAP!");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
//        mapFragment.getMapAsync(this);
//        MapsActivity map = new MapsActivity();
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
//        FragmentManager fm = getSFragmentManager();
//        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.google_map);
        if(mapFragment != null) {
//            showMessage("Detail_MAP!YES!!!");
            mapFragment.getMapAsync((OnMapReadyCallback) this);
        }
        else {
//            showMessage("Detail_MAP!NULL!");
        }
        //auto-complete!
        AutoCompleteTextView searchPlace = rootview.findViewById(R.id.FromLocationWord);
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this.getContext());
        searchPlace.setAdapter(adapter);

        //OnItemSelectedListener
        AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(ListenerFirstTime)ListenerFirstTime = false;
                else{
                    SearchForRoutes(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        Spinner sp1 = rootview.findViewById(R.id.TravelModelWord);
        sp1.setOnItemSelectedListener(selectedListener);
        return rootview;
    }

    public void SearchForRoutes(int position){
        AutoCompleteTextView searchPlace = rootview.findViewById(R.id.FromLocationWord);
        String FromPlace = searchPlace.getText().toString().replace(" ","+").replace(",","+");
        //showMessage("selected" + position + ",FromPlace = " + FromPlace);
        String model[] = {"driving","bicycling","transit","walking"};
        String getDirectionsUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + FromPlace + "&destination=";
        getDirectionsUrl += latitude + "," + longitude + "&mode=" + model[position] + "&key=AIzaSyDtK77ffpHmX74uUppSWvTp5ZTdCH31d1c";
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, getDirectionsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseContent = response;
//                showMessage("response = " + response);
                drawLineForRoutes(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage("Error in Response for getting routes!");
            }
        });
        queue.add(stringRequest);

//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(51.5, -0.1), new LatLng(34.02, -118.28))
//                .width(10)
//                .color(Color.BLUE));
//        if(line!=null)showMessage("line yes!");
    }

    public void drawLineForRoutes(String response){
        try {
            mMap.clear();
            JSONObject Obj = new JSONObject(response);
            JSONArray routes = (JSONArray) Obj.get("routes");
            if(routes.length() == 0){
                showMessage("No Routes!");
                return;
            }
            JSONObject route1 = (JSONObject)routes.get(0);
            JSONArray legs = (JSONArray) route1.get("legs");
            builder = new LatLngBounds.Builder();
            for(int i = 0;i < legs.length();i++){
                JSONObject leg_i = legs.getJSONObject(i);
                JSONArray steps = (JSONArray)leg_i.get("steps");
                //showMessage(steps.length() + " =steps.length().");
                for(int j = 0;j < steps.length();j++){
                    JSONObject step_j = steps.getJSONObject(j);
                    JSONObject startLocation = (JSONObject)step_j.get("start_location");
                    JSONObject endLocation = (JSONObject)step_j.get("end_location");
                    double x1 = Double.parseDouble(startLocation.get("lat").toString());
                    double y1 = Double.parseDouble(startLocation.get("lng").toString());
                    double x2 = Double.parseDouble(endLocation.get("lat").toString());
                    double y2 = Double.parseDouble(endLocation.get("lng").toString());
                    //showMessage(x1 + "," + y1 + "," + x2 + "," + y2);
                    builder.include(new LatLng(x1,y1));
                    builder.include(new LatLng(x2,y2));
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(x1, y1), new LatLng(x2, y2)).width(10).color(Color.BLUE));
                }
                LatLngBounds bounds = builder.build();
                CameraPosition camPos = new CameraPosition.Builder().target(bounds.getCenter())
                        .zoom(12).build();
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(cu);
            }
            //showMessage("success ! legs.length = " + legs.length());

        }catch (Exception E){
            //showMessage("No Routes!");
        }

    }

//    public void setBasicInformation(String a,String b,String c){
//        placeName = a;
//        latitude = b;
//        longitude = c;
//        //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
//        //mapFragment.getMapAsync((OnMapReadyCallback) this);
//        //showMessage("yes!");
//    }

    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 210);
        toast.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        showMessage("latitude = " + latitude + " ,longitude = " + longitude);
        LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title(placeName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney , 14));
        //mMap.addMarker()
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
