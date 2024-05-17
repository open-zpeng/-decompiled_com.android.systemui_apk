package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.qs.AutoAddTracker;
import com.android.systemui.qs.QSTileHost;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.statusbar.phone.AutoTileManager;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.HotspotController;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class AutoTileManager {
    public static final String CAST = "cast";
    public static final String HOTSPOT = "hotspot";
    public static final String INVERSION = "inversion";
    public static final String NIGHT = "night";
    public static final String SAVER = "saver";
    public static final String WORK = "work";
    private final AutoAddTracker mAutoTracker;
    private final CastController mCastController;
    private SecureSetting mColorsSetting;
    private final Context mContext;
    private final DataSaverController mDataSaverController;
    private final Handler mHandler;
    private final QSTileHost mHost;
    private final HotspotController mHotspotController;
    private final ManagedProfileController mManagedProfileController;
    private final NightDisplayListener mNightDisplayListener;
    private final ManagedProfileController.Callback mProfileCallback = new ManagedProfileController.Callback() { // from class: com.android.systemui.statusbar.phone.AutoTileManager.2
        @Override // com.android.systemui.statusbar.phone.ManagedProfileController.Callback
        public void onManagedProfileChanged() {
            if (!AutoTileManager.this.mAutoTracker.isAdded("work") && AutoTileManager.this.mManagedProfileController.hasActiveProfile()) {
                AutoTileManager.this.mHost.addTile("work");
                AutoTileManager.this.mAutoTracker.setTileAdded("work");
            }
        }

        @Override // com.android.systemui.statusbar.phone.ManagedProfileController.Callback
        public void onManagedProfileRemoved() {
        }
    };
    private final DataSaverController.Listener mDataSaverListener = new AnonymousClass3();
    private final HotspotController.Callback mHotspotCallback = new AnonymousClass4();
    @VisibleForTesting
    final NightDisplayListener.Callback mNightDisplayCallback = new AnonymousClass5();
    @VisibleForTesting
    final CastController.Callback mCastCallback = new AnonymousClass6();

    @Inject
    public AutoTileManager(Context context, AutoAddTracker autoAddTracker, QSTileHost host, @Named("background_handler") Handler handler, HotspotController hotspotController, DataSaverController dataSaverController, ManagedProfileController managedProfileController, NightDisplayListener nightDisplayListener, CastController castController) {
        this.mAutoTracker = autoAddTracker;
        this.mContext = context;
        this.mHost = host;
        this.mHandler = handler;
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        this.mManagedProfileController = managedProfileController;
        this.mNightDisplayListener = nightDisplayListener;
        this.mCastController = castController;
        if (!this.mAutoTracker.isAdded(HOTSPOT)) {
            hotspotController.addCallback(this.mHotspotCallback);
        }
        if (!this.mAutoTracker.isAdded(SAVER)) {
            dataSaverController.addCallback(this.mDataSaverListener);
        }
        if (!this.mAutoTracker.isAdded(INVERSION)) {
            this.mColorsSetting = new AnonymousClass1(this.mContext, this.mHandler, "accessibility_display_inversion_enabled");
            this.mColorsSetting.setListening(true);
        }
        if (!this.mAutoTracker.isAdded("work")) {
            managedProfileController.addCallback(this.mProfileCallback);
        }
        if (!this.mAutoTracker.isAdded(NIGHT) && ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            nightDisplayListener.setCallback(this.mNightDisplayCallback);
        }
        if (!this.mAutoTracker.isAdded(CAST)) {
            castController.addCallback(this.mCastCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.AutoTileManager$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends SecureSetting {
        AnonymousClass1(Context context, Handler handler, String settingName) {
            super(context, handler, settingName);
        }

        @Override // com.android.systemui.qs.SecureSetting
        protected void handleValueChanged(int value, boolean observedChange) {
            if (!AutoTileManager.this.mAutoTracker.isAdded(AutoTileManager.INVERSION) && value != 0) {
                AutoTileManager.this.mHost.addTile(AutoTileManager.INVERSION);
                AutoTileManager.this.mAutoTracker.setTileAdded(AutoTileManager.INVERSION);
                AutoTileManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$1$fkFB83CLnhxsYFtYdorSMjVQp8g
                    @Override // java.lang.Runnable
                    public final void run() {
                        AutoTileManager.AnonymousClass1.this.lambda$handleValueChanged$0$AutoTileManager$1();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$handleValueChanged$0$AutoTileManager$1() {
            AutoTileManager.this.mColorsSetting.setListening(false);
        }
    }

    public void destroy() {
        SecureSetting secureSetting = this.mColorsSetting;
        if (secureSetting != null) {
            secureSetting.setListening(false);
        }
        this.mAutoTracker.destroy();
        this.mHotspotController.removeCallback(this.mHotspotCallback);
        this.mDataSaverController.removeCallback(this.mDataSaverListener);
        this.mManagedProfileController.removeCallback(this.mProfileCallback);
        if (ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
        }
        this.mCastController.removeCallback(this.mCastCallback);
    }

    public void unmarkTileAsAutoAdded(String tabSpec) {
        this.mAutoTracker.setTileRemoved(tabSpec);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.AutoTileManager$3  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass3 implements DataSaverController.Listener {
        AnonymousClass3() {
        }

        @Override // com.android.systemui.statusbar.policy.DataSaverController.Listener
        public void onDataSaverChanged(boolean isDataSaving) {
            if (!AutoTileManager.this.mAutoTracker.isAdded(AutoTileManager.SAVER) && isDataSaving) {
                AutoTileManager.this.mHost.addTile(AutoTileManager.SAVER);
                AutoTileManager.this.mAutoTracker.setTileAdded(AutoTileManager.SAVER);
                AutoTileManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$3$jtlbOv9xqjXTNoW_lFuZ_dYzc1k
                    @Override // java.lang.Runnable
                    public final void run() {
                        AutoTileManager.AnonymousClass3.this.lambda$onDataSaverChanged$0$AutoTileManager$3();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onDataSaverChanged$0$AutoTileManager$3() {
            AutoTileManager.this.mDataSaverController.removeCallback(AutoTileManager.this.mDataSaverListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.AutoTileManager$4  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass4 implements HotspotController.Callback {
        AnonymousClass4() {
        }

        @Override // com.android.systemui.statusbar.policy.HotspotController.Callback
        public void onHotspotChanged(boolean enabled, int numDevices) {
            if (!AutoTileManager.this.mAutoTracker.isAdded(AutoTileManager.HOTSPOT) && enabled) {
                AutoTileManager.this.mHost.addTile(AutoTileManager.HOTSPOT);
                AutoTileManager.this.mAutoTracker.setTileAdded(AutoTileManager.HOTSPOT);
                AutoTileManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$4$B3sgSxASy9hbK7cekuTaJNclHvY
                    @Override // java.lang.Runnable
                    public final void run() {
                        AutoTileManager.AnonymousClass4.this.lambda$onHotspotChanged$0$AutoTileManager$4();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onHotspotChanged$0$AutoTileManager$4() {
            AutoTileManager.this.mHotspotController.removeCallback(AutoTileManager.this.mHotspotCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.AutoTileManager$5  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass5 implements NightDisplayListener.Callback {
        AnonymousClass5() {
        }

        public void onActivated(boolean activated) {
            if (activated) {
                addNightTile();
            }
        }

        public void onAutoModeChanged(int autoMode) {
            if (autoMode == 1 || autoMode == 2) {
                addNightTile();
            }
        }

        private void addNightTile() {
            if (AutoTileManager.this.mAutoTracker.isAdded(AutoTileManager.NIGHT)) {
                return;
            }
            AutoTileManager.this.mHost.addTile(AutoTileManager.NIGHT);
            AutoTileManager.this.mAutoTracker.setTileAdded(AutoTileManager.NIGHT);
            AutoTileManager.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$5$RSaNJ4x5t8UQTrBCygb8--uU0S0
                @Override // java.lang.Runnable
                public final void run() {
                    AutoTileManager.AnonymousClass5.this.lambda$addNightTile$0$AutoTileManager$5();
                }
            });
        }

        public /* synthetic */ void lambda$addNightTile$0$AutoTileManager$5() {
            AutoTileManager.this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.AutoTileManager$6  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass6 implements CastController.Callback {
        AnonymousClass6() {
        }

        /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
        @Override // com.android.systemui.statusbar.policy.CastController.Callback
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onCastDevicesChanged() {
            /*
                r6 = this;
                com.android.systemui.statusbar.phone.AutoTileManager r0 = com.android.systemui.statusbar.phone.AutoTileManager.this
                com.android.systemui.qs.AutoAddTracker r0 = com.android.systemui.statusbar.phone.AutoTileManager.access$000(r0)
                java.lang.String r1 = "cast"
                boolean r0 = r0.isAdded(r1)
                if (r0 == 0) goto Lf
                return
            Lf:
                r0 = 0
                com.android.systemui.statusbar.phone.AutoTileManager r2 = com.android.systemui.statusbar.phone.AutoTileManager.this
                com.android.systemui.statusbar.policy.CastController r2 = com.android.systemui.statusbar.phone.AutoTileManager.access$1000(r2)
                java.util.List r2 = r2.getCastDevices()
                java.util.Iterator r2 = r2.iterator()
            L1e:
                boolean r3 = r2.hasNext()
                if (r3 == 0) goto L38
                java.lang.Object r3 = r2.next()
                com.android.systemui.statusbar.policy.CastController$CastDevice r3 = (com.android.systemui.statusbar.policy.CastController.CastDevice) r3
                int r4 = r3.state
                r5 = 2
                if (r4 == r5) goto L36
                int r4 = r3.state
                r5 = 1
                if (r4 != r5) goto L35
                goto L36
            L35:
                goto L1e
            L36:
                r0 = 1
            L38:
                if (r0 == 0) goto L5a
                com.android.systemui.statusbar.phone.AutoTileManager r2 = com.android.systemui.statusbar.phone.AutoTileManager.this
                com.android.systemui.qs.QSTileHost r2 = com.android.systemui.statusbar.phone.AutoTileManager.access$100(r2)
                r2.addTile(r1)
                com.android.systemui.statusbar.phone.AutoTileManager r2 = com.android.systemui.statusbar.phone.AutoTileManager.this
                com.android.systemui.qs.AutoAddTracker r2 = com.android.systemui.statusbar.phone.AutoTileManager.access$000(r2)
                r2.setTileAdded(r1)
                com.android.systemui.statusbar.phone.AutoTileManager r1 = com.android.systemui.statusbar.phone.AutoTileManager.this
                android.os.Handler r1 = com.android.systemui.statusbar.phone.AutoTileManager.access$200(r1)
                com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$6$Es5SN3-RKnhrBR7n3pYQ0OR57uE r2 = new com.android.systemui.statusbar.phone.-$$Lambda$AutoTileManager$6$Es5SN3-RKnhrBR7n3pYQ0OR57uE
                r2.<init>()
                r1.post(r2)
            L5a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.AutoTileManager.AnonymousClass6.onCastDevicesChanged():void");
        }

        public /* synthetic */ void lambda$onCastDevicesChanged$0$AutoTileManager$6() {
            AutoTileManager.this.mCastController.removeCallback(AutoTileManager.this.mCastCallback);
        }
    }
}
