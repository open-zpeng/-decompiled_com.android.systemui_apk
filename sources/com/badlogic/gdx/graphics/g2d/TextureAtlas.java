package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.StreamUtils;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes21.dex */
public class TextureAtlas implements Disposable {
    private final Array<AtlasRegion> regions;
    private final ObjectSet<Texture> textures;
    static final String[] tuple = new String[4];
    static final Comparator<TextureAtlasData.Region> indexComparator = new Comparator<TextureAtlasData.Region>() { // from class: com.badlogic.gdx.graphics.g2d.TextureAtlas.1
        @Override // java.util.Comparator
        public int compare(TextureAtlasData.Region region1, TextureAtlasData.Region region2) {
            int i1 = region1.index;
            if (i1 == -1) {
                i1 = Integer.MAX_VALUE;
            }
            int i2 = region2.index;
            if (i2 == -1) {
                i2 = Integer.MAX_VALUE;
            }
            return i1 - i2;
        }
    };

    /* loaded from: classes21.dex */
    public static class TextureAtlasData {
        final Array<Page> pages = new Array<>();
        final Array<Region> regions = new Array<>();

        /* loaded from: classes21.dex */
        public static class Region {
            public int degrees;
            public boolean flip;
            public int height;
            public int index;
            public int left;
            public String name;
            public float offsetX;
            public float offsetY;
            public int originalHeight;
            public int originalWidth;
            public int[] pads;
            public Page page;
            public boolean rotate;
            public int[] splits;
            public int top;
            public int width;
        }

        /* loaded from: classes21.dex */
        public static class Page {
            public final Pixmap.Format format;
            public final float height;
            public final Texture.TextureFilter magFilter;
            public final Texture.TextureFilter minFilter;
            public Texture texture;
            public final FileHandle textureFile;
            public final Texture.TextureWrap uWrap;
            public final boolean useMipMaps;
            public final Texture.TextureWrap vWrap;
            public final float width;

            public Page(FileHandle handle, float width, float height, boolean useMipMaps, Pixmap.Format format, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uWrap, Texture.TextureWrap vWrap) {
                this.width = width;
                this.height = height;
                this.textureFile = handle;
                this.useMipMaps = useMipMaps;
                this.format = format;
                this.minFilter = minFilter;
                this.magFilter = magFilter;
                this.uWrap = uWrap;
                this.vWrap = vWrap;
            }
        }

