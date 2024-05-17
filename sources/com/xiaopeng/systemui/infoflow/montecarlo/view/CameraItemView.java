package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.xuimanager.contextinfo.CameraInfo;
/* loaded from: classes24.dex */
public class CameraItemView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = CameraItemView.class.getSimpleName();
    private CameraInfo mData;
    private ImageView mIcon;
    private ImageView mNextIcon;
    private TextView mTitle;

    public CameraItemView(Context context) {
        super(context);
    }

    public CameraItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIcon = (ImageView) findViewById(R.id.img_eleye);
        this.mTitle = (TextView) findViewById(R.id.tv_eleye_distance);
        this.mNextIcon = (ImageView) findViewById(R.id.img_next);
    }

    public void setData(CameraInfo cameraInfo) {
        this.mData = cameraInfo;
        String cameraDist = NaviUtil.getDistanceString(getContext(), cameraInfo.getCameraDist());
        SpannableString disSpan = NaviUtil.getDistanceSpannableString(cameraDist, 36, 24);
        this.mTitle.setText(disSpan);
        setIcon(cameraInfo.getCameraType());
    }

    private void setIcon(int type) {
        int resId = R.mipmap.ic_mid_transit;
        switch (type) {
            case 1:
            case 3:
                resId = R.mipmap.ic_mid_monitor;
                break;
            case 2:
                resId = R.mipmap.ic_mid_trafficsignal;
                break;
            case 4:
                resId = R.mipmap.ic_mid_transit;
                break;
            case 5:
                resId = R.mipmap.ic_mid_emergencyvehiclelane;
                break;
            case 6:
                resId = R.mipmap.ic_mid_enonmotorvehicle;
                break;
        }
        this.mIcon.setImageResource(resId);
    }

    public void setNextViewVisiable(boolean show) {
        this.mNextIcon.setVisibility(show ? 0 : 8);
    }
}
