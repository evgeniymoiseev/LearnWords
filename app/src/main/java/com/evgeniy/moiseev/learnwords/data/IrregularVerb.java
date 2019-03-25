package com.evgeniy.moiseev.learnwords.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "irregular_verbs")
public class IrregularVerb implements Comparable<IrregularVerb> {

    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("word1")
    @Expose
    @ColumnInfo(name = "word1")
    private String word1;

    @SerializedName("word2")
    @Expose
    @ColumnInfo(name = "word2")
    private String word2;

    @SerializedName("word3")
    @ColumnInfo(name = "word3")
    @Expose
    private String word3;

    @SerializedName("transcription1")
    @ColumnInfo(name = "transcription1")
    @Expose
    private String transcription1;

    @SerializedName("transcription2")
    @ColumnInfo(name = "transcription2")
    @Expose
    private String transcription2;

    @SerializedName("transcription3")
    @ColumnInfo(name = "transcription3")
    @Expose
    private String transcription3;

    @SerializedName("translationRus")
    @ColumnInfo(name = "translationRus")
    @Expose
    private String translationRus;

    @SerializedName("translationUkr")
    @ColumnInfo(name = "translationUkr")
    @Expose
    private String translationUkr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord1() {
        return word1;
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

    public String getTranscription1() {
        return transcription1;
    }

    public void setTranscription1(String transcription1) {
        this.transcription1 = transcription1;
    }

    public String getTranscription2() {
        return transcription2;
    }

    public void setTranscription2(String transcription2) {
        this.transcription2 = transcription2;
    }

    public String getTranscription3() {
        return transcription3;
    }

    public void setTranscription3(String transcription3) {
        this.transcription3 = transcription3;
    }

    public String getTranslationRus() {
        return translationRus;
    }

    public void setTranslationRus(String translationRus) {
        this.translationRus = translationRus;
    }

    public String getTranslationUkr() {
        return translationUkr;
    }

    public void setTranslationUkr(String translationUkr) {
        this.translationUkr = translationUkr;
    }

    @Override
    public int compareTo(IrregularVerb o) {
        return word1.toLowerCase().compareTo(o.word1.toLowerCase());
    }
}
