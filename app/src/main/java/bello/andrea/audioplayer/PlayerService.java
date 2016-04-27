package bello.andrea.audioplayer;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class PlayerService extends Service {

    private final static int NOTIFICATION_ID = 1;

    private static final int FORWARD_TIME = 5000;
    private static final int BACKWARD_TIME = 5000;

    public static final String ACTION_PREPARE = "bello.andrea.audioplayer.action.PREPARE";
    public static final String EXTRA_URI = "bello.andrea.servicestarttest.extra.URI";
    public static final String EXTRA_TITLE = "bello.andrea.servicestarttest.extra.TITLE";

    private MediaPlayer mediaPlayer;

    private String currentTitle;
    private Uri currentUri;

    // Binder given to clients
    private final IBinder mBinder = new PlayerBinder();

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

    public void prepare(Uri musicUri, String currentTitle){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), musicUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSelf();
                }
            });
            this.currentTitle = currentTitle;
            this.currentUri = musicUri;
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

    public void setForegroud(boolean foregroud){
        if(foregroud) {
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra(getString(R.string.intent_key_uri), currentUri);
            resultIntent.putExtra(getString(R.string.intent_key_title), currentTitle);


            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Player")
                    .setContentText("Playing: " + currentTitle)
                    .setContentIntent(resultPendingIntent);

            // NOTIFICATION_ID allows you to update the notification later on.
            startForeground(NOTIFICATION_ID, mBuilder.build());
        } else {
            stopForeground(true);
        }
    }

    public void stop(){
        mediaPlayer.stop();
        currentTitle = null;
        currentUri = null;
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public boolean isActive(){
        return currentTitle != null;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PREPARE.equals(action)) {
                final Uri uri = intent.getParcelableExtra(EXTRA_URI);
                final String title = intent.getStringExtra(EXTRA_TITLE);
                if(!uri.equals(currentUri))
                    prepare(uri, title);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}