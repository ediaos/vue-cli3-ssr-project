package com.terwergreen.jvue.api;

import com.terwergreen.jvue.core.CommonService;
import com.terwergreen.jvue.pojo.SiteConfig;
import com.terwergreen.jvue.util.RestResponse;
import com.terwergreen.jvue.util.RestResponseStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 站点配置API接口
 *
 * @author Terwer
 * @version 1.0
 * 19-2-25 下午5:59
 **/
@Controller
@RequestMapping("api/site/config")
public class SiteConfigApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public RestResponse getPosts() {
        RestResponse restResponse = new RestResponse();
        try {
            SiteConfig siteConfig = commonService.getSiteConfig();
            restResponse.setStatus(RestResponseStates.SUCCESS.getValue());
            restResponse.setMsg(RestResponseStates.SUCCESS.getMsg());
            restResponse.setData(siteConfig);
        } catch (Exception e) {
            logger.error("系统异常" + e.getLocalizedMessage(), e);
            restResponse.setStatus(RestResponseStates.SERVER_ERROR.getValue());
            restResponse.setMsg(RestResponseStates.SERVER_ERROR.getMsg());
        }
        return restResponse;
    }
}
