package com.SHGroup.cometooceantofish.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.TextView;

import com.SHGroup.cometooceantofish.R;

public class CustomArrayAdapter extends BaseAdapter {
    private ArrayList<LendItem> listViewItemList = new ArrayList<LendItem>() ;

    public CustomArrayAdapter(){
    }

    @Override
    public int getCount() {
        return listViewItemList.size() + 1 ;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i - 1;
        final Context context = viewGroup.getContext();

        int id = i == 0 ? R.layout.additem : R.layout.listitem;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(id, viewGroup, false);
        }

        if(i != 0){
            ImageView iconImageView = (ImageView) view.findViewById(R.id.image) ;
            TextView titleTextView = (TextView) view.findViewById(R.id.title) ;
            TextView descTextView = (TextView) view.findViewById(R.id.category) ;

            LendItem listViewItem = listViewItemList.get(pos);

            iconImageView.setImageDrawable(listViewItem.getImage());
            titleTextView.setText(listViewItem.getTitle());
            descTextView.setText(listViewItem.getDescription());
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0){
            return null;
        }
        return listViewItemList.get(position - 1);
    }

    public void addItem(Drawable icon, String id, String title, String desc) {
        LendItem item = new LendItem(icon, id, title, desc);
        listViewItemList.add(item);
    }
}
