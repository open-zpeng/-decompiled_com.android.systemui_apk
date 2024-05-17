package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
/* loaded from: classes24.dex */
public class VerticalWaveViewByCode extends VoiceWaveViewByCode {
    private Matrix mMatrix;

    public VerticalWaveViewByCode(Context context) {
        super(context);
        init();
    }

    public VerticalWaveViewByCode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalWaveViewByCode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mMatrix = new Matrix();
        this.mMatrix.setScale(1.0f, -1.0f);
        this.mMatrix.postTranslate(0.0f, 200.0f);
        this.mMidWaveOffsetBase = 50;
        this.mInitBaseWaveHeight = 100;
        this.mBaseWaveHeight = this.mInitBaseWaveHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.speech.ui.widget.VoiceWaveViewByCode, android.view.View
    public void onDraw(Canvas canvas) {
        canvas.concat(this.mMatrix);
        super.onDraw(canvas);
    }
}
