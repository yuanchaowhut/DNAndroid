package cn.com.egova.skin_core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import java.lang.reflect.Field;
import java.util.HashMap;

import cn.com.egova.skin_core.utils.SkinThemeUtils;

/**
 * Created by yuanchao on 2018/3/26.
 * SkinActivityLifecycle中的方法执行在Activity的生命周期方法之前.
 */

class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    HashMap<Activity, SkinLayoutFactory> mLayoutFactoryMap = new HashMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        /**更新状态栏*/
        SkinThemeUtils.updateStatusBar(activity);

        /**获取字体*/
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

        //获得Activity的布局加载器
        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出异常
            //设置 mFactorySet 标签为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SkinLayoutFactory skinLayoutFactory = new SkinLayoutFactory(activity,typeface);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutFactory);

        //注册观察者.
        SkinManager.getInstance().addObserver(skinLayoutFactory);
        mLayoutFactoryMap.put(activity, skinLayoutFactory);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //删除观察者
        SkinManager.getInstance().deleteObserver(mLayoutFactoryMap.get(activity));
        mLayoutFactoryMap.remove(activity);
    }


    public void updateSkin(Activity activity) {
        SkinLayoutFactory skinLayoutFactory = mLayoutFactoryMap.get(activity);
        skinLayoutFactory.update(null, null);
    }
}
