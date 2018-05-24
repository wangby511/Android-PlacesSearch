package com.example.wangboyuan.placessearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.example.wangboyuan.placessearch.R.layout.photo_item;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Details extends AppCompatActivity implements OnConnectionFailedListener {
    private String receivePlaceString;
    private String placeID;
    private String placeName;
    private String placeLat;
    private String placeLng;
    private String placeIcon;
    private String placeWebsiteUrl;
    private String placeAddress;
    private String splitBuffer = "////";

    private TabLayout tabLayout;
    private ViewPager tab_viewpager;
    private List<Fragment> mFragments;
    private List<Integer> iconList;
    private ArrayList<String> tabList;

    private GoogleApiClient mGoogleApiClient;
    private Place myShownPlace;

    private Detail_INFO detail_info;
    private Detail_PHOTOS detail_photos;
    private Detail_MAP detail_map;
    private Detail_REVIEWS detail_reviews;

    private List<Map<String, Object>> listphotos;
    private Bitmap bitmap;
    private int photoIndex = 0;
    private int totalphotoIndex;

    private GoogleMap mMap;
    private String googleReviews;
    private  ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_main);

        Intent intent = getIntent();
        receivePlaceString = intent.getStringExtra(DisplaySearchActivity.EXTRA_MESSAGE);

        ProgressDialog progressdialog = new ProgressDialog(Details.this);
        progressdialog.setMessage("Fetching details...");
        progressdialog.show();

        initViews();
        initGooglePlaceService();
        progressdialog.dismiss();
        //showMessage("YES!");
    }

    private void initGooglePlaceService() {
        //0.init googleapi
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        //1.deal with info
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                //showMessage("onResult!" + placeID);
                if (places.getStatus().isSuccess()) {
                    myShownPlace = places.get(0);
                    //showMessage("myShownPlace = " + myShownPlace.toString());
                    try {
                        ArrayList<String> temp = detail_info.dealWithInfo(myShownPlace);
                        placeWebsiteUrl = temp.get(1);
                        placeAddress    = temp.get(0);
                        //showMessage(placeWebsiteUrl + "," + placeAddress);
                    } catch (Exception e) {
                        //showMessage(e.getMessage());
                    }
                } else {
                    //Log.e(TAG, "Place not found");
                    //showMessage("Not Found");
                }
                places.release();
            }
        });
        //2.deal with photos
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeID).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(PlacePhotoMetadataResult photos) {
                if (!photos.getStatus().isSuccess()) {
                    return;
                }
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
//                    photoIndex = 0;
//                    totalphotoIndex = photoMetadataBuffer.getCount();
//                    showMessage("PlacePhotoMetadataBuffer!");
                    detail_photos.dealWithPhotos(photoMetadataBuffer,mGoogleApiClient,placeID);
//                    listphotos = new ArrayList<Map<String, Object>>();
//                    ImageView mImageView = findViewById(R.id.photo);
//                    showMessage(Integer.toString(photoMetadataBuffer.getCount()));
//                    for (int i = 0; i < totalphotoIndex; i++) {
//                        photoMetadataBuffer.get(i).getScaledPhoto(mGoogleApiClient, 500, 500).setResultCallback(mDisplayPhotoResultCallback);
//                    }
                } else {
                    //showMessage("error!");
                }
                photoMetadataBuffer.release();
            }
        });
        //3.deal with map
        //detail_map.setBasicInformation(placeName,placeLat,placeLng);

        //4.deal with reviews
        String mykey2 = "AIzaSyDtK77ffpHmX74uUppSWvTp5ZTdCH31d1c";
        String url_reviews = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=" + mykey2;
        RequestQueue queue = Volley.newRequestQueue(this);
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url_reviews, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //googleReviews = response;
                detail_reviews.dealWithResponseData(response);
                //showMessage(googleReviews);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showMessage("Error in Response!");
            }
        });
        queue.add(stringRequest);
    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                //showMessage("ERROR!");
                return;
            }
            //ImageView mImageView = (ImageView)findViewById(R.id.photos_list);
            //mImageView.setImageBitmap(placePhotoResult.getBitmap());
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("photo", placePhotoResult.getBitmap());
            listphotos.add(showitem);

            photoIndex++;
            //showMessage("photo : " + photoIndex);
            if (photoIndex == totalphotoIndex) {
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listphotos,
                        R.layout.photo_item, new String[]{"photo"}, new int[]{R.id.photo});
                //showMessage("adapter : " + photoIndex);
                ListView mListView = findViewById(R.id.photos_list);
                mListView.setAdapter(adapter);
                adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view.getId() == R.id.photo) {
                            ((ImageView) view).setImageBitmap((Bitmap) data);
                            return true;
                        }
                        //Otherwise do default stuff
                        return false;
                    }
                });

            }
        }
    };


    private void initViews() {
        placeName = receivePlaceString.split(splitBuffer)[1];
        placeID   = receivePlaceString.split(splitBuffer)[0];
        placeLat  = receivePlaceString.split(splitBuffer)[2];
        placeLng  = receivePlaceString.split(splitBuffer)[3];
        placeIcon = receivePlaceString.split(splitBuffer)[4];
//        showMessage("placeIcon = " + placeIcon);
//        getSupportActionBar().setIcon(R.drawable.share);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setCustomView(getMainTitleTabView(placeName,placeID));
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        tabLayout     = (TabLayout) findViewById(R.id.tablayout_detail);
        tab_viewpager = (ViewPager) findViewById(R.id.tab_viewpager_detail);

//        tabList.add("INFO");
//        tabList.add("PHOTOS");
//        tabList.add("MAP");
//        tabList.add("REVRRRRIEWS");
        detail_info    = new Detail_INFO();
        detail_photos  = new Detail_PHOTOS();
        detail_map     = new Detail_MAP(placeName,placeLat,placeLng);
        detail_reviews = new Detail_REVIEWS();

        mFragments = new ArrayList<Fragment>();
        iconList   = new ArrayList<>();
        tabList    = new ArrayList<>();

        tabList.add("INFO");
        tabList.add("PHOTOS");
        tabList.add("MAP");
        tabList.add("REVIEWS");

        mFragments.add(detail_info);
        mFragments.add(detail_photos);
        mFragments.add(detail_map);
        mFragments.add(detail_reviews);

//        tab_viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), null, mFragments));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(tab_icon("INFO",R.mipmap.ic_launcher)));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(tab_icon("PHOTOS",R.mipmap.ic_launcher)));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(tab_icon("MAP",R.mipmap.ic_launcher)));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(tab_icon("REVIEWS",R.mipmap.ic_launcher)));

        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(3)));
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //tab_viewpager.setCurrentItem(2);
        iconList.add(R.drawable.info_outline);
        iconList.add(R.drawable.photos);
        iconList.add(R.drawable.maps);
        iconList.add(R.drawable.reviews);


        tab_viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), tabList, mFragments, iconList));
        //tabLayout.setupWithViewPager(tab_viewpager);
        //tab_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tab_viewpager));
        tab_viewpager.setOffscreenPageLimit(4);

        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
        tabLayout.getTabAt(3).setCustomView(getTabView(3));
        //tab_viewpager.addOnPageChangeListener();
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(tabList.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(iconList.get(position));
        return view;
    }

    public View getMainTitleTabView(String placeName,String placeID) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab_2, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(placeName);
        ImageView img_title1 = (ImageView) view.findViewById(R.id.img_title1);
        img_title1.setImageResource(R.drawable.share);
        ImageView img_title2 = (ImageView) view.findViewById(R.id.img_title2);
        Favorite fav = new Favorite();
        if(fav.checkExistByIdString(placeID)) {
            img_title2.setImageResource(R.drawable.heart_white_nobroder);
        }else {
            img_title2.setImageResource(R.drawable.heart_outline_white);
        }
        return view;
    }

