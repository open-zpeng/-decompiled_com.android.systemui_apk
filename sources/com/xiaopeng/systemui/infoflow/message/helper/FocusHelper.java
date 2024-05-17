package com.xiaopeng.systemui.infoflow.message.helper;

import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
/* loaded from: classes24.dex */
public class FocusHelper {
    private static String mFocusItemKey = "";

    public static void saveFocusItem(int focusPosition) {
        int cardsSize = CardListData.getInstance().getCards().size();
        if (focusPosition < 0 || focusPosition > cardsSize - 1) {
            mFocusItemKey = "";
            return;
        }
        CardEntry item = CardListData.getInstance().getCards().get(focusPosition);
        mFocusItemKey = item.key;
    }

    public static String getFocusItemKey() {
        return mFocusItemKey;
    }
}
