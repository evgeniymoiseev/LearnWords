package com.evgeniy.moiseev.learnwords.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface WordDAO {

    @Query("SELECT * FROM words ORDER BY word COLLATE NOCASE ASC")
    LiveData<List<Word>> getAll();

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY word COLLATE NOCASE ASC")
    LiveData<List<Word>> getFavorites();

    @Query("SELECT * FROM words WHERE word = :word")
    List<Word> getByWord(String word);

    @Query("SELECT * FROM words WHERE category = :category")
    List<Word> getByCategory(String category);

    @Query("SELECT * FROM words WHERE difficulty = :difficulty")
    List<Word> getByDifficulty(int difficulty);

    @Query("SELECT * FROM words WHERE category = :category AND difficulty = :difficulty")
    List<Word> getByCategoryAndDifficulty(String category, int difficulty);

    @Query("SELECT COUNT(*) FROM words")
    int count();

    @Insert
    void insertList(List<Word> words);

    @Query("DELETE FROM words WHERE word = :word")
    int delete(String word);

    @Query("UPDATE words SET difficulty = :difficulty WHERE word = :word")
    int updateDifficulty(String word, int difficulty);

    @Query("UPDATE words SET guessed = :guessed, notGuessed = :notGuessed WHERE word = :word")
    int updateGuesses(String word, int guessed, int notGuessed);

    @Query("UPDATE words SET isFavorite = :isFavorite WHERE word = :word")
    int updateFavorite(String word, int isFavorite);

    @Query("UPDATE words SET isFavorite = 0")
    int clearFavorites();

}
