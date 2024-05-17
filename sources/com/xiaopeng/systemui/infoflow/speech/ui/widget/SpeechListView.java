package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.bean.FeedListUIValue;
import com.xiaopeng.speech.protocol.bean.search.ChargeData;
import com.xiaopeng.speech.protocol.bean.search.SearchContentBean;
import com.xiaopeng.speech.protocol.node.navi.NaviNode;
import com.xiaopeng.speech.protocol.node.navi.bean.PoiBean;
import com.xiaopeng.speech.protocol.node.navi.bean.RouteSelectBean;
import com.xiaopeng.speech.protocol.node.phone.bean.PhoneBean;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SearchWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.VuiEngine;
import com.xiaopeng.systemui.infoflow.message.helper.StartSnapHelper;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviChargeSearchPoiAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviPoiAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.NaviRouterAdapter;
import com.xiaopeng.systemui.infoflow.speech.ui.adapter.PhoneListAdapter;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
import com.xiaopeng.systemui.utils.Utils;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.model.VuiEvent;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class SpeechListView extends AlphaOptimizedRelativeLayout implements IVuiSceneListener, View.OnClickListener {
    private static final int AUTO_NAVI_DELAY_TIME = 15;
    private static final String KEY_INFOFLOW_CARD_HEIGHT = "infoflow_card_height";
    private static final int LIST_TYPE_DEFAULT = 0;
    private static final int LIST_TYPE_POI = 1;
    private static final int MSG_FOCUS_ITEM = 0;
    private static final int MSG_START_AUTO_NAVI_TIMER = 1;
    private static final int MSG_UPDATE_AUTO_NAVI_TIMER = 2;
    private static final String TAG = SpeechListView.class.getSimpleName();
    private static final String sPoiSceneId = "NaviPoiList";
    private int mAppTopOffset;
    private int mAutoNaviLeftTime;
    private AlphaOptimizedRelativeLayout mBtnCancelNaviRoute;
    private AlphaOptimizedRelativeLayout mBtnStartNavi;
    private AlphaOptimizedLinearLayout mBtnStartNaviContainer;
    private AlphaOptimizedRelativeLayout mBtnStartPathFind;
    protected String mCurrentExtraType;
    private String mExtraType;
    protected CardStack mFocusRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private int mListType;
    private boolean mNeedAutoSetFocus;
    private PhoneListAdapter mPhoneListAdapter;
    private NaviPoiAdapter mPoiAdapter;
    private NaviRouterAdapter mRouterAdapter;
    private NaviChargeSearchPoiAdapter mSearchAdapter;
    private SnapHelper mSnapHelper;
    private AnimatedTextView mTvLeftTime;
    private Handler mUIHandler;

    public SpeechListView(Context context) {
        super(context);
        this.mSnapHelper = new StartSnapHelper();
        this.mAppTopOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.app_top_offset);
        this.mListType = 0;
        this.mAutoNaviLeftTime = 15;
        this.mNeedAutoSetFocus = true;
        this.mUIHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    SpeechListView.this.mFocusRecyclerView.forceFocusItem(msg.arg1, false);
                } else if (i == 1) {
                    SpeechListView.this.startAutoNaviTimer();
                } else if (i == 2) {
                    SpeechListView.this.checkAndUpdateAutoNaviTimer();
                }
            }
        };
    }

    public SpeechListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSnapHelper = new StartSnapHelper();
        this.mAppTopOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.app_top_offset);
        this.mListType = 0;
        this.mAutoNaviLeftTime = 15;
        this.mNeedAutoSetFocus = true;
        this.mUIHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                int i = msg.what;
                if (i == 0) {
                    SpeechListView.this.mFocusRecyclerView.forceFocusItem(msg.arg1, false);
                } else if (i == 1) {
                    SpeechListView.this.startAutoNaviTimer();
                } else if (i == 2) {
                    SpeechListView.this.checkAndUpdateAutoNaviTimer();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mNeedAutoSetFocus = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListType == 1) {
            exitPoiListScene();
        }
        this.mNeedAutoSetFocus = false;
        this.mUIHandler.removeCallbacksAndMessages(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            addRecyclerViewSplitter();
        }
    }

    public void setData(SpeechWidget listWidget) {
        setData(listWidget, false);
    }

    public String getExtraType() {
        return this.mExtraType;
    }

    public void setData(SpeechWidget listWidget, boolean isrefresh) {
        exitPoiListScene();
        this.mCurrentExtraType = listWidget.getExtraType();
        if ("navi".equals(this.mCurrentExtraType)) {
            if (SpeechWidget.TYPE_SEARCH.equals(this.mCurrentExtraType)) {
                showSearchResult(listWidget);
            } else {
                showPoiResult(listWidget);
            }
        } else if (ListWidget.EXTRA_TYPE_NAVI_ROUTE.equals(this.mCurrentExtraType)) {
            showRouterResult(listWidget, isrefresh);
        } else if ("phone".equals(this.mCurrentExtraType)) {
            showPhoneResult(listWidget);
        }
        this.mExtraType = listWidget.getExtraType();
        this.mFocusRecyclerView.scrollToPosition(0);
    }

    private void exitPoiListScene() {
        VuiEngine.getInstance(this.mContext).exitScene(sPoiSceneId);
        VuiEngine.getInstance(this.mContext).removeVuiSceneListener(sPoiSceneId);
    }

    protected void addRecyclerViewSplitter() {
        addRecyclerViewSplitter(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addRecyclerViewSplitter(boolean showHorizontalSplitter) {
        if (this.mFocusRecyclerView.getItemDecorationCount() > 0) {
            CardStack cardStack = this.mFocusRecyclerView;
            cardStack.removeItemDecoration(cardStack.getItemDecorationAt(0));
        }
        CustomItemDecoration divider = new CustomItemDecoration(this.mContext, showHorizontalSplitter ? 1 : 0);
        divider.setDrawable(this.mContext.getDrawable(showHorizontalSplitter ? R.drawable.ic_list_splitter : R.drawable.ic_list_splitter_vertical));
        this.mFocusRecyclerView.addItemDecoration(divider);
    }

    private void showSearchResult(SpeechWidget listWidget) {
        SearchWidget searchWidget = (SearchWidget) listWidget;
        if (searchWidget.getSearchType() == 1) {
            JSONObject widget = searchWidget.getWidget();
            try {
                JSONObject searchContent = widget.getJSONObject(SpeechWidget.WIDGET_SEARCH_CONTENT);
                SearchContentBean<ChargeData> searchContentBean = (SearchContentBean) new Gson().fromJson(searchContent.toString(), new TypeToken<SearchContentBean<ChargeData>>() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.2
                }.getType());
                if (searchContentBean == null) {
                    return;
                }
                TextUtils.isEmpty(searchContentBean.aboveHint);
                TextUtils.isEmpty(searchContentBean.belowHint);
                if (this.mSearchAdapter == null) {
                    this.mSearchAdapter = new NaviChargeSearchPoiAdapter(getContext());
                }
                addRecyclerViewSplitter();
                this.mFocusRecyclerView.setAutoUnfocus(false);
                this.mFocusRecyclerView.requestFocus();
                this.mFocusRecyclerView.resetFocus();
                this.mFocusRecyclerView.setAdapter(this.mSearchAdapter);
                this.mSearchAdapter.setData(searchContentBean);
                this.mFocusRecyclerView.forceFocusItem(0);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        searchWidget.getSearchType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showPoiResult(SpeechWidget listWidget) {
        this.mListType = 1;
        this.mBtnStartNaviContainer.setVisibility(8);
        initPoiVui();
        List<SpeechWidget> list = listWidget.getList();
        ArrayList arrayList = new ArrayList();
        for (SpeechWidget speechWidget : list) {
            String poiJson = speechWidget.getExtra("navi");
            arrayList.add(PoiBean.fromJson(poiJson));
        }
        if (this.mPoiAdapter == null) {
            this.mPoiAdapter = new NaviPoiAdapter(getContext());
            this.mPoiAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.3
                @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter.OnItemClickListener
                public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                    SpeechListView.this.mFocusRecyclerView.forceFocusItem(position);
                }
            });
            this.mPoiAdapter.setViewContainer(this);
        }
        addRecyclerViewSplitter();
        this.mFocusRecyclerView.setNeedClearFocusAfterScroll(true);
        this.mFocusRecyclerView.setCheckPositionBeforeFocus(true);
        this.mFocusRecyclerView.setAutoUnfocus(false);
        this.mFocusRecyclerView.resetFocus();
        if (!isRefresh(listWidget)) {
            this.mFocusRecyclerView.setAdapter(this.mPoiAdapter);
        }
        this.mPoiAdapter.setNewData(arrayList);
        notifyInfoflowCardHeight();
    }

    private void notifyInfoflowCardHeight() {
        this.mUIHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.4
            @Override // java.lang.Runnable
            public void run() {
                int infoflowCardHeight = SpeechListView.this.getHeight() - SpeechListView.this.mAppTopOffset;
                String str = SpeechListView.TAG;
                Logger.d(str, "notifyInfoflowCardHeight : height = " + infoflowCardHeight);
                Utils.putInt(SpeechListView.this.mContext, SpeechListView.KEY_INFOFLOW_CARD_HEIGHT, infoflowCardHeight);
            }
        }, 100L);
    }

    private void initPoiVui() {
        VuiEngine.getInstance(this.mContext).addVuiSceneListener(sPoiSceneId, this.mFocusRecyclerView, this);
        VuiEngine.getInstance(this.mContext).enterScene(sPoiSceneId);
        CardStack cardStack = this.mFocusRecyclerView;
        if (cardStack != null) {
            cardStack.setSceneId(sPoiSceneId);
        }
    }

    protected NaviRouterAdapter getRouterAdapter() {
        return new NaviRouterAdapter(getContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showRouterResult(SpeechWidget listWidget, boolean isfresh) {
        String str = TAG;
        Log.d(str, "showRouterResult() isfresh = [" + isfresh + NavigationBarInflaterView.SIZE_MOD_END);
        this.mBtnStartNaviContainer.setVisibility(0);
        List<SpeechWidget> list = listWidget.getList();
        ArrayList<RouteSelectBean> arrayList = new ArrayList();
        for (SpeechWidget speechWidget : list) {
            if (arrayList.size() >= 3) {
                break;
            }
            String routerJson = speechWidget.getExtra(ListWidget.EXTRA_TYPE_NAVI_ROUTE);
            arrayList.add(RouteSelectBean.fromJson(routerJson));
        }
        String widgetId = listWidget.getWidgetId();
        if (widgetId != null && widgetId.endsWith(NaviNode.BASE_ROUTE_WIDGET_ID)) {
            for (RouteSelectBean routeSelectBean : arrayList) {
                routeSelectBean.batteryStatus = -1;
            }
        }
        if (this.mRouterAdapter == null) {
            this.mRouterAdapter = getRouterAdapter();
            this.mRouterAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.5
                @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter.OnItemClickListener
                public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                    SpeechListView.this.cancelAutoFocus();
                    SpeechListView.this.mFocusRecyclerView.forceFocusItem(position);
                    SpeechListView.this.stopAutoNaviTimer();
                    SpeechListView.this.mRouterAdapter.sendFocusedEvent(position);
                }
            });
        }
        addRecyclerViewSplitter();
        this.mFocusRecyclerView.setAutoUnfocus(false);
        this.mFocusRecyclerView.setNeedClearFocusAfterScroll(false);
        this.mFocusRecyclerView.setCheckPositionBeforeFocus(false);
        if (!isRefresh(listWidget)) {
            this.mFocusRecyclerView.setAdapter(this.mRouterAdapter);
            this.mUIHandler.removeMessages(1);
            this.mUIHandler.sendEmptyMessageDelayed(1, 200L);
        } else {
            delayToFocusItem(this.mFocusRecyclerView.getFocusIndex());
        }
        this.mRouterAdapter.setNewData(arrayList);
        if (this.mNeedAutoSetFocus) {
            delayToFocusItem(0);
        }
        notifyInfoflowCardHeight();
    }

    private void delayToFocusItem(int focusIndex) {
        this.mUIHandler.removeMessages(0);
        Message msg = this.mUIHandler.obtainMessage(0);
        msg.arg1 = focusIndex;
        this.mUIHandler.sendMessageDelayed(msg, 500L);
    }

    public void cancelAutoFocus() {
        this.mNeedAutoSetFocus = false;
        this.mUIHandler.removeMessages(0);
    }

    private boolean isRefresh(SpeechWidget listWidget) {
        String extraType;
        return (listWidget == null || (extraType = listWidget.getExtraType()) == null || !extraType.equals(this.mExtraType)) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAutoNaviTimer() {
        this.mTvLeftTime.setVisibility(0);
        this.mAutoNaviLeftTime = 15;
        this.mUIHandler.removeMessages(2);
        updateAutoNaviLeftTime();
        Message msg = this.mUIHandler.obtainMessage(2);
        this.mUIHandler.sendMessageDelayed(msg, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAndUpdateAutoNaviTimer() {
        this.mAutoNaviLeftTime--;
        if (this.mAutoNaviLeftTime == 0) {
            stopAutoNaviTimer();
            startNavi();
            return;
        }
        updateAutoNaviTimer();
    }

    private void updateAutoNaviTimer() {
        updateAutoNaviLeftTime();
        Message msg = this.mUIHandler.obtainMessage(2);
        this.mUIHandler.sendMessageDelayed(msg, 1000L);
    }

    private void updateAutoNaviLeftTime() {
        if (this.mAutoNaviLeftTime < 10) {
            AnimatedTextView animatedTextView = this.mTvLeftTime;
            animatedTextView.setText(this.mAutoNaviLeftTime + "s  ");
            return;
        }
        AnimatedTextView animatedTextView2 = this.mTvLeftTime;
        animatedTextView2.setText(this.mAutoNaviLeftTime + "s");
    }

    public void stopAutoNaviTimer() {
        AnimatedTextView animatedTextView = this.mTvLeftTime;
        if (animatedTextView != null) {
            animatedTextView.setVisibility(8);
        }
        this.mUIHandler.removeMessages(2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showPhoneResult(SpeechWidget listWidget) {
        List<SpeechWidget> list = listWidget.getList();
        ArrayList arrayList = new ArrayList();
        for (SpeechWidget speechWidget : list) {
            String phoneBeanJson = speechWidget.getExtra("phone");
            arrayList.add(PhoneBean.fromJson(phoneBeanJson));
        }
        if (this.mPhoneListAdapter == null) {
            this.mPhoneListAdapter = new PhoneListAdapter(getContext());
        }
        this.mFocusRecyclerView.setNeedClearFocusAfterScroll(true);
        this.mFocusRecyclerView.setCheckPositionBeforeFocus(true);
        this.mFocusRecyclerView.setAutoUnfocus(false);
        this.mFocusRecyclerView.setAdapter(this.mPhoneListAdapter);
        this.mPhoneListAdapter.setNewData(arrayList);
        this.mPhoneListAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.6
            @Override // com.xiaopeng.systemui.infoflow.speech.ui.adapter.BaseRecyclerAdapter.OnItemClickListener
            public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                SpeechListView.this.mPhoneListAdapter.sendSelectedEvent(position);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mFocusRecyclerView = (CardStack) findViewById(R.id.rv_widget_list);
        this.mSnapHelper.attachToRecyclerView(this.mFocusRecyclerView);
        this.mLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.mFocusRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mBtnStartNaviContainer = (AlphaOptimizedLinearLayout) findViewById(R.id.btn_start_navi_container);
        this.mBtnStartNavi = (AlphaOptimizedRelativeLayout) findViewById(R.id.btn_start_navi);
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout = this.mBtnStartNavi;
        if (alphaOptimizedRelativeLayout != null) {
            alphaOptimizedRelativeLayout.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.7
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SpeechListView.this.stopAutoNaviTimer();
                    SpeechListView.this.startNavi();
                }
            });
        }
        this.mBtnStartPathFind = (AlphaOptimizedRelativeLayout) findViewById(R.id.btn_start_pathfind);
        this.mBtnCancelNaviRoute = (AlphaOptimizedRelativeLayout) findViewById(R.id.btn_cancel_route_navi);
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout2 = this.mBtnStartPathFind;
        if (alphaOptimizedRelativeLayout2 != null) {
            alphaOptimizedRelativeLayout2.setOnClickListener(this);
        }
        AlphaOptimizedRelativeLayout alphaOptimizedRelativeLayout3 = this.mBtnCancelNaviRoute;
        if (alphaOptimizedRelativeLayout3 != null) {
            alphaOptimizedRelativeLayout3.setOnClickListener(this);
        }
        this.mTvLeftTime = (AnimatedTextView) findViewById(R.id.tv_left_time);
        this.mFocusRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechListView.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    int firstVisibleItemPosition = SpeechListView.this.mLayoutManager.findFirstVisibleItemPosition();
                    SpeechListView.sendScrollEvent(firstVisibleItemPosition);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startNavi() {
        CardStack cardStack = this.mFocusRecyclerView;
        if (cardStack != null) {
            cardStack.performClickItem();
        }
    }

    public static void sendScrollEvent(int position) {
        String str = TAG;
        Logger.d(str, "sendScrollEvent position=" + position);
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = ContextUtils.getContext().getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_SCROLL, FeedListUIValue.toJson(feedListUIValue));
    }

    public static void sendSelectedEvent(int position) {
        String str = TAG;
        Logger.d(str, "sendSelectedEvent position=" + position);
        FeedListUIValue feedListUIValue = new FeedListUIValue();
        feedListUIValue.source = ContextUtils.getContext().getPackageName();
        feedListUIValue.index = position + 1;
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.LIST_ITEM_SELECT, FeedListUIValue.toJson(feedListUIValue));
    }

    public void focusItem(int index, boolean triggerListener) {
        NaviPoiAdapter naviPoiAdapter;
        if (this.mFocusRecyclerView != null) {
            String str = this.mExtraType;
            if (str != null && str.equals("navi") && (naviPoiAdapter = this.mPoiAdapter) != null) {
                naviPoiAdapter.setShowDetail(!triggerListener);
            }
            this.mFocusRecyclerView.forceFocusItem(index, triggerListener);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiEvent(View view, VuiEvent vuiEvent) {
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public boolean onInterceptVuiEvent(View view, VuiEvent vuiEvent) {
        return false;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onBuildScene() {
        if (this.mListType == 1) {
            VuiEngine.getInstance(this.mContext).buildScene(sPoiSceneId, this.mFocusRecyclerView);
        }
    }

    public void unfocusItem(int unfocusIndex) {
        CardStack cardStack = this.mFocusRecyclerView;
        if (cardStack != null) {
            cardStack.unfocusItem(unfocusIndex);
        }
    }

    public void onPoiDetailShown(boolean b) {
    }

    public void onWidgetListExpend() {
        onPoiDetailShown(false);
    }

    public void onWidgetListFold() {
        onPoiDetailShown(true);
    }

    public void onWidgetListStopCountdown() {
        stopAutoNaviTimer();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel_route_navi) {
            FeedListUIValue feedListUIValue = new FeedListUIValue();
            feedListUIValue.source = this.mContext.getPackageName();
            SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.SCRIPT_QUIT, FeedListUIValue.toJson(feedListUIValue));
        } else if (id == R.id.btn_start_pathfind) {
            FeedListUIValue feedListUIValue2 = new FeedListUIValue();
            feedListUIValue2.source = this.mContext.getPackageName();
            SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.EXPLORE_CLICK, FeedListUIValue.toJson(feedListUIValue2));
        }
    }
}
