package com.terwergreen.jvue.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.terwergreen.jvue.pojo.Post;
import com.terwergreen.jvue.pojo.PostMeta;
import com.terwergreen.jvue.core.CommonDAO;
import com.terwergreen.jvue.core.CommonService;
import com.terwergreen.jvue.pojo.SiteConfig;
import com.terwergreen.jvue.service.PostService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.terwergreen.jvue.util.Constants.DEFAULT_PAGE_NUM;
import static com.terwergreen.jvue.util.Constants.DEFAULT_PAGE_SIZE;

/**
 * 文章相关API
 *
 * @author Terwer
 * @version 1.0
 * 2018/12/10 1:23
 **/
@Service
public class PostServiceImpl implements PostService {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private CommonDAO commonDAO;

    @Autowired
    private CommonService commonService;

    @Value("${jvue.admin.password.encode.enable}")
    private boolean isDbAdminPasswordEncoded;

    @Override
    public List<Post> getRecentPosts(Map paramMap) {
        List<Post> posts = getPostsByPage(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE, paramMap).getList();
        if (CollectionUtils.isEmpty(posts)) {
            logger.error("查询结果为空");
            return null;
        } else {
            logger.info("查询分页文章");
            return posts;
        }
    }

    /**
     * 查询单个文章
     *
     * @param slug 文章别名
     * @return 文章
     */
    @Override
    public Post getPostBySlug(String slug) {
        Map paramMap = new HashMap();
        paramMap.put("slug", slug);
        Post post = (Post) commonDAO.querySingleByMap("getPostBySlug", paramMap);
        return post;
    }

    @Override
    public Post getPostById(Integer postId) {
        Map paramMap = new HashMap();
        paramMap.put("postId", postId);
        Post post = (Post) commonDAO.querySingleByMap("getPostById", paramMap);
        return post;
    }

    @Override
    public PageInfo<Post> getPostsByPage(Integer pageNum, Integer pageSize, Map paramMap) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> list = (List<Post>) commonDAO.queryListByMap("getPostsByType", paramMap);
        // 分页信息
        PageInfo<Post> pageInfo = new PageInfo<>(list);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        if (pageNum > pages) {
            pageInfo.setList(new ArrayList<>());
        }
        pageNum = pageInfo.getPageNum();
        pageSize = pageInfo.getPageSize();
        logger.info("分页信息：total=" + total + "，pages=" + pages + "，pageNum=" + pageNum + "，pageSize=" + pageSize);
        return pageInfo;
    }

    @Override
    public List getUsersBlogs(String appkey, String username, String password) {
        //[{"blogName":"测试博客","xmlrpc":"http://localhost/wordpress/xmlrpc.php","isAdmin":true,"blogid":"1","url":"http://localhost/wordpress/"}]

        SiteConfig SiteConfig = commonService.getSiteConfig();
        List<Map> usersBlogs = new ArrayList<>();

        Map userBlog = new HashMap();
        userBlog.put("blogName", SiteConfig.getWebname());
        String url = SiteConfig.getWeburl();
        //userBlog.put("xmlrpc", url + XMLRPC_URL);
        //检测是否是管理员
        boolean isAdmin = false;
        // SysUserDTO sysUserDTO = (SysUserDTO) commonDAO.querySingleByString("selectByUserName", username);
        //SysUserDTO sysUserDTO= null;
        //if (sysUserDTO == null) {
        //    throw new UsernameNotFoundException("用户名不存在。");
        //}
        //if (isDbAdminPasswordEncoded) {
        //    password = jvueConfig.passwordEncoder().encode(sysUserDTO.getPassword());
        //}
        //hashed就是明文密码password加密后的结果，存储到数据库
        //String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        //candidate是明文密码，checkpw方法返回的是boolean
        //if (!BCrypt.checkpw(password, sysUserDTO.getPassword())) {
        //    throw new BusinessServiceException("密码错误。");
        //}
        //if (sysUserDTO == null || !BCrypt.checkpw(password, sysUserDTO.getPassword())) {
        //    // 用户名或密码不正确。
        //    throw new BusinessServiceException("用户名或密码不正确。");
        //}
        //for (SysRoleDTO role : sysUserDTO.getSysRoles()) {
        //    logger.info("role：" + role.getName());
        //    if ("ADMIN".equals(role.getName())) {
        //        isAdmin = true;
        //        break;
        //    }
        //}
        userBlog.put("isAdmin", isAdmin);
        //userBlog.put("blogid", sysUserDTO.getId());
        if (!url.endsWith("/")) {
            url += "/";
        }
        userBlog.put("url", url);

        usersBlogs.add(userBlog);
        return usersBlogs;
    }

    @Override
    public Integer newPost(Post post) {
        Integer postId = 0;
        int count = (int) commonDAO.insertByObject("insertPost", post);
        if (count > 0) {
            logger.info("新增文章成功");
            postId = post.getPostId();
        }
        return postId;
    }

    @Override
    public boolean editPostById(Post post) {
        int count = commonDAO.updateByObject("updatePost", post);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePostBySlug(String postSlug) {
        Map paramMap = new HashMap();
        paramMap.put("postSlug", postSlug);
        int count = commonDAO.delete("deletePostBySlug", paramMap);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePostById(Integer postId) {
        Map paramMap = new HashMap();
        paramMap.put("postId", postId);
        int count = commonDAO.delete("deletePostById", paramMap);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean saveOrUpdatePostMeta(PostMeta postMeta) {
        int count = commonDAO.updateByObject("saveOrUpdatePostMeta", postMeta);
        if (count > 0) {
            return true;
        }
        return false;
    }
}