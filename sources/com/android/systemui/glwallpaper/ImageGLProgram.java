package com.android.systemui.glwallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import com.badlogic.gdx.graphics.GL20;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/* loaded from: classes21.dex */
class ImageGLProgram {
    private static final String TAG = ImageGLProgram.class.getSimpleName();
    private Context mContext;
    private int mProgramHandle;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImageGLProgram(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private int loadShaderProgram(int vertexId, int fragmentId) {
        String vertexSrc = getShaderResource(vertexId);
        String fragmentSrc = getShaderResource(fragmentId);
        int vertexHandle = getShaderHandle(GL20.GL_VERTEX_SHADER, vertexSrc);
        int fragmentHandle = getShaderHandle(GL20.GL_FRAGMENT_SHADER, fragmentSrc);
        return getProgramHandle(vertexHandle, fragmentHandle);
    }

    private String getShaderResource(int shaderId) {
        Resources res = this.mContext.getResources();
        StringBuilder code = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.openRawResource(shaderId)));
            while (true) {
                String nextLine = reader.readLine();
                if (nextLine == null) {
                    break;
                }
                code.append(nextLine);
                code.append("\n");
            }
            reader.close();
        } catch (Resources.NotFoundException | IOException ex) {
            Log.d(TAG, "Can not read the shader source", ex);
            code = null;
        }
        return code == null ? "" : code.toString();
    }

    private int getShaderHandle(int type, String src) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            String str = TAG;
            Log.d(str, "Create shader failed, type=" + type);
            return 0;
        }
        GLES20.glShaderSource(shader, src);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private int getProgramHandle(int vertexHandle, int fragmentHandle) {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.d(TAG, "Can not create OpenGL ES program");
            return 0;
        }
        GLES20.glAttachShader(program, vertexHandle);
        GLES20.glAttachShader(program, fragmentHandle);
        GLES20.glLinkProgram(program);
        return program;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean useGLProgram(int vertexResId, int fragmentResId) {
        this.mProgramHandle = loadShaderProgram(vertexResId, fragmentResId);
        GLES20.glUseProgram(this.mProgramHandle);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAttributeHandle(String name) {
        return GLES20.glGetAttribLocation(this.mProgramHandle, name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getUniformHandle(String name) {
        return GLES20.glGetUniformLocation(this.mProgramHandle, name);
    }
}
