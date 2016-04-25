package bello.andrea.audioplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class PlayerService extends Service {

    private static final int FORWARD_TIME = 5000;
    private static final int BACKWARD_TIME = 5000;

    private MediaPlayer mediaPlayer;

    // Binder given to clients
    private final IBinder mBinder = new PlayerBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayerBinder extends Binder {
        PlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayerService.this;
        }
    }

    public void prepare(Uri musicUri){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), musicUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public void start(){
        mediaPlayer.start();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void goForward(){
        int temp = mediaPlayer.getCurrentPosition() + FORWARD_TIME;
        if (temp <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(temp);
        } else {
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }

    public void goBackward(){
        int temp = mediaPlayer.getCurrentPosition() - BACKWARD_TIME;
        if (temp >= 0) {
            mediaPlayer.seekTo(temp);
        } else {
            mediaPlayer.seekTo(0);
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        super.onDestroy();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}