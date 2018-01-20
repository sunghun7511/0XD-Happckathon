package com.SHGroup.cometooceantofish.fragments;

import android.graphics.drawable.Drawable;

public final class LendItem {
    private final Drawable image;
    private final String id, title, description;

    public LendItem(Drawable image, String id, String title, String description) {
        this.image = image;
        this.id = id;
        this.title = title;
        this.description = description;
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

    public final String getDescription(){
        return description;
    }
}
