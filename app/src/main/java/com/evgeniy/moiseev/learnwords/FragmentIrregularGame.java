package com.evgeniy.moiseev.learnwords;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.data.SimpleIrregular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import androidx.fragment.app.Fragment;

public class FragmentIrregularGame extends Fragment {
    private static final int DEFAULT_DURATION_BUTTON_VIBRATOR = 50;
    private static final String ARG_WORD1 = "word1";
    private static final String ARG_WORD2 = "word2";
    private static final String ARG_WORD3 = "word3";

    private Button mV1Label;
    private Button mV2Label;
    private Button mV3Label;
    private ImageView mArrow;

    private TextView mTextViewWord1;
    private TextView mTextViewWord2;
    private TextView mTextViewWord3;
    private TextView mTextViewRightWord1;
    private TextView mTextViewRightWord2;
    private TextView mTextViewRightWord3;
    private StringBuilder mStringBuilder;


    private ArrayList<TextView> mTextViews;
    private ArrayList<TextView> mRightTextViews;
    private ArrayList<TextView> vLabels;
    private TextView mTypedTextView;
    private int[] mInvisible;

    private Vibrator mVibrator;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private DisplayMetrics mDisplayMetrics;
    private int mButtonSize;
    private int mButtonSizeEng;
    private List<View> mButtons;
    private Button mButtonTest;

    private String mWord1;
    private String mWord2;
    private String mWord3;

    private ObjectAnimator arrowMovement;

