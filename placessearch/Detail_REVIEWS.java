package com.example.wangboyuan.placessearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detail_REVIEWS extends Fragment implements AdapterView.OnItemClickListener {
    private View rootview;
    private String placeInformation;
    private JSONObject infoObj = null;
    public static String showReviews;
    private String googleReviewsString = "";
    private String yelpReviewsString = "";
    private boolean firstListener1 = true;
    private boolean firstListener2 = true;

    private int pointer1;
    private int pointer2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.detail_reviews,container,false);
        //showMessage("Detail_REVIEWS!");
        //super.onCreate(savedInstanceState);
        //createReviews(placeID);
        pointer1 = 0;
        pointer2 = 0;

        AdapterView.OnItemSelectedListener selectedListener1 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(firstListener1) firstListener1 = false;
                else {
                    pointer1 = position;
                    sortReviews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        AdapterView.OnItemSelectedListener selectedListener2 = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(firstListener2) firstListener2 = false;
                else {
                    pointer2 = position;
                    sortReviews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };

        Spinner sp1 = rootview.findViewById(R.id.review_type);
        sp1.setOnItemSelectedListener(selectedListener1);
        Spinner sp2 = rootview.findViewById(R.id.review_order);
        sp2.setOnItemSelectedListener(selectedListener2);
        return rootview;
    }

    public void sortReviews(){
        //showMessage(Integer.toString(pointer1) + "," + Integer.toString(pointer2));
        if(pointer1 == 0)showReviews = googleReviewsString;
        else showReviews = yelpReviewsString;
        //showMessage("showReviews = " + showReviews);
        TextView textView = rootview.findViewById(R.id.NoRecordsInReviews);
        ListView listView = rootview.findViewById(R.id.review_list);
        if(showReviews.length() == 0) {
            //showMessage("showReviews.length() == 0");
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            return;
        }
        try {
            JSONArray obj = new JSONArray(showReviews);
            if(pointer2 == 1){
                for(int i = 0;i < obj.length();i++) {
                    int index = i;
                    for (int j = i + 1; j < obj.length(); j++) {
                        if (Integer.parseInt(obj.getJSONObject(j).getString("rating")) > Integer.parseInt(obj.getJSONObject(i).getString("rating")))
                            index = j;
                    }
                    JSONObject temp = obj.getJSONObject(index); //swap the order...
                    obj.put(index, obj.get(i));
                    obj.put(i, temp);
                }
            }else if(pointer2 == 2){
                for(int i = 0;i < obj.length();i++) {
                    int index = i;
                    for (int j = i + 1; j < obj.length(); j++) {
                        if (Integer.parseInt(obj.getJSONObject(j).getString("rating")) < Integer.parseInt(obj.getJSONObject(i).getString("rating")))
                            index = j;
                    }
                    JSONObject temp = obj.getJSONObject(index);
                    obj.put(index, obj.get(i));
                    obj.put(i, temp);
                }
            }else if(pointer2 == 3){
                if(pointer1 == 0){
                    for(int i = 0;i < obj.length();i++) {
                        int index = i;
                        //showMessage(obj.getJSONObject(i).getString("oritime"));
                        for (int j = i + 1; j < obj.length(); j++) {
                            if (Long.parseLong(obj.getJSONObject(j).getString("time")) > Long.parseLong(obj.getJSONObject(i).getString("time")))
                                index = j;
                        }
                        JSONObject temp = obj.getJSONObject(index);
                        obj.put(index, obj.get(i));
                        obj.put(i, temp);
                    }
                }else if(pointer1 == 1) {
                    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for(int i = 0;i < obj.length();i++) {
                        int index = i;
                        //showMessage(obj.getJSONObject(i).getString("oritime"));
                        Long time_i = format2.parse(obj.getJSONObject(i).getString("time_created")).getTime();
                        for (int j = i + 1; j < obj.length(); j++) {
                            Long time_j = format2.parse(obj.getJSONObject(j).getString("time_created")).getTime();
                            if (time_j > time_i)
                                index = j;
                        }
                        JSONObject temp = obj.getJSONObject(index);
                        obj.put(index, obj.get(i));
                        obj.put(i, temp);
                    }
                }

            }else if(pointer2 == 4){
                if(pointer1 == 0){
                    for(int i = 0;i < obj.length();i++) {
                        int index = i;
                        //showMessage(obj.getJSONObject(i).getString("oritime"));
                        for (int j = i + 1; j < obj.length(); j++) {
                            if (Long.parseLong(obj.getJSONObject(j).getString("time")) < Long.parseLong(obj.getJSONObject(i).getString("time")))
                                index = j;
                        }
                        JSONObject temp = obj.getJSONObject(index);
                        obj.put(index, obj.get(i));
                        obj.put(i, temp);
                    }
                }else if(pointer1 == 1) {
                    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for(int i = 0;i < obj.length();i++) {
                        int index = i;
                        //showMessage(obj.getJSONObject(i).getString("oritime"));
                        Long time_i = format2.parse(obj.getJSONObject(i).getString("time_created")).getTime();
                        for (int j = i + 1; j < obj.length(); j++) {
                            Long time_j = format2.parse(obj.getJSONObject(j).getString("time_created")).getTime();
                            if (time_j < time_i)
                                index = j;
                        }
                        JSONObject temp = obj.getJSONObject(index);
                        obj.put(index, obj.get(i));
                        obj.put(i, temp);
                    }
                }
            }
            showReviews = obj.toString();
            if(pointer1 == 0)createGoogleReviews(showReviews);
            else createYelpReviews(showReviews);
        }catch (Exception E){
            //showMessage("some error in sorting!");
        }
    }

