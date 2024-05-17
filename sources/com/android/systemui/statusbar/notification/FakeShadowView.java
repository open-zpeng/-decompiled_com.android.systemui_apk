package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import com.android.systemui.R;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;
/* loaded from: classes21.dex */
public class FakeShadowView extends AlphaOptimizedFrameLayout {
    public static final float SHADOW_SIBLING_TRESHOLD = 0.1f;
    private View mFakeShadow;
    private float mOutlineAlpha;
    private final int mShadowMinHeight;

    public FakeShadowView(Context context) {
        this(context, null);
    }

    public FakeShadowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FakeShadowView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FakeShadowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFakeShadow = new View(context);
        this.mFakeShadow.setVisibility(4);
        this.mFakeShadow.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (getResources().getDisplayMetrics().density * 48.0f)));
        this.mFakeShadow.setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.systemui.statusbar.notification.FakeShadowView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRect(0, 0, FakeShadowView.this.getWidth(), FakeShadowView.this.mFakeShadow.getHeight());
                outline.setAlpha(FakeShadowView.this.mOutlineAlpha);
            }
        });
        addView(this.mFakeShadow);
        this.mShadowMinHeight = Math.max(1, context.getResources().getDimensionPixelSize(R.dimen.notification_divider_height));
    }

    public void setFakeShadowTranslationZ(float fakeShadowTranslationZ, float outlineAlpha, int shadowYEnd, int outlineTranslation) {
        if (fakeShadowTranslationZ == 0.0f) {
            this.mFakeShadow.setVisibility(4);
            return;
        }
        this.mFakeShadow.setVisibility(0);
        this.mFakeShadow.setTranslationZ(Math.max(this.mShadowMinHeight, fakeShadowTranslationZ));
        this.mFakeShadow.setTranslationX(outlineTranslation);
        View view = this.mFakeShadow;
        view.setTranslationY(shadowYEnd - view.getHeight());
        if (outlineAlpha != this.mOutlineAlpha) {
            this.mOutlineAlpha = outlineAlpha;
            this.mFakeShadow.invalidateOutline();
        }
    }
}
