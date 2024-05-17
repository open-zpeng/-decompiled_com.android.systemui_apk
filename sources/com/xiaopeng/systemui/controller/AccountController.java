package com.xiaopeng.systemui.controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.android.systemui.R;
import com.android.systemui.util.wakelock.WakeLock;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.xiaopeng.systemui.Logger;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class AccountController implements AccountManagerCallback<Bundle>, OnAccountsUpdateListener {
    public static final String ACCOUNT_AVATAR = "avatar";
    public static final String ACCOUNT_TYPE_XP_VEHICLE = "com.xiaopeng.accountservice.ACCOUNT_TYPE_XP_VEHICLE";
    public static final String ACCOUNT_UID = "uid";
    public static final String ACCOUNT_UPDATE = "update";
    public static final String ACCOUNT_USER_TYPE = "user_type";
    public static final String AUTH_TYPE_AUTH_CODE_CAR_ACCOUNT = "com.xiaopeng.accountservice.AUTH_TYPE_AUTH_CODE_CAR_ACCOUNT";
    public static final String AUTH_TYPE_AUTH_CODE_MUSIC = "com.xiaopeng.accountservice.AUTH_TYPE_AUTH_CODE_MUSIC";
    public static final String AUTH_TYPE_AUTH_CODE_NAVIGATION = "com.xiaopeng.accountservice.AUTH_TYPE_AUTH_CODE_NAVIGATION";
    public static final int AVATAR_TYPE_DRIVER = 1;
    public static final int AVATAR_TYPE_PASSENGER = 2;
    public static final int MSG_RELOAD_AVATAR = 1;
    private static final String TAG = "AccountController";
    public static final int USER_TYPE_DRIVER = 4;
    public static final int USER_TYPE_PASSENGER = 5;
    public static final int USER_TYPE_TENANT = 3;
    public static final int USER_TYPE_TOWNER = 1;
    public static final int USER_TYPE_USER = 2;
    private AccountManager mAccountManager;
    private Context mContext;
    private static boolean sAvatarLoaded = true;
    private static AccountController sAccountController = null;
    private final Handler mHandler = new Handler() { // from class: com.xiaopeng.systemui.controller.AccountController.1
    };
    private List<OnAccountListener> mAccountListeners = new ArrayList();

    /* loaded from: classes24.dex */
    public interface OnAccountListener {
        void onAccountsChanged();
    }

    /* loaded from: classes24.dex */
    public interface OnAvatarLoadListener {
        void onAvatarLoad(int i, Bitmap bitmap);
    }

    /* loaded from: classes24.dex */
    public static class AvatarInfo {
        boolean mAvatarLoaded;
        int mHeight;
        int mRetryCount;
        int mType;
        String mUrl;
        int mWidth;

        public String getUrl() {
            return this.mUrl;
        }

        public void setUrl(String url) {
            this.mUrl = url;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public void setWidth(int width) {
            this.mWidth = width;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public void setHeight(int height) {
            this.mHeight = height;
        }

        public int getType() {
            return this.mType;
        }

        public void setType(int type) {
            this.mType = type;
        }

        public int getRetryCount() {
            return this.mRetryCount;
        }

        public void setRetryCount(int retryCount) {
            this.mRetryCount = retryCount;
        }

        public boolean isAvatarLoaded() {
            return this.mAvatarLoaded;
        }

        public void setAvatarLoaded(boolean avatarLoaded) {
            this.mAvatarLoaded = avatarLoaded;
        }
    }

    public static AccountController getInstance(Context context) {
        if (sAccountController == null) {
            synchronized (AccountController.class) {
                if (sAccountController == null) {
                    sAccountController = new AccountController(context);
                }
            }
        }
        return sAccountController;
    }

    private AccountController(Context context) {
        this.mContext = context;
        this.mAccountManager = AccountManager.get(context);
    }

    public void init() {
        this.mAccountManager.addOnAccountsUpdatedListener(this, this.mHandler, true);
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.systemui.controller.AccountController.2
            @Override // java.lang.Runnable
            public void run() {
                AccountController accountController = AccountController.this;
                accountController.onAccountsUpdated(accountController.getAccounts());
            }
        }, WakeLock.DEFAULT_MAX_TIMEOUT);
    }

    public void register(OnAccountListener listener) {
        this.mAccountListeners.add(listener);
    }

    public void unregister(OnAccountListener listener) {
        this.mAccountListeners.remove(listener);
    }

    public static void loadAvatar(final int type, Context context, final Handler handler, final OnAvatarLoadListener onAvatarLoadListener, final String url, final int width, final int height, final int retryCount) {
        Logger.d(TAG, "loadAvatar url=" + url + " type = " + type + " retryCount = " + retryCount);
        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_sysbar_account_has_people).error(R.drawable.ic_sysbar_account_has_people).priority(Priority.HIGH).override(width, height).timeout(30000).diskCacheStrategy(DiskCacheStrategy.ALL);
        SimpleTarget target = new SimpleTarget<Bitmap>() { // from class: com.xiaopeng.systemui.controller.AccountController.3
            @Override // com.bumptech.glide.request.target.Target
            public /* bridge */ /* synthetic */ void onResourceReady(@NonNull Object obj, Transition transition) {
                onResourceReady((Bitmap) obj, (Transition<? super Bitmap>) transition);
            }

            @Override // com.bumptech.glide.request.target.BaseTarget, com.bumptech.glide.request.target.Target
            public void onLoadFailed(Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                AccountController.onAvatarLoaded(false);
                Logger.d(AccountController.TAG, "loadAvatar target onLoadFailed");
            }

            public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                Logger.d(AccountController.TAG, "loadAvatar target onResourceReady");
                OnAvatarLoadListener.this.onAvatarLoad(type, bitmap);
                AccountController.onAvatarLoaded(true);
            }
        };
        RequestFutureTarget futureTarget = new RequestFutureTarget<Bitmap>(handler, width, height) { // from class: com.xiaopeng.systemui.controller.AccountController.4
            @Override // com.bumptech.glide.request.RequestFutureTarget, com.bumptech.glide.request.RequestListener
            public /* bridge */ /* synthetic */ boolean onResourceReady(Object obj, Object obj2, Target target2, DataSource dataSource, boolean z) {
                return onResourceReady((Bitmap) obj, obj2, (Target<Bitmap>) target2, dataSource, z);
            }

            @Override // com.bumptech.glide.request.RequestFutureTarget, com.bumptech.glide.request.RequestListener
            public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target2, boolean isFirstResource) {
                Logger.d(AccountController.TAG, "loadAvatar futureTarget onLoadFailed e=" + e + " retryCount = " + retryCount);
                if (e != null) {
                    e.logRootCauses("failToLoadAvatar");
                }
                int i = retryCount;
                if (i > 0) {
                    AccountController.reloadAvatar(handler, url, width, height, type, i - 1);
                    return false;
                }
                return false;
            }

            public synchronized boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target2, DataSource dataSource, boolean isFirstResource) {
                if (onAvatarLoadListener != null && resource != null) {
                    Logger.d(AccountController.TAG, "loadAvatar futureTarget onResourceReady");
                    onAvatarLoadListener.onAvatarLoad(type, resource);
                }
                return super.onResourceReady((AnonymousClass4) resource, model, (Target<AnonymousClass4>) target2, dataSource, isFirstResource);
            }
        };
        Glide.with(context).asBitmap().load(url).apply(options).listener(futureTarget).into((RequestBuilder<Bitmap>) target);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void reloadAvatar(Handler handler, String url, int width, int height, int type, int retryCount) {
        Message msg = handler.obtainMessage();
        msg.what = 1;
        AvatarInfo avatarInfo = new AvatarInfo();
        avatarInfo.setUrl(url);
        avatarInfo.setWidth(width);
        avatarInfo.setHeight(height);
        avatarInfo.setType(type);
        avatarInfo.setRetryCount(retryCount);
        msg.obj = avatarInfo;
        handler.sendMessage(msg);
    }

    public static void clearDiskCache(Context context) {
        try {
            Glide.get(context).clearDiskCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onAvatarLoaded(boolean loaded) {
        sAvatarLoaded = loaded;
    }

    public static boolean isAvatarLoaded() {
        return sAvatarLoaded;
    }

    public Account getAccount(int type) {
        Account account = null;
        Account[] accounts = getAccounts();
        if (accounts != null && accounts.length > 0) {
            StringBuffer buffer = new StringBuffer("");
            account = accounts[0];
            if (account != null) {
                String uid = this.mAccountManager.getUserData(account, "uid");
                String user = this.mAccountManager.getUserData(account, ACCOUNT_USER_TYPE);
                String icon = this.mAccountManager.getUserData(account, "head_url");
                String name = account.name;
                String avatar = this.mAccountManager.getUserData(account, ACCOUNT_AVATAR);
                String update = this.mAccountManager.getUserData(account, "update");
                buffer.append(" uid=");
                buffer.append(uid);
                buffer.append(" user=");
                buffer.append(user);
                buffer.append(" icon=");
                buffer.append(icon);
                buffer.append(" name=");
                buffer.append(name);
                buffer.append(" avatar=");
                buffer.append(avatar);
                buffer.append(" update=");
                buffer.append(update);
            }
            if (type != 4 && type == 5) {
                account = null;
            }
            Logger.d(TAG, "getAccount type=" + type + " buffer " + ((Object) buffer));
        }
        return account;
    }

    public Account[] getAccounts() {
        return this.mAccountManager.getAccountsByType(ACCOUNT_TYPE_XP_VEHICLE);
    }

    public String getAvatarUrl(int type) {
        Account account = getAccount(type);
        if (account != null) {
            return this.mAccountManager.getUserData(account, ACCOUNT_AVATAR);
        }
        return "";
    }

    public boolean hasDriverAccount() {
        return getAccount(4) != null;
    }

    public boolean hasPassengerAccount() {
        return getAccount(5) != null;
    }

    @Override // android.accounts.AccountManagerCallback
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
    }

    @Override // android.accounts.OnAccountsUpdateListener
    public void onAccountsUpdated(Account[] accounts) {
        List<OnAccountListener> list = this.mAccountListeners;
        if (list != null) {
            for (OnAccountListener listener : list) {
                if (listener != null) {
                    listener.onAccountsChanged();
                }
            }
        }
    }
}
