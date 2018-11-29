package com.geek.soft.illuwa.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

public class SharedReferenceAPI {
    public final String Guide = "illuwaGuide";
    public final String Guidekey = "Setting";

    public final String Option = "illuwaOption";
    public final String opHelpkey = "Help";
    public final String opTraffickey = "Traffic";
    public final String opByciclekey = "Bycicle";
    public final String opGuidekey = "Guide";
    // 값 불러오기
    public int getIntPreferences(Context context,String name, String key, int value){
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        int result = pref.getInt(key,value);
        return result;
    }

    // 값 저장하기
    public void saveIntPreferences(Context context, String name, String key, int value){
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    public void removeIntPreferences(Context context, String name, String key){
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    public void removeAllPreferences(Context context,String name){
        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
