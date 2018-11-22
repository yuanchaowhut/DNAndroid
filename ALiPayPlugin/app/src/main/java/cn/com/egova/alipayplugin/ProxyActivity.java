package cn.com.egova.alipayplugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;

import java.lang.reflect.Constructor;

import cn.com.egova.pluginstandard.PayInterfaceActivity;

/**
 * Created by yuanchao on 2018/11/21.
 */

public class ProxyActivity extends Activity {
    //需要加载淘票票的类名
    private String className;
    private PayInterfaceActivity payInterfaceActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ProxyActivity自己启动自己的时候，会传递类名参数进来。
        className = getIntent().getStringExtra("className");

        //根据类名，通过反射拿到需要跳转到的Activity的实例。
        try {
            Class activityClass = getClassLoader().loadClass(className);
            Constructor constructor = activityClass.getConstructor(new Class[]{});
            Object instance = constructor.newInstance(new Object[]{});
            payInterfaceActivity = (PayInterfaceActivity) instance;

            //注入上下文及生命周期
            payInterfaceActivity.attach(this);
            Bundle bundle = new Bundle();  //可传递信息
            payInterfaceActivity.onCreate(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");
        Intent i = new Intent(this, ProxyActivity.class);
        i.putExtra("className", className);
        super.startActivity(i);
    }


    @Override
    public ComponentName startService(Intent service) {
        String serviceName = service.getStringExtra("serviceName");
        Intent i = new Intent(this, ProxyService.class);
        i.putExtra("serviceName", serviceName);
        return super.startService(i);
    }


    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        IntentFilter newIntentFilter = new IntentFilter();
        for (int i = 0; i < filter.countActions(); i++) {
            newIntentFilter.addAction(filter.getAction(i));
        }
        return super.registerReceiver(new ProxyBroadCastReceiver(this, receiver.getClass().getName()), newIntentFilter);
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getDexClassLoader();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    protected void onStart() {
        super.onStart();
        payInterfaceActivity.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        payInterfaceActivity.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        payInterfaceActivity.onDestroy();
    }
}
