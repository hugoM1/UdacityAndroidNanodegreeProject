package udacity.hugo.myappportfolio.spotifystreamer.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import udacity.hugo.myappportfolio.PortfolioApplication;
import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.SpotifyStreamerMainActivity;
import udacity.hugo.myappportfolio.spotifystreamer.adapter.ArtistAdapter;
import udacity.hugo.myappportfolio.spotifystreamer.nav.NavigationStreamerHelper;
import udacity.hugo.myappportfolio.util.Utils;

/**
 * Created by hugo on 7/6/15.
 */
public class FindArtistFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher {

    private EditText searchEditText;
    private ListView artistList;
    private List<Artist> artists = new ArrayList<>();
    private ArtistAdapter artistAdapter;

    public static final String ARTIST_NAME_EXTRA = "artist_name";
    public static final String ARTIST_ID_EXTRA = "artist_id";
    public final String INTERFACE_IMPLEMENTATION_EXCEPTION_MESSAGE = "must be implement OnFragmentClickListener";
    public final String ITEMS_SEARCHED_STATE = "items_searched";

    private Context context;

    OnChangeFragment changeFragment;

    public FindArtistFragment() {
    }

    public static FindArtistFragment getInstance(Bundle args) {
        FindArtistFragment mFindArtistFragment = new FindArtistFragment();

        mFindArtistFragment.setArguments(args);

        return mFindArtistFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spotify_streamer_layout, container, false);

        searchEditText = (EditText) view.findViewById(R.id.edit_text_search);
        artistList = (ListView) view.findViewById(R.id.artist_list_view);
        artistList.setOnItemClickListener(this);

        artistAdapter = new ArtistAdapter(artists, context);
        artistList.setAdapter(artistAdapter);

        searchEditText.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState != null){
            Gson gson = new GsonBuilder().create();
            Type artistAdapterType = new TypeToken<List<Artist>>() {
            }.getType();
            List<Artist> mArtistList = gson.fromJson(savedInstanceState.getString(ITEMS_SEARCHED_STATE), artistAdapterType);
            artistAdapter.updateList(mArtistList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist artist = (Artist) artistAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString(ARTIST_NAME_EXTRA, artist.name);
        bundle.putString(ARTIST_ID_EXTRA, artist.id);

        if (Utils.isTablet(getActivity())) {
            changeFragment.changeFragment(bundle);
        } else {
            NavigationStreamerHelper.openArtistTracksActivity(context, bundle);
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

    private void searchArtistByName(final String artistName) {
        if (!artistName.isEmpty()) {
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
                    if (o instanceof ArtistsPager) {
                        ArtistsPager a = (ArtistsPager) o;
                        if (a.artists.total > 0) {
                            artists = a.artists.items;
                            artistAdapter.updateList(artists);
                        } else {
                            Toast.makeText(context, R.string.artist_not_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            task.execute(artistName);
        } else {
            artists.clear();
            artistAdapter.updateList(artists);
        }
    }

    public interface OnChangeFragment {
        void changeFragment(Bundle bundle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            changeFragment = (OnChangeFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(SpotifyStreamerMainActivity.class.getSimpleName() + INTERFACE_IMPLEMENTATION_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().create();

        Type artistAdapterType = new TypeToken<List<Artist>>() {
        }.getType();

        outState.putString(ITEMS_SEARCHED_STATE, gson.toJson(artists, artistAdapterType));
    }
}
