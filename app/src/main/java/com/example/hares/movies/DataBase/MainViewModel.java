package com.example.hares.movies.DataBase;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<FavoriteMovieDetail>> favoriteList ;

    public LiveData<List<FavoriteMovieDetail>> getFavoriteList() {
        return favoriteList;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        DataBaseHelper mDB = DataBaseHelper.getInstance(this.getApplication());
        favoriteList = mDB.myDAO().readListOfMovie();
        }
}
