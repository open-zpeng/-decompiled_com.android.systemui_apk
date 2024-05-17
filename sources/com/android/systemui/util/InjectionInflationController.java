package com.android.systemui.util;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardSliceView;
import com.android.systemui.SystemUIRootComponent;
import com.android.systemui.qs.QSCarrierGroup;
import com.android.systemui.qs.QSFooterImpl;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.qs.QuickStatusBarHeader;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import dagger.Module;
import dagger.Provides;
import dagger.Subcomponent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class InjectionInflationController {
    public static final String VIEW_CONTEXT = "view_context";
    private final ViewCreator mViewCreator;
    private final ArrayMap<String, Method> mInjectionMap = new ArrayMap<>();
    private final LayoutInflater.Factory2 mFactory = new InjectionFactory();

    @Subcomponent
    /* loaded from: classes21.dex */
    public interface ViewCreator {
        ViewInstanceCreator createInstanceCreator(ViewAttributeProvider viewAttributeProvider);
    }

    @Subcomponent(modules = {ViewAttributeProvider.class})
    /* loaded from: classes21.dex */
    public interface ViewInstanceCreator {
        NotificationShelf creatNotificationShelf();

        KeyguardClockSwitch createKeyguardClockSwitch();

        KeyguardMessageArea createKeyguardMessageArea();

        KeyguardSliceView createKeyguardSliceView();

        LockIcon createLockIcon();

        NotificationStackScrollLayout createNotificationStackScrollLayout();

        NotificationPanelView createPanelView();

        QSCarrierGroup createQSCarrierGroup();

        QSCustomizer createQSCustomizer();

        QSPanel createQSPanel();

        QSFooterImpl createQsFooter();

        QuickStatusBarHeader createQsHeader();

        QuickQSPanel createQuickQSPanel();
    }

    @Inject
    public InjectionInflationController(SystemUIRootComponent rootComponent) {
        this.mViewCreator = rootComponent.createViewCreator();
        initInjectionMap();
    }

    ArrayMap<String, Method> getInjectionMap() {
        return this.mInjectionMap;
    }

    ViewCreator getFragmentCreator() {
        return this.mViewCreator;
    }

    public LayoutInflater injectable(LayoutInflater inflater) {
        LayoutInflater ret = inflater.cloneInContext(inflater.getContext());
        ret.setPrivateFactory(this.mFactory);
        return ret;
    }

    private void initInjectionMap() {
        Method[] declaredMethods;
        for (Method method : ViewInstanceCreator.class.getDeclaredMethods()) {
            if (View.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 1) != 0) {
                this.mInjectionMap.put(method.getReturnType().getName(), method);
            }
        }
    }

    @Module
    /* loaded from: classes21.dex */
    public class ViewAttributeProvider {
        private final AttributeSet mAttrs;
        private final Context mContext;

        private ViewAttributeProvider(Context context, AttributeSet attrs) {
            this.mContext = context;
            this.mAttrs = attrs;
        }

        @Provides
        @Named(InjectionInflationController.VIEW_CONTEXT)
        public Context provideContext() {
            return this.mContext;
        }

        @Provides
        public AttributeSet provideAttributeSet() {
            return this.mAttrs;
        }
    }

    /* loaded from: classes21.dex */
    private class InjectionFactory implements LayoutInflater.Factory2 {
        private InjectionFactory() {
        }

        @Override // android.view.LayoutInflater.Factory
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            Method creationMethod = (Method) InjectionInflationController.this.mInjectionMap.get(name);
            if (creationMethod == null) {
                return null;
            }
            ViewAttributeProvider provider = new ViewAttributeProvider(context, attrs);
            try {
                return (View) creationMethod.invoke(InjectionInflationController.this.mViewCreator.createInstanceCreator(provider), new Object[0]);
            } catch (IllegalAccessException e) {
                throw new InflateException("Could not inflate " + name, e);
            } catch (InvocationTargetException e2) {
                throw new InflateException("Could not inflate " + name, e2);
            }
        }

        @Override // android.view.LayoutInflater.Factory2
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return onCreateView(name, context, attrs);
        }
    }
}
