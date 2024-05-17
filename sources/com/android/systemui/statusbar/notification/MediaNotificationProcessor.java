package com.android.systemui.statusbar.notification;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import androidx.annotation.VisibleForTesting;
import androidx.palette.graphics.Palette;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.R;
import java.util.List;
/* loaded from: classes21.dex */
public class MediaNotificationProcessor {
    private static final float BLACK_MAX_LIGHTNESS = 0.08f;
    private static final double MINIMUM_IMAGE_FRACTION = 0.002d;
    private static final float MIN_SATURATION_WHEN_DECIDING = 0.19f;
    private static final float POPULATION_FRACTION_FOR_DOMINANT = 0.01f;
    private static final float POPULATION_FRACTION_FOR_MORE_VIBRANT = 1.0f;
    private static final float POPULATION_FRACTION_FOR_WHITE_OR_BLACK = 2.5f;
    private static final int RESIZE_BITMAP_AREA = 22500;
    private static final float WHITE_MIN_LIGHTNESS = 0.9f;
    private final Palette.Filter mBlackWhiteFilter;
    private final ImageGradientColorizer mColorizer;
    private final Context mContext;
    private final Context mPackageContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$0(int rgb, float[] hsl) {
        return !isWhiteOrBlack(hsl);
    }

    public MediaNotificationProcessor(Context context, Context packageContext) {
        this(context, packageContext, new ImageGradientColorizer());
    }

