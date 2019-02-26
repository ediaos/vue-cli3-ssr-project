package com.terwergreen.jvue.core;

import com.terwergreen.jvue.pojo.SiteConfig;

/**
 * @Author Terwer
 * @Date 2018/11/26 15:12
 * @Version 1.0
 * @Description 公共服务
 **/
public interface CommonService {
    /**
     * 获取站点配置
     *
     * @return
     */
    SiteConfig getSiteConfig();

    /**
     * 获取站点配置项
     *
     * @param optionName 配置名
     * @return
     */
    Object getSiteConfigItem(String optionName);

    /**
     * 获取属性
     *
     * @param optionGroup 属性组
     * @return
     */
    Object getOption(String optionGroup);

    /**
     * 更新单个站点配置项
     *
     * @param optionName  配置名
     * @param newOptionValue 配置值
     * @return
     */
    boolean updateSiteConfig(String optionName, String newOptionValue);

    /**
     * 更新属性
     *
     * @param optionName  配置名
     * @param optionGroup 配置值
     * @return
     */
    boolean updateOption(String optionName, String optionValue, String optionGroup);
}