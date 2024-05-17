package com.xiaopeng.speech.protocol.node.userbook;

import com.xiaopeng.speech.INodeListener;
import com.xiaopeng.speech.protocol.node.carcontrol.bean.UserBookValue;
/* loaded from: classes23.dex */
public interface UserBookListener extends INodeListener {
    void onCheckUserBook(UserBookValue userBookValue);

    void onCloseUserBook();

    void onOpenUserBook();
}
