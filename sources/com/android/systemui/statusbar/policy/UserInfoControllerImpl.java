package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.util.Log;
import androidx.mediarouter.media.SystemMediaRouteProvider;
import com.android.internal.util.UserIcons;
import com.android.settingslib.drawable.UserIconDrawable;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.UserInfoController;
import java.util.ArrayList;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class UserInfoControllerImpl implements UserInfoController {
    private static final String TAG = "UserInfoController";
    private final Context mContext;
    private String mUserAccount;
    private Drawable mUserDrawable;
    private AsyncTask<Void, Void, UserInfoQueryResult> mUserInfoTask;
    private String mUserName;
    private final ArrayList<UserInfoController.OnUserInfoChangedListener> mCallbacks = new ArrayList<>();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.UserInfoControllerImpl.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.USER_SWITCHED".equals(action)) {
                UserInfoControllerImpl.this.reloadUserInfo();
            }
        }
    };
    private final BroadcastReceiver mProfileReceiver = new BroadcastReceiver() { // from class: com.android.systemui.statusbar.policy.UserInfoControllerImpl.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.provider.Contacts.PROFILE_CHANGED".equals(action) || "android.intent.action.USER_INFO_CHANGED".equals(action)) {
                try {
                    int currentUser = ActivityManager.getService().getCurrentUser().id;
                    int changedUser = intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId());
                    if (changedUser == currentUser) {
                        UserInfoControllerImpl.this.reloadUserInfo();
                    }
                } catch (RemoteException e) {
                    Log.e(UserInfoControllerImpl.TAG, "Couldn't get current user id for profile change", e);
                }
            }
        }
    };

    @Inject
    public UserInfoControllerImpl(Context context) {
        this.mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(this.mReceiver, filter);
        IntentFilter profileFilter = new IntentFilter();
        profileFilter.addAction("android.provider.Contacts.PROFILE_CHANGED");
        profileFilter.addAction("android.intent.action.USER_INFO_CHANGED");
        this.mContext.registerReceiverAsUser(this.mProfileReceiver, UserHandle.ALL, profileFilter, null, null);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(UserInfoController.OnUserInfoChangedListener callback) {
        this.mCallbacks.add(callback);
        callback.onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(UserInfoController.OnUserInfoChangedListener callback) {
        this.mCallbacks.remove(callback);
    }

    @Override // com.android.systemui.statusbar.policy.UserInfoController
    public void reloadUserInfo() {
        AsyncTask<Void, Void, UserInfoQueryResult> asyncTask = this.mUserInfoTask;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mUserInfoTask = null;
        }
        queryForUserInformation();
    }

    private void queryForUserInformation() {
        try {
            UserInfo userInfo = ActivityManager.getService().getCurrentUser();
            final Context currentUserContext = this.mContext.createPackageContextAsUser(SystemMediaRouteProvider.PACKAGE_NAME, 0, new UserHandle(userInfo.id));
            final int userId = userInfo.id;
            final boolean isGuest = userInfo.isGuest();
            final String userName = userInfo.name;
            final boolean lightIcon = this.mContext.getThemeResId() != R.style.Theme_SystemUI_Light;
            Resources res = this.mContext.getResources();
            final int avatarSize = Math.max(res.getDimensionPixelSize(R.dimen.multi_user_avatar_expanded_size), res.getDimensionPixelSize(R.dimen.multi_user_avatar_keyguard_size));
            this.mUserInfoTask = new AsyncTask<Void, Void, UserInfoQueryResult>() { // from class: com.android.systemui.statusbar.policy.UserInfoControllerImpl.3
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public UserInfoQueryResult doInBackground(Void... params) {
                    Drawable avatar;
                    Cursor cursor;
                    UserManager um = UserManager.get(UserInfoControllerImpl.this.mContext);
                    String name = userName;
                    Bitmap rawAvatar = um.getUserIcon(userId);
                    if (rawAvatar != null) {
                        avatar = new UserIconDrawable(avatarSize).setIcon(rawAvatar).setBadgeIfManagedUser(UserInfoControllerImpl.this.mContext, userId).bake();
                    } else {
                        avatar = UserIcons.getDefaultUserIcon(currentUserContext.getResources(), isGuest ? -10000 : userId, lightIcon);
                    }
                    if (um.getUsers().size() <= 1 && (cursor = currentUserContext.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, new String[]{"_id", "display_name"}, null, null, null)) != null) {
                        try {
                            if (cursor.moveToFirst()) {
                                name = cursor.getString(cursor.getColumnIndex("display_name"));
                            }
                        } finally {
                            cursor.close();
                        }
                    }
                    String userAccount = um.getUserAccount(userId);
                    return new UserInfoQueryResult(name, avatar, userAccount);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(UserInfoQueryResult result) {
                    UserInfoControllerImpl.this.mUserName = result.getName();
                    UserInfoControllerImpl.this.mUserDrawable = result.getAvatar();
                    UserInfoControllerImpl.this.mUserAccount = result.getUserAccount();
                    UserInfoControllerImpl.this.mUserInfoTask = null;
                    UserInfoControllerImpl.this.notifyChanged();
                }
            };
            this.mUserInfoTask.execute(new Void[0]);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Couldn't create user context", e);
            throw new RuntimeException(e);
        } catch (RemoteException e2) {
            Log.e(TAG, "Couldn't get user info", e2);
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged() {
        Iterator<UserInfoController.OnUserInfoChangedListener> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            UserInfoController.OnUserInfoChangedListener listener = it.next();
            listener.onUserInfoChanged(this.mUserName, this.mUserDrawable, this.mUserAccount);
        }
    }

    public void onDensityOrFontScaleChanged() {
        reloadUserInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class UserInfoQueryResult {
        private Drawable mAvatar;
        private String mName;
        private String mUserAccount;

        public UserInfoQueryResult(String name, Drawable avatar, String userAccount) {
            this.mName = name;
            this.mAvatar = avatar;
            this.mUserAccount = userAccount;
        }

        public String getName() {
            return this.mName;
        }

        public Drawable getAvatar() {
            return this.mAvatar;
        }

        public String getUserAccount() {
            return this.mUserAccount;
        }
    }
}
