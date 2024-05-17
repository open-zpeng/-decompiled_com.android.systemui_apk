package com.xiaopeng.module.aiavatar.system;

import android.util.Log;
import com.xiaopeng.module.aiavatar.base.mvp.BaseModel;
import com.xiaopeng.module.aiavatar.helper.GsonHelper;
import com.xiaopeng.module.aiavatar.mvp.avatar.bean.AvatarBean;
import java.util.HashSet;
import java.util.Iterator;
/* loaded from: classes23.dex */
public class EventDispatcherManager {
    private static EventDispatcherManager instance;
    private HashSet<BaseModel> modelHashSet = new HashSet<>();
    private static String TAG = "EventDispatcherManager";
    private static final Object mLock = new Object();

    private EventDispatcherManager() {
    }

    public static EventDispatcherManager getInstance() {
        EventDispatcherManager eventDispatcherManager;
        synchronized (mLock) {
            if (instance == null) {
                instance = new EventDispatcherManager();
            }
            eventDispatcherManager = instance;
        }
        return eventDispatcherManager;
    }

    public void register(BaseModel model) {
        this.modelHashSet.add(model);
    }

    public void dispatch(String event) {
        try {
            AvatarBean bean = (AvatarBean) GsonHelper.getInstance().getGson().fromJson(event, (Class<Object>) AvatarBean.class);
            if (bean != null) {
                Iterator<BaseModel> it = this.modelHashSet.iterator();
                while (it.hasNext()) {
                    BaseModel baseModel = it.next();
                    baseModel.receiveCmd(bean);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "json string parse error！", e);
        }
    }
}
