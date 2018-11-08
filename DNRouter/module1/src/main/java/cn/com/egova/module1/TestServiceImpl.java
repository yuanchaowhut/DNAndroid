package cn.com.egova.module1;

import android.util.Log;

import cn.com.egova.annotation.Route;
import cn.com.egova.base.TestService;

/**
 * @author Lance
 * @date 2018/3/6
 */

@Route(path = "/module1/service")
public class TestServiceImpl implements TestService {


    @Override
    public void test() {
        Log.i("Service", "我是Module1模块测试服务通信");
    }

}

