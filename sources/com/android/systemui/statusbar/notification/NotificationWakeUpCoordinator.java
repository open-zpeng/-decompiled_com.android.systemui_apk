package com.android.systemui.statusbar.notification;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.FloatProperty;
import android.view.animation.Interpolator;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationIconAreaController;
import com.android.systemui.statusbar.phone.PanelExpansionListener;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.JvmDefault;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: NotificationWakeUpCoordinator.kt */
@Singleton
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u008f\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b'*\u0001'\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003:\u0001oB'\b\u0007\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0002\u0010\fJ\u000e\u0010H\u001a\u00020I2\u0006\u0010J\u001a\u00020@J\u0006\u0010K\u001a\u00020\u001eJ\b\u0010L\u001a\u00020IH\u0002J\u0006\u0010M\u001a\u00020\u000eJ\u0010\u0010N\u001a\u00020I2\u0006\u0010O\u001a\u00020\u000eH\u0002J\u0018\u0010P\u001a\u00020I2\u0006\u0010Q\u001a\u00020\u001e2\u0006\u0010R\u001a\u00020\u001eH\u0016J\u0010\u0010S\u001a\u00020I2\u0006\u0010T\u001a\u00020\u000eH\u0016J\u0018\u0010U\u001a\u00020I2\u0006\u0010V\u001a\u00020#2\u0006\u0010W\u001a\u00020\u000eH\u0016J\u0018\u0010X\u001a\u00020I2\u0006\u0010Y\u001a\u00020\u001e2\u0006\u0010Z\u001a\u00020\u000eH\u0016J\u0010\u0010[\u001a\u00020I2\u0006\u0010\\\u001a\u00020=H\u0016J\u000e\u0010]\u001a\u00020I2\u0006\u0010J\u001a\u00020@J\u0016\u0010^\u001a\u00020I2\u0006\u0010Q\u001a\u00020\u001e2\u0006\u0010R\u001a\u00020\u001eJ \u0010_\u001a\u00020I2\u0006\u0010`\u001a\u00020\u000e2\u0006\u0010a\u001a\u00020\u000e2\u0006\u0010b\u001a\u00020\u000eH\u0002J\u001e\u0010c\u001a\u00020I2\u0006\u0010`\u001a\u00020\u000e2\u0006\u0010a\u001a\u00020\u000e2\u0006\u0010b\u001a\u00020\u000eJ\u000e\u0010d\u001a\u00020\u001e2\u0006\u0010e\u001a\u00020\u001eJ\u000e\u0010f\u001a\u00020I2\u0006\u0010g\u001a\u00020-J\u0010\u0010h\u001a\u00020I2\u0006\u0010i\u001a\u00020\u001eH\u0002J\b\u0010j\u001a\u00020\u000eH\u0002J\u0010\u0010k\u001a\u00020I2\u0006\u0010b\u001a\u00020\u000eH\u0002J\b\u0010l\u001a\u00020\u000eH\u0002J\b\u0010m\u001a\u00020IH\u0002J\u0018\u0010n\u001a\u00020I2\u0006\u0010a\u001a\u00020\u000e2\u0006\u0010b\u001a\u00020\u000eH\u0002R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e8F@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u0013\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020#0\"X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u00020'X\u0082\u0004¢\u0006\u0004\n\u0002\u0010(R\u000e\u0010)\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020-X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010/\u001a\u0004\u0018\u000100X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u00101\u001a\n 3*\u0004\u0018\u00010202X\u0082\u000e¢\u0006\u0002\n\u0000R$\u00105\u001a\u00020\u000e2\u0006\u00104\u001a\u00020\u000e@BX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b6\u0010\u0011\"\u0004\b7\u0010\u0016R\u000e\u00108\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R$\u00109\u001a\u00020\u000e2\u0006\u00104\u001a\u00020\u000e@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b:\u0010\u0011\"\u0004\b;\u0010\u0016R\u000e\u0010<\u001a\u00020=X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010>\u001a\u0012\u0012\u0004\u0012\u00020@0?j\b\u0012\u0004\u0012\u00020@`AX\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010B\u001a\u00020\u000e2\u0006\u00104\u001a\u00020\u000e@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bC\u0010\u0011\"\u0004\bD\u0010\u0016R$\u0010E\u001a\u00020\u000e2\u0006\u00104\u001a\u00020\u000e@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bF\u0010\u0011\"\u0004\bG\u0010\u0016\u0082\u0002\u0007\n\u0005\b\u0091(0\u0001¨\u0006p"}, d2 = {"Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator;", "Lcom/android/systemui/statusbar/policy/OnHeadsUpChangedListener;", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController$StateListener;", "Lcom/android/systemui/statusbar/phone/PanelExpansionListener;", "mContext", "Landroid/content/Context;", "mHeadsUpManagerPhone", "Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;", "statusBarStateController", "Lcom/android/systemui/plugins/statusbar/StatusBarStateController;", "bypassController", "Lcom/android/systemui/statusbar/phone/KeyguardBypassController;", "(Landroid/content/Context;Lcom/android/systemui/statusbar/phone/HeadsUpManagerPhone;Lcom/android/systemui/plugins/statusbar/StatusBarStateController;Lcom/android/systemui/statusbar/phone/KeyguardBypassController;)V", "<set-?>", "", "canShowPulsingHuns", "getCanShowPulsingHuns", "()Z", "collapsedEnoughToHide", "fullyAwake", "getFullyAwake", "setFullyAwake", "(Z)V", "iconAreaController", "Lcom/android/systemui/statusbar/phone/NotificationIconAreaController;", "getIconAreaController", "()Lcom/android/systemui/statusbar/phone/NotificationIconAreaController;", "setIconAreaController", "(Lcom/android/systemui/statusbar/phone/NotificationIconAreaController;)V", "mDozeAmount", "", "mDozeParameters", "Lcom/android/systemui/statusbar/phone/DozeParameters;", "mEntrySetToClearWhenFinished", "", "Lcom/android/systemui/statusbar/notification/collection/NotificationEntry;", "mLinearDozeAmount", "mLinearVisibilityAmount", "mNotificationVisibility", "com/android/systemui/statusbar/notification/NotificationWakeUpCoordinator$mNotificationVisibility$1", "Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator$mNotificationVisibility$1;", "mNotificationVisibleAmount", "mNotificationsVisible", "mNotificationsVisibleForExpansion", "mStackScroller", "Lcom/android/systemui/statusbar/notification/stack/NotificationStackScrollLayout;", "mVisibilityAmount", "mVisibilityAnimator", "Landroid/animation/ObjectAnimator;", "mVisibilityInterpolator", "Landroid/view/animation/Interpolator;", "kotlin.jvm.PlatformType", VuiConstants.ELEMENT_VALUE, "notificationsFullyHidden", "getNotificationsFullyHidden", "setNotificationsFullyHidden", "pulseExpanding", "pulsing", "getPulsing", "setPulsing", "state", "", "wakeUpListeners", "Ljava/util/ArrayList;", "Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator$WakeUpListener;", "Lkotlin/collections/ArrayList;", "wakingUp", "getWakingUp", "setWakingUp", "willWakeUp", "getWillWakeUp", "setWillWakeUp", "addListener", "", "listener", "getWakeUpHeight", "handleAnimationFinished", "isPulseExpanding", "notifyAnimationStart", "awake", "onDozeAmountChanged", "linear", "eased", "onDozingChanged", "isDozing", "onHeadsUpStateChanged", "entry", "isHeadsUp", "onPanelExpansionChanged", "expansion", "tracking", "onStateChanged", "newState", "removeListener", "setDozeAmount", "setNotificationsVisible", "visible", "animate", "increaseSpeed", "setNotificationsVisibleForExpansion", "setPulseHeight", "height", "setStackScroller", "stackScroller", "setVisibilityAmount", "visibilityAmount", "shouldAnimateVisibility", "startVisibilityAnimation", "updateDozeAmountIfBypass", "updateHideAmount", "updateNotificationVisibility", "WakeUpListener", "name"}, k = 1, mv = {1, 1, 13})
/* loaded from: classes21.dex */
public final class NotificationWakeUpCoordinator implements OnHeadsUpChangedListener, StatusBarStateController.StateListener, PanelExpansionListener {
    private final KeyguardBypassController bypassController;
    private boolean canShowPulsingHuns;
    private boolean collapsedEnoughToHide;
    private boolean fullyAwake;
    @NotNull
    public NotificationIconAreaController iconAreaController;
    private final Context mContext;
    private float mDozeAmount;
    private final DozeParameters mDozeParameters;
    private final Set<NotificationEntry> mEntrySetToClearWhenFinished;
    private final HeadsUpManagerPhone mHeadsUpManagerPhone;
    private float mLinearDozeAmount;
    private float mLinearVisibilityAmount;
    private final NotificationWakeUpCoordinator$mNotificationVisibility$1 mNotificationVisibility;
    private float mNotificationVisibleAmount;
    private boolean mNotificationsVisible;
    private boolean mNotificationsVisibleForExpansion;
    private NotificationStackScrollLayout mStackScroller;
    private float mVisibilityAmount;
    private ObjectAnimator mVisibilityAnimator;
    private Interpolator mVisibilityInterpolator;
    private boolean notificationsFullyHidden;
    private boolean pulseExpanding;
    private boolean pulsing;
    private int state;
    private final StatusBarStateController statusBarStateController;
    private final ArrayList<WakeUpListener> wakeUpListeners;
    private boolean wakingUp;
    private boolean willWakeUp;

    /* JADX WARN: Type inference failed for: r0v4, types: [com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator$mNotificationVisibility$1] */
    @Inject
    public NotificationWakeUpCoordinator(@NotNull Context mContext, @NotNull HeadsUpManagerPhone mHeadsUpManagerPhone, @NotNull StatusBarStateController statusBarStateController, @NotNull KeyguardBypassController bypassController) {
        Intrinsics.checkParameterIsNotNull(mContext, "mContext");
        Intrinsics.checkParameterIsNotNull(mHeadsUpManagerPhone, "mHeadsUpManagerPhone");
        Intrinsics.checkParameterIsNotNull(statusBarStateController, "statusBarStateController");
        Intrinsics.checkParameterIsNotNull(bypassController, "bypassController");
        this.mContext = mContext;
        this.mHeadsUpManagerPhone = mHeadsUpManagerPhone;
        this.statusBarStateController = statusBarStateController;
        this.bypassController = bypassController;
        this.mNotificationVisibility = new FloatProperty<NotificationWakeUpCoordinator>("notificationVisibility") { // from class: com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator$mNotificationVisibility$1
            @Override // android.util.FloatProperty
            public void setValue(@NotNull NotificationWakeUpCoordinator coordinator, float value) {
                Intrinsics.checkParameterIsNotNull(coordinator, "coordinator");
                coordinator.setVisibilityAmount(value);
            }

            @Override // android.util.Property
            @Nullable
            public Float get(@NotNull NotificationWakeUpCoordinator coordinator) {
                float f;
                Intrinsics.checkParameterIsNotNull(coordinator, "coordinator");
                f = coordinator.mLinearVisibilityAmount;
                return Float.valueOf(f);
            }
        };
        this.mVisibilityInterpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
        this.mEntrySetToClearWhenFinished = new LinkedHashSet();
        this.wakeUpListeners = new ArrayList<>();
        this.state = 1;
        this.mHeadsUpManagerPhone.addListener(this);
        this.statusBarStateController.addCallback(this);
        DozeParameters dozeParameters = DozeParameters.getInstance(this.mContext);
        Intrinsics.checkExpressionValueIsNotNull(dozeParameters, "DozeParameters.getInstance(mContext)");
        this.mDozeParameters = dozeParameters;
        addListener(new WakeUpListener() { // from class: com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.1
            @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
            public void onFullyHiddenChanged(boolean isFullyHidden) {
                if (isFullyHidden && NotificationWakeUpCoordinator.this.mNotificationsVisibleForExpansion) {
                    NotificationWakeUpCoordinator.this.setNotificationsVisibleForExpansion(false, false, false);
                }
            }
        });
    }

    public final boolean getFullyAwake() {
        return this.fullyAwake;
    }

    public final void setFullyAwake(boolean z) {
        this.fullyAwake = z;
    }

    public final boolean getWakingUp() {
        return this.wakingUp;
    }

    public final void setWakingUp(boolean value) {
        this.wakingUp = value;
        setWillWakeUp(false);
        if (value) {
            if (this.mNotificationsVisible && !this.mNotificationsVisibleForExpansion && !this.bypassController.getBypassEnabled()) {
                NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
                if (notificationStackScrollLayout == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
                }
                notificationStackScrollLayout.wakeUpFromPulse();
            }
            if (this.bypassController.getBypassEnabled() && !this.mNotificationsVisible) {
                updateNotificationVisibility(shouldAnimateVisibility(), false);
            }
        }
    }

    public final boolean getWillWakeUp() {
        return this.willWakeUp;
    }

    public final void setWillWakeUp(boolean value) {
        if (!value || this.mDozeAmount != 0.0f) {
            this.willWakeUp = value;
        }
    }

    @NotNull
    public final NotificationIconAreaController getIconAreaController() {
        NotificationIconAreaController notificationIconAreaController = this.iconAreaController;
        if (notificationIconAreaController == null) {
            Intrinsics.throwUninitializedPropertyAccessException("iconAreaController");
        }
        return notificationIconAreaController;
    }

    public final void setIconAreaController(@NotNull NotificationIconAreaController notificationIconAreaController) {
        Intrinsics.checkParameterIsNotNull(notificationIconAreaController, "<set-?>");
        this.iconAreaController = notificationIconAreaController;
    }

    public final boolean getPulsing() {
        return this.pulsing;
    }

    public final void setPulsing(boolean value) {
        this.pulsing = value;
        if (value) {
            updateNotificationVisibility(shouldAnimateVisibility(), false);
        }
    }

    public final boolean getNotificationsFullyHidden() {
        return this.notificationsFullyHidden;
    }

    private final void setNotificationsFullyHidden(boolean value) {
        if (this.notificationsFullyHidden != value) {
            this.notificationsFullyHidden = value;
            Iterator<WakeUpListener> it = this.wakeUpListeners.iterator();
            while (it.hasNext()) {
                WakeUpListener listener = it.next();
                listener.onFullyHiddenChanged(value);
            }
        }
    }

    public final boolean getCanShowPulsingHuns() {
        boolean canShow = this.pulsing;
        if (this.bypassController.getBypassEnabled()) {
            boolean z = true;
            if (!canShow && ((!this.wakingUp && !this.willWakeUp && !this.fullyAwake) || this.statusBarStateController.getState() != 1)) {
                z = false;
            }
            boolean canShow2 = z;
            if (this.collapsedEnoughToHide) {
                return false;
            }
            return canShow2;
        }
        return canShow;
    }

    public final void setStackScroller(@NotNull NotificationStackScrollLayout stackScroller) {
        Intrinsics.checkParameterIsNotNull(stackScroller, "stackScroller");
        this.mStackScroller = stackScroller;
        this.pulseExpanding = stackScroller.isPulseExpanding();
        stackScroller.setOnPulseHeightChangedListener(new Runnable() { // from class: com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator$setStackScroller$1
            @Override // java.lang.Runnable
            public final void run() {
                boolean z;
                ArrayList arrayList;
                boolean nowExpanding = NotificationWakeUpCoordinator.this.isPulseExpanding();
                z = NotificationWakeUpCoordinator.this.pulseExpanding;
                boolean changed = nowExpanding != z;
                NotificationWakeUpCoordinator.this.pulseExpanding = nowExpanding;
                arrayList = NotificationWakeUpCoordinator.this.wakeUpListeners;
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    NotificationWakeUpCoordinator.WakeUpListener listener = (NotificationWakeUpCoordinator.WakeUpListener) it.next();
                    listener.onPulseExpansionChanged(changed);
                }
            }
        });
    }

    public final boolean isPulseExpanding() {
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        return notificationStackScrollLayout.isPulseExpanding();
    }

    public final void setNotificationsVisibleForExpansion(boolean visible, boolean animate, boolean increaseSpeed) {
        this.mNotificationsVisibleForExpansion = visible;
        updateNotificationVisibility(animate, increaseSpeed);
        if (!visible && this.mNotificationsVisible) {
            this.mHeadsUpManagerPhone.releaseAllImmediately();
        }
    }

    public final void addListener(@NotNull WakeUpListener listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        this.wakeUpListeners.add(listener);
    }

    public final void removeListener(@NotNull WakeUpListener listener) {
        Intrinsics.checkParameterIsNotNull(listener, "listener");
        this.wakeUpListeners.remove(listener);
    }

    private final void updateNotificationVisibility(boolean animate, boolean increaseSpeed) {
        boolean z = false;
        if ((this.mNotificationsVisibleForExpansion || this.mHeadsUpManagerPhone.hasNotifications()) && getCanShowPulsingHuns()) {
            z = true;
        }
        boolean visible = z;
        if (!visible && this.mNotificationsVisible && ((this.wakingUp || this.willWakeUp) && this.mDozeAmount != 0.0f)) {
            return;
        }
        setNotificationsVisible(visible, animate, increaseSpeed);
    }

    private final void setNotificationsVisible(boolean visible, boolean animate, boolean increaseSpeed) {
        if (this.mNotificationsVisible == visible) {
            return;
        }
        this.mNotificationsVisible = visible;
        ObjectAnimator objectAnimator = this.mVisibilityAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (animate) {
            notifyAnimationStart(visible);
            startVisibilityAnimation(increaseSpeed);
            return;
        }
        setVisibilityAmount(visible ? 1.0f : 0.0f);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozeAmountChanged(float linear, float eased) {
        if (updateDozeAmountIfBypass()) {
            return;
        }
        if (linear != 1.0f && linear != 0.0f) {
            float f = this.mLinearDozeAmount;
            if (f == 0.0f || f == 1.0f) {
                notifyAnimationStart(this.mLinearDozeAmount == 1.0f);
            }
        }
        setDozeAmount(linear, eased);
    }

    public final void setDozeAmount(float linear, float eased) {
        boolean changed = linear != this.mLinearDozeAmount;
        this.mLinearDozeAmount = linear;
        this.mDozeAmount = eased;
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        notificationStackScrollLayout.setDozeAmount(this.mDozeAmount);
        updateHideAmount();
        if (changed && linear == 0.0f) {
            setNotificationsVisible(false, false, false);
            setNotificationsVisibleForExpansion(false, false, false);
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        updateDozeAmountIfBypass();
        if (this.bypassController.getBypassEnabled() && newState == 1 && this.state == 2 && (!this.statusBarStateController.isDozing() || shouldAnimateVisibility())) {
            setNotificationsVisible(true, false, false);
            setNotificationsVisible(false, true, false);
        }
        this.state = newState;
    }

    @Override // com.android.systemui.statusbar.phone.PanelExpansionListener
    public void onPanelExpansionChanged(float expansion, boolean tracking) {
        boolean collapsedEnough = expansion <= 0.9f;
        if (collapsedEnough != this.collapsedEnoughToHide) {
            boolean couldShowPulsingHuns = getCanShowPulsingHuns();
            this.collapsedEnoughToHide = collapsedEnough;
            if (couldShowPulsingHuns && !getCanShowPulsingHuns()) {
                updateNotificationVisibility(true, true);
                this.mHeadsUpManagerPhone.releaseAllImmediately();
            }
        }
    }

    private final boolean updateDozeAmountIfBypass() {
        if (this.bypassController.getBypassEnabled()) {
            float amount = 1.0f;
            amount = (this.statusBarStateController.getState() == 0 || this.statusBarStateController.getState() == 2) ? 0.0f : 0.0f;
            setDozeAmount(amount, amount);
            return true;
        }
        return false;
    }

    private final void startVisibilityAnimation(boolean increaseSpeed) {
        Interpolator interpolator;
        float f = this.mNotificationVisibleAmount;
        if (f == 0.0f || f == 1.0f) {
            if (this.mNotificationsVisible) {
                interpolator = Interpolators.TOUCH_RESPONSE;
            } else {
                interpolator = Interpolators.FAST_OUT_SLOW_IN_REVERSE;
            }
            this.mVisibilityInterpolator = interpolator;
        }
        float target = this.mNotificationsVisible ? 1.0f : 0.0f;
        ObjectAnimator visibilityAnimator = ObjectAnimator.ofFloat(this, this.mNotificationVisibility, target);
        visibilityAnimator.setInterpolator(Interpolators.LINEAR);
        long duration = 500;
        if (increaseSpeed) {
            duration = ((float) duration) / 1.5f;
        }
        visibilityAnimator.setDuration(duration);
        visibilityAnimator.start();
        this.mVisibilityAnimator = visibilityAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void setVisibilityAmount(float visibilityAmount) {
        this.mLinearVisibilityAmount = visibilityAmount;
        this.mVisibilityAmount = this.mVisibilityInterpolator.getInterpolation(visibilityAmount);
        handleAnimationFinished();
        updateHideAmount();
    }

    private final void handleAnimationFinished() {
        if (this.mLinearDozeAmount == 0.0f || this.mLinearVisibilityAmount == 0.0f) {
            Iterable $receiver$iv = this.mEntrySetToClearWhenFinished;
            for (Object element$iv : $receiver$iv) {
                NotificationEntry it = (NotificationEntry) element$iv;
                it.setHeadsUpAnimatingAway(false);
            }
            this.mEntrySetToClearWhenFinished.clear();
        }
    }

    public final float getWakeUpHeight() {
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        return notificationStackScrollLayout.getWakeUpHeight();
    }

    private final void updateHideAmount() {
        float linearAmount = Math.min(1.0f - this.mLinearVisibilityAmount, this.mLinearDozeAmount);
        float amount = Math.min(1.0f - this.mVisibilityAmount, this.mDozeAmount);
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        notificationStackScrollLayout.setHideAmount(linearAmount, amount);
        setNotificationsFullyHidden(linearAmount == 1.0f);
    }

    private final void notifyAnimationStart(boolean awake) {
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        notificationStackScrollLayout.notifyHideAnimationStart(!awake);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        if (isDozing) {
            setNotificationsVisible(false, false, false);
        }
    }

    public final float setPulseHeight(float height) {
        NotificationStackScrollLayout notificationStackScrollLayout = this.mStackScroller;
        if (notificationStackScrollLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mStackScroller");
        }
        float overflow = notificationStackScrollLayout.setPulseHeight(height);
        if (this.bypassController.getBypassEnabled()) {
            return 0.0f;
        }
        return overflow;
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(@NotNull NotificationEntry entry, boolean isHeadsUp) {
        Intrinsics.checkParameterIsNotNull(entry, "entry");
        boolean animate = shouldAnimateVisibility();
        if (!isHeadsUp) {
            if (this.mLinearDozeAmount != 0.0f && this.mLinearVisibilityAmount != 0.0f) {
                if (entry.isRowDismissed()) {
                    animate = false;
                } else if (!this.wakingUp && !this.willWakeUp) {
                    entry.setHeadsUpAnimatingAway(true);
                    this.mEntrySetToClearWhenFinished.add(entry);
                }
            }
        } else if (this.mEntrySetToClearWhenFinished.contains(entry)) {
            this.mEntrySetToClearWhenFinished.remove(entry);
            entry.setHeadsUpAnimatingAway(false);
        }
        updateNotificationVisibility(animate, false);
    }

    private final boolean shouldAnimateVisibility() {
        return this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking();
    }

    /* compiled from: NotificationWakeUpCoordinator.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0017J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u0005H\u0017ø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u0091(0\u0001¨\u0006\b"}, d2 = {"Lcom/android/systemui/statusbar/notification/NotificationWakeUpCoordinator$WakeUpListener;", "", "onFullyHiddenChanged", "", "isFullyHidden", "", "onPulseExpansionChanged", "expandingChanged", "name"}, k = 1, mv = {1, 1, 13})
    /* loaded from: classes21.dex */
    public interface WakeUpListener {
        @JvmDefault
        default void onFullyHiddenChanged(boolean isFullyHidden) {
        }

        @JvmDefault
        default void onPulseExpansionChanged(boolean expandingChanged) {
        }
    }
}
