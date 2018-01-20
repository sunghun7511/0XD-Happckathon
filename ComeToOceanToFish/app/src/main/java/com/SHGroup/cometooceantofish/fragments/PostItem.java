package com.SHGroup.cometooceantofish.fragments;

import android.graphics.drawable.Drawable;

public final class PostItem {
    private final Drawable image;
    private final String id, title, content, date, author_nickname;

    public PostItem(Drawable image, String id, String title, String content, String date, String author_nickname) {
        this.image = image;
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.author_nickname = author_nickname;
    }

    public final Drawable getImage(){
        return image;
    }

    public final String getId(){
        return id;
    }

    public final String getTitle(){
        return title;
    }

    public final String getContent(){
        return content;
    }

    public final String getDate(){
        return date;
    }

    public final String getAuthorNickname(){
        return author_nickname;
    }
}
