package com.example.user.encapsulate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

    private TextView mUsernameBlank, mPasswordBlank, mCPasswordBlank, mEmailAddBlank;
    private Button mAddAccount;
    String usernameInput, passwordInput, cPasswordInput, emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsernameBlank = (EditText) findViewById(R.id.UsernameBlank);
        mPasswordBlank = (EditText) findViewById(R.id.PasswordBlank);
        mCPasswordBlank = (EditText) findViewById(R.id.PasswordBlank2);
        mEmailAddBlank = (EditText) findViewById(R.id.EmailBlank);
        mAddAccount = (Button) findViewById(R.id.CreateButton);

        final ProgressDialog mSignUpLoader = new ProgressDialog(SignUpActivity.this);
        mSignUpLoader.setMessage(getString(R.string.signup_dialog_message));
        mSignUpLoader.setIndeterminate(true);
        mSignUpLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_red));
        mAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput = mUsernameBlank.getText().toString();
                passwordInput = mPasswordBlank.getText().toString();
                cPasswordInput = mCPasswordBlank.getText().toString();
                emailInput = mEmailAddBlank.getText().toString();

                if (usernameInput.equals(getString(R.string.nothing)) | passwordInput.equals(getString(R.string.nothing)) | cPasswordInput.equals(getString(R.string.nothing)) | emailInput.equals(getString(R.string.nothing))){
                    alertMessage(getString(R.string.empty_fields_error));
                    //checks for empty fields
                }
                else if (!passwordInput.equals(cPasswordInput)){
                    alertMessage(getString(R.string.passwords_dont_match_error));
                    mPasswordBlank.setText(getString(R.string.nothing));
                    mCPasswordBlank.setText(getString(R.string.nothing));
                }
                else {
                    ParseUser userObject = new ParseUser();
                    userObject.setUsername(usernameInput);
                    userObject.setPassword(passwordInput);
                    userObject.setEmail(emailInput);
                    userObject.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                ParseUser.logInInBackground(usernameInput, passwordInput, new LogInCallback() {
                                    public void done(ParseUser user, ParseException e) {
                                        if (user != null && e == null) {
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            SignUpActivity.this.startActivity(intent);
                                        } else {
                                            alertMessage(e.toString());
                                        }
                                    }
                                });
                            }
                            else {
                                alertMessage(e.toString());
                            }
                        }
                    });
                }
            }});



    }

    public void alertMessage(String message){
        if (message.startsWith("com.parse.ParseRequest $ParseRequestException: ")){
            message.replace("com.parse.ParseRequest $ParseRequestException: ", "");
        }
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
