package com.evgeniy.moiseev.learnwords;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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
    private CardView cardViewTraining;

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
    private View.OnClickListener cardViewTrainingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.dialog_what_train_view, null);
            final AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                    .setCancelable(false)
                    .setView(view)
                    .create();
            view.findViewById(R.id.buttonWords).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, TrainingModeActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        populateDatabases();
        mPosition = new Random(System.currentTimeMillis()).nextInt(3) + 1;
        cardViewVocabulary = findViewById(R.id.cardViewVocabulary);
        cardViewTraining = findViewById(R.id.cardViewTraining);

        cardViewVocabulary.setOnClickListener(cardViewVocabularyListener);
        cardViewTraining.setOnClickListener(cardViewTrainingListener);

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
}