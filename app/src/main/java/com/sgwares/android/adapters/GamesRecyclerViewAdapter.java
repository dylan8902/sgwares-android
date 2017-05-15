package com.sgwares.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgwares.android.R;
import com.sgwares.android.fragments.GamesFragment.OnListFragmentInteractionListener;
import com.sgwares.android.models.Game;

import java.util.List;

public class GamesRecyclerViewAdapter extends RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder> {

    private final List<Game> mGames;
    private final OnListFragmentInteractionListener mListener;

    public GamesRecyclerViewAdapter(List<Game> games, OnListFragmentInteractionListener listener) {
        mGames = games;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_games, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mGame = mGames.get(position);
        holder.mKeyView.setText(holder.mGame.getKey());
        holder.mDateTimeView.setText(holder.mGame.getDateTimeCreatedAt());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mGame);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKeyView;
        public final TextView mDateTimeView;
        public Game mGame;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mKeyView = (TextView) view.findViewById(R.id.game_key);
            mDateTimeView = (TextView) view.findViewById(R.id.game_date_time);
        }

        @Override
        public String toString() {
            return mGame.toString();
        }
    }
}
