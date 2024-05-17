package com.android.launcher3.icons;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import androidx.annotation.NonNull;
/* loaded from: classes19.dex */
public class BaseIconFactory implements AutoCloseable {
    static final boolean ATLEAST_OREO;
    static final boolean ATLEAST_P;
    private static final int DEFAULT_WRAPPER_BACKGROUND = -1;
    private static final float ICON_BADGE_SCALE = 0.444f;
    private static final String TAG = "BaseIconFactory";
    private final Canvas mCanvas;
    private final ColorExtractor mColorExtractor;
    protected final Context mContext;
    private boolean mDisableColorExtractor;
    protected final int mFillResIconDpi;
    protected final int mIconBitmapSize;
    private IconNormalizer mNormalizer;
    private final Rect mOldBounds;
    private final PackageManager mPm;
    private ShadowGenerator mShadowGenerator;
    private final boolean mShapeDetection;
    private int mWrapperBackgroundColor;
    private Drawable mWrapperIcon;

    static {
        ATLEAST_OREO = Build.VERSION.SDK_INT >= 26;
        ATLEAST_P = Build.VERSION.SDK_INT >= 28;
    }

    protected BaseIconFactory(Context context, int fillResIconDpi, int iconBitmapSize, boolean shapeDetection) {
        this.mOldBounds = new Rect();
        this.mWrapperBackgroundColor = -1;
        this.mContext = context.getApplicationContext();
        this.mShapeDetection = shapeDetection;
        this.mFillResIconDpi = fillResIconDpi;
        this.mIconBitmapSize = iconBitmapSize;
        this.mPm = this.mContext.getPackageManager();
        this.mColorExtractor = new ColorExtractor();
        this.mCanvas = new Canvas();
        this.mCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseIconFactory(Context context, int fillResIconDpi, int iconBitmapSize) {
        this(context, fillResIconDpi, iconBitmapSize, false);
    }

    protected void clear() {
        this.mWrapperBackgroundColor = -1;
        this.mDisableColorExtractor = false;
    }

    public ShadowGenerator getShadowGenerator() {
        if (this.mShadowGenerator == null) {
            this.mShadowGenerator = new ShadowGenerator(this.mIconBitmapSize);
        }
        return this.mShadowGenerator;
    }

    public IconNormalizer getNormalizer() {
        if (this.mNormalizer == null) {
            this.mNormalizer = new IconNormalizer(this.mContext, this.mIconBitmapSize, this.mShapeDetection);
        }
        return this.mNormalizer;
    }

    public BitmapInfo createIconBitmap(Intent.ShortcutIconResource iconRes) {
        try {
            Resources resources = this.mPm.getResourcesForApplication(iconRes.packageName);
            if (resources != null) {
                int id = resources.getIdentifier(iconRes.resourceName, null, null);
                return createBadgedIconBitmap(resources.getDrawableForDensity(id, this.mFillResIconDpi), Process.myUserHandle(), false);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public BitmapInfo createIconBitmap(Bitmap icon) {
        if (this.mIconBitmapSize != icon.getWidth() || this.mIconBitmapSize != icon.getHeight()) {
            icon = createIconBitmap(new BitmapDrawable(this.mContext.getResources(), icon), 1.0f);
        }
        return BitmapInfo.fromBitmap(icon, this.mDisableColorExtractor ? null : this.mColorExtractor);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, boolean shrinkNonAdaptiveIcons) {
        return createBadgedIconBitmap(icon, user, shrinkNonAdaptiveIcons, false, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, false);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp) {
        return createBadgedIconBitmap(icon, user, iconAppTargetSdk, isInstantApp, (float[]) null);
    }

    public BitmapInfo createBadgedIconBitmap(Drawable icon, UserHandle user, int iconAppTargetSdk, boolean isInstantApp, float[] scale) {
        boolean shrinkNonAdaptiveIcons = ATLEAST_P || (ATLEAST_OREO && iconAppTargetSdk >= 26);
        return createBadgedIconBitmap(icon, user, shrinkNonAdaptiveIcons, isInstantApp, scale);
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable icon, int iconAppTargetSdk) {
        boolean shrinkNonAdaptiveIcons = ATLEAST_P || (ATLEAST_OREO && iconAppTargetSdk >= 26);
        return createScaledBitmapWithoutShadow(icon, shrinkNonAdaptiveIcons);
    }

    public BitmapInfo createBadgedIconBitmap(@NonNull Drawable icon, UserHandle user, boolean shrinkNonAdaptiveIcons, boolean isInstantApp, float[] scale) {
        if (scale == null) {
            scale = new float[1];
        }
        Drawable icon2 = normalizeAndWrapToAdaptiveIcon(icon, shrinkNonAdaptiveIcons, null, scale);
        Bitmap bitmap = createIconBitmap(icon2, scale[0]);
        if (ATLEAST_OREO && (icon2 instanceof AdaptiveIconDrawable)) {
            this.mCanvas.setBitmap(bitmap);
            getShadowGenerator().recreateIcon(Bitmap.createBitmap(bitmap), this.mCanvas);
            this.mCanvas.setBitmap(null);
        }
        if (isInstantApp) {
            badgeWithDrawable(bitmap, this.mContext.getDrawable(R.drawable.ic_instant_app_badge));
        }
        if (user != null) {
            BitmapDrawable drawable = new FixedSizeBitmapDrawable(bitmap);
            Drawable badged = this.mPm.getUserBadgedIcon(drawable, user);
            if (badged instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) badged).getBitmap();
            } else {
                bitmap = createIconBitmap(badged, 1.0f);
            }
        }
        return BitmapInfo.fromBitmap(bitmap, this.mDisableColorExtractor ? null : this.mColorExtractor);
    }

    public Bitmap createScaledBitmapWithoutShadow(Drawable icon, boolean shrinkNonAdaptiveIcons) {
        RectF iconBounds = new RectF();
        float[] scale = new float[1];
        return createIconBitmap(normalizeAndWrapToAdaptiveIcon(icon, shrinkNonAdaptiveIcons, iconBounds, scale), Math.min(scale[0], ShadowGenerator.getScaleForBounds(iconBounds)));
    }

    public void setWrapperBackgroundColor(int color) {
        this.mWrapperBackgroundColor = Color.alpha(color) < 255 ? -1 : color;
    }

    public void disableColorExtraction() {
        this.mDisableColorExtractor = true;
    }

    private Drawable normalizeAndWrapToAdaptiveIcon(@NonNull Drawable icon, boolean shrinkNonAdaptiveIcons, RectF outIconBounds, float[] outScale) {
        float scale;
        if (icon == null) {
            return null;
        }
        if (!shrinkNonAdaptiveIcons || !ATLEAST_OREO) {
            scale = getNormalizer().getScale(icon, outIconBounds, null, null);
        } else {
            if (this.mWrapperIcon == null) {
                this.mWrapperIcon = this.mContext.getDrawable(R.drawable.adaptive_icon_drawable_wrapper).mutate();
            }
            AdaptiveIconDrawable dr = (AdaptiveIconDrawable) this.mWrapperIcon;
            dr.setBounds(0, 0, 1, 1);
            boolean[] outShape = new boolean[1];
            scale = getNormalizer().getScale(icon, outIconBounds, dr.getIconMask(), outShape);
            if (!(icon instanceof AdaptiveIconDrawable) && !outShape[0]) {
                FixedScaleDrawable fsd = (FixedScaleDrawable) dr.getForeground();
                fsd.setDrawable(icon);
                fsd.setScale(scale);
                icon = dr;
                scale = getNormalizer().getScale(icon, outIconBounds, null, null);
                ((ColorDrawable) dr.getBackground()).setColor(this.mWrapperBackgroundColor);
            }
        }
        outScale[0] = scale;
        return icon;
    }

    public void badgeWithDrawable(Bitmap target, Drawable badge) {
        this.mCanvas.setBitmap(target);
        badgeWithDrawable(this.mCanvas, badge);
        this.mCanvas.setBitmap(null);
    }

    public void badgeWithDrawable(Canvas target, Drawable badge) {
        int badgeSize = getBadgeSizeForIconSize(this.mIconBitmapSize);
        int i = this.mIconBitmapSize;
        badge.setBounds(i - badgeSize, i - badgeSize, i, i);
        badge.draw(target);
    }

    private Bitmap createIconBitmap(Drawable icon, float scale) {
        return createIconBitmap(icon, scale, this.mIconBitmapSize);
    }

    public Bitmap createIconBitmap(@NonNull Drawable icon, float scale, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        if (icon == null) {
            return bitmap;
        }
        this.mCanvas.setBitmap(bitmap);
        this.mOldBounds.set(icon.getBounds());
        if (ATLEAST_OREO && (icon instanceof AdaptiveIconDrawable)) {
            int offset = Math.max((int) Math.ceil(size * 0.010416667f), Math.round((size * (1.0f - scale)) / 2.0f));
            icon.setBounds(offset, offset, size - offset, size - offset);
            icon.draw(this.mCanvas);
        } else {
            if (icon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap b = bitmapDrawable.getBitmap();
                if (bitmap != null && b.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(this.mContext.getResources().getDisplayMetrics());
                }
            }
            int width = size;
            int height = size;
            int intrinsicWidth = icon.getIntrinsicWidth();
            int intrinsicHeight = icon.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float ratio = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    height = (int) (width / ratio);
                } else if (intrinsicHeight > intrinsicWidth) {
                    width = (int) (height * ratio);
                }
            }
            int left = (size - width) / 2;
            int top = (size - height) / 2;
            icon.setBounds(left, top, left + width, top + height);
            this.mCanvas.save();
            this.mCanvas.scale(scale, scale, size / 2, size / 2);
            icon.draw(this.mCanvas);
            this.mCanvas.restore();
        }
        icon.setBounds(this.mOldBounds);
        this.mCanvas.setBitmap(null);
        return bitmap;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        clear();
    }

    public BitmapInfo makeDefaultIcon(UserHandle user) {
        return createBadgedIconBitmap(getFullResDefaultActivityIcon(this.mFillResIconDpi), user, Build.VERSION.SDK_INT);
    }

    public static Drawable getFullResDefaultActivityIcon(int iconDpi) {
        return Resources.getSystem().getDrawableForDensity(Build.VERSION.SDK_INT >= 26 ? 17301651 : 17629184, iconDpi);
    }

    public static int getBadgeSizeForIconSize(int iconSize) {
        return (int) (iconSize * ICON_BADGE_SCALE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }
}
