package com.xiaopeng.module.aiavatar.graphics.g3d;

import android.util.Log;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
/* loaded from: classes23.dex */
public abstract class AbstractApplication extends InputAdapter implements ApplicationListener, GestureDetector.GestureListener {
    private static final String TAG = "Base3DSence.Abs";
    protected Label drawCallsLabel;
    protected Label glCallsLabel;
    protected GLProfiler glProfiler;
    protected Label lightsLabel;
    public ModelInstance mAxesInstance;
    public Model mAxesModel;
    private SpriteBatch mBatch;
    public PerspectiveCamera mCam;
    private BitmapFont mFont;
    private Label mFps;
    public ModelBatch mModelBatch;
    protected SenceConfiguration mSenceConfiguration;
    protected Skin mSkin;
    protected Stage mStage;
    protected Label shaderSwitchesLabel;
    protected Label textureBindsLabel;
    protected Label vertexCountLabel;
    private FPSLogger mLogger = new FPSLogger();
    public final Color mBgColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    private Logger mLoger = new Logger(TAG, 2);
    long startTime = TimeUtils.nanoTime();
    int interval = 10;
    protected StringBuilder stringBuilder = new StringBuilder();
    final float GRID_MIN = -20.0f;
    final float GRID_MAX = 20.0f;
    final float GRID_STEP = 1.0f;

    public Stage getmStage() {
        return this.mStage;
    }

    public Skin getmSkin() {
        return this.mSkin;
    }

