package com.android.systemui.qs.tileimpl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.Prefs;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.plugins.qs.QSTile.State;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.PagedTileLayout;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes21.dex */
public abstract class QSTileImpl<TState extends QSTile.State> implements QSTile, LifecycleOwner, Dumpable {
    private static final long DEFAULT_STALE_TIMEOUT = 600000;
    private boolean mAnnounceNextStateChange;
    protected final Context mContext;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    protected final QSHost mHost;
    private int mIsFullQs;
    private boolean mShowingDetail;
    private String mTileSpec;
    protected static final boolean DEBUG = Log.isLoggable("Tile", 3);
    protected static final Object ARG_SHOW_TRANSIENT_ENABLING = new Object();
    protected final String TAG = "Tile." + getClass().getSimpleName();
    protected QSTileImpl<TState>.H mHandler = new H((Looper) Dependency.get(Dependency.BG_LOOPER));
    protected final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final ArraySet<Object> mListeners = new ArraySet<>();
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    private final StatusBarStateController mStatusBarStateController = (StatusBarStateController) Dependency.get(StatusBarStateController.class);
    private final ArrayList<QSTile.Callback> mCallbacks = new ArrayList<>();
    private final Object mStaleListener = new Object();
    protected TState mState = newTileState();
    private TState mTmpState = newTileState();
    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    public abstract Intent getLongClickIntent();

    @Override // com.android.systemui.plugins.qs.QSTile
    public abstract int getMetricsCategory();

    @Override // com.android.systemui.plugins.qs.QSTile
    public abstract CharSequence getTileLabel();

    protected abstract void handleClick();

    protected abstract void handleSetListening(boolean z);

    protected abstract void handleUpdateState(TState tstate, Object obj);

    public abstract TState newTileState();

    /* JADX INFO: Access modifiers changed from: protected */
    public QSTileImpl(QSHost host) {
        this.mHost = host;
        this.mContext = host.getContext();
    }

    @Override // androidx.lifecycle.LifecycleOwner
    @NonNull
    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void setListening(Object listener, boolean listening) {
        this.mHandler.obtainMessage(13, listening ? 1 : 0, 0, listener).sendToTarget();
    }

