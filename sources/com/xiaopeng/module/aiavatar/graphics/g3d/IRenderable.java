package com.xiaopeng.module.aiavatar.graphics.g3d;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
/* loaded from: classes23.dex */
public interface IRenderable {
    void create();

    void onloaded();

    void release();

    void render(float f);

    void render(ModelBatch modelBatch, Array<ModelInstance> array);
}
