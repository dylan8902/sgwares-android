package com.sgwares.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sgwares.android.fragments.GamesFragment;
import com.sgwares.android.fragments.LeaderboardFragment;
import com.sgwares.android.fragments.SettingsFragment;
import com.sgwares.android.models.Game;
import com.sgwares.android.models.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LeaderboardFragment.OnListFragmentInteractionListener,
        GamesFragment.OnListFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 4001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivityForResult(loginActivity, LOGIN_ACTIVITY_REQUEST_CODE);
        } else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onCancelled: " + dataSnapshot.toString());
                    User user = dataSnapshot.getValue(User.class);
                    updateUserUI(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gameActivity = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(gameActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_games);
        navigationView.setNavigationItemSelectedListener(this);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_main, new GamesFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_games) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new GamesFragment())
                    .commit();
        } else if (id == R.id.nav_leaderboard) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new LeaderboardFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_main, new SettingsFragment())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(User item) {
        Log.d(TAG, "Item clicked: " + item);
    }

    @Override
    public void onListFragmentInteraction(Game game) {
        Log.d(TAG, "onListFragmentInteraction Game clicked: " + game);
        Intent gameActivity = new Intent(getApplicationContext(), GameActivity.class);
        gameActivity.putExtra(GameActivity.GAME_KEY, game.getKey());
        startActivity(gameActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            User user = (User) data.getSerializableExtra(LoginActivity.USER_RESULT_KEY);
            Log.d(TAG, "onActivityResult User: " + user);
            updateUserUI(user);
        }
    }

    private void updateUserUI(User user) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView name = (TextView) navigationHeaderView.findViewById(R.id.user_name);
        name.setText(user.getName());
        LinearLayout navigationHeader = (LinearLayout) navigationHeaderView.findViewById(R.id.header);
        navigationHeader.setBackgroundColor(Color.parseColor(user.getColour()));
    }

}
