package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class EndPointRatioClassifier extends StrokeClassifier {
    public EndPointRatioClassifier(ClassifierData classifierData) {
        this.mClassifierData = classifierData;
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "END_RTIO";
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        float ratio;
        if (stroke.getTotalLength() == 0.0f) {
            ratio = 1.0f;
        } else {
            float ratio2 = stroke.getEndPointLength();
            ratio = ratio2 / stroke.getTotalLength();
        }
        return EndPointRatioEvaluator.evaluate(ratio);
    }
}
