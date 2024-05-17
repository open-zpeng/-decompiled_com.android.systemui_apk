package com.android.systemui.tuner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.DemoMode;
import com.android.systemui.R;
import com.xiaopeng.lib.utils.info.BuildInfoUtils;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.systemui.controller.DropmenuController;
/* loaded from: classes21.dex */
public class DemoModeFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String DEMO_MODE_ON = "sysui_tuner_demo_on";
    private static final String[] STATUS_ICONS = {"volume", DropmenuController.DROPMENU_BLUETOOTH, "location", "alarm", "zen", "sync", "tty", "eri", "mute", "speakerphone", "managed_profile"};
    private final ContentObserver mDemoModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.systemui.tuner.DemoModeFragment.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            DemoModeFragment.this.updateDemoModeEnabled();
            DemoModeFragment.this.updateDemoModeOn();
        }
    };
    private SwitchPreference mEnabledSwitch;
    private SwitchPreference mOnSwitch;

    @Override // androidx.preference.PreferenceFragment
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        this.mEnabledSwitch = new SwitchPreference(context);
        this.mEnabledSwitch.setTitle(R.string.enable_demo_mode);
        this.mEnabledSwitch.setOnPreferenceChangeListener(this);
        this.mOnSwitch = new SwitchPreference(context);
        this.mOnSwitch.setTitle(R.string.show_demo_mode);
        this.mOnSwitch.setEnabled(false);
        this.mOnSwitch.setOnPreferenceChangeListener(this);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);
        screen.addPreference(this.mEnabledSwitch);
        screen.addPreference(this.mOnSwitch);
        setPreferenceScreen(screen);
        updateDemoModeEnabled();
        updateDemoModeOn();
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor(DemoMode.DEMO_MODE_ALLOWED), false, this.mDemoModeObserver);
        contentResolver.registerContentObserver(Settings.Global.getUriFor(DEMO_MODE_ON), false, this.mDemoModeObserver);
        setHasOptionsMenu(true);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(getContext(), 229, true);
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(getContext(), 229, false);
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        getContext().getContentResolver().unregisterContentObserver(this.mDemoModeObserver);
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDemoModeEnabled() {
        boolean enabled = Settings.Global.getInt(getContext().getContentResolver(), DemoMode.DEMO_MODE_ALLOWED, 0) != 0;
        this.mEnabledSwitch.setChecked(enabled);
        this.mOnSwitch.setEnabled(enabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDemoModeOn() {
        boolean enabled = Settings.Global.getInt(getContext().getContentResolver(), DEMO_MODE_ON, 0) != 0;
        this.mOnSwitch.setChecked(enabled);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean enabled = newValue == Boolean.TRUE;
        if (preference == this.mEnabledSwitch) {
            if (!enabled) {
                this.mOnSwitch.setChecked(false);
                stopDemoMode();
            }
            MetricsLogger.action(getContext(), 235, enabled);
            setGlobal(DemoMode.DEMO_MODE_ALLOWED, enabled ? 1 : 0);
        } else if (preference != this.mOnSwitch) {
            return false;
        } else {
            MetricsLogger.action(getContext(), 236, enabled);
            if (enabled) {
                startDemoMode();
            } else {
                stopDemoMode();
            }
        }
        return true;
    }

    private void startDemoMode() {
        String[] strArr;
        Intent intent = new Intent(DemoMode.ACTION_DEMO);
        intent.putExtra("command", "enter");
        getContext().sendBroadcast(intent);
        intent.putExtra("command", DemoMode.COMMAND_CLOCK);
        String demoTime = "1010";
        try {
            String[] versionParts = Build.VERSION.RELEASE.split("\\.");
            int majorVersion = Integer.valueOf(versionParts[0]).intValue();
            demoTime = String.format("%02d00", Integer.valueOf(majorVersion % 24));
        } catch (IllegalArgumentException e) {
        }
        intent.putExtra("hhmm", demoTime);
        getContext().sendBroadcast(intent);
        intent.putExtra("command", "network");
        intent.putExtra("wifi", "show");
        intent.putExtra("mobile", "show");
        intent.putExtra("sims", "1");
        intent.putExtra("nosim", OOBEEvent.STRING_FALSE);
        intent.putExtra("level", BuildInfoUtils.BID_LAN);
        intent.putExtra("datatype", "lte");
        getContext().sendBroadcast(intent);
        intent.putExtra("fully", OOBEEvent.STRING_TRUE);
        getContext().sendBroadcast(intent);
        intent.putExtra("command", DemoMode.COMMAND_BATTERY);
        intent.putExtra("level", "100");
        intent.putExtra("plugged", OOBEEvent.STRING_FALSE);
        getContext().sendBroadcast(intent);
        intent.putExtra("command", "status");
        for (String icon : STATUS_ICONS) {
            intent.putExtra(icon, "hide");
        }
        getContext().sendBroadcast(intent);
        intent.putExtra("command", DemoMode.COMMAND_NOTIFICATIONS);
        intent.putExtra("visible", OOBEEvent.STRING_FALSE);
        getContext().sendBroadcast(intent);
        setGlobal(DEMO_MODE_ON, 1);
    }

    private void stopDemoMode() {
        Intent intent = new Intent(DemoMode.ACTION_DEMO);
        intent.putExtra("command", DemoMode.COMMAND_EXIT);
        getContext().sendBroadcast(intent);
        setGlobal(DEMO_MODE_ON, 0);
    }

    private void setGlobal(String key, int value) {
        Settings.Global.putInt(getContext().getContentResolver(), key, value);
    }
}
