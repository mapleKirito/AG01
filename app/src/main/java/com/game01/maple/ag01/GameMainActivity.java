package com.game01.maple.ag01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.game01.maple.ag01.config.SystemParams;
import com.game01.maple.ag01.utils.Pedometer;

import java.util.Timer;
import java.util.TimerTask;

public class GameMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Pedometer pedometer;
    private TextView tv;
    private float lastStepCount;
    private float stepCount;
    private TextView tvD;
    private ViewPropertyAnimator mStepEventAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        pedometer = new Pedometer(this);
        tv =(TextView)findViewById(R.id.tv);
        tvD =(TextView)findViewById(R.id.TextViewDetected);

        TimerTask task = new TimerTask() {


            @Override
            public void run() {
                tv.post(new Runnable() {

                    @Override
                    public void run() {
                        if(lastStepCount<=0){
                            lastStepCount=pedometer.getStepCount();
                            SystemParams.getInstance().setFloat("lastStepCount",lastStepCount);
                        }
                        tv.setText((pedometer.getStepCount()-lastStepCount)+"");
                        tv.postInvalidate();


                    }
                });

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 100,1000);


        Button button = (Button) findViewById(R.id.saveStep);
        button.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        pedometer.register();
        stepCount=SystemParams.getInstance().getFloat("stepCount",0);
        tvD.setText(""+stepCount);
        lastStepCount=SystemParams.getInstance().getFloat("lastStepCount",pedometer.getStepCount());


    }

    @Override
    protected void onStop() {
        super.onStop();
        pedometer.unRegister();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveStep:
                float tmpStepCount=pedometer.getStepCount()-lastStepCount;
                stepCount+=tmpStepCount;
                SystemParams.getInstance().setFloat("stepCount",stepCount);
                lastStepCount=pedometer.getStepCount();
                SystemParams.getInstance().setFloat("lastStepCount",lastStepCount);
                tvD.setText(""+stepCount);
                Toast.makeText(this,"领取了"+tmpStepCount+"步",Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
