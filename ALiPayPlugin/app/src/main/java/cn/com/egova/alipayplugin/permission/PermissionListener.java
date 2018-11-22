package cn.com.egova.alipayplugin.permission;

import java.util.List;

/**
 * Created by yuanchao on 2017/5/4.
 */

public interface PermissionListener {
    void onGranted();//已授权

    void onDenied(List<String> deniedPermission);//未授权
}
