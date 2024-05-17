package com.xiaopeng.speech.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes23.dex */
public class Value implements Parcelable {
    private static final int BOOLEAN_TYPE = 5;
    private static final int DOUBLE_TYPE = 2;
    private static final int FLOAT_ARRAY_TYPE = 7;
    private static final int INTEGER_ARRAY_TYPE = 6;
    private static final int INTEGER_TYPE = 1;
    private static final int STRING_TYPE = 4;
    private int classType;
    private Boolean mBoolean;
    private Double mDouble;
    private float[] mFloatArray;
    private Integer mInteger;
    private int[] mIntegerArray;
    private String mString;
    public static final Value VOID = new Value();
    public static final Value INTERRUPT = new Value();
    public static final Parcelable.Creator<Value> CREATOR = new Parcelable.Creator<Value>() { // from class: com.xiaopeng.speech.common.bean.Value.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Value createFromParcel(Parcel in) {
            return new Value(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Value[] newArray(int size) {
            return new Value[size];
        }
    };

    public Value() {
    }

    public Value(Object value) {
        initValue(value);
    }

    private void initValue(Object value) {
        if (value instanceof Double) {
            this.classType = 2;
            this.mDouble = (Double) value;
        } else if (value instanceof String) {
            this.classType = 4;
            this.mString = (String) value;
        } else if (value instanceof Boolean) {
            this.classType = 5;
            this.mBoolean = (Boolean) value;
        } else if (value instanceof int[]) {
            this.classType = 6;
            this.mIntegerArray = (int[]) value;
        } else if (value instanceof float[]) {
            this.classType = 7;
            this.mFloatArray = (float[]) value;
        } else if (value instanceof Integer) {
            this.classType = 1;
            this.mInteger = (Integer) value;
        } else if (value instanceof Float) {
            this.classType = 2;
            this.mDouble = Double.valueOf(((Float) value).doubleValue());
        } else {
            this.classType = 0;
        }
    }

    protected Value(Parcel in) {
        this.classType = in.readInt();
        int i = this.classType;
        if (i == 2) {
            this.mDouble = Double.valueOf(in.readDouble());
        } else if (i == 4) {
            this.mString = in.readString();
        } else {
            if (i != 5) {
                if (i == 6) {
                    this.mIntegerArray = in.createIntArray();
                    return;
                } else if (i == 7) {
                    this.mFloatArray = in.createFloatArray();
                    return;
                } else if (i == 1) {
                    this.mInteger = Integer.valueOf(in.readInt());
                    return;
                } else {
                    return;
                }
            }
            this.mBoolean = Boolean.valueOf(in.readInt() == 1);
        }
    }

    public boolean isDouble() {
        return this.classType == 2;
    }

    public double getDouble() {
        return this.mDouble.doubleValue();
    }

    public boolean isString() {
        return this.classType == 4;
    }

    public String getString() {
        return this.mString;
    }

    public boolean isBoolean() {
        return this.classType == 5;
    }

    public boolean getBoolean() {
        return this.mBoolean.booleanValue();
    }

    public boolean isInteger() {
        return this.classType == 1;
    }

    public Integer getInteger() {
        return this.mInteger;
    }

    public boolean isNumber() {
        int i = this.classType;
        return i == 1 || i == 2;
    }

    public Number getNumber() {
        int i = this.classType;
        if (i == 1) {
            return this.mInteger;
        }
        if (i == 2) {
            return this.mDouble;
        }
        return 0;
    }

    public boolean isArray() {
        int i = this.classType;
        return i == 6 || i == 7;
    }

    public Object getDataFromArray(int index) {
        int i = this.classType;
        if (i == 6) {
            int[] iArr = this.mIntegerArray;
            if (index < iArr.length) {
                return Integer.valueOf(iArr[index]);
            }
            return null;
        } else if (i == 7) {
            float[] fArr = this.mFloatArray;
            if (index < fArr.length) {
                return Float.valueOf(fArr[index]);
            }
            return null;
        } else {
            return null;
        }
    }

    public boolean isFloatArray() {
        return this.classType == 7;
    }

    public float[] getFloatArray() {
        return this.mFloatArray;
    }

    public boolean isIntegerArray() {
        return this.classType == 6;
    }

    public int[] getIntegerArray() {
        return this.mIntegerArray;
    }

    public Object getArgs() {
        int i = this.classType;
        if (i == 1) {
            return this.mInteger;
        }
        if (i == 4) {
            return this.mString;
        }
        if (i == 2) {
            return this.mDouble;
        }
        if (i == 5) {
            return this.mBoolean;
        }
        if (i == 7) {
            return this.mFloatArray;
        }
        if (i == 6) {
            return this.mIntegerArray;
        }
        return null;
    }

    public void setIntegerArray(int[] integerArray) {
        this.classType = 6;
        this.mIntegerArray = integerArray;
    }

    public void setFloatArray(float[] floatArray) {
        this.classType = 7;
        this.mFloatArray = floatArray;
    }

    public void setDouble(Double aDouble) {
        this.classType = 2;
        this.mDouble = aDouble;
    }

    public void setString(String string) {
        this.classType = 4;
        this.mString = string;
    }

    public void setBoolean(Boolean aBoolean) {
        this.classType = 5;
        this.mBoolean = aBoolean;
    }

    public void setInteger(Integer integer) {
        this.classType = 1;
        this.mInteger = integer;
    }

    public void setValue(Object value) {
        initValue(value);
    }

    public int getClassType() {
        return this.classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.classType);
        int i = this.classType;
        if (i == 2) {
            dest.writeDouble(this.mDouble.doubleValue());
        } else if (i == 4) {
            dest.writeString(this.mString);
        } else if (i != 5) {
            if (i == 6) {
                dest.writeIntArray(this.mIntegerArray);
            } else if (i == 7) {
                dest.writeFloatArray(this.mFloatArray);
            } else if (i == 1) {
                dest.writeInt(this.mInteger.intValue());
            }
        } else {
            dest.writeInt(this.mBoolean.booleanValue() ? 1 : 0);
        }
    }

    public String toString() {
        return "Value{mInteger=" + this.mInteger + ", mDouble=" + this.mDouble + ", mString='" + this.mString + "', mBoolean=" + this.mBoolean + ", classType=" + this.classType + '}';
    }

    public String toObjAddress() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode());
    }
}
