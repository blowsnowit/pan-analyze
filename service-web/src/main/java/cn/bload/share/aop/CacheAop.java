package cn.bload.share.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import cn.bload.share.annotation.Cache;
import cn.bload.share.utils.AopUtil;
import cn.bload.share.utils.RedisOperator;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 8:54
 * @describe 类描述:
 */
@Component
@Aspect
public class CacheAop {
    @Autowired
    RedisOperator redisOperator;

    @Pointcut("@annotation(cn.bload.share.annotation.Cache)")
    public void pointcut(){}



    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Cache cache = method.getAnnotation(Cache.class);

        Parameter[] parameters = method.getParameters();
        Object[] args = point.getArgs();
        String key = cache.key();
        Map<String,Object> map = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            map.put(parameters[i].getName(),args[i]);
        }
        key = AopUtil.parseVariable(key,map);

        if (cache.check()){
            return redisOperator.exists(key);
        }

        if(redisOperator.exists(key)){
            return redisOperator.get(key);
        }
        Object proceed = point.proceed();
        redisOperator.set(key,proceed,cache.expire());
        return proceed;
    }
}
