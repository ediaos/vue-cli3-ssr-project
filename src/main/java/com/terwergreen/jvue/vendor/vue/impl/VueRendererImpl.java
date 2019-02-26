package com.terwergreen.jvue.vendor.vue.impl;

import com.alibaba.fastjson.JSON;
import com.eclipsesource.v8.*;
import com.terwergreen.jvue.vendor.j2v8.V8Context;
import com.terwergreen.jvue.vendor.j2v8.impl.V8ContextImpl;
import com.terwergreen.jvue.vendor.vue.VueRenderer;
import com.terwergreen.jvue.vendor.vue.VueUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端渲染Vue
 *
 * @author Terwer
 * @version 1.0
 * 2019/2/1 11:29
 **/
@Service
public class VueRendererImpl implements VueRenderer {
    private final Log logger = LogFactory.getLog(this.getClass());
    // 是否显示错误到浏览器
    private static final Integer SHOW_SERVER_ERROR = 1;
    // 最长等待时间
    private static final Integer MAX_WAIT_SECONDS = 2;
    private V8Context v8Context;
    private V8 v8;
    private NodeJS nodeJS;

    private final Object callbackLock = new Object();
    private volatile boolean callbackResolved = false;

    private Map<String, Object> htmlMap = new HashMap<>();

    private void initNodeJS() {
        v8Context = new V8ContextImpl();
        // 初始化v8和nodejs
        logger.info("初始化v8和nodejs...");
        v8 = v8Context.getV8();
        nodeJS = v8Context.getNodeJS();
        v8.getLocker().acquire();
        logger.info("initNodeJS 获取v8线程锁...");

        // 全局设置渲染模式并且处理promise异常
        v8.executeScript("" +
                "" +
                "process.env.SSR_ENV = 'ssrs';" +
                "process.env.VUE_ENV = 'server';" +
                "process.env.NODE_ENV = 'production';" +
                "process.on('unhandledRejection', function(reason, p) {" +
                "  console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);" +
                "});");

        v8.getLocker().release();
        logger.info("initNodeJS 释放v8线程锁...");
    }

    private void runMessageLoop() {
        boolean isRunning = nodeJS.isRunning();
        logger.info("nodeJS isRunning:" + isRunning);
        while (nodeJS.isRunning()) {
            nodeJS.handleMessage();
            logger.info("nodeJS is handling message...");
        }
    }

