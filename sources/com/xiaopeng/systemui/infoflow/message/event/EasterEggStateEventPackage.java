package com.xiaopeng.systemui.infoflow.message.event;

import com.xiaopeng.systemui.infoflow.common.event.EventPackage;
/* loaded from: classes24.dex */
public class EasterEggStateEventPackage extends EventPackage {
    public boolean show;

    public EasterEggStateEventPackage(Object event, Object sender, boolean show) {
        super(event, sender);
        this.show = false;
        this.show = show;
    }
}
