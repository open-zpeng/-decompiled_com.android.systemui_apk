package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.Lifecycle;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.LifecycleFragment;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class QSFragment extends LifecycleFragment implements QS, CommandQueue.Callbacks, StatusBarStateController.StateListener {
    private static final boolean DEBUG = false;
    private static final String EXTRA_EXPANDED = "expanded";
    private static final String EXTRA_LISTENING = "listening";
    private static final String TAG = "QS";
    private QSContainerImpl mContainer;
    private long mDelay;
    private QSFooter mFooter;
    protected QuickStatusBarHeader mHeader;
    private boolean mHeaderAnimating;
    private final QSTileHost mHost;
    private final InjectionInflationController mInjectionInflater;
    private boolean mLastKeyguardAndExpanded;
    private int mLayoutDirection;
    private boolean mListening;
    private QS.HeightListener mPanelView;
    private QSAnimator mQSAnimator;
    private QSCustomizer mQSCustomizer;
    private QSDetail mQSDetail;
    protected QSPanel mQSPanel;
    private boolean mQsDisabled;
    private boolean mQsExpanded;
    private final RemoteInputQuickSettingsDisabler mRemoteInputQuickSettingsDisabler;
    private boolean mShowCollapsedOnKeyguard;
    private boolean mStackScrollerOverscrolling;
    private int mState;
    private final StatusBarStateController mStatusBarStateController;
    private final Rect mQsBounds = new Rect();
    private float mLastQSExpansion = -1.0f;
    private final ViewTreeObserver.OnPreDrawListener mStartHeaderSlidingIn = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.qs.QSFragment.2
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            QSFragment.this.getView().getViewTreeObserver().removeOnPreDrawListener(this);
            QSFragment.this.getView().animate().translationY(0.0f).setStartDelay(QSFragment.this.mDelay).setDuration(448L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(QSFragment.this.mAnimateHeaderSlidingInListener).start();
            return true;
        }
    };
    private final Animator.AnimatorListener mAnimateHeaderSlidingInListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSFragment.3
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            QSFragment.this.mHeaderAnimating = false;
            QSFragment.this.updateQsState();
        }
    };

    @Inject
    public QSFragment(RemoteInputQuickSettingsDisabler remoteInputQsDisabler, InjectionInflationController injectionInflater, Context context, QSTileHost qsTileHost, StatusBarStateController statusBarStateController) {
        this.mRemoteInputQuickSettingsDisabler = remoteInputQsDisabler;
        this.mInjectionInflater = injectionInflater;
        ((CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class)).observe(getLifecycle(), (Lifecycle) this);
        this.mHost = qsTileHost;
        this.mStatusBarStateController = statusBarStateController;
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return this.mInjectionInflater.injectable(inflater.cloneInContext(new ContextThemeWrapper(getContext(), R.style.qs_theme))).inflate(R.layout.qs_panel, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mQSPanel = (QSPanel) view.findViewById(R.id.quick_settings_panel);
        this.mQSDetail = (QSDetail) view.findViewById(R.id.qs_detail);
        this.mHeader = (QuickStatusBarHeader) view.findViewById(R.id.header);
        this.mFooter = (QSFooter) view.findViewById(R.id.qs_footer);
        this.mContainer = (QSContainerImpl) view.findViewById(R.id.quick_settings_container);
        this.mQSDetail.setQsPanel(this.mQSPanel, this.mHeader, (View) this.mFooter);
        this.mQSAnimator = new QSAnimator(this, (QuickQSPanel) this.mHeader.findViewById(R.id.quick_qs_panel), this.mQSPanel);
        this.mQSCustomizer = (QSCustomizer) view.findViewById(R.id.qs_customize);
        this.mQSCustomizer.setQs(this);
        if (savedInstanceState != null) {
            setExpanded(savedInstanceState.getBoolean(EXTRA_EXPANDED));
            setListening(savedInstanceState.getBoolean(EXTRA_LISTENING));
            setEditLocation(view);
            this.mQSCustomizer.restoreInstanceState(savedInstanceState);
            if (this.mQsExpanded) {
                this.mQSPanel.getTileLayout().restoreInstanceState(savedInstanceState);
            }
        }
        setHost(this.mHost);
        this.mStatusBarStateController.addCallback(this);
        onStateChanged(this.mStatusBarStateController.getState());
    }

    @Override // com.android.systemui.util.LifecycleFragment, android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mStatusBarStateController.removeCallback(this);
        if (this.mListening) {
            setListening(false);
        }
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_EXPANDED, this.mQsExpanded);
        outState.putBoolean(EXTRA_LISTENING, this.mListening);
        this.mQSCustomizer.saveInstanceState(outState);
        if (this.mQsExpanded) {
            this.mQSPanel.getTileLayout().saveInstanceState(outState);
        }
    }

    @VisibleForTesting
    boolean isListening() {
        return this.mListening;
    }

    @VisibleForTesting
    boolean isExpanded() {
        return this.mQsExpanded;
    }

    @Override // com.android.systemui.plugins.qs.QS
    public View getHeader() {
        return this.mHeader;
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setHasNotifications(boolean hasNotifications) {
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setPanelView(QS.HeightListener panelView) {
        this.mPanelView = panelView;
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setEditLocation(getView());
        if (newConfig.getLayoutDirection() != this.mLayoutDirection) {
            this.mLayoutDirection = newConfig.getLayoutDirection();
            QSAnimator qSAnimator = this.mQSAnimator;
            if (qSAnimator != null) {
                qSAnimator.onRtlChanged();
            }
        }
    }

    private void setEditLocation(View view) {
        View edit = view.findViewById(16908291);
        int[] loc = edit.getLocationOnScreen();
        int x = loc[0] + (edit.getWidth() / 2);
        int y = loc[1] + (edit.getHeight() / 2);
        this.mQSCustomizer.setEditLocation(x, y);
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setContainer(ViewGroup container) {
        if (container instanceof NotificationsQuickSettingsContainer) {
            this.mQSCustomizer.setContainer((NotificationsQuickSettingsContainer) container);
        }
    }

    @Override // com.android.systemui.plugins.qs.QS
    public boolean isCustomizing() {
        return this.mQSCustomizer.isCustomizing();
    }

    public void setHost(QSTileHost qsh) {
        this.mQSPanel.setHost(qsh, this.mQSCustomizer);
        this.mHeader.setQSPanel(this.mQSPanel);
        this.mFooter.setQSPanel(this.mQSPanel);
        this.mQSDetail.setHost(qsh);
        QSAnimator qSAnimator = this.mQSAnimator;
        if (qSAnimator != null) {
            qSAnimator.setHost(qsh);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        if (displayId != getContext().getDisplayId()) {
            return;
        }
        int state22 = this.mRemoteInputQuickSettingsDisabler.adjustDisableFlags(state2);
        boolean disabled = (state22 & 1) != 0;
        if (disabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = disabled;
        this.mContainer.disable(state1, state22, animate);
        this.mHeader.disable(state1, state22, animate);
        this.mFooter.disable(state1, state22, animate);
        updateQsState();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateQsState() {
        int i;
        int i2;
        boolean z = true;
        int i3 = 0;
        boolean expandVisually = this.mQsExpanded || this.mStackScrollerOverscrolling || this.mHeaderAnimating;
        this.mQSPanel.setExpanded(this.mQsExpanded);
        this.mQSDetail.setExpanded(this.mQsExpanded);
        boolean keyguardShowing = isKeyguardShowing();
        QuickStatusBarHeader quickStatusBarHeader = this.mHeader;
        if (this.mQsExpanded || !keyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard) {
            i = 0;
        } else {
            i = 4;
        }
        quickStatusBarHeader.setVisibility(i);
        this.mHeader.setExpanded(!(!keyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard) || (this.mQsExpanded && !this.mStackScrollerOverscrolling));
        QSFooter qSFooter = this.mFooter;
        if (!this.mQsDisabled && (this.mQsExpanded || !keyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard)) {
            i2 = 0;
        } else {
            i2 = 4;
        }
        qSFooter.setVisibility(i2);
        QSFooter qSFooter2 = this.mFooter;
        if ((!keyguardShowing || this.mHeaderAnimating || this.mShowCollapsedOnKeyguard) && (!this.mQsExpanded || this.mStackScrollerOverscrolling)) {
            z = false;
        }
        qSFooter2.setExpanded(z);
        QSPanel qSPanel = this.mQSPanel;
        if (this.mQsDisabled || !expandVisually) {
            i3 = 4;
        }
        qSPanel.setVisibility(i3);
    }

    private boolean isKeyguardShowing() {
        return this.mStatusBarStateController.getState() == 1;
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setShowCollapsedOnKeyguard(boolean showCollapsedOnKeyguard) {
        if (showCollapsedOnKeyguard != this.mShowCollapsedOnKeyguard) {
            this.mShowCollapsedOnKeyguard = showCollapsedOnKeyguard;
            updateQsState();
            QSAnimator qSAnimator = this.mQSAnimator;
            if (qSAnimator != null) {
                qSAnimator.setShowCollapsedOnKeyguard(showCollapsedOnKeyguard);
            }
            if (!showCollapsedOnKeyguard && isKeyguardShowing()) {
                setQsExpansion(this.mLastQSExpansion, 0.0f);
            }
        }
    }

    public QSPanel getQsPanel() {
        return this.mQSPanel;
    }

    public QSCustomizer getCustomizer() {
        return this.mQSCustomizer;
    }

    @Override // com.android.systemui.plugins.qs.QS
    public boolean isShowingDetail() {
        return this.mQSPanel.isShowingCustomize() || this.mQSDetail.isShowingDetail();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isCustomizing();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setHeaderClickable(boolean clickable) {
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setExpanded(boolean expanded) {
        this.mQsExpanded = expanded;
        this.mQSPanel.setListening(this.mListening, this.mQsExpanded);
        updateQsState();
    }

    private void setKeyguardShowing(boolean keyguardShowing) {
        this.mLastQSExpansion = -1.0f;
        QSAnimator qSAnimator = this.mQSAnimator;
        if (qSAnimator != null) {
            qSAnimator.setOnKeyguard(keyguardShowing);
        }
        this.mFooter.setKeyguardShowing(keyguardShowing);
        updateQsState();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setOverscrolling(boolean stackScrollerOverscrolling) {
        this.mStackScrollerOverscrolling = stackScrollerOverscrolling;
        updateQsState();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setListening(boolean listening) {
        this.mListening = listening;
        this.mHeader.setListening(listening);
        this.mFooter.setListening(listening);
        this.mQSPanel.setListening(this.mListening, this.mQsExpanded);
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setHeaderListening(boolean listening) {
        this.mHeader.setListening(listening);
        this.mFooter.setListening(listening);
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setQsExpansion(float expansion, float headerTranslation) {
        float f;
        this.mContainer.setExpansion(expansion);
        float translationScaleY = expansion - 1.0f;
        boolean onKeyguardAndExpanded = isKeyguardShowing() && !this.mShowCollapsedOnKeyguard;
        if (!this.mHeaderAnimating && !headerWillBeAnimating()) {
            View view = getView();
            if (onKeyguardAndExpanded) {
                f = this.mHeader.getHeight() * translationScaleY;
            } else {
                f = headerTranslation;
            }
            view.setTranslationY(f);
        }
        if (expansion == this.mLastQSExpansion && this.mLastKeyguardAndExpanded == onKeyguardAndExpanded) {
            return;
        }
        this.mLastQSExpansion = expansion;
        this.mLastKeyguardAndExpanded = onKeyguardAndExpanded;
        boolean fullyExpanded = expansion == 1.0f;
        int heightDiff = (this.mQSPanel.getBottom() - this.mHeader.getBottom()) + this.mHeader.getPaddingBottom() + this.mFooter.getHeight();
        float panelTranslationY = heightDiff * translationScaleY;
        this.mHeader.setExpansion(onKeyguardAndExpanded, expansion, panelTranslationY);
        this.mFooter.setExpansion(onKeyguardAndExpanded ? 1.0f : expansion);
        this.mQSPanel.getQsTileRevealController().setExpansion(expansion);
        this.mQSPanel.getTileLayout().setExpansion(expansion);
        this.mQSPanel.setTranslationY(heightDiff * translationScaleY);
        this.mQSDetail.setFullyExpanded(fullyExpanded);
        if (fullyExpanded) {
            this.mQSPanel.setClipBounds(null);
        } else {
            this.mQsBounds.top = (int) (-this.mQSPanel.getTranslationY());
            this.mQsBounds.right = this.mQSPanel.getWidth();
            this.mQsBounds.bottom = this.mQSPanel.getHeight();
            this.mQSPanel.setClipBounds(this.mQsBounds);
        }
        QSAnimator qSAnimator = this.mQSAnimator;
        if (qSAnimator != null) {
            qSAnimator.setPosition(expansion);
        }
    }

    private boolean headerWillBeAnimating() {
        return this.mState == 1 && this.mShowCollapsedOnKeyguard && !isKeyguardShowing();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void animateHeaderSlidingIn(long delay) {
        if (!this.mQsExpanded && getView().getTranslationY() != 0.0f) {
            this.mHeaderAnimating = true;
            this.mDelay = delay;
            getView().getViewTreeObserver().addOnPreDrawListener(this.mStartHeaderSlidingIn);
        }
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void animateHeaderSlidingOut() {
        if (getView().getY() == (-this.mHeader.getHeight())) {
            return;
        }
        this.mHeaderAnimating = true;
        getView().animate().y(-this.mHeader.getHeight()).setStartDelay(0L).setDuration(360L).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSFragment.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (QSFragment.this.getView() != null) {
                    QSFragment.this.getView().animate().setListener(null);
                }
                QSFragment.this.mHeaderAnimating = false;
                QSFragment.this.updateQsState();
            }
        }).start();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setExpandClickListener(View.OnClickListener onClickListener) {
        this.mFooter.setExpandClickListener(onClickListener);
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void closeDetail() {
        this.mQSPanel.closeDetail();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void notifyCustomizeChanged() {
        this.mContainer.updateExpansion();
        this.mQSPanel.setVisibility(!this.mQSCustomizer.isCustomizing() ? 0 : 4);
        this.mFooter.setVisibility(this.mQSCustomizer.isCustomizing() ? 4 : 0);
        this.mPanelView.onQsHeightChanged();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public int getDesiredHeight() {
        if (this.mQSCustomizer.isCustomizing()) {
            return getView().getHeight();
        }
        if (this.mQSDetail.isClosingDetail()) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mQSPanel.getLayoutParams();
            int panelHeight = layoutParams.topMargin + layoutParams.bottomMargin + this.mQSPanel.getMeasuredHeight();
            return getView().getPaddingBottom() + panelHeight;
        }
        return getView().getMeasuredHeight();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void setHeightOverride(int desiredHeight) {
        this.mContainer.setHeightOverride(desiredHeight);
    }

    @Override // com.android.systemui.plugins.qs.QS
    public int getQsMinExpansionHeight() {
        return this.mHeader.getHeight();
    }

    @Override // com.android.systemui.plugins.qs.QS
    public void hideImmediately() {
        getView().animate().cancel();
        getView().setY(-this.mHeader.getHeight());
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        this.mState = newState;
        setKeyguardShowing(newState == 1);
    }
}
