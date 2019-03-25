package com.evgeniy.moiseev.learnwords.data;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Word.class, IrregularVerb.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "word_database";

    public abstract WordDAO wordDAO();

    public abstract IrregularVerbDAO irregularDAO();

    private static volatile WordDatabase INSTANCE;

    public static WordDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (WordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordDatabase.class,
                            DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public static class PopulateDbAsync extends AsyncTask<Reader, Void, Void> {

        private final WordDAO mDao;
        private final IrregularVerbDAO mIrregularDao;

        public PopulateDbAsync(WordDatabase db) {
            mDao = db.wordDAO();
            mIrregularDao = db.irregularDAO();
        }

        @Override
        protected Void doInBackground(final Reader... readers) {
            Gson gson = new Gson();
            List<Word> words;
            List<IrregularVerb> irregularVerbs;

            Type collectionType = new TypeToken<List<Word>>() {
            }.getType();
            Type collectionTypeIrregularList = new TypeToken<List<IrregularVerb>>() {
            }.getType();

            words = gson.fromJson(readers[0], collectionType);
            irregularVerbs = gson.fromJson(readers[1], collectionTypeIrregularList);

            mDao.insertList(words);
            mIrregularDao.insertList(irregularVerbs);
            return null;
        }
    }
}
