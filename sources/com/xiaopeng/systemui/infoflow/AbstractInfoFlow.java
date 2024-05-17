package com.xiaopeng.systemui.infoflow;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.aar.server.ServerConfig;
import com.xiaopeng.aar.server.ServerManager;
import com.xiaopeng.aiavatarview.IAIAvatarViewBinder;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.systemui.NapaServerListener;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.controller.ActivityController2;
import com.xiaopeng.systemui.controller.BootCompletedController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.message.presenter.MusicCardPresenter;
import com.xiaopeng.systemui.infoflow.message.presenter.SecondaryMusicCardPresenter;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer;
import com.xiaopeng.systemui.infoflow.speech.VoiceWavePresenter;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.speech.data.SpeechManager;
import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public abstract class AbstractInfoFlow implements SpeechViewContainer, VoiceWavePresenter, AvatarViewParentContainer, IInfoflowPresenter, ActivityController.OnActivityCallback {
    private static final String ACTION_START_NGP_WARNING = "com.xiaopeng.systemui.ACTION_START_NGP_WARNING";
    private static final String ACTION_STOP_NGP_WARNING = "com.xiaopeng.systemui.ACTION_STOP_NGP_WARNING";
    private static final String ACTION_SYSTEMUI_STARTED = "com.android.systemui.intent.action.SYSTEMUI_STARTED";
    private static final String TAG = "AbstractInfoFlow";
    protected IAIAvatarViewBinder mAIAvatarViewService;
    protected Context mContext;
    protected IInfoflowView mInfoflowView;
    private NapaServerListener mNapaServerListener;
    private SpeechPresenter mSpeechPresenter;
    protected boolean mInFullAvatarMode = false;
    private String KEY_INFOFLOW_STATUS = "systemui_infoflow_status";
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.xiaopeng.systemui.infoflow.AbstractInfoFlow.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            char c;
            String action = intent.getAction();
            int hashCode = action.hashCode();
            if (hashCode != -1583939831) {
                if (hashCode == 669216741 && action.equals(AbstractInfoFlow.ACTION_START_NGP_WARNING)) {
                    c = 0;
                }
                c = 65535;
            } else {
                if (action.equals(AbstractInfoFlow.ACTION_STOP_NGP_WARNING)) {
                    c = 1;
                }
                c = 65535;
            }
            if (c == 0) {
                AbstractInfoFlow.this.mInfoflowView.startNgpWarningAnim();
            } else if (c == 1) {
                AbstractInfoFlow.this.mInfoflowView.stopNgpWarningAnim();
            }
        }
    };
    ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.xiaopeng.systemui.infoflow.AbstractInfoFlow.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            AbstractInfoFlow.this.mAIAvatarViewService = IAIAvatarViewBinder.Stub.asInterface(service);
            if (AbstractInfoFlow.this.mAIAvatarViewService != null) {
                AIAvatarViewServiceHelper.instance().init(AbstractInfoFlow.this.mAIAvatarViewService);
                AbstractInfoFlow.this.onAvatarServiceConnected();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            AbstractInfoFlow.this.onAvatarServiceDisconnected();
        }
    };

    protected abstract IInfoflowView createInfoflowView();

    public abstract void onConfigChanged(Configuration configuration);

    public IInfoflowView getInfoflowView() {
        return this.mInfoflowView;
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowPresenter
    public void stopDialog() {
        SpeechClient.instance().getWakeupEngine().stopDialog();
    }

    protected void onAvatarServiceConnected() {
    }

    protected void onAvatarServiceDisconnected() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.VoiceWavePresenter
    public void stopVoiceWaveAnim() {
        this.mInfoflowView.stopVoiceWaveAnim();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.VoiceWavePresenter
    public void setAsrLoc(int asrLoc) {
        this.mInfoflowView.setAsrLoc(asrLoc);
    }

    public AbstractInfoFlow(Context context) {
        this.mContext = context;
        VuiEngine.getInstance(this.mContext).setLoglevel(LogUtils.LOG_DEBUG_LEVEL);
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            ServerConfig config = new ServerConfig.Builder().setLogLength(1024).build();
            ServerManager.get().initConfig(config);
        }
    }

    public void start() {
        Log.i(TAG, "infoflow start");
        BootCompletedController.get().init();
        PresenterCenter.getInstance().setInfoflow(this);
        ActivityController.getInstance(this.mContext).addActivityCallback(this);
        ActivityController2.get().init(this.mContext);
        Intent intent = new Intent();
        intent.setAction(ACTION_SYSTEMUI_STARTED);
        this.mContext.sendBroadcast(intent);
        startXpWallpaper();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_NGP_WARNING);
        intentFilter.addAction(ACTION_STOP_NGP_WARNING);
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mInfoflowView = createInfoflowView();
        this.mInfoflowView.initView();
        initSpeech();
        initAvatar();
        MusicCardPresenter.getInstance();
        if (CarModelsManager.getFeature().isSecondaryWindowSupport()) {
            SecondaryMusicCardPresenter.getInstance().bindData(null);
        }
        if (CarModelsManager.getFeature().getSysUIDisplayType() == 2) {
            this.mNapaServerListener = new NapaServerListener();
            ServerManager.get().setServerListener(this.mNapaServerListener);
            ServerManager.get().init(this.mContext);
        }
    }

    public void checkToShowMessageViewGroup() {
    }

    public boolean isInFullAvatarMode() {
        return this.mInFullAvatarMode;
    }

    private void initSpeech() {
        SpeechClient.instance().init(this.mContext, new ConnectManager.OnConnectCallback() { // from class: com.xiaopeng.systemui.infoflow.AbstractInfoFlow.3
            @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
            public void onConnect(ISpeechEngine iSpeechEngine) {
                Log.d(AbstractInfoFlow.TAG, "onSpeechClient connected");
            }

            @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
            public void onDisconnect() {
                Log.d(AbstractInfoFlow.TAG, "onSpeechClient disconnected");
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.AbstractInfoFlow.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AbstractInfoFlow.this.mSpeechPresenter.backToRecommendMode();
                        AbstractInfoFlow.this.mSpeechPresenter.onDialogEnd(null);
                        AIAvatarViewServiceHelper.instance().updateWakeupStatus(0, "");
                        SpeechManager.get().getDialogListener().onDialogEnd(null);
                    }
                });
            }
        });
        this.mSpeechPresenter = SpeechPresenter.getInstance();
        this.mSpeechPresenter.registerSpeechListener();
        this.mSpeechPresenter.setupWithContainer(this);
        this.mSpeechPresenter.setupWithVoiceWavePresenter(this);
        this.mSpeechPresenter.setSpeechView(this.mInfoflowView);
    }

    private void initAvatar() {
        Looper.getMainLooper();
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.xiaopeng.systemui.infoflow.AbstractInfoFlow.4
            @Override // android.os.MessageQueue.IdleHandler
            public boolean queueIdle() {
                Utils.startAvatarViewService(AbstractInfoFlow.this.mContext, AbstractInfoFlow.this.mServiceConnection);
                return false;
            }
        });
    }

    public void startXpWallpaper() {
        Logger.d(TAG, "startXpWallpaper");
        PackageHelper.startService(SystemUIApplication.getContext(), R.string.component_service_wallpaper, null);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void updateSceneType(int sceneType) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void notifyAvatarAction(int action) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onTopViewTypeChanged(int viewType, boolean isLauncher) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void showVoiceLoc(int voiceLoc) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void showSpeechBackground(boolean b) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onListeningStatusChanged(boolean listeningStatus) {
    }

    @Override // com.xiaopeng.systemui.infoflow.AvatarViewParentContainer
    public void notifyAvatarMotionEvent(MotionEvent motionEvent) {
        IAIAvatarViewBinder iAIAvatarViewBinder = this.mAIAvatarViewService;
        if (iAIAvatarViewBinder != null) {
            try {
                iAIAvatarViewBinder.notifyMotionEvent(motionEvent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AvatarViewParentContainer
    public void setAvatarVisible(boolean visible) {
        IAIAvatarViewBinder iAIAvatarViewBinder = this.mAIAvatarViewService;
        if (iAIAvatarViewBinder != null) {
            try {
                iAIAvatarViewBinder.setVisible(visible);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AvatarViewParentContainer
    public void notifyAvatarAction(int actionType, String params) {
        IAIAvatarViewBinder iAIAvatarViewBinder = this.mAIAvatarViewService;
        if (iAIAvatarViewBinder != null) {
            try {
                iAIAvatarViewBinder.notifyAvatarAction(actionType, params);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.controller.ActivityController.OnActivityCallback
    public void onActivityChanged(ActivityController.ComponentInfo ci) {
        ComponentName cn = ci.getName();
        if (cn == null || !ci.isActivityChange()) {
            return;
        }
        this.mInfoflowView.onNavigationItemChanged(cn.getPackageName(), cn.getClassName(), ActivityController.sIsCarControlReady);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowPresenter
    public void onCardFocusedChanged(int index) {
        SpeechPresenter.getInstance().onFocusChanged(index);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowPresenter
    public void sendScrollEvent(int firstVisibleItemPosition) {
        SpeechListView.sendScrollEvent(firstVisibleItemPosition);
    }

    @Override // com.xiaopeng.systemui.infoflow.IInfoflowPresenter
    public void setInfoflowStatus(int status) {
        if (status != 0 && status != 1) {
            return;
        }
        Settings.Secure.putString(this.mContext.getContentResolver(), this.KEY_INFOFLOW_STATUS, String.valueOf(status));
    }
}
