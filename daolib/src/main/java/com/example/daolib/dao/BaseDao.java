package com.example.daolib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.daolib.util.DbUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author : Administrator
 * @time : 16:42
 * @for : 到层基类
 */
public class BaseDao<T> implements IBaseDao<T> {
    /**
     * 数据库操作类
     */
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 存储到数据库数据的类型
     */
    private Class<T> entityClass;
    /**
     * 表名
     */
    private String tableName;

    /**
     * 标识--是否已经进行过初始化
     */
    private boolean isInit = false;

    /**
     * 列名--成员变量缓存表
     */
    private HashMap<String, Field> fieldCacheMap;

    /**
     * 初始化
     * @param sqLiteDatabase 数据库连接
     * @param entityClass    数据类型
     * @return               true = 初始化成功
     */
    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        if (!isInit) {
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            this.sqLiteDatabase = sqLiteDatabase;
            this.entityClass = entityClass;
            //获取表名
            tableName = DbUtil.getTableNameFromEntity(entityClass);
            //缓存列名--成员变量,列名--SQL类型--避免以后再次使用的时候需要再次反射
            fieldCacheMap = new HashMap<>();
            DbUtil.initCacheMap(entityClass, fieldCacheMap);
            //获取建表语句
            String createSql = DbUtil.getCreateSql(tableName, fieldCacheMap);
            //创建表
            sqLiteDatabase.execSQL(createSql);
            isInit = true;
        }
        return isInit;
    }

    @Override
    public long insert(T entity) {
        long insert;
        ContentValues values = DbUtil.getContentValues(entity, fieldCacheMap);
        insert = sqLiteDatabase.insert(tableName, null, values);
        return insert;
    }

    @Override
    public int update(T entity, T where) {
        int reuslt;
        ContentValues values = DbUtil.getContentValues(entity, fieldCacheMap);
        Contention contention = new Contention(where);
        reuslt = sqLiteDatabase.update(tableName, values, contention.whereClause, contention.whereArgs);
        return reuslt;
    }

    @Override
    public int delete(T where) {
        int result;
        Contention contention = new Contention(where);
        result= sqLiteDatabase.delete(tableName, contention.whereClause, contention.whereArgs);
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null);
    }

    @Override
    public List<T> query(T where, String groupBy, String orderBy) {
        Contention contention = new Contention(where);
        Cursor cursor = sqLiteDatabase.query(tableName, null, contention.whereClause, contention.whereArgs, groupBy, null, orderBy);
        List<T> list = DbUtil.getListFromCursor(entityClass,cursor,fieldCacheMap);
        return list;
    }


    /**
     * 将条件转换成 whereClause 和 whereArgs的帮助类
     * {@link SQLiteDatabase#update(String, ContentValues, String, String[])}
     */
    private class Contention {
        String whereClause;
        String[] whereArgs;
        private Contention(T where) {
            //whereClause--> id=? and name=?
            StringBuffer clauseBuffer = new StringBuffer("1 = 1");
            List<String> list = new ArrayList<String>();
            Set<String> keys = fieldCacheMap.keySet();
            for (String key : keys) {
                Field field = fieldCacheMap.get(key);
                field.setAccessible(true);
                try {
                    Object obj = field.get(where);
                    if (obj != null) {
                        clauseBuffer.append(" and ")
                                .append(key)
                                .append(" = ?");
                        list.add(obj.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            whereClause = clauseBuffer.toString();
            whereArgs = list.toArray(new String[]{});
        }
    }

}
