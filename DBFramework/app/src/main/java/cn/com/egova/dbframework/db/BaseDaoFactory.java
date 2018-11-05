package cn.com.egova.dbframework.db;

import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import cn.com.egova.dbframework.constant.DBConst;

/**
 * Created by yuanchao on 2018/3/9.
 */

public class BaseDaoFactory {
    //单例模式.
    private static BaseDaoFactory instance;
    private SQLiteDatabase sqLiteDatabase;
    //设计一个数据库连接池("BaseDao": baseDao)
    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    protected BaseDaoFactory() {
        File dir = new File(DBConst.DB_UPDATE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DBConst.DB_PUBLIC_PATH, null);
    }

    public static BaseDaoFactory getInstance() {
        if (instance == null) {
            synchronized (BaseDaoFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoFactory();
                }
            }
        }
        return instance;
    }


    /**
     * 获取BaseDao的实例.
     * 1.注意方法上的泛型;
     * 2.注意获取BaseDao是使用的反射.
     * 3. T----BaseDao.class, BaseDaoNewImpl.class ;  M---User.class, Person.class等.
     *
     * @param entityClass
     * @param <T>
     * @return
     */
    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(daoClass.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) baseDao;
    }
}
