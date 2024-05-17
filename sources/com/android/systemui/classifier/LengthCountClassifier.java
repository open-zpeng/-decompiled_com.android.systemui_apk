package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class LengthCountClassifier extends StrokeClassifier {
    public LengthCountClassifier(ClassifierData classifierData) {
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "LEN_CNT";
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        return LengthCountEvaluator.evaluate(stroke.getTotalLength() / Math.max(1.0f, stroke.getCount() - 2));
    }
}
