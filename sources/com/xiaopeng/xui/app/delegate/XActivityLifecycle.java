package com.xiaopeng.xui.app.delegate;

import android.os.Bundle;
/* loaded from: classes25.dex */
interface XActivityLifecycle {
    default void onCreate(Bundle savedInstanceState) {
    }

    default void onStart() {
    }

    default void onRecreate() {
    }

    default void onResume() {
    }

    default void onPause() {
    }

    default void onStop() {
    }

    default void onDestroy() {
    }
}
