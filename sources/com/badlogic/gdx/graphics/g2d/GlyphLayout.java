package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
/* loaded from: classes21.dex */
public class GlyphLayout implements Pool.Poolable {
    public float height;
    public float width;
    public final Array<GlyphRun> runs = new Array<>();
    private final Array<Color> colorStack = new Array<>(4);

    public GlyphLayout() {
    }

    public GlyphLayout(BitmapFont font, CharSequence str) {
        setText(font, str);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        setText(font, str, color, targetWidth, halign, wrap);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, int start, int end, Color color, float targetWidth, int halign, boolean wrap, String truncate) {
        setText(font, str, start, end, color, targetWidth, halign, wrap, truncate);
    }

    public void setText(BitmapFont font, CharSequence str) {
        setText(font, str, 0, str.length(), font.getColor(), 0.0f, 8, false, null);
    }

    public void setText(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        setText(font, str, 0, str.length(), color, targetWidth, halign, wrap, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:139:0x043f  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x045a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setText(com.badlogic.gdx.graphics.g2d.BitmapFont r36, java.lang.CharSequence r37, int r38, int r39, com.badlogic.gdx.graphics.Color r40, float r41, int r42, boolean r43, java.lang.String r44) {
        /*
            Method dump skipped, instructions count: 1159
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g2d.GlyphLayout.setText(com.badlogic.gdx.graphics.g2d.BitmapFont, java.lang.CharSequence, int, int, com.badlogic.gdx.graphics.Color, float, int, boolean, java.lang.String):void");
    }

    private void truncate(BitmapFont.BitmapFontData fontData, GlyphRun run, float targetWidth, String truncate, int widthIndex, Pool<GlyphRun> glyphRunPool) {
        GlyphRun truncateRun = glyphRunPool.obtain();
        fontData.getGlyphs(truncateRun, truncate, 0, truncate.length(), null);
        float truncateWidth = 0.0f;
        if (truncateRun.xAdvances.size > 0) {
            adjustLastGlyph(fontData, truncateRun);
            int n = truncateRun.xAdvances.size;
            for (int i = 1; i < n; i++) {
                truncateWidth += truncateRun.xAdvances.get(i);
            }
        }
        float targetWidth2 = targetWidth - truncateWidth;
        int count = 0;
        float width = run.x;
        while (true) {
            if (count >= run.xAdvances.size) {
                break;
            }
            float xAdvance = run.xAdvances.get(count);
            width += xAdvance;
            if (width > targetWidth2) {
                run.width = (width - run.x) - xAdvance;
                break;
            }
            count++;
        }
        if (count > 1) {
            run.glyphs.truncate(count - 1);
            run.xAdvances.truncate(count);
            adjustLastGlyph(fontData, run);
            if (truncateRun.xAdvances.size > 0) {
                run.xAdvances.addAll(truncateRun.xAdvances, 1, truncateRun.xAdvances.size - 1);
            }
        } else {
            run.glyphs.clear();
            run.xAdvances.clear();
            run.xAdvances.addAll(truncateRun.xAdvances);
            if (truncateRun.xAdvances.size > 0) {
                run.width += truncateRun.xAdvances.get(0);
            }
        }
        run.glyphs.addAll(truncateRun.glyphs);
        run.width += truncateWidth;
        glyphRunPool.free(truncateRun);
    }

    private GlyphRun wrap(BitmapFont.BitmapFontData fontData, GlyphRun first, Pool<GlyphRun> glyphRunPool, int wrapIndex, int widthIndex) {
        Array<BitmapFont.Glyph> glyphs2 = first.glyphs;
        int glyphCount = first.glyphs.size;
        FloatArray xAdvances2 = first.xAdvances;
        int firstEnd = wrapIndex;
        while (firstEnd > 0 && fontData.isWhitespace((char) glyphs2.get(firstEnd - 1).id)) {
            firstEnd--;
        }
        int secondStart = wrapIndex;
        while (secondStart < glyphCount && fontData.isWhitespace((char) glyphs2.get(secondStart).id)) {
            secondStart++;
        }
        int widthIndex2 = widthIndex;
        while (widthIndex2 < firstEnd) {
            first.width += xAdvances2.get(widthIndex2);
            widthIndex2++;
        }
        int n = firstEnd + 1;
        while (widthIndex2 > n) {
            widthIndex2--;
            first.width -= xAdvances2.get(widthIndex2);
        }
        GlyphRun second = null;
        if (secondStart < glyphCount) {
            GlyphRun second2 = glyphRunPool.obtain();
            second = second2;
            second.color.set(first.color);
            Array<BitmapFont.Glyph> glyphs1 = second.glyphs;
            glyphs1.addAll(glyphs2, 0, firstEnd);
            glyphs2.removeRange(0, secondStart - 1);
            first.glyphs = glyphs1;
            second.glyphs = glyphs2;
            FloatArray xAdvances1 = second.xAdvances;
            xAdvances1.addAll(xAdvances2, 0, firstEnd + 1);
            xAdvances2.removeRange(1, secondStart);
            xAdvances2.set(0, ((-glyphs2.first().xoffset) * fontData.scaleX) - fontData.padLeft);
            first.xAdvances = xAdvances1;
            second.xAdvances = xAdvances2;
        } else {
            glyphs2.truncate(firstEnd);
            xAdvances2.truncate(firstEnd + 1);
        }
        if (firstEnd == 0) {
            glyphRunPool.free(first);
            this.runs.pop();
        } else {
            adjustLastGlyph(fontData, first);
        }
        return second;
    }

    private void adjustLastGlyph(BitmapFont.BitmapFontData fontData, GlyphRun run) {
        BitmapFont.Glyph last = run.glyphs.peek();
        if (last.fixedWidth) {
            return;
        }
        float width = ((last.width + last.xoffset) * fontData.scaleX) - fontData.padRight;
        run.width += width - run.xAdvances.peek();
        run.xAdvances.set(run.xAdvances.size - 1, width);
    }

    private int parseColorMarkup(CharSequence str, int start, int end, Pool<Color> colorPool) {
        int i;
        int i2;
        if (start == end) {
            return -1;
        }
        char charAt = str.charAt(start);
        if (charAt != '#') {
            if (charAt != '[') {
                if (charAt == ']') {
                    if (this.colorStack.size > 1) {
                        colorPool.free(this.colorStack.pop());
                        return 0;
                    }
                    return 0;
                }
                for (int i3 = start + 1; i3 < end; i3++) {
                    if (str.charAt(i3) == ']') {
                        Color namedColor = Colors.get(str.subSequence(start, i3).toString());
                        if (namedColor == null) {
                            return -1;
                        }
                        Color color = colorPool.obtain();
                        this.colorStack.add(color);
                        color.set(namedColor);
                        return i3 - start;
                    }
                }
                return -1;
            }
            return -2;
        }
        int colorInt = 0;
        int i4 = start + 1;
        while (true) {
            if (i4 >= end) {
                break;
            }
            char ch = str.charAt(i4);
            if (ch == ']') {
                if (i4 >= start + 2 && i4 <= start + 9) {
                    if (i4 - start <= 7) {
                        int nn = 9 - (i4 - start);
                        for (int ii = 0; ii < nn; ii++) {
                            colorInt <<= 4;
                        }
                        colorInt |= 255;
                    }
                    Color color2 = colorPool.obtain();
                    this.colorStack.add(color2);
                    Color.rgba8888ToColor(color2, colorInt);
                    return i4 - start;
                }
            } else {
                if (ch >= '0' && ch <= '9') {
                    i = colorInt * 16;
                    i2 = ch - '0';
                } else if (ch >= 'a' && ch <= 'f') {
                    i = colorInt * 16;
                    i2 = ch - 'W';
                } else if (ch < 'A' || ch > 'F') {
                    break;
                } else {
                    i = colorInt * 16;
                    i2 = ch - '7';
                }
                colorInt = i + i2;
                i4++;
            }
        }
        return -1;
    }

    @Override // com.badlogic.gdx.utils.Pool.Poolable
    public void reset() {
        Pools.get(GlyphRun.class).freeAll(this.runs);
        this.runs.clear();
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public String toString() {
        if (this.runs.size == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(this.width);
        buffer.append('x');
        buffer.append(this.height);
        buffer.append('\n');
        int n = this.runs.size;
        for (int i = 0; i < n; i++) {
            buffer.append(this.runs.get(i).toString());
            buffer.append('\n');
        }
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }

    /* loaded from: classes21.dex */
    public static class GlyphRun implements Pool.Poolable {
        public float width;
        public float x;
        public float y;
        public Array<BitmapFont.Glyph> glyphs = new Array<>();
        public FloatArray xAdvances = new FloatArray();
        public final Color color = new Color();

        @Override // com.badlogic.gdx.utils.Pool.Poolable
        public void reset() {
            this.glyphs.clear();
            this.xAdvances.clear();
            this.width = 0.0f;
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder(this.glyphs.size);
            Array<BitmapFont.Glyph> glyphs = this.glyphs;
            int n = glyphs.size;
            for (int i = 0; i < n; i++) {
                BitmapFont.Glyph g = glyphs.get(i);
                buffer.append((char) g.id);
            }
            buffer.append(", #");
            buffer.append(this.color);
            buffer.append(", ");
            buffer.append(this.x);
            buffer.append(", ");
            buffer.append(this.y);
            buffer.append(", ");
            buffer.append(this.width);
            return buffer.toString();
        }
    }
}
