package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.systemui.R;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviRouterAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.VerticalNaviRouterAdapter;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
/* loaded from: classes24.dex */
public class VerticalSpeechListView extends SpeechListView {
    private CardStack mNonRouteResultListView;
    private CardStack mRouteResultListView;

    public VerticalSpeechListView(Context context) {
        super(context);
    }

    public VerticalSpeechListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mRouteResultListView = (CardStack) findViewById(R.id.route_result_widget_list);
        CardStack cardStack = this.mRouteResultListView;
        if (cardStack != null) {
            cardStack.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
        }
        this.mNonRouteResultListView = (CardStack) findViewById(R.id.rv_widget_list);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    public void onPoiDetailShown(boolean b) {
        setVisibility(b ? 8 : 0);
        if (this.mFocusRecyclerView != null) {
            this.mFocusRecyclerView.setVisibility(b ? 8 : 0);
            this.mFocusRecyclerView.updateVuiScene(50);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    public void showPhoneResult(SpeechWidget listWidget) {
        super.showPhoneResult(listWidget);
        addRecyclerViewSplitter();
    }

    private void showListView(boolean show) {
        setVisibility(0);
        if (this.mRouteResultListView != null) {
            this.mNonRouteResultListView.setVisibility(show ? 0 : 8);
            this.mRouteResultListView.setVisibility(show ? 8 : 0);
            if (show) {
                this.mFocusRecyclerView = this.mNonRouteResultListView;
            } else {
                this.mFocusRecyclerView = this.mRouteResultListView;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    public void showRouterResult(SpeechWidget listWidget, boolean isfresh) {
        showListView(false);
        onPoiDetailShown(false);
        super.showRouterResult(listWidget, isfresh);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    protected void addRecyclerViewSplitter() {
        addRecyclerViewSplitter(!ListWidget.EXTRA_TYPE_NAVI_ROUTE.equals(this.mCurrentExtraType));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    public void showPoiResult(SpeechWidget listWidget) {
        showListView(true);
        super.showPoiResult(listWidget);
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView
    protected NaviRouterAdapter getRouterAdapter() {
        return new VerticalNaviRouterAdapter(getContext());
    }
}
