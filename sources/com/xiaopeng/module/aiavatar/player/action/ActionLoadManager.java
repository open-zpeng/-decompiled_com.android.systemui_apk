package com.xiaopeng.module.aiavatar.player.action;

import android.util.Log;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes23.dex */
public class ActionLoadManager {
    private static final String TAG = "ActionLoadManager";
    private static final ActionLoadManager sInstanc = new ActionLoadManager();
    private Map<String, String> mActionMap;
    private Runnable mActionTask;
    private AssetManager mAssetManager;
    private ModelInstance mAvatarModel;
    private Runnable mCurTask;
    private final String ACTION_MODEL_PATH = "action/";
    private final String ACTION_MODEL_SUFFIX = ".g3db";
    private boolean mIsInit = false;
    private String[] mReadyActions = {"Anima_7_v5_zuohuanxingB", "shuohuazuo", "Anima_0_zuohuizheng", "Anima_7_v5_youhuanxingB", "shuohuayou", "Anima_0_youhuizheng"};

    public static ActionLoadManager getInstance() {
        return sInstanc;
    }

    public void init(ModelInstance avatarModel) {
        this.mAvatarModel = avatarModel;
        this.mAssetManager = new AssetManager();
        this.mActionMap = new HashMap();
        loadReadyAction();
        this.mIsInit = true;
    }

    private void loadReadyAction() {
        int i = 0;
        while (true) {
            String[] strArr = this.mReadyActions;
            if (i < strArr.length) {
                String mReadyAction = strArr[i];
                if (this.mAvatarModel.getAnimation(mReadyAction) == null) {
                    loadAction(mReadyAction, null);
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadAction(final String actionId, final Runnable task) {
        Log.d(TAG, "loadAction:" + actionId);
        String path = "action/" + actionId + ".g3db";
        this.mActionMap.put(actionId, path);
        ModelLoader.ModelParameters parameter = new ModelLoader.ModelParameters();
        parameter.loadedCallback = new AssetLoaderParameters.LoadedCallback() { // from class: com.xiaopeng.module.aiavatar.player.action.ActionLoadManager.1
            @Override // com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback
            public void finishedLoading(AssetManager assetManager, String s, Class aClass) {
                Log.d(ActionLoadManager.TAG, "finishedLoading:" + s);
                Model model = (Model) assetManager.get(s, Model.class);
                if (model != null) {
                    ActionLoadManager.this.mAvatarModel.copyAnimation(model.getAnimation(actionId));
                    model.dispose();
                    if (task != null && ActionLoadManager.this.mCurTask == task) {
                        ActionLoadManager.this.mCurTask.run();
                        ActionLoadManager.this.mCurTask = null;
                    }
                }
                ActionLoadManager.this.mActionMap.remove(actionId);
            }
        };
        try {
            this.mAssetManager.load(path, Model.class, parameter);
        } catch (Exception e) {
            Log.d(TAG, "loadAction:fail");
            this.mActionMap.remove(actionId);
            this.mCurTask = null;
        }
    }

    public void task(final String actionId, final Runnable task) {
        this.mCurTask = task;
        this.mActionTask = new Runnable() { // from class: com.xiaopeng.module.aiavatar.player.action.ActionLoadManager.2
            @Override // java.lang.Runnable
            public void run() {
                ActionLoadManager.this.loadAction(actionId, task);
            }
        };
    }

    public void tryLoad() {
        Runnable runnable = this.mActionTask;
        if (runnable != null) {
            runnable.run();
            this.mActionTask = null;
        }
        update();
    }

    public void update() {
        if (this.mActionMap.size() > 0) {
            try {
                this.mAssetManager.update();
            } catch (Exception e) {
                Log.d(TAG, "update:fail");
            }
        }
    }

    public boolean isInit() {
        return this.mIsInit;
    }
}
