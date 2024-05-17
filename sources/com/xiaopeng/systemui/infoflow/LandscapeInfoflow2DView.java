package com.xiaopeng.systemui.infoflow;

import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.tts.TtsEchoValue;
import com.xiaopeng.systemui.IMusicPlayerView;
import com.xiaopeng.systemui.MusicPlayerViewManger;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.checking.CarCheckView;
import com.xiaopeng.systemui.infoflow.egg.EasterEggView;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.SpeechHintHelper;
import com.xiaopeng.systemui.infoflow.helper.TtsEchoHelper;
import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.helper.SoundHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.PushBean;
import com.xiaopeng.systemui.infoflow.message.presenter.WeatherBean;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
import com.xiaopeng.systemui.ui.widget.AnimatedImageView;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.mediacenter.MediaInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class LandscapeInfoflow2DView extends BaseInfoflow2DView implements View.OnClickListener {
    private static final String TAG = "LandscapeInfoflow2DView";
    private AvatarViewParent mAvatarViewParent;
    private CarCheckView mCarCheckView;
    private AnimatedImageView mCloseBtn;
    private View mDateTimeView;
    private EasterEggView mEasterEggView;
    private View mFocusView;
    private MessageViewParent mMessageViewParent;
    private WindowManager.LayoutParams mVisuaizerLp;
    private View visualizerRootView;

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView
    public void initSpeechViewParent() {
        this.mSpeechViewParent = this.mRootView;
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void initView() {
        ViewStub stubInfoflow = (ViewStub) WindowHelper.getNavigationBarWindow().findViewById(R.id.stub_infoflow);
        stubInfoflow.inflate();
        super.initView();
        createVisualizerWindowsAndView();
        this.mAvatarViewParent = (AvatarViewParent) this.mRootView.findViewById(R.id.view_avatar_parent);
        this.mMessageViewParent = (MessageViewParent) this.mAvatarViewParent.findViewById(R.id.view_immerse);
        this.mCarCheckView = (CarCheckView) this.mRootView.findViewById(R.id.view_car_check);
        this.mEasterEggView = (EasterEggView) this.mRootView.findViewById(R.id.easter_egg_view);
        this.mCloseBtn = (AnimatedImageView) this.mRootView.findViewById(R.id.btn_close);
        this.mDateTimeView = this.mRootView.findViewById(R.id.view_date_time);
        MusicPlayerViewManger.getInstance().addMusicPlayerView(0, this.mMessageViewParent);
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.setupWithViewContainer(this.mPresenter);
        }
        AnimatedImageView animatedImageView = this.mCloseBtn;
        if (animatedImageView != null) {
            animatedImageView.setOnClickListener(this);
        }
        EasterEggView easterEggView = this.mEasterEggView;
        if (easterEggView != null) {
            easterEggView.setOnCloseBtnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    LandscapeInfoflow2DView.this.exitEasterMode();
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterSpeechMode(int type) {
        super.enterSpeechMode(type);
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.enterSpeechMode(this.mSpeechRootView);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitSpeechMode() {
        super.exitSpeechMode();
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.exitSpeechMode(this.mSpeechRootView);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogStart() {
        Log.i(TAG, "Landscape onDialogStart " + this.mAvatarViewParent);
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.onDialogStart();
        } else {
            AIAvatarViewServiceHelper.instance().updateDialogStatus(true);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onDialogEnd(DialogEndReason endReason) {
        Log.i(TAG, "Landscape onDialogEnd " + this.mAvatarViewParent);
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.onDialogEnd(endReason);
        } else {
            AIAvatarViewServiceHelper.instance().updateDialogStatus(false);
        }
        removeVoiceWaveLayer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeVoiceWaveLayer() {
        if (this.mVuiViewParent != null) {
            this.mVuiViewParent.removeVoiceWaveLayer();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void enterCarCheckMode() {
        this.mMessageViewParent.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void showCarCheckView(CardEntry cardEntry) {
        CarCheckView carCheckView = this.mCarCheckView;
        if (carCheckView != null) {
            if (carCheckView.getVisibility() != 0) {
                this.mCarCheckView.setVisibility(0);
            }
            this.mCarCheckView.updateData(cardEntry);
        }
        this.mSpeechRootView.setVisibility(8);
        this.mMessageViewParent.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void exitCarCheckMode() {
        CarCheckView carCheckView = this.mCarCheckView;
        if (carCheckView != null) {
            carCheckView.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVisualizerWindow(boolean show) {
        View view;
        View view2;
        Log.i(TAG, "showVisualizerWindow");
        if (show && (view2 = this.visualizerRootView) != null && !view2.isAttachedToWindow()) {
            this.mWindowManager.addView(this.visualizerRootView, this.mVisuaizerLp);
        } else if (!show && (view = this.visualizerRootView) != null && view.isAttachedToWindow()) {
            this.mWindowManager.removeView(this.visualizerRootView);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showDateTimeView(boolean visible) {
        final int targetVisibility = visible ? 0 : 8;
        View view = this.mDateTimeView;
        if (view != null && view.getVisibility() != targetVisibility) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView.2
                @Override // java.lang.Runnable
                public void run() {
                    LandscapeInfoflow2DView.this.mDateTimeView.setVisibility(targetVisibility);
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMessageViewGroup(boolean show) {
        this.mAvatarViewParent.showMessageViewGroup(show);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarSkinUpdate(Drawable skin) {
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.onAvatarSkinUpdate(skin);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onAvatarStateChanged(int state) {
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.onAvatarStateChanged(state);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void collapseCardStack() {
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.startSmallTopMarginAnimation();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void expandCardStack() {
        AvatarViewParent avatarViewParent = this.mAvatarViewParent;
        if (avatarViewParent != null) {
            avatarViewParent.startHighTopMarginAnimation();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.message.contract.CardsContract.View
    public void refreshList(List<CardEntry> entries) {
        this.mAvatarViewParent.refreshList(entries);
    }

    private void createVisualizerWindowsAndView() {
        this.visualizerRootView = View.inflate(this.mContext, R.layout.view_root_visualizer_effect, null);
        this.mVisuaizerLp = new WindowManager.LayoutParams(-1, -1, WindowHelper.TYPE_SPECTRUM, 8388648, -3);
        WindowManager.LayoutParams layoutParams = this.mVisuaizerLp;
        layoutParams.gravity = 17;
        layoutParams.token = new Binder();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            SpeechPresenter.getInstance().exitSpeechMode(0);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onWheelKeyEvent(KeyEvent keyEvent) {
        if (this.mRootView.dispatchKeyEvent(keyEvent)) {
            if (keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == 1015) {
                    SoundHelper.play(SoundHelper.PATH_WHEEL_TIP_1);
                } else if (keyCode == 1083) {
                    SoundHelper.play(SoundHelper.PATH_WHEEL_SCROLL_LEFT);
                } else if (keyCode == 1084) {
                    SoundHelper.play(SoundHelper.PATH_WHEEL_SCROLL_RIGHT);
                }
            }
        } else if (keyEvent.getAction() != 0) {
        } else {
            handleKeyEvent(keyEvent);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void setCardFocused(int cardType, boolean focused) {
        this.mMessageViewParent.setInfoflowCardFocused(cardType, focused);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setCallCardContent(String content) {
        this.mMessageViewParent.updateCallCardContent(content);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardManeuverData(Maneuver maneuver) {
        this.mMessageViewParent.setExploreSceneCardManeuverData(maneuver);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardNaviData(Navi navi) {
        this.mMessageViewParent.setExploreSceneCardNaviData(navi);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IExplorerSceneCardView
    public void setExploreSceneCardLaneData(Lane lane) {
        this.mMessageViewParent.setExploreSceneCardLaneData(lane);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardLaneData(Lane lane) {
        this.mMessageViewParent.setNaviSceneCardLaneData(lane);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardManeuverData(Maneuver maneuver) {
        this.mMessageViewParent.setNaviSceneCardManeuverData(maneuver);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardNaviData(Navi navi) {
        this.mMessageViewParent.setNaviSceneCardNaviData(navi);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INaviSceneCardView
    public void setNaviSceneCardRemainInfoData(RemainInfo remainInfo) {
        this.mMessageViewParent.setNaviSceneCardRemainInfoData(remainInfo);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardMediaInfo(int displayId, MediaInfo mediaInfo) {
        Logger.d(TAG, "setMusicCardMediaInfo : displayId = " + displayId + ", mediaInfo = " + mediaInfo);
        IMusicPlayerView musicPlayerView = MusicPlayerViewManger.getInstance().getMusicPlayerView(displayId);
        if (musicPlayerView != null) {
            musicPlayerView.updateMusicCardMediaInfo(mediaInfo);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPlayStatus(int displayId, int playStatus) {
        IMusicPlayerView musicPlayerView = MusicPlayerViewManger.getInstance().getMusicPlayerView(displayId);
        if (musicPlayerView != null) {
            musicPlayerView.updateMusicCardPlayStatus(playStatus);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardProgress(int displayId, int progress) {
        IMusicPlayerView musicPlayerView = MusicPlayerViewManger.getInstance().getMusicPlayerView(displayId);
        if (musicPlayerView != null) {
            musicPlayerView.updateMusicCardProgress(progress);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void showMusicCardProgress(int displayId, boolean show) {
        IMusicPlayerView musicPlayerView = MusicPlayerViewManger.getInstance().getMusicPlayerView(displayId);
        if (musicPlayerView != null) {
            musicPlayerView.showMusicCardProgress(show);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IMusicCardView
    public void setMusicCardPosition(int displayId, String position, String duration) {
        IMusicPlayerView musicPlayerView = MusicPlayerViewManger.getInstance().getMusicPlayerView(displayId);
        if (musicPlayerView != null) {
            musicPlayerView.setMusicCardPosition(position, duration);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardStatus(boolean hasNotification, String currentTime) {
        this.mMessageViewParent.setNotificationCardStatus(hasNotification, currentTime);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.INotificationCardView
    public void setNotificationCardSubDesc(String desc) {
        this.mMessageViewParent.setNotificationCardSubDesc(desc);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void hidePushCardNotTip() {
        this.mMessageViewParent.hidePushCardNotTip();
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IPushCardView
    public void setPushCardContent(PushBean pushBean) {
        this.mMessageViewParent.updatePushCardView(pushBean);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void hideWeatherCardNotTip() {
        this.mMessageViewParent.hideWeatherCardNotTip();
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IWeatherCardView
    public void setWeatherCardContent(WeatherBean weatherBean) {
        this.mMessageViewParent.updateWeatherCardView(weatherBean);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IRecommendCardView
    public void setRecommendCardContent(RecommendBean recommendBean) {
        this.mMessageViewParent.updateRecommendCardList(recommendBean);
    }

    private void handleKeyEvent(final KeyEvent keyEvent) {
        Log.d(TAG, "handle focus by rootView : keyEvent = " + keyEvent);
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoflow2DView.3
            @Override // java.lang.Runnable
            public void run() {
                int direction;
                LandscapeInfoflow2DView landscapeInfoflow2DView = LandscapeInfoflow2DView.this;
                landscapeInfoflow2DView.mFocusView = landscapeInfoflow2DView.mRootView.findFocus();
                if (LandscapeInfoflow2DView.this.mFocusView != null) {
                    Log.d(LandscapeInfoflow2DView.TAG, "mFocusView is " + LandscapeInfoflow2DView.this.mFocusView);
                    if (LandscapeInfoflow2DView.this.mFocusView instanceof CardStack) {
                        ((CardStack) LandscapeInfoflow2DView.this.mFocusView).performFocusNavigation(keyEvent);
                        return;
                    }
                }
                int keyCode = keyEvent.getKeyCode();
                if (keyCode != 1015) {
                    if (keyCode == 1083) {
                        direction = 130;
                    } else if (keyCode == 1084) {
                        direction = 33;
                    } else {
                        return;
                    }
                    if (LandscapeInfoflow2DView.this.mFocusView == null) {
                        LandscapeInfoflow2DView landscapeInfoflow2DView2 = LandscapeInfoflow2DView.this;
                        landscapeInfoflow2DView2.mFocusView = landscapeInfoflow2DView2.mRootView.focusSearch(null, direction);
                    } else {
                        LandscapeInfoflow2DView landscapeInfoflow2DView3 = LandscapeInfoflow2DView.this;
                        landscapeInfoflow2DView3.mFocusView = landscapeInfoflow2DView3.mFocusView.focusSearch(direction);
                    }
                    if (LandscapeInfoflow2DView.this.mFocusView != null) {
                        Log.d(LandscapeInfoflow2DView.TAG, "focusSearch mFocusView is " + LandscapeInfoflow2DView.this.mFocusView);
                        if (LandscapeInfoflow2DView.this.mFocusView instanceof CardStack) {
                            ((CardStack) LandscapeInfoflow2DView.this.mFocusView).performFocusNavigation(keyEvent);
                        }
                        if (LandscapeInfoflow2DView.this.mFocusView.requestFocus(direction)) {
                            Log.d(LandscapeInfoflow2DView.TAG, "requestFocus = " + LandscapeInfoflow2DView.this.mFocusView);
                        }
                    }
                } else if (LandscapeInfoflow2DView.this.mFocusView != null) {
                    LandscapeInfoflow2DView.this.mFocusView.performClick();
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterEasterMode() {
        Logger.d(TAG, "enterEasterMode");
        EasterEggView easterEggView = this.mEasterEggView;
        if (easterEggView != null && easterEggView.getVisibility() != 0) {
            this.mEasterEggView.setVisibility(0);
            this.mEasterEggView.show();
        }
        this.mSpeechRootView.setVisibility(8);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitEasterMode() {
        EasterEggView easterEggView = this.mEasterEggView;
        if (easterEggView != null) {
            easterEggView.dismiss();
            this.mEasterEggView.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showHint(String guideText, String hintText) {
        String msg;
        Logger.d(TAG, "showHint " + guideText + " ,hintText: " + hintText);
        if (TextUtils.isEmpty(hintText)) {
            msg = guideText;
        } else {
            msg = guideText + "：" + hintText;
        }
        SpeechHintHelper.getInstance().setHintText(msg);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void hideHint() {
        Logger.d(TAG, "hideHint ");
        SpeechHintHelper.getInstance().showHint(false);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void onTtsEcho(TtsEchoValue data) {
        TtsEchoHelper.getInstance().showTtsEchoText(data.soundArea, data.text);
    }
}
