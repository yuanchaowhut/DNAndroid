package cn.com.egova.hookframework;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by baby on 2018/4/2.
 * 提供一个用于集中式处理登录跳转的Activity，不需要任何布局，具体业务也不在本类中做。
 * 在HookUtil中处理。
 */

public class ProxyActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
    }
}
