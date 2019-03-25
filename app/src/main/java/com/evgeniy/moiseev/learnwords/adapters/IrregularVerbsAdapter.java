package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.ListHolder;
import com.evgeniy.moiseev.learnwords.R;
import com.evgeniy.moiseev.learnwords.data.IrregularVerb;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IrregularVerbsAdapter extends RecyclerView.Adapter<IrregularVerbsAdapter.ViewHolder> implements ListHolder {
    private List<IrregularVerb> mIrregularVerbs;
    private Context mContext;
    private OnIrregularCardListener mOnIrregularCardListener;

    public IrregularVerbsAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_irregular_verbs, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void setIrregularItems(List<IrregularVerb> irregularVerbs) {
        mIrregularVerbs = irregularVerbs;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.word1.setTextSize(14.0f);
        holder.word2.setTextSize(14.0f);
        holder.word3.setTextSize(14.0f);
        holder.transcription1.setTextSize(14.0f);
        holder.transcription2.setTextSize(14.0f);
        holder.transcription3.setTextSize(14.0f);

        holder.word1.setText(mIrregularVerbs.get(position).getWord1());
        holder.word2.setText(mIrregularVerbs.get(position).getWord2());
        holder.word3.setText(mIrregularVerbs.get(position).getWord3());
        holder.word1.setTextSize(measureTextSize(holder.word1.getText().toString()));
        holder.word2.setTextSize(measureTextSize(holder.word2.getText().toString()));
        holder.word3.setTextSize(measureTextSize(holder.word3.getText().toString()));

        holder.transcription1.setText(mIrregularVerbs.get(position).getTranscription1());
        holder.transcription2.setText(mIrregularVerbs.get(position).getTranscription2());
        holder.transcription3.setText(mIrregularVerbs.get(position).getTranscription3());
        holder.transcription1.setTextSize(measureTextSize(holder.transcription1.getText().toString()));
        holder.transcription2.setTextSize(measureTextSize(holder.transcription2.getText().toString()));
        holder.transcription3.setTextSize(measureTextSize(holder.transcription3.getText().toString()));

        holder.translation.setText(mIrregularVerbs.get(position).getTranslationRus());
    }

    @Override
    public int getItemCount() {
        if (mIrregularVerbs != null && mIrregularVerbs.size() != 0) {
            return mIrregularVerbs.size();
        } else {
            return 0;
        }
    }

    @Override
    public List getList() {
        return mIrregularVerbs;
    }

    public interface OnIrregularCardListener {
        void onWord1Clicked(String word1);

        void onWord2Clicked(String word2);

        void onWord3Clicked(String word3);
    }

    private float measureTextSize(String text) {
        float size = 14.0f;
        if (text.length() >= 14 && text.length() < 15)
            size = 13.0f;
        if (text.length() >= 15 && text.length() < 18)
            size = 12.0f;
        else if (text.length() >= 18 && text.length() < 25)
            size = 9.0f;
        else if (text.length() > 25)
            size = 8.0f;
        return size;
    }

    public void setOnIrregularCardListener(OnIrregularCardListener listener) {
        mOnIrregularCardListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView word1;
        TextView word2;
        TextView word3;
        TextView transcription1;
        TextView transcription2;
        TextView transcription3;
        TextView translation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            word1 = itemView.findViewById(R.id.textViewWord1);
            word2 = itemView.findViewById(R.id.textViewWord2);
            word3 = itemView.findViewById(R.id.textViewWord3);
            transcription1 = itemView.findViewById(R.id.textViewTranscription1);
            transcription2 = itemView.findViewById(R.id.textViewTranscription2);
            transcription3 = itemView.findViewById(R.id.textViewTranscription3);
            translation = itemView.findViewById(R.id.textViewTranslation);

            if (mOnIrregularCardListener != null) {
                itemView.findViewById(R.id.linear1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnIrregularCardListener.onWord1Clicked(word1.getText().toString());
                    }
                });
                itemView.findViewById(R.id.linear2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnIrregularCardListener.onWord2Clicked(word2.getText().toString());
                    }
                });
                itemView.findViewById(R.id.linear3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnIrregularCardListener.onWord3Clicked(word3.getText().toString());
                    }
                });
            }
        }
    }
}
