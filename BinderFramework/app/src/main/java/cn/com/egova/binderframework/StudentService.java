package cn.com.egova.binderframework;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import cn.com.egova.binderframework.inter.StudentManagerImp;

/**
 * Created by yuanchao on 2018/11/25.
 */

public class StudentService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("service:", "--------------onBind---------------------");
        return new StudentManagerImp();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("service:", "--------------onUnBind---------------------");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("service:", "--------------onDestroy---------------------");
        super.onDestroy();
    }
}
