package com.android.systemui.assist.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.util.PathParser;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.systemui.R;
import com.android.systemui.assist.ui.CornerPathRenderer;
/* loaded from: classes21.dex */
public final class PathSpecCornerPathRenderer extends CornerPathRenderer {
    private static final String TAG = "PathSpecCornerPathRenderer";
    private final int mBottomCornerRadius;
    private final int mHeight;
    private final float mPathScale;
    private final Path mRoundedPath;
    private final int mTopCornerRadius;
    private final int mWidth;
    private final Path mPath = new Path();
    private final Matrix mMatrix = new Matrix();

    public PathSpecCornerPathRenderer(Context context) {
        this.mWidth = DisplayUtils.getWidth(context);
        this.mHeight = DisplayUtils.getHeight(context);
        this.mBottomCornerRadius = DisplayUtils.getCornerRadiusBottom(context);
        this.mTopCornerRadius = DisplayUtils.getCornerRadiusTop(context);
        String pathData = context.getResources().getString(R.string.config_rounded_mask);
        Path path = PathParser.createPathFromPathData(pathData);
        if (path == null) {
            Log.e(TAG, "No rounded corner path found!");
            this.mRoundedPath = new Path();
        } else {
            this.mRoundedPath = path;
        }
        RectF bounds = new RectF();
        this.mRoundedPath.computeBounds(bounds, true);
        this.mPathScale = Math.min(Math.abs(bounds.right - bounds.left), Math.abs(bounds.top - bounds.bottom));
    }

    @Override // com.android.systemui.assist.ui.CornerPathRenderer
    public Path getCornerPath(CornerPathRenderer.Corner corner) {
        int cornerRadius;
        int rotateDegrees;
        int translateX;
        int translateY;
        if (this.mRoundedPath.isEmpty()) {
            return this.mRoundedPath;
        }
        int i = AnonymousClass1.$SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[corner.ordinal()];
        if (i == 1) {
            cornerRadius = this.mTopCornerRadius;
            rotateDegrees = 0;
            translateX = 0;
            translateY = 0;
        } else if (i == 2) {
            cornerRadius = this.mTopCornerRadius;
            rotateDegrees = 90;
            translateX = this.mWidth;
            translateY = 0;
        } else if (i == 3) {
            cornerRadius = this.mBottomCornerRadius;
            rotateDegrees = Opcodes.GETFIELD;
            translateX = this.mWidth;
            translateY = this.mHeight;
        } else {
            cornerRadius = this.mBottomCornerRadius;
            rotateDegrees = 270;
            translateX = 0;
            translateY = this.mHeight;
        }
        this.mPath.reset();
        this.mMatrix.reset();
        this.mPath.addPath(this.mRoundedPath);
        Matrix matrix = this.mMatrix;
        float f = this.mPathScale;
        matrix.preScale(cornerRadius / f, cornerRadius / f);
        this.mMatrix.postRotate(rotateDegrees);
        this.mMatrix.postTranslate(translateX, translateY);
        this.mPath.transform(this.mMatrix);
        return this.mPath;
    }

    /* renamed from: com.android.systemui.assist.ui.PathSpecCornerPathRenderer$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner = new int[CornerPathRenderer.Corner.values().length];

        static {
            try {
                $SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[CornerPathRenderer.Corner.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[CornerPathRenderer.Corner.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[CornerPathRenderer.Corner.BOTTOM_RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$systemui$assist$ui$CornerPathRenderer$Corner[CornerPathRenderer.Corner.BOTTOM_LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }
}
