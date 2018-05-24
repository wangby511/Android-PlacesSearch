package com.example.wangboyuan.placessearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.support.v4.view.ViewPager;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity implements LocationListener, RadioGroup.OnCheckedChangeListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private TabLayout tabLayout;
    private ViewPager tab_viewpager;
//    private Fragment[] mFragmentArrays = new Fragment[2];
//    private String[] mTabTitles = new String[2];

    private MyPagerAdapter mAdapter;

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ArrayList<String> tabList = new ArrayList<String>();
    private ArrayList<Integer> iconList = new ArrayList<>();

    private FragmentSearch fragmentSearch;
    private FragmentFavorites fragmentFavorites;

    public static RadioGroup up_menu = null;


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String ERROR_MESSAGE = "Please fix all fields with errors";

    private double latitude;
    private double longitude;

    private String MY_SERVER_URL = "http://hw8wangby2-env.us-west-1.elasticbeanstalk.com/user?";
    private String finalcontent;
//    private ActivityTopLayoutBinding binding;

//    private TextView mTextMessage;
//    private TabLayout mTabLayout;
//    private ViewPager mViewPager;
//    //public MPagerAdapter mPagerAdapter;
//    private MyFragmentPagerAdapter mFragmentPagerAdapter;
//
//    private TabLayout.Tab mTab1;
//    private TabLayout.Tab mTab2;
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getLocation();
        initEvent();
        AutoCompleteTextView searchPlace = findViewById(R.id.locationname);

//        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(this);
//        searchPlace.setAdapter(adapter);
//        searchPlace.setOnItemClickListener(onItemClickListener);

//        mAdapter = new MyPagerAdapter(getSupportFragmentManager(),tabList, mFragments);
//        tab_viewpager.setAdapter(mAdapter);
//        findview();
//        initAdapter();
//        initViews();
//        getLocation();
//        RadioButton view = findViewById(R.id.up_menu);
//        view.setNavigationItemSelectedListener(this);
    }

//    private AdapterView.OnItemClickListener onItemClickListener =
//            new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    Toast.makeText(AutoCompleteActivity.this, "selected place " + ((zoftino.com.places.Place) adapterView.getItemAtPosition(i)).getPlaceText()
//                            , Toast.LENGTH_SHORT).show();
//                    //do something with the selection
//                    searchScreen();
//                }
//            };
//
//    public void searchScreen() {
//        Intent i = new Intent();
//        i.setClass(this, AutoCompleteActivity.class);
//        startActivity(i);
//    }


    private void initViews() {
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tab_viewpager = (ViewPager) findViewById(R.id.tab_viewpager);

        tabList = new ArrayList<String>();
        iconList = new ArrayList<>();
        iconList.add(R.drawable.search);
        iconList.add(R.drawable.heart_white_nobroder);
        tabList.add("SEARCH");
        tabList.add("️FAVORITE");
//        TabLayout.Tab tab1 = tabLayout.newTab().setText(tabList.get(0));
//        TabLayout.Tab tab2 = tabLayout.newTab().setText(tabList.get(1));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(tabList.get(1)));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        FragmentSearch fragmentSearch = new FragmentSearch();
        FragmentFavorites fragmentFavorites = new FragmentFavorites();
        mFragments.add(fragmentSearch);
        mFragments.add(fragmentFavorites);


        tab_viewpager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), tabList, mFragments, iconList));
        tabLayout.setupWithViewPager(tab_viewpager);

        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));

//        tabLayout.getTabAt(0).setCustomView(getTabView(0));
//        tabLayout.getTabAt(1).setCustomView(getTabView(1));
//        tab_viewpager.setCurrentItem(1);
//        tab_viewpager.setCurrentItem(0);
//        mAdapter = new MyPagerAdapter(getSupportFragmentManager(),tabList, mFragments);
//        tab_viewpager.setAdapter(mAdapter);
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            if (tab != null) {
//                tab.setCustomView(tab_viewpager.getTabView(i));
//                if (tab.getCustomView() != null) {
//                    View tabView = (View) tab.getCustomView().getParent();
//                    tabView.setTag(i);
//                    tabView.setOnClickListener(mTabOnClickListener);
//                }
//            }
//        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == 0)showMessage("0!");
        else showMessage("1");
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(tabList.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(iconList.get(position));
        return view;
    }

//    public void gotoTab(){
//        showMessage("gototab!");
//    }

    private void initEvent() {
        //showMessage("initEvent!");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab_viewpager.setCurrentItem(tab.getPosition());
                //showMessage("onTabSelected!" + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //showMessage("onTabUnselected!");

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //showMessage("onTabReselected!");

            }
        });
    }

