package com.example.hdt.mymp3.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.hdt.mymp3.R;
import com.example.hdt.mymp3.manager.AudioOfflineManager;
import com.example.hdt.mymp3.model.ItemDataSong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hdt
 */

public class ServiceMediaOffline extends Service {
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_STOP = "STOP";
    private static final String TAG = ServiceMediaOffline.class.getSimpleName();
    private List<ItemDataSong> mOfflineSongs;
    private BroadcastReceiver mReceiver;
    private AudioOfflineManager mManagerPlayer;
    private int currentPosition = -1;
    private NotificationManager mManagerNotification;
    private boolean mIsForce = false;

    @Override
    public void onCreate() {
        super.onCreate();
        queryItemDataSongs();
        mManagerPlayer = new AudioOfflineManager();
        initBroadcastReceiver();
    }

    public void queryItemDataSongs() {
        mOfflineSongs = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        if (null == cursor) {
            return;
        }
        int indexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int indexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int indexData = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String title = cursor.getString(indexTitle);
            String artist = cursor.getString(indexArtist);
            String path = cursor.getString(indexData);
            mOfflineSongs.add(new ItemDataSong(title, artist, false, path));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void initBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_PLAY: {
                        if (mManagerPlayer.isPlaying()) {
                            mManagerPlayer.pause();
                            createNotification(currentPosition, false);
                        } else {
                            mManagerPlayer.play();
                            createNotification(currentPosition, true);
                        }
                    }
                    break;
                    case ACTION_STOP: {
                        destroyNotification(mManagerPlayer.isPlaying());
                    }
                    default:
                        break;
                }
            }
        };
//        Khởi tạo đối tượng để đăng ký lắng nghe sự kiện
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_STOP);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BinderService(this);
    }

    //    ServiceMediaOffline tạo ra BinderService extends Binder
    //    chứ ServiceMediaOffline không implements IBinder
    //    vì IBinder có nhiều pt cần override
    //    trong khi, Binder implements IBinder đã override rồi
    public class BinderService extends Binder {
        private ServiceMediaOffline mService;

        private BinderService(ServiceMediaOffline mService) {
            this.mService = mService;
        }

        public ServiceMediaOffline getmService() {
            return mService;
        }
    }

    public List<ItemDataSong> getmOfflineSongs() {
        return mOfflineSongs;
    }

    public void play(int position) throws IOException {
        mManagerPlayer.release();
        mManagerPlayer.initSong(mOfflineSongs.get(position).getmPath());
        mManagerPlayer.play();
        currentPosition = position;
        createNotification(position, true);
    }

    private void createNotification(int position, boolean isPlay) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_music);
        remoteViews.setTextViewText(R.id.txt_title, mOfflineSongs.get(position).getmTitle());
        remoteViews.setTextViewText(R.id.txt_artist, mOfflineSongs.get(position).getmArtist());
        builder.setCustomContentView(remoteViews);

        RemoteViews remoteViewsBig = new RemoteViews(getPackageName(), R.layout.notification_music_big);
        remoteViewsBig.setTextViewText(R.id.txt_title, mOfflineSongs.get(position).getmTitle());
        remoteViewsBig.setTextViewText(R.id.txt_artist, mOfflineSongs.get(position).getmArtist());
        builder.setCustomBigContentView(remoteViewsBig);

        setOnClickPending(remoteViews, remoteViewsBig);

        mManagerNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (isPlay) {
            remoteViews.setImageViewResource(R.id.btn_pause_play, R.drawable.ic_pause);
            remoteViewsBig.setImageViewResource(R.id.btn_pause_play, R.drawable.ic_pause);

//            Bắt đầu chạy Notification builder.build(), gắn id cho Notification builder.build() là 101
            startForeground(101, builder.build());
//            Tham số thứ nhất: id của Notification, Tham số thứ hai: Notification cần chạy
        } else {
            remoteViews.setImageViewResource(R.id.btn_pause_play, R.drawable.ic_play);
            remoteViewsBig.setImageViewResource(R.id.btn_pause_play, R.drawable.ic_play);

//            Bắt đầu chạy Notification builder.build(), gắn id cho Notification builder.build() là 101
//            Nếu có Notification có cùng id đã chạy trước đó, thì nó sẽ bị tắt và chạy Notification builder.build()
//            Hiểu đơn giản: cập nhật Notification có id 101
            mManagerNotification.notify(101, builder.build());
        }
    }

    private void setOnClickPending(RemoteViews remoteViews, RemoteViews remoteViewsBig) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PLAY);
        PendingIntent pendingPausePlay = PendingIntent.getBroadcast(this, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_pause_play, pendingPausePlay);
        remoteViewsBig.setOnClickPendingIntent(R.id.btn_pause_play, pendingPausePlay);

        PendingIntent pendingClose = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_STOP), 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_close, pendingClose);
        remoteViewsBig.setOnClickPendingIntent(R.id.btn_close, pendingClose);
    }

    private void destroyNotification(boolean isPlay) {
        if (!isPlay) {
            stopForeground(true);//  tắt Notification
            mManagerPlayer.release();//  tắt Nhạc
            mManagerNotification.cancel(101);
            stopSelf();
//            Nếu còn (fragment) activity còn sống thì không được tắt
            if (mIsForce) {
                System.exit(0);// tắt application
            }
        }
    }

    public void setmIsForce(boolean mIsForce) {
        this.mIsForce = mIsForce;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }
}