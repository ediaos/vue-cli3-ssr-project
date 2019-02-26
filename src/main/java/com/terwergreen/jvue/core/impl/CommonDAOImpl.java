package com.terwergreen.jvue.core.impl;

import com.terwergreen.jvue.core.CommonDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 通用DAO的实现
 *
 * @author Terwer
 * @version 1.0 2018/11/26 10:20
 **/
@SuppressWarnings("all")
@Repository
public class CommonDAOImpl implements CommonDAO {
    private static final Log logger = LogFactory.getLog(CommonDAOImpl.class);

    /**
     * 数据分页获取的最大数目
     */
    private static final int MAX_ROW = 9999;
    /**
     * 最大批量插入或者更新数目
     */
    private static final int MAX_COMMIT_SIZE = 1000;

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public List queryList(String sql) {
        return sqlSession.selectList(sql);
    }

    @Override
    public List queryListByString(String sql, String str) {
        return sqlSession.selectList(sql, str);
    }

    @Override
    public List queryListByMap(String sql, Map paraMap) {
        return sqlSession.selectList(sql, paraMap);
    }

    @Override
    public List queryListByObject(String sql, Object object) {
        return sqlSession.selectList(sql, object);
    }

    @Override
    public List queryPageList(String sql, Map paraMap, int start, int pageSize) {
        pageSize = pageSize > MAX_ROW ? MAX_ROW : pageSize;
        try {
            return sqlSession.selectList(sql, paraMap, new RowBounds(start - 1, pageSize));
        } catch (DataAccessException e) {
            logger.error("分页查询发生异常：", e);
        }
        return null;
    }

    @Override
    public List queryPageListByString(String sql, String str, int start, int pageSize) {
        pageSize = pageSize > MAX_ROW ? MAX_ROW : pageSize;
        try {
            return sqlSession.selectList(sql, str, new RowBounds(start - 1, pageSize));
        } catch (DataAccessException e) {
            logger.error("分页查询发生异常：", e);
        }
        return null;
    }

    @Override
    public List queryPageListByMap(String sql, Map paraMap, int start, int pageSize) {
        pageSize = pageSize > MAX_ROW ? MAX_ROW : pageSize;
        try {
            return sqlSession.selectList(sql, paraMap, new RowBounds(start - 1, pageSize));
        } catch (DataAccessException e) {
            logger.error("分页查询发生异常：", e);
        }
        return null;
    }

    @Override
    public List queryPageListByObject(String sql, Object object, int start, int pageSize) {
        pageSize = pageSize > MAX_ROW ? MAX_ROW : pageSize;
        try {
            return sqlSession.selectList(sql, object, new RowBounds(start - 1, pageSize));
        } catch (DataAccessException e) {
            logger.error("分页查询发生异常：", e);
        }
        return null;
    }

    @Override
    public Object querySingleByString(String sql) {
        List list = sqlSession.selectList(sql);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Object querySingleByMap(String sql, Map paraMap) {
        List list = sqlSession.selectList(sql, paraMap);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Object querySingleByString(String sql, String str) {
        List list = sqlSession.selectList(sql, str);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Object querySingleByObject(String sql, Object object) {
        List list = sqlSession.selectList(sql, object);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Object insert(String sql, Map paraMap) {
        return sqlSession.insert(sql, paraMap);
    }

    @Override
    public Object insertByObject(String sql, Object object) {
        return sqlSession.insert(sql, object);
    }

    @Override
    public int delete(String sql, Map paraMap) {
        return sqlSession.delete(sql, paraMap);
    }

    @Override
    public int deleteByObject(String sql, Object object) {
        return sqlSession.delete(sql, object);
    }

    @Override
    public boolean checkDelete(String sql, Map paraMap) {
        int row = this.delete(sql, paraMap);
        if (row > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkDeleteByObject(String sql, Object object) {
        int row = this.deleteByObject(sql, object);
        if (row > 0) {
            return true;
        }
        return false;
    }

    @Override
    public int update(String sql, Map paraMap) {
        return sqlSession.update(sql, paraMap);
    }

    @Override
    public int updateByObject(String sql, Object object) {
        return sqlSession.update(sql, object);
    }

    @Override
    public boolean checkUpdate(String sql, Map paraMap) {
        int row = this.update(sql, paraMap);
        if (row > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkUpdateByObject(String sql, Object object) {
        int row = this.updateByObject(sql, object);
        if (row > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void insertBatch(String sql, List insertList) {
        //新获取一个模式为BATCH，自动提交为false的session
        //如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            if (null != insertList || insertList.size() > 0) {
                int lsize = insertList.size();
                for (int i = 0, n = insertList.size(); i < n; i++) {
                    Object model = insertList.get(i);
                    session.insert(sql, model);
                    if ((i > 0 && i % MAX_COMMIT_SIZE == 0) || i == lsize - 1) {
                        // 手动每1000个一提交，提交后无法回滚
                        session.commit();
                        // 清理缓存，防止溢出
                        session.clearCache();
                    }
                }
            }

        } catch (Exception e) {
            // 没有提交的数据可以回滚
            session.rollback();
            logger.error(e.getLocalizedMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public void updateBatch(String sql, List updateList) {
        //新获取一个模式为BATCH，自动提交为false的session
        //如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        try {
            if (null != updateList || updateList.size() > 0) {
                int lsize = updateList.size();
                for (int i = 0, n = updateList.size(); i < n; i++) {
                    Object model = updateList.get(i);
                    session.update(sql, model);
                    if ((i > 0 && i % MAX_COMMIT_SIZE == 0) || i == lsize - 1) {
                        // 手动每1000个一提交，提交后无法回滚
                        session.commit();
                        // 清理缓存，防止溢出
                        session.clearCache();
                    }
                }
            }

        } catch (Exception e) {
            // 没有提交的数据可以回滚
            session.rollback();
            logger.error(e.getLocalizedMessage());
        } finally {
            session.close();
        }
    }
}


