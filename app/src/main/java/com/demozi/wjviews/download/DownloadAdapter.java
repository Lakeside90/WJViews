package com.demozi.wjviews.download;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demozi.wjviews.R;

import java.util.ArrayList;

/**
 * Created by wujian on 2017/3/14.
 */

public class DownloadAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DownloadTask> mTasks;


    public DownloadAdapter(Context context, ArrayList<DownloadTask> tasks) {
        mContext = context;
        mTasks = tasks;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_download, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.item_status = (TextView) convertView.findViewById(R.id.item_status);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.item_progress);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setTag(R.id.position, position);
        holder.name.setText(mTasks.get(position).getName());

        if(mTasks.get(position).isDownload()) {
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.setProgress(mTasks.get(position).getProgress());
        }else {
            holder.progress.setVisibility(View.INVISIBLE);
        }

        switch (mTasks.get(position).getStatus()) {
            case DownloadActivity.TASK_STATUS_STOP:
                holder.item_status.setText("");
                break;

            case DownloadActivity.TASK_STATUS_START:
                holder.item_status.setText("等待下载");
                break;

            case DownloadActivity.TASK_STATUS_RUNNING:
                holder.item_status.setText("下载中");
                break;

            case DownloadActivity.TASK_STATUS_FINISH:
                holder.item_status.setText("下载完成");
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView item_status;
        ProgressBar progress;
    }
}
