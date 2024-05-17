package com.android.systemui.classifier;
/* loaded from: classes21.dex */
public class DirectionClassifier extends StrokeClassifier {
    public DirectionClassifier(ClassifierData classifierData) {
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "DIR";
    }

    @Override // com.android.systemui.classifier.StrokeClassifier
    public float getFalseTouchEvaluation(int type, Stroke stroke) {
        Point firstPoint = stroke.getPoints().get(0);
        Point lastPoint = stroke.getPoints().get(stroke.getPoints().size() - 1);
        return DirectionEvaluator.evaluate(lastPoint.x - firstPoint.x, lastPoint.y - firstPoint.y, type);
    }
}
