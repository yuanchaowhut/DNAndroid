package cn.com.egova.taopiaopiao;

import android.util.Log;

/**
 * Created by yuanchao on 2018/11/21.
 */

public class OneService extends BaseService {
    private static final String TAG = "OneService";
    private int i = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.i(TAG, (i++) + "=============================");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
