package com.SHGroup.cometooceantofish.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.SHGroup.cometooceantofish.LendViewActivity;
import com.SHGroup.cometooceantofish.LoginActivity;
import com.SHGroup.cometooceantofish.MainActivity;
import com.SHGroup.cometooceantofish.NewLendActivity;
import com.SHGroup.cometooceantofish.R;
import com.SHGroup.cometooceantofish.api.RequestException;
import com.SHGroup.cometooceantofish.api.RequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class LendFragment extends FragmentBase {
    public LendFragment(){}

    private RelativeLayout list_layout;
    private ProgressBar mProgressBar;

    private ListView listView;
    private CustomArrayAdapter adapter;

    private LoadLendListTask mLoadTask;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lend, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.lend_progress);
        list_layout = (RelativeLayout) rootView.findViewById(R.id.lend_list_layout);

        listView = (ListView) rootView.findViewById(R.id.lendlist);

        adapter = new CustomArrayAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                LendItem item = (LendItem) parent.getItemAtPosition(position);

                if(item == null){
                    Intent newLend = new Intent(getActivity(), NewLendActivity.class);
                    newLend.putExtra("access_token", getAccessToken());
                    startActivity(newLend);
                    return;
                }

                Intent i = new Intent(getActivity(), LendViewActivity.class);
                i.putExtra("id", item.getId());
                i.putExtra("access_token", getAccessToken());
                startActivity(i);
            }
        }) ;

        showProgress(true);

        mLoadTask = new LoadLendListTask();
        mLoadTask.execute((Void) null);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        showProgress(true);

        mLoadTask = new LoadLendListTask();
        mLoadTask.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            list_layout.setVisibility(show ? View.GONE : View.VISIBLE);
            list_layout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    list_layout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            list_layout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class LoadLendListTask extends AsyncTask<Void, Void, Boolean> {

        private LoadLendListTask(){

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
                final RequestHelper.Response res = new RequestHelper("http://ec2.istruly.sexy:1234/rentlist").putDefaultHeader().putHeader("Authorization", getAccessToken())
                        .putQuery("category", Integer.toString(LendCategory.FISHING_ROD.getCode())).connect();

                if(res.getResponseCode() == 200 || res.getResponseCode() == 204){
                    if(res.getResponseCode() == 200){
                        try{
                            adapter.clear();

                            JSONArray jar = new JSONArray(res.getBody());
                            for(int i = jar.length() - 1 ; i >= 0; i --){
                                JSONObject jo = jar.getJSONObject(i);

                                File file = new File(getActivity().getFilesDir(), jo.getString("id") + ".bmp");
                                Drawable dr = null;
                                if(file != null && file.exists())
                                    dr = new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), 100, 100, true));

                                adapter.addItem(dr, jo.getString("id"), jo.getString("title"),
                                        LendCategory.getCategoryFromCode(Integer.parseInt(jo.getString("category"))).getKorean());
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
                            Toast.makeText(getActivity(), "대여 목록 로드에 실패하였습니다.\n(" + res.getResponseCode() + ")", Toast.LENGTH_LONG).show();
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
