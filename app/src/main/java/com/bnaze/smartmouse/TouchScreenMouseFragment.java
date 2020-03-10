package com.bnaze.smartmouse;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TouchScreenMouseFragment extends Fragment {

    private FragmentListener callback;
    private boolean connected;

    public TouchScreenMouseFragment() {
        // Required empty public constructor
    }

    public void setTouchScreenMouseListener(FragmentListener callback){
        this.callback = callback;
    }

    public static TouchScreenMouseFragment newInstance() {
        TouchScreenMouseFragment fragment = new TouchScreenMouseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_touch_screen_mouse, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
