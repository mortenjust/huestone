package com.mortenjust.huestone.controller;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
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


/**
 * MainActivity - The starting point for creating your own Hue App.
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MainActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    LightsListView lightsList;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        phHueSDK = PHHueSDK.create();
        setupViews();
        setupListeners();
        listAllLights();
    }

    void setupViews(){
        lightsList = (LightsListView) findViewById(R.id.lightList);

    }
    void setupListeners(){}

    void listAllLights(){
        PHBridge bridge = phHueSDK.getSelectedBridge();
        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        final List<PHLight> reachableLights = new ArrayList<>();

        for(PHLight l : allLights){
            if(l.getLastKnownLightState().isReachable()){
                reachableLights.add(l);
            }
        }

        lightsList.setAdapter(new LightsAdapter(this, reachableLights, bridge));


        lightsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SeekBar bar = (SeekBar) view.findViewById(R.id.brightnessSlider);
                bar.setFocusable(false);
                bar.setEnabled(false);

                lightsList.resetListState();
                //toggleLight(reachableLights.get(position), bar);
                ((MJLight) reachableLights.get(position)).toggleLightWithSeekBar(bar);
            }
        });

        lightsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SeekBar bar = (SeekBar) view.findViewById(R.id.brightnessSlider);
                TextView label = (TextView) view.findViewById(R.id.lightName);
                label.animate()
                        .alpha(0)
                        .yBy(10)
                        .setDuration(300)
                        .start();
                bar.setFocusable(true);
                bar.setEnabled(true);

                return false;
            }
        });


    }

    void animateProgressViewOnly(final SeekBar bar, int to){
        final int originalValue = bar.getProgress(); // save
        ObjectAnimator a = ObjectAnimator.ofInt(bar, "progress", to); // perform illusion
        a.setDuration(300)
                .start();

        a.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bar.setProgress(originalValue); // restore
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });



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


    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
}
