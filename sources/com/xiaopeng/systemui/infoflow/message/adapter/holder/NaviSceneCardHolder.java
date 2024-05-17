package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.INaviSceneCardView;
import com.xiaopeng.systemui.infoflow.navigation.view.LaneInfoView;
import com.xiaopeng.systemui.infoflow.navigation.view.NextManeuverInfoView;
import com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView;
import com.xiaopeng.systemui.infoflow.navigation.view.RouteInfoView;
import com.xiaopeng.systemui.utils.XTouchAreaUtils;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
/* loaded from: classes24.dex */
public class NaviSceneCardHolder extends BaseCardHolder implements INaviSceneCardView {
    private static final String TAG = "Card-NaviSceneCardHolder";
    private LaneInfoView mLaneInfoView;
    private NextManeuverInfoView mNextManeuverInfoView;
    private ImageView mQuitBtn;
    private RemainRollView mRemainRollView;
    private RouteInfoView mRouteInfoView;

    public NaviSceneCardHolder(View itemView) {
        super(itemView);
        itemView.setTag("NaviScene");
        this.mRouteInfoView = (RouteInfoView) itemView.findViewById(R.id.view_navi_route_info);
        this.mNextManeuverInfoView = (NextManeuverInfoView) itemView.findViewById(R.id.view_next_maneuver);
        this.mRemainRollView = (RemainRollView) itemView.findViewById(R.id.view_remain_roll);
        this.mLaneInfoView = (LaneInfoView) itemView.findViewById(R.id.card_navi_lane_info);
        this.mQuitBtn = (ImageView) itemView.findViewById(R.id.img_navi_quit);
        this.mQuitBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.NaviSceneCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NaviSceneCardHolder.this.mInfoflowCardPresenter.onCardCloseClicked();
            }
        });
        extendTouchArea();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 24;
    }

    private void extendTouchArea() {
        int[] paddingClose = {24, 24, 24, 24};
        XTouchAreaUtils.extendTouchArea(this.mQuitBtn, (ViewGroup) this.itemView, paddingClose);
    }

    private boolean isLandInfoViewShow(Lane lane) {
        if (lane.isLaneShow()) {
            return lane.getLaneType() == 0 ? lane.getFrontLane() != null && lane.getFrontLane().length > 0 : lane.getLaneType() == 1 && lane.getTollGateInfo() != null && lane.getTollGateInfo().length > 0;
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardNaviData(Navi navi) {
        if (navi == null) {
            Logger.d(TAG, "setNaviData navi data is null");
            this.mRouteInfoView.setVisibility(4);
            this.mNextManeuverInfoView.setVisibility(4);
            this.mRemainRollView.setVisibility(4);
            return;
        }
        Logger.d(TAG, "setNaviData- " + navi.getCurRouteName());
        this.mRouteInfoView.setVisibility(0);
        RouteInfoView routeInfoView = this.mRouteInfoView;
        if (routeInfoView == null) {
            Logger.d(TAG, "mRouteInfoView is null");
        } else {
            routeInfoView.setNaviData(navi);
        }
        boolean isTightTurnShow = navi.getIsTightTurnShow();
        if (!isTightTurnShow) {
            this.mNextManeuverInfoView.setVisibility(8);
            this.mRemainRollView.setVisibility(0);
        } else {
            this.mNextManeuverInfoView.setData(navi);
            this.mNextManeuverInfoView.setVisibility(0);
            this.mRemainRollView.setVisibility(8);
        }
        RemainRollView remainRollView = this.mRemainRollView;
        if (remainRollView != null) {
            remainRollView.setNaviRemainInfo(navi.getRouteRemainDistDisplay(), navi.getRouteRemainDistUnitDisplay(), navi.getRouteRemainTime());
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardManeuverData(Maneuver maneuverData) {
        RouteInfoView routeInfoView;
        if (maneuverData != null && (routeInfoView = this.mRouteInfoView) != null) {
            routeInfoView.setManeuverData(maneuverData);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfoData) {
        if (remainInfoData != null && this.mRemainRollView != null) {
            Logger.d(TAG, "setRemainInfoData- " + remainInfoData.getCarRemainDistDisplay());
            this.mRemainRollView.setRemainInfoData(remainInfoData);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardLaneData(Lane laneData) {
        if (laneData != null && this.mLaneInfoView != null) {
            Logger.d(TAG, "setLaneData- " + laneData.getLaneType());
            if (isLandInfoViewShow(laneData)) {
                this.mLaneInfoView.setVisibility(0);
                this.mLaneInfoView.setData(laneData);
                return;
            }
            this.mLaneInfoView.setVisibility(4);
        }
    }
}
