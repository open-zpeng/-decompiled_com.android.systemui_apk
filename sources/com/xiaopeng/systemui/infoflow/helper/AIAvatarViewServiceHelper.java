package com.xiaopeng.systemui.infoflow.helper;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.R;
import com.xiaopeng.aiavatarview.IAIAvatarViewBinder;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.controller.ui.ISpeechUI;
import com.xiaopeng.systemui.controller.ui.SystemUIController;
import com.xiaopeng.systemui.infoflow.IInfoflowView;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
/* loaded from: classes24.dex */
public class AIAvatarViewServiceHelper {
    private static final String TAG = "AIAvatarViewServiceHelper";
    private static final AIAvatarViewServiceHelper sInstance = new AIAvatarViewServiceHelper();
    private IAIAvatarViewBinder mIAIAvatarViewBinder;
    private boolean inDialog = false;
    private boolean isPanelShow = false;
    private boolean isAsrShow = false;
    private boolean inSmallScence = true;
    private boolean inReplaceScence = false;
    private boolean inRestoreScence = false;
    private IInfoflowView mInfoflowView = PresenterCenter.getInstance().getInfoFlow().getInfoflowView();
    private Context mContext = ContextUtils.getContext();

    private AIAvatarViewServiceHelper() {
        SystemUIController.get().getISpeechUI().addSpeechUICallBack(new ISpeechUI.ISpeechUICallBack() { // from class: com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper.1
            @Override // com.xiaopeng.systemui.controller.ui.ISpeechUI.ISpeechUICallBack
            public void onSpeechUIEnableChanged(boolean enable) {
                Log.i(AIAvatarViewServiceHelper.TAG, "inDialog:" + AIAvatarViewServiceHelper.this.inDialog + " &isAsrShow:" + AIAvatarViewServiceHelper.this.isAsrShow + " &isPanelShow:" + AIAvatarViewServiceHelper.this.isPanelShow + " &inReplaceScence:" + AIAvatarViewServiceHelper.this.inReplaceScence);
                if (AIAvatarViewServiceHelper.this.inReplaceScence && AIAvatarViewServiceHelper.this.isAwaken()) {
                    AIAvatarViewServiceHelper.this.restoreScene();
                }
            }
        });
    }

    public static final AIAvatarViewServiceHelper instance() {
        return sInstance;
    }

    public void init(IAIAvatarViewBinder binder) {
        this.mIAIAvatarViewBinder = binder;
    }

    public void replaceScene() {
        Log.d(TAG, "replaceScene");
        if (this.inReplaceScence) {
            Log.d(TAG, "already replaceScene");
        } else if (!SystemUIController.get().getISpeechUI().isSpeechUIEnable()) {
            Log.i(TAG, "replaceScene isSpeechUIEnable false");
        } else {
            this.inReplaceScence = true;
            this.inSmallScence = false;
            this.inRestoreScence = false;
            IAIAvatarViewBinder iAIAvatarViewBinder = this.mIAIAvatarViewBinder;
            if (iAIAvatarViewBinder == null) {
                Log.w(TAG, "AIAvatarViewServiceHelper has not init!");
                return;
            }
            try {
                iAIAvatarViewBinder.replaceAvatarScene();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void restoreScene() {
        Log.d(TAG, "restoreScene");
        if (this.inRestoreScence) {
            Log.d(TAG, "already restoreScene");
            return;
        }
        this.inReplaceScence = false;
        this.inSmallScence = false;
        this.inRestoreScence = true;
        IAIAvatarViewBinder iAIAvatarViewBinder = this.mIAIAvatarViewBinder;
        if (iAIAvatarViewBinder == null) {
            Log.w(TAG, "AIAvatarViewServiceHelper has not init!");
            return;
        }
        try {
            iAIAvatarViewBinder.restoreScene();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void smallScene() {
        Log.d(TAG, "smallScene");
        if (this.inSmallScence) {
            Log.d(TAG, "already smallScene");
            return;
        }
        this.inReplaceScence = false;
        this.inSmallScence = true;
        this.inRestoreScence = false;
        IAIAvatarViewBinder iAIAvatarViewBinder = this.mIAIAvatarViewBinder;
        if (iAIAvatarViewBinder == null) {
            Log.w(TAG, "AIAvatarViewServiceHelper has not init!");
            return;
        }
        try {
            iAIAvatarViewBinder.smallScene();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateDialogStatus(boolean dialogStarted) {
        Log.d(TAG, "updateDialogStatus:" + dialogStarted);
        this.inDialog = dialogStarted;
        updateScence();
        updateDateViewStatus();
    }

    public void updatePanelVisibleStatus(boolean panelShow) {
        Log.d(TAG, "updatePanelVisibleStatus:" + panelShow);
        this.isPanelShow = panelShow;
        updateScence();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAwaken() {
        return this.isAsrShow || this.inDialog;
    }

    private void updateScence() {
        Log.d(TAG, "inDialog:" + this.inDialog + " &isAsrShow:" + this.isAsrShow + " &isPanelShow:" + this.isPanelShow);
        if (isAwaken()) {
            if (this.isPanelShow) {
                replaceScene();
                collapseCardStack();
                return;
            }
            restoreScene();
            expandCardStack();
            return;
        }
        smallScene();
        collapseCardStack();
    }

    public void updateAsrStaus(boolean asrShow) {
        Log.d(TAG, "updateAsrStaus:" + asrShow);
        this.isAsrShow = asrShow;
        updateScence();
        updateDateViewStatus();
    }

    private void updateDateViewStatus() {
        if (!this.inDialog && !this.isAsrShow) {
            setDateViewShow(true);
        } else {
            setDateViewShow(false);
        }
    }

    private void setDateViewShow(boolean visible) {
        this.mInfoflowView.showDateTimeView(visible);
    }

    private void collapseCardStack() {
        this.mInfoflowView.collapseCardStack();
    }

    private void expandCardStack() {
        this.mInfoflowView.expandCardStack();
    }

    public void updateCallingStatus(boolean inCalling) {
        this.mInfoflowView.setWakeupStatus(inCalling ? 1 : 0, this.mContext.getString(R.string.default_xp_noservice_msg));
    }

    public void updateWakeupStatus(int status, String info) {
        this.mInfoflowView.setWakeupStatus(status, info);
    }

    /* loaded from: classes24.dex */
    public static class EventParams {
        private int musicState;
        private int source;
        private int style;
        private String styleName;
        private String title;

        public int getMusicState() {
            return this.musicState;
        }

        public void setMusicState(int musicState) {
            this.musicState = musicState;
        }

        public int getStyle() {
            return this.style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public String getStyleName() {
            return this.styleName;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSource() {
            return this.source;
        }

        public void setSource(int source) {
            this.source = source;
        }
    }
}
