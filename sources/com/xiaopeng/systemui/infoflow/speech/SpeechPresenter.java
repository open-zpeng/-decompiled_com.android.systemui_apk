package com.xiaopeng.systemui.infoflow.speech;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.jarvisproto.AsrEvent;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
import com.xiaopeng.speech.protocol.node.tts.TtsEchoValue;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.controller.ActivityController;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.helper.AIAvatarViewServiceHelper;
import com.xiaopeng.systemui.infoflow.helper.AsrHelper;
import com.xiaopeng.systemui.infoflow.message.listener.XNotificationListener;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.statusbar.QuickMenuGuide;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class SpeechPresenter implements ISpeechPresenter, AsrHelper.OnAvatarSceneChangeListener, ISpeechAvatar, ISpeechContext {
    private static final String CONTROL_PENDING_ROUTE = "command://navi.address.pending.route";
    private static final String CONTROL_START = "command://navi.control.start";
    private static final String CONTROL_WAYPOINT_START = "command://navi.control.waypoint.start";
    private static final String HINT_TYPE_TEXT = "text";
    private static final String HINT_TYPE_TIPS = "tips";
    private static final String KEY_HINT_DATA = "data";
    private static final String KEY_HINT_RELATE_LIST = "relateList";
    private static final String KEY_HINT_RELATE_TEXT = "relateText";
    private static final String KEY_HINT_SHOW_TEXT = "showText";
    private static final String KEY_HINT_TYPE = "type";
    private static final int LAST_HINT_DISPLAY_TIME = 5000;
    private static final int MSG_HIDE_HINT = 2;
    private static final int MSG_SHOW_HINT = 1;
    private static final int TEXT_DISPLAY_INTERVAL_TIME = 5000;
    private static final int TIPS_DISPLAY_INTERVAL_TIME = 10000;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_TIPS = 2;
    public static final int VOICE_LOC_BACK_LEFT = 3;
    public static final int VOICE_LOC_BACK_MID = 999;
    public static final int VOICE_LOC_BACK_RIGHT = 4;
    public static final int VOICE_LOC_DOA = 0;
    public static final int VOICE_LOC_LEFT = 1;
    public static final int VOICE_LOC_RIGHT = 2;
    public static final int VOICE_REGION_TYPE_DOA = 1;
    public static final int VOICE_REGION_TYPE_DOA_OVERSEA = 9;
    public static final int VOICE_REGION_TYPE_DOUBLE = 0;
    private static final int VOLUME_INPUT_JUDGE_VALUE = 5;
    private String mCurrentWidgetId;
    private String mCurrentWidgetType;
    protected ISpeechView mSpeechView;
    protected SpeechViewContainer mSpeechViewContainer;
    private int mType;
    private VoiceWavePresenter mVoiceWavePresenter;
    private static final String TAG = SpeechPresenter.class.getSimpleName();
    private static int sCurrentVolume = 0;
    private static volatile int sCurrentVoiceLoc = 1;
    public static boolean mIsPanelVisible = false;
    private boolean mIsListening = false;
    private int mSpeechViewType = 2;
    private String mCurrentTtsId = null;
    protected boolean mIsInterrupted = false;
    protected boolean isDialogStart = false;
    protected boolean mIsDialogEndByRouteCompute = false;
    private boolean mInSpeechMode = false;
    private int mFocusedPosition = -1;
    private long mSpeechCardDisplayTime = 0;
    private Queue<HintInfo> mHintInfoQueue = new LinkedList();
    private Handler mUIHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 1) {
                if (i == 2) {
                    SpeechPresenter.this.hideHint();
                    return;
                }
                return;
            }
            HintInfo hintInfo = (HintInfo) msg.obj;
            if (hintInfo != null) {
                SpeechPresenter.this.showHint(hintInfo.getGuideText(), hintInfo.getHintText());
                SpeechPresenter.this.showNextHint(hintInfo.mType == 2 ? 10000 : 5000);
            }
        }
    };
    protected Context mContext = ContextUtils.getContext();
    private SpeechManager mSpeechManager = SpeechManager.instance();

    /* JADX INFO: Access modifiers changed from: private */
    public void showHint(String guideText, String hintText) {
        if (this.mSpeechView != null) {
            String str = TAG;
            Logger.d(str, "onTipsListeningShow guideText " + guideText + ", hintText : " + hintText);
            this.mSpeechView.showHint(guideText, hintText);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class HintInfo {
        private String mGuideText;
        private String mHintText;
        private int mType;

        private HintInfo() {
        }

        public String getGuideText() {
            return this.mGuideText;
        }

        public void setGuideText(String guideText) {
            this.mGuideText = guideText;
        }

        public String getHintText() {
            return this.mHintText;
        }

        public void setHintText(String hintText) {
            this.mHintText = hintText;
        }

        public int getType() {
            return this.mType;
        }

        public void setType(int type) {
            this.mType = type;
        }
    }

    public void onTtsEcho(TtsEchoValue data) {
        ISpeechView iSpeechView = this.mSpeechView;
        if (iSpeechView != null) {
            iSpeechView.onTtsEcho(data);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class SingletonHolder {
        private static final SpeechPresenter sInstance;

        private SingletonHolder() {
        }

        static {
            sInstance = OrientationUtil.isLandscapeScreen(ContextUtils.getContext()) ? new LandscapeSpeechPresenter() : new VerticalSpeechPresenter();
        }
    }

    public void setupWithContainer(SpeechViewContainer speechViewContainer) {
        this.mSpeechViewContainer = speechViewContainer;
    }

    public void setupWithVoiceWavePresenter(VoiceWavePresenter voiceWavePresenter) {
        this.mVoiceWavePresenter = voiceWavePresenter;
    }

    public void setSpeechView(ISpeechView speechView) {
        this.mSpeechView = speechView;
    }

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper.OnAvatarSceneChangeListener
    public void onReplaceAvatarScene() {
        Logger.d(TAG, "onReplaceAvatarScene");
    }

    @Override // com.xiaopeng.systemui.infoflow.helper.AsrHelper.OnAvatarSceneChangeListener
    public void onRestoreAvatarScene() {
        Logger.d(TAG, "onRestoreAvatarScene");
    }

    public static boolean isPanelVisible() {
        return mIsPanelVisible;
    }

    public String getWidgetInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", this.mCurrentWidgetId);
            jsonObject.put("type", this.mCurrentWidgetType);
            jsonObject.put("focusIndex", this.mFocusedPosition);
            if (this.mSpeechCardDisplayTime == 0) {
                jsonObject.put("showTime", 0);
            } else {
                jsonObject.put("showTime", System.currentTimeMillis() - this.mSpeechCardDisplayTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static SpeechPresenter getInstance() {
        return SingletonHolder.sInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SpeechPresenter() {
        AsrHelper.getInstance().setOnAvatarSceneChangeListener(this);
    }

    public boolean isInSpeechMode() {
        return this.mInSpeechMode;
    }

    public static void setVolume(int volume) {
        String str = TAG;
        Logger.d(str, "setVolume : " + volume);
        sCurrentVolume = volume;
    }

    public void setSpeechViewType(int type) {
        String str = TAG;
        Logger.i(str, "setSpeechViewType : " + type);
        this.mSpeechViewType = type;
    }

    public void showVoiceWaveAnim(int regionType, int waveType) {
        ISpeechView iSpeechView = this.mSpeechView;
        if (iSpeechView != null) {
            iSpeechView.showVoiceWaveAnim(regionType, waveType, sCurrentVolume);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void registerSpeechListener() {
        this.mSpeechManager.getSpeechAvatarManager().addCallback(this);
        this.mSpeechManager.getSpeechContextManager().addCallback(this);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void unregisterSpeechListener() {
        this.mSpeechManager.getSpeechAvatarManager().removeCallback(this);
        this.mSpeechManager.getSpeechContextManager().removeCallback(this);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onTtsStart(String ttsId) {
        JSONObject jsonObject = transferToJson(ttsId);
        if (jsonObject != null) {
            this.mCurrentTtsId = jsonObject.optString("nlgId");
            sCurrentVoiceLoc = jsonObject.optInt("soundArea");
        } else {
            this.mCurrentTtsId = ttsId;
        }
        AsrHelper.getInstance().updateAsrStatus(4, this.mIsListening);
        showVoiceWaveAnim(sCurrentVoiceLoc, 2);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onTtsEnd(String ttsId) {
        String currentTtsId;
        JSONObject jsonObject = transferToJson(ttsId);
        boolean ignoreUi = false;
        if (jsonObject != null) {
            currentTtsId = jsonObject.optString("nlgId", null);
            if (jsonObject.has("soundArea")) {
                sCurrentVoiceLoc = jsonObject.optInt("soundArea");
            }
            ignoreUi = jsonObject.optBoolean("ignoreUi", false);
        } else {
            currentTtsId = ttsId;
        }
        AsrHelper.getInstance().updateAsrStatus(5, this.mIsListening);
        boolean needClearAsr = false;
        if ((TextUtils.isEmpty(currentTtsId) || currentTtsId.equals(this.mCurrentTtsId)) && !ignoreUi) {
            needClearAsr = true;
        }
        String str = TAG;
        Logger.i(str, "onTtsEnd : needClearAsr = " + needClearAsr + " mIsListening = " + this.mIsListening + " currentTtsId = " + currentTtsId + " mCurrentTtsId = " + this.mCurrentTtsId + ", ignoreUi = " + ignoreUi);
        if (needClearAsr) {
            AsrHelper.getInstance().clearAsr();
            showVoiceWaveAnim(sCurrentVoiceLoc, 3);
        }
    }

    public void onDialogExit(int soundArea) {
        if (this.isDialogStart) {
            showVoiceWaveAnim(soundArea, 3);
        }
    }

    private JSONObject transferToJson(String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(str);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onListeningStatusChanged(boolean listeningStatus) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onFocusChanged(int focusedPosition) {
        this.mFocusedPosition = focusedPosition;
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x008a  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00e0 A[Catch: Exception -> 0x0105, TryCatch #1 {Exception -> 0x0105, blocks: (B:10:0x0030, B:12:0x003a, B:56:0x00f7, B:15:0x004b, B:17:0x0058, B:18:0x005e, B:55:0x00f2, B:35:0x0091, B:37:0x00a6, B:38:0x00ad, B:40:0x00b3, B:42:0x00b9, B:44:0x00bf, B:46:0x00c6, B:48:0x00cc, B:53:0x00e0, B:23:0x0071, B:26:0x007b, B:57:0x0101), top: B:67:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00f2 A[Catch: Exception -> 0x0105, TryCatch #1 {Exception -> 0x0105, blocks: (B:10:0x0030, B:12:0x003a, B:56:0x00f7, B:15:0x004b, B:17:0x0058, B:18:0x005e, B:55:0x00f2, B:35:0x0091, B:37:0x00a6, B:38:0x00ad, B:40:0x00b3, B:42:0x00b9, B:44:0x00bf, B:46:0x00c6, B:48:0x00cc, B:53:0x00e0, B:23:0x0071, B:26:0x007b, B:57:0x0101), top: B:67:0x0030 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x00f7 A[SYNTHETIC] */
    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onTipsListeningShow(java.lang.String r20) {
        /*
            Method dump skipped, instructions count: 270
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.onTipsListeningShow(java.lang.String):void");
    }

    private void showHintQueue() {
        HintInfo hintInfo;
        if (this.mHintInfoQueue.size() > 0 && (hintInfo = this.mHintInfoQueue.remove()) != null) {
            Message msg = this.mUIHandler.obtainMessage(1);
            msg.obj = hintInfo;
            this.mUIHandler.sendMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNextHint(int delayTime) {
        if (this.mHintInfoQueue.size() > 0) {
            HintInfo hintInfo = this.mHintInfoQueue.remove();
            Message msg = this.mUIHandler.obtainMessage(1);
            if (hintInfo != null) {
                msg.obj = hintInfo;
                this.mUIHandler.sendMessageDelayed(msg, delayTime);
                return;
            }
            return;
        }
        this.mUIHandler.sendMessageDelayed(this.mUIHandler.obtainMessage(2), 5000L);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onTipsListeningStop() {
        this.mUIHandler.removeMessages(1);
        this.mUIHandler.removeMessages(2);
        hideHint();
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public void onSoundAreaStatusChanged(SoundAreaStatus status) {
        ISpeechView iSpeechView = this.mSpeechView;
        if (iSpeechView != null) {
            iSpeechView.onSoundAreaStatus(status);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ISpeechPresenter
    public ArrayList<SoundAreaStatus> getSoundAreaStatus() {
        int[] areas = {1, 2, 3, 4};
        ArrayList<SoundAreaStatus> statuses = new ArrayList<>(areas.length);
        for (int area : areas) {
            String result = SpeechClient.instance().getWakeupEngine().getSoundAreaStatus(area);
            SoundAreaStatus status = SoundAreaStatus.fromJson(result);
            statuses.add(status);
        }
        return statuses;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideHint() {
        ISpeechView iSpeechView = this.mSpeechView;
        if (iSpeechView != null) {
            iSpeechView.hideHint();
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onInputText(final int sourceArea, final String text, boolean isEof, final boolean isInterrupted, final boolean invalid) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.2
            @Override // java.lang.Runnable
            public void run() {
                String str = SpeechPresenter.TAG;
                Logger.d(str, "onInputText : " + sourceArea + " , " + text);
                SpeechPresenter.this.onTipsListeningStop();
                SpeechPresenter speechPresenter = SpeechPresenter.this;
                speechPresenter.mIsInterrupted = isInterrupted;
                if (sourceArea != 0) {
                    speechPresenter.mVoiceWavePresenter.setAsrLoc(sourceArea);
                }
                AsrHelper.getInstance().updateShowNormalText(sourceArea, text, invalid);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onOutputText(String text) {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onShowWidget(SpeechWidget widget) {
        Logger.d(TAG, "onShowWidget");
        String widgetId = getWidgetId(widget);
        String str = TAG;
        Logger.d(str, "onShowWidget : widgetId = " + widgetId + " mCurrentWidgetId = " + this.mCurrentWidgetId);
        if (!TextUtils.isEmpty(widgetId) && widgetId.equals(this.mCurrentWidgetId)) {
            return;
        }
        this.mCurrentWidgetId = widgetId;
        this.mCurrentWidgetType = widget.getExtraType();
        String str2 = TAG;
        Logger.d(str2, "onShowWidget : mCurrentWidgetType = " + this.mCurrentWidgetType);
        checkBackToInfoflow();
        if (!this.isDialogStart && !this.mIsDialogEndByRouteCompute) {
            return;
        }
        enterSpeechMode(this.mSpeechViewType);
        if (this.mSpeechView != null) {
            this.mSpeechCardDisplayTime = System.currentTimeMillis();
            this.mSpeechView.showListWidget(widget);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListFocus(final int index) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.3
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListFocus(index);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListSelect(final int index) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.4
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListSelect(index);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetText(String text) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.5
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter speechPresenter = SpeechPresenter.this;
                speechPresenter.enterSpeechMode(speechPresenter.mType);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetRecommend(final String text) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.6
            @Override // java.lang.Runnable
            public void run() {
                AsrHelper.getInstance().updateShowNormalText(text);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onBugReportBegin() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.7
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.d(SpeechPresenter.TAG, "onBugReportBegin");
                if (SpeechPresenter.this.mSpeechView != null) {
                    SpeechPresenter.this.mSpeechView.onBugReportBegin();
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onBugReportEnd() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.8
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.d(SpeechPresenter.TAG, "onBugReportEnd");
                if (SpeechPresenter.this.mSpeechView != null) {
                    SpeechPresenter.this.mSpeechView.onBugReportEnd();
                }
            }
        });
    }

    protected void showRecommendView(String data) {
        if (!this.mInSpeechMode) {
            backToRecommendMode();
            if (!TextUtils.isEmpty(data)) {
                XNotificationListener.getInstance(this.mContext).onEnterRecommendMode(data);
            }
        }
    }

    public void onSayWelcome(String data) {
        showRecommendView(data);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onAsrEvent(int event) {
        handleAsrEvent(event);
    }

    private void handleAsrEvent(int event) {
        if (SpeechClient.instance().getSpeechState().isDMStarted()) {
            updateVoiceLoc((WakeupReason) null);
            if (event == 1) {
                AsrHelper.getInstance().updateAsrStatus(8, this.mIsListening);
            } else if (event == 2) {
                this.mCurrentTtsId = null;
                Logger.d(TAG, "onAsrEvent : mCurrentTtsId = null");
            }
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetCancel(final String widgetId, final String cancelWay) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.9
            @Override // java.lang.Runnable
            public void run() {
                String str = SpeechPresenter.TAG;
                Logger.i(str, "onWidgetCancel : widgetId = " + widgetId + " mCurrentWidgetId = " + SpeechPresenter.this.mCurrentWidgetId);
                if (TextUtils.isEmpty(widgetId) || (!TextUtils.isEmpty(widgetId) && SpeechPresenter.this.mCurrentWidgetId != null && widgetId.equals(SpeechPresenter.this.mCurrentWidgetId))) {
                    SpeechPresenter.this.mCurrentWidgetId = null;
                    SpeechPresenter.this.mCurrentWidgetType = null;
                    SpeechPresenter.this.mSpeechCardDisplayTime = 0L;
                    if (TextUtils.isEmpty(cancelWay)) {
                        return;
                    }
                    SpeechPresenter.this.backToRecommendMode();
                    if (SpeechPresenter.this.mSpeechViewContainer != null) {
                        SpeechPresenter.this.mSpeechViewContainer.notifyAvatarAction(3);
                    }
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListCancelFocus(final int index) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.10
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListCancelFocus(index);
            }
        });
    }

    public void onExitRecommendCard() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.11
            @Override // java.lang.Runnable
            public void run() {
                XNotificationListener.getInstance(SpeechPresenter.this.mContext).onExitRecommendMode();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListExpend() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.12
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListExpand();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListFold() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.13
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListFold();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onWidgetListStopCountdown() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.14
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechView.onWidgetListStopCountdown();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechContext
    public void onAsrEvent(AsrEvent event) {
        if (event == null) {
            Logger.d(TAG, "onAsrEvent is null");
            return;
        }
        String str = TAG;
        Logger.d(str, "onAsrEvent : " + event.mEvent + "," + event.soundArea);
        sCurrentVoiceLoc = event.soundArea;
        handleAsrEvent(event.mEvent);
    }

    private String getWidgetId(SpeechWidget widget) {
        String widgetId = widget.getContent("widgetId");
        if (TextUtils.isEmpty(widgetId)) {
            return widget.getExtra("widgetId");
        }
        return widgetId;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onDialogStart(int type) {
        showAsrView(true);
        this.isDialogStart = true;
        this.mType = type;
        this.mSpeechViewContainer.onDialogStart();
        backToRecommendMode();
        this.mIsDialogEndByRouteCompute = false;
        QuickMenuGuide.getInstance().destroy();
        SpeechViewContainer speechViewContainer = this.mSpeechViewContainer;
        if (speechViewContainer != null) {
            speechViewContainer.notifyAvatarAction(3);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onDialogEnd(final DialogEndReason endReason) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.15
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.resetAsrStatus();
                SpeechPresenter.this.showAsrView(false);
                SpeechPresenter speechPresenter = SpeechPresenter.this;
                speechPresenter.isDialogStart = false;
                speechPresenter.mIsDialogEndByRouteCompute = SpeechPresenter.isDialogEndByRouteCompute(endReason);
                String str = SpeechPresenter.TAG;
                Logger.d(str, "onDialogEnd : mIsDialogEndByRouteCompute = " + SpeechPresenter.this.mIsDialogEndByRouteCompute);
                SpeechPresenter.this.mSpeechViewContainer.onDialogEnd(endReason);
                SpeechPresenter.this.stopVoiceWaveAnim();
                XNotificationListener.getInstance(SpeechPresenter.this.mContext).onExitRecommendMode();
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onSilence() {
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onVadEnd() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.16
            @Override // java.lang.Runnable
            public void run() {
                if (SpeechPresenter.this.mSpeechViewContainer != null) {
                    SpeechPresenter.this.mSpeechViewContainer.onVadEnd();
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onVadBegin() {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.17
            @Override // java.lang.Runnable
            public void run() {
                if (SpeechPresenter.this.mSpeechViewContainer != null) {
                    SpeechPresenter.this.mSpeechViewContainer.onVadBegin();
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onDialogWait(final DMWait reason) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.18
            @Override // java.lang.Runnable
            public void run() {
                DMWait dMWait = reason;
                if (dMWait != null && !TextUtils.isEmpty(dMWait.reason)) {
                    String str = SpeechPresenter.TAG;
                    Logger.d(str, "onDialogWait : reason = " + reason.reason);
                    if (reason.reason.equals("enter") || reason.reason.equals("update") || reason.reason.equals(DMWait.STATUS_INVALID_SPEECH)) {
                        if (reason.reason.equals("enter")) {
                            SpeechPresenter.this.mIsListening = true;
                            SpeechPresenter.this.updateAsrListeningStatus();
                        } else if (reason.reason.equals(DMWait.STATUS_INVALID_SPEECH)) {
                            SpeechPresenter.this.updateAsrStatus(6);
                        } else {
                            SpeechPresenter.this.updateAsrStatus(0);
                        }
                    } else if (reason.reason.equals("timeout")) {
                        SpeechPresenter.this.updateAsrStatus(3);
                    } else if (reason.reason.equals(DMWait.STATUS_FEED_NLU)) {
                        SpeechPresenter.this.updateAsrStatus(1);
                    } else if (reason.reason.equals(DMWait.STATUS_END)) {
                        SpeechPresenter.this.mIsListening = false;
                        SpeechPresenter.this.updateAsrListeningStatus();
                    } else if (!reason.reason.equals(DMWait.STATUS_TTS_END)) {
                        SpeechPresenter.this.updateAsrStatus(0);
                    } else {
                        AsrHelper.getInstance().clearAsr();
                        String str2 = SpeechPresenter.TAG;
                        Logger.d(str2, "onDialogWait : ttsEnd mIsInterrupted = " + SpeechPresenter.this.mIsInterrupted);
                        SpeechPresenter.this.onListeningStatusChanged(true);
                        SpeechPresenter.this.showVoiceWaveAnim(SpeechPresenter.sCurrentVoiceLoc, 3);
                    }
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onAvatarWakerupDisable(String reason) {
        Logger.d(TAG, "onAvatarWakerupDisable");
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onAvatarWakerupEnable(String reason) {
        Logger.d(TAG, "onAvatarWakerupEnable");
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onWakeupStatusChanged(final int status, int type, final String info) {
        String str = TAG;
        Log.i(str, "onWakeupStatusChanged status:" + status + " &type:" + type + " &info:" + info);
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.19
            @Override // java.lang.Runnable
            public void run() {
                AIAvatarViewServiceHelper.instance().updateWakeupStatus(status, info);
            }
        });
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.ISpeechAvatar
    public void onVoiceLocChanged(final int voiceLoc) {
        sCurrentVoiceLoc = voiceLoc;
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.20
            @Override // java.lang.Runnable
            public void run() {
                SpeechPresenter.this.mSpeechViewContainer.showVoiceLoc(voiceLoc);
            }
        });
    }

    public static int getVoiceLoc() {
        return sCurrentVoiceLoc;
    }

    public void showAsrView(boolean b) {
        String str = TAG;
        Logger.d(str, "showAsrView : b = " + b);
        AsrHelper.getInstance().showAsrView(b);
    }

    public static boolean isDialogEndByRouteCompute(DialogEndReason endReason) {
        return (endReason == null || TextUtils.isEmpty(endReason.reason) || (!endReason.event.equals("command://navi.control.waypoint.start") && !endReason.event.equals("command://navi.control.start") && !endReason.event.equals("command://navi.address.pending.route"))) ? false : true;
    }

    public void stopVoiceWaveAnim() {
        Logger.d(TAG, "stopVoiceWaveAnim");
        VoiceWavePresenter voiceWavePresenter = this.mVoiceWavePresenter;
        if (voiceWavePresenter != null) {
            voiceWavePresenter.stopVoiceWaveAnim();
        }
    }

    public void enterSpeechMode(int type) {
        this.mInSpeechMode = true;
        this.mSpeechViewContainer.enterSpeechMode(type);
    }

    public void backToRecommendMode() {
        Logger.d(TAG, "backToRecommendMode");
        this.mInSpeechMode = false;
        this.mSpeechViewContainer.exitSpeechMode();
    }

    public void exitSpeechMode(int navigationModel) {
        String str = TAG;
        Logger.d(str, "exitSpeechMode : navigationModel = " + navigationModel + " isDialogStart = " + this.isDialogStart);
        this.mInSpeechMode = false;
        if (navigationModel == 0) {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.SpeechPresenter.21
                @Override // java.lang.Runnable
                public void run() {
                    Logger.v(SpeechPresenter.TAG, "onClick exit speech model");
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
            });
        }
    }

    public void updateVoiceLoc(WakeupReason wakeupReason) {
        int voiceLoc;
        int voiceLoc2;
        if (CarModelsManager.getFeature().isMultiplayerVoiceSupport() && wakeupReason == null) {
            return;
        }
        if (wakeupReason == null || wakeupReason.soundArea == 0) {
            int voiceMode = SpeechClient.instance().getSoundLockState().getMode();
            if (voiceMode == 1 || voiceMode == 9) {
                voiceLoc = 0;
            } else if (wakeupReason != null && 1 == wakeupReason.reason) {
                voiceLoc = sCurrentVoiceLoc;
            } else {
                voiceLoc = SpeechClient.instance().getSoundLockState().getDriveSoundLocation();
            }
            String str = TAG;
            Logger.d(str, "updateVoiceLoc : voiceMode = " + voiceMode);
            voiceLoc2 = voiceLoc;
        } else {
            voiceLoc2 = wakeupReason.soundArea;
        }
        String str2 = TAG;
        Logger.d(str2, "updateVoiceLoc : voiceLoc = " + voiceLoc2);
        updateVoiceLoc(voiceLoc2);
    }

    public void updateVoiceLoc(int voiceLoc) {
        onVoiceLocChanged(voiceLoc);
        String str = TAG;
        Logger.d(str, "updateVoiceLoc : voiceLoc = " + voiceLoc);
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetAsrStatus() {
        this.mIsListening = false;
        AsrHelper.getInstance().updateAsrListeningStatus(false, true);
        AsrHelper.getInstance().updateAsrStatus(0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAsrListeningStatus() {
        AsrHelper.getInstance().updateAsrListeningStatus(this.mIsListening, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAsrStatus(int asrStatus) {
        AsrHelper.getInstance().updateAsrStatus(asrStatus, this.mIsListening);
    }

    public void checkBackToInfoflow() {
        if (CarModelsManager.getConfig().isClosePanelForInfoflowSpeechSupport()) {
            ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.-$$Lambda$SpeechPresenter$YykaNV7Fn4vDp2OEeJLbK8e6tM8
                @Override // java.lang.Runnable
                public final void run() {
                    SpeechPresenter.lambda$checkBackToInfoflow$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$checkBackToInfoflow$0() {
        String str = TAG;
        Logger.d(str, "checkBackToInfoflow : mIsPanelVisible = " + mIsPanelVisible);
        if (mIsPanelVisible) {
            ComponentName cn = ActivityController.getCurrentComponent();
            if (cn != null && "com.xiaopeng.aiassistant".equals(cn.getPackageName())) {
                return;
            }
            PackageHelper.gotoHome(ContextUtils.getContext());
        }
    }

    public String getWidgetId() {
        return this.mCurrentWidgetId;
    }

    public String getWidgetType() {
        return this.mCurrentWidgetType;
    }
}
