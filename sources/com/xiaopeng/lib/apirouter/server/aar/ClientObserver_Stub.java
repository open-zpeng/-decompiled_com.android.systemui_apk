package com.xiaopeng.lib.apirouter.server.aar;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.xiaopeng.aar.client.ipc.ClientObserver;
import com.xiaopeng.lib.apirouter.ParcelUtils;
import com.xiaopeng.lib.apirouter.server.TransactTranslator;
import com.xiaopeng.speech.vui.constants.VuiConstants;
/* loaded from: classes22.dex */
public class ClientObserver_Stub extends Binder implements IInterface {
    public ClientObserver provider = new ClientObserver();
    public ClientObserver_Manifest manifest = new ClientObserver_Manifest();

    @Override // android.os.IInterface
    public IBinder asBinder() {
        return this;
    }

    @Override // android.os.Binder
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == 0) {
            data.enforceInterface(ClientObserver_Manifest.DESCRIPTOR);
            Uri uri = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                Integer _real0 = (Integer) TransactTranslator.read(uri.getQueryParameter(VuiConstants.ELEMENT_TYPE), "int");
                String _real1 = (String) TransactTranslator.read(uri.getQueryParameter("appId"), "java.lang.String");
                String _real2 = (String) TransactTranslator.read(uri.getQueryParameter("module"), "java.lang.String");
                String _real3 = (String) TransactTranslator.read(uri.getQueryParameter("msgId"), "java.lang.String");
                String _real4 = (String) TransactTranslator.read(uri.getQueryParameter("data"), "java.lang.String");
                this.provider.onReceived(_real0.intValue(), _real1, _real2, _real3, _real4);
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
                reply.writeString(ClientObserver_Manifest.DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        } else {
            data.enforceInterface(ClientObserver_Manifest.DESCRIPTOR);
            Uri uri2 = (Uri) Uri.CREATOR.createFromParcel(data);
            try {
                Integer _real02 = (Integer) TransactTranslator.read(uri2.getQueryParameter(VuiConstants.ELEMENT_TYPE), "int");
                String _real12 = (String) TransactTranslator.read(uri2.getQueryParameter("appId"), "java.lang.String");
                String _real22 = (String) TransactTranslator.read(uri2.getQueryParameter("module"), "java.lang.String");
                String _real32 = (String) TransactTranslator.read(uri2.getQueryParameter("msgId"), "java.lang.String");
                String _real42 = (String) TransactTranslator.read(uri2.getQueryParameter("data"), "java.lang.String");
                byte[] _blob = ParcelUtils.readBlob(data);
                this.provider.onReceivedBlob(_real02.intValue(), _real12, _real22, _real32, _real42, _blob);
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
