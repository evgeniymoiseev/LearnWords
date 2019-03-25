package com.evgeniy.moiseev.learnwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.Category;
import com.evgeniy.moiseev.learnwords.DictionariesActivity;
import com.evgeniy.moiseev.learnwords.R;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<Category> mListCategories;
    private Map<Category, Integer> mCategoriesMap;
    private String mLocale;
    private String mCurrentCategory;
    private View mLastView;
    private boolean mSelected;
    private ChooseCategoryAdapterListener mListener;

    public ChooseCategoryAdapter(Context context, String locale,
                                 List<Category> list, Map<Category, Integer> map,
                                 ChooseCategoryAdapterListener listener) {
        mContext = context;
        mLocale = locale;
        mListCategories = list;
        mCategoriesMap = map;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.card_layout_choose_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textViewCategory.setText(mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                toSentenceCase(mListCategories.get(position).getCategoryUkr()) :
                toSentenceCase(mListCategories.get(position).getCategoryRus()));
        holder.textViewNumber.setText(String.valueOf(mCategoriesMap.get(mListCategories.get(position))));
        holder.textViewCategory.setTag(mListCategories.get(position).getCategoryEng());

        if (position == 0 && !mSelected) {
            holder.textViewCategory.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.textViewNumber.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.background_choose_category));
            mCurrentCategory = (String) holder.textViewCategory.getText();
            mLastView = holder.root;
            mListener.categoryChanged(mListCategories.get(position).getCategoryEng(), mCurrentCategory);
        }

        if (mCurrentCategory != null && mCurrentCategory.equals(holder.textViewCategory.getText())) {
            holder.textViewCategory.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.textViewNumber.setTextColor(mContext.getResources().getColor(android.R.color.white));
            holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.background_choose_category));
        } else {
            holder.textViewCategory.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
            holder.textViewNumber.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
            holder.root.setBackground(null);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastView != null && mLastView != v) {
                    TextView textViewCat = mLastView.findViewById(R.id.textViewCategory);
                    TextView textViewNum = mLastView.findViewById(R.id.textViewNumber);
                    textViewCat.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
                    textViewNum.setTextColor(mContext.getResources().getColor(R.color.colorIcons));
                    mLastView.setBackground(null);
                }
                holder.textViewCategory.setTextColor(mContext.getResources().getColor(android.R.color.white));
                holder.textViewNumber.setTextColor(mContext.getResources().getColor(android.R.color.white));
                holder.root.setBackground(mContext.getResources().getDrawable(R.drawable.background_choose_category));
                mCurrentCategory = holder.textViewCategory.getText().toString();
                mListener.categoryChanged(mListCategories.get(position).getCategoryEng(), mCurrentCategory);
                mLastView = v;
                mSelected = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewCategory;
        private TextView textViewNumber;
        private ViewGroup root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            root = itemView.findViewById(R.id.rootConstraint);
        }
    }

    public interface ChooseCategoryAdapterListener {
        void categoryChanged(String category, String originCategory);
    }

    public static String toSentenceCase(String string) {
        StringBuilder sb = new StringBuilder(string);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
