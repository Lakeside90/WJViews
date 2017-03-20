package com.demozi.wjviews.behavior;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.demozi.wjviews.R;

public class FollowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_follow);

        Button button = (Button) findViewById(R.id.btn);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //view的中心点跟随手指移动
                        view.setX(motionEvent.getRawX() - view.getWidth()/2);
                        view.setY(motionEvent.getRawY() - view.getHeight()/2);
                        break;
                }
                return false;
            }
        });
    }
}
