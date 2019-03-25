package com.evgeniy.moiseev.learnwords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evgeniy.moiseev.learnwords.adapters.TestEndsAdapter;
import com.evgeniy.moiseev.learnwords.data.SimpleWord;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentTestEnds extends Fragment {
    private static final String ARG_SIMPLE_WORDS = "simple words";

    private ArrayList<SimpleWord> mSimpleWords;
    private RecyclerView mRecyclerView;
    private TestEndsAdapter mAdapter;
    private Button buttonContinue;
    private Button buttonNextWords;

    private OnFragmentTestEndsInteractionListener mListener;
    private View.OnClickListener buttonContinueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onContinueClicked();
            }
        }
    };
    private View.OnClickListener buttonNewWordsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onNewWordsClicked();
            }
        }
    };

    public FragmentTestEnds() {
    }

    public static FragmentTestEnds newInstance(ArrayList<SimpleWord> simpleWords) {
        FragmentTestEnds fragment = new FragmentTestEnds();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SIMPLE_WORDS, simpleWords);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSimpleWords = getArguments().getParcelableArrayList(ARG_SIMPLE_WORDS);
        }
        mAdapter = new TestEndsAdapter(getActivity(), mSimpleWords);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_ends, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        buttonContinue = rootView.findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(buttonContinueListener);

        buttonNextWords = rootView.findViewById(R.id.buttonNextWords);
        buttonNextWords.setOnClickListener(buttonNewWordsListener);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentTestEndsInteractionListener) {
            mListener = (OnFragmentTestEndsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentTestEndsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentTestEndsInteractionListener {
        void onContinueClicked();

        void onNewWordsClicked();
    }
}
