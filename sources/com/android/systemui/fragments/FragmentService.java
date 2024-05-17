package com.android.systemui.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import com.android.systemui.Dumpable;
import com.android.systemui.SystemUIRootComponent;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.qs.QSFragment;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.Subcomponent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class FragmentService implements Dumpable {
    private static final String TAG = "FragmentService";
    private final FragmentCreator mFragmentCreator;
    private final ArrayMap<View, FragmentHostState> mHosts = new ArrayMap<>();
    private final ArrayMap<String, Method> mInjectionMap = new ArrayMap<>();
    private final Handler mHandler = new Handler();
    private ConfigurationController.ConfigurationListener mConfigurationListener = new ConfigurationController.ConfigurationListener() { // from class: com.android.systemui.fragments.FragmentService.1
        @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
        public void onConfigChanged(Configuration newConfig) {
            for (FragmentHostState state : FragmentService.this.mHosts.values()) {
                state.sendConfigurationChange(newConfig);
            }
        }
    };

    @Subcomponent
    /* loaded from: classes21.dex */
    public interface FragmentCreator {
        NavigationBarFragment createNavigationBarFragment();

        QSFragment createQSFragment();
    }

    @Inject
    public FragmentService(SystemUIRootComponent rootComponent, ConfigurationController configurationController) {
        this.mFragmentCreator = rootComponent.createFragmentCreator();
        initInjectionMap();
        configurationController.addCallback(this.mConfigurationListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayMap<String, Method> getInjectionMap() {
        return this.mInjectionMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FragmentCreator getFragmentCreator() {
        return this.mFragmentCreator;
    }

    private void initInjectionMap() {
        Method[] declaredMethods;
        for (Method method : FragmentCreator.class.getDeclaredMethods()) {
            if (Fragment.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 1) != 0) {
                this.mInjectionMap.put(method.getReturnType().getName(), method);
            }
        }
    }

    public FragmentHostManager getFragmentHostManager(View view) {
        View root = view.getRootView();
        FragmentHostState state = this.mHosts.get(root);
        if (state == null) {
            state = new FragmentHostState(root);
            this.mHosts.put(root, state);
        }
        return state.getFragmentHostManager();
    }

    public void removeAndDestroy(View view) {
        FragmentHostState state = this.mHosts.remove(view.getRootView());
        if (state == null) {
            return;
        }
        state.mFragmentHostManager.destroy();
    }

    public void destroyAll() {
        for (FragmentHostState state : this.mHosts.values()) {
            state.mFragmentHostManager.destroy();
        }
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Dumping fragments:");
        for (FragmentHostState state : this.mHosts.values()) {
            state.mFragmentHostManager.getFragmentManager().dump("  ", fd, pw, args);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class FragmentHostState {
        private FragmentHostManager mFragmentHostManager;
        private final View mView;

        public FragmentHostState(View view) {
            this.mView = view;
            this.mFragmentHostManager = new FragmentHostManager(FragmentService.this, this.mView);
        }

        public void sendConfigurationChange(final Configuration newConfig) {
            FragmentService.this.mHandler.post(new Runnable() { // from class: com.android.systemui.fragments.-$$Lambda$FragmentService$FragmentHostState$kEJEvu5Mq9Z5e9srOLcsFn7Glto
                @Override // java.lang.Runnable
                public final void run() {
                    FragmentService.FragmentHostState.this.lambda$sendConfigurationChange$0$FragmentService$FragmentHostState(newConfig);
                }
            });
        }

        public FragmentHostManager getFragmentHostManager() {
            return this.mFragmentHostManager;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: handleSendConfigurationChange */
        public void lambda$sendConfigurationChange$0$FragmentService$FragmentHostState(Configuration newConfig) {
            this.mFragmentHostManager.onConfigurationChanged(newConfig);
        }
    }
}
