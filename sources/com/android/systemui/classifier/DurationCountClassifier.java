package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class DurationCountClassifier extends StrokeClassifier {
    public DurationCountClassifier(ClassifierData classifierData) {
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "DUR";
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        return DurationCountEvaluator.evaluate(stroke.getDurationSeconds() / stroke.getCount());
    }
}
