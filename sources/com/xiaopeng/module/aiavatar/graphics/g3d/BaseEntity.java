package com.xiaopeng.module.aiavatar.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.xiaopeng.module.aiavatar.graphics.animation.Animation;
import com.xiaopeng.module.aiavatar.graphics.animation.Transformation;
/* loaded from: classes23.dex */
public abstract class BaseEntity implements Disposable {
    protected static Transformation sTransformation = new Transformation();
    private Animation mCurrentAnimation;
    private boolean mLocalTransformDirty;
    private Transformation mLocalTransformation;
    protected ModelInstance mModelInstance;
    private String mName;
    protected float mX;
    protected float mY;
    protected float mZ;
    protected boolean mVisiable = true;
    protected float mScaleX = 1.0f;
    protected float mScaleY = 1.0f;
    protected float mScaleZ = 1.0f;
    protected float mRotateAngle = 0.0f;
    protected Vector3 mRotateAxis = new Vector3();
    protected Vector3 mCenter = new Vector3();

    public void setCenter(Vector3 center) {
        this.mCenter = center;
    }

    public BaseEntity(ModelInstance model, String name) {
        this.mModelInstance = model;
        this.mName = name;
    }

    public ModelInstance getModelInstance() {
        return this.mModelInstance;
    }

    public String getName() {
        return this.mName;
    }

    public boolean isVisiable() {
        return this.mVisiable;
    }

    public void setVisiable(boolean visiable) {
        this.mVisiable = visiable;
    }

    public float getX() {
        return this.mX;
    }

    public float getY() {
        return this.mY;
    }

    public float getZ() {
        return this.mZ;
    }

    public void setX(float pX) {
        this.mX = pX;
        this.mLocalTransformDirty = true;
    }

    public void setY(float pY) {
        this.mY = pY;
        this.mLocalTransformDirty = true;
    }

    public void setZ(float pZ) {
        this.mZ = pZ;
        this.mLocalTransformDirty = true;
    }

    public void setPosition(float pX, float pY, float pZ) {
        this.mX = pX;
        this.mY = pY;
        this.mZ = pZ;
        this.mLocalTransformDirty = true;
    }

    public float getRotateAngle() {
        return this.mRotateAngle;
    }

    public void setRotation(float angle) {
        this.mRotateAngle = angle;
        this.mLocalTransformDirty = true;
    }

    public void setRotationAxis(float x, float y, float z) {
        this.mRotateAxis.set(x, y, z);
        this.mLocalTransformDirty = true;
    }

    public void setRotationAxle(Vector3 axle) {
        this.mRotateAxis.set(axle);
        this.mLocalTransformDirty = true;
    }

    public Vector3 getRotationAxle(Vector3 axle) {
        axle.set(this.mRotateAxis);
        return axle;
    }

    public Transformation getLocalTransformation() {
        if (this.mLocalTransformation == null) {
            this.mLocalTransformation = new Transformation();
        }
        Transformation localTransformation = this.mLocalTransformation;
        if (this.mLocalTransformDirty) {
            this.mLocalTransformation.clear();
            localTransformation.getMatrix().translate(this.mX, this.mY, this.mZ);
            if (this.mScaleX != 1.0f || this.mScaleY != 1.0f || this.mScaleZ != 1.0f) {
                localTransformation.getMatrix().scale(this.mScaleX, this.mScaleY, this.mScaleZ);
            }
            if (this.mRotateAngle != 0.0f) {
                localTransformation.getMatrix().rotate(this.mRotateAxis, this.mRotateAngle);
            }
            this.mLocalTransformDirty = false;
        }
        return localTransformation;
    }

    public void setAnimation(Animation animation) {
        this.mCurrentAnimation = animation;
        if (animation != null) {
            animation.reset();
        }
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(-1L);
        setAnimation(animation);
    }

    public void clearAnimation() {
        Animation animation = this.mCurrentAnimation;
        if (animation != null) {
            animation.detach();
        }
        this.mCurrentAnimation = null;
    }

    public Animation getAnimation() {
        return this.mCurrentAnimation;
    }

    public void update(float deltaTime) {
    }
}
