package com.bnaze.smartmouse;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class TouchScreenMouseFragment extends Fragment implements View.OnTouchListener {

    private FragmentListener callback;
    private boolean connected;
    private boolean onPaused;

    private GestureDetector detector;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_touch_screen_mouse, container, false);

        RelativeLayout touchScreen = view.findViewById(R.id.TouchScreenRelativeLayout);
        touchScreen.setOnTouchListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPaused = false;

        detector = new GestureDetector(getActivity(), new TouchScreenGesture());

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        connected = callback.ConnectedValue();

        if (connected && onPaused == false) {
            detector.onTouchEvent(event);
        }
        return true;
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
}
