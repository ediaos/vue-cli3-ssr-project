package com.terwergreen.jvue.api;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.terwergreen.jvue.pojo.Post;
import com.terwergreen.jvue.pojo.PostMeta;
import com.terwergreen.jvue.service.PostService;
import com.terwergreen.jvue.util.*;
import com.terwergreen.jvue.exception.RestException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文章相关API接口
 *
 * @author Terwer
 * @version 1.0
 * 2018/7/6 10:05
 **/
@Controller
@RequestMapping("api/blog")
public class PostApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Integer DEFAULT_HOT_NUM = 2;

    @Autowired
    private PostService postService;

    @RequestMapping(value = "/post/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getPosts(Model model, HttpServletRequest request, HttpServletResponse response, String postType, String postStatus, String search, Integer page, Integer limit) throws Exception {
        Map resultMap = new HashMap();

        if (page == null) {
            page = Constants.DEFAULT_PAGE_NUM;
        }
        if (limit == null) {
            limit = Constants.DEFAULT_PAGE_SIZE;
        }

        try {
            Map paramMap = new HashMap();
            if (!StringUtils.isEmpty(postType)) {
                paramMap.put("postType", postType);
            }
            if (!StringUtils.isEmpty(postStatus)) {
                paramMap.put("postStatus", postStatus);
            }
            if (!StringUtils.isEmpty(search)) {
                paramMap.put("search", search);
            }
            PageInfo<Post> posts = postService.getPostsByPage(page, limit, paramMap);
            resultMap.put("code", 0);
            resultMap.put("msg", "success");
            resultMap.put("count", posts.getTotal());
            resultMap.put("data", posts.getList());
        } catch (Exception e) {
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            resultMap.put("count", 0);
            resultMap.put("data", null);
            logger.error("系统异常" + e.getLocalizedMessage(), e);
            throw new RestException(e);
        }
        return JSON.toJSONString(resultMap);
    }

    @RequestMapping(value = "/post/hot", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getHotPosts(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map resultMap = new HashMap();
        try {
            Map paramMap = new HashMap();
            paramMap.put("isHot", 1);
            PageInfo<Post> posts = postService.getPostsByPage(Constants.DEFAULT_PAGE_NUM, DEFAULT_HOT_NUM, paramMap);
            resultMap.put("code", 0);
            resultMap.put("msg", "success");
            resultMap.put("count", posts.getTotal());
            resultMap.put("data", posts.getList());
        } catch (Exception e) {
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            resultMap.put("count", 0);
            resultMap.put("data", null);
            logger.error("系统异常" + e.getLocalizedMessage(), e);
            throw new RestException(e);
        }
        return JSON.toJSONString(resultMap);
    }

    @RequestMapping(value = "/post/essay", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getEssays(Model model, HttpServletRequest request, HttpServletResponse response, Integer page, Integer limit) throws Exception {
        Map resultMap = new HashMap();

        if (page == null) {
            page = Constants.DEFAULT_PAGE_NUM;
        }
        if (limit == null) {
            limit = Constants.DEFAULT_PAGE_SIZE;
        }

        try {
            Map paramMap = new HashMap();
            paramMap.put("postType", PostTypeEmum.POST_TYPE_ESSAY.getName());
            PageInfo<Post> posts = postService.getPostsByPage(page, limit, paramMap);

            //转换成说说需要的格式
            List<Map> timelines = new ArrayList<>();
            for (Post post : posts.getList()) {
                Integer postId = post.getPostId();
                Date postDate = post.getPostDate();
                String postTitle = post.getPostTitle();
                String postDesc = post.getPostDesc();
                String postContent = post.getPostContent();

                Map timelineMap = new HashMap();
                // SimpleDateFormat keySdf = new SimpleDateFormat("yyyyMMddHHmmss");
                // timelineMap.put("key", keySdf.format(postDate));
                timelineMap.put("key", postId);
                SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
                timelineMap.put("year", yearSdf.format(postDate));
                SimpleDateFormat titleSdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                timelineMap.put("title", titleSdf.format(postDate));
                if (postDesc.endsWith("...")) {
                    postContent = postDesc;
                }
                timelineMap.put("content", postContent);
                timelineMap.put("praise", post.getPraiseCount());
                timelines.add(timelineMap);
            }

            Map jsonMap = new HashMap();
            jsonMap.put("timelines", timelines);

            resultMap.put("code", 0);
            resultMap.put("msg", "success");
            resultMap.put("count", posts.getTotal());
            resultMap.put("data", jsonMap);
        } catch (Exception e) {
            resultMap.put("code", 0);
            resultMap.put("msg", "");
            resultMap.put("count", 0);
            resultMap.put("data", null);
            logger.error("系统异常" + e.getLocalizedMessage(), e);
            throw new RestException(e);
        }
        return JSON.toJSONString(resultMap);
    }

    @RequestMapping(value = "/post/new", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public RestResponse newPost(Model model, HttpServletRequest request, HttpServletResponse response,
                                Post post
    ) throws Exception {
        RestResponse restResponse = new RestResponse();
        try {
            //获得当前登陆用户对应的对象
            /*
            SysUserDTO sysUser = null;
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SysUserDTO) {
                sysUser = (SysUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                post.setPostAuthor(sysUser.getId());
            } else {
                post.setPostAuthor(1);
            }
            */
            post.setPostAuthor(1);
            if (StringUtils.isEmpty(post.getPostType())) {
                String postType = PostTypeEmum.POST_TYPE_ESSAY.getName();
                post.setPostType(postType);
            }
            if (StringUtils.isEmpty(post.getPostStatus())) {
                String postStatus = PostStatusEnum.POST_STATUS_PUBLISH.getName();
                post.setPostStatus(postStatus);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dtPostDate = sdf.parse(sdf.format(new Date()));
            if (null == post.getPostDate()) {
                post.setPostDate(dtPostDate);
            }
            if (StringUtils.isEmpty(post.getPostRawContent())) {
                logger.error("文章信息内容不能为空");
                restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
                restResponse.setMsg("文章信息内容不能为空");
            }
            logger.trace("准备新增文章，文章信息：" + JSON.toJSONString(post));
            Integer postId = postService.newPost(post);
            if (postId > 0) {
                logger.info("文章信息添加成功，postId：" + postId);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("postId", postId);
                restResponse.setData(resultMap);
                restResponse.setStatus(RestResponseStates.SUCCESS.getValue());
                restResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            } else {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("postId", 0);
                restResponse.setData(resultMap);
                restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
                restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            }
        } catch (Exception e) {
            logger.error("接口异常:error=", e);
            restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
            restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            throw new RestException(e);
        }
        return restResponse;
    }

    @RequestMapping(value = "/post/update", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public RestResponse updatePost(Model model, HttpServletRequest request, HttpServletResponse response, Post post) throws Exception {
        RestResponse RestResponse = new RestResponse();
        try {
            logger.trace("开始修改，Post=:" + JSON.toJSONString(post));
            boolean flag = postService.editPostById(post);
            if (flag) {
                logger.info("文章信息修改");
                RestResponse.setStatus(RestResponseStates.SUCCESS.getValue());
                RestResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            } else {
                RestResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
                RestResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            }
        } catch (Exception e) {
            logger.error("接口异常:error=", e);
            RestResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
            RestResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            throw new RestException(e);
        }
        return RestResponse;
    }

    @RequestMapping(value = "/post/delete/{postId}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public RestResponse deletePost(Model model, @PathVariable("postId") Integer postId) throws Exception {
        RestResponse restResponse = new RestResponse();
        try {
            boolean result = postService.deletePostById(postId);
            if (result) {
                restResponse.setStatus(RestResponseStates.SUCCESS.getValue());
                restResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            } else {
                restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
                restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            }
        } catch (Exception e) {
            logger.error("接口异常:error=", e);
            restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
            restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            throw new RestException(e);
        }
        return restResponse;
    }

    @RequestMapping(value = "/post/meta/{postId}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public RestResponse updatePostMeta(Model model, @PathVariable("postId") Integer postId, String metaKey, String metaValue) throws Exception {
        RestResponse restResponse = new RestResponse();
        try {
            PostMeta postMeta = new PostMeta();
            postMeta.setPostId(postId);
            postMeta.setMetaKey(metaKey);
            postMeta.setMetaValue(metaValue);
            boolean result = postService.saveOrUpdatePostMeta(postMeta);
            if (result) {
                restResponse.setStatus(RestResponseStates.SUCCESS.getValue());
                restResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            } else {
                restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
                restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            }
        } catch (Exception e) {
            logger.error("接口异常:error=", e);
            restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            throw new RestException(e);
        }
        return restResponse;
    }

    @RequestMapping("/post/{postSlug}")
    @ResponseBody
    public RestResponse getPostDetail(Model model, @PathVariable String postSlug) throws RestException {
        RestResponse restResponse = new RestResponse();
        try {
            Post post = null;
            if (StringUtils.isNumeric(postSlug)) {
                post = postService.getPostById(Integer.parseInt(postSlug));
            } else {
                post = postService.getPostBySlug(postSlug);
            }
            restResponse.setStatus(RestResponseStates.SUCCESS.getValue());
            restResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            restResponse.setData(post);
        } catch (Exception e) {
            logger.error("接口异常:error=", e);
            restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
            restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
            throw new RestException(e);
        }
        return restResponse;
    }
}
