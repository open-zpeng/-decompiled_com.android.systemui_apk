package com.xiaopeng.systemui.infoflow;

import android.content.res.Configuration;
import com.android.systemui.SystemUI;
import com.xiaopeng.systemui.infoflow.helper.ApiRouterEventHelper;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
/* loaded from: classes24.dex */
public class XInfoFlowProxy extends SystemUI {
    private AbstractInfoFlow mInfoFlow;

    @Override // com.android.systemui.SystemUI
    public void start() {
        ApiRouterEventHelper.notifySystemUIStart();
        if (OrientationUtil.isLandscapeScreen(this.mContext)) {
            this.mInfoFlow = new LandscapeInfoFlow(this.mContext);
        } else {
            this.mInfoFlow = new VerticalInfoFlow(this.mContext);
        }
        this.mInfoFlow.start();
    }

    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AbstractInfoFlow abstractInfoFlow = this.mInfoFlow;
        if (abstractInfoFlow != null) {
            abstractInfoFlow.onConfigChanged(newConfig);
        }
    }
}
