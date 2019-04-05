package com.evgeniy.moiseev.learnwords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.IrregularVerb;

import androidx.fragment.app.Fragment;

public class IrregularCardFragment extends Fragment {
    private static final String ARG_IRREGULAR_VERB = "irregular verb";

    private IrregularVerb mIrregularVerb;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textViewTranscription1;
    private TextView textViewTranscription2;
    private TextView textViewTranscription3;


    public IrregularCardFragment() {
    }

    public static IrregularCardFragment newInstance(IrregularVerb verb) {
        IrregularCardFragment fragment = new IrregularCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IRREGULAR_VERB, verb);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIrregularVerb = getArguments().getParcelable(ARG_IRREGULAR_VERB);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_irregular_card, container, false);
        textView1 = root.findViewById(R.id.textViewWord1);
        textView2 = root.findViewById(R.id.textViewWord2);
        textView3 = root.findViewById(R.id.textViewWord3);
        textView1.setText(mIrregularVerb.getWord1());
        textView2.setText(mIrregularVerb.getWord2());
        textView3.setText(mIrregularVerb.getWord3());
        textViewTranscription1 = root.findViewById(R.id.textViewTranscription1);
        textViewTranscription2 = root.findViewById(R.id.textViewTranscription2);
        textViewTranscription3 = root.findViewById(R.id.textViewTranscription3);
        textViewTranscription1.setText(mIrregularVerb.getTranscription1());
        textViewTranscription2.setText(mIrregularVerb.getTranscription2());
        textViewTranscription3.setText(mIrregularVerb.getTranscription3());
        return root;
    }
}
