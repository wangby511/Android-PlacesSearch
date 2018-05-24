package com.example.wangboyuan.placessearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Favorite {
    public static ArrayList<Map<String, Object>> FAV = new ArrayList<>();

    public String getSizeFAV() {
        return Integer.toString(FAV.size());
    }

    public int getSizeIntFAV() { return FAV.size(); }

    public ArrayList<Map<String, Object>> getFAV(){
        return FAV;
    }

    public boolean checkExist(Map<String, Object> curr){
        for (int i = 0;i < FAV.size();i++){
            Map<String,Object> temp = FAV.get(i);
            if(temp.get("place_id").equals(curr.get("place_id")))return true;
        }
        return false;
    }

    public boolean checkExistByIdString(String currIdString){
        for (int i = 0;i < FAV.size();i++){
            Map<String,Object> temp = FAV.get(i);
            if(temp.get("place_id").equals(currIdString))return true;
        }
        return false;
    }

    public void addToFav (Map<String, Object> curr){
        FAV.add(curr);
    }

    public boolean deleteFromFav (Map<String, Object> curr){
        for (int i = 0;i < FAV.size();i++){
            Map<String,Object> temp = FAV.get(i);
            if(temp.get("place_id").equals(curr.get("place_id"))){
                FAV.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean deleteFromFavById (String currIdString){
        for (int i = 0;i < FAV.size();i++){
            Map<String,Object> temp = FAV.get(i);
            if(temp.get("place_id").equals(currIdString)){
                FAV.remove(i);
                return true;
            }
        }
        return false;
    }
}