//    private View tab_icon(String name, int iconID) {
//        View newtab = LayoutInflater.from(this).inflate(R.layout.icon_view, null);
//        TextView tv = (TextView) newtab.findViewById(R.id.tabtext);
//        tv.setText(name);
//        ImageView im = (ImageView) newtab.findViewById(R.id.tabicon);
//        im.setImageResource(iconID);
//        return newtab;
//    }

    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this, txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showMessage("ConnectionResult onConnectionFailed!!!");
    }

    public void TWITTER(View view){
        //showMessage("twitter!");
        String twitterword = "Check out " + placeName + " located at "+ placeAddress + "\nWebsite: " + placeWebsiteUrl;
        String twitterurl = "https://twitter.com/intent/tweet?text=" + twitterword + "&hashtags=TravelAndEntertainmentSearch";
        Uri uri = Uri.parse(twitterurl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void addOrRemoveFromDetail(View view){
        Favorite fav = new Favorite();
        if(!fav.checkExistByIdString(placeID)){
            HashMap<String,Object> temp = new HashMap<>();
            temp.put("place_id",placeID);
            temp.put("icon",placeIcon);
            temp.put("name",placeName);
            temp.put("vicinity",placeAddress);
            temp.put("lat",placeLat);
            temp.put("lng",placeLng);
            temp.put("favorite",R.drawable.heart_red);
            fav.addToFav(temp);
            showMessage( placeName + " was added to favorites");
            getSupportActionBar().setCustomView(getMainTitleTabView(placeName,placeID));
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }else{
            fav.deleteFromFavById(placeID);
            showMessage( placeName + " was removed from favorites");
            getSupportActionBar().setCustomView(getMainTitleTabView(placeName,placeID));
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

//    private void placeINFO(String placeId) {
//        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlaceBuffer>() {
//            @Override
//            public void onResult(PlaceBuffer places) {
//                if (places.getStatus().isSuccess() && places.getCount() > 0) {
//                    final Place myPlace = places.get(0);
//                    //Log.i(TAG, "Place found: " + myPlace.getName());
//                } else {
//                    //Log.e(TAG, "Place not found");
//                }
//                places.release();
//            }
//        });
//    }
}
