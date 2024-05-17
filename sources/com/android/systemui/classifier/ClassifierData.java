package com.android.systemui.classifier;

import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class ClassifierData {
    private static final long MINIMUM_DT_NANOS = 16666666;
    private static final long MINIMUM_DT_SMEAR_NANOS = 2500000;
    private final float mDpi;
    private SparseArray<Stroke> mCurrentStrokes = new SparseArray<>();
    private ArrayList<Stroke> mEndingStrokes = new ArrayList<>();

    public ClassifierData(float dpi) {
        this.mDpi = dpi;
    }

    public boolean update(MotionEvent event) {
        if (event.getActionMasked() != 2 || this.mCurrentStrokes.size() == 0 || event.getEventTimeNano() - this.mCurrentStrokes.valueAt(0).getLastEventTimeNano() >= 14166666) {
            this.mEndingStrokes.clear();
            int action = event.getActionMasked();
            if (action == 0) {
                this.mCurrentStrokes.clear();
            }
            for (int i = 0; i < event.getPointerCount(); i++) {
                int id = event.getPointerId(i);
                if (this.mCurrentStrokes.get(id) == null) {
                    this.mCurrentStrokes.put(id, new Stroke(event.getEventTimeNano(), this.mDpi));
                }
                this.mCurrentStrokes.get(id).addPoint(event.getX(i), event.getY(i), event.getEventTimeNano());
                if (action == 1 || action == 3 || (action == 6 && i == event.getActionIndex())) {
                    this.mEndingStrokes.add(getStroke(id));
                }
            }
            return true;
        }
        return false;
    }

    public void cleanUp(MotionEvent event) {
        this.mEndingStrokes.clear();
        int action = event.getActionMasked();
        for (int i = 0; i < event.getPointerCount(); i++) {
            int id = event.getPointerId(i);
            if (action == 1 || action == 3 || (action == 6 && i == event.getActionIndex())) {
                this.mCurrentStrokes.remove(id);
            }
        }
    }

    public ArrayList<Stroke> getEndingStrokes() {
        return this.mEndingStrokes;
    }

    public Stroke getStroke(int id) {
        return this.mCurrentStrokes.get(id);
    }
}
