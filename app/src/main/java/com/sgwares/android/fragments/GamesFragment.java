package com.sgwares.android.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sgwares.android.R;
import com.sgwares.android.adapters.GamesRecyclerViewAdapter;
import com.sgwares.android.models.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {

    private static final String TAG = "GamesFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Game> mGames = new ArrayList<>();
    private List<String> mGamesKeys = new ArrayList<>();
    private GamesRecyclerViewAdapter mRecyclerViewAdapter;
    private Query gamesRef;
    private ChildEventListener childEventListener;

    public GamesFragment() {
    }

    public static GamesFragment newInstance(int columnCount) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mRecyclerViewAdapter = new GamesRecyclerViewAdapter(mGames, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("games").limitToLast(10);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Game game = dataSnapshot.getValue(Game.class);
                game.setKey(dataSnapshot.getKey());
                mGames.add(game);
                mGamesKeys.add(dataSnapshot.getKey());
                mRecyclerViewAdapter.notifyItemInserted(mGames.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                Game game = dataSnapshot.getValue(Game.class);
                game.setKey(dataSnapshot.getKey());
                int gameIndex = mGamesKeys.indexOf(dataSnapshot.getKey());
                if (gameIndex > -1) {
                    mGames.set(gameIndex, game);
                    mRecyclerViewAdapter.notifyItemChanged(gameIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                int gameIndex = mGamesKeys.indexOf(dataSnapshot.getKey());
                if (gameIndex > -1) {
                    mGamesKeys.remove(gameIndex);
                    mGames.remove(gameIndex);
                    mRecyclerViewAdapter.notifyItemRemoved(gameIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        };
        gamesRef.addChildEventListener(childEventListener);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (childEventListener != null) {
            gamesRef.removeEventListener(childEventListener);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Game game);
    }

}
