package com.evgeniy.moiseev.learnwords;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.evgeniy.moiseev.learnwords.view.CustomViewWordLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class HangmanGameActivity extends AppCompatActivity {
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_CATEGORY_ORIGIN = "category origin";
    public static final String EXTRA_MIN_LETTERS = "min letters";
    public static final int GUESSES = 9;
    public static final int PROGRESS_MULTIPLIER = 100;

    private CustomViewWordLabel mLabel;
    private Button buttonRepeat;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private TextView textViewVictory;
    private TextView textViewLoose;
    private ImageView image;
    private LinearLayout linearLayoutLetters1;
    private LinearLayout linearLayoutLetters2;
    private LinearLayout linearLayoutLetters3;
    private ConstraintLayout linearWord;
    private List<TextView> textViewsLetters;
    private List<TextView> textViewsWordLetters;
    private List<TextView> textViewsWordLettersStubs;
    private HashSet<Character> guessedCharacters;
    private ArrayList<String> mWords;
    private Map<Integer, Integer> pictures;
    private ArrayList<Integer> runaway;
    private String category;
    private int minLetters;

    private Vibrator mVibrator;
    private DisplayMetrics mDisplayMetrics;
    private int mButtonSizeEng;
    private int mCurrentWordSize;

    private String mWord;
    private int count;
    private int guesses;

    private Observer<List<Word>> observer = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            for (Word w : words) {
                if (!w.getWord().contains("-") && !w.getWord().contains(" ") &&
                        w.getCategory().equals(category) && w.getWord().length() >= minLetters
                        && w.getWord().length() <= 13) {
                    mWords.add(w.getWord());
                }
            }
            setupNewWord();
        }
    };
    private View.OnClickListener letterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean result = false;
            String letter = ((TextView) v).getText().toString();
            v.setAlpha(1.0f);

            //If letter is present show it
            for (int i = 0; i < textViewsWordLetters.size(); i++) {
                if (textViewsWordLetters.get(i).getText().toString().equals(letter)) {
                    guessedCharacters.add(letter.charAt(0));
                    result = true;
                    count++;
                    Animator a1 = AnimatorInflater.loadAnimator(HangmanGameActivity.this, R.animator.out_animation);
                    a1.setTarget(textViewsWordLettersStubs.get(i));
                    a1.start();
                    Animator a2 = AnimatorInflater.loadAnimator(HangmanGameActivity.this, R.animator.in_animation);
                    a2.setTarget(textViewsWordLetters.get(i));
                    a2.start();
                }
            }

            //Update variables depend of result
            if (result) {
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorGuessed));
                ((TextView) v).setTypeface(null, Typeface.BOLD);
                ((TextView) v).setTextSize(18.0f);
            } else {
                vibrate(mVibrator, 50);
                guesses--;
                textViewProgress.setText(String.valueOf(guesses));
                image.setImageResource(pictures.get(guesses));
                ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), guesses * PROGRESS_MULTIPLIER)
                        .setDuration(500).start();
                ObjectAnimator o1 = ObjectAnimator.ofFloat(textViewProgress, "textSize",
                        15, 20).setDuration(250);
                ObjectAnimator o2 = ObjectAnimator.ofFloat(textViewProgress, "textSize",
                        20, 15).setDuration(250);
                AnimatorSet set = new AnimatorSet();
                set.playSequentially(o1, o2);
                set.start();

                ((TextView) v).setTextColor(getResources().getColor(R.color.colorError));
                ((TextView) v).setTypeface(null, Typeface.BOLD);
                ((TextView) v).setTextSize(18.0f);
                ((TextView) v).setPaintFlags(((TextView) v).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            //If the game ends
            if (count == mWord.length()) {
                //If win
                ObjectAnimator.ofFloat(textViewVictory, "scaleX", 1.0f).setDuration(1000).start();
                ObjectAnimator.ofFloat(textViewVictory, "scaleY", 1.0f).setDuration(1000).start();
                disableLetters();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonRepeat.setVisibility(View.VISIBLE);
                    }
                }, 1500);
                if (guesses < 6) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        int count = 0;

                        @Override
                        public void run() {
                            if (count < runaway.size()) {
                                image.setImageResource(runaway.get(count));
                                count++;
                            }
                            handler.post(this);
                        }
                    }, 1500);

                }

            } else if (guesses == 0) {
                disableLetters();
                ObjectAnimator.ofFloat(textViewLoose, "scaleX", 1.0f).setDuration(1000).start();
                ObjectAnimator.ofFloat(textViewLoose, "scaleY", 1.0f).setDuration(1000).start();
                //If loose open cards
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < textViewsWordLetters.size(); i++) {
                            if (!guessedCharacters.contains(textViewsWordLetters.get(i).getText().toString().charAt(0))) {
                                Animator a1 = AnimatorInflater.loadAnimator(HangmanGameActivity.this, R.animator.out_animation);
                                a1.setTarget(textViewsWordLettersStubs.get(i));
                                a1.start();
                                Animator a2 = AnimatorInflater.loadAnimator(HangmanGameActivity.this, R.animator.in_animation);
                                a2.setTarget(textViewsWordLetters.get(i));
                                a2.start();
                            }
                        }

                        buttonRepeat.setVisibility(View.VISIBLE);
                    }
                }, 1500);

            }

            v.setOnClickListener(null);
        }
    };
    private View.OnClickListener repeatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startGame();
            setupNewWord();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman_game);

        mLabel = findViewById(R.id.label);
        buttonRepeat = findViewById(R.id.buttonRepeat);
        buttonRepeat.setOnClickListener(repeatListener);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(GUESSES * PROGRESS_MULTIPLIER);
        textViewProgress = findViewById(R.id.textViewProgress);
        image = findViewById(R.id.image);
        linearLayoutLetters1 = findViewById(R.id.letters1);
        linearLayoutLetters2 = findViewById(R.id.letters2);
        linearLayoutLetters3 = findViewById(R.id.letters3);
        textViewVictory = findViewById(R.id.textViewVictory);
        textViewLoose = findViewById(R.id.textViewLoose);

        textViewsLetters = new ArrayList<>();
        textViewsWordLetters = new ArrayList<>();
        textViewsWordLettersStubs = new ArrayList<>();
        guessedCharacters = new HashSet<>();
        populatePicturesMap();

        linearWord = findViewById(R.id.linearWord);
        runaway = new ArrayList<>();
        runaway.add(R.drawable.hangman_runaway1);
        runaway.add(R.drawable.hangman_runaway2);
        runaway.add(R.drawable.hangman_runaway3);
        runaway.add(R.drawable.hangman_runaway4);
        runaway.add(R.drawable.hangman_runaway5);
        runaway.add(R.drawable.hangman_runaway6);
        runaway.add(R.drawable.hangman_runaway7);
        runaway.add(R.drawable.hangman_runaway8);
        runaway.add(R.drawable.hangman_runaway9);
        runaway.add(R.drawable.hangman_runaway10);

        mLabel.setText(toSentenceCase(getIntent().getStringExtra(EXTRA_CATEGORY_ORIGIN)));
        category = getIntent().getStringExtra(EXTRA_CATEGORY);
        minLetters = getIntent().getIntExtra(EXTRA_MIN_LETTERS, 3);

        mDisplayMetrics = getResources().getDisplayMetrics();
        mButtonSizeEng = mDisplayMetrics.widthPixels / 11;
        mCurrentWordSize = mDisplayMetrics.widthPixels / 17;
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mWords = new ArrayList<>();
        char[] letters1 = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        char[] letters2 = new char[]{
                'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',};
        char[] letters3 = new char[]{
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        int margin = (int) (2 * mDisplayMetrics.density);
        for (int i = 0; i < letters1.length; i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mButtonSizeEng, mButtonSizeEng);
            params.setMargins(margin, 0, margin, 0);
            textView.setText(String.valueOf(letters1[i]));
            textView.setTextSize(16.0f);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(letterListener);
            linearLayoutLetters1.addView(textView, params);
            textViewsLetters.add(textView);
        }
        for (int i = 0; i < letters2.length; i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mButtonSizeEng, mButtonSizeEng);
            params.setMargins(margin, 0, margin, 0);
            textView.setText(String.valueOf(letters2[i]));
            textView.setTextSize(16.0f);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(letterListener);
            linearLayoutLetters2.addView(textView, params);
            textViewsLetters.add(textView);
        }
        for (int i = 0; i < letters3.length; i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mButtonSizeEng, mButtonSizeEng);
            params.setMargins(margin, 0, margin, 0);
            textView.setText(String.valueOf(letters3[i]));
            textView.setTextSize(16.0f);
            textView.setGravity(Gravity.CENTER);
            textView.setOnClickListener(letterListener);
            linearLayoutLetters3.addView(textView, params);
            textViewsLetters.add(textView);
        }

        startGame();

        ViewModelProviders.of(this).get(DictionaryViewModel.class).getAllWords().observe(this, observer);
    }

    private String toSentenceCase(String string) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private void vibrate(Vibrator vibrator, int milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else vibrator.vibrate(milliseconds);
    }

    private void populatePicturesMap() {
        pictures = new HashMap<>();
        pictures.put(9, R.drawable.hangman1);
        pictures.put(8, R.drawable.hangman2);
        pictures.put(7, R.drawable.hangman3);
        pictures.put(6, R.drawable.hangman4);
        pictures.put(5, R.drawable.hangman5);
        pictures.put(4, R.drawable.hangman6);
        pictures.put(3, R.drawable.hangman7);
        pictures.put(2, R.drawable.hangman8);
        pictures.put(1, R.drawable.hangman9);
        pictures.put(0, R.drawable.hangman10);
    }

    private void startGame() {
        count = 0;
        guesses = GUESSES;
        textViewProgress.setText(String.valueOf(guesses));
        ObjectAnimator.ofInt(progressBar, "progress", GUESSES * PROGRESS_MULTIPLIER).start();
        textViewVictory.setScaleX(0.0f);
        textViewVictory.setScaleY(0.0f);
        textViewLoose.setScaleX(0.0f);
        textViewLoose.setScaleY(0.0f);
        image.setImageResource(pictures.get(guesses));
        for (TextView t : textViewsLetters) {
            t.setEnabled(true);
            t.setTypeface(null, Typeface.NORMAL);
            t.setTextSize(16.0f);
            t.setOnClickListener(letterListener);
            t.setTextColor(Color.BLACK);
            t.setAlpha(0.7f);
            t.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }
        textViewsWordLetters.clear();
        textViewsWordLettersStubs.clear();
        guessedCharacters.clear();
    }

    private void setupNewWord() {
        linearWord.removeAllViews();
        mWord = mWords.get(new Random(System.currentTimeMillis()).nextInt(mWords.size())).toUpperCase();

        for (int i = 0; i < mWord.length(); i++) {
            TextView textView = new TextView(HangmanGameActivity.this);
            textView.setId(i);
            textView.setAlpha(0.0f);
            TextView textViewStub = new TextView(HangmanGameActivity.this);

            ConstraintLayout.LayoutParams paramsC = new ConstraintLayout.LayoutParams(mCurrentWordSize, (int) (mCurrentWordSize * 1.5));
            if (i == 0) {
                paramsC.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.rightToLeft = i + 1;
                paramsC.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            } else if (i == mWord.length() - 1) {
                paramsC.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.leftToRight = i - 1;
                paramsC.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            } else {
                paramsC.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                paramsC.leftToRight = i - 1;
                paramsC.rightToLeft = i + 1;
            }
            int margin = (int) (2 * mDisplayMetrics.density);
            paramsC.setMargins(margin, 0, margin, 0);

            textView.setText(String.valueOf(mWord.charAt(i)));
            textView.setGravity(Gravity.CENTER);
            textViewStub.setBackgroundColor(getResources().getColor(R.color.colorNotSelectedTab));
            textView.setBackgroundColor(getResources().getColor(R.color.colorNotSelectedTab));

            linearWord.addView(textView, paramsC);
            linearWord.addView(textViewStub, paramsC);
            textViewsWordLetters.add(textView);
            textViewsWordLettersStubs.add(textViewStub);
        }
    }

    private void disableLetters() {
        for (TextView t : textViewsLetters) {
            t.setOnClickListener(null);
            t.setAlpha(0.3f);
        }
    }
}
