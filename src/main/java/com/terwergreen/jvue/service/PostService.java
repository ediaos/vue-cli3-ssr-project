package com.terwergreen.jvue.service;

import com.github.pagehelper.PageInfo;
import com.terwergreen.jvue.pojo.Post;
import com.terwergreen.jvue.pojo.PostMeta;

import java.util.List;
import java.util.Map;

/**
 * 文章相关API
 *
 * @author Terwer
 * @version 1.0
 * 2018/12/10 1:23
 **/
public interface PostService {
    /**
     * 查询最新文章
     *
     * @param paramMap 筛选条件
     * @return 最新文章
     */
    List<Post> getRecentPosts(Map paramMap);

    /**
     * 查询单个文章
     *
     * @param slug 文章别名
     * @return 单个文章
     */
    Post getPostBySlug(String slug);

    /**
     * 查询单个文章
     *
     * @param postId 文章ID
     * @return 单个文章
     */
    Post getPostById(Integer postId);

    /**
     * 获取分页数据
     *
     * @param pageNum  页码
     * @param pageSize 数目
     * @param paramMap 筛选条件
     * @return 分页数据
     */
    PageInfo<Post> getPostsByPage(Integer pageNum, Integer pageSize, Map paramMap);

    /**
     * 获取博客信息
     *
     * @param appkey   appkey
     * @param username 用户名
     * @param password 密码
     * @return 博客信息
     */
    List getUsersBlogs(String appkey, String username, String password);

    /**
     * 新建文章
     *
     * @param post 文章
     * @return 新文章的ID
     */
    Integer newPost(Post post);

    /**
     * 更新文章
     *
     * @param post 文章
     * @return 是否修改成功
     */
    boolean editPostById(Post post);

    /**
     * 根据别名删除文章
     *
     * @param postSlug 文章别名
     * @return 是否删除成功
     */
    boolean deletePostBySlug(String postSlug);

    /**
     * 根据ID删除文章
     *
     * @param postId 文章ID
     * @return 是否删除成功
     */
    boolean deletePostById(Integer postId);

    /**
     * 新增或者修改文章属性
     *
     * @param postMeta 文章属性
     * @return 修改结果
     */
    boolean saveOrUpdatePostMeta(PostMeta postMeta);
}