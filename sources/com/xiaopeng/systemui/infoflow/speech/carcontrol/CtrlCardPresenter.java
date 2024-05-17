package com.xiaopeng.systemui.infoflow.speech.carcontrol;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.systemui.infoflow.speech.ui.model.CtrlCardContent;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView;
import com.xiaopeng.systemui.infoflow.speech.utils.ColorUtils;
import com.xiaopeng.systemui.infoflow.speech.utils.UiHandlerUtil;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class CtrlCardPresenter implements ICtrlCardPresenter, CtrlProgressView.OnColorPickerChangeListener {
    private static final int MESSAGE_SEND_SPEECH_COMMAND = 1;
    private static final int MESSAGE_SET_SPEECH_COMMAND_FLAG = 2;
    private static final int MESSAGE_UPDATE_ICM_BRIGHTNESS = 3;
    private static final int SEND_SPEECH_COMMAND_DELAY_TIME = 800;
    private static final String TAG = "CtrlCardPresenter";
    private int[] mAmbientLightingColor;
    private CtrlCardContent mCtrlCardContent;
    private int mCtrlGroupType;
    private int mCurrentType;
    private ILogicCtrlView mLogicView;
    private int mProgressVByCtrlKey;
    private Handler mUiHandler;
    float mLastUiniValue = -1.0f;
    private boolean mIsSpeechCMD = true;
    private ICtrlCardProvider mCardProvider = new CtrlCardProvider(this);

    public CtrlCardPresenter(ILogicCtrlView logicView, int type, CardValue cardValue) {
        this.mLogicView = logicView;
        this.mCtrlGroupType = type;
        onCreate(type, cardValue);
        this.mUiHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int i = msg.what;
                if (i != 1) {
                    if (i == 2) {
                        CtrlCardPresenter.this.mIsSpeechCMD = true;
                    } else {
                        if (i != 3) {
                        }
                    }
                } else if (CtrlCardPresenter.this.mCardProvider != null) {
                    int cardType = msg.arg1;
                    float value = ((Float) msg.obj).floatValue();
                    Logger.d(CtrlCardPresenter.TAG, "send speech command: type = " + cardType + " value = " + value);
                    CtrlCardPresenter.this.mCardProvider.passBack2SpeechValue(cardType, value);
                }
            }
        };
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void registerListener() {
        ICtrlCardProvider iCtrlCardProvider = this.mCardProvider;
        if (iCtrlCardProvider != null) {
            iCtrlCardProvider.register();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void getCurrentValue() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setATLBrightness(final int atlBrightValue) {
        UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.2
            @Override // java.lang.Runnable
            public void run() {
                if (CtrlCardPresenter.this.mCurrentType == 11 && CtrlCardPresenter.this.mLogicView != null) {
                    if (CtrlCardPresenter.this.mIsSpeechCMD) {
                        CtrlCardPresenter.this.mLogicView.refreshView(CtrlCardPresenter.this.notifyData(atlBrightValue));
                    }
                    CtrlCardPresenter.this.mIsSpeechCMD = true;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int notifyData(float value) {
        int progressValue = CtrlCardUtils.getInstance().unit2ProgressValue(this.mCurrentType, value);
        Log.d(TAG, "notifyData() called with: value = [" + value + "]unitValue" + progressValue);
        CtrlCardContent ctrlCardContent = this.mCtrlCardContent;
        if (ctrlCardContent != null) {
            ctrlCardContent.setData(value + "");
        }
        return progressValue;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void playBgAnimation(int type, float from, float to) {
        ILogicCtrlView iLogicCtrlView = this.mLogicView;
        if (iLogicCtrlView != null) {
            iLogicCtrlView.playBgAnimation(type, from, to);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public float getDriverTemp() {
        return this.mCardProvider.getCurrentModeValue();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setDriverTemp(final float lastTempValue, final float tempValue) {
        Log.d(TAG, "setDriverTemp() called with: value = [" + tempValue + NavigationBarInflaterView.SIZE_MOD_END);
        UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.3
            @Override // java.lang.Runnable
            public void run() {
                if (CtrlCardPresenter.this.mLogicView != null) {
                    if (CtrlCardPresenter.this.mIsSpeechCMD) {
                        CtrlCardPresenter.this.mLogicView.refreshView(CtrlCardPresenter.this.notifyData(tempValue));
                        CtrlCardPresenter.this.mLogicView.playBgAnimation(CtrlCardPresenter.this.mCurrentType, lastTempValue, tempValue);
                    }
                    CtrlCardPresenter.this.mIsSpeechCMD = true;
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void unRegisterListener() {
        Log.d(TAG, "unRegisterListener() called");
        ICtrlCardProvider iCtrlCardProvider = this.mCardProvider;
        if (iCtrlCardProvider != null) {
            iCtrlCardProvider.unRegister();
        }
        this.mCardProvider = null;
        this.mCtrlCardContent = null;
        this.mCurrentType = -1;
        this.mCtrlGroupType = -1;
        this.mLastUiniValue = -1.0f;
        this.mLogicView = null;
        this.mProgressVByCtrlKey = 0;
        this.mIsSpeechCMD = true;
        this.mAmbientLightingColor = null;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setSeatHeatLevel(final int level) {
        Logger.d(TAG, "setSeatHeatLevel : level = " + level + " mIsSpeechCMD = " + this.mIsSpeechCMD);
        UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.-$$Lambda$CtrlCardPresenter$FC-kitEiRDR0DPnurn6ZhruPrX0
            @Override // java.lang.Runnable
            public final void run() {
                CtrlCardPresenter.this.lambda$setSeatHeatLevel$0$CtrlCardPresenter(level);
            }
        });
    }

    public /* synthetic */ void lambda$setSeatHeatLevel$0$CtrlCardPresenter(int level) {
        ILogicCtrlView iLogicCtrlView = this.mLogicView;
        if (iLogicCtrlView != null) {
            if (this.mIsSpeechCMD) {
                iLogicCtrlView.refreshView(notifyData(level));
            }
            this.mIsSpeechCMD = true;
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setSeatVentLevel(final int level) {
        UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.-$$Lambda$CtrlCardPresenter$caR6UNGiMrUv0eBKFm1iyR_QHoU
            @Override // java.lang.Runnable
            public final void run() {
                CtrlCardPresenter.this.lambda$setSeatVentLevel$1$CtrlCardPresenter(level);
            }
        });
    }

    public /* synthetic */ void lambda$setSeatVentLevel$1$CtrlCardPresenter(int level) {
        ILogicCtrlView iLogicCtrlView = this.mLogicView;
        if (iLogicCtrlView != null) {
            if (this.mIsSpeechCMD) {
                iLogicCtrlView.refreshView(notifyData(level));
            }
            this.mIsSpeechCMD = true;
        }
    }

    public void onCreate(int type, CardValue cardValue) {
        this.mCtrlCardContent = this.mCardProvider.getCardContent(type, cardValue);
        this.mCurrentType = this.mCtrlCardContent.getType();
        if (this.mCurrentType == 12) {
            this.mAmbientLightingColor = ColorUtils.getInstance().createAmbientLightingColor();
        }
        Logger.d(TAG, "onCreate groupType=" + type + "<<<mCurrentType =" + this.mCurrentType);
        ILogicCtrlView iLogicCtrlView = this.mLogicView;
        if (iLogicCtrlView != null) {
            iLogicCtrlView.setViewStub(this.mCurrentType, this.mCtrlCardContent);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void onDestroy() {
        Handler handler = this.mUiHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setAtlColor(final int atlcolor) {
        Log.d(TAG, "setAtlColor() called with: value = [" + atlcolor + NavigationBarInflaterView.SIZE_MOD_END);
        if (this.mCurrentType == 12) {
            UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.4
                @Override // java.lang.Runnable
                public void run() {
                    if (CtrlCardPresenter.this.mIsSpeechCMD && CtrlCardPresenter.this.mLogicView != null) {
                        CtrlCardPresenter.this.mLogicView.refreshView(CtrlCardPresenter.this.notifyData(atlcolor));
                    }
                    CtrlCardPresenter.this.mIsSpeechCMD = true;
                }
            });
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void setWindLevel(final float lastWindLevel, final float windLevel) {
        Log.d(TAG, "setWindLevel() called with: windLevel = [" + windLevel + NavigationBarInflaterView.SIZE_MOD_END);
        UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.5
            @Override // java.lang.Runnable
            public void run() {
                int i = CtrlCardPresenter.this.mCurrentType;
                if ((i == 2 || i == 3 || i == 4) && CtrlCardPresenter.this.mLogicView != null) {
                    if (CtrlCardPresenter.this.mIsSpeechCMD) {
                        CtrlCardPresenter.this.mLogicView.refreshView(CtrlCardPresenter.this.notifyData(windLevel));
                        CtrlCardPresenter.this.mLogicView.playBgAnimation(CtrlCardPresenter.this.mCurrentType, lastWindLevel, windLevel);
                    }
                    CtrlCardPresenter.this.mIsSpeechCMD = true;
                }
            }
        });
    }

    public static boolean isCtrlKey(int keyCode) {
        return keyCode == 1083 || keyCode == 1084;
    }

    public void refreshByCtrlKeyEvent(boolean isUp) {
        this.mUiHandler.removeMessages(3);
        Message msg = this.mUiHandler.obtainMessage(3);
        this.mUiHandler.sendMessageDelayed(msg, 1000L);
        this.mIsSpeechCMD = false;
        CtrlCardRefreshObservable.getInstance().notifyObservers();
        ICtrlCardProvider iCtrlCardProvider = this.mCardProvider;
        if (iCtrlCardProvider != null) {
            if (this.mCtrlCardContent == null) {
                this.mCtrlCardContent = iCtrlCardProvider.getCardContent(this.mCtrlGroupType, null);
            }
            String data = this.mCtrlCardContent.getData();
            int slip = 1;
            if (this.mCtrlCardContent.getType() == 11) {
                slip = 10;
            }
            if (isUp) {
                this.mProgressVByCtrlKey = CtrlCardUtils.getInstance().unit2ProgressValue(this.mCurrentType, Float.parseFloat(data) + slip);
            } else {
                this.mProgressVByCtrlKey = CtrlCardUtils.getInstance().unit2ProgressValue(this.mCurrentType, Float.parseFloat(data) - slip);
            }
            int i = this.mProgressVByCtrlKey;
            if (i < 0) {
                this.mProgressVByCtrlKey = 0;
            } else if (i > CtrlCardUtils.getInstance().getProgressProperty(this.mCurrentType).sum()) {
                this.mProgressVByCtrlKey = CtrlCardUtils.getInstance().getProgressProperty(this.mCurrentType).sum();
            }
            float current = CtrlCardUtils.getInstance().progress2UnitValue(this.mCurrentType, this.mProgressVByCtrlKey);
            Log.d(TAG, "refreshByCtrlKeyEvent() called with: isUp = [" + isUp + "] mCurrentType=" + this.mCurrentType + " mProgressVByCtrlKey=" + this.mProgressVByCtrlKey + " data=" + data + " current=" + current);
            if (current == Float.parseFloat(data)) {
                Log.e(TAG, "refreshByCtrlKeyEvent: value no change");
                return;
            }
            CtrlCardContent ctrlCardContent = this.mCtrlCardContent;
            ctrlCardContent.setData(current + "");
            final int finalProgressValue = this.mProgressVByCtrlKey;
            UiHandlerUtil.getInstance().post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter.6
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(CtrlCardPresenter.TAG, "refreshByCtrlKeyEvent refreshView finalProgressValue=" + finalProgressValue);
                    if (CtrlCardPresenter.this.mLogicView != null) {
                        CtrlCardPresenter.this.mLogicView.stopProgressAnim();
                        CtrlCardPresenter.this.mLogicView.refreshView(finalProgressValue);
                    }
                }
            });
            if (needTriggerImmediately()) {
                this.mCardProvider.passBack2SpeechValue(this.mCurrentType, current);
            } else {
                delayToSendSpeechCommand(current);
            }
        }
    }

    public void delayToSendSpeechCommand(float current) {
        Message msg = this.mUiHandler.obtainMessage();
        msg.what = 1;
        msg.arg1 = this.mCurrentType;
        msg.obj = Float.valueOf(current);
        this.mUiHandler.removeMessages(1);
        this.mUiHandler.sendMessageDelayed(msg, 800L);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView.OnColorPickerChangeListener
    public int onColorChanged(CtrlProgressView picker, int color) {
        int i = this.mCurrentType;
        if (i == 8 || i == 9 || i == 10 || i == 12) {
            if (this.mCurrentType == 12) {
                int progress = picker.getProgress();
                int unitValue = (int) CtrlCardUtils.getInstance().progress2UnitValue(this.mCurrentType, progress);
                Log.d(TAG, "onColorChanged: lighting color progress=" + progress + "unitvalue=" + unitValue);
                int[] iArr = this.mAmbientLightingColor;
                if (iArr != null) {
                    color = iArr[unitValue - 1];
                }
            }
            ILogicCtrlView iLogicCtrlView = this.mLogicView;
            if (iLogicCtrlView != null) {
                iLogicCtrlView.refreshBgView(color);
            }
        }
        return color;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView.OnColorPickerChangeListener
    public void onProgress(int progress) {
        String strUnitValue;
        this.mIsSpeechCMD = false;
        float currUnitValue = CtrlCardUtils.getInstance().progress2UnitValue(this.mCurrentType, progress);
        Log.d(TAG, "onProgress() called with: type = " + this.mCurrentType + " mLastUiniValue = [" + this.mLastUiniValue + "]currUnitValue=" + currUnitValue + "progress=" + progress);
        if (this.mLastUiniValue != currUnitValue) {
            ILogicCtrlView iLogicCtrlView = this.mLogicView;
            if (iLogicCtrlView != null) {
                iLogicCtrlView.stopProgressAnim();
                this.mLogicView.updateRadialValue((int) currUnitValue);
                this.mLogicView.updateSeatIcon(((int) currUnitValue) * 25);
                this.mLogicView.updateOffText((int) currUnitValue);
            }
            int intUnitV = (int) currUnitValue;
            if (intUnitV == currUnitValue) {
                strUnitValue = intUnitV + "";
            } else {
                strUnitValue = currUnitValue + "";
            }
            ILogicCtrlView iLogicCtrlView2 = this.mLogicView;
            if (iLogicCtrlView2 != null) {
                iLogicCtrlView2.updateNumTv(strUnitValue);
            }
            if (this.mCardProvider != null) {
                if (needTriggerImmediately()) {
                    this.mCardProvider.passBack2SpeechValue(this.mCurrentType, currUnitValue);
                } else {
                    delayToSendSpeechCommand(currUnitValue);
                }
            }
            this.mLastUiniValue = currUnitValue;
        }
    }

    private boolean needTriggerImmediately() {
        int i = this.mCurrentType;
        if (i == 16 || i == 17) {
            return true;
        }
        return false;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView.OnColorPickerChangeListener
    public void onStartTrackingTouch(CtrlProgressView picker) {
        this.mUiHandler.removeMessages(2);
        this.mUiHandler.removeMessages(3);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.CtrlProgressView.OnColorPickerChangeListener
    public void onStopTrackingTouch(CtrlProgressView picker) {
        Message msg = this.mUiHandler.obtainMessage(2);
        this.mUiHandler.sendMessageDelayed(msg, 1000L);
        Message msg2 = this.mUiHandler.obtainMessage(3);
        this.mUiHandler.sendMessageDelayed(msg2, 1000L);
        if (this.mCardProvider != null && this.mLastUiniValue != -1.0f) {
            Log.d(TAG, "onStopTrackingTouch() called with: mCurrentType = [" + this.mCurrentType + "] mLastUiniValue=" + this.mLastUiniValue);
            this.mCardProvider.passBack2SpeechValue(this.mCurrentType, this.mLastUiniValue);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void onScreenBrightnessChanged(int value) {
        Logger.d(TAG, "onScreenBrightnessChanged : " + value + ",mCurrentType:" + this.mCurrentType + " mIsSpeechCMD = " + this.mIsSpeechCMD);
        if (this.mCurrentType == 16 && this.mIsSpeechCMD) {
            refreshView(value);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public boolean isSpeechCMD() {
        return this.mIsSpeechCMD;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void updateCtrlCardContent(CtrlCardContent ctrlCardContent) {
        Logger.d(TAG, "updateCtrlCardContent");
        this.mCtrlCardContent = ctrlCardContent;
        this.mLogicView.updateCtrlCardContent(ctrlCardContent);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.carcontrol.ICtrlCardPresenter
    public void onIcmBrightnessChanged(int value) {
        Logger.d(TAG, "onIcmBrightnessChanged : " + value + " ,mCurrentType:" + this.mCurrentType);
        if (this.mCurrentType == 17 && this.mIsSpeechCMD) {
            refreshView(value);
        }
    }

    public void refreshView(int value) {
        ILogicCtrlView iLogicCtrlView = this.mLogicView;
        if (iLogicCtrlView != null) {
            if (this.mIsSpeechCMD) {
                iLogicCtrlView.refreshView(notifyData(value));
            }
            this.mIsSpeechCMD = true;
        }
    }
}
