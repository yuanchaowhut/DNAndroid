package cn.com.egova.hello_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuanchao on 2018/11/2.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Route {
    /**
     * 路由的路径，标识一个路由节点
     */
    String path();

    /**
     * 将路由节点进行分组，可以实现按组动态加载
     */
    String group() default "";
}
