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

            playerService.setForegroud(false);
/*
            if(!playerService.isActive()){
                playerService.prepare(musicUri, title);
            }
*/
            if(playerService.isPlaying()){
                buttonPause.setEnabled(true);
                buttonPlay.setEnabled(false);
                myHandler.postDelayed(UpdateSongTime, 100);
            } else {
                buttonPause.setEnabled(false);
                buttonPlay.setEnabled(true);
            }

            seekbar.setMax(playerService.getDuration());
            seekbar.setProgress(playerService.getCurrentPosition());
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
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = getIntent().getExtras().getString(getString(R.string.intent_key_title));
        musicUri = (Uri)getIntent().getExtras().getParcelable(getString(R.string.intent_key_uri));
        seekbar = (SeekBar)findViewById(R.id.seekBar);

        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PREPARE);
        intent.putExtra(PlayerService.EXTRA_TITLE, title);
        intent.putExtra(PlayerService.EXTRA_URI, musicUri);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
        Button buttonBack = (Button)findViewById(R.id.button_back);
        buttonPause = (Button) findViewById(R.id.button_pause);
        buttonPlay =(Button)findViewById(R.id.button_play);
        Button buttonForth = (Button) findViewById(R.id.button_forth);

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
            playerService.setForegroud(true);
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void showToast(String message){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}