package com.xiaopeng.systemui.server;

import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.xuimanager.systemui.ISystemUIService;
import com.xiaopeng.xuimanager.systemui.SysLogUtils;
import com.xiaopeng.xuimanager.systemui.dock.DockItem;
import com.xiaopeng.xuimanager.systemui.dock.IDockEventListener;
import com.xiaopeng.xuimanager.systemui.masklayer.IMaskLayerListener;
import com.xiaopeng.xuimanager.systemui.osd.IOsdListener;
import com.xiaopeng.xuimanager.systemui.systembar.ISystemBarListener;
import com.xiaopeng.xuimanager.systemui.systembar.SystemBarItem;
import java.util.List;
/* loaded from: classes24.dex */
public class SystemUIServer extends ISystemUIService.Stub {
    private static final String TAG = "Server";

    public void init() {
        ServiceManager.addService(SystemUIApplication.TAG, get());
        OsdServer.get().getJSON();
        SysLogUtils.i("Server", "addService");
    }

    public int showOsd(IOsdListener callback, int osdType, String regionId) throws RemoteException {
        return OsdServer.get().showOsd(callback, osdType, regionId);
    }

    public int hideOsd(IOsdListener callback, String regionId) throws RemoteException {
        return OsdServer.get().hideOsd(callback, regionId);
    }

    public int showMaskLayer(IMaskLayerListener callback, boolean isStackWindow, int screenId) throws RemoteException {
        return MaskLayerServer.get().showMaskLayer(callback, isStackWindow, screenId);
    }

    public int dismissMaskLayer(IMaskLayerListener callback, int screenId) throws RemoteException {
        return MaskLayerServer.get().dismissMaskLayer(callback, screenId);
    }

    public int showSystemBar(String pkg, String id, SystemBarItem bar) throws RemoteException {
        return SystemBarServer.get().showSystemBar(pkg, id, bar);
    }

    public int registerSystemBarListener(String pkg, ISystemBarListener listener) throws RemoteException {
        return SystemBarServer.get().registerSystemBarListener(pkg, listener);
    }

    public int unRegisterSystemBarListener(String pkg, ISystemBarListener listener) throws RemoteException {
        return SystemBarServer.get().unRegisterSystemBarListener(pkg, listener);
    }

    public int cancelSystemBar(String pkg, String id) throws RemoteException {
        return SystemBarServer.get().cancelSystemBar(pkg, id);
    }

    public List<DockItem> getDockItems(String s) throws RemoteException {
        return null;
    }

    public List<DockItem> getShortcutAndComponents(String s) throws RemoteException {
        return null;
    }

    public void enterDockEdit(String s) throws RemoteException {
    }

    public boolean canDockEdit(String s) throws RemoteException {
        return false;
    }

    public int registerDockListener(String pkgName, IDockEventListener iDockEventListener) throws RemoteException {
        return 0;
    }

    public int unRegisterDockListener(String pkgName, IDockEventListener iDockEventListener) throws RemoteException {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public static class Holder {
        private static final SystemUIServer sInstance = new SystemUIServer();

        private Holder() {
        }
    }

    private SystemUIServer() {
    }

    public static SystemUIServer get() {
        return Holder.sInstance;
    }
}
