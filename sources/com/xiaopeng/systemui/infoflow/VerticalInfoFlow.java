package com.xiaopeng.systemui.infoflow;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.view.View;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.ContextHelper;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.quickmenu.QuickMenuPresenterManager;
import com.xiaopeng.systemui.utils.BIHelper;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class VerticalInfoFlow extends AbstractInfoFlow implements ActivityController.OnActivityCallback, IVerticalInfoflowPresenter {
    private static final String MASK_CLICK = "mask.click";
    private static final String TAG = "VerticalInfoFlow";
    private final String ACTIVITY_CAMERA;
    private final String ACTIVITY_SETTINGS;
    private final String INTENT_ACTION_SHOW_CAR_CONTROL;
    private final String PACKAGE_CAMERA;
    private final String PACKAGE_SETTINGS;
    private boolean mIsDialogStarted;
    private boolean mIsSuperPanelVisible;
    View.OnClickListener mOnClickListener;

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void enterSpeechMode(int type) {
        if (!this.mIsDialogStarted) {
            return;
        }
        this.mInfoflowView.enterSpeechMode(type);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void exitSpeechMode() {
        if (!this.mIsDialogStarted) {
            return;
        }
        this.mInfoflowView.exitSpeechMode();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onVadEnd() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onVadBegin() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onDialogStart() {
        if (this.mIsSuperPanelVisible) {
            return;
        }
        this.mIsDialogStarted = true;
        this.mInfoflowView.showPanelAsr(SpeechPresenter.isPanelVisible());
        this.mInfoflowView.addSpeechCardBackground();
        this.mInfoflowView.showMiniAsrContainer(false);
        notifyAvatarListeningStatus(false);
        QuickMenuPresenterManager.getInstance().autoHideQuickMenu(0);
    }

    private void notifyAvatarListeningStatus(boolean isListening) {
        if (isListening) {
            AIAvatarViewServiceHelper.instance().replaceScene();
        } else {
            AIAvatarViewServiceHelper.instance().restoreScene();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onDialogEnd(DialogEndReason endReason) {
        if (!this.mIsDialogStarted) {
            return;
        }
        if (!SpeechPresenter.isDialogEndByRouteCompute(endReason)) {
            this.mIsDialogStarted = false;
        }
        reset();
    }

    private void reset() {
        this.mInfoflowView.enterNormalMode();
        AIAvatarViewServiceHelper.instance().smallScene();
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onListeningStatusChanged(boolean listeningStatus) {
        if (!this.mIsDialogStarted) {
            return;
        }
        if (listeningStatus && ContextHelper.isBugReport()) {
            return;
        }
        this.mInfoflowView.showMiniAsrContainer(listeningStatus);
        notifyAvatarListeningStatus(listeningStatus);
    }

    public VerticalInfoFlow(Context context) {
        super(context);
        this.PACKAGE_CAMERA = "com.xiaopeng.xmart.camera";
        this.ACTIVITY_CAMERA = "com.xiaopeng.xmart.camera.MainActivity";
        this.PACKAGE_SETTINGS = VuiConstants.SETTINS;
        this.ACTIVITY_SETTINGS = "com.xiaopeng.car.settings.ui.activity.MainActivity";
        this.INTENT_ACTION_SHOW_CAR_CONTROL = "com.xiaopeng.carcontrol.intent.action.SHOW_CAR_CONTROL";
        this.mIsDialogStarted = false;
        this.mIsSuperPanelVisible = false;
        this.mOnClickListener = new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.VerticalInfoFlow.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Logger.d(VerticalInfoFlow.TAG, "click event");
                if (Utils.isFastClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.img_app_camera /* 2131362416 */:
                        BIHelper.sendBIData(BIHelper.ID.camera, BIHelper.Type.navigationbar);
                        PackageHelper.startCarCamera(VerticalInfoFlow.this.mContext, false);
                        return;
                    case R.id.img_app_fm /* 2131362417 */:
                        PackageHelper.startFm(VerticalInfoFlow.this.mContext);
                        return;
                    case R.id.img_app_icon /* 2131362418 */:
                    default:
                        return;
                    case R.id.img_app_list /* 2131362419 */:
                        BIHelper.sendBIData(BIHelper.ID.applist, BIHelper.Type.navigationbar);
                        PackageHelper.startAppPackages(VerticalInfoFlow.this.mContext, false);
                        return;
                    case R.id.img_app_map /* 2131362420 */:
                        BIHelper.sendBIData(BIHelper.ID.map, BIHelper.Type.navigationbar);
                        PackageHelper.startMap(VerticalInfoFlow.this.mContext);
                        return;
                    case R.id.img_app_music /* 2131362421 */:
                        BIHelper.sendBIData(BIHelper.ID.music, BIHelper.Type.navigationbar);
                        PackageHelper.startCarMusic(VerticalInfoFlow.this.mContext, 0, false, true);
                        return;
                    case R.id.img_app_music_login /* 2131362422 */:
                        PackageHelper.startMusicLogin(VerticalInfoFlow.this.mContext, false);
                        return;
                    case R.id.img_app_phone /* 2131362423 */:
                        BIHelper.sendBIData(BIHelper.ID.phone, BIHelper.Type.navigationbar);
                        PackageHelper.startBtPhone(VerticalInfoFlow.this.mContext, false);
                        return;
                    case R.id.img_app_setting /* 2131362424 */:
                        BIHelper.sendBIData(BIHelper.ID.settings, BIHelper.Type.navigationbar);
                        PackageHelper.startSettings(VerticalInfoFlow.this.mContext, false);
                        return;
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    public void start() {
        super.start();
        init(this.mContext);
    }

    private void init(Context context) {
        Core.init(context);
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    public void onConfigChanged(Configuration configuration) {
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    protected IInfoflowView createInfoflowView() {
        return ViewFactory.getVerticalInfoflowView();
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void notifyAvatarAction(int action) {
        if (this.mAIAvatarViewService != null) {
            try {
                this.mAIAvatarViewService.notifyAvatarAction(action, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.controller.ActivityController.OnActivityCallback
    public void onActivityChanged(ActivityController.ComponentInfo ci) {
        super.onActivityChanged(ci);
        ComponentName cn = ci.getName();
        if (cn != null && PackageHelper.PACKAGE_OOBE.equals(cn.getPackageName()) && this.mIsDialogStarted) {
            reset();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void showVoiceLoc(int voiceLoc) {
        this.mInfoflowView.showVoiceLoc(voiceLoc);
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void updateSceneType(int sceneType) {
        this.mInfoflowView.setSceneType(sceneType);
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow, com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onTopViewTypeChanged(int viewType, boolean isLauncher) {
        boolean z = false;
        this.mIsSuperPanelVisible = false;
        if (viewType == 0) {
            if (this.mIsDialogStarted) {
                this.mInfoflowView.showPanelAsr(false);
            }
        } else if (viewType == 1) {
            onSuperPanelDisplayed();
        } else {
            if (viewType != 2) {
                if (viewType == 3) {
                    IInfoflowView iInfoflowView = this.mInfoflowView;
                    if (this.mIsDialogStarted && !isLauncher) {
                        z = true;
                    }
                    iInfoflowView.showPanelAsr(z);
                    return;
                } else if (viewType != 4) {
                    return;
                }
            }
            if (this.mIsDialogStarted) {
                this.mInfoflowView.showPanelAsr(true);
            }
        }
    }

    private void onSuperPanelDisplayed() {
        this.mIsSuperPanelVisible = true;
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onFmClicked() {
        PackageHelper.startFm(this.mContext);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onMusicClicked() {
        BIHelper.sendBIData(BIHelper.ID.music, BIHelper.Type.navigationbar);
        PackageHelper.startCarMusic(this.mContext, 0, false, true);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onThirdPartyMusicClicked() {
        PackageHelper.startMusicLogin(this.mContext, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onSettingClicked() {
        BIHelper.sendBIData(BIHelper.ID.settings, BIHelper.Type.navigationbar);
        PackageHelper.startSettings(this.mContext, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onAppListClicked() {
        BIHelper.sendBIData(BIHelper.ID.applist, BIHelper.Type.navigationbar);
        PackageHelper.startAppPackages(this.mContext, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onCameraClicked() {
        BIHelper.sendBIData(BIHelper.ID.camera, BIHelper.Type.navigationbar);
        PackageHelper.startCarCamera(this.mContext, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onPhoneClicked() {
        BIHelper.sendBIData(BIHelper.ID.phone, BIHelper.Type.navigationbar);
        PackageHelper.startBtPhone(this.mContext, false);
    }

    @Override // com.xiaopeng.systemui.infoflow.IVerticalInfoflowPresenter
    public void onMapClicked() {
        BIHelper.sendBIData(BIHelper.ID.map, BIHelper.Type.navigationbar);
        PackageHelper.startMap(this.mContext);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.VoiceWavePresenter
    public void showAsrBackground(boolean visible) {
    }
}
