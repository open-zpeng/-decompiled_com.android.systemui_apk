package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.os.Build;
import com.badlogic.gdx.Application;
import java.lang.reflect.Constructor;
/* loaded from: classes21.dex */
public class AndroidInputFactory {
    public static AndroidInput newAndroidInput(Application activity, Context context, Object view, AndroidApplicationConfiguration config) {
        Class<?> clazz;
        try {
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= 12) {
                clazz = Class.forName("com.badlogic.gdx.backends.android.AndroidInputThreePlus");
            } else {
                clazz = Class.forName("com.badlogic.gdx.backends.android.AndroidInput");
            }
            Constructor<?> constructor = clazz.getConstructor(Application.class, Context.class, Object.class, AndroidApplicationConfiguration.class);
            AndroidInput input = (AndroidInput) constructor.newInstance(activity, context, view, config);
            return input;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't construct AndroidInput, this should never happen", e);
        }
    }
}
