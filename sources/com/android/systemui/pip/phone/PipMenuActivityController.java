package com.android.systemui.pip.phone;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.RemoteAction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.pip.phone.PipMediaController;
import com.android.systemui.pip.phone.PipMenuActivityController;
import com.android.systemui.shared.system.InputConsumerController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class PipMenuActivityController {
    private static final boolean DEBUG = false;
    public static final String EXTRA_ACTIONS = "actions";
    public static final String EXTRA_ALLOW_TIMEOUT = "allow_timeout";
    public static final String EXTRA_CONTROLLER_MESSENGER = "messenger";
    public static final String EXTRA_DISMISS_FRACTION = "dismiss_fraction";
    public static final String EXTRA_MENU_STATE = "menu_state";
    public static final String EXTRA_MOVEMENT_BOUNDS = "movement_bounds";
    public static final String EXTRA_STACK_BOUNDS = "stack_bounds";
    public static final String EXTRA_WILL_RESIZE_MENU = "resize_menu_on_show";
    public static final int MENU_STATE_CLOSE = 1;
    public static final int MENU_STATE_FULL = 2;
    public static final int MENU_STATE_NONE = 0;
    public static final int MESSAGE_DISMISS_PIP = 103;
    public static final int MESSAGE_EXPAND_PIP = 101;
    public static final int MESSAGE_MENU_STATE_CHANGED = 100;
    public static final int MESSAGE_MINIMIZE_PIP = 102;
    public static final int MESSAGE_REGISTER_INPUT_CONSUMER = 105;
    public static final int MESSAGE_SHOW_MENU = 107;
    public static final int MESSAGE_UNREGISTER_INPUT_CONSUMER = 106;
    public static final int MESSAGE_UPDATE_ACTIVITY_CALLBACK = 104;
    private static final long START_ACTIVITY_REQUEST_TIMEOUT_MS = 300;
    private static final String TAG = "PipMenuActController";
    private IActivityManager mActivityManager;
    private ParceledListSlice mAppActions;
    private Context mContext;
    private InputConsumerController mInputConsumerController;
    private ParceledListSlice mMediaActions;
    private PipMediaController mMediaController;
    private int mMenuState;
    private Runnable mOnAnimationEndRunnable;
    private boolean mStartActivityRequested;
    private long mStartActivityRequestedTime;
    private Messenger mToActivityMessenger;
    private ArrayList<Listener> mListeners = new ArrayList<>();
    private Bundle mTmpDismissFractionData = new Bundle();
    private Handler mHandler = new AnonymousClass1();
    private Messenger mMessenger = new Messenger(this.mHandler);
    private Runnable mStartActivityRequestedTimeoutRunnable = new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$46Yr3xVHMZsGyZiGhSKF_IPBnzk
        @Override // java.lang.Runnable
        public final void run() {
            PipMenuActivityController.this.lambda$new$0$PipMenuActivityController();
        }
    };
    private PipMediaController.ActionListener mMediaActionListener = new PipMediaController.ActionListener() { // from class: com.android.systemui.pip.phone.PipMenuActivityController.2
        @Override // com.android.systemui.pip.phone.PipMediaController.ActionListener
        public void onMediaActionsChanged(List<RemoteAction> mediaActions) {
            PipMenuActivityController.this.mMediaActions = new ParceledListSlice(mediaActions);
            PipMenuActivityController.this.updateMenuActions();
        }
    };

    /* loaded from: classes21.dex */
    public interface Listener {
        void onPipDismiss();

        void onPipExpand();

        void onPipMenuStateChanged(int i, boolean z);

        void onPipMinimize();

        void onPipShowMenu();
    }

    /* renamed from: com.android.systemui.pip.phone.PipMenuActivityController$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 extends Handler {
        AnonymousClass1() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 107) {
                PipMenuActivityController.this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$1$nEDJFK5X-9H1WAx_9S8qUwV6KLY
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((PipMenuActivityController.Listener) obj).onPipShowMenu();
                    }
                });
                return;
            }
            switch (i) {
                case 100:
                    int menuState = msg.arg1;
                    PipMenuActivityController.this.onMenuStateChanged(menuState, true);
                    return;
                case 101:
                    PipMenuActivityController.this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$1$8btqC3E6FFjbjLWUhiNmbnKUlfI
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((PipMenuActivityController.Listener) obj).onPipExpand();
                        }
                    });
                    return;
                case 102:
                    PipMenuActivityController.this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$1$o9fLqvuiKIYwdsSexRT0X4Ty0V4
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((PipMenuActivityController.Listener) obj).onPipMinimize();
                        }
                    });
                    return;
                case 103:
                    PipMenuActivityController.this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$1$rDXDKqpw1CLC0fwevwYEng68Bps
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((PipMenuActivityController.Listener) obj).onPipDismiss();
                        }
                    });
                    return;
                case 104:
                    PipMenuActivityController.this.mToActivityMessenger = msg.replyTo;
                    PipMenuActivityController.this.setStartActivityRequested(false);
                    if (PipMenuActivityController.this.mOnAnimationEndRunnable != null) {
                        PipMenuActivityController.this.mOnAnimationEndRunnable.run();
                        PipMenuActivityController.this.mOnAnimationEndRunnable = null;
                    }
                    if (PipMenuActivityController.this.mToActivityMessenger == null) {
                        PipMenuActivityController.this.onMenuStateChanged(0, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public /* synthetic */ void lambda$new$0$PipMenuActivityController() {
        setStartActivityRequested(false);
        Runnable runnable = this.mOnAnimationEndRunnable;
        if (runnable != null) {
            runnable.run();
            this.mOnAnimationEndRunnable = null;
        }
        Log.e(TAG, "Expected start menu activity request timed out");
    }

    public PipMenuActivityController(Context context, IActivityManager activityManager, PipMediaController mediaController, InputConsumerController inputConsumerController) {
        this.mContext = context;
        this.mActivityManager = activityManager;
        this.mMediaController = mediaController;
        this.mInputConsumerController = inputConsumerController;
    }

    public boolean isMenuActivityVisible() {
        return this.mToActivityMessenger != null;
    }

    public void onActivityPinned() {
        this.mInputConsumerController.registerInputConsumer();
    }

    public void onActivityUnpinned() {
        hideMenu();
        this.mInputConsumerController.unregisterInputConsumer();
        setStartActivityRequested(false);
    }

    public void onPinnedStackAnimationEnded() {
        if (this.mToActivityMessenger != null) {
            Message m = Message.obtain();
            m.what = 6;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify menu pinned animation ended", e);
            }
        }
    }

    public void addListener(Listener listener) {
        if (!this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void setDismissFraction(float fraction) {
        if (this.mToActivityMessenger != null) {
            this.mTmpDismissFractionData.clear();
            this.mTmpDismissFractionData.putFloat(EXTRA_DISMISS_FRACTION, fraction);
            Message m = Message.obtain();
            m.what = 5;
            m.obj = this.mTmpDismissFractionData;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify menu to update dismiss fraction", e);
            }
        } else if (!this.mStartActivityRequested || isStartActivityRequestedElapsed()) {
            startMenuActivity(0, null, null, false, false);
        }
    }

    public void showMenu(int menuState, Rect stackBounds, Rect movementBounds, boolean allowMenuTimeout, boolean willResizeMenu) {
        if (this.mToActivityMessenger != null) {
            Bundle data = new Bundle();
            data.putInt(EXTRA_MENU_STATE, menuState);
            data.putParcelable(EXTRA_STACK_BOUNDS, stackBounds);
            data.putParcelable(EXTRA_MOVEMENT_BOUNDS, movementBounds);
            data.putBoolean(EXTRA_ALLOW_TIMEOUT, allowMenuTimeout);
            data.putBoolean(EXTRA_WILL_RESIZE_MENU, willResizeMenu);
            Message m = Message.obtain();
            m.what = 1;
            m.obj = data;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify menu to show", e);
            }
        } else if (!this.mStartActivityRequested || isStartActivityRequestedElapsed()) {
            startMenuActivity(menuState, stackBounds, movementBounds, allowMenuTimeout, willResizeMenu);
        }
    }

    public void pokeMenu() {
        if (this.mToActivityMessenger != null) {
            Message m = Message.obtain();
            m.what = 2;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify poke menu", e);
            }
        }
    }

    public void hideMenu() {
        if (this.mToActivityMessenger != null) {
            Message m = Message.obtain();
            m.what = 3;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify menu to hide", e);
            }
        }
    }

    public void hideMenu(Runnable onStartCallback, Runnable onEndCallback) {
        if (this.mStartActivityRequested) {
            this.mOnAnimationEndRunnable = onEndCallback;
            onStartCallback.run();
            this.mHandler.removeCallbacks(this.mStartActivityRequestedTimeoutRunnable);
            this.mHandler.postDelayed(this.mStartActivityRequestedTimeoutRunnable, START_ACTIVITY_REQUEST_TIMEOUT_MS);
        } else if (this.mMenuState != 0 && this.mToActivityMessenger != null) {
            onStartCallback.run();
            Message m = Message.obtain();
            m.what = 3;
            m.obj = onEndCallback;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not notify hide menu", e);
            }
        }
    }

    public void hideMenuWithoutResize() {
        onMenuStateChanged(0, false);
    }

    public void setAppActions(ParceledListSlice appActions) {
        this.mAppActions = appActions;
        updateMenuActions();
    }

    private ParceledListSlice resolveMenuActions() {
        if (isValidActions(this.mAppActions)) {
            return this.mAppActions;
        }
        return this.mMediaActions;
    }

    private void startMenuActivity(int menuState, Rect stackBounds, Rect movementBounds, boolean allowMenuTimeout, boolean willResizeMenu) {
        try {
            ActivityManager.StackInfo pinnedStackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            if (pinnedStackInfo == null || pinnedStackInfo.taskIds == null || pinnedStackInfo.taskIds.length <= 0) {
                Log.e(TAG, "No PIP tasks found");
                return;
            }
            Intent intent = new Intent(this.mContext, PipMenuActivity.class);
            intent.putExtra(EXTRA_CONTROLLER_MESSENGER, this.mMessenger);
            intent.putExtra("actions", (Parcelable) resolveMenuActions());
            if (stackBounds != null) {
                intent.putExtra(EXTRA_STACK_BOUNDS, stackBounds);
            }
            if (movementBounds != null) {
                intent.putExtra(EXTRA_MOVEMENT_BOUNDS, movementBounds);
            }
            intent.putExtra(EXTRA_MENU_STATE, menuState);
            intent.putExtra(EXTRA_ALLOW_TIMEOUT, allowMenuTimeout);
            intent.putExtra(EXTRA_WILL_RESIZE_MENU, willResizeMenu);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(this.mContext, 0, 0);
            options.setLaunchTaskId(pinnedStackInfo.taskIds[pinnedStackInfo.taskIds.length - 1]);
            options.setTaskOverlay(true, true);
            this.mContext.startActivityAsUser(intent, options.toBundle(), UserHandle.CURRENT);
            setStartActivityRequested(true);
        } catch (RemoteException e) {
            setStartActivityRequested(false);
            Log.e(TAG, "Error showing PIP menu activity", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMenuActions() {
        if (this.mToActivityMessenger != null) {
            Rect stackBounds = null;
            try {
                ActivityManager.StackInfo pinnedStackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
                if (pinnedStackInfo != null) {
                    stackBounds = pinnedStackInfo.bounds;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error showing PIP menu activity", e);
            }
            Bundle data = new Bundle();
            data.putParcelable(EXTRA_STACK_BOUNDS, stackBounds);
            data.putParcelable("actions", resolveMenuActions());
            Message m = Message.obtain();
            m.what = 4;
            m.obj = data;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e2) {
                Log.e(TAG, "Could not notify menu activity to update actions", e2);
            }
        }
    }

    private boolean isValidActions(ParceledListSlice actions) {
        return actions != null && actions.getList().size() > 0;
    }

    private boolean isStartActivityRequestedElapsed() {
        return SystemClock.uptimeMillis() - this.mStartActivityRequestedTime >= START_ACTIVITY_REQUEST_TIMEOUT_MS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMenuStateChanged(final int menuState, final boolean resize) {
        if (menuState != this.mMenuState) {
            this.mListeners.forEach(new Consumer() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipMenuActivityController$oZuzXTzYX29YiUgUX8-q8QZcGtw
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((PipMenuActivityController.Listener) obj).onPipMenuStateChanged(menuState, resize);
                }
            });
            if (menuState == 2) {
                this.mMediaController.addListener(this.mMediaActionListener);
            } else {
                this.mMediaController.removeListener(this.mMediaActionListener);
            }
        }
        this.mMenuState = menuState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStartActivityRequested(boolean requested) {
        this.mHandler.removeCallbacks(this.mStartActivityRequestedTimeoutRunnable);
        this.mStartActivityRequested = requested;
        this.mStartActivityRequestedTime = requested ? SystemClock.uptimeMillis() : 0L;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleTouchEvent(MotionEvent ev) {
        if (this.mToActivityMessenger != null) {
            Message m = Message.obtain();
            m.what = 7;
            m.obj = ev;
            try {
                this.mToActivityMessenger.send(m);
            } catch (RemoteException e) {
                Log.e(TAG, "Could not dispatch touch event", e);
            }
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        String innerPrefix = prefix + "  ";
        pw.println(prefix + TAG);
        pw.println(innerPrefix + "mMenuState=" + this.mMenuState);
        pw.println(innerPrefix + "mToActivityMessenger=" + this.mToActivityMessenger);
        pw.println(innerPrefix + "mListeners=" + this.mListeners.size());
        pw.println(innerPrefix + "mStartActivityRequested=" + this.mStartActivityRequested);
        pw.println(innerPrefix + "mStartActivityRequestedTime=" + this.mStartActivityRequestedTime);
    }
}
