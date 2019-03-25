package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.AnimationUtils;
import com.evgeniy.moiseev.learnwords.DictionariesActivity;
import com.evgeniy.moiseev.learnwords.ListHolder;
import com.evgeniy.moiseev.learnwords.OnOpeningAdapter;
import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.view.CustomViewWordLabel;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> implements ListHolder, OnOpeningAdapter {
    private List<Word> mWordList;

    private Context mContext;
    private View mOpenedView = null;
    private OnFavoriteCardListener onFavoriteCardListener;

    @Override
    public View getOpenedView() {
        return mOpenedView;
    }

    @Override
    public void setOpenedView(View view) {
        mOpenedView = view;
    }

    public FavoritesAdapter(Context context) {
        mContext = context;
    }

    public void setWordItems(List<Word> wordList) {
        mWordList = wordList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card_layout_dictionary, parent, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder.mCardView == mOpenedView) {
            new AnimationUtils(mContext).forceCloseRecyclerViewItem(mOpenedView, false);
            mOpenedView = null;
        }

        holder.textViewWord.setText(mWordList.get(position).getWord());
        holder.textViewTranscription.setText(mWordList.get(position).getTranscription());

        if (((DictionariesActivity) mContext).getLocale().equals(DictionariesActivity.LOCALE_RUS)) {
            holder.textViewTranslation.setText(mWordList.get(position).getTranslationRus());
            holder.textViewCategory.setText(mWordList.get(position).getCategoryRus().toUpperCase());
        } else if (((DictionariesActivity) mContext).getLocale().equals(DictionariesActivity.LOCALE_UKR)) {
            holder.textViewTranslation.setText(mWordList.get(position).getTranslationUkr());
            holder.textViewCategory.setText(mWordList.get(position).getCategoryUkr().toUpperCase());
        } else {
            holder.textViewTranslation.setText(mWordList.get(position).getTranslationRus());
            holder.textViewCategory.setText(mWordList.get(position).getCategory().toUpperCase());
        }

        holder.textViewGuessed.setText("+" + String.valueOf(mWordList.get(position).getGuessed()));
        holder.textViewNotGuessed.setText("-" + String.valueOf(mWordList.get(position).getNotGuessed()));

        holder.imageViewFavorite.setImageResource(R.drawable.ic_favorite);

        holder.clearDifficulty();
        switch (mWordList.get(position).getDifficulty()) {
            case Word.DIFFICULTY_EASY:
                setDifficulty(holder.textViewEasy);
                break;
            case Word.DIFFICULTY_MEDIUM:
                setDifficulty(holder.textViewMedium);
                break;
            case Word.DIFFICULTY_HARD:
                setDifficulty(holder.textViewHard);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mWordList != null && mWordList.size() != 0) {
            return mWordList.size();
        } else {
            return 0;
        }
    }

    @Override
    public List getList() {
        return mWordList;
    }

    public interface OnFavoriteCardListener {
        void onSpeechImageViewClicked(String textToSpeech);

        void onCardViewClicked(OnOpeningAdapter adapter, View clickedView);

        void onDeleteClicked(String word);

    }

    public void setOnFavoriteCardListener(OnFavoriteCardListener listener) {
        onFavoriteCardListener = listener;
    }

    private void setDifficulty(TextView textView) {
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setAlpha(1.0f);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView mCardView;

        private final TextView textViewWord;
        private final TextView textViewTranscription;
        private final TextView textViewTranslation;

        private final ImageView imageViewSpeech;
        private final ImageView imageViewEdit;
        private final ImageView imageViewDelete;
        private final ImageView imageViewFavorite;

        private final TextView textViewEasy;
        private final TextView textViewMedium;
        private final TextView textViewHard;

        private final CustomViewWordLabel textViewCategory;

        private final TextView textViewGuessed;
        private final TextView textViewNotGuessed;

        public ViewHolder(CardView cardView) {
            super(cardView);

            mCardView = cardView;

            textViewWord = mCardView.findViewById(R.id.textViewWord);
            textViewTranscription = mCardView.findViewById(R.id.textViewTranscription);
            textViewTranslation = mCardView.findViewById(R.id.textViewTranslate);

            imageViewSpeech = mCardView.findViewById(R.id.speechImageView);

            textViewEasy = mCardView.findViewById(R.id.textViewEasy);
            textViewMedium = mCardView.findViewById(R.id.textViewMedium);
            textViewHard = mCardView.findViewById(R.id.textViewHard);

            textViewCategory = mCardView.findViewById(R.id.textViewCategory);

            textViewGuessed = mCardView.findViewById(R.id.textViewGuessed);
            textViewNotGuessed = mCardView.findViewById(R.id.textViewNotGuessed);

            imageViewEdit = mCardView.findViewById(R.id.editImageView);
            imageViewDelete = mCardView.findViewById(R.id.deleteImageView);
            imageViewFavorite = mCardView.findViewById(R.id.favoriteImageView);

            imageViewEdit.setOnClickListener(null);
            imageViewFavorite.setOnClickListener(null);
            textViewEasy.setOnClickListener(null);
            textViewMedium.setOnClickListener(null);
            textViewHard.setOnClickListener(null);
            mCardView.findViewById(R.id.linearLayoutDifficulty).setAlpha(0.5f);

            if (onFavoriteCardListener != null) {
                imageViewSpeech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFavoriteCardListener.onSpeechImageViewClicked(textViewWord.getText().toString());
                    }
                });

                mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFavoriteCardListener.onCardViewClicked(FavoritesAdapter.this, v);
                    }
                });

                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFavoriteCardListener.onDeleteClicked(textViewWord.getText().toString());
                    }
                });

            }
        }

        private void clearDifficulty() {
            textViewEasy.setAlpha(0.5f);
            textViewEasy.setTextColor(Color.BLACK);
            textViewEasy.setTypeface(Typeface.DEFAULT);
            textViewMedium.setAlpha(0.5f);
            textViewMedium.setTextColor(Color.BLACK);
            textViewMedium.setTypeface(Typeface.DEFAULT);
            textViewHard.setAlpha(0.5f);
            textViewHard.setTextColor(Color.BLACK);
            textViewHard.setTypeface(Typeface.DEFAULT);
        }

    }
}