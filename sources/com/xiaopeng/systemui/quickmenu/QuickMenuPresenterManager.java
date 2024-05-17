package com.xiaopeng.systemui.quickmenu;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.alibaba.fastjson.JSON;
import com.xiaopeng.input.IInputEventListener;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.BootCompletedController;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes24.dex */
public class QuickMenuPresenterManager extends IInputEventListener.Stub {
    private static final int FLAG_ACTION_CANCEL = 8;
    private static final int FLAG_ACTION_DOWN = 1;
    private static final int FLAG_ACTION_MOVE = 4;
    private static final int FLAG_ACTION_POINTER_DOWN = 16;
    private static final int FLAG_ACTION_POINTER_UP = 32;
    private static final int FLAG_ACTION_UP = 2;
    private Context mContext;
    private boolean mIfNapaInit;
    private List<QuickMenuPresenter> mQuickMenuPresenters;
    private static final String TAG = QuickMenuPresenterManager.class.getSimpleName();
    public static int FLAGS_DOWN_UP = 3;
    public static int FLAGS_DOWN_UP_MOVE = 63;
    private static boolean isDownUpRegistered = false;
    private static boolean isDownUpMoveRegistered = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingletonHolder {
        private static final QuickMenuPresenterManager sInstance = new QuickMenuPresenterManager();

        private SingletonHolder() {
        }
    }

    public static QuickMenuPresenterManager getInstance() {
        return SingletonHolder.sInstance;
    }

    private QuickMenuPresenterManager() {
        this.mQuickMenuPresenters = new ArrayList();
        this.mIfNapaInit = false;
        this.mContext = ContextUtils.getContext();
        boolean isCompleted = BootCompletedController.get().isBootCompleted();
        if (isCompleted) {
            registerInputListener(FLAGS_DOWN_UP);
        } else {
            BootCompletedController.get().addOnceCallBack(new BootCompletedController.CallBack() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager.1
                @Override // com.xiaopeng.systemui.controller.BootCompletedController.CallBack
                public void onBootCompleted() {
                    QuickMenuPresenterManager.this.registerInputListener(QuickMenuPresenterManager.FLAGS_DOWN_UP);
                }
            });
        }
    }

    public void onKeyEvent(KeyEvent event, String extra) throws RemoteException {
    }

    public void onTouchEvent(MotionEvent ev, String extra) throws RemoteException {
        if (!CarModelsManager.getFeature().isDropQuickMenuSupport() || !ifNapaInit()) {
            Log.i(TAG, String.format("isDropQuickMenuSupport: %b; ifNapaInit: %b", Boolean.valueOf(CarModelsManager.getFeature().isDropQuickMenuSupport()), Boolean.valueOf(ifNapaInit())));
            return;
        }
        final MotionEvent event = MotionEvent.obtain(ev);
        ThreadUtils.getUiThreadHandler().post(new Runnable() { // from class: com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager.2
            @Override // java.lang.Runnable
            public void run() {
                QuickMenuPresenterManager.this.handleTouchEvent(event);
                event.recycle();
            }
        });
    }

    public void handleTouchEvent(MotionEvent ev) {
        int pointCount = ev.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            float x = ev.getX(i);
            int touchScreenID = (x >= 2400.0f || x < 0.0f) ? 1 : 0;
            int actionMasked = ev.getActionMasked();
            if ((actionMasked != 0 && actionMasked != 1 && actionMasked != 3 && actionMasked != 5 && actionMasked != 6) || ev.getActionIndex() == i) {
                MotionEvent e = MotionEvent.obtain(ev.getDownTime(), ev.getEventTime(), ev.getActionMasked(), ev.getX(i), ev.getY(i), ev.getMetaState());
                dispatchTouchEvent(touchScreenID, e);
            }
        }
    }

    public void attachQuickMenu() {
        this.mQuickMenuPresenters.add(new QuickMenuPresenter(this.mContext, 0));
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            this.mQuickMenuPresenters.add(new QuickMenuPresenter(this.mContext, 1));
        }
    }

    public void dispatchTouchEvent(int screenIndex, MotionEvent ev) {
        if (screenIndex < this.mQuickMenuPresenters.size()) {
            this.mQuickMenuPresenters.get(screenIndex).dispatchTouchEvent(ev);
        }
    }

    public void autoHideQuickMenu(int screenIndex) {
        if (screenIndex < this.mQuickMenuPresenters.size()) {
            this.mQuickMenuPresenters.get(screenIndex).autoHideQuickMenu();
        }
    }

    public void registerInputListener(int flag) {
        if (flag == FLAGS_DOWN_UP) {
            if (isDownUpRegistered) {
                return;
            }
            isDownUpRegistered = true;
        }
        if (flag == FLAGS_DOWN_UP_MOVE) {
            if (isDownUpMoveRegistered) {
                return;
            }
            isDownUpMoveRegistered = true;
        }
        Log.i("InputEventListenerImpl", "registerInputListener: " + flag);
        Map<String, Integer> map = new HashMap<>();
        map.put("flags", Integer.valueOf(flag));
        String jsonString = JSON.toJSONString(map);
        InputManager.getInstance().registerInputListener(this, jsonString);
    }

    public void unregisterInputListener(int flag) {
        if (flag == FLAGS_DOWN_UP) {
            if (!isDownUpRegistered) {
                return;
            }
            isDownUpRegistered = false;
        }
        if (flag == FLAGS_DOWN_UP_MOVE) {
            if (!isDownUpMoveRegistered) {
                return;
            }
            isDownUpMoveRegistered = false;
        }
        Log.i("InputEventListenerImpl", "unregisterInputListener: " + flag);
        Map<String, Integer> map = new HashMap<>();
        map.put("flags", Integer.valueOf(flag));
        String jsonString = JSON.toJSONString(map);
        InputManager.getInstance().unregisterInputListener(this, jsonString);
    }

    private boolean ifNapaInit() {
        if (this.mIfNapaInit) {
            return true;
        }
        this.mIfNapaInit = "init_finish".equals(SystemProperties.get("sys.xiaopeng.napa_state"));
        return this.mIfNapaInit;
    }
}
