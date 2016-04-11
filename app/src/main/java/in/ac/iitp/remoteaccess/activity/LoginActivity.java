package in.ac.iitp.remoteaccess.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.iitp.remoteaccess.R;
import in.ac.iitp.remoteaccess.utils.Client;
import in.ac.iitp.remoteaccess.utils.Constants;
import in.ac.iitp.remoteaccess.utils.MyHttpClient;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    public static final String INTENT_EMAIL = "email";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    private EditText mServerIPView;
    private EditText mServerPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mServerIPView = (EditText) findViewById(R.id.server_ip);
        mServerPasswordView= (EditText) findViewById(R.id.server_pass);

        Button mLinkButton = (Button) findViewById(R.id.ip_direct_button);
        mLinkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptDirect();
            }
        });



    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            tryLogin(email, password);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length()>0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    void tryLogin(final String email,String password)
    {
        ArrayList<Pair<String,String>> param = new ArrayList<>();
        param.add(new Pair<String, String>("username",email));
        param.add(new Pair<String, String>("password",password));

        MyHttpClient client = new MyHttpClient(Constants.BASE_URL + "/api/login", param, true, new  MyHttpClient.MyHttpClientListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onFailed(Exception e) {
                showProgress(false);
                Snackbar.make(findViewById(android.R.id.content),
                        "Error in Connection", Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onSuccess(Object output) {
                String result = (String) output;
                //Update AllIDS variable and saveSharedPref...............
                if(result.equals("loggedIn"))
                {
                    Intent in =new Intent(LoginActivity.this, Devices.class);
                    //in.putExtra(INTENT_EMAIL, email);
                    LoginActivity.this.startActivity(in);
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Invalid Credentials!\n"+result, Snackbar.LENGTH_SHORT).show();
                }

                showProgress(false);
            }

            @Override
            public void onBackgroundSuccess(String result) {
                    Log.e("******************", "AA" + result);
            }
        });

    }



    private void attemptDirect() {
        String ip = mServerIPView.getText().toString();
        String pass = mServerPasswordView.getText().toString();

        Client client = new Client(this,ip){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress(true);

            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean==null) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Can't Connect!!!", Snackbar.LENGTH_SHORT).show();

                } else                if(aBoolean!=true)
                {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Invalid Details", Snackbar.LENGTH_SHORT).show();

                }
                else {
                    Intent in =new Intent(LoginActivity.this, Home.class);
                    LoginActivity.this.startActivity(in);

                }
                showProgress(false);


            }
        };
        client.execute(pass);

    }

}

