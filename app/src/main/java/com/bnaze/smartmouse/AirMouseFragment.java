package com.bnaze.smartmouse;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bnaze.smartmouse.networkutils.Message;
import com.bnaze.smartmouse.networkutils.MessageQueue;
import com.bnaze.smartmouse.networkutils.MessageType;

public class AirMouseFragment extends Fragment implements SensorEventListener, View.OnTouchListener {

    //Sensor values for gyroscope
    private SensorManager sensorManager;
    private Sensor sensor;

    //FragmentListener to communicate with MainActivity
    private FragmentListener callback;
    private boolean connected;

    private boolean onPaused;
    private GestureDetector gestureDetector;

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

        onPaused = false;

        gestureDetector = new GestureDetector(getActivity(), new AirMouseGesture());

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_air_mouse, container, false);

        RelativeLayout touchScreen = view.findViewById(R.id.AirMouseTouchScreen);
        touchScreen.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(onPaused == true){
            return;
        }

        connected = callback.ConnectedValue();

        if(connected){
            //Message value must be in json format {"type" : "type", "value" : {"x": x, "y": y}}
            MessageQueue.getInstance().push(Message.newMessage(MessageType.AIR_MOUSE,  "{'x': " + sensorEvent.values[2]*65 + ", 'y': " + sensorEvent.values[0]*65+"}"));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("Fragments", "AirMouse Not visible anymore");
                onPaused = true;
            }
            else{
                Log.d("Fragments", "AirMouse visible.");
                onPaused = false;
            }
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
    public boolean onTouch(View v, MotionEvent event) {
        connected = callback.ConnectedValue();

        if (connected && onPaused == false) {
            gestureDetector.onTouchEvent(event);
        }
        return true;
    }
}


