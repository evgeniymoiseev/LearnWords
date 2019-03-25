package com.evgeniy.moiseev.learnwords;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.SimpleWord;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.TrainingWordsViewModel;
import com.evgeniy.moiseev.learnwords.view.CustomViewPageIndicator;
import com.evgeniy.moiseev.learnwords.view.CustomViewWordLabel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class TrainingWordsActivity extends AppCompatActivity implements WordCardsFragment.OnFragmentCardsInteractionListener,
        TextToSpeech.OnInitListener, FragmentGame.OnAnswerFromFragmentListener, FragmentTestEnds.OnFragmentTestEndsInteractionListener {
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_ORIGIN_CATEGORY = "origin category";
    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_ORIGIN_DIFFICULTY = "origin difficulty";

    public static final int TYPE_FROM_ENG = 0;
    public static final int TYPE_TO_ENG = 1;
    public static final int TYPE_CHOICE_FROM_ENG = 2;
    public static final int TYPE_CHOICE_TO_ENG = 3;

    public static final int DEFAULT_TRAIN_CARDS_COUNT = 10;
    public static final int DEFAULT_TRAINING_COUNT = 20;

    private static final int DEFAULT_DELAY_FRAGMENT_CHANGE = 1000;

    private CustomViewPageIndicator mPageIndicator;
    private TextView textViewCategoryItemsCount;
    private ImageView imageViewCategoryIcon;

    private String mCategory;
    private String mDifficulty;
    private String mLocale;

    private List<Word> mWords;
    private ArrayList<Word> mTrainingWords;
    private HashSet<String> mChoiceWords;
    private HashSet<String> mTestedWords;
    private List<String> mWrongAnswers;
    private List<String> mRightAnswers;

    private String mCurrentWord;
    private ArrayList<String> mCurrentTranslations;
    private int mCurrentType;
    private int mCurrentCount;
    private boolean mFirstTimeLoadingWords;

    private TextToSpeech mTextToSpeech;
    private Handler mHandler;
    private TrainingWordsViewModel mViewModel;

    private Observer<List<Word>> listObserver = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            onListObtained(words);
        }
    };
    private DialogInterface.OnClickListener positiveButtonShowCardsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            mPageIndicator.setIndicatorCount(mWords.size() < DEFAULT_TRAIN_CARDS_COUNT ?
                    mWords.size() : DEFAULT_TRAIN_CARDS_COUNT);

            mPageIndicator.setType(CustomViewPageIndicator.TYPE_SINGLE_CHOICE);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, WordCardsFragment.newInstance(mTrainingWords, mLocale), "YYY")
                    .commit();
        }
    };
    private DialogInterface.OnClickListener negativeButtonShowCardsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mPageIndicator.setIndicatorCount(DEFAULT_TRAINING_COUNT);
            mPageIndicator.setType(CustomViewPageIndicator.TYPE_SEQUENCE);
            mWrongAnswers.clear();
            mRightAnswers.clear();
            mTestedWords.clear();
            mCurrentCount = 0;
            setRandomWordAndType();
            mHandler.post(startNewFragmentRunnable);
        }
    };
    private Runnable startNewFragmentRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentType == TYPE_FROM_ENG || mCurrentType == TYPE_TO_ENG)
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.item_animation_fall_down, R.anim.item_animation_fall_down_out)
                        .replace(R.id.fragmentContainer, FragmentGame.newInstance(mCurrentWord, mCurrentTranslations, mCurrentType))
                        .commit();
            else
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.item_animation_fall_down, R.anim.item_animation_fall_down_out)
                        .replace(R.id.fragmentContainer,
                                FragmentGame.newInstance(mCurrentWord, mCurrentTranslations, new ArrayList<>(mChoiceWords), mCurrentType))
                        .commit();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_words);
        mWrongAnswers = new ArrayList<>();
        mRightAnswers = new ArrayList<>();
        mTestedWords = new HashSet<>();
        mCurrentTranslations = new ArrayList<>();
        mChoiceWords = new HashSet<>();
        mFirstTimeLoadingWords = true;
        mHandler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocale = getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else {
            mLocale = getResources().getConfiguration().locale.getLanguage();
        }

        mTextToSpeech = new TextToSpeech(this, this);

        mCategory = getIntent().getStringExtra(EXTRA_CATEGORY);
        mDifficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);

        CustomViewWordLabel mLabel = findViewById(R.id.textViewCategory);
        mPageIndicator = findViewById(R.id.pageIndicator);
        textViewCategoryItemsCount = findViewById(R.id.categoryItemsCount);
        imageViewCategoryIcon = findViewById(R.id.categoryIcon);
        TextView textViewDifficulty = findViewById(R.id.difficulty);

        mLabel.setText(getIntent().getStringExtra(EXTRA_ORIGIN_CATEGORY));
        textViewDifficulty.setText(getIntent().getStringExtra(EXTRA_ORIGIN_DIFFICULTY));

        mViewModel = ViewModelProviders.of(this).get(TrainingWordsViewModel.class);
        mViewModel.getAllWords().observe(this, listObserver);
    }

    private void onListObtained(List<Word> words) {
        //Filter by category and difficulty
        mWords = getFilterableWords(words);

        if (mFirstTimeLoadingWords) {
            //Get min 10 words for training
            updateTrainingWords();

            //Set no repeatable words count
            List<Word> mNoRepeatableWords = getNoRepeatableWords(mWords);
            textViewCategoryItemsCount.setText(getResources().getString(R.string.string_words_count, String.valueOf(mNoRepeatableWords.size())));

            //Set words icon
            if (mCategory.equals("favorites"))
                imageViewCategoryIcon.setImageResource(R.drawable.ic_favorites);
            else imageViewCategoryIcon.setImageResource(getCategoryDrawableRes(mCategory));
            DrawableCompat.setTint(imageViewCategoryIcon.getDrawable(), getResources().getColor(R.color.colorIcons));

            //if not enough words for training - go back, else - an offer of view cards
            if (mNoRepeatableWords.size() < 5) {
                showNotEnoughWordsDialog();
            } else {
                showOfferCardsDialog();
            }
        }

        //List was already downloaded
        mFirstTimeLoadingWords = false;
    }

    private void showNotEnoughWordsDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.string_not_enough_words)
                .setMessage(R.string.string_not_enough_words_description)
                .setIcon(R.drawable.ic_error)
                .setCancelable(false)
                .setPositiveButton(R.string.string_back_to_choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrainingWordsActivity.this, TrainingModeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }

    private void showOfferCardsDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.string_show_cards)
                .setCancelable(false)
                .setPositiveButton(R.string.string_yes, positiveButtonShowCardsListener)
                .setNegativeButton(R.string.string_no, negativeButtonShowCardsListener)
                .create();
        dialog.show();
    }

    private void updateTrainingWords() {
        Collections.shuffle(mWords);
        mTrainingWords = getSublist(mWords,
                mWords.size() < DEFAULT_TRAIN_CARDS_COUNT ?
                        mWords.size() : DEFAULT_TRAIN_CARDS_COUNT);
    }

    private int getCategoryDrawableRes(String category) {
        Categories categories = new Categories();
        for (Category c : categories.getCategories()) {
            if (c.getCategoryEng().equals(category)) {
                return c.getDrawableResId();
            }
        }
        return -1;
    }

    private List<Word> getNoRepeatableWords(List<Word> words) {
        List<Word> noRepeatableWords = new ArrayList<>();
        for (Word word : words) {
            if (word.getIsMain() == 1) noRepeatableWords.add(word);
        }
        return noRepeatableWords;
    }

    private List<Word> getFilterableWords(List<Word> words) {
        List<Word> tempList = new ArrayList<>();
        List<Word> filterableWords = new ArrayList<>();

        switch (mCategory) {
            case "favorites":
                for (Word w : words) {
                    if (w.getIsFavorite() == 1) {
                        tempList.add(w);
                    }
                }
                break;
            case "all":
                tempList = words;
                break;
            default:
                for (Word w : words) {
                    if (w.getCategory().equals(mCategory)) {
                        tempList.add(w);
                    }
                }
                break;
        }

        if (mDifficulty.equals("-1")) {
            filterableWords = tempList;
        } else {
            for (Word w : tempList) {
                if (w.getDifficulty() == Integer.parseInt(mDifficulty)) {
                    filterableWords.add(w);
                }
            }
        }

        return filterableWords;
    }

    private ArrayList<Word> getSublist(List<Word> src, int to) {
        ArrayList<Word> words = new ArrayList<>();
        for (int i = 0; i < to; i++) {
            words.add(src.get(i));
        }
        return words;
    }

    @Override
    public void onPositionChanged(int newPosition) {
        mPageIndicator.setIndicator(newPosition, true);
        mTextToSpeech.setSpeechRate(1f);
        mTextToSpeech.speak(mTrainingWords.get(newPosition).getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onImageSpeechClicked(String word, boolean slow) {
        if (slow) {
            mTextToSpeech.setSpeechRate(0.5f);
            mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTextToSpeech.setSpeechRate(1f);
            mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onNextWordsClicked() {
        updateTrainingWords();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, WordCardsFragment.newInstance(mTrainingWords, mLocale))
                .commit();
    }

    @Override
    public void onStartTrainingClicked() {
        mPageIndicator.setIndicatorCount(DEFAULT_TRAINING_COUNT);
        mPageIndicator.setType(CustomViewPageIndicator.TYPE_SEQUENCE);
        mWrongAnswers.clear();
        mRightAnswers.clear();
        mTestedWords.clear();
        mCurrentCount = 0;
        setRandomWordAndType();
        mHandler.postDelayed(startNewFragmentRunnable, 0);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(findViewById(R.id.fragmentContainer),
                        getString(R.string.string_not_supported_language),
                        Snackbar.LENGTH_LONG).show();
            }
        } else Log.e("YYY", "Initialization Failed!");
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.string_back_home)
                .setMessage(R.string.string_lost_progress)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrainingWordsActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onAnswerReceived(String answer) {
        mPageIndicator.setIndicator(mCurrentCount, isGuessed(answer));

        mCurrentCount++;

        //Add to right/wrong and tested list
        switch (mCurrentType) {
            case TYPE_TO_ENG:
                mTextToSpeech.setSpeechRate(1f);
                mTextToSpeech.speak(isGuessed(answer) ? answer : mCurrentTranslations.get(0), TextToSpeech.QUEUE_FLUSH, null, null);
                if (isGuessed(answer)) {
                    mRightAnswers.add(getOriginalAnswer(answer));
                    mTestedWords.add(getOriginalAnswer(answer));
                } else {
                    mWrongAnswers.addAll(getRightEngWords());
                    mTestedWords.addAll(getRightEngWords());
                }
                break;
            case TYPE_FROM_ENG:
                mTextToSpeech.setSpeechRate(1f);
                mTextToSpeech.speak(mCurrentWord, TextToSpeech.QUEUE_FLUSH, null, null);
                mTestedWords.add(mCurrentWord);
                if (isGuessed(answer)) {
                    mRightAnswers.add(mCurrentWord);
                } else {
                    mWrongAnswers.add(mCurrentWord);
                }
                break;
            case TYPE_CHOICE_FROM_ENG:
                mTextToSpeech.setSpeechRate(1f);
                mTextToSpeech.speak(mCurrentWord, TextToSpeech.QUEUE_FLUSH, null, null);
                mTestedWords.add(mCurrentWord);
                if (isGuessed(answer)) {
                    mRightAnswers.add(mCurrentWord);
                } else {
                    mWrongAnswers.add(mCurrentWord);
                }
                break;
            case TYPE_CHOICE_TO_ENG:
                mTextToSpeech.setSpeechRate(1f);
                mTextToSpeech.speak(isGuessed(answer) ? answer : mCurrentTranslations.get(0), TextToSpeech.QUEUE_FLUSH, null, null);
                if (isGuessed(answer)) {
                    mRightAnswers.add(answer);
                    mTestedWords.add(answer);
                } else {
                    mWrongAnswers.addAll(getRightEngWords());
                    mTestedWords.addAll(getRightEngWords());
                }
                break;
        }

        //Start new word
        if (mCurrentCount != DEFAULT_TRAINING_COUNT) {
            setRandomWordAndType();
            mHandler.postDelayed(startNewFragmentRunnable, DEFAULT_DELAY_FRAGMENT_CHANGE);
        } else {
            //Get simple words for recycler view
            final List<SimpleWord> simpleWords = updateWordsGuesses();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, FragmentTestEnds.newInstance(new ArrayList<>(simpleWords))).commit();
                }
            }, DEFAULT_DELAY_FRAGMENT_CHANGE);
        }
    }

    private void setRandomWordAndType() {
        mCurrentWord = null;
        mCurrentTranslations.clear();
        mChoiceWords.clear();
        mCurrentType = -1;

        Random random = new Random(System.currentTimeMillis());
        mCurrentType = random.nextInt(4);
        switch (mCurrentType) {
            case TYPE_FROM_ENG:
                mCurrentWord = mTrainingWords.get(random.nextInt(mTrainingWords.size())).getWord();
                for (Word w : mWords) {
                    if (w.getWord().equals(mCurrentWord)) {
                        mCurrentTranslations.add(mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                                w.getTranslationUkr() : w.getTranslationRus());
                    }
                }
                break;
            case TYPE_TO_ENG:
                mCurrentWord = mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                        mTrainingWords.get(random.nextInt(mTrainingWords.size())).getTranslationUkr() :
                        mTrainingWords.get(random.nextInt(mTrainingWords.size())).getTranslationRus();
                for (Word w : mWords) {
                    String translation = mLocale.equals(DictionariesActivity.LOCALE_UKR) ? w.getTranslationUkr() : w.getTranslationRus();
                    if (mCurrentWord.equals(translation)) {
                        mCurrentTranslations.add(w.getWord());
                    }
                }
                break;
            case TYPE_CHOICE_FROM_ENG:
                //Setup word
                mCurrentWord = mTrainingWords.get(random.nextInt(mTrainingWords.size())).getWord();

                //Setup translations
                for (Word w : mWords) {
                    if (w.getWord().equals(mCurrentWord)) {
                        mCurrentTranslations.add(mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                                w.getTranslationUkr() : w.getTranslationRus());
                    }
                }

                //Setup choice buttons
                mChoiceWords.add(mCurrentTranslations.get(0));
                while (mChoiceWords.size() < 4) {
                    Word word = mTrainingWords.get(new Random(System.currentTimeMillis()).nextInt(mTrainingWords.size()));
                    mChoiceWords.add(mLocale.equals(DictionariesActivity.LOCALE_UKR) ? word.getTranslationUkr() : word.getTranslationRus());
                }

                break;
            case TYPE_CHOICE_TO_ENG:
                //Setup word
                mCurrentWord = mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                        mTrainingWords.get(random.nextInt(mTrainingWords.size())).getTranslationUkr() :
                        mTrainingWords.get(random.nextInt(mTrainingWords.size())).getTranslationRus();

                //Setup translations
                for (Word w : mWords) {
                    String translation = mLocale.equals(DictionariesActivity.LOCALE_UKR) ? w.getTranslationUkr() : w.getTranslationRus();
                    if (mCurrentWord.equals(translation)) {
                        mCurrentTranslations.add(w.getWord());
                    }
                }

                //Setup choice buttons
                mChoiceWords.add(mCurrentTranslations.get(0));
                while (mChoiceWords.size() < 4)
                    mChoiceWords.add(mTrainingWords.get(new Random(System.currentTimeMillis()).nextInt(mTrainingWords.size())).getWord());

        }
    }

    private List<String> getRightEngWords() {
        List<String> rightEngWords = new ArrayList<>();
        if (mLocale.equals(DictionariesActivity.LOCALE_UKR)) {
            for (Word w : mTrainingWords) {
                if (w.getTranslationUkr().equals(mCurrentWord)) {
                    rightEngWords.add(w.getWord());
                }
            }
        } else {
            for (Word w : mTrainingWords) {
                if (w.getTranslationRus().equals(mCurrentWord)) {
                    rightEngWords.add(w.getWord());
                }
            }
        }
        return rightEngWords;
    }

    private List<SimpleWord> updateWordsGuesses() {
        //Get actual guesses
        List<SimpleWord> simpleWordsActual = new ArrayList<>();
        for (String w : mTestedWords) {
            simpleWordsActual.add(new SimpleWord(w, getGuessedCount(w), getNotGuessedCount(w)));
        }

        //Update actual guesses
        List<SimpleWord> simpleWordsNewData = new ArrayList<>();
        for (String w : mTestedWords) {
            SimpleWord simpleWord = new SimpleWord(w, 0, 0);
            for (String s : mRightAnswers) {
                if (s.equals(simpleWord.getWord())) {
                    simpleWord.setGuessed(simpleWord.getGuessed() + 1);
                }
            }
            for (String s : mWrongAnswers) {
                if (s.equals(simpleWord.getWord())) {
                    simpleWord.setNotGuessed(simpleWord.getNotGuessed() - 1);
                }
            }
            simpleWordsNewData.add(simpleWord);
        }

        //Get result guesses and update them in database
        for (int i = 0; i < simpleWordsActual.size(); i++) {
            simpleWordsActual.get(i).setGuessed(simpleWordsActual.get(i).getGuessed() + simpleWordsNewData.get(i).getGuessed());
            simpleWordsActual.get(i).setNotGuessed(simpleWordsActual.get(i).getNotGuessed() - simpleWordsNewData.get(i).getNotGuessed());
            mViewModel.updateGuesses(
                    simpleWordsActual.get(i).getWord(),
                    simpleWordsActual.get(i).getGuessed(),
                    simpleWordsActual.get(i).getNotGuessed());
        }

        return simpleWordsNewData;
    }

    private int getGuessedCount(String word) {
        for (Word w : mWords) {
            if (w.getWord().equals(word)) {
                return w.getGuessed();
            }
        }
        return -1;
    }

    private int getNotGuessedCount(String word) {
        for (Word w : mWords) {
            if (w.getWord().equals(word)) {
                return w.getNotGuessed();
            }
        }
        return -1;
    }

    private boolean isGuessed(String answer) {
        boolean isGuessed = false;
        for (String s : mCurrentTranslations) {
            if (answer.toLowerCase().equals(s.toLowerCase()))
                isGuessed = true;
        }
        return isGuessed;
    }

    private String getOriginalAnswer(String answer) {
        for (String s : mCurrentTranslations) {
            if (answer.toLowerCase().equals(s.toLowerCase())) {
                Log.d("YYY", "Original answer = " + s);
                return s;
            }
        }
        return answer;
    }

    @Override
    public void onContinueClicked() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.string_show_cards)
                .setCancelable(false)
                .setPositiveButton(R.string.string_yes, positiveButtonShowCardsListener)
                .setNegativeButton(R.string.string_no, negativeButtonShowCardsListener)
                .create();
        dialog.show();
    }

    @Override
    public void onNewWordsClicked() {
        updateTrainingWords();
        showOfferCardsDialog();
    }
}
