package com.xiaopeng.systemui.infoflow;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.systemui.PresenterCenter;
import com.xiaopeng.systemui.ViewFactory;
import com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper;
import com.xiaopeng.systemui.infoflow.checking.CarCheckHelper;
import com.xiaopeng.systemui.infoflow.common.event.EventCenter;
import com.xiaopeng.systemui.infoflow.common.event.EventPackage;
import com.xiaopeng.systemui.infoflow.common.event.IEventListener;
import com.xiaopeng.systemui.infoflow.dao.InfoFlowConfigDao;
import com.xiaopeng.systemui.infoflow.devtool.CarCheckReceiver;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.listener.TouchRotationSpeedObserver;
import com.xiaopeng.systemui.infoflow.listener.WheelKeyEventListener;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.manager.MediaManager;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
import com.xiaopeng.systemui.infoflow.message.event.CarCheckEventPackage;
import com.xiaopeng.systemui.infoflow.message.event.EasterEggStateEventPackage;
import com.xiaopeng.systemui.infoflow.message.event.EventType;
import com.xiaopeng.systemui.infoflow.message.listener.XNotificationListener;
import com.xiaopeng.systemui.infoflow.message.presenter.CardsPresenter;
import com.xiaopeng.systemui.infoflow.receiver.FileCopyReceiver;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
import com.xiaopeng.systemui.infoflow.util.ImageUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class LandscapeInfoFlow extends AbstractInfoFlow implements ILandscapeInfoflowPresenter {
    private static final String TAG = "LandscapeInfoFlow";
    private IAIAvatarViewStatusCallback.Stub mAvatarViewStatusCallback;
    private CardListData mCardsData;
    private CardsPresenter mCardsPresenter;
    private IEventListener mEasterEggStateListener;
    private IEventListener mExitCarListener;
    private boolean mIsEasterEggViewShow;
    private XNotificationListener mNotificationListener;
    private ContextManager.OnNaviModeChangedListener mOnNaviModeChangedListener;
    MediaManager.OnVisualizerViewEnableListener mVisualizerViewEnableListener;

    /* renamed from: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow$1  reason: invalid class name */
    /* loaded from: classes24.dex */
    class AnonymousClass1 extends IAIAvatarViewStatusCallback.Stub {
        AnonymousClass1() {
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void enterFullBodyMode() throws RemoteException {
            Logger.d(LandscapeInfoFlow.TAG, "enterFullBodyMode--");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.1.1
                @Override // java.lang.Runnable
                public void run() {
                    LandscapeInfoFlow.this.mInFullAvatarMode = true;
                    LandscapeInfoFlow.this.mInfoflowView.showMessageViewGroup(false);
                }
            });
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void exitFullBodyMode() throws RemoteException {
            Logger.d(LandscapeInfoFlow.TAG, "exitFullBodyMode--");
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.1.2
                @Override // java.lang.Runnable
                public void run() {
                    LandscapeInfoFlow.this.mInFullAvatarMode = false;
                    LandscapeInfoFlow.this.checkToShowMessageViewGroup();
                }
            });
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void onSkinUpdate(String snapshotPath) throws RemoteException {
            ImageUtil.loadDrawable(LandscapeInfoFlow.this.mContext, "snapshot", snapshotPath, new ImageUtil.LoadedFileFinishListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.1.3
                @Override // com.xiaopeng.systemui.infoflow.util.ImageUtil.LoadedFileFinishListener
                public void onLoad(final Drawable drawable) {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.1.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            LandscapeInfoFlow.this.mInfoflowView.onAvatarSkinUpdate(drawable);
                        }
                    });
                }
            });
        }

        @Override // com.xiaopeng.aiavatarview.IAIAvatarViewStatusCallback
        public void onAvatarStateChanged(int state) throws RemoteException {
            LandscapeInfoFlow.this.mInfoflowView.onAvatarStateChanged(state);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    public void checkToShowMessageViewGroup() {
        boolean inSpeechMode = SpeechPresenter.getInstance().isInSpeechMode();
        boolean inCarCheckMode = this.mCardsPresenter.isInCarCheckMode();
        Logger.d(TAG, "checkToShowMessageViewGroup : speechMode --" + inSpeechMode + " &inCarCheckMode --" + inCarCheckMode + " &inFullAvatarMode --" + this.mInFullAvatarMode);
        if (this.mInfoflowView != null && !inSpeechMode && !inCarCheckMode && !this.mInFullAvatarMode) {
            this.mInfoflowView.showMessageViewGroup(true);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    protected void onAvatarServiceConnected() {
        try {
            this.mAIAvatarViewService.registerStatusCallback(this.mAvatarViewStatusCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    protected void onAvatarServiceDisconnected() {
        if (this.mAIAvatarViewService != null) {
            try {
                this.mAIAvatarViewService.unregisterStatusCallback(this.mAvatarViewStatusCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void enterSpeechMode(int type) {
        checkToShowMessageViewGroup();
        this.mInfoflowView.enterSpeechMode(type);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void exitSpeechMode() {
        this.mInfoflowView.exitSpeechMode();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onVadEnd() {
        AsrHelper.getInstance().updateAsrStatus(10, SpeechPresenter.getInstance().isListening());
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onVadBegin() {
        AsrHelper.getInstance().updateAsrStatus(9, SpeechPresenter.getInstance().isListening());
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onDialogStart() {
        CarCheckHelper.notifypeechMode(true);
        Logger.d(TAG, "landscape onDialogStart");
        this.mInfoflowView.onDialogStart();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.SpeechViewContainer
    public void onDialogEnd(DialogEndReason endReason) {
        CarCheckHelper.notifypeechMode(false);
        Logger.d(TAG, "landscape onDialogEnd");
        this.mInfoflowView.onDialogEnd(endReason);
    }

    public LandscapeInfoFlow(Context context) {
        super(context);
        this.mAvatarViewStatusCallback = new AnonymousClass1();
        this.mExitCarListener = new IEventListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.4
            @Override // com.xiaopeng.systemui.infoflow.common.event.IEventListener
            public boolean onEvent(EventPackage eventPackage) {
                CarCheckEventPackage carCheckEventPackage = (CarCheckEventPackage) eventPackage;
                if (carCheckEventPackage.exit) {
                    LandscapeInfoFlow.this.checkToShowMessageViewGroup();
                    return true;
                }
                return true;
            }
        };
        this.mIsEasterEggViewShow = false;
        this.mEasterEggStateListener = new IEventListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.5
            @Override // com.xiaopeng.systemui.infoflow.common.event.IEventListener
            public boolean onEvent(EventPackage eventPackage) {
                EasterEggStateEventPackage easterEggStateEventPackage = (EasterEggStateEventPackage) eventPackage;
                if (easterEggStateEventPackage.show && !LandscapeInfoFlow.this.mIsEasterEggViewShow) {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            LandscapeInfoFlow.this.mCardsPresenter.enterCarCheckMode();
                            LandscapeInfoFlow.this.mIsEasterEggViewShow = true;
                            LandscapeInfoFlow.this.mInfoflowView.enterEasterMode();
                        }
                    });
                    return true;
                } else if (!easterEggStateEventPackage.show && LandscapeInfoFlow.this.mIsEasterEggViewShow) {
                    ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.5.2
                        @Override // java.lang.Runnable
                        public void run() {
                            LandscapeInfoFlow.this.mCardsPresenter.exitCarCheckMode();
                            LandscapeInfoFlow.this.mIsEasterEggViewShow = false;
                            LandscapeInfoFlow.this.mInfoflowView.exitEasterMode();
                        }
                    });
                    return true;
                } else {
                    return true;
                }
            }
        };
        this.mOnNaviModeChangedListener = new ContextManager.OnNaviModeChangedListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.6
            @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviModeChangedListener
            public void onNaviModeChanged(int currentMode) {
                Logger.d(LandscapeInfoFlow.TAG, "onNaviModeChanged : " + currentMode);
                if (currentMode == 1) {
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onExitExlporeMode();
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onEnterNaviMode();
                } else if (currentMode == 2) {
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onExitNaviMode();
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onEnterExlporeMode();
                } else {
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onExitNaviMode();
                    XNotificationListener.getInstance(LandscapeInfoFlow.this.mContext).onExitExlporeMode();
                }
            }
        };
        this.mVisualizerViewEnableListener = new MediaManager.OnVisualizerViewEnableListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.7
            @Override // com.xiaopeng.systemui.infoflow.manager.MediaManager.OnVisualizerViewEnableListener
            public void onViewEnable(boolean enable) {
                LandscapeInfoFlow.this.mInfoflowView.showVisualizerWindow(enable);
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    public void start() {
        super.start();
        parseInfoFlowConfig();
        boolean infoflowEnable = InfoFlowConfigDao.getInstance().getConfig().infoflowEnable;
        if (!infoflowEnable) {
            Logger.w(TAG, "infoflow disabled");
            return;
        }
        init(this.mContext);
        initCards();
        MediaManager.getInstance().addVisualizerViewEnableListener(this.mVisualizerViewEnableListener);
    }

    private void initCards() {
        this.mCardsData = CardListData.getInstance();
        this.mCardsPresenter = new CardsPresenter(this.mCardsData, this.mInfoflowView);
        this.mNotificationListener = XNotificationListener.getInstance(this.mContext);
        this.mNotificationListener.setupWithPresenter(this.mCardsPresenter);
        PresenterCenter.getInstance().setCardsPresenter(this.mCardsPresenter);
    }

    private void init(Context context) {
        Core.init(context);
        bindCarCheckEvent();
        bindEasterEggEvent();
        registerWheelKey();
        registerNaviModeEvent();
        new FileCopyReceiver().register(this.mContext);
        CarCheckHelper.initAllowCarCheckValue();
        CarCheckHelper.resetSleepTime();
        CarCheckHelper.stopCarCheck();
        CarCheckHelper.doInfoCarCheck();
        registerGearUpdateListener();
        registerCarCheckReceiver();
        registerTouchRotationSpeedObserver();
    }

    private void parseInfoFlowConfig() {
        InfoFlowConfigDao.getInstance().parseConfigFile();
    }

    private void registerCarCheckReceiver() {
        new CarCheckReceiver().register(this.mContext);
    }

    private void registerTouchRotationSpeedObserver() {
        new TouchRotationSpeedObserver().startMonitor();
    }

    private void registerGearUpdateListener() {
        VcuControllerWrapper.getInstance().addListener(new VcuControllerWrapper.Listener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.2
            @Override // com.xiaopeng.systemui.carmanager.impl.VcuControllerWrapper.Listener
            public void onGearChanged(final int gear) {
                ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Logger.d(LandscapeInfoFlow.TAG, "onGearChanged--" + gear);
                        if (gear == 4) {
                            CarCheckHelper.doInfoCarCheck();
                        } else {
                            CarCheckHelper.stopCarCheck();
                        }
                    }
                });
            }
        });
    }

    private void registerWheelKey() {
        new WheelKeyEventListener(this.mContext, new WheelKeyEventListener.WheelKeyListener() { // from class: com.xiaopeng.systemui.infoflow.LandscapeInfoFlow.3
            @Override // com.xiaopeng.systemui.infoflow.listener.WheelKeyEventListener.WheelKeyListener
            public void onWheelKeyEvent(KeyEvent keyEvent) {
                Log.d(LandscapeInfoFlow.TAG, "keyEvent code = " + keyEvent.getKeyCode());
                LandscapeInfoFlow.this.mInfoflowView.onWheelKeyEvent(keyEvent);
            }
        }).register();
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    public void onConfigChanged(Configuration configuration) {
    }

    @Override // com.xiaopeng.systemui.infoflow.AbstractInfoFlow
    protected IInfoflowView createInfoflowView() {
        return ViewFactory.getLandscapeInfoflowView();
    }

    private void bindCarCheckEvent() {
        EventCenter.instance().bindListener(EventType.EXIT_CAR_CHECK, this.mExitCarListener);
    }

    private void bindEasterEggEvent() {
        EventCenter.instance().bindListener(EventType.EASTER_EGG_STATE, this.mEasterEggStateListener);
    }

    private void registerNaviModeEvent() {
        ContextManager.getInstance().setOnNaviModeChangeListener(this.mOnNaviModeChangedListener);
    }

    @Override // com.xiaopeng.systemui.infoflow.ILandscapeInfoflowPresenter
    public String getCardList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardEntries", new JSONArray(GsonUtil.toJson(CardListData.getInstance().getCards())));
        } catch (JSONException e) {
            Logger.d(TAG, "getCardList convert jsonData error:" + e.getMessage());
        }
        Logger.d(TAG, "getCardList : " + jsonObject.toString());
        return jsonObject.toString();
    }

    @Override // com.xiaopeng.systemui.infoflow.ILandscapeInfoflowPresenter
    public void exitCarCheck() {
        CarCheckHelper.stopCarCheck();
        EventCenter.instance().raiseEvent(new CarCheckEventPackage(EventType.EXIT_CAR_CHECK, this, true));
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.VoiceWavePresenter
    public void showAsrBackground(boolean visible) {
        if (this.mInfoflowView != null) {
            this.mInfoflowView.showAsrBackground(visible);
        }
    }
}
