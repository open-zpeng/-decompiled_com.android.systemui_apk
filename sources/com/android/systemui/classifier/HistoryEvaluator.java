package com.android.systemui.classifier;

import android.os.SystemClock;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class HistoryEvaluator {
    private static final float EPSILON = 1.0E-5f;
    private static final float HISTORY_FACTOR = 0.9f;
    private static final float INTERVAL = 50.0f;
    private final ArrayList<Data> mStrokes = new ArrayList<>();
    private final ArrayList<Data> mGestureWeights = new ArrayList<>();
    private long mLastUpdate = SystemClock.elapsedRealtime();

    public void addStroke(float evaluation) {
        decayValue();
        this.mStrokes.add(new Data(evaluation));
    }

    public void addGesture(float evaluation) {
        decayValue();
        this.mGestureWeights.add(new Data(evaluation));
    }

    public float getEvaluation() {
        return weightedAverage(this.mStrokes) + weightedAverage(this.mGestureWeights);
    }

    private float weightedAverage(ArrayList<Data> list) {
        float sumValue = 0.0f;
        float sumWeight = 0.0f;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Data data = list.get(i);
            sumValue += data.evaluation * data.weight;
            sumWeight += data.weight;
        }
        if (sumWeight == 0.0f) {
            return 0.0f;
        }
        return sumValue / sumWeight;
    }

    private void decayValue() {
        long time = SystemClock.elapsedRealtime();
        long j = this.mLastUpdate;
        if (time <= j) {
            return;
        }
        float factor = (float) Math.pow(0.8999999761581421d, ((float) (time - j)) / 50.0f);
        decayValue(this.mStrokes, factor);
        decayValue(this.mGestureWeights, factor);
        this.mLastUpdate = time;
    }

    private void decayValue(ArrayList<Data> list, float factor) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).weight *= factor;
        }
        while (!list.isEmpty() && isZero(list.get(0).weight)) {
            list.remove(0);
        }
    }

    private boolean isZero(float x) {
        return x <= EPSILON && x >= -1.0E-5f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public static class Data {
        public float evaluation;
        public float weight = 1.0f;

        public Data(float evaluation) {
            this.evaluation = evaluation;
        }
    }
}
