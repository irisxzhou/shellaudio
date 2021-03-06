package org.shellaudio.shellaudio;

import android.annotation.SuppressLint;
import android.app.backup.FullBackupDataOutput;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;

import java.util.HashMap;
import java.util.Random;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayGameActivity extends AppCompatActivity {
    int curr = 0;
    HashMap<String,Integer> semitones = new HashMap<String, Integer>();

    final static String roundInfo = "org.shellaudio.shellaudio.EXTRA_ROUNDNUM";

    public void cont(View view) {
        Intent intent = new Intent(this, PlayGameActivity.class);
        intent.putExtra(FullscreenActivity.roundInfo, Integer.toString(curr));
        startActivity(intent);
    }

    public void back(View view) {
        Intent intent = new Intent(this, PlayGameActivity.class);
        intent.putExtra(FullscreenActivity.roundInfo, "0");
        startActivity(intent);
    }
    public void nextScreenWin(View view) {
        Intent intent = new Intent(this, WinScreenActivity.class);
        //intent.putExtra(roundInfo, Integer.toString(curr + 1));
        startActivity(intent);
    }

    public void nextScreenLose(View view) {
        Intent intent = new Intent(this, LoseScreenActivity.class);
        //intent.putExtra(roundInfo, Integer.toString(curr));
        startActivity(intent);
    }
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            /*if (actionBar != null) {
                actionBar.show();
            }*/
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible = false;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_game);

        //deal with intent things
        Intent intent = getIntent();
        curr = Integer.parseInt(intent.getStringExtra(FullscreenActivity.roundInfo));


        TextView levelView = findViewById(R.id.score_counter);
        levelView.setText(Integer.toString(curr));

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        final int correct = makeSounds();

        semitones.put("Perfect 1", 0);
        semitones.put("Diminished 2", 0);
        semitones.put("Minor 2", 1);
        semitones.put("Augmented 1", 1);
        semitones.put("Major 2", 2);
        semitones.put("Diminished 3", 2);
        semitones.put("Minor 3", 3);
        semitones.put("Augmented 2", 3);
        semitones.put("Major 3", 4);
        semitones.put("Diminished 4", 4);
        semitones.put("Perfect 4", 5);
        semitones.put("Augmented 3", 5);
        semitones.put("Diminished 5", 6);
        semitones.put("Augmented 4", 6);
        semitones.put("Perfect 5", 7);
        semitones.put("Diminished 6", 7);
        semitones.put("Minor 6", 8);
        semitones.put("Augmented 5", 8);
        semitones.put("Major 6", 9);
        semitones.put("Diminished 7", 9);
        semitones.put("Minor 7", 10);
        semitones.put("Augmented 6", 10);
        semitones.put("Major 7", 11);
        semitones.put("Diminished 8", 11);
        semitones.put("Perfect 8", 12);
        semitones.put("Augmented 7", 12);

        Button submit = this.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = findViewById(R.id.modSpinner);
                String attribute = spinner.getSelectedItem().toString();

                Log.e("test", attribute);

                BubbleSeekBar intervalBar = findViewById(R.id.bubbleSeekBar);
                int interval = intervalBar.getProgress();

                Log.e("intervalTest", Integer.toString(interval));

                attribute += " ";
                attribute += interval;

                Log.e("attributeTest", attribute);

                int result = collectAnswer(attribute);
                if(result == correct){
                    curr = curr + 1;
                    cont(v);
                } else {
                    back(v);
                }
            }
        });
    }

    private int collectAnswer(String s) {

        /* collect two inputs from spinner and slider, combine into string
         then search in hashmap to find semitone distance and return
         */

        if (semitones.containsKey(s) == true) {
            return semitones.get(s);
        }
        else {
            return 13;
        }
    }

    // returns semitone difference between two notes
    private int makeSounds(){
        // Even tempered for now, feel free to change later
        // Ranges from A3 to C5 (inclusive)
        double[] notes = new double[] {220.0,233.1,246.9,261.6,277.2,
                293.7,311.1,329.6,349.2,370.0,392.0,415.3,440.0,466.2,
                493.9, 523.3};


        Random generator = new Random();
        int noteA = generator.nextInt(notes.length);
        int noteB; // must be within an octave of first note, or 12 semitones
        do {
            noteB = generator.nextInt(notes.length);
        }while (Math.abs(noteA - noteB) > 12);

        final double a = notes[noteA];
        final double b = notes[noteB];

        final int duration =  22050; // half a second

        //if button a is pushed
        Button buttonA = this.findViewById(R.id.button_a);
        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(a, duration);
            }
        });

        //if button b is pushed
        Button buttonB = this.findViewById(R.id.button_b);
        buttonB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                playSound(b, duration);
            }
        });
        return Math.abs(noteA - noteB);
    }

    // Not sure exactly how this works but it does
    private void playSound(double frequency, int duration) {
        // AudioTrack definition
        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        double[] mSound = new double[duration];
        short[] mBuffer = new short[duration];
        for (int i = 0; i < mSound.length; i++) {
            mSound[i] = Math.sin((2.0*Math.PI * i/(44100/frequency)));
            mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
        }

        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        mAudioTrack.play();

        mAudioTrack.write(mBuffer, 0, mSound.length);
        mAudioTrack.stop();
        mAudioTrack.release();

    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION); */
        mVisible = false;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


}
