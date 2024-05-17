package com.android.keyguard.clock;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.Observer;
import com.android.keyguard.clock.ClockInfo;
import com.android.keyguard.clock.ClockManager;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.settings.CurrentUserObservable;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes19.dex */
public final class ClockManager {
    private static final String TAG = "ClockOptsProvider";
    private final List<Supplier<ClockPlugin>> mBuiltinClocks;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final CurrentUserObservable mCurrentUserObservable;
    private final Observer<Integer> mCurrentUserObserver;
    private final DockManager.DockEventListener mDockEventListener;
    private final DockManager mDockManager;
    private final int mHeight;
    private boolean mIsDocked;
    private final Map<ClockChangedListener, AvailableClocks> mListeners;
    private final Handler mMainHandler;
    private final PluginManager mPluginManager;
    private final AvailableClocks mPreviewClocks;
    private final SettingsWrapper mSettingsWrapper;
    private final int mWidth;

    /* loaded from: classes19.dex */
    public interface ClockChangedListener {
        void onClockChanged(ClockPlugin clockPlugin);
    }

    public /* synthetic */ void lambda$new$0$ClockManager(Integer newUserId) {
        reload();
    }

    @Inject
    public ClockManager(Context context, InjectionInflationController injectionInflater, PluginManager pluginManager, SysuiColorExtractor colorExtractor, DockManager dockManager) {
        this(context, injectionInflater, pluginManager, colorExtractor, context.getContentResolver(), new CurrentUserObservable(context), new SettingsWrapper(context.getContentResolver()), dockManager);
    }

