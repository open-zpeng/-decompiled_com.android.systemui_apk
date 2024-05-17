package com.xiaopeng.module.aiavatar.player;

import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import java.util.List;
/* loaded from: classes23.dex */
public interface IAvatarPlayer {
    void onActionEnd(String str);

    void showWorkingState();

    void updateActionId(List<AvatarBean.AvatarAction> list);

    void updateEnvBgTexture(String str);

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
