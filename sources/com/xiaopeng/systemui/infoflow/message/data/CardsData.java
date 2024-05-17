package com.xiaopeng.systemui.infoflow.message.data;

import java.util.List;
/* loaded from: classes24.dex */
public interface CardsData {

    /* loaded from: classes24.dex */
    public interface LoadEntriesCallback {
        void onDataNotAvailable();

        void onEntriesLoaded(List<CardEntry> list);
    }

    void addCard(CardEntry cardEntry, LoadEntriesCallback loadEntriesCallback);

    CardEntry getCard(int i);

    CardEntry getCard(String str);

    List<CardEntry> getCards();

    void removeCard(CardEntry cardEntry, LoadEntriesCallback loadEntriesCallback);

    void removeNotification(String str, LoadEntriesCallback loadEntriesCallback);
}
