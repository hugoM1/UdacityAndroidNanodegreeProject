package udacity.hugo.myappportfolio.spotifystreamer;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;

public class TrackPlayerActivity extends AppCompatActivity {

    private TextView artistName, albumName, trackName, trackCurrentTimeText, trackTotalTimeText;
    private ImageButton previousTrackBtn, playPauseTrackBtn, nextTrackBtn;
    private SeekBar progressTrackBar;
    private ImageView albumTrackArtwork;

    private Bundle args;

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
        nextTrackBtn = (ImageButton) findViewById(R.id.next_track_btn);

        artistName.setText(args.getString(ArtistTracksActivity.ARTIST_NAME_INFO));
        albumName.setText(args.getString(ArtistTracksActivity.ALBUM_NAME_INFO));
        trackName.setText(args.getString(ArtistTracksActivity.TRACK_NAME_INFO));

        Picasso.with(this)
                .load(args.getString(ArtistTracksActivity.ARTWORK_URL_INFO))
                .into(albumTrackArtwork);
        trackTotalTimeText.setText((new SimpleDateFormat("mm:ss")).format(new Date(args.getLong(ArtistTracksActivity.TRACK_TOTAL_DURATION))));

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
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
