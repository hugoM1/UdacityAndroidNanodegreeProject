package udacity.hugo.myappportfolio.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hugo on 6/22/15.
 */
public class StreamerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    public static final String IS_PLAYER_PLAYING = "is_player_playing";
    public static final String CURRENT_TRACK_POSITION = "current_track_position";
    public static final String TRACK_PREVIEW_URL = "track_preview_url";

    private SpotifyPlayerBinder spotifyPlayerBinder = null;
    private Handler spotifyPlayerHandler;
    private Timer uiUpdater;
    private int currentTrackPosition;
    private boolean isPlayerPaused;
    private MediaPlayer spotifyPlayer = null;
    private String trackUrlPreview;

    @Override
    public void onCreate() {
        super.onCreate();
        spotifyPlayerBinder = new SpotifyPlayerBinder();
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return spotifyPlayerBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Message completionMessage = new Message();
        Bundle completionBundle = new Bundle();
        completionBundle.putBoolean(IS_PLAYER_PLAYING,false);
        completionMessage.setData(completionBundle);
        if(spotifyPlayerHandler != null){
            spotifyPlayerHandler.sendMessage(completionMessage);
        }
        noUpdateUI();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(@NonNull MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if(currentTrackPosition != 0){
            mediaPlayer.seekTo(currentTrackPosition * 1000);
        }
        updateUI();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.hasExtra(TRACK_PREVIEW_URL)) {
            String trackUrl = intent.getStringExtra(TRACK_PREVIEW_URL);
            if (trackUrl != null) {
                setTrackUrlPreview(intent.getStringExtra(TRACK_PREVIEW_URL));
                playTrack(0);
            }
        }
        return START_STICKY;
    }

    public void toSeekTrack(int trackProgress, boolean isTrackPaused){
        if((spotifyPlayer != null && isTrackPaused && !spotifyPlayer.isPlaying()) || (spotifyPlayer != null && spotifyPlayer.isPlaying())){
            spotifyPlayer.seekTo(trackProgress *1000);
            if(spotifyPlayer.isPlaying()){
                updateUI();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(uiUpdater != null){
            noUpdateUI();
        }
        if(spotifyPlayer != null){
            spotifyPlayer.release();
            spotifyPlayer = null;
        }
        if(spotifyPlayerHandler != null){
            spotifyPlayerHandler = null;
        }
    }

    private void initSpotifyPlayer(){

        if(spotifyPlayer == null)
            spotifyPlayer = new MediaPlayer();

        spotifyPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            spotifyPlayer.setDataSource(trackUrlPreview);
            spotifyPlayer.prepareAsync();
            spotifyPlayer.setOnCompletionListener(StreamerService.this);
            spotifyPlayer.setOnPreparedListener(StreamerService.this);

        } catch (IOException e) {
            e.printStackTrace();
        }
        spotifyPlayer.setOnErrorListener(StreamerService.this);

    }

    public void setTrackUrlPreview(String trackUrlPreview) {
        this.trackUrlPreview = trackUrlPreview;
    }

    public int getTrackDuration(){
        if(spotifyPlayer != null && (isPlayerPaused || spotifyPlayer.isPlaying()) ){
            return (spotifyPlayer.getDuration() / 1000);
        }else{
            return 0;
        }
    }

    public int pauseTrack(){
        if(spotifyPlayer != null && spotifyPlayer.isPlaying()){
            spotifyPlayer.pause();
            isPlayerPaused = true;
            noUpdateUI();
            return spotifyPlayer.getDuration() /1000;
        }else{
            return 0;
        }
    }

    public void noUpdateUI() {
        if(uiUpdater != null){
            uiUpdater.cancel();
            uiUpdater.purge();
        }
    }

    public void playTrack(int trackPosition){
        currentTrackPosition = trackPosition;
        if(spotifyPlayer != null) {
            if(spotifyPlayer.isPlaying()){
                spotifyPlayer.stop();
            }
            spotifyPlayer.reset();
        }
        initSpotifyPlayer();
        isPlayerPaused = false;
    }

    public void updateUI(){
        uiUpdater = new Timer();
        uiUpdater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendCurrentTrackPosition();
            }
        },0,1000);
    }

    private void sendCurrentTrackPosition(){
        Message positionMessage = new Message();
        positionMessage.setData(getCurrentTrackPosition());
        if(spotifyPlayerHandler != null){
            spotifyPlayerHandler.sendMessage(positionMessage);
        }
    }

    private Bundle getCurrentTrackPosition(){
        Bundle uiBundle = new Bundle();
        if(spotifyPlayer != null && (isPlayerPaused || spotifyPlayer.isPlaying())){
            uiBundle.putBoolean(IS_PLAYER_PLAYING, true);
            int trackPosition = (int)Math.ceil((double)spotifyPlayer.getCurrentPosition() / 1000);
            uiBundle.putInt(CURRENT_TRACK_POSITION, trackPosition);
        }
        return uiBundle;
    }

    public void setSpotifyPlayerHandler(Handler spotifyPlayerHandler) {

        this.spotifyPlayerHandler = spotifyPlayerHandler;
        Message spotifyPlayerMessage = new Message();
        Bundle spotifyPlayerBundle;
        if(this.spotifyPlayerHandler != null ){
            spotifyPlayerBundle = getCurrentTrackPosition();

            if(isPlayerPaused){
                updateUI();
            }else{
                spotifyPlayerBundle.putBoolean(IS_PLAYER_PLAYING,false);
            }
            spotifyPlayerMessage.setData(spotifyPlayerBundle);
            if(this.spotifyPlayerHandler != null){
                this.spotifyPlayerHandler.sendMessage(spotifyPlayerMessage);
            }
        }
    }

    public String getTrackDurationString(){
        return "00:" + String.format("%02d",getTrackDuration());
    }

    public class SpotifyPlayerBinder extends Binder {
        public StreamerService getService (){ return StreamerService.this;}
    }
}
