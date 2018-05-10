package com.example.daolib.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.example.daolib.annotation.DbField;
import com.example.daolib.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author : Administrator
 * @time : 17:13
 * @for :
 */
public class DbUtil {

    /**
     * 通过泛型类 获取表名
     *
     * @return 表名
     */
    public static <T> String getTableNameFromEntity(Class<T> entityClass) {
        String tableName;
        DbTable annotation = entityClass.getAnnotation(DbTable.class);
        if (annotation != null && !TextUtils.isEmpty(annotation.tableName())) {
            tableName = annotation.tableName();
        } else {
            tableName = entityClass.getSimpleName();
        }
        return tableName;
    }


    /**
     * 获取建表语句
     *
     * @param tableName     表名
     * @param fieldCacheMap 列名--成员变量列表
     * @return 建表语句
     */
    public static String getCreateSql(String tableName, HashMap<String, Field> fieldCacheMap) {
        //create table if not exits tableName( id INTEGER, name TEXT)
        StringBuffer buffer = new StringBuffer("create table if not exists ");
        buffer.append(tableName)
                .append("( ");
        Set<String> columns = fieldCacheMap.keySet();

        for (String column : columns) {
            Field field = fieldCacheMap.get(column);
            buffer.append(column)
                    .append(" ")
                    .append(getTypeForSql(field))
                    .append(",");
        }

        if (buffer.charAt(buffer.length() - 1) == ',') {
            buffer.deleteCharAt(buffer.length() - 1);
        }

        buffer.append(" )");
        Log.e("创建语句", buffer.toString());
        return buffer.toString();
    }

    /**
     * 初始化缓存表
     *
     * @param entityClass   存储的数据类型
     * @param fieldCacheMap 列名--成员变量列表
     */
    public static <T> void initCacheMap(Class<T> entityClass, HashMap<String, Field> fieldCacheMap) {
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String typeForSql = getTypeForSql(field);
            if (TextUtils.isEmpty(typeForSql)) {
                continue;
            }
            String columnName = getColumnName(field);
            fieldCacheMap.put(columnName, field);
        }
    }

    /**
     * @return 成员变量对应的列名
     */
    private static String getColumnName(Field field) {
        DbField annotation = field.getAnnotation(DbField.class);
        if (annotation != null && !TextUtils.isEmpty(annotation.value())) {
            return annotation.value();
        }
        return field.getName();
    }

    /**
     * @return 成员变量对应SQL的类型
     */
    private static String getTypeForSql(Field field) {
        Class type = field.getType();
        String typeForSql = null;
        if (type == Integer.class) {
            typeForSql = "INTEGER";
        } else if (type == Long.class) {
            typeForSql = "BIGINT";
        } else if (type == Double.class) {
            typeForSql = "DOUBLE";
        } else if (type == String.class) {
            typeForSql = "TEXT";
        } else if (type == Byte[].class) {
            typeForSql = "BLOB";
        } else if (type == Boolean.class) {
            typeForSql = "TEXT";
        }
        return typeForSql;
    }

    /**
     * 将实例转换成ContentValues
     *
     * @param entity        数据实例
     * @param fieldCacheMap 列名--成员变量列表
     * @return ContentValues
     */
    public static <T> ContentValues getContentValues(T entity, HashMap<String, Field> fieldCacheMap) {
        ContentValues contentValues = new ContentValues();
        for (String key : fieldCacheMap.keySet()) {
            Field field = fieldCacheMap.get(key);//成员变量
            field.setAccessible(true);//能取数据
            try {
                Object object = field.get(entity);//取数据
                if (object != null) {
                    contentValues.put(key, object.toString());//添加进去
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }

    /**
     * @param clz           数据泛型类
     * @param cursor        游标
     * @param fieldCacheMap 成员变量集合
     * @return 数据列表
     */
    public static <T> List<T> getListFromCursor(Class<T> clz, Cursor cursor, HashMap<String, Field> fieldCacheMap) {
        List<T> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                //获取一个新的实例
                T entity = clz.newInstance();
                //给成员变量赋值
                Set<String> keys = fieldCacheMap.keySet();
                for (String key : keys) {
                    int columnIndex = cursor.getColumnIndex(key);
                    if (columnIndex != -1) {
                        Field field = fieldCacheMap.get(key);
                        Class type = field.getType();
                        field.setAccessible(true);
                        if (type == Integer.class) {
                            field.set(entity, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(entity, cursor.getLong(columnIndex));
                        } else if (type == Double.class) {
                            field.set(entity, cursor.getDouble(columnIndex));
                        } else if (type == String.class) {
                            field.set(entity, cursor.getString(columnIndex));
                        } else if (type == Byte[].class) {
                            field.set(entity, cursor.getBlob(columnIndex));
                        } else if (type == Boolean.class) {
                            field.set(entity, cursor.getString(columnIndex).equals("true"));
                        }
                    }
                }
                //将实例添加进列表
                list.add(entity);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
