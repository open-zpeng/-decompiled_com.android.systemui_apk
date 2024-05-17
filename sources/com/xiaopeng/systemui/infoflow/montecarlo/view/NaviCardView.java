package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.montecarlo.view.sapa.SapaInfoView;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.xuimanager.contextinfo.Camera;
import com.xiaopeng.xuimanager.contextinfo.CameraInterval;
import com.xiaopeng.xuimanager.contextinfo.Cross;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.contextinfo.Sapa;
/* loaded from: classes24.dex */
public class NaviCardView extends AlphaOptimizedRelativeLayout implements ContextManager.OnNaviDataChangeListener {
    private static final String TAG = NaviCardView.class.getSimpleName();
    private CameraInfoView mCameraInfoView;
    private ContextManager mContextManager;
    private View mExitView;
    private LaneInfoView mLaneInfoView;
    private int mNaviMode;
    private NextManeuverInfoView mNextManeuverInfoView;
    private RemainRollView mRemainRollView;
    private RouteInfoView mRouteInfoView;
    private SapaInfoView mSapaInfoView;

    public NaviCardView(Context context) {
        super(context);
        this.mNaviMode = 2;
        init();
    }

    public NaviCardView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mNaviMode = 2;
        init();
    }

    private void init() {
        this.mContextManager = ContextManager.getInstance();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRouteInfoView = (RouteInfoView) findViewById(R.id.view_route_info);
        this.mNextManeuverInfoView = (NextManeuverInfoView) findViewById(R.id.view_next_maneuver);
        this.mRemainRollView = (RemainRollView) findViewById(R.id.view_remain_roll);
        this.mCameraInfoView = (CameraInfoView) findViewById(R.id.view_camera);
        this.mLaneInfoView = (LaneInfoView) findViewById(R.id.view_lane_info);
        this.mSapaInfoView = (SapaInfoView) findViewById(R.id.view_sapa_info);
        this.mExitView = findViewById(R.id.view_exit);
        this.mExitView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.NaviCardView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NaviCardView.this.mContextManager.setNavigationEnable(false);
            }
        });
        this.mExitView.setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContextManager.setOnNaviDataChangeListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mContextManager.setOnNaviDataChangeListener(null);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onManeuverEvent(Maneuver maneuver) {
        this.mRouteInfoView.setManeuverData(maneuver);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onNaviEvent(Navi navi) {
        this.mRouteInfoView.setNaviData(navi);
        this.mRemainRollView.setNaviRemainInfo(navi.getRouteRemainDist(), navi.getRouteRemainTime());
        this.mRemainRollView.setVisibility(0);
        boolean isTightTurnShow = navi.getIsTightTurnShow();
        if (isTightTurnShow) {
            this.mNextManeuverInfoView.setData(navi);
            this.mNextManeuverInfoView.setVisibility(0);
            return;
        }
        this.mNextManeuverInfoView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onLaneEvent(Lane lane) {
        if (lane.isLaneShow()) {
            this.mLaneInfoView.setVisibility(0);
            this.mLaneInfoView.setData(lane);
            return;
        }
        this.mLaneInfoView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onCameraEvent(Camera camera) {
        if (this.mNaviMode == 2 && camera.getIsCameraShow()) {
            this.mCameraInfoView.setData(camera);
            this.mCameraInfoView.setVisibility(0);
            return;
        }
        this.mCameraInfoView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onCameraIntervalEvent(CameraInterval cameraInterval) {
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onSapaEvent(Sapa sapa) {
        if (sapa.getIsSapaShow()) {
            this.mSapaInfoView.setVisibility(0);
            this.mSapaInfoView.setData(sapa);
            return;
        }
        this.mSapaInfoView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onCrossEvent(Cross cross) {
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviDataChangeListener
    public void onRemainInfoEvent(RemainInfo remainInfo) {
        this.mRemainRollView.setRemainInfoData(remainInfo);
    }

    public void resetStatus() {
        this.mLaneInfoView.setVisibility(8);
        this.mSapaInfoView.setVisibility(8);
        this.mNextManeuverInfoView.setVisibility(8);
    }

    public void setNaviMode(int naviMode) {
        this.mNaviMode = naviMode;
        View view = this.mExitView;
        if (view != null) {
            view.setVisibility(this.mNaviMode == 1 ? 0 : 4);
        }
    }
}
