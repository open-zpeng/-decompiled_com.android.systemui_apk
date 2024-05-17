package com.android.systemui.classifier.brightline;

import android.graphics.Point;
import android.provider.DeviceConfig;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.ArrayList;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class ZigZagClassifier extends FalsingClassifier {
    private static final float MAX_X_PRIMARY_DEVIANCE = 0.05f;
    private static final float MAX_X_SECONDARY_DEVIANCE = 0.6f;
    private static final float MAX_Y_PRIMARY_DEVIANCE = 0.1f;
    private static final float MAX_Y_SECONDARY_DEVIANCE = 0.3f;
    private final float mMaxXPrimaryDeviance;
    private final float mMaxXSecondaryDeviance;
    private final float mMaxYPrimaryDeviance;
    private final float mMaxYSecondaryDeviance;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ZigZagClassifier(FalsingDataProvider dataProvider) {
        super(dataProvider);
        this.mMaxXPrimaryDeviance = DeviceConfig.getFloat("systemui", "brightline_falsing_zigzag_x_primary_deviance", (float) MAX_X_PRIMARY_DEVIANCE);
        this.mMaxYPrimaryDeviance = DeviceConfig.getFloat("systemui", "brightline_falsing_zigzag_y_primary_deviance", 0.1f);
        this.mMaxXSecondaryDeviance = DeviceConfig.getFloat("systemui", "brightline_falsing_zigzag_x_secondary_deviance", 0.6f);
        this.mMaxYSecondaryDeviance = DeviceConfig.getFloat("systemui", "brightline_falsing_zigzag_y_secondary_deviance", (float) MAX_Y_SECONDARY_DEVIANCE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        List<Point> rotatedPoints;
        float maxYDeviance;
        float maxXDeviance;
        List<MotionEvent> motionEvents = getRecentMotionEvents();
        if (motionEvents.size() < 3) {
            return false;
        }
        if (isHorizontal()) {
            rotatedPoints = rotateHorizontal();
        } else {
            rotatedPoints = rotateVertical();
        }
        float actualDx = Math.abs(rotatedPoints.get(0).x - rotatedPoints.get(rotatedPoints.size() - 1).x);
        float actualDy = Math.abs(rotatedPoints.get(0).y - rotatedPoints.get(rotatedPoints.size() - 1).y);
        logDebug("Actual: (" + actualDx + "," + actualDy + NavigationBarInflaterView.KEY_CODE_END);
        float runningAbsDx = 0.0f;
        float runningAbsDy = 0.0f;
        float pX = 0.0f;
        float pY = 0.0f;
        boolean firstLoop = true;
        for (Point point : rotatedPoints) {
            if (firstLoop) {
                pX = point.x;
                pY = point.y;
                firstLoop = false;
            } else {
                runningAbsDx += Math.abs(point.x - pX);
                runningAbsDy += Math.abs(point.y - pY);
                pX = point.x;
                pY = point.y;
                logDebug("(x, y, runningAbsDx, runningAbsDy) - (" + pX + ", " + pY + ", " + runningAbsDx + ", " + runningAbsDy + NavigationBarInflaterView.KEY_CODE_END);
            }
        }
        float devianceX = runningAbsDx - actualDx;
        float devianceY = runningAbsDy - actualDy;
        float distanceXIn = actualDx / getXdpi();
        float distanceYIn = actualDy / getYdpi();
        float totalDistanceIn = (float) Math.sqrt((distanceXIn * distanceXIn) + (distanceYIn * distanceYIn));
        if (actualDx > actualDy) {
            float maxXDeviance2 = this.mMaxXPrimaryDeviance * totalDistanceIn * getXdpi();
            float maxXDeviance3 = this.mMaxYSecondaryDeviance;
            maxYDeviance = maxXDeviance3 * totalDistanceIn * getYdpi();
            maxXDeviance = maxXDeviance2;
        } else {
            float maxYDeviance2 = this.mMaxXSecondaryDeviance;
            float maxXDeviance4 = maxYDeviance2 * totalDistanceIn * getXdpi();
            float maxXDeviance5 = this.mMaxYPrimaryDeviance;
            maxYDeviance = maxXDeviance5 * totalDistanceIn * getYdpi();
            maxXDeviance = maxXDeviance4;
        }
        logDebug("Straightness Deviance: (" + devianceX + "," + devianceY + ") vs (" + maxXDeviance + "," + maxYDeviance + NavigationBarInflaterView.KEY_CODE_END);
        return devianceX > maxXDeviance || devianceY > maxYDeviance;
    }

    private float getAtan2LastPoint() {
        MotionEvent firstEvent = getFirstMotionEvent();
        MotionEvent lastEvent = getLastMotionEvent();
        float offsetX = firstEvent.getX();
        float offsetY = firstEvent.getY();
        float lastX = lastEvent.getX() - offsetX;
        float lastY = lastEvent.getY() - offsetY;
        return (float) Math.atan2(lastY, lastX);
    }

    private List<Point> rotateVertical() {
        double angle = 1.5707963267948966d - getAtan2LastPoint();
        logDebug("Rotating to vertical by: " + angle);
        return rotateMotionEvents(getRecentMotionEvents(), -angle);
    }

    private List<Point> rotateHorizontal() {
        double angle = getAtan2LastPoint();
        logDebug("Rotating to horizontal by: " + angle);
        return rotateMotionEvents(getRecentMotionEvents(), angle);
    }

    private List<Point> rotateMotionEvents(List<MotionEvent> motionEvents, double angle) {
        List<Point> points = new ArrayList<>();
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        MotionEvent firstEvent = motionEvents.get(0);
        float offsetX = firstEvent.getX();
        float offsetY = firstEvent.getY();
        for (MotionEvent motionEvent : motionEvents) {
            float x = motionEvent.getX() - offsetX;
            float y = motionEvent.getY() - offsetY;
            double rotatedX = (x * cosAngle) + (y * sinAngle) + offsetX;
            double d = -sinAngle;
            double sinAngle2 = sinAngle;
            double sinAngle3 = x;
            double rotatedY = (d * sinAngle3) + (y * cosAngle) + offsetY;
            points.add(new Point((int) rotatedX, (int) rotatedY));
            firstEvent = firstEvent;
            sinAngle = sinAngle2;
            cosAngle = cosAngle;
        }
        MotionEvent firstEvent2 = firstEvent;
        MotionEvent lastEvent = motionEvents.get(motionEvents.size() - 1);
        Point firstPoint = points.get(0);
        Point lastPoint = points.get(points.size() - 1);
        logDebug("Before: (" + firstEvent2.getX() + "," + firstEvent2.getY() + "), (" + lastEvent.getX() + "," + lastEvent.getY() + NavigationBarInflaterView.KEY_CODE_END);
        logDebug("After: (" + firstPoint.x + "," + firstPoint.y + "), (" + lastPoint.x + "," + lastPoint.y + NavigationBarInflaterView.KEY_CODE_END);
        return points;
    }
}
