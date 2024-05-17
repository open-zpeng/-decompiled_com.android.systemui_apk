package com.badlogic.gdx.backends.android;

import com.badlogic.gdx.graphics.GL20;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
/* loaded from: classes21.dex */
public class AndroidGL20 implements GL20 {
    public static native void init();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glActiveTexture(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glAttachShader(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBindAttribLocation(int i, int i2, String str);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBindBuffer(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBindFramebuffer(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBindRenderbuffer(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBindTexture(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBlendColor(float f, float f2, float f3, float f4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBlendEquation(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBlendEquationSeparate(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBlendFunc(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBlendFuncSeparate(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBufferData(int i, int i2, Buffer buffer, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glBufferSubData(int i, int i2, int i3, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glCheckFramebufferStatus(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glClear(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glClearColor(float f, float f2, float f3, float f4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glClearDepthf(float f);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glClearStencil(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glColorMask(boolean z, boolean z2, boolean z3, boolean z4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCompileShader(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCompressedTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCompressedTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCopyTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCopyTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glCreateProgram();

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glCreateShader(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glCullFace(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteBuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteBuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteFramebuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteFramebuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteProgram(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteRenderbuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteRenderbuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteShader(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteTexture(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDeleteTextures(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDepthFunc(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDepthMask(boolean z);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDepthRangef(float f, float f2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDetachShader(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDisable(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDisableVertexAttribArray(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDrawArrays(int i, int i2, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDrawElements(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glDrawElements(int i, int i2, int i3, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glEnable(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glEnableVertexAttribArray(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glFinish();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glFlush();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glFramebufferRenderbuffer(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glFramebufferTexture2D(int i, int i2, int i3, int i4, int i5);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glFrontFace(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGenBuffer();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGenBuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGenFramebuffer();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGenFramebuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGenRenderbuffer();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGenRenderbuffers(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGenTexture();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGenTextures(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGenerateMipmap(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native String glGetActiveAttrib(int i, int i2, IntBuffer intBuffer, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native String glGetActiveUniform(int i, int i2, IntBuffer intBuffer, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetAttachedShaders(int i, int i2, Buffer buffer, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGetAttribLocation(int i, String str);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetBooleanv(int i, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetBufferParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGetError();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetFloatv(int i, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetFramebufferAttachmentParameteriv(int i, int i2, int i3, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetIntegerv(int i, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native String glGetProgramInfoLog(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetProgramiv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetRenderbufferParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native String glGetShaderInfoLog(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetShaderPrecisionFormat(int i, int i2, IntBuffer intBuffer, IntBuffer intBuffer2);

    public native void glGetShaderSource(int i, int i2, Buffer buffer, String str);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetShaderiv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native String glGetString(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetTexParameterfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetTexParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native int glGetUniformLocation(int i, String str);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetUniformfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetUniformiv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetVertexAttribPointerv(int i, int i2, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetVertexAttribfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glGetVertexAttribiv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glHint(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsBuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsEnabled(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsFramebuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsProgram(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsRenderbuffer(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsShader(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native boolean glIsTexture(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glLineWidth(float f);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glLinkProgram(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glPixelStorei(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glPolygonOffset(float f, float f2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glReadPixels(int i, int i2, int i3, int i4, int i5, int i6, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glReleaseShaderCompiler();

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glRenderbufferStorage(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glSampleCoverage(float f, boolean z);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glScissor(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glShaderBinary(int i, IntBuffer intBuffer, int i2, Buffer buffer, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glShaderSource(int i, String str);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilFunc(int i, int i2, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilFuncSeparate(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilMask(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilMaskSeparate(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilOp(int i, int i2, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glStencilOpSeparate(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexParameterf(int i, int i2, float f);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexParameterfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexParameteri(int i, int i2, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1f(int i, float f);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1fv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1fv(int i, int i2, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1i(int i, int i2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1iv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform1iv(int i, int i2, int[] iArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2f(int i, float f, float f2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2fv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2fv(int i, int i2, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2i(int i, int i2, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2iv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform2iv(int i, int i2, int[] iArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3f(int i, float f, float f2, float f3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3fv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3fv(int i, int i2, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3i(int i, int i2, int i3, int i4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3iv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform3iv(int i, int i2, int[] iArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4f(int i, float f, float f2, float f3, float f4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4fv(int i, int i2, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4fv(int i, int i2, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4i(int i, int i2, int i3, int i4, int i5);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4iv(int i, int i2, IntBuffer intBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniform4iv(int i, int i2, int[] iArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix2fv(int i, int i2, boolean z, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix2fv(int i, int i2, boolean z, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix3fv(int i, int i2, boolean z, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix3fv(int i, int i2, boolean z, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix4fv(int i, int i2, boolean z, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUniformMatrix4fv(int i, int i2, boolean z, float[] fArr, int i3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glUseProgram(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glValidateProgram(int i);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib1f(int i, float f);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib1fv(int i, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib2f(int i, float f, float f2);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib2fv(int i, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib3f(int i, float f, float f2, float f3);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib3fv(int i, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib4f(int i, float f, float f2, float f3, float f4);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttrib4fv(int i, FloatBuffer floatBuffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttribPointer(int i, int i2, int i3, boolean z, int i4, int i5);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glVertexAttribPointer(int i, int i2, int i3, boolean z, int i4, Buffer buffer);

    @Override // com.badlogic.gdx.graphics.GL20
    public native void glViewport(int i, int i2, int i3, int i4);
}
