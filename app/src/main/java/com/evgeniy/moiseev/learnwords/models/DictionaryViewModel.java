package com.evgeniy.moiseev.learnwords.models;

import android.app.Application;

import com.evgeniy.moiseev.learnwords.WordRepository;
import com.evgeniy.moiseev.learnwords.data.IrregularVerb;
import com.evgeniy.moiseev.learnwords.data.Word;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class DictionaryViewModel extends AndroidViewModel {
    private WordRepository mRepository;
    private LiveData<List<Word>> mAllWords;
    private LiveData<List<IrregularVerb>> mAllIrregularVerbs;
    private LiveData<List<Word>> mFavorites;

    public DictionaryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
        mAllIrregularVerbs = mRepository.getAllIrregularVerbs();
        mFavorites = mRepository.getFavorites();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public LiveData<List<Word>> getFavorites() {
        return mFavorites;
    }

    public LiveData<List<Word>> getByCategory(String category) {
        return mRepository.getByCategoryAndDifficulty(category);
    }

    public LiveData<List<IrregularVerb>> getAllIrregularVerbs() {
        return mAllIrregularVerbs;
    }

    public LiveData<List<Word>> getListByWord(String word) {
        return mRepository.getByWord(word);
    }

    public List<Word> getNoRepeatableWords(List<Word> words, String category) {
        List<Word> noRepeatableWords = new ArrayList<>();
        for (Word word : words) {
            if (category.equals("all")) {
                if (word.getIsMain() == 1)
                    noRepeatableWords.add(word);
            } else {
                if (word.getIsMain() == 1 && word.getCategory().equals(category))
                    noRepeatableWords.add(word);
            }
        }
        return noRepeatableWords;
    }

    public void insertList(List<Word> words) {
        mRepository.insertList(words);
    }

    public void delete(String word) {
        mRepository.delete(word);
    }

    public void updateDifficulty(String word, int difficulty) {
        mRepository.updateDifficulty(word, difficulty);
    }

    public void updateFavorite(String word, int isFavorite) {
        mRepository.updateFavorite(word, isFavorite);
    }

    public void clearFavorites() {
        mRepository.clearFavorites();
    }

    public List<Word> getFilterableWords(List<Word> words, String pattern, String category) {
        List<Word> filteredList = new ArrayList<>();
        for (Word w : words) {
            if (w.getWord().toLowerCase().startsWith(pattern)) {
                filteredList.add(w);
            }
        }
        return getNoRepeatableWords(filteredList, category);
    }
}
