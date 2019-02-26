package com.terwergreen.jvue.util;

/**
 * 文章类型枚举
 */
public enum PostTypeEmum {
    /**
     * 文章
     */
    POST_TYPE_POST("post", 1),
    /**
     * 随笔
     */
    POST_TYPE_ESSAY("essay", 2),
    /**
     * 页面
     */
    POST_TYPE_PAGE("page", 3),
    /**
     * 笔记
     */
    POST_TYPE_NOTE("note", 3);

    // 成员变量
    private String name;
    private int index;

    // 构造方法
    PostTypeEmum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (PostTypeEmum p : PostTypeEmum.values()) {
            if (p.getIndex() == index) {
                return p.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}