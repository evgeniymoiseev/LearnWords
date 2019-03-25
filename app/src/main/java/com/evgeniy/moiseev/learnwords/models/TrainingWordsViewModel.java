package com.evgeniy.moiseev.learnwords.models;

import android.app.Application;

import com.evgeniy.moiseev.learnwords.WordRepository;
import com.evgeniy.moiseev.learnwords.data.Word;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class TrainingWordsViewModel extends AndroidViewModel {
    private WordRepository mRepository;
    private LiveData<List<Word>> mAllWords;

    public TrainingWordsViewModel(@NonNull Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void updateGuesses(String word, int guessed, int notGuessed) {
        mRepository.updateGuesses(word, guessed, notGuessed);
    }
}
