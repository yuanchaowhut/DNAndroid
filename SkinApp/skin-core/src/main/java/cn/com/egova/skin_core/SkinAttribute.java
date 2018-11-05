package cn.com.egova.skin_core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.egova.skin_core.utils.SkinResources;
import cn.com.egova.skin_core.utils.SkinThemeUtils;

/**
 * Created by yuanchao on 2018/3/26.
 */

class SkinAttribute {
    private static final List<String> mAttributes = new ArrayList<>();

    static {
        mAttributes.add("background");
        mAttributes.add("src");

        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");
    }

    List<SkinView> mSkinViews = new ArrayList<>();

    private Typeface typeface;
    ;

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * 采集需要换肤的view
     *
     * @param view
     * @param attrs
     */
    public void load(View view, AttributeSet attrs) {
        List<SkinPair> skinPairs = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获得属性名
            String attributeName = attrs.getAttributeName(i);
            //是否符合 需要筛选的属性名
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);
                //类似于 textColor="#ff00ff 写死了不用换肤.
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                //资源id
                int resId;
                if (attributeValue.startsWith("?")) {
                    //attr Id
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    //获得 主题 style 中的 对应 attr 的资源id值
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    // @12343455332
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                if (resId != 0) {
                    //可以被替换的属性
                    SkinPair skinPair = new SkinPair(attributeName, resId);
                    skinPairs.add(skinPair);
                }
            }
        }

        //将View与之对应的可以动态替换的属性集合 放入集合中.
        //1.属性名包含在mAttributes中并且资源id不为0的控件;
        //2.所有的TextView(因为只有TextView及其子类可以换字体,而此处是全局换字体,故将所有TextView都搜集起来).
        //3.所有自定义控件,自定义控件必须自己实现SkinViewSupport,本框架只负责在换肤时回调接口方法;
        if (!skinPairs.isEmpty() || view instanceof TextView || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, skinPairs);
            mSkinViews.add(skinView);
            skinView.applySkin(typeface);   //过滤出来后马上执行换肤, 这里是全局替换字体.
        }
    }


    /**
     * 换皮肤
     */
    public void applySkin(Typeface typeface) {
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin(typeface);
        }
    }

    static class SkinView {
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(Typeface typeface) {
            applySkinTypeface(typeface);   //全局换字体
            applySkinViewSupport();    //自定义控件换肤

            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
//                        Color
                        if (background instanceof Integer) {
                            view.setBackgroundColor((Integer) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
                        Typeface tf = SkinResources.getInstance().getTypeface(skinPair.resId);  //单个控件替换字体
                        applySkinTypeface(tf);
                        break;
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        private void applySkinTypeface(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }

        /**
         * 自定义换肤, 由于我们的框架里不能预先知道都有什么属性,故只提供接口,留给用户自己去实现.
         * 自定义View只需要实现 SkinViewSupport接口即可.
         */
        private void applySkinViewSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }
    }


    static class SkinPair {
        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
