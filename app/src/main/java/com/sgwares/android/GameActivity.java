package com.sgwares.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgwares.android.models.Game;
import com.sgwares.android.models.Move;
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGameRef;
    private DatabaseReference mMovesRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mParticipantsRef;
    private List<User> mPossibleParticipants = new ArrayList<>();;
    private Map<User, TextView> mParticipants = new LinkedHashMap<>();
    private ArrayAdapter mAdapter;
    private ChildEventListener mMovesListener;
    private ChildEventListener mPossibleParticipantListener;
    private ChildEventListener mParticipantsListener;
    private Game mGame;
    private GameSurface mGameSurface;
    private LinearLayout mScoreboard;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // TODO Check bundle to see if a game key is present

        setContentView(R.layout.activity_game_setup);

        // Get user and create game
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("users");
        mUsersRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                createGame();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mPossibleParticipants);
        final ListView listView = (ListView) findViewById(R.id.possible_participants);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO invite user to game
                addParticipant(mPossibleParticipants.get(position));
            }
        });

        mPossibleParticipantListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                if (!user.getKey().equals(auth.getCurrentUser().getUid())) {
                    Log.d(TAG, "New possible participant: " + user);
                    mPossibleParticipants.add(user);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                //TODO update participant picker
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                //TODO remove from participant picker
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
        mUsersRef.addChildEventListener(mPossibleParticipantListener);
    }

    /**
     * Create a new game and add current user as participant
     */
    private void createGame() {
        mGameRef = mDatabase.getReference("games").push();
        Log.d(TAG, "Created game key: " + mGameRef.getKey());
        mGame = new Game();
        List<User> initialParticipants = new ArrayList<>();
        initialParticipants.add(mUser);
        mGame.setParticipants(initialParticipants);
        mGame.setBackground("#bbbbbb");
        mGame.setKey(mGameRef.getKey());
        mGameRef.setValue(mGame);

        final Button startGame = (Button) findViewById(R.id.start);
        startGame.setVisibility(View.VISIBLE);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersRef.removeEventListener(mPossibleParticipantListener);
                startGame();
            }
        });
    }

    // TODO chnage to invite partipant
    private void addParticipant(User user) {
        Log.d(TAG, "addParticipant: " + user);
        mPossibleParticipants.remove(user);
        mAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.content_main), user.getName() + " added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * Start the game, show game surface and setup handlers
     */
    private void startGame() {
        mGameSurface = new GameSurface(getApplicationContext(), mGame, mUser);
        setContentView(R.layout.activity_game);
        mScoreboard = (LinearLayout) findViewById(R.id.scoreboard);
        RelativeLayout view = (RelativeLayout) findViewById(R.id.content_main);
        view.addView(mGameSurface, 0);
        setupMoveHandler();
        setupParticipantHandler();
    }

    /**
     * Listen for new moves and trigger redraw of the canvas
     */
    private void setupMoveHandler() {
        mMovesRef = mDatabase.getReference("moves").child(mGameRef.getKey());
        mMovesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                Move move = dataSnapshot.getValue(Move.class);
                mGame.getMoves().add(move);
                mGameSurface.invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
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
        mMovesRef.addChildEventListener(mMovesListener);
    }

    /**
     * Add new participants to game and add score
     */
    private void setupParticipantHandler() {
        mParticipantsRef = mGameRef.child("participants");
        mParticipantsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                TextView tv = new TextView(getApplicationContext());
                tv.setText(user.toString());
                tv.setTextColor(Color.parseColor(user.getColour()));
                tv.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT));
                mParticipants.put(user, tv);
                mScoreboard.addView(tv);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                TextView tv = mParticipants.get(user);
                tv.setText(user.toString());
                tv.setTextColor(Color.parseColor(user.getColour()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                mScoreboard.removeView(mParticipants.remove(user));
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
        mParticipantsRef.addChildEventListener(mParticipantsListener);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: end game");
        // TODO confirmation box, option to end game, show final score
        finish();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ended game");
        if ((mMovesRef != null) && (mMovesListener != null)) {
            mMovesRef.removeEventListener(mMovesListener);
        }
        if ((mUsersRef != null) && (mPossibleParticipantListener != null)) {
            mUsersRef.removeEventListener(mPossibleParticipantListener);
        }
        if ((mParticipantsRef != null) && (mParticipantsListener != null)) {
            mParticipantsRef.removeEventListener(mParticipantsListener);
        }
        super.onDestroy();
    }

}
