package com.android.systemui.shared.system;

import android.view.ThreadedRenderer;
/* loaded from: classes21.dex */
public class ThreadedRendererCompat {
    public static int EGL_CONTEXT_PRIORITY_HIGH_IMG = 12545;
    public static int EGL_CONTEXT_PRIORITY_MEDIUM_IMG = 12546;
    public static int EGL_CONTEXT_PRIORITY_LOW_IMG = 12547;

    public static void setContextPriority(int priority) {
        ThreadedRenderer.setContextPriority(priority);
    }
}
