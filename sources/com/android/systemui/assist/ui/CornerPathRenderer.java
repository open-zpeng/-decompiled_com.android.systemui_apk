package com.android.systemui.assist.ui;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public abstract class CornerPathRenderer {
    private static final float ACCEPTABLE_ERROR = 0.1f;

    /* loaded from: classes21.dex */
    public enum Corner {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT,
        TOP_LEFT
    }

    public abstract Path getCornerPath(Corner corner);

    public Path getInsetPath(Corner corner, float insetAmountPx) {
        return approximateInnerPath(getCornerPath(corner), -insetAmountPx);
    }

    private Path approximateInnerPath(Path input, float delta) {
        List<PointF> points = shiftBy(getApproximatePoints(input), delta);
        return toPath(points);
    }

    private ArrayList<PointF> getApproximatePoints(Path path) {
        float[] rawInput = path.approximate(0.1f);
        ArrayList<PointF> output = new ArrayList<>();
        for (int i = 0; i < rawInput.length; i += 3) {
            output.add(new PointF(rawInput[i + 1], rawInput[i + 2]));
        }
        return output;
    }

    private ArrayList<PointF> shiftBy(ArrayList<PointF> input, float delta) {
        ArrayList<PointF> output = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            PointF point = input.get(i);
            PointF normal = normalAt(input, i);
            PointF shifted = new PointF(point.x + (normal.x * delta), point.y + (normal.y * delta));
            output.add(shifted);
        }
        return output;
    }

    private Path toPath(List<PointF> points) {
        Path path = new Path();
        if (points.size() > 0) {
            path.moveTo(points.get(0).x, points.get(0).y);
            for (PointF point : points.subList(1, points.size())) {
                path.lineTo(point.x, point.y);
            }
        }
        return path;
    }

    private PointF normalAt(List<PointF> points, int index) {
        PointF point;
        PointF point2;
        if (index == 0) {
            point = new PointF(0.0f, 0.0f);
        } else {
            PointF d1 = points.get(index);
            PointF point3 = d1;
            PointF previousPoint = points.get(index - 1);
            point = new PointF(point3.x - previousPoint.x, point3.y - previousPoint.y);
        }
        if (index == points.size() - 1) {
            point2 = new PointF(0.0f, 0.0f);
        } else {
            PointF d2 = points.get(index);
            PointF point4 = d2;
            PointF nextPoint = points.get(index + 1);
            point2 = new PointF(nextPoint.x - point4.x, nextPoint.y - point4.y);
        }
        return rotate90Ccw(normalize(new PointF(point.x + point2.x, point.y + point2.y)));
    }

    private PointF rotate90Ccw(PointF input) {
        return new PointF(-input.y, input.x);
    }

    private float magnitude(PointF point) {
        return (float) Math.sqrt((point.x * point.x) + (point.y * point.y));
    }

    private PointF normalize(PointF point) {
        float magnitude = magnitude(point);
        if (magnitude == 0.0f) {
            return point;
        }
        float normal = 1.0f / magnitude;
        return new PointF(point.x * normal, point.y * normal);
    }
}
