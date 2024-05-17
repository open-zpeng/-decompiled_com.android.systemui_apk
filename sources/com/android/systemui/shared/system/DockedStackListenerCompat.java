package com.android.systemui.shared.system;

import android.view.IDockedStackListener;
/* loaded from: classes21.dex */
public class DockedStackListenerCompat {
    IDockedStackListener.Stub mListener = new IDockedStackListener.Stub() { // from class: com.android.systemui.shared.system.DockedStackListenerCompat.1
        public void onDividerVisibilityChanged(boolean visible) {
        }

        public void onDockedStackExistsChanged(boolean exists) {
            DockedStackListenerCompat.this.onDockedStackExistsChanged(exists);
        }

        public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) {
            DockedStackListenerCompat.this.onDockedStackMinimizedChanged(minimized, animDuration, isHomeStackResizable);
        }

        public void onAdjustedForImeChanged(boolean adjustedForIme, long animDuration) {
        }

        public void onDockSideChanged(int newDockSide) {
            DockedStackListenerCompat.this.onDockSideChanged(newDockSide);
        }
    };

    public void onDockedStackExistsChanged(boolean exists) {
    }

    public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) {
    }

    public void onDockSideChanged(int newDockSide) {
    }
}
