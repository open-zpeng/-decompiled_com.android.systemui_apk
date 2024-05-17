package com.xiaopeng.speech.vui.filter;

import com.xiaopeng.vui.commons.model.VuiElement;
/* loaded from: classes.dex */
public class FaultManager {
    private static volatile FaultManager faultManager;
    private FaultFilterChain mFaultChain = new FaultFilterChain();

    private FaultManager() {
        init();
    }

    public static FaultManager getInstance() {
        if (faultManager == null) {
            synchronized (FaultManager.class) {
                if (faultManager == null) {
                    faultManager = new FaultManager();
                }
            }
        }
        return faultManager;
    }

    private void init() {
        this.mFaultChain.addFilter(new ListClickEventFaultFilter());
    }

    public VuiElement startFault(VuiElement vuiScene) {
        return this.mFaultChain.doFilter(vuiScene);
    }

    public void addFilter(IFilter filter) {
        if (filter != null) {
            this.mFaultChain.addFilter(filter);
        }
    }

    public void removeAllFilter() {
        this.mFaultChain.mFilters.clear();
    }

    public void removeFilter(IFilter filter) {
        this.mFaultChain.mFilters.remove(filter);
    }
}
