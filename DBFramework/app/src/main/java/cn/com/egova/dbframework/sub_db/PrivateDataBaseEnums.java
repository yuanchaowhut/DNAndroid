package cn.com.egova.dbframework.sub_db;

import java.io.File;

import cn.com.egova.dbframework.bean.User;
import cn.com.egova.dbframework.constant.DBConst;
import cn.com.egova.dbframework.db.BaseDaoFactory;

/**
 * Created by yuanchao on 2018/3/14.
 */

public enum PrivateDataBaseEnums {
    database("");
    private String dbPath;

    PrivateDataBaseEnums(String dbPath) {
        this.dbPath = dbPath;
    }

    //用于产生当前登录用户对应的分库的路径.
    public String getPath() {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File dir = new File(DBConst.DB_UPDATE_PATH, currentUser.getId());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return dir.getAbsolutePath() + "/login.db";
            }
        }

        return null;
    }
}
