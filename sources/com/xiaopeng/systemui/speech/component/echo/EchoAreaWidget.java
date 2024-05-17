package com.xiaopeng.systemui.speech.component.echo;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.speech.component.AreaWidget;
import com.xiaopeng.systemui.speech.component.echo.EchoModel;
import com.xiaopeng.systemui.speech.data.SpeechDataEcho;
import com.xiaopeng.systemui.speech.model.AnimationListenerExUtils;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes24.dex */
public class EchoAreaWidget extends AreaWidget<IEchoListener> implements EchoModel.EchoAreaModelCallBack {
    private static final String TAG = "Sp-EchoArea";
    private int mBgRes;
    private ImageView mEchoImageViewNewBg;
    private ImageView mEchoImageViewNewIc;
    private ImageView mEchoImageViewOldBg;
    private ImageView mEchoImageViewOldIc;
    private TextView mEchoTextView;
    private int mIcRes;
    private final Animation mInBgAnimation;
    private final AnimationSet mInIcAnimation;
    private final Animation mInTxtAnimation;
    private final Animation mOutTxtAnimation;

    public EchoAreaWidget(Context context, int area) {
        super(context, area);
        EchoModel.get().addCallBack(area, this);
        this.mInBgAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mInBgAnimation.setDuration(300L);
        this.mInIcAnimation = new AnimationSet(false);
        this.mInIcAnimation.setStartOffset(300L);
        ScaleAnimation inIcAnimation1 = new ScaleAnimation(0.0f, 1.2f, 0.0f, 1.2f, 25.0f, 25.0f);
        inIcAnimation1.setDuration(150L);
        ScaleAnimation inIcAnimation2 = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 25.0f, 25.0f);
        inIcAnimation2.setStartOffset(150L);
        inIcAnimation2.setDuration(100L);
        this.mInIcAnimation.addAnimation(inIcAnimation1);
        this.mInIcAnimation.addAnimation(inIcAnimation2);
        this.mOutTxtAnimation = new AlphaAnimation(1.0f, 0.0f);
        this.mOutTxtAnimation.setDuration(300L);
        this.mInTxtAnimation = new AlphaAnimation(0.0f, 1.0f);
        this.mInTxtAnimation.setDuration(300L);
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected View initView(View view) {
        View mEchoView = view.findViewById(R.id.speech_echo_view);
        this.mEchoTextView = (TextView) view.findViewById(R.id.speech_tv_echo);
        this.mEchoImageViewOldBg = (ImageView) view.findViewById(R.id.speech_echo_img_old_bg);
        this.mEchoImageViewOldIc = (ImageView) view.findViewById(R.id.speech_echo_img_old_ic);
        this.mEchoImageViewNewBg = (ImageView) view.findViewById(R.id.speech_echo_img_new_bg);
        this.mEchoImageViewNewIc = (ImageView) view.findViewById(R.id.speech_echo_img_new_ic);
        return mEchoView;
    }

    @Override // com.xiaopeng.systemui.speech.component.AreaWidget
    protected String logTag() {
        return TAG;
    }

    @Override // com.xiaopeng.systemui.speech.component.echo.EchoModel.EchoAreaModelCallBack
    public void onEchoHide(int area) {
        Logger.i(TAG, "onAsrHide area=" + area);
        hide(true);
    }

    @Override // com.xiaopeng.systemui.speech.component.echo.EchoModel.EchoAreaModelCallBack
    public void onEchoShow(final SpeechDataEcho data) {
        boolean isFirst = isFirst();
        boolean isShowing = isShowing();
        Logger.i(TAG, "onAsrShow " + data + ", isShowing " + isShowing);
        if (!createAndShow(data, true)) {
            Logger.w(TAG, "createAndShow fail !!!");
            return;
        }
        int bgRes = 0;
        int icRes = 0;
        int i = data.type;
        if (i == 1) {
            bgRes = R.drawable.echo_bg_ok;
            icRes = R.drawable.echo_ic_ok;
        } else if (i == 2) {
            bgRes = R.drawable.echo_bg_error;
            icRes = R.drawable.echo_ic_error;
        }
        if (!isShowing || isFirst) {
            this.mEchoImageViewNewBg.setImageResource(bgRes);
            this.mEchoImageViewNewIc.setImageResource(icRes);
            this.mEchoImageViewNewIc.startAnimation(this.mInIcAnimation);
            this.mEchoTextView.setText(data.getText());
        } else {
            this.mEchoImageViewOldBg.setImageResource(this.mBgRes);
            this.mEchoImageViewOldIc.setImageResource(this.mIcRes);
            this.mEchoImageViewNewBg.setImageResource(bgRes);
            this.mEchoImageViewNewIc.setImageResource(icRes);
            this.mEchoImageViewNewBg.startAnimation(this.mInBgAnimation);
            this.mEchoImageViewNewIc.startAnimation(this.mInIcAnimation);
            AnimationListenerExUtils.start(this.mEchoTextView, this.mOutTxtAnimation, new Runnable() { // from class: com.xiaopeng.systemui.speech.component.echo.-$$Lambda$EchoAreaWidget$gMEYADxB0qkd1-d5O5Fw8UUoVXY
                @Override // java.lang.Runnable
                public final void run() {
                    EchoAreaWidget.this.lambda$onEchoShow$0$EchoAreaWidget(data);
                }
            });
        }
        this.mBgRes = bgRes;
        this.mIcRes = icRes;
    }

    public /* synthetic */ void lambda$onEchoShow$0$EchoAreaWidget(SpeechDataEcho data) {
        this.mEchoTextView.setText(data.getText());
        this.mEchoTextView.startAnimation(this.mInTxtAnimation);
    }
}
