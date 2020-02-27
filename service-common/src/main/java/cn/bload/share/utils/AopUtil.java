package cn.bload.share.utils;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 10:04
 * @describe 类描述:
 */
public class AopUtil {

    public static String parseVariable(String str, Map<String,Object> map){
        for (String key : map.keySet()) {
            str = str.replace("#" + key, "'" + map.get(key) + "'");
        }
        ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
        try {
            str = se.eval(str).toString();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return str;
    }
}
