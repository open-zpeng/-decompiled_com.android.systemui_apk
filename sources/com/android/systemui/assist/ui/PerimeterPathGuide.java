package com.android.systemui.assist.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.util.Pair;
import androidx.core.math.MathUtils;
import com.android.systemui.assist.ui.CornerPathRenderer;
/* loaded from: classes21.dex */
public class PerimeterPathGuide {
    private static final String TAG = "PerimeterPathGuide";
    private final int mBottomCornerRadiusPx;
    private final CornerPathRenderer mCornerPathRenderer;
    private final int mDeviceHeightPx;
    private final int mDeviceWidthPx;
    private final int mEdgeInset;
    private final int mTopCornerRadiusPx;
    private final Path mScratchPath = new Path();
    private final PathMeasure mScratchPathMeasure = new PathMeasure(this.mScratchPath, false);
    private int mRotation = 0;
    private RegionAttributes[] mRegions = new RegionAttributes[8];

    /* loaded from: classes21.dex */
    public enum Region {
        BOTTOM,
        BOTTOM_RIGHT,
        RIGHT,
        TOP_RIGHT,
        TOP,
        TOP_LEFT,
        LEFT,
        BOTTOM_LEFT
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class RegionAttributes {
        public float absoluteLength;
        public float endCoordinate;
        public float normalizedLength;
        public Path path;

        private RegionAttributes() {
        }
    }

    public PerimeterPathGuide(Context context, CornerPathRenderer cornerPathRenderer, int edgeInset, int screenWidth, int screenHeight) {
        this.mCornerPathRenderer = cornerPathRenderer;
        this.mDeviceWidthPx = screenWidth;
        this.mDeviceHeightPx = screenHeight;
        this.mTopCornerRadiusPx = DisplayUtils.getCornerRadiusTop(context);
        this.mBottomCornerRadiusPx = DisplayUtils.getCornerRadiusBottom(context);
        this.mEdgeInset = edgeInset;
        int i = 0;
        while (true) {
            RegionAttributes[] regionAttributesArr = this.mRegions;
            if (i < regionAttributesArr.length) {
                regionAttributesArr[i] = new RegionAttributes();
                i++;
            } else {
                computeRegions();
                return;
            }
        }
    }

    public void setRotation(int rotation) {
        if (rotation != this.mRotation) {
            if (rotation == 0 || rotation == 1 || rotation == 2 || rotation == 3) {
                this.mRotation = rotation;
                computeRegions();
                return;
            }
            Log.e(TAG, "Invalid rotation provided: " + rotation);
        }
    }

    public void strokeSegment(Path path, float startCoord, float endCoord) {
        path.reset();
        float startCoord2 = ((startCoord % 1.0f) + 1.0f) % 1.0f;
        float startCoord3 = endCoord % 1.0f;
        float endCoord2 = (startCoord3 + 1.0f) % 1.0f;
        boolean outOfOrder = startCoord2 > endCoord2;
        if (outOfOrder) {
            strokeSegmentInternal(path, startCoord2, 1.0f);
            startCoord2 = 0.0f;
        }
        strokeSegmentInternal(path, startCoord2, endCoord2);
    }

    public float getPerimeterPx() {
        RegionAttributes[] regionAttributesArr;
        float total = 0.0f;
        for (RegionAttributes region : this.mRegions) {
            total += region.absoluteLength;
        }
        return total;
    }

    public float getBottomCornerRadiusPx() {
        return this.mBottomCornerRadiusPx;
    }

    public float getCoord(Region region, float progress) {
        RegionAttributes regionAttributes = this.mRegions[region.ordinal()];
        return regionAttributes.endCoordinate - ((1.0f - MathUtils.clamp(progress, 0.0f, 1.0f)) * regionAttributes.normalizedLength);
    }

    public float getRegionCenter(Region region) {
        return getCoord(region, 0.5f);
    }

    public float getRegionWidth(Region region) {
        return this.mRegions[region.ordinal()].normalizedLength;
    }

    public static float makeClockwise(float point) {
        return point - 1.0f;
    }

