package cn.com.egova.hookframework;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import java.lang.reflect.Method;

import cn.com.egova.hookframework.hook.HookUtil;

/**
 * Created by baby on 2018/4/2.
 * 在程序启动时就hook
 */

public class MyApplication extends Application {
    private AssetManager assetManager;
    private Resources newResource;

    @Override
    public void onCreate() {
        super.onCreate();
        HookUtil hookUtil = new HookUtil();
        hookUtil.hookStartActivity(this);          //hook startActivity
        hookUtil.hookHookMh();                             //hook ActivityThread.H
        hookUtil.injectPluginClass();
        initResources();
    }

    /**
     * 加载插件apk的资源
     */
    private void initResources() {
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Const.PLUGIN_APK_NAME;
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, apkPath);

            //手动实例化插件的StringBlocks数组
            Method ensureStringBlocksMethod = assetManager.getClass().getDeclaredMethod("ensureStringBlocks");
            ensureStringBlocksMethod.setAccessible(true);
            ensureStringBlocksMethod.invoke(assetManager);

            //实例化resourecs
            Resources supResources = getResources();
            newResource = new Resources(assetManager, supResources.getDisplayMetrics(), supResources.getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AssetManager getAssetManager() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    public Resources getResources() {
        return newResource == null ? super.getResources() : newResource;
    }
}
