package com.android.systemui.classifier.brightline;
/* loaded from: classes21.dex */
public class TypeClassifier extends FalsingClassifier {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeClassifier(FalsingDataProvider dataProvider) {
        super(dataProvider);
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        boolean vertical = isVertical();
        boolean up = isUp();
        boolean right = isRight();
        int interactionType = getInteractionType();
        if (interactionType != 0) {
            if (interactionType == 1) {
                return vertical;
            }
            if (interactionType != 2) {
                if (interactionType != 4) {
                    if (interactionType == 5) {
                        return (right && up) ? false : true;
                    } else if (interactionType == 6) {
                        return right || !up;
                    } else if (interactionType != 8) {
                        if (interactionType != 9) {
                            return true;
                        }
                    }
                }
                return (vertical && up) ? false : true;
            }
        }
        return !vertical || up;
    }
}