        public TextureAtlasData(FileHandle packFile, FileHandle imagesDir, boolean flip) {
            float width;
            float height;
            Texture.TextureWrap repeatY;
            int degrees;
            BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 64);
            Page pageImage = null;
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        if (line.trim().length() == 0) {
                            pageImage = null;
                        } else if (pageImage == null) {
                            try {
                                try {
                                    FileHandle file = imagesDir.child(line);
                                    if (TextureAtlas.readTuple(reader) != 2) {
                                        width = 0.0f;
                                        height = 0.0f;
                                    } else {
                                        width = Integer.parseInt(TextureAtlas.tuple[0]);
                                        float height2 = Integer.parseInt(TextureAtlas.tuple[1]);
                                        TextureAtlas.readTuple(reader);
                                        height = height2;
                                    }
                                    Pixmap.Format format = Pixmap.Format.valueOf(TextureAtlas.tuple[0]);
                                    TextureAtlas.readTuple(reader);
                                    Texture.TextureFilter min = Texture.TextureFilter.valueOf(TextureAtlas.tuple[0]);
                                    Texture.TextureFilter max = Texture.TextureFilter.valueOf(TextureAtlas.tuple[1]);
                                    String direction = TextureAtlas.readValue(reader);
                                    Texture.TextureWrap repeatX = Texture.TextureWrap.ClampToEdge;
                                    Texture.TextureWrap repeatY2 = Texture.TextureWrap.ClampToEdge;
                                    if (direction.equals("x")) {
                                        repeatX = Texture.TextureWrap.Repeat;
                                        repeatY = repeatY2;
                                    } else if (direction.equals("y")) {
                                        repeatY = Texture.TextureWrap.Repeat;
                                    } else if (!direction.equals("xy")) {
                                        repeatY = repeatY2;
                                    } else {
                                        repeatX = Texture.TextureWrap.Repeat;
                                        repeatY = Texture.TextureWrap.Repeat;
                                    }
                                    pageImage = new Page(file, width, height, min.isMipMap(), format, min, max, repeatX, repeatY);
                                    this.pages.add(pageImage);
                                } catch (Exception e) {
                                    ex = e;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Error reading pack file: ");
                                    try {
                                        sb.append(packFile);
                                        throw new GdxRuntimeException(sb.toString(), ex);
                                    } catch (Throwable th) {
                                        ex = th;
                                        StreamUtils.closeQuietly(reader);
                                        throw ex;
                                    }
                                }
                            } catch (Throwable th2) {
                                ex = th2;
                                StreamUtils.closeQuietly(reader);
                                throw ex;
                            }
                        } else {
                            String rotateValue = TextureAtlas.readValue(reader);
                            if (rotateValue.equalsIgnoreCase(OOBEEvent.STRING_TRUE)) {
                                degrees = 90;
                            } else if (rotateValue.equalsIgnoreCase(OOBEEvent.STRING_FALSE)) {
                                degrees = 0;
                            } else {
                                degrees = Integer.valueOf(rotateValue).intValue();
                            }
                            TextureAtlas.readTuple(reader);
                            int left = Integer.parseInt(TextureAtlas.tuple[0]);
                            int top = Integer.parseInt(TextureAtlas.tuple[1]);
                            TextureAtlas.readTuple(reader);
                            int width2 = Integer.parseInt(TextureAtlas.tuple[0]);
                            int height3 = Integer.parseInt(TextureAtlas.tuple[1]);
                            Region region = new Region();
                            region.page = pageImage;
                            region.left = left;
                            region.top = top;
                            region.width = width2;
                            region.height = height3;
                            region.name = line;
                            region.rotate = degrees == 90;
                            region.degrees = degrees;
                            if (TextureAtlas.readTuple(reader) == 4) {
                                region.splits = new int[]{Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3])};
                                if (TextureAtlas.readTuple(reader) == 4) {
                                    region.pads = new int[]{Integer.parseInt(TextureAtlas.tuple[0]), Integer.parseInt(TextureAtlas.tuple[1]), Integer.parseInt(TextureAtlas.tuple[2]), Integer.parseInt(TextureAtlas.tuple[3])};
                                    TextureAtlas.readTuple(reader);
                                }
                            }
                            region.originalWidth = Integer.parseInt(TextureAtlas.tuple[0]);
                            region.originalHeight = Integer.parseInt(TextureAtlas.tuple[1]);
                            TextureAtlas.readTuple(reader);
                            region.offsetX = Integer.parseInt(TextureAtlas.tuple[0]);
                            region.offsetY = Integer.parseInt(TextureAtlas.tuple[1]);
                            region.index = Integer.parseInt(TextureAtlas.readValue(reader));
                            if (flip) {
                                region.flip = true;
                            }
                            this.regions.add(region);
                        }
                    } else {
                        StreamUtils.closeQuietly(reader);
                        this.regions.sort(TextureAtlas.indexComparator);
                        return;
                    }
                } catch (Exception e2) {
                    ex = e2;
                } catch (Throwable th3) {
                    ex = th3;
                    StreamUtils.closeQuietly(reader);
                    throw ex;
                }
            }
        }

        public Array<Page> getPages() {
            return this.pages;
        }

        public Array<Region> getRegions() {
            return this.regions;
        }
    }

    public TextureAtlas() {
        this.textures = new ObjectSet<>(4);
        this.regions = new Array<>();
    }

    public TextureAtlas(String internalPackFile) {
        this(Gdx.files.internal(internalPackFile));
    }

    public TextureAtlas(FileHandle packFile) {
        this(packFile, packFile.parent());
    }

    public TextureAtlas(FileHandle packFile, boolean flip) {
        this(packFile, packFile.parent(), flip);
    }

    public TextureAtlas(FileHandle packFile, FileHandle imagesDir) {
        this(packFile, imagesDir, false);
    }

    public TextureAtlas(FileHandle packFile, FileHandle imagesDir, boolean flip) {
        this(new TextureAtlasData(packFile, imagesDir, flip));
    }

    public TextureAtlas(TextureAtlasData data) {
        this.textures = new ObjectSet<>(4);
        this.regions = new Array<>();
        if (data != null) {
            load(data);
        }
    }

    private void load(TextureAtlasData data) {
        Texture texture;
        ObjectMap<TextureAtlasData.Page, Texture> pageToTexture = new ObjectMap<>();
        Iterator<TextureAtlasData.Page> it = data.pages.iterator();
        while (it.hasNext()) {
            TextureAtlasData.Page page = it.next();
            if (page.texture == null) {
                texture = new Texture(page.textureFile, page.format, page.useMipMaps);
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            } else {
                texture = page.texture;
                texture.setFilter(page.minFilter, page.magFilter);
                texture.setWrap(page.uWrap, page.vWrap);
            }
            this.textures.add(texture);
            pageToTexture.put(page, texture);
        }
        Iterator<TextureAtlasData.Region> it2 = data.regions.iterator();
        while (it2.hasNext()) {
            TextureAtlasData.Region region = it2.next();
            int width = region.width;
            int height = region.height;
            AtlasRegion atlasRegion = new AtlasRegion(pageToTexture.get(region.page), region.left, region.top, region.rotate ? height : width, region.rotate ? width : height);
            atlasRegion.index = region.index;
            atlasRegion.name = region.name;
            atlasRegion.offsetX = region.offsetX;
            atlasRegion.offsetY = region.offsetY;
            atlasRegion.originalHeight = region.originalHeight;
            atlasRegion.originalWidth = region.originalWidth;
            atlasRegion.rotate = region.rotate;
            atlasRegion.degrees = region.degrees;
            atlasRegion.splits = region.splits;
            atlasRegion.pads = region.pads;
            if (region.flip) {
                atlasRegion.flip(false, true);
            }
            this.regions.add(atlasRegion);
        }
    }

    public AtlasRegion addRegion(String name, Texture texture, int x, int y, int width, int height) {
        this.textures.add(texture);
        AtlasRegion region = new AtlasRegion(texture, x, y, width, height);
        region.name = name;
        region.index = -1;
        this.regions.add(region);
        return region;
    }

    public AtlasRegion addRegion(String name, TextureRegion textureRegion) {
        this.textures.add(textureRegion.texture);
        AtlasRegion region = new AtlasRegion(textureRegion);
        region.name = name;
        region.index = -1;
        this.regions.add(region);
        return region;
    }

    public Array<AtlasRegion> getRegions() {
        return this.regions;
    }

    public AtlasRegion findRegion(String name) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            if (this.regions.get(i).name.equals(name)) {
                return this.regions.get(i);
            }
        }
        return null;
    }

    public AtlasRegion findRegion(String name, int index) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name) && region.index == index) {
                return region;
            }
        }
        return null;
    }

    public Array<AtlasRegion> findRegions(String name) {
        Array<AtlasRegion> matched = new Array<>(AtlasRegion.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(new AtlasRegion(region));
            }
        }
        return matched;
    }

    public Array<Sprite> createSprites() {
        Array sprites = new Array(true, this.regions.size, Sprite.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            sprites.add(newSprite(this.regions.get(i)));
        }
        return sprites;
    }

    public Sprite createSprite(String name) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            if (this.regions.get(i).name.equals(name)) {
                return newSprite(this.regions.get(i));
            }
        }
        return null;
    }

    public Sprite createSprite(String name, int index) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name) && region.index == index) {
                return newSprite(this.regions.get(i));
            }
        }
        return null;
    }

    public Array<Sprite> createSprites(String name) {
        Array<Sprite> matched = new Array<>(Sprite.class);
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                matched.add(newSprite(region));
            }
        }
        return matched;
    }

    private Sprite newSprite(AtlasRegion region) {
        if (region.packedWidth == region.originalWidth && region.packedHeight == region.originalHeight) {
            if (region.rotate) {
                Sprite sprite = new Sprite(region);
                sprite.setBounds(0.0f, 0.0f, region.getRegionHeight(), region.getRegionWidth());
                sprite.rotate90(true);
                return sprite;
            }
            return new Sprite(region);
        }
        return new AtlasSprite(region);
    }

    public NinePatch createPatch(String name) {
        int n = this.regions.size;
        for (int i = 0; i < n; i++) {
            AtlasRegion region = this.regions.get(i);
            if (region.name.equals(name)) {
                int[] splits = region.splits;
                if (splits == null) {
                    throw new IllegalArgumentException("Region does not have ninepatch splits: " + name);
                }
                NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
                if (region.pads != null) {
                    patch.setPadding(region.pads[0], region.pads[1], region.pads[2], region.pads[3]);
                }
                return patch;
            }
        }
        return null;
    }

    public ObjectSet<Texture> getTextures() {
        return this.textures;
    }

    @Override // com.badlogic.gdx.utils.Disposable
    public void dispose() {
        ObjectSet.ObjectSetIterator<Texture> it = this.textures.iterator();
        while (it.hasNext()) {
            Texture texture = it.next();
            texture.dispose();
        }
        this.textures.clear(0);
    }

    static String readValue(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        int colon = line.indexOf(58);
        if (colon == -1) {
            throw new GdxRuntimeException("Invalid line: " + line);
        }
        return line.substring(colon + 1).trim();
    }

    static int readTuple(BufferedReader reader) throws IOException {
        int comma;
        String line = reader.readLine();
        int colon = line.indexOf(58);
        if (colon == -1) {
            throw new GdxRuntimeException("Invalid line: " + line);
        }
        int lastMatch = colon + 1;
        int i = 0;
        while (i < 3 && (comma = line.indexOf(44, lastMatch)) != -1) {
            tuple[i] = line.substring(lastMatch, comma).trim();
            lastMatch = comma + 1;
            i++;
        }
        tuple[i] = line.substring(lastMatch).trim();
        return i + 1;
    }

    /* loaded from: classes21.dex */
    public static class AtlasRegion extends TextureRegion {
        public int degrees;
        public int index;
        public String name;
        public float offsetX;
        public float offsetY;
        public int originalHeight;
        public int originalWidth;
        public int packedHeight;
        public int packedWidth;
        public int[] pads;
        public boolean rotate;
        public int[] splits;

        public AtlasRegion(Texture texture, int x, int y, int width, int height) {
            super(texture, x, y, width, height);
            this.originalWidth = width;
            this.originalHeight = height;
            this.packedWidth = width;
            this.packedHeight = height;
        }

        public AtlasRegion(AtlasRegion region) {
            setRegion(region);
            this.index = region.index;
            this.name = region.name;
            this.offsetX = region.offsetX;
            this.offsetY = region.offsetY;
            this.packedWidth = region.packedWidth;
            this.packedHeight = region.packedHeight;
            this.originalWidth = region.originalWidth;
            this.originalHeight = region.originalHeight;
            this.rotate = region.rotate;
            this.degrees = region.degrees;
            this.splits = region.splits;
        }

        public AtlasRegion(TextureRegion region) {
            setRegion(region);
            this.packedWidth = region.getRegionWidth();
            this.packedHeight = region.getRegionHeight();
            this.originalWidth = this.packedWidth;
            this.originalHeight = this.packedHeight;
        }

        @Override // com.badlogic.gdx.graphics.g2d.TextureRegion
        public void flip(boolean x, boolean y) {
            super.flip(x, y);
            if (x) {
                this.offsetX = (this.originalWidth - this.offsetX) - getRotatedPackedWidth();
            }
            if (y) {
                this.offsetY = (this.originalHeight - this.offsetY) - getRotatedPackedHeight();
            }
        }

        public float getRotatedPackedWidth() {
            return this.rotate ? this.packedHeight : this.packedWidth;
        }

        public float getRotatedPackedHeight() {
            return this.rotate ? this.packedWidth : this.packedHeight;
        }

        public String toString() {
            return this.name;
        }
    }

    /* loaded from: classes21.dex */
    public static class AtlasSprite extends Sprite {
        float originalOffsetX;
        float originalOffsetY;
        final AtlasRegion region;

        public AtlasSprite(AtlasRegion region) {
            this.region = new AtlasRegion(region);
            this.originalOffsetX = region.offsetX;
            this.originalOffsetY = region.offsetY;
            setRegion(region);
            setOrigin(region.originalWidth / 2.0f, region.originalHeight / 2.0f);
            int width = region.getRegionWidth();
            int height = region.getRegionHeight();
            if (region.rotate) {
                super.rotate90(true);
                super.setBounds(region.offsetX, region.offsetY, height, width);
            } else {
                super.setBounds(region.offsetX, region.offsetY, width, height);
            }
            setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public AtlasSprite(AtlasSprite sprite) {
            this.region = sprite.region;
            this.originalOffsetX = sprite.originalOffsetX;
            this.originalOffsetY = sprite.originalOffsetY;
            set(sprite);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setPosition(float x, float y) {
            super.setPosition(this.region.offsetX + x, this.region.offsetY + y);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setX(float x) {
            super.setX(this.region.offsetX + x);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setY(float y) {
            super.setY(this.region.offsetY + y);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setBounds(float x, float y, float width, float height) {
            float widthRatio = width / this.region.originalWidth;
            float heightRatio = height / this.region.originalHeight;
            AtlasRegion atlasRegion = this.region;
            atlasRegion.offsetX = this.originalOffsetX * widthRatio;
            atlasRegion.offsetY = this.originalOffsetY * heightRatio;
            int packedWidth = atlasRegion.rotate ? this.region.packedHeight : this.region.packedWidth;
            int packedHeight = this.region.rotate ? this.region.packedWidth : this.region.packedHeight;
            super.setBounds(this.region.offsetX + x, this.region.offsetY + y, packedWidth * widthRatio, packedHeight * heightRatio);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setSize(float width, float height) {
            setBounds(getX(), getY(), width, height);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setOrigin(float originX, float originY) {
            super.setOrigin(originX - this.region.offsetX, originY - this.region.offsetY);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void setOriginCenter() {
            super.setOrigin((this.width / 2.0f) - this.region.offsetX, (this.height / 2.0f) - this.region.offsetY);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite, com.badlogic.gdx.graphics.g2d.TextureRegion
        public void flip(boolean x, boolean y) {
            if (this.region.rotate) {
                super.flip(y, x);
            } else {
                super.flip(x, y);
            }
            float oldOriginX = getOriginX();
            float oldOriginY = getOriginY();
            float oldOffsetX = this.region.offsetX;
            float oldOffsetY = this.region.offsetY;
            float widthRatio = getWidthRatio();
            float heightRatio = getHeightRatio();
            AtlasRegion atlasRegion = this.region;
            atlasRegion.offsetX = this.originalOffsetX;
            atlasRegion.offsetY = this.originalOffsetY;
            atlasRegion.flip(x, y);
            this.originalOffsetX = this.region.offsetX;
            this.originalOffsetY = this.region.offsetY;
            this.region.offsetX *= widthRatio;
            this.region.offsetY *= heightRatio;
            translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            setOrigin(oldOriginX, oldOriginY);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public void rotate90(boolean clockwise) {
            super.rotate90(clockwise);
            float oldOriginX = getOriginX();
            float oldOriginY = getOriginY();
            float oldOffsetX = this.region.offsetX;
            float oldOffsetY = this.region.offsetY;
            float widthRatio = getWidthRatio();
            float heightRatio = getHeightRatio();
            if (clockwise) {
                AtlasRegion atlasRegion = this.region;
                atlasRegion.offsetX = oldOffsetY;
                atlasRegion.offsetY = ((atlasRegion.originalHeight * heightRatio) - oldOffsetX) - (this.region.packedWidth * widthRatio);
            } else {
                AtlasRegion atlasRegion2 = this.region;
                atlasRegion2.offsetX = ((atlasRegion2.originalWidth * widthRatio) - oldOffsetY) - (this.region.packedHeight * heightRatio);
                this.region.offsetY = oldOffsetX;
            }
            translate(this.region.offsetX - oldOffsetX, this.region.offsetY - oldOffsetY);
            setOrigin(oldOriginX, oldOriginY);
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getX() {
            return super.getX() - this.region.offsetX;
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getY() {
            return super.getY() - this.region.offsetY;
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getOriginX() {
            return super.getOriginX() + this.region.offsetX;
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getOriginY() {
            return super.getOriginY() + this.region.offsetY;
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getWidth() {
            return (super.getWidth() / this.region.getRotatedPackedWidth()) * this.region.originalWidth;
        }

        @Override // com.badlogic.gdx.graphics.g2d.Sprite
        public float getHeight() {
            return (super.getHeight() / this.region.getRotatedPackedHeight()) * this.region.originalHeight;
        }

        public float getWidthRatio() {
            return super.getWidth() / this.region.getRotatedPackedWidth();
        }

        public float getHeightRatio() {
            return super.getHeight() / this.region.getRotatedPackedHeight();
        }

        public AtlasRegion getAtlasRegion() {
            return this.region;
        }

        public String toString() {
            return this.region.toString();
        }
    }
}
