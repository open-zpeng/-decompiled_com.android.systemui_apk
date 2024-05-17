package com.android.keyguard;

import android.content.res.ColorStateList;
/* loaded from: classes19.dex */
public interface SecurityMessageDisplay {
    void formatMessage(int i, Object... objArr);

    void setMessage(int i);

    void setMessage(CharSequence charSequence);

    void setNextMessageColor(ColorStateList colorStateList);
}
