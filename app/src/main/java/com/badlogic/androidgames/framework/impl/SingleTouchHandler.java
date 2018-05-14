package com.badlogic.androidgames.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.badlogic.androidgames.framework.Pool;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.Pool.PoolObjectFactory;

public class SingleTouchHandler implements TouchHandler {
    private boolean isTouched;
    private int touchX;
    private int touchY;
    private final Pool<TouchEvent> touchEventPool;
    private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    private List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    private final float scaleX;
    private final float scaleY;
    private static final int MAXPOOLSIZE = 100;
    
    public SingleTouchHandler(View view, float scaleX, float scaleY) {
        touchEventPool = new Pool<TouchEvent>(new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }
        }, MAXPOOLSIZE);
        view.setOnTouchListener(this);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchEvent.type = TouchEvent.TOUCH_DOWN;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                    isTouched = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    touchEvent.type = TouchEvent.TOUCH_UP;
                    isTouched = false;
                    break;
            }
            
            touchEvent.x = touchX = (int)(event.getX() * scaleX);
            touchEvent.y = touchY = (int)(event.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);                        
            
            return true;
        }
    }

    @Override
    public boolean isTouchDown(int pointer) {
        synchronized(this) {
            if(pointer == 0)
                return isTouched;
            else
                return false;
        }
    }

    @Override
    public int getTouchX(int pointer) {
        synchronized(this) {
            return touchX;
        }
    }

    @Override
    public int getTouchY(int pointer) {
        synchronized(this) {
            return touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized(this) {     
            int len = touchEvents.size();
            for(int i=0; i<len; i++)
                touchEventPool.free(touchEvents.get(i));
            List<TouchEvent> tmp = touchEventsBuffer;
            touchEvents.clear();
            touchEventsBuffer = touchEvents;
            touchEvents = tmp;
            return touchEvents;
        }
    }
}