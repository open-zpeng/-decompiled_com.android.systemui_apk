package com.xiaopeng.module.aiavatar.helper;

import android.util.Log;
import android.view.ViewGroup;
import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView;
/* loaded from: classes23.dex */
public class AvatarSceneHelper {
    private static final String TAG = "AvatarSceneHelper";
    private static final AvatarSceneHelper sInstance = new AvatarSceneHelper();
    private AvatarRootView mAvatarRootView;

    public static final AvatarSceneHelper instance() {
        return sInstance;
    }

    public void init(AvatarRootView avatarRootView) {
        this.mAvatarRootView = avatarRootView;
    }

    public void replaceScene(ViewGroup sceneContainerView) {
        Log.d(TAG, "replaceScene");
        AvatarRootView avatarRootView = this.mAvatarRootView;
        if (avatarRootView != null) {
            avatarRootView.replaceAvatarContainer(sceneContainerView, 100, 100, 0.0f, -22.0f, 0.2f);
            return;
        }
        throw new RuntimeException("AvatarSceneHelper has not init!");
    }

    public void replaceScene(ViewGroup sceneContainerView, int w, int h) {
        Log.d(TAG, "replaceScene");
        AvatarRootView avatarRootView = this.mAvatarRootView;
        if (avatarRootView != null) {
            avatarRootView.replaceAvatarContainer(sceneContainerView, w, h, 0.0f, -22.0f, 0.2f);
            return;
        }
        throw new RuntimeException("AvatarSceneHelper has not init!");
    }

    public void restoreScene() {
        Log.d(TAG, "restoreScene");
        AvatarRootView avatarRootView = this.mAvatarRootView;
        if (avatarRootView != null) {
            avatarRootView.resetAvatarContainer();
            return;
        }
        throw new RuntimeException("AvatarSceneHelper has not init!");
    }
}
