package com.xiaopeng.systemui.server;

import android.os.RemoteException;
import android.util.Log;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.xuimanager.systemui.masklayer.IMaskLayerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes24.dex */
class MaskLayerServer extends BaseServer {
    private final String TAG;
    private final HashMap<Integer, ArrayList<IMaskLayerListener>> mMaskLayerQueue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public int showMaskLayer(IMaskLayerListener callback, boolean isStackWindow, int screenId) throws RemoteException {
        Log.i("XMaskLayerRemoteServiceMethod", "requestShow: callback = " + callback + "   isStackWindow = " + isStackWindow + "  screenId = " + screenId);
        if (isStackWindow) {
            synchronized (this.mMaskLayerQueue) {
                ArrayList<IMaskLayerListener> screenList = this.mMaskLayerQueue.get(Integer.valueOf(screenId));
                if (screenList == null) {
                    screenList = new ArrayList<>();
                }
                try {
                    callback.showMaskLayer((int) WindowHelper.TYPE_WATER_MARK);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                screenList.add(callback);
                this.mMaskLayerQueue.put(Integer.valueOf(screenId), screenList);
            }
            return 1;
        }
        synchronized (this.mMaskLayerQueue) {
            ArrayList<IMaskLayerListener> screenList2 = this.mMaskLayerQueue.get(Integer.valueOf(screenId));
            if (screenList2 == null) {
                screenList2 = new ArrayList<>();
            }
            while (screenList2.size() != 0) {
                try {
                    screenList2.get(0).dismissMaskLayer();
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
            }
            try {
                callback.showMaskLayer((int) WindowHelper.TYPE_WATER_MARK);
            } catch (RemoteException e3) {
                e3.printStackTrace();
            }
            screenList2.add(callback);
            this.mMaskLayerQueue.put(Integer.valueOf(screenId), screenList2);
        }
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int dismissMaskLayer(IMaskLayerListener callback, int screenId) throws RemoteException {
        Log.i("XMaskLayerRemoteServiceMethod", String.format("dismissMaskLayer callback:%s, screenId: %s", callback, Integer.valueOf(screenId)));
        IMaskLayerListener tn = null;
        ArrayList<IMaskLayerListener> tnList = this.mMaskLayerQueue.get(Integer.valueOf(screenId));
        if (tnList != null) {
            Iterator<IMaskLayerListener> it = tnList.iterator();
            while (it.hasNext()) {
                IMaskLayerListener tnIdItem = it.next();
                if (Objects.equals(callback.asBinder(), tnIdItem.asBinder())) {
                    tn = tnIdItem;
                }
            }
            tnList.remove(tn);
            this.mMaskLayerQueue.put(Integer.valueOf(screenId), tnList);
        }
        return 1;
    }

    @Override // com.xiaopeng.systemui.server.BaseServer
    protected String logTag() {
        return "MaskLayer";
    }

    /* loaded from: classes24.dex */
    private static class Holder {
        private static final MaskLayerServer sInstance = new MaskLayerServer();

        private Holder() {
        }
    }

    private MaskLayerServer() {
        this.TAG = "XMaskLayerRemoteServiceMethod";
        this.mMaskLayerQueue = new HashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MaskLayerServer get() {
        return Holder.sInstance;
    }
}
