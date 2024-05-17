package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import kotlin.jvm.internal.CharCompanionObject;
/* loaded from: classes21.dex */
public class Table extends WidgetGroup {
    private static float[] columnWeightedWidth;
    private static float[] rowWeightedHeight;
    int align;
    Drawable background;
    private final Cell cellDefaults;
    private final Array<Cell> cells;
    private boolean clip;
    private final Array<Cell> columnDefaults;
    private float[] columnMinWidth;
    private float[] columnPrefWidth;
    private float[] columnWidth;
    private int columns;
    Debug debug;
    Array<DebugRect> debugRects;
    private float[] expandHeight;
    private float[] expandWidth;
    private boolean implicitEndRow;
    Value padBottom;
    Value padLeft;
    Value padRight;
    Value padTop;
    boolean round;
    private Cell rowDefaults;
    private float[] rowHeight;
    private float[] rowMinHeight;
    private float[] rowPrefHeight;
    private int rows;
    private boolean sizeInvalid;
    private Skin skin;
    private float tableMinHeight;
    private float tableMinWidth;
    private float tablePrefHeight;
    private float tablePrefWidth;
    public static Color debugTableColor = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static Color debugCellColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static Color debugActorColor = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    static final Pool<Cell> cellPool = new Pool<Cell>() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Table.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.badlogic.gdx.utils.Pool
        public Cell newObject() {
            return new Cell();
        }
    };
    public static Value backgroundTop = new Value() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Table.2
        @Override // com.badlogic.gdx.scenes.scene2d.ui.Value
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getTopHeight();
        }
    };
    public static Value backgroundLeft = new Value() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Table.3
        @Override // com.badlogic.gdx.scenes.scene2d.ui.Value
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getLeftWidth();
        }
    };
    public static Value backgroundBottom = new Value() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Table.4
        @Override // com.badlogic.gdx.scenes.scene2d.ui.Value
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getBottomHeight();
        }
    };
    public static Value backgroundRight = new Value() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Table.5
        @Override // com.badlogic.gdx.scenes.scene2d.ui.Value
        public float get(Actor context) {
            Drawable background = ((Table) context).background;
            if (background == null) {
                return 0.0f;
            }
            return background.getRightWidth();
        }
    };

    /* loaded from: classes21.dex */
    public enum Debug {
        none,
        all,
        table,
        cell,
        actor
    }

    /* loaded from: classes21.dex */
    public static class DebugRect extends Rectangle {
        static Pool<DebugRect> pool = Pools.get(DebugRect.class);
        Color color;
    }

    public Table() {
        this(null);
    }

    public Table(Skin skin) {
        this.cells = new Array<>(4);
        this.columnDefaults = new Array<>(2);
        this.sizeInvalid = true;
        this.padTop = backgroundTop;
        this.padLeft = backgroundLeft;
        this.padBottom = backgroundBottom;
        this.padRight = backgroundRight;
        this.align = 1;
        this.debug = Debug.none;
        this.round = true;
        this.skin = skin;
        this.cellDefaults = obtainCell();
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
    }

    private Cell obtainCell() {
        Cell cell = cellPool.obtain();
        cell.setTable(this);
        return cell;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public void draw(Batch batch, float parentAlpha) {
        validate();
        if (isTransform()) {
            applyTransform(batch, computeTransform());
            drawBackground(batch, parentAlpha, 0.0f, 0.0f);
            if (this.clip) {
                batch.flush();
                float padLeft = this.padLeft.get(this);
                float padBottom = this.padBottom.get(this);
                if (clipBegin(padLeft, padBottom, (getWidth() - padLeft) - this.padRight.get(this), (getHeight() - padBottom) - this.padTop.get(this))) {
                    drawChildren(batch, parentAlpha);
                    batch.flush();
                    clipEnd();
                }
            } else {
                drawChildren(batch, parentAlpha);
            }
            resetTransform(batch);
            return;
        }
        drawBackground(batch, parentAlpha, getX(), getY());
        super.draw(batch, parentAlpha);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        if (this.background == null) {
            return;
        }
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        this.background.draw(batch, x, y, getWidth(), getHeight());
    }

    public void setBackground(String drawableName) {
        Skin skin = this.skin;
        if (skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        setBackground(skin.getDrawable(drawableName));
    }

    public void setBackground(Drawable background) {
        if (this.background == background) {
            return;
        }
        float padTopOld = getPadTop();
        float padLeftOld = getPadLeft();
        float padBottomOld = getPadBottom();
        float padRightOld = getPadRight();
        this.background = background;
        float padTopNew = getPadTop();
        float padLeftNew = getPadLeft();
        float padBottomNew = getPadBottom();
        float padRightNew = getPadRight();
        if (padTopOld + padBottomOld != padTopNew + padBottomNew || padLeftOld + padRightOld != padLeftNew + padRightNew) {
            invalidateHierarchy();
        } else if (padTopOld != padTopNew || padLeftOld != padLeftNew || padBottomOld != padBottomNew || padRightOld != padRightNew) {
            invalidate();
        }
    }

    public Table background(Drawable background) {
        setBackground(background);
        return this;
    }

    public Table background(String drawableName) {
        setBackground(drawableName);
        return this;
    }

    public Drawable getBackground() {
        return this.background;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public Actor hit(float x, float y, boolean touchable) {
        if (this.clip && ((touchable && getTouchable() == Touchable.disabled) || x < 0.0f || x >= getWidth() || y < 0.0f || y >= getHeight())) {
            return null;
        }
        return super.hit(x, y, touchable);
    }

    public void setClip(boolean enabled) {
        this.clip = enabled;
        setTransform(enabled);
        invalidate();
    }

    public boolean getClip() {
        return this.clip;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void invalidate() {
        this.sizeInvalid = true;
        super.invalidate();
    }

    public <T extends Actor> Cell<T> add(T actor) {
        Cell columnCell;
        Cell<T> cell = obtainCell();
        cell.actor = actor;
        if (this.implicitEndRow) {
            this.implicitEndRow = false;
            this.rows--;
            this.cells.peek().endRow = false;
        }
        Array<Cell> cells = this.cells;
        int cellCount = cells.size;
        if (cellCount > 0) {
            Cell lastCell = cells.peek();
            if (!lastCell.endRow) {
                cell.column = lastCell.column + lastCell.colspan.intValue();
                cell.row = lastCell.row;
            } else {
                cell.column = 0;
                cell.row = lastCell.row + 1;
            }
            if (cell.row > 0) {
                int i = cellCount - 1;
                loop0: while (true) {
                    if (i < 0) {
                        break;
                    }
                    Cell other = cells.get(i);
                    int column = other.column;
                    int nn = other.colspan.intValue() + column;
                    while (column < nn) {
                        if (column != cell.column) {
                            column++;
                        } else {
                            cell.cellAboveIndex = i;
                            break loop0;
                        }
                    }
                    i--;
                }
            }
        } else {
            cell.column = 0;
            cell.row = 0;
        }
        cells.add(cell);
        cell.set(this.cellDefaults);
        if (cell.column < this.columnDefaults.size && (columnCell = this.columnDefaults.get(cell.column)) != null) {
            cell.merge(columnCell);
        }
        cell.merge(this.rowDefaults);
        if (actor != null) {
            addActor(actor);
        }
        return cell;
    }

    public Table add(Actor... actors) {
        for (Actor actor : actors) {
            add((Table) actor);
        }
        return this;
    }

    public Cell<Label> add(CharSequence text) {
        Skin skin = this.skin;
        if (skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return add((Table) new Label(text, skin));
    }

    public Cell<Label> add(CharSequence text, String labelStyleName) {
        Skin skin = this.skin;
        if (skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return add((Table) new Label(text, (Label.LabelStyle) skin.get(labelStyleName, Label.LabelStyle.class)));
    }

    public Cell<Label> add(CharSequence text, String fontName, Color color) {
        Skin skin = this.skin;
        if (skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return add((Table) new Label(text, new Label.LabelStyle(skin.getFont(fontName), color)));
    }

    public Cell<Label> add(CharSequence text, String fontName, String colorName) {
        Skin skin = this.skin;
        if (skin == null) {
            throw new IllegalStateException("Table must have a skin set to use this method.");
        }
        return add((Table) new Label(text, new Label.LabelStyle(skin.getFont(fontName), this.skin.getColor(colorName))));
    }

    public Cell add() {
        return add((Table) null);
    }

    public Cell<Stack> stack(Actor... actors) {
        Stack stack = new Stack();
        if (actors != null) {
            for (Actor actor : actors) {
                stack.addActor(actor);
            }
        }
        return add((Table) stack);
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public boolean removeActor(Actor actor) {
        return removeActor(actor, true);
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public boolean removeActor(Actor actor, boolean unfocus) {
        if (super.removeActor(actor, unfocus)) {
            Cell cell = getCell(actor);
            if (cell != null) {
                cell.actor = null;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public Actor removeActorAt(int index, boolean unfocus) {
        Actor actor = super.removeActorAt(index, unfocus);
        Cell cell = getCell(actor);
        if (cell != null) {
            cell.actor = null;
        }
        return actor;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public void clearChildren() {
        Array<Cell> cells = this.cells;
        for (int i = cells.size - 1; i >= 0; i--) {
            Cell cell = cells.get(i);
            Actor actor = cell.actor;
            if (actor != null) {
                actor.remove();
            }
        }
        cellPool.freeAll(cells);
        cells.clear();
        this.rows = 0;
        this.columns = 0;
        Cell cell2 = this.rowDefaults;
        if (cell2 != null) {
            cellPool.free(cell2);
        }
        this.rowDefaults = null;
        this.implicitEndRow = false;
        super.clearChildren();
    }

    public void reset() {
        clearChildren();
        this.padTop = backgroundTop;
        this.padLeft = backgroundLeft;
        this.padBottom = backgroundBottom;
        this.padRight = backgroundRight;
        this.align = 1;
        debug(Debug.none);
        this.cellDefaults.reset();
        int n = this.columnDefaults.size;
        for (int i = 0; i < n; i++) {
            Cell columnCell = this.columnDefaults.get(i);
            if (columnCell != null) {
                cellPool.free(columnCell);
            }
        }
        this.columnDefaults.clear();
    }

    public Cell row() {
        if (this.cells.size > 0) {
            if (!this.implicitEndRow) {
                if (this.cells.peek().endRow) {
                    return this.rowDefaults;
                }
                endRow();
            }
            invalidate();
        }
        this.implicitEndRow = false;
        Cell cell = this.rowDefaults;
        if (cell != null) {
            cellPool.free(cell);
        }
        this.rowDefaults = obtainCell();
        this.rowDefaults.clear();
        return this.rowDefaults;
    }

    private void endRow() {
        Array<Cell> cells = this.cells;
        int rowColumns = 0;
        for (int i = cells.size - 1; i >= 0; i--) {
            Cell cell = cells.get(i);
            if (cell.endRow) {
                break;
            }
            rowColumns += cell.colspan.intValue();
        }
        int i2 = this.columns;
        this.columns = Math.max(i2, rowColumns);
        this.rows++;
        cells.peek().endRow = true;
    }

    public Cell columnDefaults(int column) {
        Cell cell = this.columnDefaults.size > column ? this.columnDefaults.get(column) : null;
        if (cell == null) {
            cell = obtainCell();
            cell.clear();
            if (column >= this.columnDefaults.size) {
                for (int i = this.columnDefaults.size; i < column; i++) {
                    this.columnDefaults.add(null);
                }
                this.columnDefaults.add(cell);
            } else {
                this.columnDefaults.set(column, cell);
            }
        }
        return cell;
    }

    public <T extends Actor> Cell<T> getCell(T actor) {
        Array<Cell> cells = this.cells;
        int n = cells.size;
        for (int i = 0; i < n; i++) {
            Cell c = cells.get(i);
            if (c.actor == actor) {
                return c;
            }
        }
        return null;
    }

    public Array<Cell> getCells() {
        return this.cells;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        float width = this.tablePrefWidth;
        Drawable drawable = this.background;
        return drawable != null ? Math.max(width, drawable.getMinWidth()) : width;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        float height = this.tablePrefHeight;
        Drawable drawable = this.background;
        return drawable != null ? Math.max(height, drawable.getMinHeight()) : height;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getMinWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinWidth;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getMinHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.tableMinHeight;
    }

    public Cell defaults() {
        return this.cellDefaults;
    }

    public Table pad(Value pad) {
        if (pad == null) {
            throw new IllegalArgumentException("pad cannot be null.");
        }
        this.padTop = pad;
        this.padLeft = pad;
        this.padBottom = pad;
        this.padRight = pad;
        this.sizeInvalid = true;
        return this;
    }

    public Table pad(Value top, Value left, Value bottom, Value right) {
        if (top == null) {
            throw new IllegalArgumentException("top cannot be null.");
        }
        if (left == null) {
            throw new IllegalArgumentException("left cannot be null.");
        }
        if (bottom == null) {
            throw new IllegalArgumentException("bottom cannot be null.");
        }
        if (right == null) {
            throw new IllegalArgumentException("right cannot be null.");
        }
        this.padTop = top;
        this.padLeft = left;
        this.padBottom = bottom;
        this.padRight = right;
        this.sizeInvalid = true;
        return this;
    }

    public Table padTop(Value padTop) {
        if (padTop == null) {
            throw new IllegalArgumentException("padTop cannot be null.");
        }
        this.padTop = padTop;
        this.sizeInvalid = true;
        return this;
    }

    public Table padLeft(Value padLeft) {
        if (padLeft == null) {
            throw new IllegalArgumentException("padLeft cannot be null.");
        }
        this.padLeft = padLeft;
        this.sizeInvalid = true;
        return this;
    }

    public Table padBottom(Value padBottom) {
        if (padBottom == null) {
            throw new IllegalArgumentException("padBottom cannot be null.");
        }
        this.padBottom = padBottom;
        this.sizeInvalid = true;
        return this;
    }

    public Table padRight(Value padRight) {
        if (padRight == null) {
            throw new IllegalArgumentException("padRight cannot be null.");
        }
        this.padRight = padRight;
        this.sizeInvalid = true;
        return this;
    }

    public Table pad(float pad) {
        pad(Value.Fixed.valueOf(pad));
        return this;
    }

    public Table pad(float top, float left, float bottom, float right) {
        this.padTop = Value.Fixed.valueOf(top);
        this.padLeft = Value.Fixed.valueOf(left);
        this.padBottom = Value.Fixed.valueOf(bottom);
        this.padRight = Value.Fixed.valueOf(right);
        this.sizeInvalid = true;
        return this;
    }

    public Table padTop(float padTop) {
        this.padTop = Value.Fixed.valueOf(padTop);
        this.sizeInvalid = true;
        return this;
    }

    public Table padLeft(float padLeft) {
        this.padLeft = Value.Fixed.valueOf(padLeft);
        this.sizeInvalid = true;
        return this;
    }

    public Table padBottom(float padBottom) {
        this.padBottom = Value.Fixed.valueOf(padBottom);
        this.sizeInvalid = true;
        return this;
    }

    public Table padRight(float padRight) {
        this.padRight = Value.Fixed.valueOf(padRight);
        this.sizeInvalid = true;
        return this;
    }

    public Table align(int align) {
        this.align = align;
        return this;
    }

    public Table center() {
        this.align = 1;
        return this;
    }

    public Table top() {
        this.align |= 2;
        this.align &= -5;
        return this;
    }

    public Table left() {
        this.align |= 8;
        this.align &= -17;
        return this;
    }

    public Table bottom() {
        this.align |= 4;
        this.align &= -3;
        return this;
    }

    public Table right() {
        this.align |= 16;
        this.align &= -9;
        return this;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Actor
    public void setDebug(boolean enabled) {
        debug(enabled ? Debug.all : Debug.none);
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Actor
    public Table debug() {
        super.debug();
        return this;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group
    public Table debugAll() {
        super.debugAll();
        return this;
    }

    public Table debugTable() {
        super.setDebug(true);
        if (this.debug != Debug.table) {
            this.debug = Debug.table;
            invalidate();
        }
        return this;
    }

    public Table debugCell() {
        super.setDebug(true);
        if (this.debug != Debug.cell) {
            this.debug = Debug.cell;
            invalidate();
        }
        return this;
    }

    public Table debugActor() {
        super.setDebug(true);
        if (this.debug != Debug.actor) {
            this.debug = Debug.actor;
            invalidate();
        }
        return this;
    }

    public Table debug(Debug debug) {
        super.setDebug(debug != Debug.none);
        if (this.debug != debug) {
            this.debug = debug;
            if (debug == Debug.none) {
                clearDebugRects();
            } else {
                invalidate();
            }
        }
        return this;
    }

    public Debug getTableDebug() {
        return this.debug;
    }

    public Value getPadTopValue() {
        return this.padTop;
    }

    public float getPadTop() {
        return this.padTop.get(this);
    }

    public Value getPadLeftValue() {
        return this.padLeft;
    }

    public float getPadLeft() {
        return this.padLeft.get(this);
    }

    public Value getPadBottomValue() {
        return this.padBottom;
    }

    public float getPadBottom() {
        return this.padBottom.get(this);
    }

    public Value getPadRightValue() {
        return this.padRight;
    }

    public float getPadRight() {
        return this.padRight.get(this);
    }

    public float getPadX() {
        return this.padLeft.get(this) + this.padRight.get(this);
    }

    public float getPadY() {
        return this.padTop.get(this) + this.padBottom.get(this);
    }

    public int getAlign() {
        return this.align;
    }

    public int getRow(float y) {
        Array<Cell> cells = this.cells;
        int row = 0;
        float y2 = y + getPadTop();
        int i = 0;
        int n = cells.size;
        if (n == 0) {
            return -1;
        }
        if (n == 1) {
            return 0;
        }
        while (i < n) {
            int i2 = i + 1;
            Cell c = cells.get(i);
            if (c.actorY + c.computedPadTop < y2) {
                break;
            }
            if (c.endRow) {
                row++;
            }
            i = i2;
        }
        int i3 = this.rows;
        return Math.min(row, i3 - 1);
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public float getRowHeight(int rowIndex) {
        float[] fArr = this.rowHeight;
        if (fArr == null) {
            return 0.0f;
        }
        return fArr[rowIndex];
    }

    public float getRowMinHeight(int rowIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.rowMinHeight[rowIndex];
    }

    public float getRowPrefHeight(int rowIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.rowPrefHeight[rowIndex];
    }

    public float getColumnWidth(int columnIndex) {
        float[] fArr = this.columnWidth;
        if (fArr == null) {
            return 0.0f;
        }
        return fArr[columnIndex];
    }

    public float getColumnMinWidth(int columnIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.columnMinWidth[columnIndex];
    }

    public float getColumnPrefWidth(int columnIndex) {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.columnPrefWidth[columnIndex];
    }

    private float[] ensureSize(float[] array, int size) {
        if (array == null || array.length < size) {
            return new float[size];
        }
        int n = array.length;
        for (int i = 0; i < n; i++) {
            array[i] = 0.0f;
        }
        return array;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        layout(0.0f, 0.0f, width, height);
        Array<Cell> cells = this.cells;
        if (this.round) {
            int n = cells.size;
            for (int i = 0; i < n; i++) {
                Cell c = cells.get(i);
                float actorWidth = Math.round(c.actorWidth);
                float actorHeight = Math.round(c.actorHeight);
                float actorX = Math.round(c.actorX);
                float actorY = (height - Math.round(c.actorY)) - actorHeight;
                c.setActorBounds(actorX, actorY, actorWidth, actorHeight);
                Actor actor = c.actor;
                if (actor != null) {
                    actor.setBounds(actorX, actorY, actorWidth, actorHeight);
                }
            }
        } else {
            int n2 = cells.size;
            for (int i2 = 0; i2 < n2; i2++) {
                Cell c2 = cells.get(i2);
                float actorHeight2 = c2.actorHeight;
                float actorY2 = (height - c2.actorY) - actorHeight2;
                c2.setActorY(actorY2);
                Actor actor2 = c2.actor;
                if (actor2 != null) {
                    actor2.setBounds(c2.actorX, actorY2, c2.actorWidth, actorHeight2);
                }
            }
        }
        Array<Actor> children = getChildren();
        int n3 = children.size;
        for (int i3 = 0; i3 < n3; i3++) {
            Actor child = children.get(i3);
            if (child instanceof Layout) {
                ((Layout) child).validate();
            }
        }
    }

    private void computeSize() {
        int cellCount;
        float uniformMinWidth;
        Array<Cell> cells;
        float uniformMinHeight;
        float uniformPrefWidth;
        int nn;
        float f;
        float[] expandWidth;
        this.sizeInvalid = false;
        Array<Cell> cells2 = this.cells;
        int cellCount2 = cells2.size;
        if (cellCount2 > 0 && !cells2.peek().endRow) {
            endRow();
            this.implicitEndRow = true;
        }
        int columns = this.columns;
        int rows = this.rows;
        float[] columnMinWidth = ensureSize(this.columnMinWidth, columns);
        this.columnMinWidth = columnMinWidth;
        float[] rowMinHeight = ensureSize(this.rowMinHeight, rows);
        this.rowMinHeight = rowMinHeight;
        float[] columnPrefWidth = ensureSize(this.columnPrefWidth, columns);
        this.columnPrefWidth = columnPrefWidth;
        float[] rowPrefHeight = ensureSize(this.rowPrefHeight, rows);
        this.rowPrefHeight = rowPrefHeight;
        float[] columnWidth = ensureSize(this.columnWidth, columns);
        this.columnWidth = columnWidth;
        float[] rowHeight = ensureSize(this.rowHeight, rows);
        this.rowHeight = rowHeight;
        float[] expandWidth2 = ensureSize(this.expandWidth, columns);
        this.expandWidth = expandWidth2;
        float[] expandHeight = ensureSize(this.expandHeight, rows);
        this.expandHeight = expandHeight;
        float spaceRightLast = 0.0f;
        int i = 0;
        while (i < cellCount2) {
            Cell c = cells2.get(i);
            float[] columnWidth2 = columnWidth;
            int column = c.column;
            float[] rowHeight2 = rowHeight;
            int row = c.row;
            int colspan = c.colspan.intValue();
            int cellCount3 = cellCount2;
            Actor a = c.actor;
            int i2 = i;
            if (c.expandY.intValue() != 0 && expandHeight[row] == 0.0f) {
                expandHeight[row] = c.expandY.intValue();
            }
            if (colspan == 1 && c.expandX.intValue() != 0 && expandWidth2[column] == 0.0f) {
                expandWidth2[column] = c.expandX.intValue();
            }
            float[] expandHeight2 = expandHeight;
            c.computedPadLeft = c.padLeft.get(a) + (column == 0 ? 0.0f : Math.max(0.0f, c.spaceLeft.get(a) - spaceRightLast));
            c.computedPadTop = c.padTop.get(a);
            if (c.cellAboveIndex == -1) {
                expandWidth = expandWidth2;
            } else {
                Cell above = cells2.get(c.cellAboveIndex);
                expandWidth = expandWidth2;
                c.computedPadTop += Math.max(0.0f, c.spaceTop.get(a) - above.spaceBottom.get(a));
            }
            float spaceRight = c.spaceRight.get(a);
            c.computedPadRight = c.padRight.get(a) + (column + colspan == columns ? 0.0f : spaceRight);
            c.computedPadBottom = c.padBottom.get(a) + (row == rows + (-1) ? 0.0f : c.spaceBottom.get(a));
            float prefWidth = c.prefWidth.get(a);
            float prefHeight = c.prefHeight.get(a);
            float minWidth = c.minWidth.get(a);
            float minHeight = c.minHeight.get(a);
            int rows2 = rows;
            float maxWidth = c.maxWidth.get(a);
            int columns2 = columns;
            float maxHeight = c.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            if (colspan == 1) {
                float hpadding = c.computedPadLeft + c.computedPadRight;
                columnPrefWidth[column] = Math.max(columnPrefWidth[column], prefWidth + hpadding);
                columnMinWidth[column] = Math.max(columnMinWidth[column], minWidth + hpadding);
            }
            float vpadding = c.computedPadTop + c.computedPadBottom;
            rowPrefHeight[row] = Math.max(rowPrefHeight[row], prefHeight + vpadding);
            rowMinHeight[row] = Math.max(rowMinHeight[row], minHeight + vpadding);
            i = i2 + 1;
            columnWidth = columnWidth2;
            rowHeight = rowHeight2;
            cellCount2 = cellCount3;
            expandHeight = expandHeight2;
            spaceRightLast = spaceRight;
            expandWidth2 = expandWidth;
            rows = rows2;
            columns = columns2;
        }
        int cellCount4 = cellCount2;
        int columns3 = columns;
        int rows3 = rows;
        float[] expandWidth3 = expandWidth2;
        float uniformMinWidth2 = 0.0f;
        float uniformMinHeight2 = 0.0f;
        float uniformPrefWidth2 = 0.0f;
        float uniformPrefHeight = 0.0f;
        int i3 = 0;
        while (true) {
            cellCount = cellCount4;
            if (i3 >= cellCount) {
                break;
            }
            Cell c2 = cells2.get(i3);
            int column2 = c2.column;
            int expandX = c2.expandX.intValue();
            if (expandX != 0) {
                int nn2 = c2.colspan.intValue() + column2;
                int ii = column2;
                while (true) {
                    if (ii < nn2) {
                        if (expandWidth3[ii] != 0.0f) {
                            break;
                        }
                        ii++;
                    } else {
                        int ii2 = column2;
                        while (ii2 < nn2) {
                            expandWidth3[ii2] = expandX;
                            ii2++;
                            nn2 = nn2;
                        }
                    }
                }
            }
            if (c2.uniformX == Boolean.TRUE && c2.colspan.intValue() == 1) {
                float hpadding2 = c2.computedPadLeft + c2.computedPadRight;
                uniformMinWidth2 = Math.max(uniformMinWidth2, columnMinWidth[column2] - hpadding2);
                uniformPrefWidth2 = Math.max(uniformPrefWidth2, columnPrefWidth[column2] - hpadding2);
            }
            if (c2.uniformY == Boolean.TRUE) {
                float vpadding2 = c2.computedPadTop + c2.computedPadBottom;
                uniformMinHeight2 = Math.max(uniformMinHeight2, rowMinHeight[c2.row] - vpadding2);
                uniformPrefHeight = Math.max(uniformPrefHeight, rowPrefHeight[c2.row] - vpadding2);
            }
            i3++;
            cellCount4 = cellCount;
        }
        if (uniformPrefWidth2 > 0.0f || uniformPrefHeight > 0.0f) {
            for (int i4 = 0; i4 < cellCount; i4++) {
                Cell c3 = cells2.get(i4);
                if (uniformPrefWidth2 > 0.0f && c3.uniformX == Boolean.TRUE && c3.colspan.intValue() == 1) {
                    float hpadding3 = c3.computedPadLeft + c3.computedPadRight;
                    columnMinWidth[c3.column] = uniformMinWidth2 + hpadding3;
                    columnPrefWidth[c3.column] = uniformPrefWidth2 + hpadding3;
                }
                if (uniformPrefHeight > 0.0f && c3.uniformY == Boolean.TRUE) {
                    float vpadding3 = c3.computedPadTop + c3.computedPadBottom;
                    rowMinHeight[c3.row] = uniformMinHeight2 + vpadding3;
                    rowPrefHeight[c3.row] = uniformPrefHeight + vpadding3;
                }
            }
        }
        int i5 = 0;
        while (i5 < cellCount) {
            Cell c4 = cells2.get(i5);
            int colspan2 = c4.colspan.intValue();
            if (colspan2 == 1) {
                uniformMinWidth = uniformMinWidth2;
                cells = cells2;
                uniformMinHeight = uniformMinHeight2;
                uniformPrefWidth = uniformPrefWidth2;
            } else {
                int column3 = c4.column;
                Actor a2 = c4.actor;
                float minWidth2 = c4.minWidth.get(a2);
                uniformMinWidth = uniformMinWidth2;
                float prefWidth2 = c4.prefWidth.get(a2);
                cells = cells2;
                float maxWidth2 = c4.maxWidth.get(a2);
                if (prefWidth2 < minWidth2) {
                    prefWidth2 = minWidth2;
                }
                if (maxWidth2 > 0.0f && prefWidth2 > maxWidth2) {
                    prefWidth2 = maxWidth2;
                }
                float maxWidth3 = c4.computedPadLeft;
                uniformMinHeight = uniformMinHeight2;
                float uniformMinHeight3 = c4.computedPadRight;
                float spannedMinWidth = -(maxWidth3 + uniformMinHeight3);
                float spannedMinWidth2 = spannedMinWidth;
                int nn3 = column3 + colspan2;
                float totalExpandWidth = spannedMinWidth;
                float totalExpandWidth2 = 0.0f;
                for (int ii3 = column3; ii3 < nn3; ii3++) {
                    spannedMinWidth2 += columnMinWidth[ii3];
                    totalExpandWidth += columnPrefWidth[ii3];
                    totalExpandWidth2 += expandWidth3[ii3];
                }
                float extraMinWidth = Math.max(0.0f, minWidth2 - spannedMinWidth2);
                uniformPrefWidth = uniformPrefWidth2;
                float uniformPrefWidth3 = prefWidth2 - totalExpandWidth;
                float extraPrefWidth = Math.max(0.0f, uniformPrefWidth3);
                int nn4 = column3 + colspan2;
                int ii4 = column3;
                while (ii4 < nn4) {
                    if (totalExpandWidth2 == 0.0f) {
                        nn = nn4;
                        f = 1.0f / colspan2;
                    } else {
                        nn = nn4;
                        f = expandWidth3[ii4] / totalExpandWidth2;
                    }
                    float ratio = f;
                    columnMinWidth[ii4] = columnMinWidth[ii4] + (extraMinWidth * ratio);
                    columnPrefWidth[ii4] = columnPrefWidth[ii4] + (extraPrefWidth * ratio);
                    ii4++;
                    nn4 = nn;
                }
            }
            i5++;
            uniformMinWidth2 = uniformMinWidth;
            cells2 = cells;
            uniformMinHeight2 = uniformMinHeight;
            uniformPrefWidth2 = uniformPrefWidth;
        }
        this.tableMinWidth = 0.0f;
        this.tableMinHeight = 0.0f;
        this.tablePrefWidth = 0.0f;
        this.tablePrefHeight = 0.0f;
        int i6 = 0;
        while (true) {
            int columns4 = columns3;
            if (i6 >= columns4) {
                break;
            }
            this.tableMinWidth += columnMinWidth[i6];
            this.tablePrefWidth += columnPrefWidth[i6];
            i6++;
            columns3 = columns4;
        }
        int i7 = 0;
        while (true) {
            int rows4 = rows3;
            if (i7 < rows4) {
                this.tableMinHeight += rowMinHeight[i7];
                this.tablePrefHeight += Math.max(rowMinHeight[i7], rowPrefHeight[i7]);
                i7++;
                rows3 = rows4;
            } else {
                float hpadding4 = this.padLeft.get(this) + this.padRight.get(this);
                float vpadding4 = this.padTop.get(this) + this.padBottom.get(this);
                this.tableMinWidth += hpadding4;
                this.tableMinHeight += vpadding4;
                this.tablePrefWidth = Math.max(this.tablePrefWidth + hpadding4, this.tableMinWidth);
                this.tablePrefHeight = Math.max(this.tablePrefHeight + vpadding4, this.tableMinHeight);
                return;
            }
        }
    }

    private void layout(float layoutX, float layoutY, float layoutWidth, float layoutHeight) {
        float padTop;
        float padLeft;
        float[] expandHeight;
        float[] columnWeightedWidth2;
        float totalGrowWidth;
        float[] rowWeightedHeight2;
        int columns;
        int rows;
        int cellCount;
        float x;
        float y;
        float[] rowHeight;
        float[] columnWidth;
        int cellCount2;
        Cell c;
        float[] columnWidth2;
        int rows2;
        char c2;
        Array<Cell> cells = this.cells;
        int cellCount3 = cells.size;
        if (this.sizeInvalid) {
            computeSize();
        }
        float padLeft2 = this.padLeft.get(this);
        float hpadding = padLeft2 + this.padRight.get(this);
        float padTop2 = this.padTop.get(this);
        float vpadding = padTop2 + this.padBottom.get(this);
        int columns2 = this.columns;
        int rows3 = this.rows;
        float[] expandWidth = this.expandWidth;
        float[] expandHeight2 = this.expandHeight;
        float[] columnWidth3 = this.columnWidth;
        float[] rowHeight2 = this.rowHeight;
        float totalExpandWidth = 0.0f;
        for (int i = 0; i < columns2; i++) {
            totalExpandWidth += expandWidth[i];
        }
        float totalExpandHeight = 0.0f;
        for (int i2 = 0; i2 < rows3; i2++) {
            totalExpandHeight += expandHeight2[i2];
        }
        float f = this.tablePrefWidth;
        float f2 = this.tableMinWidth;
        float totalGrowWidth2 = f - f2;
        if (totalGrowWidth2 != 0.0f) {
            float extraWidth = Math.min(totalGrowWidth2, Math.max(0.0f, layoutWidth - f2));
            float[] columnWeightedWidth3 = ensureSize(columnWeightedWidth, columns2);
            columnWeightedWidth = columnWeightedWidth3;
            padTop = padTop2;
            float[] columnMinWidth = this.columnMinWidth;
            padLeft = padLeft2;
            float[] columnPrefWidth = this.columnPrefWidth;
            expandHeight = expandHeight2;
            for (int i3 = 0; i3 < columns2; i3++) {
                float growWidth = columnPrefWidth[i3] - columnMinWidth[i3];
                float growRatio = growWidth / totalGrowWidth2;
                columnWeightedWidth3[i3] = columnMinWidth[i3] + (extraWidth * growRatio);
            }
            columnWeightedWidth2 = columnWeightedWidth3;
        } else {
            expandHeight = expandHeight2;
            padLeft = padLeft2;
            padTop = padTop2;
            columnWeightedWidth2 = this.columnMinWidth;
        }
        float totalGrowHeight = this.tablePrefHeight - this.tableMinHeight;
        if (totalGrowHeight != 0.0f) {
            float[] rowWeightedHeight3 = ensureSize(rowWeightedHeight, rows3);
            rowWeightedHeight = rowWeightedHeight3;
            float extraHeight = Math.min(totalGrowHeight, Math.max(0.0f, layoutHeight - this.tableMinHeight));
            float[] rowMinHeight = this.rowMinHeight;
            totalGrowWidth = totalGrowWidth2;
            float[] rowPrefHeight = this.rowPrefHeight;
            for (int i4 = 0; i4 < rows3; i4++) {
                float growHeight = rowPrefHeight[i4] - rowMinHeight[i4];
                float growRatio2 = growHeight / totalGrowHeight;
                rowWeightedHeight3[i4] = rowMinHeight[i4] + (extraHeight * growRatio2);
            }
            rowWeightedHeight2 = rowWeightedHeight3;
        } else {
            rowWeightedHeight2 = this.rowMinHeight;
            totalGrowWidth = totalGrowWidth2;
        }
        int i5 = 0;
        while (i5 < cellCount3) {
            Cell c3 = cells.get(i5);
            int column = c3.column;
            int row = c3.row;
            float totalGrowHeight2 = totalGrowHeight;
            Actor a = c3.actor;
            Array<Cell> cells2 = cells;
            int colspan = c3.colspan.intValue();
            int cellCount4 = cellCount3;
            int cellCount5 = column + colspan;
            int rows4 = rows3;
            float spannedWeightedWidth = vpadding;
            float vpadding2 = 0.0f;
            for (int rows5 = column; rows5 < cellCount5; rows5++) {
                vpadding2 += columnWeightedWidth2[rows5];
            }
            float weightedHeight = rowWeightedHeight2[row];
            float prefWidth = c3.prefWidth.get(a);
            float[] rowWeightedHeight4 = rowWeightedHeight2;
            float prefHeight = c3.prefHeight.get(a);
            float[] columnWeightedWidth4 = columnWeightedWidth2;
            float minWidth = c3.minWidth.get(a);
            float[] expandWidth2 = expandWidth;
            float minHeight = c3.minHeight.get(a);
            int columns3 = columns2;
            float maxWidth = c3.maxWidth.get(a);
            float hpadding2 = hpadding;
            float maxHeight = c3.maxHeight.get(a);
            if (prefWidth < minWidth) {
                prefWidth = minWidth;
            }
            if (prefHeight < minHeight) {
                prefHeight = minHeight;
            }
            if (maxWidth > 0.0f && prefWidth > maxWidth) {
                prefWidth = maxWidth;
            }
            if (maxHeight > 0.0f && prefHeight > maxHeight) {
                prefHeight = maxHeight;
            }
            c3.actorWidth = Math.min((vpadding2 - c3.computedPadLeft) - c3.computedPadRight, prefWidth);
            c3.actorHeight = Math.min((weightedHeight - c3.computedPadTop) - c3.computedPadBottom, prefHeight);
            if (colspan == 1) {
                columnWidth3[column] = Math.max(columnWidth3[column], vpadding2);
            }
            rowHeight2[row] = Math.max(rowHeight2[row], weightedHeight);
            i5++;
            totalGrowHeight = totalGrowHeight2;
            vpadding = spannedWeightedWidth;
            cells = cells2;
            rowWeightedHeight2 = rowWeightedHeight4;
            cellCount3 = cellCount4;
            rows3 = rows4;
            columnWeightedWidth2 = columnWeightedWidth4;
            expandWidth = expandWidth2;
            columns2 = columns3;
            hpadding = hpadding2;
        }
        Array<Cell> cells3 = cells;
        int cellCount6 = cellCount3;
        float[] columnWeightedWidth5 = columnWeightedWidth2;
        float hpadding3 = hpadding;
        float vpadding3 = vpadding;
        int columns4 = columns2;
        int rows6 = rows3;
        float[] expandWidth3 = expandWidth;
        if (totalExpandWidth <= 0.0f) {
            columns = columns4;
        } else {
            float extra = layoutWidth - hpadding3;
            int i6 = 0;
            while (true) {
                columns = columns4;
                if (i6 >= columns) {
                    break;
                }
                extra -= columnWidth3[i6];
                i6++;
                columns4 = columns;
            }
            if (extra > 0.0f) {
                float used = 0.0f;
                int lastIndex = 0;
                for (int i7 = 0; i7 < columns; i7++) {
                    if (expandWidth3[i7] != 0.0f) {
                        float amount = (expandWidth3[i7] * extra) / totalExpandWidth;
                        columnWidth3[i7] = columnWidth3[i7] + amount;
                        used += amount;
                        lastIndex = i7;
                    }
                }
                columnWidth3[lastIndex] = columnWidth3[lastIndex] + (extra - used);
            }
        }
        if (totalExpandHeight <= 0.0f) {
            rows = rows6;
        } else {
            float extra2 = layoutHeight - vpadding3;
            int i8 = 0;
            while (true) {
                rows = rows6;
                if (i8 >= rows) {
                    break;
                }
                extra2 -= rowHeight2[i8];
                i8++;
                rows6 = rows;
            }
            if (extra2 > 0.0f) {
                float used2 = 0.0f;
                int lastIndex2 = 0;
                for (int i9 = 0; i9 < rows; i9++) {
                    if (expandHeight[i9] != 0.0f) {
                        float amount2 = (expandHeight[i9] * extra2) / totalExpandHeight;
                        rowHeight2[i9] = rowHeight2[i9] + amount2;
                        used2 += amount2;
                        lastIndex2 = i9;
                    }
                }
                rowHeight2[lastIndex2] = rowHeight2[lastIndex2] + (extra2 - used2);
            }
        }
        int i10 = 0;
        while (true) {
            cellCount = cellCount6;
            if (i10 >= cellCount) {
                break;
            }
            Array<Cell> cells4 = cells3;
            Cell c4 = cells4.get(i10);
            int colspan2 = c4.colspan.intValue();
            if (colspan2 != 1) {
                float extraWidth2 = 0.0f;
                int column2 = c4.column;
                int nn = column2 + colspan2;
                while (column2 < nn) {
                    extraWidth2 += columnWeightedWidth5[column2] - columnWidth3[column2];
                    column2++;
                }
                float extraWidth3 = (extraWidth2 - Math.max(0.0f, c4.computedPadLeft + c4.computedPadRight)) / colspan2;
                if (extraWidth3 > 0.0f) {
                    int column3 = c4.column;
                    int nn2 = column3 + colspan2;
                    while (column3 < nn2) {
                        columnWidth3[column3] = columnWidth3[column3] + extraWidth3;
                        column3++;
                    }
                }
            }
            i10++;
            cellCount6 = cellCount;
            cells3 = cells4;
        }
        Array<Cell> cells5 = cells3;
        float tableWidth = hpadding3;
        for (int i11 = 0; i11 < columns; i11++) {
            tableWidth += columnWidth3[i11];
        }
        float tableHeight = vpadding3;
        for (int i12 = 0; i12 < rows; i12++) {
            tableHeight += rowHeight2[i12];
        }
        int align = this.align;
        float x2 = layoutX + padLeft;
        if ((align & 16) != 0) {
            x = x2 + (layoutWidth - tableWidth);
        } else if ((align & 8) != 0) {
            x = x2;
        } else {
            x = x2 + ((layoutWidth - tableWidth) / 2.0f);
        }
        float x3 = layoutY + padTop;
        if ((align & 4) != 0) {
            float y2 = x3 + (layoutHeight - tableHeight);
            y = y2;
        } else if ((align & 2) != 0) {
            y = x3;
        } else {
            float y3 = x3 + ((layoutHeight - tableHeight) / 2.0f);
            y = y3;
        }
        float currentX = x;
        float currentY = y;
        int i13 = 0;
        while (i13 < cellCount) {
            Cell c5 = cells5.get(i13);
            float spannedCellWidth = 0.0f;
            int column4 = c5.column;
            int columns5 = columns;
            int nn3 = c5.colspan.intValue() + column4;
            while (column4 < nn3) {
                spannedCellWidth += columnWidth3[column4];
                column4++;
            }
            float spannedCellWidth2 = spannedCellWidth - (c5.computedPadLeft + c5.computedPadRight);
            float currentX2 = currentX + c5.computedPadLeft;
            float fillX = c5.fillX.floatValue();
            float fillY = c5.fillY.floatValue();
            if (fillX <= 0.0f) {
                columnWidth2 = columnWidth3;
                rows2 = rows;
            } else {
                columnWidth2 = columnWidth3;
                rows2 = rows;
                c5.actorWidth = Math.max(spannedCellWidth2 * fillX, c5.minWidth.get(c5.actor));
                float maxWidth2 = c5.maxWidth.get(c5.actor);
                if (maxWidth2 > 0.0f) {
                    c5.actorWidth = Math.min(c5.actorWidth, maxWidth2);
                }
            }
            if (fillY > 0.0f) {
                c5.actorHeight = Math.max(((rowHeight2[c5.row] * fillY) - c5.computedPadTop) - c5.computedPadBottom, c5.minHeight.get(c5.actor));
                float maxHeight2 = c5.maxHeight.get(c5.actor);
                if (maxHeight2 > 0.0f) {
                    c5.actorHeight = Math.min(c5.actorHeight, maxHeight2);
                }
            }
            int align2 = c5.align.intValue();
            if ((align2 & 8) != 0) {
                c5.actorX = currentX2;
            } else if ((align2 & 16) != 0) {
                c5.actorX = (currentX2 + spannedCellWidth2) - c5.actorWidth;
            } else {
                c5.actorX = ((spannedCellWidth2 - c5.actorWidth) / 2.0f) + currentX2;
            }
            if ((align2 & 2) != 0) {
                c5.actorY = c5.computedPadTop + currentY;
                c2 = CharCompanionObject.MIN_VALUE;
            } else if ((align2 & 4) != 0) {
                c5.actorY = ((rowHeight2[c5.row] + currentY) - c5.actorHeight) - c5.computedPadBottom;
                c2 = CharCompanionObject.MIN_VALUE;
            } else {
                float f3 = ((rowHeight2[c5.row] - c5.actorHeight) + c5.computedPadTop) - c5.computedPadBottom;
                c2 = CharCompanionObject.MIN_VALUE;
                c5.actorY = (f3 / 2.0f) + currentY;
            }
            if (c5.endRow) {
                currentX = x;
                currentY += rowHeight2[c5.row];
            } else {
                currentX = currentX2 + spannedCellWidth2 + c5.computedPadRight;
            }
            i13++;
            columnWidth3 = columnWidth2;
            columns = columns5;
            rows = rows2;
        }
        float[] columnWidth4 = columnWidth3;
        if (this.debug == Debug.none) {
            return;
        }
        clearDebugRects();
        float currentX3 = x;
        float currentY2 = y;
        if (this.debug == Debug.table || this.debug == Debug.all) {
            rowHeight = rowHeight2;
            columnWidth = columnWidth4;
            addDebugRect(layoutX, layoutY, layoutWidth, layoutHeight, debugTableColor);
            addDebugRect(x, y, tableWidth - hpadding3, tableHeight - vpadding3, debugTableColor);
        } else {
            rowHeight = rowHeight2;
            columnWidth = columnWidth4;
        }
        float currentY3 = currentY2;
        float currentX4 = currentX3;
        int i14 = 0;
        while (i14 < cellCount) {
            Cell c6 = cells5.get(i14);
            if (this.debug == Debug.actor || this.debug == Debug.all) {
                cellCount2 = cellCount;
                c = c6;
                addDebugRect(c6.actorX, c6.actorY, c6.actorWidth, c6.actorHeight, debugActorColor);
            } else {
                cellCount2 = cellCount;
                c = c6;
            }
            float spannedCellWidth3 = 0.0f;
            int column5 = c.column;
            int nn4 = c.colspan.intValue() + column5;
            while (column5 < nn4) {
                spannedCellWidth3 += columnWidth[column5];
                column5++;
            }
            float spannedCellWidth4 = spannedCellWidth3 - (c.computedPadLeft + c.computedPadRight);
            float spannedCellWidth5 = c.computedPadLeft;
            float currentX5 = currentX4 + spannedCellWidth5;
            if (this.debug == Debug.cell || this.debug == Debug.all) {
                addDebugRect(currentX5, currentY3 + c.computedPadTop, spannedCellWidth4, (rowHeight[c.row] - c.computedPadTop) - c.computedPadBottom, debugCellColor);
            }
            if (c.endRow) {
                float currentX6 = x;
                currentY3 += rowHeight[c.row];
                currentX4 = currentX6;
            } else {
                float currentX7 = c.computedPadRight;
                currentX4 = currentX5 + spannedCellWidth4 + currentX7;
            }
            i14++;
            cellCount = cellCount2;
        }
    }

    private void clearDebugRects() {
        if (this.debugRects == null) {
            return;
        }
        DebugRect.pool.freeAll(this.debugRects);
        this.debugRects.clear();
    }

    private void addDebugRect(float x, float y, float w, float h, Color color) {
        if (this.debugRects == null) {
            this.debugRects = new Array<>();
        }
        DebugRect rect = DebugRect.pool.obtain();
        rect.color = color;
        rect.set(x, (getHeight() - y) - h, w, h);
        this.debugRects.add(rect);
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public void drawDebug(ShapeRenderer shapes) {
        if (isTransform()) {
            applyTransform(shapes, computeTransform());
            drawDebugRects(shapes);
            if (this.clip) {
                shapes.flush();
                float x = 0.0f;
                float y = 0.0f;
                float width = getWidth();
                float height = getHeight();
                if (this.background != null) {
                    x = this.padLeft.get(this);
                    y = this.padBottom.get(this);
                    width -= this.padRight.get(this) + x;
                    height -= this.padTop.get(this) + y;
                }
                if (clipBegin(x, y, width, height)) {
                    drawDebugChildren(shapes);
                    clipEnd();
                }
            } else {
                drawDebugChildren(shapes);
            }
            resetTransform(shapes);
            return;
        }
        drawDebugRects(shapes);
        super.drawDebug(shapes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.badlogic.gdx.scenes.scene2d.Actor
    public void drawDebugBounds(ShapeRenderer shapes) {
    }

    private void drawDebugRects(ShapeRenderer shapes) {
        if (this.debugRects == null || !getDebug()) {
            return;
        }
        shapes.set(ShapeRenderer.ShapeType.Line);
        if (getStage() != null) {
            shapes.setColor(getStage().getDebugColor());
        }
        float x = 0.0f;
        float y = 0.0f;
        if (!isTransform()) {
            x = getX();
            y = getY();
        }
        int n = this.debugRects.size;
        for (int i = 0; i < n; i++) {
            DebugRect debugRect = this.debugRects.get(i);
            shapes.setColor(debugRect.color);
            shapes.rect(debugRect.x + x, debugRect.y + y, debugRect.width, debugRect.height);
        }
    }

    public Skin getSkin() {
        return this.skin;
    }
}
