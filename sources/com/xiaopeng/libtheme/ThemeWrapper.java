package com.xiaopeng.libtheme;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import androidx.core.content.ThemeContextCompat;
import com.android.internal.util.ArrayUtils;
import com.xiaopeng.libtheme.ThemeManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
/* loaded from: classes23.dex */
public class ThemeWrapper {
    private static final String TAG = "ThemeWrapper";
    private static final long TIMEOUT = 5000;
    private static final int TYPE_DRAWABLE_BOTTOM = 3;
    private static final int TYPE_DRAWABLE_END = 5;
    private static final int TYPE_DRAWABLE_LEFT = 0;
    private static final int TYPE_DRAWABLE_RIGHT = 2;
    private static final int TYPE_DRAWABLE_START = 4;
    private static final int TYPE_DRAWABLE_TOP = 1;
    private static HashMap<String, List<ThemeView>> sThemeCache;
    private Handler mHandler = new WorkHandler();
    public static final boolean DEBUG = ThemeManager.DEBUG;
    private static volatile ThemeWrapper sThemeWrapper = null;

    private ThemeWrapper() {
        sThemeCache = new HashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ThemeWrapper getInstance() {
        if (sThemeWrapper == null) {
            synchronized (ThemeWrapper.class) {
                if (sThemeWrapper == null) {
                    sThemeWrapper = new ThemeWrapper();
                }
            }
        }
        return sThemeWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeout() {
        this.mHandler.removeCallbacksAndMessages(null);
        HashMap<String, List<ThemeView>> hashMap = sThemeCache;
        if (hashMap != null) {
            hashMap.clear();
        }
        sThemeCache = null;
        sThemeWrapper = null;
        this.mHandler = null;
        ThemeManager.Logger.log("onTimeout time=" + now());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void onConfigurationChanged(Context context, ThemeData data, Configuration newConfig) {
        Context ctx = context.getApplicationContext();
        boolean isNightMode = ThemeManager.isNightMode(ctx);
        boolean isThemeChanged = ThemeManager.isThemeChanged(newConfig);
        ThemeManager.Logger.log(TAG, "onConfigurationChanged isNightMode=" + isNightMode + " isThemeChanged=" + isThemeChanged);
        if (isThemeChanged) {
            handleThemeChanged(ctx, data);
        }
    }

    private synchronized void handleThemeChanged(Context context, ThemeData data) {
        ThemeManager.Logger.log("handleThemeChanged");
        put(context, data);
        timeout();
        refresh(context, data);
    }

    private void put(final Context context, final ThemeData data) {
        if (context == null || data == null) {
            return;
        }
        try {
            if (!TextUtils.isEmpty(data.xml)) {
                this.mHandler.post(new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        List<ThemeView> list = ThemeParser.parseXml(context, data.xml);
                        ThemeWrapper.sThemeCache.put(data.xml, list);
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    private void refresh(Context context, ThemeData data) {
        handleRefreshList(context, data);
        handleRefreshXml(context, data);
    }

    private void timeout() {
        this.mHandler.removeMessages(100);
        this.mHandler.sendEmptyMessageDelayed(100, TIMEOUT);
    }

    private void handleRefreshList(Context context, ThemeData data) {
        if (data != null && data.list != null) {
            for (ThemeView tv : data.list) {
                updateViewResource(context, data.root, tv);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRefreshXml(final Context context, final ThemeData data) {
        if (data != null && !TextUtils.isEmpty(data.xml)) {
            boolean xmlCached = sThemeCache.containsKey(data.xml);
            ThemeManager.Logger.log("handleRefreshXml xmlCached=" + xmlCached);
            if (xmlCached) {
                List<ThemeView> list = sThemeCache.get(data.xml);
                if (list != null) {
                    for (ThemeView tv : list) {
                        updateViewResource(context, data.root, tv);
                    }
                    return;
                }
                return;
            }
            this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    ThemeWrapper.this.handleRefreshXml(context, data);
                }
            }, 10L);
        }
    }

    private View getThemeView(Context context, View root, ThemeView tv) {
        View rootView;
        View view = null;
        if (context == null || tv == null || root == null) {
            return null;
        }
        if (tv.resId > 0 && tv.resRoot > 0 && (rootView = root.findViewById(tv.resRoot)) != null) {
            view = rootView.findViewById(tv.resId);
        }
        if (tv.resId > 0 && view == null) {
            return root.findViewById(tv.resId);
        }
        return view;
    }

    private void updateViewResource(Context context, View root, ThemeView tv) {
        try {
            View view = getThemeView(context, root, tv);
            if (context != null && root != null && tv != null && view != null && tv.resValue != null && !TextUtils.isEmpty(tv.resAttr)) {
                String resAttr = tv.resAttr;
                int resValue = -1;
                try {
                    resValue = Integer.parseInt(tv.resValue.toString());
                } catch (Exception e) {
                }
                if (ThemeManager.AttributeSet.hasAttribute(resAttr)) {
                    setViewResource(context, view, resAttr, resValue);
                } else {
                    invokeViewResource(context, view, resAttr, resValue);
                }
            }
        } catch (Exception e2) {
            ThemeManager.Logger.log(TAG, "updateViewResource e=" + e2);
        }
    }

    /* loaded from: classes23.dex */
    private static class WorkHandler extends Handler {
        private static final int MSG_HANDLER_TIMEOUT = 100;

        private WorkHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                ThemeWrapper.getInstance().onTimeout();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static HashMap<String, Integer> resolveAttribute(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, HashMap<String, List<String>> extras) {
        boolean hasStyle;
        boolean hasTheme;
        List<String> append;
        Set<String> set;
        int i = defStyleAttr;
        HashMap<String, Integer> maps = ThemeManager.AttributeSet.sAttributeMap;
        HashMap<String, Integer> list = new HashMap<>();
        List<String> append2 = (extras == null || !extras.containsKey(ThemeManager.KEY_APPEND)) ? null : extras.get(ThemeManager.KEY_APPEND);
        List<String> global = (extras == null || !extras.containsKey(ThemeManager.KEY_GLOBAL)) ? null : extras.get(ThemeManager.KEY_GLOBAL);
        if (attrs == null) {
            hasStyle = false;
            hasTheme = false;
        } else {
            int N = attrs.getAttributeCount();
            hasStyle = false;
            hasTheme = false;
            for (int i2 = 0; i2 < N; i2++) {
                try {
                    String name = attrs.getAttributeName(i2);
                    String value = attrs.getAttributeValue(i2);
                    ThemeManager.Logger.log("resolveAttribute common attr name=" + name + " value=" + value);
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                        boolean isDefineAttribute = ThemeManager.AttributeSet.hasAttribute(name);
                        boolean isAppendAttribute = append2 != null && append2.contains(name);
                        boolean isValidAttribute = (isDefineAttribute || isAppendAttribute) && value.startsWith("@");
                        boolean isThemeAttribute = ThemeManager.AttributeSet.isThemeAttribute(name);
                        boolean isStyleAttribute = ThemeManager.AttributeSet.isStyleAttribute(name);
                        if (isValidAttribute) {
                            if (isThemeAttribute) {
                                hasTheme = true;
                            }
                            if (isStyleAttribute) {
                                hasStyle = true;
                            }
                            if (!isThemeAttribute && !isStyleAttribute) {
                                int resId = Integer.parseInt(value.substring(1));
                                list.put(name, Integer.valueOf(resId));
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
        if (hasTheme || hasStyle) {
            for (String key : maps.keySet()) {
                int attr = maps.get(key).intValue();
                if (attr != 0 && !list.containsKey(key)) {
                    int resId2 = 0;
                    if (hasTheme) {
                        append = append2;
                        resId2 = getThemeResourceId(attr, context, null, 0, 0);
                    } else {
                        append = append2;
                    }
                    if (hasStyle) {
                        resId2 = getThemeResourceId(attr, context, attrs, i, defStyleRes);
                    }
                    ThemeManager.Logger.log("resolveAttribute theme&style attr key=" + key + " resId=0x" + Integer.toHexString(resId2) + " hasTheme=" + hasTheme + " hasStyle=" + hasStyle);
                    if (resId2 != 0) {
                        list.put(key, Integer.valueOf(resId2));
                    }
                    append2 = append;
                }
            }
        }
        if (i > 0 || defStyleRes > 0) {
            try {
                Set<String> set2 = maps.keySet();
                int size = set2.size();
                String[] keys = (String[]) set2.toArray(new String[size]);
                int[] values = ArrayUtils.convertToIntArray(new ArrayList(maps.values()));
                int i3 = 0;
                while (i3 < size) {
                    String key2 = keys[i3];
                    int attr2 = values[i3];
                    if (list.containsKey(key2)) {
                        set = set2;
                    } else {
                        set = set2;
                        int resId3 = getThemeResourceId(attr2, context, null, i, defStyleRes);
                        ThemeManager.Logger.log("resolveAttribute def attr key=" + key2 + " resId=0x" + Integer.toHexString(resId3));
                        if (resId3 != 0) {
                            list.put(key2, Integer.valueOf(resId3));
                        }
                    }
                    i3++;
                    i = defStyleAttr;
                    set2 = set;
                }
            } catch (Exception e2) {
                ThemeManager.Logger.log(TAG, "resolveAttribute style e=" + e2);
            }
        }
        if (global != null && !global.isEmpty()) {
            for (String key3 : global) {
                if (!TextUtils.isEmpty(key3) && !list.containsKey(key3)) {
                    if (maps.containsKey(key3)) {
                        int resId4 = getThemeResourceId(maps.get(key3).intValue(), context, null, 0, 0);
                        ThemeManager.Logger.log("resolveAttribute global attr key=" + key3 + " resId=0x" + Integer.toHexString(resId4));
                        if (resId4 != 0) {
                            list.put(key3, Integer.valueOf(resId4));
                        }
                    }
                }
            }
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void setAttributeValue(HashMap<String, Integer> map, String attr, int resId) {
        if (map != null && !TextUtils.isEmpty(attr) && resId != 0) {
            try {
                map.put(attr, Integer.valueOf(resId));
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void updateAttribute(View view, HashMap<String, Integer> map) {
        if (view != null && map != null) {
            for (String attr : map.keySet()) {
                if (ThemeManager.AttributeSet.hasAttribute(attr)) {
                    setViewResource(view.getContext(), view, attr, map.get(attr).intValue());
                } else {
                    invokeViewResource(view.getContext(), view, attr, map.get(attr) != null ? map.get(attr).intValue() : 0);
                }
            }
        }
    }

    private static void setViewResource(Context context, View view, String attr, int resId) {
        Context ctx = context != null ? context.getApplicationContext() : null;
        if (ctx != null && view != null && !TextUtils.isEmpty(attr)) {
            Resources res = ctx.getResources();
            ctx.getTheme();
            try {
                if (ThemeManager.AttributeSet.ALPHA.equals(attr)) {
                    if (Build.VERSION.SDK_INT >= 29) {
                        view.setAlpha(res.getFloat(resId));
                    }
                } else if (ThemeManager.AttributeSet.BACKGROUND.equals(attr)) {
                    view.setBackground(getOverlayDrawable(ThemeContextCompat.getDrawable(ctx, resId), view.getBackground()));
                } else if (ThemeManager.AttributeSet.FOREGROUND.equals(attr)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        view.setForeground(getOverlayDrawable(ThemeContextCompat.getDrawable(ctx, resId), view.getForeground()));
                    }
                } else if (ThemeManager.AttributeSet.SCROLLBAR_THUMB_VERTICAL.equals(attr)) {
                    setVerticalThumbDrawable(view, ThemeContextCompat.getDrawable(ctx, resId));
                } else if (ThemeManager.AttributeSet.SRC.equals(attr)) {
                    if (view instanceof ImageView) {
                        ImageView imageView = (ImageView) view;
                        imageView.setImageDrawable(getOverlayDrawable(ThemeContextCompat.getDrawable(ctx, resId), imageView.getDrawable()));
                    }
                } else if (ThemeManager.AttributeSet.TEXT_COLOR.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        ((android.widget.TextView) view).setTextColor(ThemeContextCompat.getColorStateList(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.TEXT_COLOR_HINT.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        ((android.widget.TextView) view).setHintTextColor(ThemeContextCompat.getColorStateList(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_LEFT.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(0, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_TOP.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(1, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_RIGHT.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(2, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_BOTTOM.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(3, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_START.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(4, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DRAWABLE_END.equals(attr)) {
                    if (view instanceof android.widget.TextView) {
                        setCompoundDrawables(5, (android.widget.TextView) view, ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.PROGRESS_DRAWABLE.equals(attr)) {
                    if (view instanceof ProgressBar) {
                        ((ProgressBar) view).setProgressDrawable(ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.THUMB.equals(attr)) {
                    if (view instanceof SeekBar) {
                        ((SeekBar) view).setThumb(ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.BUTTON.equals(attr)) {
                    if (view instanceof CompoundButton) {
                        ((CompoundButton) view).setButtonDrawable(ThemeContextCompat.getDrawable(ctx, resId));
                    }
                } else if (ThemeManager.AttributeSet.DIVIDER.equals(attr) && (view instanceof ListView)) {
                    ((ListView) view).setDivider(ThemeContextCompat.getDrawable(ctx, resId));
                }
                view.refreshDrawableState();
                ThemeManager.Logger.log("setViewResource view info:" + getViewInfo(view, resId, attr));
            } catch (Exception e) {
                ThemeManager.Logger.log("setViewResource error attr=" + attr + " view=" + view + " e=" + e);
            }
        }
    }

    private static void invokeViewResource(Context context, View view, String attr, int resId) {
        try {
            if (!TextUtils.isEmpty(attr) && attr.length() > 0) {
                view.getClass().getPackage().getName();
                String className = view.getClass().getName();
                String attrMethod = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
                ThemeManager.Logger.log("invokeViewResource view info:" + getViewInfo(view, resId, attr));
                Class clazz = Class.forName(className);
                if (clazz != null) {
                    Method method = clazz.getMethod(attrMethod, Integer.TYPE);
                    method.invoke(view, Integer.valueOf(resId));
                }
            }
        } catch (Exception e) {
            ThemeManager.Logger.log(TAG, "invokeViewResource view=" + view + " e=" + e);
        }
    }

    private static Drawable getOverlayDrawable(Drawable newDrawable, Drawable oldDrawable) {
        if (newDrawable != null && oldDrawable != null && newDrawable.getLevel() != oldDrawable.getLevel()) {
            newDrawable.setLevel(oldDrawable.getLevel());
        }
        return newDrawable;
    }

    private static int getThemeResourceId(int attr, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context != null ? context.getTheme().obtainStyledAttributes(attrs, new int[]{attr}, defStyleAttr, defStyleRes) : null;
        if (ta != null) {
            try {
                int resId = ta.getResourceId(0, 0);
                if (resId != 0) {
                    ta.recycle();
                    return resId;
                }
            } catch (Exception e) {
            } catch (Throwable th) {
                ta.recycle();
                throw th;
            }
            ta.recycle();
        }
        return 0;
    }

    private static void setCompoundDrawables(int type, android.widget.TextView view, Drawable drawable) {
        if (drawable != null && view != null) {
            if (type == 0 || type == 1 || type == 2 || type == 3) {
                Drawable[] drawables = view.getCompoundDrawables();
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                if (drawables != null) {
                    drawables[type] = getOverlayDrawable(drawable, drawables[type]);
                    view.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
                }
            } else if (type == 4 || type == 5) {
                Drawable[] drawables2 = view.getCompoundDrawablesRelative();
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                if (drawables2 != null) {
                    int index = type == 4 ? (char) 0 : 2;
                    drawables2[index] = getOverlayDrawable(drawable, drawables2[index]);
                    view.setCompoundDrawablesRelative(drawables2[0], drawables2[1], drawables2[2], drawables2[3]);
                }
            }
        }
    }

    private static void setVerticalThumbDrawable(View view, Drawable drawable) {
        Class<?> scrollCacheClazz;
        Field scrollBarField;
        Object scrollBarObj;
        Class<?> scrollBarClazz;
        Field verticalThumbField;
        try {
            Method method = View.class.getDeclaredMethod("getScrollCache", new Class[0]);
            if (method == null) {
                return;
            }
            method.setAccessible(true);
            Object scrollCacheObj = method.invoke(view, new Object[0]);
            if (scrollCacheObj == null || (scrollCacheClazz = scrollCacheObj.getClass()) == null || (scrollBarField = scrollCacheClazz.getField("scrollBar")) == null || (scrollBarObj = scrollBarField.get(scrollCacheObj)) == null || (scrollBarClazz = scrollBarObj.getClass()) == null || (verticalThumbField = scrollBarClazz.getDeclaredField("mVerticalThumb")) == null) {
                return;
            }
            verticalThumbField.setAccessible(true);
            verticalThumbField.set(scrollBarObj, drawable);
        } catch (Exception e) {
        }
    }

    protected static void startTextColorTransition(final android.widget.TextView view, final int startColor, final int endColor) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null) {
                        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(startColor), Integer.valueOf(endColor));
                        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.libtheme.ThemeWrapper.3.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int color = ((Integer) animation.getAnimatedValue()).intValue();
                                view.setTextColor(color);
                            }
                        });
                        colorAnimator.setDuration((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                        colorAnimator.start();
                    }
                } catch (Exception e) {
                    if (ThemeWrapper.DEBUG) {
                        Log.d(ThemeWrapper.TAG, "startTextColorAnimation e=" + e);
                    }
                }
            }
        };
        if (view != null) {
            view.post(runnable);
        }
    }

    protected static void startTextColorTransition(final android.widget.TextView view, final int startColor, final ColorStateList endColor) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.4
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null && endColor != null) {
                        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(startColor), Integer.valueOf(endColor.getDefaultColor()));
                        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.libtheme.ThemeWrapper.4.1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int color = ((Integer) animation.getAnimatedValue()).intValue();
                                view.setTextColor(color);
                            }
                        });
                        colorAnimator.setDuration((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                        colorAnimator.start();
                        view.postDelayed(new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.4.2
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setTextColor(endColor);
                            }
                        }, ThemeManager.THEME_ANIMATION_INTERVAL * 2);
                    }
                } catch (Exception e) {
                    if (ThemeWrapper.DEBUG) {
                        Log.d(ThemeWrapper.TAG, "startTextColorAnimation e=" + e);
                    }
                }
            }
        };
        if (view != null) {
            view.post(runnable);
        }
    }

    protected static void startImageDrawableTransition(final ImageView view, final Drawable startDrawable, final Drawable endDrawable) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null && startDrawable != null && endDrawable != null) {
                        if (endDrawable.getLevel() != startDrawable.getLevel()) {
                            endDrawable.setLevel(startDrawable.getLevel());
                        }
                        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{startDrawable, endDrawable});
                        view.setImageDrawable(transitionDrawable);
                        transitionDrawable.startTransition((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                        view.postDelayed(new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.5.1
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setImageDrawable(endDrawable);
                            }
                        }, (int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                    }
                } catch (Exception e) {
                    if (ThemeWrapper.DEBUG) {
                        Log.d(ThemeWrapper.TAG, "startImageDrawableTransition e=" + e);
                    }
                }
            }
        };
        if (view != null) {
            view.post(runnable);
        }
    }

    protected static void startViewBackgroundTransition(final View view, final Drawable startDrawable, final Drawable endDrawable) {
        Runnable runnable = new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (view != null && startDrawable != null && endDrawable != null) {
                        if (endDrawable.getLevel() != startDrawable.getLevel()) {
                            endDrawable.setLevel(startDrawable.getLevel());
                        }
                        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{startDrawable, endDrawable});
                        view.setBackground(transitionDrawable);
                        transitionDrawable.startTransition((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                        view.postDelayed(new Runnable() { // from class: com.xiaopeng.libtheme.ThemeWrapper.6.1
                            @Override // java.lang.Runnable
                            public void run() {
                                view.setBackground(endDrawable);
                            }
                        }, (int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                    }
                } catch (Exception e) {
                    if (ThemeWrapper.DEBUG) {
                        Log.d(ThemeWrapper.TAG, "startViewBackgroundTransition e=" + e);
                    }
                }
            }
        };
        if (view != null) {
            view.post(runnable);
        }
    }

    protected static long now() {
        return System.currentTimeMillis();
    }

    protected static String getViewInfo(View view, int resId, String attr) {
        StringBuffer buffer = new StringBuffer("getViewInfo");
        if (view != null) {
            try {
                Context context = view.getContext().getApplicationContext();
                if (context != null) {
                    Resources res = context.getResources();
                    TypedValue value = new TypedValue();
                    res.getValue(resId, value, true);
                    buffer.append(" view.id=0x" + Integer.toHexString(view.getId()));
                    buffer.append(" view.resId=0x" + Integer.toHexString(resId));
                    buffer.append(" view.attr=" + attr);
                    buffer.append(" view.toString=" + view.toString());
                    buffer.append(" view.value.string=" + ((Object) value.string));
                    buffer.append(" view.value=" + value.toString());
                } else {
                    buffer.append(" context null");
                }
            } catch (Exception e) {
                buffer.append(" error=" + e);
            }
        }
        return buffer.toString();
    }
}
