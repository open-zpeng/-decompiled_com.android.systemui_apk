package com.xiaopeng.xui.vui.floatinglayer;

import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayer;
/* loaded from: classes25.dex */
class VuiFloatingLocationUtils {
    private static final String TAG = "VuiFloatingLocation";

    VuiFloatingLocationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] getLocation(int type, VuiFloatingLayer.LayerInfo info, int viewWidth, int viewHeight) {
        int[] location = new int[2];
        if (info == null) {
            return location;
        }
        int left = info.location[0];
        if (type == 0) {
            int marginStart = info.targetWidth;
            int marginTop = (info.location[1] + (info.targetHeight / 2)) - (viewHeight / 2);
            int offsetStart = info.mCenterOffsetX + (((marginStart / 2) + left) - (viewWidth / 2));
            int offsetTop = info.mCenterOffsetY + marginTop;
            if (offsetStart < left || offsetStart > info.targetWidth + left) {
                log("offset more or less than current view width");
            }
            location[0] = offsetStart;
            location[1] = offsetTop;
        } else if (type == 1) {
            int marginStart2 = ((info.targetWidth / 2) + left) - (viewWidth / 2);
            int marginTop2 = (info.location[1] - viewHeight) + 35;
            int offsetStart2 = info.mCenterOffsetX + marginStart2;
            int offsetTop2 = info.mCenterOffsetY + marginTop2;
            if (offsetStart2 < left || offsetStart2 > info.targetWidth + left) {
                log("offset more or less than current view width");
            }
            location[0] = offsetStart2;
            location[1] = offsetTop2;
        }
        return location;
    }

    private static void log(String msg) {
        XLogUtils.v(TAG, msg);
    }
}
