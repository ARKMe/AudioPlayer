package bello.andrea.audioplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.IBinder;
import android.view.View;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class MainActivity extends Activity {

    private SeekBar seekbar;

    private Toast toast;

    private Handler myHandler = new Handler();

    private Uri musicUri;

    PlayerService playerService;

    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            playerService = binder.getService();
            playerService.prepare(musicUri);

            seekbar.setMax(playerService.getDuration());
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress >= seekBar.getMax()) {
                        buttonPause.setEnabled(false);
                        buttonPlay.setEnabled(true);
                    }
                    if (fromUser) {
                        playerService.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private Button buttonPause;
    private Button buttonPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicUri = (Uri)getIntent().getExtras().getParcelable(getString(R.string.intent_key_uri));
        seekbar = (SeekBar)findViewById(R.id.seekBar);

        Intent intent = new Intent(this, PlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Button buttonBack =(Button)findViewById(R.id.button_back);
        buttonPause = (Button) findViewById(R.id.button_pause);
        buttonPlay =(Button)findViewById(R.id.button_play);
        Button buttonForth = (Button) findViewById(R.id.button_forth);

        buttonPause.setEnabled(false);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Playing sound");
                playerService.start();

                myHandler.postDelayed(UpdateSongTime, 100);

                buttonPause.setEnabled(true);
                buttonPlay.setEnabled(false);
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Pausing sound");
                playerService.pause();

                buttonPause.setEnabled(false);
                buttonPlay.setEnabled(true);
            }
        });

        buttonForth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerService.goForward();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerService.goBackward();
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            seekbar.setProgress(playerService.getCurrentPosition());
            if(playerService.isPlaying())
                myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        playerService.stop();
    }

    private void showToast(String message){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}