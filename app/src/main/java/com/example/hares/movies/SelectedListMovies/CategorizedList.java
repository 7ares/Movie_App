package com.example.hares.movies.SelectedListMovies;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hares.movies.MoviesHomePage.DetailInfoAdapter;
import com.example.hares.movies.MoviesHomePage.MoviesActivity;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.InitializingRetrofit;
import com.example.hares.movies.Retrofit.MovieAPI;
import com.example.hares.movies.Retrofit.CategoryMovieList;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hares.movies.MoviesHomePage.MoviesActivity.*;
import static com.example.hares.movies.MoviesHomePage.MoviesActivity.movieAPI;

public class CategorizedList extends AppCompatActivity {
    public final static String TAG = "CategorizedList";

    private boolean isSearchOn = false;

    private int mLoaderId;
    private String mSort_By_Value;

    public CategorizedListAdapter mAdapter;
    private DetailInfoAdapter mFavoriteListAdapter;


    @BindView(R.id.categorized_list)
    RecyclerView mCategorizedList;
    @BindView(R.id.error_message)
    TextView mErrorMessageTextView;
    @BindView(R.id.connection_error_image)
    ImageView mErrorImageView;
    @BindView(R.id.loadingData)
    ProgressBar mLoadingProgressBar;


    public CategorizedList() {
    }


