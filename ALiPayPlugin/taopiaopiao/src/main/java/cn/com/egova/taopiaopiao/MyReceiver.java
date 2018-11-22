package cn.com.egova.taopiaopiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.com.egova.pluginstandard.PayInterfaceBroadCastReciever;

/**
 * Created by yuanchao on 2018/11/22.
 */

public class MyReceiver extends BroadcastReceiver implements PayInterfaceBroadCastReciever{

    @Override
    public void attch(Context context) {
        Toast.makeText(context, "-----绑定上下文成功---->", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "-----插件收到广播--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播1--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播2--->", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "-----插件收到广播3--->", Toast.LENGTH_SHORT).show();
    }
}
