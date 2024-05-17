package com.android.systemui.bubbles;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.InsetDrawable;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.PathParser;
import android.widget.FrameLayout;
import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.ShadowGenerator;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class BubbleView extends FrameLayout {
    private static final int DARK_ICON_ALPHA = 180;
    private static final int DEFAULT_BACKGROUND_COLOR = -3355444;
    private static final double ICON_MIN_CONTRAST = 4.1d;
    private static final float WHITE_SCRIM_ALPHA = 0.54f;
    private int mBadgeColor;
    private BadgedImageView mBadgedImageView;
    private Bubble mBubble;
    private BubbleIconFactory mBubbleIconFactory;
    private Context mContext;
    private int mIconInset;
    private boolean mSuppressDot;
    private Drawable mUserBadgedAppIcon;

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
        this.mIconInset = getResources().getDimensionPixelSize(R.dimen.bubble_icon_inset);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBadgedImageView = (BadgedImageView) findViewById(R.id.bubble_image);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setBubble(Bubble bubble) {
        this.mBubble = bubble;
    }

    public NotificationEntry getEntry() {
        Bubble bubble = this.mBubble;
        if (bubble != null) {
            return bubble.getEntry();
        }
        return null;
    }

    public String getKey() {
        Bubble bubble = this.mBubble;
        if (bubble != null) {
            return bubble.getKey();
        }
        return null;
    }

    public void update(Bubble bubble) {
        this.mBubble = bubble;
        updateViews();
    }

    public void setBubbleIconFactory(BubbleIconFactory factory) {
        this.mBubbleIconFactory = factory;
    }

    public void setAppIcon(Drawable appIcon) {
        this.mUserBadgedAppIcon = appIcon;
    }

    public ExpandableNotificationRow getRowView() {
        Bubble bubble = this.mBubble;
        if (bubble != null) {
            return bubble.getEntry().getRow();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateDotVisibility(boolean animate) {
        updateDotVisibility(animate, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSuppressDot(boolean suppressDot, boolean animate) {
        this.mSuppressDot = suppressDot;
        updateDotVisibility(animate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDotPosition(final boolean onLeft, boolean animate) {
        if (animate && onLeft != this.mBadgedImageView.getDotOnLeft() && shouldShowDot()) {
            animateDot(false, new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleView$8ligGzJrQjv0I1bfTGAaM0-HpBQ
                @Override // java.lang.Runnable
                public final void run() {
                    BubbleView.this.lambda$setDotPosition$0$BubbleView(onLeft);
                }
            });
        } else {
            this.mBadgedImageView.setDotOnLeft(onLeft);
        }
    }

    public /* synthetic */ void lambda$setDotPosition$0$BubbleView(boolean onLeft) {
        this.mBadgedImageView.setDotOnLeft(onLeft);
        animateDot(true, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float[] getDotCenter() {
        float[] unscaled = this.mBadgedImageView.getDotCenter();
        return new float[]{unscaled[0], unscaled[1]};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getDotPositionOnLeft() {
        return this.mBadgedImageView.getDotOnLeft();
    }

    private void updateDotVisibility(boolean animate, Runnable after) {
        boolean showDot = shouldShowDot();
        if (animate) {
            animateDot(showDot, after);
            return;
        }
        this.mBadgedImageView.setShowDot(showDot);
        this.mBadgedImageView.setDotScale(showDot ? 1.0f : 0.0f);
    }

    private void animateDot(final boolean showDot, final Runnable after) {
        if (this.mBadgedImageView.isShowingDot() == showDot) {
            return;
        }
        this.mBadgedImageView.setShowDot(showDot);
        this.mBadgedImageView.clearAnimation();
        this.mBadgedImageView.animate().setDuration(200L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleView$ZKBfBG7GijRplpU_8yCIB0NuyLk
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                BubbleView.this.lambda$animateDot$1$BubbleView(showDot, valueAnimator);
            }
        }).withEndAction(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleView$xA_RAS_mSp_JlDsmI3wc705YhoQ
            @Override // java.lang.Runnable
            public final void run() {
                BubbleView.this.lambda$animateDot$2$BubbleView(showDot, after);
            }
        }).start();
    }

    public /* synthetic */ void lambda$animateDot$1$BubbleView(boolean showDot, ValueAnimator valueAnimator) {
        float fraction = valueAnimator.getAnimatedFraction();
        this.mBadgedImageView.setDotScale(showDot ? fraction : 1.0f - fraction);
    }

    public /* synthetic */ void lambda$animateDot$2$BubbleView(boolean showDot, Runnable after) {
        this.mBadgedImageView.setDotScale(showDot ? 1.0f : 0.0f);
        if (after != null) {
            after.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateViews() {
        Bubble bubble = this.mBubble;
        if (bubble == null || this.mBubbleIconFactory == null) {
            return;
        }
        Notification.BubbleMetadata metadata = bubble.getEntry().getBubbleMetadata();
        Notification n = this.mBubble.getEntry().notification.getNotification();
        Icon ic = metadata.getIcon();
        boolean needsTint = ic.getType() != 5;
        Drawable iconDrawable = ic.loadDrawable(this.mContext);
        if (needsTint) {
            iconDrawable = buildIconWithTint(iconDrawable, n.color);
        }
        Bitmap bubbleIcon = this.mBubbleIconFactory.createBadgedIconBitmap(iconDrawable, (UserHandle) null, true).icon;
        BubbleIconFactory bubbleIconFactory = this.mBubbleIconFactory;
        Bitmap userBadgedBitmap = bubbleIconFactory.createIconBitmap(this.mUserBadgedAppIcon, 1.0f, bubbleIconFactory.getBadgeSize());
        Canvas c = new Canvas();
        ShadowGenerator shadowGenerator = new ShadowGenerator(this.mBubbleIconFactory.getBadgeSize());
        c.setBitmap(userBadgedBitmap);
        shadowGenerator.recreateIcon(Bitmap.createBitmap(userBadgedBitmap), c);
        this.mBubbleIconFactory.badgeWithDrawable(bubbleIcon, new BitmapDrawable(this.mContext.getResources(), userBadgedBitmap));
        this.mBadgedImageView.setImageBitmap(bubbleIcon);
        int badgeColor = determineDominateColor(iconDrawable, n.color);
        this.mBadgeColor = badgeColor;
        this.mBadgedImageView.setDotColor(badgeColor);
        Path iconPath = PathParser.createPathFromPathData(getResources().getString(17039747));
        Matrix matrix = new Matrix();
        float scale = this.mBubbleIconFactory.getNormalizer().getScale(iconDrawable, null, null, null);
        matrix.setScale(scale, scale, 50.0f, 50.0f);
        iconPath.transform(matrix);
        this.mBadgedImageView.drawDot(iconPath);
        animateDot(shouldShowDot(), null);
    }

    boolean shouldShowDot() {
        return this.mBubble.showBubbleDot() && !this.mSuppressDot;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getBadgeColor() {
        return this.mBadgeColor;
    }

    private AdaptiveIconDrawable buildIconWithTint(Drawable iconDrawable, int backgroundColor) {
        InsetDrawable foreground = new InsetDrawable(checkTint(iconDrawable, backgroundColor), this.mIconInset);
        ColorDrawable background = new ColorDrawable(backgroundColor);
        return new AdaptiveIconDrawable(background, foreground);
    }

    private Drawable checkTint(Drawable iconDrawable, int backgroundColor) {
        int backgroundColor2 = ColorUtils.setAlphaComponent(backgroundColor, 255);
        if (backgroundColor2 == 0) {
            backgroundColor2 = DEFAULT_BACKGROUND_COLOR;
        }
        iconDrawable.setTint(-1);
        double contrastRatio = ColorUtils.calculateContrast(-1, backgroundColor2);
        if (contrastRatio < ICON_MIN_CONTRAST) {
            int dark = ColorUtils.setAlphaComponent(-16777216, 180);
            iconDrawable.setTint(dark);
        }
        return iconDrawable;
    }

    private int determineDominateColor(Drawable d, int defaultTint) {
        return ColorUtils.blendARGB(defaultTint, -1, (float) WHITE_SCRIM_ALPHA);
    }
}
