package udacity.hugo.myappportfolio.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import udacity.hugo.myappportfolio.spotifystreamer.model.StreamTrack;

/**
 * Created by hugo on 6/22/15.
 */
public class StreamerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    //media player
    private MediaPlayer player;
    private String track;
    private final IBinder musicBind = new StreamBinder();
    private ArrayList<StreamTrack> trackList;
    private int songPos;

    @Override
    public void onCreate() {
        super.onCreate();

        //create player
        player = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void setTrack(String track){
        this.track = track;
    }

    public void setTrackList(ArrayList<StreamTrack> trackList){
        this.trackList = trackList;
    }

    public void setPosition(int position){
        songPos = position;
    }

    public class StreamBinder extends Binder {
        StreamerService getService() {
            return StreamerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public void playSong(){
        //play a song
        StreamTrack sTrack = trackList.get(songPos);

        try {
            player.reset();
            player.setDataSource(sTrack.getTrackPreviewUrl());
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
