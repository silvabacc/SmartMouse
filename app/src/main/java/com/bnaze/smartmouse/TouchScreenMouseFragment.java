package com.bnaze.smartmouse;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;

public class TouchScreenMouseFragment extends Fragment implements ConnectionCondition {

    private FragmentListener callback;
    private boolean connected;
    private boolean onPaused;

    public TouchScreenMouseFragment() {
        // Required empty public constructor
    }

    public static TouchScreenMouseFragment newInstance() {
        TouchScreenMouseFragment fragment = new TouchScreenMouseFragment();
        return fragment;
    }

    public void setTouchScreenMouseListener(FragmentListener callback){
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPaused = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_touch_screen_mouse, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("Fragments", "TouchScreen Not visible anymore");
                // TODO stop audio playback
                onPaused = true;
            }
            else{
                Log.d("Fragments", "TouchScreen visible.");
                onPaused = false;
            }
        }
    }

    @Override
    public void onConnected() {
        connected = true;
        callback.ConnectedValue(true);
        final String msg = "Connection established with host machine";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDisconnected() {
        connected = false;
        callback.ConnectedValue(false);
        final String msg = "Connection disrupted";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        connected = false;
        callback.ConnectedValue(false);
        final String msg = "Could not establish connection with host machine";
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    @Override
    public void onPause() {
        Log.e("states", "OnPause of TouchScreen");
        onPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e("states", "OnResume of TouchScreen");
        onPaused = false;
        super.onResume();
    }
     */
}
