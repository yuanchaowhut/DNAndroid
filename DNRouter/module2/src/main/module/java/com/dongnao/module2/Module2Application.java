package cn.com.egova.module2;

import android.app.Application;

import cn.com.egova.router.core.DNRouter;

/**
 * Created by Administrator on 2018/3/21.
 */

public class Module2Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DNRouter.init(this);
    }
}
