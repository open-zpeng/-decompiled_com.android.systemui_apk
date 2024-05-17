package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
/* loaded from: classes24.dex */
public class RemainInfoView extends AlphaOptimizedRelativeLayout {
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

    public void setNaviRemainInfo(double routeRemainDist, double routerRemainTime) {
        String distance = NaviUtil.getDistanceString(getContext(), routeRemainDist);
        SpannableString distanceSpan = NaviUtil.getDistanceSpannableString(distance, 36, 22);
        this.mRemainDistance.setText(distanceSpan);
        String time = NaviUtil.getTimeString(getContext(), routerRemainTime);
        SpannableString timeSpan = NaviUtil.getTimeSpannableString(time, 36, 22);
        this.mRemainTime.setText(timeSpan);
    }
}
