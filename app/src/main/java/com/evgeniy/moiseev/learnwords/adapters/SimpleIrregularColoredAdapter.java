package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.SimpleIrregular;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleIrregularColoredAdapter extends RecyclerView.Adapter<SimpleIrregularColoredAdapter.ViewHolder> {
    private List<SimpleIrregular> mList;
    private Context mContext;

    public SimpleIrregularColoredAdapter(List<SimpleIrregular> list, Context context) {
        mList = list;
        mContext = context;
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
        holder.textViewWord1.setText(mList.get(position).getWord1());
        holder.textViewWord1.setTextColor(mList.get(position).isB1() ?
                mContext.getResources().getColor(R.color.colorIcons) : mContext.getResources().getColor(R.color.colorError));
        holder.textViewWord2.setText(mList.get(position).getWord2());
        holder.textViewWord2.setTextColor(mList.get(position).isB2() ?
                mContext.getResources().getColor(R.color.colorIcons) : mContext.getResources().getColor(R.color.colorError));
        holder.textViewWord3.setText(mList.get(position).getWord3());
        holder.textViewWord3.setTextColor(mList.get(position).isB3() ?
                mContext.getResources().getColor(R.color.colorIcons) : mContext.getResources().getColor(R.color.colorError));

        if (mList.get(position).isRight()) {
            holder.textViewWord1.setTextColor(mContext.getResources().getColor(R.color.colorGuessed));
            holder.textViewWord2.setTextColor(mContext.getResources().getColor(R.color.colorGuessed));
            holder.textViewWord3.setTextColor(mContext.getResources().getColor(R.color.colorGuessed));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
