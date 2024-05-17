package com.android.systemui.statusbar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.format.DateFormat;
import android.util.FloatProperty;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.Dumpable;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.CallbackController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class StatusBarStateControllerImpl implements SysuiStatusBarStateController, CallbackController<StatusBarStateController.StateListener>, Dumpable {
    private static final int HISTORY_SIZE = 32;
    private static final int MAX_STATE = 3;
    private static final int MIN_STATE = 0;
    private static final String TAG = "SbStateController";
    private ValueAnimator mDarkAnimator;
    private float mDozeAmount;
    private float mDozeAmountTarget;
    private boolean mIsDozing;
    private boolean mKeyguardRequested;
    private int mLastState;
    private boolean mLeaveOpenOnKeyguardHide;
    private boolean mPulsing;
    private int mState;
    private static final Comparator<SysuiStatusBarStateController.RankedListener> sComparator = Comparator.comparingInt(new ToIntFunction() { // from class: com.android.systemui.statusbar.-$$Lambda$StatusBarStateControllerImpl$7y8VOe44iFeEd9HPscwVVB7kUfw
        @Override // java.util.function.ToIntFunction
        public final int applyAsInt(Object obj) {
            int i;
            i = ((SysuiStatusBarStateController.RankedListener) obj).mRank;
            return i;
        }
    });
    private static final FloatProperty<StatusBarStateControllerImpl> SET_DARK_AMOUNT_PROPERTY = new FloatProperty<StatusBarStateControllerImpl>("mDozeAmount") { // from class: com.android.systemui.statusbar.StatusBarStateControllerImpl.1
        @Override // android.util.FloatProperty
        public void setValue(StatusBarStateControllerImpl object, float value) {
            object.setDozeAmountInternal(value);
        }

        @Override // android.util.Property
        public Float get(StatusBarStateControllerImpl object) {
            return Float.valueOf(object.mDozeAmount);
        }
    };
    private final ArrayList<SysuiStatusBarStateController.RankedListener> mListeners = new ArrayList<>();
    private int mHistoryIndex = 0;
    private HistoricalState[] mHistoricalRecords = new HistoricalState[32];
    private int mSystemUiVisibility = 0;
    private Interpolator mDozeInterpolator = Interpolators.FAST_OUT_SLOW_IN;

    @Inject
    public StatusBarStateControllerImpl() {
        for (int i = 0; i < 32; i++) {
            this.mHistoricalRecords[i] = new HistoricalState();
        }
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController
    public int getState() {
        return this.mState;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean setState(int state) {
        if (state > 3 || state < 0) {
            throw new IllegalArgumentException("Invalid state " + state);
        }
        int i = this.mState;
        if (state == i) {
            return false;
        }
        recordHistoricalState(state, i);
        if (this.mState == 0 && state == 2) {
            Log.e(TAG, "Invalid state transition: SHADE -> SHADE_LOCKED", new Throwable());
        }
        synchronized (this.mListeners) {
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                SysuiStatusBarStateController.RankedListener rl = (SysuiStatusBarStateController.RankedListener) it.next();
                rl.mListener.onStatePreChange(this.mState, state);
            }
            this.mLastState = this.mState;
            this.mState = state;
            Iterator it2 = new ArrayList(this.mListeners).iterator();
            while (it2.hasNext()) {
                SysuiStatusBarStateController.RankedListener rl2 = (SysuiStatusBarStateController.RankedListener) it2.next();
                rl2.mListener.onStateChanged(this.mState);
            }
            Iterator it3 = new ArrayList(this.mListeners).iterator();
            while (it3.hasNext()) {
                SysuiStatusBarStateController.RankedListener rl3 = (SysuiStatusBarStateController.RankedListener) it3.next();
                rl3.mListener.onStatePostChange();
            }
        }
        return true;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController
    public boolean isDozing() {
        return this.mIsDozing;
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController
    public float getDozeAmount() {
        return this.mDozeAmount;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public float getInterpolatedDozeAmount() {
        return this.mDozeInterpolator.getInterpolation(this.mDozeAmount);
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean setIsDozing(boolean isDozing) {
        if (this.mIsDozing == isDozing) {
            return false;
        }
        this.mIsDozing = isDozing;
        synchronized (this.mListeners) {
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                SysuiStatusBarStateController.RankedListener rl = (SysuiStatusBarStateController.RankedListener) it.next();
                rl.mListener.onDozingChanged(isDozing);
            }
        }
        return true;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public void setDozeAmount(float dozeAmount, boolean animated) {
        ValueAnimator valueAnimator = this.mDarkAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            if (animated && this.mDozeAmountTarget == dozeAmount) {
                return;
            }
            this.mDarkAnimator.cancel();
        }
        this.mDozeAmountTarget = dozeAmount;
        if (animated) {
            startDozeAnimation();
        } else {
            setDozeAmountInternal(dozeAmount);
        }
    }

    private void startDozeAnimation() {
        Interpolator interpolator;
        float f = this.mDozeAmount;
        if (f == 0.0f || f == 1.0f) {
            if (this.mIsDozing) {
                interpolator = Interpolators.FAST_OUT_SLOW_IN;
            } else {
                interpolator = Interpolators.TOUCH_RESPONSE_REVERSE;
            }
            this.mDozeInterpolator = interpolator;
        }
        this.mDarkAnimator = ObjectAnimator.ofFloat(this, SET_DARK_AMOUNT_PROPERTY, this.mDozeAmountTarget);
        this.mDarkAnimator.setInterpolator(Interpolators.LINEAR);
        this.mDarkAnimator.setDuration(500L);
        this.mDarkAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDozeAmountInternal(float dozeAmount) {
        this.mDozeAmount = dozeAmount;
        float interpolatedAmount = this.mDozeInterpolator.getInterpolation(dozeAmount);
        synchronized (this.mListeners) {
            Iterator it = new ArrayList(this.mListeners).iterator();
            while (it.hasNext()) {
                SysuiStatusBarStateController.RankedListener rl = (SysuiStatusBarStateController.RankedListener) it.next();
                rl.mListener.onDozeAmountChanged(this.mDozeAmount, interpolatedAmount);
            }
        }
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean goingToFullShade() {
        return this.mState == 0 && this.mLeaveOpenOnKeyguardHide;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public void setLeaveOpenOnKeyguardHide(boolean leaveOpen) {
        this.mLeaveOpenOnKeyguardHide = leaveOpen;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean leaveOpenOnKeyguardHide() {
        return this.mLeaveOpenOnKeyguardHide;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean fromShadeLocked() {
        return this.mLastState == 2;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(StatusBarStateController.StateListener listener) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(listener, Integer.MAX_VALUE);
        }
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    @Deprecated
    public void addCallback(StatusBarStateController.StateListener listener, int rank) {
        synchronized (this.mListeners) {
            addListenerInternalLocked(listener, rank);
        }
    }

    @GuardedBy({"mListeners"})
    private void addListenerInternalLocked(StatusBarStateController.StateListener listener, int rank) {
        Iterator<SysuiStatusBarStateController.RankedListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            SysuiStatusBarStateController.RankedListener rl = it.next();
            if (rl.mListener.equals(listener)) {
                return;
            }
        }
        SysuiStatusBarStateController.RankedListener rl2 = new SysuiStatusBarStateController.RankedListener(listener, rank);
        this.mListeners.add(rl2);
        this.mListeners.sort(sComparator);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(final StatusBarStateController.StateListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.removeIf(new Predicate() { // from class: com.android.systemui.statusbar.-$$Lambda$StatusBarStateControllerImpl$TAyHbKlLKq3j8NJBke8nEPo5OK4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equals;
                    equals = ((SysuiStatusBarStateController.RankedListener) obj).mListener.equals(StatusBarStateController.StateListener.this);
                    return equals;
                }
            });
        }
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public void setKeyguardRequested(boolean keyguardRequested) {
        this.mKeyguardRequested = keyguardRequested;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public boolean isKeyguardRequested() {
        return this.mKeyguardRequested;
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public void setSystemUiVisibility(int visibility) {
        if (this.mSystemUiVisibility != visibility) {
            this.mSystemUiVisibility = visibility;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    SysuiStatusBarStateController.RankedListener rl = (SysuiStatusBarStateController.RankedListener) it.next();
                    rl.mListener.onSystemUiVisibilityChanged(this.mSystemUiVisibility);
                }
            }
        }
    }

    @Override // com.android.systemui.statusbar.SysuiStatusBarStateController
    public void setPulsing(boolean pulsing) {
        if (this.mPulsing != pulsing) {
            this.mPulsing = pulsing;
            synchronized (this.mListeners) {
                Iterator it = new ArrayList(this.mListeners).iterator();
                while (it.hasNext()) {
                    SysuiStatusBarStateController.RankedListener rl = (SysuiStatusBarStateController.RankedListener) it.next();
                    rl.mListener.onPulsingChanged(pulsing);
                }
            }
        }
    }

    public static String describe(int state) {
        return StatusBarState.toShortString(state);
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("StatusBarStateController: ");
        pw.println(" mState=" + this.mState + " (" + describe(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
        pw.println(" mLastState=" + this.mLastState + " (" + describe(this.mLastState) + NavigationBarInflaterView.KEY_CODE_END);
        StringBuilder sb = new StringBuilder();
        sb.append(" mLeaveOpenOnKeyguardHide=");
        sb.append(this.mLeaveOpenOnKeyguardHide);
        pw.println(sb.toString());
        pw.println(" mKeyguardRequested=" + this.mKeyguardRequested);
        pw.println(" mIsDozing=" + this.mIsDozing);
        pw.println(" Historical states:");
        int size = 0;
        for (int i = 0; i < 32; i++) {
            if (this.mHistoricalRecords[i].mTimestamp != 0) {
                size++;
            }
        }
        int i2 = this.mHistoryIndex;
        for (int i3 = i2 + 32; i3 >= ((this.mHistoryIndex + 32) - size) + 1; i3 += -1) {
            pw.println("  (" + (((this.mHistoryIndex + 32) - i3) + 1) + NavigationBarInflaterView.KEY_CODE_END + this.mHistoricalRecords[i3 & 31]);
        }
    }

    private void recordHistoricalState(int currentState, int lastState) {
        this.mHistoryIndex = (this.mHistoryIndex + 1) % 32;
        HistoricalState state = this.mHistoricalRecords[this.mHistoryIndex];
        state.mState = currentState;
        state.mLastState = lastState;
        state.mTimestamp = System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class HistoricalState {
        int mLastState;
        int mState;
        long mTimestamp;

        private HistoricalState() {
        }

        public String toString() {
            if (this.mTimestamp != 0) {
                return "state=" + this.mState + " (" + StatusBarStateControllerImpl.describe(this.mState) + NavigationBarInflaterView.KEY_CODE_END + "lastState=" + this.mLastState + " (" + StatusBarStateControllerImpl.describe(this.mLastState) + NavigationBarInflaterView.KEY_CODE_END + "timestamp=" + DateFormat.format("MM-dd HH:mm:ss", this.mTimestamp);
            }
            return "Empty " + getClass().getSimpleName();
        }
    }
}
