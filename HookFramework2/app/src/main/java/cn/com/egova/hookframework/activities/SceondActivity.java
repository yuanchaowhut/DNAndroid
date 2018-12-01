package cn.com.egova.hookframework.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.com.egova.hookframework.R;

/**
 * Created by 48608 on 2018/1/12.
 */

public class SceondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
