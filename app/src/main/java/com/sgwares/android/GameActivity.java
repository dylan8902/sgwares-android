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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference("users");

        final DatabaseReference usersRef = mDatabase.getReference("users");
        final ListView listView = (ListView) findViewById(R.id.possible_participants);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mPossibleParticipants);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addParticipant(mPossibleParticipants.get(position));
            }
        });

        final ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                User user = dataSnapshot.getValue(User.class);
                user.setKey(dataSnapshot.getKey());
                Log.d(TAG, "New possible participant:" + user);
                mPossibleParticipants.add(user);
                adapter.notifyDataSetChanged();
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
        usersRef.addChildEventListener(childEventListener);

        final Button startGame = (Button) findViewById(R.id.start);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.removeEventListener(childEventListener);
                createGame();
            }
        });
    }

    private void addParticipant(User user) {
        Log.d(TAG, "addParticipant: " + user);
        Snackbar.make(findViewById(R.id.content_main), user.getName() + " added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void createGame() {
        final Game game = new Game();
        final User playerA = new User("Fred", 0, "#fffaac");
        playerA.setKey("G4wMvhRNMtaoKQok9PPcPmTLZ1w0");
        final User playerB = new User("Dylan", 0, "#fffaac");
        playerA.setKey("G4wMvhRNMtaoKQok9PPcPmTLZ1w2");
        List<User> participants = new ArrayList<>();
        participants.add(playerA);
        participants.add(playerB);
        game.setParticipants(participants);
        game.setBackground("#bbbbbb");

        mGame = mDatabase.getReference("games").push();
        Log.d(TAG, "Created game key: " + mGame.getKey());
        mGame.setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Create game onComplete: " + task.isSuccessful());
                GameSurface surface = new GameSurface(getApplicationContext(), game, playerA);
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
