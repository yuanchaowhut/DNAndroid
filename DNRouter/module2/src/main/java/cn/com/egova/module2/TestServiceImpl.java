package cn.com.egova.module2;

import android.util.Log;

import cn.com.egova.annotation.Route;
import cn.com.egova.base.TestService;


/**
 * @author Lance
 * @date 2018/3/6
 */

@Route(path = "/module2/service")
public class TestServiceImpl implements TestService {

    @Override
    public void test() {
        Log.i("Service", "我是Module2模块测试服务通信");
    }

}

