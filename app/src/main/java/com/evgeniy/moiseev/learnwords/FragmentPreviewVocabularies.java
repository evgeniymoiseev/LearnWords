package com.evgeniy.moiseev.learnwords;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class FragmentPreviewVocabularies extends Fragment {
    private static final String ARG_POSITION = "position";
    private int mPosition;
    private String mLocale;

    public FragmentPreviewVocabularies() {
    }

    public static FragmentPreviewVocabularies newInstance(int position) {
        FragmentPreviewVocabularies fragment = new FragmentPreviewVocabularies();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLocale = getResources().getConfiguration().getLocales().get(0).getLanguage();
        } else
            mLocale = getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preview_vocabularies, container, false);
        ImageView imageView = rootView.findViewById(R.id.imagePreviewDictionaries);
        switch (mPosition) {
            case 1:
                imageView.setImageResource(mLocale.equals(DictionariesActivity.LOCALE_UKR) ? R.drawable.screen_1_ukr : R.drawable.screen_1_rus);
                break;
            case 2:
                imageView.setImageResource(mLocale.equals(DictionariesActivity.LOCALE_UKR) ? R.drawable.screen_2_ukr : R.drawable.screen_2_rus);
                break;
            case 3:
                imageView.setImageResource(mLocale.equals(DictionariesActivity.LOCALE_UKR) ? R.drawable.screen_3_ukr : R.drawable.screen_3_rus);
                break;
        }
        return rootView;
    }

}
