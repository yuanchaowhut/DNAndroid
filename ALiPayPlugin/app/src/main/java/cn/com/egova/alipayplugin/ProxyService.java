package cn.com.egova.alipayplugin;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;

import cn.com.egova.pluginstandard.PayInterfaceService;

/**
 * Created by yuanchao on 2018/11/21.
 */

public class ProxyService extends Service {
    private String serviceName;
    private PayInterfaceService payInterfaceService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        init(intent);
        return null;
    }

    private void init(Intent intent) {
        serviceName = intent.getStringExtra("serviceName");
        //加载Service类的实例
        try {
            Class serviceClass = PluginManager.getInstance().getDexClassLoader().loadClass(serviceName);
            Constructor constructor = serviceClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});
            payInterfaceService = (PayInterfaceService) instance;

            //注入上下文
            payInterfaceService.attach(this);

            //bundle可传递数据
            Bundle bundle = new Bundle();
            bundle.putInt("form", 1);
            payInterfaceService.onCreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (payInterfaceService == null) {
            init(intent);
        }
        return payInterfaceService.onStartCommond(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        payInterfaceService.onUnbind(intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        payInterfaceService.onRebind(intent);
        super.onRebind(intent);
    }

    @Override
    public void onLowMemory() {
        payInterfaceService.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        payInterfaceService.onDestryoy();
        super.onDestroy();
    }
}
