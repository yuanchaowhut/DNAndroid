package cn.com.egova.skin_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import cn.com.egova.skin_core.R;

/**
 * Created by yuanchao on 2018/3/26.
 */

public class SkinThemeUtils {
    private static int[] TYPEFACE_ATTR = {R.attr.skinTypeface};

    //系统控制状态栏颜色的属性, 优先读android.R.attr.statusBarColor, 没有才读android.support.v7.appcompat.R.attr.colorPrimaryDark
    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {android.support.v7.appcompat.R.attr.colorPrimaryDark};
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor, android.R.attr.navigationBarColor};

    public static int[] getResId(Context context, int[] attrs) {
        int[] resIds = new int[attrs.length];
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < typedArray.length(); i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        return resIds;
    }

    /**
     * 更新状态栏的颜色
     * @param activity
     */
    public static void updateStatusBar(Activity activity) {
        //5.0以上才能修改状态栏
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        //修改状态栏的颜色
        int[] resIds = getResId(activity, STATUSBAR_COLOR_ATTRS);
        if (resIds[0] == 0) {
            int statusBarColorId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (statusBarColorId != 0) {
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusBarColorId));  //修改状态栏颜色
            }
        } else {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resIds[0]));
        }

        //修改底部虚拟按键的颜色
        if (resIds[1] != 0) {
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(resIds[1]));
        }

    }

    /**
     * 获取字体
     * @param activity
     * @return
     */
    public static Typeface getSkinTypeface(Activity activity) {
        int skinTypeceId = getResId(activity, TYPEFACE_ATTR)[0];
        return SkinResources.getInstance().getTypeface(skinTypeceId);
    }
}
