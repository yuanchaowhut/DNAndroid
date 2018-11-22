package cn.com.egova.alipayplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Constructor;

import cn.com.egova.pluginstandard.PayInterfaceBroadCastReciever;

/**
 * Created by yuanchao on 2018/11/22.
 */

public class ProxyBroadCastReceiver extends BroadcastReceiver {
    private String className;
    private PayInterfaceBroadCastReciever payInterfaceBroadCastReciever;

    public ProxyBroadCastReceiver(Context context, String className) {
        this.className = className;
        try {
            Class receiverClass = PluginManager.getInstance().getDexClassLoader().loadClass(className);
            Constructor constructor = receiverClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});
            payInterfaceBroadCastReciever = (PayInterfaceBroadCastReciever) instance;

            payInterfaceBroadCastReciever.attch(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        payInterfaceBroadCastReciever.onReceive(context, intent);
    }
}