    private OnFragmentIrregularGameListener mListener;
    private View.OnClickListener vListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (arrayContains(mInvisible, Integer.parseInt((String) v.getTag()))) {
                movePointer(v);
                mTypedTextView = mTextViews.get(Integer.parseInt((String) v.getTag()));
                mStringBuilder.delete(0, mStringBuilder.length());
                if (!mTypedTextView.getText().toString().trim().equals("?") && mTypedTextView.getText().toString().trim().length() != 0)
                    mStringBuilder.append(mTypedTextView.getText().toString().trim());
            }
        }
    };
    private View.OnClickListener buttonTestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mButtonTest.setOnClickListener(null);
            mButtonTest.setClickable(false);

            SimpleIrregular simpleIrregular = new SimpleIrregular();
            for (int i = 0; i < mTextViews.size(); i++) {
                if (!Arrays.asList(mInvisible).contains(i)) {
                    simpleIrregular.setWord(i, mTextViews.get(i).getText().toString());
                    simpleIrregular.setB(i, true);
                }
            }

            for (View view : mButtons) {
                if (view instanceof ImageButton) {
                    view.setEnabled(false);
                    view.setAlpha(0.8f);
                    ((ImageButton) view).setImageAlpha(77);
                } else
                    view.setEnabled(false);
            }

            List<String> answers;
            //Check answers
            for (int i = 0; i < mInvisible.length; i++) {
                TextView tw = getTextViewWordByIndex(mInvisible[i]);
                TextView rtw = getTextViewRightWordByIndex(mInvisible[i]);
                answers = Arrays.asList(rtw.getText().toString().split(" / "));

                if (answers.contains(tw.getText().toString().trim())) {
                    tw.setTextColor(getResources().getColor(R.color.colorGuessed));
                    simpleIrregular.setWord(mInvisible[i], tw.getText().toString().trim());
                    simpleIrregular.setB(mInvisible[i], true);
                } else {
                    tw.setTextColor(getResources().getColor(R.color.colorError));
                    simpleIrregular.setWord(mInvisible[i], rtw.getText().toString().trim());
                    simpleIrregular.setB(mInvisible[i], false);
                    tw.setPaintFlags(tw.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);

                    float density = getResources().getDisplayMetrics().density;

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(
                            ObjectAnimator.ofFloat(tw, "translationY", tw.getTranslationY(), 30.0f * density),
                            ObjectAnimator.ofFloat(rtw, "alpha", rtw.getAlpha(), 1.0f),
                            ObjectAnimator.ofFloat(rtw, "translationY", tw.getTranslationY(), 0.0f)
                    );
                    set.start();
                }
            }

            if (mListener != null) {
                mListener.onButtonTestClicked(simpleIrregular);
            }
        }
    };
    private View.OnClickListener buttonLetterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            mStringBuilder.append(((Button) v).getText());
            mTypedTextView.setText(mStringBuilder.toString());
            mTypedTextView.setTextColor(getResources().getColor(R.color.colorIcons));
            enableTestButton();
        }
    };
    private View.OnClickListener buttonBackSpaceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            if (mStringBuilder.length() > 1) {
                mStringBuilder.deleteCharAt(mStringBuilder.length() - 1);
                mTypedTextView.setText(mStringBuilder);
            } else if (mStringBuilder.length() == 1) {
                mStringBuilder.delete(0, mStringBuilder.length());
                mTypedTextView.setText("?");
                mTypedTextView.setTextColor(getResources().getColor(R.color.colorError));
                enableTestButton();
            }
        }
    };
    private View.OnLongClickListener buttonBackspaceLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mTypedTextView.setTextColor(getResources().getColor(R.color.colorError));
            mTypedTextView.setText("?");
            mStringBuilder = new StringBuilder();
            mStringBuilder.delete(0, mStringBuilder.length());
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            enableTestButton();
            return true;
        }
    };

    public FragmentIrregularGame() {
    }

    public static FragmentIrregularGame newInstance(String word1, String word2, String word3) {
        FragmentIrregularGame fragment = new FragmentIrregularGame();
        Bundle args = new Bundle();
        args.putString(ARG_WORD1, word1);
        args.putString(ARG_WORD2, word2);
        args.putString(ARG_WORD3, word3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWord1 = getArguments().getString(ARG_WORD1);
            mWord2 = getArguments().getString(ARG_WORD2);
            mWord3 = getArguments().getString(ARG_WORD3);
        }

        mButtons = new ArrayList<>();
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        mButtonSize = (int) (mDisplayMetrics.widthPixels / 11.5);
        mButtonSizeEng = mDisplayMetrics.widthPixels / 10;
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        mStringBuilder = new StringBuilder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_irregular_game, container, false);
        mArrow = root.findViewById(R.id.arrow);
        arrowMovement = ObjectAnimator.ofFloat(mArrow, "translationX", mArrow.getTranslationX(), -20.0f * mDisplayMetrics.density);
        arrowMovement.setRepeatMode(ValueAnimator.REVERSE);
        arrowMovement.setRepeatCount(5);

        mV1Label = root.findViewById(R.id.v1Label);
        mV2Label = root.findViewById(R.id.v2Label);
        mV3Label = root.findViewById(R.id.v3Label);

        mV1Label.setOnClickListener(vListener);
        mV2Label.setOnClickListener(vListener);
        mV3Label.setOnClickListener(vListener);

        mTextViewWord1 = root.findViewById(R.id.textView1);
        mTextViewWord2 = root.findViewById(R.id.textView2);
        mTextViewWord3 = root.findViewById(R.id.textView3);

        mTextViewWord1.setText(mWord1);
        mTextViewWord2.setText(mWord2);
        mTextViewWord3.setText(mWord3);

        mTextViewRightWord1 = root.findViewById(R.id.textViewRightAnswer1);
        mTextViewRightWord2 = root.findViewById(R.id.textViewRightAnswer2);
        mTextViewRightWord3 = root.findViewById(R.id.textViewRightAnswer3);

        mTextViewRightWord1.setText(mWord1);
        mTextViewRightWord2.setText(mWord2);
        mTextViewRightWord3.setText(mWord3);

        linearLayout1 = root.findViewById(R.id.linear1);
        linearLayout2 = root.findViewById(R.id.linear2);
        linearLayout3 = root.findViewById(R.id.linear3);

        setupKeyboard();

        mButtonTest = root.findViewById(R.id.buttonTest);
        mButtonTest.setOnClickListener(buttonTestListener);
        mButtonTest.setEnabled(false);

        mTextViews = new ArrayList<>();
        mTextViews.add(mTextViewWord1);
        mTextViews.add(mTextViewWord2);
        mTextViews.add(mTextViewWord3);
        mRightTextViews = new ArrayList<>();
        mRightTextViews.add(mTextViewRightWord1);
        mRightTextViews.add(mTextViewRightWord2);
        mRightTextViews.add(mTextViewRightWord3);
        vLabels = new ArrayList<>();
        vLabels.add(mV1Label);
        vLabels.add(mV2Label);
        vLabels.add(mV3Label);

        //Disappear words
        mInvisible = getRandomPositionsOfUnknown(getRandomNumberOfUnknown());
        for (int i = 0; i < mInvisible.length; i++) {
            for (TextView tw : mTextViews) {
                if (Integer.parseInt((String) tw.getTag()) == mInvisible[i]) {
                    tw.setText("?");
                    tw.setTextColor(getResources().getColor(R.color.colorError));
                }
            }
        }

        //Set current TextView & StringBuilder
        mTypedTextView = mTextViews.get(mInvisible[0]);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Move pointer to first word
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                movePointer(vLabels.get(mInvisible[0]));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentIrregularGameListener) {
            mListener = (OnFragmentIrregularGameListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentIrregularGameListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentIrregularGameListener {
        void onButtonTestClicked(SimpleIrregular simpleIrregular);
    }

    private int getRandomNumberOfUnknown() {
        return new Random(System.currentTimeMillis()).nextInt(2) + 1;
    }

    private int[] getRandomPositionsOfUnknown(int count) {
        int[] arr = null;
        switch (count) {
            case 1:
                arr = new int[1];
                arr[0] = new Random(System.currentTimeMillis()).nextInt(3);
                break;
            case 2:
                arr = new int[2];
                int except = new Random(System.currentTimeMillis()).nextInt(3);
                int position = 0;
                for (int i = 0; i < 3; i++) {
                    if (i != except) {
                        arr[position] = i;
                        position++;
                    }
                }
                break;
        }
        Arrays.sort(arr);
        return arr;
    }

    private void setupKeyboard() {
        ImageButton buttonBackSpace = new ImageButton(getActivity());
        buttonBackSpace.setImageResource(R.drawable.ic_keyboard_backspace);
        ViewGroup.LayoutParams buttonBackSpaceParams = new ViewGroup.LayoutParams((int) (mButtonSize * 1.5), (int) (mButtonSize * 1.5));
        buttonBackSpace.setOnClickListener(buttonBackSpaceListener);
        buttonBackSpace.setOnLongClickListener(buttonBackspaceLongClickListener);

        //First row
        for (int i = 0; i < Alphabet.getEng1().length(); i++) {
            Button button = new Button(getActivity());
            button.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
            button.setAllCaps(false);
            button.setOnClickListener(buttonLetterListener);
            button.setText(String.valueOf(Alphabet.getEng1().charAt(i)));
            linearLayout1.addView(button, buttonParams);
            mButtons.add(button);
        }

        //Second row
        for (int i = 0; i < Alphabet.getEng2().length(); i++) {
            Button button = new Button(getActivity());
            button.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
            button.setAllCaps(false);
            button.setOnClickListener(buttonLetterListener);
            button.setText(String.valueOf(Alphabet.getEng2().charAt(i)));
            linearLayout2.addView(button, buttonParams);
            mButtons.add(button);
        }

        //Third row
        for (int i = 0; i < Alphabet.getEng3().length(); i++) {
            Button button = new Button(getActivity());
            button.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
            button.setAllCaps(false);
            button.setOnClickListener(buttonLetterListener);
            button.setText(String.valueOf(Alphabet.getEng3().charAt(i)));
            linearLayout3.addView(button, buttonParams);
            mButtons.add(button);
        }

        linearLayout3.addView(buttonBackSpace, buttonBackSpaceParams);
        mButtons.add(buttonBackSpace);
    }

    private void vibrate(Vibrator vibrator, int milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else vibrator.vibrate(milliseconds);
    }

    private void movePointer(View viewToMove) {
        arrowMovement.end();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mArrow.getY(), viewToMove.getY());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mArrow.setY((Float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                arrowMovement.start();
            }
        });
        valueAnimator.start();
    }

    private boolean arrayContains(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return true;
        }
        return false;
    }

    private void enableTestButton() {
        if (!mTextViewWord1.getText().toString().trim().equals("?") &&
                !mTextViewWord2.getText().toString().trim().equals("?") &&
                !mTextViewWord3.getText().toString().trim().equals("?"))
            mButtonTest.setEnabled(true);
        else mButtonTest.setEnabled(false);
    }

    private TextView getTextViewWordByIndex(int index) {
        switch (index) {
            case 0:
                return mTextViewWord1;
            case 1:
                return mTextViewWord2;
            case 2:
                return mTextViewWord3;
        }
        return null;
    }

    private TextView getTextViewRightWordByIndex(int index) {
        switch (index) {
            case 0:
                return mTextViewRightWord1;
            case 1:
                return mTextViewRightWord2;
            case 2:
                return mTextViewRightWord3;
        }
        return null;
    }
}
