package com.geek.soft.illuwa;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends FragmentActivity{

    private Fragment1 fragment1;
    private long first_time;
    private long second_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //액티비티 화면 세로고정
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);   //액티비티 화면 잠금

        setContentView(R.layout.activity_main);

        SharedReferenceAPI sharedReferenceAPI = new SharedReferenceAPI();

        //값 불러오기
        int result = sharedReferenceAPI.getIntPreferences(MainActivity.this,sharedReferenceAPI.Guide,sharedReferenceAPI.Guidekey,0);

        if(result==0){
            sharedReferenceAPI.saveIntPreferences(MainActivity.this,sharedReferenceAPI.Guide,sharedReferenceAPI.Guidekey,1);
            Intent intent=new Intent(MainActivity.this, ActivityGuide.class);
            startActivity(intent);
            fragment1 = new Fragment1();
            Bundle bundle = new Bundle(1); // bundle() 인수는 전달할 데이터 개수
            fragment1.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHere, fragment1);
            fragmentTransaction.commit();
        }
        else{
            fragment1 = new Fragment1();
            Bundle bundle = new Bundle(1); // bundle() 인수는 전달할 데이터 개수
            fragment1.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHere, fragment1);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    public void returnFirst(View view){
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        second_time = System.currentTimeMillis();
        Toast.makeText(MainActivity.this, "한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        if(second_time - first_time < 2000){

            super.onBackPressed();
            finishAffinity();
        }
        first_time = System.currentTimeMillis();
    }

}
