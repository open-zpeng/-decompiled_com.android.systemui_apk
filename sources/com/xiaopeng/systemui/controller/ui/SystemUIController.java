package com.xiaopeng.systemui.controller.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import com.android.systemui.SystemUIApplication;
/* loaded from: classes24.dex */
public class SystemUIController {
    private final Context mContext;
    private volatile ISpeechUI mISpeechUI;

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        @SuppressLint({"StaticFieldLeak"})
        private static final SystemUIController sInstance = new SystemUIController();

        private SingleHolder() {
        }
    }

    public static SystemUIController get() {
        return SingleHolder.sInstance;
    }

    private SystemUIController() {
        this.mContext = SystemUIApplication.getContext().getApplicationContext();
    }

    public ISpeechUI getISpeechUI() {
        if (this.mISpeechUI == null) {
            synchronized (this) {
                if (this.mISpeechUI == null) {
                    this.mISpeechUI = new SpeechUIImpl(this.mContext);
                }
            }
        }
        return this.mISpeechUI;
    }
}
