package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.support.rastermill.FrameSequenceUtil;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel;
import com.xiaopeng.systemui.ui.widget.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.systemui.ui.widget.AnimatedTextView;
/* loaded from: classes24.dex */
public class BugReportView extends AlphaOptimizedRelativeLayout {
    private static final int MAX_RECORD_TIME = 25;
    private Button mBtnFinish;
    private Handler mHandler;
    private AnimatedImageView mIvRedPoint;
    Runnable mRecordRunnable;
    private long mRecordStartTime;
    private AnimatedTextView mTvTime;
    private AnimatedImageView mVoiceWaveView;

    public BugReportView(Context context) {
        super(context);
        this.mHandler = new Handler();
        this.mRecordRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.BugReportView.2
            @Override // java.lang.Runnable
            public void run() {
                long timeLong = (System.currentTimeMillis() - BugReportView.this.mRecordStartTime) / 1000;
                if (timeLong <= 25) {
                    String time = "" + timeLong;
                    BugReportView.this.updateRecordTime(time);
                    BugReportView.this.mHandler.postDelayed(BugReportView.this.mRecordRunnable, 500L);
                }
            }
        };
    }

    public BugReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandler = new Handler();
        this.mRecordRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.BugReportView.2
            @Override // java.lang.Runnable
            public void run() {
                long timeLong = (System.currentTimeMillis() - BugReportView.this.mRecordStartTime) / 1000;
                if (timeLong <= 25) {
                    String time = "" + timeLong;
                    BugReportView.this.updateRecordTime(time);
                    BugReportView.this.mHandler.postDelayed(BugReportView.this.mRecordRunnable, 500L);
                }
            }
        };
    }

    public BugReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHandler = new Handler();
        this.mRecordRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.BugReportView.2
            @Override // java.lang.Runnable
            public void run() {
                long timeLong = (System.currentTimeMillis() - BugReportView.this.mRecordStartTime) / 1000;
                if (timeLong <= 25) {
                    String time = "" + timeLong;
                    BugReportView.this.updateRecordTime(time);
                    BugReportView.this.mHandler.postDelayed(BugReportView.this.mRecordRunnable, 500L);
                }
            }
        };
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mVoiceWaveView = (AnimatedImageView) findViewById(R.id.voice_wave);
        FrameSequenceUtil.with(this.mVoiceWaveView).resourceId(R.drawable.voice).loopBehavior(2).applyAsync();
        this.mBtnFinish = (Button) findViewById(R.id.btn_finish);
        this.mBtnFinish.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.BugReportView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BugReportView.this.mBtnFinish.setEnabled(false);
                SpeechClient.instance().getAgent().sendThirdCMD(SpeechConstant.ThirdCMD.CMD_FINISH_BUG_REPORT);
                BugReportView.this.destroy();
            }
        });
        this.mBtnFinish.setEnabled(false);
        this.mTvTime = (AnimatedTextView) findViewById(R.id.tv_time);
        this.mIvRedPoint = (AnimatedImageView) findViewById(R.id.iv_red_point);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroy() {
        this.mHandler.removeCallbacks(this.mRecordRunnable);
        SpeechManager.instance().getSpeechAvatarManager().onDialogEnd(null);
        SpeechManager.instance().getSpeechContextManager().onWidgetCancel("", ContextModel.CTRL_CARD_CANCEL_WAY_FORCE);
    }

    public void updateRecordTime(String time) {
        if (!this.mTvTime.getText().equals(time)) {
            this.mTvTime.setText(time);
        }
        AnimatedImageView animatedImageView = this.mIvRedPoint;
        animatedImageView.setVisibility(animatedImageView.getVisibility() == 0 ? 4 : 0);
    }

    public void onBugReportBegin() {
        this.mRecordStartTime = System.currentTimeMillis();
        this.mHandler.post(this.mRecordRunnable);
        this.mBtnFinish.setEnabled(true);
    }

    public void onBugReportEnd() {
        destroy();
    }
}
