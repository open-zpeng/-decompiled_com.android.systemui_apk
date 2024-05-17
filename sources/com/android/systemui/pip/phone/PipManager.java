package com.android.systemui.pip.phone;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.IPinnedStackController;
import android.view.IPinnedStackListener;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.pip.BasePipManager;
import com.android.systemui.pip.phone.PipManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputConsumerController;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class PipManager implements BasePipManager {
    private static final String TAG = "PipManager";
    private static PipManager sPipController;
    private IActivityManager mActivityManager;
    private IActivityTaskManager mActivityTaskManager;
    private PipAppOpsListener mAppOpsListener;
    private Context mContext;
    private InputConsumerController mInputConsumerController;
    private PipMediaController mMediaController;
    private PipMenuActivityController mMenuController;
    private PipTouchHandler mTouchHandler;
    private Handler mHandler = new Handler();
    private final PinnedStackListener mPinnedStackListener = new PinnedStackListener(this, null);
    TaskStackChangeListener mTaskStackListener = new AnonymousClass1();

    /* renamed from: com.android.systemui.pip.phone.PipManager$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 extends TaskStackChangeListener {
        AnonymousClass1() {
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
            PipManager.this.mTouchHandler.onActivityPinned();
            PipManager.this.mMediaController.onActivityPinned();
            PipManager.this.mMenuController.onActivityPinned();
            PipManager.this.mAppOpsListener.onActivityPinned(packageName);
            ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).submit(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$1$GurLWXFKpAPDop_aRGndKBjZCWU
                @Override // java.lang.Runnable
                public final void run() {
                    WindowManagerWrapper.getInstance().setPipVisibility(true);
                }
            });
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityUnpinned() {
            Pair<ComponentName, Integer> topPipActivityInfo = PipUtils.getTopPinnedActivity(PipManager.this.mContext, PipManager.this.mActivityManager);
            final ComponentName topActivity = (ComponentName) topPipActivityInfo.first;
            PipManager.this.mMenuController.onActivityUnpinned();
            PipManager.this.mTouchHandler.onActivityUnpinned(topActivity);
            PipManager.this.mAppOpsListener.onActivityUnpinned();
            ((UiOffloadThread) Dependency.get(UiOffloadThread.class)).submit(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$1$ngvLEQ68U0fQkcsOpQTOX3GlNKk
                @Override // java.lang.Runnable
                public final void run() {
                    ComponentName componentName = topActivity;
                    WindowManagerWrapper.getInstance().setPipVisibility(topActivity != null);
                }
            });
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onPinnedStackAnimationStarted() {
            PipManager.this.mTouchHandler.setTouchEnabled(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onPinnedStackAnimationEnded() {
            PipManager.this.mTouchHandler.setTouchEnabled(true);
            PipManager.this.mTouchHandler.onPinnedStackAnimationEnded();
            PipManager.this.mMenuController.onPinnedStackAnimationEnded();
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onPinnedActivityRestartAttempt(boolean clearedTask) {
            PipManager.this.mTouchHandler.getMotionHelper().expandPip(clearedTask);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class PinnedStackListener extends IPinnedStackListener.Stub {
        private PinnedStackListener() {
        }

        /* synthetic */ PinnedStackListener(PipManager x0, AnonymousClass1 x1) {
            this();
        }

        public void onListenerRegistered(final IPinnedStackController controller) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$fsM0yPTeQnwLCmc8K2TS4ZFeBWc
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onListenerRegistered$0$PipManager$PinnedStackListener(controller);
                }
            });
        }

        public /* synthetic */ void lambda$onListenerRegistered$0$PipManager$PinnedStackListener(IPinnedStackController controller) {
            PipManager.this.mTouchHandler.setPinnedStackController(controller);
        }

        public void onImeVisibilityChanged(final boolean imeVisible, final int imeHeight) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$VBLjn70VeOT58ISp8JJdGGwiLRI
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onImeVisibilityChanged$1$PipManager$PinnedStackListener(imeVisible, imeHeight);
                }
            });
        }

        public /* synthetic */ void lambda$onImeVisibilityChanged$1$PipManager$PinnedStackListener(boolean imeVisible, int imeHeight) {
            PipManager.this.mTouchHandler.onImeVisibilityChanged(imeVisible, imeHeight);
        }

        public void onShelfVisibilityChanged(final boolean shelfVisible, final int shelfHeight) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$bf4e5rlYRO_U_i4UtAT1QucT53g
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onShelfVisibilityChanged$2$PipManager$PinnedStackListener(shelfVisible, shelfHeight);
                }
            });
        }

        public /* synthetic */ void lambda$onShelfVisibilityChanged$2$PipManager$PinnedStackListener(boolean shelfVisible, int shelfHeight) {
            PipManager.this.mTouchHandler.onShelfVisibilityChanged(shelfVisible, shelfHeight);
        }

        public void onMinimizedStateChanged(final boolean isMinimized) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$BUR7BmLfjK0NpOw2OLHQV6xTO5k
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onMinimizedStateChanged$3$PipManager$PinnedStackListener(isMinimized);
                }
            });
        }

        public /* synthetic */ void lambda$onMinimizedStateChanged$3$PipManager$PinnedStackListener(boolean isMinimized) {
            PipManager.this.mTouchHandler.setMinimizedState(isMinimized, true);
        }

        public void onMovementBoundsChanged(final Rect insetBounds, final Rect normalBounds, final Rect animatingBounds, final boolean fromImeAdjustment, final boolean fromShelfAdjustment, final int displayRotation) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$qj7-lqmu1a4XOuu8emxk_Cwvcxo
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onMovementBoundsChanged$4$PipManager$PinnedStackListener(insetBounds, normalBounds, animatingBounds, fromImeAdjustment, fromShelfAdjustment, displayRotation);
                }
            });
        }

        public /* synthetic */ void lambda$onMovementBoundsChanged$4$PipManager$PinnedStackListener(Rect insetBounds, Rect normalBounds, Rect animatingBounds, boolean fromImeAdjustment, boolean fromShelfAdjustment, int displayRotation) {
            PipManager.this.mTouchHandler.onMovementBoundsChanged(insetBounds, normalBounds, animatingBounds, fromImeAdjustment, fromShelfAdjustment, displayRotation);
        }

        public void onActionsChanged(final ParceledListSlice actions) {
            PipManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.pip.phone.-$$Lambda$PipManager$PinnedStackListener$JU_-Gjrp-L4fTB-9HLmwOZwFKXw
                @Override // java.lang.Runnable
                public final void run() {
                    PipManager.PinnedStackListener.this.lambda$onActionsChanged$5$PipManager$PinnedStackListener(actions);
                }
            });
        }

        public /* synthetic */ void lambda$onActionsChanged$5$PipManager$PinnedStackListener(ParceledListSlice actions) {
            PipManager.this.mMenuController.setAppActions(actions);
        }
    }

    private PipManager() {
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void initialize(Context context) {
        this.mContext = context;
        this.mActivityManager = ActivityManager.getService();
        this.mActivityTaskManager = ActivityTaskManager.getService();
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(this.mPinnedStackListener);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to register pinned stack listener", e);
        }
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        this.mInputConsumerController = InputConsumerController.getPipInputConsumer();
        this.mMediaController = new PipMediaController(context, this.mActivityManager);
        this.mMenuController = new PipMenuActivityController(context, this.mActivityManager, this.mMediaController, this.mInputConsumerController);
        this.mTouchHandler = new PipTouchHandler(context, this.mActivityManager, this.mActivityTaskManager, this.mMenuController, this.mInputConsumerController);
        this.mAppOpsListener = new PipAppOpsListener(context, this.mActivityManager, this.mTouchHandler.getMotionHelper());
        try {
            ActivityManager.StackInfo stackInfo = this.mActivityTaskManager.getStackInfo(2, 0);
            if (stackInfo != null) {
                this.mInputConsumerController.registerInputConsumer();
            }
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void onConfigurationChanged(Configuration newConfig) {
        this.mTouchHandler.onConfigurationChanged();
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void expandPip() {
        this.mTouchHandler.getMotionHelper().expandPip(false);
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void hidePipMenu(Runnable onStartCallback, Runnable onEndCallback) {
        this.mMenuController.hideMenu(onStartCallback, onEndCallback);
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void showPictureInPictureMenu() {
        this.mTouchHandler.showPictureInPictureMenu();
    }

    public static PipManager getInstance() {
        if (sPipController == null) {
            sPipController = new PipManager();
        }
        return sPipController;
    }

    @Override // com.android.systemui.pip.BasePipManager
    public void dump(PrintWriter pw) {
        pw.println(TAG);
        this.mInputConsumerController.dump(pw, "  ");
        this.mMenuController.dump(pw, "  ");
        this.mTouchHandler.dump(pw, "  ");
    }
}
