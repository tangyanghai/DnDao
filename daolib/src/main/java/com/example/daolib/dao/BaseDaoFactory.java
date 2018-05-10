package com.example.daolib.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * @author : Administrator
 * @time : 18:00
 * @for :
 */
public class BaseDaoFactory {
    private SQLiteDatabase sqLiteDatabase;
    //定义建数据数据的路径
    //建议写到SD卡中，好处，APP让删除了，下次再安装的时候，数据还在
    private String sqliteDatabasePath;
    //数据库连接池
    public HashMap<String, BaseDao> daoCacheMap;
    //数据库所在文件夹
    private final File daoDir;

    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return ourInstance;
    }

    protected BaseDaoFactory() {
        //数据库文件夹
        daoDir = new File("data/data/com.example.administrator.androidstuct","dao");
        if (!daoDir.exists()) {
            daoDir.mkdirs();
        }
        //共有库路径
        sqliteDatabasePath = daoDir.getAbsolutePath()+"/my.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
        daoCacheMap = new HashMap<>();
    }

    /**
     * 获取共有数据库
     * @param entityClass
     */
    public synchronized <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDao;
    }

    /**
     * 获取分库
     * @param daoClass 分库的数据类型的类
     * @param subFilePath 指定文件夹,例如qq号:772323443,分库就存在这个文件夹下,项目中再针对此路径进行二次封装,然后就可以适应任何项目了
     * @return 一个私有库
     */
    public <T> BaseDao<T> getSubDao(Class<T> daoClass,String subFilePath){
        //分库在缓存中的key
        String key = subFilePath+daoClass.getSimpleName();
        BaseDao baseDao = daoCacheMap.get(key);
        if (baseDao!=null) {
            return baseDao;
        }
        //分库的文件夹
        File file = new File(daoDir,subFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //分库路径
        String path = file.getAbsolutePath()+"/private.db";
        //打开或创建分库
        SQLiteDatabase subSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(path,null);
        try {
            //创建表
            baseDao = BaseDao.class.newInstance();
            boolean init = baseDao.init(subSqliteDatabase, daoClass);
            if (init) {
                daoCacheMap.put(key,baseDao);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }



}
