package com.evgeniy.moiseev.learnwords;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class HangmanActivity extends AppCompatActivity {
    private Locale mPrimaryLocale;
    private String mLocale;
    private List<String> mListCategories;
    private ChipGroup mChipGroupCategory;
    private Chip mSelectedChip;
    private String mCategory;
    private Button buttonStartGame;
    private TextView mTextViewSeek;
    private SeekBar mSeekBar;
    private List<Word> mAllWords;
    private List<Word> mWords;

    private ChipGroup.OnCheckedChangeListener mChipGroupCategoryListener = new ChipGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ChipGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                group.getChildAt(i).setClickable(true);
            }
            mSelectedChip = (Chip) group.getChildAt(checkedId);
            mSelectedChip.setClickable(false);
            mCategory = mSelectedChip.getText().toString();
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int currProgress = progress;
                int min = 3;
                if (progress < min) {
                    currProgress = min;
                }
                mTextViewSeek.setText(String.valueOf(currProgress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            ObjectAnimator.ofFloat(mTextViewSeek, "textSize", 12f, 15f).start();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ObjectAnimator.ofFloat(mTextViewSeek, "textSize", 15f, 12f).start();
        }
    };
    private View.OnClickListener buttonStartGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (Word w : mAllWords) {
                if (!w.getWord().contains("-") && !w.getWord().contains(" ")
                        && w.getCategory().equals(String.valueOf(mSelectedChip.getTag()))
                        && w.getWord().length() >= Integer.parseInt(mTextViewSeek.getText().toString())) {
                    mWords.add(w);
                }
            }
            if (mWords.size() > 0) {
                v.setOnClickListener(null);
                Intent intent = new Intent(HangmanActivity.this, HangmanGameActivity.class);
                intent.putExtra(HangmanGameActivity.EXTRA_CATEGORY, String.valueOf(mSelectedChip.getTag()));
                intent.putExtra(HangmanGameActivity.EXTRA_CATEGORY_ORIGIN, mCategory);
                intent.putExtra(HangmanGameActivity.EXTRA_MIN_LETTERS, Integer.parseInt(mTextViewSeek.getText().toString()));
                startActivity(intent);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(HangmanActivity.this)
                        .setTitle(getResources().getString(R.string.string_no_words))
                        .setIcon(R.drawable.ic_error)
                        .setCancelable(false)
                        .setNeutralButton(R.string.string_back_to_choice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setMessage(R.string.string_no_words_message)
                        .create();
                dialog.show();
            }
        }
    };
    private Observer<List<Word>> observer = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            mAllWords = words;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman);

        mChipGroupCategory = findViewById(R.id.chipGroupCategory);
        buttonStartGame = findViewById(R.id.buttonStartGame);
        buttonStartGame.setOnClickListener(buttonStartGameListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mPrimaryLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            mPrimaryLocale = getResources().getConfiguration().locale;
        }
        mLocale = mPrimaryLocale.getLanguage();

        Categories categories = new Categories();
        if (mLocale.equals(DictionariesActivity.LOCALE_RUS)) {
            mListCategories = categories.getRusCategories();
        } else if (mLocale.equals(DictionariesActivity.LOCALE_UKR)) {
            mListCategories = categories.getUkrCategories();
        } else mListCategories = categories.getEngCategories();
        for (int i = 0; i < mListCategories.size(); i++) {
            Chip chip = new Chip(this, null, R.attr.customChipStyleHangman);
            chip.setId(i);
            chip.setTag(categories.getEngCategories().get(i));
            chip.setText(mListCategories.get(i));
            mChipGroupCategory.addView(chip);
        }

        ((Chip) mChipGroupCategory.getChildAt(0)).setChecked(true);
        mChipGroupCategory.getChildAt(0).setClickable(false);
        mSelectedChip = (Chip) mChipGroupCategory.getChildAt(0);
        mCategory = ((Chip) mChipGroupCategory.getChildAt(0)).getText().toString();
        for (int i = 0; i < mChipGroupCategory.getChildCount(); i++) {
            mChipGroupCategory.setOnCheckedChangeListener(mChipGroupCategoryListener);
        }

        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekBarListener);
        mTextViewSeek = findViewById(R.id.textViewSeek);
        mTextViewSeek.setText(String.valueOf(mSeekBar.getProgress()));

        mAllWords = new ArrayList<>();
        mWords = new ArrayList<>();

        ViewModelProviders.of(this).get(DictionaryViewModel.class).getAllWords().observe(this, observer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        buttonStartGame.setOnClickListener(buttonStartGameListener);
    }
}
