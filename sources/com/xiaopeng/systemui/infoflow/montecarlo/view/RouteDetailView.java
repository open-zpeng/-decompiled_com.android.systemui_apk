package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextSwitcher;
import android.widget.TextView;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.libcarcontrol.CarControlManager;
import com.xiaopeng.speech.protocol.node.navi.bean.RouteSelectBean;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.montecarlo.util.XpFontTagHandler;
import com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechNaviCardView;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.theme.AnimatedTextView;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
/* loaded from: classes24.dex */
public class RouteDetailView extends SpeechNaviCardView {
    protected static final int SWITCH_ANIMATOR_START = -1838984925;
    private static final String TAG = "Route_DetailView";
    protected AnimatorSet mAnimatorSet;
    protected BatteryView mBatteryView;
    private ViewStub mBatteryViewStub;
    private Context mContext;
    protected Handler mHandler;
    protected int mIndex;
    private boolean mInflated;
    protected boolean mIsSingle;
    private AnimatedTextView mLeftDistanceView;
    protected long mLightCount;
    protected AnimatedTextView mPassedDistanceView1;
    protected AnimatedTextView mPassedDistanceView2;
    protected long mRouteLeftDistance;
    private AnimatedTextView mRouteTypeNoView;
    private AnimatedTextView mRouteTypeView;
    protected boolean mRunTextSwitcher;
    private AlphaOptimizedLinearLayout mSingleTotalTimeView;
    protected long mTollCost;
    private TextSwitcher mTotalTimeView;
    private AnimatedTextView mTrafficCostView;
    private AnimatedTextView mTrafficSigView;
    protected long mTravelTime;
    protected XpFontTagHandler mXpFontTagHandler;

