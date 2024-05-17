package com.android.systemui.qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.PhoneStatusBarView;
import java.util.Objects;
/* loaded from: classes21.dex */
public class QSDetail extends LinearLayout {
    private static final long FADE_DURATION = 300;
    private static final String TAG = "QSDetail";
    private boolean mAnimatingOpen;
    private QSDetailClipper mClipper;
    private boolean mClosingDetail;
    private DetailAdapter mDetailAdapter;
    private ViewGroup mDetailContent;
    protected TextView mDetailDoneButton;
    protected TextView mDetailSettingsButton;
    private final SparseArray<View> mDetailViews;
    private View mFooter;
    private boolean mFullyExpanded;
    private QuickStatusBarHeader mHeader;
    private final AnimatorListenerAdapter mHideGridContentWhenDone;
    protected QSTileHost mHost;
    private int mOpenX;
    private int mOpenY;
    protected View mQsDetailHeader;
    protected ImageView mQsDetailHeaderProgress;
    protected Switch mQsDetailHeaderSwitch;
    protected TextView mQsDetailHeaderTitle;
    private QSPanel mQsPanel;
    protected Callback mQsPanelCallback;
    private boolean mScanState;
    private boolean mSwitchState;
    private final AnimatorListenerAdapter mTeardownDetailWhenDone;
    private boolean mTriggeredExpand;

    /* loaded from: classes21.dex */
    public interface Callback {
        void onScanStateChanged(boolean z);

        void onShowingDetail(DetailAdapter detailAdapter, int i, int i2);

        void onToggleStateChanged(boolean z);
    }

    public QSDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDetailViews = new SparseArray<>();
        this.mQsPanelCallback = new Callback() { // from class: com.android.systemui.qs.QSDetail.3
            @Override // com.android.systemui.qs.QSDetail.Callback
            public void onToggleStateChanged(final boolean state) {
                QSDetail.this.post(new Runnable() { // from class: com.android.systemui.qs.QSDetail.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        QSDetail.this.handleToggleStateChanged(state, QSDetail.this.mDetailAdapter != null && QSDetail.this.mDetailAdapter.getToggleEnabled());
                    }
                });
            }

