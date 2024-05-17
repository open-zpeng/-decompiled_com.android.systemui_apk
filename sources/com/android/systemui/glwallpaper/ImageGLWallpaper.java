package com.android.systemui.glwallpaper;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import com.badlogic.gdx.graphics.GL20;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
/* loaded from: classes21.dex */
class ImageGLWallpaper {
    static final String A_POSITION = "aPosition";
    static final String A_TEXTURE_COORDINATES = "aTextureCoordinates";
    private static final int BYTES_PER_FLOAT = 4;
    private static final int HANDLE_UNDEFINED = -1;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    static final String U_AOD2OPACITY = "uAod2Opacity";
    static final String U_PER85 = "uPer85";
    static final String U_REVEAL = "uReveal";
    static final String U_TEXTURE = "uTexture";
    private int mAttrPosition;
    private int mAttrTextureCoordinates;
    private float[] mCurrentTexCoordinate;
    private final ImageGLProgram mProgram;
    private final FloatBuffer mTextureBuffer;
    private int mTextureId;
    private int mUniAod2Opacity;
    private int mUniPer85;
    private int mUniReveal;
    private int mUniTexture;
    private final FloatBuffer mVertexBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private static final String TAG = ImageGLWallpaper.class.getSimpleName();
    private static final float[] VERTICES = {-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f};
    private static final float[] TEXTURES = {0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImageGLWallpaper(ImageGLProgram program) {
        this.mProgram = program;
        this.mVertexBuffer.put(VERTICES);
        this.mVertexBuffer.position(0);
        this.mTextureBuffer = ByteBuffer.allocateDirect(TEXTURES.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTextureBuffer.put(TEXTURES);
        this.mTextureBuffer.position(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setup(Bitmap bitmap) {
        setupAttributes();
        setupUniforms();
        setupTexture(bitmap);
    }

    private void setupAttributes() {
        this.mAttrPosition = this.mProgram.getAttributeHandle(A_POSITION);
        this.mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(this.mAttrPosition, 2, (int) GL20.GL_FLOAT, false, 0, (Buffer) this.mVertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mAttrPosition);
        this.mAttrTextureCoordinates = this.mProgram.getAttributeHandle(A_TEXTURE_COORDINATES);
        this.mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(this.mAttrTextureCoordinates, 2, (int) GL20.GL_FLOAT, false, 0, (Buffer) this.mTextureBuffer);
        GLES20.glEnableVertexAttribArray(this.mAttrTextureCoordinates);
    }

    private void setupUniforms() {
        this.mUniAod2Opacity = this.mProgram.getUniformHandle(U_AOD2OPACITY);
        this.mUniPer85 = this.mProgram.getUniformHandle(U_PER85);
        this.mUniReveal = this.mProgram.getUniformHandle(U_REVEAL);
        this.mUniTexture = this.mProgram.getUniformHandle(U_TEXTURE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int getHandle(String name) {
        boolean z;
        switch (name.hashCode()) {
            case -2002784538:
                if (name.equals(U_TEXTURE)) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case -1971276870:
                if (name.equals(U_AOD2OPACITY)) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case -1091770206:
                if (name.equals(U_REVEAL)) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case -868354715:
                if (name.equals(U_PER85)) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 17245217:
                if (name.equals(A_TEXTURE_COORDINATES)) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 1583025322:
                if (name.equals(A_POSITION)) {
                    z = false;
                    break;
                }
                z = true;
                break;
            default:
                z = true;
                break;
        }
        if (z) {
            if (!z) {
                if (!z) {
                    if (!z) {
                        if (!z) {
                            if (!z) {
                                return -1;
                            }
                            return this.mUniTexture;
                        }
                        return this.mUniReveal;
                    }
                    return this.mUniPer85;
                }
                return this.mUniAod2Opacity;
            }
            return this.mAttrTextureCoordinates;
        }
        return this.mAttrPosition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void draw() {
        GLES20.glDrawArrays(4, 0, VERTICES.length / 2);
    }

    private void setupTexture(Bitmap bitmap) {
        int[] tids = new int[1];
        if (bitmap != null && !bitmap.isRecycled()) {
            GLES20.glGenTextures(1, tids, 0);
            if (tids[0] == 0) {
                Log.w(TAG, "setupTexture: glGenTextures() failed");
                return;
            }
            try {
                GLES20.glBindTexture(GL20.GL_TEXTURE_2D, tids[0]);
                GLUtils.texImage2D(GL20.GL_TEXTURE_2D, 0, bitmap, 0);
                GLES20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
                GLES20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
                this.mTextureId = tids[0];
                return;
            } catch (IllegalArgumentException e) {
                String str = TAG;
                Log.w(str, "Failed uploading texture: " + e.getLocalizedMessage());
                return;
            }
        }
        Log.w(TAG, "setupTexture: invalid bitmap");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void useTexture() {
        GLES20.glActiveTexture(GL20.GL_TEXTURE0);
        GLES20.glBindTexture(GL20.GL_TEXTURE_2D, this.mTextureId);
        GLES20.glUniform1i(this.mUniTexture, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void adjustTextureCoordinates(Rect surface, Rect scissor, float xOffset, float yOffset) {
        this.mCurrentTexCoordinate = (float[]) TEXTURES.clone();
        if (surface == null || scissor == null) {
            this.mTextureBuffer.put(this.mCurrentTexCoordinate);
            this.mTextureBuffer.position(0);
            return;
        }
        int surfaceWidth = surface.width();
        int surfaceHeight = surface.height();
        int scissorWidth = scissor.width();
        int scissorHeight = scissor.height();
        if (surfaceWidth > scissorWidth) {
            float pixelS = Math.round((surfaceWidth - scissorWidth) * xOffset);
            float coordinateS = pixelS / surfaceWidth;
            float surfacePercentageW = scissorWidth / surfaceWidth;
            if (surfaceHeight < scissorHeight) {
                surfacePercentageW *= surfaceHeight / scissorHeight;
            }
            float s = coordinateS + surfacePercentageW > 1.0f ? 1.0f - surfacePercentageW : coordinateS;
            int i = 0;
            while (true) {
                float[] fArr = this.mCurrentTexCoordinate;
                if (i >= fArr.length) {
                    break;
                }
                if (i == 2 || i == 4 || i == 6) {
                    this.mCurrentTexCoordinate[i] = Math.min(1.0f, s + surfacePercentageW);
                } else {
                    fArr[i] = s;
                }
                i += 2;
            }
        }
        if (surfaceHeight > scissorHeight) {
            float pixelT = Math.round((surfaceHeight - scissorHeight) * yOffset);
            float coordinateT = pixelT / surfaceHeight;
            float surfacePercentageH = scissorHeight / surfaceHeight;
            if (surfaceWidth < scissorWidth) {
                surfacePercentageH *= surfaceWidth / scissorWidth;
            }
            float t = coordinateT + surfacePercentageH > 1.0f ? 1.0f - surfacePercentageH : coordinateT;
            int i2 = 1;
            while (true) {
                float[] fArr2 = this.mCurrentTexCoordinate;
                if (i2 >= fArr2.length) {
                    break;
                }
                if (i2 == 1 || i2 == 3 || i2 == 11) {
                    this.mCurrentTexCoordinate[i2] = Math.min(1.0f, t + surfacePercentageH);
                } else {
                    fArr2[i2] = t;
                }
                i2 += 2;
            }
        }
        this.mTextureBuffer.put(this.mCurrentTexCoordinate);
        this.mTextureBuffer.position(0);
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter out, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        if (this.mCurrentTexCoordinate != null) {
            int i = 0;
            while (true) {
                float[] fArr = this.mCurrentTexCoordinate;
                if (i >= fArr.length) {
                    break;
                }
                sb.append(fArr[i]);
                sb.append(',');
                if (i == this.mCurrentTexCoordinate.length - 1) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                i++;
            }
        }
        sb.append('}');
        out.print(prefix);
        out.print("mTexCoordinates=");
        out.println(sb.toString());
    }
}
