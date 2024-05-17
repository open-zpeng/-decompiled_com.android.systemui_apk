package com.android.systemui.pip.tv;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Debug;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.IPinnedStackController;
import android.view.IPinnedStackListener;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.pip.BasePipManager;
import com.android.systemui.pip.tv.PipManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class PipManager implements BasePipManager {
    private static final int CLOSE_PIP_WHEN_MEDIA_SESSION_GONE_TIMEOUT_MS = 3000;
    private static final int INVALID_RESOURCE_TYPE = -1;
    static final int PLAYBACK_STATE_PAUSED = 1;
    static final int PLAYBACK_STATE_PLAYING = 0;
    static final int PLAYBACK_STATE_UNAVAILABLE = 2;
    private static final String SETTINGS_PACKAGE_AND_CLASS_DELIMITER = "/";
    public static final int STATE_NO_PIP = 0;
    public static final int STATE_PIP = 1;
    public static final int STATE_PIP_MENU = 2;
    public static final int SUSPEND_PIP_RESIZE_REASON_WAITING_FOR_MENU_ACTIVITY_FINISH = 1;
    public static final int SUSPEND_PIP_RESIZE_REASON_WAITING_FOR_OVERLAY_ACTIVITY_FINISH = 2;
    private static final int TASK_ID_NO_PIP = -1;
    private static PipManager sPipManager;
    private static List<Pair<String, String>> sSettingsPackageAndClassNamePairList;
    private IActivityManager mActivityManager;
    private IActivityTaskManager mActivityTaskManager;
    private Context mContext;
    private Rect mCurrentPipBounds;
    private ParceledListSlice mCustomActions;
    private int mImeHeightAdjustment;
    private boolean mImeVisible;
    private boolean mInitialized;
    private String[] mLastPackagesResourceGranted;
    private MediaSessionManager mMediaSessionManager;
    private Rect mMenuModePipBounds;
    private Rect mPipBounds;
    private ComponentName mPipComponentName;
    private MediaController mPipMediaController;
    private PipNotification mPipNotification;
    private Rect mSettingsPipBounds;
    private int mSuspendPipResizingReason;
    private IWindowManager mWindowManager;
    private static final String TAG = "PipManager";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private int mState = 0;
    private int mResumeResizePinnedStackRunnableState = 0;
    private final Handler mHandler = new Handler();
    private List<Listener> mListeners = new ArrayList();
    private List<MediaListener> mMediaListeners = new ArrayList();
    private Rect mDefaultPipBounds = new Rect();
    private int mLastOrientation = 0;
    private int mPipTaskId = -1;
    private int mPinnedStackId = -1;
    private final PinnedStackListener mPinnedStackListener = new PinnedStackListener();
    private final Runnable mResizePinnedStackRunnable = new Runnable() { // from class: com.android.systemui.pip.tv.PipManager.1
        @Override // java.lang.Runnable
        public void run() {
            PipManager pipManager = PipManager.this;
            pipManager.resizePinnedStack(pipManager.mResumeResizePinnedStackRunnableState);
        }
    };
    private final Runnable mClosePipRunnable = new Runnable() { // from class: com.android.systemui.pip.tv.PipManager.2
        @Override // java.lang.Runnable
        public void run() {
            PipManager.this.closePip();
        }
    };
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.systemui.pip.tv.PipManager.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.MEDIA_RESOURCE_GRANTED".equals(action)) {
                String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
                int resourceType = intent.getIntExtra("android.intent.extra.MEDIA_RESOURCE_TYPE", -1);
                if (packageNames != null && packageNames.length > 0 && resourceType == 0) {
                    PipManager.this.handleMediaResourceGranted(packageNames);
                }
            }
        }
    };
    private final MediaSessionManager.OnActiveSessionsChangedListener mActiveMediaSessionListener = new MediaSessionManager.OnActiveSessionsChangedListener() { // from class: com.android.systemui.pip.tv.PipManager.4
        @Override // android.media.session.MediaSessionManager.OnActiveSessionsChangedListener
        public void onActiveSessionsChanged(List<MediaController> controllers) {
            PipManager.this.updateMediaController(controllers);
        }
    };
    private TaskStackChangeListener mTaskStackListener = new TaskStackChangeListener() { // from class: com.android.systemui.pip.tv.PipManager.5
        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskStackChanged() {
            if (PipManager.DEBUG) {
                Log.d(PipManager.TAG, "onTaskStackChanged()");
            }
            if (PipManager.this.getState() != 0) {
                boolean hasPip = false;
                ActivityManager.StackInfo stackInfo = PipManager.this.getPinnedStackInfo();
                if (stackInfo == null || stackInfo.taskIds == null) {
                    Log.w(PipManager.TAG, "There is nothing in pinned stack");
                    PipManager.this.closePipInternal(false);
                    return;
                }
                int i = stackInfo.taskIds.length - 1;
                while (true) {
                    if (i < 0) {
                        break;
                    } else if (stackInfo.taskIds[i] != PipManager.this.mPipTaskId) {
                        i--;
                    } else {
                        hasPip = true;
                        break;
                    }
                }
                if (!hasPip) {
                    PipManager.this.closePipInternal(true);
                    return;
                }
            }
            if (PipManager.this.getState() == 1) {
                Rect bounds = PipManager.this.isSettingsShown() ? PipManager.this.mSettingsPipBounds : PipManager.this.mDefaultPipBounds;
                if (PipManager.this.mPipBounds != bounds) {
                    PipManager.this.mPipBounds = bounds;
                    PipManager.this.resizePinnedStack(1);
                }
            }
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
            if (PipManager.DEBUG) {
                Log.d(PipManager.TAG, "onActivityPinned()");
            }
            ActivityManager.StackInfo stackInfo = PipManager.this.getPinnedStackInfo();
            if (stackInfo == null) {
                Log.w(PipManager.TAG, "Cannot find pinned stack");
                return;
            }
            if (PipManager.DEBUG) {
                Log.d(PipManager.TAG, "PINNED_STACK:" + stackInfo);
            }
            PipManager.this.mPinnedStackId = stackInfo.stackId;
            PipManager.this.mPipTaskId = stackInfo.taskIds[stackInfo.taskIds.length - 1];
            PipManager.this.mPipComponentName = ComponentName.unflattenFromString(stackInfo.taskNames[stackInfo.taskNames.length - 1]);
            PipManager.this.mState = 1;
            PipManager pipManager = PipManager.this;
            pipManager.mCurrentPipBounds = pipManager.mPipBounds;
            PipManager.this.mMediaSessionManager.addOnActiveSessionsChangedListener(PipManager.this.mActiveMediaSessionListener, null);
            PipManager pipManager2 = PipManager.this;
            pipManager2.updateMediaController(pipManager2.mMediaSessionManager.getActiveSessions(null));
            for (int i = PipManager.this.mListeners.size() - 1; i >= 0; i--) {
                ((Listener) PipManager.this.mListeners.get(i)).onPipEntered();
            }
            PipManager.this.updatePipVisibility(true);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onPinnedActivityRestartAttempt(boolean clearedTask) {
            if (PipManager.DEBUG) {
                Log.d(PipManager.TAG, "onPinnedActivityRestartAttempt()");
            }
            PipManager.this.movePipToFullscreen();
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onPinnedStackAnimationEnded() {
            if (PipManager.DEBUG) {
                Log.d(PipManager.TAG, "onPinnedStackAnimationEnded()");
            }
            if (PipManager.this.getState() == 2) {
                PipManager.this.showPipMenu();
            }
        }
    };

    /* loaded from: classes21.dex */
    public interface Listener {
        void onMoveToFullscreen();

        void onPipActivityClosed();

        void onPipEntered();

        void onPipMenuActionsChanged(ParceledListSlice parceledListSlice);

        void onPipResizeAboutToStart();

        void onShowPipMenu();
    }

    /* loaded from: classes21.dex */
    public interface MediaListener {
        void onMediaControllerChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class PinnedStackListener extends IPinnedStackListener.Stub {
        private PinnedStackListener() {
        }

        public void onListenerRegistered(IPinnedStackController controller) {
        }

        public void onImeVisibilityChanged(boolean imeVisible, int imeHeight) {
            if (PipManager.this.mState == 1 && PipManager.this.mImeVisible != imeVisible) {
                if (imeVisible) {
                    PipManager.this.mPipBounds.offset(0, -imeHeight);
                    PipManager.this.mImeHeightAdjustment = imeHeight;
                } else {
                    PipManager.this.mPipBounds.offset(0, PipManager.this.mImeHeightAdjustment);
                }
                PipManager.this.mImeVisible = imeVisible;
                PipManager.this.resizePinnedStack(1);
            }
        }

        public void onShelfVisibilityChanged(boolean shelfVisible, int shelfHeight) {
        }

        public void onMinimizedStateChanged(boolean isMinimized) {
        }

        public void onMovementBoundsChanged(Rect insetBounds, final Rect normalBounds, Rect animatingBounds, boolean fromImeAdjustment, boolean fromShelfAdjustment, int displayRotation) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.tv.-$$Lambda$PipManager$PinnedStackListener$_5-G38rv0jyzaZL9eAwuaWlTGU4
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onMovementBoundsChanged$0$PipManager$PinnedStackListener(normalBounds);
                }
            });
        }

        public /* synthetic */ void lambda$onMovementBoundsChanged$0$PipManager$PinnedStackListener(Rect normalBounds) {
            PipManager.this.mDefaultPipBounds.set(normalBounds);
        }

        public void onActionsChanged(ParceledListSlice actions) {
            PipManager.this.mCustomActions = actions;
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.tv.-$$Lambda$PipManager$PinnedStackListener$KRSqnvGtvsFkEwCqcSExZLuYv1k
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onActionsChanged$1$PipManager$PinnedStackListener();
                }
            });
        }

        public /* synthetic */ void lambda$onActionsChanged$1$PipManager$PinnedStackListener() {
            for (int i = PipManager.this.mListeners.size() - 1; i >= 0; i--) {
                ((Listener) PipManager.this.mListeners.get(i)).onPipMenuActionsChanged(PipManager.this.mCustomActions);
            }
        }
    }

    private PipManager() {
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void initialize(Context context) {
        if (this.mInitialized) {
            return;
        }
        this.mInitialized = true;
        this.mContext = context;
        this.mActivityManager = ActivityManager.getService();
        this.mActivityTaskManager = ActivityTaskManager.getService();
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_RESOURCE_GRANTED");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, intentFilter, null, null);
        if (sSettingsPackageAndClassNamePairList == null) {
            String[] settings = this.mContext.getResources().getStringArray(R.array.tv_pip_settings_class_name);
            sSettingsPackageAndClassNamePairList = new ArrayList();
            if (settings != null) {
                for (int i = 0; i < settings.length; i++) {
                    Pair<String, String> entry = null;
                    String[] packageAndClassName = settings[i].split(SETTINGS_PACKAGE_AND_CLASS_DELIMITER);
                    int length = packageAndClassName.length;
                    if (length == 1) {
                        entry = Pair.create(packageAndClassName[0], null);
                    } else if (length == 2 && packageAndClassName[1] != null) {
                        entry = Pair.create(packageAndClassName[0], packageAndClassName[1].startsWith(".") ? packageAndClassName[0] + packageAndClassName[1] : packageAndClassName[1]);
                    }
                    if (entry != null) {
                        sSettingsPackageAndClassNamePairList.add(entry);
                    } else {
                        Log.w(TAG, "Ignoring malformed settings name " + settings[i]);
                    }
                }
            }
        }
        Configuration initialConfig = this.mContext.getResources().getConfiguration();
        this.mLastOrientation = initialConfig.orientation;
        loadConfigurationsAndApply(initialConfig);
        this.mMediaSessionManager = (MediaSessionManager) this.mContext.getSystemService("media_session");
        try {
            this.mWindowManager.registerPinnedStackListener(0, this.mPinnedStackListener);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register pinned stack listener", e);
        }
        this.mPipNotification = new PipNotification(context);
    }

    private void loadConfigurationsAndApply(Configuration newConfig) {
        if (this.mLastOrientation != newConfig.orientation) {
            this.mLastOrientation = newConfig.orientation;
            return;
        }
        Resources res = this.mContext.getResources();
        this.mSettingsPipBounds = Rect.unflattenFromString(res.getString(R.string.pip_settings_bounds));
        this.mMenuModePipBounds = Rect.unflattenFromString(res.getString(R.string.pip_menu_bounds));
        this.mPipBounds = isSettingsShown() ? this.mSettingsPipBounds : this.mDefaultPipBounds;
        resizePinnedStack(getPinnedStackInfo() == null ? 0 : 1);
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void onConfigurationChanged(Configuration newConfig) {
        loadConfigurationsAndApply(newConfig);
        this.mPipNotification.onConfigurationChanged(this.mContext);
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void showPictureInPictureMenu() {
        if (getState() == 1) {
            resizePinnedStack(2);
        }
    }

    public void closePip() {
        closePipInternal(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closePipInternal(boolean removePipStack) {
        this.mState = 0;
        this.mPipTaskId = -1;
        this.mPipMediaController = null;
        this.mMediaSessionManager.removeOnActiveSessionsChangedListener(this.mActiveMediaSessionListener);
        if (removePipStack) {
            try {
                try {
                    this.mActivityTaskManager.removeStack(this.mPinnedStackId);
                } catch (RemoteException e) {
                    Log.e(TAG, "removeStack failed", e);
                }
            } finally {
                this.mPinnedStackId = -1;
            }
        }
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onPipActivityClosed();
        }
        this.mHandler.removeCallbacks(this.mClosePipRunnable);
        updatePipVisibility(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void movePipToFullscreen() {
        this.mPipTaskId = -1;
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onMoveToFullscreen();
        }
        resizePinnedStack(0);
        updatePipVisibility(false);
    }

    public void suspendPipResizing(int reason) {
        if (DEBUG) {
            Log.d(TAG, "suspendPipResizing() reason=" + reason + " callers=" + Debug.getCallers(2));
        }
        this.mSuspendPipResizingReason |= reason;
    }

    public void resumePipResizing(int reason) {
        if ((this.mSuspendPipResizingReason & reason) == 0) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "resumePipResizing() reason=" + reason + " callers=" + Debug.getCallers(2));
        }
        this.mSuspendPipResizingReason &= ~reason;
        this.mHandler.post(this.mResizePinnedStackRunnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void resizePinnedStack(int state) {
        if (DEBUG) {
            Log.d(TAG, "resizePinnedStack() state=" + state, new Exception());
        }
        boolean wasStateNoPip = this.mState == 0;
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onPipResizeAboutToStart();
        }
        int i2 = this.mSuspendPipResizingReason;
        if (i2 != 0) {
            this.mResumeResizePinnedStackRunnableState = state;
            if (DEBUG) {
                Log.d(TAG, "resizePinnedStack() deferring mSuspendPipResizingReason=" + this.mSuspendPipResizingReason + " mResumeResizePinnedStackRunnableState=" + this.mResumeResizePinnedStackRunnableState);
                return;
            }
            return;
        }
        this.mState = state;
        int i3 = this.mState;
        if (i3 == 0) {
            this.mCurrentPipBounds = null;
            if (wasStateNoPip) {
                return;
            }
        } else if (i3 == 1) {
            this.mCurrentPipBounds = this.mPipBounds;
        } else if (i3 == 2) {
            this.mCurrentPipBounds = this.mMenuModePipBounds;
        } else {
            this.mCurrentPipBounds = this.mPipBounds;
        }
        try {
            this.mActivityTaskManager.resizeStack(this.mPinnedStackId, this.mCurrentPipBounds, true, true, true, -1);
        } catch (RemoteException e) {
            Log.e(TAG, "resizeStack failed", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getState() {
        if (this.mSuspendPipResizingReason != 0) {
            return this.mResumeResizePinnedStackRunnableState;
        }
        return this.mState;
    }

    public Rect getPipBounds() {
        return this.mPipBounds;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showPipMenu() {
        if (DEBUG) {
            Log.d(TAG, "showPipMenu()");
        }
        this.mState = 2;
        for (int i = this.mListeners.size() - 1; i >= 0; i--) {
            this.mListeners.get(i).onShowPipMenu();
        }
        Intent intent = new Intent(this.mContext, PipMenuActivity.class);
        intent.setFlags(268435456);
        intent.putExtra("custom_actions", (Parcelable) this.mCustomActions);
        this.mContext.startActivity(intent);
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    public void addMediaListener(MediaListener listener) {
        this.mMediaListeners.add(listener);
    }

    public void removeMediaListener(MediaListener listener) {
        this.mMediaListeners.remove(listener);
    }

    public boolean isPipShown() {
        return this.mState != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ActivityManager.StackInfo getPinnedStackInfo() {
        try {
            ActivityManager.StackInfo stackInfo = ActivityTaskManager.getService().getStackInfo(2, 0);
            return stackInfo;
        } catch (RemoteException e) {
            Log.e(TAG, "getStackInfo failed", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMediaResourceGranted(String[] packageNames) {
        if (getState() == 0) {
            this.mLastPackagesResourceGranted = packageNames;
            return;
        }
        boolean requestedFromLastPackages = false;
        String[] strArr = this.mLastPackagesResourceGranted;
        if (strArr != null) {
            boolean requestedFromLastPackages2 = false;
            for (String packageName : strArr) {
                int length = packageNames.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        String newPackageName = packageNames[i];
                        if (!TextUtils.equals(newPackageName, packageName)) {
                            i++;
                        } else {
                            requestedFromLastPackages2 = true;
                            break;
                        }
                    }
                }
            }
            requestedFromLastPackages = requestedFromLastPackages2;
        }
        this.mLastPackagesResourceGranted = packageNames;
        if (!requestedFromLastPackages) {
            closePip();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaController(List<MediaController> controllers) {
        MediaController mediaController = null;
        if (controllers != null && getState() != 0 && this.mPipComponentName != null) {
            int i = controllers.size() - 1;
            while (true) {
                if (i < 0) {
                    break;
                }
                MediaController controller = controllers.get(i);
                if (controller.getPackageName().equals(this.mPipComponentName.getPackageName())) {
                    mediaController = controller;
                    break;
                }
                i--;
            }
        }
        if (this.mPipMediaController != mediaController) {
            this.mPipMediaController = mediaController;
            for (int i2 = this.mMediaListeners.size() - 1; i2 >= 0; i2--) {
                this.mMediaListeners.get(i2).onMediaControllerChanged();
            }
            if (this.mPipMediaController == null) {
                this.mHandler.postDelayed(this.mClosePipRunnable, 3000L);
            } else {
                this.mHandler.removeCallbacks(this.mClosePipRunnable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaController getMediaController() {
        return this.mPipMediaController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPlaybackState() {
        MediaController mediaController = this.mPipMediaController;
        if (mediaController == null || mediaController.getPlaybackState() == null) {
            return 2;
        }
        int state = this.mPipMediaController.getPlaybackState().getState();
        boolean isPlaying = state == 6 || state == 8 || state == 3 || state == 4 || state == 5 || state == 9 || state == 10;
        long actions = this.mPipMediaController.getPlaybackState().getActions();
        if (isPlaying || (4 & actions) == 0) {
            return (!isPlaying || (2 & actions) == 0) ? 2 : 0;
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSettingsShown() {
        String className;
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks = this.mActivityTaskManager.getTasks(1);
            if (runningTasks.isEmpty()) {
                return false;
            }
            ComponentName topActivity = runningTasks.get(0).topActivity;
            for (Pair<String, String> componentName : sSettingsPackageAndClassNamePairList) {
                String packageName = (String) componentName.first;
                if (topActivity.getPackageName().equals(packageName) && ((className = (String) componentName.second) == null || topActivity.getClassName().equals(className))) {
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            Log.d(TAG, "Failed to detect top activity", e);
            return false;
        }
    }

    public static PipManager getInstance() {
        if (sPipManager == null) {
            sPipManager = new PipManager();
        }
        return sPipManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePipVisibility(final boolean visible) {
        ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).submit(new Runnable() { // from class: com.android.systemui.pip.tv.-$$Lambda$PipManager$B3cwmVrFFG3e6pUajgQn8FpuCeM
            @Override // java.lang.Runnable
            public final void run() {
                WindowManagerWrapper.getInstance().setPipVisibility(visible);
            }
        });
    }
}
