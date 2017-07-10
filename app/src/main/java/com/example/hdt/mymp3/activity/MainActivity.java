package com.example.hdt.mymp3.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.hdt.mymp3.adapter.FragmentAdapter;
import com.example.hdt.mymp3.R;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControl();
    }

    private void addControl() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //Chia đều chiều rộng TabLayout cho mỗi tab
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //Đặt màu cho thanh trượt dưới TabLayout
        //tabLayout.setSelectedTabIndicatorColor(Color.GREEN);

        //noinspection ConstantConditions
        tabLayout.getTabAt(0).setCustomView(R.layout.tablayout_offline);
        //noinspection ConstantConditions
        tabLayout.getTabAt(1).setCustomView(R.layout.tablayout_online);
        //noinspection ConstantConditions
        tabLayout.getTabAt(2).setCustomView(R.layout.tablayout_favorite);

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        circlePageIndicator.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }
}