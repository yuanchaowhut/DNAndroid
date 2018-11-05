package cn.com.egova.dbframework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.com.egova.dbframework.bean.Photo;
import cn.com.egova.dbframework.bean.User;
import cn.com.egova.dbframework.db.BaseDao;
import cn.com.egova.dbframework.db.BaseDaoFactory;
import cn.com.egova.dbframework.db.BaseDaoImpl;
import cn.com.egova.dbframework.sub_db.BaseDaoSubFactory;
import cn.com.egova.dbframework.sub_db.PhotoDao;
import cn.com.egova.dbframework.sub_db.UserDao;
import cn.com.egova.dbframework.update.UpdateManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @InjectView(R.id.btn_insert)
    Button btnInsert;
    @InjectView(R.id.btn_update)
    Button btnUpdate;
    @InjectView(R.id.btn_delete)
    Button btnDelete;
    @InjectView(R.id.btn_query)
    Button btnQuery;

    @InjectView(R.id.btn_login1)
    Button btnLogin1;

    @InjectView(R.id.btn_login2)
    Button btnLogin2;

    @InjectView(R.id.btn_login3)
    Button btnLogin3;

    @InjectView(R.id.btn_save)
    Button btnSave;

    @InjectView(R.id.btn_upgrade)
    Button btnUpgrade;


    int index = 1;

    UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        updateManager = new UpdateManager();
    }


    @OnClick(R.id.btn_insert)
    public void insert() {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDaoImpl.class, User.class);
        User user = new User("N000" + index++, "tom", "123456", "tom123@126.com");
        baseDao.insert(user);
        Toast.makeText(this, "插入数据成功", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_update)
    public void update() {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User user = new User("N0001", "Marry", "123456", "marry123@126.com");
        User where = new User();
        where.setId("N0001");
        long count = baseDao.update(user, where);
        Toast.makeText(this, "更新了" + count + "条数据", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_delete)
    public void delete() {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User where = new User();
        where.setName("Marry");
        long count = baseDao.delete(where);
        Toast.makeText(this, "删除了" + count + "条数据", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_query)
    public void query() {
        BaseDao baseDao = BaseDaoFactory.getInstance().getBaseDao(BaseDao.class, User.class);
        User where = new User();
        where.setId("N0001");
        where.setName("Marry");
        List<User> result = baseDao.query(where, null, null, null);
        for (User user : result) {
            Log.e(TAG, user.toString());
        }
        Toast.makeText(this, "查询到" + result.size() + "条数据", Toast.LENGTH_SHORT).show();
    }


    @OnClick({R.id.btn_login1, R.id.btn_login2, R.id.btn_login3})
    public void login(View view) {
        User user = null;
        Photo photo = null;
        switch (view.getId()) {
            case R.id.btn_login1:
                user = new User("N0001", "Tom", "123456", "tom123@126.com");
                photo = new Photo("/sdcard/photo/tom.png", "2018-03-14 14:30:45");
                break;
            case R.id.btn_login2:
                user = new User("N0002", "Marry", "123456", "marry123@126.com");
                photo = new Photo("/sdcard/photo/marry.png", "2018-03-14 16:30:45");
                break;
            case R.id.btn_login3:
                user = new User("N0003", "Jerry", "123456", "jerry123@126.com");
                photo = new Photo("/sdcard/photo/jerry.png", "2018-03-14 14:30:45");
                break;
        }

        processLogin(user);  //插入公有库的t_user表
        processPhoto(photo);  //插入公有库的t_photo表
    }

    /**
     * 处理登录业务(操作公库)
     *
     * @param user
     */
    private void processLogin(User user) {
        UserDao userDao = BaseDaoFactory.getInstance().getBaseDao(UserDao.class, User.class);    //操作公有表.
        userDao.insert(user);
    }

    /**
     * 操作私库(私库里的头像表)
     *
     * @param photo
     */
    private void processPhoto(Photo photo) {
        PhotoDao photoDao = BaseDaoSubFactory.getInstance().getSubDao(PhotoDao.class, Photo.class);    //操作分库.
        photoDao.insert(photo);
    }


    //数据库版本升级的原理：
    // 1.保存数据库版本信息(服务端获取到的最新版/手机上安装的版本）；
    // 2.检查当前是否具备升级数据库的环境(该创建的表都创建出来)；
    // 3.升级数据库(备份数据库，备份数据表，重新建表，导入表数据，删除备份数据表，删除备份数据库）。

    @OnClick(R.id.btn_save)
    public void saveVerInfo(View view) {
        updateManager.saveVersionInfo(this, "V003");
    }

    @OnClick(R.id.btn_upgrade)
    public void upgrade(View view) {
        updateManager.checkThisVersionTable(this);  //检查当前是否具备升级环境(保证数据表齐全）
        updateManager.startUpdateDb(this);          //升级（备份，重新建表，导入数据，删除备份。
    }
}
