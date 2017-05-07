package com.sgwares.android;

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
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private static final String TAG = "LeaderboardFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<User> mUsers = new ArrayList<>();
    private List<String> mUserKeys = new ArrayList<>();
    private LeaderboardRecyclerViewAdapter mRecyclerViewAdapter;
    private Query leaderboardRef;
    private ChildEventListener childEventListener;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance(int columnCount) {
        LeaderboardFragment fragment = new LeaderboardFragment();
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
            mRecyclerViewAdapter = new LeaderboardRecyclerViewAdapter(mUsers, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        leaderboardRef = database.getReference("users").orderByChild("points").limitToLast(10);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                mUsers.add(user);
                mUserKeys.add(dataSnapshot.getKey());
                mRecyclerViewAdapter.notifyItemInserted(mUsers.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                int userIndex = mUserKeys.indexOf(dataSnapshot.getKey());
                if (userIndex > -1) {
                    mUsers.set(userIndex, user);
                    mRecyclerViewAdapter.notifyItemChanged(userIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                int userIndex = mUserKeys.indexOf(dataSnapshot.getKey());
                if (userIndex > -1) {
                    mUserKeys.remove(userIndex);
                    mUsers.remove(userIndex);
                    mRecyclerViewAdapter.notifyItemRemoved(userIndex);
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
        leaderboardRef.addChildEventListener(childEventListener);
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
            leaderboardRef.removeEventListener(childEventListener);
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User score);
    }

}
