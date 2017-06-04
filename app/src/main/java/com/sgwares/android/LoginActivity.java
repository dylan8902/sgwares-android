package com.sgwares.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sgwares.android.generators.ColourGenerator;
import com.sgwares.android.generators.NameGenerator;
import com.sgwares.android.models.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    protected static final String USER_RESULT_KEY = "User";
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private EditText mName;
    private EditText mColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mName = (EditText) findViewById(R.id.name);
        mName.setText(NameGenerator.generate());
        mColour = (EditText) findViewById(R.id.colour);
        mColour.setText(ColourGenerator.generate());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
        }

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        final Button btn = (Button) findViewById(R.id.login_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameIsValid()) {
                    Snackbar.make(findViewById(R.id.login), "Name is not valid", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (!colourIsValid()) {
                    Snackbar.make(findViewById(R.id.login), "Colour is not valid", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mName.setInputType(InputType.TYPE_NULL);
                mColour.setInputType(InputType.TYPE_NULL);
                btn.setEnabled(false);
                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously onComplete: " + task.isSuccessful());
                        if (task.isSuccessful()) {
                            processSignIn();
                        } else {
                            Snackbar.make(findViewById(R.id.login), "Unable to login", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Check the colour is valid
     * @return true if valid, false if it is not
     */
    private boolean colourIsValid() {
        try {
            Color.parseColor(mColour.getText().toString());
            return true;
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Check the name is valid
     * @return true if valid, false if it is not
     */
    private boolean nameIsValid() {
        String name = mName.getText().toString();
        if (name.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Create the user in the database and subscribe to user notifications
     * complete the activity with the user object as the result
     */
    private void processSignIn() {
        final User user = new User(mAuth.getCurrentUser());
        user.setName(mName.getText().toString());
        user.setColour(mColour.getText().toString());
        FirebaseMessaging.getInstance().subscribeToTopic(user.getKey());
        DatabaseReference newUser = usersRef.child(user.getKey());
        newUser.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Create user onComplete: " + task.isSuccessful());
                Intent output = new Intent();
                output.putExtra(USER_RESULT_KEY, user);
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }

}
