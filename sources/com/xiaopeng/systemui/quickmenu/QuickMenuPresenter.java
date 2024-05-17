package com.xiaopeng.systemui.quickmenu;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.alibaba.fastjson.JSON;
import com.android.systemui.R;
import com.google.gson.Gson;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes24.dex */
public class QuickMenuPresenter {
    private static final String ACTION_SWIPE = "android.intent.action.XP_GESTURE_SWIPE";
    private static final int AUTO_HIDE_QUICK_MENU_CHECK_TIME = 200;
    private static final String KEY_XP_QUICKMENU_FLAG = "key_xp_quickmenu_flag";
    private static final int MSG_ENABLE_AUTO_HIDE_QUICK_MENU = 2;
    private static final String TAG = "QuickMenuPresenter";
    protected Context mContext;
    private int mDropShowQuickMenuRecRegion;
    protected QuickMenuFloatingView mQuickMenuFloatingView;
    private int mScreenIndex;
    private Rect mSmallAvatarBounds;
    private int mSmallAvatarMarginLeft;
    private int mSmallAvatarMarginTop;
    private int mSmallAvatarSize;
    protected WindowManager mWindowManager;
    private final ContentResolver mResolver = ContextUtils.getContext().getContentResolver();
    private final int QS_IN_USING_EVENT = 1;
    private final int QS_UN_USING_EVENT = 0;
    private String KEY_QUICK_PANEL_EVENT = "key_quick_panel_event";
    private String KEY_QUICK_MENU_IN_SCREEN = "quick_menu_in_screen";
    private String KEY_QUICK_MENU_USING_EVENT = "quick_menu_using_event";
    private boolean mIsQuickMenuAdded = false;
    private boolean mEnableAutoHideQuickMenu = false;
    private boolean mDropShowQuickMenuStarted = false;
    private int mIsQuickMenuUsingEvent = 0;
    private boolean mHasSetPerParam = true;
    private boolean mIfAppPolicyPassChecked = false;
    private boolean mIfAppPolicyPass = false;
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                QuickMenuPresenter.this.mEnableAutoHideQuickMenu = true;
            }
        }
    };

    public QuickMenuPresenter(Context context, int screenIndex) {
        this.mScreenIndex = 0;
        this.mScreenIndex = screenIndex;
        this.KEY_QUICK_PANEL_EVENT += "_" + this.mScreenIndex;
        this.KEY_QUICK_MENU_IN_SCREEN += "_" + this.mScreenIndex;
        this.KEY_QUICK_MENU_USING_EVENT += "_" + this.mScreenIndex;
        this.mContext = context;
        Resources resources = context.getResources();
        this.mSmallAvatarSize = resources.getDimensionPixelSize(R.dimen.infoflow_small_avatar_size);
        this.mSmallAvatarMarginLeft = resources.getDimensionPixelSize(R.dimen.infoflow_small_avatar_margin_left);
        this.mSmallAvatarMarginTop = resources.getDimensionPixelSize(R.dimen.infoflow_small_avatar_margin_top);
        int i = this.mSmallAvatarMarginLeft;
        int i2 = this.mSmallAvatarMarginTop;
        int i3 = this.mSmallAvatarSize;
        this.mSmallAvatarBounds = new Rect(i, i2, i + i3, i3 + i2);
        this.mDropShowQuickMenuRecRegion = resources.getInteger(R.integer.drop_show_quick_menu_rec_region);
        this.mWindowManager = StatusBarGlobal.getInstance(context).getWindowManager();
        this.mQuickMenuFloatingView = createQuickMenuFloatingView(screenIndex);
        this.mQuickMenuFloatingView.addQuickMenuFloatingAlphaListener(new QuickMenuFloatingView.QuickMenuFloatingVisibleChangeListener() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.2
            @Override // com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.QuickMenuFloatingVisibleChangeListener
            public void visibility(boolean show) {
                if (show) {
                    QuickMenuPresenter.this.showQuickMenu(0);
                } else {
                    QuickMenuPresenter.this.dismissQuickMenu();
                }
            }

            @Override // com.xiaopeng.systemui.quickmenu.QuickMenuFloatingView.QuickMenuFloatingVisibleChangeListener
            public void willDropDown(boolean down) {
            }
        });
        final Uri quickPanelEventUri = Settings.Secure.getUriFor(this.KEY_QUICK_PANEL_EVENT);
        ContentObserver mCallbackObserver = new ContentObserver(new Handler()) { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.3
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (uri.equals(quickPanelEventUri)) {
                    String event = Settings.Secure.getString(QuickMenuPresenter.this.mContext.getContentResolver(), QuickMenuPresenter.this.KEY_QUICK_PANEL_EVENT);
                    Logger.d(QuickMenuPresenter.TAG, QuickMenuPresenter.this.KEY_QUICK_PANEL_EVENT + " event: " + event);
                    Gson gson = new Gson();
                    QuickPanelEvent quickPanelEvent = (QuickPanelEvent) gson.fromJson(event, (Class<Object>) QuickPanelEvent.class);
                    if (quickPanelEvent.getEvent().equals(String.valueOf(0))) {
                        QuickMenuPresenter.this.autoHideQuickMenu();
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(this.KEY_QUICK_PANEL_EVENT), true, mCallbackObserver);
        Map<String, Integer> map = new HashMap<>();
        map.put("sharedId", Integer.valueOf(this.mScreenIndex));
        map.put("enable", 1);
        Settings.Secure.putString(this.mResolver, QuickSlideTouchPresenter.KEY_SLIDE_TOUCH_EVENT, JSON.toJSONString(map));
        BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                Bundle bundle = intent.getBundleExtra("data");
                Logger.d(QuickMenuPresenter.TAG, String.format("mScreenIndex: %s ,screen id: %s, direction: %s", Integer.toString(QuickMenuPresenter.this.mScreenIndex), bundle.getString("screenId"), bundle.getString(VuiConstants.EVENT_VALUE_DIRECTION)));
                int swipeScreenId = Integer.parseInt((String) Objects.requireNonNull(bundle.getString("screenId")));
                if (swipeScreenId == QuickMenuPresenter.this.mScreenIndex) {
                    QuickMenuPresenter.this.mDropShowQuickMenuStarted = true;
                    QuickMenuPresenterManager.getInstance().unregisterInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP);
                    QuickMenuPresenterManager.getInstance().registerInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP_MOVE);
                }
            }
        };
        Settings.Secure.putString(this.mResolver, this.KEY_QUICK_MENU_IN_SCREEN, JSON.toJSONString(new HashMap<String, Integer>() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.5
            {
                put("show", 0);
            }
        }));
        setQsIsUsingEvent(0);
        setSystemSetting(KEY_XP_QUICKMENU_FLAG, 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SWIPE);
        this.mContext.registerReceiver(mReceiver, filter);
    }

    protected void attachQuickMenu() {
        QuickSlideTouchPresenter.getInstance().attachQuickMenu(this.mScreenIndex);
        Settings.Secure.putString(this.mResolver, this.KEY_QUICK_MENU_IN_SCREEN, JSON.toJSONString(new HashMap<String, Integer>() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.6
            {
                put("show", 1);
            }
        }));
        WindowHelper.addQuickMenu(this.mScreenIndex, this.mWindowManager, this.mQuickMenuFloatingView);
    }

    protected QuickMenuFloatingView createQuickMenuFloatingView(int screenIndex) {
        return new QuickMenuFloatingView(this.mContext, screenIndex);
    }

    public void dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == 0 || action == 5) {
            this.mIfAppPolicyPassChecked = false;
            this.mIfAppPolicyPass = false;
            if (ev.getY() < this.mDropShowQuickMenuRecRegion && !this.mSmallAvatarBounds.contains((int) ev.getX(), (int) ev.getY())) {
                this.mDropShowQuickMenuStarted = true;
                QuickMenuPresenterManager.getInstance().unregisterInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP);
                QuickMenuPresenterManager.getInstance().registerInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP_MOVE);
            }
        }
        if (this.mDropShowQuickMenuStarted) {
            if (action != 0) {
                if (action == 1) {
                    setQsIsUsingEvent(0);
                    this.mQuickMenuFloatingView.joinTouchEvent(ev);
                    this.mDropShowQuickMenuStarted = false;
                    QuickMenuPresenterManager.getInstance().unregisterInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP_MOVE);
                    QuickMenuPresenterManager.getInstance().registerInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP);
                    return;
                } else if (action != 2) {
                    if (action != 3 && action != 5) {
                        if (action == 6) {
                            setQsIsUsingEvent(0);
                            ev.setAction(1);
                            this.mQuickMenuFloatingView.joinTouchEvent(ev);
                            this.mDropShowQuickMenuStarted = false;
                            return;
                        }
                        return;
                    }
                } else {
                    if (ev.getY() > this.mDropShowQuickMenuRecRegion) {
                        setPerParam(this.mScreenIndex);
                        setQsIsUsingEvent(1);
                    }
                    this.mQuickMenuFloatingView.joinTouchEvent(ev);
                    return;
                }
            }
            this.mQuickMenuFloatingView.joinTouchEvent(ev);
        }
    }

    public void autoHideQuickMenu() {
        QuickMenuFloatingView quickMenuFloatingView = this.mQuickMenuFloatingView;
        if (quickMenuFloatingView != null && this.mEnableAutoHideQuickMenu) {
            quickMenuFloatingView.autoHide();
            this.mEnableAutoHideQuickMenu = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissQuickMenu() {
        Logger.d(TAG, "dismissQuickMenu : " + this.mIsQuickMenuAdded);
        this.mHasSetPerParam = false;
        if (this.mIsQuickMenuAdded) {
            QuickSlideTouchPresenter.getInstance().dismissQuickMenu(this.mScreenIndex);
            setSystemSetting(KEY_XP_QUICKMENU_FLAG, 0);
            Settings.Secure.putString(this.mResolver, this.KEY_QUICK_MENU_IN_SCREEN, JSON.toJSONString(new HashMap<String, Integer>() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.7
                {
                    put("show", 0);
                }
            }));
            this.mWindowManager.removeView(this.mQuickMenuFloatingView);
            this.mIsQuickMenuAdded = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showQuickMenu(int y) {
        Logger.i(TAG, "showQuickMenu : mIsQuickMenuAdded = " + this.mIsQuickMenuAdded + " y = " + y);
        if (this.mIfAppPolicyPassChecked && !this.mIfAppPolicyPass) {
            return;
        }
        this.mIfAppPolicyPassChecked = true;
        if (!this.mIsQuickMenuAdded && isPolicyPass(this.mScreenIndex)) {
            Log.i(TAG, "isPolicyPass: true");
            setSystemSetting(KEY_XP_QUICKMENU_FLAG, 1);
            this.mEnableAutoHideQuickMenu = false;
            delayToEnableAutoHideQuickMenu();
            attachQuickMenu();
            QuickMenuFloatingView quickMenuFloatingView = this.mQuickMenuFloatingView;
            quickMenuFloatingView.scrollTo(0, quickMenuFloatingView.getScreenHeight(this.mContext));
            this.mIsQuickMenuAdded = true;
        }
    }

    private void delayToEnableAutoHideQuickMenu() {
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 200L);
    }

    /* loaded from: classes24.dex */
    public class QuickPanelEvent {
        private String event;
        private String time;

        public QuickPanelEvent(String event, String time) {
            this.event = event;
            this.time = time;
        }

        public String getEvent() {
            return this.event;
        }

        public String getTime() {
            return this.time;
        }

        public String toString() {
            return "QuickPanelEvent{event='" + this.event + "', time=" + this.time + '}';
        }
    }

    private boolean isPolicyPass(int screenId) {
        if (this.mIfAppPolicyPass) {
            return true;
        }
        Bundle extras = new Bundle();
        extras.putInt("sharedId", screenId);
        extras.putString(VuiConstants.SCENE_PACKAGE_NAME, this.mContext.getPackageName());
        WindowManager wm = StatusBarGlobal.getInstance(this.mContext).getWindowManager();
        int policy = wm.getAppPolicy(extras);
        this.mIfAppPolicyPass = policy == 0;
        return this.mIfAppPolicyPass;
    }

    private void setPerParam(int mDisplayId) {
        if (this.mHasSetPerParam) {
            return;
        }
        Logger.i(TAG, "setPerParam");
        this.mHasSetPerParam = true;
        if (mDisplayId != 0) {
            if (mDisplayId == 1) {
                SystemProperties.set("persist.sys.doquicksec", String.valueOf(mDisplayId));
                return;
            }
            return;
        }
        SystemProperties.set("persist.sys.doquickpri", String.valueOf(mDisplayId));
    }

    private void setQsIsUsingEvent(int state) {
        if (state != this.mIsQuickMenuUsingEvent) {
            Logger.i(TAG, "setQsIsUsingEvent: " + state);
            this.mIsQuickMenuUsingEvent = state;
            Settings.Secure.putString(this.mResolver, this.KEY_QUICK_MENU_USING_EVENT, JSON.toJSONString(new HashMap<String, Integer>() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenter.8
                {
                    put("state", Integer.valueOf(QuickMenuPresenter.this.mIsQuickMenuUsingEvent));
                }
            }));
        }
    }

    private void setSystemSetting(String key, int value) {
        if (this.mScreenIndex == 0) {
            Logger.i("setSystemSetting key: " + key + ", value: " + value);
            Settings.System.putInt(this.mContext.getContentResolver(), key, value);
        }
    }
}
