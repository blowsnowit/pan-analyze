package cn.bload.share.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 9:49
 * @describe 类描述:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String key();

    //到期秒数 默认 39分钟 -1永不过期
    long expire() default 1800;

    //是否返回布尔检查key是否存在
    boolean check() default false;
}
