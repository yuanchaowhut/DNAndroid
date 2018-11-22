package cn.com.egova.taopiaopiao;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String BROAD_CAST_ACTION = "cn.com.egova.taopiaopiao.dynamicreceiver";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ImageView img = (ImageView) findViewById(R.id.img);
        Button sendBroad = (Button) findViewById(R.id.sendBroad);

        img.setOnClickListener(this);
        sendBroad.setOnClickListener(this);

        //注册动态广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROAD_CAST_ACTION);
        registerReceiver(new MyReceiver(), intentFilter);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.img) {
            Toast.makeText(that, "---------->", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(that, SecondActivity.class));
            startService(new Intent(that, OneService.class));
        } else {
            //发送动态广播
            Intent intent = new Intent();
            intent.setAction(BROAD_CAST_ACTION);
            sendBroadcast(intent);
        }
    }
}
