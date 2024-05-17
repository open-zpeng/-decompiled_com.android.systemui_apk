package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
/* loaded from: classes24.dex */
public class NgpWarningView extends RelativeLayout {
    private static final String TAG = "NgpWarningView";
    private AnimatedImageView mNgpViewL;
    private AnimatedImageView mNgpViewR;

    public NgpWarningView(Context context) {
        super(context);
    }

    public NgpWarningView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NgpWarningView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mNgpViewL = (AnimatedImageView) findViewById(R.id.ngp_warning_l);
        this.mNgpViewR = (AnimatedImageView) findViewById(R.id.ngp_warning_r);
    }

    public void startWarningAnim() {
        this.mNgpViewL.setVisibility(0);
        this.mNgpViewR.setVisibility(0);
        Animation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
        alphaAnimation.setDuration(766L);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatMode(2);
        alphaAnimation.setRepeatCount(-1);
        this.mNgpViewL.startAnimation(alphaAnimation);
        this.mNgpViewR.startAnimation(alphaAnimation);
    }

    public void stopWarningAnim() {
        this.mNgpViewL.setVisibility(8);
        this.mNgpViewL.clearAnimation();
        this.mNgpViewR.setVisibility(8);
        this.mNgpViewR.clearAnimation();
    }
}
