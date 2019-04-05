package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.IrregularVerb;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleIrregularAdapter extends RecyclerView.Adapter<SimpleIrregularAdapter.ViewHolder> {
    private List<IrregularVerb> mFullList;
    private List<IrregularVerb> mFilteredList;
    private boolean mFiltered;
    private Context mContext;

    public SimpleIrregularAdapter(List<IrregularVerb> irregular, Context context) {
        mFullList = irregular;
        mFilteredList = new ArrayList<>();
        for (IrregularVerb i : mFullList) {
            if (i.getTrained() == 1) {
                mFilteredList.add(i);
            }
        }
        mContext = context;
    }

    public void setFiltered(boolean filtered) {
        mFiltered = filtered;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_simple_irregular, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewWord1.setText(mFiltered ? mFilteredList.get(position).getWord1() : mFullList.get(position).getWord1());
        holder.textViewWord2.setText(mFiltered ? mFilteredList.get(position).getWord2() : mFullList.get(position).getWord2());
        holder.textViewWord3.setText(mFiltered ? mFilteredList.get(position).getWord3() : mFullList.get(position).getWord3());
    }

    @Override
    public int getItemCount() {
        return mFiltered ? mFilteredList.size() : mFullList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWord1;
        private TextView textViewWord2;
        private TextView textViewWord3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord1 = itemView.findViewById(R.id.word1);
            textViewWord2 = itemView.findViewById(R.id.word2);
            textViewWord3 = itemView.findViewById(R.id.word3);
        }
    }
}
