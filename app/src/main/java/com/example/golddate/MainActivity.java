package com.example.golddate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.golddate.adapter.ViewPagerAdapter;
import com.example.golddate.fragments.LoginFragment;
import com.example.golddate.fragments.SignUpFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter adapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mTabLayout = findViewById(R.id.main_tabLayout);
        mViewPager = findViewById(R.id.main_viewPager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new SignUpFragment(), "Sign Up");

        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
                    findViewById(R.id.main_constraintLayout).setBackground(getDrawable(R.drawable.layout_bg));
                }

                if (tab.getPosition() == 1) {
                    getWindow().setStatusBarColor(getColor(R.color.colorGold));
                    findViewById(R.id.main_constraintLayout).setBackground(getDrawable(R.drawable.signup_bg));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
//currentUser = FirebaseAuth.getInstance().getCurrentUser();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }
}
