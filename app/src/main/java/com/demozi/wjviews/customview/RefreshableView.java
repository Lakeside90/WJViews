package com.demozi.wjviews.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demozi.wjviews.R;

import java.util.logging.Logger;

/**
 * Created by wujian on 2017/2/28.
 */

public class RefreshableView extends LinearLayout implements View.OnTouchListener{


    View header;    //下拉的头部
    ProgressBar progressBar;
    ImageView arrow;
    TextView description;
    TextView updated_at;
    ListView listView;

    /**
     * 下拉状态
     */
    static final int STATUS_PULL_TO_REFRESH = 0;

    /**
     * 释放立即刷新状态
     */
    static final int STATUS_RELEASE_TO_REFRESH = 1;

    /**
     * 正在刷新
     */
    static final int STATUS_REFRESHING = 2;

    /**
     * 刷新结束或未刷新
     */
    static final int STATUS_REFRESH_FINISHED = 3;

    int currentStatus = STATUS_REFRESH_FINISHED;
    int lastStatus = STATUS_REFRESH_FINISHED;

    /**
     * 下拉回滚速度
     */
    static final int SCROLL_SPEED = -20;

    /**     下拉刷新间隔时间相关      **/
    static final String UPDATE_AT = "update_at";
    static final long ONE_MINUTE = 60 * 1000;
    static final long ONE_HOUR = 60 * ONE_MINUTE;
    static final long ONE_DAY = 24 * ONE_HOUR;
    static final long ONE_MONTH = 30 * ONE_DAY;
    static final long ONE_YEAR = 12 * ONE_MONTH;
    long lastUpdateTime;
    int mId = -1;

    SharedPreferences preferences;
    int touchSlop;
    boolean ableToPull;

    boolean loadOnce;   //onLayout是否已经加载过
    int hideHeaderHeight;     //需要隐藏的header的高度
    MarginLayoutParams headerLayoutParams;
    float yDown;    //手指按下时屏幕的纵坐标

    PullToRefreshListener mListener;

    public RefreshableView(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public RefreshableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        description = (TextView) header.findViewById(R.id.description);
        updated_at = (TextView) header.findViewById(R.id.updated_at);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        refreshUpdateAtValue();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit().putLong(UPDATE_AT+mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
    }

    /**
     * 将下拉头向上偏移进行隐藏， 给listview添加touch监听
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;
            listView = (ListView) getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
        }

    }

    /**
     * listview 触摸时调用，核心代码，处理的下拉的逻辑
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        setIsAbleToPull(motionEvent);
        if (ableToPull) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = motionEvent.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float yMove = motionEvent.getRawY();
                    int distance = (int) (yMove - yDown);
                    if (distance < touchSlop) return false;
                    if (distance < 0 && headerLayoutParams.topMargin <= hideHeaderHeight) {
                        //手指下滑，且下拉头部完全隐藏，就屏蔽下拉事件
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        }else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        //偏移下拉，实现弹力效果
                        headerLayoutParams.topMargin = (distance)/2 + hideHeaderHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        //松手时如果是立即刷新状态，则执行刷新任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        //松手时是下拉状态，则隐藏下拉头
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            //更新下拉头信息
            if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                return true;
            }
        }
        return false;
    }

    /**
     * 执行刷新操作
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                Log.e("RefreshingTask" , "RefreshingTask ==== " + topMargin);
                if(topMargin < 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            updateHeaderView();
            headerLayoutParams.topMargin = values[0];
            header.setLayoutParams(headerLayoutParams);
        }
    }

    /**
     * 隐藏下拉头
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin < hideHeaderHeight) {
                    topMargin = hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                /*try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            headerLayoutParams.topMargin = values[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            headerLayoutParams.topMargin = integer;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }


    /**
     * 根据listview滚动的位置，判断是否可以进行下拉刷新
     * @param event
     */
    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiablPos = listView.getFirstVisiblePosition();
            if (firstVisiablPos == 0 && firstChild.getTop() == 0) {
                //滚到了最顶端
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != hideHeaderHeight) {
                    headerLayoutParams.topMargin = hideHeaderHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            ableToPull = true;
        }
    }

    /**
     * 更新下拉头
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            switch (currentStatus) {
                case STATUS_PULL_TO_REFRESH:
                    description.setText(getResources().getString(R.string.pull_to_refresh));
                    arrow.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    rotateArrow();
                    break;

                case STATUS_RELEASE_TO_REFRESH:
                    description.setText(getResources().getString(R.string.release_to_refresh));
                    arrow.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                    rotateArrow();
                    break;

                case STATUS_REFRESHING:
                    description.setText(getResources().getString(R.string.refreshing));
                    arrow.setVisibility(GONE);
                    progressBar.setVisibility(VISIBLE);
                    arrow.clearAnimation();
                    break;
            }
            refreshUpdateAtValue();
        }
    }

    /**
     * 旋转下拉箭头
     */
    private void rotateArrow() {
        float pointX = arrow.getWidth() / 2f;
        float pointY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pointX, pointY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }


    /**
     * 更新下拉刷新时间
     */
    private void refreshUpdateAtValue() {
        lastUpdateTime = preferences.getLong(UPDATE_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        updated_at.setText(updateAtValue);
    }


    /**
     * 刷新回调，处理具体业务逻辑
     */
    public interface PullToRefreshListener {
        void onRefresh();
    }
}
