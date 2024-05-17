package com.xiaopeng.lib.apirouter;

import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.xiaopeng.lib.apirouter.ClientConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes22.dex */
class RemoteOperator {
    private static String TAG = RemoteOperator.class.getSimpleName();
    private String mAuthority;
    private IBinder.DeathRecipient mDeathRecipient;
    private String mDescription;
    private IBinder mIBinder;
    private AtomicBoolean mRemoteAlive = new AtomicBoolean(true);
    private Map<String, RemoteMethod> mRemoteApis;

    public static RemoteOperator fromJson(@NonNull IBinder iBinder, @NonNull String operationManifest) {
        try {
            try {
                JSONObject manifest = new JSONObject(operationManifest);
                String descriptor = manifest.getString(ClientConstants.TRANSACT.DESCRIPTOR);
                String authority = manifest.optString(ClientConstants.ALIAS.AUTHORITY, descriptor);
                JSONArray transactions = manifest.getJSONArray(ClientConstants.TRANSACT.TRANSACTION);
                Map<String, RemoteMethod> mMethd = new HashMap<>();
                int i = 0;
                int i2 = 0;
                while (i2 < transactions.length()) {
                    JSONObject method = transactions.getJSONObject(i2);
                    JSONArray jsonParams = method.optJSONArray(ClientConstants.ALIAS.PARAMETER);
                    List<Pair<String, String>> params = null;
                    if (jsonParams != null) {
                        params = new ArrayList<>(jsonParams.length());
                        for (int j = i; j < jsonParams.length(); j++) {
                            JSONObject jsonParam = jsonParams.getJSONObject(j);
                            params.add(new Pair<>(jsonParam.getString(ClientConstants.ALIAS.P_ALIAS), jsonParam.getString("name")));
                        }
                    }
                    String methodKey = method.getString(ClientConstants.TRANSACT.METHOD);
                    String pathKey = method.optString(ClientConstants.ALIAS.PATH, methodKey);
                    RemoteMethod remoteMethod = new RemoteMethod(methodKey, method.getInt(ClientConstants.TRANSACT.ID), params);
                    mMethd.put(methodKey, remoteMethod);
                    if (!methodKey.equals(pathKey)) {
                        mMethd.put(pathKey, remoteMethod);
                    }
                    i2++;
                    i = 0;
                }
                try {
                    RemoteOperator remoteOperator = new RemoteOperator(iBinder, mMethd, descriptor, authority);
                    return remoteOperator;
                } catch (JSONException e) {
                    Log.e(TAG, "Remote IDL parsed error");
                    return null;
                }
            } catch (JSONException e2) {
            }
        } catch (JSONException e3) {
        }
    }

