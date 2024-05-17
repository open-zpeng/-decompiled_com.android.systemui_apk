package com.xiaopeng.systemui.statusbar.MaskLayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.montecarlo.util.ContextUtils;
import com.xiaopeng.systemui.statusbar.IWatermarkView;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes24.dex */
public class WatermarkView extends FrameLayout implements IWatermarkView {
    private static final String TAG = "WatermarkView";
    private XTextView mAccount;
    View mRootView;
    private XTextView mTitle;
    private XTextView mVIN;
    private FrameLayout mWatermarklayout;

    public WatermarkView(Context context) {
        super(ContextUtils.getContext());
        init(context);
    }

    private void init(Context context) {
        Logger.d(TAG, "WatermarkView init ");
        initView(context);
    }

    private void initView(Context context) {
        Log.i(TAG, "onCreate");
        this.mRootView = LayoutInflater.from(context).inflate(R.layout.watermark_view, (ViewGroup) null);
        this.mRootView.setBackgroundColor(getResources().getColor(R.color.watermark_bg_color));
        addView(this.mRootView);
        this.mWatermarklayout = (FrameLayout) this.mRootView.findViewById(R.id.watermark_layout);
        this.mTitle = (XTextView) this.mWatermarklayout.findViewById(R.id.watermark_title);
        this.mVIN = (XTextView) this.mWatermarklayout.findViewById(R.id.watermark_VIN);
        this.mAccount = (XTextView) this.mWatermarklayout.findViewById(R.id.watermark_account);
    }

    @Override // com.xiaopeng.systemui.statusbar.IWatermarkView
    public void setRepairMode(String VINNumber, String AccountNumber) {
        this.mTitle.setText(R.string.watermark_title_repair_mode);
        this.mVIN.setText(VINNumber);
        this.mAccount.setText(AccountNumber);
        this.mAccount.setVisibility(0);
    }

    @Override // com.xiaopeng.systemui.statusbar.IWatermarkView
    public void setDiagnosticMode(String VINNumber) {
        this.mVIN.setText(VINNumber);
        this.mAccount.setVisibility(8);
        this.mTitle.setText(R.string.watermark_title_diagnostic_mode);
    }
}
