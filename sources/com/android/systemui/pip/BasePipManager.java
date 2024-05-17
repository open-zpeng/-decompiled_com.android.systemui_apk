package com.android.systemui.pip;

import android.content.Context;
import android.content.res.Configuration;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public interface BasePipManager {
    void initialize(Context context);

    void onConfigurationChanged(Configuration configuration);

    void showPictureInPictureMenu();

    default void expandPip() {
    }

    default void hidePipMenu(Runnable onStartCallback, Runnable onEndCallback) {
    }

    default void dump(PrintWriter pw) {
    }
}
