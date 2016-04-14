package bello.andrea.audioplayer;

import android.app.Activity;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final int FORWARD_TIME = 5000;
    private static final int BACKWARD_TIME = 5000;

    private SeekBar seekbar;

    private MediaPlayer mediaPlayer;

    private int currentPosition;

    private Toast toast;

    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonBack =(Button)findViewById(R.id.button_back);
        final Button buttonPause = (Button) findViewById(R.id.button_pause);
        final Button buttonPlay =(Button)findViewById(R.id.button_play);
        Button buttonForth = (Button) findViewById(R.id.button_forth);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);

        seekbar=(SeekBar)findViewById(R.id.seekBar);
        seekbar.setMax(mediaPlayer.getDuration());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonPause.setEnabled(false);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Playing sound");
                mediaPlayer.start();

                currentPosition = mediaPlayer.getCurrentPosition();

                seekbar.setProgress(currentPosition);
                myHandler.postDelayed(UpdateSongTime, 100);

                buttonPause.setEnabled(true);
                buttonPlay.setEnabled(false);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Pausing sound");
                mediaPlayer.pause();

                buttonPause.setEnabled(false);
                buttonPlay.setEnabled(true);
            }
        });

        buttonForth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentPosition;

                if ((temp + FORWARD_TIME) <= mediaPlayer.getDuration()) {
                    currentPosition = currentPosition + FORWARD_TIME;
                    mediaPlayer.seekTo(currentPosition);
                    showToast("You have Jumped forward 5 seconds");
                } else {
                    showToast("Cannot jump forward 5 seconds");
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentPosition;

                if ((temp - BACKWARD_TIME) >= 0) {
                    currentPosition = currentPosition - BACKWARD_TIME;
                    mediaPlayer.seekTo((int) currentPosition);
                    showToast("You have Jumped backward 5 seconds");
                } else {
                    showToast("Cannot jump backward 5 seconds");
                }
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            currentPosition = mediaPlayer.getCurrentPosition();
            seekbar.setProgress(currentPosition);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    private void showToast(String message){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}