//    public void setPlaceID(String placeID) {
//        this.placeID = placeID;
//        createReviews(placeID);
//    }

//    public void createReviews(String placeID){
//        if(placeID == null || placeID == "")return;
//        String mykey2 = "AIzaSyDtK77ffpHmX74uUppSWvTp5ZTdCH31d1c";
//        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=" + mykey2;
//        RequestQueue queue = Volley.newRequestQueue(this.getContext());
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                googleReviews = response;
//                //dealWithResponseData();
//                showMessage("yes!!lll");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                showMessage("Error in Response!");
//            }
//        });
//        queue.add(stringRequest);
//    }

    public void dealWithResponseData(String gReviews){
        this.placeInformation = gReviews;
        try {
            infoObj = new JSONObject(placeInformation);
            JSONObject results = (JSONObject) infoObj.get("result");
            JSONArray address_components = results.getJSONArray("address_components");
            JSONArray googleReviews      = results.getJSONArray("reviews");
            googleReviewsString =  googleReviews.toString();
            String searchYelpName        = results.getString("name");
            getYelpReviews(searchYelpName,address_components.toString());
            createGoogleReviews(googleReviews.toString());

        }catch (Exception e){
            //showMessage("ERROR IN dealWithResponseData REVIEWS!");
            googleReviewsString = "";
            yelpReviewsString = "";
        }
        //showMessage(googleReviews);
    }

    public void getYelpReviews(String searchYelpName,String address_components) throws Exception {
        //showMessage(searchYelpName + ", " + address_components);

        String searchYelpCity = "Los Angeles";
        String searchYelpState = "CA";
        String searchYelpCountry = "US";
        String searchYelpAddress1 = "";
        //String c = "";
        try {
            JSONArray Obj = new JSONArray(address_components);
            //showMessage("address_components = " + Obj.toString());
            for (int i = 0; i < Obj.length(); i++) {
                String type = Obj.get(i).toString();
                JSONObject typeobject = new JSONObject(type);
                JSONArray temp = (JSONArray)typeobject.get("types");
                //showMessage("temp = " + temp.toString());
                //c += temp.get(0) + ",";
                if(temp.get(0).equals("administrative_area_level_1") && typeobject.has("short_name")){
                    searchYelpState = typeobject.get("short_name").toString();
                    //showMessage(searchYelpState);
                }else if(temp.get(0).equals("locality") && typeobject.has("short_name")){
                    searchYelpCity = typeobject.get("short_name").toString();
                    //showMessage(searchYelpCity);
                }else if(temp.get(0).equals("street_address") && typeobject.has("short_name")){
                    searchYelpAddress1 = typeobject.get("short_name").toString();
                    //showMessage(searchYelpAddress1);
                }
            }
            String YelpURL = "http://hw8wangby2-env.us-west-1.elasticbeanstalk.com/user?";;
            YelpURL += "searchYelpName=" + searchYelpName;
            YelpURL += "&searchYelpCity=" + searchYelpCity;
            YelpURL += "&searchYelpState=" + searchYelpState;
            YelpURL += "&searchYelpCountry=" + searchYelpCountry;
            if(searchYelpAddress1.length() != 0 )
                YelpURL += "&searchYelpAddress1=" + searchYelpAddress1;
            RequestQueue queue = Volley.newRequestQueue(this.getContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, YelpURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //showMessage("yelpreviews = " + response);
                    yelpReviewsString = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //showMessage("Error in getYelpReviews1!");
                    yelpReviewsString = "";
                }
            });
            queue.add(stringRequest);

