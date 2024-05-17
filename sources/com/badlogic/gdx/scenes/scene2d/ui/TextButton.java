package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
/* loaded from: classes21.dex */
public class TextButton extends Button {
    private Label label;
    private TextButtonStyle style;

    public TextButton(String text, Skin skin) {
        this(text, (TextButtonStyle) skin.get(TextButtonStyle.class));
        setSkin(skin);
    }

    public TextButton(String text, Skin skin, String styleName) {
        this(text, (TextButtonStyle) skin.get(styleName, TextButtonStyle.class));
        setSkin(skin);
    }

    public TextButton(String text, TextButtonStyle style) {
        setStyle(style);
        this.style = style;
        this.label = new Label(text, new Label.LabelStyle(style.font, style.fontColor));
        this.label.setAlignment(1);
        add((TextButton) this.label).expand().fill();
        setSize(getPrefWidth(), getPrefHeight());
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Button
    public void setStyle(Button.ButtonStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        if (!(style instanceof TextButtonStyle)) {
            throw new IllegalArgumentException("style must be a TextButtonStyle.");
        }
        super.setStyle(style);
        this.style = (TextButtonStyle) style;
        Label label = this.label;
        if (label != null) {
            TextButtonStyle textButtonStyle = (TextButtonStyle) style;
            Label.LabelStyle labelStyle = label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = textButtonStyle.fontColor;
            this.label.setStyle(labelStyle);
        }
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Button
    public TextButtonStyle getStyle() {
        return this.style;
    }

    @Override // com.badlogic.gdx.scenes.scene2d.ui.Button, com.badlogic.gdx.scenes.scene2d.ui.Table, com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup, com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public void draw(Batch batch, float parentAlpha) {
        Color fontColor;
        if (isDisabled() && this.style.disabledFontColor != null) {
            fontColor = this.style.disabledFontColor;
        } else if (isPressed() && this.style.downFontColor != null) {
            fontColor = this.style.downFontColor;
        } else if (this.isChecked && this.style.checkedFontColor != null) {
            fontColor = (!isOver() || this.style.checkedOverFontColor == null) ? this.style.checkedFontColor : this.style.checkedOverFontColor;
        } else if (isOver() && this.style.overFontColor != null) {
            fontColor = this.style.overFontColor;
        } else {
            fontColor = this.style.fontColor;
        }
        if (fontColor != null) {
            this.label.getStyle().fontColor = fontColor;
        }
        super.draw(batch, parentAlpha);
    }

    public void setLabel(Label label) {
        getLabelCell().setActor(label);
        this.label = label;
    }

    public Label getLabel() {
        return this.label;
    }

    public Cell<Label> getLabelCell() {
        return getCell(this.label);
    }

    public void setText(String text) {
        this.label.setText(text);
    }

    public CharSequence getText() {
        return this.label.getText();
    }

    @Override // com.badlogic.gdx.scenes.scene2d.Group, com.badlogic.gdx.scenes.scene2d.Actor
    public String toString() {
        String name = getName();
        if (name != null) {
            return name;
        }
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf(46);
        if (dotIndex != -1) {
            className = className.substring(dotIndex + 1);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(className.indexOf(36) != -1 ? "TextButton " : "");
        sb.append(className);
        sb.append(": ");
        sb.append((Object) this.label.getText());
        return sb.toString();
    }

    /* loaded from: classes21.dex */
    public static class TextButtonStyle extends Button.ButtonStyle {
        public Color checkedFontColor;
        public Color checkedOverFontColor;
        public Color disabledFontColor;
        public Color downFontColor;
        public BitmapFont font;
        public Color fontColor;
        public Color overFontColor;

        public TextButtonStyle() {
        }

        public TextButtonStyle(Drawable up, Drawable down, Drawable checked, BitmapFont font) {
            super(up, down, checked);
            this.font = font;
        }

        public TextButtonStyle(TextButtonStyle style) {
            super(style);
            this.font = style.font;
            Color color = style.fontColor;
            if (color != null) {
                this.fontColor = new Color(color);
            }
            Color color2 = style.downFontColor;
            if (color2 != null) {
                this.downFontColor = new Color(color2);
            }
            Color color3 = style.overFontColor;
            if (color3 != null) {
                this.overFontColor = new Color(color3);
            }
            Color color4 = style.checkedFontColor;
            if (color4 != null) {
                this.checkedFontColor = new Color(color4);
            }
            Color color5 = style.checkedOverFontColor;
            if (color5 != null) {
                this.checkedOverFontColor = new Color(color5);
            }
            Color color6 = style.disabledFontColor;
            if (color6 != null) {
                this.disabledFontColor = new Color(color6);
            }
        }
    }
}