    protected long getStaleTimeout() {
        return DEFAULT_STALE_TIMEOUT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    public void handleStale() {
        setListening(this.mStaleListener, true);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public String getTileSpec() {
        return this.mTileSpec;
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void setTileSpec(String tileSpec) {
        this.mTileSpec = tileSpec;
    }

    public QSHost getHost() {
        return this.mHost;
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public QSIconView createTileView(Context context) {
        return new QSIconViewImpl(context);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public DetailAdapter getDetailAdapter() {
        return null;
    }

    protected DetailAdapter createDetailAdapter() {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void addCallback(QSTile.Callback callback) {
        this.mHandler.obtainMessage(1, callback).sendToTarget();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void removeCallback(QSTile.Callback callback) {
        this.mHandler.obtainMessage(12, callback).sendToTarget();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void removeCallbacks() {
        this.mHandler.sendEmptyMessage(11);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void click() {
        this.mMetricsLogger.write(populate(new LogMaker(925).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mHandler.sendEmptyMessage(2);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void secondaryClick() {
        this.mMetricsLogger.write(populate(new LogMaker(926).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mHandler.sendEmptyMessage(3);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void longClick() {
        this.mMetricsLogger.write(populate(new LogMaker(366).setType(4).addTaggedData(1592, Integer.valueOf(this.mStatusBarStateController.getState()))));
        this.mHandler.sendEmptyMessage(4);
        Prefs.putInt(this.mContext, Prefs.Key.QS_LONG_PRESS_TOOLTIP_SHOWN_COUNT, 2);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public LogMaker populate(LogMaker logMaker) {
        TState tstate = this.mState;
        if (tstate instanceof QSTile.BooleanState) {
            logMaker.addTaggedData(928, Integer.valueOf(((QSTile.BooleanState) tstate).value ? 1 : 0));
        }
        return logMaker.setSubtype(getMetricsCategory()).addTaggedData(1593, Integer.valueOf(this.mIsFullQs)).addTaggedData(927, Integer.valueOf(this.mHost.indexOf(this.mTileSpec)));
    }

    public void showDetail(boolean show) {
        this.mHandler.obtainMessage(6, show ? 1 : 0, 0).sendToTarget();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void refreshState() {
        refreshState(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void refreshState(Object arg) {
        this.mHandler.obtainMessage(5, arg).sendToTarget();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void userSwitch(int newUserId) {
        this.mHandler.obtainMessage(7, newUserId, 0).sendToTarget();
    }

    public void fireToggleStateChanged(boolean state) {
        this.mHandler.obtainMessage(8, state ? 1 : 0, 0).sendToTarget();
    }

    public void fireScanStateChanged(boolean state) {
        this.mHandler.obtainMessage(9, state ? 1 : 0, 0).sendToTarget();
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void destroy() {
        this.mHandler.sendEmptyMessage(10);
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public TState getState() {
        return this.mState;
    }

    @Override // com.android.systemui.plugins.qs.QSTile
    public void setDetailListening(boolean listening) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleAddCallback(QSTile.Callback callback) {
        this.mCallbacks.add(callback);
        callback.onStateChanged(this.mState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRemoveCallback(QSTile.Callback callback) {
        this.mCallbacks.remove(callback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRemoveCallbacks() {
        this.mCallbacks.clear();
    }

    protected void handleSecondaryClick() {
        handleClick();
    }

    protected void handleLongClick() {
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(getLongClickIntent(), 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleRefreshState(Object arg) {
        handleUpdateState(this.mTmpState, arg);
        boolean changed = this.mTmpState.copyTo(this.mState);
        if (changed) {
            handleStateChanged();
        }
        this.mHandler.removeMessages(14);
        this.mHandler.sendEmptyMessageDelayed(14, getStaleTimeout());
        setListening(this.mStaleListener, false);
    }

    private void handleStateChanged() {
        String announcement;
        boolean delayAnnouncement = shouldAnnouncementBeDelayed();
        boolean z = false;
        if (this.mCallbacks.size() != 0) {
            for (int i = 0; i < this.mCallbacks.size(); i++) {
                this.mCallbacks.get(i).onStateChanged(this.mState);
            }
            if (this.mAnnounceNextStateChange && !delayAnnouncement && (announcement = composeChangeAnnouncement()) != null) {
                this.mCallbacks.get(0).onAnnouncementRequested(announcement);
            }
        }
        if (this.mAnnounceNextStateChange && delayAnnouncement) {
            z = true;
        }
        this.mAnnounceNextStateChange = z;
    }

    protected boolean shouldAnnouncementBeDelayed() {
        return false;
    }

    protected String composeChangeAnnouncement() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShowDetail(boolean show) {
        this.mShowingDetail = show;
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onShowDetail(show);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isShowingDetail() {
        return this.mShowingDetail;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleToggleStateChanged(boolean state) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onToggleStateChanged(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScanStateChanged(boolean state) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            this.mCallbacks.get(i).onScanStateChanged(state);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleUserSwitch(int newUserId) {
        handleRefreshState(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSetListeningInternal(Object listener, boolean listening) {
        if (listening) {
            if (this.mListeners.add(listener) && this.mListeners.size() == 1) {
                if (DEBUG) {
                    Log.d(this.TAG, "handleSetListening true");
                }
                this.mLifecycle.markState(Lifecycle.State.RESUMED);
                handleSetListening(listening);
                refreshState();
            }
        } else if (this.mListeners.remove(listener) && this.mListeners.size() == 0) {
            if (DEBUG) {
                Log.d(this.TAG, "handleSetListening false");
            }
            this.mLifecycle.markState(Lifecycle.State.DESTROYED);
            handleSetListening(listening);
        }
        updateIsFullQs();
    }

    private void updateIsFullQs() {
        Iterator<Object> it = this.mListeners.iterator();
        while (it.hasNext()) {
            Object listener = it.next();
            if (PagedTileLayout.TilePage.class.equals(listener.getClass())) {
                this.mIsFullQs = 1;
                return;
            }
        }
        this.mIsFullQs = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleDestroy() {
        if (this.mListeners.size() != 0) {
            handleSetListening(false);
        }
        this.mCallbacks.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkIfRestrictionEnforcedByAdminOnly(QSTile.State state, String userRestriction) {
        RestrictedLockUtils.EnforcedAdmin admin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, userRestriction, ActivityManager.getCurrentUser());
        if (admin != null && !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, userRestriction, ActivityManager.getCurrentUser())) {
            state.disabledByPolicy = true;
            this.mEnforcedAdmin = admin;
            return;
        }
        state.disabledByPolicy = false;
        this.mEnforcedAdmin = null;
    }

    public static int getColorForState(Context context, int state) {
        if (state != 0) {
            if (state != 1) {
                if (state == 2) {
                    return Utils.getColorAttrDefaultColor(context, 16843827);
                }
                Log.e("QSTile", "Invalid state " + state);
                return 0;
            }
            return Utils.getColorAttrDefaultColor(context, 16842808);
        }
        return Utils.getDisabled(context, Utils.getColorAttrDefaultColor(context, 16842808));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes21.dex */
    public final class H extends Handler {
        private static final int ADD_CALLBACK = 1;
        private static final int CLICK = 2;
        private static final int DESTROY = 10;
        private static final int LONG_CLICK = 4;
        private static final int REFRESH_STATE = 5;
        private static final int REMOVE_CALLBACK = 12;
        private static final int REMOVE_CALLBACKS = 11;
        private static final int SCAN_STATE_CHANGED = 9;
        private static final int SECONDARY_CLICK = 3;
        private static final int SET_LISTENING = 13;
        private static final int SHOW_DETAIL = 6;
        private static final int STALE = 14;
        private static final int TOGGLE_STATE_CHANGED = 8;
        private static final int USER_SWITCH = 7;

        @VisibleForTesting
        protected H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            try {
                boolean z = true;
                if (msg.what == 1) {
                    QSTileImpl.this.handleAddCallback((QSTile.Callback) msg.obj);
                } else if (msg.what == 11) {
                    QSTileImpl.this.handleRemoveCallbacks();
                } else if (msg.what == 12) {
                    QSTileImpl.this.handleRemoveCallback((QSTile.Callback) msg.obj);
                } else if (msg.what == 2) {
                    if (QSTileImpl.this.mState.disabledByPolicy) {
                        Intent intent = RestrictedLockUtils.getShowAdminSupportDetailsIntent(QSTileImpl.this.mContext, QSTileImpl.this.mEnforcedAdmin);
                        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postStartActivityDismissingKeyguard(intent, 0);
                    } else {
                        QSTileImpl.this.handleClick();
                    }
                } else if (msg.what == 3) {
                    QSTileImpl.this.handleSecondaryClick();
                } else if (msg.what == 4) {
                    QSTileImpl.this.handleLongClick();
                } else if (msg.what == 5) {
                    QSTileImpl.this.handleRefreshState(msg.obj);
                } else if (msg.what == 6) {
                    QSTileImpl qSTileImpl = QSTileImpl.this;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl.handleShowDetail(z);
                } else if (msg.what == 7) {
                    QSTileImpl.this.handleUserSwitch(msg.arg1);
                } else if (msg.what == 8) {
                    QSTileImpl qSTileImpl2 = QSTileImpl.this;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl2.handleToggleStateChanged(z);
                } else if (msg.what == 9) {
                    QSTileImpl qSTileImpl3 = QSTileImpl.this;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl3.handleScanStateChanged(z);
                } else if (msg.what == 10) {
                    QSTileImpl.this.handleDestroy();
                } else if (msg.what == 13) {
                    QSTileImpl qSTileImpl4 = QSTileImpl.this;
                    Object obj = msg.obj;
                    if (msg.arg1 == 0) {
                        z = false;
                    }
                    qSTileImpl4.handleSetListeningInternal(obj, z);
                } else if (msg.what == 14) {
                    QSTileImpl.this.handleStale();
                } else {
                    throw new IllegalArgumentException("Unknown msg: " + msg.what);
                }
            } catch (Throwable t) {
                String error = "Error in " + ((String) null);
                Log.w(QSTileImpl.this.TAG, error, t);
                QSTileImpl.this.mHost.warn(error, t);
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class DrawableIcon extends QSTile.Icon {
        protected final Drawable mDrawable;
        protected final Drawable mInvisibleDrawable;

        public DrawableIcon(Drawable drawable) {
            this.mDrawable = drawable;
            this.mInvisibleDrawable = drawable.getConstantState().newDrawable();
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            return this.mDrawable;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getInvisibleDrawable(Context context) {
            return this.mInvisibleDrawable;
        }
    }

    /* loaded from: classes21.dex */
    public static class DrawableIconWithRes extends DrawableIcon {
        private final int mId;

        public DrawableIconWithRes(Drawable drawable, int id) {
            super(drawable);
            this.mId = id;
        }

        public boolean equals(Object o) {
            return (o instanceof DrawableIconWithRes) && ((DrawableIconWithRes) o).mId == this.mId;
        }
    }

    /* loaded from: classes21.dex */
    public static class ResourceIcon extends QSTile.Icon {
        private static final SparseArray<QSTile.Icon> ICONS = new SparseArray<>();
        protected final int mResId;

        private ResourceIcon(int resId) {
            this.mResId = resId;
        }

        public static synchronized QSTile.Icon get(int resId) {
            QSTile.Icon icon;
            synchronized (ResourceIcon.class) {
                icon = ICONS.get(resId);
                if (icon == null) {
                    icon = new ResourceIcon(resId);
                    ICONS.put(resId, icon);
                }
            }
            return icon;
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            return context.getDrawable(this.mResId);
        }

        @Override // com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getInvisibleDrawable(Context context) {
            return context.getDrawable(this.mResId);
        }

        public boolean equals(Object o) {
            return (o instanceof ResourceIcon) && ((ResourceIcon) o).mResId == this.mResId;
        }

        public String toString() {
            return String.format("ResourceIcon[resId=0x%08x]", Integer.valueOf(this.mResId));
        }
    }

    /* loaded from: classes21.dex */
    protected static class AnimationIcon extends ResourceIcon {
        private final int mAnimatedResId;

        public AnimationIcon(int resId, int staticResId) {
            super(staticResId);
            this.mAnimatedResId = resId;
        }

        @Override // com.android.systemui.qs.tileimpl.QSTileImpl.ResourceIcon, com.android.systemui.plugins.qs.QSTile.Icon
        public Drawable getDrawable(Context context) {
            return context.getDrawable(this.mAnimatedResId).getConstantState().newDrawable();
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(getClass().getSimpleName() + NavigationBarInflaterView.KEY_IMAGE_DELIM);
        pw.print("    ");
        pw.println(getState().toString());
    }
}
