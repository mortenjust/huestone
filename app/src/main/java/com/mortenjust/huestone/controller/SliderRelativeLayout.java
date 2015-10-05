package com.mortenjust.huestone.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by mortenjust on 10/4/15.
 */

public class SliderRelativeLayout extends RelativeLayout {
    boolean mIsScrolling = false;

    public SliderRelativeLayout(Context context) {
        super(context);
        init();
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SliderRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {

    }





}

