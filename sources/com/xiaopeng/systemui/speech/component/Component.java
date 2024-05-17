package com.xiaopeng.systemui.speech.component;

import android.content.Context;
import com.xiaopeng.systemui.speech.component.IComponentListener;
/* loaded from: classes24.dex */
public abstract class Component<T extends IComponentListener> {
    private final T mComponentListener;
    protected Context mContext;

    public abstract void start();

    public Component(Context context, T t) {
        this.mContext = context;
        this.mComponentListener = t;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T getComponentListener() {
        return this.mComponentListener;
    }
}
