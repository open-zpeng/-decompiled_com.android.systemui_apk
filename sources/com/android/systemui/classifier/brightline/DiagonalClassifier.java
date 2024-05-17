package com.android.systemui.classifier.brightline;

import android.provider.DeviceConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class DiagonalClassifier extends FalsingClassifier {
    private static final float DIAGONAL = 0.7853982f;
    private static final float HORIZONTAL_ANGLE_RANGE = 0.08726646f;
    private static final float NINETY_DEG = 1.5707964f;
    private static final float ONE_HUNDRED_EIGHTY_DEG = 3.1415927f;
    private static final float THREE_HUNDRED_SIXTY_DEG = 6.2831855f;
    private static final float VERTICAL_ANGLE_RANGE = 0.08726646f;
    private final float mHorizontalAngleRange;
    private final float mVerticalAngleRange;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DiagonalClassifier(FalsingDataProvider dataProvider) {
        super(dataProvider);
        this.mHorizontalAngleRange = DeviceConfig.getFloat("systemui", "brightline_falsing_diagonal_horizontal_angle_range", 0.08726646f);
        this.mVerticalAngleRange = DeviceConfig.getFloat("systemui", "brightline_falsing_diagonal_horizontal_angle_range", 0.08726646f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        float angle = getAngle();
        if (angle == Float.MAX_VALUE || getInteractionType() == 5 || getInteractionType() == 6) {
            return false;
        }
        float f = this.mHorizontalAngleRange;
        float minAngle = DIAGONAL - f;
        float maxAngle = f + DIAGONAL;
        if (isVertical()) {
            float f2 = this.mVerticalAngleRange;
            minAngle = DIAGONAL - f2;
            maxAngle = f2 + DIAGONAL;
        }
        return angleBetween(angle, minAngle, maxAngle) || angleBetween(angle, minAngle + 1.5707964f, maxAngle + 1.5707964f) || angleBetween(angle, minAngle - 1.5707964f, maxAngle - 1.5707964f) || angleBetween(angle, minAngle + 3.1415927f, 3.1415927f + maxAngle);
    }

    private boolean angleBetween(float angle, float min, float max) {
        float min2 = normalizeAngle(min);
        float max2 = normalizeAngle(max);
        return min2 > max2 ? angle >= min2 || angle <= max2 : angle >= min2 && angle <= max2;
    }

    private float normalizeAngle(float angle) {
        if (angle < 0.0f) {
            return (angle % 6.2831855f) + 6.2831855f;
        }
        if (angle > 6.2831855f) {
            return angle % 6.2831855f;
        }
        return angle;
    }
}
