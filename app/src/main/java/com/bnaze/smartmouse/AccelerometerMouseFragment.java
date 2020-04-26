package com.bnaze.smartmouse;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;
import com.bnaze.smartmouse.networkutils.Connector;

public class AccelerometerMouseFragment extends Fragment {

    private FragmentListener callback;
    private boolean connected;
    private boolean onPaused;

    public AccelerometerMouseFragment() {
        // Required empty public constructor
    }

    public static AccelerometerMouseFragment newInstance() {
        AccelerometerMouseFragment fragment = new AccelerometerMouseFragment();
        return fragment;
    }

    public void setAccelerometerMouseListener(FragmentListener callback) {
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
        return inflater.inflate(R.layout.fragment_accelerometer_mouse, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("Fragments", "Acc Not visible anymore.");
                // TODO stop audio playback
                onPaused = true;
            }
            else{
                Log.d("Fragments", "Acc visible.");
                onPaused = false;
            }
        }
    }


    /*
    @Override
    public void onPause() {
        Log.e("states", "OnPause of AccMouse");
        onPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e("states", "OnResume of AccMouse");
        onPaused = false;
        super.onResume();
    }
     */
}
