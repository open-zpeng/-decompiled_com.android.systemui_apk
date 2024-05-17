package com.xiaopeng.xui.utils;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes25.dex */
public class XInputUtils {
    public static void ignoreHiddenInput(@NonNull View view) {
        view.setTag(268435456, 1001);
    }
}
