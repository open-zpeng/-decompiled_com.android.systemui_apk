package com.android.systemui.statusbar;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
/* loaded from: classes21.dex */
public class GestureRecorder {
    public static final boolean DEBUG = true;
    static final long SAVE_DELAY = 5000;
    static final int SAVE_MESSAGE = 6351;
    public static final String TAG = GestureRecorder.class.getSimpleName();
    private String mLogfile;
    private int mLastSaveLen = -1;
    private Handler mHandler = new Handler() { // from class: com.android.systemui.statusbar.GestureRecorder.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == GestureRecorder.SAVE_MESSAGE) {
                GestureRecorder.this.save();
            }
        }
    };
    private LinkedList<Gesture> mGestures = new LinkedList<>();
    private Gesture mCurrentGesture = null;

    /* loaded from: classes21.dex */
    public class Gesture {
        private LinkedList<Record> mRecords = new LinkedList<>();
        private HashSet<String> mTags = new HashSet<>();
        long mDownTime = -1;
        boolean mComplete = false;

        /* loaded from: classes21.dex */
        public abstract class Record {
            long time;

            public abstract String toJson();

            public Record() {
            }
        }

        public Gesture() {
        }

        /* loaded from: classes21.dex */
        public class MotionEventRecord extends Record {
            public MotionEvent event;

            public MotionEventRecord(long when, MotionEvent event) {
                super();
                this.time = when;
                this.event = MotionEvent.obtain(event);
            }

            String actionName(int action) {
                if (action != 0) {
                    if (action != 1) {
                        if (action != 2) {
                            if (action == 3) {
                                return "cancel";
                            }
                            return String.valueOf(action);
                        }
                        return "move";
                    }
                    return VuiConstants.EVENT_VALUE_DIRECTION_UP;
                }
                return "down";
            }

            @Override // com.android.systemui.statusbar.GestureRecorder.Gesture.Record
            public String toJson() {
                return String.format("{\"type\":\"motion\", \"time\":%d, \"action\":\"%s\", \"x\":%.2f, \"y\":%.2f, \"s\":%.2f, \"p\":%.2f}", Long.valueOf(this.time), actionName(this.event.getAction()), Float.valueOf(this.event.getRawX()), Float.valueOf(this.event.getRawY()), Float.valueOf(this.event.getSize()), Float.valueOf(this.event.getPressure()));
            }
        }

        /* loaded from: classes21.dex */
        public class TagRecord extends Record {
            public String info;
            public String tag;

            public TagRecord(long when, String tag, String info) {
                super();
                this.time = when;
                this.tag = tag;
                this.info = info;
            }

            @Override // com.android.systemui.statusbar.GestureRecorder.Gesture.Record
            public String toJson() {
                return String.format("{\"type\":\"tag\", \"time\":%d, \"tag\":\"%s\", \"info\":\"%s\"}", Long.valueOf(this.time), this.tag, this.info);
            }
        }

        public void add(MotionEvent ev) {
            this.mRecords.add(new MotionEventRecord(ev.getEventTime(), ev));
            long j = this.mDownTime;
            if (j < 0) {
                this.mDownTime = ev.getDownTime();
            } else if (j != ev.getDownTime()) {
                String str = GestureRecorder.TAG;
                Log.w(str, "Assertion failure in GestureRecorder: event downTime (" + ev.getDownTime() + ") does not match gesture downTime (" + this.mDownTime + NavigationBarInflaterView.KEY_CODE_END);
            }
            int actionMasked = ev.getActionMasked();
            if (actionMasked == 1 || actionMasked == 3) {
                this.mComplete = true;
            }
        }

        public void tag(long when, String tag, String info) {
            this.mRecords.add(new TagRecord(when, tag, info));
            this.mTags.add(tag);
        }

        public boolean isComplete() {
            return this.mComplete;
        }

        public String toJson() {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            Iterator<Record> it = this.mRecords.iterator();
            while (it.hasNext()) {
                Record r = it.next();
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(r.toJson());
            }
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
            return sb.toString();
        }
    }

    public GestureRecorder(String filename) {
        this.mLogfile = filename;
    }

    public void add(MotionEvent ev) {
        synchronized (this.mGestures) {
            if (this.mCurrentGesture == null || this.mCurrentGesture.isComplete()) {
                this.mCurrentGesture = new Gesture();
                this.mGestures.add(this.mCurrentGesture);
            }
            this.mCurrentGesture.add(ev);
        }
        saveLater();
    }

    public void tag(long when, String tag, String info) {
        synchronized (this.mGestures) {
            if (this.mCurrentGesture == null) {
                this.mCurrentGesture = new Gesture();
                this.mGestures.add(this.mCurrentGesture);
            }
            this.mCurrentGesture.tag(when, tag, info);
        }
        saveLater();
    }

    public void tag(long when, String tag) {
        tag(when, tag, null);
    }

    public void tag(String tag) {
        tag(SystemClock.uptimeMillis(), tag, null);
    }

    public void tag(String tag, String info) {
        tag(SystemClock.uptimeMillis(), tag, info);
    }

    public String toJsonLocked() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append(NavigationBarInflaterView.SIZE_MOD_START);
        int count = 0;
        Iterator<Gesture> it = this.mGestures.iterator();
        while (it.hasNext()) {
            Gesture g = it.next();
            if (g.isComplete()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(g.toJson());
                count++;
            }
        }
        this.mLastSaveLen = count;
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    public String toJson() {
        String s;
        synchronized (this.mGestures) {
            s = toJsonLocked();
        }
        return s;
    }

    public void saveLater() {
        this.mHandler.removeMessages(SAVE_MESSAGE);
        this.mHandler.sendEmptyMessageDelayed(SAVE_MESSAGE, SAVE_DELAY);
    }

    public void save() {
        synchronized (this.mGestures) {
            try {
                BufferedWriter w = new BufferedWriter(new FileWriter(this.mLogfile, true));
                w.append((CharSequence) (toJsonLocked() + "\n"));
                w.close();
                this.mGestures.clear();
                if (this.mCurrentGesture != null && !this.mCurrentGesture.isComplete()) {
                    this.mGestures.add(this.mCurrentGesture);
                }
                Log.v(TAG, String.format("Wrote %d complete gestures to %s", Integer.valueOf(this.mLastSaveLen), this.mLogfile));
            } catch (IOException e) {
                Log.e(TAG, String.format("Couldn't write gestures to %s", this.mLogfile), e);
                this.mLastSaveLen = -1;
            }
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        save();
        if (this.mLastSaveLen >= 0) {
            pw.println(String.valueOf(this.mLastSaveLen) + " gestures written to " + this.mLogfile);
            return;
        }
        pw.println("error writing gestures");
    }
}