    public RouteDetailView(Context context) {
        super(context);
        this.mIsSingle = false;
        this.mRunTextSwitcher = false;
        this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                if (msg.what == RouteDetailView.SWITCH_ANIMATOR_START) {
                    RouteDetailView.this.doStartSwitchAnimator();
                    if (RouteDetailView.this.mHandler.hasMessages(RouteDetailView.SWITCH_ANIMATOR_START)) {
                        RouteDetailView.this.mHandler.removeMessages(RouteDetailView.SWITCH_ANIMATOR_START);
                    }
                    RouteDetailView.this.mHandler.sendEmptyMessageDelayed(RouteDetailView.SWITCH_ANIMATOR_START, 3000L);
                    return false;
                }
                return false;
            }
        });
        this.mInflated = false;
        this.mContext = context;
    }

    public RouteDetailView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsSingle = false;
        this.mRunTextSwitcher = false;
        this.mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                if (msg.what == RouteDetailView.SWITCH_ANIMATOR_START) {
                    RouteDetailView.this.doStartSwitchAnimator();
                    if (RouteDetailView.this.mHandler.hasMessages(RouteDetailView.SWITCH_ANIMATOR_START)) {
                        RouteDetailView.this.mHandler.removeMessages(RouteDetailView.SWITCH_ANIMATOR_START);
                    }
                    RouteDetailView.this.mHandler.sendEmptyMessageDelayed(RouteDetailView.SWITCH_ANIMATOR_START, 3000L);
                    return false;
                }
                return false;
            }
        });
        this.mInflated = false;
        this.mContext = context;
    }

    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.SpeechNaviCardView, com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        Log.e(TAG, "setFocused: " + focused);
    }

    public void onFocusChanged(boolean focused) {
        if (focused) {
            startTextSwitcher();
        } else {
            stopTextSwitcher();
        }
    }

    private void startTextSwitcher() {
        if (this.mTotalTimeView != null) {
            this.mRunTextSwitcher = true;
            if (this.mHandler.hasMessages(SWITCH_ANIMATOR_START)) {
                this.mHandler.removeMessages(SWITCH_ANIMATOR_START);
            }
            this.mHandler.sendEmptyMessageDelayed(SWITCH_ANIMATOR_START, 3000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStartSwitchAnimator() {
        if (this.mRunTextSwitcher) {
            updateTravelTimeView(true);
            if (this.mAnimatorSet != null) {
                this.mTotalTimeView.showNext();
            }
            final View currentView = this.mTotalTimeView.getCurrentView();
            final View nextView = this.mTotalTimeView.getNextView();
            ObjectAnimator currentOutAnimator = ObjectAnimator.ofFloat(currentView, "translationY", 0.0f, -this.mTotalTimeView.getHeight());
            ObjectAnimator nextInAnimator = ObjectAnimator.ofFloat(nextView, "translationY", this.mTotalTimeView.getHeight(), 0.0f);
            this.mAnimatorSet = new AnimatorSet();
            this.mAnimatorSet.setDuration(1000L);
            this.mAnimatorSet.playTogether(currentOutAnimator, nextInAnimator);
            this.mAnimatorSet.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.2
                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    currentView.setTranslationY(0.0f);
                    currentView.setVisibility(8);
                    nextView.setTranslationY(0.0f);
                    currentView.setDrawingCacheEnabled(false);
                    nextView.setDrawingCacheEnabled(false);
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    currentView.setTranslationY(0.0f);
                    currentView.setVisibility(8);
                    currentView.setDrawingCacheEnabled(false);
                    nextView.setDrawingCacheEnabled(false);
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animation) {
                }

                @Override // android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    currentView.setVisibility(0);
                    nextView.setVisibility(0);
                    currentView.setDrawingCacheEnabled(true);
                    nextView.setDrawingCacheEnabled(true);
                    nextView.setTranslationY(RouteDetailView.this.mTotalTimeView.getHeight());
                }
            });
            this.mAnimatorSet.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTextSwitcher();
    }

    private void stopTextSwitcher() {
        this.mRunTextSwitcher = false;
        AnimatorSet animatorSet = this.mAnimatorSet;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mAnimatorSet.cancel();
        }
        this.mAnimatorSet = null;
        if (this.mHandler.hasMessages(SWITCH_ANIMATOR_START)) {
            this.mHandler.removeMessages(SWITCH_ANIMATOR_START);
        }
        this.mTotalTimeView.setDisplayedChild(0);
        this.mTotalTimeView.getChildAt(0).setVisibility(0);
        this.mTotalTimeView.getChildAt(1).setVisibility(8);
    }

    public void bindData(RouteSelectBean selectBean, int position) {
        int elecPercent;
        Log.e(TAG, "bindData: " + GsonUtil.toJson(selectBean));
        if (this.mIsSingle) {
            this.mTotalTimeView.setVisibility(8);
            this.mSingleTotalTimeView.setVisibility(0);
        } else {
            this.mSingleTotalTimeView.setVisibility(8);
            this.mTotalTimeView.setVisibility(0);
        }
        this.mRouteLeftDistance = selectBean.routeLeftDistanceValue;
        this.mRouteTypeView.setText(selectBean.routeTypeName);
        this.mRouteTypeNoView.setText(selectBean.routeTypeNo);
        this.mPassedDistanceView2.setText(selectBean.remainDistance);
        this.mTrafficSigView.setText(selectBean.trafficSignal);
        this.mTrafficCostView.setText(selectBean.trafficCost);
        this.mTravelTime = selectBean.totalTimeLine1Value;
        updateRouteTypeContent(position);
        updateTimeView(this.mTravelTime, true);
        this.mLightCount = TextUtils.isEmpty(selectBean.trafficSignal) ? 0L : Long.parseLong(selectBean.trafficSignal);
        this.mTollCost = TextUtils.isEmpty(selectBean.trafficCost) ? 0L : Long.parseLong(selectBean.trafficCost);
        String mTrafficSigStr = getContext().getString(R.string.route_detail_traffic_signal, Long.valueOf(this.mLightCount));
        String mTrafficCostStr = getContext().getString(R.string.route_detail_total_tll_cost, Long.valueOf(this.mTollCost));
        updateLeftDistanceView(this.mRouteLeftDistance);
        if (TextUtils.isEmpty(mTrafficSigStr) || this.mLightCount == 0) {
            this.mTrafficSigView.setVisibility(8);
        } else {
            this.mTrafficSigView.setText(mTrafficSigStr);
        }
        if (TextUtils.isEmpty(mTrafficCostStr) || this.mTollCost == 0) {
            this.mTrafficCostView.setVisibility(8);
        } else {
            this.mTrafficCostView.setText(mTrafficCostStr);
        }
        this.mBatteryViewStub.setOnInflateListener(new ViewStub.OnInflateListener() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.3
            @Override // android.view.ViewStub.OnInflateListener
            public void onInflate(ViewStub stub, View inflated) {
                RouteDetailView.this.mInflated = true;
            }
        });
        long carRemainDis = CarControlManager.getInstance(getContext()).getDriveDistance() * 1000;
        int elecPercent2 = CarControlManager.getInstance(getContext()).getElecPercent();
        Log.e(TAG, "elecPercent: " + elecPercent2);
        if (elecPercent2 != 0) {
            elecPercent = elecPercent2;
        } else {
            elecPercent = 1;
        }
        long totalDis = (100 * carRemainDis) / elecPercent;
        onUpdateRemainDis(carRemainDis, (int) selectBean.remainDistanceValue, selectBean.batteryStatus, (int) totalDis);
    }

    private void initView() {
        this.mXpFontTagHandler = new XpFontTagHandler(this.mContext.getResources().getDisplayMetrics());
        this.mRouteTypeView = (AnimatedTextView) findViewById(R.id.tv_title);
        this.mRouteTypeNoView = (AnimatedTextView) findViewById(R.id.tv_index);
        this.mTotalTimeView = (TextSwitcher) findViewById(R.id.total_time);
        this.mSingleTotalTimeView = (AlphaOptimizedLinearLayout) findViewById(R.id.single_total_time);
        this.mLeftDistanceView = (AnimatedTextView) findViewById(R.id.tv_subtitle);
        this.mPassedDistanceView1 = (AnimatedTextView) findViewById(R.id.tv_battery_title);
        this.mPassedDistanceView2 = (AnimatedTextView) findViewById(R.id.tv_battery_content);
        this.mTrafficSigView = (AnimatedTextView) findViewById(R.id.tv_num_light);
        this.mTrafficCostView = (AnimatedTextView) findViewById(R.id.tv_money);
        this.mBatteryViewStub = (ViewStub) findViewById(R.id.img_battery);
    }

    protected void updateLeftDistanceView(long leftDistance) {
        float showLeftDistance = Math.round((float) (leftDistance / 100)) / 10.0f;
        this.mLeftDistanceView.setText(String.format("%.1f", Float.valueOf(showLeftDistance)));
    }

    protected void updateTimeView(long travelTime, boolean needSync) {
        AsyncTask<Long, Integer, Spanned> execute = new AsyncTask<Long, Integer, Spanned>() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            @SuppressLint({"WrongThread"})
            public Spanned doInBackground(Long... travelTime2) {
                long time = travelTime2[0].longValue();
                return RouteDetailView.this.getTimeSpanned(time);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Spanned spanned) {
                RouteDetailView.this.setTimeView(spanned);
            }
        };
        if (needSync) {
            execute.execute(Long.valueOf(travelTime));
        } else {
            Spanned spanned = getTimeSpanned(travelTime);
            setTimeView(spanned);
        }
        updateTravelTimeView(needSync);
    }

    private void updateTravelTimeView(boolean needSync) {
        AsyncTask<Long, Integer, Spanned> execute = new AsyncTask<Long, Integer, Spanned>() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.5
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            @SuppressLint({"WrongThread"})
            public Spanned doInBackground(Long... travelTime) {
                return RouteDetailView.this.getTravelTimeSpanned();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Spanned spanned) {
                RouteDetailView.this.setTravelTimeView(spanned);
            }
        };
        if (!needSync) {
            Spanned spanned = getTravelTimeSpanned();
            setTravelTimeView(spanned);
            return;
        }
        execute.execute(0L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Spanned getTravelTimeSpanned() {
        Time todayTime = new Time();
        todayTime.setToNow();
        Time toTime = new Time();
        toTime.set(System.currentTimeMillis() + (this.mTravelTime * 1000));
        int dDay = toTime.yearDay - todayTime.yearDay;
        String formatter = getTimeString(this.mIsSingle, dDay);
        return Html.fromHtml(toTime.format(formatter), null, this.mXpFontTagHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTravelTimeView(Spanned spanned) {
        ViewGroup parent = this.mTotalTimeView;
        if (this.mIsSingle) {
            parent = this.mSingleTotalTimeView;
        }
        ((TextView) parent.getChildAt(1)).setText(spanned);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimeView(Spanned spanned) {
        ViewGroup parent = this.mTotalTimeView;
        if (this.mIsSingle) {
            parent = this.mSingleTotalTimeView;
        }
        ((TextView) parent.getChildAt(0)).setText(spanned);
    }

    protected Spanned getTimeSpanned(long time) {
        String timeString = NaviUtil.getTimeXmlString(time, getResources().getDimensionPixelSize(R.dimen.font_size_56), getResources().getDimensionPixelSize(R.dimen.font_size_30));
        return Html.fromHtml(timeString, null, this.mXpFontTagHandler);
    }

    protected void updateRouteTypeContent(int index) {
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private String getTimeString(boolean isSingle, int dDay) {
        if (dDay == 0) {
            if (isSingle) {
                String formatStr = getContext().getString(R.string.route_estimated_time_format_1_forbignum_single);
                return formatStr;
            }
            String formatStr2 = getContext().getString(R.string.route_estimated_time_format_1_forbignum);
            return formatStr2;
        } else if (dDay == 1) {
            if (isSingle) {
                String formatStr3 = getContext().getString(R.string.route_estimated_time_format_2_forbignum_single);
                return formatStr3;
            }
            String formatStr4 = getContext().getString(R.string.route_estimated_time_format_2_forbignum);
            return formatStr4;
        } else if (isSingle) {
            String formatStr5 = getContext().getString(R.string.route_estimated_time_format_3_forbignum_single);
            return formatStr5;
        } else {
            String formatStr6 = getContext().getString(R.string.route_estimated_time_format_3_forbignum);
            return formatStr6;
        }
    }

    public void onUpdateRemainDis(final long carRemainDis, final int remainDis, final int status, final int totalDis) {
        Log.d(TAG, "onUpdateRemainDis() called with: carRemainDis = [" + carRemainDis + "], remainDis = [" + remainDis + "], status = [" + status + "], totalDis = [" + totalDis + NavigationBarInflaterView.SIZE_MOD_END);
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.montecarlo.view.RouteDetailView.6
            @Override // java.lang.Runnable
            public void run() {
                RouteDetailView.this.fillDistanceView(carRemainDis, remainDis, status, totalDis);
            }
        });
    }

    protected void fillDistanceView(long carRemainDis, int remainDis, int status, int totalDis) {
        Log.d(TAG, "fillDistanceView: status = " + status);
        updateBatterStatusColor(carRemainDis, remainDis);
        String remainText = getContext().getString(R.string.route_detail_endurance_distance_remain_unit, Integer.valueOf(remainDis / 1000));
        if (status == -1) {
            this.mPassedDistanceView2.setVisibility(4);
            this.mPassedDistanceView1.setVisibility(4);
            BatteryView batteryView = this.mBatteryView;
            if (batteryView != null) {
                batteryView.setVisibility(4);
            }
        } else if (status == 0) {
            this.mPassedDistanceView2.setVisibility(4);
            this.mPassedDistanceView1.setVisibility(0);
            this.mPassedDistanceView1.setText(R.string.route_detail_endurance_insufficient_noreache);
            BatteryView batteryView2 = this.mBatteryView;
            if (batteryView2 != null) {
                batteryView2.setVisibility(0);
            }
            updateBatteryStatus(carRemainDis, remainDis, totalDis);
        } else if (status != 1) {
            if (status == 2) {
                this.mPassedDistanceView1.setVisibility(4);
                this.mPassedDistanceView2.setVisibility(0);
                this.mPassedDistanceView2.setText(R.string.route_detail_endurance_lower_power);
            }
        } else {
            this.mPassedDistanceView1.setVisibility(0);
            this.mPassedDistanceView2.setVisibility(0);
            this.mPassedDistanceView1.setText(R.string.route_detail_endurance_distance_remain);
            this.mPassedDistanceView2.setText(remainText);
            BatteryView batteryView3 = this.mBatteryView;
            if (batteryView3 != null) {
                batteryView3.setVisibility(0);
            }
            updateBatteryStatus(carRemainDis, remainDis, totalDis);
        }
    }

    protected void updateBatterStatusColor(long carRemainDis, int remainDis) {
        ColorStateList remainColor = NaviUtil.checkColorStatus(carRemainDis, remainDis);
        this.mPassedDistanceView1.setVisibility(0);
        this.mPassedDistanceView1.setTextColor(remainColor);
        this.mPassedDistanceView2.setTextColor(remainColor);
    }

    private void updateBatteryStatus(long carRemainDis, int remainDis, int totalDis) {
        ViewStub viewStub;
        if (!this.mInflated && (viewStub = this.mBatteryViewStub) != null) {
            this.mBatteryView = (BatteryView) viewStub.inflate();
        }
        BatteryView batteryView = this.mBatteryView;
        if (batteryView != null) {
            batteryView.updateBatteryStatus((int) carRemainDis, remainDis, this.mRouteLeftDistance, totalDis);
            this.mBatteryView.setSelected(isSelected());
        }
    }
}
