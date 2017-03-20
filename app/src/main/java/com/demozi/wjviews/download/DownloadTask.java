package com.demozi.wjviews.download;

/**
 * Created by wujian on 2017/3/14.
 */

public class DownloadTask {

    private String name;
    private boolean isDownload;
    private int progress;
    private int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
