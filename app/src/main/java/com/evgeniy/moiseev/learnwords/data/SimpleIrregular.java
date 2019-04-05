package com.evgeniy.moiseev.learnwords.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleIrregular implements Parcelable {
    private String word1;
    private String word2;
    private String word3;
    private boolean b1;
    private boolean b2;
    private boolean b3;

    public SimpleIrregular() {
    }

    public boolean isRight() {
        return b1 && b2 && b3;
    }

    public boolean isB1() {
        return b1;
    }

    public void setB1(boolean b1) {
        this.b1 = b1;
    }

    public boolean isB2() {
        return b2;
    }

    public void setB2(boolean b2) {
        this.b2 = b2;
    }

    public boolean isB3() {
        return b3;
    }

    public void setB3(boolean b3) {
        this.b3 = b3;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord(int position, String word) {
        switch (position) {
            case 0:
                word1 = word;
                break;
            case 1:
                word2 = word;
                break;
            case 2:
                word3 = word;
                break;
        }
    }

    public void setB(int position, boolean b) {
        switch (position) {
            case 0:
                b1 = b;
                break;
            case 1:
                b2 = b;
                break;
            case 2:
                b3 = b;
                break;
        }
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }

    @Override
    public String toString() {
        return "SimpleIrregular[" + word1 + " = " + b1 + ", " + word2 + " = " + b2 + ", " + word3 + " = " + b3 + "]";
    }

    protected SimpleIrregular(Parcel in) {
        word1 = in.readString();
        word2 = in.readString();
        word3 = in.readString();
        b1 = in.readByte() != 0x00;
        b2 = in.readByte() != 0x00;
        b3 = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word1);
        dest.writeString(word2);
        dest.writeString(word3);
        dest.writeByte((byte) (b1 ? 0x01 : 0x00));
        dest.writeByte((byte) (b2 ? 0x01 : 0x00));
        dest.writeByte((byte) (b3 ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SimpleIrregular> CREATOR = new Parcelable.Creator<SimpleIrregular>() {
        @Override
        public SimpleIrregular createFromParcel(Parcel in) {
            return new SimpleIrregular(in);
        }

        @Override
        public SimpleIrregular[] newArray(int size) {
            return new SimpleIrregular[size];
        }
    };
}