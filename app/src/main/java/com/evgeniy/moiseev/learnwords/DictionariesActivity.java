package com.evgeniy.moiseev.learnwords;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.evgeniy.moiseev.learnwords.adapters.FavoritesAdapter;
import com.evgeniy.moiseev.learnwords.adapters.IrregularVerbsAdapter;
import com.evgeniy.moiseev.learnwords.adapters.SectionsPagerAdapter;
import com.evgeniy.moiseev.learnwords.adapters.WordAdapter;
import com.evgeniy.moiseev.learnwords.models.DictionaryViewModel;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

public class DictionariesActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public static final String LOCALE_UKR = "uk";
    public static final String LOCALE_RUS = "ru";
    public static final int SECTION_FAVORITE = 0;
    public static final int SECTION_VOCABULARY = 1;
    public static final int SECTION_IRREGULAR_VERBS = 2;
    public static final String PREFERENCE_SELECTION = "preference selection";
    public static final int MENU_SELECTION_START_ID = 1000;

    public static final int FLAG_EDIT = 1;
    public static final int FLAG_CREATE = 2;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private BottomAppBar mBottomAppBar;
    private Locale mPrimaryLocale;
    private String mLocale;
    private SharedPreferences mPreferences;
    private Categories mCategories;
    private TextToSpeech mTextToSpeech;
    private AnimationUtils animation;
    private int mCurrentSection;
    private FloatingActionButton fab;

    private FavoritesAdapter.OnFavoriteCardListener mOnFavoriteCardListener = new FavoritesAdapter.OnFavoriteCardListener() {
        @Override
        public void onSpeechImageViewClicked(String textToSpeech) {
            mOnCardViewActionListener.onSpeechImageViewClicked(textToSpeech);
        }

        @Override
        public void onCardViewClicked(OnOpeningAdapter adapter, View clickedView) {
            mOnCardViewActionListener.onCardViewClicked(adapter, clickedView, false);
        }

        @Override
        public void onDeleteClicked(final String word) {
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).
                                    updateFavorite(word, 0);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            new AlertDialog.Builder(DictionariesActivity.this)
                    .setTitle(R.string.string_remove_from_favorites)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_cancel, dialogListener)
                    .setPositiveButton(R.string.button_ok, dialogListener)
                    .create().show();
        }
    };
    private WordAdapter.OnCardViewActionListener mOnCardViewActionListener = new WordAdapter.OnCardViewActionListener() {

        @Override
        public void onSpeechImageViewClicked(String textToSpeech) {
            mTextToSpeech.speak(textToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        @Override
        public void onCardViewClicked(OnOpeningAdapter adapter, View clickedView, boolean enabled) {
            if (animation.isAnimatedNow) return;

            if (adapter.getOpenedView() == null) {
                animation.openRecyclerViewItem(clickedView, 200, enabled);
                adapter.setOpenedView(clickedView);
            } else if (adapter.getOpenedView() == clickedView) {
                animation.closeRecyclerViewItem(clickedView, 200, enabled);
                adapter.setOpenedView(null);
            } else if (adapter.getOpenedView() != clickedView) {
                adapter.getOpenedView().setTransitionName(null);
                animation.forceCloseRecyclerViewItem(adapter.getOpenedView(), enabled);
                animation.openRecyclerViewItem(clickedView, 200, enabled);
                adapter.setOpenedView(clickedView);
            }
        }

        @Override
        public void onDeleteClicked(final String word) {
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).delete(word);
                            Snackbar.make(DictionariesActivity.this.findViewById(R.id.fab),
                                    DictionariesActivity.this.getString(R.string.word_deleted, word),
                                    Snackbar.LENGTH_LONG).show();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            new AlertDialog.Builder(DictionariesActivity.this)
                    .setTitle(DictionariesActivity.this.getString(R.string.confirm_delete, word))
                    .setNegativeButton(R.string.button_cancel, dialogListener)
                    .setPositiveButton(R.string.button_ok, dialogListener)
                    .create().show();
        }

        @Override
        public void onFavoriteClicked(final String word, final WordAdapter adapter, final View clickedView, final int isFavorite) {
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            animation.closeRecyclerViewItem(clickedView, 200, false);
                            adapter.setOpenedView(null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).
                                            updateFavorite(word, isFavorite);
                                }
                            }, 500);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            new AlertDialog.Builder(DictionariesActivity.this)
                    .setTitle(isFavorite == 1 ? R.string.string_add_to_favorites : R.string.string_remove_from_favorites)
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_cancel, dialogListener)
                    .setPositiveButton(R.string.button_ok, dialogListener)
                    .create().show();
        }

        @Override
        public void onDifficultyClicked(final String word, final int difficulty, TextView textView,
                                        final WordAdapter adapter, final View clickedView) {
            DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            animation.closeRecyclerViewItem(clickedView, 200, true);
                            adapter.setOpenedView(null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).
                                            updateDifficulty(word, difficulty);
                                }
                            }, 500);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.cancel();
                            break;
                    }
                }
            };
            new AlertDialog.Builder(DictionariesActivity.this)
                    .setTitle(DictionariesActivity.this.getString(R.string.string_change_difficulty, textView.getText().toString()))
                    .setCancelable(false)
                    .setNegativeButton(R.string.button_cancel, dialogListener)
                    .setPositiveButton(R.string.button_ok, dialogListener)
                    .create().show();
        }

        @Override
        public void onEditClicked(String word, String transcription, String translation, int difficulty, String category, int guessed, int notGuessed, int isFavorite) {
            Intent intent = new Intent(DictionariesActivity.this, WordDetailActivity.class);
            intent.putExtra(WordDetailActivity.EXTRA_WORD, word);
            intent.putExtra(WordDetailActivity.EXTRA_TRANSCRIPTION, transcription);
            intent.putExtra(WordDetailActivity.EXTRA_TRANSLATION, translation);
            intent.putExtra(WordDetailActivity.EXTRA_DIFFICULTY, difficulty);
            intent.putExtra(WordDetailActivity.EXTRA_CATEGORY, category);
            intent.putExtra(WordDetailActivity.EXTRA_GUESSED, guessed);
            intent.putExtra(WordDetailActivity.EXTRA_NOT_GUESSED, notGuessed);
            intent.putExtra(WordDetailActivity.EXTRA_IS_FAVORITE, isFavorite);
            intent.putExtra(WordDetailActivity.EXTRA_FLAG, FLAG_EDIT);
            startActivity(intent);
        }

    };
    private IrregularVerbsAdapter.OnIrregularCardListener mOnIrregularCardListener = new IrregularVerbsAdapter.OnIrregularCardListener() {
        @Override
        public void onWord1Clicked(String word1) {
            mTextToSpeech.speak(word1, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        @Override
        public void onWord2Clicked(String word2) {
            mTextToSpeech.speak(word2, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        @Override
        public void onWord3Clicked(String word3) {
            mTextToSpeech.speak(word3, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        @Override
        public void onCheckedClicked(String word1, boolean checked) {
            ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).updateTrainedIrregular(word1, checked ? 1 : 0);
        }
    };
    private Toolbar.OnMenuItemClickListener bottomAppBarMenuClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_add:
                    Intent intent = new Intent(DictionariesActivity.this, WordDetailActivity.class);
                    intent.putExtra(WordDetailActivity.EXTRA_FLAG, FLAG_CREATE);
                    startActivity(intent);
                    break;
                case R.id.menu_sort:
                    break;
                case R.id.menu_clear_favorites:
                    if (mCurrentSection == SECTION_FAVORITE) {
                        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewModelProviders.of(DictionariesActivity.this)
                                        .get(DictionaryViewModel.class).clearFavorites();
                            }
                        };
                        DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        AlertDialog alertDialog = new AlertDialog.Builder(DictionariesActivity.this)
                                .setCancelable(false)
                                .setMessage(R.string.string_clear_favorites)
                                .setPositiveButton(R.string.string_yes, positiveListener)
                                .setNegativeButton(R.string.button_cancel, negativeListener)
                                .create();
                        alertDialog.show();
                    } else if (mCurrentSection == SECTION_IRREGULAR_VERBS) {
                        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ViewModelProviders.of(DictionariesActivity.this).get(DictionaryViewModel.class).clearIrregulars();
                            }
                        };
                        DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        };
                        AlertDialog alertDialog = new AlertDialog.Builder(DictionariesActivity.this)
                                .setCancelable(false)
                                .setMessage(R.string.string_clear_verbs)
                                .setPositiveButton(R.string.string_yes, positiveListener)
                                .setNegativeButton(R.string.button_cancel, negativeListener)
                                .create();
                        alertDialog.show();
                    }
                    break;
                default:
                    String category = mCategories.getCategories().get(item.getItemId() - MENU_SELECTION_START_ID).getCategoryEng();
                    mPreferences.edit().putString(PREFERENCE_SELECTION, category).apply();
                    mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
                    mViewPager.setCurrentItem(SECTION_VOCABULARY);
                    if (mCategories.getCategories().get(item.getItemId() - MENU_SELECTION_START_ID).getCategoryEng().equals("all")) {
                        ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_VOCABULARY).setText(R.string.tab_text_vocabulary);
                        ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_VOCABULARY).setIcon(R.drawable.ic_vocabulary_24dp);
                    } else {
                        String title = mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                                mCategories.getCategories().get(item.getItemId() - MENU_SELECTION_START_ID).getCategoryUkr() :
                                mCategories.getCategories().get(item.getItemId() - MENU_SELECTION_START_ID).getCategoryRus();
                        StringBuilder sb = new StringBuilder(title);
                        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                        int drawableResId = mCategories.getCategories().get(item.getItemId() - MENU_SELECTION_START_ID).getDrawableResId();
                        ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_VOCABULARY).setText(sb.toString());
                        ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_VOCABULARY).setIcon(drawableResId);
                    }

                    ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_FAVORITE).setText(R.string.tab_text_favourite);
                    ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_FAVORITE).setIcon(R.drawable.ic_favorite_24dp);

                    ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_IRREGULAR_VERBS).setText(R.string.tab_text_irregular_verbs);
                    ((TabLayout) DictionariesActivity.this.findViewById(R.id.tabs)).getTabAt(SECTION_IRREGULAR_VERBS).setIcon(R.drawable.ic_irregular_24dp);
            }
            return true;
        }
    };
    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fab.setOnClickListener(null);
            Intent intent = null;
            switch (mCurrentSection) {
                case SECTION_FAVORITE:
                    intent = new Intent(DictionariesActivity.this, TrainingModeActivity.class);
                    break;
                case SECTION_VOCABULARY:
                    intent = new Intent(DictionariesActivity.this, TrainingModeActivity.class);
                    break;
                case SECTION_IRREGULAR_VERBS:
                    intent = new Intent(DictionariesActivity.this, IrregularVerbsModeActivity.class);
                    break;
            }
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionaries);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mPrimaryLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            mPrimaryLocale = getResources().getConfiguration().locale;
        }
        mLocale = mPrimaryLocale.getLanguage();

        mCategories = new Categories();
        mPreferences = getPreferences(MODE_PRIVATE);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mBottomAppBar = findViewById(R.id.bottom_appbar);
        mBottomAppBar.replaceMenu(R.menu.bottom_appbar_menu);
        inflateMenuSort(mBottomAppBar.getMenu().findItem(R.id.menu_sort).getSubMenu());
        mBottomAppBar.setOnMenuItemClickListener(bottomAppBarMenuClickListener);
        mBottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DictionariesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        animation = new AnimationUtils(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(SECTION_VOCABULARY);
        mCurrentSection = SECTION_VOCABULARY;

        mTextToSpeech = new TextToSpeech(this, this);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupVocabularyTab();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mCurrentSection = SECTION_FAVORITE;
                        mBottomAppBar.getMenu().findItem(R.id.menu_clear_favorites).setVisible(true);
                        mBottomAppBar.getMenu().findItem(R.id.menu_add).setVisible(false);
                        mBottomAppBar.getMenu().findItem(R.id.menu_sort).setVisible(false);
                        break;
                    case 1:
                        mCurrentSection = SECTION_VOCABULARY;
                        mBottomAppBar.getMenu().findItem(R.id.menu_clear_favorites).setVisible(false);
                        mBottomAppBar.getMenu().findItem(R.id.menu_add).setVisible(true);
                        mBottomAppBar.getMenu().findItem(R.id.menu_sort).setVisible(true);
                        break;
                    case 2:
                        mCurrentSection = SECTION_IRREGULAR_VERBS;
                        mBottomAppBar.getMenu().findItem(R.id.menu_clear_favorites).setVisible(true);
                        mBottomAppBar.getMenu().findItem(R.id.menu_add).setVisible(false);
                        mBottomAppBar.getMenu().findItem(R.id.menu_sort).setVisible(false);
                        break;
                }
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);
    }

    public String getLocale() {
        return mLocale;
    }

    private void setupVocabularyTab() {
        if (mPreferences.getString(PREFERENCE_SELECTION, "all").equals("all")) {
            mTabLayout.getTabAt(SECTION_VOCABULARY).setText(R.string.tab_text_vocabulary);
            mTabLayout.getTabAt(SECTION_VOCABULARY).setIcon(R.drawable.ic_vocabulary_24dp);
        } else {
            String title = mLocale.equals(DictionariesActivity.LOCALE_UKR) ?
                    mCategories.getCategoryEng(mPreferences.getString(PREFERENCE_SELECTION, "all")).getCategoryUkr() :
                    mCategories.getCategoryEng(mPreferences.getString(PREFERENCE_SELECTION, "all")).getCategoryRus();
            StringBuilder sb = new StringBuilder(title);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            int drawableResId = mCategories.getCategoryEng(mPreferences.getString(PREFERENCE_SELECTION, "all")).getDrawableResId();
            mTabLayout.getTabAt(SECTION_VOCABULARY).setText(sb.toString());
            mTabLayout.getTabAt(SECTION_VOCABULARY).setIcon(drawableResId);
        }

        mTabLayout.getTabAt(SECTION_FAVORITE).setText(R.string.tab_text_favourite);
        mTabLayout.getTabAt(SECTION_FAVORITE).setIcon(R.drawable.ic_favorite_24dp);

        mTabLayout.getTabAt(SECTION_IRREGULAR_VERBS).setText(R.string.tab_text_irregular_verbs);
        mTabLayout.getTabAt(SECTION_IRREGULAR_VERBS).setIcon(R.drawable.ic_irregular_24dp);
    }

    private void inflateMenuSort(Menu menuSort) {
        Categories categories = new Categories();
        for (int i = 0; i < categories.getCategories().size(); i++) {
            if (mLocale.equals(LOCALE_UKR))
                menuSort.add(Menu.NONE, MENU_SELECTION_START_ID + i, Menu.NONE, categories.getUkrCategories().get(i))
                        .setIcon(categories.getCategories().get(i).getDrawableResId());
            else
                menuSort.add(Menu.NONE, MENU_SELECTION_START_ID + i, Menu.NONE, categories.getRusCategories().get(i))
                        .setIcon(categories.getCategories().get(i).getDrawableResId());
        }
    }

    public WordAdapter.OnCardViewActionListener getOnCardViewActionListener() {
        return mOnCardViewActionListener;
    }

    public FavoritesAdapter.OnFavoriteCardListener getOnFavoriteCardListener() {
        return mOnFavoriteCardListener;
    }

    public IrregularVerbsAdapter.OnIrregularCardListener getOnIrregularCardListener() {
        return mOnIrregularCardListener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fab.setOnClickListener(fabListener);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTextToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(DictionariesActivity.this.findViewById(R.id.fab),
                        getString(R.string.string_not_supported_language),
                        Snackbar.LENGTH_LONG).show();
            }
        } else Log.e("YYY", "Initialization Failed!");
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

}
