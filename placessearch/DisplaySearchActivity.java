package com.example.wangboyuan.placessearch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaySearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private String next_page_token = "";
    private String responseContent;
    private JSONObject obj = null;
    private int pageIndex = -1;
    private String[] allData = new String[]{"","",""};
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static String thiscontent = "";
    public static int thisindex = -1;

    private float mouseAtX = 0;
    private float mouseAtY = 0;
    private Favorite fav = new Favorite();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search);

//        Get the Intent that started this activity and extract the string

        Intent intent = getIntent();
        responseContent = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        if(thisindex != -1){
            pageIndex = thisindex;
        }
        //showMessage(responseContent);
        if(responseContent == null || responseContent.length() == 0){
            responseContent = thiscontent;
            pageIndex = thisindex;
            //return from the details page
        }else{
            pageIndex = 0;
        }
        dealWithResponseData(responseContent);
    }

    private void dealWithResponseData(String response) {
        obj = null;
        allData[pageIndex] = response;
        thiscontent = response;
        thisindex = pageIndex;
        //showMessage("pageNo : " + pageIndex);
        try {
            obj = new JSONObject(response);

            Button btn1 = (Button) findViewById(R.id.previous);
            if(pageIndex == 0) btn1.setEnabled(false);
            else btn1.setEnabled(true);

            Button btn2 = (Button) findViewById(R.id.next);
            if(obj.has ("next_page_token")) {
                btn2.setEnabled(true);
                next_page_token = obj.getString("next_page_token");
                //showMessage(next_page_token);
            }else {
                btn2.setEnabled(false);
                next_page_token = "";
            }
            if(pageIndex == 2)btn2.setEnabled(false);
            JSONArray results = obj.getJSONArray("results");

            List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            //ImageLoader mImageLoader = new ImageLoader(mQueue, new BitmapCache());

            for(int i = 0;i < results.length();i++){
                final Map<String, Object> showitem = new HashMap<String, Object>();
                JSONObject detail = results.getJSONObject(i);
                showitem.put("place_id", detail.getString("place_id"));
                JSONObject geometry = detail.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                showitem.put("lat", lat);
                showitem.put("lng", lng);

                showitem.put("icon",detail.getString("icon"));
//                showMessage(detail.getString("icon") + Integer.toString(i));
                if(!fav.checkExistByIdString(detail.getString("place_id"))){
                    showitem.put("favorite",R.drawable.heart_white);
                }else {
                    showitem.put("favorite", R.drawable.heart_red);
                }

//                String url = detail.getString("icon");
//                showitem.put("icon",detail.getString("icon"));
//                final int v = i;
//                ImageView mImageView = findViewById(R.id.icon);
//                ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        showitem.put("icon",bitmap);
//                        ImageView mImageView = findViewById(R.id.icon);
//                        //mImageView.setImageBitmap(bitmap);
//                        //showMessage("BITMAP OF " + v + " : " + bitmap);
//                    }
//                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //showMessage("ERROR IN MAP : " + v);
//                    }
//                });
//                requestQueue.add(imageRequest);
                showitem.put("name", detail.getString("name"));
                showitem.put("vicinity", detail.getString("vicinity"));
                listitem.add(showitem);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, listitem,
                    R.layout.result_item, new String[] { "icon", "name", "vicinity", "favorite" }, new int[] {  R.id.icon, R.id.text1, R.id.text2 ,R.id.favorite });
//            SimpleAdapter adapter = new SimpleAdapter(this, listitem,
//                    R.layout.result_item, new String[] { "name", "vicinity" }, new int[] {  R.id.text1, R.id.text2 });
            ListView mListView = findViewById(R.id.list_test);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(this);

            View.OnTouchListener gestureListener = new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    mouseAtX = event.getRawX();
                    mouseAtY =  event.getRawY();
                    //showMessage(event.getRawX() +","+ event.getRawY());
                    return false;
                }
            };
            mListView.setOnTouchListener(gestureListener);

            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view.getId() == R.id.icon) {
                        //((ImageView)view).setImageBitmap((Bitmap)data);
                        ImageView imageView = (ImageView)view;
                        Picasso.with(getBaseContext()).load(data.toString()).into(imageView);
                        return true;
                    }
                    //Otherwise do default stuff
                    return false;
                }
            });

        } catch (JSONException e) {
            //showMessage("ERROR IN PARSING THE SEARCHING RESULTS!");
        }
    }

    private void showMessage(String txt){
        Toast toast = Toast.makeText(this, txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.show();
    }

    private void showMessage(long c){
        Toast toast = Toast.makeText(this, Long.toString(c), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.show();
    }


    public void goToNextPage(View view) {
        final ProgressDialog progressdialog = new ProgressDialog(DisplaySearchActivity.this);
        progressdialog.setMessage("Fetching next page...");
        progressdialog.show();
        pageIndex++;
        if(allData[pageIndex].length() == 0) {
            String url = "http://wangby1.us-west-1.elasticbeanstalk.com/?placeNextPageToken=" + next_page_token;
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseContent = response;
                    progressdialog.dismiss();
                    dealWithResponseData(responseContent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressdialog.dismiss();
                    showMessage("Error in Response!");
                }
            });
            queue.add(stringRequest);
        }else{
            progressdialog.dismiss();
            responseContent = allData[pageIndex];
            dealWithResponseData(responseContent);
        }
        //progressdialog.dismiss();

    }

    public void goToPreviousPage(View view) {
        pageIndex--;
        responseContent = allData[pageIndex];
        dealWithResponseData(responseContent);
    }

    public void goToDetail(DisplaySearchActivity view, String place) {
        Intent intent = new Intent(this, Details.class);
        intent.putExtra(EXTRA_MESSAGE,place);
        startActivity(intent);
    }

    public void addOrDeleteFromFavorite(HashMap<String,Object> map){
        //showMessage(view.getNextFocusDownId() + "," + view.getId());

//        showMessage( map.get("name").toString() + " was added to favorites");
//        showMessage(map.get("favorite"));
        //showMessage(fav.getSizeFAV());
        if(!fav.checkExist(map)) {
            map.put("favorite",R.drawable.heart_red);
            fav.addToFav(map);
            showMessage( map.get("name").toString() + " was added to favorites");
        } else {
            map.put("favorite",R.drawable.heart_white);
            fav.deleteFromFav(map);
            showMessage( map.get("name").toString() + " was removed from favorites");
        }
        ListView mListView = findViewById(R.id.list_test);
        mListView.invalidateViews();///!!!
        //HashMap<String,String> map = (HashMap<String,String>)parent.getItemAtPosition(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //showMessage(parent + "," + view + "," + position + "," + id);
        HashMap<String,Object> map = (HashMap<String,Object>)parent.getItemAtPosition(position);
        String splitBuffer = "////";
        String placeString = map.get("place_id") + splitBuffer + map.get("name") + splitBuffer + map.get("lat") + splitBuffer + map.get("lng") + splitBuffer + map.get("icon");
        //showMessage(mouseAtX + ",," + mouseAtY);
        if(mouseAtX >= view.getWidth() - findViewById(R.id.favorite_layout).getWidth()){
            //showMessage("fav" + mouseAtX + "," +  mouseAtY);
            addOrDeleteFromFavorite(map);
        } else {
            //showMessage("det" + mouseAtX + "," +  mouseAtY);
            //showMessage(map.get("lat") + "," +  map.get("lng"));
            goToDetail(this,placeString);
        }
        //int[] viewLocation = new int[2];
        //view.getLocationOnScreen(viewLocation);
        //showMessage(viewLocation[0] + "," + viewLocation[1]);
        //showMessage(view.getScrollX() + "," + view.getScrollY());
        //goToDetail(this,placeString);
    }

//    @Override
//    public boolean onTouchEvent(android.view.MotionEvent event) {
//        float x = event.getRawX();
//        float y = event.getRawY();
//        showMessage(x + " = x,y = " + y);
//        return super.onTouchEvent(event);
//    }
}
