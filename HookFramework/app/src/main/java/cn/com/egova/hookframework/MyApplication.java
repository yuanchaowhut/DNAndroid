package cn.com.egova.hookframework;

import android.app.Application;

import cn.com.egova.hookframework.hook.HookUtil;

/**
 * Created by baby on 2018/4/2.
 * 在程序启动时就hook
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HookUtil hookUtil = new HookUtil();
        hookUtil.hookStartActivity(this);
        hookUtil.hookHookMh();
    }
}
