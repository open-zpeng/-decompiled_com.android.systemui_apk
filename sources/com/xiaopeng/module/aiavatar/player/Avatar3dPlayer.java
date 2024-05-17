package com.xiaopeng.module.aiavatar.player;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.subtitle.Cea708CCParser;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapLightAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ScreenBlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.com.badlogic.gdx.graphics.webp.WebpAnimationLruCache;
import com.com.badlogic.gdx.graphics.webp.WebpTextureManager;
import com.google.gson.Gson;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.module.aiavatar.event.FullBodyEventController;
import com.xiaopeng.module.aiavatar.fresnel.FreshnelShaderProvider;
import com.xiaopeng.module.aiavatar.fresnel.FresnelAttribute;
import com.xiaopeng.module.aiavatar.fresnel.ReflectionFrenelAttribute;
import com.xiaopeng.module.aiavatar.graphics.g3d.Base3D;
import com.xiaopeng.module.aiavatar.helper.TextureUtil;
import com.xiaopeng.module.aiavatar.mvp.avatar.AvatarRootView;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarPlayStatus;
import com.xiaopeng.module.aiavatar.player.action.ActionLoadManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes23.dex */
public class Avatar3dPlayer extends Base3D implements IAvatarPlayer, WebpAnimationLruCache.OnEntryRemovedListener {
    public static final String FILE_TYPE_WEBP = "webp";
    private static final String GLASS_BOX_NX = "model/glass_box_nx.jpg";
    private static final String GLASS_BOX_NY = "model/glass_box_ny.jpg";
    private static final String GLASS_BOX_NZ = "model/glass_box_nz.jpg";
    private static final String GLASS_BOX_PX = "model/glass_box_px.jpg";
    private static final String GLASS_BOX_PY = "model/glass_box_py.jpg";
    private static final String GLASS_BOX_PZ = "model/glass_box_pz.jpg";
    private static final String GLASS_LIGHT_BOX_NX = "model/glass_light_box.jpg";
    private static final String GLASS_LIGHT_BOX_NY = "model/glass_light_box.jpg";
    private static final String GLASS_LIGHT_BOX_NZ = "model/glass_light_box.jpg";
    private static final String GLASS_LIGHT_BOX_PX = "model/glass_light_box.jpg";
    private static final String GLASS_LIGHT_BOX_PY = "model/glass_light_box_py.jpg";
    private static final String GLASS_LIGHT_BOX_PZ = "model/glass_light_box.jpg";
    private static final String MATERIAL_BODY = "Mat_Body";
    private static final String MATERIAL_GLASS = "Mat_VRglass";
    private static final String MATERIAL_GLASS_F = "mat_glass";
    private static final String MATERIAL_GLASS_SPREAD = "mat_qian";
    private static final String MATERIAL_LEFT = "mat_ear_left";
    private static final String MATERIAL_LEFT_TOP = "mat_left_top";
    private static final String MATERIAL_RIGHT = "mat_ear_right";
    private static final String MODEL_FRAME_BODY_DAY = "model/mapalbedo.jpg";
    private static final String MODEL_FRAME_BODY_NIGHT = "model/mapnighta.jpg";
    private static final String NODE_GLASS = "VRglass";
    private static final String NODE_GLASS_SPREAD = "model_qian";
    private static final String NODE_LEFT = "model_ear_left";
    private static final String NODE_LEFT_TOP = "model_left_top";
    private static final String NODE_RIGHT = "model_ear_right";
    public static final int PLAY_STATUS_END = 2;
    private static final int PLAY_TIMES_ACTION_LOOP = -1;
    public static final float START_RENDER_Y = 13.0f;
    public static final float START_SCALE = 0.117f;
    public static final float START_X = -0.5f;
    public static final float START_Y = 3.0f;
    public static final float START_Z = 0.0f;
    private static final String TAG = "Avatar3dPlayer";
    public static final String WEBP_DAY = ".";
    public static final String WEBP_NIGHT_GLASSES = ".";
    public static final String WEBP_NIGHT_LEFT = ".";
    public static final String WEBP_NIGHT_LEFT_TOP = "_night.";
    public static final String WEBP_NIGHT_RIGHT = ".";
    private AnimationController animationController;
    private float color_a;
    private float color_b;
    private float color_g;
    private float color_r;
    private LinearInterpolator floatInterpolator;
    private GLSurfaceView glSurfaceView;
    private ModelInstance instanceModel;
    private AnimationController.AnimationDesc mAnimationDesc;
    private BlendingAttribute mAttrDefault;
    private BlendingAttribute mAttrTrans;
    private AvatarRootView mAvatarRootView;
    private Context mContext;
    private Cubemap mCubeMap;
    private Cubemap mCubeMap2;
    Runnable mCubeRunnable;
    private int mCurActionIndex;
    private List<AvatarBean.AvatarAction> mCurActionList;
    private String mCurLeftTop;
    private TextureAttribute mCurSkinAttribute;
    private Texture mCurSkinTexture;
    private FresnelAttribute mFresnelAttributeBody;
    private boolean mIsSpread;
    private int mWarnLevel;
    private WebpTextureManager mWebpTMGlasses;
    private WebpTextureManager mWebpTMLeft;
    private WebpTextureManager mWebpTMLeftTop;
    private WebpTextureManager mWebpTMRight;
    private int mWindowStatus;
    private float movingXDistance;
    private float originPositionZ;
    private AccelerateDecelerateInterpolator zoomInterpolator;
    private String g3dbModelPath = "model/body_model.g3db";
    private String modelTexturePath = "";
    private float mTranslateX = -0.5f;
    private float mTranslateY = 3.0f;
    private float mTranslateZ = 0.0f;
    private float mScale = 0.117f;
    private int pauseFlowCount = 0;
    private boolean isFloatingModel = false;
    private float mFloatTime = 0.0f;
    private float mTranslateXTime = 0.0f;
    private float zoomTime = 0.0f;
    private float beforeZ = 0.0f;
    private float beforeY = 0.0f;
    private float beforeX = 0.0f;
    private boolean isStartTranslate = false;
    private boolean isZoomed = false;
    private boolean upAndDown = true;
    private int xPosition = 0;
    private boolean canMoveX = false;
    private HashMap<String, Node> mNodeNotInModelMap = new HashMap<>();
    private String mCurGlassMaterial = MATERIAL_GLASS;
    private boolean mIsLoaded = false;
    private ExitWorkingStateTask mExitWorkingStateTask = new ExitWorkingStateTask();
    private Handler mHandler = new Handler();

