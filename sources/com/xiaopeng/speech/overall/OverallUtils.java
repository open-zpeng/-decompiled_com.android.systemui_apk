package com.xiaopeng.speech.overall;

import android.util.ArrayMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* loaded from: classes23.dex */
public class OverallUtils {
    public static Map<String, String[]> sEvents = new ArrayMap();
    public static Map<String, String[]> sPackageEvents;
    public static Map<String, String[]> sPackageQueryEvents;
    public static Map<String, String[]> sQueryEvents;

    static {
        sEvents.put("com.youku.iot", new String[]{"command://video.control.play", "command://video.control.resume", "command://video.control.pause", "command://video.control.stop", "command://video.forward", "command://video.backward", "command://video.settime", "command://video.control.collect", "command://video.control.collect.cancel", "command://video.control.prev", "command://video.control.next", "command://video.fullscreen", "command://video.fullscreen.exit", "command://video.definition.set", "command://video.play.page.exit", "command://video.play.select"});
        sQueryEvents = new ArrayMap();
        sQueryEvents.put("com.youku.iot", new String[]{"native://com.youku.iot.video.info.query"});
        sPackageEvents = new ArrayMap();
        sPackageEvents.put("com.youku.iot", new String[]{"isPlaying:false|play", "isPlaying:false|continue", "isPlaying:true|pause", "isPlaying:true|stop", "isInPlayPage:true|fastforward", "isInPlayPage:true|backforward", "isInPlayPage:true|settime", "isInPlayPage:true|favor", "isInPlayPage:true|unfavor", "isInPlayPage:true|previous", "isInPlayPage:true|next", "isFullScreen:false|fullscreen", "isFullScreen:true|unfullscreen", "isInPlayPage:true|definition", "isInPlayPage:true|back", "isInPlayPage:true|select"});
        sPackageQueryEvents = new ArrayMap();
        sPackageQueryEvents.put("com.youku.iot", new String[]{"isInPlayPage|isFullScreen|isPlaying|videoDuration|definitions|isLogin"});
    }

    public static String[] getObserverEvent(String packageName) {
        if (sEvents.containsKey(packageName) && sQueryEvents.containsKey(packageName)) {
            String[] events = sEvents.get(packageName);
            String[] querys = sQueryEvents.get(packageName);
            String[] returnEvents = new String[events.length + querys.length];
            System.arraycopy(events, 0, returnEvents, 0, events.length);
            System.arraycopy(querys, 0, returnEvents, events.length, querys.length);
            return returnEvents;
        } else if (sEvents.containsKey(packageName)) {
            return sEvents.get(packageName);
        } else {
            if (sQueryEvents.containsKey(packageName)) {
                return sQueryEvents.get(packageName);
            }
            return null;
        }
    }

    public static List<String> getPackageEvents(String packageName) {
        if (sPackageEvents.containsKey(packageName)) {
            return Arrays.asList(sPackageEvents.get(packageName));
        }
        return null;
    }

    public static List<String> getPackageQueryEvents(String packageName) {
        if (sPackageQueryEvents.containsKey(packageName)) {
            return Arrays.asList(sPackageQueryEvents.get(packageName));
        }
        return null;
    }

    public static List<String> getEvents(String packageName) {
        if (sEvents.containsKey(packageName)) {
            return Arrays.asList(sEvents.get(packageName));
        }
        return null;
    }

    public static List<String> getQueryEvents(String packageName) {
        if (sQueryEvents.containsKey(packageName)) {
            return Arrays.asList(sQueryEvents.get(packageName));
        }
        return null;
    }
}
