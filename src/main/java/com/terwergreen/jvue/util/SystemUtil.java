package com.terwergreen.jvue.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemUtil {
    private static final Log logger = LogFactory.getLog(SystemUtil.class);

    /**
     * 判断是linux系统还是其他系统
     * 如果是Linux系统，返回true，否则返回false
     */
    public static boolean isLinux() {
        String osName = getSystemName();
        String osShortName = osName.toLowerCase();
        logger.info("osShortName=>" + osShortName);
        return osShortName.contains("linux");
    }

    /**
     * 获取系统名称
     *
     * @return 系统名称
     */
    public static String getSystemName() {
        String osName = System.getProperty("os.name");
        logger.info("osName=>" + osName);
        return osName;
    }
}
