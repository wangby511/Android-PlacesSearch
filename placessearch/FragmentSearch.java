package com.example.wangboyuan.placessearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class FragmentSearch extends Fragment {
    private View rootview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_first,container,false);
        //showMessage("FragmentSearch!");
        AutoCompleteTextView searchPlace = rootview.findViewById(R.id.locationname);

        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this.getContext());
        searchPlace.setAdapter(adapter);
        //searchPlace.setOnItemClickListener(onItemClickListener);
        RadioGroup yourRadioGroup = (RadioGroup) rootview.findViewById(R.id.myRadioGroup);
        yourRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                AutoCompleteTextView textViewLocatioNname = rootview.findViewById(R.id.locationname);
                switch(checkedId)
                {
                    case R.id.myRadioButton1:
                        //showMessage("1!");
                        textViewLocatioNname.setEnabled(false);
                        break;
                    case R.id.myRadioButton2:
                        textViewLocatioNname.setEnabled(true);
                        //showMessage("2!");
                        break;
                }
            }
        });
        TextView err1 = (TextView) rootview.findViewById(R.id.errmessage1);
        err1.setVisibility(View.INVISIBLE);
        TextView err2 = (TextView) rootview.findViewById(R.id.errmessage2);
        err2.setVisibility(View.INVISIBLE);
        AutoCompleteTextView textViewLocatioNname1 = rootview.findViewById(R.id.locationname);
        textViewLocatioNname1.setEnabled(false);

        return rootview;
    }
//    private AdapterView.OnItemClickListener onItemClickListener =
//            new AdapterView.OnItemClickListener(){
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    Toast.makeText(AutoCompleteActivity.this, "selected place " + ((zoftino.com.places.Place)adapterView.getItemAtPosition(i)).getPlaceText()
//                            , Toast.LENGTH_SHORT).show();
//                    //do something with the selection
//                    //searchScreen();
//                }
//            };


    private void showMessage(String txt) {
        Toast toast = Toast.makeText(this.getContext(), txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 80);
        toast.show();
    }
}
