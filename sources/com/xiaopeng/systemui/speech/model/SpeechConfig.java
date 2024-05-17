package com.xiaopeng.systemui.speech.model;

import com.android.systemui.R;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class SpeechConfig {
    private final HashMap<Integer, SpeechWindowInfo> mWindowInfo;
    private ArrayList<Integer> sAreaList;
    private ArrayList<Integer> sEchoList;
    private ArrayList<Integer> sHintList;

    private SpeechConfig() {
        this.mWindowInfo = new HashMap<>();
    }

    public synchronized ArrayList<Integer> getSoundAreas() {
        if (this.sAreaList == null) {
            this.sAreaList = new ArrayList<>();
            this.sAreaList.add(1);
            this.sAreaList.add(2);
            this.sAreaList.add(3);
            this.sAreaList.add(4);
            this.sAreaList.add(999);
        }
        return this.sAreaList;
    }

    public ArrayList<Integer> getSoundAreasForAsr() {
        return getSoundAreas();
    }

    public synchronized ArrayList<Integer> getSoundAreasForHint() {
        if (this.sHintList == null) {
            this.sHintList = new ArrayList<>();
            this.sHintList.add(1);
        }
        return this.sHintList;
    }

    public synchronized ArrayList<Integer> getSoundAreasForEchoList() {
        if (this.sEchoList == null) {
            this.sEchoList = new ArrayList<>();
            this.sEchoList.add(1);
            this.sEchoList.add(2);
            this.sEchoList.add(3);
            this.sEchoList.add(4);
        }
        return this.sEchoList;
    }

    public synchronized SpeechWindowInfo getWindowInfo(int area) {
        SpeechWindowInfo info = this.mWindowInfo.get(Integer.valueOf(area));
        if (info != null) {
            return info;
        }
        String name = "asr-" + area;
        if (area == 1) {
            info = new SpeechWindowInfo(name, 168, 18, 620, -2, 8388659, WindowHelper.TYPE_VUI_OVERLAY);
        } else if (area == 2) {
            info = new SpeechWindowInfo(name, 0, 18, 515, -2, 8388661, WindowHelper.TYPE_VUI_OVERLAY);
        } else if (area == 3) {
            info = new SpeechWindowInfo(name, 0, 25, 525, -2, 8388691, WindowHelper.TYPE_VUI_OVERLAY);
        } else if (area == 4) {
            info = new SpeechWindowInfo(name, 0, 25, 515, -2, 8388693, WindowHelper.TYPE_VUI_OVERLAY);
        } else if (area == 999) {
            info = new SpeechWindowInfo(name, 0, 18, 490, -2, 81, WindowHelper.TYPE_VUI_OVERLAY);
        }
        this.mWindowInfo.put(Integer.valueOf(area), info);
        return info;
    }

    public int getLayout(int area) {
        if (area != 1) {
            if (area != 2) {
                if (area != 3) {
                    if (area != 4) {
                        if (area != 999) {
                            return 0;
                        }
                        return R.layout.speech_bottom_mid_window;
                    }
                    return R.layout.speech_bottom_right_window;
                }
                return R.layout.speech_bottom_left_window;
            }
            return R.layout.speech_top_right_window;
        }
        return R.layout.speech_top_left_window;
    }

    /* loaded from: classes24.dex */
    private static class SingleHolder {
        private static final SpeechConfig sInstance = new SpeechConfig();

        private SingleHolder() {
        }
    }

    public static SpeechConfig get() {
        return SingleHolder.sInstance;
    }
}
