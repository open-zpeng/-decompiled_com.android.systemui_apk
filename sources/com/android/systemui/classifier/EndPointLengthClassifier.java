package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class EndPointLengthClassifier extends StrokeClassifier {
    public EndPointLengthClassifier(ClassifierData classifierData) {
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "END_LNGTH";
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        return EndPointLengthEvaluator.evaluate(stroke.getEndPointLength());
    }
}
