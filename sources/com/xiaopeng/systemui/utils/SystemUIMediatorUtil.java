package com.xiaopeng.systemui.utils;

import com.xiaopeng.aar.server.ServerManager;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes24.dex */
public class SystemUIMediatorUtil {
    public static final String MODULE_NAME = "SystemUI";
    private static final String TAG = "SystemUIMediatorUtil";

    public static void systemUIMediatorApiRouterCall(final String path) {
        Logger.d(TAG, "systemUIMediatorApiRouterCall : " + path);
        ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.utils.SystemUIMediatorUtil.1
            @Override // java.lang.Runnable
            public void run() {
                ServerManager.get().send(SystemUIMediatorUtil.MODULE_NAME, path, null, null);
            }
        });
    }

    public static void systemUIMediatorApiRouterCallImportant(String path) {
        Logger.d(TAG, "systemUIMediatorApiRouterCall : " + path);
        ServerManager.get().send(MODULE_NAME, path, null, null);
    }

    public static void systemUIMediatorApiRouterCall(String path, Map<String, Object> params) {
        systemUIMediatorApiRouterCall(path, params, (byte[]) null);
    }

    public static void systemUIMediatorApiRouterCallImportant(String path, Map<String, Object> params) {
        systemUIMediatorApiRouterCallImportant(path, params, (byte[]) null);
    }

    public static void systemUIMediatorApiRouterCall(final String path, final Map<String, Object> params, final byte[] blob) {
        Logger.d(TAG, "systemUIMediatorApiRouterCall 2 : " + path);
        ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.utils.SystemUIMediatorUtil.2
            @Override // java.lang.Runnable
            public void run() {
                JSONObject jsonParam = new JSONObject();
                try {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        jsonParam.put(entry.getKey(), entry.getValue());
                    }
                    ServerManager.get().send(SystemUIMediatorUtil.MODULE_NAME, path, jsonParam.toString(), blob);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void systemUIMediatorApiRouterCallImportant(String path, Map<String, Object> params, byte[] blob) {
        Logger.d(TAG, "systemUIMediatorApiRouterCallImportant 2 : " + path);
        JSONObject jsonParam = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonParam.put(entry.getKey(), entry.getValue());
            }
            ServerManager.get().send(MODULE_NAME, path, jsonParam.toString(), blob);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void systemUIMediatorApiRouterCall(final String path, final String data) {
        Logger.d(TAG, "systemUIMediatorApiRouterCall 2 : " + path);
        ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.utils.SystemUIMediatorUtil.3
            @Override // java.lang.Runnable
            public void run() {
                ServerManager.get().send(SystemUIMediatorUtil.MODULE_NAME, path, data, null);
            }
        });
    }

    public static void systemUIMediatorApiRouterCallImportant(String path, String data) {
        Logger.d(TAG, "systemUIMediatorApiRouterCallImportant 2 : " + path);
        ServerManager.get().send(MODULE_NAME, path, data, null);
    }

    public static void systemUIMediatorApiRouterCall(final String path, final Map<String, Object> params, final boolean logEnable) {
        Logger.d(TAG, "systemUIMediatorApiRouterCall 3 : " + path + "," + logEnable);
        ThreadUtils.executeSingleThread(new Runnable() { // from class: com.xiaopeng.systemui.utils.SystemUIMediatorUtil.4
            @Override // java.lang.Runnable
            public void run() {
                JSONObject jsonParam = new JSONObject();
                try {
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        jsonParam.put(entry.getKey(), entry.getValue());
                    }
                    ServerManager.SendBuilder builder = new ServerManager.SendBuilder();
                    builder.setModule(SystemUIMediatorUtil.MODULE_NAME).setMsgId(path).setData(jsonParam.toString()).setLogEnable(logEnable);
                    ServerManager.get().send(builder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void systemUIMediatorApiRouterCallImportant(String path, Map<String, Object> params, boolean logEnable) {
        Logger.d(TAG, "systemUIMediatorApiRouterCallImportant 3 : " + path + "," + logEnable);
        JSONObject jsonParam = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonParam.put(entry.getKey(), entry.getValue());
            }
            ServerManager.SendBuilder builder = new ServerManager.SendBuilder();
            builder.setModule(MODULE_NAME).setMsgId(path).setData(jsonParam.toString()).setLogEnable(logEnable);
            ServerManager.get().send(builder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
