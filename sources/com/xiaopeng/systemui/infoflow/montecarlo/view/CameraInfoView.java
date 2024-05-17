package com.xiaopeng.systemui.infoflow.montecarlo.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.xuimanager.contextinfo.Camera;
import com.xiaopeng.xuimanager.contextinfo.CameraInfo;
import java.util.List;
/* loaded from: classes24.dex */
public class CameraInfoView extends AlphaOptimizedLinearLayout {
    private static final String TAG = CameraInfoView.class.getSimpleName();
    private final int ITEM_SIZE;
    private Camera mCameraData;
    private List<CameraInfo> mCameraInfos;
    private CameraItemView[] mCameraItemViews;
    private Paint mPaint;

    public CameraInfoView(Context context) {
        super(context);
        this.ITEM_SIZE = 3;
        init();
    }

    public CameraInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.ITEM_SIZE = 3;
        init();
    }

    private void init() {
        this.mPaint = new Paint();
        this.mPaint.setColor(getContext().getColor(R.color.colorLaneSeparateLine));
        this.mPaint.setAntiAlias(true);
        this.mCameraItemViews = new CameraItemView[3];
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < 3; i++) {
            this.mCameraItemViews[i] = (CameraItemView) getChildAt(i);
        }
    }

    public void setData(Camera camera) {
        this.mCameraData = camera;
        this.mCameraInfos = camera.getCameraInfo();
        updateItemViews();
    }

    private void updateItemViews() {
        List<CameraInfo> list = this.mCameraInfos;
        if (list == null) {
            return;
        }
        int camerInfoSize = list.size();
        Logger.d(TAG, "camerInfoSize -- " + camerInfoSize);
        int size = Math.min(camerInfoSize, 3);
        int i = 0;
        while (i < size) {
            boolean z = false;
            this.mCameraItemViews[i].setVisibility(0);
            this.mCameraItemViews[i].setData(this.mCameraInfos.get(i));
            boolean isLastItem = i == size + (-1);
            CameraItemView cameraItemView = this.mCameraItemViews[i];
            if (!isLastItem) {
                z = true;
            }
            cameraItemView.setNextViewVisiable(z);
            i++;
        }
        for (int j = size; j < 3; j++) {
            this.mCameraItemViews[j].setVisibility(8);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawSepLine(canvas);
    }

    private void drawSepLine(Canvas canvas) {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        canvas.drawLine(0.0f, height - 1, width, height, this.mPaint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.infoflow.theme.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mPaint.setColor(getResources().getColor(R.color.colorLaneSeparateLine, getContext().getTheme()));
            invalidate();
        }
    }
}
