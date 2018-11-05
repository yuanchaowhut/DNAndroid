package cn.com.egova.skinapp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.com.egova.skin_core.SkinManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    private Button btnSwitchSkin;
    private Button btnRestoreSkin;
    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnSwitchSkin = (Button) findViewById(R.id.btn_switch_skin);
        btnRestoreSkin = (Button) findViewById(R.id.btn_restore_skin);
        tvHello = (TextView) findViewById(R.id.tv_hello);

        btnSwitchSkin.setOnClickListener(this);
        btnRestoreSkin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_skin:
                tvHello.setText("你好, 美女!");
//                String path = Environment.getExternalStorageDirectory()+"/APK/skin.apk";
                String path = Environment.getExternalStorageDirectory()+"/APK/skin2.skin";
                SkinManager.getInstance().loadSkin(path);
                break;
            case R.id.btn_restore_skin:
                tvHello.setText("你好, 帅哥!");
                SkinManager.getInstance().loadSkin(null);
                break;
        }
    }
}
