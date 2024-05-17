package com.android.systemui.fragments;

import android.app.Fragment;
import android.app.FragmentController;
import android.app.FragmentHostCallback;
import android.app.FragmentManager;
import android.app.FragmentManagerNonConfig;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.Dependency;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.Plugin;
import com.android.systemui.util.leak.LeakDetector;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class FragmentHostManager {
    private final Context mContext;
    private FragmentController mFragments;
    private FragmentManager.FragmentLifecycleCallbacks mLifecycleCallbacks;
    private final FragmentService mManager;
    private final View mRootView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final HashMap<String, ArrayList<FragmentListener>> mListeners = new HashMap<>();
    private final InterestingConfigChanges mConfigChanges = new InterestingConfigChanges(-1073741052);
    private final ExtensionFragmentManager mPlugins = new ExtensionFragmentManager();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentHostManager(FragmentService manager, View rootView) {
        this.mContext = rootView.getContext();
        this.mManager = manager;
        this.mRootView = rootView;
        this.mConfigChanges.applyNewConfig(this.mContext.getResources());
        createFragmentHost(null);
    }

    private void createFragmentHost(Parcelable savedState) {
        this.mFragments = FragmentController.createController(new HostCallbacks());
        this.mFragments.attachHost(null);
        this.mLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() { // from class: com.android.systemui.fragments.FragmentHostManager.1
            @Override // android.app.FragmentManager.FragmentLifecycleCallbacks
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                FragmentHostManager.this.onFragmentViewCreated(f);
            }

            @Override // android.app.FragmentManager.FragmentLifecycleCallbacks
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                FragmentHostManager.this.onFragmentViewDestroyed(f);
            }

            @Override // android.app.FragmentManager.FragmentLifecycleCallbacks
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                ((LeakDetector) Dependency.get(LeakDetector.class)).trackGarbage(f);
            }
        };
        this.mFragments.getFragmentManager().registerFragmentLifecycleCallbacks(this.mLifecycleCallbacks, true);
        if (savedState != null) {
            this.mFragments.restoreAllState(savedState, (FragmentManagerNonConfig) null);
        }
        this.mFragments.dispatchCreate();
        this.mFragments.dispatchStart();
        this.mFragments.dispatchResume();
    }

    private Parcelable destroyFragmentHost() {
        this.mFragments.dispatchPause();
        Parcelable p = this.mFragments.saveAllState();
        this.mFragments.dispatchStop();
        this.mFragments.dispatchDestroy();
        this.mFragments.getFragmentManager().unregisterFragmentLifecycleCallbacks(this.mLifecycleCallbacks);
        return p;
    }

    public FragmentHostManager addTagListener(String tag, FragmentListener listener) {
        ArrayList<FragmentListener> listeners = this.mListeners.get(tag);
        if (listeners == null) {
            listeners = new ArrayList<>();
            this.mListeners.put(tag, listeners);
        }
        listeners.add(listener);
        Fragment current = getFragmentManager().findFragmentByTag(tag);
        if (current != null && current.getView() != null) {
            listener.onFragmentViewCreated(tag, current);
        }
        return this;
    }

    public void removeTagListener(String tag, FragmentListener listener) {
        ArrayList<FragmentListener> listeners = this.mListeners.get(tag);
        if (listeners != null && listeners.remove(listener) && listeners.size() == 0) {
            this.mListeners.remove(tag);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFragmentViewCreated(final Fragment fragment) {
        final String tag = fragment.getTag();
        ArrayList<FragmentListener> listeners = this.mListeners.get(tag);
        if (listeners != null) {
            listeners.forEach(new Consumer() { // from class: com.android.systemui.fragments.-$$Lambda$FragmentHostManager$OsWXqtcfRJZBAvEEeN8CG6EN5T4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((FragmentHostManager.FragmentListener) obj).onFragmentViewCreated(tag, fragment);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFragmentViewDestroyed(final Fragment fragment) {
        final String tag = fragment.getTag();
        ArrayList<FragmentListener> listeners = this.mListeners.get(tag);
        if (listeners != null) {
            listeners.forEach(new Consumer() { // from class: com.android.systemui.fragments.-$$Lambda$FragmentHostManager$AcJHY99nHc-JEzu3q8ny-wMOZ4E
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((FragmentHostManager.FragmentListener) obj).onFragmentViewDestroyed(tag, fragment);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            reloadFragments();
        } else {
            this.mFragments.dispatchConfigurationChanged(newConfig);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T extends View> T findViewById(int id) {
        return (T) this.mRootView.findViewById(id);
    }

    public FragmentManager getFragmentManager() {
        return this.mFragments.getFragmentManager();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtensionFragmentManager getExtensionManager() {
        return this.mPlugins;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroy() {
        this.mFragments.dispatchDestroy();
    }

    public <T> T create(Class<T> fragmentCls) {
        return (T) this.mPlugins.instantiate(this.mContext, fragmentCls.getName(), null);
    }

    /* loaded from: classes21.dex */
    public interface FragmentListener {
        void onFragmentViewCreated(String str, Fragment fragment);

        default void onFragmentViewDestroyed(String tag, Fragment fragment) {
        }
    }

    public static FragmentHostManager get(View view) {
        try {
            return ((FragmentService) Dependency.get(FragmentService.class)).getFragmentHostManager(view);
        } catch (ClassCastException e) {
            throw e;
        }
    }

    public static void removeAndDestroy(View view) {
        ((FragmentService) Dependency.get(FragmentService.class)).removeAndDestroy(view);
    }

    public void reloadFragments() {
        Parcelable p = destroyFragmentHost();
        createFragmentHost(p);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class HostCallbacks extends FragmentHostCallback<FragmentHostManager> {
        public HostCallbacks() {
            super(FragmentHostManager.this.mContext, FragmentHostManager.this.mHandler, 0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.app.FragmentHostCallback
        public FragmentHostManager onGetHost() {
            return FragmentHostManager.this;
        }

        @Override // android.app.FragmentHostCallback
        public void onDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            FragmentHostManager.this.dump(prefix, fd, writer, args);
        }

        public Fragment instantiate(Context context, String className, Bundle arguments) {
            return FragmentHostManager.this.mPlugins.instantiate(context, className, arguments);
        }

        @Override // android.app.FragmentHostCallback
        public boolean onShouldSaveFragmentState(Fragment fragment) {
            return true;
        }

        @Override // android.app.FragmentHostCallback
        public LayoutInflater onGetLayoutInflater() {
            return LayoutInflater.from(FragmentHostManager.this.mContext);
        }

        @Override // android.app.FragmentHostCallback
        public boolean onUseFragmentManagerInflaterFactory() {
            return true;
        }

        @Override // android.app.FragmentHostCallback
        public boolean onHasWindowAnimations() {
            return false;
        }

        @Override // android.app.FragmentHostCallback
        public int onGetWindowAnimations() {
            return 0;
        }

        @Override // android.app.FragmentHostCallback
        public void onAttachFragment(Fragment fragment) {
        }

        @Override // android.app.FragmentHostCallback, android.app.FragmentContainer
        public <T extends View> T onFindViewById(int id) {
            return (T) FragmentHostManager.this.findViewById(id);
        }

        @Override // android.app.FragmentHostCallback, android.app.FragmentContainer
        public boolean onHasView() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class ExtensionFragmentManager {
        private final ArrayMap<String, Context> mExtensionLookup = new ArrayMap<>();

        ExtensionFragmentManager() {
        }

        public void setCurrentExtension(int id, @NonNull String tag, String oldClass, @NonNull String currentClass, Context context) {
            if (oldClass != null) {
                this.mExtensionLookup.remove(oldClass);
            }
            this.mExtensionLookup.put(currentClass, context);
            FragmentHostManager.this.getFragmentManager().beginTransaction().replace(id, instantiate(context, currentClass, null), tag).commit();
            FragmentHostManager.this.reloadFragments();
        }

        Fragment instantiate(Context context, String className, Bundle arguments) {
            Context extensionContext = this.mExtensionLookup.get(className);
            if (extensionContext != null) {
                Fragment f = instantiateWithInjections(extensionContext, className, arguments);
                if (f instanceof Plugin) {
                    ((Plugin) f).onCreate(FragmentHostManager.this.mContext, extensionContext);
                }
                return f;
            }
            return instantiateWithInjections(context, className, arguments);
        }

        private Fragment instantiateWithInjections(Context context, String className, Bundle args) {
            Method method = FragmentHostManager.this.mManager.getInjectionMap().get(className);
            if (method != null) {
                try {
                    Fragment f = (Fragment) method.invoke(FragmentHostManager.this.mManager.getFragmentCreator(), new Object[0]);
                    if (args != null) {
                        args.setClassLoader(f.getClass().getClassLoader());
                        f.setArguments(args);
                    }
                    return f;
                } catch (IllegalAccessException e) {
                    throw new Fragment.InstantiationException("Unable to instantiate " + className, e);
                } catch (InvocationTargetException e2) {
                    throw new Fragment.InstantiationException("Unable to instantiate " + className, e2);
                }
            }
            return Fragment.instantiate(context, className, args);
        }
    }

    /* loaded from: classes21.dex */
    private static class PluginState {
        String mCls;
        Context mContext;

        private PluginState(String cls, Context context) {
            this.mCls = cls;
            this.mContext = context;
        }
    }
}
