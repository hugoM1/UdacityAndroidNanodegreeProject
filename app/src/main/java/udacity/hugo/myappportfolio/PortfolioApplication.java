package udacity.hugo.myappportfolio;

import android.app.Application;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by hugo on 6/16/15.
 */
public class PortfolioApplication extends Application {

    private SpotifyApi spotifyApi;
    private static PortfolioApplication sInstance;
    private SpotifyService spotifyService;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static synchronized PortfolioApplication getInstance() {
        return sInstance;
    }

    public SpotifyApi getSpotifyApi() {
        if (spotifyApi == null) {
            spotifyApi = new SpotifyApi();
        }
        return spotifyApi;
    }

    public SpotifyService getSpotifyService() {
        spotifyService = getSpotifyApi().getService();

        return spotifyService;
    }
}
