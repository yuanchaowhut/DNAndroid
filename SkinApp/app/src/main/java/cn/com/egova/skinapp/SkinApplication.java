package cn.com.egova.skinapp;

import android.app.Application;

import cn.com.egova.skin_core.SkinManager;

/**
 * Created by yuanchao on 2018/3/26.
 */

public class SkinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