//            alert("YelpURL = " + YelpURL);
//            showMessage(c);
//            showMessage(searchYelpCity + '\n' + searchYelpState + '\n' + searchYelpAddress1 + '\n' + searchYelpName);
//            JSONObject results = (JSONObject) infoObj.get("result");
//            JSONArray address_components = results.getJSONArray("address_components");
//            JSONArray googleReviews      = results.getJSONArray("reviews");
//            String searchYelpName        = results.getString("name");
        }catch (Exception E){
            //showMessage("Error in getYelpReviews2!");
        }

    }

    public void createYelpReviews(String reviews) throws Exception {
        TextView textView = rootview.findViewById(R.id.NoRecordsInReviews);
        ListView listView = rootview.findViewById(R.id.review_list);

        if(yelpReviewsString.length() == 0){
            //showMessage("yelpReviewsString.length() == 0");
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            return;
        }
        textView.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);

        showReviews = reviews;
        try {
            JSONArray obj = new JSONArray(reviews);
            List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
            for(int i = 0;i < obj.length();i++) {
                final Map<String, Object> showitem = new HashMap<String, Object>();
                JSONObject detail = obj.getJSONObject(i);

                JSONObject user = detail.getJSONObject("user");
                showitem.put("profile_photo_url",user.get("image_url"));
                showitem.put("author_name",      user.getString("name"));
                showitem.put("url",   detail.getString("url"));
                showitem.put("rating",detail.getString("rating"));
                showitem.put("text",  detail.getString("text"));
                //showMessage(detail.getString("text"));
                showitem.put("time",  detail.getString("time_created"));
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                showitem.put("oritime",format2.parse(detail.getString("time_created")).getTime());
                listitem.add(showitem);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitem,
                    R.layout.review_item, new String[] { "author_name" , "text", "time" ,"rating","profile_photo_url" },
                    new int[] {  R.id.review_name, R.id.review_text, R.id.review_time, R.id.review_ratingBar, R.id.review_icon });

            ListView mListView = rootview.findViewById(R.id.review_list);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(this); //implements AdapterView.OnItemClickListener
            //deal with the ratingBar in reviews.
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view.getId() == R.id.review_ratingBar) {
                        RatingBar ratingBar = (RatingBar) view;
                        ratingBar.setRating(Float.parseFloat(data.toString()));
                        return true;
                    }
                    if (view.getId() == R.id.review_icon){
                        ImageView imageView = (ImageView)view;
                        //Bitmap bitmap = BitmapFactory.decodeFile(data.toString());
                        //showMessage(data.toString());
                        Picasso.with(getActivity()).load(data.toString()).into(imageView);
                        //Picasso.with(getContext()).load(data.toString()).into(imageView);
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            showMessage("Error in Showing Yelp Reviews!");
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
    }

    public void createGoogleReviews(String reviews) throws Exception {
        TextView textView = rootview.findViewById(R.id.NoRecordsInReviews);
        ListView listView = rootview.findViewById(R.id.review_list);

        textView.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        showReviews = reviews;
        try {
            //showMessage("THIS STEP!");
            JSONArray obj = new JSONArray(reviews);
            //showMessage(obj.length());
            List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
            for(int i = 0;i < obj.length();i++) {
                final Map<String, Object> showitem = new HashMap<String, Object>();
                JSONObject detail = obj.getJSONObject(i);
                showitem.put("author_name",detail.getString("author_name"));
                showitem.put("rating",detail.getString("rating"));
                showitem.put("text",detail.getString("text"));
                showitem.put("url",   detail.getString("author_url"));

                //deal with profile_photo_url:
                String profile_photo_url = detail.getString("profile_photo_url");
                showitem.put("profile_photo_url",profile_photo_url);

                //deal with time:
                Long temptime = Long.parseLong(detail.get("time").toString()) * 1000;
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(temptime);
                showitem.put("time",format2.format(date));
                showitem.put("oritime",detail.get("time"));

                //String s = format2.format(date);
                //String timeString = format2.format(new Date((long)detail.getDouble("time")*1000));
                //showMessage(s);
                //showMessage("date = " + dateFormat.format(date));
                //showitem.put("time","2013-10-20 19:09:23");
                //showitem.put("time",format2.format(date));
                listitem.add(showitem);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitem,
                    R.layout.review_item, new String[] { "author_name" , "text", "time" ,"rating","profile_photo_url" },
                    new int[] {  R.id.review_name, R.id.review_text, R.id.review_time, R.id.review_ratingBar, R.id.review_icon });

            ListView mListView = rootview.findViewById(R.id.review_list);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(this); //implements AdapterView.OnItemClickListener
            //deal with the ratingBar in reviews.
            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view.getId() == R.id.review_ratingBar) {
                        RatingBar ratingBar = (RatingBar) view;
                        ratingBar.setRating(Float.parseFloat(data.toString()));
                        return true;
                    }
                    if (view.getId() == R.id.review_icon){
                        ImageView imageView = (ImageView)view;
                        //Bitmap bitmap = BitmapFactory.decodeFile(data.toString());
                        //showMessage(data.toString());
                        Picasso.with(getActivity()).load(data.toString()).into(imageView);
                        //Picasso.with(getContext()).load(data.toString()).into(imageView);
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            showMessage("Error in Showing Google Reviews!");
        }
    }

    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 310);
        toast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map = (HashMap<String,String>)parent.getItemAtPosition(position);
//        showMessage(map.get("url"));
        Uri uri = Uri.parse(map.get("url"));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}
