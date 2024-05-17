package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CarCheckCardPresenter extends BaseCardPresenter implements ICarCheckPresenter {
    private static final String TAG = "CarCheckCardPresenter";
    private final String CUSTOM_SERVICE_NUM = "4008193388";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final CarCheckCardPresenter sInstance = new CarCheckCardPresenter();

        private SingleHolder() {
        }
    }

    public static ICarCheckPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 19;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setFlags(268435456);
        Uri data = Uri.parse("tel:4008193388");
        intent.setData(data);
        PackageHelper.startActivity(this.mContext, intent, (Bundle) null);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardCloseClicked() {
        Logger.d(TAG, "onCloseClicked");
        Uri.Builder builder = new Uri.Builder();
        builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onCardCancel").appendQueryParameter("notifyId", String.valueOf(5)).appendQueryParameter("sencesId", String.valueOf(-1));
        try {
            ApiRouter.route(builder.build());
            Logger.d(TAG, "onCloseClick");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
    }
}
