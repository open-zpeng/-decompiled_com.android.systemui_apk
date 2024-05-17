package com.xiaopeng.speech.vui;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.widget.ListView;
import android.widget.ScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.core.SliceHints;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.speech.apirouter.Utils;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.speech.protocol.event.VuiEvent;
import com.xiaopeng.speech.vui.cache.VuiSceneBuildCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.cache.VuiSceneRemoveCache;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.speech.vui.model.VuiEventInfo;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.model.VuiSceneState;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.ResourceUtil;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class VuiSceneManager {
    private final String TAG;
    private String mActiveSceneId;
    private Handler mApiRouterHandler;
    private HandlerThread mApiRouterThread;
    private Binder mBinder;
    private Context mContext;
    private Handler mHandler;
    private boolean mIsInSpeech;
    private String mObserver;
    private String mPackageName;
    private String mPackageVersion;
    private String mProcessName;
    private VuiBroadCastReceiver mReceiver;
    private Runnable mSubscribeRunner;
    private HandlerThread mThread;
    private ConcurrentHashMap<String, VuiSceneInfo> mVuiSceneInfoMap;
    private ConcurrentHashMap<String, VuiSceneInfo> mVuiSubSceneInfoMap;
    private List<String> sceneIds;
    public static int TYPE_BUILD = 0;
    public static int TYPE_UPDATE = 1;
    public static int TYPE_ADD = 2;
    public static int TYPE_REMOVE = 3;
    public static int TYPE_UPDATEATTR = 4;
    private static int SEND_UPLOAD_MESSAGE = 1;

    public void setInSpeech(boolean inSpeech) {
        this.mIsInSpeech = inSpeech;
        if (this.mIsInSpeech) {
            sendSceneData(null);
        }
    }

    public boolean isInSpeech() {
        return this.mIsInSpeech;
    }

    private VuiSceneManager() {
        this.TAG = "VuiSceneManager";
        this.mIsInSpeech = false;
        this.mBinder = null;
        this.mReceiver = null;
        this.mSubscribeRunner = new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.1
            @Override // java.lang.Runnable
            public void run() {
                VuiSceneManager.this.subscribe();
            }
        };
        this.mProcessName = null;
        this.sceneIds = new ArrayList();
        this.mVuiSceneInfoMap = new ConcurrentHashMap<>();
        this.mVuiSubSceneInfoMap = new ConcurrentHashMap<>();
        lazyInitThread();
    }

    public static final VuiSceneManager instance() {
        return Holder.Instance;
    }

    public void subscribe(String observer) {
        if (!Utils.isCorrectObserver(this.mPackageName, observer)) {
            LogUtils.e("VuiSceneManager", "注册observer不合法,observer是app的包名加observer的类名组成");
            return;
        }
        this.mObserver = observer;
        if (!VuiUtils.canUseVuiFeature()) {
            return;
        }
        subscribe();
        sendBroadCast();
        registerReceiver();
    }

    private void lazyInitThread() {
        if (this.mThread == null) {
            this.mThread = new HandlerThread("VuiSceneManager-Thread");
            this.mThread.start();
            this.mHandler = new VuiSceneHandler(this.mThread.getLooper());
        }
        if (this.mApiRouterThread == null) {
            this.mApiRouterThread = new HandlerThread("VuiSceneManager-Apirouter-Thread");
            this.mApiRouterThread.start();
            this.mApiRouterHandler = new Handler(this.mApiRouterThread.getLooper());
        }
    }

    public void reSetBinder() {
        this.mBinder = null;
    }

    public void setFeatureState(boolean b) {
        try {
            if (b) {
                handleSceneDataInfo();
            } else {
                handleAllSceneCache(true);
                handleSceneDataInfo();
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class VuiSceneHandler extends Handler {
        public VuiSceneHandler() {
        }

        public VuiSceneHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == VuiSceneManager.SEND_UPLOAD_MESSAGE) {
                int type = msg.arg1;
                boolean handleCache = msg.arg2 == 1;
                if (type == VuiSceneManager.TYPE_ADD) {
                    VuiSceneManager.this.addSceneElementGroup((VuiScene) msg.obj, handleCache);
                } else if (type == VuiSceneManager.TYPE_BUILD) {
                    VuiSceneManager.this.buildScene((VuiScene) msg.obj, handleCache, true);
                } else if (type == VuiSceneManager.TYPE_UPDATE) {
                    VuiSceneManager.this.updateDynamicScene((VuiScene) msg.obj, handleCache);
                } else if (type == VuiSceneManager.TYPE_UPDATEATTR) {
                    VuiSceneManager.this.updateSceneElementAttr((VuiScene) msg.obj, handleCache);
                } else {
                    String msgStr = (String) msg.obj;
                    int index = msgStr.indexOf(",");
                    VuiSceneManager.this.removeSceneElementGroup(msgStr.substring(0, index), msgStr.substring(index + 1), handleCache);
                }
            }
        }
    }

    private void registerReceiver() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (!VuiUtils.canUseVuiFeature()) {
                            return;
                        }
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("carspeechservice.SpeechServer.Start");
                        intentFilter.addAction(VuiConstants.INTENT_ACTION_ENV_CHANGED);
                        VuiSceneManager.this.mReceiver = new VuiBroadCastReceiver();
                        Application application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, null);
                        application.registerReceiver(VuiSceneManager.this.mReceiver, intentFilter);
                    } catch (Exception e) {
                        LogUtils.e("VuiSceneManager", "registerReceiver e:" + e.getMessage());
                    }
                }
            });
        }
    }

    public void handleSceneDataInfo() {
        String str;
        if (VuiEngineImpl.mActiveSceneId != null && (str = this.mPackageName) != null && str.equals(getTopRunningPackageName())) {
            enterScene(VuiEngineImpl.mActiveSceneId, this.mPackageName, true);
        }
    }

    public void handleAllSceneCache(boolean isAllRemove) {
        try {
            if (this.sceneIds == null) {
                return;
            }
            for (int i = 0; i < this.sceneIds.size(); i++) {
                String sceneId = this.sceneIds.get(i);
                if (isAllRemove) {
                    VuiSceneCacheFactory.instance().removeAllCache(sceneId);
                    VuiSceneInfo info = this.mVuiSceneInfoMap.get(sceneId);
                    if (info != null) {
                        info.reset(false);
                        this.mVuiSceneInfoMap.put(sceneId, info);
                    }
                } else {
                    VuiSceneBuildCache buildCache = (VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    buildCache.setUploadedState(sceneId, false);
                    VuiSceneCacheFactory.instance().removeOtherCache(sceneId);
                }
            }
        } catch (Exception e) {
            LogUtils.e("VuiSceneManager", "handleAllSceneCache e:" + e.getMessage());
        }
    }

    public void sendBroadCast() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.3
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiSceneManager.this.mBinder == null) {
                        VuiSceneManager.this.mBinder = new Binder();
                    }
                    Intent binderIntent = new Intent();
                    Bundle bundle = new Bundle();
                    binderIntent.setAction("com.xiaopeng.speech.vuiengine.start");
                    binderIntent.setPackage(SpeechConstant.SPEECH_SERVICE_PACKAGE_NAME);
                    bundle.putBinder("client", VuiSceneManager.this.mBinder);
                    bundle.putString("name", VuiSceneManager.this.mPackageName);
                    bundle.putString(BaseIconCache.IconDB.COLUMN_VERSION, VuiSceneManager.this.mPackageVersion);
                    if (VuiSceneManager.this.hasProcessFeature()) {
                        bundle.putString("processName", VuiSceneManager.this.getProcessName());
                    }
                    binderIntent.putExtra("bundle", bundle);
                    VuiSceneManager.this.mContext.sendBroadcast(binderIntent);
                }
            });
        }
    }

    public void subscribeVuiFeature() {
        if (!VuiUtils.canUseVuiFeature()) {
            return;
        }
        subscribe();
        sendBroadCast();
        registerReceiver();
    }

    public void unSubscribeVuiFeature() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.4
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Uri targetUrl = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("unsubscribeVuiFeatureProcess").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneManager.this.getProcessName()).build();
                        ApiRouter.route(targetUrl);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    public void subscribe() {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.5
                @Override // java.lang.Runnable
                public void run() {
                    if (!VuiUtils.canUseVuiFeature()) {
                        return;
                    }
                    LogUtils.logInfo("VuiSceneManager", "subscribe：" + VuiSceneManager.this.mObserver);
                    if (TextUtils.isEmpty(VuiSceneManager.this.mObserver)) {
                        LogUtils.e("VuiSceneManager", "mObserver == null");
                        try {
                            Uri targetUrl = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("subscribeVuiFeatureProcess").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneManager.this.getProcessName()).build();
                            String result = (String) ApiRouter.route(targetUrl);
                            LogUtils.logDebug("VuiSceneManager", "subscribeVuiFeature：" + result);
                            if (!TextUtils.isEmpty(result)) {
                                if (result.contains("dm_start")) {
                                    VuiSceneManager.this.mIsInSpeech = true;
                                } else if (result.contains("dm_end")) {
                                    VuiSceneManager.this.mIsInSpeech = false;
                                } else if (result.contains("vui_disabled")) {
                                    VuiSceneManager.this.setFeatureState(false);
                                    VuiUtils.disableVuiFeature();
                                } else if (result.contains("vui_enable")) {
                                    VuiSceneManager.this.setFeatureState(true);
                                    VuiUtils.enableVuiFeature();
                                }
                            }
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    String[] strArr = {VuiEvent.SCENE_CONTROL};
                    if (VuiSceneManager.this.mHandler != null) {
                        VuiSceneManager.this.mHandler.removeCallbacks(VuiSceneManager.this.mSubscribeRunner);
                    }
                    try {
                        if (VuiSceneManager.this.hasProcessFeature()) {
                            Uri targetUrl2 = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("subscribeProcess").appendQueryParameter("observer", VuiSceneManager.this.mObserver).appendQueryParameter("param", "").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("sceneList", VuiSceneManager.this.sceneIds.toString()).appendQueryParameter("processName", VuiSceneManager.this.getProcessName()).build();
                            ApiRouter.route(targetUrl2);
                            return;
                        }
                        Uri targetUrl3 = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("subscribe").appendQueryParameter("observer", VuiSceneManager.this.mObserver).appendQueryParameter("param", "").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("sceneList", VuiSceneManager.this.sceneIds.toString()).build();
                        ApiRouter.route(targetUrl3);
                    } catch (Exception e2) {
                        LogUtils.e("VuiSceneManager", "subscribe e:" + e2.fillInStackTrace());
                    }
                }
            });
        }
    }

    public String getProcessName() {
        if (!TextUtils.isEmpty(this.mProcessName)) {
            return this.mProcessName;
        }
        BufferedReader bufferedReader = null;
        String processName = "main";
        try {
            try {
                File file = new File("/proc/" + Process.myPid() + "/cmdline");
                bufferedReader = new BufferedReader(new FileReader(file));
                String processName2 = bufferedReader.readLine().trim();
                if (processName2.startsWith(this.mPackageName)) {
                    processName2 = processName2.replace(this.mPackageName, "");
                }
                if (TextUtils.isEmpty(processName2)) {
                    processName = "main";
                } else {
                    processName = processName2.substring(1);
                }
                this.mProcessName = processName + ",pid_" + Process.myPid();
                String str = this.mProcessName;
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return str;
            } catch (Exception e2) {
                e2.printStackTrace();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                return processName;
            }
        } catch (Throwable th) {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public void setProcessName(String processName) {
        this.mProcessName = processName + ",pid_" + Process.myPid();
    }

    public void unSubscribe() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.6
                @Override // java.lang.Runnable
                public void run() {
                    VuiSceneManager.this.unsubscribe();
                }
            });
        }
    }

    public void unsubscribe() {
        if (TextUtils.isEmpty(this.mObserver)) {
            LogUtils.e("VuiSceneManager", "mObserver == null");
            return;
        }
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.7
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiSceneManager.this.hasProcessFeature()) {
                        Uri targetUrl = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("unsubscribeProcess").appendQueryParameter("observer", VuiSceneManager.this.mObserver).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneManager.this.getProcessName()).build();
                        try {
                            ApiRouter.route(targetUrl);
                            return;
                        } catch (Throwable e) {
                            e.printStackTrace();
                            LogUtils.e("VuiSceneManager", "unsubscribe e:" + e.getMessage());
                            return;
                        }
                    }
                    Uri targetUrl2 = new Uri.Builder().authority(VuiSceneManager.this.getAuthority()).path("unsubscribe").appendQueryParameter("observer", VuiSceneManager.this.mObserver).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).build();
                    try {
                        ApiRouter.route(targetUrl2);
                    } catch (Throwable e2) {
                        e2.printStackTrace();
                        LogUtils.e("VuiSceneManager", "unsubscribe e:" + e2.getMessage());
                    }
                }
            });
        }
    }

    public synchronized void sendSceneData(final String sceneId) {
        try {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.8
                @Override // java.lang.Runnable
                public void run() {
                    VuiSceneCache updateCache;
                    int type;
                    VuiSceneRemoveCache removeCache;
                    List<VuiElement> elements;
                    if (VuiUtils.cannotUpload()) {
                        return;
                    }
                    String sendSceneId = sceneId;
                    if (TextUtils.isEmpty(sceneId)) {
                        sendSceneId = VuiEngineImpl.mActiveSceneId;
                    }
                    if (!VuiSceneManager.this.isUploadScene(sendSceneId) || (updateCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.UPDATE.getType())) == null || (type = updateCache.getFusionType(sendSceneId)) == VuiSceneCacheFactory.CacheType.DEFAULT.getType()) {
                        return;
                    }
                    LogUtils.logInfo("VuiSceneManager", "sendSceneData from cacheType:" + type + ",processName:" + VuiSceneManager.this.getProcessName());
                    VuiScene scene = new VuiScene.Builder().sceneId(sendSceneId).appVersion(VuiSceneManager.this.mPackageVersion).packageName(VuiSceneManager.this.mPackageName).timestamp(System.currentTimeMillis()).build();
                    if (type == VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                        scene.setElements(updateCache.getCache(sendSceneId));
                        VuiSceneManager.this.sendSceneData(VuiSceneManager.TYPE_UPDATE, false, scene);
                    } else if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                        VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                        if (buildCache != null && (elements = buildCache.getCache(sendSceneId)) != null && !elements.isEmpty()) {
                            scene.setElements(buildCache.getCache(sendSceneId));
                            VuiSceneManager.this.sendSceneData(VuiSceneManager.TYPE_BUILD, false, scene);
                        }
                    } else if (type == VuiSceneCacheFactory.CacheType.REMOVE.getType() && (removeCache = (VuiSceneRemoveCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.REMOVE.getType())) != null) {
                        VuiSceneManager vuiSceneManager = VuiSceneManager.this;
                        int i = VuiSceneManager.TYPE_REMOVE;
                        vuiSceneManager.sendSceneData(i, false, sendSceneId + "," + removeCache.getRemoveCache(sendSceneId).toString().replace(NavigationBarInflaterView.SIZE_MOD_START, "").replace(NavigationBarInflaterView.SIZE_MOD_END, ""));
                    }
                }
            });
        } catch (Exception e) {
            e.fillInStackTrace();
            LogUtils.e("VuiSceneManager", "sendSceneData e:" + e.getMessage());
        }
    }

    public void sendSceneData(int type, boolean handleCache, Object obj) {
        Message msg = this.mHandler.obtainMessage();
        msg.arg1 = type;
        msg.what = SEND_UPLOAD_MESSAGE;
        msg.arg2 = handleCache ? 1 : 0;
        msg.obj = obj;
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Holder {
        private static final VuiSceneManager Instance = new VuiSceneManager();

        private Holder() {
        }
    }

    public void buildScene(final VuiScene vuiScene, final boolean handleCache, final boolean isHardUpload) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.9
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneCache sceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (handleCache || isHardUpload || !sceneCache.getUploadedState(sceneId)) {
                        if (sceneCache != null && handleCache) {
                            List<VuiElement> cacheElements = sceneCache.getCache(sceneId);
                            if (cacheElements != null && !cacheElements.isEmpty()) {
                                vuiScene.setElements(sceneCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), false));
                            }
                            sceneCache.setCache(sceneId, vuiScene.getElements());
                        }
                        String jsonStr = VuiUtils.vuiSceneConvertToString(vuiScene);
                        VuiSceneInfo info = VuiSceneManager.instance().getSceneInfo(sceneId);
                        if (info == null || !info.isWholeScene() || !info.isFull()) {
                            return;
                        }
                        info.setLastAddStr(null);
                        info.setLastUpdateStr(null);
                        LogUtils.logDebug("VuiSceneManager", "build full_scene_info:" + jsonStr);
                        if (VuiSceneManager.this.isUploadScene(sceneId)) {
                            Uri.Builder builder = new Uri.Builder();
                            builder.authority(VuiSceneManager.this.getAuthority()).path("buildScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr);
                            try {
                                LogUtils.logDebug("VuiSceneManager", " send buildScene to CarSpeech" + sceneId);
                                if (sceneCache != null) {
                                    sceneCache.setUploadedState(sceneId, false);
                                }
                                String result = (String) ApiRouter.route(builder.build());
                                if (!TextUtils.isEmpty(result) && sceneCache != null) {
                                    sceneCache.setUploadedState(sceneId, true);
                                }
                                LogUtils.logInfo("VuiSceneManager", " send buildScene to CarSpeech success" + sceneId + ",result:" + result);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                if (sceneCache != null) {
                                    sceneCache.setUploadedState(sceneId, false);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void addSceneElement(final VuiScene vuiScene, final String parentElementId) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.10
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (VuiUtils.cannotUpload()) {
                            return;
                        }
                        String jsonStr = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneManager.this.getAuthority()).path("addSceneElement").appendQueryParameter(VuiConstants.SCENE_ID, vuiScene.getSceneId()).appendQueryParameter("parentId", parentElementId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr);
                        LogUtils.logDebug("VuiSceneManager", "addSceneElement to CarSpeech " + vuiScene.getSceneId());
                        String result = (String) ApiRouter.route(builder.build());
                        LogUtils.logInfo("VuiSceneManager", "addSceneElement to CarSpeech success" + vuiScene.getSceneId() + ",result:" + result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("VuiSceneManager", "addSceneElement e:" + e.getMessage());
                    }
                }
            });
        }
    }

    public void addSceneElementGroup(final VuiScene vuiScene, boolean handleCache) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.11
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    VuiSceneRemoveCache removeCache;
                    List<VuiElement> vuiElements;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || (sceneId = vuiScene2.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneCache updateCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.UPDATE.getType());
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    int type = VuiSceneCacheFactory.CacheType.DEFAULT.getType();
                    List<VuiElement> fusionElements = null;
                    if (updateCache != null) {
                        type = updateCache.getFusionType(sceneId);
                        if (type == VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                            fusionElements = updateCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), false);
                        } else if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                            if (buildCache != null) {
                                fusionElements = buildCache.getFusionCache(sceneId, vuiScene.getElements(), false);
                            }
                        } else if (type == VuiSceneCacheFactory.CacheType.REMOVE.getType() && (removeCache = (VuiSceneRemoveCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.REMOVE.getType())) != null && (vuiElements = vuiScene.getElements()) != null && vuiElements.size() == 1) {
                            String id = vuiScene.getElements().get(0).id;
                            removeCache.deleteRemoveIdFromCache(sceneId, id);
                        }
                        if (fusionElements != null) {
                            vuiScene.setElements(fusionElements);
                        }
                    }
                    if (VuiSceneManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneManager.this.sendBuildCacheInOther(sceneId, vuiScene, buildCache);
                            return;
                        }
                        String jsonStr = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneManager.this.getAuthority()).path("addSceneElementGroup").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr);
                        try {
                            LogUtils.logDebug("VuiSceneManager", "addSceneElementGroup to CarSpeech " + sceneId);
                            String result = (String) ApiRouter.route(builder.build());
                            LogUtils.logInfo("VuiSceneManager", "addSceneElementGroup to CarSpeech success " + sceneId + ",result:" + result);
                            if (!TextUtils.isEmpty(result)) {
                                if (type == VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                                    updateCache.removeCache(sceneId);
                                }
                            } else if (updateCache != null) {
                                updateCache.setCache(sceneId, vuiScene.getElements());
                            }
                        } catch (RemoteException e) {
                            LogUtils.e("VuiSceneManager", "addSceneElementGroup " + e.fillInStackTrace());
                            e.printStackTrace();
                            if (updateCache != null) {
                                updateCache.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                        if (buildCache != null) {
                            List<VuiElement> fusionElements2 = buildCache.getFusionCache(sceneId, vuiScene.getElements(), false);
                            if (fusionElements2 != null) {
                                buildCache.setCache(sceneId, fusionElements2);
                            }
                            vuiScene.setElements(fusionElements2);
                        }
                        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                            LogUtils.logDebug("VuiSceneManager", "addSceneElementGroup full_scene_info:" + VuiUtils.vuiSceneConvertToString(vuiScene));
                            return;
                        }
                        return;
                    }
                    if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                        if (buildCache != null) {
                            buildCache.setCache(sceneId, vuiScene.getElements());
                        }
                    } else {
                        if (updateCache != null) {
                            updateCache.setCache(sceneId, vuiScene.getElements());
                        }
                        if (buildCache != null) {
                            List<VuiElement> fusionElements3 = buildCache.getFusionCache(sceneId, vuiScene.getElements(), false);
                            if (fusionElements3 != null) {
                                buildCache.setCache(sceneId, fusionElements3);
                            }
                            vuiScene.setElements(fusionElements3);
                        }
                    }
                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                        LogUtils.logDebug("VuiSceneManager", "addSceneElementGroup from full_scene_info:" + VuiUtils.vuiSceneConvertToString(vuiScene));
                    }
                }
            });
        }
    }

    public void removeSceneElementGroup(final String sceneId, final String elementGroupId, final boolean handleCache) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.12
                /* JADX WARN: Multi-variable type inference failed */
                /* JADX WARN: Removed duplicated region for block: B:80:0x028f  */
                /* JADX WARN: Removed duplicated region for block: B:86:0x02ab  */
                /* JADX WARN: Type inference failed for: r8v14 */
                /* JADX WARN: Type inference failed for: r8v15 */
                /* JADX WARN: Type inference failed for: r8v8 */
                @Override // java.lang.Runnable
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public void run() {
                    /*
                        Method dump skipped, instructions count: 869
                        To view this dump add '--comments-level debug' option
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiSceneManager.AnonymousClass12.run():void");
                }
            });
        }
    }

    public void vuiFeedBack(final View view, final VuiFeedback feedback) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.13
                @Override // java.lang.Runnable
                public void run() {
                    View view2;
                    if (feedback == null || (view2 = view) == null) {
                        return;
                    }
                    String resourceName = null;
                    if (view2 != null && view2.getId() != -1 && view.getId() != 0) {
                        resourceName = VuiUtils.getResourceName(view.getId());
                    }
                    Uri.Builder builder = new Uri.Builder();
                    Uri.Builder appendQueryParameter = builder.authority(VuiSceneManager.this.getAuthority()).path("vuiFeedback").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("resourceName", resourceName);
                    appendQueryParameter.appendQueryParameter("state", "" + feedback.state).appendQueryParameter(VuiConstants.ELEMENT_TYPE, feedback.getFeedbackType().getType()).appendQueryParameter("content", feedback.content);
                    try {
                        LogUtils.logDebug("VuiSceneManager", "vuiFeedBack ");
                        String str = (String) ApiRouter.route(builder.build());
                        LogUtils.logInfo("VuiSceneManager", "vuiFeedBack success");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void vuiFeedBack(final String id, final VuiFeedback feedback) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.14
                @Override // java.lang.Runnable
                public void run() {
                    if (feedback == null || TextUtils.isEmpty(id)) {
                        return;
                    }
                    Uri.Builder builder = new Uri.Builder();
                    Uri.Builder appendQueryParameter = builder.authority(VuiSceneManager.this.getAuthority()).path("vuiFeedback").appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("resourceName", "");
                    appendQueryParameter.appendQueryParameter("state", "" + feedback.state).appendQueryParameter(VuiConstants.ELEMENT_TYPE, feedback.getFeedbackType().getType()).appendQueryParameter("content", feedback.content);
                    try {
                        LogUtils.logDebug("VuiSceneManager", "vuiFeedBack ");
                        String str = (String) ApiRouter.route(builder.build());
                        LogUtils.logInfo("VuiSceneManager", "vuiFeedBack success");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void updateSceneElementAttr(final VuiScene vuiScene, boolean handleCache) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.15
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    VuiSceneCache updateCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.UPDATE.getType());
                    int type = VuiSceneCacheFactory.CacheType.DEFAULT.getType();
                    List<VuiElement> fusionElements = null;
                    if (updateCache != null) {
                        type = updateCache.getFusionType(sceneId);
                        if (type == VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                            fusionElements = updateCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), true);
                        } else if (type == VuiSceneCacheFactory.CacheType.BUILD.getType() && buildCache != null) {
                            fusionElements = buildCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                        }
                        if (fusionElements != null) {
                            vuiScene.setElements(fusionElements);
                        }
                    }
                    String jsonStr = VuiUtils.vuiSceneConvertToString(vuiScene);
                    if (VuiSceneManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneManager.this.sendBuildCacheInOther(sceneId, vuiScene, buildCache);
                            return;
                        }
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr);
                        try {
                            LogUtils.logDebug("VuiSceneManager", " updateSceneElementAttr to CarSpeech" + sceneId);
                            String result = (String) ApiRouter.route(builder.build());
                            LogUtils.logInfo("VuiSceneManager", "updateSceneElementAttr to CarSpeech success" + sceneId + ",result:" + result);
                            if (TextUtils.isEmpty(result)) {
                                if (updateCache != null) {
                                    updateCache.setCache(sceneId, vuiScene.getElements());
                                }
                            } else if (updateCache != null) {
                                updateCache.removeCache(sceneId);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            LogUtils.e("VuiSceneManager", "updateSceneElementAttr " + e.fillInStackTrace());
                            if (updateCache != null) {
                                updateCache.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                        if (buildCache != null) {
                            List<VuiElement> fusionElements2 = buildCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                            if (fusionElements2 != null) {
                                buildCache.setCache(sceneId, fusionElements2);
                            }
                            vuiScene.setElements(fusionElements2);
                        }
                        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                            LogUtils.logDebug("VuiSceneManager", "updateSceneElementAttr " + VuiUtils.vuiSceneConvertToString(vuiScene));
                            return;
                        }
                        return;
                    }
                    if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                        if (buildCache != null) {
                            buildCache.setCache(sceneId, vuiScene.getElements());
                        }
                    } else {
                        if (updateCache != null) {
                            Gson mGson = new Gson();
                            VuiScene curScene = (VuiScene) mGson.fromJson(jsonStr, (Class<Object>) VuiScene.class);
                            updateCache.setCache(sceneId, curScene.getElements());
                        }
                        if (buildCache != null) {
                            List<VuiElement> fusionElements3 = buildCache.getFusionCache(sceneId, vuiScene.getElements(), true);
                            if (fusionElements3 != null) {
                                buildCache.setCache(sceneId, fusionElements3);
                            }
                            vuiScene.setElements(fusionElements3);
                        }
                    }
                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                        LogUtils.logDebug("VuiSceneManager", "updateSceneElementAttr cache" + VuiUtils.vuiSceneConvertToString(vuiScene));
                    }
                }
            });
        }
    }

    public void updateDynamicScene(final VuiScene vuiScene, final boolean handleCache) {
        Handler handler = this.mApiRouterHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.16
                @Override // java.lang.Runnable
                public void run() {
                    VuiScene vuiScene2;
                    String sceneId;
                    int type;
                    if (VuiUtils.cannotUpload() || (vuiScene2 = vuiScene) == null || vuiScene2.getElements() == null || vuiScene.getElements().size() < 0 || (sceneId = vuiScene.getSceneId()) == null) {
                        return;
                    }
                    VuiSceneInfo info = VuiSceneManager.this.getSceneInfo(sceneId);
                    if (info == null) {
                        return;
                    }
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    VuiSceneCache updateCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.UPDATE.getType());
                    if (!handleCache) {
                        if (!VuiSceneManager.this.isUploadScene(sceneId) || updateCache.getFusionType(sceneId) != VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                            return;
                        }
                        String jsonStr = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder = new Uri.Builder();
                        builder.authority(VuiSceneManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr);
                        try {
                            LogUtils.logDebug("VuiSceneManager", " updateScene to CarSpeech " + sceneId);
                            String result = (String) ApiRouter.route(builder.build());
                            if (TextUtils.isEmpty(result)) {
                                updateCache.setCache(sceneId, vuiScene.getElements());
                            } else {
                                updateCache.removeCache(sceneId);
                            }
                            LogUtils.logInfo("VuiSceneManager", " updateScene to CarSpeech success" + sceneId + ",result:" + result);
                            return;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            LogUtils.e("VuiSceneManager", "updateScene " + e.fillInStackTrace());
                            updateCache.setCache(sceneId, vuiScene.getElements());
                            return;
                        }
                    }
                    int type2 = VuiSceneCacheFactory.CacheType.DEFAULT.getType();
                    List<VuiElement> fusionElements = null;
                    if (updateCache != null) {
                        int type3 = updateCache.getFusionType(sceneId);
                        if (type3 == VuiSceneCacheFactory.CacheType.UPDATE.getType()) {
                            fusionElements = updateCache.getUpdateFusionCache(sceneId, vuiScene.getElements(), false);
                        } else if (type3 == VuiSceneCacheFactory.CacheType.BUILD.getType() && buildCache != null) {
                            fusionElements = buildCache.getCache(sceneId);
                        }
                        if (fusionElements != null) {
                            vuiScene.setElements(fusionElements);
                        }
                        type = type3;
                    } else {
                        type = type2;
                    }
                    if (VuiSceneManager.this.isUploadScene(sceneId)) {
                        if (type == VuiSceneCacheFactory.CacheType.BUILD.getType()) {
                            VuiSceneManager.this.sendBuildCacheInOther(sceneId, vuiScene, buildCache);
                            return;
                        }
                        String jsonStr2 = VuiUtils.vuiSceneConvertToString(vuiScene);
                        Uri.Builder builder2 = new Uri.Builder();
                        builder2.authority(VuiSceneManager.this.getAuthority()).path("updateScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, vuiScene.getPackageName()).appendQueryParameter("packageVersion", vuiScene.getVersion()).appendQueryParameter("sceneData", jsonStr2);
                        try {
                            LogUtils.logDebug("VuiSceneManager", " updateScene to CarSpeech" + sceneId);
                            String result2 = (String) ApiRouter.route(builder2.build());
                            LogUtils.logInfo("VuiSceneManager", "updateScene to CarSpeech success" + sceneId + ",result:" + result2);
                            if (TextUtils.isEmpty(result2)) {
                                if (updateCache != null) {
                                    updateCache.setCache(sceneId, vuiScene.getElements());
                                }
                            } else if (updateCache != null) {
                                updateCache.removeCache(sceneId);
                            }
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                            LogUtils.e("VuiSceneManager", "updateScene " + e2.fillInStackTrace());
                            if (updateCache != null) {
                                updateCache.setCache(sceneId, vuiScene.getElements());
                            }
                        }
                    } else if (type != VuiSceneCacheFactory.CacheType.BUILD.getType() && updateCache != null) {
                        updateCache.setCache(sceneId, vuiScene.getElements());
                    }
                }
            });
        }
    }

    public String enterScene(final String sceneId, final String packageName, boolean isWholeScene) {
        if (sceneId == null || packageName == null) {
            return "";
        }
        String result = "";
        try {
        } catch (Exception e) {
            LogUtils.e("VuiSceneManager", "enterScene--------------e: " + e.fillInStackTrace());
            e.printStackTrace();
        }
        if (VuiUtils.canUseVuiFeature()) {
            int state = getVuiSceneState(sceneId);
            LogUtils.logInfo("VuiSceneManager", "enterScene: sceneState:" + state + ",sceneId:" + sceneId);
            if (state == VuiSceneState.INIT.getState()) {
                updateSceneState(sceneId, VuiSceneState.ACTIVE.getState());
                result = null;
            } else if (state == VuiSceneState.UNACTIVE.getState()) {
                if (isNeedBuild(sceneId)) {
                    result = null;
                }
                updateSceneState(sceneId, VuiSceneState.ACTIVE.getState());
            } else if (state == VuiSceneState.IDLE.getState()) {
                LogUtils.e("VuiSceneManager", "未注册场景信息，场景数据将不能使用");
            } else if (state == VuiSceneState.ACTIVE.getState() && isNeedBuild(sceneId)) {
                result = null;
            }
            if (VuiConstants.MAP_APPNMAE.equals(this.mPackageName) && !this.mPackageName.equals(getTopRunningPackageName())) {
                return VuiUtils.cannotUpload() ? "" : result;
            }
            if (isWholeScene) {
                if (isUploadScene(sceneId) && !VuiUtils.cannotUpload()) {
                    sendSceneData(sceneId);
                }
                this.mApiRouterHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.17
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            if (VuiSceneManager.this.hasProcessFeature()) {
                                Uri.Builder builder = new Uri.Builder();
                                builder.authority(VuiSceneManager.this.getAuthority()).path("enterProcessScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, packageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneManager.this.getProcessName());
                                LogUtils.logDebug("VuiSceneManager", "enterScene: sceneId:" + sceneId);
                                String result2 = (String) ApiRouter.route(builder.build());
                                if (TextUtils.isEmpty(result2) && VuiSceneManager.this.mIsInSpeech) {
                                    VuiSceneManager.this.mIsInSpeech = false;
                                }
                                LogUtils.logDebug("VuiSceneManager", "enterScene: sceneId success:" + sceneId + ",result:" + result2);
                                return;
                            }
                            Uri.Builder builder2 = new Uri.Builder();
                            builder2.authority(VuiSceneManager.this.getAuthority()).path("enterScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, packageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion);
                            LogUtils.logDebug("VuiSceneManager", "enterScene: sceneId:" + sceneId);
                            if (TextUtils.isEmpty((String) ApiRouter.route(builder2.build())) && VuiSceneManager.this.mIsInSpeech) {
                                VuiSceneManager.this.mIsInSpeech = false;
                            }
                            LogUtils.logDebug("VuiSceneManager", "enterScene: sceneId success:" + sceneId);
                        } catch (Exception e2) {
                            LogUtils.e("VuiSceneManager", "enterScene--------------e: " + e2.fillInStackTrace());
                        }
                    }
                });
            }
            return VuiUtils.cannotUpload() ? "" : result;
        }
        return "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasProcessFeature() {
        return true;
    }

    private void updateSceneState(String sceneId, int state) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            info.setState(state);
        }
    }

    public void exitScene(final String sceneId, final String packageName, final boolean isWholeScene) {
        if (sceneId == null || packageName == null || !VuiUtils.canUseVuiFeature()) {
            return;
        }
        int state = getVuiSceneState(sceneId);
        if (state == VuiSceneState.ACTIVE.getState()) {
            updateSceneState(sceneId, VuiSceneState.UNACTIVE.getState());
            this.mApiRouterHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.18
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (isWholeScene) {
                            Uri.Builder builder = new Uri.Builder();
                            builder.authority(VuiSceneManager.this.getAuthority()).path("exitScene").appendQueryParameter(VuiConstants.SCENE_ID, sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, packageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion);
                            LogUtils.logDebug("VuiSceneManager", "exitScene-------------- " + sceneId);
                            ApiRouter.route(builder.build());
                            LogUtils.logDebug("VuiSceneManager", "exitScene---success---------- " + sceneId);
                        }
                    } catch (Exception e) {
                        LogUtils.e("VuiSceneManager", "exitScene--e: " + e.fillInStackTrace());
                    }
                }
            });
            return;
        }
        LogUtils.e("VuiSceneManager", "场景未激活不能执行退出");
    }

    public void destroyScene(final String sceneId) {
        Handler handler;
        if (sceneId != null && (handler = this.mApiRouterHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiSceneManager.19
                @Override // java.lang.Runnable
                public void run() {
                    VuiSceneBuildCache buildCache = (VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (buildCache != null && buildCache.getUploadedState(sceneId) && !VuiUtils.cannotUpload()) {
                        try {
                            Uri.Builder builder = new Uri.Builder();
                            if (!VuiSceneManager.this.hasProcessFeature()) {
                                builder.authority(VuiSceneManager.this.getAuthority()).path("destroyScene").appendQueryParameter("sceneIds", sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion);
                            } else {
                                builder.authority(VuiSceneManager.this.getAuthority()).path("destroyProcessScene").appendQueryParameter("sceneIds", sceneId).appendQueryParameter(VuiConstants.SCENE_PACKAGE_NAME, VuiSceneManager.this.mPackageName).appendQueryParameter("packageVersion", VuiSceneManager.this.mPackageVersion).appendQueryParameter("processName", VuiSceneManager.this.getProcessName());
                            }
                            LogUtils.logDebug("VuiSceneManager", "destroyScene-------------- " + sceneId);
                            ApiRouter.route(builder.build());
                            LogUtils.logDebug("VuiSceneManager", "destroyScene--------------success " + sceneId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    LogUtils.logDebug("VuiSceneManager", "destory removeAllCache--------------" + sceneId);
                    VuiSceneCacheFactory.instance().removeAllCache(sceneId);
                }
            });
        }
    }

    public VuiSceneInfo getSceneInfo(String sceneId) {
        ConcurrentHashMap<String, VuiSceneInfo> concurrentHashMap;
        if (sceneId == null || (concurrentHashMap = this.mVuiSceneInfoMap) == null || !concurrentHashMap.containsKey(sceneId)) {
            return null;
        }
        return this.mVuiSceneInfoMap.get(sceneId);
    }

    public void setWholeSceneId(String sceneId, String wholeSceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            info.setWholeSceneId(wholeSceneId);
            this.mVuiSceneInfoMap.put(sceneId, info);
        }
    }

    public void setIsWholeScene(String sceneId, boolean isWholeScene) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            info.setWholeScene(isWholeScene);
            this.mVuiSceneInfoMap.put(sceneId, info);
        }
    }

    public void addSubSceneIds(String sceneId, List<String> subSceneIds) {
        VuiSceneInfo info;
        if (this.mVuiSceneInfoMap.containsKey(sceneId) && (info = this.mVuiSceneInfoMap.get(sceneId)) != null) {
            List<String> subSceneIdList = info.getSubSceneList();
            if (subSceneIdList == null) {
                subSceneIdList = new ArrayList();
            }
            subSceneIdList.addAll(subSceneIds);
            info.setSubSceneList(subSceneIdList);
            this.mVuiSceneInfoMap.put(sceneId, info);
        }
    }

    public void removeSubSceneIds(String sceneId, String subSceneId) {
        VuiSceneInfo info;
        if (!this.mVuiSceneInfoMap.containsKey(sceneId) || (info = this.mVuiSceneInfoMap.get(sceneId)) == null) {
            return;
        }
        List<String> subSceneIdList = info.getSubSceneList();
        if (subSceneIdList == null) {
            subSceneIdList = new ArrayList();
        }
        if (subSceneIdList.contains(subSceneId)) {
            subSceneIdList.remove(subSceneId);
        }
        info.setSubSceneList(subSceneIdList);
        this.mVuiSceneInfoMap.put(sceneId, info);
    }

    public void setSceneIdList(String sceneId, List<String> idList) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            info.setIdList(idList);
            this.mVuiSceneInfoMap.put(sceneId, info);
        }
    }

    public List<String> getSceneIdList(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return info.getIdList();
        }
        return null;
    }

    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener, boolean needBuild) {
        LogUtils.logInfo("VuiSceneManager", "addVuiSceneListener-- " + sceneId + ",needBuild:" + needBuild);
        boolean isBuildSubScene = false;
        if (!VuiUtils.canUseVuiFeature()) {
            return;
        }
        LogUtils.logInfo("VuiSceneManager", "sceneId-- " + sceneId + ",listener:" + listener);
        if (TextUtils.isEmpty(sceneId) || listener == null) {
            LogUtils.logInfo("VuiSceneManager", "sceneId-- " + sceneId + ",listener:" + listener);
            LogUtils.e("VuiSceneManager", "场景注册时所需变量不能为空");
        } else if (rootView == null && !VuiUtils.isThirdApp(this.mPackageName) && !sceneId.endsWith("Dialog") && !sceneId.endsWith("dialog")) {
            LogUtils.e("VuiSceneManager", "场景注册时所需变量不能为空");
        } else {
            if (needBuild && (this.mVuiSceneInfoMap.containsKey(sceneId) || this.sceneIds.contains(sceneId))) {
                if (listener.equals(getVuiSceneListener(sceneId))) {
                    LogUtils.w("VuiSceneManager", "上次场景撤销未调用removeVuiSceneListener或重复创建场景");
                }
                VuiSceneCacheFactory.instance().removeAllCache(sceneId);
            }
            VuiSceneInfo sceneInfo = new VuiSceneInfo();
            if (this.mVuiSubSceneInfoMap.containsKey(sceneId)) {
                VuiSceneInfo sceneInfo2 = this.mVuiSubSceneInfoMap.get(sceneId);
                sceneInfo = sceneInfo2;
                this.mVuiSubSceneInfoMap.remove(sceneId);
                isBuildSubScene = true;
            } else if (this.mVuiSceneInfoMap.containsKey(sceneId)) {
                VuiSceneInfo sceneInfo3 = this.mVuiSceneInfoMap.get(sceneId);
                sceneInfo = sceneInfo3;
                if (needBuild) {
                    sceneInfo.reset(true);
                }
            }
            sceneInfo.setListener(listener);
            sceneInfo.setRootView(rootView);
            sceneInfo.setElementChangedListener(elementChangedListener);
            if (needBuild) {
                sceneInfo.setState(VuiSceneState.INIT.getState());
            } else if (sceneInfo.isBuildComplete()) {
                sceneInfo.setState(VuiSceneState.UNACTIVE.getState());
            } else {
                sceneInfo.reset(false);
                VuiSceneCacheFactory.instance().removeAllCache(sceneId);
            }
            LogUtils.logDebug("VuiSceneManager", "build:" + sceneInfo.isBuild());
            this.mVuiSceneInfoMap.put(sceneId, sceneInfo);
            if (isBuildSubScene) {
                LogUtils.i("VuiSceneManager", "onBuildScene");
                listener.onBuildScene();
            }
            if (!this.sceneIds.contains(sceneId)) {
                this.sceneIds.add(sceneId);
            }
        }
    }

    public void initSubSceneInfo(String subSceneId, String sceneId) {
        LogUtils.d("VuiSceneManager", "initSubSceneInfo subSceneId:" + subSceneId + ",sceneId:" + sceneId);
        VuiSceneInfo sceneInfo = new VuiSceneInfo();
        if (this.mVuiSubSceneInfoMap.containsKey(subSceneId)) {
            VuiSceneInfo sceneInfo2 = this.mVuiSubSceneInfoMap.get(subSceneId);
            sceneInfo = sceneInfo2;
        }
        sceneInfo.setWholeScene(false);
        sceneInfo.setWholeSceneId(sceneId);
        this.mVuiSubSceneInfoMap.put(subSceneId, sceneInfo);
    }

    public void removeVuiSceneListener(String sceneId, boolean upload, boolean keepCache, IVuiSceneListener listener) {
        if (VuiUtils.canUseVuiFeature()) {
            if (sceneId == null) {
                LogUtils.e("VuiSceneManager", "销毁场景时SceneId不能为空");
                return;
            }
            if (!this.mVuiSceneInfoMap.containsKey(sceneId) || !this.sceneIds.contains(sceneId)) {
                LogUtils.w("VuiSceneManager", "销毁场景前请先注册场景，重复销毁信息");
            }
            if (this.mVuiSceneInfoMap.containsKey(sceneId)) {
                VuiSceneInfo info = this.mVuiSceneInfoMap.get(sceneId);
                if (listener != null && info.getListener() != null && !listener.equals(info.getListener())) {
                    LogUtils.w("VuiSceneManager", "要销毁的场景和目前持有的场景数据不一致");
                    return;
                } else if (keepCache) {
                    LogUtils.logInfo("VuiSceneManager", "removeVuiSceneListener-------------- " + keepCache + ",info:" + info);
                    info.resetViewInfo();
                    this.mVuiSceneInfoMap.put(sceneId, info);
                } else {
                    List<String> subSceneList = info.getSubSceneList();
                    if (subSceneList != null) {
                        for (String subScene : subSceneList) {
                            VuiSceneInfo subVuiSceneInfo = this.mVuiSceneInfoMap.get(subScene);
                            if (subVuiSceneInfo != null && subVuiSceneInfo.getWholeSceneId() != null && subVuiSceneInfo.getWholeSceneId().contains(sceneId)) {
                                subVuiSceneInfo.getWholeSceneId().remove(sceneId);
                                this.mVuiSceneInfoMap.put(subScene, subVuiSceneInfo);
                            }
                        }
                    }
                    info.reset(true);
                    this.mVuiSceneInfoMap.remove(sceneId);
                    if (this.sceneIds.contains(sceneId)) {
                        this.sceneIds.remove(sceneId);
                    }
                }
            }
            if (keepCache) {
                return;
            }
            if (upload) {
                destroyScene(sceneId);
            } else {
                VuiSceneCacheFactory.instance().removeAllCache(sceneId);
            }
        }
    }

    public IVuiSceneListener getVuiSceneListener(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return info.getListener();
        }
        return null;
    }

    public View getRootView(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return info.getRootView();
        }
        return null;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public void setmPackageVersion(String mPackageVersion) {
        this.mPackageVersion = mPackageVersion;
    }

    public String getmPackageName() {
        return this.mPackageName;
    }

    public String getmPackageVersion() {
        return this.mPackageVersion;
    }

    public boolean getVuiSceneBuild(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return info.isBuild();
        }
        return false;
    }

    public int getVuiSceneState(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return info.getState();
        }
        return VuiSceneState.IDLE.getState();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private String getTopRunningPackageName() {
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(SliceHints.HINT_ACTIVITY);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
            ComponentName componentName = runningTaskInfos.get(0).topActivity;
            return componentName.getPackageName();
        }
        return null;
    }

    public String checkScrollSubViewIsVisible(String sceneId, String elementId) {
        String str;
        boolean z;
        VuiSceneManager vuiSceneManager = this;
        String str2 = sceneId;
        String str3 = "elementId";
        if (str2 == null || elementId == null || VuiUtils.cannotUpload()) {
            return "";
        }
        try {
            JSONObject jsonObject = new JSONObject(elementId);
            JSONArray elements = jsonObject.optJSONArray(VuiConstants.SCENE_ELEMENTS);
            if (elements != null || elements.length() > 0) {
                JSONObject elementStatus = null;
                JSONArray elementsResult = new JSONArray();
                boolean z2 = false;
                int i = 0;
                while (i < elements.length()) {
                    JSONObject element = elements.optJSONObject(i);
                    if (element == null) {
                        str = str3;
                        z = z2;
                    } else {
                        String subViewId = element.optString(str3);
                        String scrollViewID = element.optString("scrollViewId");
                        elementStatus = new JSONObject();
                        elementStatus.put(str3, subViewId);
                        VuiEventInfo subViewInfo = vuiSceneManager.findView(str2, subViewId);
                        VuiEventInfo scrollViewInfo = vuiSceneManager.findView(str2, scrollViewID);
                        if (subViewInfo == null) {
                            str = str3;
                            z = z2;
                        } else if (subViewInfo.hitView != null) {
                            str = str3;
                            if (scrollViewInfo == null || scrollViewInfo.hitView == null) {
                                z = false;
                                elementStatus.put("visible", true);
                            } else if (!(scrollViewInfo.hitView instanceof ScrollView)) {
                                z = false;
                            } else {
                                Rect scrollBounds = new Rect();
                                scrollViewInfo.hitView.getHitRect(scrollBounds);
                                if (subViewInfo.hitView.getLocalVisibleRect(scrollBounds)) {
                                    elementStatus.put("visible", true);
                                    z = false;
                                } else {
                                    z = false;
                                    elementStatus.put("visible", false);
                                }
                            }
                        } else {
                            str = str3;
                            z = false;
                        }
                    }
                    elementsResult.put(elementStatus);
                    i++;
                    vuiSceneManager = this;
                    z2 = z;
                    str3 = str;
                    str2 = sceneId;
                }
                jsonObject.put(VuiConstants.SCENE_ELEMENTS, elementsResult);
                return String.valueOf(jsonObject);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getElementState(String sceneId, String elementId) {
        VuiEventInfo viewInfo;
        JSONObject jsonObject;
        int scrollX;
        StringBuilder sb;
        ViewPager viewPager;
        JSONObject jsonObject2;
        if (sceneId == null || elementId == null || VuiUtils.cannotUpload()) {
            return null;
        }
        VuiSceneCache vuiSceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        VuiElement targetElement = vuiSceneCache.getVuiElementById(sceneId, elementId);
        if (targetElement == null) {
            return null;
        }
        if (VuiUtils.isThirdApp(VuiUtils.getPackageNameFromSceneId(sceneId))) {
            viewInfo = null;
        } else {
            VuiEventInfo viewInfo2 = findView(sceneId, elementId);
            viewInfo = viewInfo2;
        }
        String str = "VuiSceneManager";
        if (VuiElementType.RECYCLEVIEW.getType().equals(targetElement.getType())) {
            if (viewInfo != null && viewInfo.hitView != null && (viewInfo.hitView instanceof RecyclerView)) {
                RecyclerView recyclerView = (RecyclerView) viewInfo.hitView;
                if (recyclerView instanceof IVuiElement) {
                    JSONObject props = ((IVuiElement) recyclerView).getVuiProps();
                    if (props != null && props.has(VuiConstants.PROPS_DISABLETPL)) {
                        try {
                            boolean disableTpl = props.getBoolean(VuiConstants.PROPS_DISABLETPL);
                            if (disableTpl) {
                                JSONObject jsonObject3 = new JSONObject();
                                try {
                                    jsonObject3.put(VuiConstants.PROPS_SCROLLUP, true);
                                    jsonObject3.put(VuiConstants.PROPS_SCROLLDOWN, true);
                                    return jsonObject3.toString();
                                } catch (Exception e) {
                                }
                            }
                        } catch (Exception e2) {
                        }
                    }
                }
                boolean canScrollUp = recyclerView.canScrollVertically(-1);
                boolean canScrollDown = recyclerView.canScrollVertically(1);
                boolean canScrollLeft = recyclerView.canScrollHorizontally(-1);
                boolean canScrollRight = recyclerView.canScrollHorizontally(1);
                try {
                    jsonObject2 = new JSONObject();
                    try {
                    } catch (JSONException e3) {
                        e = e3;
                    }
                } catch (JSONException e4) {
                    e = e4;
                }
                try {
                    if (((IVuiElement) recyclerView).getVuiAction().equals(VuiAction.SCROLLBYY.getName())) {
                        jsonObject2.put(VuiConstants.PROPS_SCROLLUP, canScrollUp);
                        jsonObject2.put(VuiConstants.PROPS_SCROLLDOWN, canScrollDown);
                    } else {
                        jsonObject2.put(VuiConstants.PROPS_SCROLLLEFT, canScrollLeft);
                        jsonObject2.put(VuiConstants.PROPS_SCROLLRIGHT, canScrollRight);
                    }
                    str = "VuiSceneManager";
                    LogUtils.logInfo(str, "getElementState jsonObject: " + jsonObject2.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                    return jsonObject2.toString();
                } catch (JSONException e5) {
                    e = e5;
                    str = "VuiSceneManager";
                    LogUtils.e(str, "getElementState e:" + e.getMessage());
                    return null;
                }
            } else if (targetElement.getProps() != null) {
                return targetElement.getProps().toString();
            } else {
                try {
                    JSONObject jsonObject4 = new JSONObject();
                    jsonObject4.put(VuiConstants.PROPS_SCROLLUP, true);
                    jsonObject4.put(VuiConstants.PROPS_SCROLLDOWN, true);
                    LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject4.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                    return jsonObject4.toString();
                } catch (JSONException e6) {
                    LogUtils.e("VuiSceneManager", "getElementState e:" + e6.getMessage());
                    return null;
                }
            }
        }
        VuiEventInfo viewInfo3 = viewInfo;
        if (VuiElementType.VIEWPAGER.getType().equals(targetElement.getType())) {
            int extraPage = VuiUtils.getExtraPage(targetElement);
            if (extraPage != -1) {
                try {
                    JSONObject jsonObject5 = new JSONObject();
                    jsonObject5.put(VuiConstants.PROPS_SCROLLLEFT, true);
                    jsonObject5.put(VuiConstants.PROPS_SCROLLRIGHT, true);
                    LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject5.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                    return jsonObject5.toString();
                } catch (JSONException e7) {
                    LogUtils.e("VuiSceneManager", "getElementState e:" + e7.getMessage());
                    return null;
                }
            }
            if (viewInfo3 != null && viewInfo3.hitView != null) {
                if (!(viewInfo3.hitView instanceof ViewPager)) {
                    ViewPager viewPager2 = VuiUtils.findViewPager(viewInfo3.hitView);
                    viewPager = viewPager2;
                } else {
                    ViewPager viewPager3 = (ViewPager) viewInfo3.hitView;
                    viewPager = viewPager3;
                }
                if (viewPager != null) {
                    try {
                        boolean canLeft = viewPager.canScrollHorizontally(-1);
                        boolean canRight = viewPager.canScrollHorizontally(1);
                        JSONObject jsonObject6 = new JSONObject();
                        jsonObject6.put(VuiConstants.PROPS_SCROLLLEFT, canLeft);
                        jsonObject6.put(VuiConstants.PROPS_SCROLLRIGHT, canRight);
                        LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject6.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                        return jsonObject6.toString();
                    } catch (JSONException e8) {
                        LogUtils.e("VuiSceneManager", "getElementState e:" + e8.getMessage());
                        return null;
                    }
                }
            }
            return null;
        } else if (!VuiElementType.SCROLLVIEW.getType().equals(targetElement.getType())) {
            if (viewInfo3 != null && viewInfo3.hitView != null) {
                targetElement.setEnabled(viewInfo3.hitView.isEnabled() ? null : false);
            }
            Gson mGson = new Gson();
            String result = mGson.toJson(targetElement);
            LogUtils.logInfo("VuiSceneManager", "getElementState:  result:  " + result);
            return result;
        } else if (targetElement.getActions() != null) {
            List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(targetElement.actions.entrySet());
            if (!entries.isEmpty()) {
                if (VuiAction.SCROLLBYY.getName().equals(entries.get(0).getKey())) {
                    if (viewInfo3 != null && viewInfo3.hitView != null) {
                        if (viewInfo3.hitView instanceof ScrollView) {
                            View child = ((ViewGroup) viewInfo3.hitView).getChildAt(0);
                            if (child == null) {
                                return null;
                            }
                            try {
                                JSONObject jsonObject7 = new JSONObject();
                                int scrollY = viewInfo3.hitView.getScrollY();
                                jsonObject7.put(VuiConstants.PROPS_SCROLLUP, scrollY != 0);
                                jsonObject7.put(VuiConstants.PROPS_SCROLLDOWN, viewInfo3.hitView.getHeight() + scrollY != child.getMeasuredHeight());
                                LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject7.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                                return jsonObject7.toString();
                            } catch (Exception e9) {
                                e9.printStackTrace();
                                LogUtils.e("VuiSceneManager", "getElementState e:" + e9.getMessage());
                                return null;
                            }
                        }
                        Rect rect = new Rect();
                        viewInfo3.hitView.getGlobalVisibleRect(rect);
                        try {
                            JSONObject jsonObject8 = new JSONObject();
                            int scrollY2 = viewInfo3.hitView.getScrollY();
                            jsonObject8.put(VuiConstants.PROPS_SCROLLUP, scrollY2 != 0);
                            jsonObject8.put(VuiConstants.PROPS_SCROLLDOWN, rect.height() + scrollY2 < viewInfo3.hitView.getMeasuredHeight());
                            LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject8.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                            return jsonObject8.toString();
                        } catch (Exception e10) {
                            e10.printStackTrace();
                            LogUtils.e("VuiSceneManager", "getElementState e:" + e10.getMessage());
                            return null;
                        }
                    }
                    return null;
                } else if (VuiAction.SCROLLBYX.getName().equals(entries.get(0).getKey())) {
                    if (viewInfo3 != null && viewInfo3.hitView != null) {
                        if (!(viewInfo3.hitView instanceof ScrollView)) {
                            View parent = (View) viewInfo3.hitView.getParent();
                            if (parent.getWidth() < viewInfo3.hitView.getWidth()) {
                                try {
                                    jsonObject = new JSONObject();
                                    scrollX = parent.getScrollX();
                                    sb = new StringBuilder();
                                } catch (Exception e11) {
                                    e = e11;
                                }
                                try {
                                    sb.append("view width:");
                                    sb.append(viewInfo3.hitView.getWidth());
                                    sb.append(",parent:");
                                    sb.append(parent.getWidth());
                                    sb.append(",scrollX:");
                                    sb.append(scrollX);
                                    LogUtils.e("VuiSceneManager", sb.toString());
                                    jsonObject.put(VuiConstants.PROPS_SCROLLLEFT, scrollX != 0);
                                    jsonObject.put(VuiConstants.PROPS_SCROLLRIGHT, parent.getWidth() + scrollX < viewInfo3.hitView.getWidth());
                                    LogUtils.logInfo("VuiSceneManager", "getElementState jsonObject: " + jsonObject.toString() + ",sceneId:" + sceneId + ",elementId:" + elementId);
                                    return jsonObject.toString();
                                } catch (Exception e12) {
                                    e = e12;
                                    e.printStackTrace();
                                    LogUtils.e("VuiSceneManager", "getElementState e:" + e.getMessage());
                                    return null;
                                }
                            }
                            return null;
                        }
                        return null;
                    }
                    return null;
                } else {
                    return null;
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public VuiEventInfo findView(String sceneId, String elementId) {
        View view;
        if (sceneId == null || elementId == null) {
            return null;
        }
        try {
            VuiEventInfo info = findViewFromSceneInfo(sceneId, elementId);
            if (info != null) {
                LogUtils.logDebug("VuiSceneManager", "findViewFromSceneInfo elementId:" + elementId + ",view:" + info.hitView + ",sceneId:" + info.sceneId);
                return info;
            }
            VuiEventInfo rootViewInfo = findRootView(sceneId, elementId);
            StringBuilder sb = new StringBuilder();
            sb.append("findView elementId:");
            sb.append(elementId);
            sb.append(",rootView:");
            sb.append(rootViewInfo != null ? rootViewInfo.hitView : null);
            sb.append(",sceneId:");
            sb.append(rootViewInfo != null ? rootViewInfo.sceneId : "");
            LogUtils.logDebug("VuiSceneManager", sb.toString());
            if (rootViewInfo != null && rootViewInfo.hitView != null) {
                return new VuiEventInfo(getHitView(rootViewInfo.hitView, elementId), rootViewInfo.sceneId);
            }
            VuiSceneInfo sceneInfo = getSceneInfo(sceneId);
            LogUtils.logInfo("VuiSceneManager", "findView view by rootview");
            View rootView = sceneInfo == null ? null : sceneInfo.getRootView();
            if (rootView != null) {
                View view2 = getHitView(rootView, elementId);
                if (view2 == null) {
                    List<String> subSceneList = sceneInfo.getSubSceneList();
                    int size = subSceneList == null ? 0 : subSceneList.size();
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            break;
                        }
                        String curSceneId = subSceneList.get(i);
                        VuiSceneInfo curSceneInfo = TextUtils.isEmpty(curSceneId) ? null : getSceneInfo(curSceneId);
                        View rootView2 = curSceneInfo == null ? null : curSceneInfo.getRootView();
                        if (rootView2 == null || (view = getHitView(rootView2, elementId)) == null) {
                            i++;
                        } else {
                            info = new VuiEventInfo(view, curSceneId);
                            break;
                        }
                    }
                    return info;
                }
                return new VuiEventInfo(view2, sceneId);
            }
            return info;
        } catch (Exception e) {
            LogUtils.e("VuiSceneManager", "findView e:" + e.getMessage());
            return null;
        }
    }

    private View getHitView(View rootView, String elementId) {
        View view = null;
        if (rootView != null && (view = rootView.findViewWithTag(elementId)) == null) {
            view = findViewWithId(elementId, rootView);
            if (view == null) {
                LogUtils.e("VuiSceneManager", "findViewWithId  View is null");
            } else {
                LogUtils.logDebug("VuiSceneManager", "findViewWithId:   Tag====  " + view.getTag());
            }
        }
        return view;
    }

    private VuiEventInfo findViewFromSceneInfo(String sceneId, String elementId) {
        VuiSceneInfo info;
        List<SoftReference<View>> notChildrenViewList;
        if (sceneId != null && elementId != null && (info = getSceneInfo(sceneId)) != null && info.isContainNotChildrenView() && (notChildrenViewList = info.getNotChildrenViewList()) != null) {
            for (int i = 0; i < notChildrenViewList.size(); i++) {
                SoftReference<View> rootView = notChildrenViewList.get(i);
                if (rootView != null && rootView.get() != null) {
                    View view = rootView.get().findViewWithTag(elementId);
                    if (view != null) {
                        return new VuiEventInfo(view, sceneId);
                    }
                    View view2 = findViewWithId(elementId, rootView.get());
                    if (view2 != null) {
                        LogUtils.logDebug("VuiSceneManager", "findViewWithId:   Tag====  " + view2.getTag());
                        return new VuiEventInfo(view2, sceneId);
                    }
                }
            }
        }
        return null;
    }

    private VuiEventInfo findRootView(String sceneId, String elementId) {
        VuiSceneInfo info;
        if (sceneId == null || elementId == null || (info = getSceneInfo(sceneId)) == null) {
            return null;
        }
        VuiEventInfo rootViewInfo = null;
        LogUtils.logDebug("VuiSceneManager", "findRootView idList:" + info.getIdList());
        if (info.getIdList() != null && info.getIdList().contains(elementId)) {
            View rootView = getRootView(sceneId);
            VuiEventInfo rootViewInfo2 = new VuiEventInfo(rootView, sceneId);
            return rootViewInfo2;
        }
        List<String> subSceneList = info.getSubSceneList();
        if (subSceneList != null) {
            LogUtils.logDebug("VuiSceneManager", "findRootView subSceneList:" + subSceneList);
        }
        if (subSceneList == null) {
            return null;
        }
        int size = subSceneList.size();
        for (int i = 0; i < size; i++) {
            rootViewInfo = findRootView(subSceneList.get(i), elementId);
            if (rootViewInfo != null) {
                return rootViewInfo;
            }
        }
        return rootViewInfo;
    }

    public View findViewWithId(String id, View view) {
        String po;
        LogUtils.logInfo("VuiSceneManager", "findViewWithId  ===  " + id);
        if (view == null || id == null) {
            return view;
        }
        if (id.indexOf("_") != -1) {
            String vuiElementId = id.substring(0, id.indexOf("_"));
            if (TextUtils.isEmpty(vuiElementId)) {
                return null;
            }
            if (vuiElementId.length() > 4) {
                String[] ids = id.split("_");
                if (ids.length > 2) {
                    po = id.substring(id.indexOf("_", 1) + 1, id.indexOf("_", id.indexOf("_") + 1));
                } else {
                    po = ids[1];
                }
                if (po.length() < 4) {
                    int view_id = ResourceUtil.getId(this.mContext, vuiElementId);
                    LogUtils.logInfo("VuiSceneManager", "findViewWithId view tag: " + view.findViewById(view_id).getTag());
                    View listView = getListView(view.findViewById(view_id));
                    if (listView == null) {
                        return null;
                    }
                    if (listView instanceof RecyclerView) {
                        view = ((RecyclerView) listView).getLayoutManager().findViewByPosition(Integer.valueOf(po).intValue()).findViewById(view_id);
                    }
                    if (ids.length > 2) {
                        String lastElementID = id.substring(id.indexOf("_", id.indexOf("_") + 1) + 1);
                        return findViewWithId(lastElementID, view);
                    }
                    return view;
                }
                String lastElementID2 = id.substring(id.indexOf("_") + 1);
                int view_id2 = ResourceUtil.getId(this.mContext, vuiElementId);
                return findViewWithId(lastElementID2, view.findViewById(view_id2));
            }
            String lastElementID3 = id.substring(id.indexOf("_") + 1);
            return findViewWithId(lastElementID3, view);
        }
        int view_id3 = ResourceUtil.getId(this.mContext, id);
        return view.findViewById(view_id3);
    }

    private View getListView(View view) {
        if (view == null) {
            return view;
        }
        if ((view instanceof ListView) || (view instanceof RecyclerView)) {
            return view;
        }
        if (view.getParent() == null) {
            return null;
        }
        if (view.getParent() instanceof ViewRootImpl) {
            return view;
        }
        return getListView((View) view.getParent());
    }

    private View getScrollView(View view) {
        if (view == null) {
            return view;
        }
        if ((view instanceof ListView) || (view instanceof ScrollView)) {
            return view;
        }
        if (view.getParent() == null) {
            return null;
        }
        if (view.getParent() instanceof ViewRootImpl) {
            return view;
        }
        return getScrollView((View) view.getParent());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isUploadScene(String sceneId) {
        LogUtils.logDebug("VuiSceneManager", "isUploadScene sceneId:" + sceneId + ",getTopRunningPackageName:" + getTopRunningPackageName() + ",mIsInSpeech:" + this.mIsInSpeech + ",VuiEngine.mActiveSceneId:" + VuiEngineImpl.mActiveSceneId + ",mPackageName" + this.mPackageName);
        String str = this.mPackageName;
        if (str == null || sceneId == null) {
            return false;
        }
        if ("com.android.systemui".equals(str)) {
            return true;
        }
        if (this.mIsInSpeech && sceneId.equals(VuiEngineImpl.mActiveSceneId) && (VuiConstants.SETTINS.equals(this.mPackageName) || VuiConstants.CARCONTROL.equals(this.mPackageName) || VuiConstants.CHARGE.equals(this.mPackageName))) {
            return true;
        }
        return this.mIsInSpeech && this.mPackageName.equals(getTopRunningPackageName()) && sceneId.equals(VuiEngineImpl.mActiveSceneId);
    }

    public boolean canUpdateScene(String sceneId) {
        if (sceneId == null) {
            return false;
        }
        if (!getVuiSceneBuild(sceneId)) {
            LogUtils.logDebug("VuiSceneManager", sceneId + "场景数据的update必须在场build后");
            return false;
        }
        int state = getVuiSceneState(sceneId);
        if (state == VuiSceneState.IDLE.getState()) {
            LogUtils.logDebug("VuiSceneManager", sceneId + "场景数据的build必须在场景被激活后");
            return false;
        }
        return true;
    }

    private boolean isNeedBuild(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null) {
            return true ^ info.isBuild();
        }
        return true;
    }

    public boolean canRunUpdateSceneTask(String sceneId) {
        if (sceneId == null) {
            return false;
        }
        VuiSceneBuildCache cache = (VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        List<VuiElement> cacheElements = cache.getCache(sceneId);
        if (cacheElements == null || cacheElements.isEmpty()) {
            return false;
        }
        int state = getVuiSceneState(sceneId);
        if (state == VuiSceneState.IDLE.getState()) {
            LogUtils.logDebug("VuiSceneManager", sceneId + "场景数据的build必须在场景被激活后");
            return false;
        }
        return true;
    }

    public void addVuiEventListener(String sceneId, IVuiEventListener listener) {
        VuiSceneInfo info;
        LogUtils.logInfo("VuiSceneManager", "addVuiEventListener-- " + sceneId);
        if (!VuiUtils.canUseVuiFeature() || sceneId == null || listener == null || (info = getSceneInfo(sceneId)) == null) {
            return;
        }
        info.setEventListener(listener);
    }

    public IVuiEventListener getVuiEventListener(String sceneId) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info == null) {
            return null;
        }
        return info.getEventListener();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBuildCacheInOther(String sceneId, VuiScene vuiScene, VuiSceneCache buildCache) {
        VuiSceneInfo info = getSceneInfo(sceneId);
        if (info != null && info.isBuildComplete()) {
            buildScene(vuiScene, false, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAuthority() {
        if (!Utils.isXpDevice()) {
            return VuiConstants.VUI_SCENE_THIRD_AUTHORITY;
        }
        return VuiConstants.VUI_SCENE_AUTHORITY;
    }
}
