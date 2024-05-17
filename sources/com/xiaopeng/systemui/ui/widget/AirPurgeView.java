package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
/* loaded from: classes24.dex */
public class AirPurgeView extends AlphaOptimizedRelativeLayout {
    private AnimatedImageView mHvacAirPurgeAnimation;
    private AnimatedTextView mHvacAirPurgePm;

    public AirPurgeView(Context context) {
        super(context);
        initView(context);
    }

    public AirPurgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AirPurgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_air_purge, this);
        this.mHvacAirPurgeAnimation = (AnimatedImageView) findViewById(R.id.hvac_air_purge_animation);
        this.mHvacAirPurgePm = (AnimatedTextView) findViewById(R.id.hvac_air_purge_pm);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0) {
            AnimationHelper.startAnimInfinite(this.mHvacAirPurgeAnimation, R.drawable.dock_air_purge);
        } else {
            AnimationHelper.destroyAnim(this.mHvacAirPurgeAnimation);
        }
    }

    public void setQualityInner(int qualityInner) {
        AnimatedTextView animatedTextView = this.mHvacAirPurgePm;
        animatedTextView.setText(getContext().getString(R.string.inner) + qualityInner);
    }
}
