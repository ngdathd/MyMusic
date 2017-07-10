package com.example.hdt.mymp3.manager;

import android.media.MediaPlayer;

import java.io.IOException;

public class AudioOfflineManager {
    private MediaPlayer mPlay;

    public AudioOfflineManager() {
        mPlay = new MediaPlayer();
    }

    public void initSong(String path) throws IOException {
        try {
            mPlay.setDataSource(path);
        } catch (IllegalStateException e) {
            mPlay = new MediaPlayer();
            mPlay.setDataSource(path);
        }
        mPlay.prepare();
    }

    public void release() {
//        Giải phóng đối tượng mPlay: gán mPlay = null => tắt nhạc
        mPlay.release();
    }

    public void play() {
        if (!mPlay.isPlaying()) {
            mPlay.start();
        }
    }

    public boolean isPlaying() {
        return mPlay.isPlaying();
    }

    public void pause() {
        if (mPlay.isPlaying()) {
            mPlay.pause();
        }
    }
}