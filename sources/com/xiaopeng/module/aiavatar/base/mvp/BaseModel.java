package com.xiaopeng.module.aiavatar.base.mvp;

import com.xiaopeng.module.aiavatar.base.mvp.BasePresenter;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
/* loaded from: classes23.dex */
public abstract class BaseModel<P extends BasePresenter> {
    protected P presenter;

    public abstract void receiveCmd(AvatarBean avatarBean);

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }
}
