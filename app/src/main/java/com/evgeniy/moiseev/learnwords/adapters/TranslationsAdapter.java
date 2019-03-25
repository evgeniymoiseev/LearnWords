package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TranslationsAdapter extends RecyclerView.Adapter<TranslationsAdapter.ViewHolder> {
    private List<String> mTranslations;
    private LayoutInflater mInflater;
    private OnTranslationClickListener listener;
    private boolean isInteracted = false;

    public TranslationsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setTranslations(List<String> translations) {
        mTranslations = translations;
        notifyDataSetChanged();
    }

    public List<String> getTranslations() {
        return mTranslations;
    }

    public void setListener(OnTranslationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.card_layout_translations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(mTranslations.get(position));
        if (mTranslations.size() > 1 && isInteracted) {
            holder.imageView.setAlpha(0.87f);
        } else {
            holder.imageView.setAlpha(0.38f);
        }
    }

    public void setInteracted() {
        isInteracted = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mTranslations != null) {
            return mTranslations.size();
        } else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            textView = itemView.findViewById(R.id.text_view_translation);
            imageView = itemView.findViewById(R.id.button_delete_translation);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCardClick(textView.getText().toString());
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClearClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnTranslationClickListener {
        void onCardClick(String text);

        void onClearClick(int position);
    }
}
