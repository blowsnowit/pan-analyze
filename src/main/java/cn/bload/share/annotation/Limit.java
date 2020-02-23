package cn.bload.share.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 8:39
 * @describe 类描述:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    String key();

    // 时间的，单位秒
    long period();

    // 限制访问次数
    int count();

    String message() default "访问过快，请稍后重试";
}
