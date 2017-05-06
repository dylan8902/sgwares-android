package com.sgwares.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgwares.android.LeaderboardFragment.OnListFragmentInteractionListener;
import com.sgwares.android.models.Leaderboard.Score;

import java.util.List;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {

    private final List<Score> mScores;
    private final OnListFragmentInteractionListener mListener;

    public LeaderboardRecyclerViewAdapter(List<Score> scores, OnListFragmentInteractionListener listener) {
        mScores = scores;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mScore = mScores.get(position);
        holder.mIdView.setText(mScores.get(position).id);
        holder.mContentView.setText(mScores.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mScore);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mScores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Score mScore;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
