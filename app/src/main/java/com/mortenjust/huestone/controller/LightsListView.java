package com.mortenjust.huestone.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mortenjust.huestone.R;

/**
 * Created by mortenjust on 10/4/15.
 */
public class LightsListView extends ListView {

    public LightsListView(Context c){
        super(c);
    }

    public LightsListView(Context c, AttributeSet a){
        super(c, a);
    }
    public LightsListView(Context c, AttributeSet a, int def){
        super(c, a, def);
    }

    public LightsListView(Context c, AttributeSet a, int def, int defRes){
        super(c, a, def, defRes);

    }


    public void resetListState(){
        View v;
        for(int i = 0;i < this.getCount() ; i++ ){
            v = this.getChildAt(i);
            TextView label = (TextView) v.findViewById(R.id.lightName);
            SeekBar bar = (SeekBar) v.findViewById(R.id.brightnessSlider);

            if(label.getAlpha()==0){ // then it's disabled, so let's enable it
                label.animate()
                        .alpha(0.9f)
                        .yBy(-10)
                        .setDuration(300)
                        .start();
            }
            //bar.setClickable(false);
            bar.setFocusable(true);
            bar.setEnabled(true);
        }
    }



}
