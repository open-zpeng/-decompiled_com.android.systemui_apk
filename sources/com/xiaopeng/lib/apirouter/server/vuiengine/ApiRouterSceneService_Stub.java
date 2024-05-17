package com.xiaopeng.lib.apirouter.server.vuiengine;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.server.TransactTranslator;
import com.xiaopeng.speech.apirouter.ApiRouterSceneService;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes22.dex */
public class ApiRouterSceneService_Stub extends Binder implements IInterface {
    public ApiRouterSceneService provider = new ApiRouterSceneService();
    public ApiRouterSceneService_Manifest manifest = new ApiRouterSceneService_Manifest();

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == 0) {
            data.enforceInterface(ApiRouterSceneService_Manifest.DESCRIPTOR);
            Uri uri = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                String _real0 = (String) TransactTranslator.read(uri.getQueryParameter(VuiConstants.SCENE_ID), "java.lang.String");
                String _real1 = (String) TransactTranslator.read(uri.getQueryParameter("elementId"), "java.lang.String");
                String ret = this.provider.getElementState(_real0, _real1);
                reply.writeNoException();
                TransactTranslator.reply(reply, ret);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                reply.writeException(new IllegalStateException(e.getMessage()));
                return true;
            }
        } else if (code != 1) {
            if (code == 1598968902) {
                reply.writeString(ApiRouterSceneService_Manifest.DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        } else {
            data.enforceInterface(ApiRouterSceneService_Manifest.DESCRIPTOR);
            Uri uri2 = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                String _real02 = (String) TransactTranslator.read(uri2.getQueryParameter("event"), "java.lang.String");
                String _real12 = (String) TransactTranslator.read(uri2.getQueryParameter("data"), "java.lang.String");
                this.provider.onEvent(_real02, _real12);
                reply.writeNoException();
                TransactTranslator.reply(reply, null);
                return true;
            } catch (Exception e2) {
                e2.printStackTrace();
                reply.writeException(new IllegalStateException(e2.getMessage()));
                return true;
            }
        }
    }
}
