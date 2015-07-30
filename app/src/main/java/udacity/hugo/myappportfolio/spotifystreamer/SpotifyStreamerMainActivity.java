package udacity.hugo.myappportfolio.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.fragments.FindArtistFragment;
import udacity.hugo.myappportfolio.spotifystreamer.fragments.TracksFragment;
import udacity.hugo.myappportfolio.util.Utils;

public class SpotifyStreamerMainActivity extends AppCompatActivity implements FindArtistFragment.OnChangeFragment {

    public static final String ARTIST_NAME_EXTRA = "artist_name";
    public static final String ARTIST_ID_EXTRA = "artist_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_streamer_main);


        if (savedInstanceState == null) {
            if (Utils.isTablet(this)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.left_fragment_container, FindArtistFragment.getInstance(new Bundle()))
                        .commit();

            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, FindArtistFragment.getInstance(new Bundle()))
                        .commit();
            }
        }
    }

    @Override
    public void changeFragment(Bundle bundle) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.right_fragment_container, TracksFragment.getInstance(bundle))
                .commit();
    }
}
