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
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.DualToneHandler;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.phone.StatusBarSignalPolicy;
/* loaded from: classes21.dex */
public class StatusBarMobileView extends FrameLayout implements DarkIconDispatcher.DarkReceiver, StatusIconDisplayable {
    private static final String TAG = "StatusBarMobileView";
    private StatusBarIconView mDotView;
    private DualToneHandler mDualToneHandler;
    private ImageView mIn;
    private View mInoutContainer;
    private ImageView mMobile;
    private SignalDrawable mMobileDrawable;
    private LinearLayout mMobileGroup;
    private ImageView mMobileRoaming;
    private View mMobileRoamingSpace;
    private ImageView mMobileType;
    private ImageView mOut;
    private String mSlot;
    private StatusBarSignalPolicy.MobileIconState mState;
    private int mVisibleState;

    public static StatusBarMobileView fromContext(Context context, String slot) {
        LayoutInflater inflater = LayoutInflater.from(context);
        StatusBarMobileView v = (StatusBarMobileView) inflater.inflate(R.layout.status_bar_mobile_signal_group, (ViewGroup) null);
        v.setSlot(slot);
        v.init();
        v.setVisibleState(0);
        return v;
    }

    public StatusBarMobileView(Context context) {
        super(context);
        this.mVisibleState = -1;
    }

