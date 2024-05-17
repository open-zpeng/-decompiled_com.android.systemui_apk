package com.android.systemui;

import android.content.Context;
import android.widget.Toast;
/* loaded from: classes21.dex */
public class SysUIToast {
    public static Toast makeText(Context context, int resId, int duration) {
        return makeText(context, context.getString(resId), duration);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.getWindowParams().privateFlags |= 16;
        return toast;
    }
}
