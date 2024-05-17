package com.android.systemui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.stack.StackStateAnimator;
/* loaded from: classes21.dex */
public class BrightnessDialog extends Activity {
    private BrightnessController mBrightnessController;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(48);
        window.clearFlags(2);
        window.requestFeature(1);
        View v = LayoutInflater.from(this).inflate(R.layout.quick_settings_brightness_dialog, (ViewGroup) null);
        setContentView(v);
        ToggleSliderView slider = (ToggleSliderView) findViewById(R.id.brightness_slider);
        this.mBrightnessController = new BrightnessController(this, slider);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        this.mBrightnessController.registerCallbacks();
        MetricsLogger.visible(this, (int) StackStateAnimator.ANIMATION_DURATION_DIMMED_ACTIVATED);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        MetricsLogger.hidden(this, (int) StackStateAnimator.ANIMATION_DURATION_DIMMED_ACTIVATED);
        this.mBrightnessController.unregisterCallbacks();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 25 || keyCode == 24 || keyCode == 164) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
