package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.systemui.Dependency;
import com.android.systemui.DumpController;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.tuner.TunerService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class QuickQSPanel extends QSPanel {
    public static final String NUM_QUICK_TILES = "sysui_qqs_count";
    private static final String TAG = "QuickQSPanel";
    private static int mDefaultMaxTiles;
    private boolean mDisabledByPolicy;
    protected QSPanel mFullPanel;
    private int mMaxTiles;
    private final TunerService.Tunable mNumTiles;

    @Inject
    public QuickQSPanel(@Named("view_context") Context context, AttributeSet attrs, DumpController dumpController) {
        super(context, attrs, dumpController);
        this.mNumTiles = new TunerService.Tunable() { // from class: com.android.systemui.qs.QuickQSPanel.1
            @Override // com.android.systemui.tuner.TunerService.Tunable
            public void onTuningChanged(String key, String newValue) {
                QuickQSPanel quickQSPanel = QuickQSPanel.this;
                quickQSPanel.setMaxTiles(QuickQSPanel.getNumQuickTiles(quickQSPanel.mContext));
            }
        };
        if (this.mFooter != null) {
            removeView(this.mFooter.getView());
        }
        if (this.mTileLayout != null) {
            for (int i = 0; i < this.mRecords.size(); i++) {
                this.mTileLayout.removeTile(this.mRecords.get(i));
            }
            removeView((View) this.mTileLayout);
        }
        mDefaultMaxTiles = getResources().getInteger(R.integer.quick_qs_panel_max_columns);
        this.mTileLayout = new HeaderTileLayout(context);
        this.mTileLayout.setListening(this.mListening);
        addView((View) this.mTileLayout, 0);
        super.setPadding(0, 0, 0, 0);
    }

    @Override // android.view.View
    public void setPadding(int left, int top, int right, int bottom) {
    }

    @Override // com.android.systemui.qs.QSPanel
    protected void addDivider() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.QSPanel, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this.mNumTiles, NUM_QUICK_TILES);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.QSPanel, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this.mNumTiles);
    }

    public void setQSPanelAndHeader(QSPanel fullPanel, View header) {
        this.mFullPanel = fullPanel;
    }

    @Override // com.android.systemui.qs.QSPanel
    protected boolean shouldShowDetail() {
        return !this.mExpanded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.QSPanel
    public void drawTile(QSPanel.TileRecord r, QSTile.State state) {
        if (state instanceof QSTile.SignalState) {
            QSTile.SignalState copy = new QSTile.SignalState();
            state.copyTo(copy);
            copy.activityIn = false;
            copy.activityOut = false;
            state = copy;
        }
        super.drawTile(r, state);
    }

    @Override // com.android.systemui.qs.QSPanel
    public void setHost(QSTileHost host, QSCustomizer customizer) {
        super.setHost(host, customizer);
        setTiles(this.mHost.getTiles());
    }

    public void setMaxTiles(int maxTiles) {
        this.mMaxTiles = maxTiles;
        if (this.mHost != null) {
            setTiles(this.mHost.getTiles());
        }
    }

    @Override // com.android.systemui.qs.QSPanel, com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (QSPanel.QS_SHOW_BRIGHTNESS.equals(key)) {
            super.onTuningChanged(key, "0");
        }
    }

    @Override // com.android.systemui.qs.QSPanel
    public void setTiles(Collection<QSTile> tiles) {
        ArrayList<QSTile> quickTiles = new ArrayList<>();
        for (QSTile tile : tiles) {
            quickTiles.add(tile);
            if (quickTiles.size() == this.mMaxTiles) {
                break;
            }
        }
        super.setTiles(quickTiles, true);
    }

    public static int getNumQuickTiles(Context context) {
        return ((TunerService) Dependency.get(TunerService.class)).getValue(NUM_QUICK_TILES, mDefaultMaxTiles);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDisabledByPolicy(boolean disabled) {
        if (disabled != this.mDisabledByPolicy) {
            this.mDisabledByPolicy = disabled;
            setVisibility(disabled ? 8 : 0);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (this.mDisabledByPolicy) {
            if (getVisibility() == 8) {
                return;
            }
            visibility = 8;
        }
        super.setVisibility(visibility);
    }

    /* loaded from: classes21.dex */
    private static class HeaderTileLayout extends TileLayout {
        private Rect mClippingBounds;
        private boolean mListening;

        public HeaderTileLayout(Context context) {
            super(context);
            this.mClippingBounds = new Rect();
            setClipChildren(false);
            setClipToPadding(false);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
            lp.gravity = 1;
            setLayoutParams(lp);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateResources();
        }

        @Override // android.view.View
        public void onFinishInflate() {
            updateResources();
        }

        private ViewGroup.LayoutParams generateTileLayoutParams() {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(this.mCellWidth, this.mCellHeight);
            return lp;
        }

        @Override // com.android.systemui.qs.TileLayout
        protected void addTileView(QSPanel.TileRecord tile) {
            addView(tile.tileView, getChildCount(), generateTileLayoutParams());
        }

        @Override // com.android.systemui.qs.TileLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            this.mClippingBounds.set(0, 0, r - l, 10000);
            setClipBounds(this.mClippingBounds);
            calculateColumns();
            int i = 0;
            while (i < this.mRecords.size()) {
                this.mRecords.get(i).tileView.setVisibility(i < this.mColumns ? 0 : 8);
                i++;
            }
            setAccessibilityOrder();
            layoutTileRecords(this.mColumns);
        }

        @Override // com.android.systemui.qs.TileLayout, com.android.systemui.qs.QSPanel.QSTileLayout
        public boolean updateResources() {
            this.mCellWidth = this.mContext.getResources().getDimensionPixelSize(R.dimen.qs_quick_tile_size);
            this.mCellHeight = this.mCellWidth;
            return false;
        }

        private boolean calculateColumns() {
            int prevNumColumns = this.mColumns;
            int maxTiles = this.mRecords.size();
            if (maxTiles == 0) {
                this.mColumns = 0;
                return true;
            }
            int availableWidth = (getMeasuredWidth() - getPaddingStart()) - getPaddingEnd();
            int leftoverWhitespace = availableWidth - (this.mCellWidth * maxTiles);
            int smallestHorizontalMarginNeeded = leftoverWhitespace / Math.max(1, maxTiles - 1);
            if (smallestHorizontalMarginNeeded > 0) {
                this.mCellMarginHorizontal = smallestHorizontalMarginNeeded;
                this.mColumns = maxTiles;
            } else {
                this.mColumns = this.mCellWidth == 0 ? 1 : Math.min(maxTiles, availableWidth / this.mCellWidth);
                this.mCellMarginHorizontal = (availableWidth - (this.mColumns * this.mCellWidth)) / (this.mColumns - 1);
            }
            return this.mColumns != prevNumColumns;
        }

        private void setAccessibilityOrder() {
            if (this.mRecords != null && this.mRecords.size() > 0) {
                View previousView = this;
                Iterator<QSPanel.TileRecord> it = this.mRecords.iterator();
                while (it.hasNext()) {
                    QSPanel.TileRecord record = it.next();
                    if (record.tileView.getVisibility() != 8) {
                        previousView = record.tileView.updateAccessibilityOrder(previousView);
                    }
                }
                this.mRecords.get(this.mRecords.size() - 1).tileView.setAccessibilityTraversalBefore(R.id.expand_indicator);
            }
        }

        @Override // com.android.systemui.qs.TileLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Iterator<QSPanel.TileRecord> it = this.mRecords.iterator();
            while (it.hasNext()) {
                QSPanel.TileRecord record = it.next();
                if (record.tileView.getVisibility() != 8) {
                    record.tileView.measure(exactly(this.mCellWidth), exactly(this.mCellHeight));
                }
            }
            int height = this.mCellHeight;
            if (height < 0) {
                height = 0;
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height);
        }

        @Override // com.android.systemui.qs.TileLayout, com.android.systemui.qs.QSPanel.QSTileLayout
        public int getNumVisibleTiles() {
            return this.mColumns;
        }

        @Override // com.android.systemui.qs.TileLayout
        protected int getColumnStart(int column) {
            return getPaddingStart() + ((this.mCellWidth + this.mCellMarginHorizontal) * column);
        }
    }
}
