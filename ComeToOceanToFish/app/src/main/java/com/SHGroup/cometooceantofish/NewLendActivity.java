package com.SHGroup.cometooceantofish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;
import com.SHGroup.cometooceantofish.fragments.LendCategory;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class NewLendActivity extends Activity {

    private NewLendTask mAuthTask = null;

    private EditText mTitle;
    private Spinner mLendCategory;
    private EditText mHourPrice;
    private EditText mDayPrice;
    private EditText mContent;

    private Bitmap bitmap;

    private String access_token;

    private View mProgressView;
    private View mNewLendFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lend);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        access_token = getIntent().getStringExtra("access_token");

        // Set up the login form.
        mTitle = (EditText) findViewById(R.id.new_lend_title);
        mLendCategory = (Spinner) findViewById(R.id.new_lend_category);
        mHourPrice = (EditText) findViewById(R.id.new_lend_hour_price);
        mDayPrice = (EditText) findViewById(R.id.new_lend_day_price);
        mContent = (EditText) findViewById(R.id.new_lend_content);

        ArrayList<String> arrs = new ArrayList<>();

        for(LendCategory c : LendCategory.values()){
            arrs.add(c.getKorean());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrs.toArray(new String[]{}));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLendCategory.setAdapter(adapter);


        Button newlend = (Button) findViewById(R.id.new_lend_button);
        newlend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptNewLend();
            }
        });


        mNewLendFormView = findViewById(R.id.new_lend_form);
        mProgressView = findViewById(R.id.new_lend_progress);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = (ImageView) findViewById(R.id.lendimage);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void attemptNewLend() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mTitle.setError(null);

        // Store values at the time of the login attempt.
        String title = mTitle.getText().toString();
        int selected = LendCategory.getCategoryFromKorean((String) mLendCategory.getSelectedItem()).getCode();
        int hour_price = Integer.parseInt(mHourPrice.getText().toString());
        int day_price = Integer.parseInt(mDayPrice.getText().toString());
        String content = mContent.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(title) && !isTitleValid(title)) {
            mTitle.setError("제목을 5자 이상 입력해주세요");
            focusView = mTitle;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new NewLendTask(title, selected, hour_price, day_price, content);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isTitleValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mNewLendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mNewLendFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNewLendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mNewLendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class NewLendTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final int mSelected;
        private final int mHourPrice;
        private final int mDayPrice;
        private final String mContent;

        NewLendTask(String title, int selected, int hour_price, int day_price, String content) {
            mTitle = title;
            mSelected = selected;
            mHourPrice = hour_price;
            mDayPrice = day_price;
            mContent = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bitmapdata = outputStream.toByteArray();

                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/rent").setRequestType(RequestHelper.RequestType.POST)
                        .putHeader("Authorization", access_token)
                        .putQuery("title", mTitle)
                        .putQuery("category", Integer.toString(mSelected))
                        .putQuery("hour_price", mHourPrice)
                        .putQuery("day_price", mDayPrice)
                        .putQuery("content", mContent)
                        .connect();

                if(res.getResponseCode() == 201){
                    try{
                        access_token = new JSONObject(res.getBody()).getString("access_token");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    String re = res.getBody();
                    FileOutputStream out = null;
                    try {
                        File file = new File(getFilesDir(), re + ".bmp");
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewLendActivity.this, "성공적으로 글을 작성하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                    return true;
                }else if(res.getResponseCode() == 403){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewLendActivity.this, "권한이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewLendActivity.this, "글을 올릴 수 없습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
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