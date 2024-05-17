package com.xiaopeng.systemui.infoflow.message.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.systemui.R;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.ICallCardView;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.CallInfo;
import com.xiaopeng.systemui.infoflow.message.define.CallAction;
import com.xiaopeng.systemui.infoflow.message.helper.CardHelper;
import com.xiaopeng.systemui.infoflow.message.util.CallTimer;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
/* loaded from: classes24.dex */
public class CallCardPresenter extends BaseCardPresenter implements ICallCardPresenter, IInfoflowCardPresenter {
    private static final String TAG = "CallCardPresenter";
    private ICallCardView mCallCardView;
    private final int MSG_UPDATE_TIME = 65;
    private Context mContext = ContextUtils.getContext();
    private Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 65) {
                String value = (String) msg.obj;
                CallCardPresenter.this.mInfoflowView.setCallCardContent(CallCardPresenter.this.mContext.getResources().getString(R.string.call_ongoing_content, value));
            }
        }
    };
    private CallTimer.OnElapsedTimerListener mOnElapsedTimerListener = new CallTimer.OnElapsedTimerListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter.2
        @Override // com.xiaopeng.systemui.infoflow.message.util.CallTimer.OnElapsedTimerListener
        public void onElapsedTimeString(final String time) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.CallCardPresenter.2.1
                @Override // java.lang.Runnable
                public void run() {
                    Logger.d(CallCardPresenter.TAG, "onElapsedTimeString--" + time);
                    CallCardPresenter.this.mHandler.obtainMessage(65, time).sendToTarget();
                }
            });
        }
    };
    private CallTimer mCallTimer = new CallTimer(this.mOnElapsedTimerListener);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final CallCardPresenter sInstance = new CallCardPresenter();

        private SingleHolder() {
        }
    }

    public static CallCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    protected CallCardPresenter() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 4;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public String getCardPackageName() {
        return "com.xiaopeng.btphone";
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mCallCardView = (ICallCardView) this.mCardHolder;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mCallCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        if (cardEntry != null && cardEntry.extraData != null) {
            CallInfo callInfo = (CallInfo) GsonUtil.fromJson(cardEntry.extraData, (Class<Object>) CallInfo.class);
            ICallCardView iCallCardView = this.mCallCardView;
            if (iCallCardView != null) {
                iCallCardView.updateActionImg(callInfo);
            }
            if (callInfo != null) {
                countCallTime(callInfo.elapsedTime, callInfo.callStatus);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.ICallCardPresenter
    public void onCallAcceptClicked() {
        controlCallWithAction(CallAction.ACCEPT_CALL_ACTION);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.ICallCardPresenter
    public void onCallHangupClicked() {
        controlCallWithAction(CallAction.HANGUP_CALL_ACTION);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.ICallCardPresenter
    public void onCallSwitchClicked() {
        controlCallWithAction(CallAction.SWITCH_VOICE_CALL_ACTION);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.ICallCardPresenter
    public void onCallActionClicked() {
        if (!TextUtils.isEmpty(this.mCardData.action)) {
            controlCallWithAction(this.mCardData.action);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void destroyCard() {
        stopTimer();
    }

    private void controlCallWithAction(String action) {
        Log.i(TAG, "controlCallWithAction -" + action);
        PackageHelper.sendBroadcast(this.mContext, action, null, null, null);
    }

    private void stopTimer() {
        this.mCallTimer.end();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void startTimer(long elapsedTime) {
        stopTimer();
        this.mCallTimer.startTimer(elapsedTime);
    }

    public void countCallTime(long elapsedTime, int callStatus) {
        if (callStatus == 3) {
            startTimer(elapsedTime);
        } else {
            stopTimer();
        }
    }

    private void saveIncomingCallStatus(CallInfo callInfo) {
        if (callInfo != null) {
            int callStatus = callInfo.callStatus;
            CardHelper.saveIncomingCallStatus(callStatus == 2);
            return;
        }
        CardHelper.saveIncomingCallStatus(false);
    }
}
