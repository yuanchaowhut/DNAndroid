package cn.com.egova.dbframework.db;

import java.util.List;

/**
 * Created by yuanchao on 2018/3/9.
 * 规范所有数据库的操作.
 */

public interface IBaseDao<T> {
    void insert(T entity);

    long update(T entity, T where);
//
    int delete(T where);
//
    List<T> query(String sql);
//
    List<T> query(T where);

    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
}
