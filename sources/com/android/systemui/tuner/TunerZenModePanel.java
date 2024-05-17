package com.android.systemui.tuner;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.systemui.Prefs;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.volume.ZenModePanel;
/* loaded from: classes21.dex */
public class TunerZenModePanel extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "TunerZenModePanel";
    private View mButtons;
    private ZenModePanel.Callback mCallback;
    private ZenModeController mController;
    private View mDone;
    private View.OnClickListener mDoneListener;
    private boolean mEditing;
    private View mHeaderSwitch;
    private View mMoreSettings;
    private final Runnable mUpdate;
    private int mZenMode;
    private ZenModePanel mZenModePanel;

    public TunerZenModePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUpdate = new Runnable() { // from class: com.android.systemui.tuner.TunerZenModePanel.1
            @Override // java.lang.Runnable
            public void run() {
                TunerZenModePanel.this.updatePanel();
            }
        };
    }

    public void init(ZenModeController zenModeController) {
        this.mController = zenModeController;
        this.mHeaderSwitch = findViewById(R.id.tuner_zen_switch);
        this.mHeaderSwitch.setVisibility(0);
        this.mHeaderSwitch.setOnClickListener(this);
        ((TextView) this.mHeaderSwitch.findViewById(16908310)).setText(R.string.quick_settings_dnd_label);
        this.mZenModePanel = (ZenModePanel) findViewById(R.id.zen_mode_panel);
        this.mZenModePanel.init(zenModeController);
        this.mButtons = findViewById(R.id.tuner_zen_buttons);
        this.mMoreSettings = this.mButtons.findViewById(16908314);
        this.mMoreSettings.setOnClickListener(this);
        ((TextView) this.mMoreSettings).setText(R.string.quick_settings_more_settings);
        this.mDone = this.mButtons.findViewById(16908313);
        this.mDone.setOnClickListener(this);
        ((TextView) this.mDone).setText(R.string.quick_settings_done);
        ViewGroup detail_header = (ViewGroup) findViewById(R.id.tuner_zen_switch);
        detail_header.getChildAt(0).setVisibility(8);
        findViewById(R.id.edit_container).setBackground(null);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mEditing = false;
    }

    public void setCallback(ZenModePanel.Callback zenPanelCallback) {
        this.mCallback = zenPanelCallback;
        this.mZenModePanel.setCallback(zenPanelCallback);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mHeaderSwitch) {
            this.mEditing = true;
            if (this.mZenMode == 0) {
                this.mZenMode = Prefs.getInt(this.mContext, Prefs.Key.DND_FAVORITE_ZEN, 3);
                this.mController.setZen(this.mZenMode, null, TAG);
                postUpdatePanel();
                return;
            }
            this.mZenMode = 0;
            this.mController.setZen(0, null, TAG);
            postUpdatePanel();
        } else if (v == this.mMoreSettings) {
            Intent intent = new Intent("android.settings.ZEN_MODE_SETTINGS");
            intent.addFlags(268435456);
            getContext().startActivity(intent);
        } else if (v == this.mDone) {
            this.mEditing = false;
            setVisibility(8);
            this.mDoneListener.onClick(v);
        }
    }

    public boolean isEditing() {
        return this.mEditing;
    }

    public void setZenState(int zenMode) {
        this.mZenMode = zenMode;
        postUpdatePanel();
    }

    private void postUpdatePanel() {
        removeCallbacks(this.mUpdate);
        postDelayed(this.mUpdate, 40L);
    }

    public void setDoneListener(View.OnClickListener onClickListener) {
        this.mDoneListener = onClickListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePanel() {
        boolean zenOn = this.mZenMode != 0;
        ((Checkable) this.mHeaderSwitch.findViewById(16908311)).setChecked(zenOn);
        this.mZenModePanel.setVisibility(zenOn ? 0 : 8);
        this.mButtons.setVisibility(zenOn ? 0 : 8);
    }
}
