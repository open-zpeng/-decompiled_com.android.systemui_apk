package com.xiaopeng.iotlib.utils;

import android.support.rastermill.FrameSequenceUtil;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
/* loaded from: classes22.dex */
public class WebpUtils {
    public static void loadWebp(ImageView view, @DrawableRes int res) {
        try {
            FrameSequenceUtil.destroy(view);
            FrameSequenceUtil.with(view).resourceId(res).decodingThreadId(0).loopBehavior(2).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void destroy(ImageView view) {
        try {
            FrameSequenceUtil.destroy(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