            @Override // com.android.systemui.qs.QSDetail.Callback
            public void onShowingDetail(final DetailAdapter detail, final int x, final int y) {
                QSDetail.this.post(new Runnable() { // from class: com.android.systemui.qs.QSDetail.3.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (QSDetail.this.isAttachedToWindow()) {
                            QSDetail.this.handleShowingDetail(detail, x, y, false);
                        }
                    }
                });
            }

            @Override // com.android.systemui.qs.QSDetail.Callback
            public void onScanStateChanged(final boolean state) {
                QSDetail.this.post(new Runnable() { // from class: com.android.systemui.qs.QSDetail.3.3
                    @Override // java.lang.Runnable
                    public void run() {
                        QSDetail.this.handleScanStateChanged(state);
                    }
                });
            }
        };
        this.mHideGridContentWhenDone = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSDetail.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                animation.removeListener(this);
                QSDetail.this.mAnimatingOpen = false;
                QSDetail.this.checkPendingAnimations();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (QSDetail.this.mDetailAdapter != null) {
                    QSDetail.this.mQsPanel.setGridContentVisibility(false);
                    QSDetail.this.mHeader.setVisibility(4);
                    QSDetail.this.mFooter.setVisibility(4);
                }
                QSDetail.this.mAnimatingOpen = false;
                QSDetail.this.checkPendingAnimations();
            }
        };
        this.mTeardownDetailWhenDone = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.QSDetail.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                QSDetail.this.mDetailContent.removeAllViews();
                QSDetail.this.setVisibility(4);
                QSDetail.this.mClosingDetail = false;
            }
        };
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FontSizeUtils.updateFontSize(this.mDetailDoneButton, R.dimen.qs_detail_button_text_size);
        FontSizeUtils.updateFontSize(this.mDetailSettingsButton, R.dimen.qs_detail_button_text_size);
        for (int i = 0; i < this.mDetailViews.size(); i++) {
            this.mDetailViews.valueAt(i).dispatchConfigurationChanged(newConfig);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDetailContent = (ViewGroup) findViewById(16908290);
        this.mDetailSettingsButton = (TextView) findViewById(16908314);
        this.mDetailDoneButton = (TextView) findViewById(16908313);
        this.mQsDetailHeader = findViewById(R.id.qs_detail_header);
        this.mQsDetailHeaderTitle = (TextView) this.mQsDetailHeader.findViewById(16908310);
        this.mQsDetailHeaderSwitch = (Switch) this.mQsDetailHeader.findViewById(16908311);
        this.mQsDetailHeaderProgress = (ImageView) findViewById(R.id.qs_detail_header_progress);
        updateDetailText();
        this.mClipper = new QSDetailClipper(this);
        View.OnClickListener doneListener = new View.OnClickListener() { // from class: com.android.systemui.qs.QSDetail.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                QSDetail qSDetail = QSDetail.this;
                qSDetail.announceForAccessibility(qSDetail.mContext.getString(R.string.accessibility_desc_quick_settings));
                QSDetail.this.mQsPanel.closeDetail();
            }
        };
        this.mDetailDoneButton.setOnClickListener(doneListener);
    }

    public void setQsPanel(QSPanel panel, QuickStatusBarHeader header, View footer) {
        this.mQsPanel = panel;
        this.mHeader = header;
        this.mFooter = footer;
        this.mHeader.setCallback(this.mQsPanelCallback);
        this.mQsPanel.setCallback(this.mQsPanelCallback);
    }

    public void setHost(QSTileHost host) {
        this.mHost = host;
    }

    public boolean isShowingDetail() {
        return this.mDetailAdapter != null;
    }

    public void setFullyExpanded(boolean fullyExpanded) {
        this.mFullyExpanded = fullyExpanded;
    }

    public void setExpanded(boolean qsExpanded) {
        if (!qsExpanded) {
            this.mTriggeredExpand = false;
        }
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        DisplayCutout cutout = insets.getDisplayCutout();
        Pair<Integer, Integer> padding = PhoneStatusBarView.cornerCutoutMargins(cutout, getDisplay());
        if (padding == null) {
            this.mQsDetailHeader.setPaddingRelative(getResources().getDimensionPixelSize(R.dimen.qs_detail_header_padding), getPaddingTop(), getResources().getDimensionPixelSize(R.dimen.qs_detail_header_padding), getPaddingBottom());
        } else {
            this.mQsDetailHeader.setPadding(((Integer) padding.first).intValue(), getPaddingTop(), ((Integer) padding.second).intValue(), getPaddingBottom());
        }
        return super.onApplyWindowInsets(insets);
    }

    private void updateDetailText() {
        this.mDetailDoneButton.setText(R.string.quick_settings_done);
        this.mDetailSettingsButton.setText(R.string.quick_settings_more_settings);
    }

    public void updateResources() {
        updateDetailText();
    }

    public boolean isClosingDetail() {
        return this.mClosingDetail;
    }

    public void handleShowingDetail(DetailAdapter adapter, int x, int y, boolean toggleQs) {
        Animator.AnimatorListener listener;
        boolean showingDetail = adapter != null;
        setClickable(showingDetail);
        if (showingDetail) {
            setupDetailHeader(adapter);
            if (toggleQs && !this.mFullyExpanded) {
                this.mTriggeredExpand = true;
                ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).animateExpandSettingsPanel(null);
            } else {
                this.mTriggeredExpand = false;
            }
            this.mOpenX = x;
            this.mOpenY = y;
        } else {
            x = this.mOpenX;
            y = this.mOpenY;
            if (toggleQs && this.mTriggeredExpand) {
                ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).animateCollapsePanels();
                this.mTriggeredExpand = false;
            }
        }
        boolean visibleDiff = (this.mDetailAdapter != null) != (adapter != null);
        if (visibleDiff || this.mDetailAdapter != adapter) {
            if (adapter != null) {
                int viewCacheIndex = adapter.getMetricsCategory();
                View detailView = adapter.createDetailView(this.mContext, this.mDetailViews.get(viewCacheIndex), this.mDetailContent);
                if (detailView == null) {
                    throw new IllegalStateException("Must return detail view");
                }
                setupDetailFooter(adapter);
                this.mDetailContent.removeAllViews();
                this.mDetailContent.addView(detailView);
                this.mDetailViews.put(viewCacheIndex, detailView);
                ((MetricsLogger) Dependency.get(MetricsLogger.class)).visible(adapter.getMetricsCategory());
                announceForAccessibility(this.mContext.getString(R.string.accessibility_quick_settings_detail, adapter.getTitle()));
                this.mDetailAdapter = adapter;
                listener = this.mHideGridContentWhenDone;
                setVisibility(0);
            } else {
                if (this.mDetailAdapter != null) {
                    ((MetricsLogger) Dependency.get(MetricsLogger.class)).hidden(this.mDetailAdapter.getMetricsCategory());
                }
                this.mClosingDetail = true;
                this.mDetailAdapter = null;
                Animator.AnimatorListener listener2 = this.mTeardownDetailWhenDone;
                this.mHeader.setVisibility(0);
                this.mFooter.setVisibility(0);
                this.mQsPanel.setGridContentVisibility(true);
                this.mQsPanelCallback.onScanStateChanged(false);
                listener = listener2;
            }
            sendAccessibilityEvent(32);
            animateDetailVisibleDiff(x, y, visibleDiff, listener);
        }
    }

    protected void animateDetailVisibleDiff(int x, int y, boolean visibleDiff, Animator.AnimatorListener listener) {
        if (visibleDiff) {
            this.mAnimatingOpen = this.mDetailAdapter != null;
            if (this.mFullyExpanded || this.mDetailAdapter != null) {
                setAlpha(1.0f);
                this.mClipper.animateCircularClip(x, y, this.mDetailAdapter != null, listener);
                return;
            }
            animate().alpha(0.0f).setDuration(FADE_DURATION).setListener(listener).start();
        }
    }

    protected void setupDetailFooter(final DetailAdapter adapter) {
        final Intent settingsIntent = adapter.getSettingsIntent();
        this.mDetailSettingsButton.setVisibility(settingsIntent != null ? 0 : 8);
        this.mDetailSettingsButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.-$$Lambda$QSDetail$NHQwfesA2Z6J0e0FBlLg3IIEATQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                QSDetail.lambda$setupDetailFooter$0(DetailAdapter.this, settingsIntent, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setupDetailFooter$0(DetailAdapter adapter, Intent settingsIntent, View v) {
        ((MetricsLogger) Dependency.get(MetricsLogger.class)).action(929, adapter.getMetricsCategory());
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(settingsIntent, 0);
    }

    protected void setupDetailHeader(final DetailAdapter adapter) {
        this.mQsDetailHeaderTitle.setText(adapter.getTitle());
        Boolean toggleState = adapter.getToggleState();
        if (toggleState != null) {
            this.mQsDetailHeaderSwitch.setVisibility(0);
            handleToggleStateChanged(toggleState.booleanValue(), adapter.getToggleEnabled());
            this.mQsDetailHeader.setClickable(true);
            this.mQsDetailHeader.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.QSDetail.2
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    boolean checked = !QSDetail.this.mQsDetailHeaderSwitch.isChecked();
                    QSDetail.this.mQsDetailHeaderSwitch.setChecked(checked);
                    adapter.setToggleState(checked);
                }
            });
            return;
        }
        this.mQsDetailHeaderSwitch.setVisibility(4);
        this.mQsDetailHeader.setClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleToggleStateChanged(boolean state, boolean toggleEnabled) {
        this.mSwitchState = state;
        if (this.mAnimatingOpen) {
            return;
        }
        this.mQsDetailHeaderSwitch.setChecked(state);
        this.mQsDetailHeader.setEnabled(toggleEnabled);
        this.mQsDetailHeaderSwitch.setEnabled(toggleEnabled);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScanStateChanged(boolean state) {
        if (this.mScanState == state) {
            return;
        }
        this.mScanState = state;
        final Animatable anim = (Animatable) this.mQsDetailHeaderProgress.getDrawable();
        if (state) {
            this.mQsDetailHeaderProgress.animate().cancel();
            ViewPropertyAnimator alpha = this.mQsDetailHeaderProgress.animate().alpha(1.0f);
            Objects.requireNonNull(anim);
            alpha.withEndAction(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$dWuG3P2xqsast1TFpf_9V5OJbdM
                @Override // java.lang.Runnable
                public final void run() {
                    anim.start();
                }
            }).start();
            return;
        }
        this.mQsDetailHeaderProgress.animate().cancel();
        ViewPropertyAnimator alpha2 = this.mQsDetailHeaderProgress.animate().alpha(0.0f);
        Objects.requireNonNull(anim);
        alpha2.withEndAction(new Runnable() { // from class: com.android.systemui.qs.-$$Lambda$uWzoJtW0gRQtylxIzOBLYDei0eA
            @Override // java.lang.Runnable
            public final void run() {
                anim.stop();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPendingAnimations() {
        boolean z = this.mSwitchState;
        DetailAdapter detailAdapter = this.mDetailAdapter;
        handleToggleStateChanged(z, detailAdapter != null && detailAdapter.getToggleEnabled());
    }
}
