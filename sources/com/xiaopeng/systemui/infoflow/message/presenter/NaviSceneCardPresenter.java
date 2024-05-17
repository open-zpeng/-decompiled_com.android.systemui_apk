package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.INaviSceneCardView;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.DataLogUtils;
import com.xiaopeng.xuimanager.contextinfo.Camera;
import com.xiaopeng.xuimanager.contextinfo.CameraInterval;
import com.xiaopeng.xuimanager.contextinfo.Cross;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.contextinfo.Sapa;
/* loaded from: classes24.dex */
public class NaviSceneCardPresenter extends BaseCardPresenter {
    private static final String TAG = "NaviSceneCardPresenter";
    private INaviSceneCardView mNaviSceneCardView;
    private ContextManager.OnNaviDataChangeListener mOnNaviDataChangeListener = new ContextManager.OnNaviDataChangeListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.NaviSceneCardPresenter.1
        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onManeuverEvent(Maneuver maneuver) {
            NaviSceneCardPresenter.this.mInfoflowView.setNaviSceneCardManeuverData(maneuver);
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onNaviEvent(Navi navi) {
            Logger.d(NaviSceneCardPresenter.TAG, "onNaviEvent ： " + navi.getCurRouteName());
            NaviSceneCardPresenter.this.mInfoflowView.setNaviSceneCardNaviData(navi);
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onLaneEvent(Lane lane) {
            NaviSceneCardPresenter.this.mInfoflowView.setNaviSceneCardLaneData(lane);
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onCameraEvent(Camera camera) {
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onCameraIntervalEvent(CameraInterval cameraInterval) {
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onSapaEvent(Sapa sapa) {
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onCrossEvent(Cross cross) {
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onRemainInfoEvent(RemainInfo remainInfo) {
            NaviSceneCardPresenter.this.mInfoflowView.setNaviSceneCardRemainInfoData(remainInfo);
        }
    };
    private ContextManager mContextManager = ContextManager.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final NaviSceneCardPresenter sInstance = new NaviSceneCardPresenter();

        private SingleHolder() {
        }
    }

    public static NaviSceneCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected NaviSceneCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 24;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        Logger.d(TAG, "onViewAttachedToWindow: NaviSceneCard");
        BIHelper.sendBIData(BIHelper.ID.map_card, BIHelper.Type.infoflow, BIHelper.Action.open, BIHelper.Screen.main);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        Logger.d(TAG, "onViewDetachedFromWindow: NaviSceneCard");
        BIHelper.sendBIData(BIHelper.ID.map_card, BIHelper.Type.infoflow, BIHelper.Action.close, BIHelper.Screen.main);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        this.mContextManager.setOnNaviDataChangeListener(this.mOnNaviDataChangeListener);
        Logger.d(TAG, "NaviSceneCardPresenter ： setOnNaviDataChangeListener : " + this.mOnNaviDataChangeListener);
        INaviSceneCardView iNaviSceneCardView = this.mNaviSceneCardView;
        if (iNaviSceneCardView != null) {
            iNaviSceneCardView.setNaviSceneCardManeuverData(this.mContextManager.getCurrentManeuver());
            this.mNaviSceneCardView.setNaviSceneCardLaneData(this.mContextManager.getCurrentLane());
            this.mNaviSceneCardView.setNaviSceneCardNaviData(this.mContextManager.getCurrentNavi());
            this.mNaviSceneCardView.setNaviSceneCardRemainInfoData(this.mContextManager.getCurrentRemainInfo());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mNaviSceneCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mNaviSceneCardView = (INaviSceneCardView) this.mCardHolder;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardCloseClicked() {
        ContextManager.getInstance().setNavigationEnable(false);
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_NAVI_PAGE_ID, "B002", "1", isAppForeground() ? "1" : "0");
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        PackageHelper.startHomePackage(this.mContext);
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_NAVI_PAGE_ID, "B002", "0", isAppForeground() ? "1" : "0");
    }
}
