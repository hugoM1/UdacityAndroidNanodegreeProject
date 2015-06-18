package udacity.hugo.myappportfolio.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import udacity.hugo.myappportfolio.PortfolioApplication;
import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.adapter.ArtistAdapter;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;

public class SpotifyStreamerActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, TextWatcher{

    private EditText searchEditText;
    private ListView artistList;
    private List<Artist> artists = new ArrayList<>();
    private ArtistAdapter artistAdapter;
    private Context context;

    public static final String ARTIST_NAME_EXTRA = "artist_name";
    public static final String ARTIST_ID_EXTRA = "artist_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_streamer);

        searchEditText = (EditText) findViewById(R.id.edit_text_search);
        artistList = (ListView) findViewById(R.id.artist_list_view);
        artistList.setOnItemClickListener(this);

        context = SpotifyStreamerActivity.this;

        artistAdapter = new ArtistAdapter(artists, context);
        artistList.setAdapter(artistAdapter);

        searchEditText.addTextChangedListener(this);
    }

    private void searchArtistByName(final String artistName){
        if(!artistName.isEmpty()){
            AsyncTask task = new AsyncTask() {
                @Override
                protected ArtistsPager doInBackground(Object[] params) {
                    String artistNameToSearch = params[0].toString();
                    ArtistsPager artistsPager = PortfolioApplication.getInstance()
                            .getSpotifyService().searchArtists(artistNameToSearch);

                    return artistsPager;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if(o instanceof ArtistsPager){
                        ArtistsPager a = (ArtistsPager) o;
                        if (a.artists.total > 0) {
                            artists = a.artists.items;
                            artistAdapter.updateList(artists);
                        }else{
                            Toast.makeText(context, R.string.artist_not_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            task.execute(artistName);
        }else{
            artists.clear();
            artistAdapter.updateList(artists);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchArtistByName(String.valueOf(s));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist artist = (Artist) artistAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARTIST_NAME_EXTRA, artist.name);
        bundle.putString(ARTIST_ID_EXTRA, artist.id);

        NavigationStreamerHelper.openArtistTracksActivity(SpotifyStreamerActivity.this, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spotify_streamer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
