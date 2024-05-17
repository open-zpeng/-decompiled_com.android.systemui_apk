package com.xiaopeng.systemui.infoflow.montecarlo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.montecarlo.view.NaviCardView;
/* loaded from: classes24.dex */
public class NaviRootView extends RelativeLayout implements ContextManager.OnNaviModeChangedListener {
    private static final String TAG = "NaviRootView";
    private ContextManager mContextManager;
    private NaviCardView mNaviCardView;
    private NaviViewContainer mNaviViewContainer;

    public NaviRootView(Context context) {
        this(context, null);
    }

    public NaviRootView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NaviRootView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContextManager = ContextManager.getInstance();
    }

    public void setupWithContainer(NaviViewContainer naviViewContainer) {
        this.mNaviViewContainer = naviViewContainer;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContextManager.setOnNaviModeChangeListener(this);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mNaviCardView = (NaviCardView) findViewById(R.id.view_navi_card);
    }

    @Override // com.xiaopeng.systemui.infoflow.manager.ContextManager.OnNaviModeChangedListener
    public void onNaviModeChanged(int currentMode) {
        this.mNaviCardView.setNaviMode(currentMode);
        if (currentMode == 0) {
            this.mNaviCardView.resetStatus();
            this.mNaviCardView.setVisibility(8);
            NaviViewContainer naviViewContainer = this.mNaviViewContainer;
            if (naviViewContainer != null) {
                naviViewContainer.exitNaviMode();
                return;
            }
            return;
        }
        this.mNaviViewContainer.enterNaviMode();
        this.mNaviCardView.setVisibility(0);
    }
}
