package com.evgeniy.moiseev.learnwords;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.adapters.TranslationsAdapter;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WordDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public static final String EXTRA_WORD = "word";
    public static final String EXTRA_TRANSCRIPTION = "transcription";
    public static final String EXTRA_TRANSLATION = "translation";
    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_GUESSED = "guessed";
    public static final String EXTRA_NOT_GUESSED = "notGuessed";
    public static final String EXTRA_IS_FAVORITE = "mIsFavorite";
    public static final String EXTRA_FLAG = "flag";

    private Locale mPrimaryLocale;
    private String mLocale;
    private String mPrimeWord;
    private List<String> mListCategories;

    private TextView mTextViewGuessedCount;
    private TextView mTextViewNotGuessedCount;
    private TextInputLayout mWordInputLayout;
    private TextInputEditText mEditTextWord;
    private TextInputEditText mEditTextTranscription;
    private TextInputEditText mEditTextTranslation;
    private ChipGroup mChipGroupDifficulty;
    private HorizontalScrollView mScrollViewChip;
    private ChipGroup mChipGroupCategory;
    private MaterialButton mButtonSpeech;
    private MaterialButton mButtonEditSave;
    private MaterialButton mButtonBack;
    private ImageView mButtonAddTranslation;
    private TextView mTextViewEmptyTranslationList;

    private String mWord;
    private String mTranscription;
    private String mTranslation;
    private int mDifficulty;
    private String mCategory;
    private int mGuessed;
    private int mNotGuessed;
    private int mIsFavorite;
    private Chip mSelectedChip;

    private TextToSpeech mTextToSpeech;
    private DictionaryViewModel mDictionaryViewModel;
    private RecyclerView mRecyclerView;
    private TranslationsAdapter mTranslationAdapter;

    private TranslationsAdapter.OnTranslationClickListener onTranslationClickListener = new TranslationsAdapter.OnTranslationClickListener() {
        @Override
        public void onCardClick(String text) {
            if (!mEditTextTranslation.getText().toString().equals(text)) {
                mEditTextTranslation.setText(text);
            }
        }

        @Override
        public void onClearClick(int position) {
            List<String> translations = mTranslationAdapter.getTranslations();
            if (translations.size() != 1) {
                boolean isNeedToChange = false;
                if (translations.get(position).equals(mEditTextTranslation.getText().toString())) {
                    isNeedToChange = true;
                }
                translations.remove(position);
                mTranslationAdapter.setTranslations(translations);
                if (isNeedToChange) {
                    mEditTextTranslation.setText(translations.get(0));
                }
            }
        }
    };
    private View.OnClickListener buttonSpeechClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTextToSpeech.speak(mEditTextWord.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    };
    private View.OnClickListener buttonEditSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((MaterialButton) v).getText().equals(getResources().getString(R.string.string_edit))) {
                mEditTextWord.setInputType(InputType.TYPE_CLASS_TEXT);
                mEditTextTranscription.setInputType(InputType.TYPE_CLASS_TEXT);

                for (int i = 0; i < mChipGroupDifficulty.getChildCount(); i++) {
                    mChipGroupDifficulty.getChildAt(i).setEnabled(true);
                    mChipGroupDifficulty.setOnCheckedChangeListener(mChipGroupDifficultyListener);
                }

                for (int i = 0; i < mChipGroupCategory.getChildCount(); i++) {
                    mChipGroupCategory.getChildAt(i).setEnabled(true);
                    mChipGroupCategory.setOnCheckedChangeListener(mChipGroupCategoryListener);
                }

                mEditTextTranscription.setOnFocusChangeListener(mEditTextTranscriptionFocusListener);
                mTranslationAdapter.setListener(onTranslationClickListener);

                mButtonEditSave.setText(R.string.string_save);

                mButtonAddTranslation.setOnClickListener(buttonAddTranslationListener);
                mButtonAddTranslation.getDrawable().setAlpha(222);

                mTranslationAdapter.setInteracted();
            } else if (((MaterialButton) v).getText().equals(getResources().getString(R.string.string_save))) {
                if (TextUtils.isEmpty(mEditTextWord.getText().toString().trim())) {
                    mEditTextWord.setText("");
                    mWordInputLayout.setError(getResources().getString(R.string.string_error_empty_field));
                    mWordInputLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                    mWordInputLayout.setBoxStrokeColor(getResources().getColor(R.color.colorError));
                    mWordInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                } else if (mTranslationAdapter.getItemCount() == 0) {
                    mTextViewEmptyTranslationList.setVisibility(View.VISIBLE);
                    AnimationUtils.animateHintAddTranslation(mTextViewEmptyTranslationList, mButtonAddTranslation, v);
                } else {
                    mDictionaryViewModel.delete(mPrimeWord);
                    mDictionaryViewModel.insertList(createListFromFields());
                    finish();
                }
            }
        }
    };
    private View.OnClickListener buttonAddTranslationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(WordDetailActivity.this)
                    .inflate(R.layout.dialog_add_translation_layout, null);
            final TextInputLayout textInputLayout = view.findViewById(R.id.text_input_layout_new_translation);
            final TextInputEditText editText = view.findViewById(R.id.edit_text_new_translation);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            final AlertDialog dialog = new AlertDialog.Builder(WordDetailActivity.this)
                    .setTitle(R.string.string_add_translation)
                    .setCancelable(false)
                    .setView(view)
                    .setPositiveButton(R.string.string_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                        List<String> translations = mTranslationAdapter.getTranslations();
                        if (translations.size() == 0) {
                            mEditTextTranslation.setText(editText.getText().toString().trim().toLowerCase());
                        }
                        translations.add(editText.getText().toString().trim().toLowerCase());
                        mTranslationAdapter.setTranslations(translations);
                        dialog.dismiss();
                    } else {
                        editText.setText("");
                        textInputLayout.setError(getResources().getString(R.string.string_error_empty_field));
                        textInputLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                        editText.getBackground().setColorFilter(getResources().getColor(R.color.colorError), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            });

        }
    };
    private View.OnClickListener buttonBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    private View.OnFocusChangeListener mEditTextTranslationFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                hideKeyboard(v);
            }
        }
    };
    private View.OnFocusChangeListener mEditTextTranscriptionFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                String currentText = mEditTextTranscription.getText().toString().trim();
                if (!TextUtils.isEmpty(currentText) && !currentText.equals("||") && !currentText.equals("|") && !currentText.equals("|")) {
                    if (!mEditTextTranscription.getText().toString().trim().startsWith("|")) {
                        mEditTextTranscription.setText("|" + mEditTextTranscription.getText().toString());
                    }
                    if (!mEditTextTranscription.getText().toString().trim().endsWith("|")) {
                        mEditTextTranscription.setText(mEditTextTranscription.getText().toString() + "|");
                    }
                } else mEditTextTranscription.setText("");
            }
        }
    };
    private ChipGroup.OnCheckedChangeListener mChipGroupDifficultyListener = new ChipGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ChipGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.chip_easy:
                    mDifficulty = 1;
                    break;
                case R.id.chip_medium:
                    mDifficulty = 2;
                    break;
                case R.id.chip_hard:
                    mDifficulty = 3;
                    break;
            }
            for (int i = 0; i < group.getChildCount(); i++) {
                group.getChildAt(i).setClickable(true);
            }
            group.getChildAt(mDifficulty - 1).setClickable(false);
            hideKeyboard(group);
        }
    };
    private ChipGroup.OnCheckedChangeListener mChipGroupCategoryListener = new ChipGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ChipGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                group.getChildAt(i).setClickable(true);
            }
            mSelectedChip = (Chip) group.getChildAt(checkedId);
            mSelectedChip.setClickable(false);
            mCategory = mSelectedChip.getText().toString();
            hideKeyboard(group);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        mDictionaryViewModel = ViewModelProviders.of(this).get(DictionaryViewModel.class);

        mTextViewGuessedCount = findViewById(R.id.textViewGuessedCount);
        mTextViewNotGuessedCount = findViewById(R.id.textViewNotGuessedCount);
        mWordInputLayout = findViewById(R.id.inputWord);
        mEditTextWord = findViewById(R.id.editTextWord);
        mEditTextTranscription = findViewById(R.id.editTextTranscription);
        mEditTextTranslation = findViewById(R.id.editTextTranslation);
        mChipGroupDifficulty = findViewById(R.id.chip_group_difficulty);
        mTextViewEmptyTranslationList = findViewById(R.id.text_view_error);
        mChipGroupCategory = findViewById(R.id.chipGroupCategory);
        mButtonSpeech = findViewById(R.id.button_speech);
        mButtonEditSave = findViewById(R.id.button_edit_save);
        mButtonBack = findViewById(R.id.button_back);
        mButtonAddTranslation = findViewById(R.id.button_add_translation);
        mRecyclerView = findViewById(R.id.recycler_translations);
        mScrollViewChip = findViewById(R.id.scrollViewChips);

        mEditTextTranslation.setInputType(InputType.TYPE_NULL);
        mTranslationAdapter = new TranslationsAdapter(this);
        mRecyclerView.setAdapter(mTranslationAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
            Chip chip = new Chip(this, null, R.attr.customChipStyle);
            chip.setId(i);
            chip.setText(mListCategories.get(i));
            mChipGroupCategory.addView(chip);
        }

        mTextToSpeech = new TextToSpeech(this, this);
        mEditTextTranslation.setOnFocusChangeListener(mEditTextTranslationFocusListener);
        mButtonSpeech.setOnClickListener(buttonSpeechClickListener);
        mButtonEditSave.setOnClickListener(buttonEditSaveClickListener);
        mButtonBack.setOnClickListener(buttonBackListener);

        int flag = getIntent().getIntExtra(EXTRA_FLAG, 0);
        if (flag == DictionariesActivity.FLAG_EDIT) {

            //Init widgets when activity opened as edit activity
            mWord = getIntent().getStringExtra(EXTRA_WORD);
            mPrimeWord = new String(mWord);
            mTranscription = getIntent().getStringExtra(EXTRA_TRANSCRIPTION);
            mTranslation = getIntent().getStringExtra(EXTRA_TRANSLATION);
            mDifficulty = getIntent().getIntExtra(EXTRA_DIFFICULTY, 2);
            mCategory = getIntent().getStringExtra(EXTRA_CATEGORY).toLowerCase();
            mGuessed = getIntent().getIntExtra(EXTRA_GUESSED, 0);
            mNotGuessed = getIntent().getIntExtra(EXTRA_NOT_GUESSED, 0);
            mIsFavorite = getIntent().getIntExtra(EXTRA_IS_FAVORITE, 0);

            mEditTextWord.setText(mWord);
            mEditTextTranscription.setText(mTranscription);
            mEditTextTranslation.setText(mTranslation);
            mEditTextWord.setInputType(InputType.TYPE_NULL);
            mEditTextTranscription.setInputType(InputType.TYPE_NULL);

            mChipGroupDifficulty.check(getChipId(mDifficulty));
            for (int i = 0; i < mChipGroupDifficulty.getChildCount(); i++) {
                mChipGroupDifficulty.getChildAt(i).setEnabled(false);
            }

            for (int i = 0; i < mChipGroupCategory.getChildCount(); i++) {
                Chip c = (Chip) mChipGroupCategory.getChildAt(i);
                if ((c.getText()).equals(mCategory)) {
                    c.setChecked(true);
                    mSelectedChip = c;
                }
                c.setEnabled(false);
            }
            mScrollViewChip.post(new Runnable() {
                @Override
                public void run() {
                    mScrollViewChip.scrollTo(mSelectedChip.getLeft() + mSelectedChip.getWidth() / 2 - mScrollViewChip.getWidth() / 2, 0);
                }
            });

            mButtonAddTranslation.getDrawable().setAlpha(97);
        } else if (flag == DictionariesActivity.FLAG_CREATE) {
            mPrimeWord = "";
            mButtonEditSave.setText(R.string.string_save);
            mDifficulty = 2;

            mButtonSpeech.setEnabled(false);
            mChipGroupDifficulty.check(R.id.chip_medium);
            mChipGroupDifficulty.getChildAt(1).setClickable(false);
            for (int i = 0; i < mChipGroupDifficulty.getChildCount(); i++) {
                mChipGroupDifficulty.setOnCheckedChangeListener(mChipGroupDifficultyListener);
            }

            ((Chip) mChipGroupCategory.getChildAt(0)).setChecked(true);
            mChipGroupCategory.getChildAt(0).setClickable(false);
            mSelectedChip = (Chip) mChipGroupCategory.getChildAt(0);
            mCategory = ((Chip) mChipGroupCategory.getChildAt(0)).getText().toString();
            for (int i = 0; i < mChipGroupCategory.getChildCount(); i++) {
                mChipGroupCategory.setOnCheckedChangeListener(mChipGroupCategoryListener);
            }

            mEditTextTranscription.setOnFocusChangeListener(mEditTextTranscriptionFocusListener);
            mButtonAddTranslation.setOnClickListener(buttonAddTranslationListener);
            mButtonAddTranslation.getDrawable().setAlpha(222);

            mTranslationAdapter.setInteracted();
        }

        mTextViewGuessedCount.setText(String.valueOf(mGuessed));
        mTextViewGuessedCount.setTextColor(mGuessed != 0 ? getResources().getColor(R.color.colorGuessed) : Color.parseColor("#BDBDBD"));
        mTextViewNotGuessedCount.setText(String.valueOf(mNotGuessed));
        mTextViewNotGuessedCount.setTextColor(mNotGuessed != 0 ? getResources().getColor(R.color.colorNotGuessed) : Color.parseColor("#BDBDBD"));

        mEditTextWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mButtonSpeech.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
                mWordInputLayout.setError(null);
                mWordInputLayout.setBoxStrokeColor(getResources().getColor(R.color.colorAccent));
                mWordInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            }
        });

        LiveData<List<Word>> listWordsByWord = mDictionaryViewModel.getListByWord(mWord);
        listWordsByWord.observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                List<String> translations = new ArrayList<>();
                for (Word w : words) {
                    if (mLocale.equals(DictionariesActivity.LOCALE_RUS)) {
                        translations.add(w.getTranslationRus());
                    } else if (mLocale.equals(DictionariesActivity.LOCALE_UKR)) {
                        translations.add(w.getTranslationUkr());
                    }
                }
                mTranslationAdapter.setTranslations(translations);
            }
        });
    }

    private int getChipId(int difficulty) {
        switch (difficulty) {
            case Word.DIFFICULTY_EASY:
                return R.id.chip_easy;
            case Word.DIFFICULTY_MEDIUM:
                return R.id.chip_medium;
            case Word.DIFFICULTY_HARD:
                return R.id.chip_hard;
        }
        return -1;
    }

    private List<Word> createListFromFields() {
        List<Word> wordList = new ArrayList<>();
        Categories categories = new Categories();
        Category cat = null;
        switch (mLocale) {
            case DictionariesActivity.LOCALE_RUS:
                for (Category c : categories.getCategories()) {
                    if (c.getCategoryRus().equals(mCategory)) {
                        cat = c;
                        break;
                    }
                }
                break;
            case DictionariesActivity.LOCALE_UKR:
                for (Category c : categories.getCategories()) {
                    if (c.getCategoryUkr().equals(mCategory)) {
                        cat = c;
                        break;
                    }
                }
                break;
            default:
                for (Category c : categories.getCategories()) {
                    if (c.getCategoryEng().equals(mCategory)) {
                        cat = c;
                        break;
                    }
                }
                break;
        }

        String word = mEditTextWord.getText().toString().toLowerCase().trim();
        String transcription = mEditTextTranscription.getText().toString().toLowerCase().trim();
        boolean isNewWord = !mPrimeWord.equals(word);
        for (int i = 0; i < mTranslationAdapter.getItemCount(); i++) {
            String translationRus;
            String translationUkr;
            int isMain;
            switch (mLocale) {
                case DictionariesActivity.LOCALE_RUS:
                    translationRus = mTranslationAdapter.getTranslations().get(i);
                    translationUkr = "відсутній переклад";
                    if (translationRus.equals(mEditTextTranslation.getText().toString()))
                        isMain = 1;
                    else isMain = 0;
                    break;
                case DictionariesActivity.LOCALE_UKR:
                    translationRus = "отсутствует перевод";
                    translationUkr = mTranslationAdapter.getTranslations().get(i);
                    if (translationUkr.equals(mEditTextTranslation.getText().toString()))
                        isMain = 1;
                    else isMain = 0;
                    break;
                default:
                    translationRus = mTranslationAdapter.getTranslations().get(i);
                    translationUkr = "відсутній переклад";
                    if (translationRus.equals(mEditTextTranslation.getText().toString()))
                        isMain = 1;
                    else isMain = 0;
                    break;
            }
            String category = cat.getCategoryEng();
            String categoryRus = cat.getCategoryRus();
            String categoryUkr = cat.getCategoryUkr();
            int difficulty = mDifficulty;
            int isFavorite = mIsFavorite;
            int guessed = isNewWord ? 0 : mGuessed;
            int notGuessed = isNewWord ? 0 : mNotGuessed;
            wordList.add(new Word(
                    word,
                    transcription,
                    translationRus,
                    translationUkr,
                    category,
                    categoryRus,
                    categoryUkr,
                    difficulty,
                    isMain,
                    guessed,
                    notGuessed,
                    isFavorite));
        }
        return wordList;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(WordDetailActivity.this.findViewById(R.id.main_constraint),
                        getString(R.string.string_not_supported_language),
                        Snackbar.LENGTH_LONG).show();
            }
        } else Log.e("YYY", "Initialization Failed!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
