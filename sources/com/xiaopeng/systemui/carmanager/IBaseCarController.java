package com.xiaopeng.systemui.carmanager;

import com.xiaopeng.systemui.carmanager.IBaseCallback;
/* loaded from: classes24.dex */
public interface IBaseCarController<T extends IBaseCallback> {
    void registerCallback(T t);

    void unregisterCallback(T t);
}
