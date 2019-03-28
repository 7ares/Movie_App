package com.example.hares.movies.SelectedListMovies;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hares.movies.MoviesHomePage.MoviesActivity;
import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieList;
import com.example.hares.movies.Retrofit.CategoryMovieResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularGenresList extends AppCompatActivity {//implements LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

    public static final String TAG = "PopularGenresList";
    public static final String GENRES_ID_KEY = "genresIdKey";
    public static final String SENDER_NAME = "senderClass";

    private ListView mGenresList;
    private ArrayList<String> mGenresName = new ArrayList<>();
    private ArrayList<String> mGenresID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres_list);


        mGenresList = findViewById(R.id.popular_genres_movie);
        mGenresList.setOnItemClickListener((adapterView, view, i, l) -> {
            Class destination = CategorizedList.class;
            Intent message = new Intent(PopularGenresList.this, destination);

            message.putExtra(GENRES_ID_KEY, mGenresID.get(i));
            message.putExtra(SENDER_NAME, PopularGenresList.TAG);

            startActivity(message);
        });

        if (MoviesActivity.checkNetwork(this)) {
            Call<CategoryMovieList> GENRES_MOVIE_LIST = MoviesActivity.movieAPI.getGenresList();
            GENRES_MOVIE_LIST.clone().enqueue(new Callback<CategoryMovieList>() {
                @Override
                public void onResponse(@NonNull Call<CategoryMovieList> call, @NonNull Response<CategoryMovieList> response) {
                    CategoryMovieList genresList = response.body();
                    ArrayList<CategoryMovieResult> genresDetail = genresList != null ? genresList.getGenres() : null;
                    for (int i = 0; i < (genresDetail != null ? genresDetail.size() : 0); i++) {
                        mGenresID.add(genresDetail.get(i).getId());
                        mGenresName.add(genresDetail.get(i).getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PopularGenresList.this, android.R.layout.simple_list_item_1, mGenresName);
                    mGenresList.setAdapter(adapter);
                }

                @Override
                public void onFailure(@NonNull Call<CategoryMovieList> call, @NonNull Throwable t) {

                }
            });
        }}}