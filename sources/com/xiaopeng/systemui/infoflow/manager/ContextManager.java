package com.xiaopeng.systemui.infoflow.manager;

import android.content.Context;
import android.util.Log;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.contextinfo.Camera;
import com.xiaopeng.xuimanager.contextinfo.CameraInterval;
import com.xiaopeng.xuimanager.contextinfo.ContextInfoManager;
import com.xiaopeng.xuimanager.contextinfo.Cross;
import com.xiaopeng.xuimanager.contextinfo.HomeCompanyRouteInfo;
import com.xiaopeng.xuimanager.contextinfo.Lane;
import com.xiaopeng.xuimanager.contextinfo.Maneuver;
import com.xiaopeng.xuimanager.contextinfo.Navi;
import com.xiaopeng.xuimanager.contextinfo.RemainInfo;
import com.xiaopeng.xuimanager.contextinfo.Sapa;
/* loaded from: classes24.dex */
public class ContextManager implements ContextInfoManager.ContextNaviInfoEventListener {
    private static final String TAG = ContextManager.class.getSimpleName();
    private static volatile ContextManager mInstance;
    private Context mContext;
    private ContextInfoManager mContextInfoManager;
    private Lane mCurrentLane;
    private Maneuver mCurrentManeuver;
    private Navi mCurrentNavi;
    private int mCurrentNaviMode = 0;
    private RemainInfo mCurrentRemainInfo;
    private OnAngleChangeListener mOnAngleChangeListener;
    private OnCruiseDataChangedListener mOnCruiseDataChangedListener;
    private OnNaviDataChangeListener mOnNaviDataChangeListener;
    private OnNaviModeChangedListener mOnNaviModeChangeListener;

    /* loaded from: classes24.dex */
    public interface OnAngleChangeListener {
        void onAccelerationEvent(float f);

        void onAngularVelocityEvent(float f);
    }

    /* loaded from: classes24.dex */
    public interface OnCruiseDataChangedListener {
        void onHomeCompanyRouteInfo(HomeCompanyRouteInfo homeCompanyRouteInfo);
    }

    /* loaded from: classes24.dex */
    public interface OnNaviDataChangeListener {
        void onCameraEvent(Camera camera);

        void onCameraIntervalEvent(CameraInterval cameraInterval);

        void onCrossEvent(Cross cross);

        void onLaneEvent(Lane lane);

        void onManeuverEvent(Maneuver maneuver);

        void onNaviEvent(Navi navi);

        void onRemainInfoEvent(RemainInfo remainInfo);

        void onSapaEvent(Sapa sapa);
    }

    /* loaded from: classes24.dex */
    public interface OnNaviModeChangedListener {
        void onNaviModeChanged(int i);
    }

    private ContextManager() {
    }

