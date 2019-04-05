package com.evgeniy.moiseev.learnwords;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.adapters.SimpleIrregularAdapter;
import com.evgeniy.moiseev.learnwords.data.IrregularVerb;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IrregularVerbsModeActivity extends AppCompatActivity {
    public static final String EXTRA_MARKED = "marked";
    public static final String EXTRA_NUMBER = "number";

    private ChipGroup mChipGroup;
    private Chip mSelectedChip;
    private SeekBar mSeekBar;
    private TextView mTextViewRepeat;
    private SimpleIrregularAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button mButtonOk;

    private ChipGroup.OnCheckedChangeListener chipListener = new ChipGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ChipGroup chipGroup, int id) {
            mSelectedChip = findViewById(id);
            switch (id) {
                case R.id.chipAll:
                    mAdapter.setFiltered(false);
                    chipGroup.getChildAt(0).setClickable(false);
                    chipGroup.getChildAt(1).setClickable(true);
                    break;
                case R.id.chipMarked:
                    mAdapter.setFiltered(true);
                    chipGroup.getChildAt(1).setClickable(false);
                    chipGroup.getChildAt(0).setClickable(true);
                    break;
            }

        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                int currProgress = progress;
                int min = 1;
                if (progress < min) {
                    currProgress = min;
                }
                mTextViewRepeat.setText(String.valueOf(currProgress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            ObjectAnimator.ofFloat(mTextViewRepeat, "textSize", 12f, 15f).start();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ObjectAnimator.ofFloat(mTextViewRepeat, "textSize", 15f, 12f).start();
        }
    };
    private Observer<List<IrregularVerb>> observer = new Observer<List<IrregularVerb>>() {
        @Override
        public void onChanged(List<IrregularVerb> irregularVerbs) {
            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(IrregularVerbsModeActivity.this));
            mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(IrregularVerbsModeActivity.this, R.anim.layout_animation_fall_down));
            mAdapter = new SimpleIrregularAdapter(irregularVerbs, IrregularVerbsModeActivity.this);
            mRecyclerView.setAdapter(mAdapter);
        }
    };
    private View.OnClickListener buttonOkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(IrregularVerbsModeActivity.this, TrainingIrregularsActivity.class);
            intent.putExtra(EXTRA_MARKED, (String) mSelectedChip.getTag());
            intent.putExtra(EXTRA_NUMBER, Integer.parseInt(mTextViewRepeat.getText().toString()));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irregular_verbs_mode);

        mChipGroup = findViewById(R.id.chipGroup);
        mChipGroup.check(R.id.chipAll);
        mChipGroup.getChildAt(0).setClickable(false);
        mChipGroup.setOnCheckedChangeListener(chipListener);
        mSelectedChip = findViewById(R.id.chipAll);

        mSeekBar = findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(seekBarListener);
        mTextViewRepeat = findViewById(R.id.textViewSeek);
        mTextViewRepeat.setText(String.valueOf(mSeekBar.getProgress()));
        mButtonOk = findViewById(R.id.buttonOk);
        mButtonOk.setOnClickListener(buttonOkListener);

        ViewModelProviders.of(this).get(DictionaryViewModel.class).getAllIrregularVerbs().observe(this, observer);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(IrregularVerbsModeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
