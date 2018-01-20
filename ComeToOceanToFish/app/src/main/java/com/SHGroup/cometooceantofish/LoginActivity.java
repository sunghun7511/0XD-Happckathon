package com.SHGroup.cometooceantofish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    private EditText mIDView;
    private EditText mPasswordView;


    private View mProgressView;
    private View mLoginFormView;


    private RelativeLayout mBackground;
    private Vector<BitmapDrawable> back = new Vector<>();
    private int back_now = 0;

    private Timer mTimer;
    private TimerTask mTimerTask;

    private Animation fade_in;
    private Animation fade_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mIDView = (EditText) findViewById(R.id.login_id);

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button login = (Button) findViewById(R.id.sign_in_button);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button register = (Button) findViewById(R.id.login_sign_up_button);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        mBackground = (RelativeLayout) findViewById(R.id.login_background);

        Bitmap b1 = BitmapFactory.decodeResource(getResources(),
                R.drawable.ocean1);
        Bitmap b2 = BitmapFactory.decodeResource(getResources(),
                R.drawable.ocean2);

        BitmapDrawable back1 = new BitmapDrawable(b1);
        BitmapDrawable back2 = new BitmapDrawable(b2);

        back1.setGravity(Gravity.FILL_VERTICAL | Gravity.CENTER);
        back2.setGravity(Gravity.FILL_VERTICAL | Gravity.CENTER);

        back.add(back1);
        back.add(back2);

        fade_in = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out);

        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                back_now ++;
                mBackground.setBackground(back.get(back_now % back.size()));
                mBackground.startAnimation(fade_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBackground.startAnimation(fade_out);
                    }
                });
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 5000, 5000);
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mIDView.setError(getString(R.string.error_field_required));
            focusView = mIDView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mIDView.setError(getString(R.string.error_invalid_email));
            focusView = mIDView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/auth").setRequestType(RequestHelper.RequestType.POST).putQuery("id", mUsername)
                        .putQuery("pw", mPassword).connect();

                if(res.getResponseCode() == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "성공적으로 로그인 하였습니다.", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    });
                    return true;
                }else if(res.getResponseCode() == 401){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            mIDView.setError("로그인에 실패하였습니다.");
                            mIDView.requestFocus();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
                            mIDView.setError("로그인에 실패하였습니다.");
                            mIDView.requestFocus();
                        }
                    });
                }
            }catch(RequestException ex){
                ex.printStackTrace();
                ex.getOriginalException().printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

노드제이에스 만세