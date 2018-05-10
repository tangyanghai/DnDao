package com.example.daolib.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * @author : Administrator
 * @time : 18:00
 * @for :
 */
public class DaoFactory {
    private SQLiteDatabase sqLiteDatabase;
    //定义建数据数据的路径
    //建议写到SD卡中，好处，APP让删除了，下次再安装的时候，数据还在
    private String sqliteDatabasePath;
    //数据库连接池
    private HashMap<String, BaseDao> daoCacheMap;

    private static final DaoFactory ourInstance = new DaoFactory();

    public static DaoFactory getInstance() {
        return ourInstance;
    }

    private DaoFactory() {
        //可以先判断有没有SD卡
        sqliteDatabasePath = "data/data/com.example.administrator.androidstuct/my.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
        daoCacheMap = new HashMap<>();
    }

    public <T> BaseDao getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = daoCacheMap.get(entityClass.getSimpleName());
        if (baseDao == null) {
            try {
                baseDao = BaseDao.class.newInstance();
                boolean init = baseDao.init(sqLiteDatabase, entityClass);
                if (init) {
                    daoCacheMap.put(entityClass.getSimpleName(), baseDao);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return baseDao;
    }
}
