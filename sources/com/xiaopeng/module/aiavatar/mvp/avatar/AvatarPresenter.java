package com.xiaopeng.module.aiavatar.mvp.avatar;

import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
/* loaded from: classes23.dex */
public class AvatarPresenter extends AvatarContract.Presenter {
    public static final String TAG = "AvatarPresenter";

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.Presenter
    public void processG3dbModelCmd(AvatarBean bean) {
        if (bean != null) {
            if (!((AvatarContract.View) this.view).isLoaded()) {
                Log.d(TAG, "Avatar has not loaded");
            } else if ("com.xiaopeng.aiavatarservice.stop".equals(bean.packageName)) {
                ((AvatarContract.View) this.view).stopRender();
            } else if ("com.xiaopeng.aiavatarservice.working".equals(bean.packageName)) {
                ((AvatarContract.View) this.view).showWorkingState();
            } else if ("com.xiaopeng.aiavatarservice.skin".equals(bean.packageName)) {
                ((AvatarContract.View) this.view).updateSkin(bean.skin);
            } else {
                ((AvatarContract.View) this.view).updateCurrentAvatar(bean);
                ((AvatarContract.View) this.view).updateFullBodyStatus(bean.packageName, bean.eventId);
                if (!TextUtils.isEmpty(bean.g3dbModelPath)) {
                    ((AvatarContract.View) this.view).updateG3dbModel(bean.g3dbModelPath);
                }
                if (bean.actionList != null && bean.actionList.size() > 0) {
                    ((AvatarContract.View) this.view).updateActionId(bean.actionList);
                }
                if (!TextUtils.isEmpty(bean.modelTexturePath)) {
                    ((AvatarContract.View) this.view).updateModelTexture(bean.modelTexturePath);
                }
                AvatarBean.GlassesTexture glassesTexture = bean.glassesTextureBean;
                if (glassesTexture != null && !TextUtils.isEmpty(glassesTexture.path)) {
                    ((AvatarContract.View) this.view).updateGlassesTexture(glassesTexture.path, glassesTexture.loopCount, bean.isSpread);
                }
                ((AvatarContract.View) this.view).updateLeftTop(bean.leftTop);
                ((AvatarContract.View) this.view).updateLeft(bean.left);
                ((AvatarContract.View) this.view).updateRight(bean.right);
                ((AvatarContract.View) this.view).updateWarnLevel(bean.warnLevel);
                if (!TextUtils.isEmpty(bean.envBgTexturePath)) {
                    ((AvatarContract.View) this.view).updateEnvBgTexture(bean.envBgTexturePath);
                }
                if (bean.lightColor != null) {
                    ((AvatarContract.View) this.view).updateLightColor(bean.lightColor);
                }
                ((AvatarContract.View) this.view).updateMovingX(bean.xPositon);
                ((AvatarContract.View) this.view).updateZoom(bean.isZoom);
                ((AvatarContract.View) this.view).startRender();
            }
        }
    }
}
