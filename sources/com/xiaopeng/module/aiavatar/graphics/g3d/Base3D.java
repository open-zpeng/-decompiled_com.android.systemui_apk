package com.xiaopeng.module.aiavatar.graphics.g3d;

import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
/* loaded from: classes23.dex */
public abstract class Base3D extends AbstractApplication {
    private static final String TAG = "Base3DSence";
    protected Environment environment;
    public AssetManager mAssets;
    public Environment mEnv;
    public CameraInputController mInputController;
    public Array<ModelInstance> mInstances = new Array<>();
    private float mCurrentLoadPercent = 0.0f;
    private float mPreLoadPercent = 0.0f;
    protected boolean mLoading = true;

    protected abstract void actionLoad();

    protected abstract void onLoaded();

    protected abstract void render(ModelBatch modelBatch, Array<ModelInstance> array);

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void create() {
        super.create();
        if (this.mAssets == null) {
            this.mAssets = new AssetManager();
        }
        initCamera();
        this.mEnv = new Environment();
        this.mInputController = new CameraInputController(this.mCam);
    }

    private void initCamera() {
        this.mCam = new PerspectiveCamera(20.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.mCam.position.set(0.0f, 6.0f, 86.0f);
        this.mCam.lookAt(0.0f, 8.0f, 0.0f);
        this.mCam.near = 10.0f;
        this.mCam.far = 100.0f;
        this.mCam.update();
    }

    protected void onLoadPercentChange(float currentPercent) {
        Log.i("cycle", "资源加载进度发生变化......" + this.mAssets.getProgress());
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void render() {
        super.render();
        this.mCurrentLoadPercent = this.mAssets.getProgress();
        float f = this.mCurrentLoadPercent;
        if (f != this.mPreLoadPercent) {
            onLoadPercentChange(f);
        }
        this.mPreLoadPercent = this.mCurrentLoadPercent;
        if (this.mLoading && this.mAssets.update()) {
            this.mLoading = false;
            onLoaded();
        }
        actionLoad();
        this.mInputController.update();
        render(this.mInstances);
    }

    public void render(Array<ModelInstance> instances) {
        this.mModelBatch.begin(this.mCam);
        if (instances != null) {
            render(this.mModelBatch, instances);
        }
        this.mModelBatch.end();
        getStatus(this.stringBuilder);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void dispose() {
        super.dispose();
        this.mModelBatch.dispose();
        this.mAssets.dispose();
        this.mAssets = null;
        this.mAxesModel.dispose();
        this.mAxesModel = null;
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void resume() {
        super.resume();
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void pause() {
        super.pause();
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean touchDown(float var1, float var2, int var3, int var4) {
        return super.touchDown(var1, var2, var3, var4);
    }
}
