package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.systemui.Dependency;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: PulseExpansionHandler.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u0007\u0018\u0000 [2\u00020\u0001:\u0002[\\B7\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0002\u0010\u000eJ\b\u0010A\u001a\u00020BH\u0002J\u0018\u0010C\u001a\u00020B2\u0006\u0010D\u001a\u00020!2\u0006\u0010E\u001a\u00020!H\u0002J\u001a\u0010F\u001a\u0004\u0018\u00010-2\u0006\u0010D\u001a\u00020!2\u0006\u0010E\u001a\u00020!H\u0002J\b\u0010G\u001a\u00020BH\u0002J\u0010\u0010H\u001a\u00020\u00102\u0006\u0010I\u001a\u00020JH\u0002J\u0010\u0010K\u001a\u00020\u00102\u0006\u0010I\u001a\u00020JH\u0016J\u0006\u0010L\u001a\u00020BJ\u0010\u0010M\u001a\u00020\u00102\u0006\u0010I\u001a\u00020JH\u0016J\b\u0010N\u001a\u00020BH\u0002J\u0010\u0010O\u001a\u00020B2\u0006\u0010P\u001a\u00020-H\u0002J\b\u0010Q\u001a\u00020BH\u0002J\u0010\u0010R\u001a\u00020B2\u0006\u0010S\u001a\u00020!H\u0002J\u000e\u0010T\u001a\u00020B2\u0006\u0010U\u001a\u00020\u0010J\u001e\u0010V\u001a\u00020B2\u0006\u0010=\u001a\u00020>2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010;\u001a\u00020<J\u0018\u0010W\u001a\u00020B2\u0006\u0010P\u001a\u00020-2\u0006\u0010X\u001a\u00020\u0010H\u0002J\u0010\u0010Y\u001a\u00020B2\u0006\u0010Z\u001a\u00020!H\u0002R\u001a\u0010\u000f\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0012\"\u0004\b\u0019\u0010\u0014R\u0014\u0010\u001a\u001a\u00020\u00108BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u0012R\u001e\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u001e\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u001b\u001a\u00020\u0010@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012R\u000e\u0010\u001f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020!X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020!X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020'X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u0004\u0018\u00010)X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u0004\u0018\u00010-X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020!X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00101\u001a\u00020!X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u00102\u001a\u0004\u0018\u000103X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u00105\"\u0004\b6\u00107R\u001a\u00108\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b9\u0010\u0012\"\u0004\b:\u0010\u0014R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010;\u001a\u00020<X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010=\u001a\u00020>X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010?\u001a\u0004\u0018\u00010@X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006]"}, d2 = {"Lcom/android/systemui/statusbar/PulseExpansionHandler;", "Lcom/android/systemui/Gefingerpoken;", "context", "Landroid/content/Context;", "wakeUpCoordinator", "Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator;", "bypassController", "Lcom/android/systemui/statusbar/phone/KeyguardBypassController;", "headsUpManager", "Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;", "roundnessManager", "Lcom/android/systemui/statusbar/notification/stack/NotificationRoundnessManager;", "statusBarStateController", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController;", "(Landroid/content/Context;Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator;Lcom/android/systemui/statusbar/phone/KeyguardBypassController;Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;Lcom/android/systemui/statusbar/notification/stack/NotificationRoundnessManager;Lcom/android/systemui/plugins/statusbar/StatusBarStateController;)V", "bouncerShowing", "", "getBouncerShowing", "()Z", "setBouncerShowing", "(Z)V", "expansionCallback", "Lcom/android/systemui/statusbar/PulseExpansionHandler$ExpansionCallback;", VuiConstants.ELEMENT_VALUE, "isExpanding", "setExpanding", "isFalseTouch", "<set-?>", "isWakingToShadeLocked", "leavingLockscreen", "getLeavingLockscreen", "mDraggedFarEnough", "mEmptyDragAmount", "", "mFalsingManager", "Lcom/android/systemui/plugins/FalsingManager;", "mInitialTouchX", "mInitialTouchY", "mMinDragDistance", "", "mPowerManager", "Landroid/os/PowerManager;", "mPulsing", "mReachedWakeUpHeight", "mStartingChild", "Lcom/android/systemui/statusbar/notification/row/ExpandableView;", "mTemp2", "", "mTouchSlop", "mWakeUpHeight", "pulseExpandAbortListener", "Ljava/lang/Runnable;", "getPulseExpandAbortListener", "()Ljava/lang/Runnable;", "setPulseExpandAbortListener", "(Ljava/lang/Runnable;)V", "qsExpanded", "getQsExpanded", "setQsExpanded", "shadeController", "Lcom/android/systemui/statusbar/phone/ShadeController;", "stackScroller", "Lcom/android/systemui/statusbar/notification/stack/NotificationStackScrollLayout;", "velocityTracker", "Landroid/view/VelocityTracker;", "cancelExpansion", "", "captureStartingChild", "x", "y", "findView", "finishExpansion", "maybeStartExpansion", "event", "Landroid/view/MotionEvent;", "onInterceptTouchEvent", "onStartedWakingUp", "onTouchEvent", "recycleVelocityTracker", "reset", "child", "resetClock", "setEmptyDragAmount", "amount", "setPulsing", "pulsing", "setUp", "setUserLocked", "userLocked", "updateExpansionHeight", "height", "Companion", "ExpansionCallback", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class PulseExpansionHandler implements Gefingerpoken {
    private boolean bouncerShowing;
    private final KeyguardBypassController bypassController;
    private ExpansionCallback expansionCallback;
    private final HeadsUpManagerPhone headsUpManager;
    private boolean isExpanding;
    private boolean isWakingToShadeLocked;
    private boolean leavingLockscreen;
    private boolean mDraggedFarEnough;
    private float mEmptyDragAmount;
    private final FalsingManager mFalsingManager;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private final int mMinDragDistance;
    private final PowerManager mPowerManager;
    private boolean mPulsing;
    private boolean mReachedWakeUpHeight;
    private ExpandableView mStartingChild;
    private final int[] mTemp2;
    private final float mTouchSlop;
    private float mWakeUpHeight;
    @Nullable
    private Runnable pulseExpandAbortListener;
    private boolean qsExpanded;
    private final NotificationRoundnessManager roundnessManager;
    private ShadeController shadeController;
    private NotificationStackScrollLayout stackScroller;
    private final StatusBarStateController statusBarStateController;
    private VelocityTracker velocityTracker;
    private final NotificationWakeUpCoordinator wakeUpCoordinator;
    public static final Companion Companion = new Companion(null);
    private static final float RUBBERBAND_FACTOR_STATIC = 0.25f;
    private static final int SPRING_BACK_ANIMATION_LENGTH_MS = SPRING_BACK_ANIMATION_LENGTH_MS;
    private static final int SPRING_BACK_ANIMATION_LENGTH_MS = SPRING_BACK_ANIMATION_LENGTH_MS;

    /* compiled from: PulseExpansionHandler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006"}, d2 = {"Lcom/android/systemui/statusbar/PulseExpansionHandler$ExpansionCallback;", "", "setEmptyDragAmount", "", "amount", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public interface ExpansionCallback {
        void setEmptyDragAmount(float f);
    }

    @Inject
    public PulseExpansionHandler(@NotNull Context context, @NotNull NotificationWakeUpCoordinator wakeUpCoordinator, @NotNull KeyguardBypassController bypassController, @NotNull HeadsUpManagerPhone headsUpManager, @NotNull NotificationRoundnessManager roundnessManager, @NotNull StatusBarStateController statusBarStateController) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(wakeUpCoordinator, "wakeUpCoordinator");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        Intrinsics.checkParameterIsNotNull(headsUpManager, "headsUpManager");
        Intrinsics.checkParameterIsNotNull(roundnessManager, "roundnessManager");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        this.wakeUpCoordinator = wakeUpCoordinator;
        this.bypassController = bypassController;
        this.headsUpManager = headsUpManager;
        this.roundnessManager = roundnessManager;
        this.statusBarStateController = statusBarStateController;
        this.mTemp2 = new int[2];
        this.mMinDragDistance = context.getResources().getDimensionPixelSize(R.dimen.keyguard_drag_down_min_distance);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        Intrinsics.checkExpressionValueIsNotNull(viewConfiguration, "ViewConfiguration.get(context)");
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        Object obj = Dependency.get(FalsingManager.class);
        Intrinsics.checkExpressionValueIsNotNull(obj, "Dependency.get(FalsingManager::class.java)");
        this.mFalsingManager = (FalsingManager) obj;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    /* compiled from: PulseExpansionHandler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000¨\u0006\u0007"}, d2 = {"Lcom/android/systemui/statusbar/PulseExpansionHandler$Companion;", "", "()V", "RUBBERBAND_FACTOR_STATIC", "", "SPRING_BACK_ANIMATION_LENGTH_MS", "", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    public final boolean isExpanding() {
        return this.isExpanding;
    }

    private final void setExpanding(boolean value) {
        boolean changed = this.isExpanding != value;
        this.isExpanding = value;
        this.bypassController.setPulseExpanding(value);
        if (changed) {
            if (value) {
                NotificationEntry topEntry = this.headsUpManager.getTopEntry();
                if (topEntry != null) {
                    this.roundnessManager.setTrackingHeadsUp(topEntry.getRow());
                }
            } else {
                this.roundnessManager.setTrackingHeadsUp(null);
                if (!this.leavingLockscreen) {
                    this.bypassController.maybePerformPendingUnlock();
                    Runnable runnable = this.pulseExpandAbortListener;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
            this.headsUpManager.unpinAll(true);
        }
    }

    public final boolean getLeavingLockscreen() {
        return this.leavingLockscreen;
    }

    public final boolean isWakingToShadeLocked() {
        return this.isWakingToShadeLocked;
    }

    private final boolean isFalseTouch() {
        return this.mFalsingManager.isFalseTouch();
    }

    public final boolean getQsExpanded() {
        return this.qsExpanded;
    }

    public final void setQsExpanded(boolean z) {
        this.qsExpanded = z;
    }

    @Nullable
    public final Runnable getPulseExpandAbortListener() {
        return this.pulseExpandAbortListener;
    }

    public final void setPulseExpandAbortListener(@Nullable Runnable runnable) {
        this.pulseExpandAbortListener = runnable;
    }

    public final boolean getBouncerShowing() {
        return this.bouncerShowing;
    }

    public final void setBouncerShowing(boolean z) {
        this.bouncerShowing = z;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onInterceptTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        return maybeStartExpansion(event);
    }

    private final boolean maybeStartExpansion(MotionEvent event) {
        if (!this.wakeUpCoordinator.getCanShowPulsingHuns() || this.qsExpanded || this.bouncerShowing) {
            return false;
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            Intrinsics.throwNpe();
        }
        velocityTracker.addMovement(event);
        float x = event.getX();
        float y = event.getY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mDraggedFarEnough = false;
            setExpanding(false);
            this.leavingLockscreen = false;
            this.mStartingChild = null;
            this.mInitialTouchY = y;
            this.mInitialTouchX = x;
        } else if (actionMasked == 1) {
            recycleVelocityTracker();
        } else if (actionMasked == 2) {
            float h = y - this.mInitialTouchY;
            if (h > this.mTouchSlop && h > Math.abs(x - this.mInitialTouchX)) {
                this.mFalsingManager.onStartExpandingFromPulse();
                setExpanding(true);
                captureStartingChild(this.mInitialTouchX, this.mInitialTouchY);
                this.mInitialTouchY = y;
                this.mInitialTouchX = x;
                this.mWakeUpHeight = this.wakeUpCoordinator.getWakeUpHeight();
                this.mReachedWakeUpHeight = false;
                return true;
            }
        } else if (actionMasked == 3) {
            recycleVelocityTracker();
        }
        return false;
    }

    private final void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.velocityTracker = null;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        if (!this.isExpanding) {
            return maybeStartExpansion(event);
        }
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            Intrinsics.throwNpe();
        }
        velocityTracker.addMovement(event);
        float y = event.getY();
        float moveDistance = y - this.mInitialTouchY;
        int actionMasked = event.getActionMasked();
        if (actionMasked == 1) {
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 == null) {
                Intrinsics.throwNpe();
            }
            velocityTracker2.computeCurrentVelocity(1000);
            boolean canExpand = false;
            if (moveDistance > 0) {
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 == null) {
                    Intrinsics.throwNpe();
                }
                if (velocityTracker3.getYVelocity() > -1000 && this.statusBarStateController.getState() != 0) {
                    canExpand = true;
                }
            }
            if (!this.mFalsingManager.isUnlockingDisabled() && !isFalseTouch() && canExpand) {
                finishExpansion();
            } else {
                cancelExpansion();
            }
            recycleVelocityTracker();
        } else if (actionMasked == 2) {
            updateExpansionHeight(moveDistance);
        } else if (actionMasked == 3) {
            cancelExpansion();
            recycleVelocityTracker();
        }
        return this.isExpanding;
    }

    private final void finishExpansion() {
        resetClock();
        ExpandableView expandableView = this.mStartingChild;
        if (expandableView != null) {
            if (expandableView == null) {
                Intrinsics.throwNpe();
            }
            setUserLocked(expandableView, false);
            this.mStartingChild = null;
        }
        ShadeController shadeController = this.shadeController;
        if (shadeController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("shadeController");
        }
        if (shadeController.isDozing()) {
            this.isWakingToShadeLocked = true;
            this.wakeUpCoordinator.setWillWakeUp(true);
            PowerManager powerManager = this.mPowerManager;
            if (powerManager == null) {
                Intrinsics.throwNpe();
            }
            powerManager.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:PULSEDRAG");
        }
        ShadeController shadeController2 = this.shadeController;
        if (shadeController2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("shadeController");
        }
        shadeController2.goToLockedShade(this.mStartingChild);
        this.leavingLockscreen = true;
        setExpanding(false);
        ExpandableView expandableView2 = this.mStartingChild;
        if (expandableView2 instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) expandableView2;
            if (row == null) {
                Intrinsics.throwNpe();
            }
            row.onExpandedByGesture(true);
        }
    }

    private final void updateExpansionHeight(float height) {
        float expansionHeight;
        float expansionHeight2 = Math.max(height, 0.0f);
        if (!this.mReachedWakeUpHeight && height > this.mWakeUpHeight) {
            this.mReachedWakeUpHeight = true;
        }
        ExpandableView child = this.mStartingChild;
        if (child != null) {
            if (child == null) {
                Intrinsics.throwNpe();
            }
            int newHeight = Math.min((int) (child.getCollapsedHeight() + expansionHeight2), child.getMaxContentHeight());
            child.setActualHeight(newHeight);
            expansionHeight = Math.max(newHeight, expansionHeight2);
        } else {
            float target = this.mReachedWakeUpHeight ? this.mWakeUpHeight : 0.0f;
            this.wakeUpCoordinator.setNotificationsVisibleForExpansion(height > target, true, true);
            expansionHeight = Math.max(this.mWakeUpHeight, expansionHeight2);
        }
        float emptyDragAmount = this.wakeUpCoordinator.setPulseHeight(expansionHeight);
        setEmptyDragAmount(RUBBERBAND_FACTOR_STATIC * emptyDragAmount);
    }

    private final void captureStartingChild(float x, float y) {
        if (this.mStartingChild == null && !this.bypassController.getBypassEnabled()) {
            this.mStartingChild = findView(x, y);
            ExpandableView expandableView = this.mStartingChild;
            if (expandableView != null) {
                if (expandableView == null) {
                    Intrinsics.throwNpe();
                }
                setUserLocked(expandableView, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setEmptyDragAmount(float amount) {
        this.mEmptyDragAmount = amount;
        ExpansionCallback expansionCallback = this.expansionCallback;
        if (expansionCallback == null) {
            Intrinsics.throwUninitializedPropertyAccessException("expansionCallback");
        }
        expansionCallback.setEmptyDragAmount(amount);
    }

    private final void reset(final ExpandableView child) {
        if (child.getActualHeight() == child.getCollapsedHeight()) {
            setUserLocked(child, false);
            return;
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(child, "actualHeight", child.getActualHeight(), child.getCollapsedHeight());
        Intrinsics.checkExpressionValueIsNotNull(anim, "anim");
        anim.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        anim.setDuration(SPRING_BACK_ANIMATION_LENGTH_MS);
        anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.PulseExpansionHandler$reset$1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(@NotNull Animator animation) {
                Intrinsics.checkParameterIsNotNull(animation, "animation");
                PulseExpansionHandler.this.setUserLocked(child, false);
            }
        });
        anim.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setUserLocked(ExpandableView child, boolean userLocked) {
        if (child instanceof ExpandableNotificationRow) {
            ((ExpandableNotificationRow) child).setUserLocked(userLocked);
        }
    }

    private final void resetClock() {
        ValueAnimator anim = ValueAnimator.ofFloat(this.mEmptyDragAmount, 0.0f);
        Intrinsics.checkExpressionValueIsNotNull(anim, "anim");
        anim.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        anim.setDuration(SPRING_BACK_ANIMATION_LENGTH_MS);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.PulseExpansionHandler$resetClock$1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator animation) {
                PulseExpansionHandler pulseExpansionHandler = PulseExpansionHandler.this;
                Intrinsics.checkExpressionValueIsNotNull(animation, "animation");
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.Float");
                }
                pulseExpansionHandler.setEmptyDragAmount(((Float) animatedValue).floatValue());
            }
        });
        anim.start();
    }

    private final void cancelExpansion() {
        setExpanding(false);
        this.mFalsingManager.onExpansionFromPulseStopped();
        ExpandableView expandableView = this.mStartingChild;
        if (expandableView != null) {
            if (expandableView == null) {
                Intrinsics.throwNpe();
            }
            reset(expandableView);
            this.mStartingChild = null;
        } else {
            resetClock();
        }
        this.wakeUpCoordinator.setNotificationsVisibleForExpansion(false, true, false);
    }

    private final ExpandableView findView(float x, float y) {
        NotificationStackScrollLayout notificationStackScrollLayout = this.stackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stackScroller");
        }
        notificationStackScrollLayout.getLocationOnScreen(this.mTemp2);
        int[] iArr = this.mTemp2;
        float totalX = x + iArr[0];
        float totalY = y + iArr[1];
        NotificationStackScrollLayout notificationStackScrollLayout2 = this.stackScroller;
        if (notificationStackScrollLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stackScroller");
        }
        ExpandableView childAtRawPosition = notificationStackScrollLayout2.getChildAtRawPosition(totalX, totalY);
        if (childAtRawPosition != null && childAtRawPosition.isContentExpandable()) {
            return childAtRawPosition;
        }
        return null;
    }

    public final void setUp(@NotNull NotificationStackScrollLayout stackScroller, @NotNull ExpansionCallback expansionCallback, @NotNull ShadeController shadeController) {
        Intrinsics.checkParameterIsNotNull(stackScroller, "stackScroller");
        Intrinsics.checkParameterIsNotNull(expansionCallback, "expansionCallback");
        Intrinsics.checkParameterIsNotNull(shadeController, "shadeController");
        this.expansionCallback = expansionCallback;
        this.shadeController = shadeController;
        this.stackScroller = stackScroller;
    }

    public final void setPulsing(boolean pulsing) {
        this.mPulsing = pulsing;
    }

    public final void onStartedWakingUp() {
        this.isWakingToShadeLocked = false;
    }
}
