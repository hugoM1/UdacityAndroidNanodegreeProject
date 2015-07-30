package udacity.hugo.myappportfolio.spotifystreamer.nav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import udacity.hugo.myappportfolio.spotifystreamer.ArtistTracksActivity;
import udacity.hugo.myappportfolio.spotifystreamer.SpotifyTrackPlayerActivity;

/**
 * Created by hugo on 6/17/15.
 */
public class NavigationStreamerHelper {

    public static final String TRACKS_ACTIVITY_BUNDLE_EXTRAS = "tracks_extras";
    public static final String TRACK_INFO_BUNDLE_EXTRAS = "track_info";

    public static void openArtistTracksActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, ArtistTracksActivity.class);
        intent.putExtra(TRACKS_ACTIVITY_BUNDLE_EXTRAS, bundle);
        context.startActivity(intent);
    }

    public static void openTrackPlayerActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SpotifyTrackPlayerActivity.class);
        intent.putExtra(TRACK_INFO_BUNDLE_EXTRAS, bundle);
        context.startActivity(intent);
    }
}
