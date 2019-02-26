package com.terwergreen.jvue.core;

import java.util.List;
import java.util.Map;

/**
 * @Author Terwer
 * @Date 2018/3/31 15:07
 * @Version 1.0
 * @Description 提供公共数据库增删改查
 **/
public interface CommonDAO {
    /**
     * 查询列表
     *
     * @param sql
     * @return
     */
    List queryList(String sql);

    /**
     * 查询列表
     *
     * @param sql
     * @param str
     * @return List
     */
    List queryListByString(String sql, String str);

    /**
     * 查询列表
     *
     * @param sql
     * @param paraMap
     * @return List
     */
    List queryListByMap(String sql, Map paraMap);

    /**
     * 查询列表
     *
     * @param sql
     * @param object
     * @return List
     */
    List queryListByObject(String sql, Object object);

    /**
     * 分页查询
     *
     * @param sql
     * @param paraMap  查询条件
     * @param start    起始条目
     * @param pageSize 每页显示条目
     * @return
     */
    List queryPageList(String sql, Map paraMap, int start, int pageSize);

    /**
     * 分页查询
     *
     * @param sql
     * @param str      查询条件
     * @param start    起始条目
     * @param pageSize 每页显示条目
     * @return
     */
    List queryPageListByString(String sql, String str, int start, int pageSize);

    /**
     * 分页查询
     *
     * @param sql      查询条件
     * @param paraMap
     * @param start    起始条目
     * @param pageSize 每页显示条目
     * @return
     */
    List queryPageListByMap(String sql, Map paraMap, int start, int pageSize);

    /**
     * 分页查询
     *
     * @param sql
     * @param object   查询条件
     * @param start    起始条目
     * @param pageSize 每页显示条目
     * @return
     */
    List queryPageListByObject(String sql, Object object, int start, int pageSize);

    /**
     * 查询单个信息
     *
     * @param sql
     * @return
     */
    Object querySingleByString(String sql);


    /**
     * 查询单个信息
     *
     * @param sql
     * @param str
     * @return Object
     */
    Object querySingleByString(String sql, String str);

    /**
     * 查询单个信息
     *
     * @param sql
     * @param paraMap
     * @return Object
     */
    Object querySingleByMap(String sql, Map paraMap);

    /**
     * 查询单个信息
     *
     * @param sql
     * @param object
     * @return Object
     */
    Object querySingleByObject(String sql, Object object);

    /**
     * 新增
     *
     * @param sql
     * @param paraMap
     * @return Object
     */
    Object insert(String sql, Map paraMap);

    /**
     * 新增
     *
     * @param sql
     * @param object
     * @return Object
     */
    Object insertByObject(String sql, Object object);

    /**
     * 删除
     *
     * @param sql
     * @param paraMap
     * @return int
     */
    int delete(String sql, Map paraMap);

    /**
     * 删除
     *
     * @param sql
     * @param object
     * @return int
     */
    int deleteByObject(String sql, Object object);

    /**
     * 检核删除是否成功
     *
     * @param sql
     * @param paraMap
     * @return true:成功  false:失败
     */
    boolean checkDelete(String sql, Map paraMap);

    /**
     * 检核删除是否成功
     *
     * @param sql
     * @param object
     * @return true:成功  false:失败
     */
    boolean checkDeleteByObject(String sql, Object object);

    /**
     * 更新
     *
     * @param sql
     * @param paraMap
     * @return int
     */
    int update(String sql, Map paraMap);

    /**
     * 更新
     *
     * @param sql
     * @param object
     * @return int
     */
    int updateByObject(String sql, Object object);

    /**
     * 检核更新是否成功
     *
     * @param sql
     * @param paraMap
     * @return true:成功  false:失败
     */
    boolean checkUpdate(String sql, Map paraMap);

    /**
     * 检核更新是否成功
     *
     * @param sql
     * @param object
     * @return true:成功  false:失败
     */
    boolean checkUpdateByObject(String sql, Object object);

    /**
     * 批量新增
     *
     * @param sql
     * @param insertList
     * @return
     */
    void insertBatch(String sql, List insertList);

    /**
     * 批量更新
     *
     * @param sql
     * @param updateList
     * @return
     */
    void updateBatch(String sql, List updateList);
}