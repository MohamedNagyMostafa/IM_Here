package com.here.iam.nagy.mohamed.imhere.user_account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseCreateAccount;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;

public class CreateAccountActivity extends AppCompatActivity {

    private ViewAppHolder.CreateAccountViewHolder createAccountViewHolder;
    
    private FirebaseAuth userAuth;
    private UserDataFirebaseCreateAccount userDataFirebaseCreateAccount;

    /**
     * Listener to create account button, launch progress dialog
     * then check if the inputs correct or not
     */
    private View.OnClickListener createAccountButtonListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewAccount();
                }
            };

    private View.OnClickListener signInTextViewListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent loginActivityIntent = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(loginActivityIntent);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide App name
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createAccountViewHolder = new ViewAppHolder.CreateAccountViewHolder(findViewById(R.id.sign_up_view));

        Animation.CreateAccountAnimation.startingAnimation(createAccountViewHolder);
        Animation.CreateAccountAnimation.editTextFocusAnimation(
                this,
                createAccountViewHolder.USER_NAME_EDIT_TEXT,
                createAccountViewHolder.USER_EMAIL_EDIT_TEXT,
                createAccountViewHolder.USER_PASSWORD_EDIT_TEXT);

        userAuth = FirebaseAuth.getInstance();

        createAccountViewHolder.SING_UP_BUTTON.setOnClickListener(createAccountButtonListener);
        createAccountViewHolder.SIGN_IN_TEXT_VIEW.setOnClickListener(signInTextViewListener);
    }

    /**
     * Get both email and password of user and create account
     * if the inputs is correct send data to firebase to saved
     */
    private void createNewAccount(){

        // Validation for empty field.
        if(createAccountViewHolder.USER_EMAIL_EDIT_TEXT.getText().toString().isEmpty() ||
                createAccountViewHolder.USER_NAME_EDIT_TEXT.getText().toString().isEmpty()  ||
                createAccountViewHolder.USER_PASSWORD_EDIT_TEXT.getText().toString().isEmpty() ){


            if(createAccountViewHolder.USER_NAME_EDIT_TEXT.getText().toString().isEmpty())
                createAccountViewHolder.USER_NAME_EDIT_TEXT.setError("This field must not be empty");

            if(createAccountViewHolder.USER_EMAIL_EDIT_TEXT.getText().toString().isEmpty())
                createAccountViewHolder.USER_EMAIL_EDIT_TEXT.setError("This filed must not be empty");

            if(createAccountViewHolder.USER_PASSWORD_EDIT_TEXT.getText().toString().isEmpty())
                createAccountViewHolder.USER_PASSWORD_EDIT_TEXT.setError("This filed must not be empty");

        }else if(Utility.networkIsConnected(this)){
            /// check valid email
            if(isValidEmail(createAccountViewHolder.USER_EMAIL_EDIT_TEXT.getText().toString())){

                final String USER_EMAIL = createAccountViewHolder.USER_EMAIL_EDIT_TEXT.getText().toString();
                final String USER_PASSWORD = createAccountViewHolder.USER_PASSWORD_EDIT_TEXT.getText().toString();
                final ProgressDialog PROGRESS_DIALOG = new ProgressDialog(this);

                PROGRESS_DIALOG.setMessage("Sing up ....");
                PROGRESS_DIALOG.setCancelable(false);
                PROGRESS_DIALOG.show();

                userAuth.createUserWithEmailAndPassword(USER_EMAIL,USER_PASSWORD)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if(task.isSuccessful()){
                                    PROGRESS_DIALOG.dismiss();
                                    Toast.makeText(getBaseContext(),
                                            "Your account is create, " +
                                                    "check your email inbox to verify your register",
                                            Toast.LENGTH_LONG).show();

                                    FirebaseUser user = userAuth.getCurrentUser();

                                    userDataFirebaseCreateAccount =
                                            new UserDataFirebaseCreateAccount(user.getEmail());

                                    userDataFirebaseCreateAccount.createAccount(
                                            user,createAccountViewHolder.USER_NAME_EDIT_TEXT.getText().toString());

                                    /// send verify email
                                    user.sendEmailVerification();

                                    openLoginActivity();
                                }else{
                                    PROGRESS_DIALOG.dismiss();
                                    createAccountViewHolder.USER_EMAIL_EDIT_TEXT.setError(getString(R.string.error_invalid_email_not_valid));
                                }
                            }
                        });

            }else if(Utility.networkIsConnected(this)){

                createAccountViewHolder.USER_EMAIL_EDIT_TEXT.setError("This email is not valid");
            }else{

                Toast.makeText(this,"Check your network connection",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Called when user created new account. Pass it's email id example@hotmail,com to
     * profile activity to get user identifiers
     */
    private void openLoginActivity(){

        Intent loginActivity = new Intent(getBaseContext(), LoginActivity.class);

        startActivity(loginActivity);

        finish();
    }


    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
