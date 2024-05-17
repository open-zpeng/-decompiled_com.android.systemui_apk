package com.android.systemui.qs;

import android.util.Log;
import android.view.View;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.qs.QSTileView;
import com.android.systemui.qs.PagedTileLayout;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.TouchAnimator;
import com.android.systemui.tuner.TunerService;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class QSAnimator implements QSHost.Callback, PagedTileLayout.PageListener, TouchAnimator.Listener, View.OnLayoutChangeListener, View.OnAttachStateChangeListener, TunerService.Tunable {
    private static final String ALLOW_FANCY_ANIMATION = "sysui_qs_fancy_anim";
    public static final float EXPANDED_TILE_DELAY = 0.86f;
    private static final String MOVE_FULL_ROWS = "sysui_qs_move_whole_rows";
    private static final String TAG = "QSAnimator";
    private boolean mAllowFancy;
    private TouchAnimator mBrightnessAnimator;
    private TouchAnimator mFirstPageAnimator;
    private TouchAnimator mFirstPageDelayedAnimator;
    private boolean mFullRows;
    private QSTileHost mHost;
    private float mLastPosition;
    private TouchAnimator mNonfirstPageAnimator;
    private TouchAnimator mNonfirstPageDelayedAnimator;
    private int mNumQuickTiles;
    private boolean mOnKeyguard;
    private PagedTileLayout mPagedLayout;
    private final QS mQs;
    private final QSPanel mQsPanel;
    private final QuickQSPanel mQuickQsPanel;
    private boolean mShowCollapsedOnKeyguard;
    private TouchAnimator mTranslationXAnimator;
    private TouchAnimator mTranslationYAnimator;
    private final ArrayList<View> mAllViews = new ArrayList<>();
    private final ArrayList<View> mQuickQsViews = new ArrayList<>();
    private boolean mOnFirstPage = true;
    private final TouchAnimator.Listener mNonFirstPageListener = new TouchAnimator.ListenerAdapter() { // from class: com.android.systemui.qs.QSAnimator.1
        @Override // com.android.systemui.qs.TouchAnimator.ListenerAdapter, com.android.systemui.qs.TouchAnimator.Listener
        public void onAnimationAtEnd() {
            QSAnimator.this.mQuickQsPanel.setVisibility(4);
        }

        @Override // com.android.systemui.qs.TouchAnimator.ListenerAdapter, com.android.systemui.qs.TouchAnimator.Listener
        public void onAnimationStarted() {
            QSAnimator.this.mQuickQsPanel.setVisibility(0);
        }
    };
    private Runnable mUpdateAnimators = new Runnable() { // from class: com.android.systemui.qs.QSAnimator.2
        @Override // java.lang.Runnable
        public void run() {
            QSAnimator.this.updateAnimators();
            QSAnimator.this.setCurrentPosition();
        }
    };

    public QSAnimator(QS qs, QuickQSPanel quickPanel, QSPanel panel) {
        this.mQs = qs;
        this.mQuickQsPanel = quickPanel;
        this.mQsPanel = panel;
        this.mQsPanel.addOnAttachStateChangeListener(this);
        qs.getView().addOnLayoutChangeListener(this);
        if (this.mQsPanel.isAttachedToWindow()) {
            onViewAttachedToWindow(null);
        }
        QSPanel.QSTileLayout tileLayout = this.mQsPanel.getTileLayout();
        if (tileLayout instanceof PagedTileLayout) {
            this.mPagedLayout = (PagedTileLayout) tileLayout;
        } else {
            Log.w(TAG, "QS Not using page layout");
        }
        panel.setPageListener(this);
    }

    public void onRtlChanged() {
        updateAnimators();
    }

    public void setOnKeyguard(boolean onKeyguard) {
        this.mOnKeyguard = onKeyguard;
        updateQQSVisibility();
        if (this.mOnKeyguard) {
            clearAnimationState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowCollapsedOnKeyguard(boolean showCollapsedOnKeyguard) {
        this.mShowCollapsedOnKeyguard = showCollapsedOnKeyguard;
        updateQQSVisibility();
        setCurrentPosition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentPosition() {
        setPosition(this.mLastPosition);
    }

    private void updateQQSVisibility() {
        this.mQuickQsPanel.setVisibility((!this.mOnKeyguard || this.mShowCollapsedOnKeyguard) ? 0 : 4);
    }

    public void setHost(QSTileHost qsh) {
        this.mHost = qsh;
        qsh.addCallback(this);
        updateAnimators();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View v) {
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, ALLOW_FANCY_ANIMATION, MOVE_FULL_ROWS, QuickQSPanel.NUM_QUICK_TILES);
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View v) {
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            qSTileHost.removeCallback(this);
        }
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (ALLOW_FANCY_ANIMATION.equals(key)) {
            this.mAllowFancy = TunerService.parseIntegerSwitch(newValue, true);
            if (!this.mAllowFancy) {
                clearAnimationState();
            }
        } else if (MOVE_FULL_ROWS.equals(key)) {
            this.mFullRows = TunerService.parseIntegerSwitch(newValue, true);
        } else if (QuickQSPanel.NUM_QUICK_TILES.equals(key)) {
            QuickQSPanel quickQSPanel = this.mQuickQsPanel;
            this.mNumQuickTiles = QuickQSPanel.getNumQuickTiles(this.mQs.getContext());
            clearAnimationState();
        }
        updateAnimators();
    }

    @Override // com.android.systemui.qs.PagedTileLayout.PageListener
    public void onPageChanged(boolean isFirst) {
        if (this.mOnFirstPage == isFirst) {
            return;
        }
        if (!isFirst) {
            clearAnimationState();
        }
        this.mOnFirstPage = isFirst;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAnimators() {
        QSPanel.QSTileLayout tileLayout;
        Collection<QSTile> tiles;
        int lastXDiff;
        int width;
        int heightDiff;
        int[] loc2;
        QSPanel.QSTileLayout tileLayout2;
        int[] loc1;
        int heightDiff2;
        TouchAnimator.Builder firstPageBuilder = new TouchAnimator.Builder();
        TouchAnimator.Builder translationXBuilder = new TouchAnimator.Builder();
        TouchAnimator.Builder translationYBuilder = new TouchAnimator.Builder();
        if (this.mQsPanel.getHost() == null) {
            return;
        }
        Collection<QSTile> tiles2 = this.mQsPanel.getHost().getTiles();
        int count = 0;
        int[] loc12 = new int[2];
        int[] loc22 = new int[2];
        int lastXDiff2 = 0;
        int lastX = 0;
        clearAnimationState();
        this.mAllViews.clear();
        this.mQuickQsViews.clear();
        QSPanel.QSTileLayout tileLayout3 = this.mQsPanel.getTileLayout();
        this.mAllViews.add((View) tileLayout3);
        int height = this.mQs.getView() != null ? this.mQs.getView().getMeasuredHeight() : 0;
        int width2 = this.mQs.getView() != null ? this.mQs.getView().getMeasuredWidth() : 0;
        int heightDiff3 = (height - this.mQs.getHeader().getBottom()) + this.mQs.getHeader().getPaddingBottom();
        firstPageBuilder.addFloat(tileLayout3, "translationY", heightDiff3, 0.0f);
        Iterator<QSTile> it = tiles2.iterator();
        while (true) {
            int lastX2 = lastX;
            if (!it.hasNext()) {
                break;
            }
            int height2 = height;
            QSTile tile = it.next();
            Iterator<QSTile> it2 = it;
            QSTileView tileView = this.mQsPanel.getTileView(tile);
            if (tileView == null) {
                StringBuilder sb = new StringBuilder();
                tiles = tiles2;
                sb.append("tileView is null ");
                sb.append(tile.getTileSpec());
                Log.e(TAG, sb.toString());
                lastXDiff = lastXDiff2;
                width = width2;
                heightDiff = heightDiff3;
            } else {
                tiles = tiles2;
                View tileIcon = tileView.getIcon().getIconView();
                heightDiff = heightDiff3;
                View view = this.mQs.getView();
                lastXDiff = lastXDiff2;
                width = width2;
                if (count < this.mQuickQsPanel.getTileLayout().getNumVisibleTiles() && this.mAllowFancy) {
                    QSTileView quickTileView = this.mQuickQsPanel.getTileView(tile);
                    if (quickTileView != null) {
                        int lastX3 = loc12[0];
                        getRelativePosition(loc12, quickTileView.getIcon().getIconView(), view);
                        getRelativePosition(loc22, tileIcon, view);
                        int xDiff = loc22[0] - loc12[0];
                        int yDiff = loc22[1] - loc12[1];
                        int lastXDiff3 = loc12[0] - lastX3;
                        loc2 = loc22;
                        if (count < tileLayout3.getNumVisibleTiles()) {
                            translationXBuilder.addFloat(quickTileView, "translationX", 0.0f, xDiff);
                            translationYBuilder.addFloat(quickTileView, "translationY", 0.0f, yDiff);
                            translationXBuilder.addFloat(tileView, "translationX", -xDiff, 0.0f);
                            translationYBuilder.addFloat(tileView, "translationY", -yDiff, 0.0f);
                            tileLayout2 = tileLayout3;
                        } else {
                            tileLayout2 = tileLayout3;
                            firstPageBuilder.addFloat(quickTileView, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f);
                            translationYBuilder.addFloat(quickTileView, "translationY", 0.0f, yDiff);
                            int translationX = this.mQsPanel.isLayoutRtl() ? xDiff - width : xDiff + width;
                            translationXBuilder.addFloat(quickTileView, "translationX", 0.0f, translationX);
                        }
                        this.mQuickQsViews.add(tileView.getIconWithBackground());
                        this.mAllViews.add(tileView.getIcon());
                        this.mAllViews.add(quickTileView);
                        lastX = lastX3;
                        heightDiff2 = heightDiff;
                        lastXDiff = lastXDiff3;
                        loc1 = loc12;
                    }
                } else {
                    loc2 = loc22;
                    tileLayout2 = tileLayout3;
                    if (!this.mFullRows || !isIconInAnimatedRow(count)) {
                        loc1 = loc12;
                        heightDiff2 = heightDiff;
                        firstPageBuilder.addFloat(tileView, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f);
                        firstPageBuilder.addFloat(tileView, "translationY", -heightDiff2, 0.0f);
                    } else {
                        loc12[0] = loc12[0] + lastXDiff;
                        getRelativePosition(loc2, tileIcon, view);
                        int xDiff2 = loc2[0] - loc12[0];
                        int yDiff2 = loc2[1] - loc12[1];
                        loc1 = loc12;
                        heightDiff2 = heightDiff;
                        firstPageBuilder.addFloat(tileView, "translationY", heightDiff2, 0.0f);
                        translationXBuilder.addFloat(tileView, "translationX", -xDiff2, 0.0f);
                        translationYBuilder.addFloat(tileView, "translationY", -yDiff2, 0.0f);
                        translationYBuilder.addFloat(tileIcon, "translationY", -yDiff2, 0.0f);
                        this.mAllViews.add(tileIcon);
                    }
                    lastX = lastX2;
                }
                this.mAllViews.add(tileView);
                count++;
                heightDiff3 = heightDiff2;
                it = it2;
                height = height2;
                tiles2 = tiles;
                lastXDiff2 = lastXDiff;
                width2 = width;
                loc12 = loc1;
                loc22 = loc2;
                tileLayout3 = tileLayout2;
            }
            it = it2;
            lastX = lastX2;
            height = height2;
            tiles2 = tiles;
            heightDiff3 = heightDiff;
            lastXDiff2 = lastXDiff;
            width2 = width;
        }
        Collection<QSTile> tiles3 = tiles2;
        QSPanel.QSTileLayout tileLayout4 = tileLayout3;
        int height3 = heightDiff3;
        if (!this.mAllowFancy) {
            tileLayout = tileLayout4;
        } else {
            View brightness = this.mQsPanel.getBrightnessView();
            if (brightness != null) {
                firstPageBuilder.addFloat(brightness, "translationY", height3, 0.0f);
                this.mBrightnessAnimator = new TouchAnimator.Builder().addFloat(brightness, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).setStartDelay(0.5f).build();
                this.mAllViews.add(brightness);
            } else {
                this.mBrightnessAnimator = null;
            }
            this.mFirstPageAnimator = firstPageBuilder.setListener(this).build();
            tileLayout = tileLayout4;
            this.mFirstPageDelayedAnimator = new TouchAnimator.Builder().setStartDelay(0.86f).addFloat(tileLayout, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).addFloat(this.mQsPanel.getDivider(), ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).addFloat(this.mQsPanel.getFooter().getView(), ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).build();
            this.mAllViews.add(this.mQsPanel.getDivider());
            this.mAllViews.add(this.mQsPanel.getFooter().getView());
            float px = 0.0f;
            if (tiles3.size() <= 3) {
                px = 1.0f;
            } else if (tiles3.size() <= 6) {
                px = 0.4f;
            }
            PathInterpolatorBuilder interpolatorBuilder = new PathInterpolatorBuilder(0.0f, 0.0f, px, 1.0f);
            translationXBuilder.setInterpolator(interpolatorBuilder.getXInterpolator());
            translationYBuilder.setInterpolator(interpolatorBuilder.getYInterpolator());
            this.mTranslationXAnimator = translationXBuilder.build();
            this.mTranslationYAnimator = translationYBuilder.build();
        }
        this.mNonfirstPageAnimator = new TouchAnimator.Builder().addFloat(this.mQuickQsPanel, ThemeManager.AttributeSet.ALPHA, 1.0f, 0.0f).addFloat(this.mQsPanel.getDivider(), ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).setListener(this.mNonFirstPageListener).setEndDelay(0.5f).build();
        this.mNonfirstPageDelayedAnimator = new TouchAnimator.Builder().setStartDelay(0.14f).addFloat(tileLayout, ThemeManager.AttributeSet.ALPHA, 0.0f, 1.0f).build();
    }

    private boolean isIconInAnimatedRow(int count) {
        PagedTileLayout pagedTileLayout = this.mPagedLayout;
        if (pagedTileLayout == null) {
            return false;
        }
        int columnCount = pagedTileLayout.getColumnCount();
        return count < (((this.mNumQuickTiles + columnCount) - 1) / columnCount) * columnCount;
    }

    private void getRelativePosition(int[] loc1, View view, View parent) {
        loc1[0] = (view.getWidth() / 2) + 0;
        loc1[1] = 0;
        getRelativePositionInt(loc1, view, parent);
    }

    private void getRelativePositionInt(int[] loc1, View view, View parent) {
        if (view == parent || view == null) {
            return;
        }
        if (!(view instanceof PagedTileLayout.TilePage)) {
            loc1[0] = loc1[0] + view.getLeft();
            loc1[1] = loc1[1] + view.getTop();
        }
        getRelativePositionInt(loc1, (View) view.getParent(), parent);
    }

    public void setPosition(float position) {
        if (this.mFirstPageAnimator == null) {
            return;
        }
        if (this.mOnKeyguard) {
            if (this.mShowCollapsedOnKeyguard) {
                position = 0.0f;
            } else {
                position = 1.0f;
            }
        }
        this.mLastPosition = position;
        if (this.mOnFirstPage && this.mAllowFancy) {
            this.mQuickQsPanel.setAlpha(1.0f);
            this.mFirstPageAnimator.setPosition(position);
            this.mFirstPageDelayedAnimator.setPosition(position);
            this.mTranslationXAnimator.setPosition(position);
            this.mTranslationYAnimator.setPosition(position);
            TouchAnimator touchAnimator = this.mBrightnessAnimator;
            if (touchAnimator != null) {
                touchAnimator.setPosition(position);
                return;
            }
            return;
        }
        this.mNonfirstPageAnimator.setPosition(position);
        this.mNonfirstPageDelayedAnimator.setPosition(position);
    }

    @Override // com.android.systemui.qs.TouchAnimator.Listener
    public void onAnimationAtStart() {
        this.mQuickQsPanel.setVisibility(0);
    }

    @Override // com.android.systemui.qs.TouchAnimator.Listener
    public void onAnimationAtEnd() {
        this.mQuickQsPanel.setVisibility(4);
        int N = this.mQuickQsViews.size();
        for (int i = 0; i < N; i++) {
            this.mQuickQsViews.get(i).setVisibility(0);
        }
    }

    @Override // com.android.systemui.qs.TouchAnimator.Listener
    public void onAnimationStarted() {
        updateQQSVisibility();
        if (this.mOnFirstPage) {
            int N = this.mQuickQsViews.size();
            for (int i = 0; i < N; i++) {
                this.mQuickQsViews.get(i).setVisibility(4);
            }
        }
    }

    private void clearAnimationState() {
        int N = this.mAllViews.size();
        this.mQuickQsPanel.setAlpha(0.0f);
        for (int i = 0; i < N; i++) {
            View v = this.mAllViews.get(i);
            v.setAlpha(1.0f);
            v.setTranslationX(0.0f);
            v.setTranslationY(0.0f);
        }
        int N2 = this.mQuickQsViews.size();
        for (int i2 = 0; i2 < N2; i2++) {
            this.mQuickQsViews.get(i2).setVisibility(0);
        }
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.mQsPanel.post(this.mUpdateAnimators);
    }

    @Override // com.android.systemui.qs.QSHost.Callback
    public void onTilesChanged() {
        this.mQsPanel.post(this.mUpdateAnimators);
    }
}
