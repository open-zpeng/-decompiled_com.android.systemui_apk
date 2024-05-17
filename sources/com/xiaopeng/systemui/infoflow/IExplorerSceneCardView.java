package com.xiaopeng.systemui.infoflow;

import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
/* loaded from: classes24.dex */
public interface IExplorerSceneCardView {
    void setExploreSceneCardLaneData(Lane lane);

    void setExploreSceneCardManeuverData(Maneuver maneuver);

    void setExploreSceneCardNaviData(Navi navi);
}
