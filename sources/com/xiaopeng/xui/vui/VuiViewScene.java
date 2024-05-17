package com.xiaopeng.xui.vui;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiUpdateType;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.util.List;
/* loaded from: classes25.dex */
public abstract class VuiViewScene implements IVuiViewScene, IVuiSceneListener {
    private View mRootView;
    private IVuiSceneListener mVuiSceneListener;
    private IVuiEngine mVuiEngine = null;
    private String mSceneId = "";
    private IVuiElementListener mVuiElementListener = null;
    private List<Integer> customViewIds = null;
    private Handler mHandler = new Handler();
    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.xui.vui.VuiViewScene.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.createVuiScene();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.destroyVuiScene();
            }
        }
    };
    IVuiElementChangedListener mListener = new IVuiElementChangedListener() { // from class: com.xiaopeng.xui.vui.-$$Lambda$VuiViewScene$opFwW47al5q72QJCRazwTlW1jvk
        @Override // com.xiaopeng.vui.commons.IVuiElementChangedListener
        public final void onVuiElementChaned(View view, VuiUpdateType vuiUpdateType) {
            VuiViewScene.this.lambda$new$1$VuiViewScene(view, vuiUpdateType);
        }
    };
    private boolean isUseNewProtocol = false;

    protected abstract void onBuildScenePrepare();

    /* JADX INFO: Access modifiers changed from: protected */
    public void setVuiView(@NonNull View view) {
        log("initVui");
        if (!Xui.isVuiEnable()) {
            return;
        }
        View view2 = this.mRootView;
        if (view2 != null) {
            view2.removeOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        }
        this.mRootView = view;
        view.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiEvent(View view, VuiEvent vuiEvent) {
        log("VuiViewScene onVuiEvent");
        if (view == null) {
            return;
        }
        IVuiElementListener vuiElementListener = this.mVuiElementListener;
        if (vuiElementListener != null) {
            vuiElementListener.onVuiElementEvent(view, vuiEvent);
        } else {
            VuiFloatingLayerManager.show(view);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public boolean onInterceptVuiEvent(View view, VuiEvent vuiEvent) {
        log("onInterceptVuiEvent");
        if (view == null) {
            return false;
        }
        IVuiElementListener vuiElementListener = this.mVuiElementListener;
        if (vuiElementListener != null) {
            return vuiElementListener.onVuiElementEvent(view, vuiEvent);
        }
        VuiFloatingLayerManager.show(view);
        return false;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onBuildScene() {
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.xui.vui.-$$Lambda$VuiViewScene$bhRg6IeHfpBu4ffKtyCCDOAoC1Q
            @Override // java.lang.Runnable
            public final void run() {
                VuiViewScene.this.lambda$onBuildScene$0$VuiViewScene();
            }
        });
    }

    public /* synthetic */ void lambda$onBuildScene$0$VuiViewScene() {
        IVuiEngine vuiEngine = this.mVuiEngine;
        String sceneId = this.mSceneId;
        View rootView = this.mRootView;
        if (vuiEngine != null && sceneId != null && rootView != null) {
            int displayLocation = getVuiDisplayLocation();
            log("onBuildScene:" + displayLocation);
            onBuildScenePrepare();
            vuiEngine.buildScene(sceneId, rootView, this.customViewIds, this.mVuiElementListener, String.valueOf(displayLocation));
        }
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String sceneId) {
        this.mSceneId = sceneId;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(IVuiEngine vuiEngine) {
        this.mVuiEngine = vuiEngine;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(IVuiElementListener listener) {
        this.mVuiElementListener = listener;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.customViewIds = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createVuiScene() {
        IVuiEngine vuiEngine = this.mVuiEngine;
        String sceneId = this.mSceneId;
        View rootView = this.mRootView;
        if (vuiEngine != null && sceneId != null && rootView != null) {
            log("createVuiScene");
            try {
                if (this.isUseNewProtocol) {
                    vuiEngine.addVuiSceneListener(sceneId, rootView, this, this.mListener);
                } else {
                    vuiEngine.addVuiSceneListener(sceneId, rootView, this);
                }
            } catch (AbstractMethodError e) {
                vuiEngine.addVuiSceneListener(sceneId, rootView, this);
            }
            vuiEngine.enterScene(sceneId, String.valueOf(getVuiDisplayLocation()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroyVuiScene() {
        if (this.mVuiEngine != null && this.mSceneId != null) {
            log("destroyVuiScene");
            this.mVuiEngine.exitScene(this.mSceneId, String.valueOf(getVuiDisplayLocation()));
            this.mVuiEngine.removeVuiSceneListener(this.mSceneId);
            this.mVuiEngine = null;
        }
        if (this.mVuiElementListener != null) {
            this.mVuiElementListener = null;
        }
    }

    private void log(String msg) {
        XLogUtils.d("VuiViewScene", " mSceneId " + this.mSceneId + "  " + msg + " hashcode " + hashCode() + " name " + getClass().getSimpleName());
    }

    public /* synthetic */ void lambda$new$1$VuiViewScene(View view, VuiUpdateType type) {
        IVuiEngine vuiEngine = this.mVuiEngine;
        String sceneId = this.mSceneId;
        if (vuiEngine != null && !TextUtils.isEmpty(sceneId) && view != null) {
            if (VuiUpdateType.UPDATE_VIEW.equals(type)) {
                vuiEngine.updateScene(sceneId, view);
            } else {
                vuiEngine.updateElementAttribute(sceneId, view);
            }
        }
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine) {
        this.mSceneId = sceneId;
        this.mVuiEngine = vuiEngine;
        this.isUseNewProtocol = true;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String sceneId, IVuiEngine vuiEngine, IVuiSceneListener listener) {
        this.mSceneId = sceneId;
        this.mVuiEngine = vuiEngine;
        this.isUseNewProtocol = true;
        this.mVuiSceneListener = listener;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiStateChanged() {
        IVuiSceneListener iVuiSceneListener = this.mVuiSceneListener;
        if (iVuiSceneListener != null) {
            iVuiSceneListener.onVuiStateChanged();
        }
    }
}
