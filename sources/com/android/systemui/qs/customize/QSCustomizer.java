package com.android.systemui.qs.customize;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSDetailClipper;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.NotificationsQuickSettingsContainer;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class QSCustomizer extends LinearLayout implements Toolbar.OnMenuItemClickListener {
    private static final String EXTRA_QS_CUSTOMIZING = "qs_customizing";
    private static final int MENU_RESET = 1;
    private static final String TAG = "QSCustomizer";
    private boolean isShown;
    private final QSDetailClipper mClipper;
    private final Animator.AnimatorListener mCollapseAnimationListener;
    private boolean mCustomizing;
    private final Animator.AnimatorListener mExpandAnimationListener;
    private QSTileHost mHost;
    private boolean mIsShowingNavBackdrop;
    private final KeyguardMonitor.Callback mKeyguardCallback;
    private KeyguardMonitor mKeyguardMonitor;
    private final LightBarController mLightBarController;
    private NotificationsQuickSettingsContainer mNotifQsContainer;
    private boolean mOpening;
    private QS mQs;
    private RecyclerView mRecyclerView;
    private final ScreenLifecycle mScreenLifecycle;
    private TileAdapter mTileAdapter;
    private final TileQueryHelper mTileQueryHelper;
    private Toolbar mToolbar;
    private final View mTransparentView;
    private int mX;
    private int mY;

    @Inject
    public QSCustomizer(Context context, AttributeSet attrs, LightBarController lightBarController, KeyguardMonitor keyguardMonitor, ScreenLifecycle screenLifecycle) {
        super(new ContextThemeWrapper(context, R.style.edit_theme), attrs);
        this.mKeyguardCallback = new KeyguardMonitor.Callback() { // from class: com.android.systemui.qs.customize.QSCustomizer.3
            @Override // com.android.systemui.statusbar.policy.KeyguardMonitor.Callback
            public void onKeyguardShowingChanged() {
                if (QSCustomizer.this.isAttachedToWindow() && QSCustomizer.this.mKeyguardMonitor.isShowing() && !QSCustomizer.this.mOpening) {
                    QSCustomizer.this.hide();
                }
            }
        };
        this.mExpandAnimationListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.customize.QSCustomizer.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (QSCustomizer.this.isShown) {
                    QSCustomizer.this.setCustomizing(true);
                }
                QSCustomizer.this.mOpening = false;
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                QSCustomizer.this.mOpening = false;
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }
        };
        this.mCollapseAnimationListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.customize.QSCustomizer.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!QSCustomizer.this.isShown) {
                    QSCustomizer.this.setVisibility(8);
                }
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
                QSCustomizer.this.mRecyclerView.setAdapter(QSCustomizer.this.mTileAdapter);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (!QSCustomizer.this.isShown) {
                    QSCustomizer.this.setVisibility(8);
                }
                QSCustomizer.this.mNotifQsContainer.setCustomizerAnimating(false);
            }
        };
        LayoutInflater.from(getContext()).inflate(R.layout.qs_customize_panel_content, this);
        this.mClipper = new QSDetailClipper(findViewById(R.id.customize_container));
        this.mToolbar = (Toolbar) findViewById(16908781);
        TypedValue value = new TypedValue();
        this.mContext.getTheme().resolveAttribute(16843531, value, true);
        this.mToolbar.setNavigationIcon(getResources().getDrawable(value.resourceId, this.mContext.getTheme()));
        this.mToolbar.setNavigationOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.qs.customize.QSCustomizer.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                QSCustomizer.this.hide();
            }
        });
        this.mToolbar.setOnMenuItemClickListener(this);
        this.mToolbar.getMenu().add(0, 1, 0, this.mContext.getString(17040939));
        this.mToolbar.setTitle(R.string.qs_edit);
        this.mRecyclerView = (RecyclerView) findViewById(16908298);
        this.mTransparentView = findViewById(R.id.customizer_transparent_view);
        this.mTileAdapter = new TileAdapter(getContext());
        this.mTileQueryHelper = new TileQueryHelper(context, this.mTileAdapter);
        this.mRecyclerView.setAdapter(this.mTileAdapter);
        this.mTileAdapter.getItemTouchHelper().attachToRecyclerView(this.mRecyclerView);
        GridLayoutManager layout = new GridLayoutManager(getContext(), 3);
        layout.setSpanSizeLookup(this.mTileAdapter.getSizeLookup());
        this.mRecyclerView.setLayoutManager(layout);
        this.mRecyclerView.addItemDecoration(this.mTileAdapter.getItemDecoration());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setMoveDuration(150L);
        this.mRecyclerView.setItemAnimator(animator);
        this.mLightBarController = lightBarController;
        this.mKeyguardMonitor = keyguardMonitor;
        this.mScreenLifecycle = screenLifecycle;
        updateNavBackDrop(getResources().getConfiguration());
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateNavBackDrop(newConfig);
        updateResources();
    }

    private void updateResources() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mTransparentView.getLayoutParams();
        lp.height = this.mContext.getResources().getDimensionPixelSize(17105399);
        this.mTransparentView.setLayoutParams(lp);
    }

    private void updateNavBackDrop(Configuration newConfig) {
        View navBackdrop = findViewById(R.id.nav_bar_background);
        this.mIsShowingNavBackdrop = newConfig.smallestScreenWidthDp >= 600 || newConfig.orientation != 2;
        if (navBackdrop != null) {
            navBackdrop.setVisibility(this.mIsShowingNavBackdrop ? 0 : 8);
        }
        updateNavColors();
    }

    private void updateNavColors() {
        this.mLightBarController.setQsCustomizing(this.mIsShowingNavBackdrop && this.isShown);
    }

    public void setHost(QSTileHost host) {
        this.mHost = host;
        this.mTileAdapter.setHost(host);
    }

    public void setContainer(NotificationsQuickSettingsContainer notificationsQsContainer) {
        this.mNotifQsContainer = notificationsQsContainer;
    }

    public void setQs(QS qs) {
        this.mQs = qs;
    }

    public void show(int x, int y) {
        if (!this.isShown) {
            int[] containerLocation = findViewById(R.id.customize_container).getLocationOnScreen();
            this.mX = x - containerLocation[0];
            this.mY = y - containerLocation[1];
            MetricsLogger.visible(getContext(), 358);
            this.isShown = true;
            this.mOpening = true;
            setTileSpecs();
            setVisibility(0);
            this.mClipper.animateCircularClip(this.mX, this.mY, true, this.mExpandAnimationListener);
            queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(true);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardMonitor.addCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    public void showImmediately() {
        if (!this.isShown) {
            setVisibility(0);
            this.mClipper.showBackground();
            this.isShown = true;
            setTileSpecs();
            setCustomizing(true);
            queryTiles();
            this.mNotifQsContainer.setCustomizerAnimating(false);
            this.mNotifQsContainer.setCustomizerShowing(true);
            this.mKeyguardMonitor.addCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    private void queryTiles() {
        this.mTileQueryHelper.queryTiles(this.mHost);
    }

    public void hide() {
        boolean animate = this.mScreenLifecycle.getScreenState() != 0;
        if (this.isShown) {
            MetricsLogger.hidden(getContext(), 358);
            this.isShown = false;
            this.mToolbar.dismissPopupMenus();
            setCustomizing(false);
            save();
            if (animate) {
                this.mClipper.animateCircularClip(this.mX, this.mY, false, this.mCollapseAnimationListener);
            } else {
                setVisibility(8);
            }
            this.mNotifQsContainer.setCustomizerAnimating(animate);
            this.mNotifQsContainer.setCustomizerShowing(false);
            this.mKeyguardMonitor.removeCallback(this.mKeyguardCallback);
            updateNavColors();
        }
    }

    @Override // android.view.View
    public boolean isShown() {
        return this.isShown;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCustomizing(boolean customizing) {
        this.mCustomizing = customizing;
        this.mQs.notifyCustomizeChanged();
    }

    public boolean isCustomizing() {
        return this.mCustomizing || this.mOpening;
    }

    @Override // android.widget.Toolbar.OnMenuItemClickListener
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == 1) {
            MetricsLogger.action(getContext(), 359);
            reset();
            return false;
        }
        return false;
    }

    private void reset() {
        String[] split;
        ArrayList<String> tiles = new ArrayList<>();
        String defTiles = this.mContext.getString(R.string.quick_settings_tiles_default);
        for (String tile : defTiles.split(",")) {
            tiles.add(tile);
        }
        this.mTileAdapter.resetTileSpecs(this.mHost, tiles);
    }

    private void setTileSpecs() {
        List<String> specs = new ArrayList<>();
        for (QSTile tile : this.mHost.getTiles()) {
            specs.add(tile.getTileSpec());
        }
        this.mTileAdapter.setTileSpecs(specs);
        this.mRecyclerView.setAdapter(this.mTileAdapter);
    }

    private void save() {
        if (this.mTileQueryHelper.isFinished()) {
            this.mTileAdapter.saveSpecs(this.mHost);
        }
    }

    public void saveInstanceState(Bundle outState) {
        if (this.isShown) {
            this.mKeyguardMonitor.removeCallback(this.mKeyguardCallback);
        }
        outState.putBoolean(EXTRA_QS_CUSTOMIZING, this.mCustomizing);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        boolean customizing = savedInstanceState.getBoolean(EXTRA_QS_CUSTOMIZING);
        if (customizing) {
            setVisibility(0);
            addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.qs.customize.QSCustomizer.2
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    QSCustomizer.this.removeOnLayoutChangeListener(this);
                    QSCustomizer.this.showImmediately();
                }
            });
        }
    }

    public void setEditLocation(int x, int y) {
        int[] containerLocation = findViewById(R.id.customize_container).getLocationOnScreen();
        this.mX = x - containerLocation[0];
        this.mY = y - containerLocation[1];
    }
}
