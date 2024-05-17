package com.xiaopeng.speech.vui.filter;

import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FaultFilterChain implements IFilter {
    public List<IFilter> mFilters = new ArrayList();

    public FaultFilterChain addFilter(IFilter filter) {
        this.mFilters.add(filter);
        return this;
    }

    @Override // com.xiaopeng.speech.vui.filter.IFilter
    public VuiElement doFilter(VuiElement vuiElement) {
        for (IFilter filter : this.mFilters) {
            vuiElement = filter.doFilter(vuiElement);
        }
        return vuiElement;
    }
}
