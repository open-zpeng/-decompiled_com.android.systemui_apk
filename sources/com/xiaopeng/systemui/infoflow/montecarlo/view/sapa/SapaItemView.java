package com.xiaopeng.systemui.infoflow.montecarlo.view.sapa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.xuimanager.contextinfo.SapaInfo;
/* loaded from: classes24.dex */
public class SapaItemView extends AlphaOptimizedRelativeLayout {
    private static final int SAPA_TYPE_SERVER = 0;
    private static final int SAPA_TYPE_TOLL = 1;
    private static final String TAG = SapaItemView.class.getSimpleName();
    private SapaInfo mInfo;
    private SapaDetailView mSapaDetailView;
    private TextView mSapaDistance;
    private TextView mSapaName;
    private ImageView mTypeIcon;

    public SapaItemView(Context context) {
        super(context);
    }

    public SapaItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setData(SapaInfo sapaInfo) {
        this.mInfo = sapaInfo;
        this.mSapaName.setText(this.mInfo.getName());
        this.mSapaDistance.setText(NaviUtil.getDistanceString(getContext(), this.mInfo.getRemainDist()));
        this.mSapaDetailView.setSapaDetail(this.mInfo.getSapaDetail());
        if (this.mInfo.getType() == 0) {
            this.mTypeIcon.setImageResource(R.mipmap.ic_small_p);
            setBackgroundResource(R.drawable.bg_sapa_server);
            return;
        }
        this.mTypeIcon.setImageResource(R.mipmap.ic_small_toll);
        setBackgroundResource(R.drawable.bg_sapa_toll);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mSapaDetailView = (SapaDetailView) findViewById(R.id.view_sapa_detail);
        this.mSapaName = (TextView) findViewById(R.id.tv_sapa_name);
        this.mSapaDistance = (TextView) findViewById(R.id.tv_sapa_distance);
        this.mTypeIcon = (ImageView) findViewById(R.id.img_sapa_type);
    }
}
