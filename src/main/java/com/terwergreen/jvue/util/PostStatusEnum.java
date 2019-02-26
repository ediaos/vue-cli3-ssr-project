package com.terwergreen.jvue.util;

/**
 * 文章状态枚举
 */
public enum PostStatusEnum {
    /**
     * 正常发布状态
     */
    POST_STATUS_PUBLISH("publish", 1),
    /**
     * 草稿
     */
    POST_STATUS_DRAFT("draft", 2),
    /**
     * 回收站
     */
    POST_STATUS_TRASH("trash", 2);

    // 成员变量
    private String name;
    private int index;

    // 构造方法
    PostStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (PostStatusEnum p : PostStatusEnum.values()) {
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
