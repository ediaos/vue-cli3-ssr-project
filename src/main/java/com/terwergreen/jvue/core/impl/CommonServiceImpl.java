package com.terwergreen.jvue.core.impl;

import com.terwergreen.jvue.core.CommonDAO;
import com.terwergreen.jvue.core.CommonService;
import com.terwergreen.jvue.pojo.SiteConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公共服务
 *
 * @author Terwer
 * @version 1.0 2018/11/26 15:16
 **/
@SuppressWarnings("all")
@Service
public class CommonServiceImpl implements CommonService {
    private static final Log logger = LogFactory.getLog(CommonServiceImpl.class);

    private static final String SITE_CONFIG_KEY = "siteConfig";

    @Autowired
    private CommonDAO commonDAO;

    @Value("${jvue.web.admin-path}")
    private String adminPath;

    @Override
    public SiteConfig getSiteConfig() {
        SiteConfig siteConfig = null;
        try {
            Map paramMap = new HashMap();
            paramMap.put("optionGroup", SITE_CONFIG_KEY);
            siteConfig = (SiteConfig) commonDAO.querySingleByMap("getSiteConfig", paramMap);
            if (!siteConfig.getAdminpath().equals(adminPath)) {
                logger.info("从配置文件读取后台管理路径");
                siteConfig.setAdminpath(adminPath);
                // 更新数据库
                updateSiteConfig("adminPath", adminPath);
            }
        } catch (Exception e) {
            logger.error("获取配置项异常", e);
        }
        return siteConfig;
    }

    @Override
    public Object getSiteConfigItem(String optionName) {
        String result = null;
        try {
            if (optionName.equals("adminPath")) {
                logger.info("从配置文件读取后台管理路径");
                result = adminPath;
                // 更新数据库
                updateSiteConfig("adminPath", adminPath);
            } else {
                Map paramMap = new HashMap();
                paramMap.put("optionGroup", SITE_CONFIG_KEY);
                paramMap.put("optionName", optionName);
                Map resultMap = (Map) commonDAO.querySingleByMap("getOptionByGroup", paramMap);
                if (null != resultMap) {
                    result = (String) resultMap.get("optionValue");
                }
            }
        } catch (Exception e) {
            logger.error("获取配置项异常", e);
        }
        return result;
    }

    @Override
    public Object getOption(String optionGroup) {
        List list = null;
        try {
            Map paramMap = new HashMap();
            paramMap.put("optionGroup", optionGroup);
            list = commonDAO.queryListByMap("getOptionByGroup", paramMap);
        } catch (Exception e) {
            logger.error("获取配置项异常", e);
        }
        //多个结果返回List，只有一个结果的时候直接返回
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                return list;
            } else {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public boolean updateSiteConfig(String optionName, String newOptionValue) {
        return updateOption(optionName, newOptionValue, SITE_CONFIG_KEY);
    }

    @Override
    public boolean updateOption(String optionName, String optionValue, String optionGroup) {
        boolean result = false;
        try {
            Map paramMap = new HashMap();
            paramMap.put("optionName", optionName);
            paramMap.put("optionValue", optionValue);
            paramMap.put("optionGroup", optionGroup);
            int count = commonDAO.update("updateOptionByGroup", paramMap);
            if (count > 0) {
                result = true;
            }
        } catch (Exception e) {
            logger.error("获取站点配置异常", e);
        }
        return result;
    }
}
