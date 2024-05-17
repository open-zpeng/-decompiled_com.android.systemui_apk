package com.xiaopeng.speech.protocol.node.phone;

import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.actorapi.ResultActor;
import com.xiaopeng.speech.actorapi.SupportActor;
import com.xiaopeng.speech.common.bean.SliceData;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.DeviceInfoKey;
import com.xiaopeng.speech.protocol.VocabDefine;
import com.xiaopeng.speech.protocol.event.ContextEvent;
import com.xiaopeng.speech.protocol.event.PhoneEvent;
import com.xiaopeng.speech.protocol.node.phone.bean.CallLogs;
import com.xiaopeng.speech.protocol.node.phone.bean.Contact;
import com.xiaopeng.speech.protocol.node.phone.bean.PhoneBean;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import com.xiaopeng.speech.protocol.utils.DeflaterUtils;
import com.xiaopeng.speech.proxy.HotwordEngineProxy;
import com.xiaopeng.speech.speechwidget.ContentWidget;
import com.xiaopeng.speech.speechwidget.ListWidget;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.speechwidget.SupportWidget;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class NewPhoneNode extends SpeechNode<PhoneListener> {
    private static final int CHUNKSIZE = 262144;
    private static final String COMMAND_SPLIT = "#";
    public static final String INTENT_IN_COMING_PHONE = "来电话";
    private static final long SET_EXIT_CALL_TIMEOUT = 150;
    public static final String SKILL_NAME = "离线来电话";
    public static final String TASK_PHONE = "来电话";
    private final String SLOT_ENABLE_TTS;
    private volatile String deviceId;
    private volatile String duiWidget;
    private IBinder mBinder;
    private List<PhoneBean> phoneBeanList;
    private volatile int syncResultCode;

    private NewPhoneNode() {
        this.SLOT_ENABLE_TTS = "enable_tts";
        this.mBinder = new Binder();
        this.syncResultCode = 0;
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
    }

    /* loaded from: classes23.dex */
    private static class Holder {
        private static final NewPhoneNode Instance = new NewPhoneNode();

        private Holder() {
        }
    }

    public static final NewPhoneNode instance() {
        return Holder.Instance;
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
            if (!CarTypeUtils.isOverseasCarType()) {
                JSONObject eventData = new JSONObject();
                JSONArray contacts = new JSONArray();
                try {
                    for (Contact.PhoneInfo phoneInfo : list) {
                        JSONObject phoneObj = new JSONObject();
                        phoneObj.put("id", phoneInfo.getId());
                        String strName = phoneInfo.getName();
                        if (!TextUtils.isEmpty(strName)) {
                            String strName2 = strName.replaceAll("[0-9]", "");
                            boolean isWord = isEnglish(strName2);
                            if (isWord) {
                                strName = strName2.toUpperCase();
                            } else {
                                strName = strName2.replaceAll("[a-zA-Z]", "");
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

    public void setBTStatus(boolean isConnected) {
        LogUtils.i(this, "setBTStatus:%b", Boolean.valueOf(isConnected));
        ListWidget listWidget = new ListWidget();
        listWidget.setTitle("联系人");
        listWidget.setExtraType("phone");
        listWidget.addExtra(DeviceInfoKey.PHONE_BLUETOOTH, String.valueOf(isConnected));
        SpeechClient.instance().getActorBridge().send(new ResultActor(PhoneEvent.QUERY_BLUETOOTH).setResult(listWidget));
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
        LogUtils.i("incomingCallRing, enable tts: " + enableTts);
        String slots = "";
        stopSpeechDialog();
        try {
            slots = new JSONObject().put("来电人", name).put("来电号码", number).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            slots = new JSONObject().put("isLocalSkill", true).put("来电人", name).put("来电号码", number).put("command", "command://phone.in.accept#command://phone.in.reject").put("enable_tts", enableTts).toString();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        SpeechClient.instance().getSpeechState().setCanExitFlag(false);
        SpeechClient.instance().getAgent().triggerIntent("离线来电话", "来电话", "来电话", slots);
        setPhoneCallStatus(this.mBinder, 1);
    }

    public synchronized void incomingCallRing(String name, String number) {
        incomingCallRing(name, number, true);
    }

    public void outgoingCallRing() {
        LogUtils.i("outgoingCallRing");
        stopSpeechDialog();
        setPhoneCallStatus(this.mBinder, 2);
    }

    public synchronized void callOffhook() {
        LogUtils.i("callOffhook");
        stopSpeechDialog();
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        setPhoneCallStatus(this.mBinder, 3);
    }

    public synchronized void callEnd() {
        LogUtils.i("callEnd");
        stopSpeechDialog();
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        setPhoneCallStatus(this.mBinder, 0);
    }

    public void stopSpeechDialog() {
        LogUtils.i("stopSpeechDialog");
        SpeechClient.instance().getWakeupEngine().stopDialog();
    }

    public void setPhoneCallStatus(IBinder binder, int callStatus) {
        SpeechClient.instance().getSpeechState().setPhoneCallStatusWithBinder(binder, callStatus);
    }

    public void enableMainWakeup() {
        enableWakeup();
        LogUtils.i("PhoneNode", "startRecord----------");
        setPhoneCallStatus(this.mBinder, 5);
    }

    public void disableMainWakeup() {
        disableWakeup();
        LogUtils.i("PhoneNode", "stopRecord-------");
        setPhoneCallStatus(this.mBinder, 4);
    }

    public boolean isFastWakeup() {
        return SpeechClient.instance().getWakeupEngine().isDefaultEnableWakeup();
    }

    private void enableWakeup() {
        if (!CarTypeUtils.isOverseasCarType()) {
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 2, "蓝牙电话", 1);
            SpeechClient.instance().getWakeupEngine().enableWakeupWithInfo(this.mBinder, 4, "蓝牙电话", 1);
            LogUtils.i("enableWakeup clear hot words");
            SpeechClient.instance().getHotwordEngine().removeHotWords(HotwordEngineProxy.BY_BLUETOOTH_PHONE);
            return;
        }
        SpeechClient.instance().getWakeupEngine().enableWakeupEnhance(null);
        SpeechClient.instance().getWakeupEngine().enableMainWakeupWord(this.mBinder);
    }

    private void disableWakeup() {
        if (!CarTypeUtils.isOverseasCarType()) {
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 2, "蓝牙电话", CarTypeUtils.isE38ZH() ? "通话中，语音不可用" : "通话中暂停服务，一会再叫我", 1);
            SpeechClient.instance().getWakeupEngine().disableWakeupWithInfo(this.mBinder, 4, "蓝牙电话", CarTypeUtils.isE38ZH() ? "通话中，语音不可用" : "通话中暂停服务，一会再叫我", 1);
            LogUtils.i("disableWakeup");
            return;
        }
        SpeechClient.instance().getWakeupEngine().disableWakeupEnhance(null);
        SpeechClient.instance().getWakeupEngine().disableMainWakeupWord(this.mBinder);
    }

    public void onQueryContacts(String event, String data) {
        PhoneBean phoneBean = PhoneBean.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onQueryContacts(event, phoneBean);
            }
        }
    }

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
            List<Contact.PhoneInfo> contacts2 = removeDuplicate(contacts);
            JSONArray jsonContacts = new JSONArray();
            for (Contact.PhoneInfo phoneInfo : contacts2) {
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
        } catch (JSONException e) {
            e.printStackTrace();
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

    public void onInAccept(String event, String data) {
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onInAccept();
            }
        }
    }

    public void onInReject(String event, String data) {
        SpeechClient.instance().getSpeechState().setCanExitFlag(true);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onInReject();
            }
        }
    }

    public void onRedialSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onRedialSupport();
            }
        }
    }

    public void onRedial(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onRedial();
            }
        }
    }

    public void onCallbackSupport(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onCallbackSupport();
            }
        }
    }

    public void onCallback(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onCallback();
            }
        }
    }

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

    public void onQueryBluetooth(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((PhoneListener) obj).onQueryBluetooth();
            }
        }
    }

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
