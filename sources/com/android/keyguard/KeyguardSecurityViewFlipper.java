package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.R;
/* loaded from: classes19.dex */
public class KeyguardSecurityViewFlipper extends ViewFlipper implements KeyguardSecurityView {
    private static final boolean DEBUG = false;
    private static final String TAG = "KeyguardSecurityViewFlipper";
    private Rect mTempRect;

    public KeyguardSecurityViewFlipper(Context context) {
        this(context, null);
    }

    public KeyguardSecurityViewFlipper(Context context, AttributeSet attr) {
        super(context, attr);
        this.mTempRect = new Rect();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        this.mTempRect.set(0, 0, 0, 0);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                offsetRectIntoDescendantCoords(child, this.mTempRect);
                ev.offsetLocation(this.mTempRect.left, this.mTempRect.top);
                result = child.dispatchTouchEvent(ev) || result;
                ev.offsetLocation(-this.mTempRect.left, -this.mTempRect.top);
            }
        }
        return result;
    }

    KeyguardSecurityView getSecurityView() {
        View child = getChildAt(getDisplayedChild());
        if (child instanceof KeyguardSecurityView) {
            return (KeyguardSecurityView) child;
        }
        return null;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setKeyguardCallback(KeyguardSecurityCallback callback) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.setKeyguardCallback(callback);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void setLockPatternUtils(LockPatternUtils utils) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.setLockPatternUtils(utils);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void reset() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.reset();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onPause() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.onPause();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void onResume(int reason) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.onResume(reason);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean needsInput() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            return ksv.needsInput();
        }
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public KeyguardSecurityCallback getCallback() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            return ksv.getCallback();
        }
        return null;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showPromptReason(int reason) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.showPromptReason(reason);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showMessage(CharSequence message, ColorStateList colorState) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.showMessage(message, colorState);
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void showUsabilityHint() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.showUsabilityHint();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public void startAppearAnimation() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            ksv.startAppearAnimation();
        }
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public boolean startDisappearAnimation(Runnable finishRunnable) {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            return ksv.startDisappearAnimation(finishRunnable);
        }
        return false;
    }

    @Override // com.android.keyguard.KeyguardSecurityView
    public CharSequence getTitle() {
        KeyguardSecurityView ksv = getSecurityView();
        if (ksv != null) {
            return ksv.getTitle();
        }
        return "";
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams ? new LayoutParams((LayoutParams) p) : new LayoutParams(p);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);
        int maxWidth = widthSize;
        int maxHeight = heightSize;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (lp.maxWidth > 0 && lp.maxWidth < maxWidth) {
                maxWidth = lp.maxWidth;
            }
            if (lp.maxHeight > 0 && lp.maxHeight < maxHeight) {
                maxHeight = lp.maxHeight;
            }
        }
        int i2 = getPaddingLeft();
        int wPadding = i2 + getPaddingRight();
        int hPadding = getPaddingTop() + getPaddingBottom();
        int maxWidth2 = Math.max(0, maxWidth - wPadding);
        int maxHeight2 = Math.max(0, maxHeight - hPadding);
        int width = widthMode == 1073741824 ? widthSize : 0;
        int height = heightMode == 1073741824 ? heightSize : 0;
        int i3 = 0;
        while (i3 < count) {
            View child = getChildAt(i3);
            LayoutParams lp2 = (LayoutParams) child.getLayoutParams();
            int childWidthSpec = makeChildMeasureSpec(maxWidth2, lp2.width);
            int widthMode2 = widthMode;
            int childHeightSpec = makeChildMeasureSpec(maxHeight2, lp2.height);
            child.measure(childWidthSpec, childHeightSpec);
            int childHeightSpec2 = child.getMeasuredWidth();
            width = Math.max(width, Math.min(childHeightSpec2, widthSize - wPadding));
            height = Math.max(height, Math.min(child.getMeasuredHeight(), heightSize - hPadding));
            i3++;
            widthMode = widthMode2;
            heightMode = heightMode;
        }
        setMeasuredDimension(width + wPadding, height + hPadding);
    }

    private int makeChildMeasureSpec(int maxSize, int childDimen) {
        int mode;
        int size;
        if (childDimen == -2) {
            mode = Integer.MIN_VALUE;
            size = maxSize;
        } else if (childDimen == -1) {
            mode = 1073741824;
            size = maxSize;
        } else {
            mode = 1073741824;
            size = Math.min(maxSize, childDimen);
        }
        return View.MeasureSpec.makeMeasureSpec(size, mode);
    }

    /* loaded from: classes19.dex */
    public static class LayoutParams extends FrameLayout.LayoutParams {
        @ViewDebug.ExportedProperty(category = "layout")
        public int maxHeight;
        @ViewDebug.ExportedProperty(category = "layout")
        public int maxWidth;

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((FrameLayout.LayoutParams) other);
            this.maxWidth = other.maxWidth;
            this.maxHeight = other.maxHeight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.KeyguardSecurityViewFlipper_Layout, 0, 0);
            this.maxWidth = a.getDimensionPixelSize(R.styleable.KeyguardSecurityViewFlipper_Layout_layout_maxWidth, 0);
            this.maxHeight = a.getDimensionPixelSize(R.styleable.KeyguardSecurityViewFlipper_Layout_layout_maxHeight, 0);
            a.recycle();
        }

        protected void encodeProperties(ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);
            encoder.addProperty("layout:maxWidth", this.maxWidth);
            encoder.addProperty("layout:maxHeight", this.maxHeight);
        }
    }
}
