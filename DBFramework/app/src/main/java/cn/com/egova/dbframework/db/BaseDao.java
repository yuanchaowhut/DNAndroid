package cn.com.egova.dbframework.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.egova.dbframework.annotation.DbField;
import cn.com.egova.dbframework.annotation.DbTable;

/**
 * Created by yuanchao on 2018/3/9.
 */

public class BaseDao<T> implements IBaseDao<T> {
    //持有数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;
    //表明
    private String tableName;
    //持有数据表对应的Java实体的类型(方便使用反射获取字段等字段)
    private Class<T> entityClass;
    //是否初始化的标记,默认false.
    private boolean isInit = false;

    //缓存columnName和Field的对应关系.
    private Map<String, Field> cacheMap;

    /**
     * 1.框架内部的逻辑，最好不要提供构造方法给调用层能用,而是提供一个init方法;
     * 2.为了框架内调用方便,所以一般使用protected关键字.
     * 3.因为要扩展, BaseDaoSubFactory要继承init方法,故将protected改为public.
     *
     * @param sqLiteDatabase
     * @param entityClass
     * @return
     */
    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if (!isInit) {
            //获取表名.
            if (entityClass.isAnnotationPresent(DbTable.class)) {
                this.tableName = entityClass.getAnnotation(DbTable.class).value();
            } else {
                this.tableName = entityClass.getSimpleName();
            }

            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            //根据entityClass自动建表.
            String createSql = getCreateTableSql();
            sqLiteDatabase.execSQL(createSql);

            //缓存columnName与Field的对应关系.
            cacheMap = new HashMap<>();
            initCacheMap();

            isInit = true;
        }
        return isInit;
    }

    private void initCacheMap() {
        String sql = "select * from " + tableName + " limit 1,0";  //空表,但是表有结构就行.
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        //1.数据表中的字段名
        String[] columnNames = cursor.getColumnNames();
        //2.Java实体类中的字段
        Field[] fields = entityClass.getDeclaredFields();
        //开启访问权限.
        for (Field field : fields) {
            field.setAccessible(true);
        }
        //对1,2进行映射.
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field field : fields) {
                String fieldName = null;
                if (field.isAnnotationPresent(DbField.class)) {
                    fieldName = field.getAnnotation(DbField.class).value();
                } else {
                    fieldName = field.getName();
                }
                if (columnName.equals(fieldName)) {
                    columnField = field;
                }
            }

            if (columnField != null) {
                cacheMap.put(columnName, columnField);
            }
        }
    }


    public String getCreateTableSql() {
        //create table if not exists tb_user(_id integer,name varchar(20),password varchar(20))
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName + "(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class type = field.getType();
            if (field.isAnnotationPresent(DbField.class)) {
                if (type == String.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " INTEGER,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " REAL,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BIGINT,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getAnnotation(DbField.class).value() + " BLOB,");
                } else {
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuffer.append(field.getName() + " TEXT,");
                } else if (type == Integer.class) {
                    stringBuffer.append(field.getName() + " INTEGER,");
                } else if (type == Double.class) {
                    stringBuffer.append(field.getName() + " REAL,");
                } else if (type == Long.class) {
                    stringBuffer.append(field.getName() + " BIGINT,");
                } else if (type == byte[].class) {
                    stringBuffer.append(field.getName() + " BLOB,");
                } else {
                    continue;
                }
            }
        }

        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }


    @Override
    public void insert(T entity) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        sqLiteDatabase.insert(tableName, null, values);
    }

    @Override
    public long update(T entity, T where) {
        Map<String, String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        Condition condition = new Condition(where);
        int rows = sqLiteDatabase.update(tableName, values, condition.whereClause, condition.whereArgs);
        return rows;
    }

    @Override
    public int delete(T where) {
        Condition condition = new Condition(where);
        int rows = sqLiteDatabase.delete(tableName, condition.whereClause, condition.whereArgs);
        return rows;
    }

    @Override
    public List<T> query(String sql) {
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        return null;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        //sqLiteDatabase.query(tableName,null,"id=?",new String[],null,null,orderBy,"1,5");

        String limitStr = null;
        if (startIndex != null && limit != null) {
            limitStr = startIndex + "," + limit;
        }
        Condition condition = new Condition(where);
        Cursor cursor = sqLiteDatabase.query(tableName, null, condition.whereClause, condition.whereArgs, orderBy, null, null, limitStr);
        List<T> result = getResult(cursor, where);
        return result;
    }

    private List<T> getResult(Cursor cursor, T obj) {
        List<T> list = new ArrayList<>();
        //遍历cursor封装对象集合.
        while (cursor.moveToNext()) {
            //每个内循环封装一个对象.
            try {
                T item = (T) obj.getClass().newInstance();
                for (Map.Entry<String, Field> me : cacheMap.entrySet()) {
                    String columnName = me.getKey();
                    Field columnField = me.getValue();
                    columnField.setAccessible(true);

                    int columnIndex = cursor.getColumnIndex(columnName);
                    Class fieldType = columnField.getType();

                    if (columnIndex != -1) {
                        if (fieldType == String.class) {
                            columnField.set(item, cursor.getString(columnIndex));
                        } else if (fieldType == Integer.class) {
                            columnField.set(item, cursor.getInt(columnIndex));
                        } else if (fieldType == Double.class) {
                            columnField.set(item, cursor.getDouble(columnIndex));
                        } else if (fieldType == Long.class) {
                            columnField.set(item, cursor.getLong(columnIndex));
                        } else if (fieldType == byte[].class) {
                            columnField.set(item, cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                //添加到集合.
                list.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获取columnName 与 value 一一对应的数据集.
     *
     * @return
     */
    public Map<String, String> getValues(T entity) {
        Map<String, String> map = new HashMap<>();
        Iterator<Field> iterator = cacheMap.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.setAccessible(true);

            //获取列名
            String key = null;
            if (field.isAnnotationPresent(DbField.class)) {
                key = field.getAnnotation(DbField.class).value();
            } else {
                key = field.getName();
            }

            //获取字段值.
            try {
                Object obj = field.get(entity);
                if (obj == null) {
                    continue;
                }
                String value = obj.toString();

                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public ContentValues getContentValues(Map<String, String> map) {
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> me : map.entrySet()) {
            values.put(me.getKey(), me.getValue());
        }
        return values;
    }


    class Condition {
        private String whereClause;  //"name=? and password=?"
        private String[] whereArgs;  //new String[]{"tom","123456"}

        public Condition(T where) {
            Map<String, String> map = getValues(where);
            StringBuffer sb = new StringBuffer();
            List<String> list = new ArrayList<>();
            sb.append("1=1");
            for (Map.Entry<String, String> me : map.entrySet()) {
                String key = me.getKey();
                String value = me.getValue();
                sb.append(String.format(" and %s=?", key));
                list.add(value);
            }
            this.whereClause = sb.toString();
            this.whereArgs = list.toArray(new String[list.size()]);
        }
    }
}