    public SenceConfiguration getmSenceConfiguration() {
        if (this.mSenceConfiguration == null) {
            this.mSenceConfiguration = new SenceConfiguration();
        }
        return this.mSenceConfiguration;
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void create() {
        this.mLoger.info("create");
        initModelShader();
        initAxes();
        initFPSConponent();
        initOpenGLDebugInfo();
    }

    private void initModelShader() {
        if (getmSenceConfiguration().ismURevers()) {
            String vertex = Gdx.files.internal("shader/vertex.glsl").readString();
            String fragment = Gdx.files.internal("shader/fragment.glsl").readString();
            this.mModelBatch = new ModelBatch(vertex, fragment);
            return;
        }
        this.mModelBatch = new ModelBatch();
    }

    private void initFPSConponent() {
        if (!getmSenceConfiguration().ismDebug()) {
            return;
        }
        this.mBatch = new SpriteBatch();
        this.mFont = new BitmapFont();
        this.glProfiler = new GLProfiler(Gdx.graphics);
        this.glProfiler.enable();
        this.mFps = new Label("FPS:999", new Label.LabelStyle(this.mFont, Color.GRAY));
        this.mFps.setPosition(0.0f, 20.0f);
        this.mSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
        this.mStage = new Stage();
    }

    private void initAxes() {
        if (!getmSenceConfiguration().ismDebug() || !getmSenceConfiguration().ismShowAxe()) {
            return;
        }
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("grid", 1, 3L, new Material());
        builder.setColor(Color.LIGHT_GRAY);
        for (float t = -20.0f; t <= 20.0f; t += 1.0f) {
            builder.line(t, 0.0f, -20.0f, t, 0.0f, 20.0f);
            builder.line(-20.0f, 0.0f, t, 20.0f, 0.0f, t);
        }
        MeshPartBuilder builder2 = modelBuilder.part("axes", 1, 3L, new Material());
        builder2.setColor(Color.RED);
        builder2.line(0.0f, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f);
        builder2.setColor(Color.GREEN);
        builder2.line(0.0f, 0.0f, 0.0f, 0.0f, 100.0f, 0.0f);
        builder2.setColor(Color.BLUE);
        builder2.line(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 100.0f);
        this.mAxesModel = modelBuilder.end();
        this.mAxesInstance = new ModelInstance(this.mAxesModel);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void getStatus(StringBuilder stringBuilder) {
        if (!getmSenceConfiguration().ismDebug() || !getmSenceConfiguration().ismShowOpengGL()) {
            return;
        }
        stringBuilder.setLength(0);
        stringBuilder.append("GL calls: ");
        stringBuilder.append(this.glProfiler.getCalls());
        this.glCallsLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append("Draw calls: ");
        stringBuilder.append(this.glProfiler.getDrawCalls());
        this.drawCallsLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append("Shader switches: ");
        stringBuilder.append(this.glProfiler.getShaderSwitches());
        this.shaderSwitchesLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append("Texture bindings: ");
        stringBuilder.append(this.glProfiler.getTextureBindings());
        this.textureBindsLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append("Vertices: ");
        stringBuilder.append(this.glProfiler.getVertexCount().total);
        this.vertexCountLabel.setText(stringBuilder);
        this.glProfiler.reset();
        stringBuilder.setLength(0);
    }

    private void initOpenGLDebugInfo() {
        if (!getmSenceConfiguration().ismDebug() || !getmSenceConfiguration().ismShowOpengGL()) {
            return;
        }
        this.vertexCountLabel = new Label("Vertices: 999", new Label.LabelStyle(this.mFont, Color.WHITE));
        this.vertexCountLabel.setPosition(0.0f, this.mFps.getTop());
        this.mStage.addActor(this.vertexCountLabel);
        this.textureBindsLabel = new Label("Texture bindings: 999", new Label.LabelStyle(this.mFont, Color.WHITE));
        this.textureBindsLabel.setPosition(0.0f, this.vertexCountLabel.getTop());
        this.mStage.addActor(this.textureBindsLabel);
        this.shaderSwitchesLabel = new Label("Shader switches: 999", new Label.LabelStyle(this.mFont, Color.WHITE));
        this.shaderSwitchesLabel.setPosition(0.0f, this.textureBindsLabel.getTop());
        this.mStage.addActor(this.shaderSwitchesLabel);
        this.drawCallsLabel = new Label("Draw calls: 999", new Label.LabelStyle(this.mFont, Color.WHITE));
        this.drawCallsLabel.setPosition(0.0f, this.shaderSwitchesLabel.getTop());
        this.mStage.addActor(this.drawCallsLabel);
        this.glCallsLabel = new Label("GL calls: 999", new Label.LabelStyle(this.mFont, Color.WHITE));
        this.glCallsLabel.setPosition(0.0f, this.drawCallsLabel.getTop());
        this.mStage.addActor(this.glCallsLabel);
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void resize(int width, int height) {
        this.mLoger.info("resize");
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void render() {
        Stage stage;
        Gdx.gl.glClear(16640);
        Gdx.gl.glClearColor(this.mBgColor.r, this.mBgColor.g, this.mBgColor.b, this.mBgColor.a);
        if (getmSenceConfiguration().ismDebug() && getmSenceConfiguration().ismShowFps()) {
            drawFPS();
        }
        if (getmSenceConfiguration().ismDebug() && getmSenceConfiguration().isShowLog()) {
            this.mLogger.log();
        }
        if (getmSenceConfiguration().ismDebug() && (stage = this.mStage) != null) {
            stage.draw();
        }
        if (getmSenceConfiguration().ismDebug() && getmSenceConfiguration().ismShowAxe()) {
            this.mModelBatch.begin(this.mCam);
            this.mModelBatch.render(this.mAxesInstance);
            this.mModelBatch.end();
        }
        if (getmSenceConfiguration().ismDebug() && getmSenceConfiguration().ismShowOpengGL()) {
            getStatus(this.stringBuilder);
        }
        if (getmSenceConfiguration().ismDebug()) {
            debugLog();
        }
    }

    public void debugLog() {
        if (TimeUtils.nanoTime() - this.startTime > this.interval * 1000000000) {
            Application application = Gdx.app;
            application.log(TAG, "Sence Configuration: " + getmSenceConfiguration().toString());
            this.startTime = TimeUtils.nanoTime();
        }
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void pause() {
        this.mLoger.info("pause");
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void resume() {
        this.mLoger.info("resume");
    }

    @Override // com.badlogic.gdx.ApplicationListener
    public void dispose() {
        this.mLoger.info("dispose");
        this.glProfiler.disable();
    }

    private void drawFPS() {
        if (this.mFps != null && this.mFont != null) {
            this.mBatch.begin();
            BitmapFont bitmapFont = this.mFont;
            SpriteBatch spriteBatch = this.mBatch;
            bitmapFont.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 20);
            this.mFps.setFontScale(1.2f);
            Label label = this.mFps;
            label.setText("FPS:\t" + Gdx.graphics.getFramesPerSecond());
            this.mFps.draw(this.mBatch, 1.0f);
            this.mBatch.end();
        }
    }

    protected void onExit() {
    }

    @Override // com.badlogic.gdx.InputAdapter, com.badlogic.gdx.InputProcessor
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Log.i(TAG, "touchup");
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override // com.badlogic.gdx.InputAdapter, com.badlogic.gdx.InputProcessor
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Log.i(TAG, "touchDown");
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean touchDown(float var1, float var2, int var3, int var4) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean tap(float var1, float var2, int var3, int var4) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean longPress(float var1, float var2) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean fling(float var1, float var2, int var3) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean pan(float var1, float var2, float var3, float var4) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean panStop(float var1, float var2, int var3, int var4) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean zoom(float var1, float var2) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public boolean pinch(Vector2 var1, Vector2 var2, Vector2 var3, Vector2 var4) {
        return false;
    }

    @Override // com.badlogic.gdx.input.GestureDetector.GestureListener
    public void pinchStop() {
    }
}
