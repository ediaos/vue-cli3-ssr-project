package com.terwergreen.jvue.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.terwergreen.jvue.util.HtmlUtil;
import com.terwergreen.jvue.util.ImageUtil;
import com.terwergreen.jvue.util.MarkdownUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 文章
 */
public class Post {
    /**
     * 文章ID
     */
    @Getter
    @Setter
    private int postId;
    /**
     * 文章别名
     */
    @Getter
    @Setter
    private String postSlug;
    /**
     * 文章醉着
     */
    @Getter
    @Setter
    private Integer postAuthor;
    /**
     * 文章类型
     */
    @Getter
    @Setter
    private String postType;
    /**
     * 文章状态
     */
    @Getter
    @Setter
    private String postStatus;
    /**
     * 文章标题
     */
    @Getter
    private String postTitle;
    /**
     * 文章完整标题
     */
    @Getter
    private String postFullTitle;
    /**
     * 文章内容
     */
    @Getter
    private String postContent;
    /**
     * 原始内容，数据库不保存
     */
    @Getter
    @Setter
    private String postRawContent;
    /**
     * 文章包含的图片，数据库不保存
     */
    @Getter
    @Setter
    private List<String> thumbnails;
    /**
     * 文章摘要，数据库不保存
     */
    @Getter
    private String postDesc;
    /**
     * 发布时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")  //FastJson包使用注解
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  //格式化前台日期参数注解
    @Getter
    @Setter
    private Date postDate;

    @Getter
    @Setter
    private Date postModified;

    @Getter
    @Setter
    private Integer postFinished;

    @Getter
    @Setter
    private Integer commentCount;

    @Getter
    @Setter
    private List<PostMeta> postMetas;

    @Getter
    @Setter
    // private SysUserDTO sysUser;

    /**
     * 是否是最新发布的文章
     */
    private boolean newFlag;

    /**
     * 置顶
     */
    private PostMeta ding;

    /**
     * 查看次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer praiseCount;

    public void setPostTitle(String postTitle) {
        this.postFullTitle  = postTitle;
        this.postTitle = HtmlUtil.parseHtml(postTitle, 28);
    }

    public void setPostContent(String postContent) {
        this.postRawContent = postContent;
        this.postContent = MarkdownUtil.md2html(postContent);
        this.thumbnails = ImageUtil.getImgSrc(this.getPostContent());
        this.postDesc = HtmlUtil.parseHtml(this.getPostContent(), 120);
    }

    public boolean isNewFlag() {
        return postModified != null && DateUtils.isSameDay(postModified, new Date());
    }

    public PostMeta getDing() {
        if (null == postMetas) {
            return null;
        }
        PostMeta dingPostMeta = postMetas.stream().filter(x -> x.getMetaKey().equals("ding")).findAny().orElse(null);
        return dingPostMeta;
    }

    public Integer getViewCount() {
        if (null == postMetas) {
            return 0;
        }
        Integer count = Integer.parseInt(postMetas.stream().filter(x -> x.getMetaKey().equals("view_count")).map(x -> x.getMetaValue()).findFirst().orElse("0"));
        return count;
    }

    public Integer getPraiseCount() {
        if (null == postMetas) {
            return 0;
        }
        Integer count = Integer.parseInt(postMetas.stream().filter(x -> x.getMetaKey().equals("praise_count")).map(x -> x.getMetaValue()).findFirst().orElse("0"));
        return count;
    }
}
