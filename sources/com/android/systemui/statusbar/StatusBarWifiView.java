package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
/* loaded from: classes21.dex */
public class StatusBarWifiView extends FrameLayout implements DarkIconDispatcher.DarkReceiver, StatusIconDisplayable {
    private static final String TAG = "StatusBarWifiView";
    private View mAirplaneSpacer;
    private StatusBarIconView mDotView;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mOut;
    private View mSignalSpacer;
    private String mSlot;
    private StatusBarSignalPolicy.WifiIconState mState;
    private int mVisibleState;
    private LinearLayout mWifiGroup;
    private ImageView mWifiIcon;

    public static StatusBarWifiView fromContext(Context context, String slot) {
        LayoutInflater inflater = LayoutInflater.from(context);
        StatusBarWifiView v = (StatusBarWifiView) inflater.inflate(R.layout.status_bar_wifi_group, (ViewGroup) null);
        v.setSlot(slot);
        v.init();
        v.setVisibleState(0);
        return v;
    }

    public StatusBarWifiView(Context context) {
        super(context);
        this.mVisibleState = -1;
    }

    public StatusBarWifiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mVisibleState = -1;
    }

    public StatusBarWifiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mVisibleState = -1;
    }

    public StatusBarWifiView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mVisibleState = -1;
    }

    public void setSlot(String slot) {
        this.mSlot = slot;
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setStaticDrawableColor(int color) {
        ColorStateList list = ColorStateList.valueOf(color);
        this.mWifiIcon.setImageTintList(list);
        this.mIn.setImageTintList(list);
        this.mOut.setImageTintList(list);
        this.mDotView.setDecorColor(color);
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setDecorColor(int color) {
        this.mDotView.setDecorColor(color);
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public String getSlot() {
        return this.mSlot;
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public boolean isIconVisible() {
        StatusBarSignalPolicy.WifiIconState wifiIconState = this.mState;
        return wifiIconState != null && wifiIconState.visible;
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setVisibleState(int state, boolean animate) {
        if (state == this.mVisibleState) {
            return;
        }
        this.mVisibleState = state;
        if (state == 0) {
            this.mWifiGroup.setVisibility(0);
            this.mDotView.setVisibility(8);
        } else if (state == 1) {
            this.mWifiGroup.setVisibility(8);
            this.mDotView.setVisibility(0);
        } else {
            this.mWifiGroup.setVisibility(8);
            this.mDotView.setVisibility(8);
        }
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public int getVisibleState() {
        return this.mVisibleState;
    }

    @Override // android.view.View
    public void getDrawingRect(Rect outRect) {
        super.getDrawingRect(outRect);
        float translationX = getTranslationX();
        float translationY = getTranslationY();
        outRect.left = (int) (outRect.left + translationX);
        outRect.right = (int) (outRect.right + translationX);
        outRect.top = (int) (outRect.top + translationY);
        outRect.bottom = (int) (outRect.bottom + translationY);
    }

    private void init() {
        this.mWifiGroup = (LinearLayout) findViewById(R.id.wifi_group);
        this.mWifiIcon = (ImageView) findViewById(R.id.wifi_signal);
        this.mIn = (ImageView) findViewById(R.id.wifi_in);
        this.mOut = (ImageView) findViewById(R.id.wifi_out);
        this.mSignalSpacer = findViewById(R.id.wifi_signal_spacer);
        this.mAirplaneSpacer = findViewById(R.id.wifi_airplane_spacer);
        this.mInoutContainer = findViewById(R.id.inout_container);
        initDotView();
    }

    private void initDotView() {
        this.mDotView = new StatusBarIconView(this.mContext, this.mSlot, null);
        this.mDotView.setVisibleState(1);
        int width = this.mContext.getResources().getDimensionPixelSize(R.dimen.status_bar_icon_size);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width);
        lp.gravity = 8388627;
        addView(this.mDotView, lp);
    }

    public void applyWifiState(StatusBarSignalPolicy.WifiIconState state) {
        boolean requestLayout = false;
        if (state == null) {
            requestLayout = getVisibility() != 8;
            setVisibility(8);
            this.mState = null;
        } else {
            StatusBarSignalPolicy.WifiIconState wifiIconState = this.mState;
            if (wifiIconState == null) {
                requestLayout = true;
                this.mState = state.copy();
                initViewState();
            } else if (!wifiIconState.equals(state)) {
                requestLayout = updateState(state.copy());
            }
        }
        if (requestLayout) {
            requestLayout();
        }
    }

    private boolean updateState(StatusBarSignalPolicy.WifiIconState state) {
        setContentDescription(state.contentDescription);
        if (this.mState.resId != state.resId && state.resId >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(state.resId));
        }
        this.mIn.setVisibility(state.activityIn ? 0 : 8);
        this.mOut.setVisibility(state.activityOut ? 0 : 8);
        this.mInoutContainer.setVisibility((state.activityIn || state.activityOut) ? 0 : 8);
        this.mAirplaneSpacer.setVisibility(state.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(state.signalSpacerVisible ? 0 : 8);
        boolean needsLayout = (state.activityIn == this.mState.activityIn && state.activityOut == this.mState.activityOut) ? false : true;
        if (this.mState.visible != state.visible) {
            needsLayout |= true;
            setVisibility(state.visible ? 0 : 8);
        }
        this.mState = state;
        return needsLayout;
    }

    private void initViewState() {
        setContentDescription(this.mState.contentDescription);
        if (this.mState.resId >= 0) {
            this.mWifiIcon.setImageDrawable(this.mContext.getDrawable(this.mState.resId));
        }
        this.mIn.setVisibility(this.mState.activityIn ? 0 : 8);
        this.mOut.setVisibility(this.mState.activityOut ? 0 : 8);
        this.mInoutContainer.setVisibility((this.mState.activityIn || this.mState.activityOut) ? 0 : 8);
        this.mAirplaneSpacer.setVisibility(this.mState.airplaneSpacerVisible ? 0 : 8);
        this.mSignalSpacer.setVisibility(this.mState.signalSpacerVisible ? 0 : 8);
        setVisibility(this.mState.visible ? 0 : 8);
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        if (!DarkIconDispatcher.isInArea(area, this)) {
            return;
        }
        this.mWifiIcon.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(area, this, tint)));
        this.mIn.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(area, this, tint)));
        this.mOut.setImageTintList(ColorStateList.valueOf(DarkIconDispatcher.getTint(area, this, tint)));
        this.mDotView.setDecorColor(tint);
        this.mDotView.setIconColor(tint, false);
    }

    @Override // android.view.View
    public String toString() {
        return "StatusBarWifiView(slot=" + this.mSlot + " state=" + this.mState + NavigationBarInflaterView.KEY_CODE_END;
    }
}