    public StatusBarMobileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mVisibleState = -1;
    }

    public StatusBarMobileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mVisibleState = -1;
    }

    public StatusBarMobileView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mVisibleState = -1;
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
        this.mDualToneHandler = new DualToneHandler(getContext());
        this.mMobileGroup = (LinearLayout) findViewById(R.id.mobile_group);
        this.mMobile = (ImageView) findViewById(R.id.mobile_signal);
        this.mMobileType = (ImageView) findViewById(R.id.mobile_type);
        this.mMobileRoaming = (ImageView) findViewById(R.id.mobile_roaming);
        this.mMobileRoamingSpace = findViewById(R.id.mobile_roaming_space);
        this.mIn = (ImageView) findViewById(R.id.mobile_in);
        this.mOut = (ImageView) findViewById(R.id.mobile_out);
        this.mInoutContainer = findViewById(R.id.inout_container);
        this.mMobileDrawable = new SignalDrawable(getContext());
        this.mMobile.setImageDrawable(this.mMobileDrawable);
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

    public void applyMobileState(StatusBarSignalPolicy.MobileIconState state) {
        boolean requestLayout = false;
        if (state == null) {
            requestLayout = getVisibility() != 8;
            setVisibility(8);
            this.mState = null;
        } else {
            StatusBarSignalPolicy.MobileIconState mobileIconState = this.mState;
            if (mobileIconState == null) {
                requestLayout = true;
                this.mState = state.copy();
                initViewState();
            } else if (!mobileIconState.equals(state)) {
                requestLayout = updateState(state.copy());
            }
        }
        if (requestLayout) {
            requestLayout();
        }
    }

    private void initViewState() {
        setContentDescription(this.mState.contentDescription);
        int i = 8;
        if (!this.mState.visible) {
            this.mMobileGroup.setVisibility(8);
        } else {
            this.mMobileGroup.setVisibility(0);
        }
        this.mMobileDrawable.setLevel(this.mState.strengthId);
        if (this.mState.typeId > 0) {
            this.mMobileType.setContentDescription(this.mState.typeContentDescription);
            this.mMobileType.setImageResource(this.mState.typeId);
            this.mMobileType.setVisibility(0);
        } else {
            this.mMobileType.setVisibility(8);
        }
        this.mMobileRoaming.setVisibility(this.mState.roaming ? 0 : 8);
        this.mMobileRoamingSpace.setVisibility(this.mState.roaming ? 0 : 8);
        this.mIn.setVisibility(this.mState.activityIn ? 0 : 8);
        this.mOut.setVisibility(this.mState.activityOut ? 0 : 8);
        View view = this.mInoutContainer;
        if (this.mState.activityIn || this.mState.activityOut) {
            i = 0;
        }
        view.setVisibility(i);
    }

    private boolean updateState(StatusBarSignalPolicy.MobileIconState state) {
        boolean needsLayout = false;
        setContentDescription(state.contentDescription);
        int i = 8;
        boolean z = false;
        if (this.mState.visible != state.visible) {
            this.mMobileGroup.setVisibility(state.visible ? 0 : 8);
            needsLayout = true;
        }
        if (this.mState.strengthId != state.strengthId) {
            this.mMobileDrawable.setLevel(state.strengthId);
        }
        if (this.mState.typeId != state.typeId) {
            needsLayout |= state.typeId == 0 || this.mState.typeId == 0;
            if (state.typeId != 0) {
                this.mMobileType.setContentDescription(state.typeContentDescription);
                this.mMobileType.setImageResource(state.typeId);
                this.mMobileType.setVisibility(0);
            } else {
                this.mMobileType.setVisibility(8);
            }
        }
        this.mMobileRoaming.setVisibility(state.roaming ? 0 : 8);
        this.mMobileRoamingSpace.setVisibility(state.roaming ? 0 : 8);
        this.mIn.setVisibility(state.activityIn ? 0 : 8);
        this.mOut.setVisibility(state.activityOut ? 0 : 8);
        View view = this.mInoutContainer;
        if (state.activityIn || state.activityOut) {
            i = 0;
        }
        view.setVisibility(i);
        if (state.roaming != this.mState.roaming || state.activityIn != this.mState.activityIn || state.activityOut != this.mState.activityOut) {
            z = true;
        }
        boolean needsLayout2 = needsLayout | z;
        this.mState = state;
        return needsLayout2;
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        if (!DarkIconDispatcher.isInArea(area, this)) {
            return;
        }
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(darkIntensity)));
        ColorStateList color = ColorStateList.valueOf(DarkIconDispatcher.getTint(area, this, tint));
        this.mIn.setImageTintList(color);
        this.mOut.setImageTintList(color);
        this.mMobileType.setImageTintList(color);
        this.mMobileRoaming.setImageTintList(color);
        this.mDotView.setDecorColor(tint);
        this.mDotView.setIconColor(tint, false);
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public String getSlot() {
        return this.mSlot;
    }

    public void setSlot(String slot) {
        this.mSlot = slot;
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setStaticDrawableColor(int color) {
        ColorStateList list = ColorStateList.valueOf(color);
        float intensity = color == -1 ? 0.0f : 1.0f;
        this.mMobileDrawable.setTintList(ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(intensity)));
        this.mIn.setImageTintList(list);
        this.mOut.setImageTintList(list);
        this.mMobileType.setImageTintList(list);
        this.mMobileRoaming.setImageTintList(list);
        this.mDotView.setDecorColor(color);
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setDecorColor(int color) {
        this.mDotView.setDecorColor(color);
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public boolean isIconVisible() {
        return this.mState.visible;
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public void setVisibleState(int state, boolean animate) {
        if (state == this.mVisibleState) {
            return;
        }
        this.mVisibleState = state;
        if (state == 0) {
            this.mMobileGroup.setVisibility(0);
            this.mDotView.setVisibility(8);
        } else if (state == 1) {
            this.mMobileGroup.setVisibility(4);
            this.mDotView.setVisibility(0);
        } else {
            this.mMobileGroup.setVisibility(4);
            this.mDotView.setVisibility(4);
        }
    }

    @Override // com.android.systemui.statusbar.StatusIconDisplayable
    public int getVisibleState() {
        return this.mVisibleState;
    }

    @VisibleForTesting
    public StatusBarSignalPolicy.MobileIconState getState() {
        return this.mState;
    }

    @Override // android.view.View
    public String toString() {
        return "StatusBarMobileView(slot=" + this.mSlot + " state=" + this.mState + NavigationBarInflaterView.KEY_CODE_END;
    }
}
