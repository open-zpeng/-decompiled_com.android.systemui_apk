package com.badlogic.gdx.utils;

import com.badlogic.gdx.files.FileHandle;
import java.io.InputStream;
/* loaded from: classes21.dex */
public interface BaseJsonReader {
    JsonValue parse(FileHandle fileHandle);

    JsonValue parse(InputStream inputStream);
}
