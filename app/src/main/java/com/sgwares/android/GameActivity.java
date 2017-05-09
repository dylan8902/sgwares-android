package com.sgwares.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sgwares.android.models.Game;
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGame;
    private List<User> mPossibleParticipants = new ArrayList<>();;
    private List<User> mParticipants = new ArrayList<>();
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("users");

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mPossibleParticipants);
        final ListView listView = (ListView) findViewById(R.id.possible_participants);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addParticipant(mPossibleParticipants.get(position));
            }
        });

        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                if (user.getKey().equals(auth.getCurrentUser().getUid())) {
                    Log.d(TAG, "Current user: " + user);
                    mParticipants.add(user);
                } else {
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
        final DatabaseReference usersRef = mDatabase.getReference("users");
        usersRef.addChildEventListener(childEventListener);

        final Button startGame = (Button) findViewById(R.id.start);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParticipants.isEmpty()) {
                    Snackbar.make(findViewById(R.id.content_main), "No participants", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    usersRef.removeEventListener(childEventListener);
                    createGame();
                }
            }
        });
    }

    private void addParticipant(User user) {
        Log.d(TAG, "addParticipant: " + user);
        mParticipants.add(user);
        mPossibleParticipants.remove(user);
        mAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.content_main), user.getName() + " added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void createGame() {
        final Game game = new Game();
        game.setParticipants(mParticipants);
        game.setBackground("#bbbbbb");

        mGame = mDatabase.getReference("games").push();
        Log.d(TAG, "Created game key: " + mGame.getKey());
        mGame.setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Create game onComplete: " + task.isSuccessful());
                GameSurface surface = new GameSurface(getApplicationContext(), game, mParticipants.get(0));
                setContentView(surface);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: end game");
        //TODO confirmation box, option to end game, show final score
        finish();
    }

}
