package com.gtx.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import com.gtx.util.StringUtil;

import java.io.File;

public class DownApkService extends Service {

    private Context context = this;
    private SharedPreferences mSp;

    public DownApkService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle downloadBundle = intent.getBundleExtra("download");
        if (downloadBundle != null) {
            String downloadUrl = downloadBundle.getString("downloadUrl");
            String title = downloadBundle.getString("title");
            if (!StringUtil.isEmpty(downloadUrl)) {
                mSp = context.getSharedPreferences("downloadApk", MODE_PRIVATE);
                long downloadId = downloadApk(downloadUrl, title);
                mSp.edit().putLong("downloadId", downloadId).commit();
            }
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }


    private long downloadApk(String url, String title) {
        String apkName ="朗逸工单系统.apk";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), apkName);
        if (file != null && file.exists()) {
            file.delete();
        }

        Uri downloadUri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName);

        mSp.edit().putString("apkName", apkName).commit();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setTitle(title);

        DownloadManager mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        return mDownloadManager.enqueue(request);
    }
}
