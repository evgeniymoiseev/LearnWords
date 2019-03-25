package com.evgeniy.moiseev.learnwords.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.SimpleWord;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TestEndsAdapter extends RecyclerView.Adapter<TestEndsAdapter.ViewHolder> {
    private List<SimpleWord> mSimpleWords;
    private LayoutInflater inflater;

    public TestEndsAdapter(Context context, List<SimpleWord> simpleWords) {
        inflater = LayoutInflater.from(context);
        mSimpleWords = simpleWords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_layout_end_game_statistics, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleWord currentWord = mSimpleWords.get(position);
        holder.textViewWord.setText(currentWord.getWord());
        holder.textViewPositive.setText(currentWord.getGuessed() == 0 ?
                String.valueOf(currentWord.getGuessed()) :
                "+" + String.valueOf(currentWord.getGuessed()));
        holder.textViewNegative.setText(String.valueOf(currentWord.getNotGuessed()));
        ObjectAnimator.ofInt(holder.progressBarPositive, "progress", 0, currentWord.getGuessed() * 60)
                .setDuration(300).start();
        ObjectAnimator.ofInt(holder.progressBarNegative, "progress", 0, currentWord.getNotGuessed() * -1 * 60)
                .setDuration(300).start();
    }

    @Override
    public int getItemCount() {
        return mSimpleWords.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWord;
        private TextView textViewPositive;
        private TextView textViewNegative;
        private ProgressBar progressBarPositive;
        private ProgressBar progressBarNegative;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.textViewWord);
            textViewPositive = itemView.findViewById(R.id.textViewPositive);
            textViewNegative = itemView.findViewById(R.id.textViewNegative);
            progressBarPositive = itemView.findViewById(R.id.progressBarPositive);
            progressBarNegative = itemView.findViewById(R.id.progressBarNegative);
        }
    }
}
