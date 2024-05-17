package com.android.systemui.qs;

import android.content.Context;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.external.TileServices;
import java.util.Collection;
/* loaded from: classes21.dex */
public interface QSHost {

    /* loaded from: classes21.dex */
    public interface Callback {
        void onTilesChanged();
    }

    void addCallback(Callback callback);

    void collapsePanels();

    void forceCollapsePanels();

    Context getContext();

    TileServices getTileServices();

    Collection<QSTile> getTiles();

    int indexOf(String str);

    void openPanels();

    void removeCallback(Callback callback);

    void removeTile(String str);

    void unmarkTileAsAutoAdded(String str);

    void warn(String str, Throwable th);
}
