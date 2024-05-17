package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.carmanager.CarClientWrapper;
import com.xiaopeng.systemui.carmanager.XuiClientWrapper;
import com.xiaopeng.systemui.infoflow.manager.ApiRouterDispatchManager;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.manager.FaceAngleManager;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
/* loaded from: classes24.dex */
public class Core {
    public static void init(Context context) {
        ContextManager.getInstance().init(context);
        MediaManager.getInstance().init(context);
        CarClientWrapper.getInstance().connectToCar(context);
        XuiClientWrapper.getInstance().connectToXui(context);
        new ApiRouterDispatchManager();
        if (CarModelsManager.getFeature().isFaceAngleEnabled()) {
            FaceAngleManager.getInstance().init(context);
        }
    }
}
