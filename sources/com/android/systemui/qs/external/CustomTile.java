package com.android.systemui.qs.external;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.service.quicksettings.IQSTileService;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.external.TileLifecycleManager;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes21.dex */
public class CustomTile extends QSTileImpl<QSTile.State> implements TileLifecycleManager.TileChangeListener {
    private static final long CUSTOM_STALE_TIMEOUT = 3600000;
    private static final boolean DEBUG = false;
    public static final String PREFIX = "custom(";
    private static final long UNBIND_DELAY = 30000;
    private final ComponentName mComponent;
    private Icon mDefaultIcon;
    private CharSequence mDefaultLabel;
    private boolean mIsShowingDialog;
    private boolean mIsTokenGranted;
    private boolean mListening;
    private final IQSTileService mService;
    private final TileServiceManager mServiceManager;
    private final Tile mTile;
    private final IBinder mToken;
    private final int mUser;
    private final IWindowManager mWindowManager;

    private CustomTile(QSTileHost host, String action) {
        super(host);
        this.mToken = new Binder();
        this.mWindowManager = WindowManagerGlobal.getWindowManagerService();
        this.mComponent = ComponentName.unflattenFromString(action);
        this.mTile = new Tile();
        updateDefaultTileAndIcon();
        this.mServiceManager = host.getTileServices().getTileWrapper(this);
        this.mService = this.mServiceManager.getTileService();
        this.mServiceManager.setTileChangeListener(this);
        this.mUser = ActivityManager.getCurrentUser();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected long getStaleTimeout() {
        return (this.mHost.indexOf(getTileSpec()) * TimeUtils.TIME_ONE_MINUTE) + 3600000;
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0041 A[Catch: NameNotFoundException -> 0x007f, TryCatch #0 {NameNotFoundException -> 0x007f, blocks: (B:3:0x0001, B:5:0x000f, B:6:0x0011, B:8:0x001b, B:10:0x0022, B:12:0x002d, B:18:0x0041, B:20:0x004d, B:22:0x0051, B:23:0x0058, B:25:0x0060, B:28:0x006f, B:30:0x0077, B:9:0x001e), top: B:35:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:19:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0051 A[Catch: NameNotFoundException -> 0x007f, TryCatch #0 {NameNotFoundException -> 0x007f, blocks: (B:3:0x0001, B:5:0x000f, B:6:0x0011, B:8:0x001b, B:10:0x0022, B:12:0x002d, B:18:0x0041, B:20:0x004d, B:22:0x0051, B:23:0x0058, B:25:0x0060, B:28:0x006f, B:30:0x0077, B:9:0x001e), top: B:35:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0077 A[Catch: NameNotFoundException -> 0x007f, TRY_LEAVE, TryCatch #0 {NameNotFoundException -> 0x007f, blocks: (B:3:0x0001, B:5:0x000f, B:6:0x0011, B:8:0x001b, B:10:0x0022, B:12:0x002d, B:18:0x0041, B:20:0x004d, B:22:0x0051, B:23:0x0058, B:25:0x0060, B:28:0x006f, B:30:0x0077, B:9:0x001e), top: B:35:0x0001 }] */
    /* JADX WARN: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateDefaultTileAndIcon() {
        /*
            r10 = this;
            r0 = 0
            android.content.Context r1 = r10.mContext     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            r2 = 786432(0xc0000, float:1.102026E-39)
            boolean r3 = r10.isSystemApp(r1)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r3 == 0) goto L11
            r2 = r2 | 512(0x200, float:7.175E-43)
        L11:
            android.content.ComponentName r3 = r10.mComponent     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.content.pm.ServiceInfo r3 = r1.getServiceInfo(r3, r2)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            int r4 = r3.icon     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r4 == 0) goto L1e
            int r4 = r3.icon     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            goto L22
        L1e:
            android.content.pm.ApplicationInfo r4 = r3.applicationInfo     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            int r4 = r4.icon     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
        L22:
            android.service.quicksettings.Tile r5 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.graphics.drawable.Icon r5 = r5.getIcon()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            r6 = 0
            r7 = 1
            if (r5 == 0) goto L3e
            android.service.quicksettings.Tile r5 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.graphics.drawable.Icon r5 = r5.getIcon()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.graphics.drawable.Icon r8 = r10.mDefaultIcon     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            boolean r5 = r10.iconEquals(r5, r8)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r5 == 0) goto L3c
            goto L3e
        L3c:
            r5 = r6
            goto L3f
        L3e:
            r5 = r7
        L3f:
            if (r4 == 0) goto L4c
            android.content.ComponentName r8 = r10.mComponent     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            java.lang.String r8 = r8.getPackageName()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.graphics.drawable.Icon r8 = android.graphics.drawable.Icon.createWithResource(r8, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            goto L4d
        L4c:
            r8 = r0
        L4d:
            r10.mDefaultIcon = r8     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r5 == 0) goto L58
            android.service.quicksettings.Tile r8 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            android.graphics.drawable.Icon r9 = r10.mDefaultIcon     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            r8.setIcon(r9)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
        L58:
            android.service.quicksettings.Tile r8 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            java.lang.CharSequence r8 = r8.getLabel()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r8 == 0) goto L6e
            android.service.quicksettings.Tile r8 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            java.lang.CharSequence r8 = r8.getLabel()     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            java.lang.CharSequence r9 = r10.mDefaultLabel     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            boolean r8 = android.text.TextUtils.equals(r8, r9)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r8 == 0) goto L6f
        L6e:
            r6 = r7
        L6f:
            java.lang.CharSequence r7 = r3.loadLabel(r1)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            r10.mDefaultLabel = r7     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            if (r6 == 0) goto L7e
            android.service.quicksettings.Tile r7 = r10.mTile     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            java.lang.CharSequence r8 = r10.mDefaultLabel     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
            r7.setLabel(r8)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L7f
        L7e:
            goto L84
        L7f:
            r1 = move-exception
            r10.mDefaultIcon = r0
            r10.mDefaultLabel = r0
        L84:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.qs.external.CustomTile.updateDefaultTileAndIcon():void");
    }

    private boolean isSystemApp(PackageManager pm) throws PackageManager.NameNotFoundException {
        return pm.getApplicationInfo(this.mComponent.getPackageName(), 0).isSystemApp();
    }

    private boolean iconEquals(Icon icon1, Icon icon2) {
        if (icon1 == icon2) {
            return true;
        }
        if (icon1 != null && icon2 != null && icon1.getType() == 2 && icon2.getType() == 2 && icon1.getResId() == icon2.getResId() && Objects.equals(icon1.getResPackage(), icon2.getResPackage())) {
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.qs.external.TileLifecycleManager.TileChangeListener
    public void onTileChanged(ComponentName tile) {
        updateDefaultTileAndIcon();
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public boolean isAvailable() {
        return this.mDefaultIcon != null;
    }

    public int getUser() {
        return this.mUser;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public LogMaker populate(LogMaker logMaker) {
        return super.populate(logMaker).setComponentName(this.mComponent);
    }

    public Tile getQsTile() {
        updateDefaultTileAndIcon();
        return this.mTile;
    }

    public void updateState(Tile tile) {
        this.mTile.setIcon(tile.getIcon());
        this.mTile.setLabel(tile.getLabel());
        this.mTile.setSubtitle(tile.getSubtitle());
        this.mTile.setContentDescription(tile.getContentDescription());
        this.mTile.setState(tile.getState());
    }

    public void onDialogShown() {
        this.mIsShowingDialog = true;
    }

    public void onDialogHidden() {
        this.mIsShowingDialog = false;
        try {
            this.mWindowManager.removeWindowToken(this.mToken, 0);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleSetListening(boolean listening) {
        if (this.mListening == listening) {
            return;
        }
        this.mListening = listening;
        try {
            if (listening) {
                updateDefaultTileAndIcon();
                refreshState();
                if (!this.mServiceManager.isActiveTile()) {
                    this.mServiceManager.setBindRequested(true);
                    this.mService.onStartListening();
                    return;
                }
                return;
            }
            this.mService.onStopListening();
            if (this.mIsTokenGranted && !this.mIsShowingDialog) {
                try {
                    this.mWindowManager.removeWindowToken(this.mToken, 0);
                } catch (RemoteException e) {
                }
                this.mIsTokenGranted = false;
            }
            this.mIsShowingDialog = false;
            this.mServiceManager.setBindRequested(false);
        } catch (RemoteException e2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public void handleDestroy() {
        super.handleDestroy();
        if (this.mIsTokenGranted) {
            try {
                this.mWindowManager.removeWindowToken(this.mToken, 0);
            } catch (RemoteException e) {
            }
        }
        this.mHost.getTileServices().freeService(this, this.mServiceManager);
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public QSTile.State newTileState() {
        QSTile.State state = new QSTile.State();
        return state;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    public Intent getLongClickIntent() {
        Intent i = new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES");
        i.setPackage(this.mComponent.getPackageName());
        Intent i2 = resolveIntent(i);
        if (i2 != null) {
            i2.putExtra("android.intent.extra.COMPONENT_NAME", this.mComponent);
            i2.putExtra("state", this.mTile.getState());
            return i2;
        }
        return new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", this.mComponent.getPackageName(), null));
    }

    private Intent resolveIntent(Intent i) {
        ResolveInfo result = this.mContext.getPackageManager().resolveActivityAsUser(i, 0, ActivityManager.getCurrentUser());
        if (result != null) {
            return new Intent("android.service.quicksettings.action.QS_TILE_PREFERENCES").setClassName(result.activityInfo.packageName, result.activityInfo.name);
        }
        return null;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleClick() {
        if (this.mTile.getState() == 0) {
            return;
        }
        try {
            this.mWindowManager.addWindowToken(this.mToken, 2035, 0);
            this.mIsTokenGranted = true;
        } catch (RemoteException e) {
        }
        try {
            if (this.mServiceManager.isActiveTile()) {
                this.mServiceManager.setBindRequested(true);
                this.mService.onStartListening();
            }
            this.mService.onClick(this.mToken);
        } catch (RemoteException e2) {
        }
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public CharSequence getTileLabel() {
        return getState().label;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl
    protected void handleUpdateState(QSTile.State state, Object arg) {
        Drawable drawable;
        int tileState = this.mTile.getState();
        if (this.mServiceManager.hasPendingBind()) {
            tileState = 0;
        }
        state.state = tileState;
        try {
            drawable = this.mTile.getIcon().loadDrawable(this.mContext);
        } catch (Exception e) {
            Log.w(this.TAG, "Invalid icon, forcing into unavailable state");
            state.state = 0;
            drawable = this.mDefaultIcon.loadDrawable(this.mContext);
        }
        final Drawable drawableF = drawable;
        state.iconSupplier = new Supplier() { // from class: com.android.systemui.qs.external.-$$Lambda$CustomTile$Oh-NzDEMM2yCWnVYbU2_DKTzaqo
            @Override // java.util.function.Supplier
            public final Object get() {
                return CustomTile.lambda$handleUpdateState$0(drawableF);
            }
        };
        state.label = this.mTile.getLabel();
        CharSequence subtitle = this.mTile.getSubtitle();
        if (subtitle != null && subtitle.length() > 0) {
            state.secondaryLabel = subtitle;
        } else {
            state.secondaryLabel = null;
        }
        if (this.mTile.getContentDescription() != null) {
            state.contentDescription = this.mTile.getContentDescription();
        } else {
            state.contentDescription = state.label;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ QSTile.Icon lambda$handleUpdateState$0(Drawable drawableF) {
        Drawable.ConstantState cs = drawableF.getConstantState();
        if (cs != null) {
            return new QSTileImpl.DrawableIcon(cs.newDrawable());
        }
        return null;
    }

    @Override // com.android.systemui.qs.tileimpl.QSTileImpl, com.android.systemui.plugins.qs.QSTile
    public int getMetricsCategory() {
        return 268;
    }

    public void startUnlockAndRun() {
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).postQSRunnableDismissingKeyguard(new Runnable() { // from class: com.android.systemui.qs.external.-$$Lambda$CustomTile$q1MKWZaaapZOjYFe9CyeyabLR0Q
            @Override // java.lang.Runnable
            public final void run() {
                CustomTile.this.lambda$startUnlockAndRun$1$CustomTile();
            }
        });
    }

    public /* synthetic */ void lambda$startUnlockAndRun$1$CustomTile() {
        try {
            this.mService.onUnlockComplete();
        } catch (RemoteException e) {
        }
    }

    public static String toSpec(ComponentName name) {
        return PREFIX + name.flattenToShortString() + NavigationBarInflaterView.KEY_CODE_END;
    }

    public static ComponentName getComponentFromSpec(String spec) {
        String action = spec.substring(PREFIX.length(), spec.length() - 1);
        if (action.isEmpty()) {
            throw new IllegalArgumentException("Empty custom tile spec action");
        }
        return ComponentName.unflattenFromString(action);
    }

    public static CustomTile create(QSTileHost host, String spec) {
        if (spec == null || !spec.startsWith(PREFIX) || !spec.endsWith(NavigationBarInflaterView.KEY_CODE_END)) {
            throw new IllegalArgumentException("Bad custom tile spec: " + spec);
        }
        String action = spec.substring(PREFIX.length(), spec.length() - 1);
        if (action.isEmpty()) {
            throw new IllegalArgumentException("Empty custom tile spec action");
        }
        return new CustomTile(host, action);
    }
}
