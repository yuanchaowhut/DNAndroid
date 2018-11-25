package cn.com.egova.hookframework.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.egova.hookframework.Const;
import cn.com.egova.hookframework.R;

/**
 * Created by Administrator on 2018/2/26 0026.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText name;
    private EditText password;
    private Button login;
    private String className;
    private SharedPreferences share;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);

        share = this.getSharedPreferences("user", MODE_PRIVATE);//实例化
        className = getIntent().getStringExtra("extraIntent");
        if (className != null) {
            ((TextView) findViewById(R.id.text)).setText(" 跳转至界面：" + className);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login) {
            login();
        }
    }

    public void login() {
        if ((name.getText() == null || password.getText() == null)) {
            Toast.makeText(this, "请填写用户名 或密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Const.MOCK_USER_NAME.equals(name.getText().toString()) && Const.MOCK_PASSWORD.equals(password.getText().toString())) {
            SharedPreferences share = getSharedPreferences(Const.SP_LOGGIN, MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();
            editor.putString(Const.USER_NAME, name.getText().toString());
            editor.putString(Const.PASSWORD, password.getText().toString());
            editor.putBoolean(Const.LOGIN_FLAG, true);   //设置保存的数据
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            editor.commit();    //提交数据保存

            if (className != null) {
                ComponentName componentName = new ComponentName(this, className);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
                finish();
            }
        } else {
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean(Const.LOGIN_FLAG, false);
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            editor.commit();
        }
    }
}
