package com.evgeniy.moiseev.learnwords;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.evgeniy.moiseev.learnwords.adapters.ChooseCategoryAdapter;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrainingModeActivity extends AppCompatActivity implements ChooseCategoryAdapter.ChooseCategoryAdapterListener {
    private Locale mPrimaryLocale;
    private String mLocale;
    private String mCategory;
    private String mOriginCategory;
    private String mDifficulty;
    private String mOriginDifficulty;
    private List<Category> mCategories;
    private Map<Category, Integer> mCategoriesMap;

    private ChipGroup mChipGroup;
    private Chip mSelectedChip;
    private RecyclerView mRecyclerView;
    private ChooseCategoryAdapter mAdapter;
    private Button mButtonOk;

    private Observer<List<Word>> observer = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            mCategories = new ArrayList<>();
            mCategories.add(new Category("favorites", "избранное", "вибране", R.drawable.ic_favorites));
            mCategories.addAll(new Categories().getCategories());
            mCategoriesMap = new HashMap<>();
            for (Category c : mCategories) {
                mCategoriesMap.put(c, getCount(words, c.getCategoryEng()));
            }
            mAdapter = new ChooseCategoryAdapter(TrainingModeActivity.this, mLocale, mCategories, mCategoriesMap, TrainingModeActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(TrainingModeActivity.this, R.anim.layout_animation_fall_down));
        }
    };
    private ChipGroup.OnCheckedChangeListener chipListener = new ChipGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ChipGroup chipGroup, int i) {
            mSelectedChip = findViewById(i);
            mDifficulty = (String) mSelectedChip.getTag();
            mOriginDifficulty = mSelectedChip.getText().toString();
        }
    };
    private View.OnClickListener buttonOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(TrainingModeActivity.this, TrainingWordsActivity.class);
            intent.putExtra(TrainingWordsActivity.EXTRA_CATEGORY, mCategory);
            intent.putExtra(TrainingWordsActivity.EXTRA_ORIGIN_CATEGORY, mOriginCategory);
            intent.putExtra(TrainingWordsActivity.EXTRA_DIFFICULTY, mDifficulty);
            intent.putExtra(TrainingWordsActivity.EXTRA_ORIGIN_DIFFICULTY, mOriginDifficulty);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_mode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mPrimaryLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            mPrimaryLocale = getResources().getConfiguration().locale;
        }
        mLocale = mPrimaryLocale.getLanguage();

        mChipGroup = findViewById(R.id.chipGroup);
        mChipGroup.check(R.id.chipAny);
        mChipGroup.setOnCheckedChangeListener(chipListener);

        mSelectedChip = findViewById(R.id.chipAny);
        mDifficulty = (String) mSelectedChip.getTag();
        mOriginDifficulty = mSelectedChip.getText().toString();

        mButtonOk = findViewById(R.id.buttonOk);
        mButtonOk.setOnClickListener(buttonOkListener);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewModelProviders.of(this).get(DictionaryViewModel.class).getAllWords().observe(this, observer);
    }

    private int getCount(List<Word> words, String categoryEng) {
        int count = 0;
        if (categoryEng.equals("all")) {
            for (Word w : words) {
                if (w.getIsMain() == 1)
                    count++;
            }
        } else if (categoryEng.equals("favorites")) {
            for (Word w : words) {
                if (w.getIsMain() == 1 && w.getIsFavorite() == 1)
                    count++;
            }
        } else {
            for (Word w : words) {
                if (w.getIsMain() == 1 && w.getCategory().equals(categoryEng))
                    count++;
            }
        }
        return count;
    }

    @Override
    public void categoryChanged(String category, String originCategory) {
        mCategory = category;
        mOriginCategory = originCategory;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TrainingModeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
