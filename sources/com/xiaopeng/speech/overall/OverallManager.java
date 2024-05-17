package com.xiaopeng.speech.overall;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.google.gson.Gson;
import com.xiaopeng.lib.apirouter.ApiRouter;
import com.xiaopeng.speech.apirouter.Utils;
import com.xiaopeng.speech.overall.listener.IXpOverallListener;
import com.xiaopeng.speech.overall.listener.IXpRecordListener;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import com.xiaopeng.speech.vui.constants.OverallConstants;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.utils.LogUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class OverallManager {
    private static int DELAY_TIME = 200;
    private final String TAG;
    private final String asr_cmd;
    private Map<IXpOverallListener, String[]> listenerEventMap;
    private Map<IXpOverallListener, String[]> listenerQueryMap;
    private String mCallback;
    private Context mContext;
    private String mEvent;
    private String mEventData;
    private Runnable mEventRun;
    private HashSet<String> mEvents;
    private Handler mHandler;
    private IXpOverallListener mListener;
    private Map<String, HashSet<IXpOverallListener>> mListeners;
    private Handler mMainHandler;
    private String mObserver;
    private String[] mObserverEvents;
    private String mPackageName;
    private String mQuery;
    private String mQueryData;
    private Runnable mQueryRun;
    private HashSet<String> mQuerys;
    private IXpRecordListener mRecordListener;
    private String mRecordParam;
    private HandlerThread mThread;

    public void initRecord(Context context, final String param, IXpRecordListener listener) {
        this.mRecordListener = listener;
        this.mRecordParam = param;
        if (this.mHandler != null) {
            this.mContext = context.getApplicationContext();
            if (this.mObserver == null) {
                this.mObserver = this.mContext.getPackageName() + ".ApiRouterOverallService";
                registerReceiver();
            }
            String[] strArr = this.mObserverEvents;
            if (strArr != null) {
                if (!Arrays.asList(strArr).contains("xiaopeng.asr.result")) {
                    String[] strArr2 = this.mObserverEvents;
                    String[] returnObserverEvents = new String[strArr2.length + 1];
                    System.arraycopy(strArr2, 0, returnObserverEvents, 0, strArr2.length);
                    returnObserverEvents[this.mObserverEvents.length] = "xiaopeng.asr.result";
                    this.mObserverEvents = returnObserverEvents;
                }
            } else {
                this.mObserverEvents = new String[]{"xiaopeng.asr.result"};
            }
            subscribe(this.mObserver, this.mObserverEvents);
            this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.1
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("initRecognizer").appendQueryParameter("param", param).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void startRecord(final String param) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.2
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("startRecord").appendQueryParameter("param", param).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void stopRecord() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.3
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("stopRecord").build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void destroyRecord(IXpRecordListener listener) {
        if (this.mRecordListener == listener) {
            this.mRecordListener = null;
        }
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.4
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("destroyRecord").build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    OverallManager overallManager = OverallManager.this;
                    overallManager.unsubscribe(overallManager.mObserver);
                }
            });
        }
    }

    public boolean isSupportRecord() {
        Uri targetUrl = new Uri.Builder().authority(getAuthority()).path("destroyRecord").build();
        try {
            ApiRouter.route(targetUrl);
            return true;
        } catch (Throwable e) {
            LogUtils.e("OverallManager", "isSupportRecord: " + e.getMessage());
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes23.dex */
    public static class Holder {
        private static final OverallManager Instance = new OverallManager();

        private Holder() {
        }
    }

    private OverallManager() {
        this.mListeners = new ConcurrentHashMap();
        this.listenerEventMap = new ConcurrentHashMap();
        this.listenerQueryMap = new ConcurrentHashMap();
        this.asr_cmd = "xiaopeng.asr.result";
        this.TAG = "OverallManager";
        this.mObserver = null;
        this.mObserverEvents = null;
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mEvents = new HashSet<>();
        this.mQuerys = new HashSet<>();
        this.mEvent = null;
        this.mEventData = null;
        this.mEventRun = new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.21
            @Override // java.lang.Runnable
            public void run() {
                try {
                    OverallManager.this.mHandler.removeCallbacks(OverallManager.this.mEventRun);
                    if (!TextUtils.isEmpty(OverallManager.this.mEvent)) {
                        OverallManager.this.dispatchOverallEvent(OverallManager.this.mEvent, OverallManager.this.mEventData);
                    }
                } catch (Exception e) {
                }
            }
        };
        this.mQuery = null;
        this.mQueryData = null;
        this.mCallback = null;
        this.mQueryRun = new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.22
            @Override // java.lang.Runnable
            public void run() {
                try {
                    LogUtils.logInfo("OverallManager", "mQueryRun:" + OverallManager.this.mQuery + ",mQueryData:" + OverallManager.this.mQueryData);
                    OverallManager.this.mHandler.removeCallbacks(OverallManager.this.mQueryRun);
                    if (!TextUtils.isEmpty(OverallManager.this.mQuery)) {
                        LogUtils.logInfo("OverallManager", "mQueryRun:" + OverallManager.this.mQuery + ",mQueryData:" + OverallManager.this.mQueryData);
                        OverallManager.this.dispatchOverallQuery(OverallManager.this.mQuery, OverallManager.this.mQueryData, OverallManager.this.mCallback);
                    }
                } catch (Exception e) {
                }
            }
        };
        if (this.mThread == null) {
            this.mThread = new HandlerThread("VuiEngine-overall");
            this.mThread.start();
            this.mHandler = new Handler(this.mThread.getLooper());
        }
    }

    public static final OverallManager instance() {
        return Holder.Instance;
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, IXpOverallListener listener) {
        this.mContext = context.getApplicationContext();
        this.mPackageName = context.getPackageName();
        this.mListener = listener;
        this.mObserver = this.mPackageName + ".ApiRouterOverallService";
        this.mObserverEvents = OverallUtils.getObserverEvent(this.mPackageName);
        String[] strArr = this.mObserverEvents;
        if (strArr != null) {
            subscribe(this.mObserver, strArr);
        }
        registerReceiver();
    }

    public void subscribe() {
        Context context;
        IXpRecordListener iXpRecordListener = this.mRecordListener;
        if (iXpRecordListener != null && (context = this.mContext) != null) {
            initRecord(context, this.mRecordParam, iXpRecordListener);
            try {
                JSONObject asr_result = new JSONObject();
                asr_result.put("messageCode", "501");
                this.mRecordListener.onResult(asr_result.toString());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        subscribe(this.mObserver, this.mObserverEvents);
    }

    public void addObserverEvents(String[] events, String[] querys, IXpOverallListener listener) {
        for (int i = 0; events != null && i < events.length; i++) {
            updateListener(events[i], listener);
            this.mEvents.add(events[i]);
        }
        for (int i2 = 0; querys != null && i2 < querys.length; i2++) {
            updateListener(querys[i2], listener);
            this.mQuerys.add(querys[i2]);
        }
        if (events != null) {
            this.listenerEventMap.put(listener, events);
        }
        if (querys != null) {
            this.listenerQueryMap.put(listener, querys);
        }
        String[] returnEvents = new String[(events == null ? 0 : events.length) + (querys == null ? 0 : querys.length)];
        if (events != null) {
            System.arraycopy(events, 0, returnEvents, 0, events.length);
        }
        if (querys != null) {
            System.arraycopy(querys, 0, returnEvents, events == null ? 0 : events.length, querys.length);
        }
        String[] strArr = this.mObserverEvents;
        if (strArr != null) {
            String[] returnObserverEvents = new String[strArr.length + returnEvents.length];
            System.arraycopy(strArr, 0, returnObserverEvents, 0, strArr.length);
            System.arraycopy(returnEvents, 0, returnObserverEvents, this.mObserverEvents.length, returnEvents.length);
            this.mObserverEvents = returnObserverEvents;
        } else {
            this.mObserverEvents = returnEvents;
        }
        subscribe(this.mObserver, returnEvents);
    }

    public void updateListener(String event, IXpOverallListener listener) {
        HashSet set = this.mListeners.get(event);
        if (set == null) {
            HashSet set2 = new HashSet();
            set2.add(listener);
            this.mListeners.put(event, set2);
            return;
        }
        set.add(listener);
    }

    private void registerReceiver() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("carspeechservice.SpeechServer.Start");
            OverAllBroadCastReceiver receiver = new OverAllBroadCastReceiver();
            Application application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication", new Class[0]).invoke(null, null);
            application.registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            LogUtils.e("OverallManager", "registerReceiver e:" + e.getMessage());
        }
    }

    public void subscribe(Context context, String observer, String[] events) {
        if (this.mContext == null) {
            this.mContext = context.getApplicationContext();
            this.mPackageName = context.getPackageName();
        }
        if (this.mObserver == null) {
            this.mObserver = observer;
        }
        if (this.mObserverEvents == null) {
            this.mObserverEvents = events;
        }
        subscribe(this.mObserver, this.mObserverEvents);
        registerReceiver();
    }

    private void subscribe(final String observer, final String[] events) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.5
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        if (events != null && !TextUtils.isEmpty(observer)) {
                            LogUtils.logInfo("OverallManager", "subscribe:" + observer + "events:" + Arrays.asList(events));
                            Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("subscribe").appendQueryParameter("observer", observer).appendQueryParameter("param", new Gson().toJson(events)).build();
                            ApiRouter.route(targetUrl);
                        }
                    } catch (Exception e) {
                        LogUtils.e("OverallManager", "subscribe:" + observer + "e:" + e.getMessage());
                    }
                }
            });
        }
    }

    public void unsubscribe(final String observer) {
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    OverallManager.this.mObserverEvents = null;
                    if (TextUtils.isEmpty(observer)) {
                        return;
                    }
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("unsubscribe").appendQueryParameter("observer", observer).build();
                    ApiRouter.route(targetUrl);
                } catch (Exception e) {
                }
            }
        });
    }

    public void unsubscribe(final String observer, final String[] events) {
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (TextUtils.isEmpty(observer)) {
                        return;
                    }
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("unsubscribe").appendQueryParameter("observer", observer).appendQueryParameter("events", new Gson().toJson(events)).build();
                    ApiRouter.route(targetUrl);
                } catch (Exception e) {
                }
            }
        });
    }

    public void triggerIntent(final String skill, final String task, final String intent, final String slots) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.8
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("triggerIntent").appendQueryParameter("skill", skill).appendQueryParameter("task", task).appendQueryParameter("intent", intent).appendQueryParameter("slots", slots).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void triggerEvent(final String event, final String data) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.9
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("triggerEvent").appendQueryParameter("event", event).appendQueryParameter("data", data).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void stopDialog() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.10
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("stopDialog").build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void sendEvent(final String event, final String data) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.11
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("sendEvent").appendQueryParameter("event", event).appendQueryParameter("data", data).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void startDialogFrom(final String type) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.12
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("startDialogFrom").appendQueryParameter(VuiConstants.ELEMENT_TYPE, type).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void feedbackResult(final String event, final String data) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.13
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("feedbackResult").appendQueryParameter("event", event).appendQueryParameter("data", data).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void replySupport(final String event, final boolean isSupport, final String text) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.14
                @Override // java.lang.Runnable
                public void run() {
                    Uri.Builder appendQueryParameter = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("replySupport").appendQueryParameter("event", event);
                    Uri targetUrl = appendQueryParameter.appendQueryParameter("isSupport", isSupport + "").appendQueryParameter("text", text).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void replySupport(String event, boolean isSupport) {
        replySupport(event, isSupport, "");
    }

    public void speak(final String tts) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.15
                @Override // java.lang.Runnable
                public void run() {
                    Uri targetUrl = new Uri.Builder().authority(OverallManager.this.getAuthority()).path("speak").appendQueryParameter("tts", tts).build();
                    try {
                        ApiRouter.route(targetUrl);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setOverallListener(IXpOverallListener listener) {
        this.mListener = listener;
    }

    public void addOverallListener(IXpOverallListener listener) {
        this.mListener = null;
        if (this.mListeners.size() > 0) {
            for (Map.Entry<String, HashSet<IXpOverallListener>> entry : this.mListeners.entrySet()) {
                HashSet<IXpOverallListener> listeners = entry.getValue();
                listeners.add(listener);
                this.mListeners.put(entry.getKey(), listeners);
            }
        }
        if (this.mEvents.size() > 0) {
            String[] array = new String[this.mEvents.size()];
            this.mEvents.toArray(array);
            this.listenerEventMap.put(listener, array);
        }
        if (this.mQuerys.size() > 0) {
            String[] array2 = new String[this.mQuerys.size()];
            this.mQuerys.toArray(array2);
            this.listenerQueryMap.put(listener, array2);
        }
    }

    public void removeOverallListener(IXpOverallListener listener) {
        String[] events;
        String[] events2;
        if (this.mListener == null && this.mListeners.size() == 0) {
            return;
        }
        if (this.mListener.equals(listener)) {
            this.mListener = null;
        }
        if (this.listenerEventMap.containsKey(listener) && (events2 = this.listenerEventMap.get(listener)) != null) {
            for (int i = 0; i < events2.length; i++) {
                if (this.mListeners.containsKey(events2[i])) {
                    HashSet<IXpOverallListener> listeners = this.mListeners.get(events2[i]);
                    listeners.remove(listener);
                    if (listeners.size() == 0) {
                        this.mListeners.remove(events2[i]);
                    } else {
                        this.mListeners.put(events2[i], listeners);
                    }
                }
            }
        }
        if (this.listenerQueryMap.containsKey(listener) && (events = this.listenerQueryMap.get(listener)) != null) {
            for (int i2 = 0; i2 < events.length; i2++) {
                if (this.mListeners.containsKey(events[i2])) {
                    HashSet<IXpOverallListener> listeners2 = this.mListeners.get(events[i2]);
                    listeners2.remove(listener);
                    if (listeners2.size() == 0) {
                        this.mListeners.remove(events[i2]);
                    } else {
                        this.mListeners.put(events[i2], listeners2);
                    }
                }
            }
        }
    }

    public void dispatchOverallEvent(final String event, final String data) {
        try {
            if ("xiaopeng.asr.result".equals(event) && this.mRecordListener != null) {
                this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.16
                    @Override // java.lang.Runnable
                    public void run() {
                        OverallManager.this.mRecordListener.onResult(data);
                    }
                });
            } else if (TextUtils.isEmpty(this.mPackageName) || this.mListener == null) {
                if (TextUtils.isEmpty(this.mPackageName) && this.mListener == null) {
                    this.mEvent = event;
                    this.mEventData = data;
                    this.mHandler.postDelayed(this.mEventRun, DELAY_TIME);
                } else if (this.mListener == null) {
                    LogUtils.logInfo("OverallManager", "dispatchOverallEvent mListeners:" + this.mListeners);
                    if (!this.mListeners.containsKey(event)) {
                        if (this.mListeners == null) {
                            this.mEvent = event;
                            this.mEventData = data;
                            this.mHandler.postDelayed(this.mEventRun, DELAY_TIME);
                            return;
                        } else if (OverallUtils.getQueryEvents(this.mPackageName).indexOf(event) != -1) {
                            Uri targetUrl = new Uri.Builder().authority(getAuthority()).path("feedbackResult").appendQueryParameter("event", event).appendQueryParameter("data", new JSONObject().toString()).build();
                            ApiRouter.route(targetUrl);
                            return;
                        } else {
                            return;
                        }
                    }
                    LogUtils.logInfo("OverallManager", "dispatchOverallEvent mListeners:" + this.mListeners);
                    HashSet<IXpOverallListener> listeners = this.mListeners.get(event);
                    Iterator<IXpOverallListener> it = listeners.iterator();
                    while (it.hasNext()) {
                        final IXpOverallListener listener = it.next();
                        if (this.mEvents != null && this.mEvents.contains(event)) {
                            this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.20
                                @Override // java.lang.Runnable
                                public void run() {
                                    LogUtils.logInfo("OverallManager", "dispatchOverallEvent listener:" + listener);
                                    listener.onEvent(event, data);
                                }
                            });
                        } else if (this.mQuerys != null && this.mQuerys.contains(event)) {
                            JSONObject jsonObject = new JSONObject();
                            Object result = listener.onQuery(event);
                            jsonObject.put(event, result);
                            Uri targetUrl2 = new Uri.Builder().authority(getAuthority()).path("feedbackResult").appendQueryParameter("event", event).appendQueryParameter("data", jsonObject.toString()).build();
                            ApiRouter.route(targetUrl2);
                        }
                    }
                }
            } else {
                List<String> eventList = OverallUtils.getEvents(this.mPackageName);
                char c = 0;
                if (eventList != null) {
                    int index = eventList.indexOf(event);
                    if (index != -1) {
                        final String eventStr = OverallUtils.getPackageEvents(this.mPackageName).get(index);
                        LogUtils.logInfo("OverallManager", "dispatchOverallEvent eventStr:" + eventStr);
                        if (!TextUtils.isEmpty(eventStr)) {
                            if (!eventStr.contains("|")) {
                                this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.17
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        OverallManager.this.mListener.onEvent(eventStr, data);
                                    }
                                });
                            } else {
                                String[] events = eventStr.split("\\|");
                                String[] querys = events[0].split(",");
                                int i = 0;
                                while (i < querys.length) {
                                    String[] query1 = querys[i].split(NavigationBarInflaterView.KEY_IMAGE_DELIM);
                                    Object obj = this.mListener.onQuery(query1[c]);
                                    LogUtils.logInfo("OverallManager", "dispatchOverallEvent eventStr:" + query1[1] + ",obj:" + obj);
                                    if (obj != null) {
                                        if (obj instanceof Boolean) {
                                            if ((!query1[1].equals(OOBEEvent.STRING_TRUE) || !((Boolean) obj).booleanValue()) && (!query1[1].equals(OOBEEvent.STRING_FALSE) || ((Boolean) obj).booleanValue())) {
                                                return;
                                            }
                                        } else if (obj instanceof Integer) {
                                            if (!isNumber(query1[1]) || ((Integer) obj).intValue() != Integer.parseInt(query1[1])) {
                                                return;
                                            }
                                        } else if (!(obj instanceof String) || !((String) obj).equals(query1[1])) {
                                            return;
                                        }
                                        i++;
                                        c = 0;
                                    } else {
                                        return;
                                    }
                                }
                                LogUtils.logInfo("OverallManager", "dispatchOverallEvent run:" + events[1] + ",data:" + data);
                                final String event1 = events[1];
                                this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.18
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        OverallManager.this.mListener.onEvent(event1, data);
                                    }
                                });
                            }
                        }
                    } else {
                        int index2 = OverallUtils.getQueryEvents(this.mPackageName).indexOf(event);
                        if (index2 != -1) {
                            String query = OverallUtils.getPackageQueryEvents(this.mPackageName).get(index2);
                            if (!TextUtils.isEmpty(query)) {
                                JSONObject jsonObject2 = new JSONObject();
                                if (query.contains("|")) {
                                    String[] querys2 = query.split("\\|");
                                    for (int i2 = 0; i2 < querys2.length; i2++) {
                                        Object obj2 = this.mListener.onQuery(querys2[i2]);
                                        if (obj2 != null) {
                                            jsonObject2.put(querys2[i2], obj2);
                                        }
                                    }
                                } else {
                                    Object b = this.mListener.onQuery(query);
                                    if (b != null) {
                                        jsonObject2.put(event, b);
                                    }
                                }
                                if (jsonObject2.length() > 0) {
                                    LogUtils.logInfo("OverallManager", "feedbackResult:" + jsonObject2.toString());
                                    Uri targetUrl3 = new Uri.Builder().authority(getAuthority()).path("feedbackResult").appendQueryParameter("event", event).appendQueryParameter("data", jsonObject2.toString()).build();
                                    ApiRouter.route(targetUrl3);
                                }
                            }
                        }
                    }
                } else if (this.mEvents != null && Arrays.asList(this.mEvents).contains(event)) {
                    this.mMainHandler.post(new Runnable() { // from class: com.xiaopeng.speech.overall.OverallManager.19
                        @Override // java.lang.Runnable
                        public void run() {
                            OverallManager.this.mListener.onEvent(event, data);
                        }
                    });
                } else if (this.mQuerys != null && Arrays.asList(this.mQuerys).contains(event)) {
                    JSONObject jsonObject3 = new JSONObject();
                    Object result2 = this.mListener.onQuery(event);
                    jsonObject3.put(event, result2);
                    Uri targetUrl4 = new Uri.Builder().authority(getAuthority()).path("feedbackResult").appendQueryParameter("event", event).appendQueryParameter("data", jsonObject3.toString()).build();
                    ApiRouter.route(targetUrl4);
                }
            }
        } catch (Exception e) {
        }
    }

    private boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]+(_[0-9]+)*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public void dispatchOverallQuery(String event, String data, String callback) {
        Object b;
        Object b2;
        Object b3;
        try {
            this.mCallback = callback;
            if (!TextUtils.isEmpty(this.mPackageName) && this.mListener != null) {
                int index = OverallUtils.getQueryEvents(this.mPackageName).indexOf(event);
                if (index != -1) {
                    String query = OverallUtils.getPackageQueryEvents(this.mPackageName).get(index);
                    if (!TextUtils.isEmpty(query) && !query.contains("|") && (b3 = this.mListener.onQuery(query)) != null && !TextUtils.isEmpty(callback)) {
                        try {
                            ApiRouter.route(Uri.parse(callback).buildUpon().appendQueryParameter("result", new SpeechResult(event, b3).toString()).build());
                        } catch (Exception e) {
                        }
                    }
                } else if (Arrays.asList(this.mQuerys).contains(event) && (b2 = this.mListener.onQuery(event)) != null && !TextUtils.isEmpty(callback)) {
                    try {
                        ApiRouter.route(Uri.parse(callback).buildUpon().appendQueryParameter("result", new SpeechResult(event, b2).toString()).build());
                    } catch (Exception e2) {
                    }
                }
            } else if (TextUtils.isEmpty(this.mPackageName) && this.mListener == null) {
                this.mQuery = event;
                this.mQueryData = data;
                this.mHandler.postDelayed(this.mQueryRun, DELAY_TIME);
            } else if (this.mListener == null) {
                LogUtils.logInfo("OverallManager", "dispatchOverallEvent mListeners:" + this.mListeners);
                if (this.mListeners != null && this.mListeners.size() > 0) {
                    LogUtils.logInfo("OverallManager", "dispatchOverallEvent mListeners:" + this.mListeners);
                    HashSet<IXpOverallListener> listeners = this.mListeners.get(event);
                    Iterator<IXpOverallListener> it = listeners.iterator();
                    while (it.hasNext()) {
                        IXpOverallListener listener = it.next();
                        LogUtils.logInfo("OverallManager", "dispatchOverallEvent listener:" + listener + ",event:" + event);
                        if (this.mQuerys != null && this.mQuerys.contains(event) && (b = listener.onQuery(event)) != null && !TextUtils.isEmpty(callback)) {
                            try {
                                ApiRouter.route(Uri.parse(callback).buildUpon().appendQueryParameter("result", new SpeechResult(event, b).toString()).build());
                            } catch (Exception e3) {
                            }
                        }
                    }
                } else if (this.mListeners == null) {
                    this.mQuery = event;
                    this.mQueryData = data;
                    this.mHandler.postDelayed(this.mQueryRun, DELAY_TIME);
                }
            }
        } catch (Exception e4) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getAuthority() {
        if (!Utils.isXpDevice()) {
            return OverallConstants.OVERAll_THIRD_AUTHORITY;
        }
        return OverallConstants.OVERAll_AUTHORITY;
    }
}
