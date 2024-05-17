package com.android.systemui.statusbar.phone.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;
/* loaded from: classes21.dex */
public interface TouchAnalyticsProto {

    /* loaded from: classes21.dex */
    public static final class Session extends MessageNano {
        public static final int FAILURE = 0;
        public static final int RANDOM_WAKEUP = 2;
        public static final int REAL = 3;
        public static final int REJECTED_TOUCH_REPORT = 4;
        public static final int RESERVED_1 = 0;
        public static final int RESERVED_2 = 1;
        public static final int SUCCESS = 1;
        public static final int UNKNOWN = 2;
        private static volatile Session[] _emptyArray;
        public String build;
        public String deviceId;
        public long durationMillis;
        public PhoneEvent[] phoneEvents;
        public int result;
        public SensorEvent[] sensorEvents;
        public long startTimestampMillis;
        public int touchAreaHeight;
        public int touchAreaWidth;
        public TouchEvent[] touchEvents;
        public int type;

        /* loaded from: classes21.dex */
        public static final class TouchEvent extends MessageNano {
            public static final int CANCEL = 3;
            public static final int DOWN = 0;
            public static final int MOVE = 2;
            public static final int OUTSIDE = 4;
            public static final int POINTER_DOWN = 5;
            public static final int POINTER_UP = 6;
            public static final int UP = 1;
            private static volatile TouchEvent[] _emptyArray;
            public int action;
            public int actionIndex;
            public Pointer[] pointers;
            public BoundingBox removedBoundingBox;
            public boolean removedRedacted;
            public long timeOffsetNanos;

            /* loaded from: classes21.dex */
            public static final class BoundingBox extends MessageNano {
                private static volatile BoundingBox[] _emptyArray;
                public float height;
                public float width;

                public static BoundingBox[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new BoundingBox[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public BoundingBox() {
                    clear();
                }

                public BoundingBox clear() {
                    this.width = 0.0f;
                    this.height = 0.0f;
                    this.cachedSize = -1;
                    return this;
                }

                @Override // com.google.protobuf.nano.MessageNano
                public void writeTo(CodedOutputByteBufferNano output) throws IOException {
                    if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(1, this.width);
                    }
                    if (Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(2, this.height);
                    }
                    super.writeTo(output);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.google.protobuf.nano.MessageNano
                public int computeSerializedSize() {
                    int size = super.computeSerializedSize();
                    if (Float.floatToIntBits(this.width) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(1, this.width);
                    }
                    if (Float.floatToIntBits(this.height) != Float.floatToIntBits(0.0f)) {
                        return size + CodedOutputByteBufferNano.computeFloatSize(2, this.height);
                    }
                    return size;
                }

                @Override // com.google.protobuf.nano.MessageNano
                public BoundingBox mergeFrom(CodedInputByteBufferNano input) throws IOException {
                    while (true) {
                        int tag = input.readTag();
                        if (tag == 0) {
                            return this;
                        }
                        if (tag == 13) {
                            this.width = input.readFloat();
                        } else if (tag != 21) {
                            if (!WireFormatNano.parseUnknownField(input, tag)) {
                                return this;
                            }
                        } else {
                            this.height = input.readFloat();
                        }
                    }
                }

                public static BoundingBox parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
                    return (BoundingBox) MessageNano.mergeFrom(new BoundingBox(), data);
                }

                public static BoundingBox parseFrom(CodedInputByteBufferNano input) throws IOException {
                    return new BoundingBox().mergeFrom(input);
                }
            }

            /* loaded from: classes21.dex */
            public static final class Pointer extends MessageNano {
                private static volatile Pointer[] _emptyArray;
                public int id;
                public float pressure;
                public BoundingBox removedBoundingBox;
                public float removedLength;
                public float size;
                public float x;
                public float y;

                public static Pointer[] emptyArray() {
                    if (_emptyArray == null) {
                        synchronized (InternalNano.LAZY_INIT_LOCK) {
                            if (_emptyArray == null) {
                                _emptyArray = new Pointer[0];
                            }
                        }
                    }
                    return _emptyArray;
                }

