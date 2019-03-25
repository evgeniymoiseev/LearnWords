package com.evgeniy.moiseev.learnwords;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.evgeniy.moiseev.learnwords.adapters.FavoritesAdapter;
import com.evgeniy.moiseev.learnwords.adapters.IrregularVerbsAdapter;
import com.evgeniy.moiseev.learnwords.adapters.WordAdapter;
import com.evgeniy.moiseev.learnwords.data.IrregularVerb;
import com.evgeniy.moiseev.learnwords.data.Word;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION = "section";

    private RecyclerView mRecyclerView;

    private FavoritesAdapter mFavoritesAdapter;
    private WordAdapter mWordAdapter;
    private IrregularVerbsAdapter mIrregularVerbsAdapter;

    private View rootView;
    private DictionariesActivity mContext;
    private DictionaryViewModel mDictionaryViewModel;
    private MenuItem mSearchItem;

    private int mSection;

    Observer<List<Word>> mObserverFavorites = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            mFavoritesAdapter.setWordItems(mDictionaryViewModel.getNoRepeatableWords(words, "all"));
        }
    };
    Observer<List<Word>> mObserverAllWords = new Observer<List<Word>>() {
        @Override
        public void onChanged(List<Word> words) {
            String category = mContext.getPreferences(Context.MODE_PRIVATE).getString(DictionariesActivity.PREFERENCE_SELECTION, "all");
            mWordAdapter.setFullList(mDictionaryViewModel.getNoRepeatableWords(words, category));
            if (mSearchItem != null && mSearchItem.isActionViewExpanded()) {
                String pattern = ((SearchView) mSearchItem.getActionView()).getQuery().toString().toLowerCase().trim();
                mWordAdapter.setWordItems(mDictionaryViewModel.getFilterableWords(words, pattern, category));
            } else {
                mWordAdapter.setWordItems(mDictionaryViewModel.getNoRepeatableWords(words, category));
            }
        }
    };
    Observer<List<IrregularVerb>> mObserverIrregularVerbs = new Observer<List<IrregularVerb>>() {
        @Override
        public void onChanged(List<IrregularVerb> irregularVerbs) {
            mIrregularVerbsAdapter.setIrregularItems(irregularVerbs);
        }
    };

    public static PlaceholderFragment newInstance(int section) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION, section);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        rootView = view;
        mRecyclerView = rootView.findViewById(R.id.recycler_view_words);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = (DictionariesActivity) getActivity();

        mFavoritesAdapter = new FavoritesAdapter(mContext);
        mWordAdapter = new WordAdapter(mContext, rootView.findViewById(R.id.progressBar), rootView.findViewById(R.id.textLoading));
        mIrregularVerbsAdapter = new IrregularVerbsAdapter(mContext);

        mFavoritesAdapter.setOnFavoriteCardListener(mContext.getOnFavoriteCardListener());
        mWordAdapter.setOnCardViewActionListener(mContext.getOnCardViewActionListener());
        mIrregularVerbsAdapter.setOnIrregularCardListener(mContext.getOnIrregularCardListener());

        Bundle args = getArguments();
        switch (args.getInt(ARG_SECTION)) {
            case DictionariesActivity.SECTION_FAVORITE:
                mRecyclerView.setAdapter(mFavoritesAdapter);
                rootView.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.textLoading).setVisibility(View.INVISIBLE);
                mSection = DictionariesActivity.SECTION_FAVORITE;
                break;
            case DictionariesActivity.SECTION_VOCABULARY:
                mRecyclerView.setAdapter(mWordAdapter);
                mSection = DictionariesActivity.SECTION_VOCABULARY;
                break;
            case DictionariesActivity.SECTION_IRREGULAR_VERBS:
                mRecyclerView.setAdapter(mIrregularVerbsAdapter);
                rootView.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.textLoading).setVisibility(View.INVISIBLE);
                mSection = DictionariesActivity.SECTION_IRREGULAR_VERBS;
                break;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mDictionaryViewModel = ViewModelProviders.of(getActivity()).get(DictionaryViewModel.class);

        mDictionaryViewModel.getAllWords().observe(this, mObserverAllWords);
        mDictionaryViewModel.getFavorites().observe(this, mObserverFavorites);
        mDictionaryViewModel.getAllIrregularVerbs().observe(this, mObserverIrregularVerbs);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        if (mSection == DictionariesActivity.SECTION_VOCABULARY) {
            menu.findItem(R.id.action_search).setVisible(true);
        } else if (mSection == DictionariesActivity.SECTION_IRREGULAR_VERBS || mSection == DictionariesActivity.SECTION_FAVORITE) {
            menu.findItem(R.id.action_search).setVisible(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mWordAdapter != null) {
            mSearchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) mSearchItem.getActionView();
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (mSection == DictionariesActivity.SECTION_VOCABULARY) {
                        mWordAdapter.getFilter().filter(newText);
                    }
                    return false;
                }
            });
        }
    }

        public List getList() {
        if (mSection == DictionariesActivity.SECTION_VOCABULARY) {
            return mWordAdapter.getList();
        } else if (mSection == DictionariesActivity.SECTION_IRREGULAR_VERBS)
            return mIrregularVerbsAdapter.getList();
        else if (mSection == DictionariesActivity.SECTION_FAVORITE)
            return mFavoritesAdapter.getList();
        return null;
    }
}


