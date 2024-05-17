package com.xiaopeng.systemui.infoflow.message.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.IExplorerSceneCardView;
import com.xiaopeng.systemui.infoflow.navigation.view.LaneInfoView;
import com.xiaopeng.systemui.infoflow.navigation.view.RouteInfoView;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
/* loaded from: classes24.dex */
public class ExploreSceneCardHolder extends BaseCardHolder implements IExplorerSceneCardView {
    private static final String TAG = "Card-ExploreSceneCardHolder";
    private LaneInfoView mLaneInfoView;
    private ImageView mQuitBtn;
    private RouteInfoView mRouteInfoView;

    public ExploreSceneCardHolder(View itemView) {
        super(itemView);
        this.mRouteInfoView = (RouteInfoView) itemView.findViewById(R.id.view_navi_route_info);
        this.mLaneInfoView = (LaneInfoView) itemView.findViewById(R.id.card_navi_lane_info);
        this.mQuitBtn = (ImageView) itemView.findViewById(R.id.img_navi_quit);
        this.mRouteInfoView.setExploreMode(true);
        this.mQuitBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.message.adapter.holder.ExploreSceneCardHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ExploreSceneCardHolder.this.mInfoflowCardPresenter.onCardCloseClicked();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder
    public int getCardType() {
        return 27;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardHolder, android.view.View.OnClickListener
    public void onClick(View view) {
        this.mInfoflowCardPresenter.onCardClicked();
    }

    private boolean isLandInfoViewShow(Lane lane) {
        if (lane.isLaneShow()) {
            return lane.getLaneType() == 0 ? lane.getFrontLane() != null && lane.getFrontLane().length > 0 : lane.getLaneType() == 1 && lane.getTollGateInfo() != null && lane.getTollGateInfo().length > 0;
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardNaviData(Navi navi) {
        if (navi == null) {
            Logger.d(TAG, "setNaviData navi data is null");
            this.mRouteInfoView.setVisibility(4);
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
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardManeuverData(Maneuver maneuverData) {
        RouteInfoView routeInfoView;
        if (maneuverData != null && (routeInfoView = this.mRouteInfoView) != null) {
            routeInfoView.setManeuverData(maneuverData);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardLaneData(Lane laneData) {
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
