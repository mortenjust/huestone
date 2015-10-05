package com.mortenjust.huestone.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mortenjust.huestone.R;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by mortenjust on 10/3/15.
 */

public class LightsAdapter extends ArrayAdapter<PHLight> {
    List<PHLight> lights;
    String TAG = "adaptermj";
    PHBridge mBridge;
    boolean mJustMoved = false;

    static class ViewHolder {
        TextView text;
        SeekBar brightness;
    }

    public LightsAdapter(Context context, List<PHLight> lights, PHBridge bridge){
        super(context, R.layout.light_list_item, lights);

        this.lights = lights;

        this.mBridge = bridge;
    }

    private PHLight getLight(int l){
        return lights.get(l);
    }

    private void setBrightnessForLight(PHLight l, int b){
        PHLightState state = new PHLightState();
        state.setBrightness(b);
        mBridge.updateLightState(l, state);

        l.getLastKnownLightState().setBrightness(b);
        Log.d(TAG, "set brightness "+b+" for light "+l.getName()+" which is now "+l.getLastKnownLightState().getBrightness());
    }


    @Override
    public boolean isEnabled(int position) {
        return true;
    }




    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;


        // Do we need to inflate the layout?
        if(convertView == null){ // yes, this is the first time
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.light_list_item, parent, false);
            holder = new ViewHolder(); // put the references in my object
            holder.text = (TextView) convertView.findViewById(R.id.lightName);
            holder.brightness = (SeekBar) convertView.findViewById(R.id.brightnessSlider);

            if(!lights.get(position).getLastKnownLightState().isOn()){
                holder.brightness.setAlpha(0f);
                holder.brightness.setY(-holder.brightness.getWidth());
            }

            holder.brightness.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 200;
                private long startClickTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "Action! : "+MotionEvent.actionToString(event.getAction()));

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                            if(clickDuration < MAX_CLICK_DURATION && !mJustMoved) {
                                Log.d(TAG, "IT WAS CLICKED, action:" + MotionEvent.actionToString(event.getAction()));
                                Handler handler = new Handler();
                                final SeekBar s = (SeekBar) v;
                                toggleLight(lights.get(position), s);
                                return true; // true = don't send the event further, we took care of business here
                            }
                            mJustMoved = false;
                        }
                    }
                    return false;
                }
            });

            holder.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int prevProgress = seekBar.getProgress();

                    barShouldBeVisible(true, seekBar);
                    Log.d(TAG, "progress changed");
                    PHLight light = lights.get(position);
                    setBrightnessForLight(light, progress);



                    long diff = Math.abs(seekBar.getProgress() - prevProgress);
                    Log.d(TAG, "cur: "+seekBar.getProgress()+" prev: "+prevProgress+"  is "+diff);
                    if(diff < 15){
                        mJustMoved = true;
                    } else {
                        mJustMoved = false;
                    }

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // invalidate timer
                    // start a timer

                }
            });
            convertView.setTag(holder); // save it in the tag

        } else {
            holder = (ViewHolder) convertView.getTag(); // juust get it from the tag
        }

        PHLight light = getLight(position);
        holder.text.setText(light.getName());
        holder.brightness.setProgress(light.getLastKnownLightState().getBrightness());

        holder.brightness.setClickable(false);
        holder.brightness.setFocusable(true);
        holder.brightness.setEnabled(true);

//        attachProgressUpdatedListener(holder.brightness, position);


        //setupSlider(holder.brightness, light);

        return convertView;
    }



    private void barShouldBeVisible(boolean visible, SeekBar bar){
        Interpolator i = new DecelerateInterpolator();
        int SPEED = 800;

        if(visible){
            bar.animate()
                    .alpha(1f)
                    .setDuration(SPEED)
                    .setInterpolator(i)
                    .start();
        } else {
            bar.animate()
                    .alpha(0f)
                    .setDuration(SPEED)
                    .setInterpolator(i)
                    .start();
        }
    }

    private void toggleLight(PHLight light, SeekBar bar){
        PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();

        PHLightState state = light.getLastKnownLightState();

        if (state.isOn()){ // is it on?
            state.setOn(false);
            barShouldBeVisible(false, bar);
        } else { // is it off?
            state.setOn(true);
            barShouldBeVisible(true, bar);
        }

        bridge.updateLightState(light, state, lightListener);
    }

    PHLightListener lightListener = new PHLightListener() {
        @Override
        public void onReceivingLightDetails(PHLight phLight) {

        }

        @Override
        public void onReceivingLights(List<PHBridgeResource> list) {

        }

        @Override
        public void onSearchComplete() {

        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "light success");
        }

        @Override
        public void onError(int i, String s) {
            Log.d(TAG, "light listener error: "+s);
        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }
    };



    private void attachProgressUpdatedListener(SeekBar seekBar, final int position){

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int task_id = (Integer) seekBar.getTag();
//
//                TaskHandler taskHandler = new TaskHandler(DBAdapter
//                        .getDBAdapterInstance(getContext()));
//
//                taskHandler.updateTaskProgress(task_id, progress);
//
//                lights.get(position).setProgress(progress);

                Log.d(TAG, "set progress for position " + position + " to progress " + progress);

                //need to fire an update to the activity
                notifyDataSetChanged();
                seekBar.setEnabled(false);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // empty as onStartTrackingTouch listener not being used in
                // current implementation

            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // empty as onProgressChanged listener not being used in
                // current implementation

            }
        });

    }


}