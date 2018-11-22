package cn.com.egova.alipayplugin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.com.egova.alipayplugin.permission.PermissionListener;
import cn.com.egova.alipayplugin.permission.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionListener {
    private static final String TAG = "MainActivity";
    public static final String ACTION_FROM_PLUGIN = "cn.com.egova.static.PLUGIN_ACTION";
    public static final String ACTION_FROM_HOST = "cn.com.egova.static.HOST_ACTION";
    private Button loadPlugin;
    private Button jumpActivity;
    private Button sendBroadcast;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, " 我是宿主，收到你的消息,握手完成!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  //23,Android 6.0
            permission();
        } else {
            init();
        }
    }


    private void permission() {
        PermissionUtil.requestRunPermisssion(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, this);
    }


    private void init() {
        initView();

        //注册插件发送过来的广播
        registerReceiver(mReceiver, new IntentFilter(new IntentFilter(ACTION_FROM_PLUGIN)));
    }

    private void initView() {
        loadPlugin = (Button) findViewById(R.id.loadPlugin);
        jumpActivity = (Button) findViewById(R.id.jumpActivity);
        sendBroadcast = (Button) findViewById(R.id.sendBroadcast);
        loadPlugin.setOnClickListener(this);
        jumpActivity.setOnClickListener(this);
        sendBroadcast.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadPlugin:
                loadPlugin("taopiaopiao.apk");
                break;
            case R.id.jumpActivity:
                jumpActivity();
                break;
            case R.id.sendBroadcast:
                sendBroad();
                break;
        }
    }

    //向插件发送广播(插件是静态注册)
    private void sendBroad() {
        Toast.makeText(getApplicationContext(), "我是宿主  插件插件!收到请回答!!  1", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(ACTION_FROM_HOST);
        sendBroadcast(intent);
    }

    /**
     * 跳转到插件的Activity本质上是跳转到宿主程序的占位Activity，然后由占位Activity去加载插件Activity的内容和资源.
     * PluginManager.getInstance().getPackageInfo() 拿到的是插件的PackageInfo。
     * packageInfo.activities[0]是启动Activity
     */
    private void jumpActivity() {
        Intent intent = new Intent(this, ProxyActivity.class);
        intent.putExtra("className", PluginManager.getInstance().getPackageInfo().activities[0].name);
        startActivity(intent);
    }

    private void loadPlugin(String pluginName) {
        File filesDir = this.getDir("plugin", Context.MODE_PRIVATE);
        Log.i(TAG, "this.getDir:" + filesDir.getAbsolutePath());
        String filePath = new File(filesDir, pluginName).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            Log.i(TAG, "加载插件 " + new File(Environment.getExternalStorageDirectory(), pluginName).getAbsolutePath());
            fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), pluginName));
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            File f = new File(filePath);
            if (f.exists()) {
                Toast.makeText(this, "dex overwrite", Toast.LENGTH_SHORT).show();
            }
            //获取加载插件内容和资源的DexClassLoader,Resources等
            PluginManager.getInstance().loadPath(this, "taopiaopiao.apk");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.dealResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onGranted() {
        init();
    }

    @Override
    public void onDenied(List<String> deniedPermission) {
        Toast.makeText(this, "未授权的权限包括:" + deniedPermission.toString(), Toast.LENGTH_SHORT).show();
    }
}
