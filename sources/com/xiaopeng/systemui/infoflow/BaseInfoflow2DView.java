package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.tts.TtsEchoValue;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.bean.CallInfo;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.speech.SpeechRootView;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewContainer;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.statusbar.StatusBarGlobal;
import com.xiaopeng.xuimanager.contextinfo.HomeCompanyRouteInfo;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class BaseInfoflow2DView implements IInfoflowView {
    public static final int SCENE_NORMAL = 0;
    public static final int SCENE_SPEECH = 1;
    private static final String TAG = "BaseInfoflow2DView";
    protected RelativeLayout mAsrContainer;
    protected TextView mCallingTipView;
    protected RelativeLayout mRootView;
    protected SpeechRootView mSpeechRootView;
    protected RelativeLayout mSpeechViewParent;
    protected VoiceWaveViewContainer mVoiceWaveViewContainer;
    private ViewGroup mVuiAsrContainer;
    private RelativeLayout mVuiAsrView;
    protected int mSceneType = 0;
    private boolean mMultiRouteVoice = false;
    protected Context mContext = ContextUtils.getContext();
    protected WindowManager mWindowManager = StatusBarGlobal.getInstance(this.mContext).getWindowManager();
    protected VuiViewParent mVuiViewParent = new VuiViewParent(this.mContext, this.mWindowManager);
    protected AbstractInfoFlow mPresenter = PresenterCenter.getInstance().getInfoFlow();

    public void initSpeechViewParent() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onNavigationItemChanged(String packageName, String className, boolean isCarControlReady) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void startNgpWarningAnim() {
        this.mVuiViewParent.startNgpWarningAnim();
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void stopNgpWarningAnim() {
        this.mVuiViewParent.stopNgpWarningAnim();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogEnd(DialogEndReason endReason) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListFocus(int index) {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListFocus(index);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListSelect(int index) {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListSelect(index);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onBugReportBegin() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onBugReportBegin();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onBugReportEnd() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onBugReportEnd();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showVoiceWaveAnim(int regionType, int voiceWaveType, int volume) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListCancelFocus(int index) {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListCancelFocus(index);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListExpand() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListExpend();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListFold() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListFold();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onTtsEcho(TtsEchoValue data) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showHint(String guideText, String hintText) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void hideHint() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showAsrBackground(boolean visible) {
        VuiViewParent vuiViewParent = this.mVuiViewParent;
        if (vuiViewParent != null) {
            vuiViewParent.showAsrBackground(visible);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onSoundAreaStatus(SoundAreaStatus status) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onPanelVisibilityChanged(boolean isPanelVisible) {
        if (!this.mMultiRouteVoice) {
            if (isPanelVisible) {
                if (this.mVuiAsrView == null) {
                    this.mVuiAsrView = WindowHelper.addVuiAsrView(ContextUtils.getContext(), StatusBarGlobal.getInstance(ContextUtils.getContext()).getWindowManager());
                    setVuiAsrContainer(this.mVuiAsrView);
                }
            } else if (this.mVuiAsrView != null) {
                StatusBarGlobal.getInstance(ContextUtils.getContext()).getWindowManager().removeViewImmediate(this.mVuiAsrView);
                this.mVuiAsrView = null;
            }
            AsrHelper.getInstance().updateAsrContainer(isPanelVisible ? this.mVuiAsrContainer : this.mAsrContainer);
        }
    }

    public void setVuiAsrContainer(ViewGroup vuiAsrContainer) {
        this.mVuiAsrContainer = vuiAsrContainer;
        ViewGroup viewGroup = this.mVuiAsrContainer;
        if (viewGroup != null) {
            viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.BaseInfoflow2DView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onWidgetListStopCountdown() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.onWidgetListStopCountdown();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showListWidget(SpeechWidget widget) {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.showListWidget(widget);
        }
    }

    private void showSpeechView() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.showSpeechView();
        }
    }

    public static void updateVoiceLoc(int sceneType, int voiceLoc, ViewGroup voiceLocContainer, AnimatedTextView voiceLocHintTitle, AnimatedTextView voiceLocHintDesc) {
        updateVoiceLocByScene(sceneType, voiceLoc, voiceLocContainer, voiceLocHintTitle, voiceLocHintDesc);
    }

    public static void updateSceneType(int sceneType, ViewGroup voiceLocContainer, AnimatedTextView voiceLocHintTitle, AnimatedTextView voiceLocHintDesc) {
        updateVoiceLocByScene(sceneType, SpeechPresenter.getVoiceLoc(), voiceLocContainer, voiceLocHintTitle, voiceLocHintDesc);
    }

    protected static void updateVoiceLocByScene(int sceneType, int voiceLoc, ViewGroup voiceLocContainer, AnimatedTextView voiceLocHintTitle, AnimatedTextView voiceLocHintDesc) {
        int resId;
        int titleStringId;
        int descStringId;
        Logger.i(TAG, "updateVoiceLoc : voiceLoc = " + voiceLoc + " sceneType = " + sceneType);
        if (sceneType == 0) {
            if (voiceLocContainer != null) {
                voiceLocContainer.setVisibility(8);
            }
        } else if (sceneType == 1) {
            if (CarModelsManager.getFeature().isOldAsr() && voiceLoc < 0) {
                return;
            }
            if (voiceLoc == 0) {
                resId = R.drawable.bg_recommend_card_voice_loc_doa;
                titleStringId = R.string.doa_voice;
                descStringId = R.string.doa_voice_desc;
            } else if (voiceLoc == 2) {
                resId = R.drawable.bg_recommend_card_voice_loc_passenger;
                titleStringId = R.string.passenger_voice_lock;
                descStringId = R.string.passenger_voice_lock_desc;
            } else {
                resId = R.drawable.bg_recommend_card_voice_loc_driver;
                titleStringId = R.string.driver_voice_lock;
                descStringId = R.string.driver_voice_lock_desc;
            }
            if (voiceLocContainer != null) {
                voiceLocContainer.setVisibility(0);
                voiceLocContainer.setBackgroundResource(resId);
            }
            if (voiceLocHintTitle != null) {
                voiceLocHintTitle.setText(titleStringId);
            }
            if (voiceLocHintDesc != null) {
                voiceLocHintDesc.setText(descStringId);
            }
        }
    }

    protected void startDialog(int type) {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.startDialog(type);
        }
    }

    protected void endDialog() {
        SpeechRootView speechRootView = this.mSpeechRootView;
        if (speechRootView != null) {
            speechRootView.endDialog();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void stopVoiceWaveAnim() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterSpeechMode(int type) {
        showSpeechView();
        startDialog(type);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitSpeechMode() {
        endDialog();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogStart() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMessageViewGroup(boolean show) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void showCarCheckView(CardEntry cardEntry) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void exitCarCheckMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarSkinUpdate(Drawable skin) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarStateChanged(int state) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onWheelKeyEvent(KeyEvent keyEvent) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setCardFocused(int cardType, boolean focused) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setCallCardContent(String content) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setPhoneCardStatus(int status) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setPhoneCardTime(String time) {
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardActionNum(int actionNum) {
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardBtnImages(List<String> images) {
    }

    @Override // com.xiaopeng.systemui.infoflow.ICruiseSceneCardView
    public void setCruiseSceneCardRouteInfo(HomeCompanyRouteInfo routeInfo) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardManeuverData(Maneuver maneuver) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardNaviData(Navi navi) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardLaneData(Lane lane) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardManeuverData(Maneuver maneuver) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardLaneData(Lane lane) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardNaviData(Navi navi) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfo) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardMediaInfo(int displayId, MediaInfo mediaInfo) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPlayStatus(int displayId, int playStatus) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardProgress(int displayId, int progress) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void showMusicCardProgress(int displayId, boolean show) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPosition(int displayId, String position, String duration) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
    }

    @Override // com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardSubDesc(String desc) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void hidePushCardNotTip() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IPushCardView
    public void setPushCardContent(PushBean pushBean) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void hideWeatherCardNotTip() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void setWeatherCardContent(WeatherBean weatherBean) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IRecommendCardView
    public void setRecommendCardContent(RecommendBean recommendBean) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setAsrLoc(int asrLoc) {
        this.mMultiRouteVoice = true;
        this.mVuiViewParent.setAsrLoc(asrLoc);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterEasterMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitEasterMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVisualizerWindow(boolean show) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showDateTimeView(boolean visible) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void collapseCardStack() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void expandCardStack() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setWakeupStatus(int status, String info) {
        TextView textView = this.mCallingTipView;
        if (textView != null) {
            textView.setText(info);
            this.mCallingTipView.setVisibility(status == 1 ? 0 : 8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMiniAsrContainer(boolean listeningStatus) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void addSpeechCardBackground() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showPanelAsr(boolean visible) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterNormalMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVoiceLoc(int voiceLoc) {
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setSceneType(int sceneType) {
    }

    protected RelativeLayout getRootView() {
        return (RelativeLayout) WindowHelper.getNavigationBarWindow().findViewById(R.id.view_infoflow);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowView
    public void initView() {
        this.mRootView = getRootView();
        this.mCallingTipView = (TextView) this.mRootView.findViewById(R.id.tv_calling_tip);
        initSpeechViewParent();
        RelativeLayout relativeLayout = this.mSpeechViewParent;
        if (relativeLayout != null) {
            this.mSpeechRootView = (SpeechRootView) relativeLayout.findViewById(R.id.view_speech_root);
            this.mAsrContainer = (RelativeLayout) this.mSpeechViewParent.findViewById(R.id.asr_container);
            this.mVoiceWaveViewContainer = (VoiceWaveViewContainer) this.mSpeechViewParent.findViewById(R.id.voice_wave_view_container);
            AsrHelper.getInstance().updateAsrContainer(this.mAsrContainer);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void refreshList(List<CardEntry> entries) {
    }

    @Override // com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void enterCarCheckMode() {
    }

    @Override // com.xiaopeng.systemui.infoflow.ICallCardView
    public void updateActionImg(CallInfo callInfo) {
    }
}
