package com.xiaopeng.systemui.infoflow.speech.core.speech.model;

import android.os.Bundle;
import android.text.TextUtils;
import com.android.internal.util.CollectionUtils;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.AsrEvent;
import com.xiaopeng.speech.protocol.SpeechModel;
import com.xiaopeng.speech.protocol.node.asr.AbsAsrListener;
import com.xiaopeng.speech.protocol.node.asr.AsrNode;
import com.xiaopeng.speech.protocol.node.context.AbsContextListener;
import com.xiaopeng.speech.protocol.node.context.ContextNode;
import com.xiaopeng.speech.protocol.query.context.ContextQuery;
import com.xiaopeng.speech.protocol.query.context.IContextCaller;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SearchWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.carconfig.CarModelsManager;
import com.xiaopeng.systemui.helper.PackageHelper;
import com.xiaopeng.systemui.infoflow.helper.ContextHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.SpeechPresenter;
import com.xiaopeng.systemui.infoflow.speech.core.speech.SpeechManager;
import com.xiaopeng.systemui.infoflow.speech.core.speech.behavior.SpeechContextManager;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class ContextModel extends SpeechModel {
    private static final String BUG_REPORT = "system_bugreport_open";
    public static final String CTRL_CARD_CANCEL_WAY_FORCE = "force";
    public static final String CTRL_CARD_CANCEL_WAY_SOFT = "soft";
    private static final String INTENT_NAME = "intentName";
    private static final String KEY_ERR_CODE = "errCode";
    public static final String NAME = "name";
    private static final String PACKAGE_NAME = "com.android.systemui";
    private static final String TAG = ContextModel.class.getSimpleName();
    public static final String WIDGET_BUG_REPORT = "bugReport";
    private String mAsrText = null;
    private boolean mIsEof = false;
    protected SpeechManager mSpeechManager;

    public ContextModel(SpeechManager speechManager) {
        this.mSpeechManager = speechManager;
        subscribe(ContextNode.class, new ContextListener());
        subscribe(AsrNode.class, new AsrListener());
        subscribe(ContextQuery.class, new ContextCaller());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void handleIntent(String intentName, JSONObject jsonObject) {
        char c;
        SpeechWidget speechWidget = new SpeechWidget("custom");
        switch (intentName.hashCode()) {
            case -1246319475:
                if (intentName.equals("个股行情查询")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 971539:
                if (intentName.equals("百科")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 827499880:
                if (intentName.equals("查询天气")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1391711121:
                if (intentName.equals(BUG_REPORT)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c != 0) {
            if (c == 1 || c == 2) {
                speechWidget.setExtra(jsonObject);
                getSpeechContextManager().onShowWidget(speechWidget);
                return;
            } else if (c == 3) {
                speechWidget.setExtra(jsonObject);
                getSpeechContextManager().onShowWidget(speechWidget);
                return;
            } else {
                return;
            }
        }
        int errCode = 1;
        if (jsonObject != null && jsonObject.has(KEY_ERR_CODE)) {
            try {
                errCode = jsonObject.getInt(KEY_ERR_CODE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (errCode == 0) {
            speechWidget.setExtra(jsonObject);
            getSpeechContextManager().onShowWidget(speechWidget);
        }
    }

    public SpeechContextManager getSpeechContextManager() {
        return this.mSpeechManager.getSpeechContextManager();
    }

    /* loaded from: classes24.dex */
    private class ContextCaller implements IContextCaller {
        private ContextCaller() {
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getWidgetListSize() {
            return ContextModel.this.getSpeechContextManager().getWidgetListSize();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getWidgetPageSize() {
            return ContextModel.this.getSpeechContextManager().getWidgetPageSize();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getWidgetCurrLocation() {
            return ContextModel.this.getSpeechContextManager().getWidgetCurrLocation();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public String getWidgetId() {
            return SpeechPresenter.getInstance().getWidgetId();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public String getWidgetType() {
            return SpeechPresenter.getInstance().getWidgetType();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public String getWidgetInfo() {
            return SpeechPresenter.getInstance().getWidgetInfo();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getInfoFlowOnePage() {
            return ContextModel.this.getSpeechContextManager().getInfoFlowOnePage();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getInfoFlowScrollToBottom() {
            return ContextModel.this.getSpeechContextManager().getInfoFlowScrollToBottom();
        }

        @Override // com.xiaopeng.speech.protocol.query.context.IContextCaller
        public int getInfoFlowScrollToTop() {
            return ContextModel.this.getSpeechContextManager().getInfoFlowScrollToTop();
        }
    }

    /* loaded from: classes24.dex */
    private class AsrListener extends AbsAsrListener {
        private AsrListener() {
        }

        @Override // com.xiaopeng.speech.protocol.node.asr.AbsAsrListener, com.xiaopeng.speech.protocol.node.asr.AsrListener
        public void onAsrEvent(final int event) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.AsrListener.1
                @Override // java.lang.Runnable
                public void run() {
                    String str = ContextModel.TAG;
                    Logger.d(str, "onAsrEvent1 : " + event);
                    ContextModel.this.getSpeechContextManager().onAsrEvent(event);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.asr.AsrListener
        public void onAsrEvent(final AsrEvent event) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.AsrListener.2
                @Override // java.lang.Runnable
                public void run() {
                    String str = ContextModel.TAG;
                    Logger.d(str, "onAsrEvent2 : " + event);
                    ContextModel.this.getSpeechContextManager().onAsrEvent(event);
                }
            });
        }
    }

    /* loaded from: classes24.dex */
    private class ContextListener extends AbsContextListener {
        private ContextListener() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isBugReport(JSONObject jsonObject) {
            if (jsonObject != null && jsonObject.has(ContextModel.INTENT_NAME)) {
                try {
                    String intentName = jsonObject.getString(ContextModel.INTENT_NAME);
                    if (intentName.equals(ContextModel.BUG_REPORT)) {
                        return true;
                    }
                    return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onInputText(String data) {
            boolean invalid;
            Logger.d(ContextModel.TAG, "onInputText data = " + data);
            try {
                JSONObject jo = new JSONObject(data);
                String txt = "";
                boolean isEof = jo.optBoolean("isEof");
                boolean isInterrupted = jo.optBoolean("isInterrupted");
                if (jo.has("text")) {
                    txt = jo.optString("text", "");
                } else if (jo.has("var")) {
                    txt = jo.optString("var", "");
                }
                if (!jo.has("invalid")) {
                    invalid = false;
                } else {
                    invalid = jo.optBoolean("invalid", false);
                }
                if (isEof && !TextUtils.isEmpty(txt)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(PackageHelper.EXTRA_ASR_CONTENT, txt);
                    PackageHelper.sendBroadcast(ContextUtils.getContext(), PackageHelper.ACTION_ASR_CONFIRMED, null, null, bundle);
                }
                if (!SpeechClient.instance().getSpeechState().isDMStarted()) {
                    return;
                }
                boolean isInvalidAsr = isEof && TextUtils.isEmpty(txt) && ((!TextUtils.isEmpty(ContextModel.this.mAsrText) && ContextModel.this.mIsEof) || TextUtils.isEmpty(ContextModel.this.mAsrText));
                ContextModel.this.mAsrText = txt;
                ContextModel.this.mIsEof = isEof;
                if (isInvalidAsr) {
                    return;
                }
                int soundArea = jo.optInt("soundArea", 0);
                if (CarModelsManager.getFeature().isOldAsr() && soundArea == 0) {
                    soundArea = 1;
                    Logger.d(ContextModel.TAG, "old asr and make soundArea to 1");
                }
                ContextModel.this.getSpeechContextManager().onInputText(soundArea, txt, isEof, isInterrupted, invalid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onOutputText(String data) {
            String str = ContextModel.TAG;
            Logger.d(str, "onOutputText data = " + data);
            String txt = "";
            try {
                JSONObject jo = new JSONObject(data);
                txt = jo.optString("text", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(txt)) {
                return;
            }
            ContextModel.this.getSpeechContextManager().onOutputText(txt);
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetText(final String text) {
            String str = ContextModel.TAG;
            Logger.d(str, "onWidgetText text = " + text);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        JSONObject jo = new JSONObject(text);
                        if (ContextListener.this.isBugReport(jo)) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("name", ContextModel.WIDGET_BUG_REPORT);
                            ContextModel.this.handleIntent(ContextModel.BUG_REPORT, jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetList(final String data) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.2
                @Override // java.lang.Runnable
                public void run() {
                    ListWidget listWidget = new ListWidget().fromJson(data);
                    String str = ContextModel.TAG;
                    Logger.v(str, "onWidgetList data = " + listWidget.getExtraType());
                    List<SpeechWidget> widgetList = listWidget.getList();
                    if (CollectionUtils.isEmpty(widgetList)) {
                        Logger.d(ContextModel.TAG, "onWidgetList list size = 0");
                        return;
                    }
                    if (listWidget.getExtraType().equals("navi")) {
                        SpeechPresenter.getInstance().setSpeechViewType(1);
                    }
                    if (listWidget.getExtraType().equals(ListWidget.EXTRA_TYPE_NAVI_ROUTE)) {
                        SpeechPresenter.getInstance().setSpeechViewType(0);
                    }
                    ContextModel.this.getSpeechContextManager().onShowWidget(listWidget);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListFocus(String source, int index) {
            String str = ContextModel.TAG;
            Logger.v(str, "onWidgetListFocus source = " + source + " index= " + index);
            if (source != null && !source.equals("com.android.systemui")) {
                ContextModel.this.getSpeechContextManager().onWidgetListFocus(index);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListCancelFocus(String source, int index) {
            String str = ContextModel.TAG;
            Logger.i(str, "onWidgetListCancelFocus source = " + source + " index= " + index);
            if (source != null && !source.equals("com.android.systemui")) {
                ContextModel.this.getSpeechContextManager().onWidgetListCancelFocus(index);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListSelect(String source, int index) {
            if (source != null && !source.equals("com.android.systemui")) {
                String str = ContextModel.TAG;
                Logger.v(str, "onWidgetListSelect index= " + index);
                ContextModel.this.getSpeechContextManager().onWidgetListSelect(index);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetCustom(final String data) {
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.3
                @Override // java.lang.Runnable
                public void run() {
                    String intentName = "";
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        intentName = jsonObject.optString(ContextModel.INTENT_NAME);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ContextModel.this.handleIntent(intentName, jsonObject);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetRecommend(String data) {
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetSearch(final String data) {
            String str = ContextModel.TAG;
            Logger.d(str, "onWidgetSearch:" + data);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.4
                @Override // java.lang.Runnable
                public void run() {
                    ContextModel.this.getSpeechContextManager().onShowWidget(new SearchWidget().fromJson(data));
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onSayWelcome(final String data) {
            String str = ContextModel.TAG;
            Logger.d(str, "onSayWelcome:" + data);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.5
                @Override // java.lang.Runnable
                public void run() {
                    ContextModel.this.getSpeechContextManager().onSayWelcome(data);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onExitRecommendCard() {
            Logger.i(ContextModel.TAG, "onExitRecommendCard");
            ContextModel.this.getSpeechContextManager().onExitRecommendCard();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetCancel(String widgetId, String cancelWay) {
            String str = ContextModel.TAG;
            Logger.i(str, "onWidgetCancel : widgetId = " + widgetId + " cancelWay = " + cancelWay);
            if (!ContextHelper.isBugReport()) {
                ContextModel.this.getSpeechContextManager().onWidgetCancel(widgetId, cancelWay);
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onPageNext() {
            ContextModel.this.getSpeechContextManager().onPageNext();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onPagePrev() {
            ContextModel.this.getSpeechContextManager().onPagePrev();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onPageSetLow() {
            ContextModel.this.getSpeechContextManager().onPageSetLow();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onPageTopping() {
            ContextModel.this.getSpeechContextManager().onPageTopping();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListExpend(String source, String type) {
            String str = ContextModel.TAG;
            Logger.d(str, "onWidgetListExpend source = " + source + " type = " + type);
            if (source != null && !source.equals("com.android.systemui")) {
                ContextModel.this.getSpeechContextManager().onWidgetListExpend();
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListFold(String source, String type) {
            String str = ContextModel.TAG;
            Logger.d(str, "onWidgetListFold source = " + source + " type = " + type);
            if (source != null && !source.equals("com.android.systemui")) {
                ContextModel.this.getSpeechContextManager().onWidgetListFold();
            }
        }

        @Override // com.xiaopeng.speech.protocol.node.context.AbsContextListener, com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onWidgetListStopCountdown() {
            Logger.d(ContextModel.TAG, "onWidgetListStopCountdown");
            ContextModel.this.getSpeechContextManager().onWidgetListStopCountdown();
        }

        @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onTipsListeningShow(final String data) {
            String str = ContextModel.TAG;
            Logger.d(str, "onTipsListeningShow " + data);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.6
                @Override // java.lang.Runnable
                public void run() {
                    SpeechPresenter.getInstance().onTipsListeningShow(data);
                }
            });
        }

        @Override // com.xiaopeng.speech.protocol.node.context.ContextListener
        public void onTipsListeningStop(String data) {
            String str = ContextModel.TAG;
            Logger.d(str, "onTipsListeningStop " + data);
            ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel.ContextListener.7
                @Override // java.lang.Runnable
                public void run() {
                    SpeechPresenter.getInstance().onTipsListeningStop();
                }
            });
        }
    }
}
