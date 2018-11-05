package cn.com.egova.dbframework.sub_db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cn.com.egova.dbframework.db.BaseDao;
import cn.com.egova.dbframework.db.BaseDaoFactory;

/**
 * Created by yuanchao on 2018/3/13.
 */

public class BaseDaoSubFactory extends BaseDaoFactory {
    private static BaseDaoSubFactory instance;

    //用于操作分库的数据库对象.
    //注意这个地方一定不能使用父类的 sqliteDatabase对象, 因为两者的连接不同, 此处要操作的是分库.
    protected SQLiteDatabase subSqliteDatabase;

    protected BaseDaoSubFactory() {

    }

    public static BaseDaoSubFactory getInstance() {
        if (instance == null) {
            synchronized (BaseDaoSubFactory.class) {
                if (instance == null) {
                    instance = new BaseDaoSubFactory();
                }
            }
        }
        return instance;
    }


    public <T extends BaseDao<M>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        //分库(私有库)的路径.
        String subDbPath = PrivateDataBaseEnums.database.getPath();

        if (map.get(subDbPath) != null) {
            return (T) map.get(subDbPath);
        }
        Log.i("TAG", "生成数据库文件的位置:" + PrivateDataBaseEnums.database.getPath());
        try {
            this.subSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(subDbPath, null);
            T t = daoClass.newInstance();
            t.init(subSqliteDatabase, entityClass);
            map.put(subDbPath, t);
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
