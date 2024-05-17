package com.badlogic.gdx.math;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.io.Serializable;
/* loaded from: classes21.dex */
public class GridPoint2 implements Serializable {
    private static final long serialVersionUID = -4019969926331717380L;
    public int x;
    public int y;

    public GridPoint2() {
    }

    public GridPoint2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPoint2(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
    }

    public GridPoint2 set(GridPoint2 point) {
        this.x = point.x;
        this.y = point.y;
        return this;
    }

    public GridPoint2 set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float dst2(GridPoint2 other) {
        int xd = other.x - this.x;
        int yd = other.y - this.y;
        return (xd * xd) + (yd * yd);
    }

    public float dst2(int x, int y) {
        int xd = x - this.x;
        int yd = y - this.y;
        return (xd * xd) + (yd * yd);
    }

    public float dst(GridPoint2 other) {
        int xd = other.x - this.x;
        int yd = other.y - this.y;
        return (float) Math.sqrt((xd * xd) + (yd * yd));
    }

    public float dst(int x, int y) {
        int xd = x - this.x;
        int yd = y - this.y;
        return (float) Math.sqrt((xd * xd) + (yd * yd));
    }

    public GridPoint2 add(GridPoint2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public GridPoint2 add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public GridPoint2 sub(GridPoint2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public GridPoint2 sub(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public GridPoint2 cpy() {
        return new GridPoint2(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        GridPoint2 g = (GridPoint2) o;
        if (this.x == g.x && this.y == g.y) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (1 * 53) + this.x;
        return (result * 53) + this.y;
    }

    public String toString() {
        return NavigationBarInflaterView.KEY_CODE_START + this.x + ", " + this.y + NavigationBarInflaterView.KEY_CODE_END;
    }
}
