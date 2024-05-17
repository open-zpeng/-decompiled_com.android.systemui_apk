package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.Context;
import android.os.LocaleList;
import android.os.UserHandle;
import androidx.annotation.Nullable;
import com.android.launcher3.icons.BitmapInfo;
/* loaded from: classes19.dex */
public interface CachingLogic<T> {
    ComponentName getComponent(T t);

    CharSequence getLabel(T t);

    UserHandle getUser(T t);

    void loadIcon(Context context, T t, BitmapInfo bitmapInfo);

    @Nullable
    default String getKeywords(T object, LocaleList localeList) {
        return null;
    }

    default boolean addToMemCache() {
        return true;
    }
}
