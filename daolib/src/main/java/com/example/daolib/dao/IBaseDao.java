package com.example.daolib.dao;

import java.util.List;

/**
 * @author : Administrator
 * @time : 16:41
 * @for : Dao层功能接口
 */
public interface IBaseDao<T> {

    /**
     * 插入
     */
    long insert(T entity);

    /**
     * 更新
     *
     * @param entity 更新的数据
     * @param where  条件
     */
    int update(T entity, T where);

    /**
     * 删除
     *
     * @param where 删除条件
     */
    int delete(T where);

    /**
     * 查询
     *
     * @param where 查询条件
     * @return 数据列表
     */
    List<T> query(T where);

    /**
     * 查询
     * @param where     查询条件
     * @param groupBy   分组条件
     * @param orderBy   排序条件
     * @return  查询结果列表
     */
    List<T> query(T where, String groupBy,
                  String orderBy);

}
