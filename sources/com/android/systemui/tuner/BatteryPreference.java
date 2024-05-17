package com.android.systemui.tuner;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import androidx.preference.DropDownPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
/* loaded from: classes21.dex */
public class BatteryPreference extends DropDownPreference implements TunerService.Tunable {
    private static final String DEFAULT = "default";
    private static final String DISABLED = "disabled";
    private static final String PERCENT = "percent";
    private final String mBattery;
    private boolean mBatteryEnabled;
    private ArraySet<String> mBlacklist;
    private boolean mHasPercentage;
    private boolean mHasSetValue;

    public BatteryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBattery = context.getString(17041080);
        setEntryValues(new CharSequence[]{PERCENT, DEFAULT, DISABLED});
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mHasPercentage = Settings.System.getInt(getContext().getContentResolver(), "status_bar_show_battery_percent", 0) != 0;
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, StatusBarIconController.ICON_BLACKLIST);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        super.onDetached();
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (StatusBarIconController.ICON_BLACKLIST.equals(key)) {
            this.mBlacklist = StatusBarIconController.getIconBlacklist(newValue);
            this.mBatteryEnabled = !this.mBlacklist.contains(this.mBattery);
        }
        if (!this.mHasSetValue) {
            this.mHasSetValue = true;
            if (this.mBatteryEnabled && this.mHasPercentage) {
                setValue(PERCENT);
            } else if (this.mBatteryEnabled) {
                setValue(DEFAULT);
            } else {
                setValue(DISABLED);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistString(String value) {
        boolean v = PERCENT.equals(value);
        MetricsLogger.action(getContext(), 237, v);
        Settings.System.putInt(getContext().getContentResolver(), "status_bar_show_battery_percent", v ? 1 : 0);
        if (DISABLED.equals(value)) {
            this.mBlacklist.add(this.mBattery);
        } else {
            this.mBlacklist.remove(this.mBattery);
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue(StatusBarIconController.ICON_BLACKLIST, TextUtils.join(",", this.mBlacklist));
        return true;
    }
}
