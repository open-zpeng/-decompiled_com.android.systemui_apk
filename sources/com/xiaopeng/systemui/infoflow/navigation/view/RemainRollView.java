package com.xiaopeng.systemui.infoflow.navigation.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
/* loaded from: classes24.dex */
public class RemainRollView extends AlphaOptimizedRelativeLayout {
    private static final String TAG = RemainRollView.class.getSimpleName();
    private final int DURATION_ANIMATE;
    private final int DURATION_SHOW;
    private Handler mHandler;
    private RemainBatteryInfoView mRemainBatteryInfoView;
    private RemainInfoView mRemainInfoView;

    public RemainRollView(Context context) {
        super(context);
        this.DURATION_SHOW = 5000;
        this.DURATION_ANIMATE = 500;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        init();
    }

    public RemainRollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.DURATION_SHOW = 5000;
        this.DURATION_ANIMATE = 500;
        this.mHandler = new Handler() { // from class: com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        init();
    }

    private void init() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView.2
            @Override // java.lang.Runnable
            public void run() {
                RemainRollView.this.showBatteryInfo();
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBatteryInfo() {
        this.mRemainBatteryInfoView.animate().alpha(1.0f).setDuration(500L).start();
        this.mRemainInfoView.animate().alpha(0.0f).setDuration(500L).start();
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView.3
            @Override // java.lang.Runnable
            public void run() {
                RemainRollView.this.showRemainBaseInfo();
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showRemainBaseInfo() {
        this.mRemainInfoView.animate().alpha(1.0f).setDuration(500L).start();
        this.mRemainBatteryInfoView.animate().alpha(0.0f).setDuration(500L).start();
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.navigation.view.RemainRollView.4
            @Override // java.lang.Runnable
            public void run() {
                RemainRollView.this.showBatteryInfo();
            }
        }, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages(null);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mRemainInfoView = (RemainInfoView) findViewById(R.id.view_remind_info);
        this.mRemainBatteryInfoView = (RemainBatteryInfoView) findViewById(R.id.view_remain_battery);
    }

    public void setRemainInfoData(RemainInfo remainInfo) {
        this.mRemainBatteryInfoView.setData(remainInfo);
    }

    public void setNaviRemainInfo(String routeRemainDistDisplay, int routeRemainDistUnitDisplaydouble, double routerRemainTime) {
        this.mRemainInfoView.setNaviRemainInfo(routeRemainDistDisplay, routeRemainDistUnitDisplaydouble, routerRemainTime);
        this.mRemainBatteryInfoView.setRouterRemainTime(routerRemainTime);
    }
}
