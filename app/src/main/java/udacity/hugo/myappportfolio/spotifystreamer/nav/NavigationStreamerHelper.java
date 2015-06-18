package udacity.hugo.myappportfolio.spotifystreamer.nav;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import udacity.hugo.myappportfolio.spotifystreamer.ArtistTracksActivity;

/**
 * Created by hugo on 6/17/15.
 */
public class NavigationStreamerHelper {

    public static final String TRACKS_ACTIVITY_BUNDLE_EXTRAS = "tracks_extras";

    public static void openArtistTracksActivity(AppCompatActivity appCompatActivity, Bundle bundle){
        Intent intent = new Intent(appCompatActivity, ArtistTracksActivity.class);
        intent.putExtra(TRACKS_ACTIVITY_BUNDLE_EXTRAS, bundle);
        appCompatActivity.startActivity(intent);
    }
}
