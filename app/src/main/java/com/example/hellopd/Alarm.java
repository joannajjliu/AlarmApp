package com.example.hellopd;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TimePicker;

import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.view.View;
import android.widget.ToggleButton;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.utils.IoUtils;

public class Alarm extends AppCompatActivity {

    TimePicker alarmTime;
    TextClock currentTime;

    //use Saw wave as alarm:
    EditText sawText;
    SeekBar sawSlider;

    public int saw_progress_val;

    public int alarm_freq = 19;
    public int alarm_vol = 17;

    public int saw_vol_progress_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        try{
            initPD();
            loadPDPatch();
        }
        catch (IOException e) {
            Log.i("onCreate", "initialization and loading gone wrong :(");
            finish();
        }

        alarmTime = findViewById(R.id.timePicker);
        currentTime = findViewById(R.id.textClock);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentTime.getText().toString().equals(AlarmTime())) {
                    //r.play();
                    PdBase.sendFloat("alrmfreqNum", alarm_freq);
                    PdBase.sendFloat("alrmvolNum", alarm_vol);
                } else {
                    //r.stop();
                }

            }
        }, 0, 1000); //period in milli-seconds

        initSaw();
    }


    private void initSaw(){
        //sawText = (EditText) findViewById(R.id.sawNum);
        sawSlider = (SeekBar) findViewById(R.id.snoozeVol);

        sawSlider.setOnSeekBarChangeListener( //control snooze "volume"
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // updated continuously as the user slides the thumb
                        //sawText.setText(String.valueOf(progress));
                        alarm_vol = progress;
                        PdBase.sendFloat("alrmvolNum", alarm_vol);
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
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.alarm_saw_wave), dir, true);
            Log.i("unzipping", dir.getAbsolutePath());
        } catch (IOException e) {
            Log.i("unzipping", "error unzipping");
        }
        File pdPatch = new File(dir, "alarm_saw_wave.pd");
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
            PdBase.sendFloat("alrmonOff", 1.0f);
            PdBase.sendFloat("alrmfreqNum", alarm_freq);
            PdBase.sendFloat("sawvolNum", saw_vol_progress_val);
        } else {
            PdBase.sendFloat("alrmonOff", 0.0f);
        }
    }



    public String AlarmTime(){
        Integer alarmHours = alarmTime.getCurrentHour(); //24 hours format
        Integer alarmMinutes = alarmTime.getCurrentMinute();

        String stringAlarmMinutes;

        if (alarmMinutes < 10) {
            stringAlarmMinutes = "0";
            stringAlarmMinutes = stringAlarmMinutes.concat(alarmMinutes.toString());
        } else {
            stringAlarmMinutes = alarmMinutes.toString();
        }

        String stringAlarmTime;

        if(alarmHours > 12){ //PM
            alarmHours = alarmHours - 12;
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" PM");

        } else { //AM
            stringAlarmTime = alarmHours.toString().concat(":").concat(stringAlarmMinutes).concat(" AM");
        }

        return stringAlarmTime;
    }
}
