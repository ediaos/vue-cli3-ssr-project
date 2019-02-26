package com.terwergreen.jvue.pojo;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

/**
 * 文章属性
 *
 * @author Terwer
 * @version 1.0
 * 2018/11/15 20:28
 **/
@Getter
@Setter
public class PostMeta {
    private Integer postId;
    private String metaKey;
    private String metaValue;
    private Integer count;
}
