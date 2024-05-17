package com.xiaopeng.systemui.infoflow.speech;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.helper.AnimationHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.VuiRecommendView;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class Asr2DView implements IAsrView {
    private static final int ASR_LISTENING_ANIM_DELAY_TIME = 1000;
    private static final int ASR_LISTENING_ANIM_SHOW_TIME = 500;
    private static final int ASR_LISTENING_STATUS_SHOW_TIME = 3000;
    private static final int ASR_MAX_WORD_NUM_OF_ONE_LINE = 10;
    private static final int ASR_MAX_WORD_NUM_OF_ONE_LINE_IN_VUI = 8;
    private static final int ASR_TEXT_SIZE_LARGE = 30;
    private static final int ASR_TEXT_SIZE_SMALL = 24;
    private static final String TAG = "Asr2DView";
    protected ViewGroup mAsrContainer;
    private int mAsrLoc;
    private AnimatedImageView mIvAsr;
    private String mRecommendData;
    protected TextView mSpeechShowTextView;
    private VuiRecommendView mVuiRecommendView;
    private boolean mRecommendDataInited = false;
    private Context mContext = ContextUtils.getContext();

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void updateAsrContainer(ViewGroup asrContainer) {
        if (asrContainer == null) {
            this.mAsrContainer = null;
        } else if (asrContainer == this.mAsrContainer) {
        } else {
            if (CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
                ViewGroup viewGroup = this.mAsrContainer;
                if (viewGroup != null) {
                    viewGroup.setVisibility(8);
                }
                this.mAsrContainer = asrContainer;
                ViewGroup viewGroup2 = this.mAsrContainer;
                if (viewGroup2 != null) {
                    viewGroup2.setVisibility(0);
                }
            } else {
                this.mAsrContainer = asrContainer;
            }
            this.mSpeechShowTextView = (TextView) asrContainer.findViewById(R.id.tv_speech_show);
            this.mIvAsr = (AnimatedImageView) asrContainer.findViewById(R.id.iv_asr);
            this.mVuiRecommendView = (VuiRecommendView) asrContainer.findViewById(R.id.recommend_view);
            if (this.mVuiRecommendView == null) {
                this.mRecommendDataInited = false;
            }
            if (!CarModelsManager.getFeature().isOldAsr()) {
                updateTextViewStyle();
            }
            this.mAsrContainer.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.Asr2DView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
            });
        }
    }

    private void updateTextViewStyle() {
        TextView textView = this.mSpeechShowTextView;
        if (textView != null) {
            textView.post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.Asr2DView.2
                @Override // java.lang.Runnable
                public void run() {
                    Logger.d(Asr2DView.TAG, "and-updateTextViewStyle " + Asr2DView.this.mAsrLoc);
                    int i = Asr2DView.this.mAsrLoc;
                    if (i == 1) {
                        Asr2DView.this.mSpeechShowTextView.setBackgroundResource(R.drawable.bg_asr_top_left);
                    } else if (i == 2) {
                        Asr2DView.this.mSpeechShowTextView.setBackgroundResource(R.drawable.bg_asr_top_right);
                    } else if (i == 3) {
                        Asr2DView.this.mSpeechShowTextView.setBackgroundResource(R.drawable.bg_asr_bottom_left);
                    } else if (i == 4) {
                        Asr2DView.this.mSpeechShowTextView.setBackgroundResource(R.drawable.bg_asr_bottom_right);
                    } else if (i == 999) {
                        Asr2DView.this.mSpeechShowTextView.setBackgroundResource(R.drawable.bg_asr_center);
                        Asr2DView.this.mSpeechShowTextView.setGravity(17);
                    } else {
                        Asr2DView.this.mSpeechShowTextView.setGravity(16);
                    }
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showAsr(boolean show) {
        Logger.d(TAG, "and-showAsr " + show);
        this.mSpeechShowTextView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setRecommendData(String recommendData) {
        this.mRecommendData = recommendData;
        if (this.mVuiRecommendView != null) {
            RecommendBean recommendBean = (RecommendBean) GsonUtil.fromJson(recommendData, (Class<Object>) RecommendBean.class);
            this.mRecommendDataInited = true;
            this.mVuiRecommendView.setDataList(recommendBean);
            this.mVuiRecommendView.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setAsrStatus(int status) {
        if (!CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
            if (status == 1) {
                this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_asr_tts));
            } else if (status == 2) {
                this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_asr_status));
            } else if (status == 3) {
                this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_asr_invalid_speech));
            } else {
                this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_asr_normal));
            }
        } else if (status == 3) {
            this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_multiperson_asr_invalid));
        } else {
            this.mSpeechShowTextView.setTextColor(this.mContext.getColor(R.color.infoflow_multiperson_asr_normal));
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showAsrAnimation(int status) {
        if (status != 0) {
            if (status == 1) {
                AnimationHelper.startAnimationDrawableAnim(this.mContext, this.mIvAsr, R.drawable.anim_infoflow_asr_nlu_loading);
                return;
            }
            switch (status) {
                case 8:
                case 9:
                    AnimationHelper.startAnimationDrawableAnim(this.mContext, this.mIvAsr, R.drawable.anim_infoflow_asr_speaking);
                    return;
                case 10:
                    break;
                default:
                    return;
            }
        }
        AnimationHelper.startAnimationDrawableAnim(this.mContext, this.mIvAsr, R.drawable.anim_infoflow_asr_not_speaking);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void showVuiRecommendView(boolean hasAnimation) {
        if (this.mVuiRecommendView != null) {
            if (!this.mRecommendDataInited) {
                setRecommendData(this.mRecommendData);
            }
            this.mVuiRecommendView.setVisibility(0);
            if (hasAnimation) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500L);
                this.mVuiRecommendView.startAnimation(alphaAnimation);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void hideVuiRecommendView() {
        VuiRecommendView vuiRecommendView = this.mVuiRecommendView;
        if (vuiRecommendView != null) {
            vuiRecommendView.clearData();
            this.mVuiRecommendView.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setAsrText(String text) {
        Logger.d(TAG, "and-setAsrText " + text + " ,mAsrLoc : " + this.mAsrLoc);
        if (TextUtils.isEmpty(text)) {
            this.mSpeechShowTextView.setText((CharSequence) null);
            this.mSpeechShowTextView.setVisibility(4);
            if (CarModelsManager.getFeature().isMultiplayerVoiceSupport()) {
                this.mSpeechShowTextView.setVisibility(4);
                this.mAsrContainer.post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.-$$Lambda$Asr2DView$a_FDXhXoMB9vWPLVflf3oHuGXHM
                    @Override // java.lang.Runnable
                    public final void run() {
                        Asr2DView.this.lambda$setAsrText$0$Asr2DView();
                    }
                });
                return;
            }
            if (text == null) {
                this.mAsrContainer.setVisibility(8);
            } else {
                this.mAsrContainer.setVisibility(0);
            }
            this.mSpeechShowTextView.setVisibility(8);
            return;
        }
        this.mAsrContainer.setVisibility(0);
        this.mSpeechShowTextView.setVisibility(0);
        this.mSpeechShowTextView.setText(text);
    }

    public /* synthetic */ void lambda$setAsrText$0$Asr2DView() {
        ViewGroup viewGroup = this.mAsrContainer;
        if (viewGroup != null) {
            viewGroup.setVisibility(8);
        }
    }

    private void dynamicTextSize(String text) {
        int textLen = getTextLength(text);
        if (needWordWrap(textLen)) {
            if (Utils.isChineseLanguage()) {
                this.mSpeechShowTextView.setTextSize(1, 24.0f);
            }
        } else if (Utils.isChineseLanguage()) {
            this.mSpeechShowTextView.setTextSize(1, 30.0f);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void fadeOut() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500L);
        this.mSpeechShowTextView.startAnimation(alphaAnimation);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void clearAnimation() {
        this.mSpeechShowTextView.clearAnimation();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void fadeIn(String text) {
        Logger.d(TAG, "and-fadeIn " + text);
        this.mSpeechShowTextView.setVisibility(0);
        this.mSpeechShowTextView.setText(text);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500L);
        this.mSpeechShowTextView.startAnimation(alphaAnimation);
    }

    private int getTextLength(String text) {
        if (text == null) {
            return 0;
        }
        TextPaint textPaint = this.mSpeechShowTextView.getPaint();
        return (int) (textPaint.measureText(text) / textPaint.measureText("字"));
    }

    private boolean needWordWrap(int textLen) {
        return (SpeechPresenter.mIsPanelVisible && textLen > 8) || (!SpeechPresenter.mIsPanelVisible && textLen > 10);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.IAsrView
    public void setAsrLoc(int asrLoc) {
        this.mAsrLoc = asrLoc;
    }
}
