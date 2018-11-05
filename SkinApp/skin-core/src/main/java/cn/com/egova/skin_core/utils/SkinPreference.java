package cn.com.egova.skin_core.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yuanchao on 2018/3/26.
 */

public class SkinPreference {
    private static final String SKIN_SHARED = "skins";

    private static final String KEY_SKIN_PATH = "skin-path";

    private static SkinPreference instance;

    private SharedPreferences mPref;

    private SkinPreference(Context context) {
        this.mPref = context.getSharedPreferences(SKIN_SHARED, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinPreference.class) {
                if (instance == null) {
                    instance = new SkinPreference(context);
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return instance;
    }


    public void setSkin(String skinPath) {
        mPref.edit().putString(KEY_SKIN_PATH, skinPath).apply();
    }

    public String getSkin() {
        return mPref.getString(KEY_SKIN_PATH, null);
    }
}
