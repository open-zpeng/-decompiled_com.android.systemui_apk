package com.xiaopeng.module.aiavatar.mvp.avatar;

import com.xiaopeng.module.aiavatar.base.mvp.BaseModel;
import com.xiaopeng.module.aiavatar.base.mvp.BasePresenter;
import com.xiaopeng.module.aiavatar.base.mvp.BaseView;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import java.util.List;
/* loaded from: classes23.dex */
public interface AvatarContract {

    /* loaded from: classes23.dex */
    public static abstract class Model extends BaseModel {
    }

    /* loaded from: classes23.dex */
    public static abstract class Presenter extends BasePresenter<View, AvatarModel> {
        public abstract void processG3dbModelCmd(AvatarBean avatarBean);
    }

    /* loaded from: classes23.dex */
    public interface View extends BaseView {
        boolean isLoaded();

        void showWorkingState();

        void startRender();

        void stopRender();

        void updateActionId(List<AvatarBean.AvatarAction> list);

        void updateCurrentAvatar(AvatarBean avatarBean);

        void updateEnvBgTexture(String str);

        void updateFullBodyStatus(String str, int i);

        void updateG3dbModel(String str);

        void updateGlassesTexture(String str, int i, boolean z);

        void updateLeft(String str);

        void updateLeftTop(String str);

        void updateLightColor(AvatarBean.LightColor lightColor);

        void updateModelTexture(String str);

        void updateMovingX(int i);

        void updateRight(String str);

        void updateSkin(AvatarBean.Skin skin);

        void updateWarnLevel(int i);

        void updateZoom(boolean z);
    }
}