    @VisibleForTesting
    MediaNotificationProcessor(Context context, Context packageContext, ImageGradientColorizer colorizer) {
        this.mBlackWhiteFilter = new Palette.Filter() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$MediaNotificationProcessor$oWRwwE503YseXSqqQUwqkZxEskY
            @Override // androidx.palette.graphics.Palette.Filter
            public final boolean isAllowed(int i, float[] fArr) {
                return MediaNotificationProcessor.lambda$new$0(i, fArr);
            }
        };
        this.mContext = context;
        this.mPackageContext = packageContext;
        this.mColorizer = colorizer;
    }

    public void processNotification(Notification notification, Notification.Builder builder) {
        boolean z;
        int backgroundColor;
        Icon largeIcon = notification.getLargeIcon();
        if (largeIcon != null) {
            builder.setRebuildStyledRemoteViews(true);
            Drawable drawable = largeIcon.loadDrawable(this.mPackageContext);
            if (!notification.isColorizedMedia()) {
                z = false;
                backgroundColor = this.mContext.getColor(R.color.notification_material_background_color);
            } else {
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                int area = width * height;
                if (area > RESIZE_BITMAP_AREA) {
                    double factor = Math.sqrt(22500.0f / area);
                    width = (int) (width * factor);
                    height = (int) (height * factor);
                }
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
                Palette.Builder paletteBuilder = generateArtworkPaletteBuilder(bitmap);
                Palette palette = paletteBuilder.generate();
                Palette.Swatch backgroundSwatch = findBackgroundSwatch(palette);
                backgroundColor = backgroundSwatch.getRgb();
                int width2 = bitmap.getWidth();
                int height2 = bitmap.getHeight();
                z = false;
                paletteBuilder.setRegion((int) (bitmap.getWidth() * 0.4f), 0, width2, height2);
                if (!isWhiteOrBlack(backgroundSwatch.getHsl())) {
                    final float backgroundHue = backgroundSwatch.getHsl()[0];
                    paletteBuilder.addFilter(new Palette.Filter() { // from class: com.android.systemui.statusbar.notification.-$$Lambda$MediaNotificationProcessor$jNuRDwOMbOj8fwROH917lxaryoM
                        @Override // androidx.palette.graphics.Palette.Filter
                        public final boolean isAllowed(int i, float[] fArr) {
                            return MediaNotificationProcessor.lambda$processNotification$1(backgroundHue, i, fArr);
                        }
                    });
                }
                paletteBuilder.addFilter(this.mBlackWhiteFilter);
                Palette palette2 = paletteBuilder.generate();
                int foregroundColor = selectForegroundColor(backgroundColor, palette2);
                builder.setColorPalette(backgroundColor, foregroundColor);
            }
            ImageGradientColorizer imageGradientColorizer = this.mColorizer;
            if (this.mContext.getResources().getConfiguration().getLayoutDirection() == 1) {
                z = true;
            }
            Bitmap colorized = imageGradientColorizer.colorize(drawable, backgroundColor, z);
            builder.setLargeIcon(Icon.createWithBitmap(colorized));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$processNotification$1(float backgroundHue, int rgb, float[] hsl) {
        float diff = Math.abs(hsl[0] - backgroundHue);
        return diff > 10.0f && diff < 350.0f;
    }

    private int selectForegroundColor(int backgroundColor, Palette palette) {
        if (ContrastColorUtil.isColorLight(backgroundColor)) {
            return selectForegroundColorForSwatches(palette.getDarkVibrantSwatch(), palette.getVibrantSwatch(), palette.getDarkMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -16777216);
        }
        return selectForegroundColorForSwatches(palette.getLightVibrantSwatch(), palette.getVibrantSwatch(), palette.getLightMutedSwatch(), palette.getMutedSwatch(), palette.getDominantSwatch(), -1);
    }

    private int selectForegroundColorForSwatches(Palette.Swatch moreVibrant, Palette.Swatch vibrant, Palette.Swatch moreMutedSwatch, Palette.Swatch mutedSwatch, Palette.Swatch dominantSwatch, int fallbackColor) {
        Palette.Swatch coloredCandidate = selectVibrantCandidate(moreVibrant, vibrant);
        if (coloredCandidate == null) {
            coloredCandidate = selectMutedCandidate(mutedSwatch, moreMutedSwatch);
        }
        if (coloredCandidate != null) {
            if (dominantSwatch == coloredCandidate) {
                return coloredCandidate.getRgb();
            }
            if (coloredCandidate.getPopulation() / dominantSwatch.getPopulation() < POPULATION_FRACTION_FOR_DOMINANT && dominantSwatch.getHsl()[1] > MIN_SATURATION_WHEN_DECIDING) {
                return dominantSwatch.getRgb();
            }
            return coloredCandidate.getRgb();
        } else if (hasEnoughPopulation(dominantSwatch)) {
            return dominantSwatch.getRgb();
        } else {
            return fallbackColor;
        }
    }

    private Palette.Swatch selectMutedCandidate(Palette.Swatch first, Palette.Swatch second) {
        boolean firstValid = hasEnoughPopulation(first);
        boolean secondValid = hasEnoughPopulation(second);
        if (firstValid && secondValid) {
            float firstSaturation = first.getHsl()[1];
            float secondSaturation = second.getHsl()[1];
            float populationFraction = first.getPopulation() / second.getPopulation();
            if (firstSaturation * populationFraction > secondSaturation) {
                return first;
            }
            return second;
        } else if (firstValid) {
            return first;
        } else {
            if (secondValid) {
                return second;
            }
            return null;
        }
    }

    private Palette.Swatch selectVibrantCandidate(Palette.Swatch first, Palette.Swatch second) {
        boolean firstValid = hasEnoughPopulation(first);
        boolean secondValid = hasEnoughPopulation(second);
        if (firstValid && secondValid) {
            int firstPopulation = first.getPopulation();
            int secondPopulation = second.getPopulation();
            if (firstPopulation / secondPopulation < 1.0f) {
                return second;
            }
            return first;
        } else if (firstValid) {
            return first;
        } else {
            if (secondValid) {
                return second;
            }
            return null;
        }
    }

    private boolean hasEnoughPopulation(Palette.Swatch swatch) {
        return swatch != null && ((double) (((float) swatch.getPopulation()) / 22500.0f)) > MINIMUM_IMAGE_FRACTION;
    }

    public static Palette.Swatch findBackgroundSwatch(Bitmap artwork) {
        return findBackgroundSwatch(generateArtworkPaletteBuilder(artwork).generate());
    }

    private static Palette.Swatch findBackgroundSwatch(Palette palette) {
        Palette.Swatch dominantSwatch = palette.getDominantSwatch();
        if (dominantSwatch == null) {
            return new Palette.Swatch(-1, 100);
        }
        if (!isWhiteOrBlack(dominantSwatch.getHsl())) {
            return dominantSwatch;
        }
        List<Palette.Swatch> swatches = palette.getSwatches();
        float highestNonWhitePopulation = -1.0f;
        Palette.Swatch second = null;
        for (Palette.Swatch swatch : swatches) {
            if (swatch != dominantSwatch && swatch.getPopulation() > highestNonWhitePopulation && !isWhiteOrBlack(swatch.getHsl())) {
                second = swatch;
                highestNonWhitePopulation = swatch.getPopulation();
            }
        }
        if (second == null) {
            return dominantSwatch;
        }
        if (dominantSwatch.getPopulation() / highestNonWhitePopulation > POPULATION_FRACTION_FOR_WHITE_OR_BLACK) {
            return dominantSwatch;
        }
        return second;
    }

    private static Palette.Builder generateArtworkPaletteBuilder(Bitmap artwork) {
        return Palette.from(artwork).setRegion(0, 0, artwork.getWidth() / 2, artwork.getHeight()).clearFilters().resizeBitmapArea(RESIZE_BITMAP_AREA);
    }

    private static boolean isWhiteOrBlack(float[] hsl) {
        return isBlack(hsl) || isWhite(hsl);
    }

    private static boolean isBlack(float[] hslColor) {
        return hslColor[2] <= BLACK_MAX_LIGHTNESS;
    }

    private static boolean isWhite(float[] hslColor) {
        return hslColor[2] >= WHITE_MIN_LIGHTNESS;
    }
}
