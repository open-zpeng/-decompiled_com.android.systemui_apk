package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.utils.ObjectMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Matcher;
/* loaded from: classes21.dex */
public class PixmapPackerIO {

    /* loaded from: classes21.dex */
    public static class SaveParameters {
        public boolean useIndexes;
        public ImageFormat format = ImageFormat.PNG;
        public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest;
        public Texture.TextureFilter magFilter = Texture.TextureFilter.Nearest;
    }

    /* loaded from: classes21.dex */
    public enum ImageFormat {
        CIM(".cim"),
        PNG(".png");
        
        private final String extension;

        public String getExtension() {
            return this.extension;
        }

        ImageFormat(String extension) {
            this.extension = extension;
        }
    }

    public void save(FileHandle file, PixmapPacker packer) throws IOException {
        save(file, packer, new SaveParameters());
    }

    public void save(FileHandle file, PixmapPacker packer, SaveParameters parameters) throws IOException {
        String imageName;
        int i;
        boolean z;
        char c;
        FileHandle fileHandle = file;
        PixmapPacker pixmapPacker = packer;
        boolean z2 = false;
        Writer writer = fileHandle.writer(false);
        int index = 0;
        Iterator<PixmapPacker.Page> it = pixmapPacker.pages.iterator();
        while (it.hasNext()) {
            PixmapPacker.Page page = it.next();
            if (page.rects.size > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(file.nameWithoutExtension());
                sb.append("_");
                index++;
                sb.append(index);
                sb.append(parameters.format.getExtension());
                FileHandle pageFile = fileHandle.sibling(sb.toString());
                int i2 = AnonymousClass1.$SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[parameters.format.ordinal()];
                int i3 = 1;
                if (i2 == 1) {
                    PixmapIO.writeCIM(pageFile, page.image);
                } else if (i2 == 2) {
                    PixmapIO.writePNG(pageFile, page.image);
                }
                writer.write("\n");
                writer.write(pageFile.name() + "\n");
                writer.write("size: " + page.image.getWidth() + "," + page.image.getHeight() + "\n");
                StringBuilder sb2 = new StringBuilder();
                sb2.append("format: ");
                sb2.append(pixmapPacker.pageFormat.name());
                sb2.append("\n");
                writer.write(sb2.toString());
                writer.write("filter: " + parameters.minFilter.name() + "," + parameters.magFilter.name() + "\n");
                writer.write("repeat: none\n");
                ObjectMap.Keys<String> it2 = page.rects.keys().iterator();
                while (it2.hasNext()) {
                    String name = it2.next();
                    int imageIndex = -1;
                    if (parameters.useIndexes) {
                        imageName = name;
                        Matcher matcher = PixmapPacker.indexPattern.matcher(imageName);
                        if (matcher.matches()) {
                            String imageName2 = matcher.group(i3);
                            imageIndex = Integer.parseInt(matcher.group(2));
                            imageName = imageName2;
                        }
                    } else {
                        imageName = name;
                    }
                    writer.write(imageName + "\n");
                    PixmapPacker.PixmapPackerRectangle rect = page.rects.get(name);
                    writer.write("  rotate: false\n");
                    writer.write("  xy: " + ((int) rect.x) + "," + ((int) rect.y) + "\n");
                    writer.write("  size: " + ((int) rect.width) + "," + ((int) rect.height) + "\n");
                    if (rect.splits == null) {
                        i = 1;
                        z = false;
                        c = 2;
                    } else {
                        writer.write("  split: " + rect.splits[0] + ", " + rect.splits[1] + ", " + rect.splits[2] + ", " + rect.splits[3] + "\n");
                        if (rect.pads == null) {
                            i = 1;
                            z = false;
                            c = 2;
                        } else {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("  pad: ");
                            z = false;
                            sb3.append(rect.pads[0]);
                            sb3.append(", ");
                            i = 1;
                            sb3.append(rect.pads[1]);
                            sb3.append(", ");
                            c = 2;
                            sb3.append(rect.pads[2]);
                            sb3.append(", ");
                            sb3.append(rect.pads[3]);
                            sb3.append("\n");
                            writer.write(sb3.toString());
                        }
                    }
                    writer.write("  orig: " + rect.originalWidth + ", " + rect.originalHeight + "\n");
                    writer.write("  offset: " + rect.offsetX + ", " + ((int) ((((float) rect.originalHeight) - rect.height) - ((float) rect.offsetY))) + "\n");
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("  index: ");
                    sb4.append(imageIndex);
                    sb4.append("\n");
                    writer.write(sb4.toString());
                    i3 = i;
                    z2 = z;
                }
            }
            fileHandle = file;
            pixmapPacker = packer;
            z2 = z2;
        }
        writer.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.badlogic.gdx.graphics.g2d.PixmapPackerIO$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat = new int[ImageFormat.values().length];

        static {
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[ImageFormat.CIM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$badlogic$gdx$graphics$g2d$PixmapPackerIO$ImageFormat[ImageFormat.PNG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
