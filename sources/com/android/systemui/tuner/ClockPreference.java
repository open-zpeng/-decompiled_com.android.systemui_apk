package com.android.systemui.tuner;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import androidx.preference.DropDownPreference;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.policy.Clock;
import com.android.systemui.tuner.TunerService;
/* loaded from: classes21.dex */
public class ClockPreference extends DropDownPreference implements TunerService.Tunable {
    private static final String DEFAULT = "default";
    private static final String DISABLED = "disabled";
    private static final String SECONDS = "seconds";
    private ArraySet<String> mBlacklist;
    private final String mClock;
    private boolean mClockEnabled;
    private boolean mHasSeconds;
    private boolean mHasSetValue;
    private boolean mReceivedClock;
    private boolean mReceivedSeconds;

    public ClockPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClock = context.getString(17041085);
        setEntryValues(new CharSequence[]{SECONDS, DEFAULT, DISABLED});
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, StatusBarIconController.ICON_BLACKLIST, Clock.CLOCK_SECONDS);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        super.onDetached();
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (StatusBarIconController.ICON_BLACKLIST.equals(key)) {
            this.mReceivedClock = true;
            this.mBlacklist = StatusBarIconController.getIconBlacklist(newValue);
            this.mClockEnabled = !this.mBlacklist.contains(this.mClock);
        } else if (Clock.CLOCK_SECONDS.equals(key)) {
            this.mReceivedSeconds = true;
            this.mHasSeconds = (newValue == null || Integer.parseInt(newValue) == 0) ? false : true;
        }
        if (!this.mHasSetValue && this.mReceivedClock && this.mReceivedSeconds) {
            this.mHasSetValue = true;
            if (this.mClockEnabled && this.mHasSeconds) {
                setValue(SECONDS);
            } else if (this.mClockEnabled) {
                setValue(DEFAULT);
            } else {
                setValue(DISABLED);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistString(String value) {
        ((TunerService) Dependency.get(TunerService.class)).setValue(Clock.CLOCK_SECONDS, SECONDS.equals(value) ? 1 : 0);
        if (DISABLED.equals(value)) {
            this.mBlacklist.add(this.mClock);
        } else {
            this.mBlacklist.remove(this.mClock);
        }
        ((TunerService) Dependency.get(TunerService.class)).setValue(StatusBarIconController.ICON_BLACKLIST, TextUtils.join(",", this.mBlacklist));
        return true;
    }
}
