package cn.com.egova.aptdemo;

import android.app.Activity;
import android.os.Bundle;

import cn.com.egova.apt.HelloWorld;
import cn.com.egova.apt.Router;
import cn.com.egova.hello_annotation.HelloAnnotation;
import cn.com.egova.hello_annotation.Route;

@HelloAnnotation("Hello World!")
@Route(path="/main/MainActivity")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HelloWorld.main(null);

        new Router().route("path");
    }
}
