package com.bnaze.smartmouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;
import com.bnaze.smartmouse.networkutils.Connector;

import java.sql.Connection;

public class AirMouseFragment extends Fragment implements ConnectionCondition, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private FragmentListener callback;
    private boolean connected;

    public AirMouseFragment() {
        // Required empty public constructor
    }

    public static AirMouseFragment newInstance() {
        AirMouseFragment fragment = new AirMouseFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Connector.getInstanceOf().addConnectionCondition(this);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_air_mouse, container, false);
    }

    //Do this
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(connected){
            CommandQueue.getInstance().push(Command.of(CommandType.AIR_MOUSE, MoveValue.of( sensorEvent.values[2]*65, sensorEvent.values[0]*65)));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setAirMouseSelectedListener(FragmentListener callback) {
        this.callback = callback;
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


