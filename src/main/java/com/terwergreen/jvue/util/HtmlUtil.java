package com.terwergreen.jvue.util;

public class HtmlUtil {

    /**
     * 去除html标签，残缺不全也可以
     *
     * @param html
     * @return
     */
    private static String parseHtml(String html) {
        /*
         * <.*?>为正则表达式，其中的.表示任意字符，*?表示出现0次或0次以上，此方法可以去掉双头标签(双头针对于残缺的标签)
         * "<.*?"表示<尖括号后的所有字符，此方法可以去掉残缺的标签，及后面的内容
         * " "，若有多种此种字符，可用同一方法去除
         */
        html = html.replaceAll("<.*?>", "").replaceAll(" ", "");
        html = html.replaceAll("<.*?", "");
        //html = html.replaceAll("\\n","");
        return (html);
    }

    /**
     * 截取指定长度html
     *
     * @param html
     * @param length
     * @return
     */
    public static String parseHtml(String html, int length) {
        String allText = parseHtml(html);
        if (allText.length() < length) {
            return allText;
        }
        return allText.substring(0, length) + "...";
    }
}