package com.android.systemui.qs;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.metrics.LogMaker;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.DumpController;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.qs.QSTileView;
import com.android.systemui.qs.PagedTileLayout;
import com.android.systemui.qs.QSDetail;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.qs.external.CustomTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.settings.BrightnessController;
import com.android.systemui.settings.ToggleSlider;
import com.android.systemui.settings.ToggleSliderView;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import com.android.systemui.tuner.TunerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class QSPanel extends LinearLayout implements TunerService.Tunable, QSHost.Callback, BrightnessMirrorController.BrightnessMirrorListener, Dumpable {
    public static final String QS_SHOW_BRIGHTNESS = "qs_show_brightness";
    public static final String QS_SHOW_HEADER = "qs_show_header";
    private static final String TAG = "QSPanel";
    private BrightnessController mBrightnessController;
    private BrightnessMirrorController mBrightnessMirrorController;
    protected final View mBrightnessView;
    private QSDetail.Callback mCallback;
    protected final Context mContext;
    private QSCustomizer mCustomizePanel;
    private Record mDetailRecord;
    private View mDivider;
    private DumpController mDumpController;
    protected boolean mExpanded;
    protected QSSecurityFooter mFooter;
    private PageIndicator mFooterPageIndicator;
    private boolean mGridContentVisible;
    private final H mHandler;
    protected QSTileHost mHost;
    protected boolean mListening;
    private final MetricsLogger mMetricsLogger;
    private final QSTileRevealController mQsTileRevealController;
    protected final ArrayList<TileRecord> mRecords;
    protected QSTileLayout mTileLayout;

    /* loaded from: classes21.dex */
    public static final class TileRecord extends Record {
        public QSTile.Callback callback;
        public boolean scanState;
        public QSTile tile;
        public QSTileView tileView;
    }

    public QSPanel(Context context) {
        this(context, null);
    }

    public QSPanel(Context context, AttributeSet attrs) {
        this(context, attrs, null);
    }

    @Inject
    public QSPanel(@Named("view_context") Context context, AttributeSet attrs, DumpController dumpController) {
        super(context, attrs);
        this.mRecords = new ArrayList<>();
        this.mHandler = new H();
        this.mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
        this.mGridContentVisible = true;
        this.mContext = context;
        setOrientation(1);
        this.mBrightnessView = LayoutInflater.from(this.mContext).inflate(R.layout.quick_settings_brightness_dialog, (ViewGroup) this, false);
        addView(this.mBrightnessView);
        this.mTileLayout = (QSTileLayout) LayoutInflater.from(this.mContext).inflate(R.layout.qs_paged_tile_layout, (ViewGroup) this, false);
        this.mTileLayout.setListening(this.mListening);
        addView((View) this.mTileLayout);
        this.mQsTileRevealController = new QSTileRevealController(this.mContext, this, (PagedTileLayout) this.mTileLayout);
        addDivider();
        this.mFooter = new QSSecurityFooter(this, context);
        addView(this.mFooter.getView());
        updateResources();
        this.mBrightnessController = new BrightnessController(getContext(), (ToggleSlider) findViewById(R.id.brightness_slider));
        this.mDumpController = dumpController;
    }

    protected void addDivider() {
        this.mDivider = LayoutInflater.from(this.mContext).inflate(R.layout.qs_divider, (ViewGroup) this, false);
        View view = this.mDivider;
        view.setBackgroundColor(Utils.applyAlpha(view.getAlpha(), QSTileImpl.getColorForState(this.mContext, 2)));
        addView(this.mDivider);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getPaddingBottom() + getPaddingTop();
        int numChildren = getChildCount();
        for (int i = 0; i < numChildren; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                height += child.getMeasuredHeight();
            }
        }
        int i2 = getMeasuredWidth();
        setMeasuredDimension(i2, height);
    }

    public View getDivider() {
        return this.mDivider;
    }

    public QSTileRevealController getQsTileRevealController() {
        return this.mQsTileRevealController;
    }

    public boolean isShowingCustomize() {
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        return qSCustomizer != null && qSCustomizer.isCustomizing();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        TunerService tunerService = (TunerService) Dependency.get(TunerService.class);
        tunerService.addTunable(this, QS_SHOW_BRIGHTNESS);
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            setTiles(qSTileHost.getTiles());
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.addCallback((BrightnessMirrorController.BrightnessMirrorListener) this);
        }
        DumpController dumpController = this.mDumpController;
        if (dumpController != null) {
            dumpController.addListener(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
        QSTileHost qSTileHost = this.mHost;
        if (qSTileHost != null) {
            qSTileHost.removeCallback(this);
        }
        Iterator<TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord record = it.next();
            record.tile.removeCallbacks();
        }
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback((BrightnessMirrorController.BrightnessMirrorListener) this);
        }
        DumpController dumpController = this.mDumpController;
        if (dumpController != null) {
            dumpController.removeListener(this);
        }
        super.onDetachedFromWindow();
    }

    @Override // com.android.systemui.qs.QSHost.Callback
    public void onTilesChanged() {
        setTiles(this.mHost.getTiles());
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        if (QS_SHOW_BRIGHTNESS.equals(key)) {
            updateViewVisibilityForTuningValue(this.mBrightnessView, newValue);
        }
    }

    private void updateViewVisibilityForTuningValue(View view, String newValue) {
        view.setVisibility(TunerService.parseIntegerSwitch(newValue, true) ? 0 : 8);
    }

    public void openDetails(String subPanel) {
        QSTile tile = getTile(subPanel);
        if (tile != null) {
            showDetailAdapter(true, tile.getDetailAdapter(), new int[]{getWidth() / 2, 0});
        }
    }

    private QSTile getTile(String subPanel) {
        for (int i = 0; i < this.mRecords.size(); i++) {
            if (subPanel.equals(this.mRecords.get(i).tile.getTileSpec())) {
                return this.mRecords.get(i).tile;
            }
        }
        return this.mHost.createTile(subPanel);
    }

    public void setBrightnessMirror(BrightnessMirrorController c) {
        BrightnessMirrorController brightnessMirrorController = this.mBrightnessMirrorController;
        if (brightnessMirrorController != null) {
            brightnessMirrorController.removeCallback((BrightnessMirrorController.BrightnessMirrorListener) this);
        }
        this.mBrightnessMirrorController = c;
        BrightnessMirrorController brightnessMirrorController2 = this.mBrightnessMirrorController;
        if (brightnessMirrorController2 != null) {
            brightnessMirrorController2.addCallback((BrightnessMirrorController.BrightnessMirrorListener) this);
        }
        updateBrightnessMirror();
    }

    @Override // com.android.systemui.statusbar.policy.BrightnessMirrorController.BrightnessMirrorListener
    public void onBrightnessMirrorReinflated(View brightnessMirror) {
        updateBrightnessMirror();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View getBrightnessView() {
        return this.mBrightnessView;
    }

    public void setCallback(QSDetail.Callback callback) {
        this.mCallback = callback;
    }

    public void setHost(QSTileHost host, QSCustomizer customizer) {
        this.mHost = host;
        this.mHost.addCallback(this);
        setTiles(this.mHost.getTiles());
        this.mFooter.setHostEnvironment(host);
        this.mCustomizePanel = customizer;
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        if (qSCustomizer != null) {
            qSCustomizer.setHost(this.mHost);
        }
    }

    public void setFooterPageIndicator(PageIndicator pageIndicator) {
        if (this.mTileLayout instanceof PagedTileLayout) {
            this.mFooterPageIndicator = pageIndicator;
            updatePageIndicator();
        }
    }

    private void updatePageIndicator() {
        PageIndicator pageIndicator;
        if ((this.mTileLayout instanceof PagedTileLayout) && (pageIndicator = this.mFooterPageIndicator) != null) {
            pageIndicator.setVisibility(8);
            ((PagedTileLayout) this.mTileLayout).setPageIndicator(this.mFooterPageIndicator);
        }
    }

    public QSTileHost getHost() {
        return this.mHost;
    }

    public void updateResources() {
        Resources res = this.mContext.getResources();
        setPadding(0, res.getDimensionPixelSize(R.dimen.qs_panel_padding_top), 0, res.getDimensionPixelSize(R.dimen.qs_panel_padding_bottom));
        updatePageIndicator();
        if (this.mListening) {
            refreshAllTiles();
        }
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.updateResources();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mFooter.onConfigurationChanged();
        updateResources();
        updateBrightnessMirror();
    }

    public void updateBrightnessMirror() {
        if (this.mBrightnessMirrorController != null) {
            ToggleSliderView brightnessSlider = (ToggleSliderView) findViewById(R.id.brightness_slider);
            ToggleSliderView mirrorSlider = (ToggleSliderView) this.mBrightnessMirrorController.getMirror().findViewById(R.id.brightness_slider);
            brightnessSlider.setMirror(mirrorSlider);
            brightnessSlider.setMirrorController(this.mBrightnessMirrorController);
        }
    }

    public void onCollapse() {
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        if (qSCustomizer != null && qSCustomizer.isShown()) {
            this.mCustomizePanel.hide();
        }
    }

    public void setExpanded(boolean expanded) {
        if (this.mExpanded == expanded) {
            return;
        }
        this.mExpanded = expanded;
        if (!this.mExpanded) {
            QSTileLayout qSTileLayout = this.mTileLayout;
            if (qSTileLayout instanceof PagedTileLayout) {
                ((PagedTileLayout) qSTileLayout).setCurrentItem(0, false);
            }
        }
        this.mMetricsLogger.visibility(111, this.mExpanded);
        if (!this.mExpanded) {
            closeDetail();
        } else {
            logTiles();
        }
    }

    public void setPageListener(PagedTileLayout.PageListener pageListener) {
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout instanceof PagedTileLayout) {
            ((PagedTileLayout) qSTileLayout).setPageListener(pageListener);
        }
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.setListening(listening);
        }
        if (this.mListening) {
            refreshAllTiles();
        }
    }

    public void setListening(boolean listening, boolean expanded) {
        setListening(listening && expanded);
        getFooter().setListening(listening);
        setBrightnessListening(listening);
    }

    public void setBrightnessListening(boolean listening) {
        if (listening) {
            this.mBrightnessController.registerCallbacks();
        } else {
            this.mBrightnessController.unregisterCallbacks();
        }
    }

    public void refreshAllTiles() {
        this.mBrightnessController.checkRestrictionAndSetEnabled();
        Iterator<TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord r = it.next();
            r.tile.refreshState();
        }
        this.mFooter.refreshState();
    }

    public void showDetailAdapter(boolean show, DetailAdapter adapter, int[] locationInWindow) {
        int xInWindow = locationInWindow[0];
        int yInWindow = locationInWindow[1];
        ((View) getParent()).getLocationInWindow(locationInWindow);
        Record r = new Record();
        r.detailAdapter = adapter;
        r.x = xInWindow - locationInWindow[0];
        r.y = yInWindow - locationInWindow[1];
        locationInWindow[0] = xInWindow;
        locationInWindow[1] = yInWindow;
        showDetail(show, r);
    }

    protected void showDetail(boolean show, Record r) {
        this.mHandler.obtainMessage(1, show ? 1 : 0, 0, r).sendToTarget();
    }

    public void setTiles(Collection<QSTile> tiles) {
        setTiles(tiles, false);
    }

    public void setTiles(Collection<QSTile> tiles, boolean collapsedView) {
        if (!collapsedView) {
            this.mQsTileRevealController.updateRevealedTiles(tiles);
        }
        Iterator<TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord record = it.next();
            this.mTileLayout.removeTile(record);
            record.tile.removeCallback(record.callback);
        }
        this.mRecords.clear();
        for (QSTile tile : tiles) {
            addTile(tile, collapsedView);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawTile(TileRecord r, QSTile.State state) {
        r.tileView.onStateChanged(state);
    }

    protected QSTileView createTileView(QSTile tile, boolean collapsedView) {
        return this.mHost.createTileView(tile, collapsedView);
    }

    protected boolean shouldShowDetail() {
        return this.mExpanded;
    }

    protected TileRecord addTile(QSTile tile, boolean collapsedView) {
        final TileRecord r = new TileRecord();
        r.tile = tile;
        r.tileView = createTileView(tile, collapsedView);
        QSTile.Callback callback = new QSTile.Callback() { // from class: com.android.systemui.qs.QSPanel.1
            @Override // com.android.systemui.plugins.qs.QSTile.Callback
            public void onStateChanged(QSTile.State state) {
                QSPanel.this.drawTile(r, state);
            }

            @Override // com.android.systemui.plugins.qs.QSTile.Callback
            public void onShowDetail(boolean show) {
                if (QSPanel.this.shouldShowDetail()) {
                    QSPanel.this.showDetail(show, r);
                }
            }

            @Override // com.android.systemui.plugins.qs.QSTile.Callback
            public void onToggleStateChanged(boolean state) {
                if (QSPanel.this.mDetailRecord == r) {
                    QSPanel.this.fireToggleStateChanged(state);
                }
            }

            @Override // com.android.systemui.plugins.qs.QSTile.Callback
            public void onScanStateChanged(boolean state) {
                r.scanState = state;
                Record record = QSPanel.this.mDetailRecord;
                TileRecord tileRecord = r;
                if (record == tileRecord) {
                    QSPanel.this.fireScanStateChanged(tileRecord.scanState);
                }
            }

            @Override // com.android.systemui.plugins.qs.QSTile.Callback
            public void onAnnouncementRequested(CharSequence announcement) {
                if (announcement != null) {
                    QSPanel.this.mHandler.obtainMessage(3, announcement).sendToTarget();
                }
            }
        };
        r.tile.addCallback(callback);
        r.callback = callback;
        r.tileView.init(r.tile);
        r.tile.refreshState();
        this.mRecords.add(r);
        QSTileLayout qSTileLayout = this.mTileLayout;
        if (qSTileLayout != null) {
            qSTileLayout.addTile(r);
        }
        return r;
    }

    public void showEdit(final View v) {
        v.post(new Runnable() { // from class: com.android.systemui.qs.QSPanel.2
            @Override // java.lang.Runnable
            public void run() {
                if (QSPanel.this.mCustomizePanel != null && !QSPanel.this.mCustomizePanel.isCustomizing()) {
                    int[] loc = v.getLocationOnScreen();
                    int x = loc[0] + (v.getWidth() / 2);
                    int y = loc[1] + (v.getHeight() / 2);
                    QSPanel.this.mCustomizePanel.show(x, y);
                }
            }
        });
    }

    public void closeDetail() {
        QSCustomizer qSCustomizer = this.mCustomizePanel;
        if (qSCustomizer != null && qSCustomizer.isShown()) {
            this.mCustomizePanel.hide();
        } else {
            showDetail(false, this.mDetailRecord);
        }
    }

    public int getGridHeight() {
        return getMeasuredHeight();
    }

    protected void handleShowDetail(Record r, boolean show) {
        if (r instanceof TileRecord) {
            handleShowDetailTile((TileRecord) r, show);
            return;
        }
        int x = 0;
        int y = 0;
        if (r != null) {
            x = r.x;
            y = r.y;
        }
        handleShowDetailImpl(r, show, x, y);
    }

    private void handleShowDetailTile(TileRecord r, boolean show) {
        if ((this.mDetailRecord != null) == show && this.mDetailRecord == r) {
            return;
        }
        if (show) {
            r.detailAdapter = r.tile.getDetailAdapter();
            if (r.detailAdapter == null) {
                return;
            }
        }
        r.tile.setDetailListening(show);
        int x = r.tileView.getLeft() + (r.tileView.getWidth() / 2);
        int y = r.tileView.getDetailY() + this.mTileLayout.getOffsetTop(r) + getTop();
        handleShowDetailImpl(r, show, x, y);
    }

    private void handleShowDetailImpl(Record r, boolean show, int x, int y) {
        setDetailRecord(show ? r : null);
        fireShowingDetail(show ? r.detailAdapter : null, x, y);
    }

    protected void setDetailRecord(Record r) {
        if (r == this.mDetailRecord) {
            return;
        }
        this.mDetailRecord = r;
        Record record = this.mDetailRecord;
        boolean scanState = (record instanceof TileRecord) && ((TileRecord) record).scanState;
        fireScanStateChanged(scanState);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setGridContentVisibility(boolean visible) {
        int newVis = visible ? 0 : 4;
        setVisibility(newVis);
        if (this.mGridContentVisible != visible) {
            this.mMetricsLogger.visibility(111, newVis);
        }
        this.mGridContentVisible = visible;
    }

    private void logTiles() {
        for (int i = 0; i < this.mRecords.size(); i++) {
            QSTile tile = this.mRecords.get(i).tile;
            this.mMetricsLogger.write(tile.populate(new LogMaker(tile.getMetricsCategory()).setType(1)));
        }
    }

    private void fireShowingDetail(DetailAdapter detail, int x, int y) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onShowingDetail(detail, x, y);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireToggleStateChanged(boolean state) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onToggleStateChanged(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireScanStateChanged(boolean state) {
        QSDetail.Callback callback = this.mCallback;
        if (callback != null) {
            callback.onScanStateChanged(state);
        }
    }

    public void clickTile(ComponentName tile) {
        String spec = CustomTile.toSpec(tile);
        int N = this.mRecords.size();
        for (int i = 0; i < N; i++) {
            if (this.mRecords.get(i).tile.getTileSpec().equals(spec)) {
                this.mRecords.get(i).tile.click();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QSTileLayout getTileLayout() {
        return this.mTileLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QSTileView getTileView(QSTile tile) {
        Iterator<TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord r = it.next();
            if (r.tile == tile) {
                return r.tileView;
            }
        }
        return null;
    }

    public QSSecurityFooter getFooter() {
        return this.mFooter;
    }

    public void showDeviceMonitoringDialog() {
        this.mFooter.showDeviceMonitoringDialog();
    }

    public void setMargins(int sideMargins) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view != this.mTileLayout) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                lp.leftMargin = sideMargins;
                lp.rightMargin = sideMargins;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class H extends Handler {
        private static final int ANNOUNCE_FOR_ACCESSIBILITY = 3;
        private static final int SET_TILE_VISIBILITY = 2;
        private static final int SHOW_DETAIL = 1;

        private H() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                QSPanel.this.handleShowDetail((Record) msg.obj, msg.arg1 != 0);
            } else if (msg.what == 3) {
                QSPanel.this.announceForAccessibility((CharSequence) msg.obj);
            }
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(getClass().getSimpleName() + NavigationBarInflaterView.KEY_IMAGE_DELIM);
        pw.println("  Tile records:");
        Iterator<TileRecord> it = this.mRecords.iterator();
        while (it.hasNext()) {
            TileRecord record = it.next();
            if (record.tile instanceof Dumpable) {
                pw.print("    ");
                ((Dumpable) record.tile).dump(fd, pw, args);
                pw.print("    ");
                pw.println(record.tileView.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public static class Record {
        DetailAdapter detailAdapter;
        int x;
        int y;

        protected Record() {
        }
    }

    /* loaded from: classes21.dex */
    public interface QSTileLayout {
        void addTile(TileRecord tileRecord);

        int getNumVisibleTiles();

        int getOffsetTop(TileRecord tileRecord);

        void removeTile(TileRecord tileRecord);

        void setListening(boolean z);

        boolean updateResources();

        default void saveInstanceState(Bundle outState) {
        }

        default void restoreInstanceState(Bundle savedInstanceState) {
        }

        default void setExpansion(float expansion) {
        }
    }
}
