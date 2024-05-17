package com.xiaopeng.module.aiavatar.mvp.avatar;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidViewApplication;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.module.aiavatar.R;
import com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView;
import com.xiaopeng.module.aiavatar.event.AvatarEvents;
import com.xiaopeng.module.aiavatar.event.FullBodyEventController;
import com.xiaopeng.module.aiavatar.helper.AvatarSceneHelper;
import com.xiaopeng.module.aiavatar.helper.TextToSpeechHelper;
import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import com.xiaopeng.module.aiavatar.player.Avatar3dPlayer;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.List;
/* loaded from: classes23.dex */
public class AvatarRootView extends MvpBaseView<AvatarModel, AvatarContract.View, AvatarPresenter> implements AvatarContract.View {
    private static final String TAG = "AvatarRootView";
    private AndroidViewApplication androidViewApplication;
    private AvatarViewContainer bodyControlCallback;
    private AvatarBean currentAvatar;
    private GLSurfaceView glView;
    private boolean isRequestRender;
    private AvatarTouchView mAvatarTouchView;
    private Runnable mDrawRunnable;
    private ViewGroup.LayoutParams mGLViewLayoutParams;
    private boolean mIsExitFromFull;
    private boolean mIsFullBody;
    private SpeechTextView mSpeechTextView;
    private Avatar3dPlayer player;
    private AvatarPresenter presenter;

    /* loaded from: classes23.dex */
    public interface AvatarViewContainer {
        void enterFullBodyMode();

        void exitFullBodyMode();
    }

