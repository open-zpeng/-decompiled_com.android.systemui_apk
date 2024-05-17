package com.xiaopeng.systemui.infoflow.helper;

import android.view.ViewGroup;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class VerticalAsrHelper extends AsrHelper {
    private static final String TAG = "VerticalAsrHelper";

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper
    protected void checkAvatarGone() {
    }

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper
    protected void checkAvatarShow() {
    }

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper
    protected void startListeningAnimation() {
        super.setAsrText(this.mContext.getString(R.string.i_am_listening));
    }

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper
    public void updateAsrContainer(ViewGroup asrContainer) {
        super.updateAsrContainer(asrContainer);
        super.setAsrText();
    }
}
