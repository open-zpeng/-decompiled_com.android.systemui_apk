package com.xiaopeng.systemui.infoflow.message.event;

import com.xiaopeng.systemui.infoflow.common.event.EventPackage;
/* loaded from: classes24.dex */
public class CarCheckEventPackage extends EventPackage {
    public boolean exit;

    public CarCheckEventPackage(Object event, Object sender, boolean exit) {
        super(event, sender);
        this.exit = false;
        this.exit = exit;
    }
}
