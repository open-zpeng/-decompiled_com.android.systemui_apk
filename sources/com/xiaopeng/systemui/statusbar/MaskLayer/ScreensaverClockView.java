package com.xiaopeng.systemui.statusbar.MaskLayer;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xui.widget.XFrameLayout;
import com.xiaopeng.xui.widget.XTextView;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes24.dex */
public class ScreensaverClockView extends XFrameLayout {
    private static final String ACTION_SCREENSAVER_EXIT = "com.xiaopeng.broadcast.ACTION_EXIT_SCREENSAVER";
    private static final String TAG = "ScreensaverClockView";
    private float mAnimAlphaStart;
    private int mAnimTime;
    protected Context mContext;
    View mRootView;
    private boolean mTimeFormat;
    private XTextView tvDate;
    private XTextView tvDay;
    private XTextView tvMonth;
    private XTextView tvTime;
    private XTextView tvWeek;
    private XTextView txNoon;

    public ScreensaverClockView(Context context) {
        super(ContextUtils.getContext());
        this.mAnimTime = 500;
        this.mAnimAlphaStart = 0.0f;
        init(context);
    }

    private void init(Context context) {
        Logger.d(TAG, "ScreensaverClockView init ");
        this.mContext = ContextUtils.getContext();
        initView(context);
        this.mTimeFormat = DateFormat.is24HourFormat(context);
    }

    private void initView(Context context) {
        Log.i(TAG, "onCreate");
        this.mRootView = LayoutInflater.from(context).inflate(R.layout.layout_screensaver_clock, (ViewGroup) null);
        this.mRootView.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.-$$Lambda$ScreensaverClockView$5_T_pvioILSzIggV5eMWYR2Xy_k
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ScreensaverClockView.this.lambda$initView$1$ScreensaverClockView(view);
            }
        });
        this.txNoon = (XTextView) this.mRootView.findViewById(R.id.txt_time_noon);
        this.tvTime = (XTextView) this.mRootView.findViewById(R.id.txt_time);
        this.tvDay = (XTextView) this.mRootView.findViewById(R.id.txt_day);
        this.tvMonth = (XTextView) this.mRootView.findViewById(R.id.txt_month);
        this.tvWeek = (XTextView) this.mRootView.findViewById(R.id.txt_week);
        updateTime();
    }

    public /* synthetic */ void lambda$initView$1$ScreensaverClockView(View v) {
        final Intent intent = new Intent();
        intent.setAction("com.xiaopeng.broadcast.ACTION_EXIT_SCREENSAVER");
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.MaskLayer.-$$Lambda$ScreensaverClockView$qpAmN9TqieXO__xsU4LV-TY2GZs
            @Override // java.lang.Runnable
            public final void run() {
                ScreensaverClockView.this.lambda$initView$0$ScreensaverClockView(intent);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$ScreensaverClockView(Intent intent) {
        this.mContext.sendBroadcast(intent);
        Logger.d(TAG, "ACTION_SCREENSAVER_EXIT clicked");
    }

    public void updateTime() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        SimpleDateFormat formatDay = new SimpleDateFormat(this.mContext.getString(R.string.screen_saver_data_format));
        String[] timeArray = formatDay.format(date).split(" ");
        this.tvMonth.setText(timeArray[1]);
        this.tvDay.setText(timeArray[2]);
        this.tvWeek.setText(timeArray[5]);
        if (this.mTimeFormat) {
            this.txNoon.setVisibility(8);
            this.tvTime.setText(timeArray[3]);
            return;
        }
        this.txNoon.setText(timeArray[0]);
        this.tvTime.setText(timeArray[4]);
        this.txNoon.setVisibility(0);
    }

    public void onTimeFormatChanged() {
        this.mTimeFormat = DateFormat.is24HourFormat(this.mContext);
        updateTime();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void animationIn() {
        Animation animationIn = new AlphaAnimation(this.mAnimAlphaStart, 1.0f);
        animationIn.setDuration(this.mAnimTime);
        this.mRootView.setAnimation(animationIn);
        this.mRootView.setBackgroundResource(R.drawable.x_screensaver_bg);
        addView(this.mRootView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void animationOut() {
        Animation animationOut = new AlphaAnimation(1.0f, this.mAnimAlphaStart);
        animationOut.setDuration(this.mAnimTime);
        this.mRootView.setAnimation(animationOut);
        removeView(this.mRootView);
    }
}
