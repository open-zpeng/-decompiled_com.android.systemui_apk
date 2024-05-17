package com.xiaopeng.systemui.infoflow.message.contract;

import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import java.util.List;
/* loaded from: classes24.dex */
public interface CardsContract {

    /* loaded from: classes24.dex */
    public interface Presenter {
        void addCardEntry(CardEntry cardEntry);

        void enterCarCheckMode();

        void enterCarCheckMode(CardEntry cardEntry);

        void exitCarCheckMode();

        CardEntry getCardEntry(int i);

        void removeCardEntry(CardEntry cardEntry);

        void removeNotification(String str);
    }

    /* loaded from: classes24.dex */
    public interface View {
        void enterCarCheckMode();

        void exitCarCheckMode();

        void refreshList(List<CardEntry> list);

        void showCarCheckView(CardEntry cardEntry);
    }
}
