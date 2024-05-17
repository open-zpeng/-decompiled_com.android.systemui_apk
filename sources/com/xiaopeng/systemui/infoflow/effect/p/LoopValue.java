package com.xiaopeng.systemui.infoflow.effect.p;
/* loaded from: classes24.dex */
class LoopValue {
    float delay;
    float direction;
    float max;
    float min;
    float progress;
    float speed;

    public LoopValue(float min, float max, float speed) {
        this.progress = -1.0f;
        this.direction = 1.0f;
        this.min = min;
        this.max = max;
        this.speed = speed;
    }

    public LoopValue(float min, float max, float speed, float direction) {
        this.progress = -1.0f;
        this.direction = 1.0f;
        this.min = min;
        this.max = max;
        this.speed = speed;
        this.direction = direction;
    }

    public LoopValue(float progress, float min, float max, float speed, float direction) {
        this.progress = -1.0f;
        this.direction = 1.0f;
        this.progress = progress;
        this.min = min;
        this.max = max;
        this.speed = speed;
        this.direction = direction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float move() {
        moveProgress();
        float p = getProgressWithDelay(this.delay);
        float f = this.min;
        return f + ((this.max - f) * p);
    }

    private float getProgressWithDelay(float delay) {
        float p;
        if (this.direction < 0.0f) {
            p = (1.0f - this.progress) + 1.0f;
        } else {
            p = this.progress;
        }
        float p2 = (p + delay) % 2.0f;
        if (p2 <= 1.0f) {
            return p2;
        }
        return 1.0f - (p2 - 1.0f);
    }

    private void moveProgress() {
        float f = this.progress;
        if (f <= 0.0f) {
            this.direction = 1.0f;
        } else if (f >= 1.0f) {
            this.direction = -1.0f;
        }
        float f2 = this.progress;
        if (f2 == -1.0f) {
            this.progress = 0.0f;
        } else {
            this.progress = f2 + (this.speed * this.direction);
        }
    }
}
