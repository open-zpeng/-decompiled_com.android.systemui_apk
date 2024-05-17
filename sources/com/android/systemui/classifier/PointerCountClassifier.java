package com.android.systemui.classifier;

import android.view.MotionEvent;
/* loaded from: classes21.dex */
public class PointerCountClassifier extends GestureClassifier {
    private int mCount = 0;

    public PointerCountClassifier(ClassifierData classifierData) {
    }

    @Override // com.android.systemui.classifier.Classifier
    public String getTag() {
        return "PTR_CNT";
    }

    @Override // com.android.systemui.classifier.Classifier
    public void onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == 0) {
            this.mCount = 1;
        }
        if (action == 5) {
            this.mCount++;
        }
    }

    @Override // com.android.systemui.classifier.GestureClassifier
    public float getFalseTouchEvaluation(int type) {
        return PointerCountEvaluator.evaluate(this.mCount);
    }
}
