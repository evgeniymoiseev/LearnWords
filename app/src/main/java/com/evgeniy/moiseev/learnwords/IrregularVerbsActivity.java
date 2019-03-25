package com.evgeniy.moiseev.learnwords;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

public class IrregularVerbsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irregular_verbs);
    }

//    private void setupKeyboard() {
//        Button buttonDash = new Button(this);
//        buttonDash.setText("-");
//        buttonDash.setOnClickListener(buttonLetterListener);
//
//        Button buttonSpace = new Button(getActivity());
//        buttonSpace.setText(" ");
//        LinearLayout.LayoutParams buttonSpaceParams = new LinearLayout.LayoutParams(mButtonSpaceSize, (int) (mButtonSize * 1.5));
//        buttonSpace.setOnClickListener(buttonLetterListener);
//
//        ImageButton buttonBackSpace = new ImageButton(getActivity());
//        buttonBackSpace.setImageResource(R.drawable.ic_keyboard_backspace);
//        ViewGroup.LayoutParams buttonBackSpaceParams = new ViewGroup.LayoutParams((int) (mButtonSize * 1.5), (int) (mButtonSize * 1.5));
//        buttonBackSpace.setOnClickListener(buttonBackSpaceListener);
//        buttonBackSpace.setOnLongClickListener(buttonBackspaceLongClickListener);
//
//        switch (mType) {
//            case TrainingWordsActivity.TYPE_FROM_ENG:
//                String locale;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    locale = getActivity().getResources().getConfiguration().getLocales().get(0).getLanguage();
//                } else
//                    locale = getActivity().getResources().getConfiguration().locale.getLanguage();
//
//                //First row
//                int count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr1().length() : Alphabet.getRus1().length();
//                for (int i = 0; i < count; i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
//                            Alphabet.getUkr1().charAt(i) : Alphabet.getRus1().charAt(i)));
//                    linearLayout1.addView(button, buttonParams);
//                }
//
//                //Second row
//                count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr2().length() : Alphabet.getRus2().length();
//                for (int i = 0; i < count; i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
//                            Alphabet.getUkr2().charAt(i) : Alphabet.getRus2().charAt(i)));
//                    linearLayout2.addView(button, buttonParams);
//                }
//
//                //Third row
//                count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr3().length() : Alphabet.getRus3().length();
//                for (int i = 0; i < count; i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
//                            Alphabet.getUkr3().charAt(i) : Alphabet.getRus3().charAt(i)));
//                    linearLayout3.addView(button, buttonParams);
//                }
//
//                //Forth row
//                LinearLayout.LayoutParams buttonDashParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
//                linearLayout4.addView(buttonDash, buttonDashParams);
//                linearLayout4.addView(buttonSpace, buttonSpaceParams);
//                linearLayout4.addView(buttonBackSpace, buttonBackSpaceParams);
//                break;
//            case TrainingWordsActivity.TYPE_TO_ENG:
//
//                //First row
//                for (int i = 0; i < Alphabet.getEng1().length(); i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(Alphabet.getEng1().charAt(i)));
//                    linearLayout1.addView(button, buttonParams);
//                }
//
//                //Second row
//                for (int i = 0; i < Alphabet.getEng2().length(); i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(Alphabet.getEng2().charAt(i)));
//                    linearLayout2.addView(button, buttonParams);
//                }
//
//                //Third row
//                LinearLayout.LayoutParams buttonDashParamsEng = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
//                linearLayout3.addView(buttonDash, buttonDashParamsEng);
//
//                for (int i = 0; i < Alphabet.getEng3().length(); i++) {
//                    Button button = new Button(getActivity());
//                    button.setPadding(0, 0, 0, 0);
//                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
//                    button.setAllCaps(false);
//                    button.setOnClickListener(buttonLetterListener);
//                    button.setText(String.valueOf(Alphabet.getEng3().charAt(i)));
//                    linearLayout3.addView(button, buttonParams);
//                }
//                linearLayout3.addView(buttonBackSpace, buttonBackSpaceParams);
//
//                //Forth row
//                linearLayout4.addView(buttonSpace, buttonSpaceParams);
//                break;
//        }
//
//    }
}
