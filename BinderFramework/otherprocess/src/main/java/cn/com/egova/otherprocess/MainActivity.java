package cn.com.egova.otherprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.com.egova.binderframework.bean.Student;
import cn.com.egova.binderframework.inter.IStudentManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText stuId;
    private Button query;
    private Button queryAll;
    private IStudentManager studentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initConn();
    }

    private void initView() {
        stuId = findViewById(R.id.stuId);
        query = findViewById(R.id.query);
        queryAll = findViewById(R.id.queryAll);

        query.setOnClickListener(this);
        queryAll.setOnClickListener(this);
    }

    private void initConn() {
        Intent intent = new Intent();
        intent.setAction("cn.com.egova.binderframework.StudentService");
        intent.setPackage("cn.com.egova.binderframework");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("connection:", "------------------------service connected--------------");
                studentManager = IStudentManager.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("disconnect:", name.toString());
            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.query) {
            String id = stuId.getText().toString().trim();
            if(TextUtils.isEmpty(id)){
                Toast.makeText(this, "请输入学员编号!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Student student = studentManager.getStudentById(id);
                if (student == null) {
                    Toast.makeText(this, "学员不存在!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, student.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (resId == R.id.queryAll) {
            try {
                List<Student> students = studentManager.getAllStudents();
                Toast.makeText(this, students.toString(), Toast.LENGTH_SHORT).show();
                Log.i("queryAll:", students.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