//    private void changeTabSelect(TabLayout.Tab tab) {
//        View view = tab.getCustomView();
//        final int position = tab.getPosition();
//        if (position == 0) {
//            tab_viewpager.setCurrentItem(0);
//        } else if (position == 1) {
//            tab_viewpager.setCurrentItem(1);
//        }
//    }


    private void getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);

        GPSTracker gpsTracker = new GPSTracker(locationManager);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        //showMessage("latitude = " + latitude + " ,longitude = " + longitude);
    }

    private void showMessage(String txt){
        Toast toast = Toast.makeText(this, txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.show();
    }

//    private void initAdapter() {
//        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//
//        mAdapter = new FragmentPagerAdapter(fm) {
//
//            @Override
//            public int getCount() {
//                return mFragments.size();
//            }
//
//            @Override
//            public android.support.v4.app.Fragment getItem(int position) {
//                return mFragments.get(position);
//            }
//
//            @Override
//            public Object instantiateItem(ViewGroup arg0, int position) {
//                return super.instantiateItem(arg0, position);
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);
//            }
//        };
//    }


    public void mainSearchFunction(View view) {// Do something in response to button search;
        TextView err1 = (TextView) findViewById(R.id.errmessage1);
        err1.setVisibility(View.INVISIBLE);
        TextView err2 = (TextView) findViewById(R.id.errmessage2);
        err2.setVisibility(View.INVISIBLE);
        boolean avl = true;
        //1.keyword
        EditText keyword = (EditText) findViewById(R.id.keyword);
        String keywordString = keyword.getText().toString();
        if (keywordString.length() == 0){
            err1.setVisibility(View.VISIBLE);
            showMessage(ERROR_MESSAGE);
            avl = false;
            //return;
        }

        //2.category
        Spinner category = (Spinner) findViewById(R.id.category);
        String categoryString = category.getSelectedItem().toString().toLowerCase();

        //3.distance
        EditText radius = (EditText) findViewById(R.id.distance);
        String radiusString = radius.getText().toString();
        int radiusInt;
        if(radiusString.length() == 0){
            radiusInt = 10;
        }else {
            radiusInt = Integer.parseInt(radius.getText().toString());
        }

        //4.RadioGroup
        String locationString = "";
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.myRadioGroup);
        RadioButton radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
        String radioString = radioButton.getText().toString();
        if(radioString .equals("Current location")){
            radioString = "option1";
        }else{
            radioString = "option2";
            EditText location = (EditText) findViewById(R.id.locationname);
            locationString = location.getText().toString();
            if(locationString.length() == 0){
                err2.setVisibility(View.VISIBLE);
                showMessage(ERROR_MESSAGE);
                avl = false;
                return;
            }
        }
        //5.5
        if(!avl)return;

        //6.make url
        StringBuilder awsURL = new StringBuilder();
        awsURL.append(MY_SERVER_URL);
        try {
            awsURL.append("placeChosen=");
            awsURL.append(URLEncoder.encode(radioString, "utf-8"));
            awsURL.append("&placeKeyword=");
            awsURL.append(URLEncoder.encode(keywordString, "utf-8"));
            awsURL.append("&placeLocation=");
            awsURL.append(URLEncoder.encode(locationString, "utf-8"));
            awsURL.append("&placeCategory=");
            awsURL.append(URLEncoder.encode(categoryString, "utf-8"));
            awsURL.append("&placeDistance=");
            awsURL.append(URLEncoder.encode(Integer.toString(radiusInt), "utf-8"));
            awsURL.append("&placeLatitude=");
            awsURL.append(URLEncoder.encode(Double.toString(latitude), "utf-8"));
            awsURL.append("&placeLongitude=");
            awsURL.append(URLEncoder.encode(Double.toString(longitude), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //showMessage("awsURL = " + awsURL.toString());
        String url = awsURL.toString();

        //url = "http://fanzhang-env.us-east-2.elasticbeanstalk.com/searchinfo?keyword=book&Category=default&distance=10000&lat=34.0266&lon=-118.2831";
        //String url ="http://wangby1.us-west-1.elasticbeanstalk.com/?placeNextPageToken=CrQCJQEAANhNPwvA4DIFA9DJIMENHMy_talO1C61heF31D9J-d_-g8LW2hdhFIvrGovGorPPsVCnRTX-MJvoDnEO9jKC_HwuUYImsjC5AZvxVPT7TaGmdCF4yhvM8K1wjCU1KWgKwmieqEmRnqkN5cN2jgilBMAIQm12yPMFe87QmxsQzgh9AEIked1XHqQPN14ABdC2rwyMNhyZbWswxlWQoVSEtUD6K7w2nCPAp_aVTl1IPVa1YuFnFPehueA1FvYR8o2K_7fSrU-ItltUKy62dSHsjY8_gFguhjmzX1mmYzlCcbyKf---D_PVZIjt78BIlUhwETiam0uDwyBfvCLZMo_-fjIi8ktXNyrVkiTnSYCfFkfQovkFc-Btppzf6FDelf6gNisTGMjqga07YCOej1IvKuoSENhcdxJ_PPY9LJTLr94heIEaFMcZySflUx0c18e-19yn6XnffSLX";
        //VolleyGetUrlContent volley1 = new VolleyGetUrlContent(this,url);
        //showMessage(volley1.getFinalContent());
        final ProgressDialog progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setMessage("Fetching results...");
        progressdialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressdialog.dismiss();
                finalcontent = response;
                //showMessage(response);
                try{
                    DealWithFirstSearchResult(response);
                }catch (Exception e){

                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialog.dismiss();
                showMessage("Error in Response!");
            }
        });
        queue.add(stringRequest);

    }
    private void DealWithFirstSearchResult(String response) throws JSONException{
        try{
            JSONObject obj = new JSONObject(response);//getJSONObject();
            Intent intent = new Intent(this, DisplaySearchActivity.class);
            intent.putExtra(EXTRA_MESSAGE,response);
            startActivity(intent);
        }catch(Exception e){
            showMessage("ERROR IN PARSING THE RESULT!");
        }
    }

