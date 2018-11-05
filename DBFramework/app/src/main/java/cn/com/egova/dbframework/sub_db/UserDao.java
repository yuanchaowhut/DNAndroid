package cn.com.egova.dbframework.sub_db;

import android.util.Log;

import java.util.List;

import cn.com.egova.dbframework.bean.User;
import cn.com.egova.dbframework.db.BaseDao;

/**
 * Created by yuanchao on 2018/3/14.
 * 用于维护共有数据
 */

public class UserDao extends BaseDao<User> {

    @Override
    public void insert(User entity) {
        //查询所有
        List<User> list = query(new User());
        //遍历将所有记录的状态设置为0 (0--未登录, 1--登录)
        User where = null;
        for (User user : list) {
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);
            update(user, where);
            Log.i("TAG", user.getName()+ "的登录状态: 未登录(0)");
        }
        //将当前新增用户设置为登录状态
        entity.setStatus(1);
        Log.i("TAG", entity.getName()+ "的登录状态: 登录(1)");
        super.insert(entity);
    }


    /**
     * 获取当前登录的user.
     * @return
     */
    public User getCurrentUser() {
        User where = new User();
        where.setStatus(1);
        List<User> list = query(where);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
