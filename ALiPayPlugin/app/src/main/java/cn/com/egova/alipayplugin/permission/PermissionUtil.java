package cn.com.egova.alipayplugin.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchao on 2017/5/4.
 */

public class PermissionUtil {
    public static PermissionListener mListener;
    public static final int PERMISSION_REQUESTCODE = 100;
    public static List<String> permissionLists = new ArrayList<String>();


    /**
     * 主要逻辑包括:判断,申请
     * @param activity
     * @param permissions
     * @param listener
     */
    public static void requestRunPermisssion(Activity activity, String[] permissions, PermissionListener listener) {
        if (activity == null) {
            return;
        }
        mListener = listener;

        //遍历所有权限,将为未授权的存储到集合.
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }

        if (!permissionLists.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);
        } else {
            //表示全都授权了
            mListener.onGranted();
        }
    }


    /**
     * 处理动态申请后的结果.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void dealResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (grantResults.length > 0) {
                    //存放没授权成功的权限.
                    List<String> deniedPermissions = new ArrayList<String>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i]; //该权限的授权结果
                        String permission = permissions[i]; //该权限的名称
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //说明都授权了
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }
}
