package com.android.systemui.assist;

import android.provider.DeviceConfig;
import androidx.annotation.Nullable;
import java.util.concurrent.Executor;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class DeviceConfigHelper {
    public long getLong(String name, long defaultValue) {
        return DeviceConfig.getLong("systemui", name, defaultValue);
    }

    public int getInt(String name, int defaultValue) {
        return DeviceConfig.getInt("systemui", name, defaultValue);
    }

    @Nullable
    public String getString(String name, @Nullable String defaultValue) {
        return DeviceConfig.getString("systemui", name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return DeviceConfig.getBoolean("systemui", name, defaultValue);
    }

    public void addOnPropertiesChangedListener(Executor executor, DeviceConfig.OnPropertiesChangedListener listener) {
        DeviceConfig.addOnPropertiesChangedListener("systemui", executor, listener);
    }
}
