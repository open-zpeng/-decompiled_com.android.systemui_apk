package com.xiaopeng.systemui.infoflow.widget.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.systemui.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes24.dex */
public class XRippleDrawable extends Drawable {
    private static final String TAG = XRippleDrawable.class.getSimpleName();
    private boolean mIsTouchDown;
    private float mDownX = -1.0f;
    private float mDownY = -1.0f;
    private int[] stateSpecPress = new int[2];
    private XRipple mRipple = new XRipple();

    public XRippleDrawable() {
        int[] iArr = this.stateSpecPress;
        iArr[0] = 16842919;
        iArr[1] = 16842910;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] state) {
        if (state != null && this.mRipple != null) {
            if (StateSet.stateSetMatches(this.stateSpecPress, state) && !this.mIsTouchDown) {
                float f = this.mDownX;
                if (f > 0.0f) {
                    float f2 = this.mDownY;
                    if (f2 > 0.0f) {
                        this.mIsTouchDown = true;
                        this.mRipple.pressDown(f, f2);
                    }
                }
            }
            if (this.mIsTouchDown) {
                this.mIsTouchDown = false;
                this.mRipple.pressUp();
            }
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        initRipple(bounds);
    }

    private void initRipple(Rect bounds) {
        Drawable.Callback callback = getCallback();
        if (callback != null) {
            if (callback instanceof LayerDrawable) {
                callback = ((LayerDrawable) callback).getCallback();
            }
            if (callback != null && (callback instanceof View)) {
                View view = (View) callback;
                this.mRipple.setView(view);
            }
        }
        this.mRipple.setRippleRect(new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom));
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        XRipple xRipple = this.mRipple;
        if (xRipple != null) {
            xRipple.drawRipple(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float x, float y) {
        super.setHotspot(x, y);
        this.mDownX = x;
        this.mDownY = y;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    public XRipple getXRipple() {
        return this.mRipple;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException {
        inflateAttrs(r, attrs, null);
        super.inflate(r, parser, attrs);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException {
        inflateAttrs(r, attrs, theme);
        super.inflate(r, parser, attrs, theme);
    }

    private void inflateAttrs(Resources resources, AttributeSet attrs, Resources.Theme theme) {
        TypedArray typedArray;
        if (resources != null && attrs != null) {
            if (theme != null) {
                typedArray = theme.obtainStyledAttributes(attrs, R.styleable.XRippleDrawable, 0, 0);
            } else {
                typedArray = resources.obtainAttributes(attrs, R.styleable.XRippleDrawable);
            }
            float rippleRadius = typedArray.getDimensionPixelSize(3, 0);
            int backgroundColor = typedArray.getColor(0, 0);
            int rippleColor = typedArray.getColor(1, 0);
            boolean viewSupportScale = typedArray.getBoolean(4, false);
            int duration = typedArray.getInt(2, resources.getInteger(R.integer.x_ripple_default_anim));
            this.mRipple.setRippleRadius(rippleRadius);
            if (rippleColor != 0) {
                this.mRipple.setRippleColor(rippleColor);
            }
            this.mRipple.setRippleBackgroundColor(backgroundColor);
            this.mRipple.setSupportScale(viewSupportScale);
            this.mRipple.setPressDuration(duration);
            this.mRipple.setUpDuration(duration);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        XRipple xRipple = this.mRipple;
        if (xRipple != null && changed) {
            xRipple.setVisible(visible);
        }
        return changed;
    }
}
