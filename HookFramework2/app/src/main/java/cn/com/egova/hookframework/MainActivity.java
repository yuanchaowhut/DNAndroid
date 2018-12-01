package cn.com.egova.hookframework;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ViewHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        holder = new ViewHolder(rootView);
        setContentView(rootView);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        String pkgName = "cn.com.egova.plugin";
        if (resId == R.id.jump2) {
//            jumpTo(SceondActivity.class);                                       //激活本app的SecondActivity
            jumpToPlugin(pkgName, pkgName + ".SceondActivity");         //激活插件的SecondActivity
        } else if (resId == R.id.jump3) {
//            jumpTo(ThreeActivity.class);
            jumpToPlugin(pkgName, pkgName + ".ThreeActivity");
        } else if (resId == R.id.jump4) {
//            jumpTo(ThirdActivity.class);
            jumpToPlugin(pkgName, pkgName + ".ThirdActivity");
        } else if (resId == R.id.logout) {
            logout();
        }
    }

    public void jumpTo(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void jumpToPlugin(String pkgName, String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkgName, className));  //包名+全类名
        startActivity(intent);
    }

    public void logout() {
        SharedPreferences share = this.getSharedPreferences(Const.SP_LOGGIN, MODE_PRIVATE);//实例化
        SharedPreferences.Editor editor = share.edit(); //使处于可编辑状态
        editor.putBoolean(Const.LOGIN_FLAG, false);   //设置保存的数据
        Toast.makeText(this, "退出登录成功", Toast.LENGTH_SHORT).show();
        editor.commit();    //提交数据保存
    }

    public class ViewHolder {
        View rootView;
        Button jump2;
        Button jump3;
        Button jump4;
        Button logout;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.jump2 = (Button) rootView.findViewById(R.id.jump2);
            this.jump3 = (Button) rootView.findViewById(R.id.jump3);
            this.jump4 = (Button) rootView.findViewById(R.id.jump4);
            this.logout = (Button) rootView.findViewById(R.id.logout);

            this.jump2.setOnClickListener(MainActivity.this);
            this.jump3.setOnClickListener(MainActivity.this);
            this.jump4.setOnClickListener(MainActivity.this);
            this.logout.setOnClickListener(MainActivity.this);
        }

    }
}
