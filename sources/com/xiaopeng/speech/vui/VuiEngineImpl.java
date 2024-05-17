package com.xiaopeng.speech.vui;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.policy.DecorView;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.vui.cache.VuiSceneBuildCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.model.VuiSceneState;
import com.xiaopeng.speech.vui.task.TaskDispatcher;
import com.xiaopeng.speech.vui.task.TaskWrapper;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.BuildConfig;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class VuiEngineImpl {
    private static final String TAG = "VuiEngine";
    public static String mActiveSceneId = null;
    public static String mSceneIdPrefix = null;
    private EventDispatcher eventDispatcher;
    private Context mContext;
    private Handler mDispatherHandler;
    private HandlerThread mDispatherThread;
    private Handler mHandler;
    private String mPackageName;
    private String mPackageVersion;
    private Resources mResources;
    private HandlerThread mThread;
    private TaskDispatcher taskStructure;
    private List<String> mainThreadSceneList = Arrays.asList("MainMusicConcentration");
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private List<String> mEnterSceneIds = Collections.synchronizedList(new ArrayList());
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<String> mEnterSceneStack = new ArrayList();
    private UpdateElementAttrRun mUpdateElementAttrRun = null;
    private UpdateSceneRun mUpdateSceneRun = null;

    public VuiEngineImpl(Context context, boolean initEvent) {
        if (!VuiUtils.canUseVuiFeature()) {
            return;
        }
        LogUtils.logInfo(TAG, BuildConfig.BUILD_VERSION);
        this.mContext = context;
        Foo.setContext(this.mContext);
        lazyInitThread();
        this.mResources = this.mContext.getResources();
        this.mPackageName = context.getApplicationInfo().packageName;
        VuiSceneManager.instance().setmPackageName(this.mPackageName);
        VuiSceneManager.instance().setContext(this.mContext);
        this.eventDispatcher = new EventDispatcher(this.mContext, initEvent);
        this.taskStructure = new TaskDispatcher();
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.mPackageName, 0);
            this.mPackageVersion = packageInfo.versionName;
            VuiSceneManager.instance().setmPackageVersion(this.mPackageVersion);
            mSceneIdPrefix = this.mPackageName + "-" + this.mPackageVersion;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "VuiEngine init e:" + e.getMessage());
        }
    }

    private void lazyInitThread() {
        if (this.mThread == null) {
            this.mThread = new HandlerThread("VuiEngine-Thread");
            this.mThread.start();
            this.mHandler = new Handler(this.mThread.getLooper());
        }
        if (this.mDispatherThread == null) {
            this.mDispatherThread = new HandlerThread("VuiEngine-Disptcher-Thread");
            this.mDispatherThread.start();
            this.mDispatherHandler = new Handler(this.mDispatherThread.getLooper());
        }
    }

    public void enterScene(final String sceneId, final boolean isWholeScene) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || sceneId == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.1
            /* JADX WARN: Code restructure failed: missing block: B:53:0x0169, code lost:
                com.xiaopeng.speech.vui.VuiEngineImpl.mActiveSceneId = r6;
                r17.this$0.mEnterSceneStack.add(r6);
                r0 = com.xiaopeng.speech.vui.VuiSceneManager.instance().enterScene(r6, r17.this$0.mPackageName, r3);
                r17.this$0.handlerEnterScene(r6, r0);
             */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void run() {
                /*
                    Method dump skipped, instructions count: 457
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.vui.VuiEngineImpl.AnonymousClass1.run():void");
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlerEnterScene(String sceneId, String result) {
        final IVuiSceneListener listener;
        if (!VuiUtils.cannotUpload() && (listener = VuiSceneManager.instance().getVuiSceneListener(sceneId)) != null) {
            if (result == null) {
                listener.onBuildScene();
            } else {
                this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.2
                    @Override // java.lang.Runnable
                    public void run() {
                        listener.onVuiStateChanged();
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Context getContext(String sceneId) {
        View view;
        View rootView = VuiSceneManager.instance().getRootView(sceneId);
        if (rootView != null) {
            Context context = rootView.getContext();
            if (rootView instanceof DecorView) {
                return context;
            }
            if (context != null && (context instanceof ContextWrapper)) {
                context = getDialogOwnContext(context);
            }
            if (context != null && (context instanceof Application) && !VuiConstants.MUSIC.equals(this.mPackageName) && (view = rootView.findViewById(16908290)) != null && (view instanceof ViewGroup)) {
                return getDialogOwnContext(((ViewGroup) view).getChildAt(0).getContext());
            }
            return context;
        }
        return null;
    }

    private Context getDialogOwnContext(Context context) {
        if ((context instanceof Activity) || (context instanceof Service) || (context instanceof Application)) {
            return context;
        }
        if (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return getDialogOwnContext(context);
    }

    public String getSceneUnqiueId(String sceneId) {
        return mSceneIdPrefix + "-" + sceneId;
    }

    public void exitDupScene(String sceneId, boolean isWholeScene, IVuiSceneListener listener) {
        if (listener != null && !TextUtils.isEmpty(sceneId)) {
            exitScene(listener.toString() + "-" + sceneId, isWholeScene);
        }
    }

    public void enterDupScene(String sceneId, boolean isWholeScene, IVuiSceneListener listener) {
        if (listener != null && !TextUtils.isEmpty(sceneId)) {
            enterScene(listener.toString() + "-" + sceneId, isWholeScene);
        }
    }

    public void exitScene(final String sceneId, final boolean isWholeScene) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || sceneId == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                    if (VuiEngineImpl.this.mEnterSceneStack.contains(newSceneId)) {
                        VuiEngineImpl.this.mEnterSceneStack.remove(newSceneId);
                        LogUtils.logInfo(VuiEngineImpl.TAG, "exitScene:" + newSceneId + ",mEnterSceneStack:" + VuiEngineImpl.this.mEnterSceneStack);
                        VuiSceneManager.instance().exitScene(newSceneId, VuiEngineImpl.this.mPackageName, isWholeScene);
                        if (newSceneId.equals(VuiEngineImpl.mActiveSceneId)) {
                            if (VuiEngineImpl.this.mEnterSceneStack.size() != 0) {
                                VuiEngineImpl.mActiveSceneId = (String) VuiEngineImpl.this.mEnterSceneStack.get(VuiEngineImpl.this.mEnterSceneStack.size() - 1);
                                String sceneId2 = VuiEngineImpl.mActiveSceneId;
                                String result = VuiSceneManager.instance().enterScene(sceneId2, VuiEngineImpl.this.mPackageName, isWholeScene);
                                VuiEngineImpl.this.handlerEnterScene(sceneId2, result);
                            } else {
                                VuiEngineImpl.mActiveSceneId = null;
                            }
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, "e:" + e.fillInStackTrace());
                }
            }
        });
    }

    public void buildScene(final String sceneId, final View rootView, final List<Integer> customizeIds, final IVuiElementListener viewVuiHandler, final List<String> subSceneIdList, final boolean isWholeScene, final ISceneCallbackHandler sceneCallbackHandler) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        if (rootView == null || sceneId == null) {
            return;
        }
        Handler handler = this.mHandler;
        if (handler == null) {
            return;
        }
        handler.postDelayed(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.4
            @Override // java.lang.Runnable
            public void run() {
                List<String> newSuSceneIdList;
                try {
                    String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                    int state = VuiSceneManager.instance().getVuiSceneState(newSceneId);
                    if (state == VuiSceneState.INIT.getState() && isWholeScene) {
                        LogUtils.e(VuiEngineImpl.TAG, sceneId + "场景数据的创建必须在场景被激活后");
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "buildScene:" + sceneId);
                    if (subSceneIdList == null) {
                        newSuSceneIdList = null;
                    } else {
                        List<String> newSuSceneIdList2 = new ArrayList<>();
                        newSuSceneIdList2.addAll(subSceneIdList);
                        int size = newSuSceneIdList2.size();
                        for (int i = 0; i < size; i++) {
                            String subSceneId = newSuSceneIdList2.get(i);
                            newSuSceneIdList2.remove(subSceneId);
                            newSuSceneIdList2.add(i, VuiEngineImpl.this.getSceneUnqiueId(subSceneId));
                        }
                        newSuSceneIdList = newSuSceneIdList2;
                    }
                    VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.BUILD, customizeIds, viewVuiHandler, newSuSceneIdList, Arrays.asList(rootView), isWholeScene, sceneCallbackHandler));
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, "e:" + e.fillInStackTrace());
                }
            }
        }, 200L);
    }

    public void buildScene(final String sceneId, final List<View> viewList, final List<Integer> customizeIds, final IVuiElementListener viewVuiHandler, final List<String> subSceneIdList, final boolean isWholeScene, final ISceneCallbackHandler mSceneCallbackHandler) {
        if (VuiUtils.cannotUpload() || sceneId == null || this.mMainHandler == null) {
            return;
        }
        if (viewList == null || viewList.isEmpty()) {
            String sceneIdStr = getSceneUnqiueId(sceneId);
            View rootView = VuiSceneManager.instance().getRootView(sceneIdStr);
            if (rootView != null) {
                buildScene(sceneId, rootView, customizeIds, viewVuiHandler, subSceneIdList, isWholeScene, mSceneCallbackHandler);
            }
        } else if (viewList.size() == 1) {
            buildScene(sceneId, viewList.get(0), customizeIds, viewVuiHandler, subSceneIdList, isWholeScene, mSceneCallbackHandler);
        } else {
            this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.5
                @Override // java.lang.Runnable
                public void run() {
                    List<String> newSuSceneIdList;
                    try {
                        String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                        int state = VuiSceneManager.instance().getVuiSceneState(newSceneId);
                        if (state == VuiSceneState.INIT.getState() && isWholeScene) {
                            LogUtils.e(VuiEngineImpl.TAG, sceneId + "场景数据的创建必须在场景被激活后");
                            return;
                        }
                        LogUtils.logDebug(VuiEngineImpl.TAG, "buildScene:" + sceneId);
                        if (subSceneIdList == null) {
                            newSuSceneIdList = null;
                        } else {
                            List<String> newSuSceneIdList2 = new ArrayList<>();
                            newSuSceneIdList2.addAll(subSceneIdList);
                            int size = newSuSceneIdList2.size();
                            for (int i = 0; i < size; i++) {
                                String subSceneId = newSuSceneIdList2.get(i);
                                newSuSceneIdList2.remove(subSceneId);
                                newSuSceneIdList2.add(i, VuiEngineImpl.this.getSceneUnqiueId(subSceneId));
                            }
                            newSuSceneIdList = newSuSceneIdList2;
                        }
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.BUILD, customizeIds, viewVuiHandler, newSuSceneIdList, viewList, isWholeScene, mSceneCallbackHandler));
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            }, 200L);
        }
    }

    public void updateElementAttribute(final String sceneId, final List<View> viewList) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.6
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || viewList == null || TextUtils.isEmpty(sceneId)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateElementAttribute");
                    if (VuiEngineImpl.this.mUpdateElementAttrRun != null) {
                        if (sceneId.equals(VuiEngineImpl.this.mUpdateElementAttrRun.getSceneId())) {
                            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateElementAttrRun);
                            Set<View> set = new HashSet<>(VuiEngineImpl.this.mUpdateElementAttrRun.getUpdateViews());
                            set.addAll(viewList);
                            List<View> list = new ArrayList<>(set);
                            VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(list);
                            VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                            return;
                        }
                        VuiEngineImpl.this.mUpdateElementAttrRun.run();
                        VuiEngineImpl vuiEngineImpl = VuiEngineImpl.this;
                        vuiEngineImpl.mUpdateElementAttrRun = new UpdateElementAttrRun();
                        VuiEngineImpl.this.mUpdateElementAttrRun.setSceneId(sceneId);
                        VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(viewList);
                        VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                        return;
                    }
                    VuiEngineImpl vuiEngineImpl2 = VuiEngineImpl.this;
                    vuiEngineImpl2.mUpdateElementAttrRun = new UpdateElementAttrRun();
                    VuiEngineImpl.this.mUpdateElementAttrRun.setSceneId(sceneId);
                    VuiEngineImpl.this.mUpdateElementAttrRun.setUpdateViews(viewList);
                    VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateElementAttrRun, 50L);
                }
            });
        }
    }

    public void setUpdateElementValue(final String sceneId, final String elementId, final Object value) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        try {
            if (!TextUtils.isEmpty(sceneId) && value != null) {
                this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.7
                    @Override // java.lang.Runnable
                    public void run() {
                        String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                        if (VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                            VuiScene vuiScene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                            VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                            if (buildCache != null) {
                                LogUtils.i(VuiEngineImpl.TAG, "newSceneId：" + newSceneId + "，elementId：" + elementId);
                                VuiElement targetElement = buildCache.getVuiElementById(newSceneId, elementId);
                                StringBuilder sb = new StringBuilder();
                                sb.append("targetElement：");
                                sb.append(targetElement);
                                LogUtils.i(VuiEngineImpl.TAG, sb.toString());
                                if (targetElement != null) {
                                    targetElement.setValues(value);
                                    LogUtils.i(VuiEngineImpl.TAG, "targetElement：" + targetElement);
                                    List<VuiElement> resultElement = Arrays.asList(targetElement);
                                    LogUtils.i(VuiEngineImpl.TAG, "resultElement：" + resultElement);
                                    vuiScene.setElements(resultElement);
                                    List<VuiElement> fusionElement = buildCache.getFusionCache(newSceneId, resultElement, false);
                                    buildCache.setCache(newSceneId, fusionElement);
                                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                                        VuiScene scene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                                        scene.setElements(fusionElement);
                                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(scene));
                                    }
                                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene);
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
        }
    }

    public void setUpdateElementVisible(final String sceneId, final String elementId, final boolean visible) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(sceneId)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.8
            @Override // java.lang.Runnable
            public void run() {
                VuiElement targetElement;
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    VuiScene vuiScene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (buildCache != null && (targetElement = buildCache.getVuiElementById(newSceneId, elementId)) == null) {
                        targetElement.setVisible(Boolean.valueOf(visible));
                        List<VuiElement> resultElement = Arrays.asList(targetElement);
                        vuiScene.setElements(resultElement);
                        List<VuiElement> fusionElement = buildCache.getFusionCache(newSceneId, resultElement, false);
                        buildCache.setCache(newSceneId, fusionElement);
                        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                            VuiScene scene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                            scene.setElements(fusionElement);
                            LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(scene));
                        }
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene);
                    }
                }
            }
        });
    }

    /* loaded from: classes.dex */
    class UpdateElementAttrRun implements Runnable {
        private String sceneId;
        private List<View> updateViews;

        UpdateElementAttrRun() {
        }

        public String getSceneId() {
            return this.sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public List<View> getUpdateViews() {
            return this.updateViews;
        }

        public void setUpdateViews(List<View> updateViews) {
            this.updateViews = updateViews;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateElementAttrRun);
            VuiEngineImpl.this.mUpdateElementAttrRun = null;
            String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(this.sceneId);
            if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                return;
            }
            LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneElementAttribute:" + this.sceneId);
            VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATEATTRIBUTE, this.updateViews));
        }
    }

    /* loaded from: classes.dex */
    class UpdateSceneRun implements Runnable {
        private String sceneId;
        private List<View> updateViews;

        UpdateSceneRun() {
        }

        public String getSceneId() {
            return this.sceneId;
        }

        public void setSceneId(String sceneId) {
            this.sceneId = sceneId;
        }

        public List<View> getUpdateViews() {
            return this.updateViews;
        }

        public void setUpdateViews(List<View> updateViews) {
            this.updateViews = updateViews;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateSceneRun);
            VuiEngineImpl.this.mUpdateSceneRun = null;
            String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(this.sceneId);
            if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                return;
            }
            LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + this.sceneId);
            VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATE, this.updateViews));
        }
    }

    public void updateRecyclerViewItemView(String sceneId, List<View> viewList, RecyclerView recyclerView) {
        if (VuiUtils.cannotUpload() || viewList == null || TextUtils.isEmpty(sceneId)) {
            return;
        }
        String newSceneId = getSceneUnqiueId(sceneId);
        if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
            return;
        }
        LogUtils.logInfo(TAG, "updateRecyclerViewItemView:" + sceneId);
        this.taskStructure.dispatchTask(structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATERECYCLEVIEWITEM, viewList, recyclerView));
    }

    public void updateScene(final String sceneId, final View view) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.9
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || view == null || TextUtils.isEmpty(sceneId)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene");
                    if (VuiEngineImpl.this.mUpdateSceneRun != null) {
                        if (sceneId.equals(VuiEngineImpl.this.mUpdateSceneRun.getSceneId())) {
                            VuiEngineImpl.this.mHandler.removeCallbacks(VuiEngineImpl.this.mUpdateSceneRun);
                            Set<View> set = new HashSet<>(VuiEngineImpl.this.mUpdateSceneRun.getUpdateViews());
                            set.add(view);
                            List<View> list = new ArrayList<>(set);
                            VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(list);
                            VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                            return;
                        }
                        VuiEngineImpl.this.mUpdateSceneRun.run();
                        VuiEngineImpl vuiEngineImpl = VuiEngineImpl.this;
                        vuiEngineImpl.mUpdateSceneRun = new UpdateSceneRun();
                        VuiEngineImpl.this.mUpdateSceneRun.setSceneId(sceneId);
                        VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(Arrays.asList(view));
                        VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                        return;
                    }
                    VuiEngineImpl vuiEngineImpl2 = VuiEngineImpl.this;
                    vuiEngineImpl2.mUpdateSceneRun = new UpdateSceneRun();
                    VuiEngineImpl.this.mUpdateSceneRun.setSceneId(sceneId);
                    VuiEngineImpl.this.mUpdateSceneRun.setUpdateViews(Arrays.asList(view));
                    VuiEngineImpl.this.mHandler.postDelayed(VuiEngineImpl.this.mUpdateSceneRun, 50L);
                }
            });
        }
    }

    public void updateScene(final String sceneId, final List<View> viewList) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.10
                @Override // java.lang.Runnable
                public void run() {
                    if (VuiUtils.cannotUpload() || viewList == null || TextUtils.isEmpty(sceneId)) {
                        return;
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene");
                    try {
                        String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                        if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                            return;
                        }
                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + sceneId);
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATE, (List<Integer>) null, (IVuiElementListener) null, viewList));
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            });
        }
    }

    public void updateScene(final String sceneId, final List<View> viewList, final List<Integer> ids, final IVuiElementListener vuiHandler) {
        if (VuiUtils.cannotUpload() || viewList == null || sceneId == null) {
            return;
        }
        if (viewList.size() == 1) {
            updateScene(sceneId, viewList.get(0), ids, vuiHandler);
        } else {
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.11
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                        if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                            return;
                        }
                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + sceneId);
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATE, ids, vuiHandler, viewList));
                    } catch (Exception e) {
                        LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                    }
                }
            });
        }
    }

    public void updateScene(final String sceneId, final View view, final List<Integer> ids, final IVuiElementListener callback) {
        if (VuiUtils.cannotUpload() || view == null || sceneId == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.12
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (VuiEngineImpl.this.mainThreadSceneList.contains(sceneId) && (view instanceof RecyclerView)) {
                        VuiEngineImpl.this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.12.1
                            @Override // java.lang.Runnable
                            public void run() {
                                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                                    return;
                                }
                                LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + sceneId);
                                List<View> views = new ArrayList<>();
                                views.add(view);
                                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATE, ids, callback, views));
                            }
                        });
                    } else {
                        String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                        if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                            return;
                        }
                        LogUtils.logDebug(VuiEngineImpl.TAG, "updateScene:" + sceneId);
                        List<View> views = new ArrayList<>();
                        views.add(view);
                        VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.UPDATE, ids, callback, views));
                    }
                } catch (Exception e) {
                    LogUtils.e(VuiEngineImpl.TAG, e.fillInStackTrace());
                }
            }
        });
    }

    public void handleNewRootviewToScene(final String sceneId, final List<View> views, final VuiPriority vuiPriority) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.13
            @Override // java.lang.Runnable
            public void run() {
                String str;
                if (views == null || (str = sceneId) == null) {
                    return;
                }
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(str);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "handleNewRootviewToScene:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, vuiPriority, TaskDispatcher.TaskType.ADD, (List<View>) views, true));
            }
        });
    }

    public void removeOtherRootViewFromScene(final String sceneId, final List<View> viewList) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.14
            @Override // java.lang.Runnable
            public void run() {
                String str = sceneId;
                if (str == null) {
                    return;
                }
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(str);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "removeOtherRootViewFromScene:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.REMOVE, viewList));
            }
        });
    }

    public void removeOtherRootViewFromScene(final String sceneId) {
        Handler handler;
        if (VuiUtils.cannotUpload() || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.15
            @Override // java.lang.Runnable
            public void run() {
                String str = sceneId;
                if (str == null) {
                    return;
                }
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(str);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "removeOtherRootViewFromScene:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.REMOVE, null));
            }
        });
    }

    public void addSceneElementGroup(final View rootView, final String sceneId, final VuiPriority priority, final IVuiSceneListener listener) {
        Handler handler;
        if (VuiUtils.cannotUpload() || rootView == null || sceneId == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.16
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "addSceneElementGroup:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, (String) null, priority, listener, rootView));
            }
        });
    }

    public void addSceneElement(final View view, final String parentElementId, final String sceneId) {
        Handler handler;
        if (VuiUtils.cannotUpload() || view == null || sceneId == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.17
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "addSceneElement:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, parentElementId, (VuiPriority) null, (IVuiSceneListener) null, view));
            }
        });
    }

    public void removeSceneElementGroup(final String elementGroupId, final String sceneId, final IVuiSceneListener listener) {
        Handler handler;
        if (VuiUtils.cannotUpload() || sceneId == null || elementGroupId == null || (handler = this.mHandler) == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.18
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "removeSceneElementGroup:" + sceneId);
                VuiEngineImpl.this.taskStructure.dispatchTask(VuiEngineImpl.this.structureViewWrapper(newSceneId, TaskDispatcher.TaskType.REMOVE, elementGroupId, listener));
            }
        });
    }

    public void removeVuiElement(final String sceneId, final String elementId) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(sceneId) || TextUtils.isEmpty(elementId)) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.19
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (!VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    return;
                }
                LogUtils.logInfo(VuiEngineImpl.TAG, "removeVuiElement:" + sceneId + ",elementId:" + elementId);
                VuiSceneManager instance = VuiSceneManager.instance();
                int i = VuiSceneManager.TYPE_REMOVE;
                instance.sendSceneData(i, true, sceneId + "," + elementId);
            }
        });
    }

    public void dispatchVuiEvent(final String vuiEvent, final String data) {
        Handler handler = this.mDispatherHandler;
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.20
            @Override // java.lang.Runnable
            public void run() {
                if (!VuiUtils.canUseVuiFeature()) {
                    return;
                }
                LogUtils.logDebug(VuiEngineImpl.TAG, "dispatchVuiEvent:" + vuiEvent);
                if (vuiEvent.equals("disable.vui.feature")) {
                    VuiSceneManager.instance().setFeatureState(false);
                    VuiUtils.disableVuiFeature();
                } else if (vuiEvent.equals("enable.vui.feature")) {
                    VuiUtils.enableVuiFeature();
                    VuiSceneManager.instance().setFeatureState(true);
                } else if (vuiEvent.equals("jarvis.dm.start")) {
                    VuiSceneManager.instance().setInSpeech(true);
                    VuiEngineImpl.this.sendVuiStateChangedEvent();
                } else if (vuiEvent.equals("jarvis.dm.end")) {
                    VuiSceneManager.instance().setInSpeech(false);
                    VuiEngineImpl.this.sendVuiStateChangedEvent();
                } else if (vuiEvent.equals(VuiConstants.REBUILD_EVENT) || vuiEvent.equals("scene.rebuild")) {
                    if (VuiUtils.cannotUpload() || TextUtils.isEmpty(data) || !VuiUtils.getPackageNameFromSceneId(data).equals(VuiEngineImpl.this.mPackageName)) {
                        return;
                    }
                    VuiSceneBuildCache cache = (VuiSceneBuildCache) VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    List<VuiElement> cacheElements = cache.getCache(data);
                    if (cacheElements != null && !cacheElements.isEmpty()) {
                        VuiScene vuiScene = VuiEngineImpl.this.getNewVuiScene(data, System.currentTimeMillis());
                        vuiScene.setElements(cacheElements);
                        VuiSceneCacheFactory.instance().removeAllCache(data);
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_BUILD, true, vuiScene);
                        return;
                    }
                    IVuiSceneListener listener = VuiSceneManager.instance().getVuiSceneListener(data);
                    if (listener != null) {
                        listener.onBuildScene();
                    }
                } else if (!VuiUtils.cannotUpload() && !TextUtils.isEmpty(data)) {
                    VuiEngineImpl.this.eventDispatcher.dispatch(vuiEvent, data);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendVuiStateChangedEvent() {
        final IVuiSceneListener listener;
        if (VuiUtils.cannotUpload()) {
            return;
        }
        try {
            List<String> localEnterSceneStack = this.mEnterSceneStack;
            for (int i = 0; i < localEnterSceneStack.size(); i++) {
                String sceneId = localEnterSceneStack.get(i);
                if (!TextUtils.isEmpty(sceneId) && (listener = VuiSceneManager.instance().getVuiSceneListener(sceneId)) != null) {
                    this.mainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.21
                        @Override // java.lang.Runnable
                        public void run() {
                            listener.onVuiStateChanged();
                        }
                    });
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "sendVuiStateChangedEvent error");
        }
    }

    public String getElementState(String sceneId, String elementId) {
        if (VuiUtils.cannotUpload()) {
            return null;
        }
        LogUtils.logDebug(TAG, "getElementState:" + sceneId + ",elementId:" + elementId);
        if (getJSONType(elementId)) {
            return VuiSceneManager.instance().checkScrollSubViewIsVisible(sceneId, elementId);
        }
        return VuiSceneManager.instance().getElementState(sceneId, elementId);
    }

    public static boolean getJSONType(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String str2 = str.trim();
        if (str2.startsWith("{") && str2.endsWith("}")) {
            return true;
        }
        if (!str2.startsWith(NavigationBarInflaterView.SIZE_MOD_START) || !str2.endsWith(NavigationBarInflaterView.SIZE_MOD_END)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VuiScene getNewVuiScene(String sceneId, long timeStemp) {
        return new VuiScene.Builder().sceneId(sceneId).appVersion(this.mPackageVersion).packageName(this.mPackageName).timestamp(timeStemp).build();
    }

    public void vuiFeedback(View view, VuiFeedback vuiResult) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiSceneManager.instance().vuiFeedBack(view, vuiResult);
    }

    public void vuiFeedback(String id, VuiFeedback vuiResult) {
        if (VuiUtils.cannotUpload()) {
            return;
        }
        VuiSceneManager.instance().vuiFeedBack(id, vuiResult);
    }

    public void subscribe(final String observer) {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.22
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribe:" + observer);
                    VuiSceneManager.instance().subscribe(observer);
                }
            });
        }
    }

    public void subscribeVuiFeature() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.23
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribeVuiFeature");
                    VuiSceneManager.instance().subscribeVuiFeature();
                }
            });
        }
    }

    public void unSubscribeVuiFeature() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.24
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "subscribeVuiFeature");
                    VuiSceneManager.instance().unSubscribeVuiFeature();
                }
            });
        }
    }

    public void unSubscribe() {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.25
                @Override // java.lang.Runnable
                public void run() {
                    LogUtils.logInfo(VuiEngineImpl.TAG, "unSubscribe");
                    VuiSceneManager.instance().unSubscribe();
                }
            });
        }
    }

    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener, boolean needBuild) {
        addVuiSceneListener(sceneId, rootView, listener, elementChangedListener, needBuild, false);
    }

    public void addDupVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener, boolean needBuild) {
        addVuiSceneListener(sceneId, rootView, listener, elementChangedListener, needBuild, true);
    }

    public void addVuiSceneListener(final String sceneId, final View rootView, final IVuiSceneListener listener, final IVuiElementChangedListener elementChangedListener, final boolean needBuild, final boolean isDupActivity) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || (handler = this.mHandler) == null || sceneId == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.26
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.logDebug(VuiEngineImpl.TAG, "addVuiSceneListener :" + sceneId);
                String newSceneId = sceneId;
                if (isDupActivity) {
                    newSceneId = listener.toString() + "-" + sceneId;
                }
                VuiSceneManager.instance().addVuiSceneListener(VuiEngineImpl.this.getSceneUnqiueId(newSceneId), rootView, listener, elementChangedListener, needBuild);
            }
        });
    }

    public void removeDupVuiSceneListener(String sceneId, IVuiSceneListener listener, boolean keepCache) {
        if (listener != null && !TextUtils.isEmpty(sceneId)) {
            removeVuiSceneListener(listener.toString() + "-" + sceneId, listener, keepCache);
        }
    }

    public void removeVuiSceneListener(final String sceneId, final IVuiSceneListener listener, final boolean keepCache) {
        Handler handler;
        if (VuiUtils.canUseVuiFeature() && (handler = this.mHandler) != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.27
                @Override // java.lang.Runnable
                public void run() {
                    String str = sceneId;
                    if (str == null) {
                        return;
                    }
                    String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(str);
                    VuiSceneInfo info = VuiSceneManager.instance().getSceneInfo(newSceneId);
                    boolean isupload = false;
                    if (info != null && info.isWholeScene()) {
                        isupload = true;
                    }
                    if (listener != null && info != null && info.getListener() != null && !listener.equals(info.getListener())) {
                        LogUtils.w(VuiEngineImpl.TAG, "要销毁的场景和目前持有的场景数据不一致");
                        return;
                    }
                    if (!newSceneId.equals(VuiEngineImpl.mActiveSceneId)) {
                        if (VuiEngineImpl.this.mEnterSceneStack.contains(newSceneId)) {
                            VuiEngineImpl.this.mEnterSceneStack.remove(newSceneId);
                        }
                    } else {
                        VuiEngineImpl.this.exitScene(sceneId, info == null ? true : info.isWholeScene());
                    }
                    LogUtils.logDebug(VuiEngineImpl.TAG, "removeVuiSceneListener :" + sceneId + ",isupload:" + isupload + ",keepCache:" + keepCache);
                    VuiEngineImpl.this.taskStructure.removeTask(newSceneId);
                    VuiSceneManager.instance().removeVuiSceneListener(newSceneId, isupload, keepCache, listener);
                }
            });
        }
    }

    public void setVuiElementTag(View tagView, String tag) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            String tag2 = "4657_" + tag;
            tagView.setTag(tag2);
            if (tagView instanceof IVuiElement) {
                ((IVuiElement) tagView).setVuiElementId(tag2);
            }
        }
    }

    public String getVuiElementTag(View view) {
        if (VuiUtils.canUseVuiFeature() && view != null) {
            String tag = (String) view.getTag();
            if (tag.startsWith("4657")) {
                return tag;
            }
            return null;
        }
        return null;
    }

    public void setVuiElementUnSupportTag(View tagView, boolean isUnSupport) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.vuiElementUnSupport, Boolean.valueOf(isUnSupport));
        }
    }

    public void setVuiCustomDisableControlTag(View tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.customDisableControl, true);
        }
    }

    public void setHasFeedBackTxtByViewDisable(View tagView, String tts) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.customDisableFeedbackTTS, tts);
        }
    }

    public void setVuiCustomDisableFeedbackTag(View tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.customDisableFeedback, true);
        }
    }

    public void setVuiElementDefaultAction(View tagView, String action, Object value) {
        if (!VuiUtils.canUseVuiFeature() || tagView == null || action == null || value == null) {
            return;
        }
        try {
            JSONObject actionObj = new JSONObject();
            VuiUtils.generateElementValueJSON(actionObj, action, value);
            tagView.setTag(R.id.vuiElementDefaultAction, actionObj);
        } catch (Exception e) {
            LogUtils.e(TAG, e.fillInStackTrace());
        }
    }

    public void setVuiStatefulButtonClick(View tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.vuiStatefulButtonClick, true);
        }
    }

    public void disableChildVuiAttrWhenInvisible(View tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.disableChildVuiAttrsWhenInvisible, true);
        }
    }

    public void setVuiLabelUnSupportText(View... tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            for (View tagV : tagView) {
                tagV.setTag(R.id.vuiLabelUnSupportText, true);
            }
        }
    }

    public void setVuiElementVisibleTag(View tagView, boolean isVisible) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.vuiElementVisible, Boolean.valueOf(isVisible));
        }
    }

    public Boolean getVuiElementVisibleTag(View tagView) {
        Boolean isVisible;
        if (VuiUtils.canUseVuiFeature() && tagView != null && (isVisible = (Boolean) tagView.getTag(R.id.vuiElementVisible)) != null) {
            return isVisible;
        }
        return null;
    }

    public void disableVuiFeature() {
        LogUtils.logInfo(TAG, "user disable feature");
        VuiUtils.userSetFeatureState(true);
    }

    public void enableVuiFeature() {
        LogUtils.logInfo(TAG, "user enable feature");
        VuiUtils.userSetFeatureState(false);
    }

    public boolean isVuiFeatureDisabled() {
        return VuiUtils.cannotUpload();
    }

    public boolean isInSpeech() {
        if (VuiUtils.cannotUpload()) {
            return false;
        }
        return VuiSceneManager.instance().isInSpeech();
    }

    public String getVuiElementId(String fatherId, int position, String viewId) {
        if (fatherId != null) {
            viewId = fatherId + "_" + viewId;
        }
        if (position != -1) {
            return viewId + "_" + position;
        }
        return viewId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, TaskDispatcher.TaskType taskType, String vuiElementGroupId, IVuiSceneListener listener) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        listViewWrapper.add(new TaskWrapper((View) null, sceneId, taskType, listener, vuiElementGroupId));
        return listViewWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, VuiPriority priority, TaskDispatcher.TaskType taskType, List<View> viewList, boolean containNotChildrenView) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        listViewWrapper.add(new TaskWrapper(sceneId, priority, taskType, viewList, containNotChildrenView));
        return listViewWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<Integer> customizeIds, IVuiElementListener listener, List<String> subSceneIdList, List<View> viewList, boolean isWholeScene, ISceneCallbackHandler mSceneCallbackHandler) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        if (viewList == null || viewList.size() == 0) {
            return listViewWrapper;
        }
        if (viewList.size() == 1) {
            listViewWrapper.add(new TaskWrapper(viewList.get(0), sceneId, viewList.get(0).getId(), taskType, customizeIds, listener, subSceneIdList, isWholeScene, mSceneCallbackHandler));
        } else {
            listViewWrapper.add(new TaskWrapper(viewList, sceneId, taskType, customizeIds, listener, subSceneIdList, isWholeScene, mSceneCallbackHandler));
        }
        return listViewWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<Integer> ids, IVuiElementListener callback, List<View> viewlist) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        if (viewlist == null || viewlist.isEmpty()) {
            return listViewWrapper;
        }
        if (viewlist.size() == 1) {
            View view = viewlist.get(0);
            if (view.getId() != -1) {
                listViewWrapper.add(new TaskWrapper(view, sceneId, view.getId(), taskType, ids, callback));
            } else {
                listViewWrapper.add(new TaskWrapper(view, sceneId, taskType, ids, callback));
            }
        } else {
            listViewWrapper.add(new TaskWrapper(viewlist, sceneId, taskType, ids, callback));
        }
        return listViewWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<View> viewList) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        if (viewList != null && viewList.size() == 1) {
            View view = viewList.get(0);
            if (view.getId() != -1) {
                listViewWrapper.add(new TaskWrapper(view, view.getId(), sceneId, taskType));
            } else {
                listViewWrapper.add(new TaskWrapper(sceneId, taskType, view));
            }
        } else {
            listViewWrapper.add(new TaskWrapper(sceneId, taskType, viewList));
        }
        return listViewWrapper;
    }

    private List<TaskWrapper> structureViewWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<View> viewList, RecyclerView recyclerView) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        if (viewList != null && viewList.size() == 1) {
            View view = viewList.get(0);
            if (view.getId() != -1) {
                listViewWrapper.add(new TaskWrapper(view, view.getId(), sceneId, taskType, recyclerView));
            } else {
                listViewWrapper.add(new TaskWrapper(sceneId, taskType, view, recyclerView));
            }
        } else {
            listViewWrapper.add(new TaskWrapper(sceneId, taskType, viewList, recyclerView));
        }
        return listViewWrapper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<TaskWrapper> structureViewWrapper(String sceneId, String parentElementId, VuiPriority priority, IVuiSceneListener listener, View... views) {
        List<TaskWrapper> listViewWrapper = new ArrayList<>();
        for (View view : views) {
            if (view != null) {
                if (priority == null) {
                    listViewWrapper.add(new TaskWrapper(view, sceneId, view.getId(), TaskDispatcher.TaskType.ADD, parentElementId));
                } else {
                    listViewWrapper.add(new TaskWrapper(view, sceneId, view.getId(), TaskDispatcher.TaskType.ADD, priority, listener));
                }
            }
        }
        return listViewWrapper;
    }

    public VuiScene createVuiScene(String sceneId, long time) {
        return new VuiScene.Builder().sceneId(getSceneUnqiueId(sceneId)).appVersion(this.mPackageVersion).packageName(this.mPackageName).timestamp(time).build();
    }

    public void setLoglevel(int loglevel) {
        LogUtils.setLogLevel(loglevel);
    }

    public void addVuiEventListener(final String sceneId, final IVuiEventListener listener) {
        Handler handler;
        if (!VuiUtils.canUseVuiFeature() || (handler = this.mHandler) == null || sceneId == null) {
            return;
        }
        handler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.28
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.logDebug(VuiEngineImpl.TAG, "addVuiEventListener :" + sceneId);
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                VuiSceneManager.instance().addVuiEventListener(newSceneId, listener);
            }
        });
    }

    public void disableViewVuiMode() {
        LogUtils.logInfo(TAG, "user disable view's vui mode");
        VuiUtils.userDisableViewMode();
    }

    public void setExecuteVirtualTag(View tagView, String action) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            if (TextUtils.isEmpty(action)) {
                tagView.setTag(R.id.executeVirtualId, VuiConstants.VIRTUAL_LIST_ID);
                return;
            }
            int i = R.id.executeVirtualId;
            tagView.setTag(i, "10000_" + action);
        }
    }

    public void setVirtualResourceNameTag(View tagView, String name) {
        if (VuiUtils.canUseVuiFeature() && tagView != null && !TextUtils.isEmpty(name)) {
            tagView.setTag(R.id.virtualResourceName, name);
        }
    }

    public void setCustomDoActionTag(View tagView) {
        if (VuiUtils.canUseVuiFeature() && tagView != null) {
            tagView.setTag(R.id.customDoAction, true);
        }
    }

    public void setProcessName(String processName) {
        VuiSceneManager.instance().setProcessName(processName);
    }

    public void init(String observer) {
        subscribe(this.mPackageName + "." + observer);
    }

    public void addVuiSceneListener(String sceneId, IVuiSceneListener listener) {
        addVuiSceneListener(sceneId, null, listener, null, true);
    }

    public void setBuildElements(final String sceneId, final List<VuiElement> elements) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(sceneId) || elements == null || elements.isEmpty()) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.29
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                int state = VuiSceneManager.instance().getVuiSceneState(newSceneId);
                if (state == VuiSceneState.INIT.getState()) {
                    LogUtils.e(VuiEngineImpl.TAG, sceneId + "场景数据的创建必须在场景被激活后");
                    return;
                }
                LogUtils.i(VuiEngineImpl.TAG, "newSceneId:" + newSceneId + ",elements:" + elements);
                VuiScene vuiScene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                vuiScene.setElements(elements);
                VuiSceneInfo info = VuiSceneManager.instance().getSceneInfo(newSceneId);
                if (info != null) {
                    info.setBuild(true);
                    info.setBuildComplete(true);
                }
                VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                if (buildCache != null) {
                    buildCache.setCache(newSceneId, elements);
                }
                VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_BUILD, false, vuiScene);
            }
        });
    }

    public void setUpdateElements(final String sceneId, final List<VuiElement> elements) {
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(sceneId) || elements == null || elements.isEmpty()) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.vui.VuiEngineImpl.30
            @Override // java.lang.Runnable
            public void run() {
                String newSceneId = VuiEngineImpl.this.getSceneUnqiueId(sceneId);
                if (VuiSceneManager.instance().canUpdateScene(newSceneId)) {
                    VuiScene vuiScene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (buildCache != null) {
                        List<VuiElement> resultElement = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i++) {
                            VuiElement element = (VuiElement) elements.get(i);
                            VuiElement targetElement = buildCache.getVuiElementById(newSceneId, element.getId());
                            if (targetElement == null || !targetElement.equals(element)) {
                                if (element.getElements() == null && targetElement.getElements() != null) {
                                    element.setElements(targetElement.getElements());
                                }
                                resultElement.add(element);
                            }
                        }
                        if (resultElement.isEmpty()) {
                            return;
                        }
                        vuiScene.setElements(resultElement);
                        List<VuiElement> fusionElement = buildCache.getFusionCache(newSceneId, resultElement, false);
                        buildCache.setCache(newSceneId, fusionElement);
                        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                            VuiScene scene = VuiEngineImpl.this.getNewVuiScene(newSceneId, System.currentTimeMillis());
                            scene.setElements(fusionElement);
                            LogUtils.logDebug(VuiEngineImpl.TAG, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(scene));
                        }
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene);
                    }
                }
            }
        });
    }

    public VuiElement getVuiElement(String sceneId, String id) {
        VuiSceneCache cache;
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(id) || TextUtils.isEmpty(sceneId) || (cache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType())) == null) {
            return null;
        }
        return cache.getVuiElementById(getSceneUnqiueId(sceneId), id);
    }

    public VuiScene getVuiScene(String sceneId) {
        List<VuiElement> elements;
        if (VuiUtils.cannotUpload() || TextUtils.isEmpty(sceneId)) {
            return null;
        }
        VuiScene vuiScene = createVuiScene(sceneId, System.currentTimeMillis());
        VuiSceneCache cache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        if (cache != null && (elements = cache.getCache(vuiScene.getSceneId())) != null && elements.isEmpty()) {
            vuiScene.setElements(elements);
        }
        return vuiScene;
    }

    public boolean isSpeechShowNumber() {
        if (VuiUtils.cannotUpload()) {
            return false;
        }
        return VuiSceneManager.instance().isInSpeech();
    }

    public String getActiveSceneId() {
        return mActiveSceneId;
    }
}
