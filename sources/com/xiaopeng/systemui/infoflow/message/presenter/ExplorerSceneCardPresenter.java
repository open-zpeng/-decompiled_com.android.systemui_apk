package com.xiaopeng.systemui.infoflow.message.presenter;

import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.IExplorerSceneCardView;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
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
public class ExplorerSceneCardPresenter extends BaseCardPresenter {
    private IExplorerSceneCardView mExplorerSceneCardView;
    private ContextManager.OnNaviDataChangeListener mOnNaviDataChangeListener = new ContextManager.OnNaviDataChangeListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.ExplorerSceneCardPresenter.1
        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onManeuverEvent(Maneuver maneuver) {
            if (ExplorerSceneCardPresenter.this.mExplorerSceneCardView != null) {
                ExplorerSceneCardPresenter.this.mExplorerSceneCardView.setExploreSceneCardManeuverData(maneuver);
            }
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onNaviEvent(Navi navi) {
            if (ExplorerSceneCardPresenter.this.mExplorerSceneCardView != null) {
                ExplorerSceneCardPresenter.this.mExplorerSceneCardView.setExploreSceneCardNaviData(navi);
            }
        }

        @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
        public void onLaneEvent(Lane lane) {
            if (ExplorerSceneCardPresenter.this.mExplorerSceneCardView != null) {
                ExplorerSceneCardPresenter.this.mExplorerSceneCardView.setExploreSceneCardLaneData(lane);
            }
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
        }
    };
    private ContextManager mContextManager = ContextManager.getInstance();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final ExplorerSceneCardPresenter sInstance = new ExplorerSceneCardPresenter();

        private SingleHolder() {
        }
    }

    public static ExplorerSceneCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected ExplorerSceneCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 27;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        this.mContextManager.setOnNaviDataChangeListener(this.mOnNaviDataChangeListener);
        IExplorerSceneCardView iExplorerSceneCardView = this.mExplorerSceneCardView;
        if (iExplorerSceneCardView != null) {
            iExplorerSceneCardView.setExploreSceneCardManeuverData(this.mContextManager.getCurrentManeuver());
            this.mExplorerSceneCardView.setExploreSceneCardLaneData(this.mContextManager.getCurrentLane());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mExplorerSceneCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mExplorerSceneCardView = (IExplorerSceneCardView) this.mCardHolder;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardCloseClicked() {
        ContextManager.getInstance().setNavigationEnable(false);
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_NAVI_PAGE_ID, "B003", "1", isAppForeground() ? "1" : "0");
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        PackageHelper.startHomePackage(this.mContext);
        DataLogUtils.sendInfoDataLog(DataLogUtils.INFO_NAVI_PAGE_ID, "B003", "0", isAppForeground() ? "1" : "0");
    }
}
