package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.BitmapRenderer;
import com.android.launcher3.icons.GraphicsUtils;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.SQLiteCacheHelper;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
/* loaded from: classes19.dex */
public abstract class BaseIconCache {
    private static final boolean DEBUG = false;
    public static final String EMPTY_CLASS_NAME = ".";
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final String TAG = "BaseIconCache";
    private final Looper mBgLooper;
    private final Map<ComponentKey, CacheEntry> mCache;
    protected final Context mContext;
    private final String mDbFileName;
    private final BitmapFactory.Options mDecodeOptions;
    protected IconDB mIconDb;
    protected int mIconDpi;
    protected final PackageManager mPackageManager;
    protected final Handler mWorkerHandler;
    private final HashMap<UserHandle, BitmapInfo> mDefaultIcons = new HashMap<>();
    protected LocaleList mLocaleList = LocaleList.getEmptyLocaleList();
    protected String mSystemState = "";

    /* loaded from: classes19.dex */
    public static class CacheEntry extends BitmapInfo {
        public CharSequence title = "";
        public CharSequence contentDescription = "";
    }

    protected abstract BaseIconFactory getIconFactory();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract long getSerialNumberForUser(UserHandle userHandle);

    protected abstract boolean isInstantApp(ApplicationInfo applicationInfo);

    public BaseIconCache(Context context, String dbFileName, Looper bgLooper, int iconDpi, int iconPixelSize, boolean inMemoryCache) {
        this.mContext = context;
        this.mDbFileName = dbFileName;
        this.mPackageManager = context.getPackageManager();
        this.mBgLooper = bgLooper;
        this.mWorkerHandler = new Handler(this.mBgLooper);
        if (inMemoryCache) {
            this.mCache = new HashMap(50);
        } else {
            this.mCache = new AbstractMap<ComponentKey, CacheEntry>() { // from class: com.android.launcher3.icons.cache.BaseIconCache.1
                @Override // java.util.AbstractMap, java.util.Map
                public Set<Map.Entry<ComponentKey, CacheEntry>> entrySet() {
                    return Collections.emptySet();
                }

                @Override // java.util.AbstractMap, java.util.Map
                public CacheEntry put(ComponentKey key, CacheEntry value) {
                    return value;
                }
            };
        }
        if (BitmapRenderer.USE_HARDWARE_BITMAP && Build.VERSION.SDK_INT >= 26) {
            this.mDecodeOptions = new BitmapFactory.Options();
            this.mDecodeOptions.inPreferredConfig = Bitmap.Config.HARDWARE;
        } else {
            this.mDecodeOptions = null;
        }
        updateSystemState();
        this.mIconDpi = iconDpi;
        this.mIconDb = new IconDB(context, dbFileName, iconPixelSize);
    }

