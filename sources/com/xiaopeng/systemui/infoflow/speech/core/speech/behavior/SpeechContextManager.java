package com.xiaopeng.systemui.infoflow.speech.core.speech.behavior;

import android.util.Log;
import com.xiaopeng.speech.common.util.SimpleCallbackList;
import com.xiaopeng.speech.jarvisproto.AsrEvent;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
/* loaded from: classes24.dex */
public class SpeechContextManager {
    private static final String TAG = "SpeechContextManager";
    private SimpleCallbackList<ISpeechContext> mSpeechContextCallbacks = new SimpleCallbackList<>();
    private ISpeechPageScroll mSpeechPageScroll;

    public void addCallback(ISpeechContext behavior) {
        this.mSpeechContextCallbacks.addCallback(behavior);
    }

    public void removeCallback(ISpeechContext behavior) {
        this.mSpeechContextCallbacks.removeCallback(behavior);
    }

    public void setSpeechPageScrollCallback(ISpeechPageScroll behavior) {
        Log.d(TAG, "setSpeechPageScrollCallback" + behavior);
        this.mSpeechPageScroll = behavior;
    }

    public void onInputText(int sourceArea, String text, boolean isEof, boolean isInterrupted, boolean invalid) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onInputText(sourceArea, text, isEof, isInterrupted, invalid);
            }
        }
    }

    public void onOutputText(String text) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onOutputText(text);
            }
        }
    }

    public void onShowWidget(SpeechWidget widget) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onShowWidget(widget);
            }
        }
    }

    public void onWidgetListFocus(int index) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListFocus(index);
            }
        }
    }

    public void onWidgetListSelect(int index) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListSelect(index);
            }
        }
    }

    public void onWidgetText(String data) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetText(data);
            }
        }
    }

    public void onWidgetRecommend(String data) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetRecommend(data);
            }
        }
    }

    public void onBugReportBegin() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onBugReportBegin();
            }
        }
    }

    public void onBugReportEnd() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onBugReportEnd();
            }
        }
    }

    public void onSayWelcome(String data) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onSayWelcome(data);
            }
        }
    }

    public void onAsrEvent(int event) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onAsrEvent(event);
            }
        }
    }

    public void onWidgetCancel(String widgetId, String cancelWay) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetCancel(widgetId, cancelWay);
            }
        }
    }

    public void onPageNext() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            iSpeechPageScroll.onPageNext();
        }
    }

    public void onPagePrev() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            iSpeechPageScroll.onPagePrev();
        }
    }

    public void onPageSetLow() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            iSpeechPageScroll.onPageSetLow();
        }
    }

    public void onPageTopping() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            iSpeechPageScroll.onPageTopping();
        }
    }

    public int getWidgetListSize() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getWidgetListSize();
        }
        Log.d(TAG, "getWidgetListSize mSpeechPageScroll null");
        return -1;
    }

    public int getWidgetPageSize() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getWidgetPageSize();
        }
        Log.d(TAG, "getWidgetPageSize mSpeechPageScroll null");
        return 0;
    }

    public int getWidgetCurrLocation() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getWidgetCurrLocation();
        }
        Log.d(TAG, "getWidgetCurrLocation mSpeechPageScroll null");
        return 0;
    }

    public int getInfoFlowOnePage() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getInfoFlowOnePage();
        }
        Log.d(TAG, "getInfoFlowOnePage mSpeechPageScroll null");
        return 0;
    }

    public int getInfoFlowScrollToBottom() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getInfoFlowScrollToBottom();
        }
        Log.d(TAG, "getInfoFlowScrollToBottom mSpeechPageScroll null");
        return 0;
    }

    public int getInfoFlowScrollToTop() {
        ISpeechPageScroll iSpeechPageScroll = this.mSpeechPageScroll;
        if (iSpeechPageScroll != null) {
            return iSpeechPageScroll.getInfoFlowScrollToTop();
        }
        Log.d(TAG, "getInfoFlowScrollToTop mSpeechPageScroll null");
        return 0;
    }

    public void onWidgetListCancelFocus(int index) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListCancelFocus(index);
            }
        }
    }

    public void onExitRecommendCard() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onExitRecommendCard();
            }
        }
    }

    public void onWidgetListExpend() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListExpend();
            }
        }
    }

    public void onWidgetListFold() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListFold();
            }
        }
    }

    public void onWidgetListStopCountdown() {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onWidgetListStopCountdown();
            }
        }
    }

    public void onAsrEvent(AsrEvent event) {
        Object[] iSpeechContexts = this.mSpeechContextCallbacks.collectCallbacks();
        if (iSpeechContexts != null) {
            for (Object obj : iSpeechContexts) {
                ((ISpeechContext) obj).onAsrEvent(event);
            }
        }
    }
}
