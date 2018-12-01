package cn.com.egova.hookframework.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.com.egova.hookframework.Const;
import cn.com.egova.hookframework.ProxyActivity;
import cn.com.egova.hookframework.activities.LoginActivity;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by baby on 2018/4/2.
 */

public class HookUtil {
    private Context context;

    public void hookHookMh() {
        //欲得到H，先得到ActivityThread的实例，sCurrentActivityThread正好是ActivityThread的实例也是它的静态成员，所以Hook点选它.
        try {
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = forName.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            //还原系统的ActivityTread   mH
            Object activityThreadObj = currentActivityThreadField.get(null);

            Field handlerField = forName.getDeclaredField("mH");
            handlerField.setAccessible(true);
            //hook点找到了
            Handler mH = (Handler) handlerField.get(activityThreadObj);
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            //这里选择静态代理
            callbackField.set(mH, new ActivityMH(mH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hookStartActivity(Context context) {
        //还原 gDefault 成员变量  反射  调用一次
        this.context = context;
        try {
            Class<?> ActivityManagerNativecls = Class.forName("android.app.ActivityManagerNative");
            Field gDefault = ActivityManagerNativecls.getDeclaredField("gDefault");
            gDefault.setAccessible(true);
            //因为是静态变量  所以获取的到的是系统值
            Object defaltValue = gDefault.get(null);
            //mInstance对象
            Class<?> SingletonClass = Class.forName("android.util.Singleton");
            Field mInstance = SingletonClass.getDeclaredField("mInstance");
            //还原 IactivityManager对象  系统对象
            mInstance.setAccessible(true);
            Object iActivityManagerObject = mInstance.get(defaltValue);

            //使用动态代理
            StartActivtyInvocationHandler invocationHandler = new StartActivtyInvocationHandler(iActivityManagerObject);
            Object proxyObj = invocationHandler.getProxy();
            //将系统的iActivityManager 替换成自己通过动态代理实现的对象，实现了 IActivityManager这个接口的所有方法
            mInstance.set(defaltValue, proxyObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ActivityMH implements Handler.Callback {
        private Handler mH;

        public ActivityMH(Handler mH) {
            this.mH = mH;
        }

        @Override
        public boolean handleMessage(Message msg) {
            //LAUNCH_ACTIVITY ==100 即将要加载一个activity了
            if (msg.what == 100) {
                //加工 --完  一定丢给系统  secondActivity  -hook->proxyActivity---hook->    secondeActivtiy
                handleLaunchActivity(msg);
            }
            //类似Web中的拦截器，最后一定要放开，让系统继续往下执行，并且返回值一定要为true.
            mH.handleMessage(msg);
            return true;
        }

        private void handleLaunchActivity(Message msg) {
            //还原
            Object obj = msg.obj;
            try {
                Field intentField = obj.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                //  ProxyActivity   2
                Intent realyIntent = (Intent) intentField.get(obj);
                //sconedActivity  1
                Intent oldIntent = realyIntent.getParcelableExtra("oldIntent");
                if (oldIntent != null) {
                    //集中式登录
                    SharedPreferences share = context.getSharedPreferences(Const.SP_LOGGIN, Context.MODE_PRIVATE);
                    //假如已登录或者是某些免登陆的界面，则跳转到需要进入的界面，否则跳转到登录界面.
                    if (share.getBoolean(Const.LOGIN_FLAG, false) /*|| oldIntent.getComponent().getClassName().equals(SceondActivity.class.getName())*/) {
                        //登录  还原  把原有的意图放到realyIntent
                        realyIntent.setComponent(oldIntent.getComponent());
                    } else {
                        ComponentName componentName = new ComponentName(context, LoginActivity.class);
                        realyIntent.putExtra("extraIntent", oldIntent.getComponent().getClassName());
                        realyIntent.setComponent(componentName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class StartActivtyInvocationHandler implements InvocationHandler {
        private Object realObj;

        public StartActivtyInvocationHandler(Object realObj) {
            this.realObj = realObj;
        }

        //返回指定接口的代理对象.凡是这些接口定义的方法调用时，都会拦截到。
        public Object getProxy() throws ClassNotFoundException {
            Class[] interfaces = new Class[]{Class.forName("android.app.IActivityManager")};
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i("INFO", "invoke    " + method.getName());
            //这里只拦截startActivity方法，其他的一律放过。
            if ("startActivity".equals(method.getName())) {
                Log.i("INFO", "-----------------startActivity--------------------------");
                //寻找传进来的intent
                Intent intent = null;
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof Intent) {
                        intent = (Intent) args[i];
                        index = i;
                    }
                }
                //统一导入ProxyActivity进行集中式登录管理。原来的Intent作为参数携带过去。
                Intent newIntent = new Intent();
                ComponentName componentName = new ComponentName(context, ProxyActivity.class);
                newIntent.setComponent(componentName);
                //真实的意图 被我隐藏到了  键值对
                newIntent.putExtra("oldIntent", intent);
                //将系统原来的Intent替换掉。
                args[index] = newIntent;
            }
            //如Web中的拦截器，最后一定要放过，让系统继续往下执行。
            return method.invoke(realObj, args);
        }
    }


    public void injectPluginClass() {
        String cacheDir = context.getCacheDir().getAbsolutePath();
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Const.PLUGIN_APK_NAME;
        try {
            //第一步   找到插件的Elements数组  dexPathlist  ----？dexElements
            DexClassLoader dexClassLoader = new DexClassLoader(apkPath, cacheDir, cacheDir, context.getClassLoader());
            Class myDexClazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field myPathListField = myDexClazz.getDeclaredField("pathList");
            myPathListField.setAccessible(true);
            Object myPathListObject = myPathListField.get(dexClassLoader);

            Class myPathClass = myPathListObject.getClass();
            Field myElementsField = myPathClass.getDeclaredField("dexElements");
            myElementsField.setAccessible(true);
            //拿到插件的  dexElements[]
            Object pluginDexElements = myElementsField.get(myPathListObject);

            //第二步找到宿主的Elements数组    dexElements
            PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
            Class baseDexClazzLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListFiled = baseDexClazzLoader.getDeclaredField("pathList");
            pathListFiled.setAccessible(true);
            Object pathListObject = pathListFiled.get(pathClassLoader);

            Class systemPathClazz = pathListObject.getClass();
            Field systemElementsField = systemPathClazz.getDeclaredField("dexElements");
            systemElementsField.setAccessible(true);
            //宿主的 dexElements[]
            Object systemElements = systemElementsField.get(pathListObject);

            //第三步将宿主和插件的dexElements 数组合并成新的 dexElements 然后通过反射重新注入宿主的Field （dexElements ）变量中。
            //由于DexElement只能在系统源码内部使用，故这里合并数组只能使用别的api
            int pluginLength = Array.getLength(pluginDexElements);
            int systemLength = Array.getLength(systemElements);
            int newLength = pluginLength + systemLength;
            //获取数组的每一个成员的class类型
            Class<?> singleElementClazz = systemElements.getClass().getComponentType();
            Object newElementArray = Array.newInstance(singleElementClazz, newLength);
            //给上面创建新数组的每个元素赋值
            for (int i = 0; i < newLength; i++) {
                if (i < pluginLength) {
                    Array.set(newElementArray,i,Array.get(pluginDexElements,i));
                } else {
                    Array.set(newElementArray,i,Array.get(systemElements,i-pluginLength));
                }
            }

            Field  elementsField=pathListObject.getClass().getDeclaredField("dexElements");;
            elementsField.setAccessible(true);
            //设置给宿主的DexPathList
            systemElementsField.set(pathListObject,newElementArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
