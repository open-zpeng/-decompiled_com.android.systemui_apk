package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class PointerCountClassifier extends FalsingClassifier {
    private static final int MAX_ALLOWED_POINTERS = 1;
    private static final int MAX_ALLOWED_POINTERS_SWIPE_DOWN = 2;
    private int mMaxPointerCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PointerCountClassifier(FalsingDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onTouchEvent(MotionEvent motionEvent) {
        int pCount = this.mMaxPointerCount;
        if (motionEvent.getActionMasked() == 0) {
            this.mMaxPointerCount = motionEvent.getPointerCount();
        } else {
            this.mMaxPointerCount = Math.max(this.mMaxPointerCount, motionEvent.getPointerCount());
        }
        if (pCount != this.mMaxPointerCount) {
            logDebug("Pointers observed:" + this.mMaxPointerCount);
        }
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        int interactionType = getInteractionType();
        return (interactionType == 0 || interactionType == 2) ? this.mMaxPointerCount > 2 : this.mMaxPointerCount > 1;
    }
}
