package com.example.wangboyuan.placessearch;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detail_PHOTOS extends Fragment {
    private View rootview;
    private List<Map<String, Object>> listphotos;
    private int photoIndex = 0;
    private int totalphotoIndex;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.detail_photos,null,false);
        //showMessage("Detail_PHOTOS!");
        TextView textView = rootview.findViewById(R.id.NoRecordsInPhotos);
        ListView listView = rootview.findViewById(R.id.photos_list);
        textView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        return rootview;
    }

    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 110);
        toast.show();
    }

    public void dealWithPhotos(PlacePhotoMetadataBuffer photoMetadataBuffer, final GoogleApiClient mGoogleApiClient,String placeID){
        //showMessage("PlacePhotoMetadataBuffer photoMetadataBuffer YES!");
        TextView textView = rootview.findViewById(R.id.NoRecordsInPhotos);
        ListView listView = rootview.findViewById(R.id.photos_list);
        textView.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeID).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(PlacePhotoMetadataResult photos) {
                if (!photos.getStatus().isSuccess()) {
                    return;
                }
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    photoIndex = 0;
                    totalphotoIndex = photoMetadataBuffer.getCount();

                    //showMessage("PlacePhotoMetadataBuffer!");
                    //detail_photos.dealWithPhotos(photoMetadataBuffer,mGoogleApiClient);
                    listphotos = new ArrayList<Map<String, Object>>();

                    //ImageView mImageView = getView().findViewById(R.id.photo_test);
                    //showMessage(Integer.toString(photoMetadataBuffer.getCount()));

                    for (int i = 0; i < totalphotoIndex; i++) {
                        int W = photoMetadataBuffer.get(i).getMaxWidth();
                        int H = photoMetadataBuffer.get(i).getMaxHeight();
                        photoMetadataBuffer.get(i).getScaledPhoto(mGoogleApiClient, W, H).setResultCallback(mDisplayPhotoResultCallback);
                    }

                } else {
                    showMessage("PHOTOS error!");
                }
                photoMetadataBuffer.release();
            }
        });
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
            //showitem.put("maxheight", placePhotoResult.getBitmap().getHeight());
            //showitem.put("maxwidth",  placePhotoResult.getBitmap().getWidth());
            listphotos.add(showitem);
            photoIndex++;
            //showMessage("photo : " + photoIndex);
            if (photoIndex == totalphotoIndex) {
                SimpleAdapter adapter = new SimpleAdapter(getContext().getApplicationContext(), listphotos,
                        R.layout.photo_item, new String[]{"photo"}, new int[]{R.id.photo});
                //showMessage("adapter : " + photoIndex);
                ListView mListView = getView().findViewById(R.id.photos_list);
                //if(mListView == null)showMessage("NULL!ERROR IN PHOTOS");
                mListView.setAdapter(adapter);
                adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if (view.getId() == R.id.photo) {
                            ((ImageView) view).setImageBitmap((Bitmap) data);
//                            ((ImageView) view).setMaxHeight(((Bitmap) data).getHeight());
//                            ((ImageView) view).setMaxWidth(((Bitmap) data).getWidth());
                            return true;
                        }
                        //Otherwise do default stuff
                        return false;
                    }
                });

            }
        }
    };
}
