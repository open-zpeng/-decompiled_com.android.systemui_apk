package com.xiaopeng.systemui.infoflow.navigation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
/* loaded from: classes24.dex */
public class RemainInfoView extends AlphaOptimizedLinearLayout {
    private static final String TAG = RemainInfoView.class.getSimpleName();
    private TextView mRemainDistance;
    private TextView mRemainTime;

    public RemainInfoView(Context context) {
        super(context);
    }

    public RemainInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRemainDistance = (TextView) findViewById(R.id.tv_remain_distance);
        this.mRemainTime = (TextView) findViewById(R.id.tv_remain_time);
    }

    public void setNaviRemainInfo(String routeRemainDist, int routeRemainDistUnitDisplay, double routerRemainTime) {
        String distance = NaviUtil.getDistanceString(getContext(), routeRemainDist, routeRemainDistUnitDisplay);
        this.mRemainDistance.setText(distance);
        String time = NaviUtil.getTimeString(getContext(), routerRemainTime);
        this.mRemainTime.setText(time);
    }
}
