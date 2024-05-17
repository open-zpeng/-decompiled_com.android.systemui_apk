package com.xiaopeng.systemui.controller;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.systemui.utils.Utils;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class DropmenuController {
    private static final String ACTION_DROPMENU_HIDE = "com.xiaopeng.intent.action.DROPMENU_HIDE";
    private static final String ACTION_DROPMENU_SHOW = "com.xiaopeng.intent.action.DROPMENU_SHOW";
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private static final long DELAY_HIDE_DROPMENU = 200;
    public static final String DROPMENU_BLUETOOTH = "bluetooth";
    public static final String DROPMENU_DOWNLOAD = "download";
    public static final String DROPMENU_DRIVER = "driver";
    public static final String DROPMENU_NETWORK = "network";
    public static final String DROPMENU_PASSENGER = "passenger";
    public static final String DROPMENU_STORAGE = "storage";
    public static final String DROPMENU_USB = "usb";
    public static final String DROPMENU_VOLUME = "volume";
    public static final String DROPMENU_WELCOME = "welcome";
    private static final String EXTRA_DROPMENU_ALIGN = "android.intent.extra.DROPMENU_ALIGN";
    private static final String EXTRA_DROPMENU_NAME = "android.intent.extra.DROPMENU_NAME";
    private static final String EXTRA_DROPMENU_X = "android.intent.extra.DROPMENU_X";
    private static final String KEY_DROPMENU_STATE = "key_dropmenu_state";
    private static final int MSG_HIDE_DROPMENU = 100;
    public static final int STATE_HIDE = 0;
    public static final int STATE_SHOW = 1;
    private static final String TAG = "DropmenuController";
    public static final int TAG_ALIGN = 2131363185;
    public static final int TAG_LABEL = 2131363186;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private static int sLastDropmenuState = 0;
    private static int sCurrentDropmenuState = 0;
    private static String sLastDropmenuName = "";
    private static String sCurrentDropmenuName = "";
    private static DropmenuController sDropmenuController = null;
    private static HashMap<String, View> sDropmenuItem = new HashMap<>();
    private static HashMap<String, View> sOverrideItem = new HashMap<>();
    private static int sDropMenuShift = 0;
    private OnViewListener mViewListener = new OnViewListener();
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.DropmenuController.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                DropmenuController.hideDropmenu(DropmenuController.this.mContext);
            }
        }
    };
    private SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);

    /* loaded from: classes24.dex */
    public interface OnGestureHandleListener {
        void onGestureHandleVisibleChanged(boolean z);
    }

    /* loaded from: classes24.dex */
    public interface OnItemClickListener {
        void onItemClicked(View view);
    }

    private DropmenuController(Context context) {
        this.mContext = context;
        sDropMenuShift = context.getResources().getDimensionPixelSize(R.dimen.dropmenu_shift);
        registerSettingsObserver(context);
    }

    public static DropmenuController getInstance(Context context) {
        if (sDropmenuController == null) {
            synchronized (DropmenuController.class) {
                if (sDropmenuController == null) {
                    sDropmenuController = new DropmenuController(context);
                }
            }
        }
        return sDropmenuController;
    }

    public void putDropmenuItem(View view, String label, int align) {
        if (view != null && !TextUtils.isEmpty(label)) {
            view.setTag(R.id.tag_dropmenu_label, label);
            view.setTag(R.id.tag_dropmenu_align, Integer.valueOf(align));
            sDropmenuItem.put(label, view);
        }
    }

    public void putOverrideItem(String label, View targetView, View overrideView) {
        if (targetView != null && overrideView != null && !TextUtils.isEmpty(label) && sDropmenuItem.containsKey(label)) {
            sOverrideItem.put(label, overrideView);
        }
    }

    public void initDropmenuItem() {
        HashMap<String, View> hashMap = sDropmenuItem;
        if (hashMap != null && !hashMap.isEmpty()) {
            for (String key : sDropmenuItem.keySet()) {
                View view = sDropmenuItem.get(key);
                if (view != null) {
                    view.setOnClickListener(this.mViewListener);
                    view.setOnTouchListener(this.mViewListener);
                }
            }
        }
    }

    private void registerSettingsObserver(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.registerContentObserver(Settings.System.getUriFor(KEY_DROPMENU_STATE), false, this.mSettingsObserver, -1);
    }

    private void setDropmenuSelected(String name, boolean selected) {
        View view;
        if (!TextUtils.isEmpty(name) && sDropmenuItem.containsKey(name) && (view = sDropmenuItem.get(name)) != null && view.isSelected() != selected) {
            Logger.d(TAG, "setDropmenuSelected view=" + view + " selected=" + selected);
            view.setSelected(selected);
            ((View) view.getParent()).setSelected(selected);
        }
    }

    public void onVisibleChanged() {
        Logger.d(TAG, "onVisibleChanged");
        ContentResolver resolver = this.mContext.getContentResolver();
        sLastDropmenuState = sCurrentDropmenuState;
        sCurrentDropmenuState = Settings.System.getIntForUser(resolver, KEY_DROPMENU_STATE, 0, -2);
        setDropmenuSelected(sCurrentDropmenuName, sCurrentDropmenuState == 1);
    }

    public void onItemChanged(String name, boolean selected) {
        Logger.d(TAG, "onItemChanged name=" + name + " selected=" + selected);
        sLastDropmenuName = sCurrentDropmenuName;
        sCurrentDropmenuName = name;
        if (!TextUtils.isEmpty(sCurrentDropmenuName)) {
            if (!sCurrentDropmenuName.equals(sLastDropmenuName)) {
                setDropmenuSelected(sLastDropmenuName, false);
            }
            setDropmenuSelected(sCurrentDropmenuName, true);
        }
    }

    public void onItemClicked(View view) {
        if (view != null && view.isClickable()) {
            Logger.d(TAG, "onItemClicked() called with: view = [" + view + NavigationBarInflaterView.SIZE_MOD_END);
            if (Utils.isFastClick()) {
                Logger.d(TAG, "isFastClick");
            } else {
                showDropmenu(this.mContext, view);
            }
        }
    }

    public void onItemTouched(View view, MotionEvent motionEvent) {
        if (view != null && view.getTag(R.id.tag_dropmenu_label) != null) {
            try {
                String label = view.getTag(R.id.tag_dropmenu_label).toString();
                if (isDropmenuVisible() && !TextUtils.isEmpty(label) && !label.equals(sCurrentDropmenuName)) {
                    this.mHandler.removeMessages(100);
                }
                if (motionEvent.getAction() == 1) {
                    sendDataLog(label);
                }
            } catch (Exception e) {
            }
        }
    }

    public void onOutsideTouched(MotionEvent ev) {
        Logger.d(TAG, "onTouchOutside isDropmenuVisible=" + isDropmenuVisible());
        if (isDropmenuVisible() && !Utils.isFastClick()) {
            this.mHandler.removeMessages(100);
            this.mHandler.sendEmptyMessageDelayed(100, 200L);
        }
    }

    public static boolean isDropmenuVisible() {
        return sCurrentDropmenuState == 1;
    }

    public static int[] getViewLocation(View view, int align) {
        View overrideView;
        int[] location = new int[2];
        if (view != null) {
            Object tag = view.getTag(R.id.tag_dropmenu_label);
            String label = tag != null ? view.getTag(R.id.tag_dropmenu_label).toString() : "null";
            Context context = view.getContext();
            WindowManager wm = (WindowManager) context.getSystemService("window");
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getRealMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            if (!TextUtils.isEmpty(label) && sOverrideItem.containsKey(label) && (overrideView = sOverrideItem.get(label)) != null) {
                view = overrideView;
            }
            view.getLocationOnScreen(location);
            int w = view.getWidth();
            int h = view.getHeight();
            if (align != 1) {
                if (align == 2) {
                    location[0] = ((widthPixels - location[0]) - w) - sDropMenuShift;
                }
            } else {
                location[0] = (widthPixels - location[0]) + sDropMenuShift;
                int screenWidth = ContextUtils.getScreenWidth();
                if (location[0] > screenWidth) {
                    location[0] = screenWidth;
                }
            }
            Logger.d(TAG, "getViewLocation widthPixels=" + widthPixels + " heightPixels=" + heightPixels + " w=" + w + " h=" + h + " x=" + location[0]);
        }
        return location;
    }

    public static void showWelcome(Context context, View view) {
        if (view != null && view.getTag(R.id.tag_dropmenu_label) != null && view.getTag(R.id.tag_dropmenu_align) != null) {
            try {
                String label = view.getTag(R.id.tag_dropmenu_label).toString();
                int align = Integer.parseInt(view.getTag(R.id.tag_dropmenu_align).toString());
                int[] location = getViewLocation(view, align);
                int x = location[0];
                String packageName = getDropmenuPackageName(label);
                Logger.d(TAG, "showDropmenu label=" + label + " x=" + x);
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_DROPMENU_ALIGN, align);
                bundle.putString(EXTRA_DROPMENU_NAME, DROPMENU_WELCOME);
                bundle.putInt(EXTRA_DROPMENU_X, x);
                PackageHelper.startActivity(context, ACTION_DROPMENU_SHOW, packageName, "", bundle);
                getInstance(context).onItemChanged(label, true);
            } catch (Exception e) {
            }
        }
    }

    public static void showDropmenu(Context context, View view) {
        String tag = (String) view.getTag(R.id.tag_dropmenu_label);
        if (tag.equals("network") && StatusBarGlobal.getInstance(context).clickNetworkButtonToShowOutOfDataPage()) {
            PackageHelper.startOutOfDataPage(context);
        } else if (CarModelsManager.getFeature().showPopupWinOnStatusbarClick()) {
            String intentName = null;
            char c = 65535;
            switch (tag.hashCode()) {
                case 116100:
                    if (tag.equals(DROPMENU_USB)) {
                        c = 0;
                        break;
                    }
                    break;
                case 1427818632:
                    if (tag.equals(DROPMENU_DOWNLOAD)) {
                        c = 2;
                        break;
                    }
                    break;
                case 1843485230:
                    if (tag.equals("network")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1968882350:
                    if (tag.equals(DROPMENU_BLUETOOTH)) {
                        c = 1;
                        break;
                    }
                    break;
            }
            if (c == 0) {
                intentName = "com.xiaopeng.intent.action.POPUP_USB";
            } else if (c == 1) {
                intentName = "com.xiaopeng.intent.action.POPUP_BLUETOOTH";
            } else if (c == 2) {
                intentName = "com.xiaopeng.intent.action.POPUP_DOWNLOAD";
            } else if (c == 3) {
                intentName = "com.xiaopeng.intent.action.POPUP_WLAN";
            }
            if (intentName != null) {
                Intent intent = new Intent(intentName);
                intent.setFlags(268435456);
                context.startActivity(intent);
            }
        } else if (view.getTag(R.id.tag_dropmenu_label) != null && view.getTag(R.id.tag_dropmenu_align) != null) {
            try {
                String label = view.getTag(R.id.tag_dropmenu_label).toString();
                int align = Integer.parseInt(view.getTag(R.id.tag_dropmenu_align).toString());
                int[] location = getViewLocation(view, align);
                int x = location[0];
                String packageName = getDropmenuPackageName(label);
                Logger.d(TAG, "showDropmenu label=" + label + " x=" + x + " dropmenuController = " + sDropmenuController);
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_DROPMENU_ALIGN, align);
                bundle.putString(EXTRA_DROPMENU_NAME, label);
                bundle.putInt(EXTRA_DROPMENU_X, x);
                PackageHelper.startActivity(context, ACTION_DROPMENU_SHOW, packageName, "", bundle);
                getInstance(context).onItemChanged(label, true);
            } catch (Exception e) {
            }
        }
    }

    private static void sendDataLog(String label) {
        if (DROPMENU_BLUETOOTH.equals(label)) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.BLUETOOTH_ID);
        } else if ("network".equals(label)) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.WIFI_ID);
        } else if (DROPMENU_DRIVER.equals(label)) {
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.DRIVER_ID);
        }
    }

    public static void hideDropmenu(Context context) {
        Logger.d(TAG, "hideDropmenu");
        if (isDropmenuVisible()) {
            Bundle bundle = new Bundle();
            PackageHelper.sendBroadcast(context, ACTION_DROPMENU_HIDE, "", "", bundle);
        }
    }

    public static String getDropmenuPackageName(String name) {
        if ("volume".equals(name)) {
            return VuiConstants.SETTINS;
        }
        if (!DROPMENU_DOWNLOAD.equals(name)) {
            return VuiConstants.SETTINS;
        }
        return VuiConstants.SETTINS;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /* loaded from: classes24.dex */
    private static class ItemAnimationHelper {
        private static final long ANIMATION_DURATION_MILLIS = 200;

        private ItemAnimationHelper() {
        }

        public static void onDropmenuSelected(View view, boolean selected) {
            float translate = view != null ? view.getResources().getDimension(R.dimen.sysbar_item_translate_vertical) : 0.0f;
            float fromYDelta = selected ? 0.0f : translate;
            float toYDelta = selected ? translate : 0.0f;
            float fromX = selected ? 1.0f : 0.8f;
            float toX = selected ? 0.8f : 1.0f;
            float fromY = selected ? 1.0f : 0.8f;
            float toY = selected ? 0.8f : 1.0f;
            executeAnimation(view, 200L, 1.0f, 1.0f, fromYDelta, toYDelta, fromX, toX, fromY, toY);
        }

        public static void executeAnimation(final View view, long durationMillis, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, float fromX, float toX, float fromY, float toY) {
            if (view != null) {
                new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                ScaleAnimation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, 1, 0.5f, 1, 0.5f);
                ObjectAnimator.ofFloat(view, "scaleX", fromX, toX);
                ObjectAnimator.ofFloat(view, "scaleY", fromY, toY);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.setDuration(durationMillis);
                final AnimationSet animationSet = new AnimationSet(true);
                animationSet.setFillAfter(true);
                animationSet.setDuration(durationMillis);
                animationSet.addAnimation(scaleAnimation);
                view.post(new Runnable() { // from class: com.xiaopeng.systemui.controller.DropmenuController.ItemAnimationHelper.1
                    @Override // java.lang.Runnable
                    public void run() {
                        view.clearAnimation();
                        view.setLayerType(2, null);
                        view.startAnimation(animationSet);
                    }
                });
            }
        }
    }

    /* loaded from: classes24.dex */
    private final class OnViewListener implements View.OnTouchListener, View.OnClickListener {
        private OnViewListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            DropmenuController.this.onItemClicked(view);
            if (DropmenuController.this.mOnItemClickListener != null) {
                DropmenuController.this.mOnItemClickListener.onItemClicked(view);
            }
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            DropmenuController.this.onItemTouched(view, motionEvent);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            DropmenuController.this.onVisibleChanged();
        }
    }
}
