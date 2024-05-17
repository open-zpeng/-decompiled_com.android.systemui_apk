package com.xiaopeng.xui.vui.floatinglayer;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.util.FeatureOption;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView;
import java.lang.reflect.Field;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes25.dex */
public class VuiFloatingLayer implements IVuiFloatingLayer {
    private static final String TAG = "VuiFloatingLayer";
    private Context mContext;
    private WindowManager mWindowManager;
    private SparseArray<VuiFloatingView> mVuiFloatingViewMap = new SparseArray<>();
    private SparseArray<LayerInfo> mLayerInfoMap = new SparseArray<>();
    private SparseArray<AnimationCallback> mAnimationCallbackMap = new SparseArray<>();
    private Handler mHandler = new Handler();
    private SparseArray<RunnableTouch> mRunnableTouchMap = new SparseArray<>();
    private SparseArray<RunnableTimeOut> mRunnableTimeOutMap = new SparseArray<>();

    /* loaded from: classes25.dex */
    public class LayerInfo {
        int[] location;
        int mCenterOffsetX;
        int mCenterOffsetY;
        int targetHeight;
        int targetWidth;

        public LayerInfo() {
        }

        @NonNull
        public String toString() {
            return "LayerInfo{targetWidth=" + this.targetWidth + ", targetHeight=" + this.targetHeight + ", location=" + Arrays.toString(this.location) + ", mCenterOffsetX=" + this.mCenterOffsetX + ", mCenterOffsetY=" + this.mCenterOffsetY + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VuiFloatingLayer(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = 8388659;
        mLayoutParams.type = WindowHelper.TYPE_VUI;
        mLayoutParams.packageName = this.mContext.getPackageName();
        mLayoutParams.flags = 328488;
        mLayoutParams.format = -2;
        return mLayoutParams;
    }

    private void setXpFlags(WindowManager.LayoutParams layoutParams, int screenId) {
        log("setXpFlags screenId : " + screenId);
        try {
            Field field = WindowManager.LayoutParams.class.getField("xpFlags");
            int currentXpFlags = field.getInt(layoutParams);
            if (screenId == 1) {
                currentXpFlags |= 16;
            } else if (screenId == 2) {
                currentXpFlags |= 32;
            }
            field.setInt(layoutParams, currentXpFlags);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VuiFloatingView getVuiFloatingView(final int type) {
        VuiFloatingView vuiFloatingView = this.mVuiFloatingViewMap.get(type);
        if (vuiFloatingView == null) {
            VuiFloatingView vuiFloatingView2 = new VuiFloatingView(this.mContext, type);
            this.mVuiFloatingViewMap.put(type, vuiFloatingView2);
            vuiFloatingView2.setOnTouchListener(new VuiFloatingView.OnTouchListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$UXxtbfMrTLOTVDQw-YOjhAcXvSA
                @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.OnTouchListener
                public final void onTouch(int i) {
                    VuiFloatingLayer.this.lambda$getVuiFloatingView$0$VuiFloatingLayer(type, i);
                }
            });
            return vuiFloatingView2;
        }
        return vuiFloatingView;
    }

    public /* synthetic */ void lambda$getVuiFloatingView$0$VuiFloatingLayer(int type, int type1) {
        this.mHandler.postDelayed(getRunnableTouch(type), 150L);
    }

    private AnimationCallback getAnimationCallback(int type) {
        AnimationCallback animationCallback = this.mAnimationCallbackMap.get(type);
        if (animationCallback == null) {
            AnimationCallback animationCallback2 = new AnimationCallback(type);
            this.mAnimationCallbackMap.put(type, animationCallback2);
            return animationCallback2;
        }
        return animationCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public class RunnableTouch implements Runnable {
        private int type;

        RunnableTouch(int type) {
            this.type = type;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiFloatingLayer vuiFloatingLayer = VuiFloatingLayer.this;
            vuiFloatingLayer.log("touch RunnableTouch type " + this.type);
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private RunnableTouch getRunnableTouch(int type) {
        RunnableTouch runnable = this.mRunnableTouchMap.get(type);
        if (runnable == null) {
            RunnableTouch runnable2 = new RunnableTouch(type);
            this.mRunnableTouchMap.put(type, runnable2);
            return runnable2;
        }
        return runnable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public class RunnableTimeOut implements Runnable {
        private int type;

        RunnableTimeOut(int type) {
            this.type = type;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiFloatingLayer vuiFloatingLayer = VuiFloatingLayer.this;
            vuiFloatingLayer.log("RunnableTimeOut type " + this.type);
            VuiFloatingLayer.this.getVuiFloatingView(this.type).requestNeedReLoadDrawable();
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private RunnableTimeOut getRunnableTimeOut(int type) {
        RunnableTimeOut runnable = this.mRunnableTimeOutMap.get(type);
        if (runnable == null) {
            RunnableTimeOut runnable2 = new RunnableTimeOut(type);
            this.mRunnableTimeOutMap.put(type, runnable2);
            return runnable2;
        }
        return runnable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes25.dex */
    public class AnimationCallback implements VuiFloatingView.Callback {
        private int type;

        AnimationCallback(int type) {
            this.type = type;
        }

        @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.Callback
        public void onAnimationStart(VuiFloatingView view) {
            VuiFloatingLayer.this.log("onAnimationStart ");
        }

        @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.Callback
        public void onAnimationEnd(VuiFloatingView view) {
            VuiFloatingLayer.this.log("onAnimationEnd ");
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private void setFloatingLocation(VuiFloatingView vuiFloatingView, LayerInfo info, int type) {
        if (info == null) {
            return;
        }
        WindowManager.LayoutParams mLayoutParams = createLayoutParams();
        int viewWidth = vuiFloatingView.getVisibleWidth();
        int viewHeight = vuiFloatingView.getVisibleHeight();
        mLayoutParams.width = viewWidth;
        mLayoutParams.height = viewHeight;
        int[] point = VuiFloatingLocationUtils.getLocation(type, info, viewWidth, viewHeight);
        mLayoutParams.x = point[0];
        mLayoutParams.y = point[1];
        if (FeatureOption.FO_SHARED_DISPLAY_ENABLED) {
            if (mLayoutParams.x >= 2348) {
                mLayoutParams.x -= 2400;
                setXpFlags(mLayoutParams, 2);
            } else {
                setXpFlags(mLayoutParams, 1);
            }
        }
        log_I("setFloatingLocation point : " + point[0] + " mLayoutParams.x: " + mLayoutParams.x);
        vuiFloatingView.setLocation(mLayoutParams.x, mLayoutParams.y);
        addView(vuiFloatingView, mLayoutParams, type);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void showFloatingLayer(@NonNull View targetView, final int type, int centerOffsetX, int centerOffsetY) {
        log_I("showFloatingLayer...type : " + type + " , view " + targetView);
        this.mHandler.removeCallbacks(getRunnableTouch(type));
        this.mHandler.removeCallbacks(getRunnableTimeOut(type));
        LayerInfo info = this.mLayerInfoMap.get(type);
        if (info == null) {
            info = new LayerInfo();
            this.mLayerInfoMap.put(type, info);
        }
        info.targetWidth = targetView.getMeasuredWidth();
        info.targetHeight = targetView.getMeasuredHeight();
        info.mCenterOffsetX = centerOffsetX;
        info.mCenterOffsetY = centerOffsetY;
        info.location = new int[2];
        targetView.getLocationOnScreen(info.location);
        log(info.toString());
        final VuiFloatingView view = getVuiFloatingView(type);
        final AnimationCallback animationCallback = getAnimationCallback(type);
        view.unRegisterAnimationCallback();
        view.postDelayed(new Runnable() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$PZ_K1OqtUY7adEnl6_SElfFSV0c
            @Override // java.lang.Runnable
            public final void run() {
                VuiFloatingLayer.this.lambda$showFloatingLayer$1$VuiFloatingLayer(type, view, animationCallback);
            }
        }, 500L);
        removeView(type);
        view.prepare();
        setFloatingLocation(view, info, type);
        startAnimation(view);
    }

    public /* synthetic */ void lambda$showFloatingLayer$1$VuiFloatingLayer(int type, VuiFloatingView view, AnimationCallback animationCallback) {
        log("registerAnimationCallback...type : " + type);
        view.registerAnimationCallback(animationCallback);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void hideFloatingLayer(int type) {
        log("hideFloatingLayer...type : " + type);
        VuiFloatingView view = getVuiFloatingView(type);
        stopAnimation(view);
        removeView(type);
    }

    private void addView(View vuiFloatingView, WindowManager.LayoutParams layoutParams, int type) {
        if (vuiFloatingView.getParent() == null) {
            this.mWindowManager.addView(vuiFloatingView, layoutParams);
            log_I("add to window x : " + layoutParams.x + ", y :" + layoutParams.y + " , w : " + layoutParams.width + " ,h : " + layoutParams.height);
        } else {
            this.mWindowManager.updateViewLayout(vuiFloatingView, layoutParams);
            log_I("update to window x : " + layoutParams.x + ", y :" + layoutParams.y + " , w : " + layoutParams.width + " ,h : " + layoutParams.height);
        }
        this.mHandler.postDelayed(getRunnableTimeOut(type), VuiImageDecoderUtils.getAnimateTimeOut(type));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeView(int type) {
        VuiFloatingView view = getVuiFloatingView(type);
        stopAnimation(view);
        if (view != null && view.getParent() != null) {
            this.mWindowManager.removeView(view);
            this.mHandler.removeCallbacks(getRunnableTimeOut(type));
            log_I("remove window..type : " + type);
            return;
        }
        log("view is null or view all in window");
    }

    private void startAnimation(VuiFloatingView view) {
        log("startAnimation...");
        if (view != null) {
            view.start();
        } else {
            log("view is null");
        }
    }

    private void stopAnimation(VuiFloatingView view) {
        if (view != null) {
            view.stop();
        } else {
            log("view is null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String msg) {
        XLogUtils.d(TAG, msg);
    }

    private void log_I(String msg) {
        XLogUtils.i(TAG, msg);
    }
}
