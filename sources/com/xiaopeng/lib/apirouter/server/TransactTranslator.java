package com.xiaopeng.lib.apirouter.server;

import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import androidx.annotation.Keep;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
@Keep
/* loaded from: classes22.dex */
public class TransactTranslator {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Multi-variable type inference failed */
    public static <T> T read(String data, String kind) throws RemoteException {
        char c = 65535;
        try {
            switch (kind.hashCode()) {
                case -2056817302:
                    if (kind.equals("java.lang.Integer")) {
                        c = 7;
                        break;
                    }
                    break;
                case -1325958191:
                    if (kind.equals("double")) {
                        c = 14;
                        break;
                    }
                    break;
                case -527879800:
                    if (kind.equals("java.lang.Float")) {
                        c = '\r';
                        break;
                    }
                    break;
                case -515992664:
                    if (kind.equals("java.lang.Short")) {
                        c = 5;
                        break;
                    }
                    break;
                case 104431:
                    if (kind.equals("int")) {
                        c = 6;
                        break;
                    }
                    break;
                case 3039496:
                    if (kind.equals("byte")) {
                        c = 2;
                        break;
                    }
                    break;
                case 3052374:
                    if (kind.equals("char")) {
                        c = 11;
                        break;
                    }
                    break;
                case 3327612:
                    if (kind.equals("long")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 64711720:
                    if (kind.equals("boolean")) {
                        c = 0;
                        break;
                    }
                    break;
                case 97526364:
                    if (kind.equals("float")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 109413500:
                    if (kind.equals("short")) {
                        c = 4;
                        break;
                    }
                    break;
                case 155276373:
                    if (kind.equals("java.lang.Character")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 344809556:
                    if (kind.equals("java.lang.Boolean")) {
                        c = 1;
                        break;
                    }
                    break;
                case 398507100:
                    if (kind.equals("java.lang.Byte")) {
                        c = 3;
                        break;
                    }
                    break;
                case 398795216:
                    if (kind.equals("java.lang.Long")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 761287205:
                    if (kind.equals("java.lang.Double")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1195259493:
                    if (kind.equals("java.lang.String")) {
                        c = 16;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    String string = data;
                    if (TextUtils.isEmpty(string)) {
                        string = OOBEEvent.STRING_FALSE;
                    }
                    return (T) Boolean.valueOf(string);
                case 1:
                    return (T) Boolean.valueOf(data);
                case 2:
                    String string2 = data;
                    if (TextUtils.isEmpty(string2)) {
                        string2 = "0";
                    }
                    return (T) Byte.valueOf(string2);
                case 3:
                    return (T) Byte.valueOf(data);
                case 4:
                    String string3 = data;
                    if (TextUtils.isEmpty(string3)) {
                        string3 = "0";
                    }
                    return (T) Short.valueOf(string3);
                case 5:
                    return (T) Short.valueOf(data);
                case 6:
                    String string4 = data;
                    if (TextUtils.isEmpty(string4)) {
                        string4 = "0";
                    }
                    return (T) Integer.valueOf(string4);
                case 7:
                    return (T) Integer.valueOf(data);
                case '\b':
                    String string5 = data;
                    if (TextUtils.isEmpty(string5)) {
                        string5 = "0";
                    }
                    return (T) Long.valueOf(string5);
                case '\t':
                    return (T) Long.valueOf(data);
                case '\n':
                case 11:
                    if (TextUtils.isEmpty(data)) {
                        return (T) (char) 0;
                    }
                    return (T) Character.valueOf(data.charAt(0));
                case '\f':
                    String string6 = data;
                    if (TextUtils.isEmpty(string6)) {
                        string6 = "0";
                    }
                    return (T) Float.valueOf(string6);
                case '\r':
                    return (T) Float.valueOf(data);
                case 14:
                    String string7 = data;
                    if (TextUtils.isEmpty(string7)) {
                        string7 = "0";
                    }
                    return (T) Double.valueOf(string7);
                case 15:
                    return (T) Double.valueOf(data);
                case 16:
                    return data;
                default:
                    throw new UnsupportedOperationException("un support type");
            }
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public static <T> void reply(Parcel reply, T result) {
        reply.writeValue(result);
    }
}
