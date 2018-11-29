package com.geek.soft.illuwa.ui;


import android.app.ActivityManager;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.geek.soft.illuwa.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityGuide extends FragmentActivity {
    List<Integer> temp;
    LinearLayout SlidingPage;
    ImageView imageindex[];
    ViewPager pager;
    Adapter adapter;
    Bitmap guide;
    Drawable drawable_gudie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start__guide);

        temp = new ArrayList<>();
        temp.add(R.drawable.guide_0);
        temp.add(R.drawable.guide_1);
        temp.add(R.drawable.guide_2);
        temp.add(R.drawable.guide_3);
        temp.add(R.drawable.guide_4);
        temp.add(R.drawable.guide_5);
        temp.add(R.drawable.guide_6);
        temp.add(R.drawable.guide_7);
        temp.add(R.drawable.guide_8);
        temp.add(R.drawable.guide_9);


        adapter = new Adapter(temp,this);

        //안내를 위한 View pager와  adapter 가져오기
        pager = (ViewPager)findViewById(R.id.pager);
        SlidingPage = (LinearLayout)findViewById(R.id.slidingPage);
        pager.setAdapter(adapter);

        final int pageindex = adapter.getCount();
        imageindex = new ImageView[pageindex];

        //안내 가이드 순서 그림 초기화
        for(int i = 0; i < pageindex; i++){

            imageindex[i] = new ImageView(this);
            imageindex[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.other_page_dot));

            LinearLayout.LayoutParams dotarea = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            dotarea.setMargins(8, 0, 8, 0);

            SlidingPage.addView(imageindex[i], dotarea);

        }

        imageindex[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.current_page_dot));

        //안내문 스와이프 이벤트 처리
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //현재 보고 있는 페이지 표시
            @Override
            public void onPageSelected(int position) {

                for(int i=0; i<pageindex; i++){
                    imageindex[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.other_page_dot));
                }
                imageindex[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.current_page_dot));

                if((pageindex-1)==position){
                    finish();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //종료시 메모리 해제를 위한 메소드
    @Override
    protected void onDestroy() {
        super.onDestroy();
        temp=null;
        for(int i=0; i<adapter.getCount(); i++){
            imageindex[i].setImageDrawable(null);
        }
        imageindex=null;
        pager=null;
        adapter=null;
        SlidingPage = null;
        guide.recycle();
        drawable_gudie=null;

        try {
            finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    //안내문 adapter
    class Adapter extends PagerAdapter {

        Context context;
        List<Integer> obj;
        Adapter(List<Integer> res, Context context){
            obj = res;
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int memary_size = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            Log.i("memary","value : "+memary_size);

            if(memary_size >= 256){
                View view = null;
                ImageView imageView = null;

                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.pager_adapter,container,false);
                imageView = (ImageView)view.findViewById(R.id.imageView);

                guide = BitmapFactory.decodeResource(getResources(),temp.get(position));
                drawable_gudie = new BitmapDrawable(guide);

                imageView.setImageDrawable(drawable_gudie);
                container.addView(view);

                return view;
            }
            else {
                View view = null;
                ImageView imageView = null;

                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.pager_adapter,container,false);
                imageView = (ImageView)view.findViewById(R.id.imageView);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                guide = BitmapFactory.decodeResource(getResources(),temp.get(position),options);
                drawable_gudie = new BitmapDrawable(guide);


                imageView.setImageDrawable(drawable_gudie);
                container.addView(view);

                return view;
            }

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }


        public int getCount() {
            return obj.size();
        }
    }

}
