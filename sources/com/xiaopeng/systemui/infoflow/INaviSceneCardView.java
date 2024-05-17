package com.xiaopeng.systemui.infoflow;

import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
/* loaded from: classes24.dex */
public interface INaviSceneCardView {
    void setNaviSceneCardLaneData(Lane lane);

    void setNaviSceneCardManeuverData(Maneuver maneuver);

    void setNaviSceneCardNaviData(Navi navi);

    void setNaviSceneCardRemainInfoData(RemainInfo remainInfo);
}
