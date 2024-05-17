package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
/* loaded from: classes21.dex */
public class Button extends Table implements Disableable {
    ButtonGroup buttonGroup;
    private ClickListener clickListener;
    boolean focused;
    boolean isChecked;
    boolean isDisabled;
    private boolean programmaticChangeEvents;
    private ButtonStyle style;

    public Button(Skin skin) {
        super(skin);
        this.programmaticChangeEvents = true;
        initialize();
        setStyle((ButtonStyle) skin.get(ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(Skin skin, String styleName) {
        super(skin);
        this.programmaticChangeEvents = true;
        initialize();
        setStyle((ButtonStyle) skin.get(styleName, ButtonStyle.class));
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(Actor child, Skin skin, String styleName) {
        this(child, (ButtonStyle) skin.get(styleName, ButtonStyle.class));
        setSkin(skin);
    }

    public Button(Actor child, ButtonStyle style) {
        this.programmaticChangeEvents = true;
        initialize();
        add((Button) child);
        setStyle(style);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button(ButtonStyle style) {
        this.programmaticChangeEvents = true;
        initialize();
        setStyle(style);
        setSize(getPrefWidth(), getPrefHeight());
    }

    public Button() {
        this.programmaticChangeEvents = true;
        initialize();
    }

    private void initialize() {
        setTouchable(Touchable.enabled);
        ClickListener clickListener = new ClickListener() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Button.1
            @Override // com.badlogic.gdx.scenes.scene2d.utils.ClickListener
            public void clicked(InputEvent event, float x, float y) {
                if (Button.this.isDisabled()) {
                    return;
                }
                Button button = Button.this;
                button.setChecked(!button.isChecked, true);
            }
        };
        this.clickListener = clickListener;
        addListener(clickListener);
        addListener(new FocusListener() { // from class: com.badlogic.gdx.scenes.scene2d.ui.Button.2
            @Override // com.badlogic.gdx.scenes.scene2d.utils.FocusListener
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                Button.this.focused = focused;
            }
        });
    }

    public Button(Drawable up) {
        this(new ButtonStyle(up, null, null));
    }

    public Button(Drawable up, Drawable down) {
        this(new ButtonStyle(up, down, null));
    }

    public Button(Drawable up, Drawable down, Drawable checked) {
        this(new ButtonStyle(up, down, checked));
    }

    public Button(Actor child, Skin skin) {
        this(child, (ButtonStyle) skin.get(ButtonStyle.class));
    }

    public void setChecked(boolean isChecked) {
        setChecked(isChecked, this.programmaticChangeEvents);
    }

    void setChecked(boolean isChecked, boolean fireEvent) {
        if (this.isChecked == isChecked) {
            return;
        }
        ButtonGroup buttonGroup = this.buttonGroup;
        if (buttonGroup == null || buttonGroup.canCheck(this, isChecked)) {
            this.isChecked = isChecked;
            if (fireEvent) {
                ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
                if (fire(changeEvent)) {
                    this.isChecked = !isChecked;
                }
                Pools.free(changeEvent);
            }
        }
    }

    public void toggle() {
        setChecked(!this.isChecked);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isPressed() {
        return this.clickListener.isVisualPressed();
    }

    public boolean isOver() {
        return this.clickListener.isOver();
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.utils.Disableable
    public boolean isDisabled() {
        return this.isDisabled;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.utils.Disableable
    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents) {
        this.programmaticChangeEvents = programmaticChangeEvents;
    }

    public void setStyle(ButtonStyle style) {
        Drawable background;
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null.");
        }
        this.style = style;
        if (isPressed() && !isDisabled()) {
            background = style.down == null ? style.up : style.down;
        } else if (isDisabled() && style.disabled != null) {
            background = style.disabled;
        } else if (this.isChecked && style.checked != null) {
            if (isOver() && style.checkedOver != null) {
                background = style.checkedOver;
            } else if (this.focused && style.checkedFocused != null) {
                background = style.checkedFocused;
            } else {
                background = style.checked;
            }
        } else if (isOver() && style.over != null) {
            background = style.over;
        } else if (this.focused && style.focused != null) {
            background = style.focused;
        } else {
            background = style.up;
        }
        setBackground(background);
    }

    public ButtonStyle getStyle() {
        return this.style;
    }

    public ButtonGroup getButtonGroup() {
        return this.buttonGroup;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public void draw(Batch batch, float parentAlpha) {
        float offsetX;
        float offsetY;
        validate();
        boolean isDisabled = isDisabled();
        boolean isPressed = isPressed();
        boolean isChecked = isChecked();
        boolean isOver = isOver();
        Drawable background = null;
        if (isDisabled && this.style.disabled != null) {
            background = this.style.disabled;
        } else if (isPressed && this.style.down != null) {
            background = this.style.down;
        } else if (isChecked && this.style.checked != null) {
            if (this.style.checkedOver != null && isOver) {
                background = this.style.checkedOver;
            } else if (this.style.checkedFocused != null && this.focused) {
                background = this.style.checkedFocused;
            } else {
                background = this.style.checked;
            }
        } else if (isOver && this.style.over != null) {
            background = this.style.over;
        } else if (this.focused && this.style.focused != null) {
            background = this.style.focused;
        } else if (this.style.up != null) {
            background = this.style.up;
        }
        setBackground(background);
        if (isPressed && !isDisabled) {
            offsetX = this.style.pressedOffsetX;
            offsetY = this.style.pressedOffsetY;
        } else if (isChecked && !isDisabled) {
            offsetX = this.style.checkedOffsetX;
            offsetY = this.style.checkedOffsetY;
        } else {
            offsetX = this.style.unpressedOffsetX;
            offsetY = this.style.unpressedOffsetY;
        }
        Array<Actor> children = getChildren();
        for (int i = 0; i < children.size; i++) {
            children.get(i).moveBy(offsetX, offsetY);
        }
        super.draw(batch, parentAlpha);
        for (int i2 = 0; i2 < children.size; i2++) {
            children.get(i2).moveBy(-offsetX, -offsetY);
        }
        Stage stage = getStage();
        if (stage != null && stage.getActionsRequestRendering() && isPressed != this.clickListener.isPressed()) {
            Gdx.graphics.requestRendering();
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefWidth() {
        float width = super.getPrefWidth();
        if (this.style.up != null) {
            width = Math.max(width, this.style.up.getMinWidth());
        }
        if (this.style.down != null) {
            width = Math.max(width, this.style.down.getMinWidth());
        }
        return this.style.checked != null ? Math.max(width, this.style.checked.getMinWidth()) : width;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getPrefHeight() {
        float height = super.getPrefHeight();
        if (this.style.up != null) {
            height = Math.max(height, this.style.up.getMinHeight());
        }
        if (this.style.down != null) {
            height = Math.max(height, this.style.down.getMinHeight());
        }
        return this.style.checked != null ? Math.max(height, this.style.checked.getMinHeight()) : height;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getMinWidth() {
        return getPrefWidth();
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.utils.Layout
    public float getMinHeight() {
        return getPrefHeight();
    }

    /* loaded from: classes21.dex */
    public static class ButtonStyle {
        public Drawable checked;
        public Drawable checkedFocused;
        public float checkedOffsetX;
        public float checkedOffsetY;
        public Drawable checkedOver;
        public Drawable disabled;
        public Drawable down;
        public Drawable focused;
        public Drawable over;
        public float pressedOffsetX;
        public float pressedOffsetY;
        public float unpressedOffsetX;
        public float unpressedOffsetY;
        public Drawable up;

        public ButtonStyle() {
        }

        public ButtonStyle(Drawable up, Drawable down, Drawable checked) {
            this.up = up;
            this.down = down;
            this.checked = checked;
        }

        public ButtonStyle(ButtonStyle style) {
            this.up = style.up;
            this.down = style.down;
            this.over = style.over;
            this.focused = style.focused;
            this.checked = style.checked;
            this.checkedOver = style.checkedOver;
            this.checkedFocused = style.checkedFocused;
            this.disabled = style.disabled;
            this.pressedOffsetX = style.pressedOffsetX;
            this.pressedOffsetY = style.pressedOffsetY;
            this.unpressedOffsetX = style.unpressedOffsetX;
            this.unpressedOffsetY = style.unpressedOffsetY;
            this.checkedOffsetX = style.checkedOffsetX;
            this.checkedOffsetY = style.checkedOffsetY;
        }
    }
}
