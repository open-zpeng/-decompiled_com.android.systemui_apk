package com.android.systemui.statusbar.notification;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class NotificationIconDozeHelper extends NotificationDozeHelper {
    private final int mImageDarkAlpha;
    private final int mImageDarkColor = -1;
    private PorterDuffColorFilter mImageColorFilter = null;
    private int mColor = -16777216;

    public NotificationIconDozeHelper(Context ctx) {
        this.mImageDarkAlpha = ctx.getResources().getInteger(R.integer.doze_small_icon_alpha);
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public void setImageDark(ImageView target, boolean dark, boolean fade, long delay, boolean useGrayscale) {
        if (fade) {
            if (!useGrayscale) {
                fadeImageColorFilter(target, dark, delay);
                fadeImageAlpha(target, dark, delay);
                return;
            }
            fadeGrayscale(target, dark, delay);
        } else if (!useGrayscale) {
            updateImageColorFilter(target, dark);
            updateImageAlpha(target, dark);
        } else {
            updateGrayscale(target, dark);
        }
    }

    private void fadeImageColorFilter(final ImageView target, boolean dark, long delay) {
        startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationIconDozeHelper$htKSYpnoRyOwnqgE4CjirCuv6Lc
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationIconDozeHelper.this.lambda$fadeImageColorFilter$0$NotificationIconDozeHelper(target, valueAnimator);
            }
        }, dark, delay, null);
    }

    public /* synthetic */ void lambda$fadeImageColorFilter$0$NotificationIconDozeHelper(ImageView target, ValueAnimator animation) {
        updateImageColorFilter(target, ((Float) animation.getAnimatedValue()).floatValue());
    }

    private void fadeImageAlpha(final ImageView target, boolean dark, long delay) {
        startIntensityAnimation(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$NotificationIconDozeHelper$BogTFxcTFjhpQeWXgJSk3UfaaEE
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationIconDozeHelper.this.lambda$fadeImageAlpha$1$NotificationIconDozeHelper(target, valueAnimator);
            }
        }, dark, delay, null);
    }

    public /* synthetic */ void lambda$fadeImageAlpha$1$NotificationIconDozeHelper(ImageView target, ValueAnimator animation) {
        float t = ((Float) animation.getAnimatedValue()).floatValue();
        target.setImageAlpha((int) (((1.0f - t) * 255.0f) + (this.mImageDarkAlpha * t)));
    }

    private void updateImageColorFilter(ImageView target, boolean dark) {
        updateImageColorFilter(target, dark ? 1.0f : 0.0f);
    }

    private void updateImageColorFilter(ImageView target, float intensity) {
        int color = NotificationUtils.interpolateColors(this.mColor, -1, intensity);
        PorterDuffColorFilter porterDuffColorFilter = this.mImageColorFilter;
        if (porterDuffColorFilter == null || porterDuffColorFilter.getColor() != color) {
            this.mImageColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        Drawable imageDrawable = target.getDrawable();
        if (imageDrawable != null) {
            Drawable d = imageDrawable.mutate();
            d.setColorFilter(null);
            d.setColorFilter(this.mImageColorFilter);
        }
    }

    private void updateImageAlpha(ImageView target, boolean dark) {
        target.setImageAlpha(dark ? this.mImageDarkAlpha : 255);
    }
}