    private RemoteOperator(@NonNull IBinder iBinder, @NonNull Map<String, RemoteMethod> remoteApi, @NonNull String description, @NonNull String authority) {
        this.mIBinder = iBinder;
        this.mRemoteApis = remoteApi;
        this.mDescription = description;
        this.mAuthority = authority;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public boolean isRemoteAlive() {
        IBinder iBinder = this.mIBinder;
        return iBinder != null && iBinder.isBinderAlive() && this.mRemoteAlive.get();
    }

    public Object call(@NonNull Uri uri, boolean withBlob, byte[] blob) throws RemoteException {
        return call(uri, withBlob, blob, false);
    }

    public Object call(@NonNull Uri uri, boolean withBlob, byte[] blob, boolean isOneWay) throws RemoteException {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 0) {
            throw new RemoteException("Can not find uri path segment");
        }
        String firstPathSegment = pathSegments.get(0);
        Map<String, RemoteMethod> map = this.mRemoteApis;
        if (map == null || map.size() == 0) {
            throw new RemoteException("Server do not provide any method");
        }
        RemoteMethod remoteMethod = this.mRemoteApis.get(firstPathSegment);
        if (remoteMethod == null) {
            throw new RemoteException("No matching method");
        }
        Uri.Builder builder = new Uri.Builder();
        builder.authority(this.mDescription).path(remoteMethod.getMethodName());
        if (remoteMethod.getParamsList() != null) {
            for (Pair<String, String> paramKey : remoteMethod.getParamsList()) {
                String param = uri.getQueryParameter((String) paramKey.second);
                if (!TextUtils.isEmpty(param)) {
                    builder.appendQueryParameter((String) paramKey.second, param);
                } else {
                    String param2 = uri.getQueryParameter((String) paramKey.first);
                    if (!TextUtils.isEmpty(param2)) {
                        builder.appendQueryParameter((String) paramKey.second, param2);
                    }
                }
            }
        } else {
            Set<String> queryKeys = uri.getQueryParameterNames();
            for (String queryKey : queryKeys) {
                builder.appendQueryParameter(queryKey, uri.getQueryParameter(queryKey));
            }
        }
        Uri realUrl = builder.build();
        Parcel data = Parcel.obtain();
        Parcel reply = isOneWay ? null : Parcel.obtain();
        Object result = null;
        try {
            data.writeInterfaceToken(this.mDescription);
            Uri.writeToParcel(data, realUrl);
            if (withBlob) {
                try {
                    data.writeBlob(blob);
                } catch (Throwable th) {
                    th = th;
                    if (reply != null) {
                        reply.recycle();
                        data.recycle();
                    }
                    throw th;
                }
            }
            this.mIBinder.transact(remoteMethod.getId(), data, reply, isOneWay ? 1 : 0);
            if (reply != null) {
                try {
                    reply.readException();
                    result = readResult(reply);
                } catch (RuntimeException ex) {
                    String str = TAG;
                    Log.e(str, "ex=" + ex);
                }
            }
            if (reply != null) {
                reply.recycle();
                data.recycle();
            }
            return result;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private Object readResult(Parcel reply) {
        return reply.readValue(getClass().getClassLoader());
    }

    public synchronized void linkToDeath() {
        if (this.mDeathRecipient == null) {
            this.mDeathRecipient = new IBinder.DeathRecipient() { // from class: com.xiaopeng.lib.apirouter.RemoteOperator.1
                @Override // android.os.IBinder.DeathRecipient
                public void binderDied() {
                    String str = RemoteOperator.TAG;
                    Log.d(str, "binderDied:desc=" + RemoteOperator.this.mDescription + ", auth=" + RemoteOperator.this.mAuthority + ",rec=" + Integer.toHexString(hashCode()) + ",obj=" + Integer.toHexString(RemoteOperator.this.hashCode()));
                    RemoteOperator.this.unLinkToDeath("binderDied");
                }
            };
            try {
                String str = TAG;
                Log.d(str, "linkToDeath:desc=" + this.mDescription + ",auth=" + this.mAuthority + ",rec=" + Integer.toHexString(this.mDeathRecipient.hashCode()) + ",obj=" + Integer.toHexString(hashCode()));
                this.mIBinder.linkToDeath(this.mDeathRecipient, 0);
            } catch (RemoteException e) {
                this.mRemoteAlive.set(false);
                String str2 = TAG;
                Log.e(str2, "linkToDeath:" + this.mAuthority + "'s process has already died");
            }
        }
    }

    public synchronized void unLinkToDeath(String tag) {
        this.mRemoteAlive.set(false);
        if (this.mDeathRecipient != null) {
            String str = TAG;
            Log.d(str, "unLinkToDeath(" + tag + "):desc=" + this.mDescription + ",auth= " + this.mAuthority + ",rec=" + Integer.toHexString(this.mDeathRecipient.hashCode()) + ",obj=" + Integer.toHexString(hashCode()));
            boolean result = this.mIBinder.unlinkToDeath(this.mDeathRecipient, 0);
            if (!result) {
                String str2 = TAG;
                Log.e(str2, "unLinkToDeath(" + tag + "):" + this.mAuthority + "'s process has already died");
            }
            this.mDeathRecipient = null;
        }
    }
}
