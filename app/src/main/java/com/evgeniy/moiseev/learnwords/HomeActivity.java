package com.evgeniy.moiseev.learnwords;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.evgeniy.moiseev.learnwords.data.WordDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {
    private CardView cardViewVocabulary;
    private CardView cardViewTrainingVerbs;
    private CardView cardViewTrainingWords;
    private CardView cardViewHangman;

    private FragmentManager fragmentManager;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mPosition;

    private View.OnClickListener cardViewVocabularyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, DictionariesActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener cardViewTrainingWordsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, TrainingModeActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener cardViewTrainingVerbsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, IrregularVerbsModeActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener cardViewHangmanListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HomeActivity.this, HangmanActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        populateDatabases();
        mPosition = new Random(System.currentTimeMillis()).nextInt(3) + 1;
        cardViewVocabulary = findViewById(R.id.cardViewVocabulary);
        cardViewTrainingVerbs = findViewById(R.id.cardViewTrainingVerbs);
        cardViewTrainingWords = findViewById(R.id.cardViewTrainingWords);
        cardViewHangman = findViewById(R.id.cardViewHangman);

        cardViewVocabulary.setOnClickListener(cardViewVocabularyListener);
        cardViewTrainingVerbs.setOnClickListener(cardViewTrainingVerbsListener);
        cardViewTrainingWords.setOnClickListener(cardViewTrainingWordsListener);
        cardViewHangman.setOnClickListener(cardViewHangmanListener);

        fragmentManager = getSupportFragmentManager();
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mPosition++;
                if (mPosition > 3) {
                    mPosition = 1;
                }
                selectDot(mPosition, 350);
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragmentVocabulariesImages, FragmentPreviewVocabularies.newInstance(mPosition)).commit();
                mHandler.postDelayed(this, 5000);
            }
        };
    }

    private void selectDot(final int position, long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView dot1 = findViewById(R.id.dot1);
                ImageView dot2 = findViewById(R.id.dot2);
                ImageView dot3 = findViewById(R.id.dot3);
                switch (position) {
                    case 1:
                        dot1.setImageResource(R.drawable.drawable_green_circle);
                        dot2.setImageResource(R.drawable.drawable_grey_circle);
                        dot3.setImageResource(R.drawable.drawable_grey_circle);
                        break;
                    case 2:
                        dot1.setImageResource(R.drawable.drawable_grey_circle);
                        dot2.setImageResource(R.drawable.drawable_green_circle);
                        dot3.setImageResource(R.drawable.drawable_grey_circle);
                        break;
                    case 3:
                        dot1.setImageResource(R.drawable.drawable_grey_circle);
                        dot2.setImageResource(R.drawable.drawable_grey_circle);
                        dot3.setImageResource(R.drawable.drawable_green_circle);
                        break;
                }
            }
        }, delay);

    }

    private void startPreviewDictionaries() {
        selectDot(mPosition, 0);
        fragmentManager.beginTransaction().replace(R.id.fragmentVocabulariesImages, FragmentPreviewVocabularies.newInstance(mPosition)).commit();
        mHandler.postDelayed(mRunnable, 5000);
    }

    private void populateDatabases() {
        Reader gsonReaderWords = null;
        Reader gsonReaderIrregularVerbs = null;
        try {
            gsonReaderWords = new BufferedReader(new InputStreamReader(getAssets().open("words.json")));
            gsonReaderIrregularVerbs = new BufferedReader(new InputStreamReader(getAssets().open("irregular verbs.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!new File(getDatabasePath(WordDatabase.DATABASE_NAME).getAbsolutePath()).exists()) {
            new WordDatabase.PopulateDbAsync(WordDatabase.getDatabase(this)).execute(gsonReaderWords, gsonReaderIrregularVerbs);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPreviewDictionaries();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.string_exit)
                .setMessage(R.string.string_confirm_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.string_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton(R.string.string_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }
}