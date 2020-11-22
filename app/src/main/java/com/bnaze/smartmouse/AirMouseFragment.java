package com.bnaze.smartmouse;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.FloatMath;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AirMouseFragment extends Fragment implements SensorEventListener, View.OnTouchListener {

    //Sensor values for gyroscope
    private SensorManager sensorManager;
    private Sensor sensor;

    //FragmentListener to communicate with MainActivity
    private FragmentListener callback;
    private boolean connected;

    private boolean onPaused;
    private GestureDetector gestureDetector;
    private Sensor sensorLinearAcceleration;
    private Sensor sensorAccelerometer;

    private float accelY;
    private double prevTime;
    private float velY;
    private float distY;
    private float prevVelY;
    private float prevAccelY;
    private float timestamp;
    private static final float NS2S = 1.0f / 1000000000.0f;

    private float[] mGravity;
    private double mAccelLast;
    private double mAccelCurrent;
    private double mAccel;
    private boolean isMoving = false;


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
        sensorLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorLinearAcceleration, SensorManager.SENSOR_DELAY_UI);
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

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (checkIfMoving(sensorEvent) == false) {
                Log.d("moving", "not moving");
                velY = 0;
                prevVelY = 0;
                distY = 0;
                return;
            }
            ;

            if (timestamp == 0) timestamp = sensorEvent.timestamp;
            final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
            timestamp = sensorEvent.timestamp;
            accelY = sensorEvent.values[1];

            velY += accelY * dT;
            distY += prevVelY + velY * dT;
            prevAccelY = accelY;
            prevVelY = velY;

            double senstivitiy = 10;
            Log.d("distance", distY * senstivitiy + " ");
            MessageQueue.getInstance().push(Message.newMessage(MessageType.AIR_MOUSE, "{'x': " + 0 + ", 'y': " + distY * senstivitiy + "}"));

        }

        //Determine if this fragment is being used by the user
        //If not, return
        if (isAdded() && isVisible() && getUserVisibleHint()) {
            onPaused = false;
        } else {
            onPaused = true;
            return;
        }

        if (onPaused == true) {
            return;
        }

        connected = callback.ConnectedValue();
        if (connected) {
            //Message value must be in json format {"type" : "type", "value" : {"x": x, "y": y}}
            MessageQueue.getInstance().push(Message.newMessage(MessageType.AIR_MOUSE, "{'x': " + sensorEvent.values[2] * 65 + ", 'y': " + 0 + "}"));
        }
    }


    private boolean checkIfMoving(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float diff = (float) Math.sqrt(x * x + y * y + z * z);
        if (diff > 0.5) { // 0.5 is a threshold, you can test it and change it
            return true;
        }
        return false;
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
            } else {
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


