package cn.bload.share.utils;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 2:46
 * @describe 类描述:
 */
public class StringUtil {
    /**
     * 取文本中间内容
     * @param str 原文本
     * @param leftStr 左边文本
     * @param rightStr 右边文本
     * @return
     */
    public static String getSubString(String str,String leftStr,String rightStr){
        int start = str.indexOf(leftStr) + leftStr.length();
        int end = str.indexOf(rightStr, start + 1);
        return str.substring(start, end);
    }
}
