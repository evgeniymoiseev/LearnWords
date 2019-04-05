package com.evgeniy.moiseev.learnwords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evgeniy.moiseev.learnwords.data.IrregularVerb;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IrregularCardsFragment extends Fragment {
    private static final String ARG_VERBS = "verbs";

    private ArrayList<IrregularVerb> mVerbs;
    private Button buttonStartTraining;
    private Button buttonNextVerbs;
    private int mCurrentPosition;
    private ViewPager mViewPager;

    private OnIrregularCardsFragmentListener mListener;
    private View.OnClickListener onStartTrainingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonStartTraining.setOnClickListener(null);
            if (mListener != null) {
                mListener.onStartTrainingClicked();
            }
        }
    };
    private View.OnClickListener onNextVerbsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonNextVerbs.setOnClickListener(null);
            if (mListener != null) {
                mListener.onNextVerbsClicked();
            }
        }
    };
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffset == 0)
                buttonStartTraining.setOnClickListener(onStartTrainingListener);
            else
                buttonStartTraining.setOnClickListener(null);
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

    public IrregularCardsFragment() {
    }

    public static IrregularCardsFragment newInstance(ArrayList<IrregularVerb> trainingVerbs) {
        IrregularCardsFragment fragment = new IrregularCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_VERBS, trainingVerbs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVerbs = getArguments().getParcelableArrayList(ARG_VERBS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_irregular_cards, container, false);
        mViewPager = root.findViewById(R.id.viewPagerCards);
        mViewPager.setAdapter(new ViewPagerCardsAdapter(getActivity().getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        buttonStartTraining = root.findViewById(R.id.buttonStartTraining);
        buttonStartTraining.setOnClickListener(onStartTrainingListener);
        buttonNextVerbs = root.findViewById(R.id.buttonNextVerbs);
        buttonNextVerbs.setOnClickListener(onNextVerbsListener);
        mListener.onPositionChanged(0);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnIrregularCardsFragmentListener) {
            mListener = (OnIrregularCardsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnIrregularCardsFragmentListener {
        void onPositionChanged(int newPosition);

        void onStartTrainingClicked();

        void onNextVerbsClicked();
    }

    class ViewPagerCardsAdapter extends FragmentStatePagerAdapter {

        public ViewPagerCardsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return IrregularCardFragment.newInstance(mVerbs.get(position));
        }

        @Override
        public int getCount() {
            return mVerbs.size();
        }
    }
}
