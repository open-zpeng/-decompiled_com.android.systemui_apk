package com.android.systemui.plugins;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.View;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import java.util.TimeZone;
@ProvidesInterface(action = ClockPlugin.ACTION, version = 5)
/* loaded from: classes21.dex */
public interface ClockPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_CLOCK";
    public static final int VERSION = 5;

    String getName();

    int getPreferredY(int i);

    Bitmap getPreview(int i, int i2);

    Bitmap getThumbnail();

    String getTitle();

    View getView();

    void onDestroyView();

    void setStyle(Paint.Style style);

    void setTextColor(int i);

    default View getBigClockView() {
        return null;
    }

    default void setColorPalette(boolean supportsDarkText, int[] colors) {
    }

    default void setDarkAmount(float darkAmount) {
    }

    default void onTimeTick() {
    }

    default void onTimeZoneChanged(TimeZone timeZone) {
    }

    default boolean shouldShowStatusArea() {
        return true;
    }
}
