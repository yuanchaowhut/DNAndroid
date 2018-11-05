package cn.com.egova.skin_core;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Observable;

import cn.com.egova.skin_core.utils.SkinPreference;
import cn.com.egova.skin_core.utils.SkinResources;

/**
 * Created by yuanchao on 2018/3/26.
 * 1.本换肤框架是基于 LayoutInflater.Factory2 创建View的原理,手动创建Factory2替代系统创建View.
 * 2.自己创建View的过程中采集需要换肤的View及其属性并进行保存;
 * 3.利用AssetManager,PackageManager及Resources等加载外部资源;
 * 4.采用观察者模式,当触发换肤动作时,所有观察者(SkinLayoutFactory)同时调用update方法进行换肤;
 * 5.皮肤资源为apk, 特别注意的是换肤资源名以及存放的目录必须与主应用完全保持一致. 特殊情况,比如:
 *   主应用某控件的背景是图片资源,但是apk里确是选择器,选择器用法是 @drawable/xxx   , 那么主应用
 *   里,该图片资源的存放路径就应该也放到drawable目录里,而不应该是放到mipmap目录下.
 */

public class SkinManager extends Observable {
    private static SkinManager instance;
    private Application app;
    private SkinActivityLifecycle skinActivityLifecycle;

    private SkinManager(Application app) {
        this.app = app;
        SkinPreference.init(app);
        SkinResources.init(app);

        //注册Activity生命周期回调(给每个Activity都注册生命周期方法, 这样就不用写BaseActivity被继承, 减少侵入性)
        skinActivityLifecycle = new SkinActivityLifecycle();
        app.registerActivityLifecycleCallbacks(skinActivityLifecycle);

        //加载皮肤
        loadSkin(SkinPreference.getInstance().getSkin());
    }

    public static void init(Application app) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(app);
                }
            }
        }
    }

    public static SkinManager getInstance() {
        return instance;
    }


    public void loadSkin(String path) {
        //还原默认皮肤包
        if (TextUtils.isEmpty(path)) {
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        } else {
            try {
                //添加资源路径
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager, path);

                Resources resources = app.getResources();   //本应用的Resources类
                // 横竖、语言
                Resources skinResources = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());  //皮肤APK的Resources类
                //获取外部Apk(皮肤包) 包名
                PackageManager mPm = app.getPackageManager();
                PackageInfo packageInfo = mPm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
                String pkgName = packageInfo.packageName;

                //这一步并未换肤,只是给SkinResources类中的3个重要变量赋值(mSkinResources,mSkinPkgName,isDefaultSkin)
                SkinResources.getInstance().applySkin(skinResources, pkgName);
                //保存当前使用的皮肤包
                SkinPreference.getInstance().setSkin(path);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //下面这两行代码才是真正开始换肤, 但是它也不是直接执行换肤逻辑, 而是通知所有观察者(SkinLayoutFactory)进行换肤.
        //被观察者发生变化了,通知观察者响应变化.观察者回调update()方法.
        setChanged();
        //通知观察者
        notifyObservers();
    }


    public void updateSkin(Activity activity) {
        skinActivityLifecycle.updateSkin( activity);
    }
}
