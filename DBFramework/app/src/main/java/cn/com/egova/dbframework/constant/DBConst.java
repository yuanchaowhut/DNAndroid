package cn.com.egova.dbframework.constant;

import android.os.Environment;

/**
 * Created by yuanchao on 2018/3/9.
 */

public class DBConst {
    /**app的根路径*/
    public static final String APP_PATH = Environment.getExternalStorageDirectory() + "/DBFramework";
    /**app每次都需要检查DB的版本，故设置一个update目录，用于存放数据库文件。*/
    public static final String DB_UPDATE_PATH = APP_PATH + "/update";
    /**app每次升级都需要先备份数据库文件*/
    public static final String DB_BAK_PATH = APP_PATH + "/bak";
    /**公共库路径*/
    public static final String DB_PUBLIC_PATH = DB_UPDATE_PATH + "/user.db";
}
