package cn.bload.share.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/1/19 17:24
 * @describe 类描述: 全局异常拦截，异常请抛出 MyRuntimeException
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MyRuntimeException.class)
    @ResponseBody
    public Map exceptionHandler(MyRuntimeException e){
        e.printStackTrace();

        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("message",e.getMessage());
        return map;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Map exceptionHandler(Exception e){
        e.printStackTrace();
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("message","系统异常");
        return map;
    }
}
