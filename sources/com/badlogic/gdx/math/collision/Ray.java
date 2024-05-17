package com.badlogic.gdx.math.collision;

import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;
/* loaded from: classes21.dex */
public class Ray implements Serializable {
    private static final long serialVersionUID = -620692054835390878L;
    static Vector3 tmp = new Vector3();
    public final Vector3 origin = new Vector3();
    public final Vector3 direction = new Vector3();

    public Ray() {
    }

    public Ray(Vector3 origin, Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction).nor();
    }

    public Ray cpy() {
        return new Ray(this.origin, this.direction);
    }

    public Vector3 getEndPoint(Vector3 out, float distance) {
        return out.set(this.direction).scl(distance).add(this.origin);
    }

    public Ray mul(Matrix4 matrix) {
        tmp.set(this.origin).add(this.direction);
        tmp.mul(matrix);
        this.origin.mul(matrix);
        this.direction.set(tmp.sub(this.origin));
        return this;
    }

    public String toString() {
        return "ray [" + this.origin + NavigationBarInflaterView.KEY_IMAGE_DELIM + this.direction + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public Ray set(Vector3 origin, Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction);
        return this;
    }

    public Ray set(float x, float y, float z, float dx, float dy, float dz) {
        this.origin.set(x, y, z);
        this.direction.set(dx, dy, dz);
        return this;
    }

    public Ray set(Ray ray) {
        this.origin.set(ray.origin);
        this.direction.set(ray.direction);
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Ray r = (Ray) o;
        if (this.direction.equals(r.direction) && this.origin.equals(r.origin)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (1 * 73) + this.direction.hashCode();
        return (result * 73) + this.origin.hashCode();
    }
}