    private int getPhysicalCornerRadius(CornerPathRenderer.Corner corner) {
        if (corner == CornerPathRenderer.Corner.BOTTOM_LEFT || corner == CornerPathRenderer.Corner.BOTTOM_RIGHT) {
            return this.mBottomCornerRadiusPx;
        }
        return this.mTopCornerRadiusPx;
    }

    private void computeRegions() {
        int screenWidth = this.mDeviceWidthPx;
        int screenHeight = this.mDeviceHeightPx;
        int rotateMatrix = 0;
        int i = this.mRotation;
        if (i == 1) {
            rotateMatrix = -90;
        } else if (i == 2) {
            rotateMatrix = -180;
        } else if (i == 3) {
            rotateMatrix = -270;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateMatrix, this.mDeviceWidthPx / 2, this.mDeviceHeightPx / 2);
        int i2 = this.mRotation;
        if (i2 == 1 || i2 == 3) {
            screenHeight = this.mDeviceWidthPx;
            screenWidth = this.mDeviceHeightPx;
            int i3 = this.mDeviceHeightPx;
            int i4 = this.mDeviceWidthPx;
            matrix.postTranslate((i3 - i4) / 2, (i4 - i3) / 2);
        }
        CornerPathRenderer.Corner screenBottomLeft = getRotatedCorner(CornerPathRenderer.Corner.BOTTOM_LEFT);
        CornerPathRenderer.Corner screenBottomRight = getRotatedCorner(CornerPathRenderer.Corner.BOTTOM_RIGHT);
        CornerPathRenderer.Corner screenTopLeft = getRotatedCorner(CornerPathRenderer.Corner.TOP_LEFT);
        CornerPathRenderer.Corner screenTopRight = getRotatedCorner(CornerPathRenderer.Corner.TOP_RIGHT);
        this.mRegions[Region.BOTTOM_LEFT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(screenBottomLeft, this.mEdgeInset);
        this.mRegions[Region.BOTTOM_RIGHT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(screenBottomRight, this.mEdgeInset);
        this.mRegions[Region.TOP_RIGHT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(screenTopRight, this.mEdgeInset);
        this.mRegions[Region.TOP_LEFT.ordinal()].path = this.mCornerPathRenderer.getInsetPath(screenTopLeft, this.mEdgeInset);
        this.mRegions[Region.BOTTOM_LEFT.ordinal()].path.transform(matrix);
        this.mRegions[Region.BOTTOM_RIGHT.ordinal()].path.transform(matrix);
        this.mRegions[Region.TOP_RIGHT.ordinal()].path.transform(matrix);
        this.mRegions[Region.TOP_LEFT.ordinal()].path.transform(matrix);
        Path bottomPath = new Path();
        bottomPath.moveTo(getPhysicalCornerRadius(screenBottomLeft), screenHeight - this.mEdgeInset);
        bottomPath.lineTo(screenWidth - getPhysicalCornerRadius(screenBottomRight), screenHeight - this.mEdgeInset);
        this.mRegions[Region.BOTTOM.ordinal()].path = bottomPath;
        Path topPath = new Path();
        topPath.moveTo(screenWidth - getPhysicalCornerRadius(screenTopRight), this.mEdgeInset);
        topPath.lineTo(getPhysicalCornerRadius(screenTopLeft), this.mEdgeInset);
        this.mRegions[Region.TOP.ordinal()].path = topPath;
        Path rightPath = new Path();
        rightPath.moveTo(screenWidth - this.mEdgeInset, screenHeight - getPhysicalCornerRadius(screenBottomRight));
        rightPath.lineTo(screenWidth - this.mEdgeInset, getPhysicalCornerRadius(screenTopRight));
        this.mRegions[Region.RIGHT.ordinal()].path = rightPath;
        Path leftPath = new Path();
        leftPath.moveTo(this.mEdgeInset, getPhysicalCornerRadius(screenTopLeft));
        leftPath.lineTo(this.mEdgeInset, screenHeight - getPhysicalCornerRadius(screenBottomLeft));
        this.mRegions[Region.LEFT.ordinal()].path = leftPath;
        PathMeasure pathMeasure = new PathMeasure();
        float perimeterLength = 0.0f;
        int i5 = 0;
        while (true) {
            RegionAttributes[] regionAttributesArr = this.mRegions;
            int screenWidth2 = screenWidth;
            int screenWidth3 = regionAttributesArr.length;
            if (i5 >= screenWidth3) {
                break;
            }
            pathMeasure.setPath(regionAttributesArr[i5].path, false);
            this.mRegions[i5].absoluteLength = pathMeasure.getLength();
            perimeterLength += this.mRegions[i5].absoluteLength;
            i5++;
            screenWidth = screenWidth2;
        }
        float accum = 0.0f;
        int i6 = 0;
        while (true) {
            RegionAttributes[] regionAttributesArr2 = this.mRegions;
            int screenHeight2 = screenHeight;
            int screenHeight3 = regionAttributesArr2.length;
            if (i6 < screenHeight3) {
                regionAttributesArr2[i6].normalizedLength = regionAttributesArr2[i6].absoluteLength / perimeterLength;
                accum += this.mRegions[i6].absoluteLength;
                this.mRegions[i6].endCoordinate = accum / perimeterLength;
                i6++;
                screenHeight = screenHeight2;
            } else {
                regionAttributesArr2[regionAttributesArr2.length - 1].endCoordinate = 1.0f;
                return;
            }
        }
    }

    private CornerPathRenderer.Corner getRotatedCorner(CornerPathRenderer.Corner screenCorner) {
        int corner = screenCorner.ordinal();
        int i = this.mRotation;
        if (i == 1) {
            corner += 3;
        } else if (i == 2) {
            corner += 2;
        } else if (i == 3) {
            corner++;
        }
        return CornerPathRenderer.Corner.values()[corner % 4];
    }

    private void strokeSegmentInternal(Path path, float startCoord, float endCoord) {
        Region[] values;
        Pair<Region, Float> startPoint = placePoint(startCoord);
        Pair<Region, Float> endPoint = placePoint(endCoord);
        if (((Region) startPoint.first).equals(endPoint.first)) {
            strokeRegion(path, (Region) startPoint.first, ((Float) startPoint.second).floatValue(), ((Float) endPoint.second).floatValue());
            return;
        }
        strokeRegion(path, (Region) startPoint.first, ((Float) startPoint.second).floatValue(), 1.0f);
        boolean hitStart = false;
        for (Region r : Region.values()) {
            if (r.equals(startPoint.first)) {
                hitStart = true;
            } else if (!hitStart) {
                continue;
            } else if (!r.equals(endPoint.first)) {
                strokeRegion(path, r, 0.0f, 1.0f);
            } else {
                strokeRegion(path, r, 0.0f, ((Float) endPoint.second).floatValue());
                return;
            }
        }
    }

    private void strokeRegion(Path path, Region r, float relativeStart, float relativeEnd) {
        if (relativeStart == relativeEnd) {
            return;
        }
        this.mScratchPathMeasure.setPath(this.mRegions[r.ordinal()].path, false);
        PathMeasure pathMeasure = this.mScratchPathMeasure;
        pathMeasure.getSegment(pathMeasure.getLength() * relativeStart, this.mScratchPathMeasure.getLength() * relativeEnd, path, true);
    }

    private Pair<Region, Float> placePoint(float coord) {
        if (0.0f > coord || coord > 1.0f) {
            coord = ((coord % 1.0f) + 1.0f) % 1.0f;
        }
        Region r = getRegionForPoint(coord);
        if (r.equals(Region.BOTTOM)) {
            return Pair.create(r, Float.valueOf(coord / this.mRegions[r.ordinal()].normalizedLength));
        }
        float coordOffsetInRegion = coord - this.mRegions[r.ordinal() - 1].endCoordinate;
        float coordRelativeToRegion = coordOffsetInRegion / this.mRegions[r.ordinal()].normalizedLength;
        return Pair.create(r, Float.valueOf(coordRelativeToRegion));
    }

    private Region getRegionForPoint(float coord) {
        Region[] values;
        if (coord < 0.0f || coord > 1.0f) {
            coord = ((coord % 1.0f) + 1.0f) % 1.0f;
        }
        for (Region region : Region.values()) {
            if (coord <= this.mRegions[region.ordinal()].endCoordinate) {
                return region;
            }
        }
        Log.e(TAG, "Fell out of getRegionForPoint");
        return Region.BOTTOM;
    }
}
