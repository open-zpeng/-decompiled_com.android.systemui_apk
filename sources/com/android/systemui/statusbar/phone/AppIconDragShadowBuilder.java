package com.android.systemui.statusbar.phone;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
/* loaded from: classes21.dex */
class AppIconDragShadowBuilder extends View.DragShadowBuilder {
    private static final int ICON_SCALE = 2;
    final Drawable mDrawable;
    final int mIconSize;

    public AppIconDragShadowBuilder(ImageView icon) {
        this.mDrawable = icon.getDrawable();
        this.mIconSize = icon.getHeight() * 2;
    }

    @Override // android.view.View.DragShadowBuilder
    public void onProvideShadowMetrics(Point size, Point touch) {
        int i = this.mIconSize;
        size.set(i, i);
        int i2 = this.mIconSize;
        touch.set(i2 / 2, (i2 * 2) / 3);
    }

    @Override // android.view.View.DragShadowBuilder
    public void onDrawShadow(Canvas canvas) {
        Rect oldBounds = this.mDrawable.copyBounds();
        Drawable drawable = this.mDrawable;
        int i = this.mIconSize;
        drawable.setBounds(0, 0, i, i);
        this.mDrawable.draw(canvas);
        this.mDrawable.setBounds(oldBounds);
    }
}
