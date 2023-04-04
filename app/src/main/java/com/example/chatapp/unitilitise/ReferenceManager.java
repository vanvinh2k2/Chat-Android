package com.example.chatapp.unitilitise;

import android.content.Context;
import android.content.SharedPreferences;

public class ReferenceManager {

    private final SharedPreferences share;

    public ReferenceManager(Context context) {
        this.share = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    public void putBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key){
        return share.getBoolean(key,false);
    }

    public void putString(String key,String value){
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getString(String key){
        return share.getString(key,null);
    }

    public void clear(){
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.apply();
    }
}
