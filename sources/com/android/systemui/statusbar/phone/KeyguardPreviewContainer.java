package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;
/* loaded from: classes21.dex */
public class KeyguardPreviewContainer extends FrameLayout {
    private Drawable mBlackBarDrawable;

    public KeyguardPreviewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBlackBarDrawable = new Drawable() { // from class: com.android.systemui.statusbar.phone.KeyguardPreviewContainer.1
            @Override // android.graphics.drawable.Drawable
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0, KeyguardPreviewContainer.this.getHeight() - KeyguardPreviewContainer.this.getPaddingBottom(), KeyguardPreviewContainer.this.getWidth(), KeyguardPreviewContainer.this.getHeight());
                canvas.drawColor(-16777216);
                canvas.restore();
            }

            @Override // android.graphics.drawable.Drawable
            public void setAlpha(int alpha) {
            }

            @Override // android.graphics.drawable.Drawable
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override // android.graphics.drawable.Drawable
            public int getOpacity() {
                return -1;
            }
        };
        setBackground(this.mBlackBarDrawable);
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        setPadding(0, 0, 0, insets.getStableInsetBottom());
        return super.onApplyWindowInsets(insets);
    }
}