                public Pointer() {
                    clear();
                }

                public Pointer clear() {
                    this.x = 0.0f;
                    this.y = 0.0f;
                    this.size = 0.0f;
                    this.pressure = 0.0f;
                    this.id = 0;
                    this.removedLength = 0.0f;
                    this.removedBoundingBox = null;
                    this.cachedSize = -1;
                    return this;
                }

                @Override // com.google.protobuf.nano.MessageNano
                public void writeTo(CodedOutputByteBufferNano output) throws IOException {
                    if (Float.floatToIntBits(this.x) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(1, this.x);
                    }
                    if (Float.floatToIntBits(this.y) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(2, this.y);
                    }
                    if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(3, this.size);
                    }
                    if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(4, this.pressure);
                    }
                    int i = this.id;
                    if (i != 0) {
                        output.writeInt32(5, i);
                    }
                    if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                        output.writeFloat(6, this.removedLength);
                    }
                    BoundingBox boundingBox = this.removedBoundingBox;
                    if (boundingBox != null) {
                        output.writeMessage(7, boundingBox);
                    }
                    super.writeTo(output);
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // com.google.protobuf.nano.MessageNano
                public int computeSerializedSize() {
                    int size = super.computeSerializedSize();
                    if (Float.floatToIntBits(this.x) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(1, this.x);
                    }
                    if (Float.floatToIntBits(this.y) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(2, this.y);
                    }
                    if (Float.floatToIntBits(this.size) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(3, this.size);
                    }
                    if (Float.floatToIntBits(this.pressure) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(4, this.pressure);
                    }
                    int i = this.id;
                    if (i != 0) {
                        size += CodedOutputByteBufferNano.computeInt32Size(5, i);
                    }
                    if (Float.floatToIntBits(this.removedLength) != Float.floatToIntBits(0.0f)) {
                        size += CodedOutputByteBufferNano.computeFloatSize(6, this.removedLength);
                    }
                    BoundingBox boundingBox = this.removedBoundingBox;
                    if (boundingBox != null) {
                        return size + CodedOutputByteBufferNano.computeMessageSize(7, boundingBox);
                    }
                    return size;
                }

                @Override // com.google.protobuf.nano.MessageNano
                public Pointer mergeFrom(CodedInputByteBufferNano input) throws IOException {
                    while (true) {
                        int tag = input.readTag();
                        if (tag == 0) {
                            return this;
                        }
                        if (tag == 13) {
                            this.x = input.readFloat();
                        } else if (tag == 21) {
                            this.y = input.readFloat();
                        } else if (tag == 29) {
                            this.size = input.readFloat();
                        } else if (tag == 37) {
                            this.pressure = input.readFloat();
                        } else if (tag == 40) {
                            this.id = input.readInt32();
                        } else if (tag == 53) {
                            this.removedLength = input.readFloat();
                        } else if (tag != 58) {
                            if (!WireFormatNano.parseUnknownField(input, tag)) {
                                return this;
                            }
                        } else {
                            if (this.removedBoundingBox == null) {
                                this.removedBoundingBox = new BoundingBox();
                            }
                            input.readMessage(this.removedBoundingBox);
                        }
                    }
                }

                public static Pointer parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
                    return (Pointer) MessageNano.mergeFrom(new Pointer(), data);
                }

                public static Pointer parseFrom(CodedInputByteBufferNano input) throws IOException {
                    return new Pointer().mergeFrom(input);
                }
            }

