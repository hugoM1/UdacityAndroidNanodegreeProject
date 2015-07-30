package udacity.hugo.myappportfolio.spotifystreamer.fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import udacity.hugo.myappportfolio.R;
import udacity.hugo.myappportfolio.spotifystreamer.model.StreamTrack;
import udacity.hugo.myappportfolio.spotifystreamer.service.StreamerService;
import udacity.hugo.myappportfolio.util.Utils;

/**
 * Created by hugo on 7/17/15.
 */
public class TrackPlayerFragment extends DialogFragment implements View.OnClickListener {


    private TextView artistNameTxt, albumNameTxt, trackNameTxt, trackCurrentTimeTxt, trackTotalTimeTxt;
    private ImageButton previousTrackBtn, playPauseTrackBtn, nextTrackBtn;
    private SeekBar progressTrackBar;
    private ImageView albumTrackArtwork;
    private Bundle args = new Bundle();
    private StreamerService streamerService;
    private Intent playIntent;
    private boolean isServiceBounded = false;
    private String currentTrack, artistName, albumName, trackName, trackImageURL, trackCurrentTime;
    private boolean isPlayerPlaying = false;
    private boolean isPlayerPaused = false;
    private int trackDuration = 0;
    private int trackCurrentPosition;
    private int trackListPosition = 0;
    private ArrayList<StreamTrack> streamTracks = new ArrayList<>();
    private View view;
    public static final String CURRENT_TRACK_POSITION_STATE = "currentTrackPosition";
    public static final String IS_SERVICE_BOUNDED_STATE = "serviceBounded";
    public static final String IS_PLAYING_STATE = "isPlaying";
    public static final String IS_PAUSED_STATE = "isPaused";

    public TrackPlayerFragment() {
    }

    public static TrackPlayerFragment getInstance(Bundle args) {
        TrackPlayerFragment mTrackPlayerFragment = new TrackPlayerFragment();

        mTrackPlayerFragment.setArguments(args);

        return mTrackPlayerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.player_layout, container, false);

