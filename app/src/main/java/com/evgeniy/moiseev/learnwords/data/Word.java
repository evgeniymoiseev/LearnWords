package com.evgeniy.moiseev.learnwords.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word implements Comparable<Word>, Parcelable {
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("word")
    @Expose
    @ColumnInfo(name = "word")
    private String word;

    @SerializedName("transcription")
    @Expose
    @ColumnInfo(name = "transcription")
    private String transcription;

    @SerializedName("translateRus")
    @Expose
    @ColumnInfo(name = "translateRus")
    private String translationRus;

    @SerializedName("translateUkr")
    @Expose
    @ColumnInfo(name = "translateUkr")
    private String translationUkr;

    @SerializedName("category")
    @Expose
    @ColumnInfo(name = "category")
    private String category;

    @SerializedName("categoryRus")
    @Expose
    @ColumnInfo(name = "categoryRus")
    private String categoryRus;

    @SerializedName("categoryUkr")
    @Expose
    @ColumnInfo(name = "categoryUkr")
    private String categoryUkr;

    @SerializedName("difficulty")
    @Expose
    @ColumnInfo(name = "difficulty")
    private int difficulty;

    @SerializedName("isMain")
    @Expose
    @ColumnInfo(name = "isMain")
    private int isMain;

    @SerializedName("guessed")
    @Expose
    @ColumnInfo(name = "guessed")
    private int guessed;

    @SerializedName("notGuessed")
    @Expose
    @ColumnInfo(name = "notGuessed")
    private int notGuessed;

    @SerializedName("isFavorite")
    @Expose
    @ColumnInfo(name = "isFavorite")
    private int isFavorite;

    public Word(String word, String transcription, String translationRus, String translationUkr,
                String category, String categoryRus, String categoryUkr,
                int difficulty, int isMain, int guessed, int notGuessed, int isFavorite) {
        this.word = word;
        this.transcription = transcription;
        this.translationRus = translationRus;
        this.translationUkr = translationUkr;
        this.category = category;
        this.categoryRus = categoryRus;
        this.categoryUkr = categoryUkr;
        this.difficulty = difficulty;
        this.isMain = isMain;
        this.guessed = guessed;
        this.notGuessed = notGuessed;
        this.isFavorite = isFavorite;
    }

    protected Word(Parcel in) {
        id = in.readInt();
        word = in.readString();
        transcription = in.readString();
        translationRus = in.readString();
        translationUkr = in.readString();
        category = in.readString();
        categoryRus = in.readString();
        categoryUkr = in.readString();
        difficulty = in.readInt();
        isMain = in.readInt();
        guessed = in.readInt();
        notGuessed = in.readInt();
        isFavorite = in.readInt();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranslationRus() {
        return translationRus;
    }

    public void setTranslateRus(String translateRus) {
        this.translationRus = translateRus;
    }

    public String getTranslationUkr() {
        return translationUkr;
    }

    public void setTranslateUkr(String translateUkr) {
        this.translationUkr = translateUkr;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryRus() {
        return categoryRus;
    }

    public void setCategoryRus(String categoryRus) {
        this.categoryRus = categoryRus;
    }

    public String getCategoryUkr() {
        return categoryUkr;
    }

    public void setCategoryUkr(String categoryUkr) {
        this.categoryUkr = categoryUkr;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getIsMain() {
        return isMain;
    }

    public void setIsMain(int isMain) {
        this.isMain = isMain;
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

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public int compareTo(Word o) {
        return word.toLowerCase().compareTo(o.word.toLowerCase());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(word);
        dest.writeString(transcription);
        dest.writeString(translationRus);
        dest.writeString(translationUkr);
        dest.writeString(category);
        dest.writeString(categoryRus);
        dest.writeString(categoryUkr);
        dest.writeInt(difficulty);
        dest.writeInt(isMain);
        dest.writeInt(guessed);
        dest.writeInt(notGuessed);
        dest.writeInt(isFavorite);
    }
}
