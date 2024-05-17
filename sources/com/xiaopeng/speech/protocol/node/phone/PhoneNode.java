package com.xiaopeng.speech.protocol.node.phone;

import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import com.xiaopeng.datalog.MoleEvent;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.actorapi.ResultActor;
import com.xiaopeng.speech.actorapi.SupportActor;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.common.bean.SliceData;
import com.xiaopeng.speech.common.bean.Value;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.jarvisproto.FeedUIEvent;
import com.xiaopeng.speech.protocol.DeviceInfoKey;
import com.xiaopeng.speech.protocol.VocabDefine;
import com.xiaopeng.speech.protocol.event.ContextEvent;
import com.xiaopeng.speech.protocol.event.PhoneEvent;
import com.xiaopeng.speech.protocol.event.query.QueryPhoneEvent;
import com.xiaopeng.speech.protocol.node.phone.bean.CallLogs;
import com.xiaopeng.speech.protocol.node.phone.bean.Contact;
import com.xiaopeng.speech.protocol.node.phone.bean.PhoneBean;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import com.xiaopeng.speech.protocol.utils.DeflaterUtils;
import com.xiaopeng.speech.proxy.AgentProxy;
import com.xiaopeng.speech.proxy.HotwordEngineProxy;
import com.xiaopeng.speech.speechwidget.ContentWidget;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.speechwidget.SupportWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class PhoneNode extends SpeechNode<PhoneListener> {
    public static final String CALL_VIEW_PAGE_ID = "XPPlugin_BlueToothCallApp://BlueToothCallView?from=speech";
    private static final int CHUNKSIZE = 262144;
    private static final String COMMAND_SPLIT = "#";
    public static final String INTENT_IN_COMING_PHONE = "来电话";
    private static final long SET_EXIT_CALL_TIMEOUT = 150;
    public static final String SKILL_NAME = "离线来电话";
    private static final int STOP_DIALOG_OPT_FORCE = 0;
    public static final String TASK_PHONE = "来电话";
    public static final int TRIGGER_ID = 10001;
    private volatile String deviceId;
    private volatile String duiWidget;
    private String mRawQuery;
    private List<PhoneBean> phoneBeanList;
    private static volatile int curTriggerID = -1;
    private static volatile int curNotifyType = 0;
    private static volatile boolean ifOnThePhone = false;
    private final String TAG = "PhoneNode";
    private final String SLOT_ENABLE_TTS = "enable_tts";
    private IBinder mBinder = new Binder();
    private volatile int syncResultCode = 0;

    public PhoneNode() {
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
    }

    public void syncContacts(List<Contact> list) {
        if (list != null && list.size() > 0) {
            List<String> contacts = new ArrayList<>();
            boolean isOverseasCarType = CarTypeUtils.isOverseasCarType();
            for (Contact item : list) {
                if (!isOverseasCarType) {
                    item.setName(item.getName().replaceAll("[0-9a-zA-Z]", ""));
                }
                contacts.add(item.getName());
            }
            SpeechClient.instance().getAgent().updateVocab(VocabDefine.CONTACT, (String[]) contacts.toArray(new String[contacts.size()]), true);
        }
    }

    public void syncContacts(List<Contact.PhoneInfo> list, String deviceId) {
        String unzipEventData;
        this.deviceId = deviceId;
        if (list != null && list.size() > 0) {
            if (!CarTypeUtils.isOverseasCarType() || CarTypeUtils.isE38EU() || CarTypeUtils.isE28AEU()) {
                JSONObject eventData = new JSONObject();
                JSONArray contacts = new JSONArray();
                try {
                    for (Contact.PhoneInfo phoneInfo : list) {
                        JSONObject phoneObj = new JSONObject();
                        phoneObj.put("id", phoneInfo.getId());
                        String strName = phoneInfo.getName();
                        if (!CarTypeUtils.isE38EU() && !CarTypeUtils.isE28AEU() && !TextUtils.isEmpty(strName)) {
                            strName = strName.replaceAll("[0-9]", "");
                            boolean isWord = isEnglish(strName);
                            if (isWord) {
                                strName = strName.toUpperCase();
                            } else if (!CarTypeUtils.isE38ZH() && !CarTypeUtils.isE28AZH() && !CarTypeUtils.isD21ZH()) {
                                strName = strName.replaceAll("[a-zA-Z]", "");
                            }
                        }
                        phoneInfo.setName(strName);
                        phoneObj.put("name", phoneInfo.getName());
                        contacts.put(phoneObj);
                    }
                    eventData.put("data", contacts);
                    eventData.put("deveiceId", deviceId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String unzipEventData2 = String.valueOf(eventData);
                byte[] bis = unzipEventData2.getBytes();
                LogUtils.i("PhoneNode", "uncompress source data size " + bis.length);
                String zipEventData = DeflaterUtils.compressForGzip(unzipEventData2);
                byte[] bis2 = zipEventData.getBytes();
                LogUtils.i("PhoneNode", "compress data size： " + bis2.length);
                byte[][] divideData = divideArray(bis2, 262144);
                if (divideData == null) {
                    return;
                }
                int length = divideData.length;
                int i = 0;
                while (i < length) {
                    byte[] item = divideData[i];
                    LogUtils.i("PhoneNode", "divide data");
                    if (item == null) {
                        unzipEventData = unzipEventData2;
                    } else {
                        unzipEventData = unzipEventData2;
                        SpeechClient.instance().getAgent().uploadContact(VocabDefine.CONTACT, new SliceData(item, bis2.length), 1);
                    }
                    i++;
                    unzipEventData2 = unzipEventData;
                }
                return;
            }
            List<String> contacts2 = new ArrayList<>();
            for (Contact.PhoneInfo item2 : list) {
                contacts2.add(item2.getName());
            }
            SpeechClient.instance().getAgent().updateVocab(VocabDefine.CONTACT, (String[]) contacts2.toArray(new String[contacts2.size()]), true);
        }
    }

    public boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    public byte[][] divideArray(byte[] source, int chunkSize) {
        if (source == null || source.length == 0) {
            return null;
        }
        int totalLength = source.length;
        int arraySize = (int) Math.ceil(totalLength / chunkSize);
        byte[][] ret = (byte[][]) Array.newInstance(byte.class, arraySize, chunkSize);
        int start = 0;
        for (int i = 0; i < arraySize; i++) {
            if (start + chunkSize > totalLength) {
                System.arraycopy(source, start, ret[i], 0, source.length - start);
            } else {
                System.arraycopy(source, start, ret[i], 0, chunkSize);
            }
            start += chunkSize;
        }
        return ret;
    }

    public void syncCallLogs(List<CallLogs> callLogs, String deviceId) {
        if (callLogs != null && callLogs.size() > 0) {
            JSONObject eventData = new JSONObject();
            JSONArray contacts = new JSONArray();
            try {
                for (CallLogs phoneInfo : callLogs) {
                    JSONObject phoneObj = new JSONObject();
                    phoneObj.put("id", phoneInfo.getId());
                    phoneObj.put("name", phoneInfo.getName());
                    phoneObj.put("time", phoneInfo.getTime());
                    phoneObj.put("call_count", phoneInfo.getCallCount());
                    phoneInfo.setName(phoneInfo.getName().replaceAll("[0-9a-zA-Z]", ""));
                    contacts.put(phoneObj);
                }
                eventData.put("data", contacts);
                eventData.put("deveiceId", deviceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SpeechClient.instance().getAgent().uploadContacts(VocabDefine.CALLLOGS, String.valueOf(eventData), 1);
        }
    }

    public void cleanContacts() {
        SpeechClient.instance().getAgent().updateVocab(VocabDefine.CONTACT, null, false);
    }

    public void stopSpeechDialog(int stopOption) {
        LogUtils.i(this, "stopDialog option: " + stopOption);
        if ((CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) && curTriggerID != -1) {
            LogUtils.i("PhoneNode", "in trigger dialog, can not stop dialog.");
        } else if (stopOption == 0) {
            SpeechClient.instance().getWakeupEngine().stopDialog();
        } else {
            SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.SCRIPT_QUIT, "{\"data\":{\"domain\":\"phone\"}}");
        }
    }

    public void setBTStatus(boolean isConnected) {
        LogUtils.i(this, "setBTStatus:%b", Boolean.valueOf(isConnected));
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle("联系人");
        listWidget.setExtraType("phone");
        listWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(isConnected));
        SpeechClient.instance().getActorBridge().send(new ResultActor(PhoneEvent.QUERY_BLUETOOTH).setResult(listWidget));
    }

    @SpeechAnnotation(event = PhoneEvent.QUERY_SYNC_BLUETOOTH)
    public void onQuerySyncBluetooth(String event, String data) {
        Value bluetoothValue = SpeechClient.instance().getQueryInjector().queryData(QueryPhoneEvent.GET_BLUETOOTH_STATUS, null);
        Value syncValue = SpeechClient.instance().getQueryInjector().queryData(QueryPhoneEvent.GET_CONTACT_SYNC_STATUS, null);
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle("联系人");
        listWidget.setExtraType("phone");
        listWidget.addExtra("deviceId", this.deviceId);
        if (bluetoothValue != null) {
            listWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(bluetoothValue.getBoolean()));
        }
        if (syncValue != null) {
            listWidget.addExtra("phone_sync", String.valueOf(syncValue.getInteger()));
        }
        SpeechClient.instance().getActorBridge().send(new ResultActor(PhoneEvent.QUERY_SYNC_BLUETOOTH).setResult(listWidget));
    }

    public void onQuerySyncBluetooth(String event, String data, boolean bluetoothStatus, int syncStatus) {
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle("联系人");
        listWidget.setExtraType("phone");
        listWidget.addExtra("deviceId", this.deviceId);
        listWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(bluetoothStatus));
        listWidget.addExtra("phone_sync", String.valueOf(syncStatus));
        SpeechClient.instance().getActorBridge().send(new ResultActor(PhoneEvent.QUERY_SYNC_BLUETOOTH).setResult(listWidget));
    }

    public void incomingCallRing(String name, String number, boolean enableTts) {
        boolean isMicOpen;
        String tts;
        String slots;
        int i;
        LogUtils.i("incomingCallRing, enable tts: " + enableTts);
        String slots2 = "";
        try {
            boolean isMicOpen2 = SpeechClient.instance().getSpeechState().isMicrophoneMute();
            isMicOpen = isMicOpen2;
        } catch (Exception e) {
            isMicOpen = false;
        }
        String tts2 = "";
        if (!TextUtils.isEmpty(number)) {
            tts2 = number + "来电话了，要接听还是拒绝？";
        }
        if (!TextUtils.isEmpty(name)) {
            tts2 = name + "来电话了，要接听还是拒绝？";
        }
        if (!isMicOpen) {
            tts = tts2;
        } else {
            tts = "来电话了，麦克风已禁用，通话中可在电话应用内取消静音哦";
        }
        try {
            slots2 = new JSONObject().put("tts", tts).put("来电号码", number).toString();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        try {
            slots = new JSONObject().put("isLocalSkill", true).put("来电人", name).put("来电号码", number).put("tts", tts).put("command", "command://phone.in.accept#command://phone.in.reject").put("enable_tts", enableTts).toString();
        } catch (JSONException e3) {
            e3.printStackTrace();
            slots = slots2;
        }
        SpeechClient.instance().getSpeechState().setCanExitFlag(false);
        if (CarTypeUtils.isE38ZH()) {
            i = 1;
        } else if (CarTypeUtils.isE28AZH()) {
            i = 1;
        } else {
            i = 1;
            String tts3 = slots;
            SpeechClient.instance().getAgent().triggerIntentWithBinder(this.mBinder, "离线来电话", "来电话", "来电话", tts3);
            setPhoneCallStatusWithBinder(this.mBinder, i);
        }
        if (curNotifyType != 2) {
            LogUtils.i("PhoneNode", "clear disable info, type: " + curNotifyType);
            enableWakeup();
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", "通话中，语音不可用", 2);
            curNotifyType = 2;
        } else {
            LogUtils.i("PhoneNode", "cur notify type is already toast");
        }
        AgentProxy agent = SpeechClient.instance().getAgent();
        int[] iArr = new int[i];
        iArr[0] = 0;
        agent.triggerDialog(10001, iArr, slots);
        curTriggerID = 10001;
        setPhoneCallStatusWithBinder(this.mBinder, i);
    }

    public synchronized void incomingCallRing(String name, String number) {
        incomingCallRing(name, number, true);
    }

    public void outgoingCallRing() {
        LogUtils.i("outgoingCallRing");
        SpeechClient.instance().getAgent().sendUIEvent(FeedUIEvent.SCRIPT_QUIT, "{\"data\":{\"domain\":\"phone\"}}");
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            if (curNotifyType != 2) {
                LogUtils.i("PhoneNode", "clear disable info, type: " + curNotifyType);
                enableWakeup();
                SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", "通话中，语音不可用", 2);
                curNotifyType = 2;
            } else {
                LogUtils.i("PhoneNode", "cur notify type is already toast");
            }
        }
        stopSpeechDialog();
        setPhoneCallStatusWithBinder(this.mBinder, 2);
    }

    public synchronized void callOffhook() {
        ifOnThePhone = true;
        LogUtils.i("callOffhook");
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            leaveTrigger();
            if (curNotifyType != 1) {
                LogUtils.i("PhoneNode", "update disable info type from %s to info_flow", Integer.valueOf(curNotifyType));
                enableWakeup();
                curNotifyType = 1;
                SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", "通话中，语音不可用", 1);
            } else {
                LogUtils.i("PhoneNode", "cur notify type is info_flow");
            }
        }
        stopSpeechDialog();
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        setPhoneCallStatusWithBinder(this.mBinder, 3);
    }

    public synchronized void callEnd() {
        LogUtils.i("PhoneNode", "callEnd");
        ifOnThePhone = false;
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            leaveTrigger();
            curNotifyType = 0;
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", 2);
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", 1);
        }
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        setPhoneCallStatusWithBinder(this.mBinder, 0);
    }

    public void stopSpeechDialog() {
        LogUtils.i("PhoneNode", "stopSpeechDialog");
        if ((CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) && curTriggerID != -1) {
            LogUtils.i("PhoneNode", "in trigger dialog, can not stop dialog.");
        } else {
            SpeechClient.instance().getWakeupEngine().stopDialogReason("PhoneNode");
        }
    }

    public void setPhoneCallStatus(int callStatus) {
        SpeechClient.instance().getSpeechState().setPhoneCallStatus(callStatus);
    }

    public void setPhoneCallStatusWithBinder(IBinder binder, int callStatus) {
        SpeechClient.instance().getSpeechState().setPhoneCallStatusWithBinder(binder, callStatus);
    }

    public void leaveTrigger() {
        LogUtils.i("PhoneNode", "leave trigger, curTriggerID: %s", Integer.valueOf(curTriggerID));
        try {
            SpeechClient.instance().getAgent().leaveTriggerWithID(10001);
            curTriggerID = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void enableMainWakeup() {
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            if (ifOnThePhone) {
                LogUtils.i("PhoneNode", "on the phone, can not enable wakeup.");
                return;
            } else if (curTriggerID != -1) {
                LogUtils.i("PhoneNode", "in trigger dialog, can not enable wakeup.");
                return;
            }
        }
        enableWakeup();
        LogUtils.i("PhoneNode", "startRecord----------");
        setPhoneCallStatusWithBinder(this.mBinder, 5);
    }

    public synchronized void disableMainWakeup() {
        disableWakeup();
        LogUtils.i("PhoneNode", "stopRecord-------");
        setPhoneCallStatusWithBinder(this.mBinder, 4);
    }

    public boolean isFastWakeup() {
        return SpeechClient.instance().getWakeupEngine().isDefaultEnableWakeup();
    }

    private void enableWakeup() {
        LogUtils.i("PhoneNode", "enableWakeup");
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            LogUtils.i("PhoneNode", "e38 enable");
            if (curNotifyType == 0) {
                LogUtils.i("PhoneNode", "no disable info");
                return;
            }
            if (curNotifyType == 2) {
                LogUtils.i("PhoneNode", "clear toast disable info.");
                SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", 2);
            } else if (curNotifyType == 1) {
                LogUtils.i("PhoneNode", "clear info_flow disable info");
                SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", 1);
            }
            curNotifyType = 0;
        } else if (!CarTypeUtils.isOverseasCarType() || CarTypeUtils.isE38EU() || CarTypeUtils.isE28AEU()) {
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 2, "蓝牙电话", 1);
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 4, "蓝牙电话", 1);
            LogUtils.i("enableWakeup clear hot words");
            SpeechClient.instance().getHotwordEngine().removeHotWords(HotwordEngineProxy.BY_BLUETOOTH_PHONE);
        } else {
            SpeechClient.instance().getWakeupEngine().enableWakeupEnhance(null);
            SpeechClient.instance().getWakeupEngine().enableMainWakeupWord(this.mBinder);
        }
    }

    private void disableWakeup() {
        if (CarTypeUtils.isE38ZH() || CarTypeUtils.isE28AZH()) {
            LogUtils.i("PhoneNode", "e38 disableWakeup");
            if (curNotifyType != 0) {
                LogUtils.i("PhoneNode", "have been disabled");
                return;
            }
            curNotifyType = 2;
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 1, "蓝牙电话", "通话中，语音不可用", 2);
        } else if (!CarTypeUtils.isOverseasCarType()) {
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 2, "蓝牙电话", CarTypeUtils.isE38ZH() ? "通话中，语音不可用" : "通话中暂停服务，一会再叫我", 1);
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 4, "蓝牙电话", CarTypeUtils.isE38ZH() ? "通话中，语音不可用" : "通话中暂停服务，一会再叫我", 1);
            LogUtils.i("disableWakeup");
        } else if (CarTypeUtils.isE38EU() || CarTypeUtils.isE28AEU()) {
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 2, "蓝牙电话", "Calling... Unable to use Voice Assistant.", 1);
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 4, "蓝牙电话", "Calling... Unable to use Voice Assistant.", 1);
            LogUtils.i("E38V disableWakeup");
        } else {
            SpeechClient.instance().getWakeupEngine().disableWakeupEnhance(null);
            SpeechClient.instance().getWakeupEngine().disableMainWakeupWord(this.mBinder);
        }
    }

    @SpeechAnnotation(event = PhoneEvent.QUERY_CONTACTS)
    public void onQueryContacts(String event, String data) {
        PhoneBean phoneBean = PhoneBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onQueryContacts(event, phoneBean);
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.QUERY_DETAIL_PHONEINFO)
    public void onQueryDetailPhoneInfo(String event, String data) {
        JSONArray content;
        try {
            this.duiWidget = data;
            JSONObject widgetObj = new JSONObject(data);
            List<Contact.PhoneInfo> infos = new ArrayList<>();
            if (widgetObj.has("content") && (content = widgetObj.optJSONArray("content")) != null && content.length() > 0) {
                for (int i = 0; i < content.length(); i++) {
                    JSONObject phone = content.optJSONObject(i);
                    if (phone != null) {
                        Contact.PhoneInfo info = new Contact.PhoneInfo();
                        info.setName(phone.optString(SpeechWidget.WIDGET_TITLE));
                        if (phone.has(SpeechWidget.WIDGET_SUBTITLE) && !TextUtils.isEmpty(phone.optString(SpeechWidget.WIDGET_SUBTITLE))) {
                            info.setNumber(phone.optString(SpeechWidget.WIDGET_SUBTITLE));
                        }
                        JSONObject extra = phone.optJSONObject(SpeechWidget.WIDGET_EXTRA);
                        if (extra != null) {
                            info.setId(extra.optString("id"));
                        }
                        infos.add(info);
                    }
                }
            }
            LogUtils.d("PhoneNode", "onQueryDetailPhoneInfo data = " + data);
            JSONObject extra2 = widgetObj.optJSONObject(SpeechWidget.WIDGET_EXTRA);
            if (extra2 != null) {
                this.mRawQuery = extra2.optString("raw_query");
            }
            Object[] listenerList = this.mListenerList.collectCallbacks();
            if (listenerList != null) {
                for (Object obj : listenerList) {
                    ((PhoneListener) obj).onQueryDetailPhoneInfo(infos);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void replySupport(String event, boolean isSupport, String text, boolean isBtConnected) {
        SupportWidget supportWidget = new SupportWidget();
        supportWidget.setSupport(isSupport);
        supportWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(isBtConnected));
        supportWidget.setTTS(text);
        SpeechClient.instance().getActorBridge().send(new SupportActor(event).setResult(supportWidget));
    }

    public void postContactsResult(List<PhoneBean> phoneBeanList, boolean isBtConnected) {
        postContactsResult("联系人", phoneBeanList, isBtConnected);
    }

    public void postContactsResult(String searchKey, List<PhoneBean> phoneBeanList, boolean isBtConnected) {
        this.phoneBeanList = phoneBeanList;
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle(searchKey);
        listWidget.setExtraType("phone");
        listWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(isBtConnected));
        for (PhoneBean phoneBean : phoneBeanList) {
            ContentWidget contentWidget = new ContentWidget();
            contentWidget.setTitle(phoneBean.getName());
            contentWidget.setSubTitle(phoneBean.getNumber());
            contentWidget.addExtra("phone", phoneBean.toJson());
            listWidget.addContentWidget(contentWidget);
        }
        SpeechClient.instance().getActorBridge().send(new ResultActor(PhoneEvent.QUERY_CONTACTS).setResult(listWidget));
    }

    public void postDetailPhoneInfoResult(List<Contact.PhoneInfo> contacts) {
        if (contacts == null) {
            return;
        }
        try {
            if (!CarTypeUtils.isE38ZH() && !CarTypeUtils.isE28AZH()) {
                JSONArray jsonContacts = new JSONArray();
                for (Contact.PhoneInfo phoneInfo : contacts) {
                    if (phoneInfo != null) {
                        JSONObject contact = new JSONObject();
                        contact.put(SpeechWidget.WIDGET_SUBTITLE, phoneInfo.getNumber());
                        contact.put(SpeechWidget.WIDGET_TITLE, phoneInfo.getName());
                        JSONObject phone = new JSONObject();
                        phone.put("任意联系人", phoneInfo.getName());
                        phone.put("号码", phoneInfo.getNumber());
                        JSONObject extra = new JSONObject();
                        extra.put("phone", phone.toString());
                        contact.put(SpeechWidget.WIDGET_EXTRA, extra);
                        jsonContacts.put(contact);
                    }
                }
                JSONObject widget = new JSONObject(this.duiWidget);
                widget.put("content", jsonContacts);
                SpeechClient.instance().getAgent().sendEvent(ContextEvent.WIDGET_LIST, widget.toString());
                return;
            }
            JSONObject widgetObject = new JSONObject();
            try {
                widgetObject.put("widgetName", "default");
                widgetObject.put("duiWidget", SpeechWidget.TYPE_LIST);
                widgetObject.put("widgetId", UUID.randomUUID());
                widgetObject.put("count", contacts.size());
                widgetObject.put("tipsTimeout", 0);
                widgetObject.put(VuiConstants.ELEMENT_TYPE, SpeechWidget.TYPE_LIST);
                JSONArray contentArray = new JSONArray();
                for (int i = 0; i < contacts.size(); i++) {
                    JSONObject contentObject = new JSONObject();
                    JSONObject contentExtraObject = new JSONObject();
                    Contact.PhoneInfo contactObject = contacts.get(i);
                    contentExtraObject.put("phone", "{\"任意联系人\": \"" + contactObject.getName() + "\", \"号码\": \"" + contactObject.getNumber() + "\"}");
                    contentObject.put(SpeechWidget.WIDGET_EXTRA, contentExtraObject);
                    contentObject.put(SpeechWidget.WIDGET_TITLE, contactObject.getName());
                    contentObject.put(SpeechWidget.WIDGET_SUBTITLE, contactObject.getNumber());
                    contentArray.put(contentObject);
                }
                widgetObject.put("content", contentArray);
                widgetObject.put("name", "default");
                JSONObject extraObject = new JSONObject();
                extraObject.put("phone_sync", "1");
                extraObject.put(SpeechWidget.WIDGET_TITLE, "联系人");
                extraObject.put("extraType", "phone");
                extraObject.put(DeviceInfoKey.PHONE_BLUETOOTH, true);
                if (!TextUtils.isEmpty(this.mRawQuery)) {
                    extraObject.put("raw_query", this.mRawQuery);
                }
                widgetObject.put(SpeechWidget.WIDGET_EXTRA, extraObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject napadata = new JSONObject();
            napadata.put(MoleEvent.KEY_PAGE_ID, CALL_VIEW_PAGE_ID);
            napadata.put("data", widgetObject.toString());
            LogUtils.d("PhoneNode", "postDetailPhoneInfoResult:" + napadata);
            SpeechClient.instance().getAgent().sendEvent(ContextEvent.NAPA_WIDGET_LIST, napadata.toString());
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public List<Contact.PhoneInfo> removeDuplicate(List<Contact.PhoneInfo> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (!TextUtils.isEmpty(list.get(j).getNumber()) && list.get(j).getNumber().equals(list.get(i).getNumber())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    @SpeechAnnotation(event = PhoneEvent.OUT)
    public void onOut(String event, String data) {
        PhoneBean phoneBean = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String phoneJson = jsonObject.optString("phone");
            if (!TextUtils.isEmpty(phoneJson)) {
                phoneBean = PhoneBean.fromJson(phoneJson);
            } else {
                phoneBean = new PhoneBean();
                phoneBean.setName(jsonObject.optString("name"));
                phoneBean.setNumber(jsonObject.optString("number"));
                phoneBean.setId(jsonObject.optString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (phoneBean == null) {
            LogUtils.e(this, "phoneBean == null");
            return;
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onOut(phoneBean.getName(), phoneBean.getNumber(), phoneBean.getId());
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.SELECT)
    public void onPhoneSelectOut(String event, String data) {
        int select_num;
        PhoneBean phoneBean = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            select_num = jsonObject.optInt("select_num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (this.phoneBeanList != null && this.phoneBeanList.size() > 0) {
            if (select_num > 0 && select_num <= this.phoneBeanList.size()) {
                phoneBean = this.phoneBeanList.get(select_num - 1);
                if (phoneBean == null) {
                    LogUtils.e(this, "phoneBean == null");
                    return;
                }
                Object[] listenerList = this.mListenerList.collectCallbacks();
                if (listenerList != null) {
                    for (Object obj : listenerList) {
                        ((PhoneListener) obj).onOut(phoneBean.getName(), phoneBean.getNumber(), phoneBean.getId());
                        SpeechClient.instance().getTTSEngine().speak("好的，正在呼叫 " + phoneBean.getName());
                    }
                    return;
                }
                return;
            }
            SpeechClient.instance().getTTSEngine().speak("您的选择已经超出当前列表范围了哦");
            LogUtils.e(this, "select_num is  == " + select_num);
            return;
        }
        LogUtils.e(this, "phoneBeanList == null");
    }

    @SpeechAnnotation(event = PhoneEvent.IN_ACCEPT)
    public void onInAccept(String event, String data) {
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onInAccept();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.IN_REJECT)
    public void onInReject(String event, String data) {
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onInReject();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.REDIAL_SUPPORT)
    public void onRedialSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onRedialSupport();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.REDIAL)
    public void onRedial(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onRedial();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.CALLBACK_SUPPORT)
    public void onCallbackSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onCallbackSupport();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.CALLBACK)
    public void onCallback(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onCallback();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.OUT_CUSTOMERSERVICE)
    public void onOutCustomerservice(String event, String data) {
        String number = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            number = jsonObject.optString("number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onOutCustomerservice(number);
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.OUT_HELP)
    public void onOutHelp(String event, String data) {
        String number = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            number = jsonObject.optString("number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onOutHelp(number);
            }
        }
    }

    public void onSettingOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onSettingOpen();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.QUERY_BLUETOOTH)
    public void onQueryBluetooth(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onQueryBluetooth();
            }
        }
    }

    @SpeechAnnotation(event = PhoneEvent.SYNC_CONTACT_RESULT)
    public void onSyncContactResult(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        this.syncResultCode = Integer.valueOf(data).intValue();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onSyncContactResult(this.syncResultCode);
            }
        }
    }
}