    public void updateIconParams(final int iconDpi, final int iconPixelSize) {
        this.mWorkerHandler.post(new Runnable() { // from class: com.android.launcher3.icons.cache.-$$Lambda$BaseIconCache$MwrqDESzzZpTlG8YHIovWQ692hc
            @Override // java.lang.Runnable
            public final void run() {
                BaseIconCache.this.lambda$updateIconParams$0$BaseIconCache(iconDpi, iconPixelSize);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updateIconParamsBg */
    public synchronized void lambda$updateIconParams$0$BaseIconCache(int iconDpi, int iconPixelSize) {
        this.mIconDpi = iconDpi;
        this.mDefaultIcons.clear();
        this.mIconDb.clear();
        this.mIconDb.close();
        this.mIconDb = new IconDB(this.mContext, this.mDbFileName, iconPixelSize);
        this.mCache.clear();
    }

    private Drawable getFullResIcon(Resources resources, int iconId) {
        if (resources != null && iconId != 0) {
            try {
                return resources.getDrawableForDensity(iconId, this.mIconDpi);
            } catch (Resources.NotFoundException e) {
            }
        }
        return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        try {
            return getFullResIcon(this.mPackageManager.getResourcesForApplication(packageName), iconId);
        } catch (PackageManager.NameNotFoundException e) {
            return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
        }
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        try {
            return getFullResIcon(this.mPackageManager.getResourcesForApplication(info.applicationInfo), info.getIconResource());
        } catch (PackageManager.NameNotFoundException e) {
            return BaseIconFactory.getFullResDefaultActivityIcon(this.mIconDpi);
        }
    }

    private BitmapInfo makeDefaultIcon(UserHandle user) {
        BaseIconFactory li = getIconFactory();
        try {
            BitmapInfo makeDefaultIcon = li.makeDefaultIcon(user);
            li.close();
            return makeDefaultIcon;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (li != null) {
                    try {
                        li.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    public synchronized void remove(ComponentName componentName, UserHandle user) {
        this.mCache.remove(new ComponentKey(componentName, user));
    }

    private void removeFromMemCacheLocked(String packageName, UserHandle user) {
        HashSet<ComponentKey> forDeletion = new HashSet<>();
        for (ComponentKey key : this.mCache.keySet()) {
            if (key.componentName.getPackageName().equals(packageName) && key.user.equals(user)) {
                forDeletion.add(key);
            }
        }
        Iterator<ComponentKey> it = forDeletion.iterator();
        while (it.hasNext()) {
            ComponentKey condemned = it.next();
            this.mCache.remove(condemned);
        }
    }

    public synchronized void removeIconsForPkg(String packageName, UserHandle user) {
        removeFromMemCacheLocked(packageName, user);
        long userSerial = getSerialNumberForUser(user);
        IconDB iconDB = this.mIconDb;
        iconDB.delete("componentName LIKE ? AND profileId = ?", new String[]{packageName + "/%", Long.toString(userSerial)});
    }

    public IconCacheUpdateHandler getUpdateHandler() {
        updateSystemState();
        return new IconCacheUpdateHandler(this);
    }

    private void updateSystemState() {
        this.mLocaleList = this.mContext.getResources().getConfiguration().getLocales();
        this.mSystemState = this.mLocaleList.toLanguageTags() + "," + Build.VERSION.SDK_INT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getIconSystemState(String packageName) {
        return this.mSystemState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized <T> void addIconToDBAndMemCache(T object, CachingLogic<T> cachingLogic, PackageInfo info, long userSerial, boolean replaceExisting) {
        CacheEntry entry;
        UserHandle user = cachingLogic.getUser(object);
        ComponentName componentName = cachingLogic.getComponent(object);
        ComponentKey key = new ComponentKey(componentName, user);
        CacheEntry entry2 = null;
        if (!replaceExisting && ((entry2 = this.mCache.get(key)) == null || entry2.icon == null || entry2.isLowRes())) {
            entry2 = null;
        }
        if (entry2 != null) {
            entry = entry2;
        } else {
            CacheEntry entry3 = new CacheEntry();
            cachingLogic.loadIcon(this.mContext, object, entry3);
            entry = entry3;
        }
        if (entry.icon == null) {
            return;
        }
        entry.title = cachingLogic.getLabel(object);
        entry.contentDescription = this.mPackageManager.getUserBadgedLabel(entry.title, user);
        if (cachingLogic.addToMemCache()) {
            this.mCache.put(key, entry);
        }
        ContentValues values = newContentValues(entry, entry.title.toString(), componentName.getPackageName(), cachingLogic.getKeywords(object, this.mLocaleList));
        addIconToDB(values, componentName, info, userSerial);
    }

    private void addIconToDB(ContentValues values, ComponentName key, PackageInfo info, long userSerial) {
        values.put(IconDB.COLUMN_COMPONENT, key.flattenToString());
        values.put(IconDB.COLUMN_USER, Long.valueOf(userSerial));
        values.put(IconDB.COLUMN_LAST_UPDATED, Long.valueOf(info.lastUpdateTime));
        values.put(IconDB.COLUMN_VERSION, Integer.valueOf(info.versionCode));
        this.mIconDb.insertOrReplace(values);
    }

    public synchronized BitmapInfo getDefaultIcon(UserHandle user) {
        if (!this.mDefaultIcons.containsKey(user)) {
            this.mDefaultIcons.put(user, makeDefaultIcon(user));
        }
        return this.mDefaultIcons.get(user);
    }

    public boolean isDefaultIcon(Bitmap icon, UserHandle user) {
        return getDefaultIcon(user).icon == icon;
    }

    protected <T> CacheEntry cacheLocked(@NonNull ComponentName componentName, @NonNull UserHandle user, @NonNull Supplier<T> infoProvider, @NonNull CachingLogic<T> cachingLogic, boolean usePackageIcon, boolean useLowResIcon) {
        CacheEntry packageEntry;
        assertWorkerThread();
        ComponentKey cacheKey = new ComponentKey(componentName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry == null || (entry.isLowRes() && !useLowResIcon)) {
            entry = new CacheEntry();
            if (cachingLogic.addToMemCache()) {
                this.mCache.put(cacheKey, entry);
            }
            T object = null;
            boolean providerFetchedOnce = false;
            if (!getEntryFromDB(cacheKey, entry, useLowResIcon)) {
                object = infoProvider.get();
                providerFetchedOnce = true;
                if (object != null) {
                    cachingLogic.loadIcon(this.mContext, object, entry);
                } else {
                    if (usePackageIcon && (packageEntry = getEntryForPackageLocked(componentName.getPackageName(), user, false)) != null) {
                        packageEntry.applyTo(entry);
                        entry.title = packageEntry.title;
                        entry.contentDescription = packageEntry.contentDescription;
                    }
                    if (entry.icon == null) {
                        getDefaultIcon(user).applyTo(entry);
                    }
                }
            }
            if (TextUtils.isEmpty(entry.title)) {
                if (object == null && !providerFetchedOnce) {
                    object = infoProvider.get();
                }
                if (object != null) {
                    entry.title = cachingLogic.getLabel(object);
                    entry.contentDescription = this.mPackageManager.getUserBadgedLabel(entry.title, user);
                }
            }
        }
        return entry;
    }

    public synchronized void clear() {
        assertWorkerThread();
        this.mIconDb.clear();
    }

    public synchronized void cachePackageInstallInfo(String packageName, UserHandle user, Bitmap icon, CharSequence title) {
        removeFromMemCacheLocked(packageName, user);
        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry == null) {
            entry = new CacheEntry();
        }
        if (!TextUtils.isEmpty(title)) {
            entry.title = title;
        }
        if (icon != null) {
            BaseIconFactory li = getIconFactory();
            li.createIconBitmap(icon).applyTo(entry);
            li.close();
        }
        if (!TextUtils.isEmpty(title) && entry.icon != null) {
            this.mCache.put(cacheKey, entry);
        }
    }

    private static ComponentKey getPackageKey(String packageName, UserHandle user) {
        ComponentName cn = new ComponentName(packageName, packageName + ".");
        return new ComponentKey(cn, user);
    }

    protected CacheEntry getEntryForPackageLocked(String packageName, UserHandle user, boolean useLowResIcon) {
        assertWorkerThread();
        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = this.mCache.get(cacheKey);
        if (entry == null || (entry.isLowRes() && !useLowResIcon)) {
            CacheEntry entry2 = new CacheEntry();
            boolean entryUpdated = true;
            if (!getEntryFromDB(cacheKey, entry2, useLowResIcon)) {
                try {
                    int flags = Process.myUserHandle().equals(user) ? 0 : 8192;
                    PackageInfo info = this.mPackageManager.getPackageInfo(packageName, flags);
                    ApplicationInfo appInfo = info.applicationInfo;
                    if (appInfo == null) {
                        throw new PackageManager.NameNotFoundException("ApplicationInfo is null");
                    }
                    BaseIconFactory li = getIconFactory();
                    BitmapInfo iconInfo = li.createBadgedIconBitmap(appInfo.loadIcon(this.mPackageManager), user, appInfo.targetSdkVersion, isInstantApp(appInfo));
                    li.close();
                    entry2.title = appInfo.loadLabel(this.mPackageManager);
                    entry2.contentDescription = this.mPackageManager.getUserBadgedLabel(entry2.title, user);
                    entry2.icon = useLowResIcon ? BitmapInfo.LOW_RES_ICON : iconInfo.icon;
                    entry2.color = iconInfo.color;
                    ContentValues values = newContentValues(iconInfo, entry2.title.toString(), packageName, null);
                    addIconToDB(values, cacheKey.componentName, info, getSerialNumberForUser(user));
                } catch (PackageManager.NameNotFoundException e) {
                    entryUpdated = false;
                }
            }
            if (entryUpdated) {
                this.mCache.put(cacheKey, entry2);
                return entry2;
            }
            return entry2;
        }
        return entry;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0086, code lost:
        if (0 == 0) goto L23;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean getEntryFromDB(com.android.launcher3.util.ComponentKey r11, com.android.launcher3.icons.cache.BaseIconCache.CacheEntry r12, boolean r13) {
        /*
            r10 = this;
            java.lang.String r0 = ""
            r1 = 0
            r2 = 0
            com.android.launcher3.icons.cache.BaseIconCache$IconDB r3 = r10.mIconDb     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            if (r13 == 0) goto Lb
            java.lang.String[] r4 = com.android.launcher3.icons.cache.BaseIconCache.IconDB.COLUMNS_LOW_RES     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            goto Ld
        Lb:
            java.lang.String[] r4 = com.android.launcher3.icons.cache.BaseIconCache.IconDB.COLUMNS_HIGH_RES     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
        Ld:
            java.lang.String r5 = "componentName = ? AND profileId = ?"
            r6 = 2
            java.lang.String[] r7 = new java.lang.String[r6]     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            android.content.ComponentName r8 = r11.componentName     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.String r8 = r8.flattenToString()     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r7[r2] = r8     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            android.os.UserHandle r8 = r11.user     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            long r8 = r10.getSerialNumberForUser(r8)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.String r8 = java.lang.Long.toString(r8)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r9 = 1
            r7[r9] = r8     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            android.database.Cursor r3 = r3.query(r4, r5, r7)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r1 = r3
            boolean r3 = r1.moveToNext()     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            if (r3 == 0) goto L76
            int r3 = r1.getInt(r2)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r4 = 255(0xff, float:3.57E-43)
            int r3 = com.android.launcher3.icons.GraphicsUtils.setColorAlphaBound(r3, r4)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r12.color = r3     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.String r3 = r1.getString(r9)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r12.title = r3     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.CharSequence r3 = r12.title     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            if (r3 != 0) goto L4d
            r12.title = r0     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r12.contentDescription = r0     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            goto L59
        L4d:
            android.content.pm.PackageManager r0 = r10.mPackageManager     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.CharSequence r3 = r12.title     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            android.os.UserHandle r4 = r11.user     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            java.lang.CharSequence r0 = r0.getUserBadgedLabel(r3, r4)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r12.contentDescription = r0     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
        L59:
            if (r13 == 0) goto L60
            android.graphics.Bitmap r0 = com.android.launcher3.icons.BitmapInfo.LOW_RES_ICON     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            r12.icon = r0     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            goto L70
        L60:
            byte[] r0 = r1.getBlob(r6)     // Catch: java.lang.Throwable -> L7b android.database.sqlite.SQLiteException -> L7d
            int r3 = r0.length     // Catch: java.lang.Exception -> L6e java.lang.Throwable -> L7b
            android.graphics.BitmapFactory$Options r4 = r10.mDecodeOptions     // Catch: java.lang.Exception -> L6e java.lang.Throwable -> L7b
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeByteArray(r0, r2, r3, r4)     // Catch: java.lang.Exception -> L6e java.lang.Throwable -> L7b
            r12.icon = r3     // Catch: java.lang.Exception -> L6e java.lang.Throwable -> L7b
            goto L6f
        L6e:
            r2 = move-exception
        L6f:
        L70:
            r1.close()
            return r9
        L76:
        L77:
            r1.close()
            goto L89
        L7b:
            r0 = move-exception
            goto L8a
        L7d:
            r0 = move-exception
            java.lang.String r3 = "BaseIconCache"
            java.lang.String r4 = "Error reading icon cache"
            android.util.Log.d(r3, r4, r0)     // Catch: java.lang.Throwable -> L7b
            if (r1 == 0) goto L89
            goto L77
        L89:
            return r2
        L8a:
            if (r1 == 0) goto L8f
            r1.close()
        L8f:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.cache.BaseIconCache.getEntryFromDB(com.android.launcher3.util.ComponentKey, com.android.launcher3.icons.cache.BaseIconCache$CacheEntry, boolean):boolean");
    }

    public synchronized Cursor queryCacheDb(String[] columns, String selection, String[] selectionArgs) {
        return this.mIconDb.query(columns, selection, selectionArgs);
    }

    /* loaded from: classes19.dex */
    public static final class IconDB extends SQLiteCacheHelper {
        public static final String COLUMN_COMPONENT = "componentName";
        public static final String COLUMN_KEYWORDS = "keywords";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_LAST_UPDATED = "lastUpdated";
        public static final String COLUMN_ROWID = "rowid";
        public static final String COLUMN_SYSTEM_STATE = "system_state";
        public static final String COLUMN_USER = "profileId";
        public static final String COLUMN_VERSION = "version";
        private static final int RELEASE_VERSION = 27;
        public static final String TABLE_NAME = "icons";
        public static final String COLUMN_ICON_COLOR = "icon_color";
        public static final String COLUMN_ICON = "icon";
        public static final String[] COLUMNS_HIGH_RES = {COLUMN_ICON_COLOR, "label", COLUMN_ICON};
        public static final String[] COLUMNS_LOW_RES = {COLUMN_ICON_COLOR, "label"};

        public IconDB(Context context, String dbFileName, int iconPixelSize) {
            super(context, dbFileName, 1769472 + iconPixelSize, TABLE_NAME);
        }

        @Override // com.android.launcher3.util.SQLiteCacheHelper
        protected void onCreateTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS icons (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, icon BLOB, icon_color INTEGER NOT NULL DEFAULT 0, label TEXT, system_state TEXT, keywords TEXT, PRIMARY KEY (componentName, profileId) );");
        }
    }

    private ContentValues newContentValues(BitmapInfo bitmapInfo, String label, String packageName, @Nullable String keywords) {
        ContentValues values = new ContentValues();
        values.put(IconDB.COLUMN_ICON, bitmapInfo.isLowRes() ? null : GraphicsUtils.flattenBitmap(bitmapInfo.icon));
        values.put(IconDB.COLUMN_ICON_COLOR, Integer.valueOf(bitmapInfo.color));
        values.put("label", label);
        values.put(IconDB.COLUMN_SYSTEM_STATE, getIconSystemState(packageName));
        values.put(IconDB.COLUMN_KEYWORDS, keywords);
        return values;
    }

    private void assertWorkerThread() {
        if (Looper.myLooper() != this.mBgLooper) {
            throw new IllegalStateException("Cache accessed on wrong thread " + Looper.myLooper());
        }
    }
}
