package com.evgeniy.moiseev.learnwords.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface IrregularVerbDAO {

    @Query("SELECT * FROM irregular_verbs ORDER BY word1 COLLATE NOCASE ASC")
    LiveData<List<IrregularVerb>> getAll();

    @Insert
    void insertList(List<IrregularVerb> irregularVerbs);

    @Query("UPDATE irregular_verbs SET trained = :trained WHERE word1 = :word1")
    void updateTrained(String word1, int trained);

    @Query("UPDATE irregular_verbs SET trained = 0")
    int clearIrregulars();
}
