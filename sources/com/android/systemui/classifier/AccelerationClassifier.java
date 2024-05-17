package com.android.systemui.classifier;

import android.view.MotionEvent;
import java.util.HashMap;
/* loaded from: classes21.dex */
public class AccelerationClassifier extends StrokeClassifier {
    private final HashMap<Stroke, Data> mStrokeMap = new HashMap<>();

    public AccelerationClassifier(ClassifierData classifierData) {
        this.mClassifierData = classifierData;
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "ACC";
    }

    @Override // com.android.systemui.classifier.Classifier
    public void onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 0) {
            this.mStrokeMap.clear();
        }
        for (int i = 0; i < event.getPointerCount(); i++) {
            Stroke stroke = this.mClassifierData.getStroke(event.getPointerId(i));
            Point point = stroke.getPoints().get(stroke.getPoints().size() - 1);
            if (this.mStrokeMap.get(stroke) == null) {
                this.mStrokeMap.put(stroke, new Data(point));
            } else {
                this.mStrokeMap.get(stroke).addPoint(point);
            }
        }
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        Data data = this.mStrokeMap.get(stroke);
        return SpeedRatioEvaluator.evaluate(data.maxSpeedRatio) * 2.0f;
    }

    /* loaded from: classes21.dex */
    private static class Data {
        static final float MILLIS_TO_NANOS = 1000000.0f;
        Point previousPoint;
        float previousSpeed = 0.0f;
        float maxSpeedRatio = 0.0f;

        public Data(Point point) {
            this.previousPoint = point;
        }

        public void addPoint(Point point) {
            float distance = this.previousPoint.dist(point);
            float duration = (float) ((point.timeOffsetNano - this.previousPoint.timeOffsetNano) + 1);
            float speed = distance / duration;
            if (duration > 2.0E7f || duration < 5000000.0f) {
                this.previousSpeed = 0.0f;
                this.previousPoint = point;
                return;
            }
            float f = this.previousSpeed;
            if (f != 0.0f) {
                this.maxSpeedRatio = Math.max(this.maxSpeedRatio, speed / f);
            }
            this.previousSpeed = speed;
            this.previousPoint = point;
        }
    }
}