    private void executeV8(Map<String, Object> httpContext, HttpServletRequest request) {
        try {
            // render html
            executeV8CLI(httpContext, request);

            int i = 0;
            int jsWaitTimeout = 1000 * MAX_WAIT_SECONDS;
            int interval = 200; // 等待时间间隔
            int totalWaitTime = 0; // 实际等待时间

            if (!callbackResolved) {
                while (!callbackResolved && totalWaitTime < jsWaitTimeout) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        logger.error("Thread error:", e);
                    }
                    totalWaitTime = totalWaitTime + interval;
                    if (interval < 500) interval = interval * 2;
                    i = i + 1;
                }

                if (!callbackResolved) {
                    logger.error("time is out");
                } else {
                    logger.info("cost time to resolve:" + totalWaitTime);
                }
            } else {
                logger.info("promise already resolved");
            }
        } catch (Exception e) {
            logger.error("Vue executeV8 error:", e);
        }
        logger.info("entry-server.js执行完成");
    }

    private void executeV8CLI(Map<String, Object> httpContext, HttpServletRequest request) {
        try {
            initNodeJS();

            v8.getLocker().acquire();
            logger.info("executeV8CLI 获取v8线程锁...");

            v8.executeScript("console.log('v8 execute start')");

            // 注册回调函数
            JavaVoidCallback successCallback = (V8Object receiver, V8Array parameters) -> {
                synchronized (callbackLock) {
                    if (parameters.length() > 0) {
                        if (parameters.length() == 2) {
                            callbackResolved = true;
                            String html = parameters.getString(1);
                            htmlMap.put("status", 1);
                            htmlMap.put("data", html);
                            htmlMap.put("msg", "200 OK");
                            logger.info("renderServerCallback resolved success");
                            return;
                        }

                        // handle error
                        String err = parameters.toString();
                        htmlMap.put("status", 0);
                        htmlMap.put("data", err);
                        htmlMap.put("msg", "{}");
                    }
                    logger.info("renderServerCallback invoked");
                }
            };
            v8.registerJavaMethod(successCallback, "renderServerCallback");
            logger.info("renderServerCallback注册成功");

            JavaVoidCallback setSessionCallback = (V8Object receiver, V8Array parameters) -> {
                if (parameters.length() == 2) {
                    String key = parameters.getString(0);
                    String value = parameters.getString(1);
                    // set session to java server
                    request.getSession().setAttribute(key, value);
                    logger.info("key=>" + key);
                    logger.info("value=>" + value);
                } else {
                    logger.error("setSessionCallback参数错误");
                }
            };
            v8.registerJavaMethod(setSessionCallback, "setSessionCallback");
            logger.info("setSessionCallback注册成功");

            JavaCallback getSessionCallback = (V8Object receiver, V8Array parameters) -> {
                String value = null;
                if (parameters.length() == 1) {
                    String key = parameters.getString(0);
                    logger.info("getSessionCallback,key=>" + key);
                    value = (String) request.getSession().getAttribute(key);
                } else {
                    logger.error("getSessionCallback参数错误");
                    value = "parameter error";
                }
                return value;
            };
            v8.registerJavaMethod(getSessionCallback, "getSessionCallback");
            logger.info("getSessionCallback注册成功");

            // require server module
            File serverFile = VueUtil.readVueFile("server.js");
            V8Object server = nodeJS.require(serverFile);

            // get parameters
            logger.info("httpContext=>" + JSON.toJSONString(httpContext));
            V8Array parameters = new V8Array(v8);
            parameters.push(JSON.toJSONString(httpContext));
            // get renderServerCallback
            V8Function renderServerCallback = (V8Function) v8.getObject("renderServerCallback");
            parameters.push(renderServerCallback);

            // execute renderServer
            server.executeObjectFunction("renderServer", parameters);
            runMessageLoop();
            // =====================================================================

            v8.getLocker().release();
            logger.info("executeV8CLI 释放v8线程锁...");
        } catch (Exception e) {
            logger.error("Vue executeV8CLI error:", e);
        }
        logger.info("entry-server.js执行完成");
    }

    /**
     * 通用的渲染方法
     *
     * @param httpContext 上下文
     * @param isCLI       是否命令行
     * @return 服务端html及对应状态
     */
    private Map<String, Object> renderContent(Map<String, Object> httpContext, HttpServletRequest request, boolean isCLI) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rnd", System.currentTimeMillis());
        resultMap.put("showError", SHOW_SERVER_ERROR);
        logger.info("服务端调用renderServer前，设置路由上下文context:" + JSON.toJSONString(httpContext));
        try {
            if (isCLI) {
                executeV8CLI(httpContext, request);
            } else {
                // executeV8 already invokes executeV8CLI
                executeV8(httpContext, request);
            }

            // 处理返回结果
            if (!callbackResolved && htmlMap.size() == 0) {
                String errorMessage = "<h1>Server render error,Timed out more than " + MAX_WAIT_SECONDS + " seconds...</h1>";
                logger.error(errorMessage);
                resultMap.put("renderStatus", 0);
                resultMap.put("content", errorMessage);
                return resultMap;
            }

            logger.info("renderServer获取数据成功");
            // logger.debug("htmlMap:" + htmlMap);

            Integer renderStatus = Integer.parseInt(htmlMap.get("status").toString());
            String content = String.valueOf(htmlMap.get("data"));
            String message = String.valueOf(htmlMap.get("msg"));
            resultMap.put("renderStatus", renderStatus);
            resultMap.put("content", content);
            resultMap.put("message", message);
        } catch (Exception e) {
            resultMap.put("renderStatus", 0);
            resultMap.put("content", "failed to render vue component");
            logger.error("failed to render vue component", e);
        }
        return resultMap;
    }

    // ===============================
    // implementations
    // ===============================
    @Override
    public Map<String, Object> renderContentCLI(Map<String, Object> httpContext, HttpServletRequest request) {
        return renderContent(httpContext, request, true);
    }

    @Override
    public Map<String, Object> renderContent(Map<String, Object> httpContext, HttpServletRequest request) {
        return renderContent(httpContext, request, false);
    }
}
