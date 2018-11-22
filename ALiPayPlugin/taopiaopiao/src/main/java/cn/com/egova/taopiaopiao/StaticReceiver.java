package cn.com.egova.taopiaopiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by yuanchao on 2018/11/22.
 */

public class StaticReceiver extends BroadcastReceiver {
    public static final String ACTION_FROM_PLUGIN = "cn.com.egova.static.PLUGIN_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        //注意这里的上下文是宿主程序的MainActivity，因为静态广播是由宿主解析插件的清单文件并注册的。
        Toast.makeText(context, "我是插件   收到宿主的消息  静态注册的广播  收到宿主的消息----->", Toast.LENGTH_SHORT).show();
        //向宿主发送广播
        context.sendBroadcast(new Intent(ACTION_FROM_PLUGIN));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "休眠之后---->", Toast.LENGTH_SHORT).show();
    }
}