    public AvatarRootView(@NonNull Context context) {
        super(context);
        this.mGLViewLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        this.isRequestRender = true;
        this.mDrawRunnable = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView.3
            @Override // java.lang.Runnable
            public void run() {
                if (AvatarRootView.this.isRequestRender) {
                    AvatarRootView.this.refresh();
                    AvatarRootView.this.postDelayed(this, 41L);
                }
            }
        };
    }

    public AvatarRootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mGLViewLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        this.isRequestRender = true;
        this.mDrawRunnable = new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView.3
            @Override // java.lang.Runnable
            public void run() {
                if (AvatarRootView.this.isRequestRender) {
                    AvatarRootView.this.refresh();
                    AvatarRootView.this.postDelayed(this, 41L);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView
    public AvatarModel createModel() {
        return new AvatarModel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView
    public AvatarContract.View createView() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView
    public AvatarPresenter createPresenter() {
        this.presenter = new AvatarPresenter();
        return this.presenter;
    }

    @Override // com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView
    protected void initView() {
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        try {
            Log.e(TAG, "onFinishInflate");
            this.androidViewApplication = new AvatarViewApplication(getContext());
            this.player = new Avatar3dPlayer(getContext(), this);
            AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
            cfg.a = 8;
            cfg.b = 8;
            cfg.g = 8;
            cfg.r = 8;
            cfg.numSamples = 4;
            this.glView = (GLSurfaceView) this.androidViewApplication.initializeForView(this.player, cfg);
            this.glView.getHolder().setFormat(-3);
            this.glView.setZOrderMediaOverlay(true);
            this.player.setGlSurfaceView(this.glView);
            addView(this.glView, new ViewGroup.LayoutParams(-1, -1));
            this.mAvatarTouchView = new AvatarTouchView(getContext());
            this.mAvatarTouchView.setClickable(true);
            addView(this.mAvatarTouchView, this.mGLViewLayoutParams);
            LayoutInflater.from(getContext()).inflate(R.layout.layout_avatar_speech, this);
            this.mSpeechTextView = (SpeechTextView) findViewById(R.id.tv_speech);
            this.mAvatarTouchView.setSpeakCallback(this.mSpeechTextView);
            this.mAvatarTouchView.setRootView(this);
            FullBodyEventController.instance().setAvatarRootView(this);
            FullBodyEventController.instance().setSpeechTextView(this.mSpeechTextView);
            Gdx.graphics.setContinuousRendering(false);
            startRender();
            AvatarSceneHelper.instance().init(this);
        } catch (Exception e) {
            Log.e(TAG, "onFinishInflate", e);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateG3dbModel(String g3dbModelPath) {
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateZoom(boolean isZoom) {
        this.player.updateZoom(false);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateModelTexture(String modelTexturePath) {
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateGlassesTexture(String glassesTexturePath, int loopCount, boolean isSpread) {
        this.player.updateGlassesTexture(glassesTexturePath, loopCount, isSpread);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateEnvBgTexture(String envBgTexturePath) {
        this.player.updateEnvBgTexture(envBgTexturePath);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateActionId(List<AvatarBean.AvatarAction> actionList) {
        this.player.updateActionId(actionList);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateFullBodyStatus(final String packageName, final int eventId) {
        post(new Runnable() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView.1
            @Override // java.lang.Runnable
            public void run() {
                if (!"com.xiaopeng.aiassistant".equals(packageName) || eventId != 7) {
                    if (AvatarRootView.this.mIsFullBody) {
                        AvatarRootView.this.mIsFullBody = false;
                        AvatarRootView.this.mIsExitFromFull = true;
                        if (AvatarRootView.this.bodyControlCallback != null) {
                            AvatarRootView.this.bodyControlCallback.exitFullBodyMode();
                        }
                        AvatarRootView.this.exitFullBody();
                        return;
                    }
                    return;
                }
                if (!AvatarRootView.this.mIsFullBody) {
                    AvatarRootView.this.mIsFullBody = true;
                    AvatarRootView.this.enterFullBody();
                }
                if (AvatarRootView.this.bodyControlCallback != null) {
                    AvatarRootView.this.bodyControlCallback.enterFullBodyMode();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exitFullBody() {
        FullBodyEventController.instance().exitFullBody(this.currentAvatar);
        Animation animation = new Animation() { // from class: com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView.2
            @Override // android.view.animation.Animation
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (!AvatarRootView.this.isDialogStatus()) {
                    AvatarRootView.this.player.setTranslateX(((1.0f - interpolatedTime) * 0.5f) - 0.5f);
                    AvatarRootView.this.player.setTranslateY(((1.0f - interpolatedTime) * (-8.2f)) + 3.0f);
                    AvatarRootView.this.player.setTranslateZ(((1.0f - interpolatedTime) * 2.4f) + 0.0f);
                    AvatarRootView.this.player.setScale(((1.0f - interpolatedTime) * 0.03f) + 0.117f);
                    AvatarRootView.this.player.setRenderY(((1.0f - interpolatedTime) * (-19.0f)) + 13.0f);
                }
            }
        };
        animation.setDuration(500L);
        startAnimation(animation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDialogStatus() {
        int windowStatus = this.player.getWindowStatus();
        if (windowStatus == 1) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enterFullBody() {
        clearAnimation();
        FullBodyEventController.instance().enterFullBody(this.currentAvatar);
        if (!isDialogStatus()) {
            this.player.setTranslateX(0.0f);
            this.player.setTranslateY(-5.2f);
            this.player.setTranslateZ(2.4f);
            this.player.setScale(0.147f);
            this.player.setRenderY(-6.0f);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateCurrentAvatar(AvatarBean avatarBean) {
        this.currentAvatar = avatarBean;
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateLightColor(AvatarBean.LightColor lightColor) {
        this.player.updateLightColor(lightColor);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateMovingX(int xPosition) {
        this.player.updateMovingX(xPosition);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateLeftTop(String leftTop) {
        this.player.updateLeftTop(leftTop);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateLeft(String left) {
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateRight(String right) {
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateWarnLevel(int level) {
        this.player.updateWarnLevel(level);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void showWorkingState() {
        this.player.showWorkingState();
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void updateSkin(AvatarBean.Skin skin) {
        if (isFullBody()) {
            AvatarEvents.Default1Event avatarEvent = new AvatarEvents.Default1Event(this.currentAvatar);
            FullBodyEventController.instance().pushEvent(avatarEvent);
            String tts = avatarEvent.getTts();
            if (!TextUtils.isEmpty(tts)) {
                TextToSpeechHelper.instance().speak(getContext(), tts, null);
                this.mSpeechTextView.setText(tts);
                this.mSpeechTextView.show();
            }
        }
    }

    public void onAvatarClick() {
        String pkg;
        int eventId;
        AvatarBean avatarBean = this.currentAvatar;
        if (avatarBean != null) {
            pkg = avatarBean.packageName;
            eventId = this.currentAvatar.eventId;
        } else {
            pkg = "com.xiaopeng.aiavatarservice";
            eventId = 0;
        }
        Uri.Builder builder = new Uri.Builder();
        builder.authority("com.xiaopeng.aiassistant.AiassistantService").path("onAvatarClick").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, pkg).appendQueryParameter("eventId", String.valueOf(eventId));
        try {
            ApiRouter.route(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAvatarViewContainer(AvatarViewContainer bodyControlCallback) {
        this.bodyControlCallback = bodyControlCallback;
    }

    public boolean isFullBody() {
        return this.mIsFullBody;
    }

    public boolean isExitFromFull() {
        return this.mIsExitFromFull;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refresh();
    }

    @Override // com.xiaopeng.module.aiavatar.base.mvp.MvpBaseView
    public void postGLTask(Runnable entryRemovedTask) {
        AndroidViewApplication androidViewApplication = this.androidViewApplication;
        if (androidViewApplication != null && entryRemovedTask != null) {
            androidViewApplication.postRunnable(entryRemovedTask);
        }
    }

    public void replaceAvatarContainer(ViewGroup parentView, int w, int h, float x, float y, float scale) {
        if (parentView != null) {
            ViewParent parent = this.glView.getParent();
            if (parent != null) {
                ViewGroup viewGroup = (ViewGroup) parent;
                if (viewGroup == parentView) {
                    return;
                }
                viewGroup.removeView(this.glView);
            }
            int height = w * 2;
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(w, height);
            Log.i(TAG, "replaceAvatarContainer w : " + w + " h : " + h);
            parentView.addView(this.glView, params);
            this.glView.setZOrderOnTop(true);
            this.player.setWindowStatus(1);
            this.player.setTranslateX((-0.5f) + x);
            this.player.setTranslateY(3.0f + y);
            this.player.setScale(0.117f + scale);
        }
    }

    public void resetAvatarContainer() {
        ViewParent parent = this.glView.getParent();
        if (parent != null) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup == this) {
                return;
            }
            viewGroup.removeView(this.glView);
        }
        addView(this.glView, 0, this.mGLViewLayoutParams);
        this.glView.setZOrderOnTop(false);
        this.player.setTranslateX(-0.5f);
        this.player.setTranslateY(3.0f);
        this.player.setScale(0.117f);
        this.player.setWindowStatus(0);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void stopRender() {
        Log.d(TAG, "stopRender isFullBody:" + this.mIsFullBody);
        if (!this.mIsFullBody) {
            setIsRequestRender(false);
        }
    }

    public void setIsRequestRender(boolean requestRender) {
        this.isRequestRender = requestRender;
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public void startRender() {
        Log.d(TAG, "startRender");
        setIsRequestRender(true);
        removeCallbacks(this.mDrawRunnable);
        post(this.mDrawRunnable);
    }

    @Override // com.xiaopeng.module.aiavatar.mvp.avatar.AvatarContract.View
    public boolean isLoaded() {
        Avatar3dPlayer avatar3dPlayer = this.player;
        if (avatar3dPlayer != null && avatar3dPlayer.isloaded()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        Avatar3dPlayer avatar3dPlayer = this.player;
        if (avatar3dPlayer != null) {
            avatar3dPlayer.refresh();
        }
    }
}
