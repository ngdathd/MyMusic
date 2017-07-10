package com.example.hdt.mymp3.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hdt.mymp3.R;
import com.example.hdt.mymp3.adapter.RcOfflineSongAdapter;
import com.example.hdt.mymp3.service.ServiceMediaOffline;

import java.io.IOException;

@SuppressLint("ValidFragment")
public class OfflineFragment extends Fragment implements RcOfflineSongAdapter.ISongAdapter {
    private static final String TAG = OfflineFragment.class.getSimpleName();
    private Context mContext;
    private View mView;
    private RcOfflineSongAdapter mRcOfflineSongAdapter;
    private ServiceConnection mConnection;
    private ServiceMediaOffline mService;

    @SuppressLint("InflateParams")
    public OfflineFragment(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mView = layoutInflater.inflate(R.layout.fragment_offline, null);
        mRcOfflineSongAdapter = new RcOfflineSongAdapter(context, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService();
        requestService();
    }

    private void startService() {
//        khởi động Service thông qua intent
        Intent intent = new Intent(mContext, ServiceMediaOffline.class);
        mContext.startService(intent); // Activity start 1 service
    }

    private void requestService() {
//        Tạo đường kết lối giữa UI và Service
        mConnection = new ServiceConnection() {
            @Override
            // UI nhận dữ liệu mà Service trả về, khi UI và Service đã kết nối thành công (Bước 2)
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected: ");
                ServiceMediaOffline.BinderService binderService =
                        (ServiceMediaOffline.BinderService) service;
                mService = binderService.getmService();
                // Nhận List Songs mà mService đọc được
                mRcOfflineSongAdapter.setmOfflineSongs(mService.getmOfflineSongs());
            }

            // UI gửi yêu cầu tới Service, khi UI và Service đã ngắt kết nối thành công (Bước 3)
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected: ");
            }
        };

//        Tạo intent đi từ UI đến Service
        Intent intent = new Intent(mContext, ServiceMediaOffline.class);

//        Gọi đến Service thông qua intent (Bước 1)
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // Activity liên kết mContext(bản chất là MainActivity) với một Service đã start trước đó
        // nếu Service này chưa được start thì Activity sẽ đợi Service được start rồi liên kết
        //
        // tham số thứ nhất Intent: xác định service được bindService
        // tham số thứ hai ServiceConnection: nơi nhận thông điệp mà Service gửi lại Activity
        // tham số thứ ba flags: kiểu xử lý khi Service được kết nối chưa được start
        // BIND_AUTO_CREATE: nếu Service chưa được start thì Activity start Service ngay
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initControls();
        return mView;
    }

    private void initControls() {
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.rc_offline_song);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mRcOfflineSongAdapter);
    }

    @Override
    public void onDestroy() {
        mService.setmIsForce(true);
        mContext.unbindService(mConnection);
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onClickItem(int position) {
        try {
            mService.play(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}