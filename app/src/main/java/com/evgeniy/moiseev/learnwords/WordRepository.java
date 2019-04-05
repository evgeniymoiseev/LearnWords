package com.evgeniy.moiseev.learnwords;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.evgeniy.moiseev.learnwords.data.IrregularVerb;
import com.evgeniy.moiseev.learnwords.data.IrregularVerbDAO;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.data.WordDAO;
import com.evgeniy.moiseev.learnwords.data.WordDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WordRepository {
    public static final String LOG_TAG = "repository_logs";
    private WordDAO mWordDao;
    private IrregularVerbDAO mIrregularDao;
    private LiveData<List<Word>> mAllWords;
    private LiveData<List<IrregularVerb>> mAllIrregularWords;
    private LiveData<List<Word>> mFavorites;
    private MutableLiveData<List<Word>> mListWordsByWord;
    private MutableLiveData<List<Word>> mListWordsByCategoryAndDifficulty;
    private MutableLiveData<List<Word>> mListWordsByDifficulty;

    public WordRepository(Application application) {
        WordDatabase db = WordDatabase.getDatabase(application);
        mWordDao = db.wordDAO();
        mIrregularDao = db.irregularDAO();
        mAllWords = mWordDao.getAll();
        mAllIrregularWords = mIrregularDao.getAll();
        mFavorites = mWordDao.getFavorites();
        mListWordsByWord = new MutableLiveData<>();
        mListWordsByCategoryAndDifficulty = new MutableLiveData<>();
        mListWordsByDifficulty = new MutableLiveData<>();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public LiveData<List<Word>> getFavorites() {
        return mFavorites;
    }

    public LiveData<List<IrregularVerb>> getAllIrregularVerbs() {
        return mAllIrregularWords;
    }

    public LiveData<List<Word>> getByWord(String word) {
        new listWordsByWordAsyncTask(mWordDao).execute(word);
        return mListWordsByWord;
    }

    public LiveData<List<Word>> getByCategoryAndDifficulty(String... filter) {
        new listByCategoryAndDifficulty(mWordDao).execute(filter);
        return mListWordsByCategoryAndDifficulty;
    }

    public LiveData<List<Word>> getByDifficulty(String difficulty) {
        new listByDifficulty(mWordDao).execute(difficulty);
        return mListWordsByDifficulty;
    }

    public void updateGuesses(String word, int guessed, int notGuessed) {
        new updateGuesses(mWordDao, guessed, notGuessed).execute(word);
    }

    public void insertList(List<Word> words) {
        new insertAsyncTask(mWordDao).execute(words);
    }

    public void delete(String word) {
        new deleteAsyncTask(mWordDao).execute(word);
    }

    public void updateDifficulty(String word, int difficulty) {
        new updateDifficultyAsyncTask(mWordDao, difficulty).execute(word);
    }

    public void updateTrained(String word1, int trained) {
        new updateTrainedAsynckTask(mIrregularDao, trained).execute(word1);
    }

    private static class updateTrainedAsynckTask extends AsyncTask<String, Void, Void> {
        private IrregularVerbDAO mDao;
        private int mTrained;

        public updateTrainedAsynckTask(IrregularVerbDAO irregularDao, int trained) {
            mDao = irregularDao;
            mTrained = trained;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mDao.updateTrained(strings[0], mTrained);
            return null;
        }
    }

    public void updateFavorite(String word, int isFavorite) {
        new updateFavoriteAsyncTask(mWordDao, isFavorite).execute(word);
    }

    public void clearFavorites() {
        new clearFavoritesAsyncTask(mWordDao).execute();
    }

    public void clearIrregulars() {
        new clearIrregularsAsyncTask(mIrregularDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<List<Word>, Void, Void> {

        private WordDAO mAsyncTaskDao;

        insertAsyncTask(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(List<Word>... words) {
            mAsyncTaskDao.insertList(words[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {
        private WordDAO mAsyncTaskDao;

        deleteAsyncTask(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(String... words) {
            int deletedRowsCount = mAsyncTaskDao.delete(words[0]);
            Log.d(LOG_TAG, "Deleted " + String.valueOf(deletedRowsCount) + " rows");
            return null;
        }
    }

    private static class updateDifficultyAsyncTask extends AsyncTask<String, Void, Integer> {
        private int mDifficulty;
        private WordDAO mAsyncTaskDao;

        updateDifficultyAsyncTask(WordDAO dao, int difficulty) {
            mAsyncTaskDao = dao;
            mDifficulty = difficulty;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int updatedRowsCount = mAsyncTaskDao.updateDifficulty(strings[0], mDifficulty);
            Log.d(LOG_TAG, "Updated " + String.valueOf(updatedRowsCount) + " rows");
            return null;
        }
    }

    private static class updateFavoriteAsyncTask extends AsyncTask<String, Void, Integer> {
        private int mIsFavorite;
        private WordDAO mAsyncTaskDao;

        updateFavoriteAsyncTask(WordDAO dao, int isFavorite) {
            mAsyncTaskDao = dao;
            mIsFavorite = isFavorite;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int updatedRowsCount = mAsyncTaskDao.updateFavorite(strings[0], mIsFavorite);
            Log.d(LOG_TAG, "Updated " + String.valueOf(updatedRowsCount) + " rows");
            return null;
        }
    }

    private class listWordsByWordAsyncTask extends AsyncTask<String, Void, List<Word>> {
        private WordDAO mAsyncTaskDao;

        listWordsByWordAsyncTask(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Word> doInBackground(String... strings) {
            return mAsyncTaskDao.getByWord(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Word> words) {
            mListWordsByWord.setValue(words);
        }
    }

    private class listByCategoryAndDifficulty extends AsyncTask<String, Void, List<Word>> {
        private WordDAO mAsyncTaskDao;

        listByCategoryAndDifficulty(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Word> doInBackground(String... filter) {
            switch (filter.length) {
                case 1:
                    return mAsyncTaskDao.getByCategory(filter[0]);
                case 2:
                    return mAsyncTaskDao.getByCategoryAndDifficulty(filter[0], Integer.parseInt(filter[1]));
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Word> words) {
            mListWordsByCategoryAndDifficulty.setValue(words);
        }
    }

    private class listByDifficulty extends AsyncTask<String, Void, List<Word>> {
        private WordDAO mAsyncTaskDao;

        listByDifficulty(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<Word> doInBackground(String... strings) {
            return mAsyncTaskDao.getByDifficulty(Integer.parseInt(strings[0]));
        }

        @Override
        protected void onPostExecute(List<Word> words) {
            mListWordsByDifficulty.setValue(words);
        }
    }

    private static class updateGuesses extends AsyncTask<String, Void, Void> {
        private WordDAO mAsyncTaskDao;
        private int mGuessed;
        private int mNotGuessed;

        public updateGuesses(WordDAO dao, int guessed, int notGuessed) {
            mAsyncTaskDao = dao;
            mGuessed = guessed;
            mNotGuessed = notGuessed;
        }

        @Override
        protected Void doInBackground(String... strings) {
            mAsyncTaskDao.updateGuesses(strings[0], mGuessed, mNotGuessed);
            return null;
        }
    }

    private static class clearFavoritesAsyncTask extends AsyncTask<Void, Void, Integer> {
        private WordDAO mAsyncTaskDao;

        clearFavoritesAsyncTask(WordDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int updatedRowsCount = mAsyncTaskDao.clearFavorites();
            Log.d(LOG_TAG, "Updated " + String.valueOf(updatedRowsCount) + " rows");
            return null;
        }
    }

    private static class clearIrregularsAsyncTask extends AsyncTask<Void, Void, Integer> {
        private IrregularVerbDAO mAsyncTaskDao;

        clearIrregularsAsyncTask(IrregularVerbDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int updatedRowsCount = mAsyncTaskDao.clearIrregulars();
            Log.d(LOG_TAG, "Updated " + String.valueOf(updatedRowsCount) + " rows");
            return null;
        }
    }
}
