package com.evgeniy.moiseev.learnwords;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CardFragment extends Fragment {
    private static final String ARG_WORD = "word";
    private static final String ARG_TRANSCRIPTION = "transcription";
    private static final String ARG_TRANSLATION = "translation";

    private String word;
    private String transcription;
    private String translation;

    public static CardFragment newInstance(String word, String transcription, String translation) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        args.putString(ARG_TRANSCRIPTION, transcription);
        args.putString(ARG_TRANSLATION, translation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getString(ARG_WORD);
            transcription = getArguments().getString(ARG_TRANSCRIPTION);
            translation = getArguments().getString(ARG_TRANSLATION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training_card, container, false);
        ((TextView) root.findViewById(R.id.textViewWord)).setText(word);
        ((TextView) root.findViewById(R.id.textViewTranscription)).setText(transcription);
        ((TextView) root.findViewById(R.id.textViewTranslation)).setText(translation);
        return root;
    }
}
