package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.AnimationUtils;
import com.evgeniy.moiseev.learnwords.DictionariesActivity;
import com.evgeniy.moiseev.learnwords.ListHolder;
import com.evgeniy.moiseev.learnwords.OnOpeningAdapter;
import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.view.CustomViewWordLabel;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> implements Filterable, ListHolder, OnOpeningAdapter {
    private List<Word> mWordList;
    private List<Word> mWordListFull;

    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mTextViewLoading;
    private View mOpenedView = null;
    private OnCardViewActionListener onCardViewActionListener;

    @Override
    public View getOpenedView() {
        return mOpenedView;
    }

    @Override
    public void setOpenedView(View view) {
        mOpenedView = view;
    }

    public WordAdapter(Context context, View progressBar, View textViewLoading) {
        mContext = context;
        mProgressBar = (ProgressBar) progressBar;
        mTextViewLoading = (TextView) textViewLoading;
    }

    public void setWordItems(List<Word> wordList) {
        mWordList = wordList;
        if (mWordListFull == null)
            setFullList(mWordList);
        notifyDataSetChanged();
    }

    public void setFullList(List<Word> fullList) {
        mWordListFull = new ArrayList<>(fullList);
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
            new AnimationUtils(mContext).forceCloseRecyclerViewItem(mOpenedView, true);
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

        holder.imageViewFavorite.setImageResource(mWordList.get(position).getIsFavorite() == 0 ? R.drawable.ic_favorite_empty : R.drawable.ic_favorite);

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
            mProgressBar.setVisibility(View.GONE);
            mTextViewLoading.setVisibility(View.GONE);
            return mWordList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return wordFilter;
    }

    private Filter wordFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Word> filteredList = new ArrayList<>();
            if (constraint == null && constraint.length() == 0) {
                filteredList.addAll(mWordListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Word w : mWordListFull) {
                    if (w.getWord().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(w);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mWordList.clear();
            mWordList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public List getList() {
        return mWordList;
    }

    public interface OnCardViewActionListener {
        void onSpeechImageViewClicked(String textToSpeech);

        void onCardViewClicked(OnOpeningAdapter adapter, View clickedView, boolean enabled);

        void onDeleteClicked(String word);

        void onFavoriteClicked(String word, WordAdapter adapter, View clickedView, int isFavorite);

        void onDifficultyClicked(String word, int difficulty, TextView textView, WordAdapter adapter, View clickedView);

        void onEditClicked(String word, String transcription, String translation, int difficulty, String category, int guessed, int notGuessed, int isFavorite);
    }

    public void setOnCardViewActionListener(OnCardViewActionListener listener) {
        onCardViewActionListener = listener;
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
        private final ImageView imageViewFavorite;
        private final ImageView imageViewEdit;
        private final ImageView imageViewDelete;

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

            imageViewFavorite = mCardView.findViewById(R.id.favoriteImageView);
            imageViewEdit = mCardView.findViewById(R.id.editImageView);
            imageViewDelete = mCardView.findViewById(R.id.deleteImageView);

            if (onCardViewActionListener != null) {
                imageViewSpeech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardViewActionListener.onSpeechImageViewClicked(textViewWord.getText().toString());
                    }
                });

                mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardViewActionListener.onCardViewClicked(WordAdapter.this, v, true);
                    }
                });

                imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardViewActionListener.onFavoriteClicked(textViewWord.getText().toString(),
                                WordAdapter.this, mCardView, mWordList.get(getAdapterPosition()).getIsFavorite() == 0 ? 1 : 0);
                    }
                });

                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardViewActionListener.onDeleteClicked(textViewWord.getText().toString());
                    }
                });

                imageViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardViewActionListener.onEditClicked(textViewWord.getText().toString(),
                                textViewTranscription.getText().toString(),
                                textViewTranslation.getText().toString(),
                                mWordList.get(getAdapterPosition()).getDifficulty(),
                                textViewCategory.getText(),
                                mWordList.get(getAdapterPosition()).getGuessed(),
                                mWordList.get(getAdapterPosition()).getNotGuessed(),
                                mWordList.get(getAdapterPosition()).getIsFavorite());
                    }
                });

                textViewEasy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mWordList.get(getAdapterPosition()).getDifficulty() == Word.DIFFICULTY_EASY)
                            return;
                        onCardViewActionListener.onDifficultyClicked(mWordList.get(getAdapterPosition()).getWord(),
                                Word.DIFFICULTY_EASY, textViewEasy, WordAdapter.this, mCardView);
                    }
                });

                textViewMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mWordList.get(getAdapterPosition()).getDifficulty() == Word.DIFFICULTY_MEDIUM)
                            return;
                        onCardViewActionListener.onDifficultyClicked(mWordList.get(getAdapterPosition()).getWord(),
                                Word.DIFFICULTY_MEDIUM, textViewMedium, WordAdapter.this, mCardView);
                    }
                });

                textViewHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mWordList.get(getAdapterPosition()).getDifficulty() == Word.DIFFICULTY_HARD)
                            return;
                        onCardViewActionListener.onDifficultyClicked(mWordList.get(getAdapterPosition()).getWord(),
                                Word.DIFFICULTY_HARD, textViewHard, WordAdapter.this, mCardView);
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