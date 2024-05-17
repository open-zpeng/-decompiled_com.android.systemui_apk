package com.android.systemui.plugins;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.systemui.plugins.annotations.Dependencies;
import com.android.systemui.plugins.annotations.DependsOn;
import com.android.systemui.plugins.annotations.ProvidesInterface;
@Dependencies({@DependsOn(target = Callbacks.class), @DependsOn(target = PanelViewController.class)})
@ProvidesInterface(action = GlobalActionsPanelPlugin.ACTION, version = 0)
/* loaded from: classes21.dex */
public interface GlobalActionsPanelPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_GLOBAL_ACTIONS_PANEL";
    public static final int VERSION = 0;

    PanelViewController onPanelShown(Callbacks callbacks, boolean z);

    @ProvidesInterface(version = 0)
    /* loaded from: classes21.dex */
    public interface Callbacks {
        public static final int VERSION = 0;

        void dismissGlobalActionsMenu();

        default void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
            }
        }
    }

    @ProvidesInterface(version = 0)
    /* loaded from: classes21.dex */
    public interface PanelViewController {
        public static final int VERSION = 0;

        View getPanelContent();

        void onDeviceLockStateChanged(boolean z);

        void onDismissed();

        default Drawable getBackgroundDrawable() {
            return null;
        }
    }
}
