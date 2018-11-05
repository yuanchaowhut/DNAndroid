package cn.com.egova.skin_core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import cn.com.egova.skin_core.utils.SkinThemeUtils;

/**
 * Created by yuanchao on 2018/3/26.
 * //创建所有View,并采集需要换肤的View.
 */

class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    //两个参数的构造函数
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();

    private Activity activity;

    // 属性处理类
    private SkinAttribute skinAttribute;


    public SkinLayoutFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        skinAttribute = new SkinAttribute(typeface);
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //反射 classloader
        View view = createViewFromTag(name, context, attrs);
        // 自定义View
        if (null == view) {
            view = createView(name, context, attrs);
        }

        //筛选符合属性的View.
        //这个地方有一个值得学习的地方, 这里并没有拿到筛选后的结果,然后在本类里做换肤的操作,
        // 而是直接将筛选结果存在SkinAttribute类里,然后换肤操作也是在SkinAttribute里进行.
        //遵循了单一职责原则 skinAttribute.applySkin();
        skinAttribute.load(view, attrs);

        return view;
    }

    /**
     * @param name    控件的简写名称: TextView
     * @param context
     * @param attrs
     * @return
     */
    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
        //包含了 . 自定义控件
        if (-1 != name.indexOf(".")) {
            return null;
        }
        View view = null;
        for (int i = 0; i < mClassPrefixList.length; i++) {
            view = createView(mClassPrefixList[i] + name, context, attrs);
            if (null != view) {
                break;
            }
        }
        return view;
    }

    /**
     * @param name    控件的全类名: android.widget.TextView
     * @param context
     * @param attrs
     * @return
     */
    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        if (null == constructor) {
            try {
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = aClass.getConstructor(mConstructorSignature); //使用的是2个参数的构造函数.
                sConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        if (null != constructor) {
            try {
                return constructor.newInstance(context, attrs);
            } catch (Exception e) {
            }
        }
        return null;
    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attributeSet) {
        return null;
    }

    @Override
    public void update(Observable observable, Object o) {
        SkinThemeUtils.updateStatusBar(activity);
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity); //获取字体
        skinAttribute.applySkin(typeface);
    }
}
