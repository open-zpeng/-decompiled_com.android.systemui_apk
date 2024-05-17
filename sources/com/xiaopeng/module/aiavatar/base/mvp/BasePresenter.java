package com.xiaopeng.module.aiavatar.base.mvp;

import com.xiaopeng.module.aiavatar.base.mvp.BaseModel;
import com.xiaopeng.module.aiavatar.base.mvp.BaseView;
/* loaded from: classes23.dex */
public abstract class BasePresenter<V extends BaseView, M extends BaseModel> {
    protected M model;
    protected V view;

    public void setVM(V v, M m) {
        this.view = v;
        this.model = m;
    }
}
