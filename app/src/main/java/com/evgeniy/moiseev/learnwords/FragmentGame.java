package com.evgeniy.moiseev.learnwords;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class FragmentGame extends Fragment {
    private static final String ARG_WORD = "word";
    private static final String ARG_TYPE = "type";
    private static final String ARG_TRANSLATIONS = "translations";
    private static final String ARG_CHOICE = "choice";

    private static final int DEFAULT_DURATION_BUTTON_VIBRATOR = 50;
    private static final int DEFAULT_DURATION_ANIMATION = 50;

    private String mWord;
    private ArrayList<String> mTranslations;
    private ArrayList<String> mChoices;
    private StringBuilder mAnswer;
    private int mType;
    private Vibrator mVibrator;

    //View for types TYPE_FROM_ENG and TYPE_TO_ENG
    private TextView mTextViewWord;
    private TextView textViewAnswer;
    private TextView textViewRightAnswer;
    private Button buttonTest;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private DisplayMetrics mDisplayMetrics;
    private int mButtonSize;
    private int mButtonSizeEng;
    private int mButtonSpaceSize;
    private List<View> mButtons;

    //Views for types TYPE_CHOICE_FROM_ENG and TYPE_CHOICE_TO_ENG
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;


    private OnAnswerFromFragmentListener mListener;
    private View.OnClickListener buttonLetterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            mAnswer.append(((TextView) v).getText());
            textViewAnswer.setText(mAnswer);
        }
    };
    private View.OnClickListener buttonBackSpaceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            if (mAnswer.length() > 1) {
                mAnswer.deleteCharAt(mAnswer.length() - 1);
                textViewAnswer.setText(mAnswer);
            } else if (mAnswer.length() == 1) {
                mAnswer.deleteCharAt(mAnswer.length() - 1);
                textViewAnswer.setText(mAnswer);
                if (mType == TrainingWordsActivity.TYPE_FROM_ENG)
                    textViewAnswer.setText(R.string.string_your_answer);
                if (mType == TrainingWordsActivity.TYPE_TO_ENG)
                    textViewAnswer.setText(R.string.string_your_answer_en);
            }
        }
    };
    private View.OnClickListener buttonTestListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonTest.setOnClickListener(null);
            buttonTest.setClickable(false);
            for (View view : mButtons) {
                if (view instanceof ImageButton) {
                    view.setEnabled(false);
                    view.setAlpha(0.8f);
                    ((ImageButton) view).setImageAlpha(77);
                } else
                    view.setEnabled(false);
            }

            //Is the answer right?
            boolean rightAnswer = false;
            for (String s : mTranslations) {
                if (s.toLowerCase().equals(mAnswer.toString().toLowerCase().replaceAll("(^ +)|( +$)", ""))) {
                    rightAnswer = true;
                    break;
                }
            }

            //Animate textView with answers
            if (rightAnswer) {
                textViewAnswer.setTextColor(getResources().getColor(R.color.colorGuessed));
            } else {
                textViewRightAnswer.setText(mTranslations.get(0));
                textViewAnswer.setTextColor(getResources().getColor(R.color.colorError));
                textViewAnswer.setPaintFlags(textViewAnswer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                vibrate(mVibrator, DEFAULT_DURATION_ANIMATION);

                //Animate the bad answer
                final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textViewAnswer.getLayoutParams();
                ValueAnimator valueAnimatorLeft = ValueAnimator.ofInt(params.leftMargin, (int) (10 * getResources().getDisplayMetrics().density));
                valueAnimatorLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.leftMargin = (int) animation.getAnimatedValue();
                        textViewAnswer.setLayoutParams(params);
                    }
                });
                ValueAnimator valueAnimatorRight = ValueAnimator.ofInt(params.rightMargin, (int) (10 * getResources().getDisplayMetrics().density));
                valueAnimatorRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.rightMargin = (int) animation.getAnimatedValue();
                        textViewAnswer.setLayoutParams(params);
                    }
                });
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playSequentially(valueAnimatorLeft, valueAnimatorRight);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playSequentially(valueAnimatorLeft, valueAnimatorRight);
                AnimatorSet animatorSetShake = new AnimatorSet();
                animatorSetShake.playSequentially(animatorSet1, animatorSet2);
                animatorSetShake.setDuration(DEFAULT_DURATION_ANIMATION);
                animatorSetShake.start();

                ValueAnimator valueAnimatorBottom = ValueAnimator.ofInt(params.bottomMargin, (int) (50 * getResources().getDisplayMetrics().density));
                valueAnimatorBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.bottomMargin = (int) animation.getAnimatedValue();
                        textViewAnswer.setLayoutParams(params);
                    }
                });

                ObjectAnimator answerVisibility = ObjectAnimator.ofFloat(textViewRightAnswer, "alpha", 0f, 1f);
                AnimatorSet animatorSetMoveUp = new AnimatorSet();
                animatorSetMoveUp.playTogether(valueAnimatorBottom, answerVisibility);
                animatorSetMoveUp.setStartDelay(DEFAULT_DURATION_ANIMATION * 5);
                animatorSetMoveUp.setDuration(DEFAULT_DURATION_ANIMATION);
                animatorSetMoveUp.start();
            }

            //Tell to activity the result
            if (mListener != null) {
                mListener.onAnswerReceived(mAnswer.toString().replaceAll("(^ +)|( +$)", ""));
            }
        }
    };
    private View.OnLongClickListener buttonBackspaceLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mAnswer = new StringBuilder();
            if (mType == TrainingWordsActivity.TYPE_FROM_ENG)
                textViewAnswer.setText(R.string.string_your_answer);
            if (mType == TrainingWordsActivity.TYPE_TO_ENG)
                textViewAnswer.setText(R.string.string_your_answer_en);
            vibrate(mVibrator, DEFAULT_DURATION_BUTTON_VIBRATOR);
            return true;
        }
    };
    private View.OnClickListener textViewChoiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textView1.setOnClickListener(null);
            textView2.setOnClickListener(null);
            textView3.setOnClickListener(null);
            textView4.setOnClickListener(null);

            //Is the answer right?
            mAnswer = new StringBuilder(((TextView) v).getText().toString());
            boolean rightAnswer = false;
            for (String s : mTranslations) {
                if (s.toLowerCase().equals(mAnswer.toString().toLowerCase())) {
                    rightAnswer = true;
                    break;
                }
            }

            //Show right answer and error if needed
            if (rightAnswer) {
                v.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_green));
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorGuessed));
            } else {
                AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.animator_shake_wrong_answer);
                animatorSet.setTarget(v);
                animatorSet.start();
                vibrate(mVibrator, DEFAULT_DURATION_ANIMATION);
                v.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_red));
                ((TextView) v).setTextColor(getResources().getColor(R.color.colorError));

                for (String s : mTranslations) {
                    if (s.equals(textView1.getText().toString())) {
                        textView1.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_green));
                        textView1.setTextColor(getResources().getColor(R.color.colorGuessed));
                        break;
                    } else if (s.equals(textView2.getText().toString())) {
                        textView2.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_green));
                        textView2.setTextColor(getResources().getColor(R.color.colorGuessed));
                        break;
                    } else if (s.equals(textView3.getText().toString())) {
                        textView3.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_green));
                        textView3.setTextColor(getResources().getColor(R.color.colorGuessed));
                        break;
                    } else if (s.equals(textView4.getText().toString())) {
                        textView4.setBackground(getResources().getDrawable(R.drawable.custom_shape_drawable_green));
                        textView4.setTextColor(getResources().getColor(R.color.colorGuessed));
                        break;
                    }
                }
            }

            //Tell to activity the result
            if (mListener != null) {
                mListener.onAnswerReceived(mAnswer.toString());
            }
        }
    };

    public FragmentGame() {

    }

    public static FragmentGame newInstance(String word, ArrayList<String> translations, int type) {
        FragmentGame fragment = new FragmentGame();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        args.putStringArrayList(ARG_TRANSLATIONS, translations);
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentGame newInstance(String word, ArrayList<String> translations, ArrayList<String> choice, int type) {
        FragmentGame fragment = new FragmentGame();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        args.putStringArrayList(ARG_TRANSLATIONS, translations);
        args.putStringArrayList(ARG_CHOICE, choice);
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mButtons = new ArrayList<>();
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        mButtonSize = (int) (mDisplayMetrics.widthPixels / 11.5);
        mButtonSizeEng = mDisplayMetrics.widthPixels / 10;
        mButtonSpaceSize = mDisplayMetrics.widthPixels / 2;
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        if (getArguments() != null) {
            mWord = getArguments().getString(ARG_WORD);
            mTranslations = getArguments().getStringArrayList(ARG_TRANSLATIONS);
            mType = getArguments().getInt(ARG_TYPE);
            if (mType == TrainingWordsActivity.TYPE_CHOICE_FROM_ENG || mType == TrainingWordsActivity.TYPE_CHOICE_TO_ENG) {
                mChoices = getArguments().getStringArrayList(ARG_CHOICE);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        if (mType == TrainingWordsActivity.TYPE_FROM_ENG || mType == TrainingWordsActivity.TYPE_TO_ENG) {
            rootView = inflater.inflate(R.layout.fragment_game, container, false);
            mTextViewWord = rootView.findViewById(R.id.textViewWord);
            mTextViewWord.setText(mWord);
            textViewAnswer = rootView.findViewById(R.id.textViewAnswer);
            textViewRightAnswer = rootView.findViewById(R.id.textViewRightAnswer);

            if (mType == TrainingWordsActivity.TYPE_FROM_ENG)
                textViewAnswer.setText(R.string.string_your_answer);
            if (mType == TrainingWordsActivity.TYPE_TO_ENG)
                textViewAnswer.setText(R.string.string_your_answer_en);
            buttonTest = rootView.findViewById(R.id.buttonTest);
            buttonTest.setOnClickListener(buttonTestListener);
            mAnswer = new StringBuilder();
            linearLayout1 = rootView.findViewById(R.id.linear1);
            linearLayout2 = rootView.findViewById(R.id.linear2);
            linearLayout3 = rootView.findViewById(R.id.linear3);
            linearLayout4 = rootView.findViewById(R.id.linear4);
            setupKeyboard();
        } else if (mType == TrainingWordsActivity.TYPE_CHOICE_FROM_ENG || mType == TrainingWordsActivity.TYPE_CHOICE_TO_ENG) {
            rootView = inflater.inflate(R.layout.fragment_game_choice, container, false);
            mTextViewWord = rootView.findViewById(R.id.textViewWord);
            mTextViewWord.setText(mWord);
            textView1 = rootView.findViewById(R.id.textView1);
            textView1.setText(mChoices.get(0));
            textView1.setOnClickListener(textViewChoiceClickListener);
            animateChoiceButton(textView1);
            textView2 = rootView.findViewById(R.id.textView2);
            textView2.setText(mChoices.get(1));
            textView2.setOnClickListener(textViewChoiceClickListener);
            animateChoiceButton(textView2);
            textView3 = rootView.findViewById(R.id.textView3);
            textView3.setText(mChoices.get(2));
            textView3.setOnClickListener(textViewChoiceClickListener);
            animateChoiceButton(textView3);
            textView4 = rootView.findViewById(R.id.textView4);
            textView4.setText(mChoices.get(3));
            textView4.setOnClickListener(textViewChoiceClickListener);
            animateChoiceButton(textView4);
        }

        return rootView;
    }

    private void setupKeyboard() {
        Button buttonDash = new Button(getActivity());
        buttonDash.setText("-");
        buttonDash.setOnClickListener(buttonLetterListener);

        Button buttonSpace = new Button(getActivity());
        buttonSpace.setText(" ");
        LinearLayout.LayoutParams buttonSpaceParams = new LinearLayout.LayoutParams(mButtonSpaceSize, (int) (mButtonSize * 1.5));
        buttonSpace.setOnClickListener(buttonLetterListener);

        ImageButton buttonBackSpace = new ImageButton(getActivity());
        buttonBackSpace.setImageResource(R.drawable.ic_keyboard_backspace);
        ViewGroup.LayoutParams buttonBackSpaceParams = new ViewGroup.LayoutParams((int) (mButtonSize * 1.5), (int) (mButtonSize * 1.5));
        buttonBackSpace.setOnClickListener(buttonBackSpaceListener);
        buttonBackSpace.setOnLongClickListener(buttonBackspaceLongClickListener);

        switch (mType) {
            case TrainingWordsActivity.TYPE_FROM_ENG:
                String locale;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    locale = getActivity().getResources().getConfiguration().getLocales().get(0).getLanguage();
                } else
                    locale = getActivity().getResources().getConfiguration().locale.getLanguage();

                //First row
                int count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr1().length() : Alphabet.getRus1().length();
                for (int i = 0; i < count; i++) {
                    Button button = new Button(getActivity());
                    button.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
                    button.setAllCaps(false);
                    button.setOnClickListener(buttonLetterListener);
                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
                            Alphabet.getUkr1().charAt(i) : Alphabet.getRus1().charAt(i)));
                    linearLayout1.addView(button, buttonParams);
                    mButtons.add(button);
                }

                //Second row
                count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr2().length() : Alphabet.getRus2().length();
                for (int i = 0; i < count; i++) {
                    Button button = new Button(getActivity());
                    button.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
                    button.setAllCaps(false);
                    button.setOnClickListener(buttonLetterListener);
                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
                            Alphabet.getUkr2().charAt(i) : Alphabet.getRus2().charAt(i)));
                    linearLayout2.addView(button, buttonParams);
                    mButtons.add(button);
                }

                //Third row
                count = locale.equals(DictionariesActivity.LOCALE_UKR) ? Alphabet.getUkr3().length() : Alphabet.getRus3().length();
                for (int i = 0; i < count; i++) {
                    Button button = new Button(getActivity());
                    button.setPadding(0, 0, 0, 0);
                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
                    button.setAllCaps(false);
                    button.setOnClickListener(buttonLetterListener);
                    button.setText(String.valueOf(locale.equals(DictionariesActivity.LOCALE_UKR) ?
                            Alphabet.getUkr3().charAt(i) : Alphabet.getRus3().charAt(i)));
                    linearLayout3.addView(button, buttonParams);
                    mButtons.add(button);
                }

                //Forth row
                LinearLayout.LayoutParams buttonDashParams = new LinearLayout.LayoutParams(mButtonSize, (int) (mButtonSize * 1.5));
                linearLayout4.addView(buttonDash, buttonDashParams);
                linearLayout4.addView(buttonSpace, buttonSpaceParams);
                linearLayout4.addView(buttonBackSpace, buttonBackSpaceParams);
                mButtons.add(buttonDash);
                mButtons.add(buttonSpace);
                mButtons.add(buttonBackSpace);
                break;
            case TrainingWordsActivity.TYPE_TO_ENG:

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
                LinearLayout.LayoutParams buttonDashParamsEng = new LinearLayout.LayoutParams(mButtonSizeEng, (int) (mButtonSizeEng * 1.5));
                linearLayout3.addView(buttonDash, buttonDashParamsEng);
                mButtons.add(buttonDash);

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

                //Forth row
                linearLayout4.addView(buttonSpace, buttonSpaceParams);
                mButtons.add(buttonSpace);
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerFromFragmentListener) {
            mListener = (OnAnswerFromFragmentListener) context;
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

    private void vibrate(Vibrator vibrator, int milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else vibrator.vibrate(milliseconds);
    }

    public interface OnAnswerFromFragmentListener {
        void onAnswerReceived(String answer);
    }

    private void animateChoiceButton(View view) {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.animator_choice_buttons_appearing);
        set.setTarget(view);
        set.start();
    }
}
