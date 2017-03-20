package com.demozi.wjviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.demozi.wjviews.behavior.DrawerActivity;
import com.demozi.wjviews.behavior.FollowActivity;
import com.demozi.wjviews.behavior.QuickReturnActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void startNavigationActivity(View view) {
       startActivity(new Intent(this, NavigationDrawerActivity.class));
    }

    public void startScrollingActivity(View view) {
        startActivity(new Intent(this, QuickReturnActivity.class));
    }

    public void startSettingsActivity(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void startTabActivity(View view) {
        startActivity(new Intent(this, TabActivity.class));
    }
}
