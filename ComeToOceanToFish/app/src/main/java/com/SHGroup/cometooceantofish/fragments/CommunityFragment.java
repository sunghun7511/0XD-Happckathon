package com.SHGroup.cometooceantofish.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.LendViewActivity;
import com.SHGroup.cometooceantofish.LoginActivity;
import com.SHGroup.cometooceantofish.NewLendActivity;
import com.SHGroup.cometooceantofish.R;
import com.SHGroup.cometooceantofish.RegisterActivity;
import com.SHGroup.cometooceantofish.UploadPostActivity;
import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommunityFragment extends FragmentBase{
    public CommunityFragment(){
    }

    private ListView mPostList;
    private CustomPostArrayAdapter adapter;

    private ProgressBar mProgressBar;
    private PostLoadTask mLoadTask;

    private Button upload;

    private View list_form;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_community, container, false);

        mPostList = (ListView) rootView.findViewById(R.id.communitylist);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.community_progress);
        list_form = rootView.findViewById(R.id.community_form);

        upload = (Button) rootView.findViewById(R.id.community_upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UploadPostActivity.class);
                i.putExtra("access_token", getAccessToken());
                startActivity(i);
            }
        });

        adapter = new CustomPostArrayAdapter();
        mPostList.setAdapter(adapter);

        showProgress(true);

        mLoadTask = new PostLoadTask();
        mLoadTask.execute((Void) null);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        showProgress(true);

        mLoadTask = new PostLoadTask();
        mLoadTask.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            list_form.setVisibility(show ? View.GONE : View.VISIBLE);
            list_form.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    list_form.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            list_form.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class  PostLoadTask extends AsyncTask<Void, Void, Boolean> {

        private PostLoadTask(){
        }

        private final Drawable getDrawable(String u) throws Exception{
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            input.close();
            return new BitmapDrawable(myBitmap);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/boast").putDefaultHeader().putHeader("Authorization", getAccessToken()).connect();

                if(res.getResponseCode() == 200 || res.getResponseCode() == 204){
                    if(res.getResponseCode() == 200){
                        try{
                            adapter.clear();

                            JSONArray jar = new JSONArray(res.getBody());
                            for(int i = jar.length() - 1 ; i >= 0 ; i --){
                                JSONObject jo = jar.getJSONObject(i);
                                File file = new File(getActivity().getFilesDir(), jo.getString("id") + ".post.bmp");
                                Drawable dr = null;
                                Log.i("aaa", " " + file.exists());
                                Log.i("aaa", " " + getActivity().getFilesDir().getAbsolutePath());
                                if(file.exists())
                                    dr = new BitmapDrawable(BitmapFactory.decodeFile(file.getAbsolutePath()));

                                adapter.addItem(dr, jo.getString("id"), jo.getString("title"), jo.getString("content"), jo.getString("date"), jo.getString("author_nickname"));
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                    return true;
                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "자랑 목록 로드에 실패하였습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
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
            mLoadTask = null;
            showProgress(false);
            if (!success) {

            }
        }

        @Override
        protected void onCancelled() {
            mLoadTask = null;
            showProgress(false);
        }
    }
}
