package com.android.systemui.tuner;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.preference.SwitchPreference;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.tuner.TunerService;
import java.util.Set;
/* loaded from: classes21.dex */
public class StatusBarSwitch extends SwitchPreference implements TunerService.Tunable {
    private Set<String> mBlacklist;

    public StatusBarSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, StatusBarIconController.ICON_BLACKLIST);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        super.onDetached();
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (!StatusBarIconController.ICON_BLACKLIST.equals(key)) {
            return;
        }
        this.mBlacklist = StatusBarIconController.getIconBlacklist(newValue);
        setChecked(!this.mBlacklist.contains(getKey()));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistBoolean(boolean value) {
        if (!value) {
            if (!this.mBlacklist.contains(getKey())) {
                MetricsLogger.action(getContext(), 234, getKey());
                this.mBlacklist.add(getKey());
                setList(this.mBlacklist);
                return true;
            }
            return true;
        } else if (this.mBlacklist.remove(getKey())) {
            MetricsLogger.action(getContext(), 233, getKey());
            setList(this.mBlacklist);
            return true;
        } else {
            return true;
        }
    }

    private void setList(Set<String> blacklist) {
        ContentResolver contentResolver = getContext().getContentResolver();
        Settings.Secure.putStringForUser(contentResolver, StatusBarIconController.ICON_BLACKLIST, TextUtils.join(",", blacklist), ActivityManager.getCurrentUser());
    }
}
