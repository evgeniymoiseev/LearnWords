package com.evgeniy.moiseev.learnwords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.evgeniy.moiseev.learnwords.adapters.SimpleIrregularColoredAdapter;
import com.evgeniy.moiseev.learnwords.data.SimpleIrregular;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentIrregularTestEnds extends androidx.fragment.app.Fragment {
    private static final String ARG_LIST = "list";

    private ArrayList<SimpleIrregular> mResult;
    private RecyclerView mRecycler;
    private SimpleIrregularColoredAdapter mAdapter;
    private Button mButtonContinue;
    private Button mButtonNextVerbs;

    private OnFragmentResultListener mListener;

    private View.OnClickListener buttonContinueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onContinueClicked();
            }
        }
    };
    private View.OnClickListener buttonNewVerbsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onNewVerbsClicked();
            }
        }
    };

    public FragmentIrregularTestEnds() {
    }

    public static FragmentIrregularTestEnds newInstance(ArrayList<SimpleIrregular> result) {
        FragmentIrregularTestEnds fragment = new FragmentIrregularTestEnds();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIST, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResult = getArguments().getParcelableArrayList(ARG_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_irregular_test_ends, container, false);
        mRecycler = root.findViewById(R.id.recyclerView);
        mAdapter = new SimpleIrregularColoredAdapter(mResult, getActivity());
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);
        mButtonContinue = root.findViewById(R.id.buttonContinue);
        mButtonContinue.setOnClickListener(buttonContinueListener);
        mButtonNextVerbs = root.findViewById(R.id.buttonNextWords);
        mButtonNextVerbs.setOnClickListener(buttonNewVerbsListener);
        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentResultListener) {
            mListener = (OnFragmentResultListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentResultListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentResultListener {
        void onContinueClicked();

        void onNewVerbsClicked();
    }
}
