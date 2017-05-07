package com.sgwares.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgwares.android.LeaderboardFragment.OnListFragmentInteractionListener;
import com.sgwares.android.models.LeaderboardScore;

import java.util.List;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {

    private final List<LeaderboardScore> mScores;
    private final OnListFragmentInteractionListener mListener;

    public LeaderboardRecyclerViewAdapter(List<LeaderboardScore> scores, OnListFragmentInteractionListener listener) {
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
        String pos = String.valueOf(mScores.get(position).getPosition());
        String points = String.valueOf(mScores.get(position).getPoints());
        holder.mPositionView.setText(pos);
        holder.mNameView.setText(mScores.get(position).getName());
        holder.mPointsView.setText(points);

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
        public final TextView mPositionView;
        public final TextView mNameView;
        public final TextView mPointsView;
        public LeaderboardScore mScore;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPositionView = (TextView) view.findViewById(R.id.leaderboard_position);
            mNameView = (TextView) view.findViewById(R.id.leaderboard_name);
            mPointsView = (TextView) view.findViewById(R.id.leaderboard_points);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
