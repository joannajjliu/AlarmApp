package com.example.hellopd;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextClock currentTime2;
    EditText alarmTime;

    EditText sawText;

    SeekBar sawSlider;

    SeekBar sawVolSlider;
    EditText sawVol;

    Button b_activity;

    Button alarm_activity;

    public int alarm_freq = 19;
    public int alarm_vol = 30; //initial volume
    public int alrm_off = 0; //turns to 1 when off

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            initPD();
            loadPDPatch();
        }
        catch (IOException e) {
            Log.i("onCreate", "initialization and loading gone wrong :(");
            finish();
        }

        alarmTime = findViewById(R.id.editAlarm);
        currentTime2 = findViewById(R.id.textClock2);

        Log.d("alrmtime2", alarmTime.getText().toString());
        Log.d("currtime2", currentTime2.getText().toString());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentTime2.getText().toString().equals(alarmTime.getText().toString())) {
                    //change to alarm activity:
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000); //period in milli-seconds

        initSaw();
        initVolSaw();
        initActivity();
        initAlarmActivity();
    }

    private void initActivity(){
        b_activity = (Button) findViewById(R.id.b_activity);
        b_activity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openActivity2();
            }
        });
    }

    private void initAlarmActivity(){
        alarm_activity = (Button) findViewById(R.id.alarm_activity);
        alarm_activity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openAlarmActivity();
            }
        });
    }

    public void openActivity2(){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public void openAlarmActivity(){
        Intent intent = new Intent(this, Alarm.class);
        startActivity(intent);
    }

    public int saw_progress_val;

    public int saw_vol_progress_val;


    private void initVolSaw(){

        sawVolSlider = (SeekBar) findViewById(R.id.sawVolSlider);
        sawVol = (EditText) findViewById(R.id.sawVol);

        sawVolSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb
                        sawVol.setText(String.valueOf(progress));
                        saw_vol_progress_val = progress;
                        PdBase.sendFloat("sawvolNum", saw_vol_progress_val);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // called when the user first touches the SeekBar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // called after the user finishes moving the SeekBar
                    }
                }
        );
    }

    private void initSaw(){
        sawText = (EditText) findViewById(R.id.sawNum);
        sawSlider = (SeekBar) findViewById(R.id.sawSlider);

        sawSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb
                        sawText.setText(String.valueOf(progress));
                        saw_progress_val = progress;
                        PdBase.sendFloat("sawfreqNum", saw_progress_val);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // called when the user first touches the SeekBar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // called after the user finishes moving the SeekBar
                    }
                }
        );
    }

    private void loadPDPatch(){
        File dir = getFilesDir();
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.test_all_waves), dir, true);
            Log.i("unzipping", dir.getAbsolutePath());
        } catch (IOException e) {
            Log.i("unzipping", "error unzipping");
        }
        File pdPatch = new File(dir, "test_all_waves.pd");
        try {
            PdBase.openPatch(pdPatch.getAbsolutePath());
        } catch (IOException e) {
            Log.i("opening patch", "error opening patch");
            Log.i("opening patch", e.toString());
        }
        
        int i = 0;
    }

    private PdUiDispatcher dispatcher;

    private void initPD() throws IOException {
        int samplerate = AudioParameters.suggestSampleRate();
        PdAudio.initAudio(samplerate, 0 , 2, 8, true);

        dispatcher = new PdUiDispatcher();
        PdBase.setReceiver(dispatcher);

    }

    @Override
    protected void onResume(){
        super.onResume();
        PdAudio.startAudio(this);

    }

    @Override
    protected void onPause(){
        super.onPause();
        PdAudio.stopAudio();
    }

    //toggle button to control saw on/off:
    public void sawState(View view) {
        boolean checked = ((ToggleButton)view).isChecked();
        if (checked) {
            PdBase.sendFloat("sawonOff", 1.0f);
            PdBase.sendFloat("sawfreqNum", saw_progress_val);
            PdBase.sendFloat("sawvolNum", saw_vol_progress_val);
        } else {
            PdBase.sendFloat("sawonOff", 0.0f);
        }
    }

}
