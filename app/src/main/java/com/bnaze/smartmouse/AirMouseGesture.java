package com.bnaze.smartmouse;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.bnaze.smartmouse.networkutils.Message;
import com.bnaze.smartmouse.networkutils.MessageQueue;
import com.bnaze.smartmouse.networkutils.MessageType;

public class AirMouseGesture extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d("SingleTap", "SingleTap");
        MessageQueue.getInstance().push(Message.newMessage(MessageType.MOUSE_LEFT_CLICK, null));
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        MessageQueue.getInstance().push(Message.newMessage(MessageType.MOUSE_DOUBLE_CLICK, null));
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        MessageQueue.getInstance().push(Message.newMessage(MessageType.MOUSE_SCROLL, "{'x': " + distanceX*0.1 + ", 'y': " +distanceY*0.1 +"}"));
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        MessageQueue.getInstance().push(Message.newMessage(MessageType.MOUSE_RIGHT_CLICK, null));
    }
}
