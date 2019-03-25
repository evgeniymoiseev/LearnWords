package com.evgeniy.moiseev.learnwords.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleWord implements Parcelable {
    private String word;
    private int guessed;
    private int notGuessed;

    public SimpleWord(String word, int guessed, int notGuessed) {
        this.word = word;
        this.guessed = guessed;
        this.notGuessed = notGuessed;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getGuessed() {
        return guessed;
    }

    public void setGuessed(int guessed) {
        this.guessed = guessed;
    }

    public int getNotGuessed() {
        return notGuessed;
    }

    public void setNotGuessed(int notGuessed) {
        this.notGuessed = notGuessed;
    }

    protected SimpleWord(Parcel in) {
        word = in.readString();
        guessed = in.readInt();
        notGuessed = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeInt(guessed);
        dest.writeInt(notGuessed);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SimpleWord> CREATOR = new Parcelable.Creator<SimpleWord>() {
        @Override
        public SimpleWord createFromParcel(Parcel in) {
            return new SimpleWord(in);
        }

        @Override
        public SimpleWord[] newArray(int size) {
            return new SimpleWord[size];
        }
    };
}
