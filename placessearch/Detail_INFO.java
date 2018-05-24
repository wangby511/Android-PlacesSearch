package com.example.wangboyuan.placessearch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.ArrayList;


public class Detail_INFO extends Fragment {
    private View rootview;
    private Place MyPlace = null;
    private String cid;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        showMessage("oncreate!");
//        super.onCreate(savedInstanceState);
//    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //showMessage("Detail_INFO!");
        rootview = inflater.inflate(R.layout.detail_info,container,false);
        ViewGroup parent = (ViewGroup) rootview.getParent();
        if(parent != null){
            parent.removeView(rootview);
        }
        if(MyPlace != null){
            try {
                //showMessage("staticMyPlace = " + MyPlace.toString());
                dealWithInfo(MyPlace);
            }catch (Exception e){
                showMessage(e.getLocalizedMessage() + "!");
            }
        }
        //showMessage("myPlace = " + staticMyPlace.toString());
        return rootview;
    }

    public void setMyPlace(Place x){
        MyPlace = x;
    }

    public ArrayList<String> dealWithInfo(Place myPlace) throws Exception{
        MyPlace = myPlace;
        ArrayList<String> res = new ArrayList<>();
        res.add("");
        res.add("");

        String URL = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + myPlace.getId() + "&key=AIzaSyDtK77ffpHmX74uUppSWvTp5ZTdCH31d1c";
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject temp1 = new JSONObject(response);
                    JSONObject temp2 = (JSONObject) temp1.get("result");
                    String cid = temp2.getString("url");
                    //showMessage("cid = " + cid);
                    //5.LatLngBounds
                    TextView textView5 = rootview.findViewById(R.id.page_value);
                    textView5.setText(cid);
                }catch (Exception e){
                    //showMessage("error in url!");
                    TextView textView5 = rootview.findViewById(R.id.page_value);
                    //textView5.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

        //showMessage("myPlace = " + staticMyPlace.toString());
        //1.
        TextView textView1 = rootview.findViewById(R.id.address_value);
        textView1.setVisibility(View.VISIBLE);
        if (myPlace.getAddress() != null) {
            String address = myPlace.getAddress().toString();
            res.set(0,address);
            textView1.setText(address);
        } else {
            textView1.setVisibility(View.INVISIBLE);
        }

        //2.
        TextView textView2 = rootview.findViewById(R.id.phone_value);
        textView2.setVisibility(View.VISIBLE);
        if (myPlace.getPhoneNumber() != null) {
            String phoneNumber = myPlace.getPhoneNumber().toString();
            textView2.setText(phoneNumber);
        }else{
            textView2.setVisibility(View.INVISIBLE);
        }

        //3.
        TextView textView3 = rootview.findViewById(R.id.pricelevel_value);
        textView3.setVisibility(View.VISIBLE);
        if(myPlace.getPriceLevel() != -1) {
            String priceLevel = "";
            for (int i = 0; i < myPlace.getPriceLevel(); i++) {//getPriceLevel() – 此地点的价位，以整数形式返回，其值范围为 0（最便宜）到 4（最昂贵）
                priceLevel += "$";
            }
            textView3.setText(priceLevel);
        }else{
            textView3.setVisibility(View.INVISIBLE);
        }

        //4.RatingBar,rating
        RatingBar textView4 = (RatingBar)rootview.findViewById(R.id.ratingBar);
        float rating = myPlace.getRating();
        textView4.setRating(rating);

        //6.getWebsiteUri
        TextView textView6 = rootview.findViewById(R.id.website_value);
        textView6.setVisibility(View.VISIBLE);
        if(myPlace.getWebsiteUri() != null) {
            String websiteUrl = myPlace.getWebsiteUri().toString();
            res.set(1,websiteUrl);
            textView6.setText(websiteUrl);
        }else{
            textView6.setVisibility(View.INVISIBLE);
        }
        return res;
    }

    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 10);
        toast.show();
    }
}

//public class MyActivity extends FragmentActivity implements OnConnectionFailedListener {
//    private GoogleApiClient mGoogleApiClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    // TODO: Please implement GoogleApiClient.OnConnectionFailedListener to
//    // handle connection failures.
//}
