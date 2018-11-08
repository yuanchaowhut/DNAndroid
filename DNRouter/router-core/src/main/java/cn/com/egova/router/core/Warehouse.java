package cn.com.egova.router.core;


import java.util.HashMap;
import java.util.Map;

import cn.com.egova.annotation.model.RouteMeta;
import cn.com.egova.router.core.template.IRouteGroup;
import cn.com.egova.router.core.template.IService;

/**
 * @author Lance
 * 3个map集合，用于保存路由信息表。
 * @date 2018/2/22
 */

public class Warehouse {

    // root 映射表 保存分组信息（key:组名 value:类名）
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据(key:组名 value:对应组的路由信息)
    static Map<String, RouteMeta> routes = new HashMap<>();

    // group 映射表 保存组中的所有数据(key:组名 value:对应组的路由信息)
    static Map<Class, IService> services = new HashMap<>();
    // TestServiceImpl.class , TestServiceImpl 没有再反射


}
