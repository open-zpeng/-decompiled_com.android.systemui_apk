package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;
/* loaded from: classes19.dex */
public abstract class SQLiteCacheHelper {
    private static final boolean IN_MEMORY_CACHE = false;
    private static final String TAG = "SQLiteCacheHelper";
    private boolean mIgnoreWrites = false;
    private final MySQLiteOpenHelper mOpenHelper;
    private final String mTableName;

    protected abstract void onCreateTable(SQLiteDatabase sQLiteDatabase);

    public SQLiteCacheHelper(Context context, String name, int version, String tableName) {
        this.mTableName = tableName;
        this.mOpenHelper = new MySQLiteOpenHelper(context, name, version);
    }

    public void delete(String whereClause, String[] whereArgs) {
        if (this.mIgnoreWrites) {
            return;
        }
        try {
            this.mOpenHelper.getWritableDatabase().delete(this.mTableName, whereClause, whereArgs);
        } catch (SQLiteFullException e) {
            onDiskFull(e);
        } catch (SQLiteException e2) {
            Log.d(TAG, "Ignoring sqlite exception", e2);
        }
    }

    public void insertOrReplace(ContentValues values) {
        if (this.mIgnoreWrites) {
            return;
        }
        try {
            this.mOpenHelper.getWritableDatabase().insertWithOnConflict(this.mTableName, null, values, 5);
        } catch (SQLiteFullException e) {
            onDiskFull(e);
        } catch (SQLiteException e2) {
            Log.d(TAG, "Ignoring sqlite exception", e2);
        }
    }

    private void onDiskFull(SQLiteFullException e) {
        Log.e(TAG, "Disk full, all write operations will be ignored", e);
        this.mIgnoreWrites = true;
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs) {
        return this.mOpenHelper.getReadableDatabase().query(this.mTableName, columns, selection, selectionArgs, null, null, null);
    }

    public void clear() {
        MySQLiteOpenHelper mySQLiteOpenHelper = this.mOpenHelper;
        mySQLiteOpenHelper.clearDB(mySQLiteOpenHelper.getWritableDatabase());
    }

    public void close() {
        this.mOpenHelper.close();
    }

    /* loaded from: classes19.dex */
    private class MySQLiteOpenHelper extends NoLocaleSQLiteHelper {
        public MySQLiteOpenHelper(Context context, String name, int version) {
            super(context, name, version);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            SQLiteCacheHelper.this.onCreateTable(db);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearDB(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + SQLiteCacheHelper.this.mTableName);
            onCreate(db);
        }
    }
}
