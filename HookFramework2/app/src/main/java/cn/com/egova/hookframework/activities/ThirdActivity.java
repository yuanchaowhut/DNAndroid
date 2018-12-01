package cn.com.egova.hookframework.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.com.egova.hookframework.R;

/**
 * Created by Administrator on 2018/2/26 0026.
 */

public class ThirdActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thrid);
    }
}
