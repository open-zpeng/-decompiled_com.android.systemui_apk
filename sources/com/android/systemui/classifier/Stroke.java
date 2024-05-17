package com.android.systemui.classifier;

import java.util.ArrayList;
/* loaded from: classes21.dex */
public class Stroke {
    private final float mDpi;
    private long mEndTimeNano;
    private float mLength;
    private long mStartTimeNano;
    private final float NANOS_TO_SECONDS = 1.0E9f;
    private ArrayList<Point> mPoints = new ArrayList<>();

    public Stroke(long eventTimeNano, float dpi) {
        this.mDpi = dpi;
        this.mEndTimeNano = eventTimeNano;
        this.mStartTimeNano = eventTimeNano;
    }

    public void addPoint(float x, float y, long eventTimeNano) {
        this.mEndTimeNano = eventTimeNano;
        float f = this.mDpi;
        Point point = new Point(x / f, y / f, eventTimeNano - this.mStartTimeNano);
        if (!this.mPoints.isEmpty()) {
            float f2 = this.mLength;
            ArrayList<Point> arrayList = this.mPoints;
            this.mLength = f2 + arrayList.get(arrayList.size() - 1).dist(point);
        }
        this.mPoints.add(point);
    }

    public int getCount() {
        return this.mPoints.size();
    }

    public float getTotalLength() {
        return this.mLength;
    }

    public float getEndPointLength() {
        ArrayList<Point> arrayList = this.mPoints;
        return this.mPoints.get(0).dist(arrayList.get(arrayList.size() - 1));
    }

    public long getDurationNanos() {
        return this.mEndTimeNano - this.mStartTimeNano;
    }

    public float getDurationSeconds() {
        return ((float) getDurationNanos()) / 1.0E9f;
    }

    public ArrayList<Point> getPoints() {
        return this.mPoints;
    }

    public long getLastEventTimeNano() {
        if (this.mPoints.isEmpty()) {
            return this.mStartTimeNano;
        }
        ArrayList<Point> arrayList = this.mPoints;
        return arrayList.get(arrayList.size() - 1).timeOffsetNano;
    }
}
