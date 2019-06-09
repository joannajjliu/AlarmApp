package com.example.hellopd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText freqText;
    SeekBar freqSlider;

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

        initGUI();
    }

    public int progress_val;

    private void initGUI(){
        freqText = (EditText) findViewById(R.id.freqNum);
        freqSlider = (SeekBar) findViewById(R.id.freqSlider);

        //final Switch onOffSwitch = findViewById(R.id.onOffSwitch);

        freqSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb

                        freqText.setText(String.valueOf(progress));
                        progress_val = progress;
                        PdBase.sendFloat("freqNum", progress_val);
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
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.simplepatch), dir, true);
            Log.i("unzipping", dir.getAbsolutePath());
        } catch (IOException e) {
            Log.i("unzipping", "error unzipping");
        }
        File pdPatch = new File(dir, "simplepatch.pd");
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

    //toggle button to control on/off frequency:
    public void changeState(View view) {
        boolean checked = ((ToggleButton)view).isChecked();
        if (checked) {
            PdBase.sendFloat("onOff", 1.0f);
            PdBase.sendFloat("freqNum", progress_val);
        } else {
            PdBase.sendFloat("onOff", 0.0f);
        }
    }
}
