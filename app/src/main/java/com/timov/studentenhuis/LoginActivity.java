package com.timov.studentenhuis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Inloggen...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        Ion.with(this)
            .load("http://studentenhuis-api.herokuapp.com/login")
            .setJsonObjectBody(json)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e == null) {
                        String status = result.get("status").getAsString();

                        if (Objects.equals(status, "failed")) {
                            Toast.makeText(getBaseContext(), result.get("error").getAsString(), Toast.LENGTH_LONG).show();
                            onLoginFailed();
                        } else {
                            String token = result.get("token").getAsString();
                            String userId = result.get("userId").getAsString();

                            if (!TextUtils.isEmpty(token)) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("loginToken", token);
                                editor.putString("userId", userId);
                                editor.apply();

                                onLoginSuccess();
                            } else {
                                Toast.makeText(getBaseContext(), "Er is iets misgegaan, probeer het opnieuw.", Toast.LENGTH_LONG).show();
                                onLoginFailed();
                            }
                        }
                    } else {
                        if (TextUtils.isEmpty(e.getMessage())) {
                            Toast.makeText(getBaseContext(), "Er is iets misgegaan, probeer het opnieuw.", Toast.LENGTH_LONG).show();
                            onLoginFailed();
                        } else {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            onLoginFailed();
                        }
                    }
                    progressDialog.dismiss();
                }
            });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                setResult(Activity.RESULT_OK, data);
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        setResult(Activity.RESULT_OK);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Vul een geldig e-mailadres in.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Vul een wachtwoord in.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}