//    private JSONObject execute(String params) {
//        HttpURLConnection urlConnection = null;
//        BufferedReader buffer = null;
//
//        try {
//            // read responseURLEncoder.encode(para, "GBK");
//            URL url = new URL(params);
//            urlConnection = (HttpURLConnection)url.openConnection();
//            showMessage(params);
//            //showMessage(Integer.toString(HttpURLConnection.HTTP_OK));
//            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
//            {
//                showMessage("XXXNULL!!!");
//                return null;
//            }
//
//            /* optional request header */
//            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            //showMessage("searching2...");
//
//            /* optional request header */
//            urlConnection.setRequestProperty("Accept", "application/json");
//            //showMessage("searching3...");
//
//            /* for Get request */
//            urlConnection.setRequestMethod("GET");
//
//            //urlConnection.setAllowUserInteraction(false);
//            //urlConnection.setInstanceFollowRedirects(true);
//            if(urlConnection == null){
//                showMessage(" = null!");
//                return null;
//            }
//            //int statusCode = urlConnection.getResponseCode();
//            //BufferedReader buffer = null;
//            /* 200 represents HTTP OK */
//            //if (statusCode == 200) {
//            //inputStream = new BufferedInputStream(urlConnection.getInputStream());
//            showMessage("searching5...");
//            buffer = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
//            showMessage("searching6...");
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line;
//            while ((line = buffer.readLine()) != null)
//            {
//                stringBuffer.append(line);
//            }
//            showMessage("searching7...");
//            return new JSONObject(stringBuffer.toString());
//
//        } catch (Exception e) {
//            showMessage("error1..." + e.getMessage());
//            //e.printStackTrace();
//        } finally {
//            if (urlConnection != null) {
//                showMessage("urlConnection disconnect...");
//                urlConnection.disconnect();
//            }
//        }
//        return null;
//    }


    public void mainClearFunction(View view) {
        //showMessage(latitude + "," + longitude);
        //tab_viewpager.setCurrentItem(1);
        TextView err1 = (TextView) findViewById(R.id.errmessage1);
        err1.setVisibility(View.INVISIBLE);
        TextView err2 = (TextView) findViewById(R.id.errmessage2);
        err2.setVisibility(View.INVISIBLE);

        EditText keyword = (EditText) findViewById(R.id.keyword);
        keyword.setText("");
        Spinner category = (Spinner) findViewById(R.id.category);
        category.setSelection(0,true);
        EditText radius = (EditText) findViewById(R.id.distance);
        radius.setText("");
        RadioButton r = (RadioButton) findViewById(R.id.myRadioButton1);
        r.setChecked(true);
        EditText location = (EditText) findViewById(R.id.locationname);
        location.setText("");
        AutoCompleteTextView textViewLocatioNname1 = findViewById(R.id.locationname);
        textViewLocatioNname1.setEnabled(false);

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


//        myviewpager = (ViewPager)this.findViewById(R.id.viewPager);
//
//        btn_first = (Button)this.findViewById(R.id.FAGMENT1);
//        btn_second = (Button)this.findViewById(R.id.FAGMENT2);
//
//        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
//
//
//        mTextMessage = (TextView) findViewById(R.id.message);
//        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        //mPagerAdapter = new MPagerAdapter(getSupportFragmentManager());
//
//        mViewPager = (ViewPager) findViewById(R.id.viewpager);
//        mTabLayout = (TabLayout) findViewById(R.id.tab);
//
//        List<Fragment> fragments = new ArrayList<>();
//        fragments.add(new BlankFragment());
//        fragments.add(new BlankFragment());
//
//        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"Search", "Favorites"});
//        mViewPager.setAdapter(adapter);
//
//        mTabLayout.setupWithViewPager(mViewPager);
//
//
//        mTab1 = mTabLayout.getTabAt(0);
//        mTab2 = mTabLayout.getTabAt(1);
//
//    }
//    public void setTab(){
//
//        mTabLayout.setupWithViewPager(mViewPager);
//
//        mTabLayout.getTabAt(0).setText("tab1");
//        mTabLayout.getTabAt(1).setText("tab2");
//        mTabLayout.getTabAt(2).setText("tab3");
//        mTabLayout.getTabAt(3).setText("tab4");
//
