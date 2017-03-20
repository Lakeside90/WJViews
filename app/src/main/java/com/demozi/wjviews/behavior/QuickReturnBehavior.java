package com.demozi.wjviews.behavior;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.TextView;

/**
 * Created by wujian on 2017/1/14.
 */

public class QuickReturnBehavior extends CoordinatorLayout.Behavior<TextView>{

    private int mDySinceDirectionChange;
    private static Interpolator interpolator = new LinearOutSlowInInterpolator();

    public QuickReturnBehavior(Context context, AttributeSet attributeSet) {
       super(context, attributeSet);
   }

    //监听垂直方向滚动信息
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, TextView child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, TextView child, View target, int dx, int dy, int[] consumed) {

        if ((dy > 0 && mDySinceDirectionChange < 0) || (dy < 0 && mDySinceDirectionChange > 0)) {
            //往相反的方向滑动
            child.animate().cancel();
            mDySinceDirectionChange = 0;
        }
        mDySinceDirectionChange += dy;
        if (mDySinceDirectionChange > 0 && child.getVisibility() == View.VISIBLE) {
            hide(child);
        }else if (mDySinceDirectionChange < 0 && child.getVisibility() == View.GONE){
            show(child);
        }
    }

    private void hide(final TextView view) {
        ViewPropertyAnimator animator = view.animate().translationY(view.getHeight()).setInterpolator(interpolator).setDuration(200);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void show(final TextView view) {
        ViewPropertyAnimator animator = view.animate().translationY(0).setInterpolator(interpolator).setDuration(200);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }
}
