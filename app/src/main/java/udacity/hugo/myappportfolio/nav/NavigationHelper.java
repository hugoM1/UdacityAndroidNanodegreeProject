package udacity.hugo.myappportfolio.nav;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import udacity.hugo.myappportfolio.spotifystreamer.SpotifyStreamerActivity;

/**
 * Created by hugo on 6/16/15.
 */
public class NavigationHelper {

    private static String spotifyApp = "SPOTIFY STREAMER";

    public static void openCurrentApp(AppCompatActivity appCompatActivity, String appName){
        Intent intent;
        if(spotifyApp.equals(appName)){
            intent = new Intent(appCompatActivity, SpotifyStreamerActivity.class);
            appCompatActivity.startActivity(intent);
        }
    }
}
