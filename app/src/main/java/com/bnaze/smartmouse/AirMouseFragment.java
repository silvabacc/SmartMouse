package com.bnaze.smartmouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bnaze.smartmouse.networkutils.ConnectionCondition;
import com.bnaze.smartmouse.networkutils.Connector;
import com.bnaze.smartmouse.networkutils.Message;
import com.bnaze.smartmouse.networkutils.MessageQueue;
import com.bnaze.smartmouse.networkutils.MessageType;
import com.google.android.material.appbar.AppBarLayout;

public class AirMouseFragment extends Fragment implements ConnectionCondition, SensorEventListener, GestureDetector.OnGestureListener {

    //Sensor values for gyroscope
    private SensorManager sensorManager;
    private Sensor sensor;

    //FragmentListener to communicate with MainActivity
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

        Connector.getInstance().addConnectionCondition(this);

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
            //Message value must be in json format {"type" : "type", "value" : {"x": x, "y": y}}
            MessageQueue.getInstance().push(Message.newMessage(MessageType.AIR_MOUSE,  "{'x': " + sensorEvent.values[2]*65 + ", 'y': " + sensorEvent.values[0]*65+"}"));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Method called in MainActivity
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

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("onLongPressed","LongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}


