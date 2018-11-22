package cn.com.egova.pluginstandard;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by yuanchao on 2018/11/21.
 */

public interface PayInterfaceActivity {

    public void attach(Activity proxyActivity);

    /**
     * 生命周期相关
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState);

    public void onStart();

    public void onResume();

    public void onPause();

    public void onStop();

    public void onDestroy();

    public void onSaveInstanceState(Bundle outState);

    public void onBackPressed();
}
