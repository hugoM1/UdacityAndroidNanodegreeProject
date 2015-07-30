package udacity.hugo.myappportfolio.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.fragments.TrackPlayerFragment;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;
import udacity.hugo.myappportfolio.util.Utils;

public class SpotifyTrackPlayerActivity extends AppCompatActivity {

    private Bundle args;
    public static final String DIALOG_TAG = "trackPlayerDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_track_player);

        if(savedInstanceState == null){
            if(getIntent().getExtras() != null){
                args = getIntent().getBundleExtra(NavigationStreamerHelper.TRACK_INFO_BUNDLE_EXTRAS);
                showDialog();
            }
        }
    }

    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (Utils.isTablet(this)) {
            TrackPlayerFragment.getInstance(args)
                    .show(fragmentManager, DIALOG_TAG);
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.player_container,
                    TrackPlayerFragment.getInstance(args)).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spotify_track_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
