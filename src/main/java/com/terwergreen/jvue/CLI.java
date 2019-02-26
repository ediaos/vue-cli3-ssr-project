package com.terwergreen.jvue;

import com.alibaba.fastjson.JSON;
import com.terwergreen.jvue.vendor.j2v8.V8Context;
import com.terwergreen.jvue.vendor.j2v8.impl.V8ContextImpl;
import com.terwergreen.jvue.vendor.vue.VueRenderer;
import com.terwergreen.jvue.vendor.vue.impl.VueRendererImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CLI {
    private static final Log logger = LogFactory.getLog(CLI.class);

    /**
     * 运行命令
     * mvn -v && mvn compile && mvn exec:java
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        // 设置路由上下文
        Map<String, Object> httpContext = new HashMap<>();
        httpContext.put("url", "/");

        // 添加seo
        httpContext.put("title", "title");
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("keywords", "keywords");
        metaMap.put("description", "description");
        httpContext.put("meta", metaMap);

        // 渲染Vue
        VueRenderer vueRenderer = new VueRendererImpl();
        HttpServletRequest request = null;
        Map<String, Object> resultMap = vueRenderer.renderContentCLI(httpContext, request);
        logger.info("resultMap=>" + JSON.toJSONString(resultMap));
    }
}
