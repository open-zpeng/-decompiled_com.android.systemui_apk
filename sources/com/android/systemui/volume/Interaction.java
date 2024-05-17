package com.android.systemui.volume;

import android.view.MotionEvent;
import android.view.View;
/* loaded from: classes21.dex */
public class Interaction {

    /* loaded from: classes21.dex */
    public interface Callback {
        void onInteraction();
    }

    public static void register(View v, final Callback callback) {
        v.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.volume.Interaction.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v2, MotionEvent event) {
                Callback.this.onInteraction();
                return false;
            }
        });
        v.setOnGenericMotionListener(new View.OnGenericMotionListener() { // from class: com.android.systemui.volume.Interaction.2
            @Override // android.view.View.OnGenericMotionListener
            public boolean onGenericMotion(View v2, MotionEvent event) {
                Callback.this.onInteraction();
                return false;
            }
        });
    }
}
