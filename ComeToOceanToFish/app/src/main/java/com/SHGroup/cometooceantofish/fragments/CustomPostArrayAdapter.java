package com.SHGroup.cometooceantofish.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.SHGroup.cometooceantofish.R;

import java.util.ArrayList;

public class CustomPostArrayAdapter extends BaseAdapter {
    private ArrayList<PostItem> postItemList = new ArrayList<>() ;

    public CustomPostArrayAdapter(){
    }

    @Override
    public int getCount() {
        return postItemList.size();
    }

    public void clear(){
        postItemList.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.postitem, viewGroup, false);
        }
        ImageView iconImageView = (ImageView) view.findViewById(R.id.post_image) ;
        TextView title = (TextView) view.findViewById(R.id.post_title) ;
        TextView content = (TextView) view.findViewById(R.id.post_content) ;

        PostItem listViewItem = postItemList.get(pos);

        iconImageView.setImageDrawable(listViewItem.getImage());

        title.setText(listViewItem.getTitle());

        content.setText(listViewItem.getDate() + " / " + listViewItem.getAuthorNickname() + "\n\n" + listViewItem.getContent() + "\n");

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return postItemList.get(position);
    }

    public void addItem(Drawable icon, String id, String title, String content, String date, String author_nickname) {
        PostItem item = new PostItem(icon, id, title, content, date, author_nickname);
        postItemList.add(item);
    }
}
