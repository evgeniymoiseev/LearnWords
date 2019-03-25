package com.evgeniy.moiseev.learnwords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.evgeniy.moiseev.learnwords.data.Word;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WordCardsFragment extends Fragment {
    private static final String ARG_WORDS = "words";
    private static final String ARG_LOCALE = "locale";

    private ImageView imageViewSpeech;
    private ImageView imageViewSpeechSlow;
    private Button buttonStartTraining;
    private Button buttonNextWords;

    private ArrayList<Word> mWords;
    private String mLocale;
    private ViewPager mViewPager;

    private int mCurrentPosition;

    private OnFragmentCardsInteractionListener mListener;
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mListener != null) {
                mCurrentPosition = position;
                mListener.onPositionChanged(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private View.OnClickListener onImageSpeechListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onImageSpeechClicked(mWords.get(mCurrentPosition).getWord(), false);
            }
        }
    };
    private View.OnClickListener onImageSpeechSlowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onImageSpeechClicked(mWords.get(mCurrentPosition).getWord(), true);
            }
        }
    };
    private View.OnClickListener onStartTrainingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonStartTraining.setOnClickListener(null);
            if (mListener != null) {
                mListener.onStartTrainingClicked();
            }
        }
    };
    private View.OnClickListener onNextWordsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonNextWords.setOnClickListener(null);
            if (mListener != null) {
                mListener.onNextWordsClicked();
            }
        }
    };

    public WordCardsFragment() {
    }

    public static WordCardsFragment newInstance(ArrayList<Word> trainingWords, String locale) {
        WordCardsFragment fragment = new WordCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_WORDS, trainingWords);
        args.putString(ARG_LOCALE, locale);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWords = getArguments().getParcelableArrayList(ARG_WORDS);
            mLocale = getArguments().getString(ARG_LOCALE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_cards, container, false);
        mViewPager = rootView.findViewById(R.id.viewPagerCards);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0)
                    buttonStartTraining.setOnClickListener(onStartTrainingListener);
                else
                    buttonStartTraining.setOnClickListener(null);
            }
        });
        imageViewSpeech = rootView.findViewById(R.id.imageSpeech);
        imageViewSpeechSlow = rootView.findViewById(R.id.imageSpeechSlow);
        buttonStartTraining = rootView.findViewById(R.id.buttonStartTraining);
        buttonNextWords = rootView.findViewById(R.id.buttonNextWords);

        mViewPager.setAdapter(new ViewPagerCardsAdapter(getActivity().getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        imageViewSpeech.setOnClickListener(onImageSpeechListener);
        imageViewSpeechSlow.setOnClickListener(onImageSpeechSlowListener);
        buttonNextWords.setOnClickListener(onNextWordsListener);

        mListener.onPositionChanged(0);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCardsInteractionListener) {
            mListener = (OnFragmentCardsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAnswerFromFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentCardsInteractionListener {
        void onPositionChanged(int newPosition);

        void onImageSpeechClicked(String word, boolean slow);

        void onNextWordsClicked();

        void onStartTrainingClicked();
    }

    class ViewPagerCardsAdapter extends FragmentStatePagerAdapter {

        public ViewPagerCardsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CardFragment.newInstance(mWords.get(position).getWord(),
                    mWords.get(position).getTranscription(),
                    mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                            mWords.get(position).getTranslationUkr() : mWords.get(position).getTranslationRus());
        }

        @Override
        public int getCount() {
            return mWords.size() < TrainingWordsActivity.DEFAULT_TRAIN_CARDS_COUNT ?
                    mWords.size() : TrainingWordsActivity.DEFAULT_TRAIN_CARDS_COUNT;
        }
    }
}
