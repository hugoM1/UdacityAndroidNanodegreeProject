package udacity.hugo.myappportfolio.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.model.StreamTrack;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;
import udacity.hugo.myappportfolio.spotifystreamer.service.StreamerService;

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

    private ArrayList<StreamTrack> streamTracks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().getExtras() != null){
            args = getIntent().getBundleExtra(NavigationStreamerHelper.TRACK_INFO_BUNDLE_EXTRAS);
        }
        initView();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StreamerService.StreamBinder binder = (StreamerService.StreamBinder)service;
            //get service
            streamerService = binder.getService();
            //pass list
            streamerService.setTrack(currentTrack);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void initView() {
        artistName = (TextView) findViewById(R.id.artist_name);
        albumName = (TextView) findViewById(R.id.album_name);
        trackName = (TextView) findViewById(R.id.track_name);
        trackCurrentTimeText = (TextView) findViewById(R.id.track_current_time);
        trackTotalTimeText = (TextView) findViewById(R.id.track_total_time);

        albumTrackArtwork = (ImageView) findViewById(R.id.album_artwork);

        progressTrackBar = (SeekBar) findViewById(R.id.progress_track_bar);

        previousTrackBtn = (ImageButton) findViewById(R.id.previous_track_btn);
        playPauseTrackBtn = (ImageButton) findViewById(R.id.play_pause_track_btn);
        playPauseTrackBtn.setOnClickListener(this);
        nextTrackBtn = (ImageButton) findViewById(R.id.next_track_btn);

        artistName.setText(args.getString(ArtistTracksActivity.ARTIST_NAME_INFO));
        albumName.setText(args.getString(ArtistTracksActivity.ALBUM_NAME_INFO));
        trackName.setText(args.getString(ArtistTracksActivity.TRACK_NAME_INFO));

        Picasso.with(this)
                .load(args.getString(ArtistTracksActivity.ARTWORK_URL_INFO))
                .into(albumTrackArtwork);
        trackTotalTimeText.setText((new SimpleDateFormat("mm:ss")).format(new Date(args.getLong(ArtistTracksActivity.TRACK_TOTAL_DURATION))));

        currentTrack = args.getString(ArtistTracksActivity.TRACK_PREVIEW_URL_INFO);


        streamTracks = args.getParcelableArrayList("theTracks");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, StreamerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.play_pause_track_btn:{
                if(!streamerService.isPlaying()){
                    streamerService.setPosition(args.getInt("songPos"));
                    streamerService.setTrack(currentTrack);
                    streamerService.setTrackList(streamTracks);
                    streamerService.playSong();
                }else {
                    streamerService.pauseMusic();
                }
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
        switch (id){
            case android.R.id.home:{
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
    protected void onDestroy() {
        stopService(playIntent);
        streamerService = null;
        super.onDestroy();
    }
}
