package com.android.systemui.qs;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import com.android.systemui.statusbar.policy.Listenable;
/* loaded from: classes21.dex */
public abstract class GlobalSetting extends ContentObserver implements Listenable {
    private final Context mContext;
    private final String mSettingName;

    protected abstract void handleValueChanged(int i);

    public GlobalSetting(Context context, Handler handler, String settingName) {
        super(handler);
        this.mContext = context;
        this.mSettingName = settingName;
    }

    public int getValue() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), this.mSettingName, 0);
    }

    public void setValue(int value) {
        Settings.Global.putInt(this.mContext.getContentResolver(), this.mSettingName, value);
    }

    @Override // com.android.systemui.statusbar.policy.Listenable
    public void setListening(boolean listening) {
        if (listening) {
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor(this.mSettingName), false, this);
        } else {
            this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        handleValueChanged(getValue());
    }
}
