package com.xiaopeng.module.aiavatar.mvp.avatar;

import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
/* loaded from: classes23.dex */
public class AvatarModel extends AvatarContract.Model {
    @Override // com.xiaopeng.module.aiavatar.base.mvp.BaseModel
    public void receiveCmd(AvatarBean bean) {
        ((AvatarPresenter) this.presenter).processG3dbModelCmd(bean);
    }
}
