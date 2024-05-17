package com.xiaopeng.systemui.carmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.slice.core.SliceHints;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.infoflow.manager.ContextManager;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.qs.XpTilesConfig;
import com.xiaopeng.xuimanager.XUIManager;
import com.xiaopeng.xuimanager.XUIServiceNotConnectedException;
import com.xiaopeng.xuimanager.ambientlight.AmbientLightManager;
import com.xiaopeng.xuimanager.contextinfo.ContextInfoManager;
import com.xiaopeng.xuimanager.makeuplight.MakeupLightManager;
import com.xiaopeng.xuimanager.userscenario.UserScenarioManager;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class XuiClientWrapper implements MakeupLightManager.EventListener {
    public static final int MIC_POWER_STATUS_OFF = 0;
    public static final int MIC_POWER_STATUS_ON = 1;
    public static final int MIC_STATUS_DONGLE_OFF = 4;
    public static final int MIC_STATUS_DONGLE_ON = 3;
    public static final int MIC_STATUS_POWER_OFF = 6;
    public static final int MIC_STATUS_POWER_ON = 5;
    private static final String TAG = XuiClientWrapper.class.getSimpleName();
    private AmbientLightManager mAtlManager;
    private ContextInfoManager mCtiManager;
    private MakeupLightManager mMakeupLightManager;
    private UserScenarioManager mUserScenarioManager;
    private XUIManager mXuiManager;
    private Handler mHandler = new Handler();
    private boolean mKaraokeManagerInited = false;
    private List<MakeupLightListener> mMakeupLightListenerList = new ArrayList();
    private final ServiceConnection mXuiConnectionCb = new ServiceConnection() { // from class: com.xiaopeng.systemui.carmanager.XuiClientWrapper.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d(XuiClientWrapper.TAG, "onXuiServiceConnected");
            XuiClientWrapper.this.initXuiManagers();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            Logger.d(XuiClientWrapper.TAG, "onXuiServiceDisconnected");
            XuiClientWrapper.this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.XuiClientWrapper.1.1
                @Override // java.lang.Runnable
                public void run() {
                    XuiClientWrapper.this.checkToReconnectXui();
                }
            }, OsdController.TN.DURATION_TIMEOUT_SHORT);
        }
    };

    /* loaded from: classes24.dex */
    public interface MakeupLightListener {
        void onMakeupLightStatusChanged(boolean z);
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final XuiClientWrapper sInstance = new XuiClientWrapper();

        private SingleHolder() {
        }
    }

    public static XuiClientWrapper getInstance() {
        return SingleHolder.sInstance;
    }

    public void addMakeupLightListener(MakeupLightListener makeupLightListener) {
        if (!this.mMakeupLightListenerList.contains(makeupLightListener)) {
            this.mMakeupLightListenerList.add(makeupLightListener);
        }
    }

    public void connectToXui(Context context) {
        this.mXuiManager = XUIManager.createXUIManager(context, this.mXuiConnectionCb);
        Logger.d(TAG, "Start to connect XUI service");
        checkToReconnectXui();
    }

    public boolean isMakeupLightOn() {
        MakeupLightManager makeupLightManager = this.mMakeupLightManager;
        if (makeupLightManager != null) {
            try {
                return makeupLightManager.getMakeupAvailable() == 1;
            } catch (XUIServiceNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkToReconnectXui() {
        if (isXuiServiceDisconnected()) {
            Logger.d(TAG, "Start to reconnect XUI service");
            this.mXuiManager.connect();
            this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.XuiClientWrapper.2
                @Override // java.lang.Runnable
                public void run() {
                    XuiClientWrapper.this.checkToReconnectXui();
                }
            }, OsdController.TN.DURATION_TIMEOUT_SHORT);
        }
    }

    private boolean isXuiServiceDisconnected() {
        return (this.mXuiManager.isConnecting() || this.mXuiManager.isConnected()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initXuiManagers() {
        try {
            this.mAtlManager = (AmbientLightManager) this.mXuiManager.getXUIServiceManager("ambientlight");
            this.mCtiManager = (ContextInfoManager) this.mXuiManager.getXUIServiceManager("contextinfo");
            this.mUserScenarioManager = (UserScenarioManager) this.mXuiManager.getXUIServiceManager("userscenario");
            this.mMakeupLightManager = (MakeupLightManager) this.mXuiManager.getXUIServiceManager("makeuplight");
            if (this.mMakeupLightManager != null) {
                this.mMakeupLightManager.registerListener(this);
            }
            ContextManager.getInstance().setContextInfoManager(this.mCtiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AmbientLightManager getAtlManager() {
        return this.mAtlManager;
    }

    public ContextInfoManager getCtiManager() {
        return this.mCtiManager;
    }

    public void startMeditationMode() {
        startMeditationMode(0);
    }

    public void startMeditationMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("meditation_mode", SliceHints.HINT_ACTIVITY, obj.toString());
            return;
        }
        Logger.e(TAG, "startMeditationMode : mUserScenarioManager is null");
    }

    public void startCleanMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("cleaning_mode", SliceHints.HINT_ACTIVITY, obj.toString());
            return;
        }
        Logger.e(TAG, "startCleanMode : mUserScenarioManager is null");
    }

    public void startSpaceCapsuleMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("spacecapsule_mode", SliceHints.HINT_ACTIVITY);
            return;
        }
        Logger.e(TAG, "startSpaceCapsuleMode : mUserScenarioManager is null");
    }

    public void startSleepMode() {
        startSleepMode(0);
    }

    public void startSleepMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("spacecapsule_mode_sleep", SliceHints.HINT_ACTIVITY);
            return;
        }
        Logger.e(TAG, "startSleepMode : mUserScenarioManager is null");
    }

    public void startWaitingMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario(XpTilesConfig.WAITING_MODE, SliceHints.HINT_ACTIVITY);
            return;
        }
        Logger.e(TAG, "startWaitingMode : mUserScenarioManager is null");
    }

    public void startMovieMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("spacecapsule_mode_movie", SliceHints.HINT_ACTIVITY);
            return;
        }
        Logger.e(TAG, "startMovieMode : mUserScenarioManager is null");
    }

    public void startMakeupMode() {
        startMakeupMode(0);
    }

    public void startMakeupMode(int screenId) {
        if (this.mUserScenarioManager != null) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("screenId", screenId);
            } catch (JSONException e) {
                String str = TAG;
                Log.w(str, "enter e=" + e);
            }
            this.mUserScenarioManager.enterUserScenario("cosmetic_space_mode", SliceHints.HINT_ACTIVITY);
            return;
        }
        Logger.e(TAG, "startMakeupMode : mUserScenarioManager is null");
    }

    public void onAvailable(final boolean status) {
        ThreadUtils.runOnMainThread(new Runnable() { // from class: com.xiaopeng.systemui.carmanager.XuiClientWrapper.3
            @Override // java.lang.Runnable
            public void run() {
                for (MakeupLightListener makeupLightListener : XuiClientWrapper.this.mMakeupLightListenerList) {
                    makeupLightListener.onMakeupLightStatusChanged(status);
                }
            }
        });
    }
}
