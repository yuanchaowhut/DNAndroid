package cn.com.egova.pluginstandard;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * Created by yuanchao on 2018/11/21.
 */

public interface PayInterfaceService {

    public void onCreate();

    public void onStart(Intent intent, int startId);

    public int onStartCommond(Intent intent, int flags, int startId);

    public void onDestryoy();

    public void onConfigurationChanged(Configuration newConfig);

    public void onLowMemory();

    public void onTrimMemory(int level);

    public IBinder onBind(Intent intent);

    public boolean onUnbind(Intent intent);

    public void onRebind(Intent intent);

    public void onTaskRemoved(Intent rootIntent);

    public void attach(Service proxyService);

}
