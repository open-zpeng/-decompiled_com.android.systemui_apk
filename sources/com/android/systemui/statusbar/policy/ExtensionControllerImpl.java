package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.ArrayMap;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.LeakDetector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class ExtensionControllerImpl implements ExtensionController {
    public static final int SORT_ORDER_DEFAULT = 4;
    public static final int SORT_ORDER_FEATURE = 2;
    public static final int SORT_ORDER_PLUGIN = 0;
    public static final int SORT_ORDER_TUNER = 1;
    public static final int SORT_ORDER_UI_MODE = 3;
    private final ConfigurationController mConfigurationController;
    private final Context mDefaultContext;
    private final LeakDetector mLeakDetector;
    private final PluginManager mPluginManager;
    private final TunerService mTunerService;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface Item<T> extends Producer<T> {
        int sortOrder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public interface Producer<T> {
        void destroy();

        T get();
    }

    @Inject
    public ExtensionControllerImpl(Context context, LeakDetector leakDetector, PluginManager pluginManager, TunerService tunerService, ConfigurationController configurationController) {
        this.mDefaultContext = context;
        this.mLeakDetector = leakDetector;
        this.mPluginManager = pluginManager;
        this.mTunerService = tunerService;
        this.mConfigurationController = configurationController;
    }

    @Override // com.android.systemui.statusbar.policy.ExtensionController
    public <T> ExtensionBuilder<T> newExtension(Class<T> cls) {
        return new ExtensionBuilder<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ExtensionBuilder<T> implements ExtensionController.ExtensionBuilder<T> {
        private ExtensionImpl<T> mExtension;

        private ExtensionBuilder() {
            this.mExtension = new ExtensionImpl<>();
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.ExtensionBuilder<T> withTunerFactory(ExtensionController.TunerFactory<T> factory) {
            this.mExtension.addTunerFactory(factory, factory.keys());
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public <P extends T> ExtensionController.ExtensionBuilder<T> withPlugin(Class<P> cls) {
            return withPlugin(cls, PluginManager.Helper.getAction(cls));
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public <P extends T> ExtensionController.ExtensionBuilder<T> withPlugin(Class<P> cls, String action) {
            return withPlugin(cls, action, null);
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public <P> ExtensionController.ExtensionBuilder<T> withPlugin(Class<P> cls, String action, ExtensionController.PluginConverter<T, P> converter) {
            this.mExtension.addPlugin(action, cls, converter);
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.ExtensionBuilder<T> withDefault(Supplier<T> def) {
            this.mExtension.addDefault(def);
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.ExtensionBuilder<T> withUiMode(int uiMode, Supplier<T> supplier) {
            this.mExtension.addUiMode(uiMode, supplier);
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.ExtensionBuilder<T> withFeature(String feature, Supplier<T> supplier) {
            this.mExtension.addFeature(feature, supplier);
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.ExtensionBuilder<T> withCallback(Consumer<T> callback) {
            ((ExtensionImpl) this.mExtension).mCallbacks.add(callback);
            return this;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.ExtensionBuilder
        public ExtensionController.Extension build() {
            Collections.sort(((ExtensionImpl) this.mExtension).mProducers, Comparator.comparingInt(new ToIntFunction() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$LO8p3lRLZXpohPDzojcJ_BVuMnk
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((ExtensionControllerImpl.Item) obj).sortOrder();
                }
            }));
            this.mExtension.notifyChanged();
            return this.mExtension;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ExtensionImpl<T> implements ExtensionController.Extension<T> {
        private final ArrayList<Consumer<T>> mCallbacks;
        private T mItem;
        private Context mPluginContext;
        private final ArrayList<Item<T>> mProducers;

        private ExtensionImpl() {
            this.mProducers = new ArrayList<>();
            this.mCallbacks = new ArrayList<>();
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public void addCallback(Consumer<T> callback) {
            this.mCallbacks.add(callback);
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public T get() {
            return this.mItem;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public Context getContext() {
            Context context = this.mPluginContext;
            return context != null ? context : ExtensionControllerImpl.this.mDefaultContext;
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public void destroy() {
            for (int i = 0; i < this.mProducers.size(); i++) {
                this.mProducers.get(i).destroy();
            }
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public T reload() {
            notifyChanged();
            return get();
        }

        @Override // com.android.systemui.statusbar.policy.ExtensionController.Extension
        public void clearItem(boolean isDestroyed) {
            if (isDestroyed && this.mItem != null) {
                ExtensionControllerImpl.this.mLeakDetector.trackGarbage(this.mItem);
            }
            this.mItem = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void notifyChanged() {
            if (this.mItem != null) {
                ExtensionControllerImpl.this.mLeakDetector.trackGarbage(this.mItem);
            }
            this.mItem = null;
            int i = 0;
            while (true) {
                if (i >= this.mProducers.size()) {
                    break;
                }
                T item = this.mProducers.get(i).get();
                if (item == null) {
                    i++;
                } else {
                    this.mItem = item;
                    break;
                }
            }
            for (int i2 = 0; i2 < this.mCallbacks.size(); i2++) {
                this.mCallbacks.get(i2).accept(this.mItem);
            }
        }

        public void addDefault(Supplier<T> def) {
            this.mProducers.add(new Default(def));
        }

        public <P> void addPlugin(String action, Class<P> cls, ExtensionController.PluginConverter<T, P> converter) {
            this.mProducers.add(new PluginItem(action, cls, converter));
        }

        public void addTunerFactory(ExtensionController.TunerFactory<T> factory, String[] keys) {
            this.mProducers.add(new TunerItem(factory, keys));
        }

        public void addUiMode(int uiMode, Supplier<T> mode) {
            this.mProducers.add(new UiModeItem(uiMode, mode));
        }

        public void addFeature(String feature, Supplier<T> mode) {
            this.mProducers.add(new FeatureItem(feature, mode));
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public class PluginItem<P extends Plugin> implements Item<T>, PluginListener<P> {
            private final ExtensionController.PluginConverter<T, P> mConverter;
            private T mItem;

            public PluginItem(String action, Class<P> cls, ExtensionController.PluginConverter<T, P> converter) {
                this.mConverter = converter;
                ExtensionControllerImpl.this.mPluginManager.addPluginListener(action, (PluginListener) this, (Class<?>) cls);
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginConnected(P plugin, Context pluginContext) {
                ExtensionImpl.this.mPluginContext = pluginContext;
                ExtensionController.PluginConverter<T, P> pluginConverter = this.mConverter;
                if (pluginConverter != null) {
                    this.mItem = pluginConverter.getInterfaceFromPlugin(plugin);
                } else {
                    this.mItem = plugin;
                }
                ExtensionImpl.this.notifyChanged();
            }

            @Override // com.android.systemui.plugins.PluginListener
            public void onPluginDisconnected(P plugin) {
                ExtensionImpl.this.mPluginContext = null;
                this.mItem = null;
                ExtensionImpl.this.notifyChanged();
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public T get() {
                return this.mItem;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public void destroy() {
                ExtensionControllerImpl.this.mPluginManager.removePluginListener(this);
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Item
            public int sortOrder() {
                return 0;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public class TunerItem<T> implements Item<T>, TunerService.Tunable {
            private final ExtensionController.TunerFactory<T> mFactory;
            private T mItem;
            private final ArrayMap<String, String> mSettings = new ArrayMap<>();

            public TunerItem(ExtensionController.TunerFactory<T> factory, String... setting) {
                this.mFactory = factory;
                ExtensionControllerImpl.this.mTunerService.addTunable(this, setting);
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public T get() {
                return this.mItem;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public void destroy() {
                ExtensionControllerImpl.this.mTunerService.removeTunable(this);
            }

            @Override // com.android.systemui.tuner.TunerService.Tunable
            public void onTuningChanged(String key, String newValue) {
                this.mSettings.put(key, newValue);
                this.mItem = this.mFactory.create(this.mSettings);
                ExtensionImpl.this.notifyChanged();
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Item
            public int sortOrder() {
                return 1;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public class UiModeItem<T> implements Item<T>, ConfigurationController.ConfigurationListener {
            private final int mDesiredUiMode;
            private Handler mHandler = new Handler();
            private final Supplier<T> mSupplier;
            private int mUiMode;

            public UiModeItem(int uiMode, Supplier<T> supplier) {
                this.mDesiredUiMode = uiMode;
                this.mSupplier = supplier;
                this.mUiMode = ExtensionControllerImpl.this.mDefaultContext.getResources().getConfiguration().uiMode & 15;
                ExtensionControllerImpl.this.mConfigurationController.addCallback(this);
            }

            @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
            public void onConfigChanged(Configuration newConfig) {
                int newMode = newConfig.uiMode & 15;
                if (newMode != this.mUiMode) {
                    this.mUiMode = newMode;
                    Handler handler = this.mHandler;
                    final ExtensionImpl extensionImpl = ExtensionImpl.this;
                    handler.post(new Runnable() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$ExtensionControllerImpl$ExtensionImpl$UiModeItem$YxWnygmpicVRY0SiBFRly9CYj24
                        @Override // java.lang.Runnable
                        public final void run() {
                            ExtensionControllerImpl.ExtensionImpl.this.notifyChanged();
                        }
                    });
                }
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public T get() {
                if (this.mUiMode == this.mDesiredUiMode) {
                    return this.mSupplier.get();
                }
                return null;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public void destroy() {
                ExtensionControllerImpl.this.mConfigurationController.removeCallback(this);
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Item
            public int sortOrder() {
                return 3;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public class FeatureItem<T> implements Item<T> {
            private final String mFeature;
            private final Supplier<T> mSupplier;

            public FeatureItem(String feature, Supplier<T> supplier) {
                this.mSupplier = supplier;
                this.mFeature = feature;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public T get() {
                if (ExtensionControllerImpl.this.mDefaultContext.getPackageManager().hasSystemFeature(this.mFeature)) {
                    return this.mSupplier.get();
                }
                return null;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public void destroy() {
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Item
            public int sortOrder() {
                return 2;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes21.dex */
        public class Default<T> implements Item<T> {
            private final Supplier<T> mSupplier;

            public Default(Supplier<T> supplier) {
                this.mSupplier = supplier;
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public T get() {
                return this.mSupplier.get();
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Producer
            public void destroy() {
            }

            @Override // com.android.systemui.statusbar.policy.ExtensionControllerImpl.Item
            public int sortOrder() {
                return 4;
            }
        }
    }
}
