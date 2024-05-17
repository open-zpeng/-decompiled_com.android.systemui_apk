package com.xiaopeng.lib.apirouter;

import android.content.ContentProviderClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import java.util.List;
/* loaded from: classes22.dex */
public class ApiRouter {
    private static final String TAG = "ApiRouter";
    private static ApiMatcher mApiMatcher = new ApiMatcher();

    private ApiRouter() {
    }

    private static RemoteOperator publishModule(@NonNull UriStruct uriStruct, @NonNull IBinder iBinder, @NonNull String operationManifest) {
        RemoteOperator remoteOperator = RemoteOperator.fromJson(iBinder, operationManifest);
        mApiMatcher.publishUri(uriStruct, remoteOperator);
        return remoteOperator;
    }

    private static RemoteOperator wakeRemoteService(@NonNull UriStruct uriStruct) throws RemoteException {
        if (ApiPublisherProvider.CONTEXT == null) {
            throw new RemoteException("ApiRouter can not route. If it is an asynchronous thread, please check your Context first!");
        }
        Uri.Builder uriBuilder = new Uri.Builder();
        if (TextUtils.isEmpty(uriStruct.processTag)) {
            Uri.Builder scheme = uriBuilder.scheme("content");
            scheme.authority(uriStruct.applicationId + ClientConstants.BINDER.API_SUFFIX);
        } else {
            Uri.Builder scheme2 = uriBuilder.scheme("content");
            scheme2.authority(uriStruct.applicationId + "." + uriStruct.processTag + ClientConstants.BINDER.API_SUFFIX);
        }
        ContentProviderClient contentProviderClient = ApiPublisherProvider.CONTEXT.getContentResolver().acquireContentProviderClient(uriBuilder.build());
        if (contentProviderClient == null) {
            throw new RemoteException("Unknown service " + uriStruct);
        }
        try {
            Bundle bundle = contentProviderClient.call(uriStruct.serviceName, null, null);
            if (bundle == null) {
                throw new RemoteException("Server does not implement call");
            }
            IBinder remoteBinder = bundle.getBinder("binder");
            String manifest = bundle.getString("manifest");
            if (remoteBinder == null || TextUtils.isEmpty(manifest)) {
                throw new RemoteException("No matching method");
            }
            return publishModule(uriStruct, remoteBinder, manifest);
        } finally {
            contentProviderClient.release();
        }
    }

    public static <T> T route(@NonNull Uri uri) throws RemoteException {
        return (T) route(uri, false, null, false);
    }

    public static <T> T route(@NonNull Uri uri, boolean isOneWay) throws RemoteException {
        return (T) route(uri, false, null, isOneWay);
    }

    public static <T> T route(@NonNull Uri uri, byte[] blob) throws RemoteException {
        return (T) route(uri, true, blob, false);
    }

    public static <T> T route(@NonNull Uri uri, byte[] blob, boolean isOneWay) throws RemoteException {
        return (T) route(uri, true, blob, isOneWay);
    }

    private static <T> T route(@NonNull Uri uri, boolean withBlob, byte[] blob, boolean isOneWay) throws RemoteException {
        UriStruct uriStruct = toUriStruct(uri);
        RemoteOperator remoteOperator = mApiMatcher.matchRemoteOperator(uriStruct);
        Log.d(TAG, "route remoteOperator = " + remoteOperator);
        if (remoteOperator == null) {
            remoteOperator = wakeRemoteService(uriStruct);
        } else if (!remoteOperator.isRemoteAlive()) {
            mApiMatcher.unpublishUri(uriStruct);
            remoteOperator = wakeRemoteService(uriStruct);
        }
        return (T) remoteOperator.call(uri, withBlob, blob, isOneWay);
    }

    private static UriStruct toUriStruct(Uri uri) throws RemoteException {
        String fullAuthority = uri.getAuthority();
        if (TextUtils.isEmpty(fullAuthority)) {
            throw new RemoteException("Can not find authority in uri");
        }
        int indexOfBinder = fullAuthority.lastIndexOf(".");
        if (indexOfBinder == -1) {
            throw new RemoteException("Illegal uri authority");
        }
        String applicationId = fullAuthority.substring(0, indexOfBinder);
        String serviceName = fullAuthority.substring(indexOfBinder + 1);
        UriStruct uriStruct = new UriStruct();
        uriStruct.applicationId = applicationId;
        uriStruct.serviceName = serviceName;
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments != null && pathSegments.size() > 1) {
            uriStruct.processTag = pathSegments.get(1);
        }
        return uriStruct;
    }
}
