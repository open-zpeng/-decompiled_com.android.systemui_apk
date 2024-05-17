package com.android.systemui.colorextraction;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.types.ExtractionType;
import com.android.internal.colorextraction.types.Tonal;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.ConfigurationController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class SysuiColorExtractor extends ColorExtractor implements Dumpable, ConfigurationController.ConfigurationListener {
    private static final String TAG = "SysuiColorExtractor";
    private final ColorExtractor.GradientColors mBackdropColors;
    private boolean mHasMediaArtwork;
    private final ColorExtractor.GradientColors mNeutralColorsLock;
    private final Tonal mTonal;

    @Inject
    public SysuiColorExtractor(Context context, ConfigurationController configurationController) {
        this(context, new Tonal(context), configurationController, (WallpaperManager) context.getSystemService(WallpaperManager.class), false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @VisibleForTesting
    public SysuiColorExtractor(Context context, ExtractionType type, ConfigurationController configurationController, WallpaperManager wallpaperManager, boolean immediately) {
        super(context, type, immediately, wallpaperManager);
        this.mTonal = type instanceof Tonal ? (Tonal) type : new Tonal(context);
        this.mNeutralColorsLock = new ColorExtractor.GradientColors();
        configurationController.addCallback(this);
        this.mBackdropColors = new ColorExtractor.GradientColors();
        this.mBackdropColors.setMainColor(-16777216);
        if (wallpaperManager.isWallpaperSupported()) {
            wallpaperManager.removeOnColorsChangedListener(this);
            wallpaperManager.addOnColorsChangedListener(this, null, -1);
        }
    }

    protected void extractWallpaperColors() {
        super.extractWallpaperColors();
        Tonal tonal = this.mTonal;
        if (tonal == null || this.mNeutralColorsLock == null) {
            return;
        }
        tonal.applyFallback(this.mLockColors == null ? this.mSystemColors : this.mLockColors, this.mNeutralColorsLock);
    }

    public void onColorsChanged(WallpaperColors colors, int which, int userId) {
        if (userId != KeyguardUpdateMonitor.getCurrentUser()) {
            return;
        }
        if ((which & 2) != 0) {
            this.mTonal.applyFallback(colors, this.mNeutralColorsLock);
        }
        super.onColorsChanged(colors, which);
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onUiModeChanged() {
        extractWallpaperColors();
        triggerColorsChanged(3);
    }

    public ColorExtractor.GradientColors getColors(int which, int type) {
        if (this.mHasMediaArtwork && (which & 2) != 0) {
            return this.mBackdropColors;
        }
        return super.getColors(which, type);
    }

    public ColorExtractor.GradientColors getNeutralColors() {
        return this.mHasMediaArtwork ? this.mBackdropColors : this.mNeutralColorsLock;
    }

    public void setHasMediaArtwork(boolean hasBackdrop) {
        if (this.mHasMediaArtwork != hasBackdrop) {
            this.mHasMediaArtwork = hasBackdrop;
            triggerColorsChanged(2);
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("SysuiColorExtractor:");
        pw.println("  Current wallpaper colors:");
        pw.println("    system: " + this.mSystemColors);
        pw.println("    lock: " + this.mLockColors);
        ColorExtractor.GradientColors[] system = (ColorExtractor.GradientColors[]) this.mGradientColors.get(1);
        ColorExtractor.GradientColors[] lock = (ColorExtractor.GradientColors[]) this.mGradientColors.get(2);
        pw.println("  Gradients:");
        pw.println("    system: " + Arrays.toString(system));
        pw.println("    lock: " + Arrays.toString(lock));
        pw.println("  Neutral colors: " + this.mNeutralColorsLock);
        pw.println("  Has media backdrop: " + this.mHasMediaArtwork);
    }
}
