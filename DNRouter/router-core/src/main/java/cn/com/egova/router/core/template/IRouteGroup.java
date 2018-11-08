package cn.com.egova.router.core.template;


import java.util.Map;

import cn.com.egova.annotation.model.RouteMeta;

/**
 * @author Lance
 * @date 2018/2/22
 */

public interface IRouteGroup {

    void loadInto(Map<String, RouteMeta> atlas);
}