    public int getScreenOrientation(Context context){
        final int screenOrientation = ((WindowManager)  context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (screenOrientation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 1;
            case Surface.ROTATION_180:
                return 0;
            default:
                return 1;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorized_list);
        ButterKnife.bind(this);

        RelativeLayout view = findViewById(R.id.tablet_layout);

        // set Grid layout column depending on screen orientation
        GridLayoutManager layoutManager;
        if (view == null && getScreenOrientation(this) == 0)
            layoutManager = new GridLayoutManager(this, 2);
        else if (getScreenOrientation(this) == 1  && view != null)
            layoutManager = new GridLayoutManager(this, 5);
        else layoutManager = new GridLayoutManager(this, 3);

        // set up RecyclerView
        mCategorizedList.setLayoutManager(layoutManager);
        mCategorizedList.setHasFixedSize(true);


        String favoriteList = getIntent().getStringExtra(MoviesActivity.TAG);
        String senderClass = getIntent().getStringExtra(PopularGenresList.SENDER_NAME);

        if (favoriteList != null) {
            mLoaderId = 100;

        } else if (senderClass != null && senderClass.length() > 0) {
            String mGenresId = getIntent().getStringExtra(PopularGenresList.GENRES_ID_KEY);
            mLoaderId = Integer.valueOf(mGenresId);
        } else mLoaderId = getIntent().getIntExtra("NumberOfList", -1);

        if (mLoaderId != 100 && MoviesActivity.checkNetwork(this)) {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            showAllTheList(mLoaderId);
        } else if (mLoaderId != 100 && !MoviesActivity.checkNetwork(this)) displayErrorMessage();
        handleIntent(getIntent());
    }

    private void callMethods(Call<CategoryMovieList> call) {
        call.clone().enqueue(new Callback<CategoryMovieList>() {
            @Override
            public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {
                CategoryMovieList resource = response.body();

                mLoadingProgressBar.setVisibility(View.GONE);
                mAdapter = new CategorizedListAdapter(CategorizedList.this, resource != null ? resource.getCategoryMovieResults() : null);
                mCategorizedList.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    private void displayErrorMessage() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mErrorImageView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }


    private void showAllTheList(int i) {
        switch (i) {
            case 0:
                callMethods(LATEST_MOVIE);
                Objects.requireNonNull(getSupportActionBar()).hide();
                break;
            case 1:
                callMethods(UPCOMING_MOVIE_LIST);
                Objects.requireNonNull(getSupportActionBar()).hide();
                break;
            case 2:
                callMethods(MOVIES_IN_THEATER);
                Objects.requireNonNull(getSupportActionBar()).hide();
                break;
            case 3:
                callMethods(MOST_POPULAR_MOVIES);
                Objects.requireNonNull(getSupportActionBar()).hide();
                break;
            case 4:
                callMethods(TOP_RATED_MOVIES);
                Objects.requireNonNull(getSupportActionBar()).hide();
                break;
            default:
                Call<CategoryMovieList> getMoviesOfThatGenresID = movieAPI.getMovieByGenres(i + "", "");
                callMethods(getMoviesOfThatGenresID);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_option, menu);
        Spinner mSortList = (Spinner) menu.findItem(R.id.sort_list).getActionView();
        final String[] sortValues = getResources().getStringArray(R.array.sort_by_value);

        if (!isSearchOn) {

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by_title, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSortList.setAdapter(adapter);

            mSortList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mSort_By_Value = sortValues[i];

                    if (MoviesActivity.checkNetwork(CategorizedList.this)) {
                        mLoadingProgressBar.setVisibility(View.VISIBLE);
                        mErrorImageView.setVisibility(View.GONE);
                        mErrorMessageTextView.setVisibility(View.GONE);
                        mCategorizedList.setVisibility(View.GONE);

                        MovieAPI movieAPI = InitializingRetrofit.getClient().create(MovieAPI.class);
                        Call<CategoryMovieList> getMoviesOfThatGenresID = movieAPI.getMovieByGenres(mLoaderId + "", mSort_By_Value);

                        switch (mLoaderId) {
                            case 100:
                                mLoadingProgressBar.setVisibility(View.GONE);
                                mFavoriteListAdapter = new DetailInfoAdapter(CategorizedList.this);
                                mFavoriteListAdapter.setData(mFavoriteListDetail, getString(R.string.FavoriteList_sender));
                                mCategorizedList.setAdapter(mFavoriteListAdapter);
                                mCategorizedList.setVisibility(View.VISIBLE);
                                break;
                            default:
                                orderTheList(getMoviesOfThatGenresID);
                                break;
                        }
                    } else if (mLoaderId == 100) {
                        mLoadingProgressBar.setVisibility(View.GONE);
                        mFavoriteListAdapter = new DetailInfoAdapter(CategorizedList.this);
                        mFavoriteListAdapter.setData(mFavoriteListDetail, getString(R.string.FavoriteList_sender));
                        mCategorizedList.setAdapter(mFavoriteListAdapter);
                        mCategorizedList.setVisibility(View.VISIBLE);
                    } else {
                        mCategorizedList.setVisibility(View.GONE);
                        displayErrorMessage();
                    }
                }

                private void orderTheList(Call<CategoryMovieList> sortLatestMovieList) {
                    sortLatestMovieList.clone().enqueue(new Callback<CategoryMovieList>() {
                        @Override
                        public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {

                            CategoryMovieList resource = response.body();
                            mCategorizedList.setVisibility(View.VISIBLE);
                            mAdapter = new CategorizedListAdapter(CategorizedList.this, resource != null ? resource.getCategoryMovieResults() : null);
                            mCategorizedList.setAdapter(mAdapter);
                            mLoadingProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String query = s.replaceAll(" ", "+");

                Intent intent = new Intent(CategorizedList.this, CategorizedList.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                mSearchView.setIconified(true);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(() -> {
            mSortList.setVisibility(View.VISIBLE);
            return false;
        });
        mSearchView.setOnSearchClickListener(v -> mSortList.setVisibility(View.GONE));

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            isSearchOn = true;
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

    private void showResults(String query) {
        MovieAPI movieAPI = MoviesActivity.movieAPI;
        Call<CategoryMovieList> doSearch = movieAPI.doSearch(query);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search Result");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        doSearch.enqueue(new Callback<CategoryMovieList>() {
            @Override
            public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {
                CategoryMovieList searchList = response.body();

                CategorizedListAdapter adapter = new CategorizedListAdapter(CategorizedList.this, searchList != null ? searchList.getCategoryMovieResults() : null);
                mCategorizedList.setAdapter(adapter);
                mCategorizedList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {
            }
        });
    }

}

