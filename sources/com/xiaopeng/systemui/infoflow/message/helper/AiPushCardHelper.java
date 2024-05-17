package com.xiaopeng.systemui.infoflow.message.helper;

import com.xiaopeng.systemui.infoflow.message.data.CardEntry;
import com.xiaopeng.systemui.infoflow.message.data.CardListData;
import com.xiaopeng.systemui.infoflow.widget.CardStack;
import java.util.List;
/* loaded from: classes24.dex */
public class AiPushCardHelper {
    public static void scrollAndFocusNewItem(int position, CardStack cardStack, boolean insertCardScroll) {
        List<CardEntry> cardEntries = CardListData.getInstance().getCards();
        if (position >= 0 && position < cardEntries.size()) {
            CardEntry cardEntry = cardEntries.get(position);
            if (isAIPushCard(cardEntry)) {
                cardStack.scrollToPosition(0);
            } else if (insertCardScroll) {
                cardStack.smoothScrollToPosition(position);
            }
            if (position == 0) {
                isForceFocusCard(cardEntry);
            }
        }
    }

    private static boolean isAIPushCard(CardEntry cardEntry) {
        return cardEntry.type == 17 || cardEntry.type == 19;
    }

    private static boolean isForceFocusCard(CardEntry cardEntry) {
        return (cardEntry.type == 17 && cardEntry.priority) || (cardEntry.type == 19 && CardHelper.isCarHasException(cardEntry));
    }

    public static void scrollAndFocusChangedItem(int position, CardStack cardStack, boolean updateCardScroll) {
        List<CardEntry> cardEntries = CardListData.getInstance().getCards();
        if (position >= 0 && position < cardEntries.size()) {
            CardEntry cardEntry = cardEntries.get(position);
            if (isAIPushCard(cardEntry)) {
                cardStack.scrollToPosition(0);
            } else if (updateCardScroll) {
                cardStack.scrollToPosition(position);
            }
            if (position == 0) {
                isForceFocusCard(cardEntry);
            }
        }
    }

    public static void scrollAndFocusMovedItem(int fromPosition, int newPosition, CardStack cardStack, boolean insertCardScroll) {
        List<CardEntry> cardEntries = CardListData.getInstance().getCards();
        if (newPosition >= 0 && newPosition < cardEntries.size()) {
            CardEntry cardEntry = cardEntries.get(newPosition);
            if (isAIPushCard(cardEntry)) {
                cardStack.scrollToPosition(0);
            } else if (insertCardScroll) {
                cardStack.scrollToPosition(newPosition);
            }
            if (newPosition == 0) {
                isForceFocusCard(cardEntry);
            }
        }
    }
}