    public static ContextManager getInstance() {
        if (mInstance == null) {
            synchronized (ContextManager.class) {
                if (mInstance == null) {
                    mInstance = new ContextManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void setContextInfoManager(ContextInfoManager contextInfoManager) {
        this.mContextInfoManager = contextInfoManager;
        registeListener();
    }

    private void registeListener() {
        String str = TAG;
        Logger.d(str, "registerListener : " + this.mContextInfoManager);
        ContextInfoManager contextInfoManager = this.mContextInfoManager;
        if (contextInfoManager != null) {
            try {
                contextInfoManager.registerListener(this);
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onErrorEvent(int i, int i1) {
    }

    public void onManeuverEvent(Maneuver maneuver, int msgType) {
        if (msgType != 0 && maneuver != null) {
            this.mCurrentManeuver = maneuver;
        }
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onManeuverEvent(maneuver);
        }
    }

    public void onRemainInfoEvent(RemainInfo remainInfo, int msgType) {
        if (msgType != 0) {
            this.mCurrentRemainInfo = remainInfo;
        }
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onRemainInfoEvent(remainInfo);
        }
    }

    public void onHomeCompanyRouteInfo(HomeCompanyRouteInfo info, int msgType) {
        String str = TAG;
        Logger.d(str, "routeInfoType : " + info.getRouteInfoType() + " distance : " + info.getDistance() + " msgType : " + msgType);
        notifyNaviMode(msgType);
        OnCruiseDataChangedListener onCruiseDataChangedListener = this.mOnCruiseDataChangedListener;
        if (onCruiseDataChangedListener != null) {
            onCruiseDataChangedListener.onHomeCompanyRouteInfo(info);
        }
    }

    public void onNaviEvent(Navi navi, int msgType) {
        String str = TAG;
        Logger.d(str, "onNaviEvent : " + msgType + NavigationBarInflaterView.KEY_IMAGE_DELIM + navi.getCurRouteName());
        if (msgType != 0) {
            this.mCurrentNavi = navi;
        }
        notifyNaviMode(msgType);
        String str2 = TAG;
        Logger.d(str2, "onNaviEvent : mOnNaviDataChangeListener = " + this.mOnNaviDataChangeListener);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onNaviEvent(navi);
        } else {
            Logger.d(TAG, "onNaviEvent OnNaviDataChangeListener is null");
        }
    }

    public void onLaneEvent(Lane lane, int msgType) {
        if (msgType != 0) {
            this.mCurrentLane = lane;
        }
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onLaneEvent(lane);
        }
    }

    public void onCameraEvent(Camera camera, int msgType) {
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onCameraEvent(camera);
        }
    }

    public void onCameraIntervalEvent(CameraInterval cameraInterval, int msgType) {
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onCameraIntervalEvent(cameraInterval);
        }
    }

    public void onSapaEvent(Sapa sapa, int msgType) {
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onSapaEvent(sapa);
        }
    }

    public void onCrossEvent(Cross cross, int msgType) {
        notifyNaviMode(msgType);
        OnNaviDataChangeListener onNaviDataChangeListener = this.mOnNaviDataChangeListener;
        if (onNaviDataChangeListener != null) {
            onNaviDataChangeListener.onCrossEvent(cross);
        }
    }

    public void onMsgEvent(int msgType) {
        notifyNaviMode(msgType);
    }

    public void onNavigationEnable(boolean enable) {
        Logger.d(TAG, "onNavigationEnable");
    }

    public void setOnNaviModeChangeListener(OnNaviModeChangedListener listener) {
        this.mOnNaviModeChangeListener = listener;
        Log.i(TAG, "sync the navi mode status");
        this.mOnNaviModeChangeListener.onNaviModeChanged(this.mCurrentNaviMode);
    }

    public void setOnNaviDataChangeListener(OnNaviDataChangeListener listener) {
        this.mOnNaviDataChangeListener = listener;
    }

    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        this.mOnAngleChangeListener = listener;
    }

    public void setOnCruiseDataChangedListener(OnCruiseDataChangedListener listener) {
        this.mOnCruiseDataChangedListener = listener;
    }

    private void notifyNaviMode(int type) {
        if (type != this.mCurrentNaviMode) {
            this.mCurrentNaviMode = type;
            resetNaviInfo();
            OnNaviModeChangedListener onNaviModeChangedListener = this.mOnNaviModeChangeListener;
            if (onNaviModeChangedListener != null) {
                onNaviModeChangedListener.onNaviModeChanged(this.mCurrentNaviMode);
            } else {
                Log.w(TAG, "mOnNaviModeChangeListener null");
            }
        }
    }

    private void resetNaviInfo() {
        this.mCurrentLane = null;
    }

    public void setNavigationEnable(final boolean enable) {
        ThreadUtils.execute(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.manager.ContextManager.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ContextManager.this.mContextInfoManager.setNavigationEnable(enable);
                } catch (XUIServiceNotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Navi getCurrentNavi() {
        return this.mCurrentNavi;
    }

    public Lane getCurrentLane() {
        return this.mCurrentLane;
    }

    public Maneuver getCurrentManeuver() {
        return this.mCurrentManeuver;
    }

    public RemainInfo getCurrentRemainInfo() {
        return this.mCurrentRemainInfo;
    }
}
