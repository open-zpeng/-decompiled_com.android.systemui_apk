package com.android.systemui.shared.recents.utilities;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
/* loaded from: classes21.dex */
public class Utilities {
    public static void postAtFrontOfQueueAsynchronously(Handler h, Runnable r) {
        Message msg = h.obtainMessage().setCallback(r);
        h.sendMessageAtFrontOfQueue(msg);
    }

    public static float computeContrastBetweenColors(int bg, int fg) {
        float fgB;
        float fgR;
        float bgR = Color.red(bg) / 255.0f;
        float bgG = Color.green(bg) / 255.0f;
        float bgB = Color.blue(bg) / 255.0f;
        float bgL = ((bgR < 0.03928f ? bgR / 12.92f : (float) Math.pow((bgR + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.2126f) + ((bgG < 0.03928f ? bgG / 12.92f : (float) Math.pow((bgG + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.7152f) + ((bgB < 0.03928f ? bgB / 12.92f : (float) Math.pow((bgB + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.0722f);
        float fgR2 = Color.red(fg) / 255.0f;
        float fgG = Color.green(fg) / 255.0f;
        float fgB2 = Color.blue(fg) / 255.0f;
        if (fgR2 < 0.03928f) {
            fgR = fgR2 / 12.92f;
            fgB = fgB2;
        } else {
            fgB = fgB2;
            fgR = (float) Math.pow((fgR2 + 0.055f) / 1.055f, 2.4000000953674316d);
        }
        float fgL = (0.2126f * fgR) + (0.7152f * (fgG < 0.03928f ? fgG / 12.92f : (float) Math.pow((fgG + 0.055f) / 1.055f, 2.4000000953674316d))) + ((fgB < 0.03928f ? fgB / 12.92f : (float) Math.pow((fgB + 0.055f) / 1.055f, 2.4000000953674316d)) * 0.0722f);
        return Math.abs((fgL + 0.05f) / (0.05f + bgL));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
