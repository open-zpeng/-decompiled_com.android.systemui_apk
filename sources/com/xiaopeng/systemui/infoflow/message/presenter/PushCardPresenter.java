package com.xiaopeng.systemui.infoflow.message.presenter;

import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.event.MessageEvent;
import com.xiaopeng.speech.protocol.node.dialog.DialogListener;
import com.xiaopeng.speech.protocol.node.dialog.DialogNode;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogExitReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
import com.xiaopeng.speech.protocol.node.message.AbsMessageListener;
import com.xiaopeng.speech.protocol.node.message.MessageNode;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.infoflow.IPushCardView;
import com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper;
import com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.utils.BIHelper;
/* loaded from: classes24.dex */
public class PushCardPresenter extends BaseCardPresenter implements DialogListener, IPushCardPresenter {
    private static final int NO_TIPS_7_DAYS = 2;
    private static final int NO_TIPS_ALL = 1;
    private static final String TAG = "PushCardPresenter";
    private AbsMessageListener mMessageListener;
    private PushBean mPushBean;
    private IPushCardView mPushCardView;
    private String mTtsId;
    private boolean mIsSpeeching = false;
    private Handler mHandler = new Handler();
    private Runnable mTtsWakeupAction = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.1
        @Override // java.lang.Runnable
        public void run() {
            if (PushCardPresenter.this.mPushBean != null && !TextUtils.isEmpty(PushCardPresenter.this.mPushBean.tts) && PushCardPresenter.this.mPushBean.isTtsEnable) {
                PushCardPresenter pushCardPresenter = PushCardPresenter.this;
                pushCardPresenter.mTtsId = pushCardPresenter.speak(pushCardPresenter.mPushBean.tts);
                PushCardPresenter pushCardPresenter2 = PushCardPresenter.this;
                pushCardPresenter2.enterWakeup(pushCardPresenter2.mPushBean.wakeupContent);
                PushCardPresenter.this.setCardFocused(true);
                PushCardPresenter.this.mPushBean.isTtsEnable = false;
                PushCardPresenter pushCardPresenter3 = PushCardPresenter.this;
                pushCardPresenter3.savePushBean(pushCardPresenter3.mPushBean);
            }
        }
    };
    private Runnable mHideNotTipAction = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.15
        @Override // java.lang.Runnable
        public void run() {
            PushCardPresenter.this.hideNotTip();
        }
    };
    private SpeechModel mSpeechModel = new SpeechModel();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingleHolder {
        private static final PushCardPresenter sInstance = new PushCardPresenter();

        private SingleHolder() {
        }
    }

    public static PushCardPresenter getInstance() {
        return SingleHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PushCardPresenter() {
        this.mSpeechModel.subscribe(DialogNode.class, this);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected int getCardType() {
        return 17;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter
    public void onNotTipDownClicked() {
        this.mHandler.removeCallbacks(this.mHideNotTipAction);
        this.mHandler.postDelayed(this.mHideNotTipAction, 4000L);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter
    public void onNotTipCloseClicked() {
        cancel(false);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter
    public void onNotTip7DayClicked() {
        notTip(2);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter
    public void onNotTipAllDayClicked() {
        notTip(1);
    }

    @Override // com.xiaopeng.systemui.infoflow.message.presenter.IPushCardPresenter
    public void onNotTipClicked() {
        hideNotTip();
    }

    protected boolean supportTts() {
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            return false;
        }
        return true;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewAttachedToWindow() {
        super.onViewAttachedToWindow();
        BIHelper.sendBIData(BIHelper.ID.ai_push_card, BIHelper.Type.infoflow, BIHelper.Action.open, BIHelper.Screen.main);
        if (supportTts()) {
            Runnable runnable = this.mTtsWakeupAction;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
            }
            this.mHandler.postDelayed(this.mTtsWakeupAction, 500L);
        }
    }

    protected void updateView() {
        PushBean pushBean = getPushBean(this.mCardData.content);
        if (pushBean != null) {
            this.mPushBean = pushBean;
            IPushCardView iPushCardView = this.mPushCardView;
            if (iPushCardView != null) {
                iPushCardView.setPushCardContent(pushBean);
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void bindDataImpl(CardEntry cardEntry) {
        updateView();
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithInfoflowView() {
        this.mPushCardView = this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter
    protected void fillViewWithCardHolder() {
        this.mPushCardView = (IPushCardView) this.mCardHolder;
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow();
        Logger.d(TAG, "PageBean : " + this.mPushBean);
        if (this.mPushBean != null) {
            BIHelper.sendBIData(BIHelper.ID.ai_push_card, BIHelper.Type.infoflow, BIHelper.Action.close, BIHelper.Screen.main);
        }
        if (supportTts()) {
            Runnable runnable = this.mTtsWakeupAction;
            if (runnable != null) {
                this.mHandler.removeCallbacks(runnable);
            }
            String str = this.mTtsId;
            if (str != null) {
                shutup(str);
            }
            exitWakeup("onViewDetachedFromWindow");
            setCardFocused(false);
        }
        hideNotTip();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCardFocused(boolean focused) {
        IPushCardView iPushCardView = this.mPushCardView;
        if (iPushCardView != null) {
            iPushCardView.setCardFocused(17, focused);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.adapter.holder.BaseCardPresenter, com.xiaopeng.systemui.infoflow.message.presenter.IInfoflowCardPresenter
    public void onCardClicked() {
        onBtnClick(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void savePushBean(PushBean pushBean) {
        if (pushBean != null && this.mCardData != null) {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.2
                @Override // java.lang.Runnable
                public void run() {
                    String json = GsonUtil.toJson(PushCardPresenter.this.mPushBean);
                    PushCardPresenter.this.mCardData.content = json;
                }
            });
        }
    }

    private PushBean getPushBean(String content) {
        if (!TextUtils.isEmpty(content)) {
            PushBean pushBean = (PushBean) GsonUtil.fromJson(content, (Class<Object>) PushBean.class);
            return pushBean;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String speak(String tts) {
        Log.i(TAG, "shutup tts : " + tts);
        if (!TextUtils.isEmpty(tts)) {
            return TextToSpeechHelper.instance().speak(tts, new TextToSpeechHelper.ISpeakCallback() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.3
                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onStart(String utteranceId) {
                    PushCardPresenter.this.onTtsStart();
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onEnd(String utteranceId) {
                    PushCardPresenter.this.onTtsEnd();
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onStop(String name) {
                    PushCardPresenter.this.onError();
                }

                @Override // com.xiaopeng.systemui.infoflow.aissistant.push.TextToSpeechHelper.ISpeakCallback
                public void onError(String utteranceId) {
                }
            });
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTtsStart() {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.4
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onTtsStart").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "onTtsStart");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onError() {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.5
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onTtsError").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "onError");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTtsEnd() {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.6
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onTtsEnd").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "onTtsEnd");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shutup(String ttsId) {
        Log.i(TAG, "shutup ttsId : " + ttsId);
        if (!TextUtils.isEmpty(ttsId)) {
            TextToSpeechHelper.instance().stop();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBtnClick(boolean bySpeech) {
        PushBean pushBean = this.mPushBean;
        if (pushBean != null && pushBean.action != null) {
            doAction(bySpeech);
        } else {
            showMessageBox();
        }
    }

    private void showMessageBox() {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.7
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("showMessageBox").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "showMessageBox");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterWakeup(final String wakeupContent) {
        Log.i(TAG, "enterWakeup:" + wakeupContent);
        if (this.mMessageListener != null) {
            exitWakeup("enterWakeup");
        }
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.8
            @Override // java.lang.Runnable
            public void run() {
                if (!TextUtils.isEmpty(wakeupContent)) {
                    SpeechClient.instance().getAgent().sendEvent(MessageEvent.COMMON_MESSAGE_AI_TO_SPEECH, wakeupContent);
                    PushCardPresenter.this.mMessageListener = new AbsMessageListener() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.8.1
                        @Override // com.xiaopeng.speech.protocol.node.message.AbsMessageListener, com.xiaopeng.speech.protocol.node.message.MessageListener
                        public void onCommonSubmit() {
                            Log.i(PushCardPresenter.TAG, "onCommonSubmit");
                            PushCardPresenter.this.onBtnClick(true);
                        }

                        @Override // com.xiaopeng.speech.protocol.node.message.AbsMessageListener, com.xiaopeng.speech.protocol.node.message.MessageListener
                        public void onCommonCancel() {
                            Log.i(PushCardPresenter.TAG, "onCommonCancel");
                            PushCardPresenter.this.cancel(true);
                        }

                        @Override // com.xiaopeng.speech.protocol.node.message.AbsMessageListener, com.xiaopeng.speech.protocol.node.message.MessageListener
                        public void onCommonAIMessage(String data) {
                            Log.i(PushCardPresenter.TAG, "onCommonAIMessage data : " + data);
                            super.onCommonAIMessage(data);
                            if (TextUtils.isEmpty(data)) {
                                PushCardPresenter.this.setCardFocused(false);
                            }
                        }

                        @Override // com.xiaopeng.speech.protocol.node.message.AbsMessageListener, com.xiaopeng.speech.protocol.node.message.MessageListener
                        public void onAIMessageDisable() {
                            Log.i(PushCardPresenter.TAG, "onAIMessageDisable");
                            PushCardPresenter.this.notTip(1);
                        }

                        @Override // com.xiaopeng.speech.protocol.node.message.AbsMessageListener, com.xiaopeng.speech.protocol.node.message.MessageListener
                        public void onAIMessageDisableSevenDays() {
                            Log.i(PushCardPresenter.TAG, "onAIMessageDisableSevenDays");
                            PushCardPresenter.this.notTip(2);
                        }
                    };
                    PushCardPresenter.this.mSpeechModel.subscribe(MessageNode.class, PushCardPresenter.this.mMessageListener);
                    Log.i(PushCardPresenter.TAG, "enterWakeup end");
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancel(final boolean bySpeech) {
        Log.i(TAG, "cancel");
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.9
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onCardCancel").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status)).appendQueryParameter("sencesId", String.valueOf(PushCardPresenter.this.mPushBean.sensesId)).appendQueryParameter("bySpeech", String.valueOf(bySpeech));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "onCardCancel");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitWakeup(final String tag) {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.10
            @Override // java.lang.Runnable
            public void run() {
                boolean isListening = PushCardPresenter.this.mMessageListener != null;
                Log.i(PushCardPresenter.TAG, "exitWakeup:" + tag + ":isListening=" + isListening);
                if (isListening) {
                    SpeechClient.instance().getAgent().sendEvent(MessageEvent.COMMON_MESSAGE_AI_TO_SPEECH, null);
                    if (!PushCardPresenter.this.mIsSpeeching) {
                        SpeechClient.instance().getWakeupEngine().stopDialog();
                    }
                    PushCardPresenter.this.mSpeechModel.unsubscribe(MessageNode.class, PushCardPresenter.this.mMessageListener);
                    PushCardPresenter.this.mMessageListener = null;
                    Uri.Builder builder = new Uri.Builder();
                    builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onExitWakeup").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status));
                    try {
                        ApiRouter.route(builder.build());
                        Log.i(PushCardPresenter.TAG, "exitWakeup end");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notTip(final int notTipStatus) {
        hideNotTip();
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.11
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onNotTipAction").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status)).appendQueryParameter("messageSensesId", String.valueOf(PushCardPresenter.this.mPushBean.sensesId)).appendQueryParameter("notTipStatus", String.valueOf(notTipStatus));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "notTip");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    private void doAction(final boolean bySpeech) {
        ThreadUtils.postDelayed(0, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.12
            @Override // java.lang.Runnable
            public void run() {
                Uri.Builder builder = new Uri.Builder();
                builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onCardAction").appendQueryParameter("notifyId", String.valueOf(PushCardPresenter.this.mCardData.status)).appendQueryParameter("sencesId", String.valueOf(PushCardPresenter.this.mPushBean.sensesId)).appendQueryParameter("actionPkg", PushCardPresenter.this.mPushBean.action.actionPkg).appendQueryParameter("actionContent", PushCardPresenter.this.mPushBean.action.actionContent).appendQueryParameter("bySpeech", String.valueOf(bySpeech));
                try {
                    ApiRouter.route(builder.build());
                    Log.i(PushCardPresenter.TAG, "doAction");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L);
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogStart(WakeupReason wakeupReason) {
        Log.i(TAG, "onDialogStart");
        ThreadUtils.postDelayed(1, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.13
            @Override // java.lang.Runnable
            public void run() {
                PushCardPresenter.this.exitWakeup("onDialogStart");
                PushCardPresenter pushCardPresenter = PushCardPresenter.this;
                pushCardPresenter.shutup(pushCardPresenter.mTtsId);
                PushCardPresenter.this.setCardFocused(false);
                PushCardPresenter.this.mIsSpeeching = true;
            }
        }, 0L);
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogError() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogEnd(DialogEndReason endReason) {
        Log.i(TAG, "onDialogEnd");
        ThreadUtils.postDelayed(1, new Runnable() { // from class: com.xiaopeng.systemui.infoflow.message.presenter.PushCardPresenter.14
            @Override // java.lang.Runnable
            public void run() {
                PushCardPresenter.this.mIsSpeeching = false;
            }
        }, 0L);
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogExit(DialogExitReason dialogExitReason) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogWait(DMWait dmWait) {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onDialogContinue() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onWakeupResult() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onVadBegin() {
    }

    @Override // com.xiaopeng.speech.protocol.node.dialog.DialogListener
    public void onVadEnd() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideNotTip() {
        hideCardNotTip();
        this.mHandler.removeCallbacks(this.mHideNotTipAction);
    }

    protected void hideCardNotTip() {
        IPushCardView iPushCardView = this.mPushCardView;
        if (iPushCardView != null) {
            iPushCardView.hidePushCardNotTip();
        }
    }
}