    @VisibleForTesting
    ClockManager(Context context, InjectionInflationController injectionInflater, PluginManager pluginManager, final SysuiColorExtractor colorExtractor, ContentResolver contentResolver, CurrentUserObservable currentUserObservable, SettingsWrapper settingsWrapper, DockManager dockManager) {
        this.mBuiltinClocks = new ArrayList();
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mContentObserver = new ContentObserver(this.mMainHandler) { // from class: com.android.keyguard.clock.ClockManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Uri uri, int userId) {
                super.onChange(selfChange, uri, userId);
                if (Objects.equals(Integer.valueOf(userId), ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue())) {
                    ClockManager.this.reload();
                }
            }
        };
        this.mCurrentUserObserver = new Observer() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$hg7TNpAa_jeQQKjwxI39ao59w9U
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                ClockManager.this.lambda$new$0$ClockManager((Integer) obj);
            }
        };
        this.mDockEventListener = new DockManager.DockEventListener() { // from class: com.android.keyguard.clock.ClockManager.2
            @Override // com.android.systemui.dock.DockManager.DockEventListener
            public void onEvent(int event) {
                ClockManager clockManager = ClockManager.this;
                boolean z = true;
                if (event != 1 && event != 2) {
                    z = false;
                }
                clockManager.mIsDocked = z;
                ClockManager.this.reload();
            }
        };
        this.mListeners = new ArrayMap();
        this.mContext = context;
        this.mPluginManager = pluginManager;
        this.mContentResolver = contentResolver;
        this.mSettingsWrapper = settingsWrapper;
        this.mCurrentUserObservable = currentUserObservable;
        this.mDockManager = dockManager;
        this.mPreviewClocks = new AvailableClocks();
        final Resources res = context.getResources();
        final LayoutInflater layoutInflater = injectionInflater.injectable(LayoutInflater.from(context));
        addBuiltinClock(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$qcpjSm9nfcenHjNSU7lKV-TGsX4
            @Override // java.util.function.Supplier
            public final Object get() {
                return ClockManager.lambda$new$1(res, layoutInflater, colorExtractor);
            }
        });
        addBuiltinClock(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$mCJuewhSbfqGAUXaP_8PWw4nqZs
            @Override // java.util.function.Supplier
            public final Object get() {
                return ClockManager.lambda$new$2(res, layoutInflater, colorExtractor);
            }
        });
        addBuiltinClock(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$KuKx3QjFfullqZu9O8YrysFYdRw
            @Override // java.util.function.Supplier
            public final Object get() {
                return ClockManager.lambda$new$3(res, layoutInflater, colorExtractor);
            }
        });
        DisplayMetrics dm = res.getDisplayMetrics();
        this.mWidth = dm.widthPixels;
        this.mHeight = dm.heightPixels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ClockPlugin lambda$new$1(Resources res, LayoutInflater layoutInflater, SysuiColorExtractor colorExtractor) {
        return new DefaultClockController(res, layoutInflater, colorExtractor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ClockPlugin lambda$new$2(Resources res, LayoutInflater layoutInflater, SysuiColorExtractor colorExtractor) {
        return new BubbleClockController(res, layoutInflater, colorExtractor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ClockPlugin lambda$new$3(Resources res, LayoutInflater layoutInflater, SysuiColorExtractor colorExtractor) {
        return new AnalogClockController(res, layoutInflater, colorExtractor);
    }

    public void addOnClockChangedListener(ClockChangedListener listener) {
        if (this.mListeners.isEmpty()) {
            register();
        }
        AvailableClocks availableClocks = new AvailableClocks();
        for (int i = 0; i < this.mBuiltinClocks.size(); i++) {
            availableClocks.addClockPlugin(this.mBuiltinClocks.get(i).get());
        }
        this.mListeners.put(listener, availableClocks);
        this.mPluginManager.addPluginListener((PluginListener) availableClocks, ClockPlugin.class, true);
        reload();
    }

    public void removeOnClockChangedListener(ClockChangedListener listener) {
        AvailableClocks availableClocks = this.mListeners.remove(listener);
        this.mPluginManager.removePluginListener(availableClocks);
        if (this.mListeners.isEmpty()) {
            unregister();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ClockInfo> getClockInfos() {
        return this.mPreviewClocks.getInfo();
    }

    ClockPlugin getCurrentClock() {
        return this.mPreviewClocks.getCurrentClock();
    }

    @VisibleForTesting
    boolean isDocked() {
        return this.mIsDocked;
    }

    @VisibleForTesting
    ContentObserver getContentObserver() {
        return this.mContentObserver;
    }

    private void addBuiltinClock(Supplier<ClockPlugin> pluginSupplier) {
        ClockPlugin plugin = pluginSupplier.get();
        this.mPreviewClocks.addClockPlugin(plugin);
        this.mBuiltinClocks.add(pluginSupplier);
    }

    private void register() {
        this.mPluginManager.addPluginListener((PluginListener) this.mPreviewClocks, ClockPlugin.class, true);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("lock_screen_custom_clock_face"), false, this.mContentObserver, -1);
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("docked_clock_face"), false, this.mContentObserver, -1);
        this.mCurrentUserObservable.getCurrentUser().observeForever(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
        }
    }

    private void unregister() {
        this.mPluginManager.removePluginListener(this.mPreviewClocks);
        this.mContentResolver.unregisterContentObserver(this.mContentObserver);
        this.mCurrentUserObservable.getCurrentUser().removeObserver(this.mCurrentUserObserver);
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.removeListener(this.mDockEventListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reload() {
        this.mPreviewClocks.reloadCurrentClock();
        this.mListeners.forEach(new BiConsumer() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$i436KHmxBKLRfCOA6rL_7pJbxgc
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ClockManager.lambda$reload$4((ClockManager.ClockChangedListener) obj, (ClockManager.AvailableClocks) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$reload$4(ClockChangedListener listener, AvailableClocks clocks) {
        clocks.reloadCurrentClock();
        ClockPlugin clock = clocks.getCurrentClock();
        if (clock instanceof DefaultClockController) {
            listener.onClockChanged(null);
        } else {
            listener.onClockChanged(clock);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public final class AvailableClocks implements PluginListener<ClockPlugin> {
        private final List<ClockInfo> mClockInfo;
        private final Map<String, ClockPlugin> mClocks;
        private ClockPlugin mCurrentClock;

        private AvailableClocks() {
            this.mClocks = new ArrayMap();
            this.mClockInfo = new ArrayList();
        }

        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginConnected(ClockPlugin plugin, Context pluginContext) {
            addClockPlugin(plugin);
            reloadIfNeeded(plugin);
        }

        @Override // com.android.systemui.plugins.PluginListener
        public void onPluginDisconnected(ClockPlugin plugin) {
            removeClockPlugin(plugin);
            reloadIfNeeded(plugin);
        }

        ClockPlugin getCurrentClock() {
            return this.mCurrentClock;
        }

        List<ClockInfo> getInfo() {
            return this.mClockInfo;
        }

        void addClockPlugin(final ClockPlugin plugin) {
            String id = plugin.getClass().getName();
            this.mClocks.put(plugin.getClass().getName(), plugin);
            List<ClockInfo> list = this.mClockInfo;
            ClockInfo.Builder id2 = ClockInfo.builder().setName(plugin.getName()).setTitle(plugin.getTitle()).setId(id);
            Objects.requireNonNull(plugin);
            list.add(id2.setThumbnail(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$d3U4w-CuqsezzeLGogc1fLHnUj0
                @Override // java.util.function.Supplier
                public final Object get() {
                    return ClockPlugin.this.getThumbnail();
                }
            }).setPreview(new Supplier() { // from class: com.android.keyguard.clock.-$$Lambda$ClockManager$AvailableClocks$3xFQeynnnUMh38fqZ7v9xTaqzmA
                @Override // java.util.function.Supplier
                public final Object get() {
                    return ClockManager.AvailableClocks.this.lambda$addClockPlugin$0$ClockManager$AvailableClocks(plugin);
                }
            }).build());
        }

        public /* synthetic */ Bitmap lambda$addClockPlugin$0$ClockManager$AvailableClocks(ClockPlugin plugin) {
            return plugin.getPreview(ClockManager.this.mWidth, ClockManager.this.mHeight);
        }

        private void removeClockPlugin(ClockPlugin plugin) {
            String id = plugin.getClass().getName();
            this.mClocks.remove(id);
            for (int i = 0; i < this.mClockInfo.size(); i++) {
                if (id.equals(this.mClockInfo.get(i).getId())) {
                    this.mClockInfo.remove(i);
                    return;
                }
            }
        }

        private void reloadIfNeeded(ClockPlugin plugin) {
            boolean wasCurrentClock = plugin == this.mCurrentClock;
            reloadCurrentClock();
            boolean isCurrentClock = plugin == this.mCurrentClock;
            if (wasCurrentClock || isCurrentClock) {
                ClockManager.this.reload();
            }
        }

        void reloadCurrentClock() {
            this.mCurrentClock = getClockPlugin();
        }

        private ClockPlugin getClockPlugin() {
            String name;
            ClockPlugin plugin = null;
            if (ClockManager.this.isDocked() && (name = ClockManager.this.mSettingsWrapper.getDockedClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue().intValue())) != null && (plugin = this.mClocks.get(name)) != null) {
                return plugin;
            }
            String name2 = ClockManager.this.mSettingsWrapper.getLockScreenCustomClockFace(ClockManager.this.mCurrentUserObservable.getCurrentUser().getValue().intValue());
            if (name2 != null) {
                return this.mClocks.get(name2);
            }
            return plugin;
        }
    }
}
