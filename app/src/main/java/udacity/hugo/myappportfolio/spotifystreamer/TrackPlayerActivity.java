package udacity.hugo.myappportfolio.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.fragments.TracksFragment;
import udacity.hugo.myappportfolio.spotifystreamer.model.StreamTrack;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;
import udacity.hugo.myappportfolio.spotifystreamer.service.StreamerService;
import udacity.hugo.myappportfolio.util.Utils;

public class TrackPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView artistName, albumName, trackName, trackCurrentTimeText, trackTotalTimeText;
    private ImageButton previousTrackBtn, playPauseTrackBtn, nextTrackBtn;
    private SeekBar progressTrackBar;
    private ImageView albumTrackArtwork;

    private Bundle args;

    private StreamerService streamerService;
    private Intent playIntent;
    private boolean musicBound = false;
    private String currentTrack;
    private boolean isPlayerPlaying = false;
    private boolean isPlayerPaused = false;
    private int trackDuration = 0;
    private int trackCurrentPosition;
    private int trackListPosition = 0;
    private ArrayList<StreamTrack> streamTracks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

        artistName = (TextView) findViewById(R.id.artist_name);
        albumName = (TextView) findViewById(R.id.album_name);
        trackName = (TextView) findViewById(R.id.track_name);
        trackCurrentTimeText = (TextView) findViewById(R.id.track_current_time);
        trackTotalTimeText = (TextView) findViewById(R.id.track_total_time);
        albumTrackArtwork = (ImageView) findViewById(R.id.album_artwork);
        progressTrackBar = (SeekBar) findViewById(R.id.progress_track_bar);
        previousTrackBtn = (ImageButton) findViewById(R.id.previous_track_btn);
        previousTrackBtn.setOnClickListener(this);
        playPauseTrackBtn = (ImageButton) findViewById(R.id.play_pause_track_btn);
        playPauseTrackBtn.setOnClickListener(this);
        nextTrackBtn = (ImageButton) findViewById(R.id.next_track_btn);
        nextTrackBtn.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            args = getIntent().getBundleExtra(NavigationStreamerHelper.TRACK_INFO_BUNDLE_EXTRAS);
            initView();
        }else{
            args = savedInstanceState.getBundle(NavigationStreamerHelper.TRACK_INFO_BUNDLE_EXTRAS);
            streamTracks = args.getParcelableArrayList("theTracks");
            setPlayerInfo(args.getInt("songPos"));
            trackCurrentPosition = args.getInt("pos");
            musicBound = args.getBoolean("serviceB");
            isPlayerPlaying = args.getBoolean("isPlaying");
            isPlayerPaused = args.getBoolean("isPaused");
            if (isPlayerPlaying){
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StreamerService.SpotifyPlayerBinder binder = (StreamerService.SpotifyPlayerBinder) service;
            //get service
            streamerService = binder.getService();
            musicBound = true;
            //pass list
//            if (!isPlayerPlaying) {
//                isPlayerPlaying = true;
//            }

            startSpotifyService(currentTrack);
            isPlayerPlaying = true;
            setTrackDuration();
            streamerService.setSpotifyPlayerHandler(playerHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    final Handler playerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (trackDuration == 0) {
                setTrackDuration();
            }
            trackCurrentPosition = msg.getData().getInt(StreamerService.CURRENT_TRACK_POSITION);
            progressTrackBar.setProgress(trackCurrentPosition);
            trackCurrentTimeText.setText("00:" + String.format("%02d", trackCurrentPosition));

            if (trackCurrentPosition == trackDuration && trackCurrentPosition != 0) {
                isPlayerPlaying = false;
                isPlayerPaused = false;
                trackCurrentPosition = 0;

            }
            if (isPlayerPlaying) {
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    };

    private void initView() {


        artistName.setText(args.getString(TracksFragment.ARTIST_NAME_INFO));
        albumName.setText(args.getString(TracksFragment.ALBUM_NAME_INFO));
        trackName.setText(args.getString(TracksFragment.TRACK_NAME_INFO));

        setAlbumImage(args.getString(TracksFragment.ARTWORK_URL_INFO), albumTrackArtwork);
        trackTotalTimeText.setText(args.getString(TracksFragment.TRACK_TOTAL_DURATION));

        currentTrack = args.getString(TracksFragment.TRACK_PREVIEW_URL_INFO);


        streamTracks = args.getParcelableArrayList("theTracks");

        trackListPosition = args.getInt("songPos");

        progressTrackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NonNull SeekBar seekBar, int i, boolean b) {
                trackCurrentTimeText.setText("00:" + String.format("%02d", i));
                trackCurrentPosition = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isPlayerPlaying) {
                    streamerService.noUpdateUI();
                }
            }

            @Override
            public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
                trackCurrentPosition = seekBar.getProgress();
                if (streamerService != null) {
                    streamerService.toSeekTrack(trackCurrentPosition, isPlayerPaused);
                }
            }
        });

    }

    private void changeTrack(int trackListPosition) {

        isPlayerPlaying = true;
        isPlayerPaused = false;
        setPlayerInfo(trackListPosition);
        streamerService.setTrackUrlPreview(streamTracks.get(trackListPosition).getTrackPreviewUrl());
        streamerService.noUpdateUI();
        streamerService.playTrack(0);
        //resetTrackDuration();

    }

    private void setPlayerInfo(int trackListPosition) {

        artistName.setText(streamTracks.get(trackListPosition).getTrackName());
        albumName.setText(streamTracks.get(trackListPosition).getAlbumName());
        setAlbumImage(streamTracks.get(trackListPosition).getAlbumImage(), albumTrackArtwork);
        trackName.setText(streamTracks.get(trackListPosition).getTrackName());

    }

    private void setAlbumImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            Picasso.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, StreamerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.play_pause_track_btn: {
                if (!isPlayerPlaying) {
                    streamerService.playTrack(trackCurrentPosition);
                    isPlayerPaused = false;
                    isPlayerPlaying = true;
                } else {
                    streamerService.pauseTrack();
                    isPlayerPaused = true;
                    isPlayerPlaying = false;
                }
                break;
            }
            case R.id.next_track_btn: {
                trackCurrentPosition = (trackListPosition + 1) % streamTracks.size();
                changeTrack(trackCurrentPosition);
                trackListPosition = trackCurrentPosition;
                break;
            }
            case R.id.previous_track_btn: {
                trackListPosition = trackListPosition - 1;
                if (trackListPosition < 0) {
                    trackListPosition = streamTracks.size() - 1;
                }
                changeTrack(trackListPosition);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                stopService(playIntent);
                streamerService = null;
                System.exit(0);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBound) {
            unbindService(musicConnection);
            musicBound = false;
        }
    }

    public void setTrackDuration() {
        if (streamerService != null) {
            trackDuration = streamerService.getTrackDuration();
            progressTrackBar.setMax(trackDuration);
            trackTotalTimeText.setText(streamerService.getTrackDurationString());
        }
    }

    private void startSpotifyService(String trackUrl) {
        playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);

        Intent spotifyServiceIntent = new Intent(this, StreamerService.class);
        spotifyServiceIntent.putExtra(StreamerService.TRACK_PREVIEW_URL, trackUrl);

        if (!Utils.isServiceRunning(StreamerService.class, this) && isPlayerPlaying) {
            trackCurrentPosition = 0;
            this.getApplicationContext().stopService(spotifyServiceIntent);
            this.getApplicationContext().startService(spotifyServiceIntent);
        } else if (Utils.isServiceRunning(StreamerService.class, this)) {
            trackCurrentPosition = 0;
            this.getApplicationContext().startService(spotifyServiceIntent);
        }
        if (streamerService != null) {
            this.getApplicationContext().bindService(spotifyServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        args.putParcelableArrayList("theTracks", streamTracks);
        args.putInt("songPos", trackListPosition);
        args.putInt("pos", trackCurrentPosition);
        args.putBoolean("serviceB", musicBound);
        args.putBoolean("isPlaying", isPlayerPlaying);
        args.putBoolean("isPaused", isPlayerPaused);

        outState.putBundle(NavigationStreamerHelper.TRACK_INFO_BUNDLE_EXTRAS, args);
    }
}
