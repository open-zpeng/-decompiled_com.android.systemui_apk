package com.xiaopeng.systemui.infoflow.message.data;

import android.os.SystemClock;
import android.util.ArrayMap;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.infoflow.message.presenter.NotificationCardPresenter;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/* loaded from: classes24.dex */
public class NotificationList {
    private static final String MESSAGE_ENTRY_KEY = "com.android.systemui.message_entry_key";
    private static final String TAG = NotificationList.class.getSimpleName();
    private static volatile NotificationList mInstance;
    private ArrayMap<String, CardEntry> mMessageEntry = new ArrayMap<>();
    private ArrayList<CardEntry> mSortAndFliterMessages = new ArrayList<>();
    private final Comparator<CardEntry> mTimeComparator = new Comparator<CardEntry>() { // from class: com.xiaopeng.systemui.infoflow.message.data.NotificationList.1
        @Override // java.util.Comparator
        public int compare(CardEntry o1, CardEntry o2) {
            return Long.compare(o2.when, o1.when);
        }
    };

    private NotificationList() {
    }

    public static NotificationList getInstance() {
        if (mInstance == null) {
            synchronized (NotificationList.class) {
                if (mInstance == null) {
                    mInstance = new NotificationList();
                }
            }
        }
        return mInstance;
    }

    public int getSize() {
        return this.mSortAndFliterMessages.size();
    }

    public void add(CardEntry cardEntry) {
        synchronized (this.mMessageEntry) {
            this.mMessageEntry.put(cardEntry.key, cardEntry);
        }
        filterAndSort();
    }

    private void filterAndSort() {
        this.mSortAndFliterMessages.clear();
        synchronized (this.mMessageEntry) {
            int N = this.mMessageEntry.size();
            for (int i = 0; i < N; i++) {
                CardEntry entry = this.mMessageEntry.valueAt(i);
                this.mSortAndFliterMessages.add(entry);
            }
        }
        Collections.sort(this.mSortAndFliterMessages, this.mTimeComparator);
    }

    public void remove(CardEntry cardEntry) {
        synchronized (this.mMessageEntry) {
            this.mMessageEntry.remove(cardEntry.key);
        }
        filterAndSort();
    }

    public void remove(String key) {
        synchronized (this.mMessageEntry) {
            this.mMessageEntry.remove(key);
        }
        filterAndSort();
    }

    public void clear() {
        synchronized (this.mMessageEntry) {
            this.mMessageEntry.clear();
        }
        this.mSortAndFliterMessages.clear();
    }

    public CardEntry getShowEntry() {
        if (this.mSortAndFliterMessages.size() == 0) {
            return getDefaultNotification();
        }
        CardEntry firstEntry = this.mSortAndFliterMessages.get(0);
        CardEntry showEntry = null;
        try {
            showEntry = firstEntry.m44clone();
            showEntry.key = "com.android.systemui.message_entry_key";
            showEntry.position = 6;
            showEntry.importance = 0;
            showEntry.summary = SystemUIApplication.getContext().getResources().getString(R.string.message_count, Integer.valueOf(this.mSortAndFliterMessages.size()));
            showEntry.when = SystemClock.elapsedRealtime();
            return showEntry;
        } catch (CloneNotSupportedException e) {
            Logger.e(TAG, "card entry clone failed");
            return showEntry;
        }
    }

    public String getShowEntryReallyId() {
        if (this.mSortAndFliterMessages.size() == 0) {
            return "com.android.systemui.message_entry_key";
        }
        CardEntry firstEntry = this.mSortAndFliterMessages.get(0);
        return firstEntry.key;
    }

    public CardEntry getDefaultNotification() {
        CardEntry cardEntry = new CardEntry();
        cardEntry.type = 1;
        cardEntry.key = "com.android.systemui.message_entry_key";
        cardEntry.position = 6;
        String noMessage = SystemUIApplication.getContext().getResources().getString(R.string.no_notification);
        cardEntry.title = noMessage;
        cardEntry.content = NotificationCardPresenter.getCurrentTime();
        cardEntry.importance = 0;
        cardEntry.when = SystemClock.elapsedRealtime();
        return cardEntry;
    }
}
