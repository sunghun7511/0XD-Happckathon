package com.SHGroup.cometooceantofish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;
import com.SHGroup.cometooceantofish.fragments.LendCategory;

import org.json.JSONObject;

import java.io.File;

public class UploadPostActivity extends Activity {

    private ViewLendTask mViewLendTask = null;

    private TextView mTitle;
    private TextView mLendCategory;
    private TextView mHourPrice;
    private TextView mDayPrice;
    private TextView mContent;
    private TextView mAuthorID;
    private TextView mAuthorNickname;
    private TextView mAuthorPhone;

    private ImageView viewlend;

    private Bitmap bitmap;

    private String access_token;

    private String id;

    private View mProgressView;
    private View mNewLendFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lend);

        access_token = getIntent().getStringExtra("access_token");
        id = getIntent().getStringExtra("id");

        // Set up the login form.
        mTitle = (TextView) findViewById(R.id.view_lend_title);
        mLendCategory = (TextView) findViewById(R.id.view_lend_category);
        mHourPrice = (TextView) findViewById(R.id.view_lend_hour_price);
        mDayPrice = (TextView) findViewById(R.id.view_lend_day_price);
        mContent = (TextView) findViewById(R.id.view_lend_content);
        mAuthorID = (TextView) findViewById(R.id.view_lend_author_id);
        mAuthorNickname = (TextView) findViewById(R.id.view_lend_author_nickname);
        mAuthorPhone = (TextView) findViewById(R.id.view_lend_author_phone);

        mNewLendFormView = findViewById(R.id.view_lend_form);
        mProgressView = findViewById(R.id.view_lend_progress);

        viewlend = (ImageView)findViewById(R.id.viewlend);

        showProgress(true);
        mViewLendTask = new ViewLendTask();
        mViewLendTask.execute((Void) null);
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

    public class ViewLendTask extends AsyncTask<Void, Void, Boolean> {

        ViewLendTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/rent").setRequestType(RequestHelper.RequestType.GET)
                        .putHeader("Authorization", access_token)
                        .putQuery("id", id)
                        .connect();

                if(res.getResponseCode() == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try{
                            JSONObject jo = new JSONObject(res.getBody());

                            String title = jo.getString("title");
                            String day_price = jo.getString("day_price");
                            String hour_price = jo.getString("hour_price");
                            String content = jo.getString("content");
                            String category = LendCategory.getCategoryFromCode(Integer.parseInt(jo.getString("category"))).getKorean();
                            String author_id = jo.getString("author_id");
                            String author_nickname = jo.getString("author_nickname");
                            String author_phone = jo.getString("author_phone");

                            mTitle.setText(title);
                            mDayPrice.setText("하루당 가격 : " + day_price);
                            mHourPrice.setText("시간당 가격 : " + hour_price);
                            mLendCategory.setText("카테고리 : " + category);
                            mContent.setText(content);
                            mAuthorID.setText("ID : " + author_id);
                            mAuthorNickname.setText("Nickname : " + author_nickname);
                            mAuthorPhone.setText("Phone : " + author_phone);

                            File file = new File(getFilesDir(), id + ".bmp");
                            Drawable dr = null;
                            if(file != null) {
                                dr = new BitmapDrawable(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                viewlend.setImageDrawable(dr);
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        }
                    });
                    return true;
                }else if(res.getResponseCode() == 204){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadPostActivity.this, "글이 없습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadPostActivity.this, "글을 볼 수 없습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
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
            mViewLendTask = null;
            showProgress(false);

            if (!success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mViewLendTask = null;
            showProgress(false);
        }
    }
}