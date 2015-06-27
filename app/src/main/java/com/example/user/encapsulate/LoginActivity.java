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


public class LoginActivity extends Activity {

    private TextView mSignUpTextView;
    private EditText userBlank;
    private EditText passBlank;
    private String mUsername;
    private String mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = (TextView) findViewById(R.id.signUpTextView);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

        userBlank = (EditText) findViewById(R.id.userEditText);
        passBlank = (EditText) findViewById(R.id.passEditText);

        mLogin = (Button) findViewById(R.id.loginButton);
        final ProgressDialog mLoginLoader = new ProgressDialog(LoginActivity.this);
        mLoginLoader.setMessage(getString(R.string.login_dialog_message));
        mLoginLoader.setIndeterminate(true);
        mLoginLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_bar_red));
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginLoader.show();
                mUsername = userBlank.getText().toString();
                mPassword = passBlank.getText().toString();
                if (mUsername.equals("") | mPassword.equals("")){
                    alertMessage("Please fill in the empty fields.");
                    //checks for empty fields
                    mLoginLoader.dismiss();
                }
                else ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        mLoginLoader.dismiss();
                        if (user != null && e == null) {
                            userBlank.setText("");
                            passBlank.setText("");
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            alertMessage(e.toString());
                        }
                    }
                });
            }
        });
    }

    public void alertMessage(String Message)
    {
        Toast.makeText(LoginActivity.this, Message, Toast.LENGTH_SHORT).show();
    }
}