package com.sgwares.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.sgwares.android.models.Game;
import com.sgwares.android.models.Move;
import com.sgwares.android.models.User;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Setup an example game
        Game game = new Game();
        User playerA = new User("Bob", 4);
        User playerB = new User("Dylan", 19);
        List<User> participants = new ArrayList<>();
        participants.add(playerA);
        participants.add(playerB);
        game.setParticipants(participants);
        List<Move> moves = new ArrayList<>();

        // -. . .
        //  . . .
        Move move = new Move(0, 0, 0);
        move.setUser(playerA);
        moves.add(move);

        // -. . .
        //| . . .
        move = new Move(0, 0, 1);
        move.setUser(playerB);
        moves.add(move);

        // -.-. .
        //| . . .
        move = new Move(1, 0, 0);
        move.setUser(playerA);
        moves.add(move);

        game.setMoves(moves);

        setContentView(new GameSurface(this, game));
    }

    @Override
    public void onBackPressed() {

    }

}
