package com.xiaopeng.lib.apirouter.server;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.systemui.ApiRouterRemoteService;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes22.dex */
public class ApiRouterRemoteService_Stub extends Binder implements IInterface {
    public ApiRouterRemoteService provider = new ApiRouterRemoteService();
    public ApiRouterRemoteService_Manifest manifest = new ApiRouterRemoteService_Manifest();

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code != 0) {
            if (code == 1598968902) {
                reply.writeString(ApiRouterRemoteService_Manifest.DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }
        data.enforceInterface(ApiRouterRemoteService_Manifest.DESCRIPTOR);
        Uri uri = (Uri) Uri.CREATOR.createFromParcel(data);
        try {
            String _real0 = (String) TransactTranslator.read(uri.getQueryParameter("event"), "java.lang.String");
            String _real1 = (String) TransactTranslator.read(uri.getQueryParameter(VuiConstants.SCENE_PACKAGE_NAME), "java.lang.String");
            this.provider.updateEvent(_real0, _real1);
            reply.writeNoException();
            TransactTranslator.reply(reply, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            reply.writeException(new IllegalStateException(e.getMessage()));
            return true;
        }
    }
}
