package com.android.systemui.volume;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import com.android.keyguard.AlphaOptimizedImageButton;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class CaptionsToggleImageButton extends AlphaOptimizedImageButton {
    private static final int[] OPTED_OUT_STATE = {R.attr.optedOut};
    private boolean mCaptionsEnabled;
    private ConfirmedTapListener mConfirmedTapListener;
    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mGestureListener;
    private boolean mOptedOut;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public interface ConfirmedTapListener {
        void onConfirmedTap();
    }

    public CaptionsToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCaptionsEnabled = false;
        this.mOptedOut = false;
        this.mGestureListener = new GestureDetector.SimpleOnGestureListener() { // from class: com.android.systemui.volume.CaptionsToggleImageButton.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return CaptionsToggleImageButton.this.tryToSendTapConfirmedEvent();
            }
        };
        setContentDescription(getContext().getString(R.string.volume_odi_captions_content_description));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        GestureDetector gestureDetector = this.mGestureDetector;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override // android.widget.ImageView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 1);
        if (this.mOptedOut) {
            mergeDrawableStates(state, OPTED_OUT_STATE);
        }
        return state;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Runnable setCaptionsEnabled(boolean areCaptionsEnabled) {
        String string;
        int i;
        this.mCaptionsEnabled = areCaptionsEnabled;
        AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat = AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK;
        if (this.mCaptionsEnabled) {
            string = getContext().getString(R.string.volume_odi_captions_hint_disable);
        } else {
            string = getContext().getString(R.string.volume_odi_captions_hint_enable);
        }
        ViewCompat.replaceAccessibilityAction(this, accessibilityActionCompat, string, new AccessibilityViewCommand() { // from class: com.android.systemui.volume.-$$Lambda$CaptionsToggleImageButton$G1CrD-3iT19JR_3d-rnIgC4b3Mg
            @Override // androidx.core.view.accessibility.AccessibilityViewCommand
            public final boolean perform(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
                return CaptionsToggleImageButton.this.lambda$setCaptionsEnabled$0$CaptionsToggleImageButton(view, commandArguments);
            }
        });
        if (this.mCaptionsEnabled) {
            i = R.drawable.ic_volume_odi_captions;
        } else {
            i = R.drawable.ic_volume_odi_captions_disabled;
        }
        return setImageResourceAsync(i);
    }

    public /* synthetic */ boolean lambda$setCaptionsEnabled$0$CaptionsToggleImageButton(View view, AccessibilityViewCommand.CommandArguments commandArguments) {
        return tryToSendTapConfirmedEvent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean tryToSendTapConfirmedEvent() {
        ConfirmedTapListener confirmedTapListener = this.mConfirmedTapListener;
        if (confirmedTapListener != null) {
            confirmedTapListener.onConfirmedTap();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getCaptionsEnabled() {
        return this.mCaptionsEnabled;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOptedOut(boolean isOptedOut) {
        this.mOptedOut = isOptedOut;
        refreshDrawableState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getOptedOut() {
        return this.mOptedOut;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnConfirmedTapListener(ConfirmedTapListener listener, Handler handler) {
        this.mConfirmedTapListener = listener;
        if (this.mGestureDetector == null) {
            this.mGestureDetector = new GestureDetector(getContext(), this.mGestureListener, handler);
        }
    }
}
