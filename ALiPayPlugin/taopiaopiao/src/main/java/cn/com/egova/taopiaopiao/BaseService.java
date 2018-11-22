package cn.com.egova.taopiaopiao;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.com.egova.pluginstandard.PayInterfaceService;

/**
 * Created by yuanchao on 2018/11/21.
 */

public class BaseService extends Service implements PayInterfaceService {
    public static final String TAG = "BaseService";
    private Service that;

    @Override
    public void attach(Service proxyService) {
        this.that = proxyService;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,   " onCreate================================");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG,   " onStart================================");
    }

    @Override
    public int onStartCommond(Intent intent, int flags, int startId) {
        Log.i(TAG,   " onStartCommond================================");
        return Service.START_STICKY;
    }



    @Override
    public void onDestryoy() {
        Log.i(TAG,   " onDestroy================================");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG,   " onConfigurationChanged================================");
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG,   " onLowMemory================================");
    }

    @Override
    public void onTrimMemory(int level) {
        Log.i(TAG,   " onTrimMemory================================");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,   " onUnbind================================");
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG,   " onRebind================================");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG,   " onTaskRemoved================================");
    }
}
