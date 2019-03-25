package com.evgeniy.moiseev.learnwords;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AnimationUtils {

    private Context mContext;
    public boolean isAnimatedNow = false;

    public AnimationUtils(Context context) {
        mContext = context;
    }

    public void openRecyclerViewItem(final View view, long duration, boolean enabled) {

        //SetVisible guesses
        ObjectAnimator objectAnimatorVisibleGuesses;
        ObjectAnimator objectAnimatorVisibleNotGuesses;

        if (!((TextView) view.findViewById(R.id.textViewGuessed)).getText().toString().equals("+0")) {
            objectAnimatorVisibleGuesses =
                    ObjectAnimator.ofFloat(view.findViewById(R.id.textViewGuessed), "alpha", 1);
        } else
            objectAnimatorVisibleGuesses =
                    ObjectAnimator.ofFloat(view.findViewById(R.id.textViewGuessed), "alpha", 0);

        if (!((TextView) view.findViewById(R.id.textViewNotGuessed)).getText().toString().equals("-0")) {
            objectAnimatorVisibleNotGuesses =
                    ObjectAnimator.ofFloat(view.findViewById(R.id.textViewNotGuessed), "alpha", 1);
        } else objectAnimatorVisibleNotGuesses =
                ObjectAnimator.ofFloat(view.findViewById(R.id.textViewNotGuessed), "alpha", 0);

        objectAnimatorVisibleGuesses.setStartDelay(300);
        objectAnimatorVisibleNotGuesses.setStartDelay(300);

        //Increase height
        final ViewGroup.LayoutParams paramsCardView = view.getLayoutParams();
        ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close),
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_open));
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsCardView.height = (int) valueAnimator.getAnimatedValue();
                view.setLayoutParams(paramsCardView);
            }
        });

        //Increase elevation
        ObjectAnimator objectAnimatorElevation = ObjectAnimator.ofFloat(view, "elevation",
                mContext.getResources().getInteger(R.integer.recycler_view_item_elevation_open) *
                        mContext.getResources().getDisplayMetrics().density);

        //Increase words width
        final LinearLayout linearLayoutTop = view.findViewById(R.id.linearTop);
        final ConstraintLayout.LayoutParams linearTopParams = (ConstraintLayout.LayoutParams) linearLayoutTop.getLayoutParams();
        ValueAnimator valueAnimatorWidthLinearTop = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.linear_top_right_offset), 0);
        valueAnimatorWidthLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.rightMargin = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Decrease words height
        ValueAnimator valueAnimatorHeightLinearTop = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close),
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close) * 2 / 3);
        valueAnimatorHeightLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.height = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Increase words marginTop
        ValueAnimator valueAnimatorMarginTopLinearTop = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close) / 3);
        valueAnimatorMarginTopLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.topMargin = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Decrease SpeechIcon size
        final ImageView speechImageView = view.findViewById(R.id.speechImageView);
        final ConstraintLayout.LayoutParams paramsSpeechImageView = (ConstraintLayout.LayoutParams) speechImageView.getLayoutParams();
        ValueAnimator valueAnimatorSpeechImageViewSize = ValueAnimator.ofInt(paramsSpeechImageView.height,
                (int) mContext.getResources().getDimension(R.dimen.small_image_views_size));
        valueAnimatorSpeechImageViewSize.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                paramsSpeechImageView.height = (int) animation.getAnimatedValue();
                paramsSpeechImageView.width = (int) animation.getAnimatedValue();
                speechImageView.setLayoutParams(paramsSpeechImageView);
            }
        });

        //Decrease SpeechIcon padding
        ValueAnimator valueAnimatorSpeechImageViewPadding = ValueAnimator.ofInt(speechImageView.getPaddingTop(),
                (int) mContext.getResources().getDimension(R.dimen.small_padding_for_click));
        valueAnimatorSpeechImageViewPadding.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pad = (int) animation.getAnimatedValue();
                speechImageView.setPadding(pad, pad, pad, pad);
            }
        });

        //Move in FavoriteIcon
        final ImageView favoriteImageView = view.findViewById(R.id.favoriteImageView);
        final ConstraintLayout.LayoutParams paramsFavoriteImageView = (ConstraintLayout.LayoutParams) favoriteImageView.getLayoutParams();
        ValueAnimator valueAnimatorFavoriteImageViewMarginRight = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset), 0);
        valueAnimatorFavoriteImageViewMarginRight.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorFavoriteImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsFavoriteImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                favoriteImageView.setLayoutParams(paramsFavoriteImageView);
            }
        });

        //Set visible FavoriteIcon
        ObjectAnimator objectAnimatorFavoriteVisible = ObjectAnimator.ofFloat(
                favoriteImageView, "alpha", 0f, enabled ? 0.87f : 0.2f);

        //Move in EditIcon
        final ImageView editImageView = view.findViewById(R.id.editImageView);
        final ConstraintLayout.LayoutParams paramsEditImageView = (ConstraintLayout.LayoutParams) editImageView.getLayoutParams();
        ValueAnimator valueAnimatorEditImageViewMarginRight = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset), 0);
        valueAnimatorEditImageViewMarginRight.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorEditImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsEditImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                editImageView.setLayoutParams(paramsEditImageView);
            }
        });

        //Set visible EditIcon
        ObjectAnimator objectAnimatorEditVisible = ObjectAnimator.ofFloat(
                editImageView, "alpha", 0f, enabled ? 0.87f : 0.2f);

        //Move in DeleteIcon
        final ImageView deleteImageView = view.findViewById(R.id.deleteImageView);
        final ConstraintLayout.LayoutParams paramsDeleteImageView = (ConstraintLayout.LayoutParams) deleteImageView.getLayoutParams();
        ValueAnimator valueAnimatorDeleteImageViewMarginRight = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset), 0);
        valueAnimatorDeleteImageViewMarginRight.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorDeleteImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsDeleteImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                deleteImageView.setLayoutParams(paramsDeleteImageView);
            }
        });

        //Set visible EditIcon
        ObjectAnimator objectAnimatorDeleteVisible = ObjectAnimator.ofFloat(
                deleteImageView, "alpha", 0f, 0.87f);

        //Move in Difficulty
        final LinearLayout linearLayoutDifficulty = view.findViewById(R.id.linearLayoutDifficulty);
        final ConstraintLayout.LayoutParams paramsLinearLayoutDifficulty = (ConstraintLayout.LayoutParams) linearLayoutDifficulty.getLayoutParams();
        paramsLinearLayoutDifficulty.topMargin = (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close);
        linearLayoutDifficulty.setLayoutParams(paramsLinearLayoutDifficulty);
        linearLayoutDifficulty.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimatorLinearLayoutDifficultyTopMargin = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close), 0);
        valueAnimatorLinearLayoutDifficultyTopMargin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                paramsLinearLayoutDifficulty.topMargin = (int) animation.getAnimatedValue();
                linearLayoutDifficulty.setLayoutParams(paramsLinearLayoutDifficulty);
            }
        });
        valueAnimatorLinearLayoutDifficultyTopMargin.setStartDelay(300);

        //Create and start open animation
        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.playTogether(valueAnimatorHeight, objectAnimatorElevation,
                objectAnimatorVisibleGuesses, objectAnimatorVisibleNotGuesses,
                valueAnimatorWidthLinearTop, valueAnimatorHeightLinearTop, valueAnimatorMarginTopLinearTop,
                valueAnimatorSpeechImageViewSize, valueAnimatorSpeechImageViewPadding,
                valueAnimatorEditImageViewMarginRight, objectAnimatorEditVisible,
                valueAnimatorDeleteImageViewMarginRight, objectAnimatorDeleteVisible,
                valueAnimatorFavoriteImageViewMarginRight, objectAnimatorFavoriteVisible,
                valueAnimatorLinearLayoutDifficultyTopMargin);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimatedNow = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimatedNow = false;
            }
        });

        set.start();
    }

    public void closeRecyclerViewItem(final View view, long duration, boolean enabled) {

        //SetInvisible guesses
        ObjectAnimator objectAnimatorVisibleGuesses =
                ObjectAnimator.ofFloat(view.findViewById(R.id.textViewGuessed), "alpha", 0);

        ObjectAnimator objectAnimatorVisibleNotGuesses =
                ObjectAnimator.ofFloat(view.findViewById(R.id.textViewNotGuessed), "alpha", 0);

        //Decrease height
        final ViewGroup.LayoutParams paramsCardView = view.getLayoutParams();
        ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_open),
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close));
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsCardView.height = (int) valueAnimator.getAnimatedValue();
                view.setLayoutParams(paramsCardView);
            }
        });

        //Decrease elevation
        ObjectAnimator objectAnimatorElevation = ObjectAnimator.ofFloat(view, "elevation",
                mContext.getResources().getInteger(R.integer.recycler_view_item_elevation_close) *
                        mContext.getResources().getDisplayMetrics().density);


        //Decrease words width
        final LinearLayout linearLayoutTop = view.findViewById(R.id.linearTop);
        final ConstraintLayout.LayoutParams linearTopParams = (ConstraintLayout.LayoutParams) linearLayoutTop.getLayoutParams();
        ValueAnimator valueAnimatorWidthLinearTop = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.linear_top_right_offset));
        valueAnimatorWidthLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.rightMargin = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Increase words height
        ValueAnimator valueAnimatorHeightLinearTop = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close) * 2 / 3,
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close));
        valueAnimatorHeightLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.height = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Decrease words marginTop
        ValueAnimator valueAnimatorMarginTopLinearTop = ValueAnimator.ofInt(
                (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close) / 3, 0);
        valueAnimatorMarginTopLinearTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                linearTopParams.topMargin = (int) animation.getAnimatedValue();
                linearLayoutTop.setLayoutParams(linearTopParams);
            }
        });

        //Increase SpeechIcon size
        final ImageView speechImageView = view.findViewById(R.id.speechImageView);
        final ConstraintLayout.LayoutParams paramsSpeechImageView = (ConstraintLayout.LayoutParams) speechImageView.getLayoutParams();
        ValueAnimator valueAnimatorSpeechImageViewSize = ValueAnimator.ofInt(paramsSpeechImageView.height,
                (int) mContext.getResources().getDimension(R.dimen.default_image_views_size));
        valueAnimatorSpeechImageViewSize.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                paramsSpeechImageView.height = (int) animation.getAnimatedValue();
                paramsSpeechImageView.width = (int) animation.getAnimatedValue();
                speechImageView.setLayoutParams(paramsSpeechImageView);
            }
        });

        //Increase SpeechIcon padding
        ValueAnimator valueAnimatorSpeechImageViewPadding = ValueAnimator.ofInt(speechImageView.getPaddingTop(),
                (int) mContext.getResources().getDimension(R.dimen.default_padding_for_click));
        valueAnimatorSpeechImageViewPadding.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pad = (int) animation.getAnimatedValue();
                speechImageView.setPadding(pad, pad, pad, pad);
            }
        });

        //Move out FavoriteIcon
        final ImageView favoriteImageView = view.findViewById(R.id.favoriteImageView);
        final ConstraintLayout.LayoutParams paramsFavoriteImageView = (ConstraintLayout.LayoutParams) favoriteImageView.getLayoutParams();
        ValueAnimator valueAnimatorFavoriteImageViewMarginRight = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset));
        valueAnimatorFavoriteImageViewMarginRight.setInterpolator(new AccelerateInterpolator());
        valueAnimatorFavoriteImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsFavoriteImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                favoriteImageView.setLayoutParams(paramsFavoriteImageView);
            }
        });

        //Set invisible FavoriteIcon
        ObjectAnimator objectAnimatorFavoriteInvisible = ObjectAnimator.ofFloat(
                favoriteImageView, "alpha", enabled ? 0.87f : 0.2f, 0f);

        //Move out EditIcon
        final ImageView editImageView = view.findViewById(R.id.editImageView);
        final ConstraintLayout.LayoutParams paramsEditImageView = (ConstraintLayout.LayoutParams) editImageView.getLayoutParams();
        ValueAnimator valueAnimatorEditImageViewMarginRight = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset));
        valueAnimatorEditImageViewMarginRight.setInterpolator(new AccelerateInterpolator());
        valueAnimatorEditImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsEditImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                editImageView.setLayoutParams(paramsEditImageView);
            }
        });

        //Set invisible EditIcon
        ObjectAnimator objectAnimatorEditInvisible = ObjectAnimator.ofFloat(
                editImageView, "alpha", enabled ? 0.87f : 0.2f, 0f);

        //Move out DeleteIcon
        final ImageView deleteImageView = view.findViewById(R.id.deleteImageView);
        final ConstraintLayout.LayoutParams paramsDeleteImageView = (ConstraintLayout.LayoutParams) deleteImageView.getLayoutParams();
        ValueAnimator valueAnimatorDeleteImageViewMarginRight = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset));
        valueAnimatorDeleteImageViewMarginRight.setInterpolator(new AccelerateInterpolator());
        valueAnimatorDeleteImageViewMarginRight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                paramsDeleteImageView.rightMargin = (int) valueAnimator.getAnimatedValue();
                deleteImageView.setLayoutParams(paramsDeleteImageView);
            }
        });

        //Set invisible EditIcon
        ObjectAnimator objectAnimatorDeleteInvisible = ObjectAnimator.ofFloat(
                deleteImageView, "alpha", 0.87f, 0f);

        //Move out Difficulty
        final LinearLayout linearLayoutDifficulty = view.findViewById(R.id.linearLayoutDifficulty);
        final ConstraintLayout.LayoutParams paramsLinearLayoutDifficulty = (ConstraintLayout.LayoutParams) linearLayoutDifficulty.getLayoutParams();
        ValueAnimator valueAnimatorLinearLayoutDifficultyTopMargin = ValueAnimator.ofInt(
                0, (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close));
        valueAnimatorLinearLayoutDifficultyTopMargin.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                paramsLinearLayoutDifficulty.topMargin = (int) animation.getAnimatedValue();
                linearLayoutDifficulty.setLayoutParams(paramsLinearLayoutDifficulty);
            }
        });

        //Create and start close animation
        valueAnimatorLinearLayoutDifficultyTopMargin.start();
        objectAnimatorVisibleGuesses.start();
        objectAnimatorVisibleNotGuesses.start();

        AnimatorSet set = new AnimatorSet();
        set.setDuration(duration);
        set.playTogether(valueAnimatorHeight, objectAnimatorElevation,
                valueAnimatorWidthLinearTop, valueAnimatorHeightLinearTop, valueAnimatorMarginTopLinearTop,
                valueAnimatorSpeechImageViewSize, valueAnimatorSpeechImageViewPadding,
                valueAnimatorEditImageViewMarginRight, objectAnimatorEditInvisible,
                valueAnimatorFavoriteImageViewMarginRight, objectAnimatorFavoriteInvisible,
                valueAnimatorDeleteImageViewMarginRight, objectAnimatorDeleteInvisible);
        set.setStartDelay(300);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimatedNow = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimatedNow = false;
            }
        });

        set.start();
    }

    public void forceCloseRecyclerViewItem(final View view, boolean enabled) {

        //Set invisible guesses
        view.findViewById(R.id.textViewGuessed).setAlpha(0);
        view.findViewById(R.id.textViewNotGuessed).setAlpha(0);

        //Move out Difficulty
        final LinearLayout linearLayoutDifficulty = view.findViewById(R.id.linearLayoutDifficulty);
        final ConstraintLayout.LayoutParams paramsLinearLayoutDifficulty = (ConstraintLayout.LayoutParams) linearLayoutDifficulty.getLayoutParams();
        paramsLinearLayoutDifficulty.topMargin = (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close);
        linearLayoutDifficulty.setLayoutParams(paramsLinearLayoutDifficulty);
        linearLayoutDifficulty.setVisibility(View.GONE);

        //Decrease height
        final ViewGroup.LayoutParams paramsCardView = view.getLayoutParams();
        paramsCardView.height = (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close);
        view.setLayoutParams(paramsCardView);

        //Decrease elevation
        view.setElevation(mContext.getResources().getInteger(R.integer.recycler_view_item_elevation_close) *
                mContext.getResources().getDisplayMetrics().density);

        //Decrease words width, height, marginTop
        final LinearLayout linearLayoutTop = view.findViewById(R.id.linearTop);
        final ConstraintLayout.LayoutParams linearTopParams = (ConstraintLayout.LayoutParams) linearLayoutTop.getLayoutParams();
        linearTopParams.rightMargin = (int) mContext.getResources().getDimension(R.dimen.linear_top_right_offset);
        linearTopParams.topMargin = 0;
        linearTopParams.height = (int) mContext.getResources().getDimension(R.dimen.recycler_view_item_height_close);
        linearLayoutTop.setLayoutParams(linearTopParams);

        //Increase SpeechIcon size
        final ImageView speechImageView = view.findViewById(R.id.speechImageView);
        final ConstraintLayout.LayoutParams paramsSpeechImageView = (ConstraintLayout.LayoutParams) speechImageView.getLayoutParams();
        paramsSpeechImageView.width = (int) mContext.getResources().getDimension(R.dimen.default_image_views_size);
        paramsSpeechImageView.height = (int) mContext.getResources().getDimension(R.dimen.default_image_views_size);
        int pad = (int) mContext.getResources().getDimension(R.dimen.default_padding_for_click);
        speechImageView.setPadding(pad, pad, pad, pad);
        speechImageView.setLayoutParams(paramsSpeechImageView);

        //Move out FavoriteIcon
        final ImageView favoriteImageView = view.findViewById(R.id.favoriteImageView);
        final ConstraintLayout.LayoutParams paramsFavoriteImageView = (ConstraintLayout.LayoutParams) favoriteImageView.getLayoutParams();
        paramsFavoriteImageView.rightMargin = (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset);
        favoriteImageView.setLayoutParams(paramsFavoriteImageView);

        //Set invisible EditIcon
        favoriteImageView.setAlpha(0.0f);

        //Move out EditIcon
        final ImageView editImageView = view.findViewById(R.id.editImageView);
        final ConstraintLayout.LayoutParams paramsEditImageView = (ConstraintLayout.LayoutParams) editImageView.getLayoutParams();
        paramsEditImageView.rightMargin = (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset);
        editImageView.setLayoutParams(paramsEditImageView);

        //Set invisible EditIcon
        editImageView.setAlpha(0.0f);

        //Move out DeleteIcon
        final ImageView deleteImageView = view.findViewById(R.id.deleteImageView);
        final ConstraintLayout.LayoutParams paramsDeleteImageView = (ConstraintLayout.LayoutParams) deleteImageView.getLayoutParams();
        paramsDeleteImageView.rightMargin = (int) mContext.getResources().getDimension(R.dimen.edit_delete_icons_right_offset);
        deleteImageView.setLayoutParams(paramsDeleteImageView);

        //Set invisible EditIcon
        deleteImageView.setAlpha(0.0f);
    }

    public static void animateHintAddTranslation(TextView textView, final ImageView imageView, final View animationStarter) {

        ObjectAnimator objectAnimatorText = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        objectAnimatorText.setDuration(3000);
        objectAnimatorText.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationStarter.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                animationStarter.setClickable(false);
            }
        });
        objectAnimatorText.start();

        final ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int startValue = params.height;
        int endValue = (int) (params.height * 1.1);

        ValueAnimator valueAnimatorForward = ValueAnimator.ofInt(startValue, endValue);
        valueAnimatorForward.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.height = (int) animation.getAnimatedValue();
                params.width = (int) animation.getAnimatedValue();
                imageView.setLayoutParams(params);
            }
        });
        valueAnimatorForward.setDuration(250);

        ValueAnimator valueAnimatorReverse = ValueAnimator.ofInt(endValue, startValue);
        valueAnimatorReverse.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.height = (int) animation.getAnimatedValue();
                params.width = (int) animation.getAnimatedValue();
                imageView.setLayoutParams(params);
            }
        });
        valueAnimatorReverse.setDuration(250);

        AnimatorSet animatorSetButton = new AnimatorSet();
        animatorSetButton.playSequentially(valueAnimatorForward, valueAnimatorReverse);
        animatorSetButton.addListener(new AnimatorListenerAdapter() {
            int count = 0;

            @Override
            public void onAnimationEnd(Animator animation) {
                if (count == 2) {
                    animation.cancel();
                } else {
                    count++;
                    animation.start();
                }
            }
        });
        animatorSetButton.start();
    }
}
