package com.badlogic.gdx.backends.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.NetJavaImpl;
import com.badlogic.gdx.net.NetJavaServerSocketImpl;
import com.badlogic.gdx.net.NetJavaSocketImpl;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
/* loaded from: classes21.dex */
public class AndroidNet implements Net {
    final AndroidApplicationBase app;
    NetJavaImpl netJavaImpl = new NetJavaImpl();

    public AndroidNet(AndroidApplicationBase app) {
        this.app = app;
    }

    @Override // com.badlogic.gdx.Net
    public void sendHttpRequest(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener) {
        this.netJavaImpl.sendHttpRequest(httpRequest, httpResponseListener);
    }

    @Override // com.badlogic.gdx.Net
    public void cancelHttpRequest(Net.HttpRequest httpRequest) {
        this.netJavaImpl.cancelHttpRequest(httpRequest);
    }

    @Override // com.badlogic.gdx.Net
    public ServerSocket newServerSocket(Net.Protocol protocol, String hostname, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, hostname, port, hints);
    }

    @Override // com.badlogic.gdx.Net
    public ServerSocket newServerSocket(Net.Protocol protocol, int port, ServerSocketHints hints) {
        return new NetJavaServerSocketImpl(protocol, port, hints);
    }

    @Override // com.badlogic.gdx.Net
    public Socket newClientSocket(Net.Protocol protocol, String host, int port, SocketHints hints) {
        return new NetJavaSocketImpl(protocol, host, port, hints);
    }

    @Override // com.badlogic.gdx.Net
    public boolean openURI(String URI) {
        final Uri uri = Uri.parse(URI);
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        PackageManager pm = this.app.getContext().getPackageManager();
        if (pm.resolveActivity(intent, 65536) == null) {
            return false;
        }
        this.app.runOnUiThread(new Runnable() { // from class: com.badlogic.gdx.backends.android.AndroidNet.1
            @Override // java.lang.Runnable
            public void run() {
                Intent intent2 = new Intent("android.intent.action.VIEW", uri);
                if (!(AndroidNet.this.app.getContext() instanceof Activity)) {
                    intent2.addFlags(268435456);
                }
                AndroidNet.this.app.startActivity(intent2);
            }
        });
        return true;
    }
}
