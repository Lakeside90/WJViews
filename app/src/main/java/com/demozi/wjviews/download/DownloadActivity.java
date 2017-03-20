package com.demozi.wjviews.download;

import android.app.DownloadManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demozi.wjviews.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadActivity extends AppCompatActivity {

    private ListView mListView;

    private ArrayList<DownloadTask> mTasks;
    ExecutorService executorService;

    final int THREAD_MAX_NUM = 5;

    public static final int TASK_STATUS_STOP = -1;
    public static final int TASK_STATUS_START = 0;
    public static final int TASK_STATUS_RUNNING = 1;
    public static final int TASK_STATUS_FINISH = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        initData();

        mListView = (ListView) findViewById(R.id.list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = (int) view.getTag(R.id.position);
                if (((ThreadPoolExecutor)executorService).getActiveCount() == THREAD_MAX_NUM) {
                    mTasks.get(position).setStatus(TASK_STATUS_START);
                    updateDownloadStatus(position);
                }
                download(position);
            }
        });

        mListView.setAdapter(new DownloadAdapter(this, mTasks));
    }

    private void initData() {
        executorService = Executors.newFixedThreadPool(5);

        mTasks = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            DownloadTask task = new DownloadTask();
            task.setName(" download task " + i);
            task.setDownload(false);
            task.setProgress(0);
            task.setStatus(TASK_STATUS_STOP);
            mTasks.add(task);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void download(final int positionInAdapter) {
        DownloadThread  downloadThread = new DownloadThread(positionInAdapter);
        executorService.execute(downloadThread);
    }

    public void publishProgress(final int positionInAdapter, final int progress) {
        mTasks.get(positionInAdapter).setProgress(progress);
        if (progress == 100) {
            mTasks.get(positionInAdapter).setStatus(TASK_STATUS_FINISH);
        }else {
            mTasks.get(positionInAdapter).setStatus(TASK_STATUS_RUNNING);
        }
        mTasks.get(positionInAdapter).setDownload(true);
        if(positionInAdapter >= mListView.getFirstVisiblePosition() &&
                positionInAdapter <= mListView.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - mListView.getFirstVisiblePosition();
            ProgressBar item = (ProgressBar) mListView.getChildAt(positionInListView)
                    .findViewById(R.id.item_progress);
            item.setProgress(progress);
            item.setVisibility(View.VISIBLE);
        }
        updateDownloadStatus(positionInAdapter);
    }

    public void updateDownloadStatus(int positionInAdapter) {
        if(positionInAdapter >= mListView.getFirstVisiblePosition() &&
                positionInAdapter <= mListView.getLastVisiblePosition()) {
            int positionInListView = positionInAdapter - mListView.getFirstVisiblePosition();
            TextView item = (TextView) mListView.getChildAt(positionInListView)
                    .findViewById(R.id.item_status);
            switch (mTasks.get(positionInAdapter).getStatus()) {
                case TASK_STATUS_START:
                    item.setText("等待下载");
                    break;

                case TASK_STATUS_RUNNING:
                    item.setText("下载中");
                    break;

                case TASK_STATUS_FINISH:
                    item.setText("下载完成");
                    break;
            }
        }
    }

    class DownloadThread implements Runnable {

        int positionInAdapter;

        public DownloadThread(int positionInAdapter) {
            this.positionInAdapter = positionInAdapter;
        }

        @Override
        public void run() {
            for (int i = 1; i < 101; i++) {
                final int progress = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishProgress(positionInAdapter, progress);
                    }
                });
                SystemClock.sleep(500);
            }
        }
    }
}
