package com.example.hares.movies.MoviesHomePage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hares.movies.R;
import com.example.hares.movies.Retrofit.CategoryMovieResult;
import com.example.hares.movies.Retrofit.MovieDetail;
import com.example.hares.movies.SelectedListMovies.CategorizedList;
import com.example.hares.movies.SelectedListMovies.PopularGenresList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hares.movies.MoviesHomePage.DetailInfoAdapter.LIST_NAME;
import static com.example.hares.movies.MoviesHomePage.MoviesActivity.*;


public class MainInfoAdapter extends RecyclerView.Adapter<MainInfoAdapter.ViewHolder> {
    private final String TAG = "MainInfoAdapter";

    // array that have the data and will fill the view from it
    private ArrayList<MovieDetail> mTitle = new ArrayList<>();
    private Context mContext;
    private ArrayList<ArrayList<CategoryMovieResult>> mDataLists;

    public MainInfoAdapter(@NonNull Context context, ArrayList<ArrayList<CategoryMovieResult>> lists) {
        mContext = context;
        mDataLists = lists;
        // fill array with title name
        createTitle();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layoutForListItem = R.layout.movies_items;
        View view = inflater.inflate(layoutForListItem, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MainInfoAdapter.ViewHolder holder, final int position) {

        holder.mCategoryTitles.setText(mTitle.get(position).getTitle());

        if (position == 5)
            holder.mAdapter.setData(mDataLists.get(holder.getAdapterPosition()), LIST_NAME);

        else holder.mAdapter.setData(mDataLists.get(position), "");

        holder.mMoviesListRecyclerView.setAdapter(holder.mAdapter);
        holder.mShowAllItemButton.setOnClickListener(view -> {
            Class destination;
            Intent intent;
            if (position == 5) {
                destination = PopularGenresList.class;
                intent = new Intent(mContext, destination);
            } else {
                destination = CategorizedList.class;
                intent = new Intent(mContext, destination);
                intent.putExtra("NumberOfList", position);
            }
            if (checkNetwork(mContext)) mContext.startActivity(intent);
            else Toast.makeText(mContext, R.string.ToastErrorMessage, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    // use ViewHolder Pattern form smooth scrolling list
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_title)
        TextView mCategoryTitles;
        @BindView(R.id.show_all_item)
        Button mShowAllItemButton;
        @BindView(R.id.movie_list)
        RecyclerView mMoviesListRecyclerView;
        private DetailInfoAdapter mAdapter;

        //  catch of children views of list items
        private ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

            mAdapter = new DetailInfoAdapter(mContext);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            mMoviesListRecyclerView.setLayoutManager(layoutManager);
            mMoviesListRecyclerView.setHasFixedSize(true);


        }
    }

    private void createTitle() {
        String[] titleName = mContext.getResources().getStringArray(R.array.category_title);
        for (String TitleName : titleName) mTitle.add(new MovieDetail(TitleName));
    }

}




