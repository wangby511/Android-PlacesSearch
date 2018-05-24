package com.example.wangboyuan.placessearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentFavorites extends Fragment implements AdapterView.OnItemClickListener {
    private View rootview;
    private float mouseAtX = 0;
    private float mouseAtY = 0;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private TextView textViewNoRecords;
    private ListView mListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_second,container,false);
        //showMessage("FragmentFavorites");
        Favorite fav = new Favorite();
        ArrayList<Map<String, Object>> list_favorite = new ArrayList<Map<String, Object>>();
        list_favorite = fav.getFAV();

        textViewNoRecords = rootview.findViewById(R.id.Norecords);
        mListView = rootview.findViewById(R.id.list_favorite);

        if(fav.getSizeIntFAV() == 0){
            textViewNoRecords.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }else {
            textViewNoRecords.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }

        SimpleAdapter adapter = new SimpleAdapter(this.getContext(), list_favorite,
                R.layout.result_item, new String[] { "icon", "name", "vicinity", "favorite" }, new int[] {  R.id.icon, R.id.text1, R.id.text2 ,R.id.favorite });

        mListView.setAdapter(adapter);
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view.getId() == R.id.icon) {
                    //((ImageView)view).setImageBitmap((Bitmap)data);
                    ImageView imageView = (ImageView)view;
                    Picasso.with(getActivity()).load(data.toString()).into(imageView);
                    return true;
                }
                //Otherwise do default stuff
                return false;
            }
        });
        mListView.invalidateViews();
//        showMessage("Welcome to this page fav!");

        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                 TODO Auto-generated method stub
                mouseAtX = event.getRawX();
                mouseAtY =  event.getRawY();
//                showMessage(event.getRawX() + "," + event.getRawY());
                return false;
            }
        };
        mListView.setOnTouchListener(gestureListener);

        mListView.setOnItemClickListener(this);

        return rootview;
    }
    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,Object> map = (HashMap<String,Object>)parent.getItemAtPosition(position);
        String splitBuffer = "////";
        String placeString = map.get("place_id") + splitBuffer + map.get("name") + splitBuffer + map.get("lat") + splitBuffer + map.get("lng") + splitBuffer + map.get("icon");;
        if(mouseAtX >= view.getWidth() - rootview.findViewById(R.id.favorite_layout).getWidth()){
            //showMessage("fav" + mouseAtX + "," +  mouseAtY);
            DeleteFromFavorite(map);
        } else {
            //showMessage("det" + mouseAtX + "," +  mouseAtY);
            //showMessage(map.get("lat") + "," +  map.get("lng"));
            goToDetail(placeString);
        }
    }

    public void DeleteFromFavorite(HashMap<String,Object> map){
        Favorite fav = new Favorite();
        map.put("favorite",R.drawable.heart_white);
        fav.deleteFromFav(map);
        showMessage( map.get("name").toString() + " was removed from favorites");
        ListView mListView = rootview.findViewById(R.id.list_favorite);
        mListView.invalidateViews();///!!!
        if(fav.getSizeIntFAV() == 0){
            textViewNoRecords.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }else {
            textViewNoRecords.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    public void goToDetail(String place) {
        Intent intent = new Intent(this.getContext(), Details.class);
        intent.putExtra(EXTRA_MESSAGE,place);
        startActivity(intent);
    }
}
