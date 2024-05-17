package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.SnapshotArray;
import kotlin.jvm.internal.CharCompanionObject;
/* loaded from: classes21.dex */
public class HorizontalGroup extends WidgetGroup {
    private boolean expand;
    private float fill;
    private float lastPrefHeight;
    private float padBottom;
    private float padLeft;
    private float padRight;
    private float padTop;
    private float prefHeight;
    private float prefWidth;
    private boolean reverse;
    private int rowAlign;
    private FloatArray rowSizes;
    private float space;
    private boolean wrap;
    private float wrapSpace;
    private boolean sizeInvalid = true;
    private int align = 8;
    private boolean round = true;

    public HorizontalGroup() {
        setTouchable(Touchable.childrenOnly);
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private void computeSize() {
        float width;
        float height;
        int n;
        float space;
        this.sizeInvalid = false;
        SnapshotArray<Actor> children = getChildren();
        int n2 = children.size;
        this.prefHeight = 0.0f;
        if (!this.wrap) {
            this.prefWidth = this.padLeft + this.padRight + (this.space * (n2 - 1));
            for (int i = 0; i < n2; i++) {
                Actor child = children.get(i);
                if (!(child instanceof Layout)) {
                    this.prefWidth += child.getWidth();
                    this.prefHeight = Math.max(this.prefHeight, child.getHeight());
                } else {
                    Layout layout = (Layout) child;
                    this.prefWidth += layout.getPrefWidth();
                    this.prefHeight = Math.max(this.prefHeight, layout.getPrefHeight());
                }
            }
        } else {
            this.prefWidth = 0.0f;
            FloatArray floatArray = this.rowSizes;
            if (floatArray == null) {
                this.rowSizes = new FloatArray();
            } else {
                floatArray.clear();
            }
            FloatArray rowSizes = this.rowSizes;
            float space2 = this.space;
            float wrapSpace = this.wrapSpace;
            float pad = this.padLeft + this.padRight;
            float groupWidth = getWidth() - pad;
            float x = 0.0f;
            float y = 0.0f;
            float rowHeight = 0.0f;
            int i2 = 0;
            int incr = 1;
            if (this.reverse) {
                i2 = n2 - 1;
                n2 = -1;
                incr = -1;
            }
            while (i2 != n2) {
                Actor child2 = children.get(i2);
                if (child2 instanceof Layout) {
                    Layout layout2 = (Layout) child2;
                    width = layout2.getPrefWidth();
                    if (width > groupWidth) {
                        width = Math.max(groupWidth, layout2.getMinWidth());
                    }
                    height = layout2.getPrefHeight();
                } else {
                    width = child2.getWidth();
                    height = child2.getHeight();
                }
                float incrX = width + (x > 0.0f ? space2 : 0.0f);
                if (x + incrX <= groupWidth || x <= 0.0f) {
                    n = n2;
                    space = space2;
                } else {
                    rowSizes.add(x);
                    rowSizes.add(rowHeight);
                    n = n2;
                    space = space2;
                    float space3 = x + pad;
                    this.prefWidth = Math.max(this.prefWidth, space3);
                    if (y > 0.0f) {
                        y += wrapSpace;
                    }
                    y += rowHeight;
                    rowHeight = 0.0f;
                    x = 0.0f;
                    incrX = width;
                }
                x += incrX;
                rowHeight = Math.max(rowHeight, height);
                i2 += incr;
                n2 = n;
                space2 = space;
            }
            rowSizes.add(x);
            rowSizes.add(rowHeight);
            this.prefWidth = Math.max(this.prefWidth, x + pad);
            if (y > 0.0f) {
                y += wrapSpace;
            }
            this.prefHeight = Math.max(this.prefHeight, y + rowHeight);
        }
        this.prefHeight += this.padTop + this.padBottom;
        if (this.round) {
            this.prefWidth = Math.round(this.prefWidth);
            this.prefHeight = Math.round(this.prefHeight);
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void layout() {
        float startY;
        float width;
        float height;
        float padBottom;
        char c;
        boolean round;
        int align;
        float fill;
        float rowHeight;
        if (this.sizeInvalid) {
            computeSize();
        }
        if (this.wrap) {
            layoutWrapped();
            return;
        }
        boolean round2 = this.round;
        int align2 = this.align;
        float space = this.space;
        float padBottom2 = this.padBottom;
        float fill2 = this.fill;
        float rowHeight2 = ((this.expand ? getHeight() : this.prefHeight) - this.padTop) - padBottom2;
        float x = this.padLeft;
        if ((align2 & 16) != 0) {
            x += getWidth() - this.prefWidth;
        } else if ((align2 & 8) == 0) {
            x += (getWidth() - this.prefWidth) / 2.0f;
        }
        if ((align2 & 4) != 0) {
            startY = padBottom2;
        } else if ((align2 & 2) != 0) {
            startY = (getHeight() - this.padTop) - rowHeight2;
        } else {
            float startY2 = getHeight();
            startY = ((((startY2 - padBottom2) - this.padTop) - rowHeight2) / 2.0f) + padBottom2;
        }
        int align3 = this.rowAlign;
        SnapshotArray<Actor> children = getChildren();
        int i = 0;
        int n = children.size;
        int incr = 1;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        while (i != n) {
            Actor child = children.get(i);
            Layout layout = null;
            if (child instanceof Layout) {
                layout = (Layout) child;
                width = layout.getPrefWidth();
                height = layout.getPrefHeight();
            } else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (fill2 > 0.0f) {
                height = rowHeight2 * fill2;
            }
            float height2 = height;
            if (layout == null) {
                padBottom = padBottom2;
            } else {
                padBottom = padBottom2;
                height2 = Math.max(height2, layout.getMinHeight());
                float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height2 > maxHeight) {
                    height2 = maxHeight;
                }
            }
            float y = startY;
            if ((align3 & 2) != 0) {
                y += rowHeight2 - height2;
                c = CharCompanionObject.MIN_VALUE;
            } else if ((align3 & 4) != 0) {
                c = CharCompanionObject.MIN_VALUE;
            } else {
                c = CharCompanionObject.MIN_VALUE;
                y += (rowHeight2 - height2) / 2.0f;
            }
            if (round2) {
                round = round2;
                align = align3;
                fill = fill2;
                rowHeight = rowHeight2;
                child.setBounds(Math.round(x), Math.round(y), Math.round(width), Math.round(height2));
            } else {
                round = round2;
                align = align3;
                fill = fill2;
                rowHeight = rowHeight2;
                child.setBounds(x, y, width, height2);
            }
            x += width + space;
            if (layout != null) {
                layout.validate();
            }
            i += incr;
            padBottom2 = padBottom;
            round2 = round;
            align3 = align;
            fill2 = fill;
            rowHeight2 = rowHeight;
        }
    }

    private void layoutWrapped() {
        float width;
        float height;
        float maxWidth;
        float fill;
        int align;
        boolean round;
        float wrapSpace;
        int r;
        float prefHeight = getPrefHeight();
        if (prefHeight != this.lastPrefHeight) {
            this.lastPrefHeight = prefHeight;
            invalidateHierarchy();
        }
        int align2 = this.align;
        boolean round2 = this.round;
        float space = this.space;
        float f = this.padBottom;
        float fill2 = this.fill;
        float wrapSpace2 = this.wrapSpace;
        float maxWidth2 = (this.prefWidth - this.padLeft) - this.padRight;
        float rowY = prefHeight - this.padTop;
        float groupWidth = getWidth();
        float xStart = this.padLeft;
        if ((align2 & 2) != 0) {
            rowY += getHeight() - prefHeight;
        } else if ((align2 & 4) == 0) {
            rowY += (getHeight() - prefHeight) / 2.0f;
        }
        if ((align2 & 16) != 0) {
            xStart += groupWidth - this.prefWidth;
        } else if ((align2 & 8) == 0) {
            xStart += (groupWidth - this.prefWidth) / 2.0f;
        }
        float groupWidth2 = groupWidth - this.padRight;
        int align3 = this.rowAlign;
        FloatArray rowSizes = this.rowSizes;
        SnapshotArray<Actor> children = getChildren();
        int i = 0;
        int n = children.size;
        int incr = 1;
        if (this.reverse) {
            i = n - 1;
            n = -1;
            incr = -1;
        }
        float f2 = rowY;
        int r2 = 0;
        int i2 = i;
        float rowHeight = 0.0f;
        float rowHeight2 = 0.0f;
        float rowY2 = f2;
        while (i2 != n) {
            Actor child = children.get(i2);
            Layout layout = null;
            int n2 = n;
            if (child instanceof Layout) {
                layout = (Layout) child;
                width = layout.getPrefWidth();
                if (width > groupWidth2) {
                    float width2 = layout.getMinWidth();
                    width = Math.max(groupWidth2, width2);
                }
                height = layout.getPrefHeight();
            } else {
                width = child.getWidth();
                height = child.getHeight();
            }
            if (rowHeight2 + width > groupWidth2 || r2 == 0) {
                rowHeight2 = xStart;
                if ((align3 & 16) != 0) {
                    rowHeight2 += maxWidth2 - rowSizes.get(r2);
                } else if ((align3 & 8) == 0) {
                    rowHeight2 += (maxWidth2 - rowSizes.get(r2)) / 2.0f;
                }
                maxWidth = maxWidth2;
                float rowHeight3 = rowSizes.get(r2 + 1);
                if (r2 > 0) {
                    rowY2 -= wrapSpace2;
                }
                rowY2 -= rowHeight3;
                r2 += 2;
                rowHeight = rowHeight3;
            } else {
                maxWidth = maxWidth2;
            }
            if (fill2 > 0.0f) {
                height = rowHeight * fill2;
            }
            float height2 = height;
            if (layout == null) {
                fill = fill2;
            } else {
                fill = fill2;
                float fill3 = layout.getMinHeight();
                height2 = Math.max(height2, fill3);
                float maxHeight = layout.getMaxHeight();
                if (maxHeight > 0.0f && height2 > maxHeight) {
                    height2 = maxHeight;
                }
            }
            float y = rowY2;
            if ((align3 & 2) != 0) {
                y += rowHeight - height2;
            } else if ((align3 & 4) == 0) {
                y += (rowHeight - height2) / 2.0f;
            }
            if (round2) {
                align = align3;
                round = round2;
                wrapSpace = wrapSpace2;
                r = r2;
                int r3 = Math.round(height2);
                child.setBounds(Math.round(rowHeight2), Math.round(y), Math.round(width), r3);
            } else {
                align = align3;
                round = round2;
                wrapSpace = wrapSpace2;
                r = r2;
                child.setBounds(rowHeight2, y, width, height2);
            }
            rowHeight2 += width + space;
            if (layout != null) {
                layout.validate();
            }
            i2 += incr;
            n = n2;
            fill2 = fill;
            maxWidth2 = maxWidth;
            align3 = align;
            round2 = round;
            wrapSpace2 = wrapSpace;
            r2 = r;
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefWidth() {
        if (this.wrap) {
            return 0.0f;
        }
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public HorizontalGroup reverse() {
        this.reverse = true;
        return this;
    }

    public HorizontalGroup reverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    public boolean getReverse() {
        return this.reverse;
    }

    public HorizontalGroup space(float space) {
        this.space = space;
        return this;
    }

    public float getSpace() {
        return this.space;
    }

    public HorizontalGroup wrapSpace(float wrapSpace) {
        this.wrapSpace = wrapSpace;
        return this;
    }

    public float getWrapSpace() {
        return this.wrapSpace;
    }

    public HorizontalGroup pad(float pad) {
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        return this;
    }

    public HorizontalGroup pad(float top, float left, float bottom, float right) {
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        return this;
    }

    public HorizontalGroup padTop(float padTop) {
        this.padTop = padTop;
        return this;
    }

    public HorizontalGroup padLeft(float padLeft) {
        this.padLeft = padLeft;
        return this;
    }

    public HorizontalGroup padBottom(float padBottom) {
        this.padBottom = padBottom;
        return this;
    }

    public HorizontalGroup padRight(float padRight) {
        this.padRight = padRight;
        return this;
    }

    public float getPadTop() {
        return this.padTop;
    }

    public float getPadLeft() {
        return this.padLeft;
    }

    public float getPadBottom() {
        return this.padBottom;
    }

    public float getPadRight() {
        return this.padRight;
    }

    public HorizontalGroup align(int align) {
        this.align = align;
        return this;
    }

    public HorizontalGroup center() {
        this.align = 1;
        return this;
    }

    public HorizontalGroup top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public HorizontalGroup left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public HorizontalGroup bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public HorizontalGroup right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    public int getAlign() {
        return this.align;
    }

    public HorizontalGroup fill() {
        this.fill = 1.0f;
        return this;
    }

    public HorizontalGroup fill(float fill) {
        this.fill = fill;
        return this;
    }

    public float getFill() {
        return this.fill;
    }

    public HorizontalGroup expand() {
        this.expand = true;
        return this;
    }

    public HorizontalGroup expand(boolean expand) {
        this.expand = expand;
        return this;
    }

    public boolean getExpand() {
        return this.expand;
    }

    public HorizontalGroup grow() {
        this.expand = true;
        this.fill = 1.0f;
        return this;
    }

    public HorizontalGroup wrap() {
        this.wrap = true;
        return this;
    }

    public HorizontalGroup wrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }

    public boolean getWrap() {
        return this.wrap;
    }

    public HorizontalGroup rowAlign(int rowAlign) {
        this.rowAlign = rowAlign;
        return this;
    }

    public HorizontalGroup rowCenter() {
        this.rowAlign = 1;
        return this;
    }

    public HorizontalGroup rowTop() {
        this.rowAlign |= 2;
        this.rowAlign &= -5;
        return this;
    }

    public HorizontalGroup rowLeft() {
        this.rowAlign |= 8;
        this.rowAlign &= -17;
        return this;
    }

    public HorizontalGroup rowBottom() {
        this.rowAlign |= 4;
        this.rowAlign &= -3;
        return this;
    }

    public HorizontalGroup rowRight() {
        this.rowAlign |= 16;
        this.rowAlign &= -9;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.badlogic.gdx.scenes.scene2d.Actor
    public void drawDebugBounds(ShapeRenderer shapes) {
        super.drawDebugBounds(shapes);
        if (getDebug()) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            if (getStage() != null) {
                shapes.setColor(getStage().getDebugColor());
            }
            shapes.rect(getX() + this.padLeft, getY() + this.padBottom, getOriginX(), getOriginY(), (getWidth() - this.padLeft) - this.padRight, (getHeight() - this.padBottom) - this.padTop, getScaleX(), getScaleY(), getRotation());
        }
    }
}
