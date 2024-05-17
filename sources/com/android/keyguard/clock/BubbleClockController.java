package com.android.keyguard.clock;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ClockPlugin;
import java.util.TimeZone;
/* loaded from: classes19.dex */
public class BubbleClockController implements ClockPlugin {
    private ImageClock mAnalogClock;
    private final SmallClockPosition mClockPosition;
    private final SysuiColorExtractor mColorExtractor;
    private final LayoutInflater mLayoutInflater;
    private TextClock mLockClock;
    private View mLockClockContainer;
    private final Resources mResources;
    private ClockLayout mView;
    private final ViewPreviewer mRenderer = new ViewPreviewer();
    private final ClockPalette mPalette = new ClockPalette();

    public BubbleClockController(Resources res, LayoutInflater inflater, SysuiColorExtractor colorExtractor) {
        this.mResources = res;
        this.mLayoutInflater = inflater;
        this.mColorExtractor = colorExtractor;
        this.mClockPosition = new SmallClockPosition(res);
    }

    private void createViews() {
        this.mView = (ClockLayout) this.mLayoutInflater.inflate(R.layout.bubble_clock, (ViewGroup) null);
        this.mAnalogClock = (ImageClock) this.mView.findViewById(R.id.analog_clock);
        this.mLockClockContainer = this.mLayoutInflater.inflate(R.layout.digital_clock, (ViewGroup) null);
        this.mLockClock = (TextClock) this.mLockClockContainer.findViewById(R.id.lock_screen_clock);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void onDestroyView() {
        this.mView = null;
        this.mAnalogClock = null;
        this.mLockClockContainer = null;
        this.mLockClock = null;
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public String getName() {
        return "bubble";
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public String getTitle() {
        return this.mResources.getString(R.string.clock_title_bubble);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public Bitmap getThumbnail() {
        return BitmapFactory.decodeResource(this.mResources, R.drawable.bubble_thumbnail);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public Bitmap getPreview(int width, int height) {
        View view = getBigClockView();
        setDarkAmount(1.0f);
        setTextColor(-1);
        ColorExtractor.GradientColors colors = this.mColorExtractor.getColors(2);
        setColorPalette(colors.supportsDarkText(), colors.getColorPalette());
        onTimeTick();
        return this.mRenderer.createPreview(view, width, height);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public View getView() {
        if (this.mLockClockContainer == null) {
            createViews();
        }
        return this.mLockClockContainer;
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public View getBigClockView() {
        if (this.mView == null) {
            createViews();
        }
        return this.mView;
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public int getPreferredY(int totalHeight) {
        return this.mClockPosition.getPreferredY();
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void setStyle(Paint.Style style) {
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void setTextColor(int color) {
        updateColor();
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void setColorPalette(boolean supportsDarkText, int[] colorPalette) {
        this.mPalette.setColorPalette(supportsDarkText, colorPalette);
        updateColor();
    }

    private void updateColor() {
        int primary = this.mPalette.getPrimaryColor();
        int secondary = this.mPalette.getSecondaryColor();
        this.mLockClock.setTextColor(secondary);
        this.mAnalogClock.setClockColors(primary, secondary);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void setDarkAmount(float darkAmount) {
        this.mPalette.setDarkAmount(darkAmount);
        this.mClockPosition.setDarkAmount(darkAmount);
        this.mView.setDarkAmount(darkAmount);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void onTimeTick() {
        this.mAnalogClock.onTimeChanged();
        this.mView.onTimeChanged();
        this.mLockClock.refresh();
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public void onTimeZoneChanged(TimeZone timeZone) {
        this.mAnalogClock.onTimeZoneChanged(timeZone);
    }

    @Override // com.android.systemui.plugins.ClockPlugin
    public boolean shouldShowStatusArea() {
        return true;
    }
}
