package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.android.systemui.qs.QSPanel;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class TileLayout extends ViewGroup implements QSPanel.QSTileLayout {
    private static final String TAG = "TileLayout";
    private static final float TILE_ASPECT = 1.2f;
    protected int mCellHeight;
    protected int mCellMarginHorizontal;
    private int mCellMarginTop;
    protected int mCellMarginVertical;
    protected int mCellWidth;
    protected int mColumns;
    private boolean mListening;
    protected int mMaxAllowedRows;
    protected final ArrayList<QSPanel.TileRecord> mRecords;
    protected int mRows;
    protected int mSidePadding;

    public TileLayout(Context context) {
        this(context, null);
    }

    public TileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRows = 1;
        this.mRecords = new ArrayList<>();
        this.mMaxAllowedRows = 3;
        setFocusableInTouchMode(true);
        updateResources();
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public int getOffsetTop(QSPanel.TileRecord tile) {
        return getTop();
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void setListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        Iterator<QSPanel.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanel.TileRecord record = it.next();
            record.tile.setListening(this, this.mListening);
        }
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void addTile(QSPanel.TileRecord tile) {
        this.mRecords.add(tile);
        tile.tile.setListening(this, this.mListening);
        addTileView(tile);
    }

    protected void addTileView(QSPanel.TileRecord tile) {
        addView(tile.tileView);
    }

    @Override // com.android.systemui.qs.QSPanel.QSTileLayout
    public void removeTile(QSPanel.TileRecord tile) {
        this.mRecords.remove(tile);
        tile.tile.setListening(this, false);
        removeView(tile.tileView);
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        Iterator<QSPanel.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanel.TileRecord record = it.next();
            record.tile.setListening(this, false);
        }
        this.mRecords.clear();
        super.removeAllViews();
    }

    public boolean updateResources() {
        Resources res = this.mContext.getResources();
        int columns = Math.max(1, res.getInteger(R.integer.quick_settings_num_columns));
        this.mCellHeight = this.mContext.getResources().getDimensionPixelSize(R.dimen.qs_tile_height);
        this.mCellMarginHorizontal = res.getDimensionPixelSize(R.dimen.qs_tile_margin_horizontal);
        this.mCellMarginVertical = res.getDimensionPixelSize(R.dimen.qs_tile_margin_vertical);
        this.mCellMarginTop = res.getDimensionPixelSize(R.dimen.qs_tile_margin_top);
        this.mSidePadding = res.getDimensionPixelOffset(R.dimen.qs_tile_layout_margin_side);
        this.mMaxAllowedRows = Math.max(1, getResources().getInteger(R.integer.quick_settings_max_rows));
        if (this.mColumns != columns) {
            this.mColumns = columns;
            requestLayout();
            return true;
        }
        return false;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int numTiles = this.mRecords.size();
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = (width - getPaddingStart()) - getPaddingEnd();
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == 0) {
            int i = this.mColumns;
            this.mRows = ((numTiles + i) - 1) / i;
        }
        int i2 = this.mCellMarginHorizontal;
        int i3 = this.mColumns;
        this.mCellWidth = ((availableWidth - (this.mSidePadding * 2)) - (i2 * i3)) / i3;
        View previousView = this;
        Iterator<QSPanel.TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            QSPanel.TileRecord record = it.next();
            if (record.tileView.getVisibility() != 8) {
                record.tileView.measure(exactly(this.mCellWidth), exactly(this.mCellHeight));
                previousView = record.tileView.updateAccessibilityOrder(previousView);
            }
        }
        int i4 = this.mCellHeight;
        int i5 = this.mCellMarginVertical;
        int i6 = this.mRows;
        int height = ((i4 + i5) * i6) + (i6 != 0 ? this.mCellMarginTop - i5 : 0);
        if (height < 0) {
            height = 0;
        }
        setMeasuredDimension(width, height);
    }

    public boolean updateMaxRows(int heightMeasureSpec, int tilesCount) {
        int size = View.MeasureSpec.getSize(heightMeasureSpec) - this.mCellMarginTop;
        int i = this.mCellMarginVertical;
        int availableHeight = size + i;
        int previousRows = this.mRows;
        this.mRows = availableHeight / (this.mCellHeight + i);
        int i2 = this.mRows;
        int i3 = this.mMaxAllowedRows;
        if (i2 >= i3) {
            this.mRows = i3;
        } else if (i2 <= 1) {
            this.mRows = 1;
        }
        int i4 = this.mRows;
        int i5 = this.mColumns;
        if (i4 > ((tilesCount + i5) - 1) / i5) {
            this.mRows = ((tilesCount + i5) - 1) / i5;
        }
        return previousRows != this.mRows;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int exactly(int size) {
        return View.MeasureSpec.makeMeasureSpec(size, 1073741824);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void layoutTileRecords(int numRecords) {
        boolean isRtl = getLayoutDirection() == 1;
        int row = 0;
        int column = 0;
        int tilesToLayout = Math.min(numRecords, this.mRows * this.mColumns);
        int i = 0;
        while (i < tilesToLayout) {
            if (column == this.mColumns) {
                column = 0;
                row++;
            }
            QSPanel.TileRecord record = this.mRecords.get(i);
            int top = getRowTop(row);
            int left = getColumnStart(isRtl ? (this.mColumns - column) - 1 : column);
            int right = this.mCellWidth + left;
            record.tileView.layout(left, top, right, record.tileView.getMeasuredHeight() + top);
            i++;
            column++;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutTileRecords(this.mRecords.size());
    }

    private int getRowTop(int row) {
        return ((this.mCellHeight + this.mCellMarginVertical) * row) + this.mCellMarginTop;
    }

    protected int getColumnStart(int column) {
        int paddingStart = getPaddingStart() + this.mSidePadding;
        int i = this.mCellMarginHorizontal;
        return paddingStart + (i / 2) + ((this.mCellWidth + i) * column);
    }

    public int getNumVisibleTiles() {
        return this.mRecords.size();
    }
}
