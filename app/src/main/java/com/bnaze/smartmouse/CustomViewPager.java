package com.bnaze.smartmouse;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean enabled;


    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //This variable is used to unlock/lock scrolling for the viewpager
        this.enabled = false;
    }

    //To stop scrolling
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    //to avoid interrupted movements
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }


}