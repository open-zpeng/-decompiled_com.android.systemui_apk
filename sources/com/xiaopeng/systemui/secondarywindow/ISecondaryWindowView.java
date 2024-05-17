package com.xiaopeng.systemui.secondarywindow;

import android.content.res.Configuration;
import com.xiaopeng.appstore.storeprovider.AssembleInfo;
import com.xiaopeng.appstore.storeprovider.AssembleResult;
import java.util.List;
/* loaded from: classes24.dex */
public interface ISecondaryWindowView {
    public static final int MODE_ENTRIES_TYPE_1 = 1;
    public static final int MODE_ENTRIES_TYPE_NORMAL = 0;

    void dispatchConfigurationChanged(Configuration configuration);

    void notifyDownloadInfo(AssembleInfo assembleInfo);

    void notifyDownloadResult(String str, AssembleResult assembleResult);

    void notifyUninstallResult(String str, int i);

    void onActivityChanged(String str);

    void setPsnBluetoothState(int i);

    void setPsnVolume(int i);

    default List<String> getNewInstalledApp() {
        return null;
    }

    default String getSecondScreenAppsInfo() {
        return null;
    }
}
