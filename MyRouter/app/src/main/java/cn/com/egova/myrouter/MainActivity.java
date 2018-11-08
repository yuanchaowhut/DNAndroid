package cn.com.egova.myrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String format = DateUtils.format(new Date());
        Toast.makeText(this, format, Toast.LENGTH_SHORT).show();
    }
}
