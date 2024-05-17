package com.badlogic.gdx.graphics.g3d.utils.shapebuilders;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ShortArray;
/* loaded from: classes21.dex */
public class SphereShapeBuilder extends BaseShapeBuilder {
    private static final ShortArray tmpIndices = new ShortArray();
    private static final Matrix3 normalTransform = new Matrix3();

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV) {
        build(builder, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    @Deprecated
    public static void build(MeshPartBuilder builder, Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV) {
        build(builder, transform, width, height, depth, divisionsU, divisionsV, 0.0f, 360.0f, 0.0f, 180.0f);
    }

    public static void build(MeshPartBuilder builder, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        build(builder, matTmp1.idt(), width, height, depth, divisionsU, divisionsV, angleUFrom, angleUTo, angleVFrom, angleVTo);
    }

    @Deprecated
    public static void build(MeshPartBuilder builder, Matrix4 transform, float width, float height, float depth, int divisionsU, int divisionsV, float angleUFrom, float angleUTo, float angleVFrom, float angleVTo) {
        float v;
        float h;
        int i = divisionsU;
        int i2 = divisionsV;
        float hw = width * 0.5f;
        float hh = height * 0.5f;
        float hd = 0.5f * depth;
        float auo = angleUFrom * 0.017453292f;
        float stepU = ((angleUTo - angleUFrom) * 0.017453292f) / i;
        float avo = angleVFrom * 0.017453292f;
        float u = ((angleVTo - angleVFrom) * 0.017453292f) / i2;
        float us = 1.0f / i;
        float vs = 1.0f / i2;
        MeshPartBuilder.VertexInfo curr1 = vertTmp3.set(null, null, null, null);
        curr1.hasNormal = true;
        curr1.hasPosition = true;
        curr1.hasUV = true;
        normalTransform.set(transform);
        int s = i + 3;
        tmpIndices.clear();
        tmpIndices.ensureCapacity(i * 2);
        tmpIndices.size = s;
        int tempOffset = 0;
        builder.ensureVertices((i2 + 1) * (i + 1));
        builder.ensureRectangleIndices(i);
        int iv = 0;
        while (iv <= i2) {
            int tempOffset2 = tempOffset;
            float angleV = avo + (iv * u);
            float v2 = iv * vs;
            float t = MathUtils.sin(angleV);
            float h2 = MathUtils.cos(angleV) * hh;
            float hh2 = hh;
            int iu = 0;
            float avo2 = avo;
            int tempOffset3 = tempOffset2;
            while (iu <= i) {
                float stepV = u;
                float stepV2 = iu;
                float angleU = auo + (stepV2 * stepU);
                float u2 = 1.0f - (iu * us);
                float us2 = us;
                float hw2 = hw;
                float hd2 = hd;
                curr1.position.set(MathUtils.cos(angleU) * hw * t, h2, MathUtils.sin(angleU) * hd * t);
                curr1.normal.set(curr1.position).mul(normalTransform).nor();
                curr1.position.mul(transform);
                curr1.uv.set(u2, v2);
                tmpIndices.set(tempOffset3, builder.vertex(curr1));
                int o = tempOffset3 + s;
                if (iv <= 0 || iu <= 0) {
                    v = v2;
                    h = h2;
                } else {
                    v = v2;
                    h = h2;
                    builder.rect(tmpIndices.get(tempOffset3), tmpIndices.get((o - 1) % s), tmpIndices.get((o - (i + 2)) % s), tmpIndices.get((o - (i + 1)) % s));
                }
                tempOffset3 = (tempOffset3 + 1) % tmpIndices.size;
                iu++;
                i = divisionsU;
                u = stepV;
                us = us2;
                hw = hw2;
                hd = hd2;
                v2 = v;
                h2 = h;
            }
            iv++;
            i = divisionsU;
            i2 = divisionsV;
            tempOffset = tempOffset3;
            avo = avo2;
            hh = hh2;
        }
    }
}
