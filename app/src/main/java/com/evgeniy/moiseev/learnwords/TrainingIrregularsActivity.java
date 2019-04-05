package com.evgeniy.moiseev.learnwords;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.IrregularVerb;
import com.evgeniy.moiseev.learnwords.data.SimpleIrregular;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.evgeniy.moiseev.learnwords.view.CustomViewPageIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class TrainingIrregularsActivity extends AppCompatActivity implements FragmentIrregularGame.OnFragmentIrregularGameListener,
        IrregularCardsFragment.OnIrregularCardsFragmentListener, TextToSpeech.OnInitListener, FragmentIrregularTestEnds.OnFragmentResultListener {
    private static final int NUMBER_OF_ONCE_TRAIN_WORDS = 10;
    private static final int DEFAULT_DELAY_FRAGMENT_CHANGE = 1500;

    private CustomViewPageIndicator mIndicator;
    private TextView mTextViewCount;
    private int mNumberRepeat;
    private String mMarked;
    private Handler mHandler;
    private List<IrregularVerb> mVerbs;
    private Set<IrregularVerb> mTrainingVerbs;
    private ArrayList<SimpleIrregular> mSimpleIrregulars;
    private IrregularVerb mCurrentIrregularVerb;
    private int mCurrentTrainingPosition;
    private TextToSpeech mTextToSpeech;
    //private boolean mTestEnded;

    private Observer<List<IrregularVerb>> observer = new Observer<List<IrregularVerb>>() {
        @Override
        public void onChanged(List<IrregularVerb> irregularVerbs) {
            if (mMarked.equals("all")) {
                mVerbs = irregularVerbs;
            } else if (mMarked.equals("marked")) {
                mVerbs = new ArrayList<>();
                for (IrregularVerb i : irregularVerbs) {
                    if (i.getTrained() == 1)
                        mVerbs.add(i);
                }
            }
            mTextViewCount.setText(String.valueOf(mVerbs.size()));
            if (mVerbs.size() > 0) {
                mTrainingVerbs = getRandomTrainingVerbs();
                showOfferCardsDialog();
            } else showNotEnoughWordsDialog();
        }
    };
    private DialogInterface.OnClickListener positiveButtonShowCardsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mIndicator.setType(CustomViewPageIndicator.TYPE_SINGLE_CHOICE);
            mIndicator.setIndicatorCount(mTrainingVerbs.size());
            mIndicator.setIndicator(0, true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, IrregularCardsFragment.newInstance(new ArrayList<>(mTrainingVerbs)))
                    .commit();
        }
    };
    private DialogInterface.OnClickListener negativeButtonShowCardsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            startNewGame();
        }
    };
    private Runnable startNewFragmentRunnable = new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.item_animation_fall_down, R.anim.item_animation_fall_down_out)
                    .replace(R.id.fragmentContainer, FragmentIrregularGame.newInstance(
                            mCurrentIrregularVerb.getWord1(),
                            mCurrentIrregularVerb.getWord2(),
                            mCurrentIrregularVerb.getWord3()))
                    .commit();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_irregulars);

        mTextToSpeech = new TextToSpeech(this, this);
        mNumberRepeat = getIntent().getIntExtra(IrregularVerbsModeActivity.EXTRA_NUMBER, 0);
        mMarked = getIntent().getStringExtra(IrregularVerbsModeActivity.EXTRA_MARKED);

        mIndicator = findViewById(R.id.pageIndicator);
        mIndicator.setType(CustomViewPageIndicator.TYPE_SINGLE_CHOICE);
        mIndicator.setIndicatorCount(mNumberRepeat);

        mTextViewCount = findViewById(R.id.textViewVerbsCount);
        mSimpleIrregulars = new ArrayList<>();

        mHandler = new Handler();

        ViewModelProviders.of(this).get(DictionaryViewModel.class).getAllIrregularVerbs().observe(this, observer);

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

    private IrregularVerb getRandomIrregularVerb() {
        ArrayList<IrregularVerb> temp = new ArrayList<>(mTrainingVerbs);
        return temp.get(new Random(System.currentTimeMillis()).nextInt(mTrainingVerbs.size()));
    }

    private Set<IrregularVerb> getRandomTrainingVerbs() {
        mTrainingVerbs = new HashSet<>();
        if (mVerbs.size() > NUMBER_OF_ONCE_TRAIN_WORDS) {
            while (mTrainingVerbs.size() != NUMBER_OF_ONCE_TRAIN_WORDS) {
                mTrainingVerbs.add(mVerbs.get(new Random(System.currentTimeMillis()).nextInt(mVerbs.size())));
            }
        } else {
            mTrainingVerbs.addAll(mVerbs);
        }
        return mTrainingVerbs;
    }

    @Override
    public void onButtonTestClicked(SimpleIrregular simpleIrregular) {
        mSimpleIrregulars.add(simpleIrregular);
        mIndicator.setIndicator(mCurrentTrainingPosition, simpleIrregular.isRight());
        mCurrentTrainingPosition++;

        if (mCurrentTrainingPosition != mNumberRepeat) {
            mCurrentIrregularVerb = getRandomIrregularVerb();
            mHandler.postDelayed(startNewFragmentRunnable, DEFAULT_DELAY_FRAGMENT_CHANGE);
        } else {
            //End of training
            //mTestEnded = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, FragmentIrregularTestEnds.newInstance(mSimpleIrregulars))
                            .commit();
                }
            }, DEFAULT_DELAY_FRAGMENT_CHANGE);
        }
    }

    @Override
    public void onPositionChanged(final int newPosition) {
        mIndicator.setIndicator(newPosition, true);
    }

    @Override
    public void onStartTrainingClicked() {
        startNewGame();
    }

    private void showNotEnoughWordsDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.string_not_enough_words)
                .setMessage(R.string.string_not_enough_irregulars_description)
                .setIcon(R.drawable.ic_error)
                .setCancelable(false)
                .setPositiveButton(R.string.string_back_to_choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrainingIrregularsActivity.this, IrregularVerbsModeActivity.class);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }

    private void startNewGame() {
        mSimpleIrregulars.clear();
        mIndicator.setType(CustomViewPageIndicator.TYPE_SEQUENCE);
        mIndicator.setIndicatorCount(mNumberRepeat);
        mCurrentTrainingPosition = 0;
        mCurrentIrregularVerb = getRandomIrregularVerb();
        mHandler.postDelayed(startNewFragmentRunnable, DEFAULT_DELAY_FRAGMENT_CHANGE);
    }

    @Override
    public void onNextVerbsClicked() {
        mTrainingVerbs = getRandomTrainingVerbs();
        mIndicator.setIndicator(0, true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, IrregularCardsFragment.newInstance(new ArrayList<>(mTrainingVerbs)))
                .commit();
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
    public void onContinueClicked() {
        //mTestEnded = false;
        showOfferCardsDialog();
    }

    @Override
    public void onNewVerbsClicked() {
        //mTestEnded = false;
        mTrainingVerbs = getRandomTrainingVerbs();
        showOfferCardsDialog();
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.string_back_home)
                //.setMessage(mTestEnded ? R.string.string_saved_progress : R.string.string_lost_progress)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TrainingIrregularsActivity.this, HomeActivity.class);
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
}
