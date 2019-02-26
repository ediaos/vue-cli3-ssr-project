package com.terwergreen.jvue.vendor.j2v8.impl;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.terwergreen.jvue.util.SystemUtil;
import com.terwergreen.jvue.vendor.j2v8.Console;
import com.terwergreen.jvue.vendor.j2v8.V8Context;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class V8ContextImpl implements V8Context {
    private final Log logger = LogFactory.getLog(this.getClass());
    private static V8 v8;
    private static NodeJS nodeJS;

    public V8ContextImpl() {
        long start = System.currentTimeMillis();
        logger.info("init v8Context finish");

        nodeJS = NodeJS.createNodeJS();
        String nodeVersion = nodeJS.getNodeVersion();
        logger.info("createNodeJS on " + SystemUtil.getSystemName());
        logger.info("createNodeJS finish:nodeVersion=>" + nodeVersion);

        v8 = nodeJS.getRuntime();
        v8.getLocker().acquire();
        registerConsole();
        v8.getLocker().release();
        logger.info("init v8 finish");

        long end = System.currentTimeMillis();
        logger.info("init V8Context cost time {" + (end - start) + "} ms");
    }

    private void registerConsole() {
        Console console = new Console();
        V8Object v8Console = new V8Object(v8);
        v8Console.registerJavaMethod(console, "log", "log", new Class<?>[]{String.class});
        v8Console.registerJavaMethod(console, "error", "error", new Class<?>[]{String.class});
        v8.add("console", v8Console);
        v8Console.release();
    }

    @Override
    public V8 getV8() {
        return v8;
    }

    @Override
    public NodeJS getNodeJS() {
        return nodeJS;
    }
}
