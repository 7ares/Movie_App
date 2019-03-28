package com.example.hares.movies.MoviesHomePage;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hares.movies.DataBase.MainViewModel;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.InitializingRetrofit;
import com.example.hares.movies.Retrofit.MovieAPI;
import com.example.hares.movies.Retrofit.CategoryMovieResult;
import com.example.hares.movies.Retrofit.CategoryMovieList;
import com.example.hares.movies.SelectedListMovies.CategorizedList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesActivity extends AppCompatActivity {
    public static final String TAG = "MoviesActivity";

    // get url paths
    public static final MovieAPI movieAPI = InitializingRetrofit.getClient().create(MovieAPI.class);

    public static final Call<CategoryMovieList> LATEST_MOVIE = movieAPI.getLatestMovie();
    public static final Call<CategoryMovieList> UPCOMING_MOVIE_LIST = movieAPI.getUpcomingMovieList();
    public static final Call<CategoryMovieList> MOVIES_IN_THEATER = movieAPI.getMoviesInTheater(startDate(), endDate());
    public static final Call<CategoryMovieList> MOST_POPULAR_MOVIES = movieAPI.getMostPopularMovies();
    public static final Call<CategoryMovieList> TOP_RATED_MOVIES = movieAPI.getTopRatedMovies();
    public static final Call<CategoryMovieList> GENRES_CALL = movieAPI.getGenresList();

    public static final ArrayList<String> posterPath = new ArrayList<>();
    public static final ArrayList<Integer> FAVORITE_LIST_IDS = new ArrayList<>();

    private ArrayList<String> mGenresId = new ArrayList<>();
    private ArrayList<ArrayList<CategoryMovieResult>> lists = new ArrayList<>();
    private MainInfoAdapter mAdapter;

    private int mNumberOfList = 0;
    private int mGenresIdIndex;

    @BindView(R.id.error_message)
    TextView mErrorMessageTextView;
    @BindView(R.id.loadingData)
    ProgressBar mLoadingProgressBar;
    @BindView(R.id.connection_error_image)
    ImageView mErrorImageView;
    @BindView(R.id.refresh)
    Button mTryAgainButton;
    @BindView(R.id.categoryHeadLineList)
    RecyclerView mRecyclerView;


    public static ArrayList<CategoryMovieResult> mFavoriteListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);

        // setup recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setFocusableInTouchMode(true);

        // when we launch the App at first time we read favorite list table
        // to know the movies that user saved it and can use it offline
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavoriteList().observe(this, favoriteMovieDetails -> {
            CategoryMovieResult result;

            // every time we need to clear List Ids to prevent unnecessary redundant
            FAVORITE_LIST_IDS.clear();
            mFavoriteListDetail = new ArrayList<>();

            if (favoriteMovieDetails != null) {
                for (int i = 0; i < favoriteMovieDetails.size(); i++) {
                    result = new CategoryMovieResult();

                    result.setId(favoriteMovieDetails.get(i).M_ID + "");
                    result.setTitle(favoriteMovieDetails.get(i).MOVIE_NAME);
                    result.setVote_average(favoriteMovieDetails.get(i).MOVIE_VOTE_AVERAGE);
                    result.setImage(getImage(favoriteMovieDetails.get(i).MOVIE_POSTER));
                    FAVORITE_LIST_IDS.add(favoriteMovieDetails.get(i).M_ID);
                    mFavoriteListDetail.add(result);
                }
            }
        });

        // check network connectivity before start fetch data from network
        if (checkNetwork(this)) {
            // if connectivity is OK so display ProgressBar as indication that data being downloaded
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            callMethods(LATEST_MOVIE);
        } else {
            // else display message for user to make him know that there's no connection
            displayErrorMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.open_favorite_list) {
            Intent sendMessage = new Intent(this, CategorizedList.class);
            sendMessage.putExtra(TAG, "open favorite list");
            Toast.makeText(this, mFavoriteListDetail.size() + "", Toast.LENGTH_LONG).show();
            startActivity(sendMessage);
        }
        return true;
    }

    // refresh activity to try to load data if network restored again
    @OnClick(R.id.refresh)
    void refresh() {
        finish();
        startActivity(getIntent());
    }

    // fetching data from server
    private void callMethods(Call<CategoryMovieList> call) {

        mNumberOfList++;
        call.clone().enqueue(new Callback<CategoryMovieList>() {

            @Override
            public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {
                CategoryMovieList resource = response.body();

                if (resource != null) {
                    if (mNumberOfList != 6) {
                        lists.add(resource.getCategoryMovieResults());
                    } else {
                        lists.add(resource.getGenres());
                        for (int i = 0; i < 10; i++)
                            mGenresId.add(resource.getGenres().get(i).getId());
                        getPosterPathForGenresList(mGenresId, mGenresIdIndex);
                        mLoadingProgressBar.setVisibility(View.GONE);
                        mAdapter = new MainInfoAdapter(MoviesActivity.this, lists);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
                switch (mNumberOfList) {
                    case 1:
                        callMethods(UPCOMING_MOVIE_LIST);
                        break;
                    case 2:
                        callMethods(MOVIES_IN_THEATER);
                        break;
                    case 3:
                        callMethods(MOST_POPULAR_MOVIES);
                        break;
                    case 4:
                        callMethods(TOP_RATED_MOVIES);
                        break;
                    case 5:
                        callMethods(GENRES_CALL);
                        break;
                }
            }

            // represent each genres of movie by image of movie that has same type
            // get pics of specified movie that belong to genres name
            private void getPosterPathForGenresList(ArrayList<String> id, int i) {
                Call<CategoryMovieList> GENRES_MOVIE_LIST = movieAPI.getMovieByGenres(id.get(i), "");
                Log.i("trc", "kkkk");
                GENRES_MOVIE_LIST.clone().enqueue(new Callback<CategoryMovieList>() {
                    @Override
                    public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {
                        Log.i("trc", response.toString());
                        CategoryMovieList categoryMovieList = response.body();
                        if (categoryMovieList != null) {
                            posterPath.add(categoryMovieList.getCategoryMovieResults().get(mGenresIdIndex + 5).getPoster_path());
                        }

                        mGenresIdIndex++;
                        if (mGenresIdIndex < 10)
                            getPosterPathForGenresList(mGenresId, mGenresIdIndex);
                    }

                    @Override
                    public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {
                Log.e(TAG, "call failure : " + t.getMessage());
                call.cancel();
            }
        });

    }

    // handle error message
    public void displayErrorMessage() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.VISIBLE);
        mTryAgainButton.setVisibility(View.VISIBLE);
    }

    // if we need movies at specified Date so this method help to define start and end Date that we want to  see result in
    public static String startDate() {
        SimpleDateFormat yearPattern = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -15);
        Date date = calendar.getTime();
        return yearPattern.format(date);

    }

    public static String endDate() {
        SimpleDateFormat yearPattern = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return yearPattern.format(date);

    }

    // check Network connectivity
    public static boolean checkNetwork(Context context) {
        boolean isNetworkConnected = false;

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connManager != null) {
            networkInfo = connManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) isNetworkConnected = true;

        return isNetworkConnected;
    }

    // get image from byte []
    public static Bitmap getImage(byte[] pic) {
        Bitmap bitmap = null;
        if (pic != null)
            bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);

        return bitmap;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}