            public static TouchEvent[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new TouchEvent[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public TouchEvent() {
                clear();
            }

            public TouchEvent clear() {
                this.timeOffsetNanos = 0L;
                this.action = 0;
                this.actionIndex = 0;
                this.pointers = Pointer.emptyArray();
                this.removedRedacted = false;
                this.removedBoundingBox = null;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano output) throws IOException {
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    output.writeUInt64(1, j);
                }
                int i = this.action;
                if (i != 0) {
                    output.writeInt32(2, i);
                }
                int i2 = this.actionIndex;
                if (i2 != 0) {
                    output.writeInt32(3, i2);
                }
                Pointer[] pointerArr = this.pointers;
                if (pointerArr != null && pointerArr.length > 0) {
                    int i3 = 0;
                    while (true) {
                        Pointer[] pointerArr2 = this.pointers;
                        if (i3 >= pointerArr2.length) {
                            break;
                        }
                        Pointer element = pointerArr2[i3];
                        if (element != null) {
                            output.writeMessage(4, element);
                        }
                        i3++;
                    }
                }
                boolean z = this.removedRedacted;
                if (z) {
                    output.writeBool(5, z);
                }
                BoundingBox boundingBox = this.removedBoundingBox;
                if (boundingBox != null) {
                    output.writeMessage(6, boundingBox);
                }
                super.writeTo(output);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int size = super.computeSerializedSize();
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    size += CodedOutputByteBufferNano.computeUInt64Size(1, j);
                }
                int i = this.action;
                if (i != 0) {
                    size += CodedOutputByteBufferNano.computeInt32Size(2, i);
                }
                int i2 = this.actionIndex;
                if (i2 != 0) {
                    size += CodedOutputByteBufferNano.computeInt32Size(3, i2);
                }
                Pointer[] pointerArr = this.pointers;
                if (pointerArr != null && pointerArr.length > 0) {
                    int i3 = 0;
                    while (true) {
                        Pointer[] pointerArr2 = this.pointers;
                        if (i3 >= pointerArr2.length) {
                            break;
                        }
                        Pointer element = pointerArr2[i3];
                        if (element != null) {
                            size += CodedOutputByteBufferNano.computeMessageSize(4, element);
                        }
                        i3++;
                    }
                }
                boolean z = this.removedRedacted;
                if (z) {
                    size += CodedOutputByteBufferNano.computeBoolSize(5, z);
                }
                BoundingBox boundingBox = this.removedBoundingBox;
                if (boundingBox != null) {
                    return size + CodedOutputByteBufferNano.computeMessageSize(6, boundingBox);
                }
                return size;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public TouchEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
                while (true) {
                    int tag = input.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        this.timeOffsetNanos = input.readUInt64();
                    } else if (tag == 16) {
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.action = value;
                                continue;
                        }
                    } else if (tag == 24) {
                        int arrayLength = input.readInt32();
                        this.actionIndex = arrayLength;
                    } else if (tag == 34) {
                        int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 34);
                        Pointer[] pointerArr = this.pointers;
                        int i = pointerArr == null ? 0 : pointerArr.length;
                        Pointer[] newArray = new Pointer[i + arrayLength2];
                        if (i != 0) {
                            System.arraycopy(this.pointers, 0, newArray, 0, i);
                        }
                        while (i < newArray.length - 1) {
                            newArray[i] = new Pointer();
                            input.readMessage(newArray[i]);
                            input.readTag();
                            i++;
                        }
                        newArray[i] = new Pointer();
                        input.readMessage(newArray[i]);
                        this.pointers = newArray;
                    } else if (tag == 40) {
                        this.removedRedacted = input.readBool();
                    } else if (tag != 50) {
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this;
                        }
                    } else {
                        if (this.removedBoundingBox == null) {
                            this.removedBoundingBox = new BoundingBox();
                        }
                        input.readMessage(this.removedBoundingBox);
                    }
                }
            }

            public static TouchEvent parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
                return (TouchEvent) MessageNano.mergeFrom(new TouchEvent(), data);
            }

            public static TouchEvent parseFrom(CodedInputByteBufferNano input) throws IOException {
                return new TouchEvent().mergeFrom(input);
            }
        }

        /* loaded from: classes21.dex */
        public static final class SensorEvent extends MessageNano {
            public static final int ACCELEROMETER = 1;
            public static final int GYROSCOPE = 4;
            public static final int LIGHT = 5;
            public static final int PROXIMITY = 8;
            public static final int ROTATION_VECTOR = 11;
            private static volatile SensorEvent[] _emptyArray;
            public long timeOffsetNanos;
            public long timestamp;
            public int type;
            public float[] values;

            public static SensorEvent[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new SensorEvent[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public SensorEvent() {
                clear();
            }

            public SensorEvent clear() {
                this.type = 1;
                this.timeOffsetNanos = 0L;
                this.values = WireFormatNano.EMPTY_FLOAT_ARRAY;
                this.timestamp = 0L;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano output) throws IOException {
                int i = this.type;
                if (i != 1) {
                    output.writeInt32(1, i);
                }
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    output.writeUInt64(2, j);
                }
                float[] fArr = this.values;
                if (fArr != null && fArr.length > 0) {
                    int i2 = 0;
                    while (true) {
                        float[] fArr2 = this.values;
                        if (i2 >= fArr2.length) {
                            break;
                        }
                        output.writeFloat(3, fArr2[i2]);
                        i2++;
                    }
                }
                long j2 = this.timestamp;
                if (j2 != 0) {
                    output.writeUInt64(4, j2);
                }
                super.writeTo(output);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int size = super.computeSerializedSize();
                int i = this.type;
                if (i != 1) {
                    size += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    size += CodedOutputByteBufferNano.computeUInt64Size(2, j);
                }
                float[] fArr = this.values;
                if (fArr != null && fArr.length > 0) {
                    int dataSize = fArr.length * 4;
                    size = size + dataSize + (fArr.length * 1);
                }
                long j2 = this.timestamp;
                if (j2 != 0) {
                    return size + CodedOutputByteBufferNano.computeUInt64Size(4, j2);
                }
                return size;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public SensorEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
                while (true) {
                    int tag = input.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int value = input.readInt32();
                        if (value == 1 || value == 8 || value == 11 || value == 4 || value == 5) {
                            this.type = value;
                        }
                    } else if (tag == 16) {
                        this.timeOffsetNanos = input.readUInt64();
                    } else if (tag == 26) {
                        int length = input.readRawVarint32();
                        int limit = input.pushLimit(length);
                        int arrayLength = length / 4;
                        float[] fArr = this.values;
                        int i = fArr == null ? 0 : fArr.length;
                        float[] newArray = new float[i + arrayLength];
                        if (i != 0) {
                            System.arraycopy(this.values, 0, newArray, 0, i);
                        }
                        while (i < newArray.length) {
                            newArray[i] = input.readFloat();
                            i++;
                        }
                        this.values = newArray;
                        input.popLimit(limit);
                    } else if (tag == 29) {
                        int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 29);
                        float[] fArr2 = this.values;
                        int i2 = fArr2 == null ? 0 : fArr2.length;
                        float[] newArray2 = new float[i2 + arrayLength2];
                        if (i2 != 0) {
                            System.arraycopy(this.values, 0, newArray2, 0, i2);
                        }
                        while (i2 < newArray2.length - 1) {
                            newArray2[i2] = input.readFloat();
                            input.readTag();
                            i2++;
                        }
                        newArray2[i2] = input.readFloat();
                        this.values = newArray2;
                    } else if (tag != 32) {
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this;
                        }
                    } else {
                        this.timestamp = input.readUInt64();
                    }
                }
            }

            public static SensorEvent parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
                return (SensorEvent) MessageNano.mergeFrom(new SensorEvent(), data);
            }

            public static SensorEvent parseFrom(CodedInputByteBufferNano input) throws IOException {
                return new SensorEvent().mergeFrom(input);
            }
        }

        /* loaded from: classes21.dex */
        public static final class PhoneEvent extends MessageNano {
            public static final int ON_AFFORDANCE_SWIPING_ABORTED = 23;
            public static final int ON_BOUNCER_HIDDEN = 5;
            public static final int ON_BOUNCER_SHOWN = 4;
            public static final int ON_CAMERA_HINT_STARTED = 27;
            public static final int ON_CAMERA_ON = 24;
            public static final int ON_LEFT_AFFORDANCE_HINT_STARTED = 28;
            public static final int ON_LEFT_AFFORDANCE_ON = 25;
            public static final int ON_LEFT_AFFORDANCE_SWIPING_STARTED = 22;
            public static final int ON_NOTIFICATION_ACTIVE = 11;
            public static final int ON_NOTIFICATION_DISMISSED = 18;
            public static final int ON_NOTIFICATION_DOUBLE_TAP = 13;
            public static final int ON_NOTIFICATION_INACTIVE = 12;
            public static final int ON_NOTIFICATION_START_DISMISSING = 19;
            public static final int ON_NOTIFICATION_START_DRAGGING_DOWN = 16;
            public static final int ON_NOTIFICATION_STOP_DISMISSING = 20;
            public static final int ON_NOTIFICATION_STOP_DRAGGING_DOWN = 17;
            public static final int ON_QS_DOWN = 6;
            public static final int ON_RIGHT_AFFORDANCE_SWIPING_STARTED = 21;
            public static final int ON_SCREEN_OFF = 2;
            public static final int ON_SCREEN_ON = 0;
            public static final int ON_SCREEN_ON_FROM_TOUCH = 1;
            public static final int ON_SUCCESSFUL_UNLOCK = 3;
            public static final int ON_TRACKING_STARTED = 9;
            public static final int ON_TRACKING_STOPPED = 10;
            public static final int ON_UNLOCK_HINT_STARTED = 26;
            public static final int RESET_NOTIFICATION_EXPANDED = 15;
            public static final int SET_NOTIFICATION_EXPANDED = 14;
            public static final int SET_QS_EXPANDED_FALSE = 8;
            public static final int SET_QS_EXPANDED_TRUE = 7;
            private static volatile PhoneEvent[] _emptyArray;
            public long timeOffsetNanos;
            public int type;

            public static PhoneEvent[] emptyArray() {
                if (_emptyArray == null) {
                    synchronized (InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = new PhoneEvent[0];
                        }
                    }
                }
                return _emptyArray;
            }

            public PhoneEvent() {
                clear();
            }

            public PhoneEvent clear() {
                this.type = 0;
                this.timeOffsetNanos = 0L;
                this.cachedSize = -1;
                return this;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public void writeTo(CodedOutputByteBufferNano output) throws IOException {
                int i = this.type;
                if (i != 0) {
                    output.writeInt32(1, i);
                }
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    output.writeUInt64(2, j);
                }
                super.writeTo(output);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.google.protobuf.nano.MessageNano
            public int computeSerializedSize() {
                int size = super.computeSerializedSize();
                int i = this.type;
                if (i != 0) {
                    size += CodedOutputByteBufferNano.computeInt32Size(1, i);
                }
                long j = this.timeOffsetNanos;
                if (j != 0) {
                    return size + CodedOutputByteBufferNano.computeUInt64Size(2, j);
                }
                return size;
            }

            @Override // com.google.protobuf.nano.MessageNano
            public PhoneEvent mergeFrom(CodedInputByteBufferNano input) throws IOException {
                while (true) {
                    int tag = input.readTag();
                    if (tag == 0) {
                        return this;
                    }
                    if (tag == 8) {
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                                this.type = value;
                                continue;
                        }
                    } else if (tag != 16) {
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this;
                        }
                    } else {
                        this.timeOffsetNanos = input.readUInt64();
                    }
                }
            }

            public static PhoneEvent parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
                return (PhoneEvent) MessageNano.mergeFrom(new PhoneEvent(), data);
            }

            public static PhoneEvent parseFrom(CodedInputByteBufferNano input) throws IOException {
                return new PhoneEvent().mergeFrom(input);
            }
        }

        public static Session[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Session[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Session() {
            clear();
        }

        public Session clear() {
            this.startTimestampMillis = 0L;
            this.durationMillis = 0L;
            this.build = "";
            this.result = 0;
            this.touchEvents = TouchEvent.emptyArray();
            this.sensorEvents = SensorEvent.emptyArray();
            this.touchAreaWidth = 0;
            this.touchAreaHeight = 0;
            this.type = 0;
            this.phoneEvents = PhoneEvent.emptyArray();
            this.deviceId = "";
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            long j = this.startTimestampMillis;
            if (j != 0) {
                output.writeUInt64(1, j);
            }
            long j2 = this.durationMillis;
            if (j2 != 0) {
                output.writeUInt64(2, j2);
            }
            if (!this.build.equals("")) {
                output.writeString(3, this.build);
            }
            int i = this.result;
            if (i != 0) {
                output.writeInt32(4, i);
            }
            TouchEvent[] touchEventArr = this.touchEvents;
            if (touchEventArr != null && touchEventArr.length > 0) {
                int i2 = 0;
                while (true) {
                    TouchEvent[] touchEventArr2 = this.touchEvents;
                    if (i2 >= touchEventArr2.length) {
                        break;
                    }
                    TouchEvent element = touchEventArr2[i2];
                    if (element != null) {
                        output.writeMessage(5, element);
                    }
                    i2++;
                }
            }
            SensorEvent[] sensorEventArr = this.sensorEvents;
            if (sensorEventArr != null && sensorEventArr.length > 0) {
                int i3 = 0;
                while (true) {
                    SensorEvent[] sensorEventArr2 = this.sensorEvents;
                    if (i3 >= sensorEventArr2.length) {
                        break;
                    }
                    SensorEvent element2 = sensorEventArr2[i3];
                    if (element2 != null) {
                        output.writeMessage(6, element2);
                    }
                    i3++;
                }
            }
            int i4 = this.touchAreaWidth;
            if (i4 != 0) {
                output.writeInt32(9, i4);
            }
            int i5 = this.touchAreaHeight;
            if (i5 != 0) {
                output.writeInt32(10, i5);
            }
            int i6 = this.type;
            if (i6 != 0) {
                output.writeInt32(11, i6);
            }
            PhoneEvent[] phoneEventArr = this.phoneEvents;
            if (phoneEventArr != null && phoneEventArr.length > 0) {
                int i7 = 0;
                while (true) {
                    PhoneEvent[] phoneEventArr2 = this.phoneEvents;
                    if (i7 >= phoneEventArr2.length) {
                        break;
                    }
                    PhoneEvent element3 = phoneEventArr2[i7];
                    if (element3 != null) {
                        output.writeMessage(12, element3);
                    }
                    i7++;
                }
            }
            if (!this.deviceId.equals("")) {
                output.writeString(13, this.deviceId);
            }
            super.writeTo(output);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.protobuf.nano.MessageNano
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            long j = this.startTimestampMillis;
            if (j != 0) {
                size += CodedOutputByteBufferNano.computeUInt64Size(1, j);
            }
            long j2 = this.durationMillis;
            if (j2 != 0) {
                size += CodedOutputByteBufferNano.computeUInt64Size(2, j2);
            }
            if (!this.build.equals("")) {
                size += CodedOutputByteBufferNano.computeStringSize(3, this.build);
            }
            int i = this.result;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(4, i);
            }
            TouchEvent[] touchEventArr = this.touchEvents;
            if (touchEventArr != null && touchEventArr.length > 0) {
                int i2 = 0;
                while (true) {
                    TouchEvent[] touchEventArr2 = this.touchEvents;
                    if (i2 >= touchEventArr2.length) {
                        break;
                    }
                    TouchEvent element = touchEventArr2[i2];
                    if (element != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(5, element);
                    }
                    i2++;
                }
            }
            SensorEvent[] sensorEventArr = this.sensorEvents;
            if (sensorEventArr != null && sensorEventArr.length > 0) {
                int i3 = 0;
                while (true) {
                    SensorEvent[] sensorEventArr2 = this.sensorEvents;
                    if (i3 >= sensorEventArr2.length) {
                        break;
                    }
                    SensorEvent element2 = sensorEventArr2[i3];
                    if (element2 != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(6, element2);
                    }
                    i3++;
                }
            }
            int i4 = this.touchAreaWidth;
            if (i4 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(9, i4);
            }
            int i5 = this.touchAreaHeight;
            if (i5 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(10, i5);
            }
            int i6 = this.type;
            if (i6 != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(11, i6);
            }
            PhoneEvent[] phoneEventArr = this.phoneEvents;
            if (phoneEventArr != null && phoneEventArr.length > 0) {
                int i7 = 0;
                while (true) {
                    PhoneEvent[] phoneEventArr2 = this.phoneEvents;
                    if (i7 >= phoneEventArr2.length) {
                        break;
                    }
                    PhoneEvent element3 = phoneEventArr2[i7];
                    if (element3 != null) {
                        size += CodedOutputByteBufferNano.computeMessageSize(12, element3);
                    }
                    i7++;
                }
            }
            if (!this.deviceId.equals("")) {
                return size + CodedOutputByteBufferNano.computeStringSize(13, this.deviceId);
            }
            return size;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public Session mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.startTimestampMillis = input.readUInt64();
                        break;
                    case 16:
                        this.durationMillis = input.readUInt64();
                        break;
                    case 26:
                        this.build = input.readString();
                        break;
                    case 32:
                        int value = input.readInt32();
                        if (value != 0 && value != 1 && value != 2) {
                            break;
                        } else {
                            this.result = value;
                            break;
                        }
                    case 42:
                        int arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 42);
                        TouchEvent[] touchEventArr = this.touchEvents;
                        int i = touchEventArr == null ? 0 : touchEventArr.length;
                        TouchEvent[] newArray = new TouchEvent[i + arrayLength];
                        if (i != 0) {
                            System.arraycopy(this.touchEvents, 0, newArray, 0, i);
                        }
                        while (i < newArray.length - 1) {
                            newArray[i] = new TouchEvent();
                            input.readMessage(newArray[i]);
                            input.readTag();
                            i++;
                        }
                        newArray[i] = new TouchEvent();
                        input.readMessage(newArray[i]);
                        this.touchEvents = newArray;
                        break;
                    case 50:
                        int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 50);
                        SensorEvent[] sensorEventArr = this.sensorEvents;
                        int i2 = sensorEventArr == null ? 0 : sensorEventArr.length;
                        SensorEvent[] newArray2 = new SensorEvent[i2 + arrayLength2];
                        if (i2 != 0) {
                            System.arraycopy(this.sensorEvents, 0, newArray2, 0, i2);
                        }
                        while (i2 < newArray2.length - 1) {
                            newArray2[i2] = new SensorEvent();
                            input.readMessage(newArray2[i2]);
                            input.readTag();
                            i2++;
                        }
                        newArray2[i2] = new SensorEvent();
                        input.readMessage(newArray2[i2]);
                        this.sensorEvents = newArray2;
                        break;
                    case 72:
                        this.touchAreaWidth = input.readInt32();
                        break;
                    case 80:
                        this.touchAreaHeight = input.readInt32();
                        break;
                    case 88:
                        int value2 = input.readInt32();
                        if (value2 != 0 && value2 != 1 && value2 != 2 && value2 != 3 && value2 != 4) {
                            break;
                        } else {
                            this.type = value2;
                            break;
                        }
                    case 98:
                        int arrayLength3 = WireFormatNano.getRepeatedFieldArrayLength(input, 98);
                        PhoneEvent[] phoneEventArr = this.phoneEvents;
                        int i3 = phoneEventArr == null ? 0 : phoneEventArr.length;
                        PhoneEvent[] newArray3 = new PhoneEvent[i3 + arrayLength3];
                        if (i3 != 0) {
                            System.arraycopy(this.phoneEvents, 0, newArray3, 0, i3);
                        }
                        while (i3 < newArray3.length - 1) {
                            newArray3[i3] = new PhoneEvent();
                            input.readMessage(newArray3[i3]);
                            input.readTag();
                            i3++;
                        }
                        newArray3[i3] = new PhoneEvent();
                        input.readMessage(newArray3[i3]);
                        this.phoneEvents = newArray3;
                        break;
                    case 106:
                        this.deviceId = input.readString();
                        break;
                    default:
                        if (WireFormatNano.parseUnknownField(input, tag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static Session parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Session) MessageNano.mergeFrom(new Session(), data);
        }

        public static Session parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Session().mergeFrom(input);
        }
    }
}
