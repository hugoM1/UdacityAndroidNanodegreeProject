package udacity.hugo.myappportfolio.spotifystreamer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Tracks;
import udacity.hugo.myappportfolio.PortfolioApplication;
import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.SpotifyStreamerMainActivity;
import udacity.hugo.myappportfolio.spotifystreamer.SpotifyTrackPlayerActivity;
import udacity.hugo.myappportfolio.spotifystreamer.adapter.ArtistTrackAdapter;
import udacity.hugo.myappportfolio.spotifystreamer.model.StreamTrack;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;
import udacity.hugo.myappportfolio.util.Utils;

/**
 * Created by hugo on 7/6/15.
 */
public class TracksFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Bundle bundle;
    private String queryCountry = "country";
    private String queryLimit = "limit";
    private String tracksLimit = "10";
    private ListView tracksListView;
    private ArtistTrackAdapter trackAdapter;
    private List<StreamTrack> tracksList = new ArrayList<>();

    public static final String ARTIST_NAME_INFO = "artistName";
    public static final String ALBUM_NAME_INFO = "albumName";
    public static final String TRACK_NAME_INFO = "trackName";
    public static final String ARTWORK_URL_INFO = "trackArtworkUrl";
    public static final String TRACK_PREVIEW_URL_INFO = "trackPreviewUrl";
    public static final String TRACK_TOTAL_DURATION = "trackTotalDuration";
    public static final String CURRENT_TRACKS = "currentTracks";
    public static final String SONG_POSITION = "songPosition";

    private ArrayList<StreamTrack> parcelableTrack = new ArrayList<>();
    private Context context;

    public TracksFragment() {
    }

    public static TracksFragment getInstance(Bundle args) {
        TracksFragment mTracksFragment = new TracksFragment();

        mTracksFragment.setArguments(args);

        return mTracksFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracks_layout, container, false);

        initView(view);

        if (!Utils.isTablet(getActivity())) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (getArguments() != null) {
                    bundle = getArguments().getBundle(NavigationStreamerHelper.TRACKS_ACTIVITY_BUNDLE_EXTRAS);
                    actionBar.setSubtitle(bundle.getString(SpotifyStreamerMainActivity.ARTIST_NAME_EXTRA));
                }
            }

        } else {
            bundle = getArguments();
        }
        getTenTopTracksByArtist(bundle.getString(SpotifyStreamerMainActivity.ARTIST_ID_EXTRA));

        return view;
    }

    private void initView(View view) {
        tracksListView = (ListView) view.findViewById(R.id.list_view_top_tracks);
        tracksListView.setOnItemClickListener(this);
        trackAdapter = new ArtistTrackAdapter(tracksList, context);
        tracksListView.setAdapter(trackAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StreamTrack track = (StreamTrack) trackAdapter.getItem(position);

        Bundle bundle = new Bundle();

        bundle.putString(ARTIST_NAME_INFO, track.getTrackArtistName());
        bundle.putString(ALBUM_NAME_INFO, track.getAlbumName());
        bundle.putString(ARTWORK_URL_INFO, track.getAlbumImage());
        bundle.putString(TRACK_PREVIEW_URL_INFO, track.getTrackPreviewUrl());
        bundle.putString(TRACK_TOTAL_DURATION, String.valueOf(track.getTrackDuration()));
        bundle.putString(TRACK_NAME_INFO, track.getTrackName());
        bundle.putParcelableArrayList(CURRENT_TRACKS, parcelableTrack);
        bundle.putInt(SONG_POSITION, position);

        if (!Utils.isTablet(getActivity())) {
            NavigationStreamerHelper.openTrackPlayerActivity(context, bundle);
        } else {
            // The device is using a large layout, so show the fragment as a dialog
            TrackPlayerFragment.getInstance(bundle)
                    .show(getFragmentManager(), SpotifyTrackPlayerActivity.DIALOG_TAG);
        }
    }

    private void getTenTopTracksByArtist(final String artistId) {
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
                if (o instanceof Tracks) {
                    Tracks tracks = (Tracks) o;
                    //tracksList = tracks.tracks;
                    //trackAdapter.updateList(tracksList);
                    for (int index = 0; index < tracks.tracks.size(); index++) {
                        StreamTrack streamTrack = new StreamTrack();
                        streamTrack.setTrackName(tracks.tracks.get(index).name);
                        streamTrack.setTrackPreviewUrl(tracks.tracks.get(index).preview_url);
                        streamTrack.setAlbumName(tracks.tracks.get(index).album.name);
                        streamTrack.setAlbumImage(tracks.tracks.get(index).album.images.get(0).url);
                        streamTrack.setTrackArtistName(tracks.tracks.get(index).artists.get(0).name);
                        streamTrack.setTrackDuration(tracks.tracks.get(index).duration_ms);
                        parcelableTrack.add(streamTrack);
                    }
                    tracksList = parcelableTrack;
                    trackAdapter.updateList(tracksList);
                }
            }
        };
        asyncTask.execute();
    }
}
