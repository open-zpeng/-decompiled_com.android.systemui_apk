package com.xiaopeng.systemui.utils;

import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.biutil.BiLog;
import com.xiaopeng.biutil.BiLogFactory;
import com.xiaopeng.biutil.BiLogUploader;
import com.xiaopeng.biutil.BiLogUploaderFactory;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
/* loaded from: classes24.dex */
public class BIHelper {
    private static final String BID = "B001";
    private static final String MODULE = "systemui";
    private static final String PID = "P10911";
    private static final String TAG = "BIHelper";
    private static BiLogUploader sBiLogUploader;

    /* loaded from: classes24.dex */
    public enum ID {
        applist,
        control_settings,
        carcontrol,
        air_conditioning,
        music,
        home,
        phone,
        parking,
        space,
        speech,
        defrost,
        mirror_heat,
        account,
        demist_front,
        demist_back,
        seat_heat_vent,
        ihb,
        incall,
        microphone,
        mode_factory,
        mode_check,
        download,
        mode_auth,
        usb,
        childsafety,
        mode_child,
        xlogo,
        mode_disable,
        ecall,
        power,
        bluetooth,
        wifi,
        signal,
        lock,
        headset,
        sos,
        car_show_mode,
        wireless_charge,
        sound_area,
        individual,
        cruise_card,
        map_card,
        phone_card,
        calling_card,
        media_card,
        incall_card,
        navi_card,
        speech_weather_card,
        ai_push_card,
        makeup,
        cinema,
        dubi,
        app,
        muse_space,
        map,
        camera,
        settings,
        panel,
        truck,
        charge_port,
        wiper,
        rearview_mirror,
        childsafe,
        seat_vent,
        seat_heat,
        seat_massg,
        brightness,
        screen_off,
        clean_screen,
        soundwave_in,
        soundwave_out,
        volume,
        window_all_open,
        window_all_close,
        flash_cool,
        purify,
        refresh,
        meditation,
        sleep,
        movie,
        xiaop,
        driver_mode,
        child_mode_left,
        child_mode_right,
        panoramic_view,
        waiting_mode,
        easy_load,
        steering_wheel
    }

    /* loaded from: classes24.dex */
    public enum Type {
        statusbar,
        navigationbar,
        dock,
        panel,
        infoflow,
        desktop,
        dialog,
        speech
    }

    /* loaded from: classes24.dex */
    public enum Screen {
        main(0),
        second(1),
        third(2),
        none(88);
        
        int value;

        Screen(int i) {
            this.value = i;
        }
    }

    /* loaded from: classes24.dex */
    public enum Action {
        click(-1),
        close(0),
        open(1),
        up(3),
        down(4),
        update(5);
        
        int value;

        Action(int i) {
            this.value = i;
        }
    }

    public static void test() {
    }

    public static void sendBIData(ID id, Type type) {
        sendBIData(id, type, Action.click);
    }

    public static void sendBIData(ID id, Type type, Action action) {
        sendBIData(id, type, action, Screen.main);
    }

    public static void sendBIData(ID id, Type type, Action action, Screen screen) {
        sendBIData(id, type, action, screen, null);
    }

    public static void sendBIData(ID id, Type type, Action action, Screen screen, String status) {
        sendBIData(id, type, action, screen, status, (String) null);
    }

    public static void sendBIData(ID id, Type type, Action action, Screen screen, String status, String extra) {
        String actions = null;
        String screens = null;
        if (action != null) {
            actions = String.valueOf(action.value);
        }
        if (screen != null) {
            screens = String.valueOf(screen.value);
        }
        sendBIData(id, type, actions, screens, status, extra);
    }

    public static void sendBIData(String arg) {
        Log.d(TAG, "sendBIDataj:" + arg);
        BpData data = (BpData) GsonUtil.fromJson(arg, (Class<Object>) BpData.class);
        if (data == null || TextUtils.isEmpty(data.id) || TextUtils.isEmpty(data.type)) {
            return;
        }
        ID id = null;
        Type type = null;
        try {
            id = ID.valueOf(data.id);
            type = Type.valueOf(data.type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendBIData(id, type, data.action, data.screen, data.status, data.extra);
    }

    private static void sendBIData(ID id, Type type, String action, String screen, String status, String extra) {
        if (id == null || type == null) {
            Logger.w(TAG, "Uploader id、type is null");
            return;
        }
        BiLog bilog = BiLogFactory.create(MODULE, PID, "B001");
        bilog.push("id", String.valueOf(id));
        bilog.push(VuiConstants.ELEMENT_TYPE, String.valueOf(type));
        if (action == null) {
            bilog.push("action", String.valueOf(Action.click.value));
        } else {
            bilog.push("action", action);
        }
        if (screen != null) {
            bilog.push("screen", screen);
        }
        if (status != null) {
            bilog.push("status", status);
        }
        if (extra != null) {
            bilog.push(SpeechWidget.WIDGET_EXTRA, extra);
        }
        Log.i(TAG, "Uploader:" + bilog.getString());
        if (sBiLogUploader == null) {
            sBiLogUploader = BiLogUploaderFactory.create();
        }
        sBiLogUploader.submit(bilog);
    }

    /* loaded from: classes24.dex */
    public static class BpData {
        private String action;
        private String extra;
        private String id;
        private String screen;
        private String status;
        private String type;

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAction() {
            return this.action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getScreen() {
            return this.screen;
        }

        public void setScreen(String screen) {
            this.screen = screen;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getExtra() {
            return this.extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String toString() {
            return "BpData{id='" + this.id + "', type='" + this.type + "', action='" + this.action + "', screen='" + this.screen + "', status='" + this.status + "', extra='" + this.extra + "'}";
        }
    }
}
