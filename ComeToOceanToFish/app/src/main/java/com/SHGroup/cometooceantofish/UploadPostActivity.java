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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;
import com.SHGroup.cometooceantofish.fragments.LendCategory;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class UploadPostActivity extends Activity {

    private UploadPostTask mUploadPostTask = null;

    private TextView mTitle;
    private TextView mContent;

    private ImageView viewlend;

    private Bitmap bitmap;

    private Button upload;

    private String access_token;

    private View mProgressView;
    private View mNewLendFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        access_token = getIntent().getStringExtra("access_token");

        mTitle = (TextView) findViewById(R.id.upload_post_title);
        mContent = (TextView) findViewById(R.id.upload_post_content);

        mNewLendFormView = findViewById(R.id.upload_post_form);
        mProgressView = findViewById(R.id.upload_post_progress);

        viewlend = (ImageView) findViewById(R.id.post_image);

        upload = (Button) findViewById(R.id.upload_post_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTitle.getText().length() <= 4){
                    mTitle.setError("제목의 길이는 5글자 이상이어야 합니다.");
                    mTitle.requestFocus();
                    return;
                }
                showProgress(true);
                mUploadPostTask = new UploadPostTask(mTitle.getText().toString(), mContent.getText().toString());
                mUploadPostTask.execute((Void) null);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = (ImageView) findViewById(R.id.post_image);
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

    public class UploadPostTask extends AsyncTask<Void, Void, Boolean> {

        private String mTitle;
        private String mContent;

        UploadPostTask(String title, String content) {
            mTitle = title;
            mContent = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/boast").setRequestType(RequestHelper.RequestType.POST)
                        .putHeader("Authorization", access_token)
                        .putQuery("title", mTitle)
                        .putQuery("content", mContent)
                        .connect();

                if(res.getResponseCode() == 201){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        try{
                            String id = res.getBody();
                            File file = new File(getFilesDir(), id + ".post.bmp");
                            Drawable dr = null;
                            FileOutputStream out = null;
                            try {
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
                            viewlend.setImageDrawable(dr);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        }
                    });
                    return true;
                }else if(res.getResponseCode() == 205){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadPostActivity.this, "제목이 너무 짧습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadPostActivity.this, "글을 쓸 수 없습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
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
            mUploadPostTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mUploadPostTask = null;
            showProgress(false);
        }
    }
}