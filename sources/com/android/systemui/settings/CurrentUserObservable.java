package com.android.systemui.settings;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
/* loaded from: classes21.dex */
public class CurrentUserObservable {
    private final MutableLiveData<Integer> mCurrentUser = new MutableLiveData<Integer>() { // from class: com.android.systemui.settings.CurrentUserObservable.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.lifecycle.LiveData
        public void onActive() {
            super.onActive();
            CurrentUserObservable.this.mTracker.startTracking();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.lifecycle.LiveData
        public void onInactive() {
            super.onInactive();
            CurrentUserObservable.this.mTracker.startTracking();
        }
    };
    private final CurrentUserTracker mTracker;

    public CurrentUserObservable(Context context) {
        this.mTracker = new CurrentUserTracker(context) { // from class: com.android.systemui.settings.CurrentUserObservable.2
            @Override // com.android.systemui.settings.CurrentUserTracker
            public void onUserSwitched(int newUserId) {
                CurrentUserObservable.this.mCurrentUser.setValue(Integer.valueOf(newUserId));
            }
        };
    }

    public LiveData<Integer> getCurrentUser() {
        if (this.mCurrentUser.getValue() == null) {
            this.mCurrentUser.setValue(Integer.valueOf(this.mTracker.getCurrentUserId()));
        }
        return this.mCurrentUser;
    }
}
