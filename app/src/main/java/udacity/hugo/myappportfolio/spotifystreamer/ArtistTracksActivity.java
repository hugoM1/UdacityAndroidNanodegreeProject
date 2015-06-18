package udacity.hugo.myappportfolio.spotifystreamer;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import udacity.hugo.myappportfolio.PortfolioApplication;
import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.adapter.ArtistTrackAdapter;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;

public class ArtistTracksActivity extends AppCompatActivity {


    private Bundle bundle;
    private String queryCountry = "country";
    private String queryLimit = "limit";
    private String tracksLimit = "10";
    private ListView tracksListView;
    private ArtistTrackAdapter trackAdapter;
    private List<Track> tracksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_tracks);

        tracksListView = (ListView) findViewById(R.id.list_view_top_tracks);
        trackAdapter = new ArtistTrackAdapter(tracksList, this);
        tracksListView.setAdapter(trackAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (getIntent() != null) {
                bundle = getIntent().getBundleExtra(NavigationStreamerHelper.TRACKS_ACTIVITY_BUNDLE_EXTRAS);
                actionBar.setSubtitle(bundle.getString(SpotifyStreamerActivity.ARTIST_NAME_EXTRA));
            }
        }

        getTenTopTracksByArtist(bundle.getString(SpotifyStreamerActivity.ARTIST_ID_EXTRA));
    }

    private void getTenTopTracksByArtist(final String artistId){
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Tracks doInBackground(Object[] params) {
                Map<String, Object> query = new HashMap<>();
                query.put(queryCountry, String.valueOf(Locale.getDefault().getCountry()));
                query.put(queryLimit, tracksLimit);

             Tracks tracks = PortfolioApplication.getInstance()
                        .getSpotifyService().getArtistTopTrack(artistId, query);

                return tracks;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o instanceof Tracks){
                    Tracks tracks = (Tracks) o;
                    tracksList = tracks.tracks;
                    trackAdapter.updateList(tracksList);
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_tracks, menu);
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
