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
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {

    EditText sineText;
    EditText sawText;
    EditText sqrText;

    SeekBar sineSlider;
    SeekBar sawSlider;
    SeekBar sqrSlider;

    SeekBar sawVolSlider;

    EditText sawVol;

    Button b_long;
    Button b_short;
    Button b_pattern;
    Vibrator vibrator;

    Button b_activity;

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
        initSine();
        initSaw();
        initSqr();
        initVolSaw();
        initVib();
        initActivity();

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

    public void openActivity2(){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }

    public int sine_progress_val;
    public int saw_progress_val;
    public int sqr_progress_val;

    public int saw_vol_progress_val;

    private void initVib(){
        b_long = (Button) findViewById(R.id.b_long);
        b_short = (Button) findViewById(R.id.b_short);
        b_pattern = (Button) findViewById(R.id.b_pattern);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        b_long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vibrate in ms
                vibrator.vibrate(500);

            }
        });

        b_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vibrate in ms
                vibrator.vibrate(50);

            }
        });

        b_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 0 : Start without a delay
                // 400 : Vibrate for 400 milliseconds
                // 200 : Pause for 200 milliseconds
                // 400 : Vibrate for 400 milliseconds
                long[] mVibratePattern = new long[]{0, 400, 200, 400};

                // -1 : Do not repeat this pattern
                // pass 0 if you want to repeat this pattern from 0th index
                vibrator.vibrate(mVibratePattern, -1);
            }
        });
    }

    private void initSine(){
        sineText = (EditText) findViewById(R.id.sineNum);
        sineSlider = (SeekBar) findViewById(R.id.sineSlider);

        //final Switch onOffSwitch = findViewById(R.id.onOffSwitch);

        sineSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb
                        sineText.setText(String.valueOf(progress));
                        sine_progress_val = progress;
                        PdBase.sendFloat("sinefreqNum", sine_progress_val);
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

    private void initSqr(){
        sqrText = (EditText) findViewById(R.id.sqrNum);
        sqrSlider = (SeekBar) findViewById(R.id.sqrSlider);

        //final Switch onOffSwitch = findViewById(R.id.onOffSwitch);
        sqrSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb
                        sqrText.setText(String.valueOf(progress));
                        sqr_progress_val = progress;
                        PdBase.sendFloat("sqrfreqNum", sqr_progress_val);
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

    //toggle button to control sine on/off:
    public void sineState(View view) {
        boolean checked = ((ToggleButton)view).isChecked();
        if (checked) {
            PdBase.sendFloat("sineonOff", 1.0f);
            PdBase.sendFloat("sinefreqNum", sine_progress_val);
        } else {
            PdBase.sendFloat("sineonOff", 0.0f);
        }
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

    //toggle button to control sqr on/off:
    public void sqrState(View view) {
        boolean checked = ((ToggleButton)view).isChecked();
        if (checked) {
            PdBase.sendFloat("sqronOff", 1.0f);
            PdBase.sendFloat("sqrfreqNum", sqr_progress_val);
        } else {
            PdBase.sendFloat("sqronOff", 0.0f);
        }
    }
}
