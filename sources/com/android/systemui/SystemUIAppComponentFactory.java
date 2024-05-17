package com.android.systemui;

import android.app.Application;
import android.content.Context;
import androidx.core.app.CoreComponentFactory;
import com.android.systemui.SystemUIApplication;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class SystemUIAppComponentFactory extends CoreComponentFactory {
    @Inject
    public ContextComponentHelper mComponentHelper;

    @Override // androidx.core.app.CoreComponentFactory, android.app.AppComponentFactory
    public Application instantiateApplication(ClassLoader cl, String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Application app = super.instantiateApplication(cl, className);
        if (app instanceof SystemUIApplication) {
            ((SystemUIApplication) app).setContextAvailableCallback(new SystemUIApplication.ContextAvailableCallback() { // from class: com.android.systemui.-$$Lambda$SystemUIAppComponentFactory$LTMvIPTiTOOtdqpeHYTYFPUw6Js
                @Override // com.android.systemui.SystemUIApplication.ContextAvailableCallback
                public final void onContextAvailable(Context context) {
                    SystemUIAppComponentFactory.this.lambda$instantiateApplication$0$SystemUIAppComponentFactory(context);
                }
            });
        }
        return app;
    }

    public /* synthetic */ void lambda$instantiateApplication$0$SystemUIAppComponentFactory(Context context) {
        SystemUIFactory.createFromConfig(context);
        SystemUIFactory.getInstance().getRootComponent().inject(this);
    }
}
