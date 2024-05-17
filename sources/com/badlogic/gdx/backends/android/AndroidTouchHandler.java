package com.badlogic.gdx.backends.android;

import android.content.Context;
import android.view.MotionEvent;
/* loaded from: classes21.dex */
public interface AndroidTouchHandler {
    void onTouch(MotionEvent motionEvent, AndroidInput androidInput);

    boolean supportsMultitouch(Context context);
}
