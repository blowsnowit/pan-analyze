package cn.bload.share.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import cn.bload.share.annotation.Limit;
import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.utils.IpUtil;
import cn.bload.share.utils.RedisOperator;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 8:54
 * @describe 类描述:
 */
@Component
@Aspect
public class LimitAop {
    @Autowired
    RedisOperator redisOperator;
    @Autowired
    HttpServletRequest request;


    @Pointcut("@annotation(cn.bload.share.annotation.Limit)")
    public void pointcut(){}



    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Limit limitAnnotation = method.getAnnotation(Limit.class);

//        Parameter[] parameters = method.getParameters();
//        Object[] args = point.getArgs();
//        Map<String,Object> map = new HashMap<>();
//        for (int i = 0; i < parameters.length; i++) {
//            map.put(parameters[i].getName(),args[i]);
//        }

        String key = limitAnnotation.key();
//        key = AopUtil.parseVariable(key,map);
        key = key + "" + IpUtil.getIpAddr(request);

        Integer count = 0;
        if (redisOperator.exists(key)){
            count = (Integer) redisOperator.get(key);
            if (count + 1 > limitAnnotation.count()){
                throw new MyRuntimeException(limitAnnotation.message());
            }
        }
        //计算是否超时
        redisOperator.set(key,count + 1,limitAnnotation.period());

        return point.proceed();
    }



}
