package cn.com.egova.alipayplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by yuanchao on 2018/11/21.
 */

public class PluginManager {
    private Context context;
    private PackageInfo packageInfo;
    private DexClassLoader dexClassLoader;
    private Resources resources;

    private static final PluginManager instance = new PluginManager();

    private PluginManager() {
    }

    public static PluginManager getInstance() {
        return instance;
    }


    public void loadPath(Context context, String pluginName) {
        this.context = context;
        //packageInfo
        File filesDir = context.getDir("plugin", Context.MODE_PRIVATE);
        String filePath = new File(filesDir, pluginName).getAbsolutePath();
        PackageManager packageManager = context.getPackageManager();
        packageInfo = packageManager.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);

        //dexClassLoader
        File dexOutFile = context.getDir("dex", Context.MODE_PRIVATE);
        //参数1:apk的路径， 参数2：dex文件缓存路径， 参数3：lib路径，null表示相对本项目，参数4：本项目的classLoader
        dexClassLoader = new DexClassLoader(filePath, dexOutFile.getAbsolutePath(), null, context.getClassLoader());

        //resources
        try {
            //加载插件apk中的资源，通过AssetManager的@hide注解掉的addAssetPath方法，方法的参数是apk的路径
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, filePath);
            //参数1：assetManager，参数2：当前运行环境下的显示状态，参数3：当前运行环境下的配置
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());

            //阅读源码，采用系统的方式注册静态广播.
            parseReceivers(context,filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void parseReceivers(Context context, String path) {
//        Package对象
//        PackageParser pp = new PackageParser();
//        PackageParser.Package  pkg = pp.parsePackage(scanFile, parseFlags);

        try {

            Class packageParserClass = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();
            Object packageObj = parsePackageMethod.invoke(packageParser, new File(path), PackageManager.GET_ACTIVITIES);

            Field receiverField = packageObj.getClass().getDeclaredField("receivers");
            //拿到receivers  广播集合    app存在多个广播   集合  List<Activity>  name  ————》 ActivityInfo   className
            List receivers = (List) receiverField.get(packageObj);

            //PackageParser$Component身上有intents，类型是ArrayList<II> intents，II extends IntentInfo,IntentInfo继承IntentFilter.
            Class<?> componentClass = Class.forName("android.content.pm.PackageParser$Component");
            Field intentsField = componentClass.getDeclaredField("intents");

            //public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId)

            // 调用generateActivityInfo 方法所需参数1： PackageParser.Activity
            Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");

            //调用generateActivityInfo 方法所需参数3：PackageUserState
            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Object defaltUserState = packageUserStateClass.newInstance();

            //调用generateActivityInfo 方法所需参数4：userId
            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserIdMethod.invoke(null);

            // public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId)
            Method generateReceiverInfo = packageParserClass.getDeclaredMethod("generateActivityInfo",
                    packageParser$ActivityClass, int.class, packageUserStateClass, int.class);

            for (Object activity : receivers) {
                ActivityInfo info = (ActivityInfo) generateReceiverInfo.invoke(packageParser, activity, 0, defaltUserState, userId);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) dexClassLoader.loadClass(info.name).newInstance();

                //intents是PackageParser$Component的成员变量，public final static class Activity extends Component<ActivityIntentInfo>
                List<? extends IntentFilter> intents = (List<? extends IntentFilter>) intentsField.get(activity);
                for (IntentFilter intentFilter : intents) {
                    context.registerReceiver(broadcastReceiver, intentFilter);
                }
            }
            //generateActivityInfo
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public DexClassLoader getDexClassLoader() {
        return dexClassLoader;
    }

    public Resources getResources() {
        return resources;
    }
}
