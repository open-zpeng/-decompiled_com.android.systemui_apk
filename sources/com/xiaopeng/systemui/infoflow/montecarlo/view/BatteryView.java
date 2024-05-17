package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.montecarlo.util.NaviUtil;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedRelativeLayout;
/* loaded from: classes24.dex */
public class BatteryView extends AlphaOptimizedRelativeLayout {
    public static final int BETTERY_NORMAL = 0;
    public static final int BETTERY_REDWARNING = 2;
    public static final int BETTERY_WARNING = 1;
    private static final String TAG = "BatteryView";
    private float mBatteryHeight;
    private int mBatteryNormalBackground;
    private int mBatteryProgress;
    private float mBatteryProgressMarginBottom;
    private float mBatteryProgressMarginLeft;
    private float mBatteryProgressMarginRight;
    private float mBatteryProgressMarginTop;
    private int mBatteryRedWarningBackground;
    private int mBatterySecondaryProgress;
    private int mBatteryState;
    private int mBatteryWarningBackground;
    private float mBatteryWidth;
    private Context mContext;
    private ProgressBar mProgressBar;
    private RelativeLayout mRootView;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BatteryView, defStyleAttr, 0);
        if (a != null) {
            try {
                try {
                    this.mBatteryWidth = a.getDimension(11, 0.0f);
                    this.mBatteryHeight = a.getDimension(0, 0.0f);
                    this.mBatteryNormalBackground = a.getResourceId(1, 0);
                    this.mBatteryWarningBackground = a.getResourceId(10, 0);
                    this.mBatteryRedWarningBackground = a.getResourceId(7, 0);
                    this.mBatteryProgressMarginLeft = a.getDimension(4, 0.0f);
                    this.mBatteryProgressMarginTop = a.getDimension(6, 0.0f);
                    this.mBatteryProgressMarginBottom = a.getDimension(3, 0.0f);
                    this.mBatteryProgressMarginRight = a.getDimension(5, 0.0f);
                    this.mBatteryState = a.getInteger(9, 0);
                    this.mBatteryProgress = a.getInteger(2, 0);
                    this.mBatterySecondaryProgress = a.getInteger(8, 0);
                } catch (Resources.NotFoundException | UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            } finally {
                a.recycle();
            }
        }
        initBatteryView(context);
    }

    public ProgressBar getProgressBar() {
        return this.mProgressBar;
    }

    @Override // android.view.View
    public RelativeLayout getRootView() {
        return this.mRootView;
    }

    public void updateBattery(int state, int progress, int secondaryProgress) {
        this.mBatteryState = state;
        this.mBatteryProgress = progress;
        this.mBatterySecondaryProgress = secondaryProgress;
        Log.d(TAG, "updateBattery() called with: state = [" + state + "], progress = [" + progress + "], secondaryProgress = [" + secondaryProgress + NavigationBarInflaterView.SIZE_MOD_END);
        int i = this.mBatteryState;
        if (i == 0) {
            this.mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.selector_battery_progress_layer));
            this.mRootView.setBackground(getResources().getDrawable(this.mBatteryNormalBackground));
        } else if (1 == i) {
            this.mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_battery_progress_warning));
            this.mRootView.setBackground(getResources().getDrawable(this.mBatteryWarningBackground));
        } else if (2 == i) {
            this.mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.layer_battery_progress_redwarning));
            this.mRootView.setBackground(getResources().getDrawable(this.mBatteryRedWarningBackground));
        }
        this.mProgressBar.setProgress(this.mBatteryProgress);
        this.mProgressBar.setSecondaryProgress(this.mBatterySecondaryProgress);
    }

    public void updateBatteryStatus(int carRemainDis, int remainDis, long routeRemainDis, int totalDis) {
        int distanceLevel = NaviUtil.checkBatteryStatus(carRemainDis, remainDis);
        if (distanceLevel == -1) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        int carRemainProgress = 0;
        int leftRemainProgress = 0;
        Log.d(TAG, ">>> updateBatteryStatus totalDis = " + totalDis);
        if (totalDis != 0) {
            carRemainProgress = (carRemainDis * 100) / totalDis;
            leftRemainProgress = (remainDis * 100) / totalDis;
        }
        Log.d(TAG, ">>> updateBatteryStatus carRemainProgress = " + carRemainProgress + " leftRemainProgress=" + leftRemainProgress);
        updateBattery(distanceLevel, leftRemainProgress, carRemainProgress);
    }

    private void initBatteryView(Context context) {
        this.mContext = context;
        this.mRootView = (RelativeLayout) LayoutInflater.from(this.mContext).inflate(R.layout.layout_battery, this);
        this.mProgressBar = (ProgressBar) this.mRootView.findViewById(R.id.battery_progress);
        formatRootView();
        formatProgressBar();
    }

    private void formatProgressBar() {
        ViewGroup.LayoutParams params;
        if (this.mProgressBar.getLayoutParams() != null) {
            params = this.mProgressBar.getLayoutParams();
        } else {
            params = new ViewGroup.LayoutParams(-1, -2);
        }
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
        if (marginParams.leftMargin == this.mBatteryProgressMarginLeft && marginParams.topMargin == this.mBatteryProgressMarginTop && marginParams.rightMargin == this.mBatteryProgressMarginRight && marginParams.bottomMargin == this.mBatteryProgressMarginBottom) {
            return;
        }
        marginParams.leftMargin = (int) this.mBatteryProgressMarginLeft;
        marginParams.topMargin = (int) this.mBatteryProgressMarginTop;
        marginParams.rightMargin = (int) this.mBatteryProgressMarginRight;
        marginParams.bottomMargin = (int) this.mBatteryProgressMarginBottom;
        this.mProgressBar.setLayoutParams(params);
    }

    private void formatRootView() {
        ViewGroup.LayoutParams params;
        if (this.mRootView.getLayoutParams() != null) {
            params = this.mRootView.getLayoutParams();
        } else {
            params = new ViewGroup.LayoutParams(-2, -2);
        }
        if (params.width == this.mBatteryWidth && params.height == this.mBatteryHeight) {
            return;
        }
        params.width = (int) this.mBatteryWidth;
        params.height = (int) this.mBatteryHeight;
        this.mRootView.setLayoutParams(params);
    }
}
