package com.sgwares.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sgwares.android.models.Game;
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private GameSurface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        final EditText etName = (EditText) findViewById(R.id.name);
        final EditText etColour = (EditText) findViewById(R.id.colour);
        final EditText etBackground = (EditText) findViewById(R.id.background);
        Button startGame = (Button) findViewById(R.id.start);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = new Game();
                User playerA = new User(etName.getText().toString(), 0, etColour.getText().toString());
                playerA.setKey("abc");
                User playerB = new User("Dylan", 0, "#fffaac");
                playerA.setKey("def");
                List<User> participants = new ArrayList<>();
                participants.add(playerA);
                participants.add(playerB);
                game.setParticipants(participants);
                game.setBackground(etBackground.getText().toString());
                surface = new GameSurface(getApplicationContext(), game, playerA);
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
