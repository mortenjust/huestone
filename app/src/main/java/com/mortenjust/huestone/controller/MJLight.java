package com.mortenjust.huestone.controller;

import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.SeekBar;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.List;
import java.util.Map;

/**
 * Created by mortenjust on 10/4/15.
 */
public class MJLight extends PHLight {
    String TAG = "mj.mjlight";

    public MJLight(String name, String identifier, String versionNumber, String modelNumber){
        super(name, identifier, versionNumber, modelNumber);
    }

    public MJLight(PHLight light){
        super(light);
    }


    private void barShouldBeVisible(boolean visible, SeekBar bar){
        Interpolator i = new DecelerateInterpolator();
        int SPEED = 800;

        if(visible){
            bar.animate()
                    .alpha(1f)
                    .setDuration(SPEED)
                    .setInterpolator(i)
                    .x(0)
                    .start();
        } else {
            bar.animate()
                    .alpha(0f)
                    .setDuration(SPEED)
                    .setInterpolator(i)
                    .x(-bar.getWidth())
                    .start();
        }
    }

    public void toggleLightWithSeekBar(SeekBar bar){
        PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();

        PHLightState state = getLastKnownLightState();

        Log.d(TAG, "tooglelight and state.ison: "+state.isOn());

        if (state.isOn()){ // is it on?
            state.setOn(false);
            barShouldBeVisible(false, bar);
        } else { // is it off?
            state.setOn(true);
            barShouldBeVisible(true, bar);
        }

        bridge.updateLightState(this, state, lightListener);
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

}
