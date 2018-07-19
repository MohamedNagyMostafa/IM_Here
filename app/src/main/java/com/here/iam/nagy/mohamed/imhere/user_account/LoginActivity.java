package com.here.iam.nagy.mohamed.imhere.user_account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.animation.Animation;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.ui.main_ui.MainUserActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ViewAppHolder.LoginViewHolder loginViewHolder;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private View.OnClickListener signUpTextViewListener=
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createAccountActivityIntent =
                            new Intent(getBaseContext(),CreateAccountActivity.class);
                    startActivity(createAccountActivityIntent);
                }
            };

    private View.OnClickListener signInButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide App name
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loginViewHolder = new ViewAppHolder.LoginViewHolder(findViewById(R.id.login_activity_view));
        // Animation.
        Animation.LoginActivityAnimation.startingAnimation(loginViewHolder);
        Animation.LoginActivityAnimation.editTextFocusAnimation(this,
                loginViewHolder.EMAIL_EDIT_TEXT, loginViewHolder.PASSWORD_EDIT_TEXT);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null && user.isEmailVerified()){

                    openMainActivity(user);
                }

            }
        };

        loginViewHolder.SIGN_IN_BUTTON.setOnClickListener(signInButtonListener);
        loginViewHolder.SIGN_UP_TEXT_VIEW.setOnClickListener(signUpTextViewListener);
    }

    private void openMainActivity(FirebaseUser user){

        Intent mainActivityIntent = new Intent(getBaseContext(), MainUserActivity.class);

        mainActivityIntent.putExtra(
                Constants.USER_EXTRA,
                Utility.encodeUserEmail(Objects.requireNonNull(user.getEmail()))
        );

        startActivity(mainActivityIntent);

        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        firebaseAuth.removeAuthStateListener(authStateListener);
        super.onStop();
    }

    private void signIn(){

        if(loginViewHolder.EMAIL_EDIT_TEXT.getText().toString().isEmpty() || 
                loginViewHolder.PASSWORD_EDIT_TEXT.getText().toString().isEmpty()) {

            if(loginViewHolder.PASSWORD_EDIT_TEXT.getText().toString().isEmpty())
                loginViewHolder.PASSWORD_EDIT_TEXT.setError(getString(R.string.empty_field));

            if(loginViewHolder.EMAIL_EDIT_TEXT.getText().toString().isEmpty())
                loginViewHolder.EMAIL_EDIT_TEXT.setError(getString(R.string.empty_field));

            return;
        }else if(!isValidEmail(loginViewHolder.EMAIL_EDIT_TEXT.getText().toString())){

            loginViewHolder.EMAIL_EDIT_TEXT.setError(getString(R.string.empty_field));
            return;
        }else if(!Utility.networkIsConnected(this)){

            Toast.makeText(this,getString(R.string.network_connection),Toast.LENGTH_SHORT).show();
            return;
        }

        final String USER_EMAIL = loginViewHolder.EMAIL_EDIT_TEXT.getText().toString();
        final String USER_PASSWORD = loginViewHolder.PASSWORD_EDIT_TEXT.getText().toString();
        final ProgressDialog PROGRESS_DIALOG = new ProgressDialog(this);

        PROGRESS_DIALOG.setMessage(getString(R.string.loading_sign_in));
        PROGRESS_DIALOG.setCancelable(false);
        PROGRESS_DIALOG.show();

        firebaseAuth.signInWithEmailAndPassword(USER_EMAIL,USER_PASSWORD)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null && user.isEmailVerified()) {
                                PROGRESS_DIALOG.dismiss();
                                openMainActivity(user);

                            }else{
                                PROGRESS_DIALOG.dismiss();
                                Toast.makeText(getBaseContext(),
                                        getString(R.string.email_verify),
                                        Toast.LENGTH_LONG).show();
                            }

                        }else{
                            PROGRESS_DIALOG.dismiss();
                            loginViewHolder.EMAIL_EDIT_TEXT.setError(getString(R.string.email_reg));
                        }

                    }
                });
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



}
