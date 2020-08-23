package com.example.golddate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.golddate.adapter.HomeViewPagerAdapter;
import com.example.golddate.fragments.ChatFragment;
import com.example.golddate.fragments.DiscoverFragment;
import com.example.golddate.fragments.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private Button homeLogoutBtn;
    private TabLayout mHomeTabs;
    private ViewPager mViewPager;
    private HomeViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mHomeTabs = findViewById(R.id.home_tabLayout);
        mViewPager = findViewById(R.id.home_viewPager);
        adapter = new HomeViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ProfileFragment(), "Profile");
        adapter.addFragment(new DiscoverFragment(), "Discover");
        adapter.addFragment(new ChatFragment(), "Chat");

        mViewPager.setAdapter(adapter);
        mHomeTabs.setupWithViewPager(mViewPager);
        mHomeTabs.getTabAt(0).setIcon(R.drawable.profile);
        mHomeTabs.getTabAt(1).setIcon(R.drawable.discover);
        mHomeTabs.getTabAt(2).setIcon(R.drawable.chat);

//        Set default tab
        mHomeTabs.selectTab(mHomeTabs.getTabAt(1));

        mHomeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