        artistNameTxt = (TextView) view.findViewById(R.id.artist_name);
        albumNameTxt = (TextView) view.findViewById(R.id.album_name);
        trackNameTxt = (TextView) view.findViewById(R.id.track_name);
        trackCurrentTimeTxt = (TextView) view.findViewById(R.id.track_current_time);
        trackTotalTimeTxt = (TextView) view.findViewById(R.id.track_total_time);
        albumTrackArtwork = (ImageView) view.findViewById(R.id.album_artwork);
        progressTrackBar = (SeekBar) view.findViewById(R.id.progress_track_bar);
        previousTrackBtn = (ImageButton) view.findViewById(R.id.previous_track_btn);
        previousTrackBtn.setOnClickListener(this);
        playPauseTrackBtn = (ImageButton) view.findViewById(R.id.play_pause_track_btn);
        playPauseTrackBtn.setOnClickListener(this);
        nextTrackBtn = (ImageButton) view.findViewById(R.id.next_track_btn);
        nextTrackBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            args = getArguments();
            artistName = args.getString(TracksFragment.ARTIST_NAME_INFO);
            albumName = args.getString(TracksFragment.ALBUM_NAME_INFO);
            trackName = args.getString(TracksFragment.TRACK_NAME_INFO);
            trackImageURL = args.getString(TracksFragment.ARTWORK_URL_INFO);
            currentTrack = args.getString(TracksFragment.TRACK_PREVIEW_URL_INFO);
            streamTracks = args.getParcelableArrayList(TracksFragment.CURRENT_TRACKS);
            trackListPosition = args.getInt(TracksFragment.SONG_POSITION);
            initView();

        } else {
            trackImageURL = savedInstanceState.getString(TracksFragment.ARTWORK_URL_INFO);
            streamTracks = savedInstanceState.getParcelableArrayList(TracksFragment.CURRENT_TRACKS);
            setPlayerInfo(savedInstanceState.getInt(TracksFragment.SONG_POSITION));
            trackCurrentPosition = savedInstanceState.getInt(CURRENT_TRACK_POSITION_STATE);
            isServiceBounded = savedInstanceState.getBoolean(IS_SERVICE_BOUNDED_STATE);
            isPlayerPlaying = savedInstanceState.getBoolean(IS_PLAYING_STATE);
            isPlayerPaused = savedInstanceState.getBoolean(IS_PAUSED_STATE);
            if (isPlayerPlaying) {
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    private void initView() {

        artistNameTxt.setText(artistName);
        albumNameTxt.setText(albumName);
        trackNameTxt.setText(trackName);

        setAlbumImage(trackImageURL, albumTrackArtwork);
        startSpotifyService(currentTrack);
        progressTrackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@NonNull SeekBar seekBar, int i, boolean b) {
                trackCurrentTimeTxt.setText("00:" + String.format("%02d", i));
                trackCurrentPosition = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isPlayerPlaying) {
                    streamerService.noUpdateUI();
                }
            }

            @Override
            public void onStopTrackingTouch(@NonNull SeekBar seekBar) {
                trackCurrentPosition = seekBar.getProgress();
                if (streamerService != null) {
                    streamerService.toSeekTrack(trackCurrentPosition, isPlayerPaused);
                }
            }
        });
    }

    private void changeTrack(int trackListPosition) {

        isPlayerPlaying = true;
        isPlayerPaused = false;
        setPlayerInfo(trackListPosition);
        streamerService.setTrackUrlPreview(streamTracks.get(trackListPosition).getTrackPreviewUrl());
        streamerService.noUpdateUI();
        streamerService.playTrack(0);
        //resetTrackDuration();

    }

    private void setPlayerInfo(int trackListPosition) {
        trackTotalTimeTxt.setText(streamerService.getTrackDurationString());
        artistNameTxt.setText(streamTracks.get(trackListPosition).getTrackName());
        albumNameTxt.setText(streamTracks.get(trackListPosition).getAlbumName());
        setAlbumImage(streamTracks.get(trackListPosition).getAlbumImage(), albumTrackArtwork);
        trackNameTxt.setText(streamTracks.get(trackListPosition).getTrackName());

    }

    private void setAlbumImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(imageView);
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StreamerService.SpotifyPlayerBinder binder = (StreamerService.SpotifyPlayerBinder) service;
            //get service
            streamerService = binder.getService();
            isServiceBounded = true;
            //pass list
            if (!isPlayerPlaying) {
                isPlayerPlaying = true;
            }
            //isPlayerPlaying = true;
            setTrackDuration();
            streamerService.setSpotifyPlayerHandler(playerHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded = false;
        }
    };

    final Handler playerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (trackDuration == 0) {
                setTrackDuration();
            }
            trackCurrentPosition = msg.getData().getInt(StreamerService.CURRENT_TRACK_POSITION);
            progressTrackBar.setProgress(trackCurrentPosition);
            trackCurrentTimeTxt.setText("00:" + String.format("%02d", trackCurrentPosition));

            if (trackCurrentPosition == trackDuration && trackCurrentPosition != 0) {
                isPlayerPlaying = false;
                isPlayerPaused = false;
                trackCurrentPosition = 0;

            }
            if (isPlayerPlaying) {
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_play);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), StreamerService.class);
            getActivity().getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.play_pause_track_btn: {
                if (!isPlayerPlaying) {
                    streamerService.playTrack(trackCurrentPosition);
                    isPlayerPaused = false;
                    isPlayerPlaying = true;
                } else {
                    streamerService.pauseTrack();
                    isPlayerPaused = true;
                    isPlayerPlaying = false;
                }
                break;
            }
            case R.id.next_track_btn: {
                trackCurrentPosition = (trackListPosition + 1) % streamTracks.size();
                changeTrack(trackCurrentPosition);
                trackListPosition = trackCurrentPosition;
                break;
            }
            case R.id.previous_track_btn: {
                trackListPosition = trackListPosition - 1;
                if (trackListPosition < 0) {
                    trackListPosition = streamTracks.size() - 1;
                }
                changeTrack(trackListPosition);
                break;
            }
        }
    }

    public void setTrackDuration() {
        if (streamerService != null) {
            trackDuration = streamerService.getTrackDuration();
            progressTrackBar.setMax(trackDuration);
            trackTotalTimeTxt.setText(streamerService.getTrackDurationString());
        }
    }

    private void startSpotifyService(String trackUrl) {
        playPauseTrackBtn.setImageResource(android.R.drawable.ic_media_pause);

        Intent spotifyServiceIntent = new Intent(getActivity(), StreamerService.class);
        spotifyServiceIntent.putExtra(StreamerService.TRACK_PREVIEW_URL, trackUrl);

        if (Utils.isServiceRunning(StreamerService.class, getActivity()) && !isPlayerPlaying) {
            trackCurrentPosition = 0;
            getActivity().stopService(spotifyServiceIntent);
            getActivity().startService(spotifyServiceIntent);
        } else if (!Utils.isServiceRunning(StreamerService.class, getActivity())) {
            trackCurrentPosition = 0;
            getActivity().startService(spotifyServiceIntent);
        }
        if (streamerService != null) {
            getActivity().bindService(spotifyServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TracksFragment.CURRENT_TRACKS, streamTracks);
        outState.putInt(TracksFragment.SONG_POSITION, trackListPosition);
        outState.putInt(CURRENT_TRACK_POSITION_STATE, trackCurrentPosition);
        outState.putBoolean(IS_PLAYING_STATE, isPlayerPlaying);
        outState.putBoolean(IS_PAUSED_STATE, isPlayerPaused);
    }

    private void destroySpotifyService() {

        if (streamerService != null) {
            streamerService.noUpdateUI();
            if (isServiceBounded) {
                getActivity().getApplicationContext().unbindService(musicConnection);
                isServiceBounded = false;
            }
        }
        if (!isPlayerPaused && !isPlayerPlaying) {
            getActivity().getApplicationContext().stopService(new Intent(getActivity(), StreamerService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroySpotifyService();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
