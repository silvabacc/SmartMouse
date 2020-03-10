package com.bnaze.smartmouse;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;
import com.bnaze.smartmouse.networkutils.Connector;

public class AccelerometerMouseFragment extends Fragment implements ConnectionCondition {

    private FragmentListener callback;
    private boolean connected;

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

        Connector.getInstanceOf().addConnectionCondition(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accelerometer_mouse, container, false);
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
}
