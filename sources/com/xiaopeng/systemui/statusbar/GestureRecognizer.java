package com.xiaopeng.systemui.statusbar;

import android.os.SystemProperties;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.utils.DataLogUtils;
/* loaded from: classes24.dex */
public class GestureRecognizer {
    private static final int GESTURE_HOT_REGION_HORIZONTAL = 820;
    public static final int GESTURE_HOT_REGION_INVALID = -1;
    public static final int GESTURE_HOT_REGION_LEFT = 0;
    public static final int GESTURE_HOT_REGION_RIGHT = 1;
    private static final int GESTURE_HOT_REGION_VERTICAL = 110;
    private static final int GESTURE_JUDGE_STATUS_INIT = -1;
    private static final int GESTURE_JUDGE_STATUS_STARTED = 0;
    private static final int GESTURE_JUDGE_STATUS_SUCCESS = 1;
    private static final String TAG = "GestureRecognizer";
    private static int mGestureWorkingLength = SystemProperties.getInt("persist.systemui.gesture.length", 110);
    private IGestureRecognitionListener mGestureRecognitionListener;
    private float mTouchXStart = 0.0f;
    private float mTouchXEnd = 0.0f;
    private float mTouchYStart = 0.0f;
    private float mTouchYEnd = 0.0f;
    private int mGestureJudgeStatus = -1;

    /* loaded from: classes24.dex */
    public interface IGestureRecognitionListener {
        int getWidth();

        void performClickLeftRegion();

        void performClickRightRegion();
    }

    public GestureRecognizer(IGestureRecognitionListener gestureRecognitionListener) {
        this.mGestureRecognitionListener = gestureRecognitionListener;
    }

    public boolean inRecognizeRegion(MotionEvent ev) {
        Logger.d(TAG, "inRecognizeRegion() called with: ev = [" + ev + NavigationBarInflaterView.SIZE_MOD_END);
        float x = ev.getX();
        float y = ev.getY();
        int width = this.mGestureRecognitionListener.getWidth();
        if (((x >= 0.0f && x <= 820.0f) || (x >= width - 820 && x <= width)) && y >= 0.0f && y <= 110.0f) {
            return true;
        }
        return false;
    }

    public void start(MotionEvent ev) {
        Logger.d(TAG, "start() called with: ev = [" + ev + NavigationBarInflaterView.SIZE_MOD_END);
        this.mTouchXStart = ev.getX();
        this.mTouchYStart = ev.getY();
        this.mGestureJudgeStatus = 0;
    }

    public void end() {
        if (this.mGestureJudgeStatus != 1) {
            this.mGestureJudgeStatus = -1;
        }
    }

    public void move(MotionEvent ev) {
        if (this.mGestureJudgeStatus != 0) {
            return;
        }
        this.mTouchXEnd = ev.getX();
        this.mTouchYEnd = ev.getY();
        int gestureHotRegionType = getGestureHotRegionType(this.mTouchXStart, this.mTouchXEnd, this.mTouchYStart, this.mTouchYEnd);
        if (gestureHotRegionType != -1) {
            onGestureResult(gestureHotRegionType);
            this.mGestureJudgeStatus = 1;
        }
    }

    public boolean isRecognizing() {
        return this.mGestureJudgeStatus != -1;
    }

    private int getGestureHotRegionType(float mTouchXStart, float mTouchXEnd, float mTouchYStart, float mTouchYEnd) {
        if (mTouchYEnd - mTouchYStart >= mGestureWorkingLength) {
            if (mTouchXStart >= 0.0f && mTouchXStart <= 820.0f && mTouchXEnd >= 0.0f && mTouchXEnd <= 820.0f) {
                return 0;
            }
            int width = this.mGestureRecognitionListener.getWidth();
            Logger.d(TAG, "getGestureHotRegionType: width = " + width);
            if (mTouchXStart >= width - 820 && mTouchXStart <= width && mTouchXEnd >= width - 820 && mTouchXEnd <= width) {
                return 1;
            }
            return -1;
        }
        return -1;
    }

    private void onGestureResult(int gestureType) {
        Logger.d(TAG, "onGestureResult() called with: gestureType = [" + gestureType + NavigationBarInflaterView.SIZE_MOD_END);
        if (gestureType == 0) {
            this.mGestureRecognitionListener.performClickLeftRegion();
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.DRIVER_REGION_SLIDE_ID);
        } else if (gestureType == 1) {
            this.mGestureRecognitionListener.performClickRightRegion();
            DataLogUtils.sendDataLog(DataLogUtils.SYSTEMUI_PAGE_ID, DataLogUtils.PASSENGER_REGION_SLIDE_ID);
        }
    }
}
