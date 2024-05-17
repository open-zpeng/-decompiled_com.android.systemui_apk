package com.xiaopeng.xui.app.delegate;

import android.app.Activity;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xui.app.delegate.XActivityDismissCause;
import java.util.Iterator;
/* loaded from: classes25.dex */
abstract class XActivityDismissCauseGroup implements XActivityLifecycle, XActivityDismissCause.OnPauseCause, XActivityDismissCause.SpeedTimeCause, XActivityDismissCause.OutSideCause, XActivityDismissCause.BackCause {
    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void enableBackScene();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void enableOnPauseScene();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void enableOutSideScene();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void enableSpeedTimeOutScene();

    @Nullable
    abstract XActivityDismissCause.BackCause getBackSceneInspect();

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public abstract XActivityDismissCause.OnPauseCause getOnPauseSceneInspect();

    @Nullable
    abstract XActivityDismissCause.OutSideCause getOutSideSceneInspect();

    @Nullable
    abstract XActivityDismissCause.SpeedTimeCause getSpeedTimeOutSceneInspect();

    XActivityDismissCauseGroup() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XActivityDismissCauseGroup create(Activity activity, XActivityDismissCause.CallBack callBack) {
        return new XActivityDismissCauseGroupImpl(activity, callBack);
    }

    /* loaded from: classes25.dex */
    private static class XActivityDismissCauseGroupImpl extends XActivityDismissCauseGroup {
        private Activity mActivity;
        private ArraySet<XActivityLifecycle> mActivityLifeCycles = new ArraySet<>();
        private XActivityDismissCause.BackCause mBackSceneInspect;
        private XActivityDismissCause.CallBack mCallBack;
        private XActivityDismissCause.OnPauseCause mOnPauseSceneInspect;
        private XActivityDismissCause.OutSideCause mOutSideSceneInspect;
        private XActivityDismissCause.SpeedTimeCause mSpeedTimeOutSceneInspect;

        XActivityDismissCauseGroupImpl(Activity activity, XActivityDismissCause.CallBack callBack) {
            this.mActivity = activity;
            this.mCallBack = callBack;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public void enableOnPauseScene() {
            this.mOnPauseSceneInspect = XActivityDismissCause.createOnPause(this.mActivity, this.mCallBack);
            this.mActivityLifeCycles.add(this.mOnPauseSceneInspect);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public void enableSpeedTimeOutScene() {
            this.mSpeedTimeOutSceneInspect = XActivityDismissCause.createSpeedTimeOut(this.mActivity, this.mCallBack);
            this.mActivityLifeCycles.add(this.mSpeedTimeOutSceneInspect);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public void enableOutSideScene() {
            this.mOutSideSceneInspect = XActivityDismissCause.createOutSide(this.mActivity, this.mCallBack);
            this.mActivityLifeCycles.add(this.mOutSideSceneInspect);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public void enableBackScene() {
            this.mBackSceneInspect = XActivityDismissCause.createBack(this.mActivity, this.mCallBack);
            this.mActivityLifeCycles.add(this.mBackSceneInspect);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public XActivityDismissCause.OnPauseCause getOnPauseSceneInspect() {
            return this.mOnPauseSceneInspect;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public XActivityDismissCause.SpeedTimeCause getSpeedTimeOutSceneInspect() {
            return this.mSpeedTimeOutSceneInspect;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public XActivityDismissCause.OutSideCause getOutSideSceneInspect() {
            return this.mOutSideSceneInspect;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCauseGroup
        public XActivityDismissCause.BackCause getBackSceneInspect() {
            return this.mBackSceneInspect;
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onCreate(Bundle var1) {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onCreate(var1);
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onStart() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onStart();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onResume() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onResume();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onRecreate() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onRecreate();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onPause() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onPause();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onStop() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onStop();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityLifecycle
        public void onDestroy() {
            Iterator<XActivityLifecycle> it = this.mActivityLifeCycles.iterator();
            while (it.hasNext()) {
                XActivityLifecycle lifecycle = it.next();
                lifecycle.onDestroy();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.BackCause
        public void onBackPressed() {
            XActivityDismissCause.BackCause backCause = this.mBackSceneInspect;
            if (backCause != null) {
                backCause.onBackPressed();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.OnPauseCause
        public void ignoreDismissOneshot() {
            XActivityDismissCause.OnPauseCause onPauseCause = this.mOnPauseSceneInspect;
            if (onPauseCause != null) {
                onPauseCause.ignoreDismissOneshot();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.OutSideCause
        public boolean onTouchEvent(MotionEvent event) {
            XActivityDismissCause.OutSideCause outSideCause = this.mOutSideSceneInspect;
            return outSideCause != null && outSideCause.onTouchEvent(event);
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.SpeedTimeCause
        public void dispatchUserEvent() {
            XActivityDismissCause.SpeedTimeCause speedTimeCause = this.mSpeedTimeOutSceneInspect;
            if (speedTimeCause != null) {
                speedTimeCause.dispatchUserEvent();
            }
        }

        @Override // com.xiaopeng.xui.app.delegate.XActivityDismissCause.SpeedTimeCause
        public void dispatchTouchEvent() {
            XActivityDismissCause.SpeedTimeCause speedTimeCause = this.mSpeedTimeOutSceneInspect;
            if (speedTimeCause != null) {
                speedTimeCause.dispatchTouchEvent();
            }
        }

        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{ mBackSceneInspect=");
            sb.append(this.mBackSceneInspect != null ? "has" : "no");
            sb.append(", mOnPauseSceneInspect=");
            sb.append(this.mOnPauseSceneInspect != null ? "has" : "no");
            sb.append(", mOutSideSceneInspect=");
            sb.append(this.mOutSideSceneInspect != null ? "has" : "no");
            sb.append(", mSpeedTimeOutSceneInspect=");
            sb.append(this.mSpeedTimeOutSceneInspect == null ? "no" : "has");
            sb.append('}');
            return sb.toString();
        }
    }
}