    static /* synthetic */ int access$908(Avatar3dPlayer x0) {
        int i = x0.mCurActionIndex;
        x0.mCurActionIndex = i + 1;
        return i;
    }

    public Avatar3dPlayer(Context context, AvatarRootView avatarRootView) {
        this.mContext = context;
        this.mWebpTMGlasses = new WebpTextureManager(1, context, "Glasses", 2, Cea708CCParser.Const.CODE_C1_DLW);
        this.mWebpTMGlasses.initDefaultWebpAnimation();
        this.mWebpTMGlasses.setCallbackOnWebpEnd(true);
        this.mWebpTMGlasses.setActive(true);
        this.mWebpTMGlasses.setEntryRemovedListener(this);
        this.mWebpTMRight = new WebpTextureManager(4, context, "Right", 2, 2);
        this.mWebpTMRight.setEntryRemovedListener(this);
        this.mWebpTMLeft = new WebpTextureManager(3, context, "Left", 2, 2);
        this.mWebpTMLeft.setEntryRemovedListener(this);
        this.mWebpTMLeftTop = new WebpTextureManager(2, context, "LeftTop", 2, 2);
        this.mWebpTMLeftTop.setEntryRemovedListener(this);
        this.mAttrDefault = new BlendingAttribute((int) GL20.GL_SRC_ALPHA, (int) GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.mAttrTrans = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.0f);
        this.mAvatarRootView = avatarRootView;
        this.zoomInterpolator = new AccelerateDecelerateInterpolator();
        this.floatInterpolator = new LinearInterpolator();
        initDefaultColor();
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.Base3D, com.xiaopeng.module.aiavatar.graphics.g3d.AbstractApplication, com.badlogic.gdx.ApplicationListener
    public void create() {
        super.create();
        Log.d(TAG, "create");
        loadFresnelBatch();
        this.originPositionZ = this.mCam.position.z;
        this.mAssets.load(this.g3dbModelPath, Model.class);
    }

    private void loadFresnelBatch() {
        AndroidFiles files = new AndroidFiles(this.mContext.getResources().getAssets(), this.mContext.getFilesDir().getAbsolutePath());
        FileHandle vex = files.internal("shader/default_freshnel_vex.glsl");
        FileHandle frg = files.internal("shader/default_freshnel_frg.glsl");
        this.mModelBatch = new ModelBatch(new FreshnelShaderProvider(vex, frg));
    }

    private void initDefaultColor() {
        this.color_r = 0.9372549f;
        this.color_g = 0.99215686f;
        this.color_b = 1.0f;
        this.color_a = 1.0f;
    }

    private void initLightDay() {
        this.mEnv.clear();
        initDefaultColor();
        this.color_r *= 0.42f;
        this.color_g *= 0.42f;
        this.color_b *= 0.42f;
        DirectionalLight left_before = new DirectionalLight();
        left_before.setColor(this.color_r * 0.3f, this.color_g * 0.3f, this.color_b * 0.3f, this.color_a);
        left_before.setDirection(new Vector3(-1.0f, -0.2f, -1.0f));
        DirectionalLight right_before = new DirectionalLight();
        right_before.setColor(this.color_r * 0.3f, this.color_g * 0.3f, this.color_b * 0.3f, this.color_a);
        right_before.setDirection(new Vector3(1.0f, -0.2f, -1.0f));
        DirectionalLight left_after = new DirectionalLight();
        left_after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        left_after.setDirection(new Vector3(0.0f, 0.0f, 1.0f));
        DirectionalLight right_after = new DirectionalLight();
        right_after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        right_after.setDirection(new Vector3(1.0f, -0.2f, 1.0f));
        DirectionalLight before = new DirectionalLight();
        before.setColor(this.color_r * 0.5f, this.color_g * 0.5f, this.color_b * 0.5f, this.color_a);
        before.setDirection(new Vector3(0.0f, -0.4f, -1.0f));
        DirectionalLight after = new DirectionalLight();
        after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        after.setDirection(new Vector3(0.0f, 0.0f, 1.0f));
        DirectionalLight left = new DirectionalLight();
        left.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        left.setDirection(new Vector3(-1.0f, 0.0f, 0.0f));
        DirectionalLight right = new DirectionalLight();
        right.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        right.setDirection(new Vector3(1.0f, 0.0f, 0.0f));
        DirectionalLight top = new DirectionalLight();
        float f = this.color_r;
        float f2 = this.color_g;
        float f3 = this.color_b;
        float fator = this.color_a;
        top.setColor(f, f2, f3, fator);
        top.setDirection(new Vector3(0.0f, -1.0f, 0.0f));
        DirectionalLight top2 = new DirectionalLight();
        top2.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        top2.setDirection(new Vector3(0.0f, -0.5f, 0.0f));
        DirectionalLight top3 = new DirectionalLight();
        top3.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        top3.setDirection(new Vector3(0.0f, 0.0f, 0.0f));
        DirectionalLight bottom = new DirectionalLight();
        bottom.setColor(this.color_r * 0.3f, this.color_g * 0.3f, this.color_b * 0.3f, this.color_a);
        bottom.setDirection(new Vector3(0.0f, 1.0f, 0.0f));
        DirectionalLight beforeBottom = new DirectionalLight();
        float fator_a = this.color_a;
        beforeBottom.setColor(this.color_r * 0.3f, this.color_g * 0.3f, this.color_b * 0.3f, fator_a);
        beforeBottom.setDirection(new Vector3(0.0f, 1.0f, -1.0f));
        this.mEnv.clear();
        this.mEnv.add(before);
        this.mEnv.add(left_before);
        this.mEnv.add(right_before);
        this.mEnv.add(after);
        this.mEnv.add(left);
        this.mEnv.add(right);
        this.mEnv.add(top);
        this.mEnv.add(top2);
        this.mEnv.add(beforeBottom);
        this.mEnv.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, this.mCubeMap));
    }

    private void initLight() {
        boolean isNightModel = ThemeManager.isNightMode(this.mContext);
        if (!isNightModel) {
            initLightDay();
        } else {
            initLightNight();
        }
    }

    private void initLightNight() {
        this.mEnv.clear();
        initDefaultColor();
        this.color_r *= 0.1f;
        this.color_g *= 0.1f;
        this.color_b *= 0.1f;
        DirectionalLight left_before = new DirectionalLight();
        left_before.setColor(this.color_r * 0.4f, this.color_g * 0.4f, this.color_b * 0.4f, this.color_a);
        left_before.setDirection(new Vector3(-1.0f, -0.2f, -1.0f));
        DirectionalLight right_before = new DirectionalLight();
        right_before.setColor(this.color_r * 0.4f, this.color_g * 0.4f, this.color_b * 0.4f, this.color_a);
        right_before.setDirection(new Vector3(1.0f, -0.2f, -1.0f));
        DirectionalLight left_after = new DirectionalLight();
        left_after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        left_after.setDirection(new Vector3(0.0f, 0.0f, 1.0f));
        DirectionalLight right_after = new DirectionalLight();
        right_after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        right_after.setDirection(new Vector3(1.0f, -0.2f, 1.0f));
        DirectionalLight before = new DirectionalLight();
        before.setColor(this.color_r * 0.5f, this.color_g * 0.5f, this.color_b * 0.5f, this.color_a);
        before.setDirection(new Vector3(0.0f, -0.4f, -1.0f));
        DirectionalLight after = new DirectionalLight();
        after.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        after.setDirection(new Vector3(0.0f, 0.0f, 1.0f));
        DirectionalLight left = new DirectionalLight();
        left.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        left.setDirection(new Vector3(-1.0f, 0.0f, 0.0f));
        DirectionalLight right = new DirectionalLight();
        right.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        right.setDirection(new Vector3(1.0f, 0.0f, 0.0f));
        DirectionalLight top = new DirectionalLight();
        float f = this.color_r;
        float f2 = this.color_g;
        float f3 = this.color_b;
        float fator = this.color_a;
        top.setColor(f, f2, f3, fator);
        top.setDirection(new Vector3(0.0f, -1.0f, 0.0f));
        DirectionalLight top2 = new DirectionalLight();
        top2.setColor(this.color_r, this.color_g, this.color_b, this.color_a);
        top2.setDirection(new Vector3(0.0f, -0.5f, 0.0f));
        DirectionalLight beforeBottom = new DirectionalLight();
        float fator_a = this.color_a;
        beforeBottom.setColor(this.color_r * 0.4f, this.color_g * 0.4f, this.color_b * 0.4f, fator_a);
        beforeBottom.setDirection(new Vector3(0.0f, 1.0f, -1.0f));
        this.mEnv.clear();
        this.mEnv.add(before);
        this.mEnv.add(left_before);
        this.mEnv.add(right_before);
        this.mEnv.add(after);
        this.mEnv.add(left);
        this.mEnv.add(right);
        this.mEnv.add(top);
        this.mEnv.add(top2);
        this.mEnv.add(beforeBottom);
        this.mEnv.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, this.mCubeMap));
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.Base3D
    protected void render(ModelBatch batch, Array<ModelInstance> mInstances) {
        TextureAttribute curFrame;
        Material material;
        TextureAttribute curFrame2;
        Material material2;
        TextureAttribute curFrame3;
        Material material3;
        TextureAttribute curFrame4;
        Material material4;
        try {
            if (this.animationController != null) {
                this.animationController.update(0.041f);
            }
            if (this.instanceModel != null) {
                if (this.mAvatarRootView.isFullBody()) {
                    setBodyStatus(3);
                } else {
                    setBodyStatus(1);
                }
                updateModelTexture();
                if (getTranslateX() != this.mTranslateX) {
                    translateX(this.mTranslateX);
                }
                if (getTranslateY() != this.mTranslateY) {
                    translateY(this.mTranslateY);
                }
                if (getTranslateZ() != this.mTranslateZ) {
                    translateZ(this.mTranslateZ);
                }
                if (getScale() != this.mScale) {
                    scale(this.mScale);
                }
                renderWindowStatus();
                if (this.isFloatingModel && this.pauseFlowCount == 0) {
                    floatingModel();
                } else {
                    this.pauseFlowCount--;
                }
                if (this.isStartTranslate) {
                    avatarZoom();
                }
                if (this.canMoveX) {
                    movingXAxis();
                }
                if (this.mWebpTMGlasses.canRun() && (curFrame4 = this.mWebpTMGlasses.getCurrentFrame()) != null && (material4 = getMaterialById(this.mCurGlassMaterial)) != null) {
                    material4.set(curFrame4);
                }
                if (this.mWebpTMLeftTop.canRun() && (curFrame3 = this.mWebpTMLeftTop.getCurrentFrame()) != null && (material3 = getMaterialById(MATERIAL_LEFT_TOP)) != null) {
                    material3.set(curFrame3);
                    material3.set(this.mAttrDefault);
                }
                if (this.mWebpTMLeft.canRun() && (curFrame2 = this.mWebpTMLeft.getCurrentFrame()) != null && (material2 = getMaterialById(MATERIAL_LEFT)) != null) {
                    material2.set(curFrame2);
                    material2.set(this.mAttrDefault);
                }
                if (this.mWebpTMRight.canRun() && (curFrame = this.mWebpTMRight.getCurrentFrame()) != null && (material = getMaterialById(MATERIAL_RIGHT)) != null) {
                    material.set(curFrame);
                    material.set(this.mAttrDefault);
                }
                if (this.mCubeRunnable != null) {
                    this.mCubeRunnable.run();
                    this.mCubeRunnable = null;
                }
                batch.render(this.instanceModel, this.mEnv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateModelTexture() {
        if (ThemeManager.isNightMode(this.mContext)) {
            updateModelTexture(MODEL_FRAME_BODY_NIGHT);
        } else {
            updateModelTexture(MODEL_FRAME_BODY_DAY);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindLightCube() {
        initLightCube();
        CubemapLightAttribute cubemapAttribute = new CubemapLightAttribute(CubemapLightAttribute.EnvironmentMap, this.mCubeMap2);
        Material glass = getMaterialById(MATERIAL_GLASS_F);
        if (glass != null) {
            glass.set(cubemapAttribute);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindBoxCube() {
        initBoxCube();
        this.mEnv.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, this.mCubeMap));
    }

    private Material getMaterialById(String id) {
        ModelInstance modelInstance = this.instanceModel;
        if (modelInstance == null) {
            return null;
        }
        Iterator<Material> it = modelInstance.materials.iterator();
        while (it.hasNext()) {
            Material material = it.next();
            if (material.id.equals(id)) {
                return material;
            }
        }
        return null;
    }

    private void floatingModel() {
        float deltaTime = Gdx.graphics.getDeltaTime() * 1000.0f;
        this.mFloatTime += deltaTime;
        float f = this.mFloatTime;
        if (f <= 1800.0f) {
            float currentY = this.floatInterpolator.getInterpolation(f / 1800.0f);
            float translateY = (currentY - this.beforeY) * 0.6f;
            avatarTranslateY(translateY);
            this.beforeY = currentY;
            return;
        }
        float translateY2 = (1.0f - this.beforeY) * 0.6f;
        avatarTranslateY(translateY2);
        this.beforeY = 0.0f;
        this.mFloatTime = 0.0f;
        this.upAndDown = !this.upAndDown;
    }

    private void avatarTranslateY(float translateY) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        if (this.upAndDown) {
            matrix4[13] = matrix4[13] + translateY;
        } else {
            matrix4[13] = matrix4[13] + (-translateY);
        }
        this.instanceModel.transform.set(matrix4);
    }

    public void setTranslateX(float translateX) {
        this.mTranslateX = translateX;
    }

    public void translateX(float translateY) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        matrix4[13] = translateY;
        this.instanceModel.transform.set(matrix4);
    }

    public float getTranslateX() {
        float[] matrix4 = this.instanceModel.transform.getValues();
        return matrix4[12];
    }

    public void setTranslateY(float translateY) {
        this.mTranslateY = translateY;
    }

    public float getTranslateY() {
        float[] matrix4 = this.instanceModel.transform.getValues();
        return matrix4[13];
    }

    public void translateY(float translateY) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        matrix4[13] = translateY;
        this.instanceModel.transform.set(matrix4);
    }

    public void translateZ(float translateZ) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        matrix4[14] = translateZ;
        this.instanceModel.transform.set(matrix4);
    }

    public void setTranslateZ(float translateZ) {
        this.mTranslateZ = translateZ;
    }

    public float getTranslateZ() {
        float[] matrix4 = this.instanceModel.transform.getValues();
        return matrix4[13];
    }

    public void setRenderY(float renderY) {
        FresnelAttribute fresnelAttribute = this.mFresnelAttributeBody;
        if (fresnelAttribute != null) {
            fresnelAttribute.setBodyRenderY(renderY);
        }
    }

    public void scale(float scale) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        matrix4[0] = scale;
        matrix4[5] = scale;
        matrix4[10] = scale;
        this.instanceModel.transform.set(matrix4);
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public float getScale() {
        float[] matrix4 = this.instanceModel.transform.getValues();
        return matrix4[0];
    }

    public void setWindowStatus(int status) {
        Log.i(TAG, "setWindowStatus status : " + status);
        if (status == this.mWindowStatus) {
            return;
        }
        this.mWindowStatus = status;
        this.mCubeRunnable = new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.1
            @Override // java.lang.Runnable
            public void run() {
                if (Avatar3dPlayer.this.mCubeMap != null) {
                    Avatar3dPlayer.this.mCubeMap.dispose();
                }
                Avatar3dPlayer.this.bindBoxCube();
                if (Avatar3dPlayer.this.mCubeMap2 != null) {
                    Avatar3dPlayer.this.mCubeMap2.dispose();
                }
                Avatar3dPlayer.this.bindLightCube();
                Avatar3dPlayer.this.updateModelTexture();
            }
        };
        refresh();
        releaseGlassTexture();
    }

    public int getWindowStatus() {
        return this.mWindowStatus;
    }

    public void releaseGlassTexture() {
        this.glSurfaceView.queueEvent(new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.2
            @Override // java.lang.Runnable
            public void run() {
                Avatar3dPlayer.this.mWebpTMGlasses.releseTexture();
            }
        });
    }

    private void renderWindowStatus() {
        Material materialBody = getMaterialById(MATERIAL_BODY);
        FresnelAttribute fresnelAttribute = this.mFresnelAttributeBody;
        if (fresnelAttribute != null) {
            int windowStatus = fresnelAttribute.getWindowStatus();
            int i = this.mWindowStatus;
            if (windowStatus != i) {
                this.mFresnelAttributeBody.setWindowStatus(i);
                if (materialBody != null) {
                    materialBody.set(this.mFresnelAttributeBody);
                }
            }
        }
    }

    private void avatarZoom() {
        float deltaTime = Gdx.graphics.getDeltaTime() * 1000.0f;
        this.zoomTime += deltaTime;
        float f = this.zoomTime;
        if (f <= 400.0f) {
            float currentZ = this.zoomInterpolator.getInterpolation(f / 400.0f);
            float translateZ = (currentZ - this.beforeZ) * 4.2f;
            avatarTranslateZ(translateZ, false);
            this.beforeZ = currentZ;
            return;
        }
        float translateZ2 = (1.0f - this.beforeZ) * 4.2f;
        avatarTranslateZ(translateZ2, true);
        this.zoomTime = 0.0f;
        this.beforeZ = 0.0f;
    }

    private void avatarTranslateZ(float translateZ, boolean isLastTime) {
        if (!this.isZoomed) {
            if (isLastTime) {
                this.mCam.position.z = this.originPositionZ;
            } else {
                this.mCam.position.z += translateZ;
            }
        } else {
            this.mCam.position.z += -translateZ;
        }
        this.mCam.update();
        if (isLastTime) {
            this.isStartTranslate = false;
        }
    }

    private void movingXAxis() {
        float deltaTime = Gdx.graphics.getDeltaTime() * 1000.0f;
        this.mTranslateXTime += deltaTime;
        float f = this.mTranslateXTime;
        if (f <= 400.0f) {
            float currentX = this.floatInterpolator.getInterpolation(f / 400.0f);
            float translateX = (currentX - this.beforeX) * this.movingXDistance;
            avatarTranslateX(translateX);
            this.beforeX = currentX;
            return;
        }
        float translateX2 = (1.0f - this.beforeX) * this.movingXDistance;
        avatarTranslateX(translateX2);
        this.beforeX = 0.0f;
        this.mTranslateXTime = 0.0f;
        this.canMoveX = false;
    }

    private void avatarTranslateX(float translateX) {
        float[] matrix4 = this.instanceModel.transform.getValues();
        matrix4[12] = matrix4[12] + translateX;
        this.instanceModel.transform.set(matrix4);
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.Base3D
    protected void onLoaded() {
        if (this.mAssets.isLoaded(this.g3dbModelPath)) {
            Log.d(TAG, "onLoaded");
            initModelResources();
            initPosition();
            addBlendingAttri();
            removeHeadNode();
            initCubeMap();
            initFresne();
            this.mIsLoaded = true;
        }
    }

    @Override // com.xiaopeng.module.aiavatar.graphics.g3d.Base3D
    protected void actionLoad() {
        if (ActionLoadManager.getInstance().isInit()) {
            ActionLoadManager.getInstance().tryLoad();
        }
    }

    private void initCubeMap() {
        initBoxCube();
        initLightCube();
    }

    private void initBoxCube() {
        try {
            this.mCubeMap = new Cubemap(TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_PX), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_NX), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_PY), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_NY), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_PZ), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_BOX_NZ), Pixmap.Format.RGB565, false));
        } catch (Exception e) {
            Log.d(TAG, "initCubeMap : fail");
        }
    }

    private void initLightCube() {
        try {
            this.mCubeMap2 = new Cubemap(TextureData.Factory.loadFromFile(Gdx.files.internal("model/glass_light_box.jpg"), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal("model/glass_light_box.jpg"), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal(GLASS_LIGHT_BOX_PY), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal("model/glass_light_box.jpg"), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal("model/glass_light_box.jpg"), Pixmap.Format.RGB565, false), TextureData.Factory.loadFromFile(Gdx.files.internal("model/glass_light_box.jpg"), Pixmap.Format.RGB565, false));
        } catch (Exception e) {
            Log.d(TAG, "initLightCubeMap : fail");
        }
    }

    private void initFresne() {
        this.mFresnelAttributeBody = new FresnelAttribute(FresnelAttribute.Type);
        this.mFresnelAttributeBody.setDefaultStartColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.mFresnelAttributeBody.setDefautEndColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.mFresnelAttributeBody.setPow(2);
        this.mFresnelAttributeBody.setFactor(1.0f);
        this.mFresnelAttributeBody.setLow(0.15f);
        this.mFresnelAttributeBody.setWarningLevel(0);
        this.mFresnelAttributeBody.setBodyStatus(-1);
        this.mFresnelAttributeBody.setWindowStatus(0);
        this.mFresnelAttributeBody.setBodyRenderY(13.0f);
        CubemapLightAttribute cubemapAttribute = new CubemapLightAttribute(CubemapLightAttribute.EnvironmentMap, this.mCubeMap2);
        ReflectionFrenelAttribute reflectionFrenelAttribute = new ReflectionFrenelAttribute(ReflectionFrenelAttribute.Type);
        ScreenBlendingAttribute screenBlendingAttribute = new ScreenBlendingAttribute(ScreenBlendingAttribute.Type);
        ColorAttribute diffuse = ColorAttribute.createDiffuse(0.7f, 0.7f, 0.7f, 1.0f);
        Material materialLeftTop = getMaterialById(MATERIAL_LEFT_TOP);
        if (materialLeftTop != null) {
            materialLeftTop.set(diffuse);
        }
        Material materialBody = getMaterialById(MATERIAL_BODY);
        if (materialBody != null) {
            materialBody.set(diffuse);
            materialBody.set(this.mFresnelAttributeBody);
        }
        Material glass = getMaterialById(MATERIAL_GLASS_F);
        if (glass != null) {
            glass.set(reflectionFrenelAttribute);
            glass.set(cubemapAttribute);
            glass.set(screenBlendingAttribute);
            glass.set(new BlendingAttribute((int) GL20.GL_SRC_ALPHA, (int) GL20.GL_ONE_MINUS_SRC_ALPHA));
        }
    }

    public void setBodyStatus(int bodyStatus) {
        Material materialBody = getMaterialById(MATERIAL_BODY);
        FresnelAttribute fresnelAttribute = this.mFresnelAttributeBody;
        if (fresnelAttribute != null && fresnelAttribute.getBodyStatus() != bodyStatus) {
            this.mFresnelAttributeBody.setBodyStatus(bodyStatus);
            if (bodyStatus == 0 || bodyStatus == 2) {
                this.mFresnelAttributeBody.setDefaultStartColor(1.0f, 1.0f, 1.0f, 1.0f);
                this.mFresnelAttributeBody.setDefautEndColor(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                this.mFresnelAttributeBody.setDefaultStartColor(0.0f, 0.3f, 0.8f, 1.0f);
                this.mFresnelAttributeBody.setDefautEndColor(0.0f, 0.3f, 0.8f, 1.0f);
            }
            if (materialBody != null) {
                materialBody.set(this.mFresnelAttributeBody);
            }
        }
    }

    private void removeHeadNode() {
        if (this.instanceModel != null) {
            putNodeNotInModelMap(NODE_GLASS_SPREAD);
            putNodeNotInModelMap(NODE_LEFT_TOP);
            putNodeNotInModelMap(NODE_LEFT);
            putNodeNotInModelMap(NODE_RIGHT);
        }
    }

    private void putNodeNotInModelMap(final String node) {
        GLSurfaceView gLSurfaceView;
        final Node nodeById = getNodeById(node);
        if (nodeById != null && (gLSurfaceView = this.glSurfaceView) != null) {
            gLSurfaceView.queueEvent(new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.3
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(Avatar3dPlayer.TAG, "putNodeNotInModelMap : " + nodeById.id);
                    Avatar3dPlayer.this.mNodeNotInModelMap.put(node, nodeById);
                }
            });
        }
    }

    private void addBlendingAttri() {
        Material materialBody = getMaterialById(MATERIAL_BODY);
        if (materialBody != null) {
            materialBody.set(this.mAttrDefault);
        }
        Material materialLeft = getMaterialById(MATERIAL_LEFT);
        if (materialLeft != null) {
            materialLeft.set(this.mAttrTrans);
        }
        Material materialRight = getMaterialById(MATERIAL_RIGHT);
        if (materialRight != null) {
            materialRight.set(this.mAttrTrans);
        }
        Material materialLeftTop = getMaterialById(MATERIAL_LEFT_TOP);
        if (materialLeftTop != null) {
            materialLeftTop.set(this.mAttrTrans);
        }
        Material materialGlassesSpread = getMaterialById(MATERIAL_GLASS_SPREAD);
        if (materialGlassesSpread != null) {
            materialGlassesSpread.set(this.mAttrDefault);
        }
    }

    private void initModelResources() {
        Model modelAction = (Model) this.mAssets.get(this.g3dbModelPath);
        this.instanceModel = new ModelInstance(modelAction);
        Iterator<Animation> it = this.instanceModel.animations.iterator();
        while (it.hasNext()) {
            Animation anim = it.next();
            Log.i(TAG, "anim:" + anim.id);
        }
        this.animationController = new AnimationController(this.instanceModel);
        ActionLoadManager.getInstance().init(this.instanceModel);
    }

    private void initPosition() {
        this.instanceModel.transform.translate(this.mTranslateX, this.mTranslateY, this.mTranslateZ);
        Matrix4 matrix4 = this.instanceModel.transform;
        float f = this.mScale;
        matrix4.scale(f, f, f);
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateZoom(boolean isZoom) {
        if (isZoom && !this.isZoomed) {
            this.isStartTranslate = true;
            this.isZoomed = true;
        } else if (!isZoom && this.isZoomed) {
            this.isStartTranslate = true;
            this.isZoomed = false;
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateG3dbModel(String g3dbModelPath) {
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateModelTexture(String modelTexturePath) {
        if (!modelTexturePath.equals(this.modelTexturePath) || !TextureUtil.isTexture(this.mCurSkinTexture)) {
            this.modelTexturePath = modelTexturePath;
            Material material = getMaterialById(MATERIAL_BODY);
            if (material != null) {
                Texture texture = this.mCurSkinTexture;
                if (texture != null) {
                    texture.dispose();
                }
                this.mCurSkinTexture = new Texture(Gdx.files.internal(modelTexturePath), Pixmap.Format.RGB565, true);
                this.mCurSkinTexture.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.MipMap);
                this.mCurSkinAttribute = TextureAttribute.createDiffuse(this.mCurSkinTexture);
                material.set(this.mCurSkinAttribute);
                Log.d(TAG, "updateModelTexture ID : " + this.mCurSkinTexture.getTextureObjectHandle() + " isTexture : " + TextureUtil.isTexture(this.mCurSkinTexture));
            }
            this.pauseFlowCount = 100;
            initLight();
        }
    }

    private Node getNodeById(String id) {
        Node node;
        if (this.instanceModel == null) {
            return null;
        }
        for (int i = 0; i < this.instanceModel.nodes.size && (node = this.instanceModel.nodes.get(i)) != null; i++) {
            if (node.id.equals(id)) {
                return this.instanceModel.nodes.removeIndex(i);
            }
        }
        return null;
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateGlassesTexture(String glassesTexturePath, int loopCount, boolean isSpread) {
        this.mIsSpread = isSpread;
        if (this.mIsSpread) {
            if (!this.mWebpTMGlasses.isWebpLoaded(glassesTexturePath)) {
                this.mWebpTMGlasses.decodeWebp(glassesTexturePath, loopCount);
            }
            this.mCurGlassMaterial = MATERIAL_GLASS_SPREAD;
            if (this.instanceModel != null) {
                if (!this.mNodeNotInModelMap.containsKey(NODE_GLASS)) {
                    putNodeNotInModelMap(NODE_GLASS);
                }
                if (this.mNodeNotInModelMap.containsKey(NODE_GLASS_SPREAD)) {
                    addNode(this.mNodeNotInModelMap.get(NODE_GLASS_SPREAD));
                    this.mNodeNotInModelMap.remove(NODE_GLASS_SPREAD);
                }
            }
        } else {
            this.mCurGlassMaterial = MATERIAL_GLASS;
            if (this.instanceModel != null) {
                if (!this.mNodeNotInModelMap.containsKey(NODE_GLASS_SPREAD)) {
                    putNodeNotInModelMap(NODE_GLASS_SPREAD);
                }
                if (this.mNodeNotInModelMap.containsKey(NODE_GLASS)) {
                    addNode(this.mNodeNotInModelMap.get(NODE_GLASS));
                    this.mNodeNotInModelMap.remove(NODE_GLASS);
                }
            }
        }
        this.mWebpTMGlasses.playAnimation(glassesTexturePath, glassesTexturePath.replace(".", "."), loopCount);
    }

    private void addNode(final Node node) {
        GLSurfaceView gLSurfaceView;
        if (node != null && (gLSurfaceView = this.glSurfaceView) != null) {
            gLSurfaceView.queueEvent(new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.4
                @Override // java.lang.Runnable
                public void run() {
                    Log.d(Avatar3dPlayer.TAG, "addNode : " + node.id);
                    Avatar3dPlayer.this.instanceModel.nodes.add(node);
                }
            });
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateEnvBgTexture(String envBgTexturePath) {
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateActionId(List<AvatarBean.AvatarAction> actionList) {
        if (actionList == null || actionList.size() == 0) {
            return;
        }
        this.mCurActionIndex = 0;
        this.mCurActionList = actionList;
        playAnimation(this.mCurActionList.get(this.mCurActionIndex));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playAnimation(final AvatarBean.AvatarAction action) {
        if (this.animationController == null) {
            return;
        }
        final String actionId = action.getActionId();
        if (TextUtils.isEmpty(actionId)) {
            return;
        }
        this.animationController.setAnimation((String) null);
        Animation animation = this.instanceModel.getAnimation(actionId);
        Runnable animAction = new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.5
            @Override // java.lang.Runnable
            public void run() {
                final int loopTimes = action.getLoopTimes();
                int loopCount = loopTimes >= 1 ? loopTimes : 1;
                AnimationController.AnimationDesc desc = Avatar3dPlayer.this.animationController.action(actionId, loopCount, 1.0f, new AnimationController.AnimationListener() { // from class: com.xiaopeng.module.aiavatar.player.Avatar3dPlayer.5.1
                    @Override // com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener
                    public void onEnd(AnimationController.AnimationDesc animation2) {
                        if (loopTimes == -1) {
                            Avatar3dPlayer.this.playAnimation(action);
                            return;
                        }
                        Avatar3dPlayer.access$908(Avatar3dPlayer.this);
                        if (Avatar3dPlayer.this.mCurActionIndex < Avatar3dPlayer.this.mCurActionList.size()) {
                            Avatar3dPlayer.this.playAnimation((AvatarBean.AvatarAction) Avatar3dPlayer.this.mCurActionList.get(Avatar3dPlayer.this.mCurActionIndex));
                        } else if (animation2 != null) {
                            if (Avatar3dPlayer.this.mAvatarRootView.isFullBody()) {
                                FullBodyEventController.instance().onEventEnd(animation2.animation.id);
                            } else {
                                Avatar3dPlayer.this.onActionEnd(animation2.animation.id);
                            }
                        }
                    }

                    @Override // com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener
                    public void onLoop(AnimationController.AnimationDesc animation2) {
                    }
                }, 0.1f);
                desc.time = 0.0f;
            }
        };
        if (animation != null) {
            animAction.run();
        } else {
            ActionLoadManager.getInstance().task(actionId, animAction);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void onActionEnd(String actionId) {
        AvatarPlayStatus playStatus = new AvatarPlayStatus();
        playStatus.setStatus(2);
        Uri.Builder builder = new Uri.Builder();
        builder.authority("com.xiaopeng.aiavatarservice.APIRouterHelper").path("onAvatarStateChange").appendQueryParameter("param", new Gson().toJson(playStatus));
        try {
            ApiRouter.route(builder.build());
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "onActionEnd error for ApiRouter Exception.");
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateLightColor(AvatarBean.LightColor lightColor) {
        this.color_a = lightColor.alpha;
        this.color_b = lightColor.blue / 255.0f;
        this.color_g = lightColor.green / 255.0f;
        this.color_r = lightColor.red / 255.0f;
        initLight();
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateMovingX(int xPosition) {
        if (xPosition != this.xPosition) {
            if (xPosition == 0 || xPosition == -1 || xPosition == 1) {
                int i = this.xPosition;
                if (i == 0) {
                    this.movingXDistance = xPosition < 0 ? -2.0f : 2.0f;
                } else if (i < 0) {
                    this.movingXDistance = xPosition == 0 ? 2.0f : 2.0f * 2.0f;
                } else {
                    this.movingXDistance = xPosition == 0 ? -2.0f : (-2.0f) * 2.0f;
                }
                this.canMoveX = true;
                this.xPosition = xPosition;
            }
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateLeftTop(String leftTop) {
        this.mCurLeftTop = leftTop;
        if (this.mHandler.hasCallbacks(this.mExitWorkingStateTask)) {
            this.mHandler.removeCallbacks(this.mExitWorkingStateTask);
        }
        if (!TextUtils.isEmpty(leftTop)) {
            if (!this.mWebpTMLeftTop.isWebpLoaded(leftTop)) {
                this.mWebpTMLeftTop.decodeWebp(leftTop, -1);
            }
            if (this.instanceModel != null && this.mNodeNotInModelMap.containsKey(NODE_LEFT_TOP)) {
                addNode(this.mNodeNotInModelMap.get(NODE_LEFT_TOP));
                this.mNodeNotInModelMap.remove(NODE_LEFT_TOP);
            }
            this.mWebpTMLeftTop.setActive(true);
            this.mWebpTMLeftTop.playAnimation(leftTop, leftTop.replace(".", WEBP_NIGHT_LEFT_TOP), -1);
            return;
        }
        this.mWebpTMLeftTop.setActive(false);
        if (!this.mNodeNotInModelMap.containsKey(NODE_LEFT_TOP)) {
            putNodeNotInModelMap(NODE_LEFT_TOP);
        }
        Material material = getMaterialById(MATERIAL_LEFT_TOP);
        if (material != null) {
            material.set(this.mAttrTrans);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateLeft(String left) {
        if (!TextUtils.isEmpty(left)) {
            if (!this.mWebpTMLeft.isWebpLoaded(left)) {
                this.mWebpTMLeft.decodeWebp(left, -1);
            }
            if (this.instanceModel != null && this.mNodeNotInModelMap.containsKey(NODE_LEFT)) {
                addNode(this.mNodeNotInModelMap.get(NODE_LEFT));
                this.mNodeNotInModelMap.remove(NODE_LEFT);
            }
            this.mWebpTMLeft.setActive(true);
            this.mWebpTMLeft.playAnimation(left, left.replace(".", "."), -1);
            return;
        }
        this.mWebpTMLeft.setActive(false);
        if (!this.mNodeNotInModelMap.containsKey(NODE_LEFT)) {
            putNodeNotInModelMap(NODE_LEFT);
        }
        Material material = getMaterialById(MATERIAL_LEFT);
        if (material != null) {
            material.set(this.mAttrTrans);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateRight(String right) {
        if (!TextUtils.isEmpty(right)) {
            if (!this.mWebpTMRight.isWebpLoaded(right)) {
                this.mWebpTMRight.decodeWebp(right, -1);
            }
            if (this.instanceModel != null && this.mNodeNotInModelMap.containsKey(NODE_RIGHT)) {
                addNode(this.mNodeNotInModelMap.get(NODE_RIGHT));
                this.mNodeNotInModelMap.remove(NODE_RIGHT);
            }
            this.mWebpTMRight.setActive(true);
            this.mWebpTMRight.playAnimation(right, right.replace(".", "."), -1);
            return;
        }
        this.mWebpTMRight.setActive(false);
        if (!this.mNodeNotInModelMap.containsKey(NODE_RIGHT)) {
            putNodeNotInModelMap(NODE_RIGHT);
        }
        Material material = getMaterialById(MATERIAL_RIGHT);
        if (material != null) {
            material.set(this.mAttrTrans);
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateWarnLevel(int level) {
        this.mWarnLevel = level;
    }

    @Override // com.com.badlogic.gdx.graphics.webp.WebpAnimationLruCache.OnEntryRemovedListener
    public void onEntryRemoved(Runnable entryRemovedTask) {
        if (entryRemovedTask != null) {
            this.mAvatarRootView.postGLTask(entryRemovedTask);
        }
    }

    public boolean isloaded() {
        return this.mIsLoaded;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes23.dex */
    public class ExitWorkingStateTask implements Runnable {
        private String restoreLeftTop;

        private ExitWorkingStateTask() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Avatar3dPlayer.this.updateLeftTop(this.restoreLeftTop);
            Avatar3dPlayer.this.refresh();
        }

        public void setRestoreLeftTop(String restoreLeftTop) {
            this.restoreLeftTop = restoreLeftTop;
        }
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void showWorkingState() {
        String workingRes;
        if (this.mHandler.hasCallbacks(this.mExitWorkingStateTask)) {
            this.mHandler.removeCallbacks(this.mExitWorkingStateTask);
        }
        if (!"avatar/working.webp".equals(this.mCurLeftTop) && !"avatar/working_night.webp".equals(this.mCurLeftTop)) {
            this.mExitWorkingStateTask.setRestoreLeftTop(this.mCurLeftTop);
        }
        if (!ThemeManager.isNightMode(this.mContext)) {
            workingRes = "avatar/working.webp";
        } else {
            workingRes = "avatar/working_night.webp";
        }
        updateLeftTop(workingRes);
        refresh();
        this.mHandler.postDelayed(this.mExitWorkingStateTask, 3000L);
    }

    @Override // com.xiaopeng.module.aiavatar.player.IAvatarPlayer
    public void updateSkin(AvatarBean.Skin skin) {
        if (skin == null) {
            return;
        }
        Log.d(TAG, "updateSkin skin=" + skin.FullDay + "; " + skin.halfDay);
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        this.mWebpTMGlasses.setGlSurfaceView(glSurfaceView);
        this.mWebpTMLeft.setGlSurfaceView(glSurfaceView);
        this.mWebpTMRight.setGlSurfaceView(glSurfaceView);
        this.mWebpTMLeftTop.setGlSurfaceView(glSurfaceView);
    }

    public void refresh() {
        if (this.glSurfaceView != null && Gdx.graphics != null) {
            Gdx.graphics.requestRendering();
        }
    }
}
