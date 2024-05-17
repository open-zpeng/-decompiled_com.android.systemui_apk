package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
/* loaded from: classes21.dex */
public class ProgressBar extends Widget implements Disableable {
    private float animateDuration;
    private float animateFromValue;
    private Interpolation animateInterpolation;
    private float animateTime;
    boolean disabled;
    private float max;
    private float min;
    float position;
    private boolean round;
    private float stepSize;
    private ProgressBarStyle style;
    private float value;
    final boolean vertical;
    private Interpolation visualInterpolation;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ProgressBar(float r8, float r9, float r10, boolean r11, com.badlogic.gdx.scenes.scene2d.ui.Skin r12) {
        /*
            r7 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "default-"
            r0.append(r1)
            if (r11 == 0) goto L10
            java.lang.String r1 = "vertical"
            goto L12
        L10:
            java.lang.String r1 = "horizontal"
        L12:
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.Class<com.badlogic.gdx.scenes.scene2d.ui.ProgressBar$ProgressBarStyle> r1 = com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle.class
            java.lang.Object r0 = r12.get(r0, r1)
            r6 = r0
            com.badlogic.gdx.scenes.scene2d.ui.ProgressBar$ProgressBarStyle r6 = (com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle) r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            r1.<init>(r2, r3, r4, r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.<init>(float, float, float, boolean, com.badlogic.gdx.scenes.scene2d.ui.Skin):void");
    }

    public ProgressBar(float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
        this(min, max, stepSize, vertical, (ProgressBarStyle) skin.get(styleName, ProgressBarStyle.class));
    }

    public ProgressBar(float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
        this.animateInterpolation = Interpolation.linear;
        this.visualInterpolation = Interpolation.linear;
        this.round = true;
        if (min > max) {
            throw new IllegalArgumentException("max must be > min. min,max: " + min + ", " + max);
        } else if (stepSize <= 0.0f) {
            throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
        } else {
            setStyle(style);
            this.min = min;
            this.max = max;
            this.stepSize = stepSize;
            this.vertical = vertical;
            this.value = min;
            setSize(getPrefWidth(), getPrefHeight());
        }
    }

    public void setStyle(ProgressBarStyle style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        invalidateHierarchy();
    }

    public ProgressBarStyle getStyle() {
        return this.style;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Actor
    public void act(float delta) {
        super.act(delta);
        float f = this.animateTime;
        if (f > 0.0f) {
            this.animateTime = f - delta;
            Stage stage = getStage();
            if (stage == null || !stage.getActionsRequestRendering()) {
                return;
            }
            Gdx.graphics.requestRendering();
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Widget, com.badlogic.gdx.scenes.scene2d.Actor
    public void draw(Batch batch, float parentAlpha) {
        float positionWidth;
        float bgLeftWidth;
        float knobWidthHalf;
        float positionHeight;
        float bgTopHeight;
        float knobHeightHalf;
        ProgressBarStyle style = this.style;
        boolean disabled = this.disabled;
        Drawable knob = getKnobDrawable();
        Drawable bg = (!disabled || style.disabledBackground == null) ? style.background : style.disabledBackground;
        Drawable knobBefore = (!disabled || style.disabledKnobBefore == null) ? style.knobBefore : style.disabledKnobBefore;
        Drawable knobAfter = (!disabled || style.disabledKnobAfter == null) ? style.knobAfter : style.disabledKnobAfter;
        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float knobHeight = knob == null ? 0.0f : knob.getMinHeight();
        float knobWidth = knob == null ? 0.0f : knob.getMinWidth();
        float percent = getVisualPercent();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (this.vertical) {
            float bgBottomHeight = 0.0f;
            if (bg == null) {
                positionHeight = height;
                bgTopHeight = 0.0f;
            } else {
                if (this.round) {
                    bg.draw(batch, Math.round(x + ((width - bg.getMinWidth()) * 0.5f)), y, Math.round(bg.getMinWidth()), height);
                } else {
                    bg.draw(batch, (x + width) - (bg.getMinWidth() * 0.5f), y, bg.getMinWidth(), height);
                }
                float bgTopHeight2 = bg.getTopHeight();
                bgBottomHeight = bg.getBottomHeight();
                float positionHeight2 = height - (bgTopHeight2 + bgBottomHeight);
                positionHeight = positionHeight2;
                bgTopHeight = bgTopHeight2;
            }
            if (knob == null) {
                float knobHeightHalf2 = knobBefore == null ? 0.0f : knobBefore.getMinHeight() * 0.5f;
                this.position = (positionHeight - knobHeightHalf2) * percent;
                this.position = Math.min(positionHeight - knobHeightHalf2, this.position);
                knobHeightHalf = knobHeightHalf2;
            } else {
                float knobHeightHalf3 = knobHeight * 0.5f;
                this.position = (positionHeight - knobHeight) * percent;
                this.position = Math.min(positionHeight - knobHeight, this.position) + bgBottomHeight;
                knobHeightHalf = knobHeightHalf3;
            }
            this.position = Math.max(Math.min(0.0f, bgBottomHeight), this.position);
            if (knobBefore != null) {
                if (!this.round) {
                    knobBefore.draw(batch, x + ((width - knobBefore.getMinWidth()) * 0.5f), y + bgTopHeight, knobBefore.getMinWidth(), this.position + knobHeightHalf);
                } else {
                    knobBefore.draw(batch, Math.round(x + ((width - knobBefore.getMinWidth()) * 0.5f)), Math.round(y + bgTopHeight), Math.round(knobBefore.getMinWidth()), Math.round(this.position + knobHeightHalf));
                }
            }
            if (knobAfter != null) {
                if (this.round) {
                    knobAfter.draw(batch, Math.round(x + ((width - knobAfter.getMinWidth()) * 0.5f)), Math.round(y + this.position + knobHeightHalf), Math.round(knobAfter.getMinWidth()), Math.round((height - this.position) - knobHeightHalf));
                } else {
                    knobAfter.draw(batch, x + ((width - knobAfter.getMinWidth()) * 0.5f), y + this.position + knobHeightHalf, knobAfter.getMinWidth(), (height - this.position) - knobHeightHalf);
                }
            }
            if (knob != null) {
                if (!this.round) {
                    knob.draw(batch, x + ((width - knobWidth) * 0.5f), y + this.position, knobWidth, knobHeight);
                    return;
                } else {
                    knob.draw(batch, Math.round(x + ((width - knobWidth) * 0.5f)), Math.round(y + this.position), Math.round(knobWidth), Math.round(knobHeight));
                    return;
                }
            }
            return;
        }
        if (bg == null) {
            positionWidth = width;
            bgLeftWidth = 0.0f;
        } else {
            if (this.round) {
                bg.draw(batch, x, Math.round(y + ((height - bg.getMinHeight()) * 0.5f)), width, Math.round(bg.getMinHeight()));
            } else {
                bg.draw(batch, x, y + ((height - bg.getMinHeight()) * 0.5f), width, bg.getMinHeight());
            }
            float bgLeftWidth2 = bg.getLeftWidth();
            float bgRightWidth = bg.getRightWidth();
            float positionWidth2 = width - (bgLeftWidth2 + bgRightWidth);
            positionWidth = positionWidth2;
            bgLeftWidth = bgLeftWidth2;
        }
        if (knob == null) {
            float knobWidthHalf2 = knobBefore == null ? 0.0f : knobBefore.getMinWidth() * 0.5f;
            this.position = (positionWidth - knobWidthHalf2) * percent;
            this.position = Math.min(positionWidth - knobWidthHalf2, this.position);
            knobWidthHalf = knobWidthHalf2;
        } else {
            float knobWidthHalf3 = knobWidth * 0.5f;
            this.position = (positionWidth - knobWidth) * percent;
            this.position = Math.min(positionWidth - knobWidth, this.position) + bgLeftWidth;
            knobWidthHalf = knobWidthHalf3;
        }
        this.position = Math.max(Math.min(0.0f, bgLeftWidth), this.position);
        if (knobBefore != null) {
            if (this.round) {
                knobBefore.draw(batch, Math.round(x + bgLeftWidth), Math.round(y + ((height - knobBefore.getMinHeight()) * 0.5f)), Math.round(this.position + knobWidthHalf), Math.round(knobBefore.getMinHeight()));
            } else {
                knobBefore.draw(batch, x + bgLeftWidth, y + ((height - knobBefore.getMinHeight()) * 0.5f), this.position + knobWidthHalf, knobBefore.getMinHeight());
            }
        }
        if (knobAfter != null) {
            if (this.round) {
                knobAfter.draw(batch, Math.round(x + this.position + knobWidthHalf), Math.round(y + ((height - knobAfter.getMinHeight()) * 0.5f)), Math.round((width - this.position) - knobWidthHalf), Math.round(knobAfter.getMinHeight()));
            } else {
                knobAfter.draw(batch, x + this.position + knobWidthHalf, y + ((height - knobAfter.getMinHeight()) * 0.5f), (width - this.position) - knobWidthHalf, knobAfter.getMinHeight());
            }
        }
        if (knob != null) {
            if (!this.round) {
                knob.draw(batch, x + this.position, y + ((height - knobHeight) * 0.5f), knobWidth, knobHeight);
                return;
            }
            float round = Math.round(x + this.position);
            float round2 = Math.round(y + ((height - knobHeight) * 0.5f));
            float bgLeftWidth3 = Math.round(knobWidth);
            knob.draw(batch, round, round2, bgLeftWidth3, Math.round(knobHeight));
        }
    }

    public float getValue() {
        return this.value;
    }

    public float getVisualValue() {
        float f = this.animateTime;
        return f > 0.0f ? this.animateInterpolation.apply(this.animateFromValue, this.value, 1.0f - (f / this.animateDuration)) : this.value;
    }

    public float getPercent() {
        float f = this.min;
        float f2 = this.max;
        if (f == f2) {
            return 0.0f;
        }
        return (this.value - f) / (f2 - f);
    }

    public float getVisualPercent() {
        if (this.min == this.max) {
            return 0.0f;
        }
        Interpolation interpolation = this.visualInterpolation;
        float visualValue = getVisualValue();
        float f = this.min;
        return interpolation.apply((visualValue - f) / (this.max - f));
    }

    protected Drawable getKnobDrawable() {
        return (!this.disabled || this.style.disabledKnob == null) ? this.style.knob : this.style.disabledKnob;
    }

    protected float getKnobPosition() {
        return this.position;
    }

    public boolean setValue(float value) {
        float value2 = clamp(Math.round(value / this.stepSize) * this.stepSize);
        float oldValue = this.value;
        if (value2 == oldValue) {
            return false;
        }
        float oldVisualValue = getVisualValue();
        this.value = value2;
        ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
        boolean cancelled = fire(changeEvent);
        if (cancelled) {
            this.value = oldValue;
        } else {
            float f = this.animateDuration;
            if (f > 0.0f) {
                this.animateFromValue = oldVisualValue;
                this.animateTime = f;
            }
        }
        Pools.free(changeEvent);
        return !cancelled;
    }

    protected float clamp(float value) {
        return MathUtils.clamp(value, this.min, this.max);
    }

    public void setRange(float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max: " + min + " <= " + max);
        }
        this.min = min;
        this.max = max;
        float f = this.value;
        if (f < min) {
            setValue(min);
        } else if (f > max) {
            setValue(max);
        }
    }

    public void setStepSize(float stepSize) {
        if (stepSize <= 0.0f) {
            throw new IllegalArgumentException("steps must be > 0: " + stepSize);
        }
        this.stepSize = stepSize;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Widget, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefWidth() {
        if (this.vertical) {
            Drawable knob = getKnobDrawable();
            Drawable bg = (!this.disabled || this.style.disabledBackground == null) ? this.style.background : this.style.disabledBackground;
            return Math.max(knob == null ? 0.0f : knob.getMinWidth(), bg != null ? bg.getMinWidth() : 0.0f);
        }
        return 140.0f;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Widget, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefHeight() {
        if (this.vertical) {
            return 140.0f;
        }
        Drawable knob = getKnobDrawable();
        Drawable bg = (!this.disabled || this.style.disabledBackground == null) ? this.style.background : this.style.disabledBackground;
        return Math.max(knob == null ? 0.0f : knob.getMinHeight(), bg != null ? bg.getMinHeight() : 0.0f);
    }

    public float getMinValue() {
        return this.min;
    }

    public float getMaxValue() {
        return this.max;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setAnimateDuration(float duration) {
        this.animateDuration = duration;
    }

    public void setAnimateInterpolation(Interpolation animateInterpolation) {
        if (animateInterpolation == null) {
            throw new IllegalArgumentException("animateInterpolation cannot be null.");
        }
        this.animateInterpolation = animateInterpolation;
    }

    public void setVisualInterpolation(Interpolation interpolation) {
        this.visualInterpolation = interpolation;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.utils.Disableable
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.utils.Disableable
    public boolean isDisabled() {
        return this.disabled;
    }

    public boolean isVertical() {
        return this.vertical;
    }

    /* loaded from: classes21.dex */
    public static class ProgressBarStyle {
        public Drawable background;
        public Drawable disabledBackground;
        public Drawable disabledKnob;
        public Drawable disabledKnobAfter;
        public Drawable disabledKnobBefore;
        public Drawable knob;
        public Drawable knobAfter;
        public Drawable knobBefore;

        public ProgressBarStyle() {
        }

        public ProgressBarStyle(Drawable background, Drawable knob) {
            this.background = background;
            this.knob = knob;
        }

        public ProgressBarStyle(ProgressBarStyle style) {
            this.background = style.background;
            this.disabledBackground = style.disabledBackground;
            this.knob = style.knob;
            this.disabledKnob = style.disabledKnob;
            this.knobBefore = style.knobBefore;
            this.knobAfter = style.knobAfter;
            this.disabledKnobBefore = style.disabledKnobBefore;
            this.disabledKnobAfter = style.disabledKnobAfter;
        }
    }
}
