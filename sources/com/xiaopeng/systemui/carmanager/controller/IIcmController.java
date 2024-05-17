package com.xiaopeng.systemui.carmanager.controller;

import android.view.KeyEvent;
import com.xiaopeng.systemui.carmanager.IBaseCallback;
import com.xiaopeng.systemui.carmanager.IBaseCarController;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import java.util.List;
/* loaded from: classes24.dex */
public interface IIcmController extends IBaseCarController<Callback> {
    public static final String CMD_ACTION = "Action";
    public static final String CMD_CLOSE = "close";
    public static final String CMD_OPEN = "open";
    public static final int MEDIA_PALYED = 1;
    public static final int MEDIA_PAUSED = 0;

    /* loaded from: classes24.dex */
    public interface Callback extends IBaseCallback {
        void onInfoflowMsg(String str);
    }

    void exitEnjoyMode();

    void setIcmAllCard(List<CardEntry> list);

    void setIcmInfoCardAdd(CardEntry cardEntry);

    void setIcmInfoCardRemoved(CardEntry cardEntry);

    void setIcmInfoCardUpdate(CardEntry cardEntry);

    void setWheelKey(KeyEvent keyEvent);
}
