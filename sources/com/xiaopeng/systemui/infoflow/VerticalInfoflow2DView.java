package com.xiaopeng.systemui.infoflow;

import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedImageView;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.ui.widget.AppIconContainer;
import com.xiaopeng.systemui.utils.Utils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class VerticalInfoflow2DView extends BaseInfoflow2DView implements View.OnAttachStateChangeListener, View.OnClickListener {
    private static final String TAG = "VerticalInfoflow2DView";
    private AppIconContainer appCamera;
    private AppIconContainer appFm;
    private AppIconContainer appList;
    private AppIconContainer appMap;
    private AppIconContainer appMusic;
    private AppIconContainer appMusicLogin;
    private AppIconContainer appPhone;
    private AppIconContainer appSetting;
    private AnimatedImageView mAsrBackgound;
    private FrameLayout mAvatarView;
    private WindowManager.LayoutParams mAvatarViewLp;
    private AnimatedImageView mCloseBtn;
    private WindowManager.LayoutParams mLp;
    private RelativeLayout mMiniAsrContainer;
    private WindowManager.LayoutParams mMiniAsrContainerLp;
    private AnimatedImageView mPanelAsrBackground;
    private FrameLayout mSpeechCardBackground;
    private WindowManager.LayoutParams mSpeechCardBgLp;
    private List<View> mViewList;
    private AlphaOptimizedRelativeLayout mVoiceLocContainer;
    private AnimatedTextView mVoiceLocHintTitle;
    private boolean mIsSpeechViewParentAdded = false;
    private boolean mIsMiniAsrContainerAdded = false;
    private boolean mIsAvatarViewAdded = false;
    private boolean mIsSpeechCardBackgroundAdded = false;
    private int mVoiceWaveRegion = -1;

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void initView() {
        super.initView();
        this.mViewList = new ArrayList();
        if (this.mAsrContainer != null) {
            this.mAsrBackgound = (AnimatedImageView) this.mAsrContainer.findViewById(R.id.asr_background);
            this.mPanelAsrBackground = (AnimatedImageView) this.mAsrContainer.findViewById(R.id.panel_asr_background);
            this.mCloseBtn = (AnimatedImageView) this.mAsrContainer.findViewById(R.id.iv_close);
            this.mCloseBtn.setOnClickListener(this);
        }
        this.appCamera = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_camera);
        this.appSetting = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_setting);
        this.appList = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_list);
        this.appMap = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_map);
        this.appPhone = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_phone);
        this.appMusic = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_music);
        this.appMusicLogin = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_music_login);
        this.appFm = (AppIconContainer) this.mRootView.findViewById(R.id.img_app_fm);
        AppIconContainer appIconContainer = this.appCamera;
        if (appIconContainer != null) {
            appIconContainer.setOnClickListener(this);
            this.appCamera.setTag(this.mContext.getString(R.string.component_camera));
            this.mViewList.add(this.appCamera);
        }
        AppIconContainer appIconContainer2 = this.appSetting;
        if (appIconContainer2 != null) {
            appIconContainer2.setOnClickListener(this);
            this.appSetting.setTag(this.mContext.getString(R.string.component_settings));
            this.mViewList.add(this.appSetting);
        }
        AppIconContainer appIconContainer3 = this.appList;
        if (appIconContainer3 != null) {
            appIconContainer3.setOnClickListener(this);
            this.appList.setTag(this.mContext.getString(R.string.component_app));
            this.mViewList.add(this.appList);
        }
        AppIconContainer appIconContainer4 = this.appMap;
        if (appIconContainer4 != null) {
            appIconContainer4.setOnClickListener(this);
            this.appMap.setTag(this.mContext.getString(R.string.component_map));
            this.mViewList.add(this.appMap);
        }
        AppIconContainer appIconContainer5 = this.appPhone;
        if (appIconContainer5 != null) {
            appIconContainer5.setOnClickListener(this);
            this.appPhone.setTag(this.mContext.getString(R.string.component_phone));
            this.mViewList.add(this.appPhone);
        }
        AppIconContainer appIconContainer6 = this.appMusic;
        if (appIconContainer6 != null) {
            appIconContainer6.setOnClickListener(this);
            this.appMusic.setTag(this.mContext.getString(R.string.component_music));
            this.mViewList.add(this.appMusic);
        }
        AppIconContainer appIconContainer7 = this.appMusicLogin;
        if (appIconContainer7 != null) {
            appIconContainer7.setOnClickListener(this);
            this.appMusicLogin.setTag(this.mContext.getString(R.string.component_music_login));
            this.mViewList.add(this.appMusicLogin);
        }
        AppIconContainer appIconContainer8 = this.appFm;
        if (appIconContainer8 != null) {
            appIconContainer8.setOnClickListener(this);
            this.appFm.setTag(this.mContext.getString(R.string.component_fm));
            this.mViewList.add(this.appFm);
        }
        if (this.mAsrContainer != null) {
            this.mAsrContainer.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.VerticalInfoflow2DView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView
    protected RelativeLayout getRootView() {
        return (RelativeLayout) WindowHelper.getStatusBarWindow().findViewById(R.id.view_infoflow);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView
    public void initSpeechViewParent() {
        if (CarModelsManager.getFeature().isSpeechInterActionSupport()) {
            this.mSpeechViewParent = (RelativeLayout) View.inflate(this.mContext, R.layout.speech_view_parent, null);
            this.mSpeechViewParent.addOnAttachStateChangeListener(this);
            this.mLp = new WindowManager.LayoutParams(-1, -2, WindowHelper.TYPE_VUI, 25165864, -3);
            this.mLp.gravity = 51;
            this.mSpeechCardBackground = (FrameLayout) View.inflate(this.mContext, R.layout.view_speech_card_background, null);
            this.mSpeechCardBackground.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.VerticalInfoflow2DView.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    FeedListUIValue feedListUIValue = new FeedListUIValue();
                    feedListUIValue.source = VerticalInfoflow2DView.this.mContext.getPackageName();
                    SpeechClient.instance().getAgent().sendUIEvent("mask.click", FeedListUIValue.toJson(feedListUIValue));
                }
            });
            this.mSpeechCardBgLp = new WindowManager.LayoutParams(-1, 1, 2048, 25165864, -3);
            this.mSpeechCardBgLp.gravity = 51;
            addSpeechCardBackground();
            this.mMiniAsrContainer = (RelativeLayout) View.inflate(this.mContext, R.layout.view_mini_asr, null);
            this.mMiniAsrContainer.setOnClickListener(this);
            this.mMiniAsrContainerLp = new WindowManager.LayoutParams(-1, -2, WindowHelper.TYPE_VUI, 25165864, -3);
            this.mMiniAsrContainerLp.gravity = 51;
            this.mAvatarView = (FrameLayout) View.inflate(this.mContext, R.layout.view_avatar, null);
            this.mAvatarView.setOnClickListener(this);
            this.mAvatarViewLp = new WindowManager.LayoutParams(120, 120, WindowHelper.TYPE_VUI, 25165864, -3);
            WindowManager.LayoutParams layoutParams = this.mAvatarViewLp;
            layoutParams.gravity = 51;
            layoutParams.x = 65;
            layoutParams.y = 64;
            this.mVoiceLocContainer = (AlphaOptimizedRelativeLayout) this.mSpeechViewParent.findViewById(R.id.voice_loc_hint_container);
            this.mVoiceLocHintTitle = (AnimatedTextView) this.mSpeechViewParent.findViewById(R.id.voice_loc_hint_title);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void onNavigationItemChanged(String packageName, String className, boolean isCarControlReady) {
        ActivityController.onNavigationItemChanged(packageName, className, this.mViewList, isCarControlReady);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View v) {
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View v) {
        if (v == this.mSpeechViewParent) {
            Logger.d(TAG, "onViewDetachedFromWindow mSpeechViewParent");
            AsrHelper.getInstance().hideVuiRecommendView();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterSpeechMode(int type) {
        super.enterSpeechMode(type);
        if (this.mSpeechRootView != null) {
            this.mSpeechRootView.setVisibility(0);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void exitSpeechMode() {
        super.exitSpeechMode();
        if (this.mSpeechRootView != null) {
            this.mSpeechRootView.setVisibility(8);
        }
        showSpeechBackground(false);
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showMiniAsrContainer(boolean listeningStatus) {
        Logger.d(TAG, "showMiniAsrContainer : " + listeningStatus);
        if (listeningStatus) {
            removeSpeechViewParent();
            showSpeechBackground(false);
            addMiniAsrContainer();
            return;
        }
        removeMiniAsrContainer();
        addSpeechViewParent();
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void enterNormalMode() {
        removeSpeechViewParent();
        removeMiniAsrContainer();
        removeSpeechCardBackground();
    }

    private void removeSpeechViewParent() {
        if (this.mSpeechViewParent == null) {
            return;
        }
        if (this.mIsSpeechViewParentAdded && this.mSpeechViewParent.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mSpeechViewParent);
            this.mIsSpeechViewParentAdded = false;
        } else if (this.mSpeechViewParent.getVisibility() == 0) {
            this.mSpeechViewParent.setVisibility(8);
        }
    }

    private void removeMiniAsrContainer() {
        Logger.d(TAG, "removeMiniAsrContainer : " + this.mMiniAsrContainer);
        RelativeLayout relativeLayout = this.mMiniAsrContainer;
        if (relativeLayout == null) {
            return;
        }
        if (this.mIsMiniAsrContainerAdded && relativeLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mMiniAsrContainer);
            this.mIsMiniAsrContainerAdded = false;
        } else if (this.mMiniAsrContainer.getVisibility() == 0) {
            this.mMiniAsrContainer.setVisibility(8);
        }
        if (this.mIsAvatarViewAdded && this.mAvatarView.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mAvatarView);
            this.mIsAvatarViewAdded = false;
        } else if (this.mAvatarView.getVisibility() == 0) {
            this.mAvatarView.setVisibility(8);
        }
    }

    private void removeSpeechCardBackground() {
        FrameLayout frameLayout = this.mSpeechCardBackground;
        if (frameLayout == null) {
            return;
        }
        if (this.mIsSpeechCardBackgroundAdded && frameLayout.isAttachedToWindow()) {
            this.mWindowManager.removeViewImmediate(this.mSpeechCardBackground);
            this.mIsSpeechCardBackgroundAdded = false;
        } else if (this.mSpeechCardBackground.getVisibility() == 0) {
            this.mSpeechCardBackground.setVisibility(8);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void addSpeechCardBackground() {
        if (this.mSpeechCardBackground == null) {
            return;
        }
        if (!this.mIsSpeechCardBackgroundAdded) {
            this.mSpeechCardBgLp.height = 0;
            this.mWindowManager.addView(this.mSpeechCardBackground, this.mSpeechCardBgLp);
            this.mIsSpeechCardBackgroundAdded = true;
        }
        if (this.mSpeechCardBackground.getVisibility() != 0) {
            this.mSpeechCardBackground.setVisibility(0);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showPanelAsr(boolean visible) {
        AnimatedImageView animatedImageView = this.mPanelAsrBackground;
        if (animatedImageView != null) {
            animatedImageView.setVisibility(visible ? 0 : 8);
        }
        AnimatedImageView animatedImageView2 = this.mAsrBackgound;
        if (animatedImageView2 != null) {
            animatedImageView2.setVisibility(visible ? 8 : 0);
        }
        if (this.mSpeechRootView != null) {
            this.mSpeechRootView.setVisibility(visible ? 8 : 0);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void showVoiceWaveAnim(int regionType, int voiceWaveType, int volume) {
        if (this.mVoiceWaveViewContainer != null) {
            if (regionType != this.mVoiceWaveRegion) {
                this.mVoiceWaveViewContainer.stopVoiceWaveAnim(this.mVoiceWaveRegion);
                this.mVoiceWaveRegion = regionType;
            }
            this.mVoiceWaveViewContainer.showVoiceWaveAnim(regionType, voiceWaveType, volume);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.speech.ISpeechView
    public void stopVoiceWaveAnim() {
        if (this.mVoiceWaveViewContainer != null) {
            this.mVoiceWaveViewContainer.stopVoiceWaveAnim();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void showVoiceLoc(int voiceLoc) {
        if (this.mVoiceLocContainer != null && this.mVoiceLocHintTitle != null) {
            updateVoiceLoc(this.mSceneType, voiceLoc, this.mVoiceLocContainer, this.mVoiceLocHintTitle, null);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.BaseInfoflow2DView, com.xiaopeng.systemui.infoflow.IInfoflowView
    public void setSceneType(int sceneType) {
        AnimatedTextView animatedTextView;
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout = this.mVoiceLocContainer;
        if (alphaOptimizedRelativeLayout != null && (animatedTextView = this.mVoiceLocHintTitle) != null) {
            this.mSceneType = sceneType;
            updateSceneType(sceneType, alphaOptimizedRelativeLayout, animatedTextView, null);
        }
    }

    private void showSpeechBackground(boolean show) {
        if (this.mSpeechCardBgLp != null && this.mSpeechCardBackground.isAttachedToWindow()) {
            this.mSpeechCardBgLp.height = show ? -1 : 0;
            this.mWindowManager.updateViewLayout(this.mSpeechCardBackground, this.mSpeechCardBgLp);
        }
    }

    private void addMiniAsrContainer() {
        Logger.d(TAG, "addMiniAsrContainer : " + this.mMiniAsrContainer);
        if (this.mMiniAsrContainer == null) {
            return;
        }
        if (!this.mIsMiniAsrContainerAdded) {
            this.mWindowManager.addView(this.mMiniAsrContainer, this.mMiniAsrContainerLp);
            this.mIsMiniAsrContainerAdded = true;
            AsrHelper.getInstance().updateAsrContainer(this.mMiniAsrContainer);
        }
        if (!this.mIsAvatarViewAdded) {
            this.mWindowManager.addView(this.mAvatarView, this.mAvatarViewLp);
            this.mIsAvatarViewAdded = true;
        }
        if (this.mMiniAsrContainer.getVisibility() != 0) {
            this.mMiniAsrContainer.setVisibility(0);
        }
        if (this.mAvatarView.getVisibility() != 0) {
            this.mAvatarView.setVisibility(0);
        }
    }

    private void addSpeechViewParent() {
        if (this.mSpeechViewParent == null) {
            return;
        }
        if (!this.mIsSpeechViewParentAdded) {
            this.mIsSpeechViewParentAdded = true;
            this.mWindowManager.addView(this.mSpeechViewParent, this.mLp);
            this.mAsrContainer = (RelativeLayout) this.mSpeechViewParent.findViewById(R.id.asr_container);
        }
        if (this.mSpeechViewParent.getVisibility() != 0) {
            this.mSpeechViewParent.setVisibility(0);
        }
        if (this.mAsrContainer != null) {
            AsrHelper.getInstance().updateAsrContainer(this.mAsrContainer);
            this.mAsrContainer.setVisibility(0);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Logger.d(TAG, "click event");
        if (Utils.isFastClick()) {
            return;
        }
        IVerticalInfoflowPresenter landscapeInfoflowPresenter = (IVerticalInfoflowPresenter) this.mPresenter;
        int id = v.getId();
        if (id != R.id.iv_close && id != R.id.layout_avatar && id != R.id.mini_asr_container) {
            switch (id) {
                case R.id.img_app_camera /* 2131362416 */:
                    landscapeInfoflowPresenter.onCameraClicked();
                    return;
                case R.id.img_app_fm /* 2131362417 */:
                    landscapeInfoflowPresenter.onFmClicked();
                    return;
                default:
                    switch (id) {
                        case R.id.img_app_list /* 2131362419 */:
                            landscapeInfoflowPresenter.onAppListClicked();
                            return;
                        case R.id.img_app_map /* 2131362420 */:
                            landscapeInfoflowPresenter.onMapClicked();
                            return;
                        case R.id.img_app_music /* 2131362421 */:
                            landscapeInfoflowPresenter.onMusicClicked();
                            return;
                        case R.id.img_app_music_login /* 2131362422 */:
                            landscapeInfoflowPresenter.onThirdPartyMusicClicked();
                            return;
                        case R.id.img_app_phone /* 2131362423 */:
                            landscapeInfoflowPresenter.onPhoneClicked();
                            return;
                        case R.id.img_app_setting /* 2131362424 */:
                            landscapeInfoflowPresenter.onSettingClicked();
                            return;
                        default:
                            return;
                    }
            }
        }
        landscapeInfoflowPresenter.stopDialog();
    }
}
