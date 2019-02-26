package com.terwergreen.jvue.vendor.j2v8;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Console {
    private static final Log logger = LogFactory.getLog(Console.class);

    public void log(final String message) {
        logger.info("[INFO] " + message);
    }

    public void error(final String message) {
        logger.error("[ERROR] " + message);
    }
}
