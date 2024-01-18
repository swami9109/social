package com.example.samscials;

import static com.example.samscials.utils.Constants.PREF_DIRECTORY;
import static com.example.samscials.utils.Constants.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DrawableUtils;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.samscials.adapter.ViewPagerAdapter;
import com.example.samscials.fragments.Search;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements Search.OnDataPass {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        innit();
        addTabs();
    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.search));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.heart));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.user));
//        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        String directory = preferences.getString(PREF_DIRECTORY, "");
//        Bitmap bitmap = loadProfileImage(directory);
//        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//
//        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.homeclicked);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.homeclicked);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.addclicked);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.notificationclicked);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.userclicked);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.notification);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.user);
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.homeclicked);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.addclicked);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.notificationclicked);
                        break;
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.userclicked);
                        break;
                }
            }
        });
    }
    private Bitmap loadProfileImage(String directory){
        try {
            File file = new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void innit() {

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }
    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;
    @Override
    public void onChange(String uid) {
        USER_ID = uid;
        IS_SEARCHED_USER = true;
        viewPager.setCurrentItem(4);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 4){
            viewPager.setCurrentItem(0);
            IS_SEARCHED_USER = false;
        }else
            super.onBackPressed();
    }

}