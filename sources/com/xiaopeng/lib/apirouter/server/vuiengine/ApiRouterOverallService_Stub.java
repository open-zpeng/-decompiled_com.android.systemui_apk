package com.xiaopeng.lib.apirouter.server.vuiengine;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.lib.apirouter.server.TransactTranslator;
import com.xiaopeng.speech.apirouter.ApiRouterOverallService;
/* loaded from: classes22.dex */
public class ApiRouterOverallService_Stub extends Binder implements IInterface {
    public ApiRouterOverallService provider = new ApiRouterOverallService();
    public ApiRouterOverallService_Manifest manifest = new ApiRouterOverallService_Manifest();

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == 0) {
            data.enforceInterface(ApiRouterOverallService_Manifest.DESCRIPTOR);
            Uri uri = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                String _real0 = (String) TransactTranslator.read(uri.getQueryParameter("event"), "java.lang.String");
                String _real1 = (String) TransactTranslator.read(uri.getQueryParameter("data"), "java.lang.String");
                this.provider.onEvent(_real0, _real1);
                reply.writeNoException();
                TransactTranslator.reply(reply, null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                reply.writeException(new IllegalStateException(e.getMessage()));
                return true;
            }
        } else if (code != 1) {
            if (code == 1598968902) {
                reply.writeString(ApiRouterOverallService_Manifest.DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        } else {
            data.enforceInterface(ApiRouterOverallService_Manifest.DESCRIPTOR);
            Uri uri2 = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                String _real02 = (String) TransactTranslator.read(uri2.getQueryParameter("event"), "java.lang.String");
                String _real12 = (String) TransactTranslator.read(uri2.getQueryParameter("data"), "java.lang.String");
                String _real2 = (String) TransactTranslator.read(uri2.getQueryParameter("callback"), "java.lang.String");
                this.provider.onQuery(_real02, _real12, _real2);
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
