package com.badlogic.gdx.net;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Disposable;
/* loaded from: classes21.dex */
public interface ServerSocket extends Disposable {
    Socket accept(SocketHints socketHints);

    Net.Protocol getProtocol();
}
