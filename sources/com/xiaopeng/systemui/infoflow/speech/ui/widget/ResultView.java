package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.speech.protocol.bean.CardValue;
import com.xiaopeng.speech.protocol.bean.weather.WeatherBean;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardPresenter;
import com.xiaopeng.systemui.infoflow.speech.core.speech.model.ContextModel;
import com.xiaopeng.systemui.infoflow.speech.ui.CaracPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class ResultView extends RelativeLayout {
    private static final String TAG = ResultView.class.getSimpleName();
    private BugReportView mBugReportView;
    private CtrlCardPresenter mCardPresenter;
    private RelativeLayout mContentContainer;
    private String mInputText;
    private ListWidget mListWidget;
    private boolean mNeedCreateListView;
    private String mOutputText;

    public ResultView(Context context) {
        super(context);
        this.mNeedCreateListView = true;
    }

    public ResultView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mNeedCreateListView = true;
    }

    public void inputText(String text) {
        this.mInputText = text;
    }

    public void outputText(String text) {
        this.mOutputText = text;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContentContainer = (RelativeLayout) findViewById(R.id.result_content);
    }

    public void onLongTextOutput(String text) {
        showLongTextView(text);
    }

    private void showLongTextView(String text) {
        clearContentContainer();
        BaikeView baikeView = (BaikeView) LayoutInflater.from(getContext()).inflate(R.layout.view_baike, (ViewGroup) this.mContentContainer, false);
        this.mContentContainer.addView(baikeView);
        baikeView.setContent(text);
        baikeView.setTitle(this.mInputText);
    }

    public void setListData(SpeechWidget widget) {
        try {
            JSONObject jsonObject = widget.getExtra();
            String widgetName = jsonObject.optString("name");
            if ("weather".equals(widgetName)) {
                WeatherBean weatherBean = WeatherBean.fromJsonObj(jsonObject);
                showWeatherView(weatherBean);
            } else if (ContextModel.WIDGET_BUG_REPORT.equals(widgetName)) {
                showBugReportView();
            } else if (SpeechWidget.TYPE_LIST.equals(widget.getType())) {
                ListWidget listWidget = (ListWidget) widget;
                this.mListWidget = listWidget;
                showWidgetListView(listWidget);
            } else if (SpeechWidget.TYPE_SEARCH.equals(widget.getType())) {
                showWidgetListView(widget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetChildrenView() {
        this.mListWidget = null;
    }

    private void showBugReportView() {
        clearContentContainer();
        this.mBugReportView = (BugReportView) LayoutInflater.from(getContext()).inflate(R.layout.view_bugreport, (ViewGroup) this.mContentContainer, false);
        this.mContentContainer.addView(this.mBugReportView);
    }

    private boolean checkDataSame(ListWidget widget) {
        ListWidget listWidget = this.mListWidget;
        return listWidget != null && listWidget.getTitle().equals(widget.getTitle()) && this.mListWidget.getDataSource().equals(widget.getDataSource());
    }

    private void showWidgetListView(SpeechWidget listWidget) {
        SpeechListView speechListView;
        String str = TAG;
        Logger.i(str, "showWidgetListView : mNeedCreateListView = " + this.mNeedCreateListView);
        if (ListWidget.EXTRA_TYPE_NAVI_ROUTE.equals(listWidget.getExtraType()) || "navi".equals(listWidget.getExtraType())) {
            if (this.mNeedCreateListView) {
                RelativeLayout relativeLayout = this.mContentContainer;
                if (relativeLayout != null) {
                    relativeLayout.removeAllViews();
                }
                this.mListWidget = null;
                speechListView = (SpeechListView) LayoutInflater.from(getContext()).inflate(R.layout.view_navi_widget_list, (ViewGroup) this.mContentContainer, false);
                this.mContentContainer.addView(speechListView);
                this.mNeedCreateListView = false;
            } else {
                SpeechListView speechListView2 = findListView();
                if (speechListView2 != null) {
                    Log.d(TAG, "showWidgetListView: is refresh");
                    speechListView2.setData(listWidget, true);
                    return;
                }
                return;
            }
        } else {
            clearContentContainer();
            speechListView = (SpeechListView) LayoutInflater.from(getContext()).inflate(R.layout.view_widget_list, (ViewGroup) this.mContentContainer, false);
            this.mContentContainer.addView(speechListView);
        }
        if (speechListView != null) {
            speechListView.setData(listWidget);
        }
    }

    private SpeechListView findListView() {
        for (int i = 0; i < this.mContentContainer.getChildCount(); i++) {
            View childAt = this.mContentContainer.getChildAt(i);
            if (childAt instanceof SpeechListView) {
                return (SpeechListView) childAt;
            }
        }
        return null;
    }

    public void showCaracView() {
        clearContentContainer();
        CaracView caracView = (CaracView) LayoutInflater.from(getContext()).inflate(R.layout.view_carac, (ViewGroup) this.mContentContainer, false);
        CaracPresenter caracPresenter = new CaracPresenter(caracView);
        caracView.setPresenter(caracPresenter);
        this.mContentContainer.addView(caracView);
    }

    public void showCtrlView(int type, CardValue cardValue) {
        Logger.d(TAG, "showCtrlView");
        clearContentContainer();
        CtrlCardView mCtrlCardView = (CtrlCardView) LayoutInflater.from(getContext()).inflate(R.layout.view_ctrlcard, (ViewGroup) this.mContentContainer, false);
        CtrlCardPresenter ctrlCardPresenter = this.mCardPresenter;
        if (ctrlCardPresenter != null) {
            ctrlCardPresenter.unRegisterListener();
            this.mCardPresenter = null;
        }
        this.mCardPresenter = new CtrlCardPresenter(mCtrlCardView, type, cardValue);
        this.mCardPresenter.registerListener();
        mCtrlCardView.setPresenter(this.mCardPresenter);
        mCtrlCardView.requestFocus();
        mCtrlCardView.setOnKeyListener(new View.OnKeyListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.ResultView.1
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == 0) {
                    if (keyEvent.getKeyCode() == 1083) {
                        ResultView.this.mCardPresenter.refreshByCtrlKeyEvent(true);
                    } else if (keyEvent.getKeyCode() == 1084) {
                        ResultView.this.mCardPresenter.refreshByCtrlKeyEvent(false);
                    }
                }
                return true;
            }
        });
        this.mContentContainer.addView(mCtrlCardView);
    }

    private void showWeatherView(WeatherBean weatherBean) {
        clearContentContainer();
        WeatherView weatherView = (WeatherView) LayoutInflater.from(getContext()).inflate(R.layout.view_weather, (ViewGroup) this.mContentContainer, false);
        this.mContentContainer.addView(weatherView);
        weatherView.setData(weatherBean);
    }

    public void clearContentContainer() {
        Log.d(TAG, "bb_clearContentContainer: ");
        RelativeLayout relativeLayout = this.mContentContainer;
        if (relativeLayout != null) {
            relativeLayout.removeAllViews();
        }
        this.mListWidget = null;
        this.mNeedCreateListView = true;
    }

    public void onBugReportBegin() {
        BugReportView bugReportView = this.mBugReportView;
        if (bugReportView != null) {
            bugReportView.onBugReportBegin();
        }
    }

    public void onBugReportEnd() {
        BugReportView bugReportView = this.mBugReportView;
        if (bugReportView != null) {
            bugReportView.onBugReportEnd();
        }
    }

    public void exitCtrlCard() {
        Log.d(TAG, "exitCtrlCard() called");
        CtrlCardPresenter ctrlCardPresenter = this.mCardPresenter;
        if (ctrlCardPresenter != null) {
            ctrlCardPresenter.unRegisterListener();
            this.mCardPresenter = null;
        }
        clearContentContainer();
    }

    public void onIcmBrightnessChanged(int value) {
        CtrlCardPresenter ctrlCardPresenter = this.mCardPresenter;
        if (ctrlCardPresenter != null) {
            ctrlCardPresenter.onIcmBrightnessChanged(value);
        }
    }

    public void onScreenBrightnessChanged(int value) {
        CtrlCardPresenter ctrlCardPresenter = this.mCardPresenter;
        if (ctrlCardPresenter != null) {
            ctrlCardPresenter.onScreenBrightnessChanged(value);
        }
    }

    private String getWidgetType() {
        ListWidget listWidget = this.mListWidget;
        if (listWidget == null) {
            return "";
        }
        return listWidget.getExtraType();
    }

    public void onWidgetListFocus(int index) {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            int focusIndex = index - 1;
            if (focusIndex < 0) {
                focusIndex = 0;
            }
            checkToCancelAutoFocus(speechListView);
            speechListView.focusItem(focusIndex, false);
            speechListView.stopAutoNaviTimer();
        }
    }

    private void checkToCancelAutoFocus(SpeechListView speechListView) {
        if (getWidgetType().equals(ListWidget.EXTRA_TYPE_NAVI_ROUTE)) {
            speechListView.cancelAutoFocus();
        }
    }

    public void onWidgetListSelect(int index) {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            int focusIndex = index - 1;
            if (focusIndex < 0) {
                focusIndex = 0;
            }
            checkToCancelAutoFocus(speechListView);
            speechListView.focusItem(focusIndex, true);
            speechListView.stopAutoNaviTimer();
        }
    }

    public void onWidgetListCancelFocus(int index) {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            int unfocusIndex = index - 1;
            if (unfocusIndex < 0) {
                unfocusIndex = 0;
            }
            speechListView.unfocusItem(unfocusIndex);
        }
    }

    public void onWidgetListExpend() {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            speechListView.onWidgetListExpend();
        }
    }

    public void onWidgetListFold() {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            speechListView.onWidgetListFold();
        }
    }

    public void onWidgetListStopCountdown() {
        SpeechListView speechListView = findListView();
        if (speechListView != null) {
            speechListView.onWidgetListStopCountdown();
        }
    }
}
