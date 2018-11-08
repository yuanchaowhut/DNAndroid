package cn.com.egova.router.core.callback;


import cn.com.egova.router.core.Postcard;

/**
 */
public interface NavigationCallback {

    /**
     * 找到跳转页面
     *
     * @param postcard meta
     */
    void onFound(Postcard postcard);

    /**
     * 未找到
     *
     * @param postcard meta
     */
    void onLost(Postcard postcard);

    /**
     * 成功跳转
     *
     * @param postcard meta
     */
    void onArrival(Postcard postcard);

}
