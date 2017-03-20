package com.demozi.wjviews.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by wujian on 2017/1/6.
 */

public class FollowBehavior extends CoordinatorLayout.Behavior<TextView> {


    /**
     * 无参构造方法用于在程序中设置Behavior中调用
     *
     */
    public FollowBehavior() {
        super();
    }


    /**
     * 这个构造方法用于xml布局发射使用
     * @param context
     * @param attrs
     */
   public FollowBehavior(Context context, AttributeSet attrs) {
       super(context, attrs);
   }

    /**
     * 当依赖的View发生变化（位置）时，当前的View页做出变化
     * dependency 下移，child也下移
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        child.setText(dependency.getX()+","+dependency.getY());
        child.setX(dependency.getX() + (dependency.getWidth() - child.getWidth())/2);
        child.setY(dependency.getY()+200);
        return true;
    }

    //确定依赖关系 child:应用behavior的view
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof Button;
    }

    /**
     * CoordinatorLayout onInterceptTouchEvent() 中会调用Behavior的onInterceptTouchEvent()
     * @param parent
     * @param child
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, TextView child, MotionEvent ev) {
        Log.e("FollowBehavior", "--- onInterceptTouchEvent ---");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, TextView child, MotionEvent ev) {
        Log.e("FollowBehavior", "--- onTouchEvent ---");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, TextView child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, TextView child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }
}
