package com.example.hdt.mymp3.model;

/**
 * Created by hdt
 */

public class ItemDataSong {
    private String mTitle;
    private String mArtist;
    private boolean mIsFavorite;
    private String mPath;

    public ItemDataSong(String name, String author, boolean isFavorite, String path) {
        mTitle = name;
        mArtist = author;
        mIsFavorite = isFavorite;
        mPath = path;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public boolean ismIsFavorite() {
        return mIsFavorite;
    }

    public String getmPath() {
        return mPath;
    }
}