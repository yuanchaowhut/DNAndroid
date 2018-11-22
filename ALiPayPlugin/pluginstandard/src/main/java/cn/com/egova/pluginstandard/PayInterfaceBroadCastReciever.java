package cn.com.egova.pluginstandard;

import android.content.Context;
import android.content.Intent;

/**
 * Created by yuanchao on 2018/11/22.
 */

public interface PayInterfaceBroadCastReciever {
    public void attch(Context context);

    public void onReceive(Context context, Intent intent);
}
